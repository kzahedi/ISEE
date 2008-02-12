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

/*
 * Created on 06.05.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package hinton.analyser.netstructure.graphlayouter;

import hinton.analyser.netstructure.GraphLayouter;
import hinton.analyser.netstructure.NeuronRenderer;
import hinton.analyser.netstructure.VisualNet;
import hinton.analyser.toolkit.InputValue;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import cholsey.Net;
import cholsey.Synapse;
import cholsey.SynapseList;


/**
 * @author rosemann
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Genetic extends GraphLayouter
                     implements Runnable
{
  private InputValue.IInteger sizePopulation;                     
  private InputValue.IInteger countGenerations;
  private InputValue.IInteger minDist;
  private InputValue.IDouble  mutationPropability;
  private InputValue.IDouble  crossoverPropability;
  private InputValue.IInteger selectionPercentage;
  private InputValue.IBoolean currLayoutInitPop;
  private InputValue[]        properties;
  private Population          population;
  private Edge[]              edges;
  private int                 synapseCount;
  private int[][]             floydDistance;
  private double[]               averageDistance;
  private JDialog             progressDialog;
  private JProgressBar        pgbGenerations;
  private JLabel              lblFirstGen;
  private VisualNet           visualNet;
  private boolean             keepOnRunning;
  
  /* (non-Javadoc)
   * @see Hinton.analyser.netstructure.GraphLayouter#layoutGraph(Hinton.analyser.netstructure.VisualNet)
   */
  protected void layoutGraph(VisualNet visualNet)
  {
    if (visualNet.getNet().size() == 0)
    {
      return;
    }
    Thread t = new Thread(this);
    this.visualNet = visualNet;
    this.createDialog(visualNet);
    this.keepOnRunning = true;
    t.start();
    this.progressDialog.setVisible(true);
    this.progressDialog.show();
  }
  /* (non-Javadoc)
   * @see Hinton.analyser.netstructure.GraphLayouter#computeInitialState(Hinton.analyser.netstructure.VisualNet)
   */
  protected void computeInitialState(VisualNet visualNet) {
    int              neuronCount = visualNet.getNet().size();
    int              src, dest;
    Net              net           = visualNet.getNet();
    SynapseList      synapses      = new SynapseList();
    Synapse          current;
    LinkedList       dmyEdges      = new LinkedList();
    Edge             currEdge;
    NeuronRenderer   nr;
    int              maxDist = 1;
    
    this.floydDistance = new int[neuronCount][neuronCount];
    for (int i = 0; i < neuronCount; i++)
    {
      for (int j = 0; j < neuronCount; j++)
      {
        this.floydDistance[i][j] = (i == j ? 0 : neuronCount + 1);
      }
    }
      
    
    this.population = new Population();
    this.population.individual = new Individual[this.sizePopulation.value];
    
    if (this.currLayoutInitPop.value)
    {
      for (int i = 0; i < this.population.individual.length; i++)
      {
        this.population.individual[i] = new Individual();
        this.population.individual[i].neuronCount     = neuronCount;
        this.population.individual[i].neuronPos       = new Genetic.Node[neuronCount];
        for (int j = 0; j < neuronCount; j++)
        {
          this.population.individual[i].neuronPos[j] = new Genetic.Node();
        }
      }
      for (int j = 0; j < neuronCount; j++)
      {
        nr = visualNet.getNeuronRenderer(j);
        this.population.individual[0].neuronPos[j].x = (double)nr.getX();
        this.population.individual[0].neuronPos[j].y = (double)nr.getY();
      }
      for (int j = 1; j < (int)(this.sizePopulation.value * 0.5d); j++)
      {
        this.getMutation(this.population.individual[j - 1],
            this.population.individual[j]);
      }
      this.getNextGeneration();
    }
    else
    {
      for (int i = 0; i < this.population.individual.length; i++)
      {
        
        this.population.individual[i] = new Individual();
        this.population.individual[i].neuronCount     = neuronCount;
        this.population.individual[i].neuronPos       = new Genetic.Node[neuronCount];
        this.computeStartPosition(this.population.individual[i],
            visualNet);
      }
    }
    
    for (int i = 0; i < net.size(); i++)
    {
      if (net.getNeuron(i).synapses() != null)
      {
        synapses.addAll(net.getNeuron(i).synapses());
      }
    }
    this.synapseCount = synapses.size();
  
    
    for (int i = 0; i < synapses.size(); i++)
    {
      current  = synapses.next();
      src      = current.getSource().id();
      dest     = current.getDestination().id();
      
      
      if (src != dest)
      {
        currEdge = new Edge();
        currEdge.from = src;
        currEdge.to   = dest;
        dmyEdges.add(currEdge);
        this.floydDistance[src][dest] = this.floydDistance[dest][src] = 1;
      }
    }
    this.edges = (Edge[])dmyEdges.toArray(new Edge[0]);
    
    for (int k = 0; k < neuronCount; k++)
    {
      for (int  i = 0; i < neuronCount; i++)
      {
        for (int j = 0; j < neuronCount; j++)
        {
          this.floydDistance[i][j] = Math.min(this.floydDistance[i][j],
              this.floydDistance[i][k] + this.floydDistance[k][j]);
          if (this.floydDistance[i][j] != neuronCount + 1 &&
            this.floydDistance[i][j] > maxDist)
          {
            maxDist = this.floydDistance[i][j];
          }
        }
      }
    }
    maxDist++;
    for (int i = 0; i < neuronCount; i++)
    {
      for (int j = 0; j < neuronCount; j++)
      {
        if (this.floydDistance[i][j] == neuronCount + 1)
        {
          this.floydDistance[i][j] = maxDist;
        }
      }
    }
    
    
    
  }
  /* (non-Javadoc)
   * @see Hinton.analyser.netstructure.GraphLayouter#getLayoutName()
   */
  public String getLayoutName() {
    return "Genetic Algorithm";
  }
  /* (non-Javadoc)
   * @see Hinton.analyser.netstructure.GraphLayouter#getProperties()
   */
  protected InputValue[] getProperties() {
    if (this.sizePopulation == null)
    {
      this.sizePopulation = new InputValue.IInteger("Populationsize",
                              50,
                              10, 500);
      this.countGenerations = 
                          new InputValue.IInteger("Number of Generations",
                              100,
                              10, 1000000);
      this.minDist = new InputValue.IInteger("Minimal Synapse length",
                                             100,
                                             100, 300);
      this.selectionPercentage = new InputValue.IInteger("Selection %",
                                                         10,
                                                         1, 90);
      this.mutationPropability = new InputValue.IDouble("Mutation propability",
                                                        0.25d,
                                                        0.1d, 1.0d);
      this.crossoverPropability = new InputValue.IDouble(
                                                "Crossover probability",
                                                0.85d,
                                                0.1d, 1.0d);
      
      this.currLayoutInitPop = new InputValue.IBoolean(
                                        "Current Layout in startpopulation",
                                        false);

      this.properties = new InputValue[] {
                                          this.currLayoutInitPop,
                                          this.minDist, 
                                          this.sizePopulation,
                                          this.countGenerations,
                                          this.selectionPercentage,
                                          this.mutationPropability,
                                          this.crossoverPropability};
      
    }
    return this.properties;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  public void run() {
    double lastFitness = Double.MAX_VALUE;
    for (int i = 0; i < this.countGenerations.value &&
                    this.keepOnRunning; i++)
    {
      this.pgbGenerations.setString("Generation " + i);
      for (int j = 0; j < this.sizePopulation.value; j++)
      {
        this.calculateFitness(population.individual[j]);
      }
      Arrays.sort(population.individual, new Genetic.IndividualComparator());
      this.population.fitness = (population.individual[0].fitness);
      
      if (lastFitness > this.population.fitness)
      {
        this.lblFirstGen.setText(
            "Fittest Individual first occurs in Generation " + i);
        lastFitness = this.population.fitness;
      }
      
      if ((i + 1) % 100 == 0)
      {
        this.doRealMove(this.population.individual[0], this.visualNet);
      }
      
      if (i < this.countGenerations.value -1)
      {
        this.getNextGeneration();
      }
      
      
      this.progressDialog.pack();
    }
    this.doRealMove(this.population.individual[0], this.visualNet); 
    this.progressDialog.setVisible(false);
    this.progressDialog.dispose();
    
  }
  
  private void createDialog(VisualNet visNet)
  {
    GridBagConstraints gbc = new GridBagConstraints();
    JButton btn            = new JButton("Cancel");
    
    this.progressDialog = new JDialog(JOptionPane.getFrameForComponent(visNet),
                                      "Progress", true );
    this.progressDialog.getContentPane().setLayout(new GridBagLayout());
    this.pgbGenerations = new JProgressBar(0, this.countGenerations.value);
    this.pgbGenerations.setValue(0);
    this.pgbGenerations.setBorderPainted(true);
    this.pgbGenerations.setString("Generation 0");
    this.pgbGenerations.setStringPainted(true);
    
    gbc.gridx     = 0;
    gbc.gridy     = 0;
    gbc.gridwidth = 2;
    gbc.fill      = GridBagConstraints.HORIZONTAL;
    gbc.weightx   = 1.0d;
    this.progressDialog.getContentPane().add(this.pgbGenerations, gbc);
    
    this.lblFirstGen = new JLabel(
        "Fittest Individual first occurs in Generation 0");
    gbc.gridx     = 0;
    gbc.gridy     = 1;
    gbc.gridwidth = 2;
    gbc.fill      = GridBagConstraints.HORIZONTAL;
    gbc.weightx   = 1.0d;
    this.progressDialog.getContentPane().add(this.lblFirstGen, gbc);
    
    btn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e)
      {
        keepOnRunning = false;
      }
    });
    gbc.gridx     = 1;
    gbc.gridy     = 2;
    gbc.gridwidth = 1;
    gbc.fill      = GridBagConstraints.HORIZONTAL;
    gbc.weightx   = 0.5d;
    this.progressDialog.getContentPane().add(btn, gbc);
    this.progressDialog.pack();
  }
  
  private void doRealMove(Individual ind, VisualNet visualNet)
  {
    NeuronRenderer current;
    int px, py;
    
    for (int i = 0; i < ind.neuronCount; i++)
    {
      px = (int)ind.neuronPos[i].x;
      py = (int)ind.neuronPos[i].y;
      current = visualNet.getNeuronRenderer(i);
      current.setLocation(px, py);
    }
  }
  
  /**
   * Randomly generates an individual
   * @param ind
   * @param visualNet
   */
  private void computeStartPosition(Individual ind, VisualNet visualNet)
  {   
    for (int i = 0; i < ind.neuronCount; i++)
    {
      ind.neuronPos[i]   = new Genetic.Node();
      ind.neuronPos[i].x = Math.random() * 1000;
      ind.neuronPos[i].y = Math.random() * 1000;
    }
    
  }
  
  private final void getNextGeneration()
  {
    int n = (int)(Math.ceil(this.sizePopulation.value * 
                            this.selectionPercentage.value / 100));
    int j = n;
    int i, k, l;
    LinkedList selectedIndividuals = new LinkedList();
    

    j = n;
    for (i = 0; i < n && j < this.population.individual.length; i++)
    {
      if (Math.random() < this.mutationPropability.value)
      {
         this.getMutation(
            this.population.individual[i], this.population.individual[j]);
        j++;
      }
    }
    
    for (i = 0; i < n && j < this.sizePopulation.value; i++)
    {
      for (k = 0; k < n && j < this.sizePopulation.value; k++)
      {
        if (k != i && Math.random() < this.crossoverPropability.value)
        {
          l = (j+1 < this.sizePopulation.value ? j + 1 : j);
            
          this.getChildren(this.population.individual[i],
                          this.population.individual[k],
                          this.population.individual[j],
                          this.population.individual[l]);
          j += 2;
        }
      }
    }
    
  }
  
  /**
   * Returns a mutation of the given individual. Mutation is 
   * either done by random movement of one vertice or swapping
   * of the coordinates of two vertices
   * 
   * @param individual the individual to be mutated
   * @param mutation of the given individual
   */
  private final void getMutation(Individual individual, Individual mutation)
  {
    int neuronCount          = individual.neuronCount;
    int i, j;
    
    mutation.neuronCount = neuronCount;
    mutation.fitness     = -1;
    for (i = 0; i < neuronCount; i++)
    {
      mutation.neuronPos[i].x = individual.neuronPos[i].x;
      mutation.neuronPos[i].y = individual.neuronPos[i].y;
    } 
    
    if (Math.random() < 0.5d)
    {
      //Randomly alter the position of one vertice
      i = (int)(Math.random() * neuronCount);
      
      mutation.neuronPos[i].x += 2 * this.minDist.value * 
                                (Math.random() -  0.5d);
      mutation.neuronPos[i].y += 2 * this.minDist.value *
                                (Math.random() -  0.5d);
    }
    else
    {
      //Swap the positions of two vertices
      i = (int)(Math.random() * neuronCount);
      j = (int)(Math.random() * neuronCount);
      mutation.neuronPos[i].x = individual.neuronPos[j].x;
      mutation.neuronPos[i].y = individual.neuronPos[j].y;
      mutation.neuronPos[j].x = individual.neuronPos[i].x;
      mutation.neuronPos[j].y = individual.neuronPos[i].y;
    }
  }
  
  /**
   * Fitness is calculated by the amount of edge intersections and the
   * divergence of edgelength to the desired length
   * @param ind the individual to calculate the fitness for
   */
  private final void calculateFitness(Individual ind)
  {
    double edgefitness = 0.0d;
    double p1x, p1y, p2x, p2y;
    double currDist;
    double desDist;
    int    from, to;
    
    if (ind.fitness == -1)
    {
      
      for (int i = 0; i < ind.neuronCount; i++)
      {
        for (int j = i + 1; j < ind.neuronCount; j++)
        {
          p1x = ind.neuronPos[i].x;
          p1y = ind.neuronPos[i].y;
          p2x = ind.neuronPos[j].x;
          p2y = ind.neuronPos[j].y;
          currDist = Point2D.distance(p1x, p1y, p2x, p2y);
          desDist  = (double)floydDistance[i][j];
          edgefitness += (1.0d / (desDist * desDist)) *
            ((currDist / this.minDist.value - desDist) *
                (currDist / this.minDist.value - desDist));
        }
      }
      ind.fitness = edgefitness;
    }
  }
  

  
  /**
   * Returns the two "children" of p1 and p2 generated by the Cycle Crossover
   * known from the Path Representation of the Travelling Salesman Problem
   * for Genetic Algorithm
   * 
   * @param p1 a parent individual
   * @param p2 another parent indidvidual
   * @param c1 used as return parameter for child 1
   * @param c2 used as return parameter for child 2
   */
  private final void getChildren(Individual p1, Individual p2,
                          Individual c1, Individual c2)
  {
    int neuronCount     = p1.neuronCount;
    int positionCount   = 2 * neuronCount;
    int crossOverPoint  = (int)(Math.random() * neuronCount);
    int i,j, k;
    double p;
    
    c1.neuronCount = neuronCount;
    c2.neuronCount = neuronCount;
    c1.fitness     = -1;
    c2.fitness     = -1;
    
    for (i = 0; i < neuronCount; i++)
    {
      c1.neuronPos[i].x = p1.neuronPos[i].x;
      c1.neuronPos[i].y = p1.neuronPos[i].y;
      
      c2.neuronPos[i].x = p2.neuronPos[i].x;
      c2.neuronPos[i].y = p2.neuronPos[i].y;
    }
    
    p = Math.random();
         if (p <= 0.33d)
    {
      
      c1.neuronPos[crossOverPoint].x = p2.neuronPos[crossOverPoint].x;
      c1.neuronPos[crossOverPoint].y = p2.neuronPos[crossOverPoint].y;
    
      c2.neuronPos[crossOverPoint].x = p1.neuronPos[crossOverPoint].x;
      c2.neuronPos[crossOverPoint].y = p1.neuronPos[crossOverPoint].y;
    }
    else if (p <= 0.66d)
    {
      i = crossOverPoint;
      j = (int)(Math.random() * neuronCount);
      
      c1.neuronPos[i].x += ((p1.neuronPos[j].x - p1.neuronPos[i].x) +
                            (p2.neuronPos[j].x - p2.neuronPos[i].x)) / 2.0d;
      c1.neuronPos[i].y += ((p1.neuronPos[j].y - p1.neuronPos[i].y) +
                            (p2.neuronPos[j].y - p2.neuronPos[i].y)) / 2.0d;
      
      c2.neuronPos[j].x += ((p1.neuronPos[i].x - p1.neuronPos[j].x) +
                            (p2.neuronPos[i].x - p2.neuronPos[j].x)) / 2.0d;
      c2.neuronPos[j].y += ((p1.neuronPos[i].y - p1.neuronPos[j].y) +
                            (p2.neuronPos[i].y - p2.neuronPos[j].y)) / 2.0d;
    }
    else
    {
      Integer[] rang1 = new Integer[neuronCount];
      Integer[] rang2 = new Integer[neuronCount];
      java.util.BitSet from1 = new java.util.BitSet();
      java.util.BitSet from2 = new java.util.BitSet();
      
      for (i = 0; i < neuronCount; i++)
      {
        rang1[i] = new Integer(i);
        rang2[i] = new Integer(i);
      }

      java.util.Arrays.sort(rang1, new GradientNodeSort(p1.neuronPos));
      java.util.Arrays.sort(rang2, new GradientNodeSort(p2.neuronPos));

      j = 0;
      k = neuronCount - 1;
      int id;
      for (i = 0; i < neuronCount; i++)
      {
        if (i % 2 == 0)
        {
          while (from2.get(rang1[j].intValue()))
          {
            j++;
          }
          id = rang1[j].intValue();
          c1.neuronPos[id].x = p1.neuronPos[id].x;
          c1.neuronPos[id].y = p1.neuronPos[id].y;
          c2.neuronPos[id].x = p2.neuronPos[id].x;
          c2.neuronPos[id].y = p2.neuronPos[id].y;
          from1.set(id);
          j++;
        }
        else
        {
          while (from1.get(rang2[k].intValue()))
          {
            k--;
          }
          id = rang2[k].intValue();
          c1.neuronPos[id].x = p2.neuronPos[id].x;
          c1.neuronPos[id].y = p2.neuronPos[id].y;
          c2.neuronPos[id].x = p1.neuronPos[id].x;
          c2.neuronPos[id].y = p1.neuronPos[id].y;
          from2.set(id);
          k--;
        }
      }
    }
    
      
  
  }
  
  private class Population
  {
    public Individual[] individual;
    public double fitness = -1;
  }
  
  private class Individual
  {
    public Genetic.Node[] neuronPos;
    public int    neuronCount;
    public double fitness = -1;
    
    public Individual()
    {
      this.fitness = -1;
    }
    
    public String toString()
    {
      String ret = "";
      for (int i = 0; i < neuronPos.length; i++)
      {
        ret += "(" + neuronPos[i].x + "," + neuronPos[i].y + ") ";
      }
      return ret;
    }
  }
  
  private class Edge
  {
    int from;
    int to;
  }
  
  private class Node
  {
    double x;
    double y;

  }
  
  private class IndividualComparator implements Comparator
  {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object o1, Object o2) {
      Individual i1 = (Individual)o1;
      Individual i2 = (Individual)o2;
      
           if (i1.fitness == i2.fitness)
      {
        return 0;
      }
      else if (i1.fitness > i2.fitness)
      {
        return 1;
      }
      else
      {
        return -1;
      }
    }
    
  }
  
  private class NodeComparator implements Comparator
  {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object o1, Object o2) {
      Genetic.Node n1 = (Genetic.Node)o1;
      Genetic.Node n2 = (Genetic.Node)o2;
      double s1, s2;
      
      s1 = n1.x + n1.y;
      s2 = n2.x + n2.y;
      
           if (s1 == s2)
      {
        return 0;
      }
      else if (s1 >  s2)
      {
        return 1;
      }
      else
      {
        return -1;
      }
    }
    
  }
  
  public static class GradientNodeSort implements Comparator
  {
    Genetic.Node[] nodes;

    GradientNodeSort(Genetic.Node[] n) { nodes = n; };
    public int compare(Object o1, Object o2)
    {
      Genetic.Node n1 = nodes[((Integer)o1).intValue()];
      Genetic.Node n2 = nodes[((Integer)o2).intValue()];

      Double d1 = new Double(n1.x + n1.y);
      Double d2 = new Double(n2.x + n2.y);

      return d1.compareTo(d2);
    }
  }


}
