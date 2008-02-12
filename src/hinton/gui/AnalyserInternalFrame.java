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


package hinton.gui;

import hinton.ambassador.RobotStruct;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Vector;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import util.io.ExFileFilter;

import cholsey.Net;

public class AnalyserInternalFrame extends JInternalFrame implements
InternalFrameListener, MouseListener, ActionListener, ItemListener
{
  private MyPanel panel = new MyPanel();
  private double fitness = 0;
  private NumberFormat numberFormat = NumberFormat.getInstance(Locale.UK);
  private Vector openWindows = null;
  private Net net = null;
  private RobotStruct robotStruct = null;
  private JPopupMenu rightButtonMenu = new JPopupMenu("Select Neuron");
  private JMenu rightButtonInputNeuronsMenu 
                                     = new JMenu("Input Neuron");
  private JMenu rightButtonOutputNeuronsMenu 
                                     = new JMenu("Output Neuron");
  private JMenu rightButtonHiddenNeuronsMenu 
                                     = new JMenu("Hidden Neuron");
  private JMenuItem maxDataMenu  = null;
  private JMenuItem txtOutputMenu  = null;
  private Vector rightButtonInputNeuronsMenuItems = new Vector();
  private Vector rightButtonOutputNeuronsMenuItems = new Vector();
  private Vector rightButtonHiddenNeuronsMenuItems = new Vector();

  private Vector inputColors  = new Vector();
  private Vector outputColors = new Vector();
  private Vector hiddenColors = new Vector();

  private Vector dataPoints   = new Vector();

  private int MIN_X_SIZE = 250;
  private int MIN_Y_SIZE = 150;

  private int maxDataPoints = 100;

  public AnalyserInternalFrame(String name, 
      int index, 
      Vector p_openWindows,
      Net net,
      RobotStruct robotStruct,
      Vector inputColors,
      Vector outputColors,
      Vector hiddenColors,
      Vector dataPoints)
  {
    super(name + " " + index,
        true, //resizable
        true, //closable
        true, //maximizable
        true);//iconifiable
    setSize(MIN_X_SIZE, MIN_Y_SIZE);
    setMinimumSize(new Dimension(MIN_X_SIZE,MIN_Y_SIZE));
    setLocation(index*20, index*20);
    setContentPane(panel);

    this.addMouseListener(this);
    this.addInternalFrameListener(this);

    this.openWindows  = p_openWindows;
    this.net          = net;
    this.robotStruct  = robotStruct;

    this.inputColors  = inputColors;
    this.outputColors = outputColors;
    this.hiddenColors = hiddenColors;

    this.dataPoints   = dataPoints; // 0 = input, 1 = output, 2 = hidden

    updateRightMouseMenus();
    maxDataMenu = 
      new JMenuItem("DataPoints " + maxDataPoints);
    maxDataMenu.addActionListener(this);

    txtOutputMenu = 
      new JMenuItem("Textoutput ");
    txtOutputMenu.addActionListener(this);

    rightButtonMenu.add(rightButtonInputNeuronsMenu);
    rightButtonMenu.add(rightButtonOutputNeuronsMenu);
    rightButtonMenu.add(rightButtonHiddenNeuronsMenu);
    rightButtonMenu.add(maxDataMenu);
    rightButtonMenu.add(txtOutputMenu);
    
  }


  public void actionPerformed(ActionEvent e)
  {
    if(e.getSource() == maxDataMenu)
    {
      String input = JOptionPane.showInputDialog(
          "How many data points?","" + maxDataPoints);
      if(input != null)
      {
        maxDataPoints = Integer.parseInt(input);
        maxDataMenu.setText("DataPoints " + maxDataPoints);
      }
    }
    if(e.getSource() == txtOutputMenu)
    {
      String outputfilename = new String();
      JFileChooser fileDialog = new JFileChooser(".");
      fileDialog.addChoosableFileFilter(new ExFileFilter("xml","Neural Net ( .xml )"));
      int returnVal = fileDialog.showSaveDialog(this);
      if(returnVal == JFileChooser.APPROVE_OPTION) {

        outputfilename = new String(fileDialog.getCurrentDirectory() +
            System.getProperty("file.separator") +
            fileDialog.getSelectedFile().getName());

        if(outputfilename.indexOf(".") == -1)
        {
          outputfilename = new String(outputfilename+".xml");
        }

        File xmlFile = new File(outputfilename);
        panel.writeToFile(xmlFile);
      }

    }
  }

  public void itemStateChanged(ItemEvent e)
  {
    if(rightButtonHiddenNeuronsMenuItems.indexOf(e.getSource()) != -1 ||
       rightButtonInputNeuronsMenuItems.indexOf(e.getSource())  != -1 ||
       rightButtonOutputNeuronsMenuItems.indexOf(e.getSource()) != -1)
    {
      JCheckBoxMenuItem menu = (JCheckBoxMenuItem)e.getSource();
    }
  }


  public void internalFrameOpened(InternalFrameEvent e)
  {

  }

  public void internalFrameClosing(InternalFrameEvent e) 
  {
    openWindows.setElementAt(null, openWindows.indexOf(this));
    setVisible(false);
  };

  public void internalFrameClosed(InternalFrameEvent e) 
  { };
  public void internalFrameIconified(InternalFrameEvent e) {};
  public void internalFrameDeiconified(InternalFrameEvent e) {};
  public void internalFrameActivated(InternalFrameEvent e) {};
  public void internalFrameDeactivated(InternalFrameEvent e) {};

  // **************************************************************************  
  // mouse menu stuff
  // **************************************************************************  
  public void mouseClicked(MouseEvent e)
  {
    if (e.getButton() == MouseEvent.BUTTON3)
    {
      //updateRightMouseMenus();
      rightButtonMenu.show(e.getComponent(),
          e.getX(), e.getY());
    }

    /*
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
    */
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
    /*
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
    */
  }

  public void mouseMoved(MouseEvent e)
  { }

  public void setNet(Net net)
  {
    this.net = net;
    updateRightMouseMenus();
  }

  public void setRobotStruct(RobotStruct robotStruct)
  {
    this.robotStruct = robotStruct;
    updateRightMouseMenus();
  }


  private void updateRightMouseMenus()
  {

    rightButtonInputNeuronsMenu.removeAll();
    rightButtonOutputNeuronsMenu.removeAll();
    rightButtonHiddenNeuronsMenu.removeAll();

    rightButtonInputNeuronsMenuItems.removeAllElements();
    rightButtonOutputNeuronsMenuItems.removeAllElements();
    rightButtonHiddenNeuronsMenuItems.removeAllElements();


    if(net == null)
    {
      System.out.println("net is null");
      return;
    }

    if(robotStruct == null)
    {
      for(int i=0;i<net.getInputNeurons().size();i++)
      {
        System.out.println("Input Neuron " + i);
        JCheckBoxMenuItem inputNeuronMenu = 
          new JCheckBoxMenuItem("Input Neuron " + i);
        rightButtonInputNeuronsMenu.add(inputNeuronMenu);
        rightButtonInputNeuronsMenuItems.add(inputNeuronMenu);
        inputNeuronMenu.addItemListener(this);
      }

      for(int i=0;i<net.getOutputNeurons().size();i++)
      {
        System.out.println("Output Neuron " + i);
        JCheckBoxMenuItem outputNeuronMenu = 
          new JCheckBoxMenuItem("Output Neuron " + i);
        rightButtonOutputNeuronsMenu.add(outputNeuronMenu);
        rightButtonOutputNeuronsMenuItems.add(outputNeuronMenu);
        outputNeuronMenu.addItemListener(this);
      }

      for(int i=0;i<net.getHiddenNeurons().size();i++)
      {
        System.out.println("Hidden Neuron " + i);
        JCheckBoxMenuItem hiddenNeuronMenu = 
          new JCheckBoxMenuItem("Hidden Neuron " + i);
        rightButtonHiddenNeuronsMenu.add(hiddenNeuronMenu);
        rightButtonHiddenNeuronsMenuItems.add(hiddenNeuronMenu);
        hiddenNeuronMenu.addItemListener(this);
      }

    }
    else
    {
      for(int i=0;i<net.getInputNeurons().size();i++)
      {
        JCheckBoxMenuItem inputNeuronMenu = 
          new JCheckBoxMenuItem(robotStruct.getOutputObject(i));
        rightButtonInputNeuronsMenu.add(inputNeuronMenu);
        rightButtonInputNeuronsMenuItems.add(inputNeuronMenu);
        inputNeuronMenu.addItemListener(this);
      }

      for(int i=0;i<net.getOutputNeurons().size();i++)
      {
        JCheckBoxMenuItem outputNeuronMenu = 
          new JCheckBoxMenuItem(robotStruct.getInputObject(i));
        rightButtonOutputNeuronsMenu.add(outputNeuronMenu);
        rightButtonOutputNeuronsMenuItems.add(outputNeuronMenu);
        outputNeuronMenu.addItemListener(this);
      }

      for(int i=0;i<net.getHiddenNeurons().size();i++)
      {
        JCheckBoxMenuItem hiddenNeuronMenu = 
          new JCheckBoxMenuItem("Hidden Neuron " + i);
        rightButtonHiddenNeuronsMenu.add(hiddenNeuronMenu);
        rightButtonHiddenNeuronsMenuItems.add(hiddenNeuronMenu);
        hiddenNeuronMenu.addItemListener(this);
      }

    }

  }


  public void draw()
  {
    if(!isVisible() || dataPoints.size() == 0)
    {
      return;
    }

    Vector dataVector = new Vector();
    Vector colorVector = new Vector();
    Vector namesVector = new Vector();

    Vector inputDataPoints = (Vector)dataPoints.elementAt(0); 
    for(int i=0;i<rightButtonInputNeuronsMenuItems.size();i++)
    {
      JCheckBoxMenuItem cb =
        (JCheckBoxMenuItem)rightButtonInputNeuronsMenuItems.elementAt(i);
      if(cb != null && cb.getState() == true) // is selected
      {
        dataVector.add((Vector)inputDataPoints.elementAt(i));
        colorVector.add((Color)inputColors.elementAt(i % inputColors.size()));
        namesVector.add(cb.getText() + " (I)");
      }
    }

    Vector outputDataPoints = (Vector)dataPoints.elementAt(1); 
    if(outputDataPoints.size() > 0 && 
        rightButtonOutputNeuronsMenuItems.size() > 0)
    {
      for(int i=0;i<rightButtonOutputNeuronsMenuItems.size();i++)
      {
        JCheckBoxMenuItem cb =
          (JCheckBoxMenuItem)rightButtonOutputNeuronsMenuItems.elementAt(i);
        if(cb != null && cb.getState() == true) // is selected
        {
          dataVector.add((Vector)outputDataPoints.elementAt(i));
          colorVector.add((Color)outputColors.elementAt(i % outputColors.size()));
          namesVector.add(cb.getText() + "(O)");
        }
      }
    }

    Vector hiddenDataPoints = (Vector)dataPoints.elementAt(2); 
    if(hiddenDataPoints.size() > 0 && 
        rightButtonHiddenNeuronsMenuItems.size() > 0)
    {
      for(int i=0;i<rightButtonHiddenNeuronsMenuItems.size();i++)
      {
        JCheckBoxMenuItem cb =
          (JCheckBoxMenuItem)rightButtonHiddenNeuronsMenuItems.elementAt(i);
        if(cb != null && cb.getState() == true) // is selected
        {
          dataVector.add((Vector)hiddenDataPoints.elementAt(i));
          colorVector.add((Color)hiddenColors.elementAt(i % hiddenColors.size()));
          namesVector.add("Hidden " + i);
        }
      }
    }


    panel.setData(dataVector, colorVector, namesVector, maxDataPoints);

  }


  // **************************************************************************
  // **************************************************************************
  // MY PANEL
  // **************************************************************************
  // **************************************************************************


  // by default the y-range is scaled form -1 to 1
  private class MyPanel extends JPanel 
  {
    private final static int LEFT_OFFSET  = 50;
    private final static int RIGHT_OFFSET = 100;
    private final static int TOP_OFFSET = 25;
    private final static int BOTTOM_OFFSET = 25;
    private Point2D min = null;
    private Point2D max = null;
    private int paintWidth = 0; // for the data. W_p = width - 2 * x_offset
    private int paintHeight = 0; // for the data. H_p = height - 2 * y_offset
    private Image offscreen = null;

    private final static double Y_MIN =    -1;
    private final static double Y_MAX =   1;
    private double X_MIN =    0;
    private double X_MAX = 2000;

    private Vector data = null;
    private Vector colors = null;
    private Vector names = null;



    public MyPanel()
    {
      super();
    }

    public void paintComponent(Graphics g)
    {
      if(offscreen == null)
      {
        return;
      }
      g.drawImage(offscreen,0,0, this);
    }

    private void drawLegend()
    {
      Graphics g = offscreen.getGraphics();
      g.drawRect(LEFT_OFFSET,TOP_OFFSET,
          super.getWidth()-LEFT_OFFSET-RIGHT_OFFSET,
          super.getHeight()-TOP_OFFSET-BOTTOM_OFFSET);

      if(names != null)
      {
        Color oldColor = g.getColor();
        for(int i=0;i<names.size();i++)
        {
          String name = (String)names.elementAt(i);
          Color color = (Color)colors.elementAt(i);
          g.setColor(color);
          g.drawString(name,
              super.getWidth()-RIGHT_OFFSET + 5,
              TOP_OFFSET + 10 + (i*15));

        }
        g.setColor(oldColor);
      }

    }


    private int xCoord(double xValue)
    {
      return (int)(0.5 + LEFT_OFFSET+ 
        (super.getWidth() - (LEFT_OFFSET + RIGHT_OFFSET)) *
        (xValue - X_MIN)/(X_MAX-X_MIN));
    }

    private int yCoord(double yValue)
    {
      return super.getHeight() - (int)(
          0.5 +
          TOP_OFFSET +
        (super.getHeight() - (TOP_OFFSET + BOTTOM_OFFSET)) *
        (yValue - Y_MIN) / (Y_MAX-Y_MIN));
    }


    private void setData(Vector data, Vector colors, Vector names, int maxDataPoints)
    {
      this.data = data;
      this.colors = colors;
      this.names = names;
      this.X_MAX = maxDataPoints;
      offscreen = super.createImage(super.getWidth(), super.getHeight());
      draw();
      super.repaint();
    }

    private void writeToFile(File filename)
    {
      if(data == null || colors == null)
      {
        return;
      }
      int startIndex = 0;
      try
      {
        PrintWriter out = new PrintWriter(
            new OutputStreamWriter(
              new FileOutputStream(filename)));


        startIndex = Math.max(0, dataPoints.size()-(int)X_MAX-1);
        Vector dataPoints = (Vector)data.elementAt(0);
        for(int i=startIndex;i<dataPoints.size()-1;i++)
        {
          for(int dataIndex = 0; dataIndex < data.size(); dataIndex++)
          {
            dataPoints = (Vector)data.elementAt(dataIndex);

            double y0 = (((Double)dataPoints.elementAt(i)).doubleValue());
            /*
               System.out.print("From (" 
               + (i-startIndex) 
               + ", " + (((Double)dataPoints.elementAt(i)).doubleValue())
               + ") -> (" + (i+1 - startIndex)
               + ", " + (((Double)dataPoints.elementAt(i+1)).doubleValue())
               + ") we draw: ");

               System.out.println("(" +x0 + ", " + y0 + ") -> (" + x1 +", " +y1
               +")");
             */
            //g.drawLine(x0,y0,x1,y1);
            out.print("" + y0 + " ");
          }
          out.println("");
        }
        out.flush();
        out.close();
      }
      catch(IOException e)
      {
        e.printStackTrace();
      }
    }

    private void draw()
    {
      if(data == null || colors == null)
      {
        return;
      }
      Graphics g = offscreen.getGraphics();
      Color oldColor = g.getColor();
      int startIndex = 0;

      drawLegend();
      
      for(int dataIndex = 0; dataIndex < data.size(); dataIndex++)
      {
        Vector dataPoints = (Vector)data.elementAt(dataIndex);
        Color color = (Color)colors.elementAt(dataIndex);
        g.setColor(color);
        startIndex = Math.max(0, dataPoints.size()-(int)X_MAX-1);
        for(int i=startIndex;i<dataPoints.size()-1;i++)
        {
          int x0 = xCoord(i - startIndex);
          int x1 = xCoord(i+1 - startIndex);
          int y0 = yCoord(((Double)dataPoints.elementAt(i)).doubleValue());
          int y1 = yCoord(((Double)dataPoints.elementAt(i+1)).doubleValue());
          /*
             System.out.print("From (" 
             + (i-startIndex) 
             + ", " + (((Double)dataPoints.elementAt(i)).doubleValue())
             + ") -> (" + (i+1 - startIndex)
             + ", " + (((Double)dataPoints.elementAt(i+1)).doubleValue())
             + ") we draw: ");

             System.out.println("(" +x0 + ", " + y0 + ") -> (" + x1 +", " +y1
             +")");
           */
          g.drawLine(x0,y0,x1,y1);
        }
      }
      g.setColor(oldColor);
    }
  }

}
