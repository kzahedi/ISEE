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
import brightwell.gui.Error;

import cholsey.Neuron;

import cholsey.Synapse;


import java.awt.Color;
import java.awt.Component;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;

import util.misc.IseeLogger;

public class SingleNeuronAttractor extends Tool
{ 
  private static Logger log = IseeLogger.getLogger(SingleNeuronAttractor.class);
 
  private final static String MAX_DIFF_STRING = "max. diff";
  private final static String NEURON_2_PLOT_STRING = "plot neuron";
  private final static int SYNAPSE_0 = 0;
  private final static int SYNAPSE_1 = 1;

  private final static int NEURON_0 = 2;
  private final static int NEURON_1 = 3;

  private int parameterX = 0;
  private int parameterY = 0;

  private Synapse synapse0 = null;
  private Synapse synapse1 = null;
  private Neuron neuron0 = null; 
  private Neuron neuron1 = null; 
  
  private Vector periodVector = null;

  private DrawingPlane dp = null;

  private Color[] colors = null;
  
  private double maxActivationDiff = 0;

  private int neuron2PlotIndex = 1;

  private Color[] yColor = 
  {
    Color.black,
    Color.white
  };

  private String[] comboBoxEntries = 
  { 
    "colour",
    "grayscale"
  };

  private final static Color[] COLOR_ARRAY = 
  {
    Color.white,
    Color.blue,
    Color.cyan,
    Color.green,
    Color.magenta,
    Color.orange,
    Color.pink,
    Color.red,
    Color.yellow,
    Color.black // chaos
  };

  private final static Color[] GRAYSCALE_ARRAY =
  {
    Color.white,
    new Color(200,200,200),
    new Color(160,160,160),
    new Color(140,140,140),
    new Color(110,110,110),
    new Color(90,90,90),
    new Color(70,70,70),
    new Color(55,55,55),
    new Color(40,40,40),
    Color.black // chaos
  };

  public boolean needsNet()
  {
    return true;
  }

  public void doAnalysis()
  {
    neuron2PlotIndex = getInteger(NEURON_2_PLOT_STRING)-1;
    if(neuron2PlotIndex >= net.neurons().size())

    {
      Error.parseErrorMisc(parent, "Neuron to plot doesn't exist!");
      return;
    }
    
    int colorMode = getComboBoxIndex("color mode");
    String biasString    = getString("bias");
    String weightString    = getString("weight");
    maxActivationDiff = getDouble(MAX_DIFF_STRING);

    log.debug("selectedColor = " + comboBoxEntries[colorMode]);
    log.debug("biasString = " + biasString);
    log.debug("weightString = " + weightString);
    log.debug("maxActivationDiff = " + maxActivationDiff);

    if(biasString.equals("") && weightString.equals(""))
    {
      Error.attractorParseError(parent);
      return;
    }

    switch(colorMode)
    {
      case 0:
        colors = COLOR_ARRAY;
        break;
      case 1:
        colors = GRAYSCALE_ARRAY;
        break;
    }

    String title = extractIndexInformation(biasString,weightString);

    if(title == null)
    {
      return;
    }


    if (synapse0 == null &&
        synapse1 == null &&
        neuron0 == null &&
        neuron1 == null)
    {
      Error.attractorParseError((Component)parent);
      return;
    }


    dp = getNewWindow(title);
    setLabels();
    doAnalysis = true;
    calculateAttractor();
    dp.drawLegend();
    dp.drawPeriodLegend(periodVector,colors);

  }

  public void init()
  {
    setToolPriority(10);

    addComboBox("color mode",comboBoxEntries,0);
    addString("bias","");
    addString("weight","");
    addDouble(MAX_DIFF_STRING,0,Double.MAX_VALUE,0.0001);

    addInteger(NEURON_2_PLOT_STRING,1,32000,1);
    //addCheckBox("liapunov",false);

  }

  public String getToolName()
  {
    return "Single Neuron Attractor";
  }

  public String getToolDescription()
  {
    return "Basins Visualisation";
  }

  private void setLabels()
  {
    switch(parameterX)
    {
      case NEURON_0:
        dp.setXLabel("\u0398", "" + (neuron0.id()+1));
        break;
      case NEURON_1:
        dp.setXLabel("\u0398", "" + (neuron1.id()+1));
        break;
      case SYNAPSE_0:
        dp.setXLabel("w",""
            + (synapse0.getDestination().id()+1)
            + ","
            + (synapse0.getSource().id()+1));
        break;
      case SYNAPSE_1:
        dp.setXLabel("w",""
            + (synapse1.getDestination().id()+1)
            + ","
            + (synapse1.getSource().id()+1));
        break;
    }

    switch(parameterY)
    {
      case NEURON_0:
        dp.setYLabel("\u0398", "" + (neuron0.id()+1));
        break;
      case NEURON_1:
        dp.setYLabel("\u0398", "" + (neuron1.id()+1));
        break;
      case SYNAPSE_0:
        dp.setYLabel("w",""
            + (synapse0.getDestination().id()+1)
            + ","
            + (synapse0.getSource().id()+1));
        break;
      case SYNAPSE_1:
        dp.setYLabel("w",""
            + (synapse1.getDestination().id()+1)
            + ","
            + (synapse1.getSource().id()+1));
        break;
    }
  }
  
  // biasString = bias-string
  // weightString = weight-string
  // fills the synapse0/1 and/or neurons0/1
  // fills paramter0/1 with SYNAPSE0/1 or NEURON0/1
  private String extractIndexInformation(String biasString, String weightString)
  {
    String title = new String("Attractor Map. ");
    if(biasString.equals("") && weightString.equals(""))
    {
      synapse0 = null;
      synapse1 = null;
      neuron0  = null;
      neuron1  = null;
      return null;
    };

    if(biasString.equals("")) // --> only weights to do
    {
      StringTokenizer st0 = new StringTokenizer(weightString,",");
      String first = st0.nextToken().trim();
      String second = st0.nextToken().trim();
      title = title.concat("Weight " + first + ", Weight " + second + 
           " varied");
      char axisFirst = first.charAt(first.length()-1);
      char axisSecond = second.charAt(second.length()-1);
      first = first.substring(0,first.length()-1);
      second = second.substring(0,second.length()-1);
      /*
      System.out.println("Axis 1: " + axisFirst + " :: " + first);
      System.out.println("Axis 1: " + axisSecond + " :: " + second);
      */
      synapse0 = extractSynapse(first);
      synapse1 = extractSynapse(second);
      switch (axisFirst)
      {
        case 'x':
          parameterX = SYNAPSE_0;
          parameterY = SYNAPSE_1;
          break;
        case 'y':
          parameterX = SYNAPSE_1;
          parameterY = SYNAPSE_0;
          break;
      }
      return title; // done
    }

    if(weightString.equals("")) // --> only biases to do
    {
      StringTokenizer st0 = new StringTokenizer(biasString,",");
      String first = null;
      String second = null;
      try 
      {
        first = st0.nextToken().trim();
        second = st0.nextToken().trim();
      }
      catch(NoSuchElementException nse)
      {
        Error.attractorParseError(parent);
        return null;
      }
      catch(NumberFormatException nfe)
      {
        Error.attractorParseError(parent);
        return null;
      }
      title = title.concat("Bias " + first + ", Bias " + second + 
           " varied");
      char axisFirst = first.charAt(first.length()-1);
      char axisSecond = second.charAt(second.length()-1);
      first = first.substring(0,first.length()-1);
      second = second.substring(0,second.length()-1);
      /*
      System.out.println("Axis 1: " + axisFirst + " :: " + first);
      System.out.println("Axis 1: " + axisSecond + " :: " + second);
      */
      // TODO catch if index is out of bounce
      neuron0 = net.getNeuron(Integer.parseInt(first)-1);
      neuron1 = net.getNeuron(Integer.parseInt(second)-1);
      switch(axisFirst)
      {
        case 'x' :
          parameterX = NEURON_0;
          parameterY = NEURON_1;
          break;
        case 'y' :
          parameterX = NEURON_1;
          parameterY = NEURON_0;
          break;
      }
      return title; // done
    }

    // else case ... one weight and one bias
    title = title.concat("Bias " + biasString + ", Weight " + weightString + 
        " varied");
    char neuronAxis = biasString.charAt(biasString.length()-1);
    neuron0 = net.getNeuron(
        Integer.parseInt(
          biasString.substring(0,biasString.length()-1))-1);

    char synapseAxis = weightString.charAt(weightString.length()-1);
    synapse0 = extractSynapse(weightString.substring(0,weightString.length()-1));
    
    switch(neuronAxis)
    {
      case 'x':
        parameterX = NEURON_0;
        parameterY = SYNAPSE_0;
        break;
      case 'y':
        parameterX = SYNAPSE_0;
        parameterY = NEURON_0;
        break;
    }
    return title;
  }

  private Synapse extractSynapse(String index)
  {
    StringTokenizer st = new StringTokenizer(index,"-");
    int destination = -1;
    int source      = -1;
    try
    {
      destination = Integer.parseInt(st.nextToken().trim());
      source      = Integer.parseInt(st.nextToken().trim());
    }
    catch(NoSuchElementException nse)
    {
      Error.attractorParseError(parent);
      return null;
    }
    //System.out.println(source + " -> " + destination);
    Neuron destNeuron = net.getNeuron(destination-1);
    Neuron sourceNeuron = net.getNeuron(source-1);
    Synapse synapse = destNeuron.getSynapse(sourceNeuron);
    //System.out.println("extractSynapse: \n" + synapse.toString());
    return synapse;
  }

  private void calculateAttractor()
  {
    double xi = 0;
    double yi = 0;
    periodVector = new Vector();
    Integer period = new Integer(0);

    if(dataStorage.getInialActivityMode() == 1) // user defined
    {
      net = dataStorage.getNet().copy(); // the origianl still has the initial
                                         // activities
    }
    else // random
    {
      net.randomInitActivity();
    }


 
    for(int x=0;x < stepsX && doAnalysis; x++)
    {
      xi = xStart + x * dx;
      setParameter(parameterX,xi);
      for(int y=0;y < stepsY && doAnalysis; y++)
      {
        yi = yStart + y * dy;
        setParameter(parameterY,yi);

        dp.drawProgressPointY(yi, yColor[ (x+1) % 2 ]);

        for(int i=0;i<convergenceIterations;i++)
        {
          net.process();
        }

        period = new Integer(getPeriod());

        dp.drawPoint(xi,yi,colors[Math.max(0,period.intValue()-1)]);

        if(periodVector.indexOf(period) == -1)
        {
          periodVector.add(period);
        }


      }

      dp.drawProgressPointX(xi, Color.blue); // process bar
    }
  }

  private int getPeriod()
  {
    Vector activities = new Vector();
    for(net.neurons().start();net.neurons().hasMore();net.neurons().next())
    {
      activities.add(new Double(net.neurons().neuron().getActivation()));
    }

    for(int i=1;i<colors.length;i++) // colors.length-1 is defined as chaos
    {
      net.process();
      if(periodReached(activities))
      {
        return i;
      }
    }
    return colors.length;
  }

  private boolean periodReached(Vector activities)
  {
    int index = 0;
    for(net.neurons().start();net.neurons().hasMore();net.neurons().next())
    {
      //check only the user defined neuron
      if(index == neuron2PlotIndex)
      {
        double activity = ((Double)activities.elementAt(index)).doubleValue();
      // return false if one activity is not equal the origianl
        if(Math.abs(activity - net.neurons().neuron().getActivation()) >
            maxActivationDiff)
        {
          return false;
        }
      }
      index++;
    }

    return true;
  }

  private void setParameter(int parameterIndex, double value)
  {
    switch(parameterIndex)
    {
      case NEURON_0:
        //System.out.println("Setting " + neuron0.toString() + " to " + value);
        neuron0.setBias(value);
        break;
      case NEURON_1:
        //System.out.println("Setting " + neuron1.toString() + " to " + value);
        neuron1.setBias(value);
        break;
      case SYNAPSE_0:
        //System.out.println("Setting " + synapse0.toString() + " to " + value);
        synapse0.setStrength(value);
        break;
      case SYNAPSE_1:
        //System.out.println("Setting " + synapse1.toString() + " to " + value);
        synapse1.setStrength(value);
        break;
    }
  }


}
