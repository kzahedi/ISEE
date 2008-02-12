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

import hinton.executive.ProcessParameter;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class LastNetDialog extends JFrame implements ActionListener
{

  private NumberFormat numberFormat = NumberFormat.getInstance(Locale.UK);
  private int bestAge = -1;
  private double bestFitness = Double.NEGATIVE_INFINITY;
  private ProcessParameter processParameter = null;
  private JButton closeButton = new JButton("close");
  private JLabel     lastFitnessValueLabel      = new JLabel("Fitness Value");
  private JLabel     lastLivedCyclesLabel       = new JLabel("Age");
  private JLabel     bestFitnessValueLabel      = new JLabel("Best");
  private JLabel     bestLivedCyclesLabel       = new JLabel("Best");
  private JLabel     triesLabel                 = new JLabel("Tries");
  private JLabel     cyclesLabel                = new JLabel("Cycles");
  private JLabel     warmUpStepsLabel           = new JLabel("WarmUpSteps");
  private JLabel     iterationsLabel            = new JLabel("Iterations");
  private JLabel     initialIterationsLabel     = new JLabel("Initial Iterations");
  private JLabel     constraintsLabel           = new JLabel("Constants 0-3");
  private JLabel     speedFactorLabel           = new JLabel("SpeedFactor");
  private JTextField lastFitnessValueTextField  = new JTextField("last fitness",5);
  private JTextField lastLivedCyclesTextField   = new JTextField("last age",5);
  private JTextField bestFitnessValueTextField  = new JTextField("best fitness",5);
  private JTextField bestLivedCyclesTextField   = new JTextField("best age",5);
  private JTextField triesTextField             = new JTextField("0",5);
  private JTextField cyclesTextField            = new JTextField("0",5);
  private JTextField warmUpStepsTextField       = new JTextField("0",5);
  private JTextField iterationsTextField        = new JTextField("0",5);
  private JTextField initialIterationsTextField = new JTextField("0",5);
  private JTextField constraintsTextField       = new JTextField("0,0,0,0",22);
  private JTextField speedFactorTextField       = new JTextField("0",5);


  public LastNetDialog()
  {
    super("LastNetDialogue");
    JPanel rootPanel = new JPanel();

    numberFormat.setMinimumFractionDigits(4);
    numberFormat.setMaximumFractionDigits(4);

    closeButton.addActionListener(this);
    GridBagLayout gbl = new GridBagLayout();
    

    rootPanel.setLayout(gbl);
    GridBagConstraints lastFitnessValueLabelConstraints = 
      new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
        GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
    GridBagConstraints lastFitnessValueTextFieldConstraints = 
      new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(lastFitnessValueLabel,lastFitnessValueLabelConstraints);
    gbl.setConstraints(lastFitnessValueTextField,lastFitnessValueTextFieldConstraints);

    GridBagConstraints lastLivedCyclesLabelConstraints = 
      new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.EAST,
        GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
    GridBagConstraints lastLivedCyclesTextFieldConstraints = 
      new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(lastLivedCyclesLabel,lastLivedCyclesLabelConstraints);
    gbl.setConstraints(lastLivedCyclesTextField,lastLivedCyclesTextFieldConstraints);

    rootPanel.add(lastLivedCyclesTextField);
    rootPanel.add(lastLivedCyclesLabel);

    rootPanel.add(lastFitnessValueTextField);
    rootPanel.add(lastFitnessValueLabel);

    GridBagConstraints bestFitnessValueLabelConstraints = 
      new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
        GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
    GridBagConstraints bestFitnessValueTextFieldConstraints = 
      new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(bestFitnessValueLabel,bestFitnessValueLabelConstraints);
    gbl.setConstraints(bestFitnessValueTextField,bestFitnessValueTextFieldConstraints);

    rootPanel.add(bestFitnessValueTextField);
    rootPanel.add(bestFitnessValueLabel);

    GridBagConstraints bestLivedCyclesLabelConstraints = 
      new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.EAST,
        GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
    GridBagConstraints bestLivedCyclesTextFieldConstraints = 
      new GridBagConstraints(3, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(bestLivedCyclesLabel,bestLivedCyclesLabelConstraints);
    gbl.setConstraints(bestLivedCyclesTextField,bestLivedCyclesTextFieldConstraints);

    rootPanel.add(bestLivedCyclesTextField);
    rootPanel.add(bestLivedCyclesLabel);

    // **************************************************************************
    // 
    // **************************************************************************


    GridBagConstraints cyclesLabelConstraints = 
      new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.EAST,
        GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
    GridBagConstraints cyclesTextFieldConstraints = 
      new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(cyclesLabel,
        cyclesLabelConstraints);
    gbl.setConstraints(cyclesTextField,
        cyclesTextFieldConstraints);

    rootPanel.add(cyclesLabel);
    rootPanel.add(cyclesTextField);



    GridBagConstraints warmUpStepsLabelConstraints = 
      new GridBagConstraints(2, 2, 1, 1, 0, 0, GridBagConstraints.EAST,
        GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
    GridBagConstraints warmUpStepsTextFieldConstraints = 
      new GridBagConstraints(3, 2, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(warmUpStepsLabel,warmUpStepsLabelConstraints);
    gbl.setConstraints(warmUpStepsTextField,warmUpStepsTextFieldConstraints);

    rootPanel.add(warmUpStepsLabel);
    rootPanel.add(warmUpStepsTextField);

    // **************************************************************************
    // 
    // **************************************************************************

    GridBagConstraints iterationsLabelConstraints = 
      new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.EAST,
        GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
    GridBagConstraints iterationsTextFieldConstraints = 
      new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(iterationsLabel,iterationsLabelConstraints);
    gbl.setConstraints(iterationsTextField,iterationsTextFieldConstraints);

    rootPanel.add(iterationsLabel);
    rootPanel.add(iterationsTextField);

    GridBagConstraints initialIterationsLabelConstraints = 
      new GridBagConstraints(2, 3, 1, 1, 0, 0, GridBagConstraints.EAST,
        GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
    GridBagConstraints initialIterationsTextFieldConstraints = 
      new GridBagConstraints(3, 3, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(initialIterationsLabel,
        initialIterationsLabelConstraints);
    gbl.setConstraints(initialIterationsTextField,
        initialIterationsTextFieldConstraints);

    rootPanel.add(initialIterationsLabel);
    rootPanel.add(initialIterationsTextField);

    // **************************************************************************
    // 
    // **************************************************************************

    GridBagConstraints triesLabelConstraints = 
      new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.EAST,
        GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
    GridBagConstraints triesTextFieldConstraints = 
      new GridBagConstraints(1, 4, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(triesLabel,triesLabelConstraints);
    gbl.setConstraints(triesTextField,triesTextFieldConstraints);

    rootPanel.add(triesLabel);
    rootPanel.add(triesTextField);

    GridBagConstraints speedFactorLabelConstraints = 
      new GridBagConstraints(2, 4, 1, 1, 0, 0, GridBagConstraints.EAST,
        GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
    GridBagConstraints speedFactorTextFieldConstraints = 
      new GridBagConstraints(3, 4, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(speedFactorLabel,speedFactorLabelConstraints);
    gbl.setConstraints(speedFactorTextField,speedFactorTextFieldConstraints);

    rootPanel.add(speedFactorLabel);
    rootPanel.add(speedFactorTextField);


    // **************************************************************************
    // 
    // **************************************************************************

    GridBagConstraints constraintsLabelConstraints = 
      new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.EAST,
        GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
    GridBagConstraints constraintsTextFieldConstraints = 
      new GridBagConstraints(1, 5, 3, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(constraintsLabel,constraintsLabelConstraints);
    gbl.setConstraints(constraintsTextField,constraintsTextFieldConstraints);

    rootPanel.add(constraintsLabel);
    rootPanel.add(constraintsTextField);

    lastFitnessValueTextField.setEditable(false);
    lastLivedCyclesTextField.setEditable(false);
    bestFitnessValueTextField.setEditable(false);
    bestLivedCyclesTextField.setEditable(false);
    triesTextField.setEditable(false);
    speedFactorTextField.setEditable(false);
    cyclesTextField.setEditable(false);
    warmUpStepsTextField.setEditable(false);
    iterationsTextField.setEditable(false);
    initialIterationsTextField.setEditable(false);
    constraintsTextField.setEditable(false);

    getContentPane().add(rootPanel);
    pack();
    setResizable(false);

  }

  public void setProcessParameter(ProcessParameter processParameter)
  {
    this.processParameter = processParameter;
  }

  public void update(double fitnessValue, int age)
  {
    setAge(age);
    setFitnessValue(fitnessValue);
    updateProcessParameter();
  }


  private void setAge(int age)
  {
    lastLivedCyclesTextField.setText(""+age);
    if(age > bestAge)
    {
      bestAge = age;
      bestLivedCyclesTextField.setText(""+bestAge);
    }
  }

  private void setFitnessValue(double fitnessValue)
  {
    lastFitnessValueTextField.setText(numberFormat.format(fitnessValue));
    if(fitnessValue > bestFitness)
    {
      bestFitness = fitnessValue;
      bestFitnessValueTextField.setText(""+numberFormat.format(bestFitness));
    }
  }

  private void updateProcessParameter()
  {
    triesTextField.setText("" + processParameter.tries());
    cyclesTextField.setText("" + processParameter.cycles());
    warmUpStepsTextField.setText("" + processParameter.warmUpSteps());
    iterationsTextField.setText("" + processParameter.iterations());
    initialIterationsTextField.setText("" +
        processParameter.getInitialIterations());
    constraintsTextField.setText(
        ""   + processParameter.getConstant(0) +
        ", " + processParameter.getConstant(1) +
        ", " + processParameter.getConstant(2) +
        ", " + processParameter.getConstant(3));
    speedFactorTextField.setText( "" + processParameter.getMaxSpeed());
  }


  public void actionPerformed(ActionEvent e)
  {
    if(e.getSource() == closeButton)
    {
      setVisible(false);
      return;
    }
  }

  public void resetPanel()
  {
    bestAge     = -1;
    bestFitness = Double.NEGATIVE_INFINITY;
  }
  
  public static void main(String argv[])
  {
    LastNetDialog pd = new LastNetDialog();
    pd.setVisible(true);
    pd.setAge(1000);
  }
}
