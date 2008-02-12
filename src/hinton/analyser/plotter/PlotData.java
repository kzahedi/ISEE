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
 * Created on 23.04.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package hinton.analyser.plotter;

import hinton.analyser.Analyser;
import hinton.analyser.ModifiedNeuronOutputList;
import hinton.analyser.NetObserver;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;

import cholsey.Net;
import cholsey.Neuron;
import cholsey.NeuronList;
import cholsey.Synapse;


/**
 * @author rosemann
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PlotData implements NetObserver
{
  private Net          net;
  private Net          initialNet;
  private int          steps;
  private LinkedList[] netInputValues;
  private LinkedList   modifiedNeurons;
  private HashMap      plotData;
  
  private static PlotData instance;

  private PlotData()
  {
    this.net = null;
    this.steps = 0;
    this.netInputValues = new LinkedList[0];
    this.modifiedNeurons = new LinkedList();
    this.plotData = new HashMap();
  }
  
  public static PlotData getInstance()
  {
    if (PlotData.instance == null)
    {
      PlotData.instance = new PlotData();
    }
    return PlotData.instance;
  }
  
  synchronized public void reset()
  {
    Object[] plotObjects = this.plotData.keySet().toArray();
    for (int i = 0; i < plotObjects.length; i++)
    {
      ((PlotValues)this.plotData.get(plotObjects[i])).values.clear();
    }
    
    for (int i = 0; i < this.netInputValues.length; i++)
    {
      this.netInputValues[i].clear();
    }
    
    this.modifiedNeurons.clear();
    this.steps        = 0;

  }

  /* (non-Javadoc)
   * @see Hinton.analyser.NetObserver#setNet(Cholsey.Net)
   */
  public void setNet(Net net)
  {
    NeuronList nl = net.getInputNeurons();
    Neuron[]   ns;
    this.net = net;

    if (nl != null)
    {
      this.netInputValues = new LinkedList[nl.size()];
      ns = (Neuron[])nl.toArray(new Neuron[0]);
      for (int i = 0; i < ns.length; i++)
      {
        this.netInputValues[i] = new LinkedList();
        //this.netInputValues[i].add(new Double(ns[i].getActivation()));
      }
    } else {
      this.netInputValues = new LinkedList[0];
    }
    this.plotData.clear();
    this.steps = 0;
  }

  /* (non-Javadoc)
   * @see Hinton.analyser.NetObserver#netUpdate(Cholsey.Net)
   */
  public synchronized void netUpdate(Net net)
  {
    NeuronList nl = net.getInputNeurons();
    Neuron[]   ns;
    Object[]   plotObjects;
    PlotValues retargeter;
    double[]   outputValues;
    double[]   newOutputValues;
    Synapse    synapse;
    Synapse[]  synapses;
    
    if (this.steps == 0)
    {
      this.initialNet = net.copy();
    }
    
    if (nl != null)
    {
      ns = (Neuron[])nl.toArray(new Neuron[0]);
      
      for (int i = 0; i < ns.length; i++)
      {
        this.netInputValues[i].add(new Double(ns[i].getActivation()));
      }
    }
    
    this.modifiedNeurons.add(
        ModifiedNeuronOutputList.getInstance().getModifiedNeurons());
    
    if (!this.plotData.isEmpty())
    {
      plotObjects = this.plotData.keySet().toArray();
      
      for (int j=0; j < plotObjects.length; j++)
      {
        retargeter      = (PlotValues)this.plotData.get(plotObjects[j]);
        
        if (plotObjects[j] instanceof Neuron)
        {
          retargeter.values.add(
          new Double(net.getNeuron(((Neuron)plotObjects[j]).id()).getOutput()));
        }
        else if (plotObjects[j] instanceof Synapse)
        {
          synapse  = (Synapse)plotObjects[j];
          synapses = (Synapse[])net.getNeuron(
              synapse.getDestination().id()).synapses().toArray(new Synapse[0]);
          for (int k = 0; k < synapses.length; k++)
          {
            if (synapses[k].getSource().id() == synapse.getSource().id())
            {
              retargeter.values.add(new Double(synapses[k].strength()));
            }
          }
        }

      }
    }
    this.steps++;
    
  }

  /**
   * Returns a "Stream" of values for the plotObject
   * 
   * @param neuron
   * @return plotable values for the plotObject
   */
  public synchronized PlotValues getPlotValueStream(Object plotObject)
  {
    PlotValues newValues;
    if (this.plotData.containsKey(plotObject))
    {
      return (PlotValues)this.plotData.get(plotObject);
    }
    else
    {
      Analyser.getInstance().pause();
      newValues = this.getValues(plotObject);
      newValues.plotObject = plotObject;
      this.plotData.put(plotObject,
                        newValues);
      Analyser.getInstance().resume();
      return newValues;
    }
  }

  private synchronized PlotValues getValues(Object plotObject)
  {
    PlotValues     plotValues  = new PlotValues();
    Net            netClone    = this.initialNet.copy();
    ListIterator[] inputValues = new ListIterator[this.netInputValues.length];
    ListIterator   modifiedIT  = this.modifiedNeurons.listIterator();
    HashMap        modified;
    Synapse        synapse;
    Synapse[]      synapses;
    int            steps;
    
    
    steps              = this.steps;
    plotValues.values = new Vector(steps);
    
    /* get iterators for lists for faster traverse */
    for (int i = 0; i < inputValues.length; i++)
    {
      inputValues[i] = this.netInputValues[i].listIterator();
      inputValues[i].next();
    }
    modifiedIT.next();
    
    /* Value of the first step is given in the initialNet */
    plotValues.values.add(this.addValue(plotObject, this.initialNet));
    for (int i = 1; i < steps; i++)
    {
      for (int j = 0; j < this.netInputValues.length; j++)
      {
        netClone.getInputNeurons().neuron(j).setActivation(
                  ((Double)inputValues[j].next()).doubleValue());
        
      }
      // Consider the Output modification of the currently reprocessed step
      modified = (HashMap)modifiedIT.next();
      this.modifyOutput(netClone, modified);
      netClone.process();
      this.modifyOutput(netClone, modified);

      plotValues.values.add(this.addValue(plotObject, netClone));
    }
    return plotValues;
  }
  
  private Double addValue(Object plotObject, Net netClone)
  {
    Synapse synapse;
    Synapse[] synapses;
    if (plotObject instanceof Neuron)
    {
      return new Double(netClone.getNeuron(
          ((Neuron)plotObject).id()).getOutput());
    }
    else if (plotObject instanceof Synapse)
    {
      synapse  = (Synapse)plotObject;
      synapses = (Synapse[])netClone.getNeuron(
          synapse.getDestination().id()).synapses().toArray(new Synapse[0]);
      for (int j = 0; j < synapses.length; j++)
      {
        if (synapses[j].getSource().id() == synapse.getSource().id())
        {
          return new Double(synapses[j].strength());
        }
      }
    }
    return new Double(0);
  }
  
  private void modifyOutput(Net modnet, HashMap modifiedMap)
  {
    Neuron[]   neurons  = (Neuron[])modnet.neurons().toArray(new Neuron[0]);
    Integer[]  modified = (Integer[])
                          modifiedMap.keySet().toArray(new Integer[0]);
    
    for (int i = 0; i < modified.length; i++)
    {
      try
      {
        neurons[modified[i].intValue()].setOutput(
                      Double.parseDouble(
                            modifiedMap.get(modified[i]).toString()));  
      }
      catch (NumberFormatException nfe)
      {
        //Nothing
      }
      
    } 
  }

  /**
   * 
   * @author rosemann
   *
   * This class represents plotable values as a "Stream", Stream is meant as the
   * Vector values is automatically extended with new values
   */
  public static class PlotValues
  {
    public Object    plotObject;
    public Vector    values;
  }
}
