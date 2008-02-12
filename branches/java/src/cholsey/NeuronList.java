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
 * Implements a Enumeration with neuron-elements. Etends Vector
 */

package cholsey;

import java.util.HashMap;
import java.util.Vector;
/**
 *  This class implements a wrapper to the vector class. <br>
 *  Example: <br>
 *  <code>
 NeuronList neurons = net.neurons(); <br>
 for(neurons.start(); neurons.hasMore(); neurons.next()) <br>
 {<br>
 &nbsp;&nbsp;&nbsp;Neuron neuron = neurons.neuron();<br>
 &nbsp;&nbsp;&nbsp;...<br>
 }
 </code>
 *  
 */
public class NeuronList extends Vector
{

  private HashMap indices; /** HashMap for the current index positions of the accessing Threads*

  /**
   * default constructor
   */
  public NeuronList()
  {
    super();
    this.indices = new HashMap();
    this.setIndex(0);
  }

  // **************************************************************************
  // neuron functions
  // **************************************************************************
  /**
   * Returns the currently selected neuron. In a for-loop <br>
   * for(nl.start();nl.hasMore();nl.next()) <br>
   * {<br>
   * &nbsp;&nbsp; Neuron n = nl.neuron();<br>
   * }
   * @param    none
   * @return   the current selected neuron
   */
  public Neuron neuron()
  {
    int index = this.getIndex();
    return (Neuron)elementAt(index);
  }
  /**
   * Returns the current neuron and increases the index.
   * @param    none
   * @return   the current neuron
   * @see #neuron()
   */
  public Neuron next()
  {
    Neuron returnNeuron;
    int    index       = this.getIndex();



    if(index < size())
    {
      returnNeuron = (Neuron)elementAt(index);
      index++;
      this.setIndex(index);
      return returnNeuron;
    }
    return null;
  }

  /**
   * Returns the i-th neuron. This function is a wrapper to the
   * Vector.elementAt(i) function.
   * @param    index int index, the index of the wanted neuron
   * @return   Neuron neuron, the neuron selected
   */
  public Neuron neuron(int index)
  {
    return (Neuron)elementAt(index);
  }
  // **************************************************************************
  // flow control functions
  // **************************************************************************
  /**
   * Returns true if the end of the list is not reached yet.
   * @param    none
   * @return   true, if there a still some neurons to follow, false otherwise
   * @see #neuron()
   */
  public boolean hasMore()
  {
    int index = this.getIndex();
    if (index < size())
    {
      return true;
    }
    return false;
  }

  /**
   * Sets the index to the first Neuron in the list.
   * @param    none
   * @return   none
   * @see #neuron()
   */
  public void start()
  {
    this.setIndex(0);
  }

  // **************************************************************************
  // output functions
  // **************************************************************************

  /**
   * Human readable representation of the list. For every neuron invoke toString
   * and return the stuff.
   * @param    none
   * @return   String - human readable representation
   */
  public String toString()
  {
    String s = new String();
    for(int i=0;i<size();i++)
    {
      Neuron n = neuron(i);
      s = s.concat(n.toString() + "\n");
    }
    return s;
  }

  // **************************************************************************
  // worker methods
  // **************************************************************************

  /**
   *  Remove stopped Threads form the indices HashMap
   */
  private synchronized void cleanUpIndices()
  {
    Thread[] keys = (Thread[])this.indices.keySet().toArray(new Thread[0]);

    for (int i = 0; i < keys.length; i++)
    {
      if (keys[i] == null ||
          !keys[i].isAlive())
      {
        this.indices.remove(keys[i]);
      }
    }
  }

  /**
   * returns the current index for the active Thread
   */
  private synchronized int getIndex()
  {
    Thread thread = Thread.currentThread();

    this.cleanUpIndices();

    if (this.indices.containsKey(thread))
    {
      return ((Integer)this.indices.get(thread)).intValue();
    }
    else
    {
      this.indices.put(thread, new Integer(0));
      return 0;
    }
  }

  /**
   * sets the index for the active Thread to the given index
   */
  private synchronized void setIndex(int index)
  {
    Thread thread = Thread.currentThread();

    this.cleanUpIndices();

    this.indices.put(thread, new Integer(index));
  }

  // **************************************************************************
  // self test
  // **************************************************************************

  /**
   * For Selfstest. Do <b> not call </b> as class method.
   * @param    none
   * @return   none
   */
  public static void main(String args[])
  {
    NeuronList       nl = new NeuronList();

    for(int i=0;i<100;i++)
    {
      Neuron n = new Neuron(i);
      nl.add(n);
    }

    for (int i = 0; i < 50; i ++)
    {
      new CallerThread(nl).start();
    }


  }

  static class CallerThread extends Thread
  {
    private NeuronList nl;

    public CallerThread(NeuronList nl)
    {
      this.nl = nl;
    }

    public void run()
    {
      for(nl.start();nl.hasMore();nl.next())
      {
        /*  try
            {
            Thread.sleep((long)(Math.random() * 10));
            }
            catch (Exception e)
            {
        // Nothing
        }*/
        Neuron n = nl.neuron();
        System.out.println("T-Name " + this.getName() + " N-ID " + n.id());
      }
    }
  }

}
