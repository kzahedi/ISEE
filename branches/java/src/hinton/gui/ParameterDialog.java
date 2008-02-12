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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cholsey.Net;


public class ParameterDialog extends JFrame implements ActionListener
{
  private RobotStruct robotStruct = null;
  private Vector inputSelections = new Vector();
  private Vector outputSelections = new Vector();
  private int inputNeurons  = 0;
  private int outputNeurons = 0;


  public ParameterDialog(RobotStruct robotStruct, Net net)
  {
    super("ParameterDialog");
    setBounds (100,100, 400,200);
    this.robotStruct = robotStruct;
    inputNeurons  = net.getInputNeurons().size();
    outputNeurons = net.getOutputNeurons().size();
    Vector inputValues = new Vector();
    Vector outputValues = new Vector();
    JPanel rootPanel = new JPanel();
    JScrollPane scrollPane = new JScrollPane(rootPanel);
    JButton okButton     = new JButton("OK");
    JButton cancelButton = new JButton("CANCEL");

    okButton.addActionListener(this);
    cancelButton.addActionListener(this);
    
    for(Enumeration k = robotStruct.inputKeys(); k.hasMoreElements();)
    {
      String key = (String)k.nextElement();
      inputValues.add(key.toString());
    }

    for(Enumeration k = robotStruct.outputKeys(); k.hasMoreElements();)
    {
      String key = (String)k.nextElement();
      outputValues.add(key.toString());
    }

    for(int i=0;i<outputNeurons;i++)
    {
      JComboBox jc = new JComboBox(inputValues);
      String item = robotStruct.getInputObject(i);
      if (item != null)
      {
        jc.setSelectedItem(item);
      }
      else 
      {
        jc.setEnabled(false);
      }
      outputSelections.add(jc);
    }

    for(int i=0;i<inputNeurons;i++)
    {
      JComboBox jc = new JComboBox(outputValues);
      String item = robotStruct.getOutputObject(i);
      if (item != null)
      {
        jc.setSelectedItem(item);
      }
      else 
      {
        jc.setEnabled(false);
      }
      inputSelections.add(jc);
    }

    GridBagLayout gbl = new GridBagLayout();
    GridBagConstraints lc;
    GridBagConstraints c;

    rootPanel.setLayout(gbl);
    for(int i=0;i<outputNeurons;i++)
    {
      int y = 2*i;
      JLabel label = new JLabel("Output Neuron " + i);
      JComboBox jcb = (JComboBox)outputSelections.elementAt(i);
      lc = new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.EAST,
            GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
      c = 
        new GridBagConstraints(1, y, 1, 1, 0, 0, GridBagConstraints.WEST,
            GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
      gbl.setConstraints(label,lc);
      gbl.setConstraints(jcb,c);
      rootPanel.add(label);
      rootPanel.add(jcb);
    }

    for(int i=0;i<inputNeurons;i++)
    {
      int y = (2*i);
      JLabel label = new JLabel("Input Neuron " + i);
      JComboBox jcb = (JComboBox)inputSelections.elementAt(i);
      lc = 
        new GridBagConstraints(2, y, 1, 1, 0, 0, GridBagConstraints.EAST,
            GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
      c = 
        new GridBagConstraints(3, y, 1, 1, 0, 0, GridBagConstraints.WEST,
            GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
      gbl.setConstraints(label,lc);
      gbl.setConstraints(jcb,c);
      rootPanel.add(label);
      rootPanel.add(jcb);
    }

    int index = 2*((inputNeurons > outputNeurons)?inputNeurons 
        : outputNeurons)+1;
    gbl.setConstraints(okButton,
        new GridBagConstraints(2, index, 1, 1, 0, 0, GridBagConstraints.WEST,
            GridBagConstraints.NONE, new Insets(5,5,5,5),5,5));
    gbl.setConstraints(cancelButton,
        new GridBagConstraints(3, index, 1, 1, 0, 0, GridBagConstraints.WEST,
            GridBagConstraints.NONE, new Insets(5,5,5,5),5,5));
    rootPanel.add(okButton);
    rootPanel.add(cancelButton);
    getContentPane().add(scrollPane);
    
  }

  public void actionPerformed(ActionEvent e)
  {
    if (e.getActionCommand().equals("OK"))
    {
      saveTheStuff();
      hide();
      return;
    }
    if(e.getActionCommand().equals("CANCEL"))
    {
      hide();
      return;
    }
  }
  

  public void saveTheStuff()
  {
    for(int i=0;i<inputNeurons;i++)
    {
      JComboBox jc = (JComboBox)outputSelections.elementAt(i);
      if(jc.isEnabled())
      {
        robotStruct.setOutputMap(i,jc.getSelectedItem());
      }
    }
    for(int i=0;i<outputNeurons;i++)
    {
      JComboBox jc = (JComboBox)inputSelections.elementAt(i);
      if(jc.isEnabled())
      {
        robotStruct.setInputMap(i,jc.getSelectedItem());
      }
    }
  }
  
  public static void main(String argv[])
  {
    ParameterDialog pd = new ParameterDialog(null,null);
    pd.setVisible(true);
  }
}
