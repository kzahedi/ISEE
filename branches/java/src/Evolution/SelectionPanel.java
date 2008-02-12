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


public class SelectionPanel implements ActionListener{
  private JPanel panel = new JPanel(false);
  private Population pop = null;
  
  private JLabel     avgPopSize    = new JLabel(" avg. pop. size : ");
  private JTextField avgPopSizeInp = new JTextField(6);
  private JLabel     birthGamma    = new JLabel(" birth gamma : ");
  private JTextField birthGammaInp = new JTextField(6);
  private JLabel     deathProb     = new JLabel(" death prob. : ");
  private JTextField deathProbInp  = new JTextField(6);
  private JLabel     saveNBest     = new JLabel(" save the best : ");
  private JTextField saveNBestInp  = new JTextField(6);

  
  private JLabel     space         = new JLabel(" ");
  

  public SelectionPanel(Population population){
  this.pop   = population;

  this.pop.setCtrlSelectionPanel(this);
  
  GridBagLayout      layout = new GridBagLayout();
  GridBagConstraints c      = new GridBagConstraints();
  this.panel.setLayout(layout);
  
  
  this.panel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createTitledBorder("selection"),
      BorderFactory.createEmptyBorder(5,5,5,5)));


  // birth gamma
  c.fill = GridBagConstraints.HORIZONTAL;
  c.gridx = 0;
  c.gridy = 0;
  layout.setConstraints(birthGamma,c);
  panel.add(birthGamma);

  c.fill = GridBagConstraints.HORIZONTAL;
  c.gridx = 1;
  c.gridy = 0;
  birthGammaInp.addActionListener(this);
  birthGammaInp.setText(String.valueOf(pop.getBirthGamma()));
  layout.setConstraints(birthGammaInp,c);
  panel.add(birthGammaInp);


  // avg. popsize 
  c.fill = GridBagConstraints.HORIZONTAL;
  c.gridx = 2;
  c.gridy = 0;
  layout.setConstraints(avgPopSize,c);
  panel.add(avgPopSize);

  c.fill = GridBagConstraints.HORIZONTAL;
  c.gridx = 3;
  c.gridy = 0;
  avgPopSizeInp.setText(String.valueOf(pop.getPopSize()));
  layout.setConstraints(avgPopSizeInp,c);
  avgPopSizeInp.addActionListener(this);
  panel.add(avgPopSizeInp);

  // save the n best
  c.fill = GridBagConstraints.HORIZONTAL;
  c.gridx = 4;
  c.gridy = 0;
  layout.setConstraints(saveNBest,c);
  panel.add(saveNBest);

  c.fill = GridBagConstraints.HORIZONTAL;
  c.gridx = 5;
  c.gridy = 0;
  saveNBestInp.setText(String.valueOf(pop.getSaveNBest())); 
  layout.setConstraints(saveNBestInp,c);
  saveNBestInp.addActionListener(this); 
  panel.add(saveNBestInp);





  /*
  c.fill = GridBagConstraints.HORIZONTAL;
  c.gridx = 2;
  c.gridy = 1;
  layout.setConstraints(deathProb,c);
  panel.add(deathProb);

  c.fill = GridBagConstraints.HORIZONTAL;
  c.gridx = 3;
  c.gridy = 1;
  deathProbInp.addActionListener(this);
  deathProbInp.setText(String.valueOf(pop.getDeathProb()));
  layout.setConstraints(deathProbInp,c);
  panel.add(deathProbInp);
  */
    } 
    

    public JPanel getPanel(){
  return this.panel;
    }
    
    
    public void actionPerformed(ActionEvent e){
  double dblVal;
  int    intVal; 

  if(e.getSource() ==  avgPopSizeInp)
  {
      intVal = Integer.parseInt(avgPopSizeInp.getText());
      this.pop.setPopSize(intVal);
      avgPopSizeInp.setText(Integer.toString(this.pop.getPopSize()));

      System.out.println(pop.getName() + 
             " : set 'popsize ' to " + 
             Integer.toString(this.pop.getPopSize()) );
  }

  if(e.getSource() ==  birthGammaInp)
  {
      dblVal = Double.parseDouble(birthGammaInp.getText());
      this.pop.setBirthGamma(dblVal);
      birthGammaInp.setText(Double.toString(this.pop.getBirthGamma()));

      System.out.println(pop.getName() + 
             " : set 'birth gamma' to " + 
             Double.toString(this.pop.getBirthGamma()) );

  }

  if(e.getSource() ==  deathProbInp)
  {
      dblVal = Double.parseDouble(deathProbInp.getText());
      this.pop.setDeathProb(dblVal);
      deathProbInp.setText(Double.toString(this.pop.getDeathProb()));

      System.out.println(pop.getName() + 
             " : set 'death rate' to " + 
             Double.toString(this.pop.getDeathProb()) );

  }

  if(e.getSource() == saveNBestInp)
  {
      intVal = Integer.parseInt(saveNBestInp.getText());
      this.pop.setSaveNBest(intVal);
      saveNBestInp.setText(Integer.toString(this.pop.getSaveNBest()));

      System.out.println(pop.getName() + 
             " : survivals per definition now " + 
             Integer.toString(this.pop.getSaveNBest()) );
      
  }


  if((e.getSource() == this.pop) && (e.getActionCommand() == "REFRESH"))
  {

      saveNBestInp.setText(Integer.toString(this.pop.getSaveNBest()));
      birthGammaInp.setText(Double.toString(this.pop.getBirthGamma()));
      avgPopSizeInp.setText(Integer.toString(this.pop.getPopSize()));
  }

  if((e.getSource() == this.pop) && (e.getActionCommand() == "INIT"))
  {
      // acording to the evaluation method MIN_SYNC the popSize may have changed
      avgPopSizeInp.setText(Integer.toString(this.pop.getPopSize()));
  }

    }
}











