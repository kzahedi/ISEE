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

package Evolution;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
//TODO check is it really necessary
//import com.equitysoft.components.IEButton;

public class EvoCtrlPanel implements ActionListener{
  private JPanel panel = new JPanel(false);
  private Evolution evo = null;
  private Thread evoThread = null;

  // labels for states
  private JLabel     genLabel        = new JLabel("evolving gen.: ");
  private JTextField genLabelInput   = new JTextField(6);

  // labels and input fields
  private JLabel     warmUpLabel         = new JLabel("warm up :");
  private JTextField warmUpLabelInput    = new JTextField(8);
  private JLabel     testStepsLabel      = new JLabel("test steps :");
  private JTextField testStepsLabelInput = new JTextField(8);




  public EvoCtrlPanel(Evolution evo){
    this.evo   = evo;
    new Thread(this.evo);


    /* set ActionListener */
    this.evo.setEvoCtrlPanel(this);


    GridBagLayout      layout = new GridBagLayout();
    GridBagConstraints c      = new GridBagConstraints();
    this.panel.setLayout(layout);

    panel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder("global evolution data"),
          BorderFactory.createEmptyBorder(5,5,5,5)));

    // generation 
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 0;
    c.weighty = 0.5;
    c.weightx = 0.5;
    layout.setConstraints(genLabel, c);
    panel.add(genLabel);

    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 0;
    layout.setConstraints(genLabelInput, c);
    genLabelInput.setEditable(false);
    genLabelInput.setText(String.valueOf(evo.getGenNmb()));
    panel.add(genLabelInput);


    // warum up time input 
    c.anchor = GridBagConstraints.NORTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 1;
    layout.setConstraints(warmUpLabel,c);
    panel.add(warmUpLabel);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 1;
    c.anchor = GridBagConstraints.NORTHWEST;
    layout.setConstraints(warmUpLabelInput, c);
    warmUpLabelInput.setText(String.valueOf(evo.getWarmUpSpan()));
    warmUpLabelInput.addActionListener(this);
    panel.add(warmUpLabelInput);

    // life time input 
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.NORTHWEST;
    c.gridx = 0;
    c.gridy = 2;
    layout.setConstraints(testStepsLabel,c);
    panel.add(testStepsLabel);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 2;
    c.anchor = GridBagConstraints.NORTHWEST;
    layout.setConstraints(testStepsLabelInput, c);
    testStepsLabelInput.setText(String.valueOf(evo.getEvalTestSteps()));
    testStepsLabelInput.addActionListener(this);
    panel.add(testStepsLabelInput);

    // update eval. steps for all populations
    PopulationList pops = evo.getPopulations();
    Population pop = null;
    for(pops.start(); pops.hasMore(); pops.next())
    {
      pop = pops.currentPop();
      pop.setWarmUpSpan(this.evo.getWarmUpSpan());
      pop.setEvalTestSteps(this.evo.getEvalTestSteps());
      pop.getCtrlEvaluationPanel().actionPerformed(new ActionEvent(this,70,"UPDATE_EVA_PANEL"));  
    }



    // evaluation method 
    EvalCtrlPanel evalCtrlPanel = new EvalCtrlPanel(this.evo);
    c.fill = GridBagConstraints.BOTH;
    c.gridheight = 3;
    c.gridwidth = 1;
    c.gridx = 2;
    c.gridy = 0;
    layout.setConstraints(evalCtrlPanel.getPanel(),c);
    panel.add(evalCtrlPanel.getPanel());

    // logging data
    LoggingPanel loggingPanel = new LoggingPanel(this.evo);
    c.fill = GridBagConstraints.BOTH;
    c.gridheight = 2;
    c.gridwidth = 2;
    c.gridx = 3;
    c.gridy = 0;
    layout.setConstraints(loggingPanel.getPanel(),c);
    panel.add(loggingPanel.getPanel());

    // task control
    TaskCtrlPanel taskCtrlPanel = new TaskCtrlPanel(this.evo);
    c.fill = GridBagConstraints.BOTH;
    c.gridheight = 1;
    c.gridwidth = 2;
    c.gridx = 3;
    c.gridy = 2;
    layout.setConstraints(taskCtrlPanel.getPanel(),c);
    panel.add(taskCtrlPanel.getPanel());

    // window control 
    WinCtrlPanel winCtrlPanel = new WinCtrlPanel(this.evo);
    c.fill = GridBagConstraints.BOTH;
    c.gridheight = 3;
    c.gridwidth = 1;
    c.gridx = 5;
    c.gridy = 0;
    layout.setConstraints(winCtrlPanel.getPanel(),c);
    panel.add(winCtrlPanel.getPanel());



  }

  public JPanel getPanel(){
    return this.panel;
  }

  public void actionPerformed(ActionEvent e){
    int intVal;


    if(e.getSource() == evo){
      genLabelInput.setText(String.valueOf(evo.getGenNmb()));
    }


    if((e.getSource() == warmUpLabelInput) || (e.getSource() == testStepsLabelInput)){
      intVal = Integer.parseInt(warmUpLabelInput.getText());
      this.evo.setWarmUpSpan(intVal);
      warmUpLabelInput.setText(Integer.toString(this.evo.getWarmUpSpan()));

      intVal = Integer.parseInt(testStepsLabelInput.getText());
      this.evo.setEvalTestSteps(intVal);
      testStepsLabelInput.setText(Integer.toString(this.evo.getEvalTestSteps()));


      PopulationList pops = evo.getPopulations();
      Population pop = null;
      for(pops.start(); pops.hasMore(); pops.next())
      {
        pop = pops.currentPop();
        pop.setWarmUpSpan(this.evo.getWarmUpSpan());
        pop.setEvalTestSteps(this.evo.getEvalTestSteps());
        pop.getCtrlEvaluationPanel().actionPerformed(new ActionEvent(this,70,"UPDATE_EVA_PANEL"));  
      }

      System.out.println("evo-task : set 'warm up time' to " + 
          Integer.toString(this.evo.getWarmUpSpan()) );
      System.out.println("evo-task : set 'test steps' to " + 
          Integer.toString(this.evo.getEvalTestSteps()) );
    }

  }


}













