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

import brightwell.analyser.Tool;
//import javax.vecmath.Vector3f;
//import javax.vecmath.Color3f;

public class IsoPlot3D extends Tool
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
//  private DrawingPlane3D dp = null;

  // entries for the comboBox
  private String[] comboBoxEntries =  
  {
    "entry 1",
    "entry 2",
    "entry 3"
  };

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
    return "IsoPlot3d";
  }

  /**
   * This function returns a description of the tool which is displayed, when
   * the mouse is placed over a tool name for some seconds.
   */
  public String getToolDescription()
  {
    return "3-diemensional Isoperiodic plot";
  }

  /**
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

    // addComboBox: name, list of entries, initially selected entry
    addComboBox("my selection",comboBoxEntries,1);

    // addDouble: name, minValue, maxValue, initial value
    addDouble("my double value",-1.2,1.5,0.5);

    // addInteger: name, minValue, maxValue, initial value
    addInteger("my integer value",-10,5,-2);

    // addString: name, initial string
    addString("my string value","some initial string");

    // addCheckBox: name, initially true/false
    addCheckBox("my check box",false);

    
    // setToolPriority defined the order of the tools in the tabbed pane. tools
    // with lower numbers will appear first.
    setToolPriority(-5);
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

    // dp must be declared before (see above), but can only be initialised in
    // this function (after "run" has been pressed)
//    dp = getNewWindow3D("My Drawing Panel");
//    dp.setUniverseMinMax(new Point3f(0f,0f,-1f), new Point3f(100f,100f,+1f));
//    for(int i=0; i < dp.getMaxNumberOfPoints(); i++)
//    {
//      for(int j=0; j<100; j++)
//      {
//        float value = (float)(Math.sin(i) * Math.cos(j));
//        float x = (float)((float)i / dp.UNIVERSE_SIZE);
//        float y = (float)((float)j / dp.UNIVERSE_SIZE);
//        dp.drawVoxel(Color.blue, new Vector3f(x,y,value));
//      }
//      dp.setPoint( 0,i,i, new Color3f(1.0f, 0.0f, 0.0f));
//    }
//      dp.setPoint(0,0,0, new Color3f(0.0f, 0.0f, 0.0f));

  }

  // **************************************************************************
  // end of must-have-functions
  // start with you own functions here
  // **************************************************************************
}
