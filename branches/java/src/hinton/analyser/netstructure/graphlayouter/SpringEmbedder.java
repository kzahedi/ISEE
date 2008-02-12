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
 * Created on 26.03.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package hinton.analyser.netstructure.graphlayouter;

import hinton.analyser.netstructure.GraphLayouter;
import hinton.analyser.netstructure.NeuronRenderer;
import hinton.analyser.netstructure.VisualNet;
import hinton.analyser.toolkit.InputValue;
import cholsey.Net;
import cholsey.NeuronType;
import cholsey.Synapse;
import cholsey.SynapseList;

/**
 * @author rosemann
 * This class implements the spring embedder layout algorithm
 */
public class SpringEmbedder extends GraphLayouter
{
	private static final Integer HORIZONTAL_ORI   = new Integer(2);
	private static final Integer VERTICAL_ORI     = new Integer(3);
	
	private Node[] nodes;
	private Edge[] edges;
	private Node[] bestposition;
	
	private InputValue.IBoolean random;
	private InputValue.IInteger steps;
	private InputValue.IInteger edgeLength;
	private InputValue.IInteger attractionFactor;
	private InputValue.IInteger repellenceFactor;
	private InputValue.IObject  initialState;
	private InputValue.IObject  orientation;
	private InputValue.IBoolean fixInOutNeurons;
	
	private double oldstress = Double.MAX_VALUE;
	
	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.GraphDrawOptimizer#optimizeGraph(Hinton.analyser.netstructure.VisualNet, Cholsey.Net)
	 */
	protected void layoutGraph(VisualNet visualNet) {
		for (int i = 0; i < steps.value; i++)
		{
			if (this.random.value && Math.random() < 0.03)
			{
				Node n = this.nodes[(int)(this.nodes.length * Math.random())];
				if (!n.fixed)
				{
					n.x = 200 * Math.random() - 100;
					n.y = 200 * Math.random() - 100;
				}
			}
			relaxStep();
		}
		makeMove(visualNet);

	}
	
	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.GraphDrawOptimizer#computeInitialState(Hinton.analyser.netstructure.VisualNet, Cholsey.Net)
	 */
	protected void computeInitialState(VisualNet visualNet) 
	{
		SynapseList synapseList    = new SynapseList();
		Synapse     currentSynapse;
		Net         net            = visualNet.getNet();
		int outNeuron = 0;
		int inNeuron = 0;
		int radius;
	
		this.nodes = new Node[net.size()];
		this.bestposition = new Node[net.size()];
		radius = this.nodes.length / 4;
		for (int i = 0; i < this.nodes.length; i++)
		{
			this.nodes[i] = new Node();
			if (((String)this.initialState.value).compareTo("Random") == 0)
			{
				this.nodes[i].x = 150 + Math.random() * this.edgeLength.value *
																(int)Math.ceil(Math.sqrt(net.size()));
				this.nodes[i].y = 150 + Math.random() * this.edgeLength.value *
																(int)Math.ceil(Math.sqrt(net.size()));
			}
			else
			{
				NeuronRenderer nr = visualNet.getNeuronRenderer(i);
				this.nodes[i].x = nr.getX();
				this.nodes[i].y = nr.getY();
			}
			if (net.getNeuron(i).synapses() != null)
			{
				synapseList.addAll(net.getNeuron(i).synapses());
			} 
			
			if (this.fixInOutNeurons.value)
			{
				if (net.getNeuron(i).getNeuronType().type()
						== NeuronType.NEURON_TYPE_INPUT)
				{
					inNeuron++;
					if (this.orientation.value == SpringEmbedder.VERTICAL_ORI)
					{
						this.nodes[i].x = inNeuron * this.edgeLength.value;
						this.nodes[i].y = 0;
					}
					else
					{
						this.nodes[i].x = 0;
						this.nodes[i].y = inNeuron * this.edgeLength.value;
					}
					this.nodes[i].fixed = true;
				}
				if (net.getNeuron(i).getNeuronType().type()
						== NeuronType.NEURON_TYPE_OUTPUT)
				{
					outNeuron++;
					if (this.orientation.value == SpringEmbedder.VERTICAL_ORI)
					{
						this.nodes[i].x = outNeuron * this.edgeLength.value;
						this.nodes[i].y = (int)Math.ceil(Math.sqrt(net.size())) * 
						                  this.edgeLength.value;
					}
					else
					{
						this.nodes[i].x = (int)Math.ceil(Math.sqrt(net.size())) * 
            									this.edgeLength.value;
						this.nodes[i].y = outNeuron * this.edgeLength.value;
					}
					this.nodes[i].fixed = true;
				}
			}
		}
		

		this.edges = new Edge[synapseList.size()];
		for (int i = 0; i < synapseList.size(); i++)
		{
			currentSynapse     = synapseList.synapse(i);
			this.edges[i]      = new Edge();
			this.edges[i].from = currentSynapse.getSource().id();
			this.edges[i].to   = currentSynapse.getDestination().id();
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see Hinton.analyser.netstructure.GraphLayouter#getProperties()
	 */
	protected InputValue[] getProperties()
	{
		if (this.random == null)
		{
			this.random = new InputValue.IBoolean("Use random positioning",
			                                      true);
			this.steps  = new InputValue.IInteger("Iterationsteps",
																						1000,
																						10, 10000);
			this.edgeLength = new InputValue.IInteger("Desired edgelength",
			                                          150,
			                                          100, 400);
			this.attractionFactor = new InputValue.IInteger("Attractionfactor",
																											25,
																											1, 100);
			this.repellenceFactor = new InputValue.IInteger("Repellencefactor",
																											1,
																											1, 100);
			this.initialState     = new InputValue.IObject("Initial state",
																										0,
																										new String [] { 
																												"Random",
																												"Current Layout"
																										});
			this.orientation      = new InputValue.IObject(
					"Layerorientation\nfor fixed In-/output Neurons",
					0,
					new Integer[] { SpringEmbedder.VERTICAL_ORI, SpringEmbedder.HORIZONTAL_ORI },
					new String[] { "vertical", "horizontal" });
			
			this.fixInOutNeurons = new InputValue.IBoolean("Fix In-/Output Neurons",
																										 false);
		}
		return new InputValue[] { this.initialState,
															this.fixInOutNeurons,
															this.orientation,
															this.random,
															this.steps,
															this.edgeLength,
															this.attractionFactor,
															this.repellenceFactor 
														};
	}
	
	/*
	 *  (non-Javadoc)
	 * @see Hinton.analyser.netstructure.GraphLayouter#getLayoutName()
	 */
	public String getLayoutName()
	{
		return "Spring Embedded";
	}
	
	/**
	 * Move the NeuronRenderers of the given visualNet to the
	 * calculated positions. This method is called at the end of
	 * the iteration cause the intermediate steps are performed on
	 * the utility classes Node and Edge
	 * 
	 * @param visualNet 
	 */
	private void makeMove(VisualNet visualNet)
	{
		NeuronRenderer nr;
		for (int i = 0; i < this.bestposition.length; i++)
		{
			nr = visualNet.getNeuronRenderer(i);
			nr.setLocation((int)(this.bestposition[i].x - nr.getWidth()/2.0d),
			               (int)(this.bestposition[i].y - nr.getHeight()/2.0d));
		}
	}
	
	/**
	 * Perform one iteration step of the Spring Embedder Algorithm
	 *
	 */
	private void relaxStep()
	{
		double stress = 0;
		for (int i = 0 ; i < this.edges.length ; i++)
		{
			Edge e = edges[i];
			if (e.from != e.to)
			{
				double vx = nodes[e.to].x - nodes[e.from].x;
				double vy = nodes[e.to].y - nodes[e.from].y;
				double len = Math.sqrt(vx * vx + vy * vy);
					len = (len == 0) ? 0.0001 : len;
				double f;
				if (len > e.len)
				{
					f = -this.attractionFactor.value * Math.log(e.len/len);
				}
				else
				{
					f = 0.0d;
				}
				double dx = f * vx / len;
				double dy = f * vy / len;
	
				nodes[e.to].dx += -dx;
				nodes[e.to].dy += -dy ;
				nodes[e.from].dx += dx;
				nodes[e.from].dy += dy;
				
			
			}
		}

		for (int i = 0 ; i < this.nodes.length ; i++)
		{
			Node n1 = nodes[i];
			double dx = 0;
			double dy = 0;
			

			for (int j = 0 ; j < this.nodes.length ; j++)
			{
				if (i != j)
				{
					Node n2 = nodes[j];
					double vx = n1.x - n2.x;
					double vy = n1.y - n2.y;
					double len = vx * vx + vy * vy;
					
					     if (len == 0)
					{
						dx += Math.random();
						dy += Math.random();
					}
					else if (len < this.edgeLength.value * this.edgeLength.value * 4)
					{
						double f;
						len = Math.sqrt(len);
						vx = vx / len;
						vy = vy / len;
						f   = this.repellenceFactor.value *
						      Math.log(len/(this.edgeLength.value*2));
						dx += -vx * f;
						dy += -vy * f;
					}
				}
			}
			n1.dx += dx;
			n1.dy += dy;
		}
		
		
	
		for (int i = 0 ; i < this.nodes.length ; i++)
		{
			Node n = nodes[i];
				
			double len = Math.sqrt(n.dx*n.dx + n.dy*n.dy);
			
			stress += len;
			n.dx += (Math.random() - 0.5) * len;
			n.dy += (Math.random() - 0.5) * len;			
		}
		
		if (stress < this.oldstress)
		{
				for (int i = 0; i < this.nodes.length; i++)
				{
					this.bestposition[i] = new Node();
					this.bestposition[i].x = this.nodes[i].x;
					this.bestposition[i].y = this.nodes[i].y;
					this.bestposition[i].dx = this.nodes[i].dx;
					this.bestposition[i].dy = this.nodes[i].dy;
				}
				oldstress = stress;
		}
		else
		{
			for (int i = 0; i < this.nodes.length; i++)
			{
				Node n = nodes[i];
				if (!n.fixed)
				{
					n.x += Math.max(-300, Math.min(300, n.dx));
					n.y += Math.max(-300, Math.min(300, n.dy));
				}
				
				n.dx /= 2;
				n.dy /= 2;
			}
		}
		
	}
	

	
	// Utilclasses
	
	private class Node
	{
		double x;
		double y;

		double dx;
		double dy;
		
		boolean fixed = false;
	}
	
	private class Edge
	{
		int from;
		int to;

		double len = edgeLength.value; // default edgeLength
	}

	

}
