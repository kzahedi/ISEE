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

package util.misc;

import java.util.ArrayList;

import cholsey.Net;
import cholsey.Neuron;
import cholsey.NeuronType;
import cholsey.SynapseList;
import cholsey.SynapseMode;
import cholsey.SynapseType;

public class Graph 
{

  private ArrayList nodes = null;

  public Graph()
  {
    nodes = new ArrayList();
  }

  public int size()
  {
    if(nodes == null)
    {
      return 0;
    }
    return nodes.size();
  }
  
  public void createGraph(Net net)
  {
    int index = 1;
    for(net.neurons().start(); net.neurons().hasMore(); net.neurons().next())
    {
      Neuron neuron = net.neurons().neuron();
      Node node = new Node();
      switch(neuron.getNeuronType().type())
      {
        case NeuronType.NEURON_TYPE_INPUT:
          node.setName("Input Neuron " + index);
          break;
        case NeuronType.NEURON_TYPE_OUTPUT:
          node.setName("Output Neuron " + index);
          break;
        case NeuronType.NEURON_TYPE_HIDDEN:
          node.setName("Hidden Neuron " + index);
          break;
      }
      nodes.add(node);
      index++;
    }

    System.out.println("node.size:" + nodes.size());

    for(net.neurons().start(); net.neurons().hasMore(); net.neurons().next())
    {
      SynapseList sl = net.neurons().neuron().synapses();

      if(sl != null)
      {
        for(sl.start(); sl.hasMore(); sl.next())
        {
          int soureIndex = net.neurons().indexOf(sl.synapse().getSource());
          int destinationIndex = net.neurons().indexOf(sl.synapse().getDestination());

          Edge out = new Edge();
          Edge in = new Edge();

          if(net.getSynapseMode() == SynapseMode.DYNAMIC)
          {
            if(sl.synapse().type() == SynapseType.INHIBITORY)
            {
              out.setValue(-1);
              in.setValue(-1);
            }
            else
            {
              out.setValue(1);
              in.setValue(1);
            }
          }
          else
          {
            out.setValue(sl.synapse().strength());
            in.setValue(sl.synapse().strength());
          }

          Node n1 = (Node)nodes.get(soureIndex);
          Node n2 = (Node)nodes.get(destinationIndex);

          out.setSource(n1);
          out.setDestination(n2);

          in.setSource(n1);
          in.setDestination(n2);

          n1.addOutgoingEdge(out);
          n2.addIncomingEdge(in);
        }
      }
    }

  }

  public String toString()
  {
    String s = new String();
    for(int index=0; index < nodes.size(); index++)
    {
      Node n = (Node)nodes.get(index);

      s = s.concat(n.toString());
      s = s.concat("\n");
    }
    return s;
  }

  public ArrayList getNodes()
  {
    return nodes;
  }

}
