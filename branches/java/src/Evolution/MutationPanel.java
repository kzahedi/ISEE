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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cholsey.SynapseMode;


public class MutationPanel implements ActionListener, ItemListener{
  private JPanel panel = new JPanel();
  private Population pop = null;

  /* combinational variation probabilities */
  private JLabel     insNeuLab    = new JLabel(" insert neuron   : ");
  private JTextField insNeuLabInp = new JTextField(6);
  private JLabel     delNeuLab    = new JLabel(" delete neuron   : ");
  private JTextField delNeuLabInp = new JTextField(6);
  private JLabel     maxNeuLab    = new JLabel(" max nmb. neurons : ");
  private JTextField maxNeuLabInp = new JTextField(6);

  private JLabel     insSynLab    = new JLabel(" insert synapse : ");
  private JTextField insSynLabInp = new JTextField(6);
  private JLabel     delSynLab    = new JLabel(" delete synapse : ");
  private JTextField delSynLabInp = new JTextField(6);
  private JLabel     conLab       = new JLabel(" connectivity           : ");
  private JTextField conLabInp    = new JTextField(6);

  private JLabel    insSynLabel     = new JLabel("syn. ins. mode:");
  private JComboBox insSynComboBox = new JComboBox();

  /* real vale variation probabilities */
  private JLabel     chgBiasLab    = new JLabel(  " change bias     : ");
  private JTextField chgBiasLabInp = new JTextField(6);
  private JLabel     strBiasLab    = new JLabel(  " delta bias     : ");
  private JTextField strBiasLabInp = new JTextField(6);
  private JLabel     limBiasLab    = new JLabel(  " absolute limit bias     : ");
  private JTextField limBiasLabInp = new JTextField(6);

  private JLabel     chgWeightLab    = new JLabel(" change weight : ");
  private JTextField chgWeightLabInp = new JTextField(6);
  private JLabel     strWeightLab    = new JLabel(" delta weight : ");
  private JTextField strWeightLabInp = new JTextField(6);
  private JLabel     limWeightLab    = new JLabel(  " absolute limit weight : ");
  private JTextField limWeightLabInp = new JTextField(6);

  private JLabel     chgDecayLab    = new JLabel( " change decay   : ");
  private JTextField chgDecayLabInp = new JTextField(6);
  private JLabel     strDecayLab    = new JLabel( " delta decay   : ");
  private JTextField strDecayLabInp = new JTextField(6);
  private JLabel     limDecayLab    = new JLabel(  " absolute limit decay   : ");
  private JTextField limDecayLabInp = new JTextField(6);


  // learn parameter dialog + button 
  LearnParameterDialog learnDialog = null;    
  private JButton learnParamButton = new JButton("learn param.");



  public MutationPanel(Population population){
    this.pop   = population;
    learnDialog = new LearnParameterDialog(this.pop);
    this.pop.setCtrlLearnPanel(learnDialog);

    /* give the population this control panel for net type and structure */
    this.pop.setCtrlMutationPanel(this);


    GridBagLayout      layout = new GridBagLayout();
    GridBagConstraints c      = new GridBagConstraints();
    this.panel.setLayout(layout);

    this.panel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder("neuron type and initial structure"),
          BorderFactory.createEmptyBorder(5,5,5,5)));


    JPanel combinationalPanel = new JPanel(false);
    JPanel real_valuePanel = new JPanel(false);



    this.panel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder("stochastic variation probabilities"),
          BorderFactory.createEmptyBorder(5,5,5,5)));


    c.fill = GridBagConstraints.BOTH;
    c.weightx = 0.5;
    c.weighty = 0.5;
    c.gridx = 0;
    c.gridy = 0;
    layout.setConstraints(combinationalPanel,c);
    combinationalPanel = setupCombinationalPanel(combinationalPanel, pop);
    this.panel.add(combinationalPanel);

    c.fill = GridBagConstraints.BOTH;
    c.weightx = 0.5;
    c.weighty = 0.5;
    c.gridx = 0;
    c.gridy = 1;
    layout.setConstraints(real_valuePanel,c);
    real_valuePanel    = setupReal_valuePanel(real_valuePanel, pop);
    this.panel.add(real_valuePanel);

    // add learn panel
    //c.fill = GridBagConstraints.BOTH;
    //c.weightx = 0.5;
    //c.weighty = 0.5;
    //c.gridx = 0;
    //c.gridy = 2;
    //layout.setConstraints(learnParamPanel,c);
    //this.panel.add(learnParamPanel);

    // add learn parameter panel button
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = 2;
    learnParamButton.addActionListener(this);
    layout.setConstraints(learnParamButton,c);
    this.panel.add(learnParamButton);



  }


  private JPanel setupCombinationalPanel(JPanel panel, Population pop){
    GridBagLayout      layout = new GridBagLayout();
    GridBagConstraints c      = new GridBagConstraints();
    panel.setLayout(layout);

    panel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder("combinational mutation"),
          BorderFactory.createEmptyBorder(5,5,5,5)));

    /* neuron probabilities */
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = 0;
    layout.setConstraints(insNeuLab,c);
    panel.add(insNeuLab);

    insNeuLabInp.addActionListener(this);
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = 0;
    insNeuLabInp.setText(String.valueOf(pop.getInsNeuProb()));
    layout.setConstraints(insNeuLabInp,c);
    panel.add(insNeuLabInp);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 2;
    c.gridy = 0;
    layout.setConstraints(delNeuLab,c);
    panel.add(delNeuLab);

    delNeuLabInp.addActionListener(this);
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 3;
    c.gridy = 0;
    layout.setConstraints(delNeuLabInp,c);
    delNeuLabInp.setText(String.valueOf(pop.getDelNeuProb()));
    panel.add(delNeuLabInp);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 4;
    c.gridy = 0;
    layout.setConstraints(maxNeuLab,c);
    panel.add(maxNeuLab);

    maxNeuLabInp.addActionListener(this);
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 5;
    c.gridy = 0;
    layout.setConstraints(maxNeuLabInp,c);
    maxNeuLabInp.setText(String.valueOf(pop.getMaxHidNeuNmb()));
    panel.add(maxNeuLabInp);



    /* synapse probabilities */
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = 1;
    layout.setConstraints(insSynLab,c);
    panel.add(insSynLab);

    insSynLabInp.addActionListener(this);
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = 1;
    insSynLabInp.setText(String.valueOf(pop.getInsSynProb()));
    layout.setConstraints(insSynLabInp,c);
    panel.add(insSynLabInp);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 2;
    c.gridy = 1;
    layout.setConstraints(delSynLab,c);
    panel.add(delSynLab);

    delSynLabInp.addActionListener(this);
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 3;
    c.gridy = 1;
    layout.setConstraints(delSynLabInp,c);
    delSynLabInp.setText(String.valueOf(pop.getDelSynProb()));
    panel.add(delSynLabInp);

    /* connectivity */
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 4;
    c.gridy = 1;
    layout.setConstraints(conLab,c);
    panel.add(conLab);

    conLabInp.addActionListener(this);
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 5;
    c.gridy = 1;
    layout.setConstraints(conLabInp,c);
    conLabInp.setText(String.valueOf(pop.getConnProb()));
    panel.add(conLabInp);

    /* synapse probabilities */
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = 2;
    layout.setConstraints(insSynLabel,c);
    panel.add(insSynLabel);


    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = 2;
    layout.setConstraints(insSynComboBox,c);
    panel.add(insSynComboBox);

    for(int i=0; i < EvoObject.SYN_MODES.length; i++)
    {
      insSynComboBox.addItem(EvoObject.SYN_MODES[i]);
    }

    insSynComboBox.addItemListener(this);


    return panel;
  }


  private JPanel setupReal_valuePanel(JPanel panel, Population pop){
    GridBagLayout      layout = new GridBagLayout();
    GridBagConstraints c      = new GridBagConstraints();
    panel.setLayout(layout);


    panel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder("real value mutation"),
          BorderFactory.createEmptyBorder(5,5,5,5)));

    /* weight probabilities */
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = 0;
    layout.setConstraints(chgWeightLab,c);
    panel.add(chgWeightLab);

    chgWeightLabInp.addActionListener(this);
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = 0;
    chgWeightLabInp.setText(String.valueOf(pop.getChangeWeightProb()));
    layout.setConstraints(chgWeightLabInp,c);
    panel.add(chgWeightLabInp);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 2;
    c.gridy = 0;
    layout.setConstraints(strWeightLab,c);
    panel.add(strWeightLab);

    strWeightLabInp.addActionListener(this);
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 3;
    c.gridy = 0;
    strWeightLabInp.setText(String.valueOf(pop.getChangeWeightIntens()));
    layout.setConstraints(strWeightLabInp,c);
    panel.add(strWeightLabInp);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 4;
    c.gridy = 0;
    layout.setConstraints(limWeightLab,c);
    panel.add(limWeightLab);

    limWeightLabInp.addActionListener(this);
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 5;
    c.gridy = 0;
    limWeightLabInp.setText(String.valueOf(pop.getWeightLim()));
    layout.setConstraints(limWeightLabInp,c);
    panel.add(limWeightLabInp);


    /* bias probabilities */
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = 1;
    layout.setConstraints(chgBiasLab,c);
    panel.add(chgBiasLab);

    chgBiasLabInp.addActionListener(this);
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = 1;
    chgBiasLabInp.setText(String.valueOf(pop.getChangeBiasProb()));
    layout.setConstraints(chgBiasLabInp,c);
    panel.add(chgBiasLabInp);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 2;
    c.gridy = 1;
    layout.setConstraints(strBiasLab,c);
    panel.add(strBiasLab);

    strBiasLabInp.addActionListener(this);
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 3;
    c.gridy = 1;
    strBiasLabInp.setText(String.valueOf(pop.getChangeBiasIntens()));
    layout.setConstraints(strBiasLabInp,c);
    panel.add(strBiasLabInp);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 4;
    c.gridy = 1;
    layout.setConstraints(limBiasLab,c);
    panel.add(limBiasLab);

    limBiasLabInp.addActionListener(this);
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 5;
    c.gridy = 1;
    limBiasLabInp.setText(String.valueOf(pop.getBiasLim()));
    layout.setConstraints(limBiasLabInp,c);
    panel.add(limBiasLabInp);


    /* decay probabilities */
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = 2;
    layout.setConstraints(chgDecayLab,c);
    panel.add(chgDecayLab);

    chgDecayLabInp.addActionListener(this);
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = 2;
    chgDecayLabInp.setText(String.valueOf(pop.getChangeDecayProb()));
    layout.setConstraints(chgDecayLabInp,c);
    panel.add(chgDecayLabInp);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 2;
    c.gridy = 2;
    layout.setConstraints(strDecayLab,c);
    panel.add(strDecayLab);

    strDecayLabInp.addActionListener(this);
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 3;
    c.gridy = 2;
    strDecayLabInp.setText(String.valueOf(pop.getChangeDecayIntens()));
    layout.setConstraints(strDecayLabInp,c);
    panel.add(strDecayLabInp);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 4;
    c.gridy = 2;
    layout.setConstraints(limDecayLab,c);
    panel.add(limDecayLab);

    limDecayLabInp.addActionListener(this);
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 5;
    c.gridy = 2;
    limDecayLabInp.setText(String.valueOf(pop.getDecayLim()));
    layout.setConstraints(limDecayLabInp,c);
    panel.add(limDecayLabInp);

    return panel;
  }





  public JPanel getPanel(){
    return this.panel;
  }


  public void itemStateChanged(ItemEvent event)
  {
    if (event.getSource() == insSynComboBox)
    {
      this.pop.setSynapseInsertionMode(insSynComboBox.getSelectedIndex());
      System.out.println("Set Synapse Insertion Mode to: " +
          EvoObject.SYN_MODES[insSynComboBox.getSelectedIndex()]);
    }
  }

  public void actionPerformed(ActionEvent e){
    double dblVal;
    int    intVal; 

    if(e.getSource() == learnParamButton)
    {
      //learnDialog.setVisible(true);
      learnDialog.switchVisible();
    }


    if(e.getSource() ==  insNeuLabInp)
    {
      dblVal = Double.parseDouble(insNeuLabInp.getText());
      this.pop.setInsNeuProb(dblVal);
      insNeuLabInp.setText(Double.toString(this.pop.getInsNeuProb()));

      System.out.println(pop.getName() + 
          " : set 'insNeuProb' to " + 
          Double.toString(this.pop.getInsNeuProb()) );
    }

    if(e.getSource() ==  delNeuLabInp)
    {
      dblVal = Double.parseDouble(delNeuLabInp.getText());
      this.pop.setDelNeuProb(dblVal);
      delNeuLabInp.setText(Double.toString(this.pop.getDelNeuProb()));

      System.out.println(pop.getName() + 
          " : set 'delNeuProb' to " + 
          Double.toString(this.pop.getDelNeuProb()) );
    }

    if(e.getSource() ==  maxNeuLabInp)
    {
      intVal = Integer.parseInt(maxNeuLabInp.getText());
      this.pop.setMaxHidNeuNmb(intVal);
      maxNeuLabInp.setText(Integer.toString(this.pop.getMaxHidNeuNmb()));

      System.out.println(pop.getName() + 
          " : set 'maxHiddenNs' to " + 
          Integer.toString(this.pop.getMaxHidNeuNmb()) );
    }

    if(e.getSource() ==  insSynLabInp)
    {
      dblVal = Double.parseDouble(insSynLabInp.getText());
      this.pop.setInsSynProb(dblVal);
      insSynLabInp.setText(Double.toString(this.pop.getInsSynProb()));

      System.out.println(pop.getName() + 
          " : set 'insSynProb' to " + 
          Double.toString(this.pop.getInsSynProb()) );
    }

    if(e.getSource() ==  delSynLabInp)
    {
      dblVal = Double.parseDouble(delSynLabInp.getText());
      this.pop.setDelSynProb(dblVal);
      delSynLabInp.setText(Double.toString(this.pop.getDelSynProb()));

      System.out.println(pop.getName() + 
          " : set 'delSynProb' to " + 
          Double.toString(this.pop.getDelSynProb()) );
    }

    if(e.getSource() ==  conLabInp)
    {
      dblVal = Double.parseDouble(conLabInp.getText());
      this.pop.setConnProb(dblVal);
      conLabInp.setText(Double.toString(this.pop.getConnProb()));

      System.out.println(pop.getName() + 
          " : set 'conProb' to " + 
          Double.toString(this.pop.getConnProb()) );
    }


    if(e.getSource() ==  chgBiasLabInp)
    {
      dblVal = Double.parseDouble(chgBiasLabInp.getText());
      this.pop.setChangeBiasProb(dblVal);
      chgBiasLabInp.setText(Double.toString(this.pop.getChangeBiasProb()));

      System.out.println(pop.getName() + 
          " : set 'chgBiasProb' to " + 
          Double.toString(this.pop.getChangeBiasProb()) );
    }

    if(e.getSource() ==  strBiasLabInp)
    {
      dblVal = Double.parseDouble(strBiasLabInp.getText());
      this.pop.setChangeBiasIntens(dblVal);
      strBiasLabInp.setText(Double.toString(this.pop.getChangeBiasIntens()));

      System.out.println(pop.getName() + 
          " : set 'BiasIntens' to " + 
          Double.toString(this.pop.getChangeBiasIntens()) );
    }


    if(e.getSource() ==  limBiasLabInp)
    {
      dblVal = Double.parseDouble(limBiasLabInp.getText());
      this.pop.setBiasLim(dblVal);
      limBiasLabInp.setText(Double.toString(this.pop.getBiasLim()));

      System.out.println(pop.getName() + 
          " : set 'Bias lim.' to " + 
          Double.toString(this.pop.getBiasLim()) );
    }


    if(e.getSource() ==  chgWeightLabInp)
    {
      dblVal = Double.parseDouble(chgWeightLabInp.getText());
      this.pop.setChangeWeightProb(dblVal);
      chgWeightLabInp.setText(Double.toString(this.pop.getChangeWeightProb()));

      System.out.println(pop.getName() + 
          " : set 'chgWeightProb' to " + 
          Double.toString(this.pop.getChangeWeightProb()) );
    }

    if(e.getSource() ==  strWeightLabInp)
    {
      dblVal = Double.parseDouble(strWeightLabInp.getText());
      this.pop.setChangeWeightIntens(dblVal);
      strWeightLabInp.setText(Double.toString(this.pop.getChangeWeightIntens()));

      System.out.println(pop.getName() + 
          " : set 'Weight Intens' to " + 
          Double.toString(this.pop.getChangeWeightIntens()) );
    }


    if(e.getSource() ==  limWeightLabInp)
    {
      dblVal = Double.parseDouble(limWeightLabInp.getText());
      this.pop.setWeightLim(dblVal);
      limWeightLabInp.setText(Double.toString(this.pop.getWeightLim()));

      System.out.println(pop.getName() + 
          " : set 'Weight lim.' to " + 
          Double.toString(this.pop.getWeightLim()) );
    }


    if(e.getSource() ==  chgDecayLabInp)
    {
      dblVal = Double.parseDouble(chgDecayLabInp.getText());
      this.pop.setChangeDecayProb(dblVal);
      chgDecayLabInp.setText(Double.toString(this.pop.getChangeDecayProb()));

      System.out.println(pop.getName() + 
          " : set 'chgDecayProb' to " + 
          Double.toString(this.pop.getChangeDecayProb()) );
    }

    if(e.getSource() ==  strDecayLabInp)
    {
      dblVal = Double.parseDouble(strDecayLabInp.getText());
      this.pop.setChangeDecayIntens(dblVal);
      strDecayLabInp.setText(Double.toString(this.pop.getChangeDecayIntens()));

      System.out.println(pop.getName() + 
          " : set 'Decay Intens' to " + 
          Double.toString(this.pop.getChangeDecayIntens()) );
    }


    if(e.getSource() ==  limDecayLabInp)
    {
      dblVal = Double.parseDouble(limDecayLabInp.getText());
      this.pop.setDecayLim(dblVal);
      limDecayLabInp.setText(Double.toString(this.pop.getDecayLim()));

      System.out.println(pop.getName() + 
          " : set 'Decay lim.' to " + 
          Double.toString(this.pop.getDecayLim()) );
    }


    /**
     * GUI update according to the initialization of the evolution run
     *
     */
    if((e.getSource() == pop) && (e.getActionCommand() == "INIT")){
      if(pop.getSynMode() == SynapseMode.CONVENTIONAL)
      {
        chgWeightLabInp.setEditable(true); 
        strWeightLabInp.setEditable(true);  
        limWeightLabInp.setEditable(true);
        chgWeightLabInp.setText(Double.toString(pop.getChangeWeightProb())); 
        strWeightLabInp.setText(Double.toString(pop.getChangeWeightIntens()));  
        limWeightLabInp.setText(Double.toString(pop.getWeightLim()));
      }
      else
      {
        chgWeightLabInp.setEditable(true); 
        strWeightLabInp.setEditable(false);  
        limWeightLabInp.setEditable(false);
        chgWeightLabInp.setText(Double.toString(pop.getChangeWeightProb())); 
        strWeightLabInp.setText("n.d.");  
        limWeightLabInp.setText("n.d.");
      }
    }

    if((e.getSource() == pop) && (e.getActionCommand() == "RESET")){
      if(pop.getSynMode() == SynapseMode.CONVENTIONAL)
      {
        chgWeightLabInp.setEditable(true); 
        strWeightLabInp.setEditable(true);  
        limWeightLabInp.setEditable(true);
        chgWeightLabInp.setText(Double.toString(pop.getChangeWeightProb())); 
        strWeightLabInp.setText(Double.toString(pop.getChangeWeightIntens()));  
        limWeightLabInp.setText(Double.toString(pop.getWeightLim()));
      }
      else
      {
        chgWeightLabInp.setEditable(true); 
        strWeightLabInp.setEditable(false);  
        limWeightLabInp.setEditable(false);
        chgWeightLabInp.setText(Double.toString(pop.getChangeWeightProb())); 
        strWeightLabInp.setText("n.d.");  
        limWeightLabInp.setText("n.d.");
      }
    }

    if((e.getSource() == pop) && (e.getActionCommand() == "SYNMODE_CHANGED")){
      if(pop.getSynMode() == SynapseMode.CONVENTIONAL)
      {
        chgWeightLabInp.setEditable(true); 
        strWeightLabInp.setEditable(true);  
        limWeightLabInp.setEditable(true);
        chgWeightLabInp.setText(Double.toString(pop.getChangeWeightProb())); 
        strWeightLabInp.setText(Double.toString(pop.getChangeWeightIntens()));  
        limWeightLabInp.setText(Double.toString(pop.getWeightLim()));
      }
      else
      {
        chgWeightLabInp.setEditable(true); 
        strWeightLabInp.setEditable(false);  
        limWeightLabInp.setEditable(false);
        chgWeightLabInp.setText(Double.toString(pop.getChangeWeightProb())); 
        strWeightLabInp.setText("n.d.");  
        limWeightLabInp.setText("n.d.");
      }
    }

    // new population paramter may have changed, update of the gui
    if((e.getSource() == pop) && (e.getActionCommand() == "REFRESH")){


      // weight parmater 
      if(pop.getSynMode() == SynapseMode.CONVENTIONAL)
      {
        chgWeightLabInp.setText(Double.toString(pop.getChangeWeightProb())); 
        strWeightLabInp.setText(Double.toString(pop.getChangeWeightIntens()));  
        limWeightLabInp.setText(Double.toString(pop.getWeightLim()));
      }
      else
      {
        chgWeightLabInp.setText(Double.toString(pop.getChangeWeightProb())); 
        strWeightLabInp.setText("n.d.");  
        limWeightLabInp.setText("n.d.");
      }
      // bias parmater 
      chgBiasLabInp.setText(Double.toString(pop.getChangeBiasProb())); 
      strBiasLabInp.setText(Double.toString(pop.getChangeBiasIntens()));  
      limBiasLabInp.setText(Double.toString(pop.getBiasLim()));
      // decay parmater 
      chgDecayLabInp.setText(Double.toString(pop.getChangeDecayProb())); 
      strDecayLabInp.setText(Double.toString(pop.getChangeDecayIntens()));  
      limDecayLabInp.setText(Double.toString(pop.getDecayLim()));

      // insert, delete, connectivity and max. number of hidden
      conLabInp.setText(Double.toString(this.pop.getConnProb()));
      delSynLabInp.setText(Double.toString(this.pop.getDelSynProb()));
      insSynLabInp.setText(Double.toString(this.pop.getInsSynProb()));
      insNeuLabInp.setText(Double.toString(this.pop.getInsNeuProb()));
      delNeuLabInp.setText(Double.toString(this.pop.getDelNeuProb()));
      delSynLabInp.setText(Double.toString(this.pop.getDelSynProb()));
      maxNeuLabInp.setText(Integer.toString(this.pop.getMaxHidNeuNmb()));
    }
  }
}








