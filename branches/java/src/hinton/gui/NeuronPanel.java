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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cholsey.Net;
import cholsey.Neuron;
import cholsey.NeuronList;

public class NeuronPanel extends JInternalFrame implements ActionListener
{
  private JPanel panel = new JPanel();
  private JPanel inputNeuronPanel = new JPanel();
  private JPanel outputNeuronPanel = new JPanel();
  private JPanel hiddenNeuronPanel = new JPanel();

  private Vector inputValues = new Vector();
  private Vector outputValues = new Vector();
  private Vector hiddenValues = new Vector();

  private Net net = null;

  

  public NeuronPanel()
  {
    super("Brain Surgeon",
        true, //resizable
        true, //closable
        true, //maximizable
        true);//iconifiable

    setBounds ( 100,100, 500, 200);
    setContentPane(panel);

    inputNeuronPanel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder("Input Neurons"),
          BorderFactory.createEmptyBorder(5,5,5,5)));

    outputNeuronPanel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder("Output Neurons"),
          BorderFactory.createEmptyBorder(5,5,5,5)));


    hiddenNeuronPanel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder("Hidden Neurons"),
          BorderFactory.createEmptyBorder(5,5,5,5)));
    panel.setLayout(new GridLayout(0,3));
    panel.add(inputNeuronPanel);
    panel.add(outputNeuronPanel);
    panel.add(hiddenNeuronPanel);

    //setSize(getMinimumSize());

  }

  public void setNet(Net net)
  {
    this.net = net;
    updatePanel();
  }

  public void actionPerformed(ActionEvent e)
  {

  }

  private void updatePanel()
  {
    setInputNeuronPanel();
    setOutputNeuronPanel();
    setHiddenNeuronPanel();
    //setSize(getMinimumSize());
  }

  public void updateNet()
  {
    if(inputValues.size() == 0)
    {
      updatePanel();
    }
    NeuronList inputNeurons = net.getInputNeurons();
    NeuronList outputNeurons = net.getOutputNeurons();
    NeuronList hiddenNeurons = net.getHiddenNeurons();
    int i=0;
    for(inputNeurons.start();inputNeurons.hasMore();inputNeurons.next())
    {
      JTextField input = (JTextField)inputValues.elementAt(i);
      Neuron n = inputNeurons.neuron();
      try
      {
        double value = Double.parseDouble(input.getText());
        n.setOutput(value);
      }
      catch(NumberFormatException e)
      { }
      i++;
    }
    i=0;
    for(outputNeurons.start();outputNeurons.hasMore();outputNeurons.next())
    {
      JTextField output = (JTextField)outputValues.elementAt(i);
      Neuron n = outputNeurons.neuron();
      try
      {
        double value = Double.parseDouble(output.getText());
        n.setActivation(value);
      }
      catch(NumberFormatException e)
      { }
      i++;
    }
    i=0;
    if(hiddenNeurons != null && hiddenNeurons.size() > 0)
    {
      for(hiddenNeurons.start();hiddenNeurons.hasMore();hiddenNeurons.next())
      {
        JTextField hidden = (JTextField)hiddenValues.elementAt(i);
        Neuron n = hiddenNeurons.neuron();
        try
        {
          double value = Double.parseDouble(hidden.getText());
          n.setActivation(value);
        }
        catch(NumberFormatException e)
        { }
        i++;
      }
    }

  }

  private void setInputNeuronPanel()
  {
    inputNeuronPanel.removeAll();

    GridBagLayout gbl = new GridBagLayout();
    inputNeuronPanel.setLayout(gbl);
    inputValues = new Vector(net.getInputNeurons().size());
    int row = 0;

    NeuronList inputNeurons = net.getInputNeurons();
    for(inputNeurons.start();inputNeurons.hasMore();inputNeurons.next())
    {
      JLabel     label = new JLabel("Input Neuron " + row);
      JTextField input = new JTextField("x",3);


      GridBagConstraints labelConstraints = 
        new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.CENTER,
            GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
      GridBagConstraints inputConstraints = 
        new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.CENTER,
            GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

      gbl.setConstraints(label, labelConstraints);
      gbl.setConstraints(input, inputConstraints);

      inputNeuronPanel.add(label);
      inputNeuronPanel.add(input);
      inputValues.add(input);
      row++;
    }
  }

  private void setOutputNeuronPanel()
  {
    outputNeuronPanel.removeAll();

    GridBagLayout gbl = new GridBagLayout();
    outputNeuronPanel.setLayout(gbl);
    outputValues = new Vector(net.getOutputNeurons().size());
    int row = 0;

    NeuronList outputNeurons = net.getOutputNeurons();
    for(outputNeurons.start();outputNeurons.hasMore();outputNeurons.next())
    {
      JLabel     label = new JLabel("Output Neuron " + row);
      JTextField output = new JTextField("x",3);


      GridBagConstraints labelConstraints = 
        new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.CENTER,
            GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
      GridBagConstraints outputConstraints = 
        new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.CENTER,
            GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

      gbl.setConstraints(label, labelConstraints);
      gbl.setConstraints(output, outputConstraints);

      outputNeuronPanel.add(label);
      outputNeuronPanel.add(output);
      outputValues.add(output);
      row++;
    }
  }


  private void setHiddenNeuronPanel()
  {
    hiddenNeuronPanel.removeAll();

    GridBagLayout gbl = new GridBagLayout();
    hiddenNeuronPanel.setLayout(gbl);
    hiddenValues = new Vector(net.getHiddenNeurons().size());
    int row = 0;

    NeuronList hiddenNeurons = net.getHiddenNeurons();
    if(hiddenNeurons != null && hiddenNeurons.size() > 0)
    {
      for(hiddenNeurons.start();hiddenNeurons.hasMore();hiddenNeurons.next())
      {
        JLabel     label = new JLabel("Hidden Neuron " + row);
        JTextField hidden = new JTextField("x",3);


        GridBagConstraints labelConstraints = 
          new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.CENTER,
              GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
        GridBagConstraints hiddenConstraints = 
          new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.CENTER,
              GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

        gbl.setConstraints(label, labelConstraints);
        gbl.setConstraints(hidden, hiddenConstraints);

        hiddenNeuronPanel.add(label);
        hiddenNeuronPanel.add(hidden);
        hiddenValues.add(hidden);
        row++;
      }
    }
    else
    {
      JLabel label = new JLabel("no hidden");
      JLabel label2 = new JLabel("neurons");
      GridBagConstraints labelConstraints = 
        new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
            GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
      GridBagConstraints label2Constraints = 
        new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER,
            GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

      gbl.setConstraints(label, labelConstraints);
      gbl.setConstraints(label2, label2Constraints);

      hiddenNeuronPanel.add(label);
      hiddenNeuronPanel.add(label2);


    }
  }





}



