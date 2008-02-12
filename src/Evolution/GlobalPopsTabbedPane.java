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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;


public class GlobalPopsTabbedPane implements  ActionListener, ComponentListener {
  private JTabbedPane panel = new JTabbedPane();
  private Evolution evo = null;


  public GlobalPopsTabbedPane(Evolution evolution){
    Population pop;
    int counter  = 0;
    String file  = "images/b00";
    
    this.evo   = evolution;
    
    PopulationList pops = evo.getPopulations();
    for(pops.start();pops.hasMore();pops.next())
    {
      ImageIcon icon = new ImageIcon(
          Toolkit.getDefaultToolkit().getImage(
            LoggingPanel.class.getResource(file+""+ counter + ".gif")));
      counter = (counter + 1) % 10; 
      
      pop = pops.currentPop();
      pop.setIcon(icon);
      Component subPanel = makeTextPanel(pop, icon);
      
      subPanel.addComponentListener(this);
      panel.addTab(pop.getName(), icon, subPanel);
    }
    panel.setSelectedIndex(0);
    


    NetRankFrame netRankFrame = new NetRankFrame(this.evo);
    MonitorFrame monitor      = new MonitorFrame(this.evo);
    
  }
  
  private Component makeTextPanel(Population pop, ImageIcon icon) {
    JPanel popPanel         = new JPanel(false);
    
    JLabel iconLabel        = new JLabel(icon);
    JPanel iconPanel        = new JPanel();
    
    PopParSaveLoadPanel popParamLoadSavePanel = new PopParSaveLoadPanel(pop);
    NetTypePanel secondBoundaryPanel = new NetTypePanel(pop);
    MutationPanel secondMutationPanel = new MutationPanel(pop);
    EvaluationPanel secondEvaluationPanel = new EvaluationPanel(pop);
    SelectionPanel secondSelectionPanel = new SelectionPanel(pop);
    
    GridBagLayout      layout = new GridBagLayout();
    GridBagConstraints c      = new GridBagConstraints();
    popPanel.setLayout(layout);
    
    popPanel.setBorder(BorderFactory.createCompoundBorder(
			 BorderFactory.createTitledBorder(pop.getName() + " population parameter"),
			 BorderFactory.createEmptyBorder(0,0,0,0)));
    
    /* add icon panel */
    iconPanel.add(iconLabel);
    c.weightx = 0.5;
    c.weighty = 0.5;
    c.gridx = 0;
    c.gridy = 0;
    layout.setConstraints(iconPanel,c);
    popPanel.add(iconPanel);
    
    /* add load save buttons */
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = 0;
    layout.setConstraints(popParamLoadSavePanel.getPanel(),c);  
    popPanel.add(popParamLoadSavePanel.getPanel());
    
    
    
    /* net type and structure init */
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = 1;
    layout.setConstraints(secondBoundaryPanel.getPanel(),c);  
    popPanel.add(secondBoundaryPanel.getPanel());
    
    /* mutation probabilities */
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = 2;
    layout.setConstraints(secondMutationPanel.getPanel(),c);  
    popPanel.add(secondMutationPanel.getPanel());
    
    /* neuron costs and constants */
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = 3;
    layout.setConstraints(secondEvaluationPanel.getPanel(),c);  
    popPanel.add(secondEvaluationPanel.getPanel());
    
    /* selection paramter */
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = 4;
    layout.setConstraints(secondSelectionPanel.getPanel(),c);
    popPanel.add(secondSelectionPanel.getPanel());
    
    return popPanel;
  }
  
  
  public JTabbedPane getTabbedPane(){
    return this.panel;
  }
  
  
  public void componentShown(ComponentEvent e){
    System.out.println("neuer popindex = " + panel.getSelectedIndex());
    this.evo.setPopIndex(panel.getSelectedIndex());
    (this.evo.getNetRankPanel()).actionPerformed(new ActionEvent(this,30,"NEW_TAB_VISIBLE"));
    (this.evo.getMonitorPanel()).actionPerformed(new ActionEvent(this,31,"NEW_POP_VISIBLE"));
  } 
    
  public void componentHidden(ComponentEvent e) {
    ;;
  } 
  

  public void componentMoved(ComponentEvent e) {
    ;;
  } 
  
  public void componentResized(ComponentEvent e) {
    // System.out.println(panel.getSelectedIndex() + " is visited");
  } 
  
  
  public void actionPerformed(ActionEvent e){
    if(e.getSource() == panel){
      // System.out.println("tab nmb." + panel.getSelectedIndex() +"is selected ");
    }
  
  }
  
  

}








