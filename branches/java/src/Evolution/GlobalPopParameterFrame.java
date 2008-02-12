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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GlobalPopParameterFrame extends JFrame implements ActionListener {
  Evolution evo        = null;
  JPanel    mainPanel  = new JPanel();
  private JButton mutationParamCopyButton = null;  

  public GlobalPopParameterFrame(Evolution evoTask) {
    super();

    this.evo  = evoTask;

    setTitle("parameter of all populations");
//    setBounds (50,50, 600,600);
    //addWindowListener(new WindowAdapter() {
    //  public void windowClosing(WindowEvent e) {System.exit(0);}  
    //});

    GridBagLayout      layout = new GridBagLayout();
    GridBagConstraints c      = new GridBagConstraints();
    this.mainPanel.setLayout(layout);

    this.mainPanel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder("population parameter"),
          BorderFactory.createEmptyBorder(5,5,5,5)));




    GlobalPopsTabbedPane popsPanel = new GlobalPopsTabbedPane(this.evo);

    c.fill = GridBagConstraints.BOTH;
    c.weightx = 0.5;
    c.weighty = 0.5;
    c.gridx = 0;
    c.gridy = 0;
    layout.setConstraints(popsPanel.getTabbedPane(),c);
    mainPanel.add(popsPanel.getTabbedPane());

    if((this.evo.getPopulations()).size() > 1)
    {
      mutationParamCopyButton = new JButton("copy mutation, evaluation and slection parameters");
      c.fill = GridBagConstraints.BOTH;
      c.weightx = 0.5;
      c.weighty = 0.5;
      c.gridx = 0;
      c.gridy = 1;
      layout.setConstraints(mutationParamCopyButton,c);
      mutationParamCopyButton.addActionListener(this);
      this.mainPanel.add(mutationParamCopyButton);
    }



    setContentPane(mainPanel);
    setSize(getMinimumSize());
    pack();
    setVisible(false);

    this.evo.setPopParaWin(this);
  }

  public void switchVisible(){
    this.setVisible(!this.isVisible());
  }


  public void actionPerformed(ActionEvent e){

    if(e.getSource() == mutationParamCopyButton)
    {
      Population refPop = (this.evo.getPopulations()).getPop(this.evo.getPopIndex());
      Population pop; 

      // get  reference parameters


      PopulationList pops = evo.getPopulations();
      for(pops.start();pops.hasMore();pops.next())
      {
        pop = pops.currentPop();

        //copy mutaion, evaluation and selection parameters 
        // mutation
        pop.setInsNeuProb(refPop.getInsNeuProb());
        pop.setInsSynProb(refPop.getInsSynProb());
        pop.setDelNeuProb(refPop.getDelNeuProb());
        pop.setDelSynProb(refPop.getDelSynProb());
        pop.setChangeBiasProb(refPop.getChangeBiasProb());
        pop.setChangeWeightProb(refPop.getChangeWeightProb());
        pop.setChangeDecayProb(refPop.getChangeDecayProb ());
        pop.setChangeBiasIntens(refPop.getChangeBiasIntens());
        pop.setChangeWeightIntens(refPop.getChangeWeightIntens());
        pop.setChangeDecayIntens(refPop.getChangeDecayIntens());
        pop.setBiasLim(refPop.getBiasLim());
        pop.setWeightLim(refPop.getWeightLim());
        pop.setDecayLim(refPop.getDecayLim());
        pop.setConnProb(refPop.getConnProb());
        pop.setMaxHidNeuNmb(refPop.getMaxHidNeuNmb());
        // evaluation
        pop.setC3(refPop.getC3());
        pop.setC2(refPop.getC2());
        pop.setC1(refPop.getC1());
        pop.setC0(refPop.getC0());
        pop.setCostNeu(refPop.getCostNeu());
        pop.setCostSyn(refPop.getCostSyn());
        //selection
        pop.setSaveNBest(refPop.getSaveNBest());
        pop.setBirthGamma(refPop.getBirthGamma());
        pop.setPopSize(refPop.getPopSize());

        pop.popRefresh();

      }
    }


  }

  public static void main(String argv[]){
    Evolution evo = new Evolution();

    GlobalPopParameterFrame win = new GlobalPopParameterFrame(evo);
  }
}







