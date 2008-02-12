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
/**
 *  Implementation of Synapse-mode-enum. A synapse can be "conventional", that
 *  is, the strength is fixed, or "dynamic", that is, it is dependent of the
 *  innner states of the source and destination neuron. This is part of the
 *  apdation of learning of the neural net.
 *  For use see Cholsey.NeuronType
 *  @see cholsey.NeuronType
 */
public final class SynapseMode
{
  /** Synapse has static strength */
  public final static int SYNAPSE_MODE_CONVENTIONAL = 0;
  /** Synapse strength is dependent of inner states of source and destination
   * neuron */
  public final static int SYNAPSE_MODE_DYNAMIC      = 1;
  private int mode;
  private String name;
  private SynapseMode(String nm, int m) {name=nm; mode=m;};
  /**
   * Returns a human readable representation of the SynapseMode.
   * @param    none
   * @return   String string, human readable representation
   */
  public String toString() {return name;};
  /**
   * Returns a XML-valid readable representation of the SynapseMode.
   * @param    none
   * @return   String xml, XML-valid representation
   */
  public String toXML()
  {
    switch(mode)
    {
      case SYNAPSE_MODE_DYNAMIC:
        return "dynamic";
      case SYNAPSE_MODE_CONVENTIONAL:
        return "conventional";
    }
    return null;
  }
  /**
   * Returns the mode of this neuron/synapse. Return value is one of the above
   * constants.
   * @param    none
   * @return   int mode
   * @see #SYNAPSE_MODE_CONVENTIONAL
   * @see #SYNAPSE_MODE_DYNAMIC
   */
  public int mode() {return mode;};
  /**
   * Enables self-testing. <b> Should never be called as class-method!</b>.
   * Class can be executed by: java Cholsey.SynapseMode
   * @param    none
   * @return   none
   */
  public static void main(String argv[])
  {
    SynapseMode sm1 = SynapseMode.CONVENTIONAL;
    SynapseMode sm2 = SynapseMode.DYNAMIC;
    SynapseMode sm3 = SynapseMode.CONVENTIONAL;
    SynapseMode sm4 = SynapseMode.DYNAMIC;
    System.out.println("** Created sm = System.CONVENTIONAL");
    System.out.println("toString : " + sm1.toString());
    System.out.println("mode()   : " + sm1.mode());
    System.out.println("** Created sm2 = System.DYNAMIC");
    System.out.println("toString : " + sm2.toString());
    System.out.println("mode()   : " + sm2.mode());
    System.out.println("** Created sm3 = System.CONVENTIONAL");
    System.out.println("toString : " + sm3.toString());
    System.out.println("mode()   : " + sm3.mode());
    System.out.println("** Created sm4 = System.DYNAMIC");
    System.out.println("toString : " + sm4.toString());
    System.out.println("mode()   : " + sm4.mode());
    System.out.println("********** testing sm **********");
    System.out.println("sm1 == sm2: " + (sm1 == sm2));
    System.out.println("sm1 == sm3: " + (sm1 == sm3));
    System.out.println("sm1 == sm4: " + (sm1 == sm4));
    System.out.println("********** testing sm2 **********");
    System.out.println("sm2 == sm1: " + (sm2 == sm1));
    System.out.println("sm2 == sm3: " + (sm2 == sm3));
    System.out.println("sm2 == sm4: " + (sm2 == sm4));
    System.out.println("Test done.");
  }
  /** Implementation of the conventional synapse mode */
  public final static SynapseMode CONVENTIONAL = new
    SynapseMode("Conventional",SYNAPSE_MODE_CONVENTIONAL);
  /** Implementation of the dynamic synapse mode */
  public final static SynapseMode DYNAMIC = new
    SynapseMode("Dynamic",SYNAPSE_MODE_DYNAMIC);
  /** a list of all possible synapse modes */
  public final static SynapseMode[] SYNAPSE_MODE_LIST = 
  {
    CONVENTIONAL,
    DYNAMIC
  };
}
