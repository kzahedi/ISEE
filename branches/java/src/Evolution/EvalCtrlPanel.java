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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;



//TODO check is it really necessary
//import com.equitysoft.components.IEButton;

public class EvalCtrlPanel implements ActionListener{
  private JPanel panel = new JPanel(false);
  private Evolution evo = null;

  // evaluation organization 
  private String[]  evalStrings  = { EvaluationType.ONE2MANY.toString(),
    EvaluationType.ONE2ONE.toString(),
    EvaluationType.MIN_SYNC.toString(),
    EvaluationType.MAX_SYNC.toString()};
  private JComboBox evalList     = new JComboBox(evalStrings);

  private JLabel     insNLab    = new JLabel("N =  ");
  private JTextField insNLabInp = new JTextField(3);



  public EvalCtrlPanel(Evolution evo){
    this.evo   = evo;


    this.evo.setEvalCtrlPanel(this);

    GridBagLayout      layout = new GridBagLayout();
    GridBagConstraints c      = new GridBagConstraints();
    this.panel.setLayout(layout);

    panel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder("eval. method"),
          BorderFactory.createEmptyBorder(5,5,5,5)));

    // set default evlauation type
    this.evo.setEvalType(EvaluationType.ONE2ONE);
    evalList.setSelectedIndex(1); 

    c.fill = GridBagConstraints.HORIZONTAL;
    c.weighty = 0.5;
    c.weightx = 0.5;
    c.gridx = 1;
    c.gridy = 0;
    layout.setConstraints(evalList, c);
    panel.add(evalList);
    evalList.addActionListener(this); 


    // N input
    c.gridx = 0; 
    c.gridy = 1;
    layout.setConstraints(insNLab, c);
    panel.add(insNLab); 

    c.gridx = 1; 
    c.gridy = 1;
    layout.setConstraints(insNLabInp, c);
    panel.add(insNLabInp);  
    insNLabInp.addActionListener(this); 
    // disable, because by default evaluation type is ONE2ONE
    //  and N has no meaning
    disableNInput();



  }

  public JPanel getPanel(){
    return this.panel;
  }


  private void disableNInput(){
    insNLabInp.setText("n.d.");
    insNLabInp.setEditable(false);
  }


  private void enableNInput(){
    insNLabInp.setText(String.valueOf(this.evo.getNumberEvalTasks()));
    insNLabInp.setEditable(true);
  }



  public void actionPerformed(ActionEvent e){
    int intVal;

    if((e.getSource() ==  this.evo) && (e.getActionCommand() == "INIT"))
    {
      evalList.setEnabled(false);
      insNLabInp.setEditable(false);
    }


    if((e.getSource() ==  this.evo) && (e.getActionCommand() == "RESET"))
    {
      evalList.setEnabled(true);
      if(this.evo.getPopNumb() < 2)
      {
        insNLabInp.setEditable(true);
      }
    }

    if(e.getSource() == this.evalList){
      int index = this.evalList.getSelectedIndex();

      // parallel evaluation of one population iff.
      // the who evolution process contain only one population
      if((index == 0) && (this.evo.getPopNumb() > 1))
      {
        index = 1; 
      }

      if((index == 2) && (this.evo.getPopNumb() < 2))
      {
        index = 1;
      }

      if((index == 3) && (this.evo.getPopNumb() < 2))
      {
        index = 1;
      }

      this.evalList.setSelectedIndex(index);

      // set eval mode 
      switch(index)
      {
        case 0:
          this.evo.setEvalType(EvaluationType.ONE2MANY);
          enableNInput();
          break;
        case 1:
          this.evo.setEvalType(EvaluationType.ONE2ONE);
          disableNInput();
          break;
        case 2:
          this.evo.setEvalType(EvaluationType.MIN_SYNC);
          disableNInput();
          break;
        case 3:
          this.evo.setEvalType(EvaluationType.MAX_SYNC);
          disableNInput();
          break;
      }
    }

    if(e.getSource() == this.insNLabInp)
    {
      intVal = Integer.parseInt(insNLabInp.getText());
      this.evo.setNumberEvalTasks(intVal);
      insNLabInp.setText(String.valueOf(this.evo.getNumberEvalTasks()));    
    }

  }

}











