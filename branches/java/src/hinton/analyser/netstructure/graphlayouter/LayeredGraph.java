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
 * Created on 19.04.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package hinton.analyser.netstructure.graphlayouter;

import hinton.analyser.netstructure.GraphLayouter;
import hinton.analyser.netstructure.NeuronRenderer;
import hinton.analyser.netstructure.VisualNet;
import hinton.analyser.toolkit.InputValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import cholsey.Net;
import cholsey.NeuronList;
import cholsey.Synapse;
import cholsey.SynapseList;



/**
 * @author rosemann
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class LayeredGraph extends GraphLayouter
{
  private static final Integer GREEDY_SWITCH_CR = new Integer(0);
  private static final Integer BARYCENTER_CR    = new Integer(1);
  private static final Integer HORIZONTAL_ORI   = new Integer(2);
  private static final Integer VERTICAL_ORI     = new Integer(3);


  private Node[]       nodes;
  private Edge[]       edges;
  private ArrayList[]  src;
  private ArrayList[]  dest;
  private int          layerCount;
  private ArrayList[]  layers;
  private InputValue.IObject  crossingReduction;
  private InputValue.IObject  orientation;
  private InputValue.IInteger horizontalDist;
  private InputValue.IInteger verticalDist;
  private InputValue.IBoolean forceInputOutputLayer;

  private InputValue[] properties;
  private Net          net;

  /* (non-Javadoc)
   * @see Hinton.analyser.netstructure.GraphLayouter#layoutGraph(Hinton.analyser.netstructure.VisualNet)
   */
  protected void layoutGraph(VisualNet visualNet) {
    this.net = visualNet.getNet();
    this.computeLayerAssignment();
    this.computeCrossingReduction();
    this.computeYCoordinates();
    this.makeMove(visualNet);
  }

  /* (non-Javadoc)
   * @see Hinton.analyser.netstructure.GraphLayouter#computeInitialState(Hinton.analyser.netstructure.VisualNet)
   */
  protected void computeInitialState(VisualNet visualNet)
  {
    Net         net      = visualNet.getNet();
    SynapseList synapses = new SynapseList();
    Synapse     current;
    LinkedList  dmyEdges = new LinkedList();
    Edge        edge;
    int         i;
    this.src   = new ArrayList[net.size()];
    this.dest  = new ArrayList[net.size()];
    this.nodes = new Node[net.size()];
    for (i = 0; i < net.size(); i++)
    {
      this.src[i]   = new ArrayList();
      this.dest[i]  = new ArrayList();
      this.nodes[i] = new Node();
      if (net.getNeuron(i).synapses() != null)
      {
        synapses.addAll(net.getNeuron(i).synapses());
      }
    }

    for (synapses.start(); synapses.hasMore(); synapses.next())
    {
      current = synapses.synapse();
      edge = new Edge();
      if ((edge.from = current.getSource().id()) != 
          (edge.to = current.getDestination().id()))
      {

        this.src[edge.from].add(new Integer(edge.to));
        this.dest[edge.to].add(new Integer(edge.from));
        dmyEdges.addLast(edge);
      }
    }
    this.edges = (Edge[])dmyEdges.toArray(new Edge[0]);

    this.computeAcyclicGraph();
  }

  /* (non-Javadoc)
   * @see Hinton.analyser.netstructure.GraphLayouter#getLayoutName()
   */
  public String getLayoutName() {
    return "Layered Drawing";
  }

  /* (non-Javadoc)
   * @see Hinton.analyser.netstructure.GraphLayouter#getProperties()
   */
  protected InputValue[] getProperties() {
    if (this.horizontalDist == null)
    {
      this.horizontalDist = new InputValue.IInteger("Horizontal distance",
          100,
          100, 400);
      this.verticalDist = new InputValue.IInteger("Vertical distance",
          100,
          100, 400);
      this.crossingReduction = new InputValue.IObject("Crossing Reduction algorithm",
          0,
          new Integer[] { LayeredGraph.GREEDY_SWITCH_CR,
            LayeredGraph.BARYCENTER_CR },
            new String[] { "Greedy switch", "Barycenter" });

      this.orientation = new InputValue.IObject("Layer orientation",
          0,
          new Integer[] { LayeredGraph.VERTICAL_ORI, LayeredGraph.HORIZONTAL_ORI },
          new String[] { "vertical", "horizontal" });




      this.forceInputOutputLayer = new InputValue.IBoolean(
          "Force Layer for Input/Output Neurons",
          false);
      this.properties = new InputValue[] {
        this.orientation,
          this.crossingReduction,
          this.horizontalDist,
          this.verticalDist,
          this.forceInputOutputLayer};
    }

    return this.properties;
  }

  private void addEdge(Edge edge)
  {
    ArrayList newedges = new ArrayList(Arrays.asList(this.edges));

    this.src[edge.from].add(new Integer(edge.to));
    this.dest[edge.to].add(new Integer(edge.from));

    newedges.add(edge);
    this.edges = (Edge[])newedges.toArray(new Edge[0]);
  }

  private void removeEdge(Edge edge)
  {
    ArrayList newedges = new ArrayList(Arrays.asList(this.edges));

    this.src[edge.from].remove(new Integer(edge.to));
    this.dest[edge.to].add(new Integer(edge.from));

    newedges.remove(edge);
    this.edges = (Edge[])newedges.toArray(new Edge[0]);
  }

  private void addNode(Node node)
  {
    ArrayList newnodes = new ArrayList(Arrays.asList(this.nodes));
    ArrayList newsrc   = new ArrayList(Arrays.asList(this.src));
    ArrayList newdest  = new ArrayList(Arrays.asList(this.dest));

    newnodes.add(node);
    newsrc.add(new ArrayList());
    newdest.add(new ArrayList());

    this.nodes = (Node[])newnodes.toArray(new Node[0]);
    this.src   = (ArrayList[])newsrc.toArray(new ArrayList[0]);
    this.dest  = (ArrayList[])newdest.toArray(new ArrayList[0]);
  }

  private void removeNode(int idx)
  {
    Integer   value    = new Integer(idx);
    ArrayList newnodes = new ArrayList(Arrays.asList(this.nodes));
    ArrayList newsrc   = new ArrayList(Arrays.asList(this.src));
    ArrayList newdest  = new ArrayList(Arrays.asList(this.dest));

    newnodes.remove(idx);
    newsrc.remove(idx);
    newdest.remove(idx);

    this.nodes = (Node[])newnodes.toArray(new Node[0]);
    this.src   = (ArrayList[])newsrc.toArray(new ArrayList[0]);
    this.dest  = (ArrayList[])newdest.toArray(new ArrayList[0]);

    for (int i = 0; i < this.src.length; i++)
    {
      this.src[i].remove(value);
    }
    for (int i = 0; i < this.dest.length; i++)
    {
      this.dest[i].remove(value);
    }
  }

  /**
   * computes a maximum acyclic graph of the given graph by using
   * a greedy heuristic
   */
  private void computeAcyclicGraph()
  {
    int           i, j;
    int           pNode;
    int           maxDegree;
    int           swap;
    Edge          edge;
    BitSet        toProcess   = new BitSet(this.nodes.length);
    ArrayList[][] degreeOutIn = new ArrayList[this.nodes.length][2];

    for (i = 0; i < this.nodes.length; i++)
    {
      degreeOutIn[i][0] = new ArrayList();
      degreeOutIn[i][1] = new ArrayList();
    }

    for (i = 0; i < this.edges.length; i++)
    {
      edge = this.edges[i];
      degreeOutIn[edge.from][0].add(edge);
      degreeOutIn[edge.to][1].add(edge);
    }

    toProcess.set(0, this.nodes.length);

    while (!toProcess.isEmpty())
    {
      maxDegree = Integer.MIN_VALUE;
      pNode     = toProcess.nextSetBit(0);

      for (j = toProcess.nextSetBit(0); j >= 0; j = toProcess.nextSetBit(j + 1))
      {
        if (maxDegree < degreeOutIn[j][0].size())
        {
          maxDegree = degreeOutIn[j][0].size();
          pNode     = j;
        }
        if (maxDegree < degreeOutIn[j][1].size())
        {
          maxDegree = degreeOutIn[j][1].size();
          pNode     = j;
        }
      }
      toProcess.clear(pNode);

      if (degreeOutIn[pNode][0].size() >= degreeOutIn[pNode][1].size())
      {
        swap = 1;
      }
      else
      {
        swap = 0;
      }

      for (i = 0; i < degreeOutIn[pNode][0].size(); i++)
      {
        edge = (Edge)degreeOutIn[pNode][0].get(i);

        for (j = 0; j < this.nodes.length; j++)
        {
          if (j != i)
          {
            degreeOutIn[j][1].remove(edge);
          }
        }

        if (swap == 0)
        {
          this.removeEdge(edge);
        }
      }

      for (i = 0; i < degreeOutIn[pNode][1].size(); i++)
      {
        edge = (Edge)degreeOutIn[pNode][1].get(i);

        for (j = 0; j < this.nodes.length; j++)
        {
          if (j != i)
          {
            degreeOutIn[j][0].remove(edge);
          }
        }

        if (swap == 1)
        {
          this.removeEdge(edge);
        }
      }
    }
  }

  /**
   * 
   *
   */
  private void computeLayerAssignment()
  {
    int        i, j, k, l;
    int        piMax;
    int        quantity = this.nodes.length;
    int        pi[]     = new int[quantity];
    ArrayList  candidates = new ArrayList();
    ArrayList  oldlayers;
    int        candidate;
    NeuronList nl;
    BitSet     u, v_wo_u, current, copy, layerSetUnion, candidateNPlus;

    for (i = 0; i < quantity; i++)
    {
      pi[i] = quantity + 1;
    }

    for (i = 0; i < quantity; i++)
    {
      candidates.clear();
      for (j = 0; j < quantity; j++)
      {
        if (pi[j] == quantity + 1)
        {
          candidates.add(new Integer(j));
        }
      }
      candidate = ((Integer)Collections.min(candidates,
            new SourceDistanceComparator(pi, this.dest))).intValue();
      pi[candidate] = i + 1;
    }

    u = new BitSet(quantity);
    u.set(0, quantity);
    v_wo_u = new BitSet(quantity);
    v_wo_u.clear();
    layerSetUnion = new BitSet(quantity);
    layerSetUnion.clear();
    k = 0;
    l = 0;
    this.layers = new ArrayList[1];
    this.layers[0] = new ArrayList();
    candidateNPlus = new BitSet();

    if (this.forceInputOutputLayer.value)
    {
      nl = this.net.getOutputNeurons();
      if (nl != null && nl.size() != 0)
      {
        for (i = 0; i < nl.size(); i++)
        {
          this.layers[0].add(new Integer(nl.neuron(i).id()));
          this.nodes[nl.neuron(i).id()].layer = 0;
          this.nodes[nl.neuron(i).id()].y     = i;
          v_wo_u.set(nl.neuron(i).id());
          u.clear(nl.neuron(i).id());
          layerSetUnion.set(nl.neuron(i).id());
        }
        oldlayers = new ArrayList(Arrays.asList(this.layers));
        oldlayers.add(new ArrayList());
        this.layers = (ArrayList[])oldlayers.toArray(new ArrayList[0]);
        k++;
      }
      nl = this.net.getInputNeurons();
      if (nl != null && nl.size() != 0)
      {
        for (i = 0; i < nl.size(); i++)
        {
          u.clear(nl.neuron(i).id());
        }
      }
    }

    while (!u.isEmpty())
    {
      piMax = -1;
      candidate = u.nextSetBit(0);
      for (i = u.nextSetBit(0); i >= 0; i = u.nextSetBit(i+1))
      {
        current = new BitSet(quantity);
        for (j = 0; j < this.src[i].size(); j++)
        {
          current.set(((Integer)this.src[i].get(j)).intValue());
        }
        copy = (BitSet)current.clone();
        copy.and(v_wo_u);
        if (copy.equals(current) && pi[i] > piMax)
        {
          candidate = i;
          candidateNPlus = (BitSet)current.clone();
          piMax     = pi[i];
        }
      }

      copy = (BitSet)candidateNPlus.clone();
      copy.and(layerSetUnion);

      /* Check wether all Nodes incident by the edges of
       * candidateNPlus have layer < k
       */
      if (copy.equals(candidateNPlus))
      {
        this.nodes[candidate].layer = k;
        this.nodes[candidate].y     = l;
        this.layers[k].add(new Integer(candidate));
        l++;
      }
      else
      {
        oldlayers = new ArrayList(Arrays.asList(this.layers));
        oldlayers.add(new ArrayList());
        this.layers = (ArrayList[])oldlayers.toArray(new ArrayList[0]);
        k++;
        l = 0;
        layerSetUnion = (BitSet)v_wo_u.clone();
        this.nodes[candidate].layer = k;
        this.nodes[candidate].y     = l;
        this.layers[k].add(new Integer(candidate));
        l++;
      }
      v_wo_u.set(candidate);
      u.clear(candidate);
    }

    if (this.forceInputOutputLayer.value)
    {
      nl = this.net.getInputNeurons();
      if (nl != null && nl.size() != 0)
      {
        oldlayers = new ArrayList(Arrays.asList(this.layers));
        oldlayers.add(new ArrayList());
        this.layers = (ArrayList[])oldlayers.toArray(new ArrayList[0]);
        k++;

        for (i = 0; i < nl.size(); i++)
        {
          this.nodes[nl.neuron(i).id()].layer = k;
          this.nodes[nl.neuron(i).id()].y     = i;
          this.layers[k].add(new Integer(nl.neuron(i).id()));
        }
      }
    }

    this.layerCount = k+1;
    this.insertDummyVertices();
  }

  private void insertDummyVertices()
  {
    int       i, j;
    int       layerDist;
    int       layer;
    int       to;
    int       from;
    Edge[]    copy     = new Edge[this.edges.length];
    Edge      edge;
    Node      node;
    ArrayList newnodes = new ArrayList(Arrays.asList(this.nodes));

    for (i = 0; i < this.edges.length; i++)
    {
      copy[i]      = new Edge();
      copy[i].from = this.edges[i].from;
      copy[i].to   = this.edges[i].to;
    }


    for (i = 0; i < copy.length; i++)
    {
      to        = copy[i].to;
      from      = copy[i].from;
      layerDist = this.nodes[from].layer - this.nodes[to].layer;
      if (layerDist < 0)
      {
        layerDist = -layerDist;
        to        = copy[i].from;
        from      = copy[i].to;
      }
      if (layerDist > 1)
      {
        for (j = 1; j < layerDist; j++)
        {
          layer     = this.nodes[from].layer - 1;

          edge      = new Edge();
          edge.from = from;
          edge.to   = this.nodes.length;

          from        = this.nodes.length;
          node        = new Node();
          node.y      = this.layers[layer].size();
          node.layer  = layer;
          node.pseudo = true; 

          this.addNode(node);
          // edge.to is the index of the new inserted node
          this.layers[node.layer].add(new Integer(edge.to));
          this.addEdge(edge);
        }

        edge      = new Edge();
        edge.from = from;
        edge.to   = to;

        this.removeEdge(copy[i]);
        this.addEdge(edge);
      }
    }
  }

  private HashMap getCrossingNumbers(int varLayer, int fixLayer)
  {
    int         j,k,l;
    int         src;
    Edge        dmy, dmy2;
    ArrayList   edges = new ArrayList();
    ArrayList[] lookUp;
    HashMap     crossingNumbers = new HashMap();
    HashMap     dmyHash;

    if (fixLayer < varLayer)
    {
      lookUp = this.src;
    }
    else
    {
      lookUp = this.dest;
    }

    for (j = 0; j < this.layers[varLayer].size(); j++)
    {
      crossingNumbers.put(this.layers[varLayer].get(j), new HashMap());
      for (k = 0; k < this.layers[varLayer].size(); k++)
      {
        dmyHash = (HashMap)crossingNumbers.get(this.layers[varLayer].get(j));
        dmyHash.put(this.layers[varLayer].get(k), new Integer(0));
      }
    }


    for (j = 0; j < this.edges.length; j++)
    {
      if (this.nodes[this.edges[j].from].layer == varLayer)
      {
        if (this.nodes[this.edges[j].to].layer == fixLayer)
        {
          dmy = new Edge();
          dmy.from = this.edges[j].from;
          dmy.to   = this.edges[j].to;
          edges.add(dmy);
        }
      }
      else if (this.nodes[this.edges[j].to].layer == varLayer)
      {
        if (this.nodes[this.edges[j].from].layer == fixLayer)
        {
          dmy = new Edge();
          dmy.from = this.edges[j].to;
          dmy.to   = this.edges[j].from;
          edges.add(dmy);
        }
      }
    }

    for (j = 0; j < edges.size(); j++)
    {
      dmy = (Edge)edges.get(j);
      for (k = j + 1; k < edges.size(); k++)
      {
        dmy2 = (Edge)edges.get(k);
        if (dmy.from != dmy2.from)
        {

          if (this.nodes[dmy.to].y > this.nodes[dmy2.to].y)
          {
            dmyHash =(HashMap)crossingNumbers.get(new Integer(dmy.from));
            l = ((Integer)dmyHash.get(new Integer(dmy2.from))).intValue() + 1;
            dmyHash.put(new Integer(dmy2.from), new Integer(l));

          }
          if (this.nodes[dmy.to].y < this.nodes[dmy2.to].y)
          {
            dmyHash =(HashMap)crossingNumbers.get(new Integer(dmy2.from));
            l = ((Integer)dmyHash.get(new Integer(dmy.from))).intValue() + 1;
            dmyHash.put(new Integer(dmy.from), new Integer(l));
          }
        }
      }
    }

    return crossingNumbers;
  }

  private void performSwitch(int layer, HashMap crossingNumbers)
  {
    int crossings, oldcrossings;
    int j, k;
    int v1, v2, cNv1v2, cNv2v1;


    crossings = this.countCrossings(layer, crossingNumbers);

    do
    {
      oldcrossings = crossings;
      for (j = 0; j < this.layers[layer].size() - 1; j++)
      {
        v1 = ((Integer)this.layers[layer].get(j)).intValue();
        v2 = ((Integer)this.layers[layer].get(j+1)).intValue();

        cNv1v2 = ((Integer)((HashMap)crossingNumbers.get(new Integer(v1))).
            get(new Integer(v2))).intValue();
        cNv2v1 = ((Integer)((HashMap)crossingNumbers.get(new Integer(v2))).
            get(new Integer(v1))).intValue();

        if (cNv2v1 < cNv1v2)
        {
          this.nodes[v1].y = j + 1;
          this.nodes[v2].y = j;
          this.layers[layer].set(j, new Integer(v2));
          this.layers[layer].set(j + 1, new Integer(v1));
        }
      }

      crossings = this.countCrossings(layer, crossingNumbers);
    } while (crossings < oldcrossings);
  }

  private int countCrossings(int layer, HashMap crossingNumbers)
  {
    int     j, k;
    int     crossings = 0;
    HashMap dmy;

    for (j = 0; j < this.layers[layer].size() - 1; j++)
    {
      for (k = j + 1; k < this.layers[layer].size(); k++)
      {
        dmy        = (HashMap)crossingNumbers.get(this.layers[layer].get(j));
        crossings += ((Integer)dmy.get(this.layers[layer].get(k))).intValue();
      }
    }
    return crossings;
  }

  private void baryCenterCrossingReduction(int varLayer, int fixLayer)
  {
    int         i, j, k;
    int         layersize;
    ArrayList[] lookup;
    int         currSrc;
    int[]       neighbourPos;
    int[]       neighbourCount;
    int[]       newPermutation;
    ArrayList   barycenter = new ArrayList();


    /* LN -> LN-1 -> ... -> LN2 -> LN1 -> LN0 */
    if (fixLayer < varLayer)
    {
      lookup = this.src;
    }
    else
    {
      lookup = this.dest;
    }


    layersize      = this.layers[varLayer].size();
    neighbourPos   = new int[layersize];
    neighbourCount = new int[layersize];
    newPermutation = new int[layersize];
    barycenter.clear();

    Arrays.fill(neighbourPos, 0);
    Arrays.fill(neighbourCount, 0);

    for (j = 0; j < layersize; j ++)
    {
      currSrc = ((Integer)this.layers[varLayer].get(j)).intValue();
      neighbourCount[j] = this.src[currSrc].size() + this.dest[currSrc].size();
      for (k = 0; k < this.src[currSrc].size(); k++)
      {
        neighbourPos[j] += 
          this.nodes[((Integer)this.src[currSrc].get(k)).intValue()].y;
      }
      for (k = 0; k < this.dest[currSrc].size(); k++)
      {
        neighbourPos[j] +=
          this.nodes[((Integer)this.dest[currSrc].get(k)).intValue()].y;
      }
    }

    for (j = 0; j < layersize; j++)
    {
      if (neighbourCount[j] > 0)
      {
        barycenter.add(new Integer(neighbourPos[j] / neighbourCount[j]));
      }
      else
      {
        barycenter.add(new Integer(-1));
      }
    }

    for (j = 0; j < layersize; j++)
    {
      k = barycenter.indexOf(Collections.min(barycenter));
      barycenter.set(k, new Integer(Integer.MAX_VALUE));

      newPermutation[j] = ((Integer)this.layers[varLayer].get(k)).intValue();
      this.nodes[newPermutation[j]].y = j;
    }

    for (j = 0; j < layersize; j++)
    { 
      this.layers[varLayer].set(j, new Integer(newPermutation[j]));
    }

  }

  private void computeCrossingReduction()
  {
    int         i, j, k;
    HashMap     crossingNumbers;
    int         v1, v2;
    int         cNv1v2, cNv2v1;
    int         crossings, oldcrossings;
    int         sinksFixedCrossings;
    ArrayList[] sinksFixedFirst, initial;


    initial = new ArrayList[this.layers.length];
    for (i = 0; i < this.layers.length; i++)
    {
      initial[i] = new ArrayList(this.layers[i]);
    }

    crossings = Integer.MAX_VALUE;
    do
    {
      oldcrossings = crossings;
      for (i = 1; i < this.layers.length; i++)
      {
        if (this.crossingReduction.value == LayeredGraph.GREEDY_SWITCH_CR)
        {
          crossingNumbers = this.getCrossingNumbers(i, i - 1);
          this.performSwitch(i, crossingNumbers);
        }
        else
        {
          this.baryCenterCrossingReduction(i, i - 1);
        }
      }

      for (i = this.layers.length - 2; i >= 0; i--)
      {
        if (this.crossingReduction.value == LayeredGraph.GREEDY_SWITCH_CR)
        {
          crossingNumbers = this.getCrossingNumbers(i, i + 1);
          this.performSwitch(i, crossingNumbers);
        }
        else
        {
          this.baryCenterCrossingReduction(i, i + 1);
        }
      }

      crossings = 0;
      for (i = 1; i < this.layers.length; i++)
      {
        crossings += this.countCrossings(i, this.getCrossingNumbers(i, i - 1));
      }
    } while (crossings < oldcrossings);

    sinksFixedCrossings = crossings;
    sinksFixedFirst = new ArrayList[this.layers.length];
    for (i = 0; i < this.layers.length; i++)
    {
      sinksFixedFirst[i] = new ArrayList(this.layers[i]);
    }

    this.layers = initial;

    crossings = Integer.MAX_VALUE;
    do
    {
      oldcrossings = crossings;

      for (i = this.layers.length - 2; i >= 0; i--)
      {
        if (this.crossingReduction.value == LayeredGraph.GREEDY_SWITCH_CR)
        {
          crossingNumbers = this.getCrossingNumbers(i, i + 1);
          this.performSwitch(i, crossingNumbers);
        }
        else
        {
          this.baryCenterCrossingReduction(i, i + 1);
        }
      }

      for (i = 1; i < this.layers.length; i++)
      {
        if (this.crossingReduction.value == LayeredGraph.GREEDY_SWITCH_CR)
        {
          crossingNumbers = this.getCrossingNumbers(i, i - 1);
          this.performSwitch(i, crossingNumbers);
        }
        else
        {
          this.baryCenterCrossingReduction(i, i - 1);
        }
      }

      crossings = 0;
      for (i = 1; i < this.layers.length; i++)
      {
        crossings += this.countCrossings(i, this.getCrossingNumbers(i, i - 1));
      }
    } while (crossings < oldcrossings);


    if (sinksFixedCrossings < crossings)
    {
      this.layers = sinksFixedFirst;
    }
  }

  private void computeYCoordinates()
  {
    int       i, j, k, l;
    int       nodesPerLayer;
    ArrayList layer;
    Node      n1;
    double    nodeSum, layerSum;

    layerSum = 0;

    for (i = 1; i < this.layers.length; i++)
    {
      layer         = this.layers[i];
      nodesPerLayer = layer.size();

      layerSum = 0.0d;
      for (j = 0; j < nodesPerLayer; j++)
      {
        k       = ((Integer)layer.get(j)).intValue();
        if (this.src[k].size() > 0)
        {
          nodeSum = 0.0d;
          for (l = 0; l < this.src[k].size(); l++)
          {
            n1 = this.nodes[((Integer)this.src[k].get(l)).intValue()];

            nodeSum += n1.y;

          }
          layerSum += (nodeSum  / this.src[k].size()) - this.nodes[k].y;
        }
      }
      layerSum /= nodesPerLayer;

      for (j = 0; j < nodesPerLayer; j++)
      {
        n1    = this.nodes[((Integer)layer.get(j)).intValue()];
        n1.y += layerSum;
      }

    }
    for (i = this.layers.length - 2; i >= 0; i--)
    {
      layer         = this.layers[i];
      nodesPerLayer = layer.size();

      layerSum = 0.0d;
      for (j = 0; j < nodesPerLayer; j++)
      {
        k       = ((Integer)layer.get(j)).intValue();
        if (this.dest[k].size() > 0)
        {
          nodeSum = 0.0d;
          for (l = 0; l < this.dest[k].size(); l++)
          {
            n1 = this.nodes[((Integer)this.dest[k].get(l)).intValue()];

            nodeSum += n1.y;

          }
          layerSum += (nodeSum  / this.dest[k].size()) - this.nodes[k].y;
        }
      }
      layerSum /= nodesPerLayer;

      for (j = 0; j < nodesPerLayer; j++)
      {
        n1    = this.nodes[((Integer)layer.get(j)).intValue()];
        n1.y += layerSum;
      }
    }


  }

  private void makeMove(VisualNet visualNet)
  {
    NeuronRenderer nr;

    for (int i = 0; i < visualNet.getNet().size(); i++)
    {
      nr = visualNet.getNeuronRenderer(i);
      if (this.orientation.value == LayeredGraph.VERTICAL_ORI)
      {
        nr.setLocation(
            (int)(this.nodes[i].y * this.horizontalDist.value),
            (int)((this.layerCount - this.nodes[i].layer) * this.verticalDist.value));
      }
      else
      {
        nr.setLocation((int)(
              (this.layerCount - this.nodes[i].layer) * this.horizontalDist.value),
            (int)(this.nodes[i].y * this.verticalDist.value));
      }
    }

  }


  private static class SourceDistanceComparator implements Comparator
  {
    ArrayList   pi1;
    ArrayList   pi2;
    int[]       pi;
    ArrayList[] dest;


    public SourceDistanceComparator(int[] pi, ArrayList[] dest)
    {
      this.pi  = pi;
      this.dest = dest;

    }
    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object o1, Object o2) {
      int n1 = ((Integer)o1).intValue();
      int n2 = ((Integer)o2).intValue();

      pi1 = new ArrayList();
      pi2 = new ArrayList();

      for (int i = 0; i < this.dest[n1].size(); i++)
      {
        pi1.add(new Integer(this.pi[((Integer)dest[n1].get(i)).intValue()]));
      }

      for (int i = 0; i < this.dest[n2].size(); i++)
      {
        pi2.add(new Integer(this.pi[((Integer)dest[n2].get(i)).intValue()]));;
      }

      if (isSmaller(n1, n2))
      {
        return -1;
      }
      else
      {
        return 1;
      }


    }

    public boolean isSmaller(int n1, int n2)
    {
      int max1, max2;
      if (this.pi1.size() == 0 && this.pi2.size() == 0)
      {
        return true;
      }

      if (this.pi1.size() != 0)
      {
        max1 = ((Integer)Collections.max(this.pi1)).intValue();
      }
      else
      {
        max1 = -1;
      }

      if (this.pi2.size() != 0)
      {
        max2 = ((Integer)Collections.max(this.pi2)).intValue();
      }
      else
      {
        max2 = -1;
      }

      if (max1 < max2)
      {
        return true;
      }
      else if (max1 == max2)
      {
        this.pi1.remove(Collections.max(this.pi1));
        this.pi2.remove(Collections.max(this.pi2));
        return isSmaller(n1, n2);
      }
      else
      {
        return false;
      }
    }

  }

  private static class Node
  {
    double  y      = 0.0;
    int     layer  = 0;
    boolean pseudo = false;
  }

  private static class Edge
  {
    int from;
    int to;

    public String toString()
    {
      return "(" + from + ", " + to + ")";
    }

    public int hashCode()
    {

      return from * 10 * (((int)Math.floor(Math.log(to + 1) / Math.log(10))) + 1) 
        + to;
    }
  }

  public static void main(String args[])
  {
    Net net = new Net();
    VisualNet visnet;
    LayeredGraph lg = new LayeredGraph();

    for (int i = 0; i <= 6; i++)
    {
      net.addNeuron();
    }

    net.addSynapse(net.getNeuron(0), net.getNeuron(5), 0);
    net.addSynapse(net.getNeuron(1), net.getNeuron(3), 0);
    net.addSynapse(net.getNeuron(1), net.getNeuron(4), 0);
    net.addSynapse(net.getNeuron(1), net.getNeuron(6), 0);
    net.addSynapse(net.getNeuron(2), net.getNeuron(4), 0);
    net.addSynapse(net.getNeuron(2), net.getNeuron(5), 0);

    visnet = new VisualNet(net);
    lg.computeInitialState(visnet);

    lg.layerCount = 2;
    lg.layers = new ArrayList[2];
    lg.layers[0] = new ArrayList();
    lg.layers[1] = new ArrayList();

    lg.layers[0].add(new Integer(3));
    lg.nodes[3].layer = 0;
    lg.nodes[3].y = 0;
    lg.layers[0].add(new Integer(4));
    lg.nodes[4].layer = 0;
    lg.nodes[4].y = 1;
    lg.layers[0].add(new Integer(5));
    lg.nodes[5].layer = 0;
    lg.nodes[5].y = 2;
    lg.layers[0].add(new Integer(6));
    lg.nodes[6].layer = 0;
    lg.nodes[6].y = 3;

    lg.layers[1].add(new Integer(0));
    lg.nodes[0].layer = 1;
    lg.nodes[0].y = 0;
    lg.layers[1].add(new Integer(1));
    lg.nodes[1].layer = 1;
    lg.nodes[1].y = 1;
    lg.layers[1].add(new Integer(2));
    lg.nodes[2].layer = 1;
    lg.nodes[2].y = 2;

    HashMap hm1 = lg.getCrossingNumbers(1, 0);
    Object[] hm1keys = hm1.keySet().toArray();

    for (int i = 0; i < hm1keys.length; i++)
    {

      HashMap hm2 = (HashMap)hm1.get(hm1keys[i]);
      Object[] hm2keys = hm2.keySet().toArray();
      for (int j = 0; j < hm2keys.length; j++)
      {
        System.out.print(hm2.get(hm2keys[j]) + " ");
      }
      System.out.println();
    }

    lg.computeCrossingReduction();

    for (int i = 0; i < 3; i++)
    {
      System.out.print(lg.layers[1].get(i) + " ");
    }
    System.out.println();
  }

}
