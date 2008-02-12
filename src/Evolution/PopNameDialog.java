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

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class PopNameDialog {
    JFrame parent = null;
    JFrame frame =  new JFrame("new population names?");
    PopulationList pops  = null;
    JPanel         panel =  new JPanel(false);


    public PopNameDialog(JFrame parent){
  this.parent = parent;
  this.pops = null;
    };

    public void getNewNames(PopulationList populations){
  this.pops   = populations;

  GridBagLayout      layout = new GridBagLayout();
  GridBagConstraints c      = new GridBagConstraints();
  this.panel.setLayout(layout);

  panel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createTitledBorder("change population names"),
      BorderFactory.createEmptyBorder(5,5,5,5)));

  int i = 0;
  for(this.pops.start();this.pops.hasMore();this.pops.next())
  {
      i++;
      Population pop = pops.currentPop();

      JLabel     nameLab    = new JLabel(Integer.toString(i)+". population name: ");
      JTextField nameLabInp = new JTextField(10);

      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridx = 0;
      c.gridy = i;
      layout.setConstraints(nameLab,c);
      panel.add(nameLab);
      
      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridx = 1;
      c.gridy = i;
      nameLabInp.setText(pop.getName());
      nameLabInp.addActionListener(new PopNameActionListener(pop,nameLabInp));
      layout.setConstraints(nameLabInp,c);
      panel.add(nameLabInp);
  }


  /* get visible */
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new GridLayout(1,1));
        contentPane.add(panel);
  
        frame.addWindowListener(new WindowAdapter() {
    public void windowClosing(WindowEvent e) {
       System.exit(0);
    }
      });
        frame.pack();
        frame.setVisible(true);
    }



    /* simulate an actionlistener with new parameters */
    private class  PopNameActionListener implements ActionListener{
  Population pop = null;
  JTextField field = null;

  public PopNameActionListener(Population population, JTextField inputs){
      this.pop = population;
      this.field = inputs;
  };

  public void actionPerformed(ActionEvent e) {
      String old = pop.getName();
      pop.setName(this.field.getText());
      this.field.setText(pop.getName());
      System.out.println("change name " + old +
           " to " + pop.getName());
  }
    }
}

















