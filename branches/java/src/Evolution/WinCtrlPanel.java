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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
//TODO check is it really necessary
//import com.equitysoft.components.IEButton;

public class WinCtrlPanel implements ActionListener{
  private JPanel    panel = new JPanel(false);
  private Evolution evo = null;
  private Thread    evoThread = null;
  
  private ImageIcon     rankIcon          = new ImageIcon();
  private JButton       rankButton        = new JButton("net rank");
  private ImageIcon     paramIcon          = new ImageIcon();
  private JButton       paramButton        = new JButton("pop. param.");
  private ImageIcon     dynamicsIcon         = new ImageIcon();
  private JButton       dynamicsButton       = new JButton("evo. dyn.");
  
 

  
  public WinCtrlPanel(Evolution evo){
    this.evo   = evo;
        
    GridBagLayout      layout = new GridBagLayout();
    GridBagConstraints c      = new GridBagConstraints();
    this.panel.setLayout(layout);
    
    panel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder("monitors"),
          BorderFactory.createEmptyBorder(5,5,5,5)));
    
    // window buttons
    // net rank button
    c.anchor = GridBagConstraints.NORTH;
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = 0;
    layout.setConstraints(rankButton, c);
    rankButton.addActionListener(this);
    panel.add(rankButton);
    
    // population parameter button
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = 1;
    layout.setConstraints(paramButton, c);
    paramButton.addActionListener(this);
    panel.add(paramButton);
    
    // evolution dynamics button
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = 2;
    layout.setConstraints(dynamicsButton, c);
    dynamicsButton.addActionListener(this);
    panel.add(dynamicsButton);
  }
  
  
  public JPanel getPanel(){
    return this.panel;
  }
  



  public void actionPerformed(ActionEvent e){
    if(e.getSource() == rankButton) {
      (this.evo.getNetRankWin()).switchVisible();
    }
    
    if(e.getSource() == paramButton) {    
      (this.evo.getPopParaWin()).switchVisible();
    }
    
    if(e.getSource() == dynamicsButton) {
      (this.evo.getMonitorWin()).switchVisible();
    }
    
  }

}


