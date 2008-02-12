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

import cholsey.Neuron;
import cholsey.NeuronType;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.Vector;

public class TransientPlot extends Tool
{ 

  private final static Color[] COLOR_ARRAY = 
  {
    Color.blue,
    Color.cyan,
    Color.green,
    Color.magenta,
    Color.orange,
    Color.pink,
    Color.red,
    Color.yellow,
    Color.black
  };

  private static String FILE_NAME                 = "file name";
  private static String FILE_OUTPUT               = "to file";
  private static String NEURONS                   = "neurons";
  private static String PLOT_START_INDEX          = "plot start index";

  private DrawingPlane  dp                        = null;

  private Vector        outputVector              = new Vector();
  private Vector        hiddenVector              = new Vector();
  private Vector        inputVector               = new Vector();
  private Vector        readBufferVector          = new Vector();
  private Vector        lastOutputValueVector     = new Vector();
  private Vector        lastInputValueVector      = new Vector();
  private Vector        lastHiddenValueVector     = new Vector();
  private Vector        lastReadBufferValueVector = new Vector();
  private Vector        legends                   = new Vector();

  private String        filename                  = null;
  private boolean       writeFile                 = false;
  private String        neuronString               = null;
  private int           plotStartIndex            = 0;

  private Vector        dpTransmitter             = null;
  private Vector        dpActivation              = null;
  private Vector        dpReceptor                = null;
  private Vector        dpSynapseStrength         = null;


  public void init()
  {
    setToolPriority(-8);
    addString(NEURONS,"");
    addInteger(PLOT_START_INDEX,0,Integer.MAX_VALUE,0);
    addString(FILE_NAME,"");
    addCheckBox(FILE_OUTPUT,false);

  }

  public boolean needsNet()
  {
    return true;
  }


  public String getToolName()
  {
    return "Transient Plot";
  }

  public String getToolDescription()
  {
    return "Plot of Net Dynamics";
  }

  public void doAnalysis()
  {
    neuronString = getString(NEURONS);
    plotStartIndex = getInteger(PLOT_START_INDEX);
    filename = getString(FILE_NAME);
    writeFile = getCheckBox(FILE_OUTPUT);

    dp = getNewWindow("TransientPlot",
        plotStartIndex,
        convergenceIterations,
        yStart,
        yEnd);

    //dx = (xEnd-xStart)/(double)(convergenceIterations - plotStartIndex);




    analyseUserInputs();
    calculateTransientPlot();
  }

 
  private void calculateTransientPlot()
  {
    File file = null;
    PrintWriter out = null;
    int colorIndex = 0;
    if(dataStorage.getInialActivityMode() == 1) // user defined
    {
      net = dataStorage.getNet().copy(); // the origianl still has the initial
                                         // activities
    }
    else // random
    {
      net.randomInitActivity();
    }

    int outputNeuronsSize = Math.min(outputVector.size(),
        net.getOutputNeurons().size());
    int inputNeuronsSize = Math.min(inputVector.size(),
        net.getInputNeurons().size());
    int hiddenNeuronsSize = Math.min(hiddenVector.size(),
        net.getHiddenNeurons().size());
    int readBufferNeuronsSize = Math.min(readBufferVector.size(),
        net.getReadBufferNeurons().size());

    if(writeFile == true)
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
        for(int i=0; i < net.getReadBufferNeurons().size(); i++)
        {
          out.print(" ReadBufferNeuron_" + i + "_Aktivitaet ");
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
        for(int i=0; i < net.getReadBufferNeurons().size(); i++)
        {
          out.print(" ReadBufferNeuron_" + i + "_Output ");
        }
        for(int i=0; i < net.getHiddenNeurons().size(); i++)
        {
          out.print(" HiddenNeuron_" + i + "_Output ");
        }
        out.println("");
      }
      catch(IOException ioe)
      {
        ioe.printStackTrace();
      }
    }


    for(int step = 0; step < convergenceIterations + 1; step++)
    {
      colorIndex = 0;
      net.process();
      if(step < plotStartIndex)
      {
        continue;
      }

      int x = step;

      if(writeFile)
      {
        writeAllTheStuff(out);
      }
      
      for(int i=0; i < outputNeuronsSize; i++)
      {
        int neuronIndex = ((Integer)outputVector.elementAt(i)).intValue()-1;
        if(x > plotStartIndex) // draw line if at least 2 points are drawn
        {
          double lastY =
            ((Double)lastOutputValueVector.elementAt(i)).doubleValue();

          dp.drawLine(
              x - 1,
              lastY,
              x,
              net.neurons().neuron(neuronIndex).getOutput(),
              (colorIndex >= COLOR_ARRAY.length)?
                Color.black:
                COLOR_ARRAY[colorIndex]);

          if(x < convergenceIterations - 1)
          {
            dp.drawFilledCircle(
                x,
                net.neurons().neuron(neuronIndex).getOutput(),
                5,
                (colorIndex >= COLOR_ARRAY.length)?
                Color.black:
                COLOR_ARRAY[colorIndex]);
          }
        }
        colorIndex = colorIndex + 1;
        lastOutputValueVector.setElementAt(new Double(
              net.neurons().neuron(neuronIndex).getOutput()),i);
      }
      
      for(int i=0; i < hiddenNeuronsSize; i++)
      {
        int neuronIndex = ((Integer)hiddenVector.elementAt(i)).intValue()-1;
        if(x > plotStartIndex) // draw line if at least 2 points are drawn
        {
          double lastY =
            ((Double)lastHiddenValueVector.elementAt(i)).doubleValue();

          dp.drawLine(
              x-1,
              lastY,
              x,
              net.neurons().neuron(neuronIndex).getOutput(),
              (colorIndex >= COLOR_ARRAY.length)?
                Color.black:
                COLOR_ARRAY[colorIndex]);

          if(x < convergenceIterations - 1)
          {
            dp.drawFilledCircle(
                x,
                net.neurons().neuron(neuronIndex).getOutput(),
                2,
                (colorIndex >= COLOR_ARRAY.length)?
                Color.black:
                COLOR_ARRAY[colorIndex]);
          }
        }
        colorIndex = colorIndex + 1;
        lastHiddenValueVector.setElementAt(new Double(
              net.neurons().neuron(neuronIndex).getOutput()),i);
      }

      for(int i=0; i < inputNeuronsSize; i++)
      {
        int neuronIndex = ((Integer)inputVector.elementAt(i)).intValue()-1;
        if(x > plotStartIndex) // draw line if at least 2 points are drawn
        {
          double lastY =
            ((Double)lastInputValueVector.elementAt(i)).doubleValue();

          dp.drawLine(
              x-1,
              lastY,
              x,
              net.neurons().neuron(neuronIndex).getOutput(),
              (colorIndex >= COLOR_ARRAY.length)?
              Color.black:
              COLOR_ARRAY[colorIndex]);
          if(x < convergenceIterations - 1)
          {
            dp.drawFilledCircle(
                x,
                net.neurons().neuron(neuronIndex).getOutput(),
                2,
                (colorIndex >= COLOR_ARRAY.length)?
                Color.black:
                COLOR_ARRAY[colorIndex]);
          }        
        }
        colorIndex = colorIndex + 1;
        lastInputValueVector.setElementAt(new Double(
              net.neurons().neuron(neuronIndex).getOutput()),i);
      }

      for(int i=0; i < readBufferNeuronsSize; i++)
      {
        int neuronIndex = ((Integer)readBufferVector.elementAt(i)).intValue()-1;
        if(x > plotStartIndex) // draw line if at least 2 points are drawn
        {
          double lastY =
            ((Double)lastReadBufferValueVector.elementAt(i)).doubleValue();

          dp.drawLine(
              x-1,
              lastY,
              x,
              net.neurons().neuron(neuronIndex).getOutput(),
              (colorIndex >= COLOR_ARRAY.length)?
              Color.black:
              COLOR_ARRAY[colorIndex]);
          if(x < convergenceIterations - 1)
          {
            dp.drawFilledCircle(
                x,
                net.neurons().neuron(neuronIndex).getOutput(),
                2,
                (colorIndex >= COLOR_ARRAY.length)?
                Color.black:
                COLOR_ARRAY[colorIndex]);
          }        
        }
        colorIndex = colorIndex + 1;
        lastReadBufferValueVector.setElementAt(new Double(
              net.neurons().neuron(neuronIndex).getOutput()),i);
      }


    }

    if(writeFile == true)
    {
      out.flush();
      out.close();
    }

    dp.drawLegend();
    dp.drawTransientPlotLegend(legends, COLOR_ARRAY);
    //dp.drawFilledCircle(0,0,10, Color.blue);
    //dp.drawFilledCircle(0,0.5,10);


  }



  /**
   * takes the input string from the panel, and generates vector from that.
   */
  private void analyseUserInputs()
  {
    outputVector = new Vector();
    lastOutputValueVector = new Vector();
    hiddenVector = new Vector();
    lastHiddenValueVector = new Vector();
    inputVector = new Vector();
    lastInputValueVector = new Vector();
    readBufferVector= new Vector();
    lastReadBufferValueVector = new Vector();
    legends = new Vector();

    StringTokenizer st = new StringTokenizer(neuronString, ",");
    String token = null;

    while(st.hasMoreTokens())
    {
      token = st.nextToken().trim();
      int tokenIndex = Integer.parseInt(token);
      int neuronIndex = tokenIndex - 1;
      Neuron n = net.neurons().neuron(neuronIndex);
      switch(n.getNeuronType().type())
      {
        case NeuronType.NEURON_TYPE_INPUT:
          inputVector.add(tokenIndex);
          lastInputValueVector.add(new Double(0.0));
          break;
        case NeuronType.NEURON_TYPE_OUTPUT:
          outputVector.add(tokenIndex);
          lastOutputValueVector.add(new Double(0.0));
          break;
        case NeuronType.NEURON_TYPE_HIDDEN:
          hiddenVector.add(tokenIndex);
          lastHiddenValueVector.add(new Double(0.0));
          break;
        case NeuronType.NEURON_TYPE_READ_BUFFER:
          readBufferVector.add(tokenIndex);
          lastReadBufferValueVector.add(new Double(0.0));
          break;

      }
    }

    for(int i=0; i < inputVector.size(); i++)
    {
      int index = ((Integer)inputVector.elementAt(i)).intValue();
      legends.add("I"+index);
    }
    for(int i=0; i < outputVector.size(); i++)
    {
      int index = ((Integer)outputVector.elementAt(i)).intValue();
      legends.add("O"+index);
    }
    for(int i=0; i < hiddenVector.size(); i++)
    {
      int index = ((Integer)hiddenVector.elementAt(i)).intValue();
      legends.add("H"+index);
    }
    for(int i=0; i < readBufferVector.size(); i++)
    {
      int index = ((Integer)readBufferVector.elementAt(i)).intValue();
      legends.add("R"+index);
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
    for(int i=0; i < net.getReadBufferNeurons().size(); i++)
    {
      out.print(net.getReadBufferNeurons().neuron(i).getActivation() + " ");
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
    for(int i=0; i < net.getReadBufferNeurons().size(); i++)
    {
      out.print(net.getReadBufferNeurons().neuron(i).getOutput() + " ");
    }
    for(int i=0; i < net.getHiddenNeurons().size(); i++)
    {
      out.print(net.getHiddenNeurons().neuron(i).getOutput() + " ");
    }
    out.println("");

  }

}
