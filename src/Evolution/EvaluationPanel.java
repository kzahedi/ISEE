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


public class EvaluationPanel implements ActionListener{
    private JPanel panel = new JPanel(false);
    private Population pop = null;

    private JLabel     costNeuLab       = new JLabel(" cost neurons  : ");
    private JTextField costNeuLabInp    = new JTextField(6);
    private JLabel     costSynLab       = new JLabel(" cost synapsis : ");
    private JTextField costSynLabInp    = new JTextField(6);
    private JLabel     c0Lab            = new JLabel(" C0 : ");
    private JTextField c0LabInp         = new JTextField(6);
    private JLabel     c1Lab            = new JLabel(" C1 : ");
    private JTextField c1LabInp         = new JTextField(6);
    private JLabel     c2Lab            = new JLabel(" C2 : ");
    private JTextField c2LabInp         = new JTextField(6);
    private JLabel     c3Lab            = new JLabel(" C3 : ");
    private JTextField c3LabInp         = new JTextField(6);
    private JLabel     portLabel        = new JLabel(" port :");
    private JTextField portLabelInput   = new JTextField(6);
    private JLabel     lifeLabel        = new JLabel("life span");
    private JTextField lifeLabelInput   = new JTextField(6);
    

    public EvaluationPanel(Population population){
  this.pop   = population;

  this.pop.setCtrlEvaluationPanel(this);
  
  GridBagLayout      layout = new GridBagLayout();
  GridBagConstraints c      = new GridBagConstraints();
  this.panel.setLayout(layout);


  this.panel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createTitledBorder("evaluation, costs, and auxiliary constants"),
      BorderFactory.createEmptyBorder(5,5,5,5)));

  /* costs */
  c.fill = GridBagConstraints.BOTH;
  c.gridx = 0;
  c.gridy = 0;
  layout.setConstraints(costNeuLab,c);
  this.panel.add(costNeuLab);

  c.fill = GridBagConstraints.BOTH;
  c.gridx = 1;
  c.gridy = 0;
  costNeuLabInp.addActionListener(this);
  costNeuLabInp.setText(String.valueOf(pop.getCostNeu()));
  costNeuLabInp.addActionListener(this);
  layout.setConstraints(costNeuLabInp,c);
  this.panel.add(costNeuLabInp);


  c.fill = GridBagConstraints.BOTH;
  c.gridx = 0;
  c.gridy = 1;
  layout.setConstraints(costSynLab,c);
  this.panel.add(costSynLab);

  c.fill = GridBagConstraints.BOTH;
  c.gridx = 1;
  c.gridy = 1;
  costSynLabInp.addActionListener(this);
  costSynLabInp.setText(String.valueOf(pop.getCostSyn()));
  layout.setConstraints(costSynLabInp,c);
  this.panel.add(costSynLabInp);
  
  /* constants */
  c.fill = GridBagConstraints.BOTH;
  c.gridx = 2;
  c.gridy = 0;
  layout.setConstraints(c0Lab,c);
  this.panel.add(c0Lab);

  c.fill = GridBagConstraints.BOTH;
  c.gridx = 3;
  c.gridy = 0;
  c0LabInp.addActionListener(this);
  c0LabInp.setText(String.valueOf(pop.getC0()));
  layout.setConstraints(c0LabInp,c);
  this.panel.add(c0LabInp);

  /* c1 */
  c.fill = GridBagConstraints.BOTH;
  c.gridx = 4;
  c.gridy = 0;
  layout.setConstraints(c1Lab,c);
  this.panel.add(c1Lab);

  c.fill = GridBagConstraints.BOTH;
  c.gridx = 5;
  c.gridy = 0;
  c1LabInp.addActionListener(this);
  c1LabInp.setText(String.valueOf(pop.getC1()));
  layout.setConstraints(c1LabInp,c);
  this.panel.add(c1LabInp);

  /* c2 */
  c.fill = GridBagConstraints.BOTH;
  c.gridx = 2;
  c.gridy = 1;
  layout.setConstraints(c2Lab,c);
  this.panel.add(c2Lab);

  c.fill = GridBagConstraints.BOTH;
  c.gridx = 3;
  c.gridy = 1;
  c2LabInp.addActionListener(this);
  c2LabInp.setText(String.valueOf(pop.getC2()));
  layout.setConstraints(c2LabInp,c);
  this.panel.add(c2LabInp);

  /* c3 */
  c.fill = GridBagConstraints.BOTH;
  c.gridx = 4;
  c.gridy = 1;
  layout.setConstraints(c3Lab,c);
  this.panel.add(c3Lab);

  c.fill = GridBagConstraints.BOTH;
  c.gridx = 5;
  c.gridy = 1;
  c3LabInp.addActionListener(this);
  c3LabInp.setText(String.valueOf(pop.getC3()));
  layout.setConstraints(c3LabInp,c);
  this.panel.add(c3LabInp);

  /* port input */
  c.fill = GridBagConstraints.BOTH;
  c.gridx = 6;
  c.gridy = 0;
  layout.setConstraints(portLabel, c);
  panel.add(portLabel);

  c.fill = GridBagConstraints.BOTH;
  c.gridx = 7;
  c.gridy = 0;
  layout.setConstraints(portLabelInput, c);
  portLabelInput.setText(String.valueOf(this.pop.getPortNumb()));
  portLabelInput.addActionListener(this);
  panel.add(portLabelInput);

  /* life time input */
  c.fill = GridBagConstraints.BOTH;
  c.gridx = 6;
  c.gridy = 1;
  layout.setConstraints(lifeLabel,c);
  panel.add(lifeLabel);

  c.fill = GridBagConstraints.BOTH;
  c.gridx = 7;
  c.gridy = 1;
  layout.setConstraints(lifeLabelInput, c);
  lifeLabelInput.setText(String.valueOf(this.pop.getWarmUpSpan() + this.pop.getEvalTestSteps()));
  lifeLabelInput.addActionListener(this);
  lifeLabelInput.setEditable(false);
  panel.add(lifeLabelInput);
    } 
    


    public JPanel getPanel(){
  return this.panel;
    }
    
    
    public void actionPerformed(ActionEvent e){
  double dblVal;
  int    intVal;

  // this action command comes from EvoCtrlPanel, when life span is changed
  if(e.getActionCommand() == "UPDATE_EVA_PANEL")
  {
      lifeLabelInput.setText(String.valueOf(this.pop.getWarmUpSpan() + this.pop.getEvalTestSteps()));

      System.out.println(this.pop.getWarmUpSpan() + " + " + this.pop.getEvalTestSteps());
  }

  
  if((e.getSource() == this.pop) && (e.getActionCommand() == "INIT"))
  {
      portLabelInput.setText(Integer.toString(this.pop.getPortNumb()));
      portLabelInput.setEditable(false);
  }

  if((e.getSource() == this.pop) && (e.getActionCommand() == "PORT_CHANGED"))
  {
      portLabelInput.setText(Integer.toString(this.pop.getPortNumb()));
  }


  if((e.getSource() == this.pop) && (e.getActionCommand() == "RESET"))
  {
      portLabelInput.setEditable(true);
  }

  if(e.getSource() ==  costNeuLabInp)
  {
      dblVal = Double.parseDouble(costNeuLabInp.getText());
      this.pop.setCostNeu(dblVal);
      costNeuLabInp.setText(Double.toString(this.pop.getCostNeu()));

      System.out.println(pop.getName() + 
             " : set 'cost neu.' to " + 
             Double.toString(this.pop.getCostNeu()) );
  }

  if(e.getSource() ==  costSynLabInp)
  {
      dblVal = Double.parseDouble(costSynLabInp.getText());
      this.pop.setCostSyn(dblVal);
      costSynLabInp.setText(Double.toString(this.pop.getCostSyn()));

      System.out.println(pop.getName() + 
             " : set 'cost syn.' to " + 
             Double.toString(this.pop.getCostSyn()) );
  }

  if(e.getSource() ==  c0LabInp)
  {
      dblVal = Double.parseDouble(c0LabInp.getText());
      this.pop.setC0(dblVal);
      c0LabInp.setText(Double.toString(this.pop.getC0()));

      System.out.println(pop.getName() + 
             " : set 'c0' to " + 
             Double.toString(this.pop.getC0()) );
  }

  if(e.getSource() ==  c1LabInp)
  {
      dblVal = Double.parseDouble(c1LabInp.getText());
      this.pop.setC1(dblVal);
      c1LabInp.setText(Double.toString(this.pop.getC1()));

      System.out.println(pop.getName() + 
             " : set 'c1' to " + 
             Double.toString(this.pop.getC1()) );
  }


  if(e.getSource() ==  c2LabInp)
  {
      dblVal = Double.parseDouble(c2LabInp.getText());
      this.pop.setC2(dblVal);
      c2LabInp.setText(Double.toString(this.pop.getC2()));

      System.out.println(pop.getName() + 
             " : set 'c2' to " + 
             Double.toString(this.pop.getC2()) );
  }


  if(e.getSource() ==  c3LabInp)
  {
      dblVal = Double.parseDouble(c3LabInp.getText());
      this.pop.setC3(dblVal);
      c3LabInp.setText(Double.toString(this.pop.getC3()));

      System.out.println(pop.getName() + 
             " : set 'c3' to " + 
             Double.toString(this.pop.getC3()) );
  }

  if(e.getSource() == portLabelInput ){
      intVal = Integer.parseInt(portLabelInput.getText());
      this.pop.setPortNumb(intVal);
      portLabelInput.setText(Integer.toString(this.pop.getPortNumb()));
      
      System.out.println("evo-task : set 'port' to " + 
             Integer.toString(this.pop.getPortNumb()) );
  }

  if((e.getSource() == this.pop) && (e.getActionCommand() == "REFRESH"))
  {
      portLabelInput.setText(Integer.toString(this.pop.getPortNumb()));
      c3LabInp.setText(Double.toString(this.pop.getC3()));
      c2LabInp.setText(Double.toString(this.pop.getC2()));
      c1LabInp.setText(Double.toString(this.pop.getC1()));
      c0LabInp.setText(Double.toString(this.pop.getC0()));
      costNeuLabInp.setText(Double.toString(this.pop.getCostNeu()));
      costSynLabInp.setText(Double.toString(this.pop.getCostSyn()));
  }

    }
    
}












