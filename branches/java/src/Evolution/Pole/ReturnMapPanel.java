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
import javax.swing.JPanel;




public class ReturnMapPanel implements ActionListener{

    private Pole        pole          = null;


    private JPanel       panel        = new JPanel(false);
    private NumberFormat numberFormat = NumberFormat.getInstance(Locale.UK);


    private PoleDrawPanel   poleDrawPanel = null;



    public ReturnMapPanel(Pole p){
  this.pole = p;

  this.numberFormat.setMinimumFractionDigits(2);
  this.numberFormat.setMaximumFractionDigits(2);
  
  GridBagLayout      layout = new GridBagLayout();
  GridBagConstraints c      = new GridBagConstraints();
  this.panel.setLayout(layout);

  this.panel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createTitledBorder("1st return map"),
      BorderFactory.createEmptyBorder(5,5,5,5)));

  poleDrawPanel =  new PoleDrawPanel();


  /* drawing panel */
  c.fill = GridBagConstraints.BOTH;
  c.gridx = 0;
  c.gridy = 0;
  c.weightx = 0.5;
  c.weighty = 0.5;
  layout.setConstraints(poleDrawPanel,c);
  panel.add(poleDrawPanel);



    }

    public void init(){
  double[] xRange = {-1.2,1.2};
  double[] yRange = {-1.2,1.2};

  /* init draw panel */
  poleDrawPanel.init();
  poleDrawPanel.setRange(xRange,yRange);
  poleDrawPanel.drawLegend();
    }


    public JPanel getPanel(){
  return (this.panel);
    }


    public void refresh(){
  
    }

    public void drawBestTurn(){
  PoleLogList list;
  PoleState   stateN0;
  PoleState   stateN1;

  poleDrawPanel.drawLegend();

  list = pole.getBestTurn();
  for(int i = 1; i < list.size(); i++)
  {
      stateN0 = list.state(i-1);
      stateN1 = list.state(i);
      poleDrawPanel.drawPoint(stateN1.getForceSignal() / 10.0, stateN1.getForceSignal() / 10.0);
  }
    }




    public void actionPerformed(ActionEvent e){
  if(e.getActionCommand() == "NEW_POINTS"){
      poleDrawPanel.drawPoint(pole.getN0ForceSignal(), pole.getN1ForceSignal());
  }

  if(e.getActionCommand() == "NEW_TURN"){
      poleDrawPanel.drawLegend();
  }

  if(e.getActionCommand() == "DRAW_BEST"){
      poleDrawPanel.drawLegend();
      drawBestTurn();
  }


    }

}


























