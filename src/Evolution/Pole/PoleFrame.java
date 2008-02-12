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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;




/**
 *  Populations implements management system of a set of networks.  <br>
 *
 *
 *
 *
 */



public class PoleFrame extends JFrame{
  JPanel mainPanel = new JPanel(false);


  public PoleFrame(){
    super();

    setTitle("benchmark: pole balancer");
    setBounds (50,50, 850,550);
    setResizable(false);


    GridBagLayout      layout = new GridBagLayout();
    GridBagConstraints c      = new GridBagConstraints();
    this.mainPanel.setLayout(layout);

    this.mainPanel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder("pole balancer"),
          BorderFactory.createEmptyBorder(5,5,5,5)));



    addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {System.exit(0);}

        });

    Pole pole = new Pole();

    /* parameter panel */
    PoleParameterPanel poleParameterPanel = new PoleParameterPanel(pole); 
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 0.5;
    //c.weighty = 0.5;
    c.gridx = 0;
    c.gridy = 0;
    layout.setConstraints(poleParameterPanel.getPanel(),c);
    mainPanel.add(poleParameterPanel.getPanel());

    /* communication panel */
    PoleClientPanel poleClientPanel = new PoleClientPanel(pole); 
    pole.setClientPanel(poleClientPanel);

    //c.fill = GridBagConstraints.BOTH;
    //c.weightx = 0.5;
    //c.weighty = 0.5;
    c.gridx = 1;
    c.gridy = 0;
    layout.setConstraints(poleClientPanel.getPanel(),c);
    mainPanel.add(poleClientPanel.getPanel());

    /* first return map panel */
    ReturnMapPanel returnMap = new ReturnMapPanel(pole);
    c.fill = GridBagConstraints.BOTH;
    //c.weightx = 0.5;
    c.weighty = 0.5;
    c.gridx = 1;
    c.gridy = 1;
    layout.setConstraints(returnMap.getPanel(),c);
    mainPanel.add(returnMap.getPanel());


    /* phase panel */
    PolePhasePanel phaseMap = new PolePhasePanel(pole);
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 0.5;
    c.weighty = 0.5;
    c.gridx = 0;
    c.gridy = 1;
    layout.setConstraints(phaseMap.getPanel(),c);
    mainPanel.add(phaseMap.getPanel());



    /* make all visible */
    setContentPane(mainPanel);
    setResizable(true);
    setVisible(true);

    returnMap.init();
    phaseMap.init();
    pole.setFirstReturnPanel(returnMap);
    pole.setPhasePanel(phaseMap);

  }

  public static void main(String argv[]){
    PoleFrame win = new PoleFrame();
  }
}









