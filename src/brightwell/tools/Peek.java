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

import brightwell.analyser.DataStorage;
import brightwell.analyser.Tool;
import brightwell.gui.drawingplane.DrawingPlane;
import brightwell.gui.Error;

import cholsey.Neuron;
import cholsey.Synapse;
import cholsey.SynapseMode;
import cholsey.SynapseList;


import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.Random;

import org.apache.log4j.Logger;

import util.misc.IseeLogger;


public class Peek extends Tool
{ 
  private static Logger       log                  = IseeLogger.getLogger(Peek.class);

  private Random random = new Random();

  private final static int    MODE_PEEK            = 0;
  private final static int    MODE_RAMP            = 1;
  private final static int    MODE_NOISE           = 2;

  private final static int    SWITCH_LENGTH        = 20;

  private final static String MODE_STRING          = "mode";
  private final static String PEEK_MIN_STRING      = "peek min";
  private final static String PEEK_MAX_STRING      = "peek max";
  private final static String PRE_STEPS_STRING     = "pre steps";
  private final static String PEEK_STEPS_STRING    = "peek steps";
  private final static String POST_STEPS_STRING    = "post steps";
  private final static String PLOT_START_STRING    = "plot start";
  private final static String PEEKED_NEURON_STRING = "neuron";
  private final static String TO_FILE              = "to file";
  private final static String FILE_NAME            = "file name";

  private int                 neuronIndex          = -1;                               // the index of the neuron, which will get the peek
  private double              peekMin              = -1;
  private double              peekMax              = -1;
  private int                 preSteps             = -1;
  private int                 peekSteps            = -1;
  private int                 postSteps            = -1;
  private int                 plotStart            = -1;
  private int                 mode                 = -1;
  private boolean             toFile               = false;
  private boolean             swichOrder           = false;
  private String              peekedNeuron         = null;
  private String              filename             = null;
  private File                file                 = null;
  private PrintWriter         out                  = null;

  private Vector              drawingPlanes        = null;
  private Vector              dpTransmitter        = null;
  private Vector              dpReceptor           = null;
  private Vector              dpSynapseStrength    = null;

  public boolean needsNet()
  {
    return true;
  }

  public String getToolName()
  {
    return "Peek";
  }

  public String getToolDescription()
  {
    return "Visualisation of Net reaction to peek input";
  }

  public void init()
  {
    setToolPriority(-9);
    String[] comboBoxEntries = 
    { 
      "peek",
      "ramp",
      "noise"
    };

    addComboBox(MODE_STRING,comboBoxEntries,0);
    addDouble(PEEK_MIN_STRING,-Double.MAX_VALUE,Double.MAX_VALUE,-1);
    addDouble(PEEK_MAX_STRING,-Double.MAX_VALUE,Double.MAX_VALUE,1);
    addInteger(PRE_STEPS_STRING,0,Integer.MAX_VALUE,100);
    addInteger(PEEK_STEPS_STRING,0,Integer.MAX_VALUE,200);
    addInteger(POST_STEPS_STRING,0,Integer.MAX_VALUE,100);
    addInteger(PLOT_START_STRING,0,Integer.MAX_VALUE,0);
    addString(PEEKED_NEURON_STRING,"");
    addString(FILE_NAME,"");
    addCheckBox(TO_FILE,false);
  }

  public void doAnalysis()
  {
    mode         = getComboBoxIndex(MODE_STRING);
    peekMin      = getDouble(PEEK_MIN_STRING);
    peekMax      = getDouble(PEEK_MAX_STRING);
    preSteps     = getInteger(PRE_STEPS_STRING);
    peekSteps    = getInteger(PEEK_STEPS_STRING);
    postSteps    = getInteger(POST_STEPS_STRING);
    plotStart    = getInteger(PLOT_START_STRING);
    peekedNeuron = getString(PEEKED_NEURON_STRING);
    filename     = getString(FILE_NAME);
    toFile       = getCheckBox(TO_FILE);

    int width = dataStorage.getWindowSize()[0];
    int height = dataStorage.getWindowSize()[1];
    int row = 0;

    boolean invalidPeekString = 
      (peekedNeuron == null) ||
      (peekedNeuron.trim().length() == 0) ||
      (peekedNeuron.trim().length() > 2);
    if(invalidPeekString)
    {
      Error.enterPeekNeuron(parent);
      return;
    }

    if(getNeuronIndex(peekedNeuron) == false)
    {
      Error.enterPeekNeuron(parent);
      return;
    }

    drawingPlanes     = new Vector();
    dpTransmitter     = new Vector();
    dpReceptor        = new Vector();
    dpSynapseStrength = new Vector();

    if(toFile == true)
    {
      try
      {
        file = new File(filename);
        out = new PrintWriter(
            new OutputStreamWriter(
              new FileOutputStream(file)));
        out.print("### ");
        for(int i=0; i < net.getInputNeurons().size(); i++)
        {
          out.print(" InputNeuron_" + i + "_Aktivitaet ");
        }
        for(int i=0; i < net.getOutputNeurons().size(); i++)
        {
          out.print(" OutputNeuron_" + i + "_Aktivitaet ");
        }
        for(int i=0; i < net.getHiddenNeurons().size(); i++)
        {
          out.print(" HiddenNeuron_" + i + "_Aktivitaet ");
        }
        for(int i=0; i < net.getInputNeurons().size(); i++)
        {
          out.print(" InputNeuron_" + i + "_Output ");
        }
        for(int i=0; i < net.getOutputNeurons().size(); i++)
        {
          out.print(" OutputNeuron_" + i + "_Output ");
        }
        for(int i=0; i < net.getHiddenNeurons().size(); i++)
        {
          out.print(" HiddenNeuron_" + i + "_Output ");
        }
        for(int i=0; i < net.neurons().size(); i++)
        {
          SynapseList sl = net.neurons().neuron(i).synapses();
          if(sl != null)
          {
            for(sl.start(); sl.hasMore(); sl.next())
            {
              out.print("Synapse from " + sl.synapse().getSource().id() + " -> " +
                  sl.synapse().getDestination().id() + " = " +
                  sl.synapse().strength());
            }
          }
        }
        out.println("");
      }
      catch(IOException ioe)
      {
        ioe.printStackTrace();
      }
    }


    if(net.getSynapseMode() == SynapseMode.DYNAMIC)
    {
      for(net.neurons().start(); net.neurons().hasMore(); net.neurons().next())
      {
        Neuron localNeuron = net.neurons().neuron();
        if(localNeuron.synapses() != null)
        {
          for(localNeuron.synapses().start();
              localNeuron.synapses().hasMore();
              localNeuron.synapses().next())
          {
            Synapse localSynapse = localNeuron.synapses().synapse();

            DrawingPlane dpLocal = getNewWindow(
                "Synapse " + localSynapse.getSource().id() + 
                " -> " + localSynapse.getDestination().id(),
                plotStart, (double)(preSteps + peekSteps + postSteps),
                dataStorage.getSynapseRange()[0], 
                dataStorage.getSynapseRange()[1]);

            dpLocal.setLocation(
                20 + net.neurons().indexOf(localNeuron) * (width + 10)
                + localNeuron.synapses().indexOf(localSynapse) * 20, 
                20 + localNeuron.synapses().indexOf(localSynapse) * 20 );
            dpLocal.drawXLine(0,Color.blue);
            dpSynapseStrength.add(dpLocal);
          }
        }
      }


      row++;

      for(net.neurons().start(); net.neurons().hasMore(); net.neurons().next())
      {
        Neuron n = net.neurons().neuron();
        int neuronIndex = net.neurons().indexOf(n);
        DrawingPlane dp = getNewWindow("Receptor " + (neuronIndex + 1),
            plotStart, (double)(preSteps + peekSteps + postSteps),
            dataStorage.getReceptorRange()[0], 
            dataStorage.getReceptorRange()[1]);

        dp.setVisible(true);
        dpReceptor.add(dp);
        dp.setLocation(neuronIndex*(width+5),row*(height+5));
        dp.drawXLine(0,Color.blue);
      }

      row++;
      for(net.neurons().start(); net.neurons().hasMore(); net.neurons().next())
      {
        Neuron n = net.neurons().neuron();
        int neuronIndex = net.neurons().indexOf(n);
        DrawingPlane dp = getNewWindow("Transmitter " + (neuronIndex + 1),
            plotStart, (double)(preSteps + peekSteps + postSteps),
            dataStorage.getTransmitterRange()[0], 
            dataStorage.getTransmitterRange()[1]);

        dp.setVisible(true);
        dpTransmitter.add(dp);
        dp.setLocation(neuronIndex*(width+5),row*(height+5));
        dp.drawXLine(0,Color.blue);
      }
    }


    for(net.neurons().start(); net.neurons().hasMore(); net.neurons().next())
    {
      Neuron n = net.neurons().neuron();
      int neuronIndex = net.neurons().indexOf(n);
      DrawingPlane dp = getNewWindow("Neuron " + (neuronIndex + 1) 
          + " (" 
          + n.getNeuronType().toString() 
          + ")", plotStart, (double)(preSteps + peekSteps + postSteps),
          -1.1, 1.1);
      dp.setVisible(true);
      drawingPlanes.add(dp);
      dp.setLocation(neuronIndex*(width+5),0);
      dp.drawXLine(0,Color.blue);
    }


    doThePeek();

    if(toFile == true)
    {
      out.flush();
      out.close();
    }


  }

  // **************************************************************************
  // private functions
  // **************************************************************************

  private void doThePeek()
  {

    switch(dataStorage.getInialActivityMode())
    {
      case DataStorage.INITIAL_ACTIVITY_USER_DEFINED:
        log.debug("INITIAL_ACTIVITY_USER_DEFINED");
        net = dataStorage.getNet().copy(); // the origianl still has the initial
        break;
      case DataStorage.INITIAL_ACTIVITY_RANDOM:
        log.debug("INITIAL_ACTIVITY_RANDOM");
        net.randomInitActivity();
        break;
    }


    Neuron peekedNeuron = net.neurons().neuron(neuronIndex);
    int rampSize = (int)((double)peekSteps / 3.0 + 0.5);
    double x = 0;
    xStart   = 0;
    xEnd     = (double)(preSteps + peekSteps + postSteps);
    dx       =  stepsX / xEnd;

    for(int i=0; i < preSteps; i++)
    {
      x = i;
      peekedNeuron.setActivation(peekMin);
      net.process();
      if(x > plotStart)
      {
        drawAllNeurons(x, peekMin);
      }
      if(toFile)
      {
        writeAllTheStuff(out);
      }

    }

    for(int i=0; i < peekSteps; i++)
    {
      x = (i + preSteps);
      switch(mode)
      {
        case MODE_NOISE:
          peekedNeuron.setActivation(noiseInput());
          net.process();
          if(x > plotStart)
          {
            drawAllNeurons(x, peekMax);
          }
          break;
        case MODE_PEEK:
          peekedNeuron.setActivation(peekMax);
          net.process();
          if(x > plotStart)
          {
            drawAllNeurons(x, peekMax);
          }
          break;
        case MODE_RAMP:
          double activation = 0;
          if( i < rampSize) 
          {
            double factor = (double)( i ) /
              (double)rampSize;
            activation = peekMin + factor * (peekMax - peekMin);
            peekedNeuron.setActivation(activation);
          }
          else if( i < 2 * rampSize)
          {
            peekedNeuron.setActivation(peekMax);
            activation = peekMax;
          }
          else
          {
            double factor = (double)((3 * rampSize - ( i )) /
              (double)rampSize);
            activation = peekMin + factor * (peekMax - peekMin);
            peekedNeuron.setActivation(activation);
          }
          net.process();
          if(x > plotStart)
          {
            drawAllNeurons(x, activation);
          }
          break;
      }
      if(x > plotStart)
      {
        if(toFile)
        {
          writeAllTheStuff(out);
        }
      }
    }

    for(int i = 0; i < postSteps; i++)
    {
      x = (i + preSteps + peekSteps);
      peekedNeuron.setActivation(peekMin);
      net.process();
      if(x > plotStart)
      {
        drawAllNeurons(x, peekMin);
        if(toFile)
        {
          writeAllTheStuff(out);
        }
      }
    }

  }

  private void drawAllNeurons(double x, double peek)
  {
    if( x % SWITCH_LENGTH == 0)
    {
      swichOrder = !swichOrder;
    }

    drawAllOutputs(x, peek);
    if(net.getSynapseMode() == SynapseMode.DYNAMIC)
    {
      drawAllTransmitter(x, peek);
      drawAllReceptor(x, peek);
      drawAllSypanses(x,peek);
    }

  }

  private void drawAllSypanses(double x, double peek)
  {
    int panelIndex = 0;
    for(net.neurons().start(); net.neurons().hasMore(); net.neurons().next())
    {
      Neuron localNeuron = net.neurons().neuron();
      if(localNeuron.synapses() != null)
      {
        for(localNeuron.synapses().start();
            localNeuron.synapses().hasMore();
            localNeuron.synapses().next())
        {
          Synapse localSynapse = localNeuron.synapses().synapse();
          DrawingPlane dpLocal = 
            (DrawingPlane)dpSynapseStrength.elementAt(panelIndex);

          dpLocal.drawPoint(x, peek, Color.blue);

          dpLocal.drawPoint(x,localSynapse.strength());
          panelIndex++;

        }
      }
    }
  }

  private void drawAllOutputs(double x, double peek)
  {
    int index = 0;
    for(net.neurons().start(); net.neurons().hasMore(); net.neurons().next())
    {
      index = net.neurons().indexOf(net.neurons().neuron());

      if(swichOrder)
      {
        ((DrawingPlane)drawingPlanes.elementAt(index)).drawPoint(x,
                                   net.neurons().neuron().getOutput(), Color.black);

        if(index == neuronIndex)
        {
          ((DrawingPlane)drawingPlanes.elementAt(index)).drawPoint(x,
                                     peek, Color.red);
        }
        else
        {
          ((DrawingPlane)drawingPlanes.elementAt(index)).drawPoint(x,
                                     peek, Color.blue);
        }
      }
      else
      {

        if(index == neuronIndex)
        {
          ((DrawingPlane)drawingPlanes.elementAt(index)).drawPoint(x,
                                     peek, Color.red);
        }
        else
        {
          ((DrawingPlane)drawingPlanes.elementAt(index)).drawPoint(x,
                                     peek, Color.blue);
        }

        ((DrawingPlane)drawingPlanes.elementAt(index)).drawPoint(x,
                               net.neurons().neuron().getOutput(), Color.black);
      }
    }

  }

  private void drawAllTransmitter(double x, double peek)
  {
    int index = 0;
    for(net.neurons().start(); net.neurons().hasMore(); net.neurons().next())
    {
      index = net.neurons().indexOf(net.neurons().neuron());

      if(swichOrder)
      {
        ((DrawingPlane)dpTransmitter.elementAt(index)).drawPoint(x,
                    net.neurons().neuron().getTransmitterLevel(), Color.black);

        if(index == neuronIndex)
        {
          ((DrawingPlane)dpTransmitter.elementAt(index)).drawPoint(x,
                                               peek, Color.red);
        }
        else
        {
          ((DrawingPlane)dpTransmitter.elementAt(index)).drawPoint(x,
                                               peek, Color.blue);
        }
      }
      else
      {

        if(index == neuronIndex)
        {
          ((DrawingPlane)dpTransmitter.elementAt(index)).drawPoint(x,
                                               peek, Color.red);
        }
        else
        {
          ((DrawingPlane)dpTransmitter.elementAt(index)).drawPoint(x,
                                               peek, Color.blue);
        }
        ((DrawingPlane)dpTransmitter.elementAt(index)).drawPoint(x,
                    net.neurons().neuron().getTransmitterLevel(), Color.black);
      }
    }
  }

  private void drawAllReceptor(double x, double peek)
  {
    int index = 0;
    for(net.neurons().start(); net.neurons().hasMore(); net.neurons().next())
    {
      index = net.neurons().indexOf(net.neurons().neuron());

      if(swichOrder)
      {
        ((DrawingPlane)dpReceptor.elementAt(index)).drawPoint(x,
                    net.neurons().neuron().getReceptorLevel(), Color.black);

        if(index == neuronIndex)
        {
          ((DrawingPlane)dpReceptor.elementAt(index)).drawPoint(x,
                                               peek, Color.red);
        }
        else
        {
          ((DrawingPlane)dpReceptor.elementAt(index)).drawPoint(x,
                                               peek, Color.blue);
        }
      }
      else
      {

        if(index == neuronIndex)
        {
          ((DrawingPlane)dpReceptor.elementAt(index)).drawPoint(x,
                                               peek, Color.red);
        }
        else
        {
          ((DrawingPlane)dpReceptor.elementAt(index)).drawPoint(x,
                                               peek, Color.blue);
        }
        ((DrawingPlane)dpReceptor.elementAt(index)).drawPoint(x,
                    net.neurons().neuron().getReceptorLevel(), Color.black);
      }
    }
  }

  private boolean getNeuronIndex(String neuronString)
  {
    try
    {
      neuronIndex = Integer.parseInt(neuronString.trim());
      return true;
    }
    catch(NumberFormatException nfe)
    {
      String neuronTypeString = neuronString.trim().substring(0,1);
      String restOfTheString = neuronString.trim().substring(1,
          neuronString.length());

      try // again
      {
        neuronIndex = Integer.parseInt(restOfTheString)-1;
        if(neuronTypeString.toUpperCase().equals("I"))
        {
          neuronIndex = net.neurons().indexOf(
              net.getInputNeurons().neuron(neuronIndex));
          return true;
        }
        if(neuronTypeString.toUpperCase().equals("O"))
        {
          neuronIndex = net.neurons().indexOf(
              net.getOutputNeurons().neuron(neuronIndex));
          return true;
        }
        if(neuronTypeString.toUpperCase().equals("H"))
        {
          neuronIndex = net.neurons().indexOf(
              net.getHiddenNeurons().neuron(neuronIndex));
          return true;
        }

        neuronIndex = -1;
        return false;
      }
      catch(NumberFormatException nfe2)
      {
        return false;
      }
    }
  }

  private void writeAllTheStuff(PrintWriter out)
  {
    for(int i=0; i < net.getInputNeurons().size(); i++)
    {
      out.print(net.getInputNeurons().neuron(i).getActivation() + " ");
    }
    for(int i=0; i < net.getOutputNeurons().size(); i++)
    {
      out.print(net.getOutputNeurons().neuron(i).getActivation() + " ");
    }
    for(int i=0; i < net.getHiddenNeurons().size(); i++)
    {
      out.print(net.getHiddenNeurons().neuron(i).getActivation() + " ");
    }
    for(int i=0; i < net.getInputNeurons().size(); i++)
    {
      out.print(net.getInputNeurons().neuron(i).getOutput() + " ");
    }
    for(int i=0; i < net.getOutputNeurons().size(); i++)
    {
      out.print(net.getOutputNeurons().neuron(i).getOutput() + " ");
    }
    for(int i=0; i < net.getHiddenNeurons().size(); i++)
    {
      out.print(net.getHiddenNeurons().neuron(i).getOutput() + " ");
    }

    for(int i=0; i < net.neurons().size(); i++)
    {
      SynapseList sl = net.neurons().neuron(i).synapses();
      if(sl != null)
      {
        for(sl.start(); sl.hasMore(); sl.next())
        {
          out.print(sl.synapse().strength() + " " );
        }
      }
    }

    out.println("");

  }

  private double noiseInput()
  {
    return random.nextFloat() * (peekMax - peekMin) + peekMin;
  }
}
