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

import java.util.Random;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;


/**
 *  Net implements the (extended) neural network. The net has 2 possible mode.
 *  This first mode is the conventional mode. The strength of the synapse is
 *  determined by the xml file and is not variable. The second mode has
 *  dynamic synapses. The strength of the synapse is determined by the level
 *  of transmitters and receptors in the source and destination neuron <br>
 *
 *  A Neural Net has the following stucture and concept. The Net itself does
 *  nothing more than handling the neurons, that is, is knows which neurons the
 *  network has, what transferfunction is used. The actualy processing is done
 *  in the neurons. <br>
 *
 *  Synapses are always associated with the destination neuron. <br>
 *
 */
public class Net 
{
  private static LearningRuleClassLoader learningRuleClassLoader = new
    LearningRuleClassLoader();
  // **************************************************************************
  // constants
  // **************************************************************************
  private final static double MIN_INIT_ACTIVITY    =  -1;
  private final static double MAX_INIT_ACTIVITY    =  1;

  private final static double MIN_INIT_TRANSMITTER =  0.0;
  private final static double MAX_INIT_TRANSMITTER =  0.5;

  private final static double MIN_INIT_RECEPTOR    =  0.0;
  private final static double MAX_INIT_RECEPTOR    =  0.5;
  // **************************************************************************
  // init values
  // **************************************************************************
  private Transferfunction transferfunction  = Transferfunction.TANH;
  private SynapseMode      synapseMode       = SynapseMode.CONVENTIONAL;
  private Vector           initialActivies   = new Vector();
  private NeuronList       inputNeurons      = new NeuronList();
  private NeuronList       outputNeurons     = new NeuronList();
  private NeuronList       readBufferNeurons = new NeuronList();
  private NeuronList       hiddenNeurons     = new NeuronList();
  private Vector           properties        = new Vector();
  private String           learningRuleName  = null;

  protected NeuronList     neurons           = new NeuronList();

  private Random random = new Random(); // initialised by current time


  /**
   * default constructor. <br> Transferfunction is sigm. <br> SynapseMode is
   * conventional. <br> no neurons <br> no synapses <br>
   * @see cholsey.Transferfunction
   * @see cholsey.SynapseMode
   */
  public Net()
  { };


 // **************************************************************************
  // init functions
  // **************************************************************************

  /**
   * Initialises all neuron activties randomly. The initial activity for each
   * neuron is determined randomly between -5 and 5.
   */
  public void randomInitActivity()
  {
    for(neurons.start();neurons.hasMore();neurons.next())
    {
      Neuron neuron = neurons.neuron();
      if(neuron.getNeuronType() == NeuronType.INPUT)
      {
        continue;
      }
      double activity = random.nextDouble() * (MAX_INIT_ACTIVITY -
          MIN_INIT_ACTIVITY) + MIN_INIT_ACTIVITY;
      double receptor = random.nextDouble() * (MAX_INIT_RECEPTOR -
          MIN_INIT_RECEPTOR) + MIN_INIT_RECEPTOR;
      double transmitter = random.nextDouble() * (MAX_INIT_TRANSMITTER -
          MIN_INIT_TRANSMITTER) + MIN_INIT_TRANSMITTER;
      neuron.setActivation(activity);
      neuron.setTransmitterLevel(transmitter);
      neuron.setReceptorLevel(receptor);
      initialActivies.add(new Double(activity));
      initialActivies.add(new Double(transmitter));
      initialActivies.add(new Double(receptor));
    }
  }

  /**
   * Sets the activities of all neurons to the initial values, such as
   * activation, receptor and transmitter level.
   */
  public void resetInitialActivities()
  {
    if(initialActivies == null || initialActivies.size() == 0)
    {
      return;
    }
    int i=0;
    for(neurons.start();neurons.hasMore();neurons.next())
    {
      if(neurons.neuron().getNeuronType() == NeuronType.INPUT)
      {
        continue;
      }
      neurons.neuron().setActivation(
          ((Double)initialActivies.elementAt(i)).doubleValue());
      neurons.neuron().setTransmitterLevel(
          ((Double)initialActivies.elementAt(i+1)).doubleValue());
      neurons.neuron().setReceptorLevel(
          ((Double)initialActivies.elementAt(i+2)).doubleValue());
      i = i + 3;
    }
  }

  // **************************************************************************
  // synapse functions
  // **************************************************************************
  /**
   * Sets the synapse mode for every existing synapse as well as for synapse
   * added in the following.
   * @param    synapseMode SynapseMode synapseMode - The mode of the synapse
   * (dynamic, conventional)
   * @see cholsey.SynapseMode
   * @return none
   */
  public void setSynapseMode(SynapseMode synapseMode)
  {
    this.synapseMode = synapseMode;
    for(neurons().start(); neurons().hasMore(); neurons().next())
    {
      SynapseList synapses = neurons().neuron().synapses();
      neurons().neuron().setSynapseMode(synapseMode);
      if (synapses != null)
      {
        for(synapses.start();synapses.hasMore();synapses.next())
        {
          synapses.synapse().setSynapseMode(synapseMode);
        }
      }
    }
  }

  /**
   * Returns the synapse mode of the net. The Synapse mode determines wheter the
   * synapse weight is fixed or dynamic.
   * @param    none no parameter
   * @return   SynapseMode synapseMode - the mode of the synapse
   * @see cholsey.SynapseMode
   */
  public SynapseMode getSynapseMode()
  {
    return synapseMode;
  }

  /**
   * Adds a new Synapse the the net. The source and the destination neurons must
   * be added the the net before adding a connection synapse. The synapse is
   * directed from source to destnation. The synapse is always associated with
   * the destination neuron.
   * @param    source The source neuron
   * @param    destination The destination neuron
   * @param    strength The synapse weight
   * @param    processMode The ProcessMode, needed by the evalution programm
   * @return   The New Synapse
   * @see cholsey.ProcessMode
   * @see cholsey.Neuron
   * @see cholsey.Synapse
   */
  public Synapse addSynapse(Neuron source, Neuron destination, double strength,
      ProcessMode processMode)
  {
    Synapse synapse = new Synapse(source, destination, strength, processMode);
    synapse.setSynapseMode(synapseMode);
    destination.addSynapse(synapse);
    return synapse;
  }

  /**
   * Adds a new Synapse the the net. The source and the destination neurons must
   * be added the the net before adding a connection synapse. The synapse is
   * directed from source to destnation. The synapse is always associated with
   * the destination neuron.
   * @param    source The source neuron
   * @param    destination The destination neuron
   * @param    processMode The ProcessMode, needed by the evalution programm
   * @param    synapseType The SynapseType
   * @return   The New Synapse
   * @see cholsey.ProcessMode
   * @see cholsey.Neuron
   * @see cholsey.Synapse
   * @see cholsey.SynapseType
   */
  public Synapse addSynapse(Neuron source, Neuron destination, 
      ProcessMode processMode, SynapseType synapseType)
  {
    Synapse synapse = new Synapse(source, destination, processMode,
        synapseType);
    synapse.setSynapseMode(synapseMode);
    destination.addSynapse(synapse);
    return synapse;
  }


  /**
   * Adds a default Synapse the the net. ProcessMode is set to DYNAMIC
   * @see #addSynapse(Neuron source, Neuron destination, double strength,
   * ProcessMode processMode)
   */

  public Synapse addSynapse(Neuron source, Neuron destination, double strength)
  {
    return addSynapse(source, destination, strength, ProcessMode.DYNAMIC);
  }

  public void delSynapse(Neuron source, Neuron destination)
  {
    destination.removeSynapse(source);
  }

  public void delSynapse(int sourceIndex, int destinationIndex)
  {
    neurons().neuron(destinationIndex).removeSynapse(
        neurons().neuron(sourceIndex));

  }


  public int getSynapseCount()
  {
    int synapseCount = 0;
    if(neurons == null)
    {
      return 0;
    }
    for(neurons.start();neurons.hasMore();neurons.next())
    {
      SynapseList synapseList = neurons.neuron().synapses();
      if(synapseList != null)
      {
        synapseCount += neurons.neuron().synapses().size();
      }
    }
    return synapseCount;
  }

  // **************************************************************************
  // transferfunction functions
  // **************************************************************************
  /**
   * Sets the transferfunction of the net.
   * The transferfunction is valid for the
   * hole net. <b> use only </b> when all neurons are added, because the
   * transferfunction is passed to all neurons. if a neuron is added later, it
   * might have a different transferfunction
   *
   * @param    transferfunction a valid transferfunction of type
   * Transferfunction
   */
  public void setTransferfunction(Transferfunction transferfunction)
  {
    this.transferfunction = transferfunction;
    for(neurons.start();neurons.hasMore();neurons.next())
    {
      Neuron neuron = neurons.neuron();
      neuron.setTransferfunction(transferfunction);
    }
  }

  /**
   * Returns the assigned transferfunction
   * @param    none no parameter
   * @return   transferfunction - the assigned transferfunction of type
   * Transferfunction
   */
  public Transferfunction getTransferfunction()
  {
    return transferfunction;
  }

  // **************************************************************************
  // properties functions
  // **************************************************************************

  public void setProperties(Vector properties)
  {
    this.properties = properties;
  }

  public Vector getProperties()
  {
    return properties;
  }

  // **************************************************************************
  // neuron parameter 
  // **************************************************************************

  public void setAlpha(double alpha)
  {
    for(neurons().start(); neurons().hasMore(); neurons.next())
    {
      neurons().neuron().setAlpha(alpha);
    }
  }

  public void setBeta(double beta)
  {
    for(neurons().start(); neurons().hasMore(); neurons.next())
    {
      neurons().neuron().setBeta(beta);
    }
  }

  public void setGamma(double gamma)
  {
    for(neurons().start(); neurons().hasMore(); neurons.next())
    {
      neurons().neuron().setGamma(gamma);
    }
  }

  public void setDelta(double delta)
  {
    for(neurons().start(); neurons().hasMore(); neurons.next())
    {
      neurons().neuron().setDelta(delta);
    }
  }

  public void setKappa(double kappa)
  {
    for(neurons().start(); neurons().hasMore(); neurons.next())
    {
      neurons().neuron().setKappa(kappa);
    }
  }


  // **************************************************************************
  // neuron functions
  // **************************************************************************

  public int size()
  {
    return neurons.size();
  }
  
  /**
   * Add a default neuron. Values are : <br>
   * <ol>
   *  <li> bias = 0 </li>
   *  <li> transmitterLevel = 0 </li>
   *  <li> receptorLevel = 0 </li>
   *  <li> processMode = ProcessMode.DYNAMIC </li>
   *  <li> neuronType = NeuronType.OUTPUT </li>
   * </ol>
   * @see #addNeuron(double bias, double transmitterLevel, double receptorLevel,
   * ProcessMode process, NeuronType neuronType) 
   */
  public Neuron addNeuron()
  {
    return addNeuron(0,0,0,ProcessMode.DYNAMIC, NeuronType.OUTPUT);
  }

  /**
   * Add a default neuron of given type. Values are : <br>
   * <ol>
   *  <li> bias = 0 </li>
   *  <li> transmitterLevel = 0 </li>
   *  <li> receptorLevel = 0 </li>
   *  <li> processMode = ProcessMode.DYNAMIC </li>
   * </ol>
   * @see #addNeuron(double bias, double transmitterLevel, double receptorLevel,
   * ProcessMode process, NeuronType neuronType) 
   */
  public Neuron addNeuron(NeuronType neuronType)
  {
    return addNeuron(0,0,0,ProcessMode.DYNAMIC, neuronType);
  }


  /**
   * Adds a neuron to the current net. Neuron must be valid. The Neuron must
   * have the same transferfunction as the rest of the net, so that it is
   * consitent
   * @param    bias double bias, the bias of the new neuron
   * @param    transmitterLevel double transmitterLevel, the transmitterLevel of
   *           the new neuron
   * @param    receptorLevel double receptorLevel, the receptorLevel of
   *           the new neuron
   * @param    process ProcessMode process, the processMode of
   *           the new neuron
   * @param    neuronType NeuronType neuronType, the neuronType of
   *           the new neuron
   * @return   none
   * @see cholsey.Neuron
   */
  public Neuron addNeuron( double bias,
            double transmitterLevel,
            double receptorLevel,
            ProcessMode process,
            NeuronType neuronType)
  {
    Neuron neuron = new Neuron(neurons.size(),
        bias,
        transmitterLevel,
        receptorLevel,
        process,
        neuronType, 
        transferfunction);
    addNeuron(neuron);
    setNewNeuronAttributes(neuron);
    return neuron;
  }
 
 
  /**
   * Adds a neuron to the net. The id is changed to the position in the neuron
   * list.
   *  @param    neuron
   */
  public void addNeuron(Neuron neuron)
  {
    NeuronType neuronType = neuron.getNeuronType();
    setNewNeuronAttributes(neuron);
    if(neuronType == NeuronType.INPUT) 
    {
      neurons.insertElementAt(neuron,inputNeurons.size());
      inputNeurons.add(neuron);
    }
    if(neuronType == NeuronType.OUTPUT) 
    {
      neurons.insertElementAt(neuron,
          inputNeurons.size() + outputNeurons.size());
      outputNeurons.add(neuron);
    }
    if(neuronType == NeuronType.READ_BUFFER) 
    {
      neurons.insertElementAt(neuron,
          inputNeurons.size() + outputNeurons.size() + readBufferNeurons.size());
      readBufferNeurons.add(neuron);
    }
    if(neuronType == NeuronType.HIDDEN) 
    {
      neurons.add(neuron);
      hiddenNeurons.add(neuron);
    }
    reindexAll();
  }

  /**
   * Any new neuron added to the net, should get the important attributes.
   * @param    Neuron neuron
   */
  private void setNewNeuronAttributes(Neuron neuron)
  {
    neuron.setTransferfunction(transferfunction);
    neuron.setSynapseMode(synapseMode);
    neuron.setLearningRule(learningRuleName);
  }

  /**
   * Deletes the neuron given by neuron.
   * @param    neuron of type Neuron
   * @see cholsey.Neuron
   */
  public void delNeuron(Neuron neuron)
  {
    int neuronIndex = neurons().indexOf(neuron);
    int neuronId = neurons().neuron(neuronIndex).id();
    for(neurons().start();neurons().hasMore();neurons().next())
    {
      // remove all synapses that have the delete neuron as source
      neurons().neuron().removeSynapse(neuron);
    }
    neurons().remove(neuronIndex);
    inputNeurons.remove(neuron);
    outputNeurons.remove(neuron);
    readBufferNeurons.remove(neuron);
    hiddenNeurons.remove(neuron);
    for(neurons().start();neurons().hasMore();neurons().next())
    {
      Neuron n = neurons().neuron();
      if(n.id() > neuronId)
      {
        n.setId(n.id()-1);
      }
    }
    reindexAll();
  }

  /**
   * Deletes the neuron given by the neuron-index.
   * @param    neuronIndex of type int, first neuron has index 0
   */
  public void delNeuron(int neuronIndex)
  {
    delNeuron(neurons.neuron(neuronIndex));
  }

  /**
   * Sets the type of this neuron. That is, "hidden", "input", "output"
   * @param    Neuron the neuron that should be changed
   * @param    neuronType NeuronType neuronType, the type of this neuron
   * @return   none
   */
  public void setNeuronType(Neuron neuron, NeuronType neuronType)
  {
    // remove
    if(neuron.getNeuronType() == NeuronType.HIDDEN)
    {
      hiddenNeurons.remove(neuron);
    }
    if(neuron.getNeuronType() == NeuronType.OUTPUT)
    {
      outputNeurons.remove(neuron);
    }
    if(neuron.getNeuronType() == NeuronType.READ_BUFFER)
    {
      readBufferNeurons.remove(neuron);
    }
    if(neuron.getNeuronType() == NeuronType.INPUT)
    {
      inputNeurons.remove(neuron);
    }

    // add
    if(neuronType == NeuronType.HIDDEN)
    {
      hiddenNeurons.add(neuron);
    }
    if(neuronType == NeuronType.OUTPUT)
    {
      outputNeurons.add(neuron);
    }
    if(neuronType == NeuronType.READ_BUFFER)
    {
      readBufferNeurons.add(neuron);
    }
    if(neuronType == NeuronType.INPUT)
    {
      inputNeurons.add(neuron);
    }
    neuron.setNeuronType(neuronType);
  }

  /**
   * Returns the list of all input neurons.
   * @param    none
   * @return   NodeList input-neurons
   */
  public NeuronList getInputNeurons()
  {
    return inputNeurons;
  }

  /**
   * Returns the list of all hidden neurons.
   * @param    none
   * @return   NodeList hidden-neurons
   */
  public NeuronList getHiddenNeurons()
  {
    return hiddenNeurons;
  }

  /**
   * Returns the list of all output neurons.
   * @param    none
   * @return   NodeList output-neurons
   */
  public NeuronList getOutputNeurons()
  {
    return outputNeurons;
  }

  /**
   * Returns the list of all read buffer neurons.
   * @param    none
   * @return   NodeList read-buffer-neurons
   */
  public NeuronList getReadBufferNeurons()
  {
    return readBufferNeurons;
  }


  /**
   * Returns a list of neurons with the specified type.
   * @param    NeuronType the required type of neurons
   * @return   NeuronList the list of wanted neurons
   */
  private NeuronList getNeuronType(NeuronType type)
  {
    NeuronList nl = new NeuronList();
    for(neurons.start();neurons.hasMore();neurons.next())
    {
      Neuron n = neurons.neuron();
      if(n.getNeuronType() == type)
      {
        nl.add(n);
      }
    }
    return nl;
  }

  /**
   * Returns the i-th neuron of the net.
   * If the index exceeds the number of neurons in the net, null is returned.
   * @param    index Int index, index of the neuron
   * @return   Neuron neuron - i-th neuron of the net
   */
  public Neuron getNeuron(int index)
  {
    if(index>=neurons.size())
    {
      return null;
    }
    return neurons.neuron(index);
  }

  /**
   * Returns the list of all neurons.
   * @return   NeuronList
   */
  public NeuronList neurons() 
  {
    return neurons;
  }

  /**
   * Sets the activation of a input neuron. Sets the Activation for a
   * input-neuron specified by the index and the value. The index should not
   * exceed the number of input neurons
   * @param    index the index of the input neuron, which activation is to be set
   * @param    value the new value for the input neuron
   */
  public void setInputNeuronValue(int index, double value)
  {
    Neuron n = inputNeurons.neuron(index);
    n.setActivation(value);
  }

  /**
   * Returns the output-value of the i-th output-neuron
   * @param    index the index of the output neuron
   * @return   the output-value if the i-th output neuron
   */
  public double getOutputNeuronValue(int index)
  {
    Neuron n = outputNeurons.neuron(index);
    return n.getOutput();
  }

  /**
   * Returns the read-buffer-value of the i-th read-buffer-neuron
   * @param    index the index of the read-buffer neuron
   * @return   the read-buffer-value if the i-th read-buffer neuron
   */
  public double getReadBufferNeuronValue(int index)
  {
    Neuron n = readBufferNeurons.neuron(index);
    return n.getOutput();
  }


  /**
   * Returns the output value of the i-th input neuron.
   * @param    index the index of the input neuron
   * @return   the output-value of the i-th input neuron
   */
  public double getInputNeuronValue(int index)
  {
    Neuron n = inputNeurons.neuron(index);
    return n.getOutput();
  }


  /**
   * Resets the index of all neurons
   */
  public void reindexAll()
  {
    for(neurons().start(); neurons().hasMore(); neurons().next())
    {
      Neuron neuron = neurons().neuron();
      neuron.setId(neurons().indexOf(neuron));
    }
  }
  
  // **************************************************************************
  // processing functions
  // **************************************************************************
  /**
   * Processes the net once.
   * For each neuron do:
   * <ul>
   * <li> update inner receptor and transmitter level (if needed) </li>
   * <li> process </li>
   * <li> update output value </li>
   * </ul>
   * @return   none
   */
  public void process()
  {
    for(neurons.start();neurons.hasMore();neurons.next())
    {
      neurons.neuron().updateActivation();
    }
    for(neurons.start();neurons.hasMore();neurons.next())
    {
      neurons.neuron().updateLearningParameter();
    }
    for(neurons.start();neurons.hasMore();neurons.next())
    {
      neurons.neuron().updateOutput();
    }
  }

  // **************************************************************************
  // copy function
  // **************************************************************************

  /**
   * Returns a copy of the net.
   * @return   a copy of the net
   */
  public Net copy()
  {
    Net net = new Net();
    int i = 0;

    //for(neurons().start(); neurons.hasMore(); neurons().next())
    for(i=0; i < neurons.size(); i++)
    {
      //Neuron oldNeuron = neurons().neuron();
      Neuron oldNeuron = neurons.neuron(i);
      Neuron newNeuron = net.addNeuron(
          oldNeuron.getBias(),
          oldNeuron.getTransmitterLevel(),
          oldNeuron.getReceptorLevel(),
          oldNeuron.getProcessMode(),
          oldNeuron.getNeuronType());
      newNeuron.setAlpha(oldNeuron.getAlpha());
      newNeuron.setBeta(oldNeuron.getBeta());
      newNeuron.setGamma(oldNeuron.getGamma());
      newNeuron.setDelta(oldNeuron.getDelta());
      newNeuron.setMy(oldNeuron.getMy());
      newNeuron.setNy(oldNeuron.getNy());
      newNeuron.setKappa(oldNeuron.getKappa());
      newNeuron.setActivation(oldNeuron.getActivation());
      newNeuron.setTransferfunction(oldNeuron.getTransferfunction());
      //newNeuron.updateOutput();
      newNeuron.setOutput(oldNeuron.getOutput());
      newNeuron.setId(oldNeuron.id());
      newNeuron.setDaleMode(oldNeuron.getDaleMode());
    }
    NeuronList newNeurons = net.neurons();
    for(i=0; i < neurons.size(); i++)
    {
      Neuron oldNeuron = neurons.neuron(i);
      Neuron newNeuron = newNeurons.neuron(i);
      SynapseList synapses = oldNeuron.synapses();
      if (synapses != null)
      {
        for(synapses.start();synapses.hasMore();synapses.next())
        {
          Synapse synapse = synapses.synapse();
          int sourceId = synapse.getSource().id();
          int destId   = synapse.getDestination().id();
          Neuron newSourceNeuron = newNeurons.neuron(sourceId);
          Neuron newDestNeuron = newNeurons.neuron(destId);
          // copying the strength value - not the function!!!
          Synapse s = net.addSynapse(newSourceNeuron, newDestNeuron, synapse.strength,
              synapse.getProcessMode());
          s.setSynapseType(synapse.type());
        }
      }
    }
    net.setTransferfunction(transferfunction);
    net.setSynapseMode(synapseMode);
    return net;
  }

  /**
   * Copies the content of another net to this net.
   */
  public void setContent(Net net)
  {
    transferfunction  = Transferfunction.SIGM;
    synapseMode       = SynapseMode.CONVENTIONAL;
    initialActivies   = new Vector();
    inputNeurons      = new NeuronList();
    outputNeurons     = new NeuronList();
    readBufferNeurons = new NeuronList();
    hiddenNeurons     = new NeuronList();
    properties        = new Vector();
    neurons           = new NeuronList();

    double alpha      = 0;
    double beta       = 0;
    double gamma      = 0;
    double delta      = 0;
    int i             = 0;

    if(size() > 0) 
    {
      alpha = net.getNeuron(0).getAlpha();
      beta  = net.getNeuron(0).getBeta();
      gamma = net.getNeuron(0).getGamma();
      delta = net.getNeuron(0).getDelta();
    }
    //for(neurons().start(); neurons.hasMore(); neurons().next())
    for(i=0; i < net.neurons.size(); i++)
    {
      //Neuron oldNeuron = neurons().neuron();
      Neuron oldNeuron = net.neurons.neuron(i);
      Neuron newNeuron = addNeuron(
          oldNeuron.getBias(),
          oldNeuron.getTransmitterLevel(),
          oldNeuron.getReceptorLevel(),
          oldNeuron.getProcessMode(),
          oldNeuron.getNeuronType());
      newNeuron.setActivation(oldNeuron.getActivation());
      newNeuron.setOutput(oldNeuron.getOutput());
      //newNeuron.updateOutput();
      newNeuron.setAlpha(oldNeuron.getAlpha());
      newNeuron.setBeta(oldNeuron.getBeta());
      newNeuron.setGamma(oldNeuron.getGamma());
      newNeuron.setDelta(oldNeuron.getDelta());
      newNeuron.setKappa(oldNeuron.getKappa());
      newNeuron.setId(oldNeuron.id());
    }
    NeuronList newNeurons = neurons();
    for(i=0; i < net.neurons.size(); i++)
    {
      Neuron oldNeuron = net.neurons.neuron(i);
      Neuron newNeuron = newNeurons.neuron(i);
      SynapseList synapses = oldNeuron.synapses();
      if (synapses != null)
      {
        for(synapses.start();synapses.hasMore();synapses.next())
        {
          Synapse synapse = synapses.synapse();
          int sourceId = synapse.getSource().id();
          int destId   = synapse.getDestination().id();
          Neuron newSourceNeuron = newNeurons.neuron(sourceId);
          Neuron newDestNeuron = newNeurons.neuron(destId);
          addSynapse(newSourceNeuron, newDestNeuron, synapse.strength(),
              synapse.getProcessMode());
        }
      }
    }
    setTransferfunction(net.getTransferfunction());
    setSynapseMode(net.getSynapseMode());
  }
  
 
  // **************************************************************************
  // output functions
  // **************************************************************************
  /**
   * Returns a human-readable string-representation of the net.
   * @param    none 
   * @return   String - String representing the net
   */
  public String toString()
  {
    String s = new String();
    for(neurons.start();neurons.hasMore();neurons.next())
    {
      s = s.concat(neurons.neuron().toString());
    }
    return s;
  }

  /**
   * Returns an valid XML representation of the net.
   * The returned string can be parsed by the Hinton.IO.XMLHandler class
   * @param    spaces number of preleading spaces
   * @return   String - xml resprensetation of the net
   * @see util.io.XMLHandler 
   */
  public String toXML(int spaces)
  {
    String spacesString = new String();
    for(int i=0;i<spaces;i++)
    {
      spacesString = spacesString.concat(" ");
    }
    String s = new String(spacesString+"<Net "
        + "Transferfunction=\"" + getTransferfunction().toXML() + "\" ");
        if(learningRuleClassLoader != null &&
          learningRuleClassLoader.getSelectedLearningRule() != null)
        {
        s = s + "LearningRule=\"" +
        learningRuleClassLoader.getSelectedLearningRule().getName() + "\" ";
        }
        s = s + "SynapseMode=\"" + getSynapseMode().toXML()+"\"";

    if(properties.size() > 0)
    {
      s = s.concat(" Properties=\"");
      for(int i=0;i<properties.size()-1;i++)
      {
        s = s.concat(""+((Double)properties.elementAt(i))+",");
      }
      s = s.concat(""+((Double)properties.elementAt(properties.size()-1)));
      s = s.concat("\"");
    }
    s = s.concat(">\n");
    for(neurons.start();neurons.hasMore();neurons.next())
    {
      s = s.concat(neurons.neuron().toXML(spacesString) +"\n");
    }
    s=s.concat(spacesString+"</Net>");
    return s;
  }


  public void reset()
  {
    for(neurons().start(); neurons.hasMore(); neurons.next())
    {
      Neuron n = neurons().neuron();
      n.setActivation(0);
      n.setOutput(0);
//      double receptor = random.nextDouble() * (MAX_INIT_RECEPTOR -
//          MIN_INIT_RECEPTOR) + MIN_INIT_RECEPTOR;
//      double transmitter = random.nextDouble() * (MAX_INIT_TRANSMITTER -
//          MIN_INIT_TRANSMITTER) + MIN_INIT_TRANSMITTER;
//      n.setTransmitterLevel(transmitter);
//      n.setReceptorLevel(receptor);
      n.setTransmitterLevel(0);
      n.setReceptorLevel(0);
    }
  }


  /**
   * Returns an valid XML representation of the net.
   * The returned string can be parsed by the Hinton.IO.XMLHandler class
   * @param    none
   * @return   String - xml resprensetation of the net
   * @see util.io.XMLHandler 
   */
  public String toXML()
  {
    return toXML(0);
  }

  /**
   * Returns the correct grammar for this net. The header must be added if
   * needed.
   * @param    spaces number of pre-leading spaces
   * @return   string, the grammar
   */
  public String getXMLGrammar(int spaces)
  {
    String spaceString = new String();
    String s = new String();

    for(int i=0;i<spaces;i++)
    {
      spaceString = spaceString.concat(" ");
    }

    s = s.concat(spaceString.concat("<!ELEMENT Net (Neuron+)>\n"));
    s = s.concat(spaceString.concat("<!ATTLIST Net\n"));
    s = s.concat(spaceString.concat("    Transferfunction      CDATA #REQUIRED\n"));
    s = s.concat(spaceString.concat("    LearningRule          CDATA #IMPLIED\n"));
    s = s.concat(spaceString.concat("    SynapseMode           CDATA #REQUIRED\n"));
    s = s.concat(spaceString.concat("    Properties            CDATA #IMPLIED>\n"));
    s = s.concat(spaceString.concat("\n"));
    s = s.concat(spaceString.concat("<!ELEMENT Neuron (Synapse*)>\n"));
    s = s.concat(spaceString.concat("<!ATTLIST Neuron\n"));
    s = s.concat(spaceString.concat("      Bias                CDATA #REQUIRED\n"));
    s = s.concat(spaceString.concat("      TransmitterLevel    CDATA #IMPLIED\n"));
    s = s.concat(spaceString.concat("      ReceptorLevel       CDATA #IMPLIED\n"));
    s = s.concat(spaceString.concat("      Layer               CDATA #REQUIRED\n"));
    s = s.concat(spaceString.concat("      Process             CDATA #REQUIRED\n"));
    s = s.concat(spaceString.concat("      Alpha               CDATA #IMPLIED\n"));
    s = s.concat(spaceString.concat("      Beta                CDATA #IMPLIED\n"));
    s = s.concat(spaceString.concat("      Gamma               CDATA #IMPLIED\n"));
    s = s.concat(spaceString.concat("      Delta               CDATA #IMPLIED\n"));
    s = s.concat(spaceString.concat("      My                  CDATA #IMPLIED\n"));
    s = s.concat(spaceString.concat("      Ny                  CDATA #IMPLIED\n"));
    s = s.concat(spaceString.concat("      DaleMode            CDATA #IMPLIED>\n"));

    s = s.concat(spaceString.concat("\n"));
    s = s.concat(spaceString.concat("<!ELEMENT Synapse EMPTY>\n"));
    s = s.concat(spaceString.concat("<!ATTLIST Synapse\n"));
    s = s.concat(spaceString.concat("      Source              CDATA #REQUIRED\n"));
    s = s.concat(spaceString.concat("      Strength            CDATA #REQUIRED\n"));
    s = s.concat(spaceString.concat("      SynapseType         CDATA #IMPLIED\n"));
    s = s.concat(spaceString.concat("      Process             CDATA #REQUIRED>\n"));
    return s;
  }

  /**
   * Returns the correct grammar for this net. The header must be added if
   * needed.
   * @return   string, the grammar
   */
  public String getXMLGrammar()
  {
    return getXMLGrammar(0);
  }

  /**
   * Returns a DefaultMutableTreeNode representation of the net.
   * @return   JTree, a tree representation of the net
   * @see DefaultMutableTreeNode
   */
  public DefaultMutableTreeNode toJTreeNode()
  {
    //Net treeNodeNet = this.copy();
    DefaultMutableTreeNode netNode = new DefaultMutableTreeNode("Net");
    netNode.add(new DefaultMutableTreeNode(transferfunction.toString())); 
    if(learningRuleClassLoader.getSelectedIndex() != -1)
    {
      netNode.add(new DefaultMutableTreeNode("Learning Rule: " +
            learningRuleClassLoader.getSelectedLearningRule().getName()));
    }
    for(inputNeurons.start(); 
        inputNeurons.hasMore();
        inputNeurons.next())
    {
      netNode.add(inputNeurons.neuron().toJTreeNode()); 
    }
    for(outputNeurons.start(); 
        outputNeurons.hasMore();
        outputNeurons.next())
    {
      netNode.add(outputNeurons.neuron().toJTreeNode()); 
    }
    for(readBufferNeurons.start(); 
        readBufferNeurons.hasMore();
        readBufferNeurons.next())
    {
      netNode.add(readBufferNeurons.neuron().toJTreeNode()); 
    }
    for(hiddenNeurons.start();
        hiddenNeurons.hasMore();
        hiddenNeurons.next())
    {
      netNode.add(hiddenNeurons.neuron().toJTreeNode()); 
    }
    return netNode;
  }

  // **************************************************************************
  // self-test
  // **************************************************************************
  public static void main(String argv[])
  {
    Net net = new Net();
    net.setTransferfunction(Transferfunction.TANH);
    Neuron a = net.addNeuron(0,0,0,ProcessMode.DYNAMIC,NeuronType.INPUT);
    Neuron b = net.addNeuron(0,0,0,ProcessMode.DYNAMIC,NeuronType.HIDDEN);
    Neuron c = net.addNeuron(0,0,0,ProcessMode.DYNAMIC,NeuronType.HIDDEN);
    Neuron d = net.addNeuron(0,0,0,ProcessMode.DYNAMIC,NeuronType.OUTPUT);
    net.addSynapse(a,b,1,ProcessMode.DYNAMIC);
    net.addSynapse(a,c,1,ProcessMode.DYNAMIC);
    net.addSynapse(c,b,1,ProcessMode.DYNAMIC);
    net.addSynapse(b,c,1,ProcessMode.DYNAMIC);
    net.addSynapse(b,b,1,ProcessMode.DYNAMIC);
    net.addSynapse(c,c,1,ProcessMode.DYNAMIC);
    net.addSynapse(b,d,1,ProcessMode.DYNAMIC);
    net.addSynapse(c,d,1,ProcessMode.DYNAMIC);
    net.randomInitActivity();
    Random localRandom = null;
    for(int j=0;j<10;j++)
    {
      localRandom = new Random(0);
      net.resetInitialActivities();
      net.getInputNeurons().neuron(0).setActivation(localRandom.nextFloat());
      //System.out.println("***********\n"+d.toString());
      System.out.print(net.getOutputNeurons().neuron(0).getOutput() + " -> " );
      for(int i=0;i<10;i++)
      {
        net.process();
        net.getInputNeurons().neuron(0).setActivation(localRandom.nextFloat());
      }
      System.out.println(net.getOutputNeurons().neuron(0).getOutput());
    }
  //  System.exit(0);
    net.setTransferfunction(Transferfunction.SIGM);
    net.randomInitActivity();
    Net net2 = net.copy();
    System.out.println("****************************");
    System.out.println(net.toString());
    System.out.println("****************************");
    System.out.println(net2.toString());
    System.out.println("****************************");
    System.out.println("Net:");
    System.out.println(net.toString());
    System.out.println("after processing: ");
    net.process();
    System.out.println(net.toString());
    NeuronList ni = net.getInputNeurons();
    NeuronList no = net.getOutputNeurons();
    NeuronList nh = net.getHiddenNeurons();
    System.out.println("Input Neurons:\n" + ni.toString());
    System.out.println("Hidden Neurons:\n" + nh.toString());
    System.out.println("Output Neurons:\n" + no.toString());
    System.out.println("#Synapses     :" + net2.getSynapseCount());
    System.out.println("****************************");
    System.out.println("****** properties check ****");
    System.out.println("****************************");
    System.out.println(net.toXML());
    Vector v = new Vector(5);
    v.add(new Double(1.0));
    v.add(new Double(1.1));
    v.add(new Double(1.2));
    v.add(new Double(2.3));
    v.add(new Double(2.4));
    net.setProperties(v);
    System.out.println("****************************");
    System.out.println("****** grammar check *******");
    System.out.println("****************************");
    System.out.println(net.toXML());
    System.out.println(net.getXMLGrammar());
    System.out.println(net.getXMLGrammar(5));
    System.out.println(net.toXML(10));

    System.out.println("Neural Net Grammar");
    System.out.println(net.getXMLGrammar());
    System.out.println("net.toXML(): ");
    System.out.println(net.toXML());
    
  }
}


