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

import brightwell.gui.drawingplane.DrawingPlane;


import java.awt.Color;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;

import util.misc.IseeLogger;

public class PoincareMap extends Tool
{ 
  private static Logger log = IseeLogger.getLogger(PoincareMap.class);
  
  private final static String PROJECTION_STRING = "projection";
  private final static String SELECTION_STRING  = "selection";

  private DrawingPlane dp = null;
  private Vector dpSelection = null;

  private int colorIndex = 0; // because it 
  private Vector selectionIndexVector = new Vector(0); // Vector of Vector(2) = 
                                                  // first is index of o_i(t),
                                                  // and the second is the index
                                                  // of o_j(t)
  private Vector selectionValuesVector = new Vector(0);

  private final static Color[] colorArray = 
  {
    Color.blue,
    Color.red,
    Color.green,
    Color.cyan,
    Color.magenta,
    Color.orange,
    Color.pink,
    Color.yellow,
    Color.black 
  };




  public boolean needsNet()
  {
    return true;
  }

  public String getToolName()
  {
    return "Poincare Map";
  }

  public String getToolDescription()
  {
    return "Poincare Map Visualisation";
  }

  public void init()
  {
    setToolPriority(-7);
    String[] comboBoxEntries = 
    { 
      "average",
      "selection"
    };

    addComboBox(PROJECTION_STRING,comboBoxEntries,0);
    addString(SELECTION_STRING,"");

  }

  public void doAnalysis()
  {
    int mode = getComboBoxIndex(PROJECTION_STRING);
    String selectionString = getString(SELECTION_STRING);

    if(mode == 0)
    {
      // start bifurcation diagram window ...
      dp = getNewWindow("Pseudo PoincareMap");
      // and make it visible
      dp.setVisible(true);
      calculatePoincareMap();
      dp.drawLegend();
    }
    else
    {
      getSelection(selectionString);
      calculatePoincareMapSelection();
      for(int i=0;i<dpSelection.size();i++)
      {
        DrawingPlane dpS = (DrawingPlane)dpSelection.elementAt(i);
        dpS.drawLegend();
      }
    }

  }

  private void getSelection(String selectionString)
  {
    StringTokenizer st = new StringTokenizer(selectionString,",");
    String token = null;
    selectionIndexVector  = new Vector(0);
    selectionValuesVector = new Vector(0);
    dpSelection = new Vector(0);
    try
    {
      while(!(token = st.nextToken()).equals(""))
      {
        StringTokenizer st1 = new StringTokenizer(token,"-");
        Vector selectionIndex  = new Vector(2);
        Vector selectionValues = new Vector(2);
        int first = Integer.parseInt(st1.nextToken().trim())-1;
        int second = Integer.parseInt(st1.nextToken().trim())-1;
        selectionIndex.add(new Integer(first));
        selectionIndex.add(new Integer(second));
        selectionValues.add(new Double(0));
        selectionValues.add(new Double(0));
        // storage
        selectionIndexVector.add(selectionIndex);
        selectionValuesVector.add(selectionValues);
        DrawingPlane dpSel = getNewWindow("O" + (first+1) + " - O" + (second+1));
        dpSel.setVisible(true);
        dpSelection.add(dpSel);
      }
    }
    catch(NoSuchElementException e)
    {
      // done
    }
  }
  
  private void calculatePoincareMap()
  {
    double outputValuesT = 0;  // output average at time t
    double outputValuesTPO = 0; // output average at time t+1
    doAnalysis = true;
    while(doAnalysis) // esc
    {
      if(dataStorage.getInialActivityMode() == 1) // user defined
      {
        net = dataStorage.getNet().copy(); // the origianl still has the initial
        // activities
      }
      else // random
      {
        net.randomInitActivity();
      }

      for(int i=0;i<convergenceIterations;i++)
      {
        net.process();
      }


      Color color = colorArray[colorIndex];
      
      outputValuesT = 0;

      for(net.neurons().start();net.neurons().hasMore();net.neurons().next())
      {
        outputValuesT += net.neurons().neuron().getOutput();
      }
      outputValuesT /= (double)net.size(); 

      while((!spacePressed()) && doAnalysis) // space
      {
        //System.out.println("doing color " + color.toString());
        net.process();
        outputValuesTPO = 0;
        for(net.neurons().start();net.neurons().hasMore();net.neurons().next())
        {
          outputValuesTPO += net.neurons().neuron().getOutput();
        }
        outputValuesTPO /= (double)net.size(); 

        //System.out.println("X : " + outputValuesT + " Y: " + outputValuesTPO);
        dp.drawPoint(outputValuesT,outputValuesTPO,color);
        outputValuesT = outputValuesTPO;
      }
      colorIndex = (colorIndex + 1) % colorArray.length;
      log.debug("colorIndex = " + colorIndex);
    } // while doAnalysis
  }

  private void calculatePoincareMapSelection()
  {
    DrawingPlane dpS = null;
    // reset the stuff
    for(int i=0;i<selectionValuesVector.size();i++)
    {
      Vector selectionValues = (Vector)selectionValuesVector.elementAt(i);
      selectionValues.setElementAt(new Double(0),0);
      selectionValues.setElementAt(new Double(0),1);
    }

    doAnalysis = true;
    while(doAnalysis) // esc
    {
      if(dataStorage.getInialActivityMode() == 1) // user defined
      {
        net = dataStorage.getNet().copy(); // the origianl still has the initial
        // activities
      }
      else // random
      {
        net.randomInitActivity();
      }

      for(int i=0;i<convergenceIterations;i++)
      {
        net.process();
      }


      Color color = colorArray[colorIndex];
      
      for(int i=0;i<selectionValuesVector.size();i++)
      {
        Vector indices = (Vector)selectionIndexVector.elementAt(i);
        Vector values  = (Vector)selectionValuesVector.elementAt(i);
        int indexOI = ((Integer)indices.elementAt(0)).intValue();
        //index 0 = oi, index 1 = oj
        values.setElementAt(
            new Double(net.neurons().neuron(indexOI).getOutput()), 0); 
      }

      while((!spacePressed()) && doAnalysis) // space
      {
        //System.out.println("doing color " + color.toString());
        net.process();
        for(int i=0;i<selectionValuesVector.size();i++)
        {
          Vector indices = (Vector)selectionIndexVector.elementAt(i);
          Vector values  = (Vector)selectionValuesVector.elementAt(i);
          int indexOI = ((Integer)indices.elementAt(0)).intValue(); // OJ
          int indexOJ = ((Integer)indices.elementAt(1)).intValue();
          values.setElementAt(
              new Double(net.neurons().neuron(indexOI).getOutput())
              , 0);
          values.setElementAt(
              new Double(net.neurons().neuron(indexOJ).getOutput())
              , 1);
          dpS = (DrawingPlane)dpSelection.elementAt(i);
          double o_i = ((Double)values.elementAt(0)).doubleValue();
          double o_j = ((Double)values.elementAt(1)).doubleValue();
          dpS.drawPoint(o_i,o_j,color);
          values.setElementAt(new
              Double(net.neurons().neuron(indexOI).getOutput()),0); // o_i 
          values.setElementAt(new
              Double(net.neurons().neuron(indexOJ).getOutput()),1); // o_j 
        }
      }
      colorIndex = (colorIndex + 1) % colorArray.length;
      log.debug("colorIndex = " + colorIndex);
    } // while doAnalysis

  }
}
