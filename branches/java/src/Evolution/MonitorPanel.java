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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;



public class MonitorPanel implements ComponentListener, ActionListener{
    private Evolution   evo = null;
    private Population  pop        = null;
    private NumberFormat numberFormat = NumberFormat.getInstance(Locale.UK);

    private JPanel      panel      = new JPanel(false);
    
    private DrawPanel   drawPanel  = new DrawPanel();

    private JPanel     popIconPanel    = new JPanel();
    private JLabel     popIconLabel    = new JLabel();

    private int        plotQual = 0;


    private JCheckBox  autoScaleBox   = new JCheckBox("auto. scale");
    private boolean    autoScale      = true;

    private JLabel     genStartLab    = new JLabel("plot from gen.: ");
    private JTextField genStartLabInp = new JTextField(4);
    private JLabel     genEndLab    = new JLabel(" to: ");
    private JTextField genEndLabInp = new JTextField(4);

    private JLabel     space        = new JLabel("  ");

    private JLabel     yEndLab    = new JLabel("y max. : ");
    private JTextField yEndLabInp = new JTextField(4);
    private JLabel     yStartLab    = new JLabel("y min. : ");
    private JTextField yStartLabInp = new JTextField(4);


    private int        genStart     = 0;   
    private int        genEnd       = 0; 
    private double[]   yStartArray  = new double[10]; 
    private double[]   yEndArray    = new double[10];
    /*
    private double     yStart       = 0.0; 
    private double     yEnd         = 0.0;
    */



    public MonitorPanel(Evolution evolution){
  this.evo  = evolution;
  this.pop   = this.evo.getPopulations().getPop(evo.getPopIndex());

  PopulationList pops = evo.getPopulations();
  Population p = null;
  for(pops.start(); pops.hasMore(); pops.next())
  {
      p = pops.currentPop();
      p.setMonitorPanel(this);
  }

  this.evo.setMonitorPanel(this);

  this.numberFormat.setMinimumFractionDigits(2);
  this.numberFormat.setMaximumFractionDigits(2);

  GridBagLayout      layout = new GridBagLayout();
  GridBagConstraints c      = new GridBagConstraints();
  this.panel.setLayout(layout);

  this.panel.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createTitledBorder("population dynamics"),
      BorderFactory.createEmptyBorder(5,5,5,5)));

  /* pop logo */
  popIconLabel.setIcon(this.pop.getIcon());
  popIconPanel.add(popIconLabel);
  c.fill = GridBagConstraints.HORIZONTAL;
  c.anchor = GridBagConstraints.NORTHWEST;
  c.gridx = 0;
  c.gridy = 0;
  c.weighty = 0.5;
  layout.setConstraints(popIconPanel,c);
  panel.add(popIconPanel);



  /* auto scale check box */
  if(autoScale)
  {
      autoScaleBox.setSelected(true);
  }
  else
  {
      autoScaleBox.setSelected(false);
  }
  autoScaleBox.addActionListener(this);
  c.anchor = GridBagConstraints.NORTH;
  c.gridx = 0;
  c.gridy = 1;
  c.weighty = 0.0;
  c.weightx = 0.0;
  layout.setConstraints(autoScaleBox,c);
  panel.add(autoScaleBox);
  
  /* space */
  c.gridx = 0;
  c.gridy = 2;
  layout.setConstraints(space,c);
  panel.add(space);



  /* set y-border panels */
  c.anchor = GridBagConstraints.NORTH;
  c.gridx = 0;
  c.gridy = 3;
  c.weighty = 0.0;
  c.weightx = 0.0;
  layout.setConstraints(yEndLab,c);
  panel.add(yEndLab);

  c.gridx = 0;
  c.gridy = 4;
  layout.setConstraints(yEndLabInp,c);
  panel.add(yEndLabInp);
  yEndLabInp.addActionListener(this);
  yEndLabInp.setText(Double.toString(this.yEndArray[plotQual]));



  c.gridx = 0;
  c.gridy = 5;
  c.weightx = 0.0;
  c.weighty = 0.0;
  layout.setConstraints(yStartLab,c);
  panel.add(yStartLab);

  c.gridx = 0;
  c.gridy = 6;
  layout.setConstraints(yStartLabInp,c);
  panel.add(yStartLabInp);
  yStartLabInp.addActionListener(this);
  yStartLabInp.setText(Double.toString(this.yStartArray[plotQual]));



  /* space */
  c.gridx = 0;
  c.gridy = 7;
  layout.setConstraints(space,c);
  panel.add(space);


  /* set-gerneration-border panels */
  c.anchor = GridBagConstraints.NORTH;
  c.gridx = 0;
  c.gridy = 8;
  c.weightx = 0.0;
  c.weighty = 0.0;
  layout.setConstraints(genStartLab,c);
  panel.add(genStartLab);

  c.gridx = 0;
  c.gridy = 9;
  layout.setConstraints(genStartLabInp,c);
  panel.add(genStartLabInp);
  genStartLabInp.addActionListener(this);
  genStartLabInp.setText(Integer.toString(this.genStart));

  c.gridx = 0;
  c.gridy = 10;
  layout.setConstraints(genEndLab,c);
  panel.add(genEndLab);

  c.gridx = 0;
  c.gridy = 11;
  layout.setConstraints(genEndLabInp,c);
  panel.add(genEndLabInp);
  genEndLabInp.addActionListener(this);
  genEndLabInp.setText(Integer.toString(this.genEnd));



  /* drawing panel */
  c.fill = GridBagConstraints.BOTH;
  c.gridx = 1;
  c.gridy = 0;
  c.weightx = 0.5;
  c.weighty = 0.5;
  c.gridheight = 12;
  layout.setConstraints(drawPanel,c);
  panel.add(drawPanel);
  drawPanel.addComponentListener(this);

    }


    public void init(){
  /* init draw panel */

  drawPanel.init(this);

  PopDyn pd = this.pop.getPopDyn();
  if(pd.size() == 0)
  {
      drawPanel.setRange(0,(double)this.evo.getGenNmb(),0.0, 0.0);
  }
  else
  {
      plotQual = 0;
      this.drawPanel.setRange(0, (double) this.evo.getGenNmb(), pd.getLowestMaxPerf(), pd.getHighestMaxPerf());
  }


  drawPanel.drawLegend();

    }
    
    public void refresh(){
  drawPanel.init(this);

  popIconLabel.setIcon(this.pop.getIcon());

  yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStartArray[plotQual])));
  yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEndArray[plotQual])));

  /* */
  if(plotQual == 9)
  {
      drawPopSize();
  }

  if(plotQual == 8)
  {
      drawAgeOldest();
  }

  if(plotQual == 7)
  {
      drawAvgAge();
  }

  if(plotQual == 6)
  {
      drawAgeBest();
  }
  if(plotQual == 5)
  {
      drawAvgNmbSynapses();
  }
  
  if(plotQual == 4)
  {
      drawNmbSynapsesBest();
  }
  
  if(plotQual == 3)
  {
      drawAvgNmbHidden();
  }

  if(plotQual == 2)
  {
      drawNmbHiddenBest();
  }

  if(plotQual == 1)
  {
      drawAvgPerf();
  }

  if(plotQual == 0)
  {
      drawBestPerf();
  }
    }

    public JPanel getPanel(){
  return this.panel;
    }

    
    /* drawing methods */


    private void drawBestPerf(){
  Color color = null;
  PopState ps_n0 = null;
  PopState ps_n1 = null;
  double uppestLim, lowestLim;


  /* get time sequence of performance  */

  PopDyn pd = this.pop.getPopDyn();
  if(pd.size() == 0)
  {
      this.drawPanel.drawLegend();
      return;
  }

  /* Y range calculation */
  if(pd.getLowestMaxPerf() < pd.getLowestMaxOutPerf())
  {
      lowestLim = (double) pd.getLowestMaxPerf();
  }
  else
  {
      lowestLim = (double) pd.getLowestMaxOutPerf();
  }

  if(pd.getHighestMaxPerf() > pd.getHighestMaxOutPerf())
  {
      uppestLim = (double) pd.getHighestMaxPerf();
  }
  else
  {
      uppestLim = (double) pd.getHighestMaxOutPerf();
  }

  if(autoScale){
      this.yStartArray[plotQual] = lowestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStartArray[plotQual])));
      this.yEndArray[plotQual] = uppestLim;
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEndArray[plotQual])));
  }

  /*
  if(this.yStart > lowestLim)
  {
      this.yStart = lowestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStart)));
  }
  
  if(this.yEnd < uppestLim)
  {
      this.yEnd = uppestLim;
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEnd)));
  }
  */

  if(this.yEndArray[plotQual] < this.yStartArray[plotQual])
  {
      this.yStartArray[plotQual] = lowestLim;
      this.yEndArray[plotQual] = uppestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStartArray[plotQual])));
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEndArray[plotQual])));
  }



  //this.drawPanel.setRange(0, (double) this.evo.getGenNmb(), pd.getLowestMaxPerf(), pd.getHighestMaxPerf());
  this.drawPanel.setRange((double) this.genStart, (double) this.genEnd, this.yStartArray[plotQual], this.yEndArray[plotQual] );
  this.drawPanel.drawLegend();

  ps_n0 = null;
  ps_n1 = null;
  color = new Color((float)0.5,(float)0.0,(float)0.0);
  //for(int i = 1; i < (pd.size()); i++)
  for(int i = this.genStart; i < this.genEnd; i++)
  {
      ps_n0 = pd.state(i);
      ps_n1 = pd.state(i+1);
      this.drawPanel.drawBigPoint(((double)i), ps_n0.getMaxOutPerf());
      this.drawPanel.drawBigPoint(((double)i+1), ps_n1.getMaxOutPerf());
      this.drawPanel.drawLine(((double)i),ps_n0.getMaxOutPerf(),((double) i+1),  ps_n1.getMaxOutPerf(), color);
  }

  color = new Color((float)0.0,(float)0.5,(float)0.0);
  //for(int i = 1; i < (pd.size()); i++)
  for(int i = this.genStart; i < this.genEnd; i++)
  {
      ps_n0 = pd.state(i);
      ps_n1 = pd.state(i+1);
      this.drawPanel.drawBigPoint(((double)i), ps_n0.getMaxSysPerf());
      this.drawPanel.drawBigPoint(((double)i+1), ps_n1.getMaxSysPerf());
      this.drawPanel.drawLine(((double)i),ps_n0.getMaxSysPerf(),((double) i+1),  ps_n1.getMaxSysPerf(), color);
  }


    }


    private void drawAvgPerf(){
  Color darkColor = new Color((float)0.0,(float)0.5,(float)0.0);
  Color brightColor = new Color((float)0.0,(float)0.7,(float)0.0);
  double lowestLim, uppestLim;
  
  /* get time sequence of performance  */

  PopDyn pd = this.pop.getPopDyn();
  if(pd.size() == 0)
  {
      return;
  }

  /* set y range */
  lowestLim = (pd.getLowestAvgSysPerf() - pd.getHighestVarSysPerf());
  uppestLim = (pd.getHighestAvgSysPerf() + pd.getHighestVarSysPerf());

  if(autoScale  || ( (this.yStartArray[plotQual] == 0 ) &&  (this.yEndArray[plotQual] == 0) ) ){
      this.yStartArray[plotQual] = lowestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStartArray[plotQual])));
      this.yEndArray[plotQual] = uppestLim;
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEndArray[plotQual])));
  }

  /*
  if(this.yStart > lowestLim)
  {
      this.yStart = lowestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStart)));
  }
  
  if(this.yEnd < uppestLim)
  {
      this.yEnd = uppestLim;
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEnd)));
  }
  */

  if(this.yEndArray[plotQual] < this.yStartArray[plotQual])
  {
      this.yStartArray[plotQual] = lowestLim;
      this.yEndArray[plotQual] = uppestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStartArray[plotQual])));
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEndArray[plotQual])));
  }



  // this.drawPanel.setRange(0, (double) this.evo.getGenNmb(), 
  //      (pd.getLowestAvgSysPerf() - pd.getHighestVarSysPerf()), 
  //      (pd.getHighestAvgSysPerf() + pd.getHighestVarSysPerf()) );
  this.drawPanel.setRange((double) this.genStart, (double) this.genEnd, 
        this.yStartArray[plotQual],this.yEndArray[plotQual]);
  this.drawPanel.drawLegend();

  PopState ps_n0 = null;
  PopState ps_n1 = null;
  for(int i = this.genStart; i < this.genEnd; i++)
  {
      ps_n0 = pd.state(i);
      ps_n1 = pd.state(i+1);
      /* average */
      this.drawPanel.drawBigPoint(((double)i), ps_n0. getAvgSysPerf());
      this.drawPanel.drawBigPoint(((double)i+1), ps_n1.getAvgSysPerf());
      this.drawPanel.drawFatLine(((double)i),ps_n0.getAvgSysPerf(),((double) i+1),  ps_n1.getAvgSysPerf(), darkColor);

      /* average + variance */
      this.drawPanel.drawBigPoint(((double)i), ps_n0.getAvgSysPerf() + ps_n0.getVarSysPerf());
      this.drawPanel.drawBigPoint(((double)i+1), ps_n1.getAvgSysPerf() + ps_n1.getVarSysPerf());
      this.drawPanel.drawLine(((double)i),ps_n0.getAvgSysPerf()+ ps_n0.getVarSysPerf(),
            ((double) i+1),  ps_n1.getAvgSysPerf() + ps_n1.getVarSysPerf(), brightColor);

      /* average - variance */
      this.drawPanel.drawBigPoint(((double)i), ps_n0.getAvgSysPerf() - ps_n0.getVarSysPerf());
      this.drawPanel.drawBigPoint(((double)i+1), ps_n1.getAvgSysPerf() - ps_n1.getVarSysPerf());
      this.drawPanel.drawLine(((double)i),ps_n0.getAvgSysPerf() - ps_n0.getVarSysPerf(),
            ((double) i+1),  ps_n1.getAvgSysPerf() - ps_n1.getVarSysPerf(), brightColor);

    
  }
    }


    private void drawPopSize(){
  Color color = null; 
  double lowestLim, uppestLim; 

  /* get time sequence  */
  PopDyn pd = this.pop.getPopDyn();
  if(pd.size() == 0)
  {
      return;
  }

  /* set y range */
  lowestLim = 0.0;
  uppestLim = pd.getHighestPopSize();
  if(this.yStartArray[plotQual] > lowestLim)
  {
      this.yStartArray[plotQual] = lowestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStartArray[plotQual])));
  }

  if(autoScale || ( (this.yStartArray[plotQual] == 0 ) &&  (this.yEndArray[plotQual] == 0) )){
      this.yStartArray[plotQual] = lowestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStartArray[plotQual])));
      this.yEndArray[plotQual] = uppestLim;
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEndArray[plotQual])));
  }
  
  /*
  if(this.yEnd < uppestLim)
  {
      this.yEnd = uppestLim;
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEnd)));
  }

  if(this.yEnd < this.yStart)
  {
      this.yStart = lowestLim;
      this.yEnd = uppestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStart)));
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEnd)));
  }
  */



  this.drawPanel.setRange((double) this.genStart, (double) this.genEnd, 
        this.yStartArray[plotQual], this.yEndArray[plotQual]);
  this.drawPanel.drawLegend();

  // total pop. size
  color = new Color((float)1.0,(float)0.5,(float)0.0);
  PopState ps_n0 = null;
  PopState ps_n1 = null;
  for(int i = this.genStart; i < this.genEnd; i++)
  {
      ps_n0 = pd.state(i);
      ps_n1 = pd.state(i+1);
      this.drawPanel.drawBigPoint(((double)i), ps_n0.getCurrentPopSize());
      this.drawPanel.drawBigPoint(((double)i+1), ps_n1.getCurrentPopSize());
      this.drawPanel.drawLine(((double)i),ps_n0.getCurrentPopSize(),((double) i+1),  ps_n1.getCurrentPopSize(), color);
  }

  // number of offsprings 
  color = new Color((float)1.0,(float)0.0,(float)0.0);
  ps_n0 = null;
  ps_n1 = null;
  for(int i = this.genStart; i < this.genEnd; i++)
  {
      ps_n0 = pd.state(i);
      ps_n1 = pd.state(i+1);
      this.drawPanel.drawBigPoint(((double)i), ps_n0.getCurrentOffsprings());
      this.drawPanel.drawBigPoint(((double)i+1), ps_n1.getCurrentOffsprings());
      this.drawPanel.drawLine(((double)i),ps_n0.getCurrentOffsprings(),((double) i+1),  ps_n1.getCurrentOffsprings(), color);
  }

  // number of parents
  color = new Color((float)0.0,(float)0.5,(float)0.0);
  ps_n0 = null;
  ps_n1 = null;
  for(int i = this.genStart; i < this.genEnd; i++)
  {
      ps_n0 = pd.state(i);
      ps_n1 = pd.state(i+1);
      this.drawPanel.drawBigPoint(((double)i),  ps_n0.getCurrentPopSize() - ps_n0.getCurrentOffsprings());
      this.drawPanel.drawBigPoint(((double)i+1),  ps_n1.getCurrentPopSize() - ps_n1.getCurrentOffsprings());
      this.drawPanel.drawLine(((double)i), ps_n0.getCurrentPopSize() - ps_n0.getCurrentOffsprings(),
            ((double) i+1),   ps_n1.getCurrentPopSize() - ps_n1.getCurrentOffsprings(), color);
  }


    }



    private void drawNmbHiddenBest(){
  Color color = new Color((float)0.0,(float)0.0,(float)0.5);
  double lowestLim, uppestLim; 

  /* get time sequence  */
  PopDyn pd = this.pop.getPopDyn();
  if(pd.size() == 0)
  {
      return;
  }

  /* set y range */
  lowestLim = (pd.getLowestNmbHiddenBest());
  uppestLim = (pd.getHighestNmbHiddenBest());

  if(autoScale || ( (this.yStartArray[plotQual] == 0 ) &&  (this.yEndArray[plotQual] == 0) )){
      this.yStartArray[plotQual] = lowestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStartArray[plotQual])));
      this.yEndArray[plotQual] = uppestLim;
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEndArray[plotQual])));
  }

  /*
  if(this.yStart > lowestLim)
  {
      this.yStart = lowestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStart)));
  }
  
  if(this.yEnd < uppestLim)
  {
      this.yEnd = uppestLim;
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEnd)));
  }
  */

  if(this.yEndArray[plotQual] < this.yStartArray[plotQual])
  {
      this.yStartArray[plotQual] = lowestLim;
      this.yEndArray[plotQual] = uppestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStartArray[plotQual])));
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEndArray[plotQual])));
  }



  this.drawPanel.setRange((double) this.genStart, (double) this.genEnd, 
        this.yStartArray[plotQual], this.yEndArray[plotQual]);
  this.drawPanel.drawLegend();

  PopState ps_n0 = null;
  PopState ps_n1 = null;
  for(int i = this.genStart; i < this.genEnd; i++)
  {
      ps_n0 = pd.state(i);
      ps_n1 = pd.state(i+1);
      this.drawPanel.drawBigPoint(((double)i), ps_n0.getNmbHiddenBest());
      this.drawPanel.drawBigPoint(((double)i+1), ps_n1.getNmbHiddenBest());
      this.drawPanel.drawLine(((double)i),ps_n0.getNmbHiddenBest(),((double) i+1),  ps_n1.getNmbHiddenBest(), color);
  }
    }



    private void drawAvgNmbHidden(){
  Color darkColor = new Color((float)0.0,(float)0.0,(float)0.5);
  Color brightColor = new Color((float)0.0,(float)0.0,(float)0.7);
  double lowestLim, uppestLim;

  /* get time sequence */

  PopDyn pd = this.pop.getPopDyn();
  if(pd.size() == 0)
  {
      return;
  }


  /* set y range */
  lowestLim = (pd.getLowestAvgNmbHidden() - pd.getHighestVarNmbHidden());
  uppestLim = (pd.getHighestAvgNmbHidden() + pd.getHighestVarNmbHidden());
  
  if(autoScale || ( (this.yStartArray[plotQual] == 0 ) &&  (this.yEndArray[plotQual] == 0) )){
      this.yStartArray[plotQual] = lowestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStartArray[plotQual])));
      this.yEndArray[plotQual] = uppestLim;
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEndArray[plotQual])));
  }
  
  /*
  if(this.yStart > lowestLim)
  {
      this.yStart = lowestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStart)));
  }
  
  if(this.yEnd < uppestLim)
  {
      this.yEnd = uppestLim;
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEnd)));
  }
  */

  if(this.yEndArray[plotQual] < this.yStartArray[plotQual])
  {
      this.yStartArray[plotQual] = lowestLim;
      this.yEndArray[plotQual] = uppestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStartArray[plotQual])));
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEndArray[plotQual])));
  }



  this.drawPanel.setRange((double) this.genStart, (double) this.genEnd, 
        this.yStartArray[plotQual], this.yEndArray[plotQual] );
  this.drawPanel.drawLegend();

  PopState ps_n0 = null;
  PopState ps_n1 = null;
  for(int i = this.genStart; i < this.genEnd; i++)
  {
      ps_n0 = pd.state(i);
      ps_n1 = pd.state(i+1);
      /* average */
      this.drawPanel.drawBigPoint(((double)i), ps_n0. getAvgNmbHidden());
      this.drawPanel.drawBigPoint(((double)i+1), ps_n1.getAvgNmbHidden());
      this.drawPanel.drawFatLine(((double)i),ps_n0.getAvgNmbHidden(),((double) i+1),  ps_n1.getAvgNmbHidden(), darkColor);

      /* average + variance */
      this.drawPanel.drawBigPoint(((double)i), ps_n0.getAvgNmbHidden() + ps_n0.getVarNmbHidden());
      this.drawPanel.drawBigPoint(((double)i+1), ps_n1.getAvgNmbHidden() + ps_n1.getVarNmbHidden());
      this.drawPanel.drawLine(((double)i),ps_n0.getAvgNmbHidden()+ ps_n0.getVarNmbHidden(),
            ((double) i+1),  ps_n1.getAvgNmbHidden() + ps_n1.getVarNmbHidden(), brightColor);

      /* average - variance */
      this.drawPanel.drawBigPoint(((double)i), ps_n0.getAvgNmbHidden() - ps_n0.getVarNmbHidden());
      this.drawPanel.drawBigPoint(((double)i+1), ps_n1.getAvgNmbHidden() - ps_n1.getVarNmbHidden());
      this.drawPanel.drawLine(((double)i),ps_n0.getAvgNmbHidden() - ps_n0.getVarNmbHidden(),
            ((double) i+1),  ps_n1.getAvgNmbHidden() - ps_n1.getVarNmbHidden(), brightColor);

    
  }
    }

    private void drawNmbSynapsesBest(){
  Color color = new Color((float)0.7,(float)0.7,(float)0.0);
  double lowestLim, uppestLim;

  /* get time sequence  */
  PopDyn pd = this.pop.getPopDyn();
  if(pd.size() == 0)
  {
      return;
  }

  /* set y range */
  lowestLim = pd.getLowestNmbSynapsesBest();
  uppestLim = pd.getHighestNmbSynapsesBest();

  if(autoScale || ( (this.yStartArray[plotQual] == 0 ) &&  (this.yEndArray[plotQual] == 0) )){
      this.yStartArray[plotQual] = lowestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStartArray[plotQual])));
      this.yEndArray[plotQual] = uppestLim;
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEndArray[plotQual])));
  }

  /*
  if(this.yStart > lowestLim)
  {
      this.yStart = lowestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStart)));
  }
  
  if(this.yEnd < uppestLim)
  {
      this.yEnd = uppestLim;
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEnd)));
  }
  */

  if(this.yEndArray[plotQual] < this.yStartArray[plotQual])
  {
      this.yStartArray[plotQual] = lowestLim;
      this.yEndArray[plotQual] = uppestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStartArray[plotQual])));
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEndArray[plotQual])));
  }




  this.drawPanel.setRange((double) this.genStart, (double) this.genEnd, 
        this.yStartArray[plotQual], this.yEndArray[plotQual]);
  this.drawPanel.drawLegend();

  PopState ps_n0 = null;
  PopState ps_n1 = null;
  for(int i = this.genStart; i < this.genEnd; i++)
  {
      ps_n0 = pd.state(i);
      ps_n1 = pd.state(i+1);
      this.drawPanel.drawBigPoint(((double)i), ps_n0.getNmbSynapsesBest());
      this.drawPanel.drawBigPoint(((double)i+1), ps_n1.getNmbSynapsesBest());
      this.drawPanel.drawLine(((double)i),ps_n0.getNmbSynapsesBest(),((double) i+1),  ps_n1.getNmbSynapsesBest(), color);
  }
    }


    private void drawAvgNmbSynapses(){
  Color darkColor = new Color((float)0.5,(float)0.5,(float)0.0);
  Color brightColor = new Color((float)0.7,(float)0.7,(float)0.0);
  double lowestLim, uppestLim;

  /* get time sequence */

  PopDyn pd = this.pop.getPopDyn();
  if(pd.size() == 0)
  {
      return;
  }

  /* set y range */
  lowestLim = (pd.getLowestAvgNmbSynapses() - pd.getHighestVarNmbSynapses());
  uppestLim = (pd.getHighestAvgNmbSynapses() + pd.getHighestVarNmbSynapses());

  if(autoScale || ( (this.yStartArray[plotQual] == 0 ) &&  (this.yEndArray[plotQual] == 0) )){
      this.yStartArray[plotQual] = lowestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStartArray[plotQual])));
      this.yEndArray[plotQual] = uppestLim;
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEndArray[plotQual])));
  }

  /*
  if(this.yStart > lowestLim)
  {
      this.yStart = lowestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStart)));
  }
  
  if(this.yEnd < uppestLim)
  {
      this.yEnd = uppestLim;
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEnd)));
  }
  */

  if(this.yEndArray[plotQual] < this.yStartArray[plotQual])
  {
      this.yStartArray[plotQual] = lowestLim;
      this.yEndArray[plotQual] = uppestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStartArray[plotQual])));
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEndArray[plotQual])));
  }




  this.drawPanel.setRange((double)this.genStart, (double) this.genEnd, 
        this.yStartArray[plotQual], this.yEndArray[plotQual]);
  this.drawPanel.drawLegend();

  PopState ps_n0 = null;
  PopState ps_n1 = null;
  for(int i = this.genStart; i < this.genEnd; i++)
  {
      ps_n0 = pd.state(i);
      ps_n1 = pd.state(i+1);
      /* average */
      this.drawPanel.drawBigPoint(((double)i), ps_n0. getAvgNmbSynapses());
      this.drawPanel.drawBigPoint(((double)i+1), ps_n1.getAvgNmbSynapses());
      this.drawPanel.drawFatLine(((double)i),ps_n0.getAvgNmbSynapses(),
               ((double) i+1),  ps_n1.getAvgNmbSynapses(), darkColor);

      /* average + variance */
      this.drawPanel.drawBigPoint(((double)i), ps_n0.getAvgNmbSynapses() + ps_n0.getVarNmbSynapses());
      this.drawPanel.drawBigPoint(((double)i+1), ps_n1.getAvgNmbSynapses() + ps_n1.getVarNmbSynapses());
      this.drawPanel.drawLine(((double)i),ps_n0.getAvgNmbSynapses()+ ps_n0.getVarNmbSynapses(),
            ((double) i+1),  ps_n1.getAvgNmbSynapses() + ps_n1.getVarNmbSynapses(), brightColor);

      /* average - variance */
      this.drawPanel.drawBigPoint(((double)i), ps_n0.getAvgNmbSynapses() - ps_n0.getVarNmbSynapses());
      this.drawPanel.drawBigPoint(((double)i+1), ps_n1.getAvgNmbSynapses() - ps_n1.getVarNmbSynapses());
      this.drawPanel.drawLine(((double)i),ps_n0.getAvgNmbSynapses() - ps_n0.getVarNmbSynapses(),
            ((double) i+1),  ps_n1.getAvgNmbSynapses() - ps_n1.getVarNmbSynapses(), brightColor);

    
  }
    }

    private void drawAgeOldest(){
  Color color = new Color((float)0.3,(float)0.3,(float)0.3);
  double lowestLim, uppestLim;

  /* get time sequence  */
  PopDyn pd = this.pop.getPopDyn();
  if(pd.size() == 0)
  {
      return;
  }

  /* set y range */
  lowestLim = pd.getLowestAgeOldest();
  uppestLim = pd.getHighestAgeOldest();

  if(autoScale || ( (this.yStartArray[plotQual] == 0 ) &&  (this.yEndArray[plotQual] == 0) )){
      this.yStartArray[plotQual] = lowestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStartArray[plotQual])));
      this.yEndArray[plotQual] = uppestLim;
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEndArray[plotQual])));
  }

  /*
  if(this.yStart > lowestLim)
  {
      this.yStart = lowestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStart)));
  }
  
  if(this.yEnd < uppestLim)
  {
      this.yEnd = uppestLim;
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEnd)));
  }
  */

  if(this.yEndArray[plotQual] < this.yStartArray[plotQual])
  {
      this.yStartArray[plotQual] = lowestLim;
      this.yEndArray[plotQual] = uppestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStartArray[plotQual])));
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEndArray[plotQual])));
  }



  this.drawPanel.setRange((double) this.genStart, (double) this.genEnd, 
        this.yStartArray[plotQual], this.yEndArray[plotQual]);
  this.drawPanel.drawLegend();

  PopState ps_n0 = null;
  PopState ps_n1 = null;
  for(int i = this.genStart; i < this.genEnd; i++)
  {
      ps_n0 = pd.state(i);
      ps_n1 = pd.state(i+1);
      this.drawPanel.drawBigPoint(((double)i), ps_n0.getAgeOldest());
      this.drawPanel.drawBigPoint(((double)i+1), ps_n1.getAgeOldest());
      this.drawPanel.drawLine(((double)i),ps_n0.getAgeOldest(),((double) i+1),  ps_n1.getAgeOldest(), color);
  }
    }

    private void drawAgeBest(){
  Color color = new Color((float)0.5,(float)0.5,(float)0.5);
  double lowestLim, uppestLim;

  /* get time sequence  */
  PopDyn pd = this.pop.getPopDyn();
  if(pd.size() == 0)
  {
      return;
  }


  /* set y range */
  lowestLim = pd.getLowestAgeBest();
  uppestLim = pd.getHighestAgeBest();

  if(autoScale || ( (this.yStartArray[plotQual] == 0 ) &&  (this.yEndArray[plotQual] == 0) )){
      this.yStartArray[plotQual] = lowestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStartArray[plotQual])));
      this.yEndArray[plotQual] = uppestLim;
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEndArray[plotQual])));
  }

  /*
  if(this.yStart > lowestLim)
  {
      this.yStart = lowestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStart)));
  }
  
  if(this.yEnd < uppestLim)
  {
      this.yEnd = uppestLim;
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEnd)));
  }
  */

  if(this.yEndArray[plotQual] < this.yStartArray[plotQual])
  {
      this.yStartArray[plotQual] = lowestLim;
      this.yEndArray[plotQual] = uppestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStartArray[plotQual])));
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEndArray[plotQual])));
  }



  this.drawPanel.setRange((double) this.genStart, (double) this.genEnd, 
        this.yStartArray[plotQual], this.yEndArray[plotQual]);
  this.drawPanel.drawLegend();

  PopState ps_n0 = null;
  PopState ps_n1 = null;
  for(int i = this.genStart; i < this.genEnd; i++)
  {
      ps_n0 = pd.state(i);
      ps_n1 = pd.state(i+1);
      this.drawPanel.drawBigPoint(((double)i), ps_n0.getAgeBest());
      this.drawPanel.drawBigPoint(((double)i+1), ps_n1.getAgeBest());
      this.drawPanel.drawLine(((double)i),ps_n0.getAgeBest(),((double) i+1),  ps_n1.getAgeBest(), color);
  }
    }




    private void drawAvgAge(){
  Color darkColor = new Color((float)0.5,(float)0.5,(float)0.5);
  Color brightColor = new Color((float)0.7,(float)0.7,(float)0.7);
  double lowestLim, uppestLim;

  /* get time sequence */

  PopDyn pd = this.pop.getPopDyn();
  if(pd.size() == 0)
  {
      return;
  }



  /* set y range */
  lowestLim = (pd.getLowestAvgAge() - pd.getHighestVarAge());
  uppestLim = (pd.getHighestAvgAge() + pd.getHighestVarAge());

  if(autoScale || ( (this.yStartArray[plotQual] == 0 ) &&  (this.yEndArray[plotQual] == 0) )){
      this.yStartArray[plotQual] = lowestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStartArray[plotQual])));
      this.yEndArray[plotQual] = uppestLim;
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEndArray[plotQual])));
  }
  
  /*
  if(this.yStart > lowestLim)
  {
      this.yStart = lowestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStart)));
  }
  
  if(this.yEnd < uppestLim)
  {
      this.yEnd = uppestLim;
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEnd)));
  }
  */

  if(this.yEndArray[plotQual] < this.yStartArray[plotQual])
  {
      this.yStartArray[plotQual] = lowestLim;
      this.yEndArray[plotQual] = uppestLim;
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStartArray[plotQual])));
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEndArray[plotQual])));
  }



  this.drawPanel.setRange((double) this.genStart, (double) this.genEnd, 
        this.yStartArray[plotQual], this.yEndArray[plotQual]);
  this.drawPanel.drawLegend();

  PopState ps_n0 = null;
  PopState ps_n1 = null;
  for(int i = this.genStart; i < this.genEnd; i++)
  {
      ps_n0 = pd.state(i);
      ps_n1 = pd.state(i+1);
      /* average */
      this.drawPanel.drawBigPoint(((double)i), ps_n0. getAvgAge());
      this.drawPanel.drawBigPoint(((double)i+1), ps_n1.getAvgAge());
      this.drawPanel.drawFatLine(((double)i),ps_n0.getAvgAge(),
               ((double) i+1),  ps_n1.getAvgAge(), darkColor);

      /* average + variance */
      this.drawPanel.drawBigPoint(((double)i), ps_n0.getAvgAge() + ps_n0.getVarAge());
      this.drawPanel.drawBigPoint(((double)i+1), ps_n1.getAvgAge() + ps_n1.getVarAge());
      this.drawPanel.drawLine(((double)i),ps_n0.getAvgAge()+ ps_n0.getVarAge(),
            ((double) i+1),  ps_n1.getAvgAge() + ps_n1.getVarAge(), brightColor);

      /* average - variance */
      this.drawPanel.drawBigPoint(((double)i), ps_n0.getAvgAge() - ps_n0.getVarAge());
      this.drawPanel.drawBigPoint(((double)i+1), ps_n1.getAvgAge() - ps_n1.getVarAge());
      this.drawPanel.drawLine(((double)i),ps_n0.getAvgAge() - ps_n0.getVarAge(),
            ((double) i+1),  ps_n1.getAvgAge() - ps_n1.getVarAge(), brightColor);
   
  }
    }



    private void setGenStart(int intVal){
  this.genStart = intVal;
  
  if(this.genStart > (this.evo.getGenNmb() - 3))
  {
      this.genStart = 0;
  }
  
  if(this.genStart < 0)
  {
      this.genStart = 0;
  }
  
  if(this.genStart > this.genEnd - 3)
  {
      this.genStart = 0;
  }
    }

    private void setGenEnd(int intVal){
  if(intVal <= 0)
  {
      this.genEnd = this.evo.getGenNmb();
  }
  else
  {
      this.genEnd = intVal;
  }
  
  if( (this.genEnd - 3) < this.genStart)
  {
      this.genEnd = this.evo.getGenNmb() - 1; 
  }
  
  if( this.genEnd > (this.evo.getGenNmb() - 1) )
  {
      this.genEnd = this.evo.getGenNmb() - 1;
  }
    }

    private void setYStart(double dblVal){
  this.yStartArray[plotQual] = dblVal;
 
  if(this.yStartArray[plotQual] > this.yEndArray[plotQual]){
      this.yStartArray[plotQual] = this.yEndArray[plotQual];
  }
    }

    private void setYEnd(double dblVal){
  this.yEndArray[plotQual] = dblVal;
  
  if(this.yStartArray[plotQual] > this.yEndArray[plotQual]){
      this.yEndArray[plotQual] = this.yStartArray[plotQual];
  }
    }



    /* ActionListener method */
    public void actionPerformed(ActionEvent e){
  int intVal;
  double dblVal;

  if(! (this.evo.getMonitorWin()).isVisible() )
  {
    return ;
  }

  if(e.getSource() == this.autoScaleBox)
  {
      if(autoScaleBox.isSelected())
      {
    autoScale = true;
      }
      else
      {
    autoScale = false;
      }
      refresh();
  }


  if(e.getSource() == this.genStartLabInp){
      intVal = Integer.parseInt(genStartLabInp.getText());
      setGenStart(intVal);
      genStartLabInp.setText(Integer.toString(this.genStart));
      this.refresh();
  }

  if(e.getSource() == this.genEndLabInp){
      intVal = Integer.parseInt(genEndLabInp.getText());
      setGenEnd(intVal);
      genEndLabInp.setText(Integer.toString(this.genEnd));
      this.refresh();
  }
  
  if(e.getSource() == this.yStartLabInp){
      dblVal = Double.parseDouble(yStartLabInp.getText());
      setYStart(dblVal);
      yStartLabInp.setText(String.valueOf(numberFormat.format(this.yStartArray[plotQual])));
      this.refresh();
  }

  if(e.getSource() == this.yEndLabInp){
      dblVal = Double.parseDouble(yEndLabInp.getText());
      setYEnd(dblVal);
      yEndLabInp.setText(String.valueOf(numberFormat.format(this.yEndArray[plotQual])));
      this.refresh();
  }




  if(e.getActionCommand() == "NEW_POP_VISIBLE"){
      this.pop   = (this.evo.getPopulations()).getPop(evo.getPopIndex());
      if(!this.evo.doEvolution()){
    this.refresh();
    this.pop.popUpdate();
      }
      // System.out.println("NEW_POP_VISIBLE");
  }

  /* update the monitor during the initializing of the evolutionary process */
  if((e.getSource() == this.pop) && (e.getActionCommand() == "INIT")){
      // System.out.println("monPOPINIT");
      this.genStart = 0;
      this.genEnd = 0;
      genEndLabInp.setText(Integer.toString(this.genEnd));
      genStartLabInp.setText(Integer.toString(this.genStart));
      this.refresh();
  }

  if((e.getSource() == this.evo) && (e.getActionCommand() == "EVO_REFRESH")){
      // System.out.println("evoRefresh");

      setGenEnd(this.evo.getGenNmb());
      // be careful with the following line, because the activation 
      // of generation counter is crucial (see evoStep() method in class 
      // Evolution ), I know, that the following line
      // will not produce a out of range error - but you!!!
      genEndLabInp.setText(Integer.toString(this.genEnd));

      if(this.genStart != 0){
    setGenStart(this.genStart + 1);
    genStartLabInp.setText(Integer.toString(this.genStart));
    
      }
      this.refresh();
  }

  if(e.getSource() == drawPanel){
      if(e.getActionCommand() ==  "SHOW_MAX_PERF")
      {
    plotQual = 0;
      }

      if(e.getActionCommand() ==  "SHOW_AVG_PERF")
      {
    plotQual = 1;
      }

      if(e.getActionCommand() ==  "SHOW_MAX_HIDD")
      {
    plotQual = 2;
      }

      if(e.getActionCommand() ==  "SHOW_AVG_HIDD")
      {
    plotQual = 3;
      }

      if(e.getActionCommand() ==  "SHOW_MAX_SYN")
      {
    plotQual = 4;
      }

      if(e.getActionCommand() ==  "SHOW_AVG_SYN")
      {
    plotQual = 5;
      }

      if(e.getActionCommand() ==  "SHOW_AGE_BEST")
      {
    plotQual = 6;
      }

      if(e.getActionCommand() ==  "SHOW_AVG_AGE")
      {
    plotQual = 7;
      }

      if(e.getActionCommand() ==  "SHOW_VET_AGE")
      {
    plotQual = 8;
      }

      if(e.getActionCommand() ==  "SHOW_POP_SIZE")
      {
    plotQual = 9;
      }

      this.refresh();
  }

  if( (e.getSource() == drawPanel) &&
      (e.getActionCommand() ==  "SAVE_TXT") )
  {
      PrintWriter  out        = null;
      JFileChooser fileDialog = new JFileChooser(".");
      int          returnVal  = fileDialog.showSaveDialog(this.drawPanel);
      
      if(returnVal == JFileChooser.APPROVE_OPTION) 
      {
    File file =  fileDialog.getSelectedFile(); 
    try 
    {
        
        out = new PrintWriter(
      new OutputStreamWriter(
          new FileOutputStream(file.getPath())));
    }
    catch (IOException ev)
    {
        ev.printStackTrace();
    }

    /* write */
    out.print((this.pop.getPopDyn()).toString());
    
    /* close */
    out.close();
      }
      
  }



    }

    public void componentHidden(ComponentEvent e){
    }
    public void componentShown(ComponentEvent e){
    }
    public void componentResized(ComponentEvent e){
  refresh();
    }
    public void componentMoved(ComponentEvent e){
    }


}

