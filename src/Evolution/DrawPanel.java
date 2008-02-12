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

package Evolution;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;



public  class DrawPanel extends JPanel implements  MouseListener, MouseMotionListener, ActionListener
{
  private NumberFormat numberFormat = NumberFormat.getInstance(Locale.UK);


  private int X_A;
  private int X_E;
  private int Y_A;
  private int Y_E;

  private double X_MAX;
  private double X_MIN;
  private double Y_MAX;
  private double Y_MIN;

  private Graphics g = null;

  private BufferedImage offscreen = null;

  private JPopupMenu rightButtonMenu = new JPopupMenu("Menu");
  private JMenuItem saveJPGMenu = new JMenuItem("save jpg");
  private JMenuItem savePNGMenu = new JMenuItem("save png");
  private JMenuItem saveTxt     = new JMenuItem("save txt");

  private JPopupMenu middleButtonMenu = new JPopupMenu("Middle");
  private JMenuItem  selMaxPerf       = new JMenuItem("max.Perf. (out / sys)");
  private JMenuItem  selAvgPerf       = new JMenuItem("avg. +/- var. sys.-perf.");
  private JMenuItem  selPopSize       = new JMenuItem("curr. pop. size");
  private JMenuItem  selVetAge        = new JMenuItem("veteran's age");
  private JMenuItem  selAgeBest       = new JMenuItem("age best");
  private JMenuItem  selAvgAge        = new JMenuItem("avg. +/- var. age");
  private JMenuItem  selMaxHidden     = new JMenuItem("nmb. hidden best");
  private JMenuItem  selAvgHidden     = new JMenuItem("avg. +/- var. nmb. hidden");
  private JMenuItem  selMaxSyn        = new JMenuItem("nmb. synapses best");
  private JMenuItem  selAvgSyn        = new JMenuItem("avg. +/- var. nmb. synapses");


  private JPopupMenu coordButtonMenu = new JPopupMenu("Coord");
  private JMenuItem coordMenuItem = new JMenuItem("coord");


  private ActionListener  parentPanel;


  public DrawPanel(){
    super();

    this.addMouseListener(this);
    this.addMouseMotionListener(this);

    saveJPGMenu.addActionListener(this);
    saveTxt.addActionListener(this);
    savePNGMenu.addActionListener(this);
    selMaxPerf.addActionListener(this);
    selAvgPerf.addActionListener(this);
    selMaxHidden.addActionListener(this);
    selAvgHidden.addActionListener(this);
    selMaxSyn.addActionListener(this);
    selAvgSyn.addActionListener(this);
    selAgeBest.addActionListener(this);
    selAvgAge.addActionListener(this);
    selVetAge.addActionListener(this);
    selPopSize.addActionListener(this);




    rightButtonMenu.add(saveJPGMenu);
    rightButtonMenu.add(savePNGMenu);
    rightButtonMenu.add(saveTxt);

    middleButtonMenu.add(selMaxPerf);
    middleButtonMenu.add(selAvgPerf);
    middleButtonMenu.add(selPopSize);
    middleButtonMenu.add(selAgeBest);
    middleButtonMenu.add(selAvgAge);
    middleButtonMenu.add(selVetAge);
    middleButtonMenu.add(selMaxHidden);
    middleButtonMenu.add(selAvgHidden);
    middleButtonMenu.add(selMaxSyn);
    middleButtonMenu.add(selAvgSyn);

    coordButtonMenu.add(coordMenuItem);
    coordMenuItem.addActionListener(this);


    this.numberFormat.setMinimumFractionDigits(2);
    this.numberFormat.setMaximumFractionDigits(2);
  }

  /*
  public int print(Graphics gp, PageFormat pf, int pi) throws PrinterException {
  if (pi >= 1) 
  {
  return Printable.NO_SUCH_PAGE;
  }
  gp.drawImage(offscreen,0,0, this);
  return Printable.PAGE_EXISTS;
  }
   */

  public BufferedImage getImage(){
    return offscreen;
  }

  public void setRange(double xMin, double xMax, double yMin, double yMax){
    this.X_MIN = xMin;
    this.X_MAX = xMax;
    this.Y_MIN = yMin;
    this.Y_MAX = yMax;
  }

  public void drawPoint(double x_p, double y_p){
    if (g == null)
    {
      init();
    }

    g.setColor(Color.BLACK);
    int xP = xCoord(x_p);
    int yP = yCoord(y_p);
    g.drawLine(xP,yP,xP,yP);
    super.repaint(0,xP,yP,1,1);
  }


  public void drawLine(double xa, double ya, double xe, double ye, Color color){
    if (g == null)
    {
      init();
    }

    g.setColor(color);
    int XA = xCoord(xa);
    int YA = yCoord(ya);
    int XE = xCoord(xe);
    int YE = yCoord(ye);
    g.drawLine(XA,YA,XE,YE);

    super.repaint();
    //System.out.println("( " + XA + "," + YA + " )---( " + XE + "," + YE + " )");
  }

  public void drawFatLine(double xa, double ya, double xe, double ye, Color color){
    if (g == null)
    {
      init();
    }

    g.setColor(color);
    int XA = xCoord(xa);
    int YA = yCoord(ya);
    int XE = xCoord(xe);
    int YE = yCoord(ye);
    g.drawLine(XA,YA,XE,YE);
    XA = xCoord(xa);
    YA = yCoord(ya)+1;
    XE = xCoord(xe);
    YE = yCoord(ye)+1;
    g.drawLine(XA,YA,XE,YE);
    XA = xCoord(xa);
    YA = yCoord(ya)-1;
    XE = xCoord(xe);
    YE = yCoord(ye)-1;
    g.drawLine(XA,YA,XE,YE);
    super.repaint();
  }


  public void drawPoint(double x_p, double y_p, Color color){

    if (g == null)
    {
      init();
    }

    g.setColor(color);
    int xP = xCoord(x_p);
    int yP = yCoord(y_p);
    g.drawLine(xP,yP,xP,yP);
    super.repaint(0,xP,yP,1,1);
  }

  public void drawBigPoint(double x_p, double y_p, Color color){

    if (g == null)
    {
      init();
    }

    g.setColor(color);
    int xP = xCoord(x_p);
    int yP = yCoord(y_p);
    g.fillRect(xP-1,yP-1,3,3);
    super.repaint(0,xP,yP,1,1);
  }

  public void drawBigPoint(double x_p, double y_p){

    if (g == null)
    {
      init();
    }

    g.setColor(Color.BLACK);
    int xP = xCoord(x_p);
    int yP = yCoord(y_p);
    g.fillRect(xP-1,yP-1,3,3);
    super.repaint(0,xP,yP,1,1);

    //System.out.println(xP + "," + yP);
  }




  // MUST BE CALLED AFTER!!! the panel is visible!!!
  public void init(){
    offscreen = (BufferedImage)super.createImage(super.getWidth(), super.getHeight());
    g = offscreen.getGraphics();
    // g.setColor(Color.WHITE);
    // g.drawRect(0,0,super.getWidth(),super.getHeight());
    // g.setColor(Color.BLACK);
    // super.repaint();

    X_A = 60;
    X_E = super.getWidth() - 4;
    Y_A = 10;
    Y_E = super.getHeight() - 6;

  }

  public void init(ActionListener p){
    parentPanel = p;
    this.init();
  }

  public void drawLegend(){
    if (g == null)
    {
      init();
    }

    g.setColor(Color.BLACK);

    g.clearRect(0,0,super.getWidth(),super.getHeight());

    /* draw a fat frame */
    g.drawRect((X_A-1), (Y_A-5), (X_E+1) - (X_A-1), (Y_E+5) - (Y_A-3));
    g.drawRect((X_A-2), (Y_A-6), (X_E+2) - (X_A-2), (Y_E+6) - (Y_A-4));
    g.drawRect((X_A-3), (Y_A-7), (X_E+3) - (X_A-3), (Y_E+7) - (Y_A-5));

    drawXmax();
    g.fillRect(X_A - 8, Y_A - 1, 6, 3);

    drawXmin();
    g.fillRect(X_A - 8, Y_E - 1, 6, 3);

    super.repaint();
  }


  public void drawXmax(){
    g.drawString(numberFormat.format(Y_MAX),
        X_A - 50, Y_A + 3);
  }

  public void drawXmin(){
    g.drawString(numberFormat.format(Y_MIN),
        X_A - 50, Y_E + 3);
  }


  public int xCoord(double xValue){
    double x;

    double delta = ((double)(X_A - X_E)) / (X_MIN - X_MAX);
    x = delta*(xValue - X_MAX) + ((double) X_E);
    return ((int) x);
  }


  public int yCoord(double yValue){
    double y;

    double delta = ((double)(Y_E - Y_A)) / (Y_MIN - Y_MAX);
    y =  delta*(yValue - Y_MAX) + ((double) Y_A);
    return ((int) y);
  }

  public double reverseXCoord(int lxCoord){
    double x; 

    double delta =  (X_MIN - X_MAX) / ((double)(X_A - X_E));
    x = delta*((double)(lxCoord - X_E)) + X_MAX;
    return x;
  }

  public double reverseYCoord(int lyCoord){
    double y; 

    double delta = (Y_MIN - Y_MAX) / ((double) (Y_E - Y_A));
    y = delta*((double)(lyCoord - Y_A)) + Y_MAX;
    return y;
  }


  public void paintComponent(Graphics g){

    if(offscreen == null)
    {
      return;
    }

    g.drawImage(offscreen,0,0, this);
  }

  // **************************************************************************
  // mouse litening section
  // **************************************************************************

  public void mouseClicked(MouseEvent e)
  {
    if (e.getButton() == MouseEvent.BUTTON2)
    {
      rightButtonMenu.show(e.getComponent(),
          e.getX(), e.getY());
    }


    if (e.getButton() == MouseEvent.BUTTON3)
    {
      middleButtonMenu.show(e.getComponent(),
          e.getX(), e.getY());
    }



    if(e.getButton() == MouseEvent.BUTTON1)
    {
      coordMenuItem.setText("" 
          + ((int)(0.5 + reverseXCoord(e.getX())))  
          + ", " 
          + numberFormat.format(reverseYCoord(e.getY())));
      coordButtonMenu.show(e.getComponent(), e.getX(), e.getY());
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
    coordMenuItem.setText("" 
        + ((int)(0.5 + reverseXCoord(e.getX())))  
        + ", " 
        + numberFormat.format(reverseYCoord(e.getY())));
    coordButtonMenu.show(e.getComponent(), e.getX(), e.getY());
  }

  public void mouseMoved(MouseEvent e){ 
  }


  // ActionListener methods
  public void actionPerformed(ActionEvent e)
  {
    if (e.getSource() == coordMenuItem)
    {
      ;;
    }

    if(e.getSource() == selAgeBest){
      this.parentPanel.actionPerformed(new ActionEvent(this,61,"SHOW_AGE_BEST"));
    }

    if(e.getSource() == selAvgAge){
      this.parentPanel.actionPerformed(new ActionEvent(this,62,"SHOW_AVG_AGE"));
    }

    if(e.getSource() == selVetAge){
      this.parentPanel.actionPerformed(new ActionEvent(this,68,"SHOW_VET_AGE"));
    }

    if(e.getSource() == selMaxPerf){
      this.parentPanel.actionPerformed(new ActionEvent(this,61,"SHOW_MAX_PERF"));
    }

    if(e.getSource() == selAvgPerf){
      this.parentPanel.actionPerformed(new ActionEvent(this,62,"SHOW_AVG_PERF"));
    }

    if(e.getSource() == selMaxHidden){
      this.parentPanel.actionPerformed(new ActionEvent(this,63,"SHOW_MAX_HIDD"));
    }

    if(e.getSource() == selAvgHidden){
      this.parentPanel.actionPerformed(new ActionEvent(this,64,"SHOW_AVG_HIDD"));
    }

    if(e.getSource() == selMaxSyn){
      this.parentPanel.actionPerformed(new ActionEvent(this,65,"SHOW_MAX_SYN"));
    }

    if(e.getSource() == selAvgSyn){
      this.parentPanel.actionPerformed(new ActionEvent(this,66,"SHOW_AVG_SYN"));
    }

    if(e.getSource() == selPopSize){
      this.parentPanel.actionPerformed(new ActionEvent(this,66,"SHOW_POP_SIZE"));
    }

    if(e.getSource() == saveTxt){
      this.parentPanel.actionPerformed(new ActionEvent(this,67,"SAVE_TXT"));
    }


    if (e.getSource() == savePNGMenu)
    {
      JFileChooser fileDialog = new JFileChooser(".");
      //fileDialog.addChoosableFileFilter(new ExFileFilter("png","Snapshot ( .png )"));
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
          ImageIO.write(getImage(), "png", new File(file));
        }
        catch (IOException ex)
        {
          ex.printStackTrace();
        }
      }
    }

    if (e.getSource() == saveJPGMenu)
    {
      JFileChooser fileDialog = new JFileChooser(".");
      //fileDialog.addChoosableFileFilter(new ExFileFilter("jpg","Snapshot ( .jpg )"));
      int returnVal = fileDialog.showSaveDialog(this);
      if(returnVal == JFileChooser.APPROVE_OPTION) 
      {
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
          ImageIO.write((BufferedImage)getImage(), "jpg", new File(file));
        }
        catch (IOException ex)
        {
          ex.printStackTrace();
        }
      }
    }

  }

}













