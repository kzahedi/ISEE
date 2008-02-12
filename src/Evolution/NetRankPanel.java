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


import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import util.misc.NetTreeView;

public class NetRankPanel implements ActionListener, ListSelectionListener{
  private Evolution   evo = null;
  private Population  pop        = null;
  private NumberFormat numberFormat = NumberFormat.getInstance(Locale.UK);

  /* state  variables */
  /* performance */
  JPanel     popIconPanel    = new JPanel();
  JLabel     popIconLabel    = new JLabel();
  JLabel     popName     = new JLabel("population:");
  JTextField popNameMon  = new JTextField(30);
  JLabel     maxPerf     = new JLabel("max. s-perf.:");
  JTextField maxPerfMon  = new JTextField(6);
  JLabel     minPerf     = new JLabel("min. s-perf.:");
  JTextField minPerfMon  = new JTextField(6);
  JLabel     avgPerf     = new JLabel("avg. s-perf.:");
  JTextField avgPerfMon  = new JTextField(12);
  JLabel     avgOutPerf     = new JLabel("avg. o-perf.:");
  JTextField avgOutPerfMon  = new JTextField(12);
  /* population size */
  JLabel     popSize     = new JLabel("pop. size next gen.:");
  JTextField popSizeMon  = new JTextField(6);
  JLabel     genNmb      = new JLabel("generation:");
  JTextField genNmbMon   = new JTextField(6);
  /* network size */
  JLabel     avgNmbHidden     = new JLabel("avg. nmb. hidden:");
  JTextField avgNmbHiddenMon  = new JTextField(12);
  JLabel     avgNmbSyn        = new JLabel("avg. nmb. synapses:");
  JTextField avgNmbSynMon     = new JTextField(12);


  /* scroll panel */
  private JPanel      panel      = new JPanel(false);
  private JTable      table = null;    
  private JScrollPane scrollPane = null;

  /* constants for scroll panel */
  private int MAX_ROW;
  private int MAX_COL;
  private int row;
  private int col;
  private Object [][] tableContent = null;

  public NetRankPanel(Evolution evolution){
    this.evo  = evolution;
    this.pop   = this.evo.getPopulations().getPop(evo.getPopIndex());

    PopulationList pops = evo.getPopulations();
    Population p = null;
    for(pops.start(); pops.hasMore(); pops.next())
    {
      p = pops.currentPop();
      p.setMonitorNetRankPanel(this);
    }

    this.evo.setNetRankPanel(this);

    this.numberFormat.setMinimumFractionDigits(2);
    this.numberFormat.setMaximumFractionDigits(2);

    GridBagLayout      layout = new GridBagLayout();
    GridBagConstraints c      = new GridBagConstraints();
    this.panel.setLayout(layout);


    /* table layout */
    String [] columnNames = {"PIN","age","#","s-perf.","o-perf.","n","s","i","o","h"};
    MAX_ROW = 100;                  // one of the hopefully small numbers of fixed parameters 
    MAX_COL = 10;                   // number of elements in columnNames list
    tableContent = new Object [MAX_ROW][MAX_COL];


    this.panel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder("ranking of the best " + MAX_ROW + " nets of population "),
          BorderFactory.createEmptyBorder(5,5,5,5)));

    popNameMon.setEditable(false);
    genNmbMon.setEditable(false);
    maxPerfMon.setEditable(false);
    minPerfMon.setEditable(false);
    avgPerfMon.setEditable(false);
    avgOutPerfMon.setEditable(false);
    popSizeMon.setEditable(false);
    avgNmbHiddenMon.setEditable(false);
    avgNmbSynMon.setEditable(false);


    /* pop logo */
    popIconLabel.setIcon(this.pop.getIcon());
    popIconPanel.add(popIconLabel);
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = 0;
    layout.setConstraints(popIconPanel,c);
    panel.add(popIconPanel);

    /* pop name */
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = 2;
    layout.setConstraints(popName,c);
    panel.add(popName);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 2;
    c.gridy = 2;
    popNameMon.setText(String.valueOf(pop.getName()));
    layout.setConstraints(popNameMon,c);
    panel.add(popNameMon);  

    /* gen number */
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = 3;
    layout.setConstraints(genNmb,c);
    panel.add(genNmb);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 2;
    c.gridy = 3;
    //genNmbMon.setText(String.valueOf(this.evo.getGenNmb()));
    genNmbMon.setText("initial structure(s)");
    layout.setConstraints(genNmbMon,c);
    panel.add(genNmbMon); 

    /* current popsize */
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = 4;
    layout.setConstraints(popSize,c);
    panel.add(popSize);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 2;
    c.gridy = 4;
    popSizeMon.setText(String.valueOf(pop.getCurrentPopSize()));
    layout.setConstraints(popSizeMon,c);
    panel.add(popSizeMon);  

    /* max and min performance */

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = 5;
    layout.setConstraints(maxPerf,c);
    panel.add(maxPerf);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 2;
    c.gridy = 5;
    maxPerfMon.setText(String.valueOf(pop.bestPerformance()));
    layout.setConstraints(maxPerfMon,c);
    panel.add(maxPerfMon);  

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = 6;
    layout.setConstraints(minPerf,c);
    panel.add(minPerf);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 2;
    c.gridy = 6;
    minPerfMon.setText(String.valueOf(pop.worstPerformance()));
    layout.setConstraints(minPerfMon,c);
    panel.add(minPerfMon);  

    /* mean +/- var s-performance */
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = 7;
    layout.setConstraints(avgPerf,c);
    panel.add(avgPerf);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 2;
    c.gridy = 7;
    avgPerfMon.setText(String.valueOf(pop.avgPerformance()) + "   +/- " +
        String.valueOf(pop.sqrtVarPerformance()));
    layout.setConstraints(avgPerfMon,c);
    panel.add(avgPerfMon);  

    /* mean +/- var o-performance */
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = 8;
    layout.setConstraints(avgOutPerf,c);
    panel.add(avgOutPerf);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 2;
    c.gridy = 8;
    avgOutPerfMon.setText(String.valueOf(pop.avgOutPerf()) + "   +/- " +
        String.valueOf(pop.sqrtVarOutPerf()));
    layout.setConstraints(avgOutPerfMon,c);
    panel.add(avgOutPerfMon); 

    /* mean +/- var nmb hidden neurons */
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = 9;
    layout.setConstraints(avgNmbHidden,c);
    panel.add(avgNmbHidden);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 2;
    c.gridy = 9;
    avgNmbHiddenMon.setText(String.valueOf(pop.avgNmbHidden()) + "   +/-   " +
        String.valueOf(java.lang.Math.sqrt(pop.varNmbHidden())));
    layout.setConstraints(avgNmbHiddenMon,c);
    panel.add(avgNmbHiddenMon); 


    /* mean +/- var nmb of synapses */
    /* mean +/- var nmb of synapses */
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.gridy = 10;
    layout.setConstraints(avgNmbSyn,c);
    panel.add(avgNmbSyn);

    c.fill = GridBagConstraints.BOTH;
    c.gridx = 2;
    c.gridy = 10;
    avgNmbSynMon.setText(String.valueOf(pop.avgNmbSynapses()) + "   +/-   " +
        String.valueOf(java.lang.Math.sqrt(pop.varNmbSynapses())));
    layout.setConstraints(avgNmbSynMon,c);
    panel.add(avgNmbSynMon);  

    /* initialize tabel content with current population */
    EvoObjectList indies = pop.getParents();
    EvoObject     obj;
    int           index = 0;

    for(indies.start(); indies.hasMore(); indies.next())
    {
      obj = indies.object();
      tableContent[index][0] = new Integer(obj.getPIN());
      tableContent[index][1] = new Integer(obj.getAge());
      tableContent[index][2] = new Integer(obj.getCount());
      tableContent[index][3] = new String (String.valueOf(numberFormat.format(obj.getPerformance())));
      tableContent[index][4] = new String (String.valueOf(numberFormat.format(obj.getOutPerf())));
      /* number of input units and synapsis */
      tableContent[index][5] = new Integer(((obj.getNet()).neurons()).size());
      tableContent[index][6] = new Integer( (obj.getNet()).getSynapseCount());
      tableContent[index][7] = new Integer(((obj.getNet()).getInputNeurons()).size());
      tableContent[index][8] = new Integer(((obj.getNet()).getOutputNeurons()).size());
      tableContent[index][9] = new Integer(((obj.getNet()).getHiddenNeurons()).size());
    }


    table = new JTable(tableContent, columnNames);
    table.setEnabled(true);
    table.setPreferredScrollableViewportSize(new Dimension(500, 700));
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    ListSelectionModel rowSM = table.getSelectionModel();
    rowSM.addListSelectionListener(this);

    //Create the scroll pane and add the table to it. 
    scrollPane = new JScrollPane(table);

    //Add the scroll pane to this window.
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 1;
    c.weightx = 0.5;
    c.gridwidth = 2;
    c.gridy = 1;
    c.weighty = 0.5;
    layout.setConstraints(scrollPane,c);  
    panel.add(scrollPane);

  }


  public void refresh(){
    try{

      EvoObjectList indies = pop.getParents();
      EvoObject     obj;
      int           index = 0;

      double        bestPerf;

      bestPerf = pop.bestPerformance();

      index=0;
      for(indies.start(); indies.hasMore(); indies.next())
      {
        obj = indies.object();

        tableContent[index][0] = new Integer(obj.getPIN());
        tableContent[index][1] = new Integer(obj.getAge());
        tableContent[index][2] = new Integer(obj.getCount());
        tableContent[index][3] = new String (String.valueOf(numberFormat.format(obj.getPerformance())));
        tableContent[index][4] = new String (String.valueOf(numberFormat.format(obj.getOutPerf())));
        /* number of input units and synapsis */
        tableContent[index][5] = new Integer(((obj.getNet()).neurons()).size());
        tableContent[index][6] = new Integer( (obj.getNet()).getSynapseCount());
        tableContent[index][7] = new Integer(((obj.getNet()).getInputNeurons()).size());
        tableContent[index][8] = new Integer(((obj.getNet()).getOutputNeurons()).size());
        tableContent[index][9] = new Integer(((obj.getNet()).getHiddenNeurons()).size());
        index++;
        if(index >= MAX_ROW) break;
      }

      for(int i = index;i<MAX_ROW;i++)
      {
        for(int j = 0; j<MAX_COL; j++)
        {
          tableContent[i][j] = new String (" ");
        }
      }
      table.repaint();



      /* statistics update */
      popNameMon.setText(pop.getName());
      if(this.evo.getGenNmb() <= 0)
      {
        genNmbMon.setText("initial structure(s)");
      }
      else
      {
        genNmbMon.setText(String.valueOf(this.evo.getGenNmb() - 1));
      }
      popSizeMon.setText(String.valueOf(pop.getCurrentPopSize()));
      maxPerfMon.setText(String.valueOf(numberFormat.format(pop.bestPerformance())));
      minPerfMon.setText(String.valueOf(numberFormat.format(pop.worstPerformance())));
      avgPerfMon.setText(String.valueOf(numberFormat.format(pop.avgPerformance())) + "   +/-   " +
          String.valueOf(numberFormat.format(pop.sqrtVarPerformance())));     
      avgOutPerfMon.setText(String.valueOf(numberFormat.format(pop.avgOutPerf())) + "   +/-   " +
          String.valueOf(numberFormat.format(pop.sqrtVarOutPerf())));     
      avgNmbHiddenMon.setText(String.valueOf(numberFormat.format(pop.avgNmbHidden())) + "   +/-  " +
          String.valueOf(numberFormat.format(java.lang.Math.sqrt(pop.varNmbHidden()))));
      avgNmbSynMon.setText(String.valueOf(numberFormat.format(pop.avgNmbSynapses())) + "   +/-   " +
          String.valueOf(numberFormat.format(java.lang.Math.sqrt(pop.varNmbSynapses()))));

      popIconLabel.setIcon(this.pop.getIcon());

    }
    catch(ArrayIndexOutOfBoundsException e)
    {
      System.out.println("NO COMPLETE REFRESH");; // 
    };
  }


  public JPanel getPanel(){
    return this.panel;
  }


  /* ActionListener method */
  public void actionPerformed(ActionEvent e){

    if(e.getActionCommand() == "NEW_TAB_VISIBLE"){
      System.out.println("neuer sichtbare popindex = " + evo.getPopIndex());
      this.pop   = (this.evo.getPopulations()).getPop(evo.getPopIndex());
      if(!this.evo.doEvolution()){
        this.refresh();
      }
    }

    /* update the net ranking list during the initializing of the evolutionary process */
    if((e.getSource() == this.pop) && (e.getActionCommand() == "INIT")){
      System.out.println("POPINIT");
      this.refresh();
    }

    if((e.getSource() == this.evo) && (e.getActionCommand() == "EVO_REFRESH")){
      this.refresh();
    }

  }


  /*  ListSelectionListener method */
  public void valueChanged(ListSelectionEvent e) {
    //Ignore extra messages.
    if (e.getValueIsAdjusting()) return;

    ListSelectionModel lsm = (ListSelectionModel)e.getSource();
    if (lsm.isSelectionEmpty()) 
    {
      System.out.println("No rows are selected.");
    } 
    else 
    {
      int selectedRow = lsm.getMinSelectionIndex();
      System.out.print("Row " + selectedRow
          + " is now selected.");

      NetTreeView net = new NetTreeView(NetTreeView.MODE_VIEW_NET);
      try{
        EvoObjectList p = this.pop.getParents();
        EvoObject     o = p.object(selectedRow);
        net.updateView(o.getNet());
        System.out.println("Net-PIN: " + o.getPIN());
      }
      catch(ArrayIndexOutOfBoundsException s)
      {         
        System.out.println("(NO NET)");
        if (selectedRow < this.pop.getParents().size())
        {
          System.out.println("(But there should be one net. Please call immediately Martin!)");
        }
      }
      System.out.println();
    }
  }

}










