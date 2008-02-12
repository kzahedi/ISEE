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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JPanel;




public class PolePhasePanel implements ActionListener {

  private Pole        pole          = null;


  private JPanel       panel        = new JPanel(false);
  private NumberFormat numberFormat = NumberFormat.getInstance(Locale.UK);


  private PoleDrawPanel   phaseDrawPanel = null;



  public PolePhasePanel(Pole p){
    this.pole = p;

    this.numberFormat.setMinimumFractionDigits(2);
    this.numberFormat.setMaximumFractionDigits(2);

    GridBagLayout      layout = new GridBagLayout();
    GridBagConstraints c      = new GridBagConstraints();
    this.panel.setLayout(layout);

    this.panel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder("phase map"),
          BorderFactory.createEmptyBorder(5,5,5,5)));

    phaseDrawPanel =  new PoleDrawPanel();


    /* drawing panel */
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0.5;
    c.weighty = 0.5;
    layout.setConstraints(phaseDrawPanel,c);
    panel.add(phaseDrawPanel);



  }

  public void init(){
    double[] xRange = {0.0, (double)this.pole.getTimeMaxNr()};
    double[] yRange = {-((double)this.pole. getMaxLocCar() + 0.2),this.pole.getMaxLocCar() + 0.2};

    /* init draw panel */
    phaseDrawPanel.init();
    phaseDrawPanel.setRange(xRange,yRange);
    phaseDrawPanel.drawLegend();
  }

  public JPanel getPanel(){
    return (this.panel);
  }

  public void drawBestTurn(){
    PoleLogList list;
    PoleState   state;

    Color red = new Color((float)0.8,(float)0.0,(float)0.0);
    Color green   = new Color((float)0.0,(float)0.8,(float)0.0);
    Color darkRed = new Color((float)0.5,(float)0.0,(float)0.0);
    Color darkGreen   = new Color((float)0.0,(float)0.5,(float)0.0);


    phaseDrawPanel.drawLegend();

    list = pole.getBestTurn();
    int i = 0;
    for(list.start(); list.hasMore(); list.next())
    {
      state = list.state();
      phaseDrawPanel.drawPoint(((double)i), state.getLoc(), green);
      phaseDrawPanel.drawPoint(((double)i), 10.0*state.getAng(), red);
      phaseDrawPanel.drawPoint(((double)i), state.getVelCar(), darkGreen);
      phaseDrawPanel.drawPoint(((double)i), state.getVelAng(), darkRed);
      phaseDrawPanel.drawPoint(((double)i), state.getForceSignal() / 10.0);
      i++;
    }
  }


  public void actionPerformed(ActionEvent e){
    Color red = new Color((float)0.8,(float)0.0,(float)0.0);
    Color green   = new Color((float)0.0,(float)0.8,(float)0.0);
    Color darkRed = new Color((float)0.5,(float)0.0,(float)0.0);
    Color darkGreen   = new Color((float)0.0,(float)0.5,(float)0.0);


    if(e.getActionCommand() == "NEW_RANGE"){

      double[] xRange = {0.0, (double)this.pole.getTimeMaxNr()};
      double[] yRange = {-((double)this.pole. getMaxLocCar() + 0.2),this.pole.getMaxLocCar() + 0.2};


      phaseDrawPanel.setRange(xRange, yRange);
      phaseDrawPanel.drawLegend();

      System.out.println("phase:NEW_RANGE");

    }

    if(e.getActionCommand() == "NEW_POINTS"){
      phaseDrawPanel.drawPoint(((double)pole.getSimTime()), pole.getLocCar(), green);
      phaseDrawPanel.drawPoint(((double)pole.getSimTime()), 10.0*pole.getAngPole(), red);
      phaseDrawPanel.drawPoint(((double)pole.getSimTime()), pole.getRateLocCar(), darkGreen);
      phaseDrawPanel.drawPoint(((double)pole.getSimTime()), pole.getRateAngPole(), darkRed);
      phaseDrawPanel.drawPoint(((double)pole.getSimTime()), pole.getForceCar() / 10.0);
    }

    if(e.getActionCommand() == "DRAW_BEST"){
      phaseDrawPanel.drawLegend();
      drawBestTurn();

      System.out.println("phase:DRAW_BEST");
    }

    if(e.getActionCommand() == "NEW_TURN"){
      phaseDrawPanel.drawLegend();

      System.out.println("phase:NEW_TURN");
    }


  }
}









