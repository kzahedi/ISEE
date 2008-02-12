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
import brightwell.util.Liapunov;

import cholsey.Neuron;
import cholsey.Synapse;
import cholsey.SynapseList;
import cholsey.SynapseMode;


import java.awt.Color;
import java.awt.Component;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;

import util.misc.IseeLogger;

public class Bifurcation extends Tool
{ 

  /** weight will be shifted for bifurcation analysis */
  private final static int        ITERATE_WEIGHT     = 0;
  /** bias will be shifted for bifurcation analysis */
  private final static int        ITERATE_BIAS       = 1;

  // direction upwards -> increasing x-values, downwards, decreasing x-values
  private final static int        UPWARDS            = 0;
  private final static int        DOWNWARDS          = 1;

  private final static String     INIT_ACTIVITIY     = "init act.";
  private final static String     BIAS               = "bias";
  private final static String     WEIGHT             = "weight";
  private final static String     PLOT_NEURONS       = "plot neurons";
  private final static String     CHAOS_INPUT        = "chaos input";
  private final static String     TO_FILE            = "to file";
  private final static String     FILE_NAME          = "file name";
  private final static String     USE_LIAPUNOV       = "liapunov";

  private PrintWriter             out                = null;

  private Vector                  dpActivation       = null;
  private Vector                  dpReceptor         = null;
  private Vector                  dpTransmitter      = null;
  private Vector                  dpSynapseStrength  = null;
  private Vector                  singleNeuronVector = new Vector();

  private DrawingPlane            dpLiapunov         = null;

  private String                  biasString         = null;
  private String                  weightString       = null;
  private String                  fileName           = null;
  private int                     initActivityIndex  = 0;
  private boolean                 toFile             = false;
  private boolean                 useLiapunov        = false;
  private String                  neurons            = null;

  private Liapunov                liapunov           = null;
  private double[]                oldExponents       = null;
  private double[]                exponents          = null;

  private static Logger           log                = IseeLogger.getLogger(Bifurcation.class);

  private DrawingPlane            dp                 = null;
  private Vector                  dpNeurons          = new Vector(0);

  private int                     mode               = -1;
  private Neuron                  neuron             = null;
  private SynapseList             synapses           = null;

  private String[]                comboBoxEntries    = 
    { 
      "continous",
      "random",
      "reset"
    };

  private final static Color[] LIAPUNOV_COLOR_ARRAY = 
  {
    Color.blue,
    Color.black,
    Color.pink,
    Color.green,
    Color.magenta,
    Color.orange,
    Color.yellow
  };



  public boolean needsNet()
  {
    return true;
  }

  public void init()
  {
    setToolPriority(-10);
    addComboBox(INIT_ACTIVITIY,comboBoxEntries,0);
    addString(BIAS,"");
    addString(WEIGHT,"");
    addString(PLOT_NEURONS,"");
    addString(FILE_NAME,"");
    addCheckBox(TO_FILE,false);
    addCheckBox(CHAOS_INPUT,false);
    addCheckBox(USE_LIAPUNOV,false);
  }


  public void doAnalysis()
  {
    log.debug("doAnalysisning feigenbaum");

    biasString        = getString(BIAS);
    weightString      = getString(WEIGHT);
    initActivityIndex = getComboBoxIndex(INIT_ACTIVITIY);
    toFile            = getCheckBox(TO_FILE);
    fileName          = getString(FILE_NAME);
    neurons           = getString(PLOT_NEURONS);
    useLiapunov       = getCheckBox(USE_LIAPUNOV);

    if(net.getSynapseMode() == SynapseMode.CONVENTIONAL)
    {
      parseForNeurons(neurons.trim());
    }

    log.debug("USING INIT. ACT.:  " + initActivityIndex);

    if(biasString.equals("") && weightString.equals(""))
    {
      Error.feigenBaumInputError((Component)parent);
      return;
    }

    if(useLiapunov)
    {
      liapunov = new Liapunov(net);
      dpLiapunov = getNewLiapunovWindow("Liapunov exponent","liapunov");
      dpLiapunov.drawXLineNoLegend(0, Color.red);
    }


    // start transmitter window
    if (net.getSynapseMode() == SynapseMode.DYNAMIC)
    {
      dpActivation      = new Vector(net.size());
      dpReceptor        = new Vector(net.size());
      dpTransmitter     = new Vector(net.size());
      dpSynapseStrength = new Vector(net.size());

      int width = dataStorage.getWindowSize()[0];
      int height = dataStorage.getWindowSize()[1];

      int row = 0;
      // receptor windows
      row++;
      for(int i=0;i<net.size();i++)
      {
        DrawingPlane dpLocal = getNewReceptorWindow("Receptor Level Neuron " +
            i, "receptor"+i);
        dpLocal.setLocation(i*(width+5),row*(height+5));
        dpLocal.drawXLineNoLegend(0,Color.blue);
        dpReceptor.add(dpLocal);
        //dpLocal.setTitle("receptor " + i);
      }

      // transmitter windows
      row++;
      for(int i=0;i<net.size();i++)
      {
        DrawingPlane dpLocal = getNewTransmitterWindow("Transmitter Level "
            +" Neuron "+ i, "transmitter" + i );
        dpLocal.setLocation(i*(width+5),row*(height+5));
        dpLocal.drawXLineNoLegend(0,Color.blue);
        dpTransmitter.add(dpLocal);
      }

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

            DrawingPlane dpLocal = getNewSynapseWindow(
                "Synapse " + localSynapse.getSource().id() + 
                " -> " + localSynapse.getDestination().id(),
                "synapse" + localSynapse.getSource().id() + 
                "_" + localSynapse.getDestination().id()
                );

            dpLocal.setLocation(
                20 + net.neurons().indexOf(localNeuron) * (width + 10)
                + localNeuron.synapses().indexOf(localSynapse) * 20, 
                20 + localNeuron.synapses().indexOf(localSynapse) * 20 );
            dpLocal.drawXLineNoLegend(0,Color.blue);
            dpSynapseStrength.add(dpLocal);
          }
        }
      }
      // activation windows
      for(int i=0;i<net.size();i++)
      {
        DrawingPlane dpLocal = getNewWindow("Bifurcation Diagram "
            +" (Neuron "+ i+" )", "neuron"+i);
        dpLocal.setLocation(i*(width+5),0);
        dpLocal.drawXLineNoLegend(0,Color.blue);
        dpActivation.add(dpLocal);
        //dpLocal.setTitle("output " + i);
      }
    }
    else
    {
      dp = getNewWindow();
    }

    if(toFile)
    {
      try
      {

        out = new PrintWriter(
            new OutputStreamWriter(
              new FileOutputStream(fileName)));
        out.print("# ");
        for(int nIndex=0;nIndex<net.size();nIndex++)
        {
          out.print(" Output " + nIndex + ";"
              + " Receptor " + nIndex + ";"
              + " Transmitter " + nIndex);
        }
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
              out.print("; Synapse " + localSynapse.getSource().id() + "->"
                  + localSynapse.getDestination().id());
            }
          }
        }
        if(useLiapunov)
        {
          out.print("; Liapunov exponent");
        }
        out.println("");
        out.flush();
      }
      catch(FileNotFoundException ex)
      {
        ex.printStackTrace();
        return ;
      }
    }
    
    calculateBifurcation(UPWARDS, dp);
    calculateBifurcation(DOWNWARDS, dp);
    
    //close file
    if(toFile)
    {
      out.close();
    }
 
    if(net.getSynapseMode() == SynapseMode.DYNAMIC)
    {
      for(int i=0;i<net.size();i++)
      {
        DrawingPlane dpTmp = null;
        dpTmp = ((DrawingPlane)dpActivation.elementAt(i));
        dpTmp.drawLegend();
        switch(mode)
        {
          case ITERATE_BIAS:
            dpTmp.setXLabel("\u0398", "" + (neuron.id()+1));
            break;
          case ITERATE_WEIGHT:
            dpTmp.setXLabel("w",""
                + (synapses.synapse(0).getDestination().id()+1)
                + ","
                + (synapses.synapse(0).getSource().id()+1));
            break;
        }
        dpTmp.setYLabel("o",""+(i+1));

        dpTmp = ((DrawingPlane)dpTransmitter.elementAt(i));
        dpTmp.drawLegend();
        switch(mode)
        {
          case ITERATE_BIAS:
            dpTmp.setXLabel("\u0398", "" + (neuron.id()+1));
            break;
          case ITERATE_WEIGHT:
            dpTmp.setXLabel("w",""
                + (synapses.synapse(0).getDestination().id()+1)
                + ","
                + (synapses.synapse(0).getSource().id()+1));
            break;
        }
        dpTmp.setYLabel("\u03b7",""+(i+1));

        dpTmp = ((DrawingPlane)dpReceptor.elementAt(i));
        dpTmp.drawLegend();
        switch(mode)
        {
          case ITERATE_BIAS:
            dpTmp.setXLabel("\u0398", "" + (neuron.id()+1));
            break;
          case ITERATE_WEIGHT:
            dpTmp.setXLabel("w",""
                + (synapses.synapse(0).getDestination().id()+1)
                + ","
                + (synapses.synapse(0).getSource().id()+1));
            break;
        }
        dpTmp.setYLabel("\u03be",""+(i+1));

      }
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
            dpLocal.drawLegend();
            panelIndex++;
            switch(mode)
            {
              case ITERATE_BIAS:
                dpLocal.setXLabel("\u0398", "" + (neuron.id()+1));
                break;
              case ITERATE_WEIGHT:
                dpLocal.setXLabel("w",""
                    + (synapses.synapse(0).getDestination().id()+1)
                    + ","
                    + (synapses.synapse(0).getSource().id()+1));
                break;
            }
            dpLocal.setYLabel("w","" + (localSynapse.getDestination().id()+1) 
                + "," 
                + (localSynapse.getSource().id()+1));

          }
        }
      }
    }
    else
    {
      dp.drawLegend();
      switch(mode)
      {
        case ITERATE_BIAS:
          dp.setXLabel("\u0398", "" + (neuron.id()+1));
          if(dpNeurons != null)
          {
            for(int i=0; i<dpNeurons.size(); i++)
            {
              ((DrawingPlane)dpNeurons.elementAt(i)).setXLabel("\u0398", "" + (neuron.id()+1));
            }
          }
          break;
        case ITERATE_WEIGHT:
          dp.setXLabel("w",""
              + (synapses.synapse(0).getDestination().id()+1) 
              + ","
              + (synapses.synapse(0).getSource().id()+1));
          for(int i=0; i<dpNeurons.size(); i++)
          {
            ((DrawingPlane)dpNeurons.elementAt(i)).setXLabel( "w",""
            + (synapses.synapse(0).getDestination().id()+1) 
            + ","
            + (synapses.synapse(0).getSource().id()+1));

          }

          break;
      }
      dp.setYLabel("o","avg");
    }

  }

  public String getToolName()
  {
    return "Bifurcation";
  }

  public String getToolDescription()
  {
    return "Bifucation Analysis";
  }

  private void calculateBifurcation(int direction, DrawingPlane dp)
  {
    double x = 0;
    switch(dataStorage.getInialActivityMode())
    {
      case DataStorage.INITIAL_ACTIVITY_USER_DEFINED:
        log.debug("INITIAL_ACTIVITY_USER_DEFINED");
        net = dataStorage.getNet().copy(); // the original still has the initial
        break;
      case DataStorage.INITIAL_ACTIVITY_RANDOM:
        log.debug("INITIAL_ACTIVITY_RANDOM");
        net.randomInitActivity();
        break;
    }

    parseInputStrings();

    //System.out.println("net:" + net.toString());
    for(int i = 0; i < stepsX && doAnalysis; i++)
    {
      switch(direction)
      {
        case UPWARDS:
          x = xStart + dx * i;
          if(net.getSynapseMode() == SynapseMode.DYNAMIC)
          {
            for(int j=0;j<net.size();j++)
            {
              ((DrawingPlane)dpActivation.elementAt(j)).drawProgressPointX(x,Color.blue);
            }
          }
          else
          {
            dp.drawProgressPointX(x,Color.blue);
          }
          break;
        case DOWNWARDS:
          x = xEnd - dx * i;
          if(net.getSynapseMode() == SynapseMode.DYNAMIC)
          {
            for(int j=0;j<net.size();j++)
            {
              ((DrawingPlane)dpActivation.elementAt(j)).drawProgressPointX(x,Color.red);
            }
          }
          else
          {
            dp.drawProgressPointX(x,Color.red);
          }
          break;
      }

      setParameter(x);

      switch(initActivityIndex)
      {
        case 0: // continous ... do nothing
          log.debug("continous activity");
          break;
        case 1: // random // random
          net.randomInitActivity();
          log.debug("random activity");
          break;
        case 2: // reseted activity
          net.resetInitialActivities();
          log.debug("reseted activity: ");

          for(net.neurons().start(); net.neurons().hasMore();
              net.neurons().next())
          {
            log.debug("neuron " + net.neurons().neuron().id() 
                + " b = " + net.neurons().neuron().getBias()
                + " a = " + net.neurons().neuron().getActivation()
                + " o = " + net.neurons().neuron().getOutput()
                + " t = " + net.neurons().neuron().getTransmitterLevel()
                + " r = " + net.neurons().neuron().getReceptorLevel());
            SynapseList sl = net.neurons().neuron().synapses();
            if(sl != null)
            {
              for(sl.start(); sl.hasMore(); sl.next())
              {
                log.debug("Synapse from " + sl.synapse().getSource().id() + " -> " +
                    sl.synapse().getDestination().id() + " = " +
                    sl.synapse().strength());
              }
            }
              
          }
          break;
      }

      if(useLiapunov)
      {
        liapunov.reset();
      }
      for(int j=0;j<convergenceIterations && doAnalysis;j++)
      {
        if(useLiapunov)
        {
          liapunov.calculate(j);
        }
        net.process();
      }
      if(useLiapunov)
      {
        oldExponents = exponents;
        exponents = liapunov.getLiapunovExponents();
        for(int exponentIndex = 0; exponentIndex < exponents.length; exponentIndex++)
        {
          if(oldExponents != null) 
          {
            dpLiapunov.drawLine(
                x, oldExponents[exponentIndex],
                x, exponents[exponentIndex], 
                LIAPUNOV_COLOR_ARRAY[exponentIndex % LIAPUNOV_COLOR_ARRAY.length]);
          }
        }
      }


      //System.out.println("neuron activation: " +
      //net.neurons().neuron(0).getActivation());
      for(int j=0;j<drawIterations && doAnalysis;j++)
      {
        net.process();
        if(net.getSynapseMode() == SynapseMode.DYNAMIC)
        {

          for(int nIndex=0;nIndex<net.size();nIndex++)
          {
            ((DrawingPlane) dpActivation.elementAt(nIndex)).
              drawPoint(x,net.neurons().neuron(nIndex).getOutput());
            ((DrawingPlane) dpTransmitter.elementAt(nIndex)).
              drawPoint(x,net.neurons().neuron(nIndex).getTransmitterLevel());
            ((DrawingPlane) dpReceptor.elementAt(nIndex)).
              drawPoint(x,net.neurons().neuron(nIndex).getReceptorLevel());
            if(toFile)
            {
              out.print("" + x 
                  + " " +  net.neurons().neuron(nIndex).getOutput()
                  + " " +  net.neurons().neuron(nIndex).getReceptorLevel()
                  + " " +  net.neurons().neuron(nIndex).getTransmitterLevel());
            }
          }

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
                if(toFile)
                {
                  out.print(" " + localSynapse.strength());
                }

                dpLocal.drawPoint(x,localSynapse.strength());
                panelIndex++;

              }
            }
          }
        }
        else
        {
            dp.drawPoint(x,averageOutput());
            for(int ni=0; ni < dpNeurons.size(); ni++)
            {
              DrawingPlane dpN = (DrawingPlane)dpNeurons.elementAt(ni);
              Neuron n = (Neuron)singleNeuronVector.elementAt(ni);
              dpN.drawPoint(x,n.getOutput());
            }
            if(toFile)
            {
              out.print("" + x + " " +  averageOutput());
            }
        }
        if(toFile)
        {
          if(useLiapunov)
          {
            for(int exponentIndex=0; exponentIndex < exponents.length; exponentIndex++)
            {
              out.print("" + exponents[exponentIndex]);
            }
          }
          out.println("");
        }
      }


    }// for (i -> width-1)
  }

  private double averageOutput()
  {
    double output = 0;
    for(net.neurons().start();net.neurons().hasMore();net.neurons().next())
    {
      output += net.neurons().neuron().getOutput();
    }
    output = output / (double)net.neurons().size();
    return output;
  }

  private void setParameter(double value)
  {
    switch(mode)
    {
      case ITERATE_BIAS:
//        if (useChaoticNeuron)
//        {
//          neuron.setBias(dataStorage.getChaosValue());
//        }
//        else
        {
          neuron.setBias(value);
        }
        break;
      case ITERATE_WEIGHT:
//        if(useChaoticNeuron)
//        {
//          synapse.setStrength(dataStorage.getChaosValue());
//        }
//        else
        {
          for(synapses.start(); synapses.hasMore(); synapses.next())
          {
            Synapse synapse = synapses.synapse();
            synapse.setStrength(value);
          }
        }
        break;
    }
  }

  private void parseForNeurons(String neuronString)
  {
    if(neuronString.length() == 0)
    {
      return;
    }
    singleNeuronVector = new Vector();
    Vector singleNeuronNamesVector = new Vector();
    String names[] = neuronString.split(",");
    dpNeurons = new Vector(names.length);

    for(int i=0; i < names.length; i++)
    {
      singleNeuronNamesVector.add(names[i].trim());
      String neuronType = names[i].trim().substring(0,1);
      int neuronIndex = Integer.parseInt(
          names[i].trim().substring(1,names[i].trim().length()));
      if(neuronType.toLowerCase().equals("h"))
      {
        singleNeuronVector.add(net.getHiddenNeurons().neuron(neuronIndex-1));
      }
      if(neuronType.toLowerCase().equals("o"))
      {
        singleNeuronVector.add(net.getOutputNeurons().neuron(neuronIndex-1));
      }
      if(neuronType.toLowerCase().equals("i"))
      {
        singleNeuronVector.add(net.getInputNeurons().neuron(neuronIndex-1));
      }
      DrawingPlane dpTmp = getNewWindow(names[i].trim());
      dpTmp.setYLabel(names[i].substring(0,1),names[i].substring(1,names[i].length()));
      dpNeurons.add(dpTmp);
      ((DrawingPlane)dpNeurons.elementAt(i)).setLocation(100 + i * 10, 100 + i * 10);
    }




  }

  private void parseInputStrings()
  {

    String index = null;

    if(biasString.equals(""))
    {
      index = weightString;
      mode = ITERATE_WEIGHT;
    }
    else
    {
      index = biasString;
      mode = ITERATE_BIAS;
    }

    switch(mode)
    {
      case ITERATE_BIAS:
        // if bias : get the neuron, which bias will be set
        neuron = net.getNeuron(Integer.parseInt(index)-1);
        break;
      case ITERATE_WEIGHT:
        // if weight : get the synapse, that will be set
        StringTokenizer stWeights = new StringTokenizer(index,",");
        while(stWeights.hasMoreTokens())
        {
          String token = stWeights.nextToken();
          log.debug("parsing: " + token);
          StringTokenizer st = new StringTokenizer(token,"-");
          int destinationIndex = Integer.parseInt(st.nextToken())-1;
          int sourceIndex = Integer.parseInt(st.nextToken())-1;
          Neuron destination = net.getNeuron(destinationIndex);
          Neuron source = net.getNeuron(sourceIndex);
          // here it is
          if(synapses == null)
          {
            synapses = new SynapseList();
          }
          synapses.add(destination.getSynapse(source));
          log.debug("added synapse " + sourceIndex + " -> " + destinationIndex +
              " to list list of synapses");
              
        }
        break;
    }


  }

}
