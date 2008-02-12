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

import javax.swing.tree.DefaultMutableTreeNode;



/**
 *  Implementation of a Neuron. The neuron has all the information that is
 *  needed to calculate its own state. That is, it knows all the synapses
 *  leading towards it, and can access the output-values of all neurons, which
 *  are connected to itself. A neuron is the main processing-unit within the
 *  neural net.<br><br>
 *  If a neuron is set to be an <u> input neuron</u>, then it functions purly as a
 *  buffer neuron. The output of the input neuron is equal to its activation.
 *  <br> <br>
 *  
 *  Synapses are always associated with the destination neuron.
 *
 *  @see cholsey.NeuronType
 */
public class Neuron
{

  public final static int NEURON_DALE_UNDEFINED  = 0;
  public final static int NEURON_DALE_INHIBITORY = 1;
  public final static int NEURON_DALE_EXCITATORY = 2;
  // **************************************************************************
  // private parameters
  // **************************************************************************
  private int              id               = 0;
  private double           bias             = 0; // bias value
  private double           receptorLevel    = 0; // receptor level
  private double           transmitterLevel = 0; // receptor level
  private double           activation       = 0; // a(t+1)
  private double           old_activation   = 0; // a(t)
  private double           output           = 0;
  private Transferfunction transferfunction = Transferfunction.SIGM;
  private ProcessMode      processMode      = ProcessMode.DYNAMIC;
  private NeuronType       neuronType       = NeuronType.INPUT;
  private SynapseList      synapses         = null; // ref to synapses to
  private SynapseMode      synapseMode      = SynapseMode.CONVENTIONAL;
  private double           alpha            = 1.0; // standard values
  private double           beta             = 0.01;
  private double           gamma            = 0.01;
  private double           delta            = 0.02; 
  private double           my               = 0.015; 
  private double           ny               = 0.015; 
  private double           kappa            = 0; // for chaos?

  private int              daleMode         = NEURON_DALE_UNDEFINED;

  private int              learningRuleIndex = 0;
  private Vector           learningRuleClasses = null;
  private static LearningRuleClassLoader learningRuleClassLoader = new
    LearningRuleClassLoader();

  private LearningRuleInterface learningRule;
//  private Method learningMethod;
  private boolean learningClassFound = false;

  private Vector learnParameter = new Vector(10);

  /**
   *  public constructor (sets the id)
   *  @param    id int id, the id of the neuron
   */
  public Neuron(int id)
  {
    setId(id);
    setBias(0.0);
    setTransmitterLevel(0.0);
    setReceptorLevel(0.0);
    setProcessMode(ProcessMode.DYNAMIC);
    setNeuronType(NeuronType.OUTPUT);
    setTransferfunction(Transferfunction.TANH);
    loadLearningRuleClass();
  }

  /**
   * @param    id   int id, the id of the neuron 
   * @param    bias double bias of the neuron
   */
  public Neuron(int id, double bias, double transmitterLevel, double
      receptorLevel, ProcessMode process, NeuronType neuronType,
      Transferfunction transferfunction)
  {
    setId(id);
    setBias(bias);
    setTransmitterLevel(transmitterLevel);
    setReceptorLevel(receptorLevel);
    setProcessMode(process);
    setNeuronType(neuronType);
    setTransferfunction(transferfunction);

    loadLearningRuleClass();
  }

  // **************************************************************************
  // class loader section
  // **************************************************************************

  /**
   * Load the class which inhibits the learning rule.
   */
  private void loadLearningRuleClass()
  {
    if(learningRuleClassLoader.getSelectedIndex() == -1)
    {
      learningClassFound = false;
    }
    else
    {
      learningClassFound = true;
      learningRule = learningRuleClassLoader.getSelectedLearningRule();
    }


    for(int i=0;i<learnParameter.capacity();i++)
    {
      learnParameter.add(new Double(0));
    }
  }

  protected void setLearningRule(int lrIndex)
  {
    learningRuleIndex = lrIndex;
    learningRule = 
      (LearningRuleInterface)(learningRuleClasses.elementAt(learningRuleIndex));
  }

  protected void setLearningRule(String learningRuleName)
  {
    learningClassFound = false;
    if(learningRuleName == null)
    {
      return;
    }
    for(int i=0; i < learningRuleClasses.size(); i++)
    {
      if(learningRuleName.trim().equals(
            ((LearningRuleInterface)learningRuleClasses.elementAt(i)).getName().trim()))
      {
        learningClassFound = true;
        learningRuleIndex = i;
        learningRule = 
          (LearningRuleInterface)(learningRuleClasses.elementAt(learningRuleIndex));
        return;
      }
    }
  }

  // **************************************************************************
  // update function
  // **************************************************************************

  /**
   * Updates all inner variables, updateOutput must be called after all neurons
   * were processed.
   * <ul>
   * <li> <u> forall </u> neurons <u> do </u> updateActivation() <u> od </u> </li>
   * <li> <u> forall </u> neurons <u> do </u> updateLearningParameter() <u> od </u>
   </li>
   * <li> <u> forall </u> neurons <u> do </u> updateOutput() <u> od </u> </li>
   * @param    
   * @return   
   */
  public void updateActivation()
  {
    if(neuronType == NeuronType.INPUT)
    {
      return; // input neurons are only buffers
    }

    old_activation = activation; // for transmitter & recpetor calculation

    activation = kappa * activation + bias;

    if(synapses != null)
    {
      for(synapses.start();synapses.hasMore();synapses.next())
      {
        Synapse synapse = synapses.synapse();
        activation += synapse.strength() * synapse.getSource().getOutput();
      }
    }
  }

  /**
   * Updates the learning parameter. Needs old_activation as var for a(t). Sets
   * receptorLevel(t+1) and transmitterLevel(t+1) in dependance of a(t)
   */
  public void updateLearningParameter()
  {
    if(synapseMode == SynapseMode.DYNAMIC)
    {
      if(neuronType != NeuronType.INPUT && neuronType != NeuronType.READ_BUFFER)
      {
        if(learningRuleClassLoader.getSelectedIndex() != -1)
        {
          learnParameter.setElementAt(new Double(output),0);
          learnParameter.setElementAt(new Double(bias),1); 
          learnParameter.setElementAt(new Double(transmitterLevel),2);
          learnParameter.setElementAt(new Double(receptorLevel),3);
          learnParameter.setElementAt(new Double(alpha),4);
          learnParameter.setElementAt(new Double(beta),5);
          learnParameter.setElementAt(new Double(gamma),6);
          learnParameter.setElementAt(new Double(delta),7);
          learnParameter.setElementAt(new Double(my),8);
          learnParameter.setElementAt(new Double(ny), 9); 

          learningRule.calculateLearningParameter(learnParameter);

          bias             = ((Double)learnParameter.elementAt(1)).doubleValue();
          transmitterLevel = ((Double)learnParameter.elementAt(2)).doubleValue();
          receptorLevel    = ((Double)learnParameter.elementAt(3)).doubleValue();

        }
        else
        {
          System.out.println(
              "Neuron.updateLearningParameter(): no learning rule given!!!");
        }
      }
    }
  }

  /**
   *  Updates the output of the neuron according to the activation and the
   *  selected transferfunction. Should be called after <b> all </b> neurons are
   *  processed, because in process getOutput is called. If updated to early,
   *  the wrong value will be passed. Should only be called by overlaying
   *  structure (Cholsey.Net). If the neuron is an input neuron, the output is
   *  equal to the activation.
   *  @param    none
   *  @return   none
   *  @see cholsey.Net
   */
  public void updateOutput()
  {
    if(neuronType == NeuronType.INPUT || neuronType == NeuronType.READ_BUFFER)
    {
      output = activation + bias;
      return;
    }
    output = transferfunction.calculate(activation);
  }


  // **************************************************************************
  // receptor functions
  // **************************************************************************
  /**
   *  Sets the receptor level of the neuron
   *  @param    receptorLevel double receptorLevel, new receptor level of the
   *  neuron
   *  @return   none
   */
  public void setReceptorLevel(double receptorLevel)
  {
    this.receptorLevel = receptorLevel;
  }
  /**
   *  Returns the receptor-level of the neuron. Returns 0 if neuron is input
   *  neuron.
   *  @param    none
   *  @return   double receptorLevel, current receptor level of the neuron
   */
  public double getReceptorLevel()
  {
    switch(neuronType.type())
    {
      case NeuronType.NEURON_TYPE_INPUT:
        return 0;
      case NeuronType.NEURON_TYPE_READ_BUFFER:
        return 1;
      default:
        return receptorLevel;
    }
  }

  // **************************************************************************
  // transmitter functions
  // **************************************************************************
  /**
   *  Sets the transmitter level of the neuron.
   *  @param    transmitterLevel double transmitterLevel, new level of
   *  transmitters
   *  @return   none
   */
  public void setTransmitterLevel(double transmitterLevel)
  {
    this.transmitterLevel = transmitterLevel;
  }
  /**
   *  Returns the transmitter-level of the neuron. Returns 1 if neuron is input
   *  neuron.
   *  @param    none
   *  @return   double transmitterLevel, current level of transmitters
   */
  public double getTransmitterLevel()
  {
    switch(neuronType.type())
    {
      case NeuronType.NEURON_TYPE_INPUT:
        return 1;
      case NeuronType.NEURON_TYPE_READ_BUFFER:
        return 0;
      default:
        return transmitterLevel;
    }
  }

  // **************************************************************************  
  // Bias functions
  // **************************************************************************  
  /**
   *  Sets the value of the bias of the neuron. The bias is added to the
   *  activity of the neuron.
   *  @param    bias double bias, new bias of the neuron
   *  @return   none
   */
  public void setBias(double bias)
  {
    this.bias = bias;
  }

  /**
   *  Returns the current bias value of this neuron.
   *  @param    none
   *  @return   double bias, current bias of the neuron.
   */
  public double getBias()
  {
    return bias;
  }
  // **************************************************************************
  // activation functions
  // **************************************************************************
  /**
   *  Sets the activation of the neuron. This does <b> not </b> include the
   *  output of the neuron. The output must be set sepreatly
   *  @param    activation double activation, new activation value of the neuron
   *  @return   none
   */
  public void setActivation(double activation)
  {
    this.activation = activation;
    updateOutput();
  }
  /**
   *  Returns the activation of the neuron.
   *  @param    none
   *  @return   double activation, current activation of the neuron
   */
  public double getActivation()
  {
    return activation;
  }


  // **************************************************************************
  // alpha functions
  // **************************************************************************

  /**
   * Sets the value of alpha. Alpha is a constant used by the learning
   * algorithm.
   */
  public void setAlpha(double alpha)
  {
    this.alpha = alpha;
  }

  /**
   * Returns the value of alpha. Alpha is a constant used by the learning
   * algorithm.
   */
  public double getAlpha()
  {
    return alpha;
  }


  // **************************************************************************
  // beta functions
  // **************************************************************************

  /**
   * Sets the value of beta. Beta is a constant used by the learning
   * algorithm.
   */
  public void setBeta(double beta)
  {
    this.beta = beta;
  }

  /**
   * Returns the value of beta. Beta is a constant used by the learning
   * algorithm.
   */
  public double getBeta()
  {
    return beta;
  }

  // **************************************************************************
  // gamma functions
  // **************************************************************************

  /**
   * Sets the value of gamma. Gamma is a constant used by the learning
   * algorithm.
   */
  public void setGamma(double gamma)
  {
    this.gamma = gamma;
  }

  /**
   * Returns the value of gamma. Gamma is a constant used by the learning
   * algorithm.
   */
  public double getGamma()
  {
    return gamma;
  }


  // **************************************************************************
  // delta functions
  // **************************************************************************

  /**
   * Sets the value of delta. Delta is a constant used by the learning
   * algorithm.
   */
  public void setDelta(double delta)
  {
    this.delta = delta;
  }

  /**
   * Returns the value of delta. Delta is a constant used by the learning
   * algorithm.
   */
  public double getDelta()
  {
    return delta;
  }

  // **************************************************************************
  // my functions
  // **************************************************************************

  /**
   * Sets the value of my. My is a constant used by the learning
   * algorithm.
   */
  public void setMy(double my)
  {
    this.my = my;
  }

  /**
   * Returns the value of my. My is a constant used by the learning
   * algorithm.
   */
  public double getMy()
  {
    return my;
  }

  // **************************************************************************
  // ny functions
  // **************************************************************************

  /**
   * Sets the value of ny. Ny is a constant used by the learning
   * algorithm.
   */
  public void setNy(double ny)
  {
    this.ny = ny;
  }

  /**
   * Returns the value of ny. Ny is a constant used by the learning
   * algorithm.
   */
  public double getNy()
  {
    return ny;
  }



  // **************************************************************************
  // kappa functions
  // **************************************************************************
  /**
   *  Sets the kappa in the activation function. a(t+1) = kappa*a(t) + ...
   *  @return   none
   */
  public void setKappa(double kappa)
  {
    this.kappa = kappa;
  }
  /**
   *  Returns the kappa parameter of the activation function.
   *  @param    none
   */
  public double getKappa()
  {
    return kappa;
  }


  // **************************************************************************
  // output functions
  // **************************************************************************
  /**
   *  Returns the output of this neuron. If the function updateOutput is not
   *  used after a process-functioncall, this is the old output value.
   *  @param    none
   *  @return   double output, current output (since last call of updateOutput)
   */
  public double getOutput()
  {
    return output;
  }

  /**
   *  Sets the output of this neuron. The activation is not touched
   *  @param   double output, current output 
   */
  public void setOutput(double output)
  {
    this.output = output;
  }

  // **************************************************************************
  // Transferfunction functions
  // **************************************************************************

  /**
   *  Sets the transferfunction of this neuron. 
   *  @param    transferfunction Transferfunction transferfunction, the
   *  currently used transferfunction
   *  @return   none
   */
  public void setTransferfunction(Transferfunction transferfunction)
  {
    this.transferfunction = transferfunction;
  }
  /**
   * Returns the currently used transferfunction.
   * @param    none
   * @return   Transferfunction transferfunction, the currently used
   * transferfunction
   */
  public Transferfunction getTransferfunction()
  {
    return transferfunction;
  }

  // **************************************************************************
  // synapses functions
  // **************************************************************************
  /**
   * Sets the vector of synapses for the neuron. 
   * @param    synapses Vector synapses, the vector of all synapses, witch <em>
   * destination </em> is this neuron
   * @return   none
   */
  public void setSynapses(SynapseList synapses)
  {
    this.synapses = synapses;
  }

  /**
   * Adds one synapse to the list of synapses.
   * @param    synapse Synapse synapse, new valid synapse, which destination is
   * this neuron
   * @return   none
   */
  public void addSynapse(Synapse synapse)
  {
    if(synapses == null)
    {
      synapses = new SynapseList();
    }
    synapse.setSynapseMode(synapseMode);
    synapses.add(synapse);
  }

  /**
   * Set the weight of the synase from the source neuron.
   * @param    source the source neuron
   * @param    weight the new weight of the synapse
   */
  public void setWeight(Neuron source, double weight)
  {
    for(synapses.start();synapses.hasMore();synapses.next())
    {
      Synapse synapse = synapses.synapse();
      if(synapse.getSource() == source)
      {
        synapse.setStrength(weight);
      }
    }
  }

  /**
   *  Returns the list of synapses that target this neuron.
   *  @return   list of synapses pointing at this neuron
   *  @see cholsey.SynapseList
   */
  public SynapseList synapses()
  {
    return synapses;
  }

  /**
   *  Returns the synapse that target this neuron and has the source given by
   *  the parameter source.
   *  @return   synapses pointing at this neuron, or null if non-existing
   *  @see cholsey.Synapse
   */
  public Synapse getSynapse(Neuron source)
  {
    if(synapses == null)
    {
      return null;
    }
    for(synapses.start();synapses.hasMore();synapses.next())
    {
      Synapse synapse = synapses.synapse();
      if(synapse.getSource() == source)
      {
        return synapse;
      }
    }
    return null;
  }

  /**
   *  Removes the synapse with a given source from the list of synapses.
   */
  public void removeSynapse(Neuron source)
  {
    if(synapses == null)
    {
      return;
    }
    for(synapses.start();synapses.hasMore();synapses.next())
    {
      Synapse synapse = synapses.synapse();
      if(synapse.getSource() == source)
      {
        synapses.remove(synapse);
        return;
      }
    }
  }

  /**
   * Set the synapse mode of the neuron. Only the neurons synapse mode is set,
   * <b> none </b> of the synapses are changed!
   * @param    SynapseMode synapseMode
   * @see cholsey.SynapseMode
   */
  public void setSynapseMode(SynapseMode synapseMode)
  {
    this.synapseMode = synapseMode;
    if(synapses != null)
    {
      for(synapses.start();synapses.hasMore();synapses.next())
      {
        synapses.synapse().setSynapseMode(synapseMode);
      }
    }
  }

  /**
   * Returns the synapse mode of the neuron. Must <b> not correspond </b> with
   * the synapse mode of the neurons connected to this neuron!
   * @return    SynapseMode synapseMode
   * @see cholsey.SynapseMode
   */
  public SynapseMode getSynapseMode()
  {
    return synapseMode;
  }


  // **************************************************************************   
  // id functions
  // **************************************************************************   
  /**
   * Returns the id of this neuron.
   * @param    none
   * @return   int id, id of this neuron.
   */
  public int id()
  {
    return id;
  }

  /**
   * Sets the id of this neuron. The id is according to the index of the neuron
   * in the list of neurons, which is held by the net. The id should only be set
   * by creation and from the overlaying structure Cholsey.Net
   * @param    id int id, the id of this neuron
   * @return   none
   * @see      cholsey.Net
   */
  public void setId(int id)
  {
    this.id = id;
  }

  // **************************************************************************  
  // neuron type
  // **************************************************************************  
  /**
   * Sets the type of this neuron. That is, "hidden", "input", "output"
   * @param    neuronType NeuronType neuronType, the type of this neuron
   * @return   none
   */
  protected void setNeuronType(NeuronType neuronType)
  {
    this.neuronType = neuronType;
  }

  /**
   * Returns the type of this neuron. That is, "hidden", "input", "output"
   * @param    none
   * @return   NeuronType neuronType, type of this neuron
   */
  public NeuronType getNeuronType()
  {
    return neuronType;
  }

  // **************************************************************************
  // process mode functions
  // **************************************************************************
  /**
   * Sets the processing-mode of this neuron. That is, is the neuron
   * "deletable", "variable", "static". Used by the evolution programm.
   * @param    processMode ProcessMode processMode, the process mode of this
   * neuron
   * @return   none
   */
  public void setProcessMode(ProcessMode processMode)
  {
    this.processMode = processMode;
  }

  /**
   * Returns the processing mode of this neuron.
   * @param    none
   * @return   ProcessMode processMode
   */
  public ProcessMode getProcessMode()
  {
    return processMode;
  }

  // **************************************************************************
  // dale mode
  // **************************************************************************

  public void setDaleMode(int daleMode)
  {
    this.daleMode = daleMode;
  }

  public int getDaleMode()
  {
    return daleMode;
  }

  // **************************************************************************  
  // output functions
  // **************************************************************************  
  /**
   * Returns a human-readable representation of the neuron. That is, all inner
   * states, like activation, output, bias ... as well as all synapses assigned
   * to it.
   * @param    none
   * @return   String string, human-readable representation
   */
  public String toString()
  {
    String s = new String("Neuron " + id 
        + "\n Activation        = " + getActivation() 
        + "\n Output            = " + getOutput()
        + "\n bias              = " + getBias()
        + "\n alpha             = " + getAlpha()
        + "\n beta              = " + getBeta()
        + "\n gamma             = " + getGamma()
        + "\n delta             = " + getDelta()
        + "\n my                = " + getMy()
        + "\n ny                = " + getNy()
        + "\n kappa             = " + getKappa()
        + "\n TransmitterLevel  = " + getTransmitterLevel()
        + "\n ReceptorLevel     = " + getReceptorLevel()
        + "\n Transferfunction  = " + getTransferfunction().toString()
        + "\n NeuronType        = " + getNeuronType().toString()
        + "\n ProcessMode       = " + getProcessMode().toString()
        + "\n");
    if (synapses != null) // if there is no synapse to this neuron (input-neuron)
    {
      for(synapses.start();synapses.hasMore();synapses.next())
      {
        Synapse synapse = synapses.synapse();
        Neuron source = synapse.getSource();
        Neuron target = synapse.getDestination();
        s=s.concat("Synapse from " + source.id() + " -> " + target.id()+ 
            ", strength = " + synapse.strength()+" (" +
            synapse.mode().toString() + " " + synapse.type().toString() +
            ")\n");
      }
    }
    return s;
  }

  /**
   * Returns an string, which is an XML-valid representation of the Neuron.
   * @param    spaces number of preleading spaces
   * @return   String xml, XML-valid representation
   */
  public String toXML(String spacesString)
  {
    String s = new String();
    s = s.concat(spacesString + "  <Neuron " + "Bias=\"" + getBias() +"\"\n");

    if(getTransmitterLevel() > 0 && getReceptorLevel() > 0) 
    {
      s = s.concat(spacesString 
          +  "          TransmitterLevel=\"" + getTransmitterLevel() +
          "\"\n");
      s = s.concat(spacesString 
          + "           ReceptorLevel=\"" + getReceptorLevel() +"\"\n");
    }
    s = s.concat(spacesString + "          Alpha=\"" + getAlpha() + "\"\n");
    s = s.concat(spacesString + "          Beta=\""  + getBeta()  + "\"\n");
    s = s.concat(spacesString + "          Gamma=\"" + getGamma() + "\"\n");
    s = s.concat(spacesString + "          Delta=\"" + getDelta() + "\"\n");
    s = s.concat(spacesString + "          My=\"" + getMy() + "\"\n");
    s = s.concat(spacesString + "          Ny=\"" + getNy() + "\"\n");

    if(getKappa() > 0)
    {
      s = s.concat(spacesString + "          Kappa=\"" + getKappa() + "\"\n");
    }
    s = s.concat(spacesString + 
        "          Layer=\"" + getNeuronType().toXML() + "\"\n");
    s = s.concat(spacesString + 
        "          Process=\"" + getProcessMode().toXML() +"\"\n");
    switch(getDaleMode())
    {
      case NEURON_DALE_UNDEFINED:
        s = s.concat(spacesString + 
            "          DaleMode=\"undefined\"");
        break;
      case NEURON_DALE_EXCITATORY:
        s = s.concat(spacesString + 
            "          DaleMode=\"excitatory\"");
        break;
      case NEURON_DALE_INHIBITORY:
        s = s.concat(spacesString + 
            "          DaleMode=\"inhibitory\"");
        break;
    }
    s += ">\n";
    if(synapses != null)  // input neurons have no synapses pointing to them
    {
      for(synapses.start();synapses.hasMore();synapses.next())
      {
        Synapse synapse = synapses.synapse();
        s=s.concat(spacesString +"    " +synapse.toXML()+"\n");
      }
    }
    s=s.concat(spacesString+"  </Neuron>");
    return s;
  }

  /**
   * Returns an string, which is an XML-valid representation of the Neuron.
   * @param    none
   * @return   String xml, XML-valid representation
   */
  public String toXML()
  {
    return toXML("");
  }

  /**
   * Returns an DefaultMutableTreeNode, which is an JTree-valid representation
   * of the Neuron.
   * @param    none
   * @return   DefaultMutableTreeNode node, JTree-valid representation
   */
  public DefaultMutableTreeNode toJTreeNode()
  {
    String fontString = null;
    switch(neuronType.type())
    {
      case NeuronType.NEURON_TYPE_INPUT:
        fontString = new String("<font color=#ef0000>");
        break;
      case NeuronType.NEURON_TYPE_OUTPUT:
        fontString = new String("<font color=#002a00>");
        break;
      case NeuronType.NEURON_TYPE_HIDDEN:
        fontString = new String("<font color=#0000ff>");
        break;
      case NeuronType.NEURON_TYPE_READ_BUFFER:
        fontString = new String("<font color=#ff00ff>");
        break;
    }
    DefaultMutableTreeNode neuronTreeNode = new DefaultMutableTreeNode(
        "<html>" + fontString + "Neuron "
        + (id + 1)
        +"</font>");
    DefaultMutableTreeNode daleTypeNode = null;
    switch(getDaleMode())
    {
      case NEURON_DALE_UNDEFINED:
        daleTypeNode = new DefaultMutableTreeNode("Dale Type: undefined");
        break;
      case NEURON_DALE_EXCITATORY:
        daleTypeNode = new DefaultMutableTreeNode(
            "<html>Dale Type: " + "<font color=#ef0000> excitatory </font>");
        break;
      case NEURON_DALE_INHIBITORY:
        daleTypeNode = new DefaultMutableTreeNode(
            "<html>Dale Type:" + "<font color=#0000ff> inhibitory </font>");
        break;
    }

    DefaultMutableTreeNode activationNode       = new
      DefaultMutableTreeNode("Activation:"
          + activation);
    DefaultMutableTreeNode biasNode       = new DefaultMutableTreeNode("Bias: "
        + bias);
    DefaultMutableTreeNode typeNode       = new DefaultMutableTreeNode("Type: "
        + getNeuronType());
    DefaultMutableTreeNode transmitterNode       = new
      DefaultMutableTreeNode("TransmitterLevel: "
          + getTransmitterLevel());
    DefaultMutableTreeNode receptorNode       = new
      DefaultMutableTreeNode("ReceptorLevel: "
          + getReceptorLevel());

    DefaultMutableTreeNode alphaNode       = new
      DefaultMutableTreeNode("Alpha: "
          + getAlpha());

    DefaultMutableTreeNode betaNode       = new
      DefaultMutableTreeNode("Beta: "
          + getBeta());

    DefaultMutableTreeNode gammaNode       = new
      DefaultMutableTreeNode("Gamma: "
          + getGamma());

    DefaultMutableTreeNode deltaNode       = new
      DefaultMutableTreeNode("Delta: "
          + getDelta());

    DefaultMutableTreeNode myNode       = new
      DefaultMutableTreeNode("My: "
          + getMy());

    DefaultMutableTreeNode nyNode       = new
      DefaultMutableTreeNode("Ny: "
          + getNy());



    neuronTreeNode.add(typeNode);
    neuronTreeNode.add(daleTypeNode);
    neuronTreeNode.add(activationNode);
    neuronTreeNode.add(biasNode);
    neuronTreeNode.add(alphaNode);
    neuronTreeNode.add(betaNode);
    neuronTreeNode.add(gammaNode);
    neuronTreeNode.add(deltaNode);
    neuronTreeNode.add(myNode);
    neuronTreeNode.add(nyNode);

    if(getReceptorLevel() != 0)
    {
      neuronTreeNode.add(receptorNode);
    }
    if(getTransmitterLevel() != 0)
    {
      neuronTreeNode.add(transmitterNode);
    }
    if(synapses != null)
    {
      SynapseList sortedList = synapses.sort();
      for(sortedList.start();sortedList.hasMore();sortedList.next())
      {
        neuronTreeNode.add(sortedList.synapse().toJTreeNode());
      }
    }
    return neuronTreeNode;
  }

  // **************************************************************************
  // testing functions
  // **************************************************************************
  /**
   * Makes the class self-testable. <b> should never be called as
   * memeber-function!</b>. The class can be executed as: java Cholsey.Neuron
   * @param    none
   * @return   none
   */
  public static void main(String argv[])
  {
    Neuron n1 = new Neuron(0);
    Neuron n2 = new Neuron(1);
    Neuron n3 = new Neuron(2);
    Synapse s;
    SynapseList sv1 = new SynapseList();
    SynapseList sv2 = new SynapseList();
    SynapseList sv3 = new SynapseList();

    n1.setReceptorLevel(2);
    n1.setTransmitterLevel(3);
    n2.setReceptorLevel(4);
    n2.setTransmitterLevel(5);
    n3.setReceptorLevel(6);
    n3.setTransmitterLevel(7);

    n1.setActivation(1);
    n2.setActivation(2);
    n3.setActivation(-1);

    sv1.removeAllElements();
    s = new Synapse(n1,n1,1,ProcessMode.DYNAMIC); // n1 ->(*1) n1
    sv1.add(s);
    s = new Synapse(n2,n1,2,ProcessMode.DYNAMIC); // n2 ->(*4) n1 
    sv1.add(s);
    s = new Synapse(n3,n1,3,ProcessMode.DYNAMIC); // n3 ->(*7) n1
    sv1.add(s);
    n1.setSynapses(sv1);

    sv2.removeAllElements();
    s = new Synapse(n1,n2,4,ProcessMode.DYNAMIC); // n1 ->(*2) n2
    sv2.add(s);
    s = new Synapse(n3,n2,5,ProcessMode.DYNAMIC); // n3 ->(*6) n2
    sv2.add(s);
    n2.setSynapses(sv2);

    sv3.removeAllElements();
    s = new Synapse(n2,n3,6,ProcessMode.DYNAMIC); // n2 ->(*5) n3
    sv3.add(s); 
    s = new Synapse(n1,n3,7,ProcessMode.DYNAMIC); // n1 ->(*3) n3
    sv3.add(s);
    n3.setSynapses(sv3);

    System.out.println("Created 3 Neurons and 7 Synapses");
    System.out.println("N1: \n" + n1.toString());
    System.out.println("N2: \n" + n2.toString());
    System.out.println("N3: \n" + n3.toString());
    n1.updateActivation();
    n2.updateActivation();
    n3.updateActivation();
    System.out.println("*** After processing\n");;
    System.out.println("N1: \n" + n1.toString());
    System.out.println("N2: \n" + n2.toString());
    System.out.println("N3: \n" + n3.toString());
    System.out.println("Test done.");
    System.out.println("N1: \n" + n1.toXML());
    System.out.println("N2: \n" + n2.toXML());
    System.out.println("N3: \n" + n3.toXML());
    n1.setKappa(0.3);
    System.out.println("N1: \n" + n1.toXML());
    System.out.println("*** TESTING LEARNING RULE");
    n2.setNeuronType(NeuronType.HIDDEN);
    n2.setSynapseMode(SynapseMode.DYNAMIC);
    n2.updateActivation();
    System.out.println("*** DONE");

    Neuron n10 = new Neuron(0, 0.0, 0.0, 0.0, ProcessMode.DYNAMIC, NeuronType.INPUT,
        Transferfunction.SIGM);
    System.out.println("TransmitterLevel " + n10.getTransmitterLevel());
  }
};





/*

 */
