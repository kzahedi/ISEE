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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;




public class LearnParameterDialog extends JFrame implements DocumentListener,
       ActionListener, ItemListener{
  
  private JPanel rootPanel = new JPanel();
  private Population pop = null;
  
  
  /* meta learning parameter for homeokinese approach */

  private JLabel    alphaProbabilityLabel = new JLabel("Probability");
  private JLabel    alphaVariationLabel  = new JLabel("Variation");
  private JLabel    alphaInitialLabel   = new JLabel("Initial");
  private JLabel    alphaMinLabel       = new JLabel("Min");
  private JLabel    alphaMaxLabel       = new JLabel("Max");

  private JTextField alphaProbabilityTextField = new JTextField("0.0",5);
  private JTextField alphaVariationTextField  = new JTextField("0.0",5);
  private JTextField alphaInitialTextField   = new JTextField("1.0",5);
  private JTextField alphaMinTextField       = new JTextField("0.0",5);
  private JTextField alphaMaxTextField       = new JTextField("1.0",5);

  private JLabel    betaProbabilityLabel = new JLabel("Probability");
  private JLabel    betaVariationLabel  = new JLabel("Variation");
  private JLabel    betaInitialLabel   = new JLabel("Initial");
  private JLabel    betaMinLabel       = new JLabel("Min");
  private JLabel    betaMaxLabel       = new JLabel("Max");

  private JTextField betaProbabilityTextField = new JTextField("0.0",5);
  private JTextField betaVariationTextField  = new JTextField("0.00",5);
  private JTextField betaInitialTextField   = new JTextField("0.01",5);
  private JTextField betaMinTextField       = new JTextField("0.0",5);
  private JTextField betaMaxTextField       = new JTextField("0.2",5);

  private JLabel    gammaProbabilityLabel = new JLabel("Probability");
  private JLabel    gammaVariationLabel  = new JLabel("Variation");
  private JLabel    gammaInitialLabel   = new JLabel("Initial");
  private JLabel    gammaMinLabel       = new JLabel("Min");
  private JLabel    gammaMaxLabel       = new JLabel("Max");

  private JTextField gammaProbabilityTextField = new JTextField("0.0",5);
  private JTextField gammaVariationTextField  = new JTextField("0.00",5);
  private JTextField gammaInitialTextField   = new JTextField("0.01",5);
  private JTextField gammaMinTextField       = new JTextField("0.0",5);
  private JTextField gammaMaxTextField       = new JTextField("0.2",5);

  private JLabel    deltaProbabilityLabel = new JLabel("Probability");
  private JLabel    deltaVariationLabel  = new JLabel("Variation");
  private JLabel    deltaInitialLabel   = new JLabel("Initial");
  private JLabel    deltaMinLabel       = new JLabel("Min");
  private JLabel    deltaMaxLabel       = new JLabel("Max");

  private JTextField deltaProbabilityTextField = new JTextField("0.0",5);
  private JTextField deltaVariationTextField  = new JTextField("0.00",5);
  private JTextField deltaInitialTextField   = new JTextField("0.02",5);
  private JTextField deltaMinTextField       = new JTextField("0.0",5);
  private JTextField deltaMaxTextField       = new JTextField("0.2",5);



  private JLabel    parameterLabel    = new JLabel("parameter mode:");
  private JComboBox parameterComboBox = new JComboBox();

  private boolean visible = false;


  
  public LearnParameterDialog(Population population){

    super(population.getName() + ": stochastic learn parameter probabilities");

    this.pop   = population;
   
    setPanel(rootPanel);
    getContentPane().add(rootPanel);
    setSize(680,230);
    pack();

    /* give the population this control panel for net type and structure */
    this.pop.setCtrlLearnPanel(this);
    pop.updatePanel();
  }


  private void setPanel(JPanel panel){
    
    GridBagLayout      layout = new GridBagLayout();
    GridBagLayout      layoutAlpha = new GridBagLayout();
    GridBagLayout      layoutBeta = new GridBagLayout();
    GridBagLayout      layoutGamma = new GridBagLayout();
    GridBagLayout      layoutDelta = new GridBagLayout();
    GridBagConstraints c      = new GridBagConstraints();
    panel.setLayout(new GridLayout(3,2));
    
    
    panel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder("stochastic learn parameter probabilities"),
          BorderFactory.createEmptyBorder(5,5,5,5)));

    JPanel alphaPanel = new JPanel();
    alphaPanel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder("alpha"),
          BorderFactory.createEmptyBorder(5,5,5,5)));

    JPanel betaPanel = new JPanel();
    betaPanel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder("beta"),
          BorderFactory.createEmptyBorder(5,5,5,5)));

    JPanel gammaPanel = new JPanel();
    gammaPanel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder("gamma"),
          BorderFactory.createEmptyBorder(5,5,5,5)));

    JPanel deltaPanel = new JPanel();
    deltaPanel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder("delta"),
          BorderFactory.createEmptyBorder(5,5,5,5)));


    JPanel parameterPanel = new JPanel();
    parameterPanel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder("parameter mode"),
          BorderFactory.createEmptyBorder(5,5,5,5)));



    panel.add(alphaPanel);
    panel.add(betaPanel);
    panel.add(gammaPanel);
    panel.add(deltaPanel);
    panel.add(parameterPanel);
    alphaPanel.setLayout(layoutAlpha);
    betaPanel.setLayout(layoutBeta);
    gammaPanel.setLayout(layoutGamma);
    deltaPanel.setLayout(layoutDelta);
    parameterPanel.setLayout(layout);

    int row = 0;
    // **************************************************************************
    // alpha
    // **************************************************************************
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = row;
    layoutAlpha.setConstraints(alphaProbabilityLabel,c);
    alphaPanel.add(alphaProbabilityLabel,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = row++;
    layoutAlpha.setConstraints(alphaProbabilityTextField,c);
    alphaPanel.add(alphaProbabilityTextField,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = row;
    layoutAlpha.setConstraints(alphaVariationLabel,c);
    alphaPanel.add(alphaVariationLabel,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = row++;
    layoutAlpha.setConstraints(alphaVariationTextField,c);
    alphaPanel.add(alphaVariationTextField,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = row;
    layoutAlpha.setConstraints(alphaInitialLabel,c);
    alphaPanel.add(alphaInitialLabel,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = row++;
    layoutAlpha.setConstraints(alphaInitialTextField,c);
    alphaPanel.add(alphaInitialTextField,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = row;
    layoutAlpha.setConstraints(alphaMinLabel,c);
    alphaPanel.add(alphaMinLabel,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = row++;
    layoutAlpha.setConstraints(alphaMinTextField,c);
    alphaPanel.add(alphaMinTextField,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = row;
    layoutAlpha.setConstraints(alphaMaxLabel,c);
    alphaPanel.add(alphaMaxLabel,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = row++;
    layoutAlpha.setConstraints(alphaMaxTextField,c);
    alphaPanel.add(alphaMaxTextField,c);

    // **************************************************************************
    // beta
    // **************************************************************************
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = row;
    layoutAlpha.setConstraints(betaProbabilityLabel,c);
    betaPanel.add(betaProbabilityLabel,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = row++;
    layoutAlpha.setConstraints(betaProbabilityTextField,c);
    betaPanel.add(betaProbabilityTextField,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = row;
    layoutAlpha.setConstraints(betaVariationLabel,c);
    betaPanel.add(betaVariationLabel,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = row++;
    layoutAlpha.setConstraints(betaVariationTextField,c);
    betaPanel.add(betaVariationTextField,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = row;
    layoutAlpha.setConstraints(betaInitialLabel,c);
    betaPanel.add(betaInitialLabel,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = row++;
    layoutAlpha.setConstraints(betaInitialTextField,c);
    betaPanel.add(betaInitialTextField,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = row;
    layoutAlpha.setConstraints(betaMinLabel,c);
    betaPanel.add(betaMinLabel,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = row++;
    layoutAlpha.setConstraints(betaMinTextField,c);
    betaPanel.add(betaMinTextField,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = row;
    layoutAlpha.setConstraints(betaMaxLabel,c);
    betaPanel.add(betaMaxLabel,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = row++;
    layoutAlpha.setConstraints(betaMaxTextField,c);
    betaPanel.add(betaMaxTextField,c);
    // **************************************************************************
    // gamma
    // **************************************************************************
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = row;
    layoutAlpha.setConstraints(gammaProbabilityLabel,c);
    gammaPanel.add(gammaProbabilityLabel,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = row++;
    layoutAlpha.setConstraints(gammaProbabilityTextField,c);
    gammaPanel.add(gammaProbabilityTextField,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = row;
    layoutAlpha.setConstraints(gammaVariationLabel,c);
    gammaPanel.add(gammaVariationLabel,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = row++;
    layoutAlpha.setConstraints(gammaVariationTextField,c);
    gammaPanel.add(gammaVariationTextField,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = row;
    layoutAlpha.setConstraints(gammaInitialLabel,c);
    gammaPanel.add(gammaInitialLabel,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = row++;
    layoutAlpha.setConstraints(gammaInitialTextField,c);
    gammaPanel.add(gammaInitialTextField,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = row;
    layoutAlpha.setConstraints(gammaMinLabel,c);
    gammaPanel.add(gammaMinLabel,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = row++;
    layoutAlpha.setConstraints(gammaMinTextField,c);
    gammaPanel.add(gammaMinTextField,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = row;
    layoutAlpha.setConstraints(gammaMaxLabel,c);
    gammaPanel.add(gammaMaxLabel,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = row++;
    layoutAlpha.setConstraints(gammaMaxTextField,c);
    gammaPanel.add(gammaMaxTextField,c);
    // **************************************************************************
    // delta
    // **************************************************************************
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = row;
    layoutAlpha.setConstraints(deltaProbabilityLabel,c);
    deltaPanel.add(deltaProbabilityLabel,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = row++;
    layoutAlpha.setConstraints(deltaProbabilityTextField,c);
    deltaPanel.add(deltaProbabilityTextField,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = row;
    layoutAlpha.setConstraints(deltaVariationLabel,c);
    deltaPanel.add(deltaVariationLabel,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = row++;
    layoutAlpha.setConstraints(deltaVariationTextField,c);
    deltaPanel.add(deltaVariationTextField,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = row;
    layoutAlpha.setConstraints(deltaInitialLabel,c);
    deltaPanel.add(deltaInitialLabel,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = row++;
    layoutAlpha.setConstraints(deltaInitialTextField,c);
    deltaPanel.add(deltaInitialTextField,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = row;
    layoutAlpha.setConstraints(deltaMinLabel,c);
    deltaPanel.add(deltaMinLabel,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = row++;
    layoutAlpha.setConstraints(deltaMinTextField,c);
    deltaPanel.add(deltaMinTextField,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = row;
    layoutAlpha.setConstraints(deltaMaxLabel,c);
    deltaPanel.add(deltaMaxLabel,c);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = row++;
    layoutAlpha.setConstraints(deltaMaxTextField,c);
    deltaPanel.add(deltaMaxTextField,c);


    /* synapse probabilities */
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = 0;
    layout.setConstraints(parameterLabel,c);
    parameterPanel.add(parameterLabel);


    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = 0;
    layout.setConstraints(parameterComboBox,c);
    parameterPanel.add(parameterComboBox);

    for(int i=0; i < EvoObject.PARAMETER_MODES.length; i++)
    {
      parameterComboBox.addItem(EvoObject.PARAMETER_MODES[i]);
    }

    parameterComboBox.addItemListener(this);

    alphaProbabilityTextField.getDocument().addDocumentListener(this);
    alphaVariationTextField.getDocument().addDocumentListener(this);
    alphaInitialTextField.getDocument().addDocumentListener(this);
    alphaMinTextField.getDocument().addDocumentListener(this);
    alphaMaxTextField.getDocument().addDocumentListener(this);


    betaProbabilityTextField.getDocument().addDocumentListener(this);
    betaVariationTextField.getDocument().addDocumentListener(this);
    betaInitialTextField.getDocument().addDocumentListener(this);
    betaMinTextField.getDocument().addDocumentListener(this);
    betaMaxTextField.getDocument().addDocumentListener(this);


    gammaProbabilityTextField.getDocument().addDocumentListener(this);
    gammaVariationTextField.getDocument().addDocumentListener(this);
    gammaInitialTextField.getDocument().addDocumentListener(this);
    gammaMinTextField.getDocument().addDocumentListener(this);
    gammaMaxTextField.getDocument().addDocumentListener(this);


    deltaProbabilityTextField.getDocument().addDocumentListener(this);
    deltaVariationTextField.getDocument().addDocumentListener(this);
    deltaInitialTextField.getDocument().addDocumentListener(this);
    deltaMinTextField.getDocument().addDocumentListener(this);
    deltaMaxTextField.getDocument().addDocumentListener(this);



  }

  
  public void actionPerformed(ActionEvent e)
  {
    if((e.getSource() == pop) && (e.getActionCommand() == "REFRESH")){
    }
  }
 
  public JPanel getPanel(){
    return this.rootPanel;
  }

  public void switchVisible(){
    this.visible = !this.isVisible();
    this.setVisible(this.visible);
  }
  
  
  public void itemStateChanged(ItemEvent event)
  {
    if (event.getSource() == parameterComboBox)
    {
      this.pop.setParameterMode(parameterComboBox.getSelectedIndex());
      System.out.println("Set Parameter Combo Box to: " +
          EvoObject.PARAMETER_MODES[parameterComboBox.getSelectedIndex()]);
    }
  }

  public void insertUpdate(DocumentEvent event)
  {
    process(event);
  }

  public void changedUpdate(DocumentEvent event)
  {
  }

  public void removeUpdate(DocumentEvent event)
  {
    process(event);
  }

  public void updatePanel(
    double alphaMax,
    double alphaMin,
    double alphaInitial,
    double alphaProbability,
    double alphaVariation,
    double betaMax,
    double betaMin,
    double betaInitial,
    double betaProbability,
    double betaVariation,
    double gammaMax,
    double gammaMin,
    double gammaInitial,
    double gammaProbability,
    double gammaVariation,
    double deltaMax,
    double deltaMin,
    double deltaInitial,
    double deltaProbability,
    double deltaVariation)
  {
    alphaMaxTextField.setText("" + alphaMax);
    alphaMinTextField.setText("" + alphaMin);
    alphaInitialTextField.setText("" + alphaInitial);
    alphaProbabilityTextField.setText("" + alphaProbability);
    alphaVariationTextField.setText("" + alphaVariation);
    betaMaxTextField.setText("" + betaMax);
    betaMinTextField.setText("" + betaMin);
    betaInitialTextField.setText("" + betaInitial);
    betaProbabilityTextField.setText("" + betaProbability);
    betaVariationTextField.setText("" + betaVariation);
    gammaMaxTextField.setText("" + gammaMax);
    gammaMinTextField.setText("" + gammaMin);
    gammaInitialTextField.setText("" + gammaInitial);
    gammaProbabilityTextField.setText("" + gammaProbability);
    gammaVariationTextField.setText("" + gammaVariation);
    deltaMaxTextField.setText("" + deltaMax);
    deltaMinTextField.setText("" + deltaMin);
    deltaInitialTextField.setText("" + deltaInitial);
    deltaProbabilityTextField.setText("" + deltaProbability);
    deltaVariationTextField.setText("" + deltaVariation);
  }


  private void process(DocumentEvent event)
  {
    if(event.getDocument().getLength() > 0)
    {
      try
      {
      pop.setLearningParameter(
          (new Double(alphaMaxTextField.getText())).doubleValue(),
          (new Double(alphaMinTextField.getText())).doubleValue(),
          (new Double(alphaInitialTextField.getText())).doubleValue(),
          (new Double(alphaProbabilityTextField.getText())).doubleValue(),
          (new Double(alphaVariationTextField.getText())).doubleValue(),
          (new Double(betaMaxTextField.getText())).doubleValue(),
          (new Double(betaMinTextField.getText())).doubleValue(),
          (new Double(betaInitialTextField.getText())).doubleValue(),
          (new Double(betaProbabilityTextField.getText())).doubleValue(),
          (new Double(betaVariationTextField.getText())).doubleValue(),
          (new Double(gammaMaxTextField.getText())).doubleValue(),
          (new Double(gammaMinTextField.getText())).doubleValue(),
          (new Double(gammaInitialTextField.getText())).doubleValue(),
          (new Double(gammaProbabilityTextField.getText())).doubleValue(),
          (new Double(gammaVariationTextField.getText())).doubleValue(),
          (new Double(deltaMaxTextField.getText())).doubleValue(),
          (new Double(deltaMinTextField.getText())).doubleValue(),
          (new Double(deltaInitialTextField.getText())).doubleValue(),
          (new Double(deltaProbabilityTextField.getText())).doubleValue(),
          (new Double(deltaVariationTextField.getText())).doubleValue());
      }
      catch(NumberFormatException nfe)
      {
        System.out.println("not valid number");
      }
    }

  }


 

}






