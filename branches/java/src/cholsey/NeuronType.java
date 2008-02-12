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


package cholsey;

import java.util.Vector;

/**
 *  Implements enumeration of neuron-types. The neuron type determens, wheter a
 *  neuron is an input, output or hidden neuron. There is no public constructor
 *  to this class. NeuronTypes can only be used in one of the following cases:
 *  <ul>
 *  <li> NeruonType neuronType = NeuronTypes.HIDDEN </li>
 *  <li> NeruonType neuronType = NeuronTypes.INPUT </li>
 *  <li> NeruonType neuronType = NeuronTypes.OUTPUT </li>
 *  </ul>
 *
 */
public final class NeuronType
{
  /** Neuron is a input neuron, part of the hidden layer. Input neurons are
   * just buffers */
  public final static int NEURON_TYPE_INPUT       = 0;
  /** Neuron is a hidden neuron, part of the hidden layer */
  public final static int NEURON_TYPE_HIDDEN      = 1;
  /** Neuron is a output neuron, part of the output layer. Output neurons are
   * the controller neuron. Their output will be passed directly to the roboter */
  public final static int NEURON_TYPE_OUTPUT      = 2;
  public final static int NEURON_TYPE_READ_BUFFER = 3;
  private int mode;
  private String name;
  private NeuronType(String nm, int m) {name=nm; mode=m;};
  /**
   * Returns a human readable representation of the NeuronType.
   * @param    none
   * @return   String string, human readable representation
   */
  public String toString() {return name;};
  /**
   * Returns a XML-valid readable representation of the NeuronType.
   * @param    none
   * @return   String xml, XML-valid representation
   */
  public String toXML()
  {
    switch(mode)
    {
      case NEURON_TYPE_INPUT:
        return "input";
      case NEURON_TYPE_OUTPUT:
        return "output";
      case NEURON_TYPE_HIDDEN:
        return "hidden";
      case NEURON_TYPE_READ_BUFFER:
        return "read-buffer";
    }
    return null;
  }
  /**
   * Returns the type of this neuron. Return value is one of the above
   * constants.
   * @param    none
   * @return   int mode
   * @see #NEURON_TYPE_HIDDEN
   * @see #NEURON_TYPE_OUTPUT
   * @see #NEURON_TYPE_INPUT
   * @see #NEURON_TYPE_READ_BUFFER
   */
  public int type() {return mode;};
  /**
   * Enables self-testing. <b> Should never be called as class-method!</b>.
   * Class can be executed by: java Cholsey.NeuronType
   * @param    none
   * @return   none
   */
  public static void main(String argv[])
  {
    Vector pm = new Vector();
    NeuronType p1 =  NeuronType.HIDDEN;
    NeuronType p2 =  NeuronType.INPUT;
    NeuronType p3 =  NeuronType.OUTPUT;
    NeuronType p4 =  NeuronType.HIDDEN;
    NeuronType p5 =  NeuronType.INPUT;
    NeuronType p6 =  NeuronType.OUTPUT;
    pm.add(p1);
    pm.add(p2);
    pm.add(p3);
    pm.add(p4);
    pm.add(p5);
    pm.add(p6);
    for(int i=0;i<pm.size();i++)
    {
      NeuronType a = (NeuronType)pm.elementAt(i);
      for(int j=0;j<pm.size();j++)
      {
        NeuronType b = (NeuronType)pm.elementAt(j);
        System.out.println(a.toString() + "(" +i+") == " + b.toString() +
            "("+j+"): " + (a==b));
      }
    }
    System.out.println("Test done.");

  }
    /** Impements the input neuron type. A input neuron is only a buffer! No
     * trasferfunction is performed on the activation */
  public final static NeuronType INPUT = new
    NeuronType("Input-Neuron ( BUFFER )",NEURON_TYPE_INPUT);
    /** Impements the hidden neuron type */
  public final static NeuronType HIDDEN  = new
    NeuronType("Hidden-Neuron",NEURON_TYPE_HIDDEN);
    /** Impements the output neuron type */
  public final static NeuronType OUTPUT    = new
    NeuronType("Output-Neuron",NEURON_TYPE_OUTPUT);
  public final static NeuronType READ_BUFFER    = new
    NeuronType("Read-Buffer-Neuron",NEURON_TYPE_READ_BUFFER);

  /** list of all possible neuron types */
  public final static NeuronType[] LIST = 
  {
    INPUT,
    OUTPUT,
    READ_BUFFER,
    HIDDEN
  };
}










