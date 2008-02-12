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
 * Created on 26.04.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package hinton.analyser;

import java.util.HashMap;

import cholsey.Net;
import cholsey.Neuron;


/**
 *
 * This class holds a list of all neurons that outputs are modified
 * by the analyser user
 */
public class ModifiedNeuronOutputList implements NetModifier
{
	private static ModifiedNeuronOutputList instance;
	
	private HashMap modifiedOutputs;
	private Net     net;
	
	private ModifiedNeuronOutputList()
	{
		this.modifiedOutputs = new HashMap();
	}
	
	/**
	 * Returns the singleton instance of the ModifiedNeuronOutputList
	 * 
	 * @return the <b>singleton</b> instance of ModifiedNeuronOutputList
	 */
	public static ModifiedNeuronOutputList getInstance()
	{
		if (ModifiedNeuronOutputList.instance == null)
		{
			ModifiedNeuronOutputList.instance = new ModifiedNeuronOutputList();
		}
		return ModifiedNeuronOutputList.instance;
	}
	
	/**
	 * Sets the output for the neuron with the given neuronID to output
	 * The parameter output is of type String since non numeric values will be
	 * ignored by modifyNet()
	 * 
	 * @param neuronID the id of the neuron which output is to be modified
	 * @param output the output value
	 */
	public void setNeuronOutput(int neuronID, String output)
	{
		modifiedOutputs.put(new Integer(neuronID), output);
	}
	
	/**
	 * Gets the setted outputvalue for the neuron identified by neuronID.
	 * 
	 * If no output is set for the neuron <i>"x"</i> is returned 
	 * 
	 * @param neuronID the id of the neuron
	 * @return the setted output of the neuron
	 */
	public String getNeuronOutput(int neuronID)
	{
		Object ret;
		ret = modifiedOutputs.get(new Integer(neuronID));
		
		if (ret == null)
		{
			ret = "x";
		}
		return ret.toString();
	}
	
	/**
	 * This method removes the neuron identified by neuronID from the list
	 * of modified neurons. The output of this neuron will not longer be altered
	 * by modifyNet()
	 * @param neuronID the id of the neuron to be removed
	 */
	public void removeNeuronOutput(int neuronID)
	{
		this.modifiedOutputs.remove(new Integer(neuronID));
	}
	
	/**
	 * This method returns the currently modified neurons as a Hashmap, NeuronId 
	 * as key and Outputvalue as value. If Outputvalue is not numeric it has to be 
	 * ignored
	 * @return modiefied Neurons
	 */
	public HashMap getModifiedNeurons()
	{
		return new HashMap(this.modifiedOutputs);
	}

	/* (non-Javadoc)
	 * @see Hinton.analyser.NetModifier#setNet(Cholsey.Net)
	 */
	public void setNet(Net net)
	{
		this.net = net;
		this.modifiedOutputs.clear();
	}

	/* (non-Javadoc)
	 * @see Hinton.analyser.NetModifier#modifyNet()
	 */
	public void modifyNet()
	{
		Neuron[]   neurons  = (Neuron[])this.net.neurons().toArray(new Neuron[0]);
		Integer[]  modified = (Integer[])
													this.modifiedOutputs.keySet().toArray(new Integer[0]);
		
		for (int i = 0; i < modified.length; i++)
		{
			try
			{
				neurons[modified[i].intValue()].setOutput(
											Double.parseDouble(
														this.modifiedOutputs.get(modified[i]).toString()));	
			}
			catch (NumberFormatException nfe)
			{
				//Nothing
			}
			
		} 
	}
	
}
