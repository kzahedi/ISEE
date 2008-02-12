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
 *  Implements enumeration of Process-modes. The processmode determens, wheter a
 *  neuron or synapse can be deleted or/and varied during the evolution process.
 *  For use see Cholsey.NeuronType
 *  @see cholsey.NeuronType
 */
public final class ProcessMode
{
  /** Neuron or Synapse can not be varied in values (bias, strength) */
  public final static int PROCESS_STATIC    = 0;
  /** Neuron or Synapse can not be deleted */
  public final static int PROCESS_CONSISTENT = 1;
  /** Neuron or Synapse can be deleted and varied in values */
  public final static int PROCESS_DYNAMIC   = 2;
  private int mode;
  private String name;
  private ProcessMode(String nm, int m) {name=nm; mode=m;};
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
    switch (mode)
    {
      case PROCESS_DYNAMIC:
        return "dynamic";
      case PROCESS_CONSISTENT:
        return "consistent";
      case PROCESS_STATIC:
        return "static";
    }
    return null;
  }
  /**
   * Returns the mode of this neuron/synapse. Return value is one of the above
   * constants.
   * @param    none
   * @return   int mode
   * @see #PROCESS_DYNAMIC
   * @see #PROCESS_STATIC
   * @see #PROCESS_CONSISTENT
   */
  public int mode() {return mode;};
  /**
   * Enables self-testing. <b> Should never be called as class-method!</b>.
   * Class can be executed by: java Cholsey.ProcessMode
   * @param    none
   * @return   none
   */
  public static void main(String argv[])
  {
    Vector pm = new Vector();
    ProcessMode p1 =  ProcessMode.STATIC;
    ProcessMode p2 =  ProcessMode.DYNAMIC;
    ProcessMode p3 =  ProcessMode.CONSISTENT;
    ProcessMode p4 =  ProcessMode.STATIC;
    ProcessMode p5 =  ProcessMode.DYNAMIC;
    ProcessMode p6 =  ProcessMode.CONSISTENT;
    pm.add(p1);
    pm.add(p2);
    pm.add(p3);
    pm.add(p4);
    pm.add(p5);
    pm.add(p6);
    for(int i=0;i<pm.size();i++)
    {
      ProcessMode a = (ProcessMode)pm.elementAt(i);
      for(int j=0;j<pm.size();j++)
      {
        ProcessMode b = (ProcessMode)pm.elementAt(j);
        System.out.println(a.toString() + "(" +i+") == " + b.toString() +
            "("+j+"): " + (a==b));
      }
    }
    System.out.println("Test done.");

  }
  /** Implements the static process mode */
  public final static ProcessMode STATIC = new
    ProcessMode("Static",PROCESS_STATIC);
  /** Implements the consitent process mode */
  public final static ProcessMode CONSISTENT = new
    ProcessMode("Consistent",PROCESS_CONSISTENT);
  /** Implements the dynamic process mode */
  public final static ProcessMode DYNAMIC = new
    ProcessMode("Dynamic",PROCESS_DYNAMIC);

  /** list of all possible process modes */
  public final static ProcessMode[] LIST =
  {
    STATIC,
    CONSISTENT,
    DYNAMIC
  };
}

