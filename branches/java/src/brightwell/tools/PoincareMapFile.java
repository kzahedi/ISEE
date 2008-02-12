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

package brightwell.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.swing.ProgressMonitor;

import brightwell.analyser.Tool;
import brightwell.gui.drawingplane.DrawingPlane;

public class PoincareMapFile extends Tool
{ 
 
  // **************************************************************************
  // predefined variables, that are accessable by default. no declaraion or
  // initialisation needed. they are just there.
  // **************************************************************************

  // **************************************************************************
  // boolean doAnalysis 
  //
  // is true as long as the tool should be running. is set false, when the user
  // presses the stop button
  // **************************************************************************

  // **************************************************************************
  // Net net 
  //
  // contains the neural net loaded or constructed by the user, or is null.
  // should not be accessed, when needsNet() returns false (see below)
  // **************************************************************************

  // **************************************************************************
  // int convergenceIterations 
  //
  // the number of convergence iterations, that the user has set for the
  // analysis, i.e. attractor map, feigenbaum
  // **************************************************************************
  
  // **************************************************************************   
  // int drawIterations 
  // 
  // the number of draw iteration, the user wants to be displayed, i.e.
  // feigenbaum
  // **************************************************************************   

  // **************************************************************************   
  // double xStart 
  //
  // the start value of the x-range, also the start value for the drawing
  // coordiante system, used by the drawing plane
  //
  // the drawing coordinate system is defined by:
  //  (xStart, yEnd)   +------+ (xEnd, yEnd)
  //                   |      |
  //                   |      |
  //  (xStart, yStart) +------+ (xEnd, yStart)
  //
  // **************************************************************************   

  // **************************************************************************   
  // double xEnd   
  //
  // the end value of the x-range, also the end value for the drawing
  // coordiante system, used by the drawing plane
  //
  // the drawing coordinate system is defined by:
  //  (xStart, yEnd)   +------+ (xEnd, yEnd)
  //                   |      |
  //                   |      |
  //  (xStart, yStart) +------+ (xEnd, yStart)
  //
  // **************************************************************************   
   

  // **************************************************************************   
  // double yStart 
  //
  // the start value of the y-range, also the start value for the drawing
  // coordiante system, used by the drawing plane
  //
  // the drawing coordinate system is defined by:
  //  (xStart, yEnd)   +------+ (xEnd, yEnd)
  //                   |      |
  //                   |      |
  //  (xStart, yStart) +------+ (xEnd, yStart)
  //
  // **************************************************************************   

  // **************************************************************************   
  // double yEnd   
  //
  // the end value of the y-range, also the end value for the drawing
  // coordiante system, used by the drawing plane
  //
  // the drawing coordinate system is defined by:
  //  (xStart, yEnd)   +------+ (xEnd, yEnd)
  //                   |      |
  //                   |      |
  //  (xStart, yStart) +------+ (xEnd, yStart)
  // **************************************************************************   

  // **************************************************************************  
  // double stepsX 
  //
  // the displaying width of the analysing panel, corresponding the number of
  // steps in x-direction, in pixels.
  //
  //     +-------------------+ ---
  //     |                   |  |
  //     |                   |  stepsY = height
  //     |                   |  |
  //     +-------------------+ ---
  //
  //     |-- stepsX = width--|
  // **************************************************************************  
   
  // **************************************************************************  
  // double stepsY 
  //
  // the displaying width of the analysing panel, corresponding the number of
  // steps in y-direction, in pixels.
  //
  //     +-------------------+ ---
  //     |                   |  |
  //     |                   |  stepsY = height
  //     |                   |  |
  //     +-------------------+ ---
  //
  //     |-- stepsX = width--|
  // **************************************************************************  

  // **************************************************************************
  // double dx 
  //
  // the step width in x direction. dx = (xEnd - yStart) / stepsX
  // **************************************************************************

  // **************************************************************************
  // double dy
  //
  // the step width in y direction. dy = (yEnd - yStart) / stepsY
  // **************************************************************************
    
  // **************************************************************************
  // end of predefined variables
  // **************************************************************************

  // reserve name for the drawinPlane that will be used to display data
  private DrawingPlane dp = null;

  private final static int COLUMNS = 2;

  private final static int USE_LINES  = 0;
  private final static int USE_POINTS = 1;

  // **************************************************************************
  // the following functions are required by Brightwell. These are the minimum
  // set of functions to define a tool within Brightwell
  // **************************************************************************

  /**
   * This function indicates Brightwell, if this tool needs a neural net. If a
   * neural net is needed, return true. If no neural net is needed return false.
   * Brightwell will ask for this value, when the run button is pressed. In case
   * a tool need a neural net, Brightwell will check if a net is selected or
   * created, and only proceeds, if so.
   * @return   true/false
   */
  public boolean needsNet()
  {
    return false;
  }

  /**
   * This function returns the name displayed in the tabbed pane. 
   */
  public String getToolName()
  {
    return "Poincare Map (File Input)";
  }

  /**
   * This function returns a description of the tool which is displayed, when
   * the mouse is placed over a tool name for some seconds.
   */
  public String getToolDescription()
  { 
    return "Poincare Map from data file";
  }

  /*
   * Initialising the Tool.
   *
   * <ul>
   *  <li> setup initial variables, if needed </li>
   *  <li> setup input panel if needed</li>
   *  <li> ... </li>
   * </ul>
   *
   * This function is called only onve, at the startup of Brightwell.
   *
   */
  public void init()
  {

    // all names are the names, which are displayed, and which reference to the
    // values of the selection field.
    // f.e. addString("your name","") displays: 
    // your name: ________ 
    // on the screen. if you want to access the string entered, you will have to
    // ask with
    // String string = getString("your name"), and you will receive the name
    // entered by the user. get-functions can only be called in the
    // doAnalysis-function (see below)
    String[] comboBoxEntries =  
    {
      "lines",
      "points"
    };


    // addComboBox: name, list of entries, initially selected entry
    addFileChooser("File:","");

    addInteger("X", 0, 1, 0);
    addInteger("Y", 0, 1, 1);
    addInteger("Start", 0, Integer.MAX_VALUE, 1);
    addInteger("End", 0, Integer.MAX_VALUE, 1000);
    addString("XLabel","i_0");
    addString("YLabel","o_2");
    addComboBox("Use", comboBoxEntries, 0);

    
    // setToolPriority defined the order of the tools in the tabbed pane. tools
    // with lower numbers will appear first.
    setToolPriority(20);
  }


  /**
   * This function is called after all initialisation is done. In this function
   * the main analysis routine is processed. It is called, when the user presses
   * the run-button, and if the check for a net has been positive.
   * Include your analysis here.
   */
  public void doAnalysis()
  {
    // first of all, we need a drawing panel

    // now we access the data, that was requested by the user
    String filename  = getFileChooser("File:");
    String xLabel    = getString("XLabel");
    String yLabel    = getString("YLabel");
    int xIndex       = getInteger("X");
    int yIndex       = getInteger("Y");
    int startIndex   = getInteger("Start");
    int endIndex     = getInteger("End");
    int dataCount    = endIndex - startIndex;
    int use          = getComboBoxIndex("Use");


    if(filename == null)
    {
      // TODO
    }

    if(dataCount <= 0)
    {
      // TODO
    }

    ProgressMonitor progressMonitor = new ProgressMonitor(
        parent, "Finding First Entry", "Index", 0, 100);
    progressMonitor.setMillisToDecideToPopup(100); // 10
    progressMonitor.setMillisToPopup(500); // 100

    progressMonitor.setMaximum(endIndex-1);

    double[][] dataArray = new double[dataCount][2];

    try 
    {
      BufferedReader in = new BufferedReader(
          new FileReader(new File(filename)));
      for(int i=0; i < startIndex; i++)
      {
        String firstLine = in.readLine(); // reading stuff away
        progressMonitor.setProgress(i);
        progressMonitor.setNote("Index: " + i + "/" + endIndex);
      }

      progressMonitor.setNote("Loading data");

      String lineString = null;

      for(int i=0;i<dataCount;i++)
      {
        lineString = in.readLine();
        StringTokenizer st = new StringTokenizer(lineString," ");
        for(int j=0;j<COLUMNS;j++)
        {
          double value = Double.parseDouble(st.nextToken().trim());
          dataArray[i][j] = value;
        }
        progressMonitor.setNote("Index: " + (startIndex + i) + "/" + endIndex);
        progressMonitor.setProgress(startIndex + i);
      }

      progressMonitor.close();
    }
    catch(FileNotFoundException fnfe)
    {
      // TODO
      fnfe.printStackTrace();
    }
    catch(IOException ie)
    {
      // TODO
      ie.printStackTrace();
    }

    dp = getNewWindow("First Return Map");

    switch(use)
    {
      case USE_POINTS:
        for(int i=1; i < dataCount && doAnalysis; i++)
        {
          dp.drawPoint(dataArray[i-1][xIndex], dataArray[i][yIndex]);
        }
        break;
      case USE_LINES:
        for(int i=2; i < dataCount && doAnalysis; i++)
        {
          dp.drawLine(
              dataArray[i-2][xIndex], dataArray[i-1][yIndex],
              dataArray[i-1][xIndex], dataArray[i][yIndex]);
        }

    }

    StringTokenizer st = new StringTokenizer(xLabel,"_");
    String xLabel1 = st.nextToken();
    String xLabel2 = st.nextToken();
    st = new StringTokenizer(yLabel,"_");
    String yLabel1 = st.nextToken();
    String yLabel2 = st.nextToken();

    dp.setYLabel(yLabel1,yLabel2,"t+1");
    dp.setXLabel(xLabel1,xLabel2,"t");


  }

  // **************************************************************************
  // end of must-have-functions
  // start with you own functions here
  // **************************************************************************
}
