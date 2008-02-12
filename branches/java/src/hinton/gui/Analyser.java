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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import cholsey.Net;
import cholsey.Neuron;
import cholsey.NeuronList;
import cholsey.NeuronType;

public class Analyser extends JFrame implements ActionListener
{

  private JDesktopPane desktop    = new JDesktopPane();
  private Vector   openWindows    = new Vector();
  private JMenuItem    newWindowMenuItem 
                                  = new JMenuItem("New Neuron Panel");
  private JMenuItem colorMenu     = new JMenuItem("Colors");
  private JMenuItem cutMenu       = new JMenuItem("Cut");

  private double fitnessValue     = 0;
  private Net net = null;
  private RobotStruct robotStruct = null;

  private Vector inputColors      = new Vector();
  private Vector outputColors     = new Vector();
  private Vector hiddenColors     = new Vector();

  private Vector dataPoints       = new Vector();
  private Vector inputDataPoints  = new Vector();
  private Vector outputDataPoints = new Vector();
  private Vector hiddenDataPoints = new Vector();

  private AnalyserColorChooser analyserColorChooser = null;

  private NeuronPanel neuronPanel = new NeuronPanel();


  public Analyser()
  {
    super("Analyser");
    setBounds ( 100,100, 400,400);
    setJMenuBar(getMenu());
    setContentPane(desktop);
    reset();
    setColorVector();
    analyserColorChooser = new AnalyserColorChooser( 
        inputColors, 
        outputColors,
        hiddenColors);

    dataPoints.add(inputDataPoints);
    dataPoints.add(outputDataPoints);
    dataPoints.add(hiddenDataPoints);

    desktop.add(neuronPanel);

  }

  /**
   * Sets the initial color vectors for drawing the output/input/hidden neuron
   * output
   */
  private void setColorVector()
  {
    inputColors.add(Color.blue);
    inputColors.add(Color.red);
    inputColors.add(Color.green);
    inputColors.add(Color.yellow);

    outputColors.add(Color.blue);
    outputColors.add(Color.red);
    outputColors.add(Color.green);
    outputColors.add(Color.yellow);

    hiddenColors.add(Color.blue);
    hiddenColors.add(Color.red);
    hiddenColors.add(Color.green);
    hiddenColors.add(Color.yellow);

  }

  /**
   * Sets the net, that should be visualised
   * @param    net
   * @see cholsey.Net
   */
  public void setNet(Net net)
  {
    this.net = net;
    for(int i=0;i<openWindows.size(); i++)
    {
      AnalyserInternalFrame ai =
        (AnalyserInternalFrame)openWindows.elementAt(i);
      if(ai != null)
      {
        ai.setNet(this.net);
      }
    }
  }

  /**
   * Sets the RobotStruct for the Analysis. The RobotStruct gives the names of
   * the input/output neurons
   * @param    robotStruct
   * @see hinton.Executive.RobotStruct
   */
  public void setRobotStruct(RobotStruct robotStruct)
  {
    this.robotStruct = robotStruct;
    for(int i=0;i<openWindows.size(); i++)
    {
      AnalyserInternalFrame ai =
        (AnalyserInternalFrame)openWindows.elementAt(i);
      if(ai != null)
      {
        ai.setRobotStruct(this.robotStruct);
      }
    }
  }

  /**
   * Adds a step to the Analysis. In every step all the outputs from the
   * input/output/hidden neurons are added to the analysis data. 
   */
  public void step(Net localNet)
  {
    //localNet = processParameter.localNet();
    NeuronList inputNeurons  = localNet.getInputNeurons();
    NeuronList outputNeurons = localNet.getOutputNeurons();
    NeuronList hiddenNeurons = localNet.getHiddenNeurons();
    NeuronList neurons       = localNet.neurons();

    // new localNet or no output vectors there -> create new vectors
    if(outputDataPoints.size() != outputNeurons.size())
    {
      outputDataPoints.removeAllElements();
      for(outputNeurons.start(); outputNeurons.hasMore(); outputNeurons.next())
      {
        outputDataPoints.add(new Vector());
      }
      if(outputNeurons.size() > outputColors.size())
      {
        for(int i=0;i<outputNeurons.size()-outputColors.size();i++)
        {
          outputColors.add(Color.blue);
        }
      }
    }

    if(inputDataPoints.size() != inputNeurons.size())
    {
      inputDataPoints.removeAllElements();
      for(inputNeurons.start(); inputNeurons.hasMore(); inputNeurons.next())
      {
        inputDataPoints.add(new Vector());
      }
      if(inputNeurons.size() > inputColors.size())
      {
        for(int i=0;i<inputNeurons.size()-inputColors.size();i++)
        {
          inputColors.add(Color.blue);
        }
      }
    }

    if(hiddenDataPoints.size() != hiddenNeurons.size())
    {
      hiddenDataPoints.removeAllElements();
      for(hiddenNeurons.start(); hiddenNeurons.hasMore(); hiddenNeurons.next())
      {
        hiddenDataPoints.add(new Vector());
      }
      if(hiddenNeurons.size() > hiddenColors.size())
      {
        for(int i=0;i<hiddenNeurons.size()-hiddenColors.size();i++)
        {
          hiddenColors.add(Color.blue);
        }
      }
    }



    // only because getInputNeurons / getHiddenNeurons / getOutputNeurons seem
    // to be buggy
    int inputIndex  = 0;
    int outputIndex = 0;
    int hiddenIndex = 0;
    for(neurons.start(); neurons.hasMore(); neurons.next())
    {
      Neuron n = neurons.neuron();
      Vector v = null;
      switch (n.getNeuronType().type())
      {
        case NeuronType.NEURON_TYPE_INPUT:
          v = (Vector)inputDataPoints.elementAt(inputIndex);
          inputIndex++;
          v.add(new Double(n.getOutput()));
          break;
        case NeuronType.NEURON_TYPE_OUTPUT:
          v = (Vector)outputDataPoints.elementAt(outputIndex);
          outputIndex++;
          v.add(new Double(n.getOutput()));
          break;
        case NeuronType.NEURON_TYPE_HIDDEN:
          v = (Vector)hiddenDataPoints.elementAt(hiddenIndex);
          hiddenIndex++;
          v.add(new Double(n.getOutput()));
          break;
      }
    }

  }

  /**
   * Draw allt the analysis data.
   */
  public void draw()
  {
    for(int i=0;i<openWindows.size();i++)
    {
      AnalyserInternalFrame ai =
        (AnalyserInternalFrame)openWindows.elementAt(i);
      if(ai != null)
      {
        ai.draw();
      }
    }
  }

  /**
   * Reset the analysis data. Remove all collected data points.
   */
  public void reset()
  {
    inputDataPoints.removeAllElements();
    outputDataPoints.removeAllElements();
    hiddenDataPoints.removeAllElements();
  }

  /**
   * Set the current fitness value, so it can be displayed.
   */
  public void setFitnessValue(double fitnessValue)
  {
    this.fitnessValue = fitnessValue;
  }

  public void actionPerformed(ActionEvent e)
  {
    System.out.println(e.getActionCommand());

    if(e.getSource() == cutMenu)
    {
      neuronPanel.setNet(net);
      neuronPanel.setVisible(true);
      return;
    }

    if(e.getSource() == newWindowMenuItem)
    {
      int updateIndex = -1;
      for(int i=0; i < openWindows.size(); i++)
      {
        if (openWindows.elementAt(i) == null)
        {
          updateIndex = i;
        }
      }

      AnalyserInternalFrame ai = new AnalyserInternalFrame("Neuron Panel",
          (updateIndex == -1)?openWindows.size():updateIndex, 
          openWindows, 
          net,
          robotStruct,
          inputColors,
          outputColors,
          hiddenColors,
          dataPoints);

      if(updateIndex == -1 )
      {
        openWindows.add(ai);
      }
      else 
      {
        openWindows.setElementAt(ai,updateIndex);
      }

      desktop.add(ai);
      ai.setVisible(true);
    }


    if(e.getSource() == colorMenu)
    {
      analyserColorChooser.updateView();
      analyserColorChooser.setVisible(true);
    }
  }

  private JMenuBar getMenu()
  {
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("Open");
    menuBar.add(menu);

    menu.add(newWindowMenuItem);
    newWindowMenuItem.addActionListener(this);

    menu = new JMenu("Config");
    menuBar.add(menu);
    menu.add(colorMenu);
    colorMenu.addActionListener(this);

    menu = new JMenu("Brain");
    menuBar.add(menu);
    menu.add(cutMenu);
    cutMenu.addActionListener(this);

    return menuBar;

  } 

  public void updateNet()
  {
    if(neuronPanel.isVisible())
    {
      neuronPanel.updateNet();
    }
  }

}
