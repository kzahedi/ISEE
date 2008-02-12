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
 *  Implements enumeration of synapse-types. The synapse type determens, wheter a
 *  synapse is an excitatory or inhibitory synapse. There is no public constructor
 *  to this class. SynapseTypes can only be used in one of the following cases:
 *  <ul>
 *  <li> NeruonType synapseType = SynapseTypes.INHIBITORY </li>
 *  <li> NeruonType synapseType = SynapseTypes.EXCITATORY </li>
 *  </ul>
 *
 */
public final class SynapseType
{
  /** Synapse is an inhibitory synapse (factor is -1).*/
  public final static int SYNAPSE_TYPE_INHIBITORY    = 0;
  /** Synapse is an excitatory synapse (factor is +1).*/
  public final static int SYNAPSE_TYPE_EXCITATORY    = 1;
  private int mode;
  private String name;
  private SynapseType(String nm, int m) 
  {
    name=nm; 
    mode=m;
  };

  /**
   * Returns a human readable representation of the SynapseType.
   * @param    none
   * @return   String string, human readable representation
   */
  public String toString() {return name;};

  /**
   * Returns a XML-valid readable representation of the SynapseType.
   * @param    none
   * @return   String xml, XML-valid representation
   */
  public String toXML()
  {
    switch(mode)
    {
      case SYNAPSE_TYPE_INHIBITORY:
        return "inhibitory";
      case SYNAPSE_TYPE_EXCITATORY:
        return "excitatory";
    }
    return null;
  }
  /**
   * Returns the type of this synapse. Return value is one of the above
   * constants.
   * @param    none
   * @return   int mode
   * @see #SYNAPSE_TYPE_EXCITATORY
   * @see #SYNAPSE_TYPE_INHIBITORY
   */
  public int type() {return mode;};


  /**
   * Returns the factor for the synapse weight of this synapse. 
   * @param    none
   * @return   -1, if inhibitory, <br> +1 if excitatory 
   * @see #SYNAPSE_TYPE_EXCITATORY
   * @see #SYNAPSE_TYPE_INHIBITORY
   */
  public int sign()
  {
    switch(mode)
    {
      case SYNAPSE_TYPE_INHIBITORY:
        return -1;
      case SYNAPSE_TYPE_EXCITATORY:
        return +1;
      default:
        System.err.println("SynapseType.sign(): UNKNOWN SynapseType");
        System.exit(-1);
    }
    return 0;
  }

  /** Impements the inhibitory synapse type. */
  public final static SynapseType INHIBITORY = new
    SynapseType("Inhibitory Synapse (-1)",SYNAPSE_TYPE_INHIBITORY);

  /** Impements the excitatory synapse type */
  public final static SynapseType EXCITATORY  = new
    SynapseType("Excitatory Synapse (+1)",SYNAPSE_TYPE_EXCITATORY);

  /** list of all possible synapse types */
  public final static SynapseType[] LIST = 
  {
    INHIBITORY,
    EXCITATORY
  };
}










