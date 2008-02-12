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


/**
  * Implements a Enumeration with synapse-elements. Etends Vector
  */

package cholsey;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Vector;

/**
 *  This class implements a wrapper to the vector class. <br>
 *  Example: <br>
 *  <code>
  SynapseList synapses = neuron.getSynapseList(); <br>
  for(synapses.start(); synapses.hasMore(); synapses.next()) <br>
  {<br>
   &nbsp;&nbsp;&nbsp;Synapse synapse = synapses.synapse();<br>
   &nbsp;&nbsp;&nbsp;...<br>
  }
  </code>
 *  
 */
public class SynapseList extends Vector
{

  private Comparator comparator = null;
  private int index = 0;
  /**
   * default constructor
   */
  public SynapseList()
  {
    super();
    comparator = new Comparator() {
      public int compare(Object a, Object b)
      {
        int ca = ((Synapse)a).getSource().id();
        int cb = ((Synapse)b).getSource().id();
        if (ca - cb > 0 ) return  1;
        else              return -1;
      }
    };
  }

  // **************************************************************************  
  // synapse functions
  // **************************************************************************  
  /**
   * Returns the currently selected synapse. In a for-loop <br>
   * for(sl.start();sl.hasMore();sl.next()) <br>
   * {<br>
   * &nbsp;&nbsp; Synapse s = sl.synapse();<br>
   * }
   * @param    none
   * @return   the current selected synapse
   */
  public Synapse synapse() 
  {
    return (Synapse)elementAt(index);
  }
  /**
   * Returns the current synapse and increases the index.
   * @param    none
   * @return   the current synapse
   * @see #synapse()
   */
  public Synapse next()
  {
    Synapse returnSynapse;
    if(index < size())
    {
      returnSynapse = (Synapse)elementAt(index);
      index++;
      return returnSynapse;
    }
    return null;
  }

  /**
   * Returns the i-th synapse. This function is a wrapper to the
   * Vector.elementAt(i) function.
   * @param    index int index, the index of the wanted synapse
   * @return   Synapse synapse, the synapse selected
   */
  public Synapse synapse(int index)
  {
    return (Synapse)elementAt(index);
  }
  // **************************************************************************
  // flow control functions
  // **************************************************************************
  /**
   * Returns true if the end of the list is not reached yet.
   * @param    none
   * @return   true, if there a still some synapses to follow, false otherwise
   * @see #synapse()
   */
  public boolean hasMore()
  {
    if (index < size())
    {
      return true;
    }
    return false;
  }

  /**
   * Sets the index to the first Synapse in the list.
   * @param    none
   * @return   none
   * @see #synapse()
   */
  public void start()
  {
    index = 0;
  }

  // **************************************************************************
  // 
  // **************************************************************************
  /**
   * Returns a snypase list of sorted synapse sources.
   * @return  a sorted synapse list, first input neurons sources, then
   * output neurons sources, then hidden neurons sources
   */
  public SynapseList sort()
  {
    SynapseList returnList = new SynapseList();
    LinkedList inputNeuronsList = new LinkedList();
    LinkedList outputNeuronsList = new LinkedList();
    LinkedList hiddenNeuronsList = new LinkedList();

    for(start();hasMore();next())
    {
      if(synapse().getSource().getNeuronType() == NeuronType.INPUT)
      {
        inputNeuronsList.add(synapse());
      }
    }

    for(start();hasMore();next())
    {
      if(synapse().getSource().getNeuronType() == NeuronType.OUTPUT)
      {
        outputNeuronsList.add(synapse());
      }
    }

    for(start();hasMore();next())
    {
      if(synapse().getSource().getNeuronType() == NeuronType.HIDDEN)
      {
        hiddenNeuronsList.add(synapse());
      }
    }

    Collections.sort(inputNeuronsList, comparator);

    Collections.sort(outputNeuronsList, comparator);

    Collections.sort(hiddenNeuronsList, comparator);

    for(int i=0;i<inputNeuronsList.size();i++)
    {
      returnList.add(inputNeuronsList.get(i));
    }
    for(int i=0;i<outputNeuronsList.size();i++)
    {
      returnList.add(outputNeuronsList.get(i));
    }
    for(int i=0;i<hiddenNeuronsList.size();i++)
    {
      returnList.add(hiddenNeuronsList.get(i));
    }

    return returnList;
  }

  // **************************************************************************
  // output functions
  // **************************************************************************

  /**
   * Human readable representation of the list. For every synapse invoke toString
   * and return the stuff.
   * @param    none
   * @return   String - human readable representation
   */
  public String toString()
  {
    String s = new String();
    for(start();hasMore();next())
    {
      s = s.concat(synapse().toString() + "\n");
    }
    return s;
  }
  
  // **************************************************************************
  // self test
  // **************************************************************************
  
  /**
   * For Selfstest. Do <b> not call </b> as class method. <b> <i> not
   * implemented yet </i> </b>
   * @param    none
   * @return   none
   */
  public static void main(String argv[])
  {
    System.out.println("Not implemented yet");
    SynapseList sl = new SynapseList();
    for(sl.start(); sl.hasMore(); sl.next())
    {
      System.out.println("Checking a synapse");
    }

  }
  
}
