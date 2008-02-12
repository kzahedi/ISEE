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

import javax.swing.tree.DefaultMutableTreeNode;


/**
 *  Implementation of a Synapse. A Synapse has a source neuron, destination
 *  neuron, and a strength. The strength can be fixed or depedending on the
 *  source and destination neuron. The synapse is associated with the
 *  destination neuron.
 *  @see cholsey.Neuron
 */
public class Synapse 
{
  protected double      strength      = 0; // for conventional mode
  private Neuron      destination   = null;
  private Neuron      source        = null;
  private ProcessMode processMode   = ProcessMode.DYNAMIC;
  private SynapseMode synapseMode   = SynapseMode.CONVENTIONAL;
  private SynapseType synapseType   = SynapseType.INHIBITORY;


  /**
   *  default constructor
   *  @param    none
   */
  public Synapse()
  {
  }

  /**
   * Construct with all the needed information.
   * @param    source Neuron source, a ref to the source neuron
   * @param    destination Neuron destination, a ref to the destination neuron
   * @param    strength double strength, the (static) strength of the synapse
   * @param    processMode ProcessMode processMode, the processmode of the
   * synapse
   * @see #strength()
   * @see #setSynapseMode(SynapseMode synapseMode)
   */
  public Synapse(Neuron source, Neuron destiantion, double strength, ProcessMode
      processMode)
  {
    setSourceDestination(source, destiantion);
    setStrength(strength);
    setProcessMode(processMode);
  }

  /**
   * Construct with all the needed information.
   * @param    source Neuron source, a ref to the source neuron
   * @param    destination Neuron destination, a ref to the destination neuron
   * @param    processMode ProcessMode processMode, the processmode of the
   * @param    synapseType SynapseType synapseType
   * synapse
   * @see #setSynapseMode(SynapseMode synapseMode)
   * @see cholsey.SynapseType
   */
  public Synapse(Neuron source, Neuron destiantion, ProcessMode processMode,
      SynapseType synapseType)
  {
    setSourceDestination(source, destiantion);
    //setStrength(strength);
    setProcessMode(processMode);
    setSynapseType(synapseType);
  }


  // **************************************************************************
  // SynapseMode functions
  // **************************************************************************
  /**
   *  Sets the synapse mode. That is, if the 
   *  @param    SynapseMode synapseMode
   *  @return   none
   *  @see cholsey.SynapseMode
   */
  public void setSynapseMode(SynapseMode synapseMode)
  {
    this.synapseMode = synapseMode; // only reference so all synapses in the net
                                    // have the same mode!!
  }

  /**
   *  Returns the SynapseMode. 
   *  @param    none
   *  @return   SynapseMode synapseMode, the current SynapseMode
   *  @see cholsey.SynapseMode
   */
  public SynapseMode mode()
  {
    return synapseMode;
  }

  // **************************************************************************
  // Neuron functions
  // **************************************************************************
  /**
   *  Sets the source neuron of the synapse. The source neuron must be a valid
   *  neuron of the overlieing net
   *  @param    Neuron source
   *  @return   none
   *  @see cholsey.Net
   *  @see cholsey.Neuron
   */
  public void setSourceNeuron(Neuron source)
  {
    this.source = source;
  }

  /**
   *  Sets the destination neuron of the synapse. The source neuron must be a valid
   *  neuron of the overlieing net
   *  @param    Neuron destination
   *  @return   none
   *  @see cholsey.Net
   *  @see cholsey.Neuron
   */
  public void setDestinationNeuron(Neuron destination)
  {
    this.destination = destination;
  }

  /**
   *  Sets the source and destionation neuron of the synapse. 
   *  @param    Neuron source, Neuron destination
   *  @return   none
   *  @see #setSourceNeuron(Neuron source)
   *  @see #setDestinationNeuron(Neuron destination)
   */
  public void setSourceDestination(Neuron source, Neuron destination)
  {
    setSourceNeuron(source);
    setDestinationNeuron(destination);
  }

  /**
   *  Return the source neuron.
   *  @param    none 
   *  @return   Neuron source, the source neuron of the synapse
   *  @see cholsey.Neuron
   */
  public Neuron getSource()
  {
    return source;
  }

  /**
   *  Return the destination neuron.
   *  @param    none
   *  @return   Neuron destination, the destination neuron of the synapse
   *  @see cholsey.Neuron
   */
  public Neuron getDestination()
  {
    return destination;
  }

  // **************************************************************************
  // processMode functions
  // **************************************************************************
  /**
   * Sets the process-mode of this neuron.
   * @param    processMode ProcessMode processMode, the process mode of this
   * neuron
   * @return   none
   * @see cholsey.ProcessMode
   */
  public void setProcessMode(ProcessMode processMode)
  {
    this.processMode = processMode;
  }
  /**
   * Returns the process-mode of this neuron.
   * @param    none
   * @return   ProcessMode processMode, the process mode of this neuron
   * @see cholsey.ProcessMode
   */
  public ProcessMode getProcessMode()
  {
    return processMode;
  }

  // **************************************************************************
  // synapse type functions
  // **************************************************************************

  /**
   *  Returns a type of the synapse.
   *  @return   SynapseType synapseType
   *  @see cholsey.SynapseType 
   */
  public SynapseType type()
  {
    return synapseType;
  }

  /**
   *  Sets the SynapseType of this synapse.
   *  @param   SynapseType synapseType
   *  @see cholsey.SynapseType 
   */
  public void setSynapseType(SynapseType synapseType)
  {
    this.synapseType = synapseType;
  }

  // **************************************************************************
  // strength functions
  // **************************************************************************
  /**
   *  Returns a mode dependent strength of the synapse.
   *  If the synapse mode is dynamic, the product of transmitter and receptors
   *  is returned, else the assigned (static) strength
   *  @param    none
   *  @return   double strength
   *  @see cholsey.Neuron 
   *  @see cholsey.SynapseMode
   */
  public double strength()
  {
    switch (synapseMode.mode())
    {
      case SynapseMode.SYNAPSE_MODE_CONVENTIONAL:
        return strength;
      case SynapseMode.SYNAPSE_MODE_DYNAMIC:
        return source.getTransmitterLevel() 
          * destination.getReceptorLevel() 
          * synapseType.sign();
      default: // error
        System.out.println("Synapse: UKNOWN MODE in strength()");
        System.exit(-1);
        return -1d;
    }
  }
  /**
   *  Sets the (static) strength of the connection.
   *  @param    double strength, (static) strength of connection
   *  @return   none
   *  @see #strength()
   */
  public void setStrength(double strength)
  {
    this.strength = strength;
  }

  // **************************************************************************
  // output functions
  // **************************************************************************
  /**
   *  Returns the strength in XML-valid string
   *  @param    none
   *  @return   String xml, Strength="&lt;value&gt;"
   */
  public String toString()
  {
    switch (synapseMode.mode())
    {
      case SynapseMode.SYNAPSE_MODE_CONVENTIONAL:
        return new String("Strength=\"" + strength +"\"");
      case SynapseMode.SYNAPSE_MODE_DYNAMIC:
        return new String("Strength=\"" + Math.abs(strength) + "\"");
        //return new String("Strength=\"0\"");
      default: // error
        System.out.println("Synapse: UKNOWN MODE in toString()");
        System.exit(-1);
        return new String();
    }
  }

  public void toggle()
  {
    strength *= -1;
    switch(synapseType.type())
    {
      case SynapseType.SYNAPSE_TYPE_EXCITATORY:
        synapseType = SynapseType.INHIBITORY;
        break;
      case SynapseType.SYNAPSE_TYPE_INHIBITORY:
        synapseType = SynapseType.EXCITATORY;
        break;
      default: // error
        System.out.println("Synapse: UKNOWN TYPE in toggle()");
        System.exit(-1);
    }
  }
  /**
   *  Returns a XML-valid representation of the synapse.
   *  @param    none
   *  @return   String xml, XMl-valid representation
   */
  public String toXML()
  {
    return new String("<Synapse Source=\"" + getSource().id() + "\" "
        + "Strength=\"" + strength() + "\" "
        + "Process=\"" + getProcessMode().toXML() +"\" "
        + "SynapseType=\"" + synapseType.toXML() + "\" "
        + "/>");
  }

  /**
   * Returns an DefaultMutableTreeNode, which is an JTree-valid representation
   * of the Synapse.
   * @param    none
   * @return   DefaultMutableTreeNode node, JTree-valid representation
   */
  public DefaultMutableTreeNode toJTreeNode()
  {
    String sourceString = null;
    String destinationString = null;
    switch(getSource().getNeuronType().type())
    {
      case NeuronType.NEURON_TYPE_INPUT:
        sourceString = new String("<font color=#ef0000>");
        break;
      case NeuronType.NEURON_TYPE_OUTPUT:
        sourceString = new String("<font color=#00a000>");
        break;
      case NeuronType.NEURON_TYPE_READ_BUFFER:
        sourceString = new String("<font color=#ff00ff>");
        break;
    case NeuronType.NEURON_TYPE_HIDDEN:
        sourceString = new String("<font color=#0000ff>");
        break;
    }
    switch(getDestination().getNeuronType().type())
    {
      case NeuronType.NEURON_TYPE_INPUT:
        destinationString = new String("<font color=#ef0000>");
        break;
      case NeuronType.NEURON_TYPE_OUTPUT:
        destinationString = new String("<font color=#00a000>");
        break;
      case NeuronType.NEURON_TYPE_READ_BUFFER:
        destinationString = new String("<font color=#ff00ff>");
        break;
      case NeuronType.NEURON_TYPE_HIDDEN:
        destinationString = new String("<font color=#0000ff>");
        break;
    }


    DefaultMutableTreeNode synapseTreeNode = null;
    
    if(synapseMode == SynapseMode.CONVENTIONAL)
    {
      synapseTreeNode = new DefaultMutableTreeNode(
          "<html>"
          + "Synapse " 
          + sourceString
          + (getSource().id()+1)
          + "</font>"
          + " -> " 
          + destinationString
          + (getDestination().id()+1) 
          + "</font>"
          + " (" 
          + strength()
          + ")");
    }
    else
    {
      String synapseColor = "<font color=";
      if(synapseType == SynapseType.INHIBITORY)
      {
        synapseColor = synapseColor + "#0000ff>";
      }
      else
      {
        synapseColor = synapseColor + "#ff0000>";
      }
      synapseTreeNode = new DefaultMutableTreeNode(
          "<html>"
          + synapseColor
          + "Synapse " 
          + "</font>"
          + sourceString
          + (getSource().id()+1)
          + "</font>"
          + " -> " 
          + destinationString
          + (getDestination().id()+1) 
          + "</font>"
          + " (" 
          + type().sign()
          + ")");

    }
    return synapseTreeNode;
  }


  // **************************************************************************
  // testfuncton
  // **************************************************************************
  /**
   *  Enables self-testing. <b> do not call as class-method!</b>. Class can be
   *  tested by: java Cholsey.Synapse
   *  @param    none
   *  @return   none
   */
  public static void main(String argv[])
  {
    Neuron a = new Neuron(0);
    Neuron b = new Neuron(1);
    Synapse s = new Synapse();
    System.out.println("Created Neuron a, b and Synapse s");
    System.out.println("SynapseMode is " + s.mode().toString());
    System.out.println("setting synapse to a and b");
    s.setSourceDestination(a,b);
    System.out.println("setting strength to 5.6");
    s.setStrength(5.6);
    System.out.println("s.toString() = \"" + s.toString() + "\"");
    System.out.println("s.strength() = " + s.strength());
    System.out.println("setting a.setTransmitterLevel(5) and" +
        " b.setReceptorLevel(2) and SynapseMode to Dynamic");
    a.setTransmitterLevel(5);
    b.setReceptorLevel(2);
    s.setSynapseMode(SynapseMode.DYNAMIC);
    System.out.println("setting a.setNeuronType(NeuronType.OUTPUT)");
    a.setNeuronType(NeuronType.OUTPUT);
    System.out.println("SynapseMode is " + s.mode().toString());
    System.out.println("s.strength() = " + s.strength());
    System.out.println("s.toString() = \"" + s.toString() + "\"");
    System.out.println("setting a.setNeuronType(NeuronType.INPUT)");
    a.setNeuronType(NeuronType.INPUT);
    System.out.println("SynapseMode is " + s.mode().toString());
    System.out.println("s.strength() = " + s.strength());
    System.out.println("s.toString() = \"" + s.toString() + "\"");
    System.out.println("TESTING");
    s.setSynapseMode(SynapseMode.CONVENTIONAL);
    s.setStrength(-2.4);
    System.out.println("strength() = " + s.strength());
    s.setSynapseMode(SynapseMode.DYNAMIC);
    System.out.println("transmitter = " + s.source.getTransmitterLevel());
    System.out.println("receptors   = " + s.destination.getReceptorLevel());
    System.out.println("strength()  = " + s.strength());
    System.out.println("Test done.");
  }
}
