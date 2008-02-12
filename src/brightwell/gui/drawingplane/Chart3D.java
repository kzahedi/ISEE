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

package brightwell.gui.drawingplane;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RefineryUtilities;

public class Chart3D extends JFrame
{
  private DefaultCategoryDataset defaultcategorydataset = null;

  public Chart3D(String s)
  {
    super(s);
    defaultcategorydataset = new DefaultCategoryDataset();
  }

  private CategoryDataset createDataset()
  {
    return defaultcategorydataset;
  }

  public void addValue(double d, String category, String data)
  {
    defaultcategorydataset.addValue(d, category, data);
//    defaultcategorydataset.addValue(0.1, "Degree", "Neuron 1");
//    defaultcategorydataset.addValue(0.2, "Degree", "Neuron 2");
//    defaultcategorydataset.addValue(0.4, "Closeness", "Neuron 1");
//    defaultcategorydataset.addValue(0.2, "Closeness", "Neuron 2");
//    defaultcategorydataset.addValue(0.2, "Betweenness", "Neuron 1");
//    defaultcategorydataset.addValue(0.9, "Betweenness", "Neuron 2");

  }

  public void doneValues()
  {
    CategoryDataset categorydataset = createDataset();
    JFreeChart jfreechart = createChart(categorydataset);
    ChartPanel chartpanel = new ChartPanel(jfreechart);
    chartpanel.setPreferredSize(new Dimension(500, 270));
    setContentPane(chartpanel);
  }

  private JFreeChart createChart(CategoryDataset categorydataset)
  {
    JFreeChart jfreechart = ChartFactory.createBarChart3D("Centrality", "Neuron", "Value", categorydataset, PlotOrientation.VERTICAL, true, true, false);
    CategoryPlot categoryplot = jfreechart.getCategoryPlot();
    CategoryAxis categoryaxis = categoryplot.getDomainAxis();
    categoryaxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(0.39269908169872414D));
    CategoryItemRenderer categoryitemrenderer = categoryplot.getRenderer();
    categoryitemrenderer.setItemLabelsVisible(true);
    BarRenderer barrenderer = (BarRenderer)categoryitemrenderer;
    barrenderer.setMaxBarWidth(0.050000000000000003D);
    return jfreechart;
  }

  public JPanel createDemoPanel()
  {
    JFreeChart jfreechart = createChart(createDataset());
    return new ChartPanel(jfreechart);
  }

  public static void main(String args[])
  {
    Chart3D barchart3ddemo3 = new Chart3D("3D Bar Chart3D Demo 3");
    barchart3ddemo3.pack();
    RefineryUtilities.centerFrameOnScreen(barchart3ddemo3);
    barchart3ddemo3.setVisible(true);
  }
}
