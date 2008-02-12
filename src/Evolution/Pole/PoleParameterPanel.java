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

package Evolution.Pole;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;




public class PoleParameterPanel implements ActionListener{
    private Pole        pole          = null;


    private JPanel       panel        = new JPanel(false);
    private NumberFormat numberFormat = NumberFormat.getInstance(Locale.UK);


    private JLabel     timeStepLab    = new JLabel("  time step: ");
    private JTextField timeStepLabInp = new JTextField(6);

    private JLabel     timeQuietNrLab    = new JLabel("  num. quiet steps: ");
    private JTextField timeQuietNrLabInp = new JTextField(6);

    private JLabel     timeMaxNrLab     = new JLabel("  num. time step: ");
    private JTextField timeMaxNrLabInp  = new JTextField(6);
    private JLabel     timeTestNrLab    = new JLabel("  num. test steps: ");
    private JTextField timeTestNrLabInp = new JTextField(6);

    private JLabel     cost_forceLab        = new JLabel("  cost force: ");
    private JTextField cost_forceLabInp     = new JTextField(6);
    private JLabel     bonus_loc_cartLab    = new JLabel("  bonus loc car: ");
    private JTextField bonus_loc_cartLabInp = new JTextField(6);
    private JLabel     bonus_ang_poleLab    = new JLabel("  bonus ang pole: ");
    private JTextField bonus_ang_poleLabInp = new JTextField(6);

    private JLabel     max_init_loc_cartLab    = new JLabel("  init loc car: ");
    private JTextField max_init_loc_cartLabInp = new JTextField(6);
    private JLabel     max_init_ang_poleLab    = new JLabel("  init ang pole: ");
    private JTextField max_init_ang_poleLabInp = new JTextField(6);

    private JLabel     max_loc_cartLab    = new JLabel("  max loc car: ");
    private JTextField max_loc_cartLabInp = new JTextField(6);
    private JLabel     max_ang_poleLab    = new JLabel("  max ang pole: ");
    private JTextField max_ang_poleLabInp = new JTextField(6);

    private JLabel     pause = null;


    public PoleParameterPanel(Pole p){
  this.pole = p;

  this.numberFormat.setMinimumFractionDigits(2);
  this.numberFormat.setMaximumFractionDigits(2);
  
  GridBagLayout      layout = new GridBagLayout();
  GridBagConstraints c      = new GridBagConstraints();
  this.panel.setLayout(layout);


  this.panel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createTitledBorder("parameter"),
      BorderFactory.createEmptyBorder(5,5,5,5)));

  this.pole.setParameterPanel(this);

  /* simulatin steps*/
  c.gridx = 0;
  c.gridy = 0;
  layout.setConstraints(timeStepLab,c);
  panel.add(timeStepLab);

  c.gridx = 1;
  c.gridy = 0;
  timeStepLabInp.setText(String.valueOf(pole.getTimeStep()));
  layout.setConstraints(timeStepLabInp,c);
        timeStepLabInp.addActionListener(this);
  panel.add(timeStepLabInp);

  
  c.gridx = 2;
  c.gridy = 0;
  layout.setConstraints(timeQuietNrLab,c);
  panel.add(timeQuietNrLab);
    
  c.gridx = 3;
  c.gridy = 0;
  timeQuietNrLabInp.setText(String.valueOf(pole.getQuietSteps()));
  layout.setConstraints(timeQuietNrLabInp,c);
  timeQuietNrLabInp.setEditable(false);
  panel.add(timeQuietNrLabInp);

  
  c.gridx = 0;
  c.gridy = 1;
  c.anchor = GridBagConstraints.WEST;
  layout.setConstraints(timeMaxNrLab,c);
  panel.add(timeMaxNrLab);
  
  
  c.gridx = 1;
  c.gridy = 1;
  timeMaxNrLabInp.setText(String.valueOf(pole.getTimeMaxNr()));
  layout.setConstraints(timeMaxNrLabInp,c);
  timeMaxNrLabInp.addActionListener(this);
  panel.add(timeMaxNrLabInp);

  
  c.gridx = 2;
  c.gridy = 1;
  layout.setConstraints(timeTestNrLab,c);
  panel.add(timeTestNrLab);
    
  c.gridx = 3;
  c.gridy = 1;
  timeTestNrLabInp.setText(String.valueOf(pole.getTimeTestNr()));
  layout.setConstraints(timeTestNrLabInp,c);
  timeTestNrLabInp.addActionListener(this);
  panel.add(timeTestNrLabInp);

  pause = new JLabel(" ");
  c.gridx = 0;
  c.gridy = 2;
  layout.setConstraints(pause,c);
  panel.add(pause);
  

  // fitness function factors 
  c.gridx = 0;
  c.gridy = 3;
  layout.setConstraints(cost_forceLab,c);
  panel.add(cost_forceLab);
  
  c.gridx = 1;
  c.gridy = 3;
  cost_forceLabInp.setText(String.valueOf(pole.getCostForce()));
  layout.setConstraints(cost_forceLabInp,c);
  cost_forceLabInp.addActionListener(this);
  panel.add(cost_forceLabInp);


  c.gridx = 2;
  c.gridy = 3;
  layout.setConstraints(bonus_loc_cartLab,c);
  panel.add(bonus_loc_cartLab);
    
  c.gridx = 3;
  c.gridy = 3;
  bonus_loc_cartLabInp.setText(String.valueOf(pole.getBonusLocCar()));
  layout.setConstraints(bonus_loc_cartLabInp,c);
  bonus_loc_cartLabInp.addActionListener(this);
  panel.add(bonus_loc_cartLabInp);

  
  c.gridx = 0;
  c.gridy = 4;
  layout.setConstraints(bonus_ang_poleLab,c);
  panel.add(bonus_ang_poleLab);
    
  c.gridx = 1;
  c.gridy = 4;
  bonus_ang_poleLabInp.setText(String.valueOf(pole.getBonusAngPole()));
  layout.setConstraints(bonus_ang_poleLabInp,c);
  bonus_ang_poleLabInp.addActionListener(this);
  panel.add(bonus_ang_poleLabInp);

  pause = new JLabel(" ");
  c.gridx = 0;
  c.gridy = 5;
  layout.setConstraints(pause,c);
  panel.add(pause);


  // initial parameters 
  c.gridx = 0;
  c.gridy = 6;
  layout.setConstraints(max_init_loc_cartLab,c);
  panel.add(max_init_loc_cartLab);
    
  c.gridx = 1;
  c.gridy = 6;
  max_init_loc_cartLabInp.setText(String.valueOf(pole.getMaxInitLocCar()));
  layout.setConstraints(max_init_loc_cartLabInp,c);
  max_init_loc_cartLabInp.addActionListener(this);
  panel.add(max_init_loc_cartLabInp);


  c.gridx = 2;
  c.gridy = 6;
  layout.setConstraints(max_init_ang_poleLab,c);
  panel.add(max_init_ang_poleLab);
    
  c.gridx = 3;
  c.gridy = 6;
  max_init_ang_poleLabInp.setText(String.valueOf(pole.getMaxInitAngPole()));
  layout.setConstraints(max_init_ang_poleLabInp,c);
  max_init_ang_poleLabInp.addActionListener(this);
  panel.add(max_init_ang_poleLabInp);


  // boundary conditions 
  c.gridx = 0;
  c.gridy = 8;
  layout.setConstraints(max_loc_cartLab,c);
  panel.add(max_loc_cartLab);
    
  c.gridx = 1;
  c.gridy = 8;
  max_loc_cartLabInp.setText(String.valueOf(pole.getMaxLocCar()));
  layout.setConstraints(max_loc_cartLabInp,c);
  max_loc_cartLabInp.addActionListener(this);
  panel.add(max_loc_cartLabInp);


  c.gridx = 2;
  c.gridy = 8;
  layout.setConstraints(max_ang_poleLab,c);
  panel.add(max_ang_poleLab);
    
  c.gridx = 3;
  c.gridy = 8;
  max_ang_poleLabInp.setText(String.valueOf(pole.getMaxAngPole()));
  layout.setConstraints(max_ang_poleLabInp,c);
  max_ang_poleLabInp.addActionListener(this);
  panel.add(max_ang_poleLabInp);
  

    }





    public JPanel getPanel(){
  return (this.panel);
    }
    

    public void actionPerformed(ActionEvent e){
  int intVal;
  double dblVal;

  if((e.getSource() == this.pole) && (e.getActionCommand() == "NEW_GEN"))
  {
      int timeTestNrOld = Integer.parseInt(timeTestNrLabInp.getText());
      int timeMaxNrOld  = Integer.parseInt(timeMaxNrLabInp.getText());

      if((timeTestNrOld != this.pole.getTimeTestNr()) ||
         (timeMaxNrOld  != this.pole.getTimeMaxNr())     )
      {
    timeTestNrLabInp.setText(Integer.toString(this.pole.getTimeTestNr()));
    timeMaxNrLabInp.setText(Integer.toString(this.pole.getTimeMaxNr()));
    (this.pole.getPhasePanel()).actionPerformed(new ActionEvent(this,310,"NEW_RANGE"));
      };


  }

  
  if(e.getSource() == timeStepLabInp)
  {
      dblVal = Double.parseDouble(timeStepLabInp.getText());
      this.pole.setTimeStep(dblVal);
      timeStepLabInp.setText(Double.toString(this.pole.getTimeStep()));
  }

  
  if(e.getSource() == timeMaxNrLabInp)
  {
      intVal = Integer.parseInt(timeMaxNrLabInp.getText());
      this.pole.setTimeMaxNr(intVal);
      timeMaxNrLabInp.setText(Integer.toString(this.pole.getTimeMaxNr()));

      (this.pole.getPhasePanel()).actionPerformed(new ActionEvent(this,310,"NEW_RANGE"));

  }

  
  if(e.getSource() == timeTestNrLabInp)
  {
      intVal = Integer.parseInt(timeTestNrLabInp.getText());
      this.pole.setTimeTestNr(intVal);
      timeTestNrLabInp.setText(Integer.toString(this.pole.getTimeTestNr()));
  }

  
  if(e.getSource() == cost_forceLabInp)
  {
      dblVal = Double.parseDouble(cost_forceLabInp.getText());
      this.pole.setCostForce(dblVal);
      cost_forceLabInp.setText(Double.toString(this.pole.getCostForce()));

  }

  
  if(e.getSource() == bonus_loc_cartLabInp)
  {
      dblVal = Double.parseDouble(bonus_loc_cartLabInp.getText());
      this.pole.setBonusLocCar(dblVal);
      bonus_loc_cartLabInp.setText(Double.toString(this.pole.getBonusLocCar()));
  }

  
  if(e.getSource() == bonus_ang_poleLabInp)
  {
      dblVal = Double.parseDouble(bonus_ang_poleLabInp.getText());
      this.pole.setBonusAngPole(dblVal);
      bonus_ang_poleLabInp.setText(Double.toString(this.pole.getBonusAngPole()));
  }

  
  if(e.getSource() == max_init_loc_cartLabInp)
  {
      dblVal = Double.parseDouble(max_init_loc_cartLabInp.getText());
      this.pole.setMaxInitLocCar(dblVal);
      max_init_loc_cartLabInp.setText(Double.toString(this.pole.getMaxInitLocCar()));
  }

  
  if(e.getSource() == max_init_ang_poleLabInp)
  {
      dblVal = Double.parseDouble(max_init_ang_poleLabInp.getText());
      this.pole.setMaxInitAngPole(dblVal);
      max_init_ang_poleLabInp.setText(Double.toString(this.pole.getMaxInitAngPole()));
  }

  
  if(e.getSource() == max_loc_cartLabInp)
  {
      dblVal = Double.parseDouble(max_loc_cartLabInp.getText());
      this.pole.setMaxLocCar(dblVal);
      max_loc_cartLabInp.setText(Double.toString(this.pole.getMaxLocCar()));

      (this.pole.getPhasePanel()).actionPerformed(new ActionEvent(this,310,"NEW_RANGE"));
  }
  

  if(e.getSource() == max_ang_poleLabInp)
  {
      dblVal = Double.parseDouble(max_ang_poleLabInp.getText());
      this.pole.setMaxAngPole(dblVal);
      max_ang_poleLabInp.setText(Double.toString(this.pole.getMaxAngPole()));
      
  }

    }

}




