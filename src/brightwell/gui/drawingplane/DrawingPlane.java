/* *********************************************************************** *
 *                                                                         *
 * This file is part of Integrated Structure Evolution Environment (ISEE). *
 * Copyright (C) 2002-2007 Keyan Zahedi and Martin Huelse                  *
 * All rights reserved.                                                    *
 * Email: {keyan,aberys}@users.sourceforge.net                             *
 * Web: http://sourceforge.net/projects/isee                               *
 *                                                                         *
 * For a list of contributors see the file AUTHORS.                        *
 *                                                                         *
 * ISEE is free software; you can redistribute it and/or modify it under   *
 * the terms of the GNU General Public License as published by the Free    *
 * Software Foundation; either version 2 of the License, or (at your       *
 * option) any later version.                                              *
 *                                                                         *
 * ISEE is distributed in the hope that it will be useful, but WITHOUT     *
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or   *
 * FITNESS FOR A PARTICULAR PURPOSE.                                       *
 *                                                                         *
 * You should have received a copy of the GNU General Public License       *
 * along with ISEE in the file COPYING; if not, write to the Free          *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor,                 *
 * Boston, MA 02110-1301, USA                                              *
 *                                                                         *
 * *********************************************************************** */


package brightwell.gui.drawingplane;


import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.RepaintManager;

import org.jibble.epsgraphics.EpsGraphics2D;

import util.io.ExFileFilter;

public class DrawingPlane extends JFrame implements MouseListener,
  MouseMotionListener, ActionListener, Printable
{

  private NumberFormat numberFormat = NumberFormat.getInstance(Locale.UK);
  private DrawingPanel panel = null;
  private JPopupMenu rightButtonMenu = new JPopupMenu("Menu");
  private JPopupMenu coordButtonMenu = new JPopupMenu("Coord");
  private JMenuItem saveJPGMenu = new JMenuItem("save jpg");
  private JMenuItem savePNGMenu = new JMenuItem("save png");
  private JMenuItem saveEpsMenu = new JMenuItem("save eps");
  private JMenuItem saveStdEpsMenu = new JMenuItem("save std eps");
  private JMenuItem printMenu = new JMenuItem("print");
  private JMenuItem coordMenuItem = new JMenuItem("coord");
  private String standardFileName = "";

  private static JFileChooser fileDialog = new JFileChooser(".");

  public DrawingPlane(String name, int width, int height,
      double[] x, double y[], Object analyser)
  {
    this(name, width, height, x, y, analyser, "");
  }

  public DrawingPlane(String name, int width, int height,
      double[] x, double y[], Object analyser, String standardFileName)
  {
    super(name);
    setSize(width, height);
    panel = new DrawingPanel();
    panel.setSize(width, height);
    setContentPane(panel);
    setBackground(Color.WHITE);
    panel.setRange(x,y);
    setResizable(false);
    this.addKeyListener((KeyListener)analyser);
    this.addMouseListener(this);
    this.addMouseMotionListener(this);
    this.standardFileName = standardFileName;
    saveJPGMenu.addActionListener(this);
    savePNGMenu.addActionListener(this);
    saveEpsMenu.addActionListener(this);
    saveStdEpsMenu.addActionListener(this);
    printMenu.addActionListener(this);

    rightButtonMenu.add(saveJPGMenu);
    rightButtonMenu.add(savePNGMenu);
    rightButtonMenu.add(saveEpsMenu);
    rightButtonMenu.add(saveStdEpsMenu);
    rightButtonMenu.add(printMenu);

    coordButtonMenu.add(coordMenuItem);
    coordMenuItem.addActionListener(this);

    numberFormat.setMinimumFractionDigits(2);
    numberFormat.setMaximumFractionDigits(2);


  }

  public void setUseBigPoints(boolean useBigBoints)
  {
    panel.setUseBigPoints(useBigBoints);
  }

  public void drawProgressPointX(double x, Color color)
  {
    panel.drawProgressPointX(x, color);
  }

  public void drawProgressPointY(double y, Color color)
  {
    panel.drawProgressPointY(y, color);
  }

  public void drawXLine(double x)
  {
    panel.drawXLine(x);
  }

  public void drawXLine(double x, Color color)
  {
    panel.drawXLine(x, color);
  }

  public void drawXLineNoLegend(double x, Color color)
  {
    panel.drawXLineNoLegend(x, color);
  }


  public void drawYLine(double y)
  {
    panel.drawYLine(y);
  }

  public void setTitle(String title)
  {
    panel.setTitle(title);
  }
  
  public void drawString(String string, int x, int y)
  {
    panel.drawString(string, x, y);
  }


  // **************************************************************************
  // actionListener
  // **************************************************************************
  public void actionPerformed(ActionEvent e)
  {
    if (e.getSource() == coordMenuItem)
    {
      StringTokenizer st = new StringTokenizer(e.getActionCommand(),",");
      String xString = st.nextToken().trim();
      String yString = st.nextToken().trim();
      double x = Double.parseDouble(xString);
      double y = Double.parseDouble(yString);
      //System.out.println("X: " + x + " Y: " + y);
      panel.drawXLine(y);
      panel.drawYLine(x);
      return;
    }

    if (e.getSource() == saveStdEpsMenu)
    {
      //JFileChooser fileDialog = new JFileChooser(".");
      clearFileFilters();
      fileDialog.addChoosableFileFilter(new ExFileFilter("eps","Snapshot ( .eps )"));
      fileDialog.setSelectedFile(new File(
            fileDialog.getCurrentDirectory() 
            + System.getProperty("file.separator")
            + standardFileName + ".eps"));
      int returnVal = fileDialog.showSaveDialog(this);
      if(returnVal == JFileChooser.APPROVE_OPTION) {
        String file =  fileDialog.getCurrentDirectory() 
          + System.getProperty("file.separator") 
          + fileDialog.getSelectedFile().getName();
        if (file.indexOf(".eps") == -1) 
        {
          file = file + ".eps";
        }

        //System.out.println("You chose to save this file: " + file);
        try 
        {
          FileOutputStream outputStream = new FileOutputStream(file);
          EpsGraphics2D epsG = new EpsGraphics2D("Example", outputStream, 0, 0,
              super.getWidth(), super.getHeight());
          epsG.drawImage(panel.getImage(),0,0,Color.white,null);
          epsG.flush();
          epsG.close();

        }
        catch (IOException ex)
        {
          ex.printStackTrace();
        }
      }
      return;
    }

    if (e.getSource() == saveEpsMenu)
    {
      //JFileChooser fileDialog = new JFileChooser(".");
      clearFileFilters();
      fileDialog.addChoosableFileFilter(new ExFileFilter("eps","Snapshot ( .eps )"));
      int returnVal = fileDialog.showSaveDialog(this);
      if(returnVal == JFileChooser.APPROVE_OPTION) {
        String file =  fileDialog.getCurrentDirectory() 
          + System.getProperty("file.separator") 
          + fileDialog.getSelectedFile().getName();
        if (file.indexOf(".eps") == -1) 
        {
          file = file + ".eps";
        }

        //System.out.println("You chose to save this file: " + file);
        try 
        {
          FileOutputStream outputStream = new FileOutputStream(file);
          EpsGraphics2D epsG = new EpsGraphics2D("Example", outputStream, 0, 0,
              super.getWidth(), super.getHeight());
          epsG.drawImage(panel.getImage(),0,0,Color.white,null);
          epsG.flush();
          epsG.close();

        }
        catch (IOException ex)
        {
          ex.printStackTrace();
        }
      }
      return;
    }

    if (e.getSource() == savePNGMenu)
    {
      //JFileChooser fileDialog = new JFileChooser(".");
      clearFileFilters();
      fileDialog.addChoosableFileFilter(new ExFileFilter("png","Snapshot ( .png )"));
      int returnVal = fileDialog.showSaveDialog(this);
      if(returnVal == JFileChooser.APPROVE_OPTION) {
        String file =  fileDialog.getCurrentDirectory() 
          + System.getProperty("file.separator") 
          + fileDialog.getSelectedFile().getName();
        if (file.indexOf(".png") == -1) 
        {
          file = file + ".png";
        }

        //System.out.println("You chose to save this file: " + file);
        try 
        {
          ImageIO.write(panel.getImage(), "png", new File(file));
        }
        catch (IOException ex)
        {
          ex.printStackTrace();
        }
      }
    }

    if (e.getSource() == saveJPGMenu)
    {
      //JFileChooser fileDialog = new JFileChooser(".");
      clearFileFilters();
      fileDialog.addChoosableFileFilter(new ExFileFilter("jpg","Snapshot ( .jpg )"));
      int returnVal = fileDialog.showSaveDialog(this);
      if(returnVal == JFileChooser.APPROVE_OPTION) {
        String file =  fileDialog.getCurrentDirectory() 
          + System.getProperty("file.separator") 
          + fileDialog.getSelectedFile().getName();
        if (file.indexOf(".jpg") == -1) 
        {
          file = file + ".jpg";
        }
        //System.out.println("You chose to save this file: " + file);
        try 
        {
          ImageIO.write((BufferedImage)panel.getImage(), "jpg", new File(file));
        }
        catch (IOException ex)
        {
          ex.printStackTrace();
        }
      }
    }

    if (e.getSource() == printMenu)
    {
      print();
    }
  }

  // **************************************************************************
  // printing section
  // **************************************************************************
  public void print() {
    PrinterJob printJob = PrinterJob.getPrinterJob();
    printJob.setPrintable(this);
    if (printJob.printDialog())
      try {
        printJob.print();
      } catch(PrinterException pe) {
        System.out.println("Error printing: " + pe);
      }
  }

  public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
    if (pageIndex > 0) {
      return(NO_SUCH_PAGE);
    } else {
      Graphics2D g2d = (Graphics2D)g;
      g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
      disableDoubleBuffering(panel);
      panel.paint(g2d);
      enableDoubleBuffering(panel);
      return(PAGE_EXISTS);
    }
  }

  public static void disableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(false);
  }

  public static void enableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(true);
  } 



  // **************************************************************************
  // mouse litening section
  // **************************************************************************
  
  public void mouseClicked(MouseEvent e)
  {
    if (e.getButton() == MouseEvent.BUTTON3)
    {
      //rightButtonMenu.setVisible(true);
      rightButtonMenu.show(e.getComponent(),
          e.getX(), e.getY());
    }
    if(e.getButton() == MouseEvent.BUTTON1)
    {
      if( e.getX() > panel.getXOffset() 
          && e.getX() < (super.getWidth()-panel.getXOffset())
          && e.getY() > panel.getYOffset() 
          && e.getY() < (super.getHeight()-panel.getYOffset()))
      {
        coordMenuItem.setText("" 
            + numberFormat.format(panel.reverseXCoord(e.getX())) 
            + ", " 
            + numberFormat.format(panel.reverseYCoord(e.getY())));
        coordButtonMenu.show(e.getComponent(), e.getX(), e.getY());
      }
    }
  }

  public void mouseEntered(MouseEvent e)
  { }
  public void mouseExited(MouseEvent e)
  { }
  public void mousePressed(MouseEvent e)
  { }
  public void mouseReleased(MouseEvent e) 
  { }

  public void mouseDragged(MouseEvent e)
  { 
    if( e.getX() > panel.getXOffset() 
        && e.getX() < (super.getWidth()-panel.getXOffset())
        && e.getY() > panel.getYOffset() 
        && e.getY() < (super.getHeight()-panel.getYOffset()))
    {
      coordMenuItem.setText("" 
          + numberFormat.format(panel.reverseXCoord(e.getX())) 
          + ", " 
          + numberFormat.format(panel.reverseYCoord(e.getY())));
      coordButtonMenu.show(e.getComponent(), e.getX(), e.getY());
    }
  }

  public void mouseMoved(MouseEvent e)
  { }

  public void drawLegend()
  {
    panel.drawLegend();
  }

  public void setXLabel(String string)
  {
    panel.setXLabel(string, "", "");
  }

  public void setXLabel(String string, String subscript)
  {
    panel.setXLabel(string, subscript);
  }

  public void setXLabel(String string, String subscript, String superscript)
  {
    panel.setXLabel(string, subscript, superscript);
  }


  public void setYLabel(String string)
  {
    panel.setYLabel(string, "", "");
  }


  public void setYLabel(String string, String subscript)
  {
    panel.setYLabel(string, subscript, "");
  }


  public void setYLabel(String string, String subscript, String superscript)
  {
    panel.setYLabel(string, subscript, superscript);
  }



  // in the x-range, y-range coordintes, defined by the data panel
  public void drawPoint(double x, double y)
  {
    panel.drawPoint(x,y);
  }

  public void drawPoint(double x, double y, Color color)
  {
    panel.drawPoint(x, y, color);
  }

  public void drawBigPoint(double x, double y, Color color)
  {
    panel.drawBigPoint(x, y, color);
  }


  public int getDrawingHeight()
  {
    return panel.getDrawingHeight();
  }

  public int getDrawingWidth()
  {
    return panel.getDrawingWidth();
  }

  public void drawPeriodLegend(Vector periodVector, Color[] colors)
  {
    panel.drawPeriodLegend(periodVector, colors);
  }

  public void drawTransientPlotLegend(Vector legendString, Color[] colors)
  {
    panel.drawTransientPlotLegend(legendString, colors);
  }

  public void drawLine(double x0_p, double y0_p, double x1_p, double y1_p)
  {
    panel.drawLine( x0_p,  y0_p,  x1_p,  y1_p);
  }

  public void drawLine(double x0_p, double y0_p, double x1_p, double y1_p, 
      Color color)
  {
    panel.drawLine( x0_p,  y0_p,  x1_p,  y1_p, color);
  }

  public void drawFilledCircle(double x, double y, double radius, Color color)
  {
    panel.drawFilledCircle(x, y, radius, color);
  }

  public void drawFilledCircle(double x, double y, double radius)
  {
    panel.drawFilledCircle(x, y, radius, Color.black);
  }


  private void clearFileFilters()
  {

    javax.swing.filechooser.FileFilter[] filefilters = fileDialog.getChoosableFileFilters();
    if(filefilters != null)
    {
      for(int i=0; i < filefilters.length; i++)
      {
        fileDialog.removeChoosableFileFilter(filefilters[i]);
      }
    }

  }



}
