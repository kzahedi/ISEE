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


package Evolution;

import java.util.Random;
import java.util.Vector;

import Evolution.Random.Gauss;
import cholsey.Net;
import cholsey.Neuron;
import cholsey.NeuronList;
import cholsey.NeuronType;
import cholsey.ProcessMode;
import cholsey.Synapse;
import cholsey.SynapseList;
import cholsey.SynapseMode;
import cholsey.SynapseType;


/**
 *  EvoObject implements an Object, which 
 *    consists a network, performance, etc.  <br>
 *
 *
 *
 *
 */


public class EvoObject {

  public final static int PARAMETER_MODE_DEFAULT     = 0;
  public final static int PARAMETER_MODE_C_EQ_D      = 1;
  public final static int PARAMETER_MODE_B_EQ_C_EQ_D = 2;
  public final static int PARAMETER_MODE_1_5_C_EQ_D  = 3;


  public final static int SYN_INS_MODE_DEFAULT   = 0;
  public final static int SYN_INS_MODE_DALE      = 1;
  public final static int SYN_INS_MODE_DALE_2    = 2;

  public final static String[] SYN_MODES =
  {
    "Default",
    "Dale's Rule", 
    "Dale's Rule 2" // if new neuron, check for average synapse weight
  };

  public final static String[] PARAMETER_MODES =
  {
    "Default",
    "c = d", 
    "b = c = d",
    "1.5c = d" // if new neuron, check for average synapse weight
  };



  private Net     net         = null;
  private int     count       = 1;
  private double  sysPerf     = 0.0;
  private double  outPerf     = 0.0;
  private int     age         = 0;

  private int     myPIN;
  private int     parentsPIN;


  /* global pseudo random generators */
  private Random  uniRand;
  private Gauss   gaussRand;


  public EvoObject(int pin, Random uni, Gauss gauss){

    /* homeokinese meta parameter - default values */
    double modFreq = 0.0;
    double modAmpl = 0.0;
    double learnRate = 0.0;
    double velOffSet = 0.0;
    Vector homeoKineseParam = new Vector();
    homeoKineseParam.add(0,new Double(modFreq));
    homeoKineseParam.add(1,new Double(modAmpl));
    homeoKineseParam.add(2,new Double(learnRate));
    homeoKineseParam.add(3,new Double(velOffSet));


    this.myPIN = pin;
    this.parentsPIN = -1;
    this.uniRand = uni;
    this.gaussRand = gauss;
    this.net = new Net();
    this.net.setProperties(homeoKineseParam); 

    this.sysPerf = java.lang.Double.NEGATIVE_INFINITY;
    this.outPerf = java.lang.Double.NEGATIVE_INFINITY;
    this.age = 0;
    this.count = 1;
  }

  public EvoObject(EvoObject obj, int pin, Random uni, Gauss gauss){
    Vector v = new Vector();

    this.myPIN = pin;
    this.parentsPIN = obj.getPIN();
    this.uniRand = uni;
    this.gaussRand = gauss;
    this.net = (obj.getNet()).copy();

    int i;
    if (obj.getNet().getProperties() != null)
    {
      for (i=0; i< obj.getNet().getProperties().size(); i++)
      {
        v.addElement(obj.getNet().getProperties().elementAt(i));
      }
    }
    this.net.setProperties(v);



    this.sysPerf = obj.getPerformance();
    this.outPerf = obj.getOutPerf();
    this.age = obj.getAge();
    this.count = obj.count;
  }

  public EvoObject(EvoObject obj, Random uni, Gauss gauss){
    Vector v = new Vector();

    this.myPIN = obj.getPIN();
    this.parentsPIN = obj.getParentsPIN();
    this.uniRand = uni;
    this.gaussRand = gauss;
    this.net = (obj.getNet()).copy();

    int i;
    if (obj.getNet().getProperties() != null)
    {
      for (i=0; i< obj.getNet().getProperties().size(); i++)
      {
        v.addElement(obj.getNet().getProperties().elementAt(i));
      }
    }
    this.net.setProperties(v);

    this.sysPerf = obj.getPerformance();
    this.outPerf = obj.getOutPerf();
    this.age = obj.getAge();
    this.count = obj.count;
  }


  public boolean isEmpty(){
    if(this.net == null)
    {
      return true;
    }
    return false;
  }

  public void modFreqVar(double chgProb, double intens, double limit){
    int index = 0;
    homeoKineseVar(index, chgProb, intens, limit);
  }
  public void modAmplVar(double chgProb, double intens, double limit){
    int index = 1;
    homeoKineseVar(index, chgProb, intens, limit);
  }
  public void learnRateVar(double chgProb, double intens, double limit){
    int index = 2;
    homeoKineseVar(index, chgProb, intens, limit);
  }
  public void offSetVar(double chgProb, double intens, double limit){
    int index = 3;
    homeoKineseVar(index, chgProb, intens, limit);
  }

  private void homeoKineseVar(int index, double chgProb, double intens, double limit){
    Vector  homeokinVal = null;
    double  value, delta;

    if(index < 0){ return;};
    if(index > 3){ return;};

    this.gaussRand.initGauss(0.0, intens);
    if(chgProb > 0){

      homeokinVal = this.net.getProperties(); 

      if(homeokinVal.size() == 0){return;};


      if(this.uniRand.nextDouble() <= chgProb)
      {
        value = ((Double) homeokinVal.elementAt(index)).doubleValue();
        delta = this.gaussRand.nextGauss();
        if( (java.lang.Math.abs(value + delta) < java.lang.Math.abs(limit)) &&
            ((value + delta) > 0.0) )
        {
          value = value + delta;
        }
        homeokinVal.setElementAt(new Double(value),index);
        this.net.setProperties(homeokinVal);
      } 
    }
    return;
  }



  /**
   * Changes the sign of the connections with probability of <prob>.
   * This is done according to the currently used learning rule 
   * for dynamical process mode.
   * 
   * @param double prob  
   */
  public void signSwitchOfSynapse(double prob){
    SynapseList synapseList;
    Synapse     synapse; 
    NeuronList  neurons;
    Neuron      neuron;

    if((prob > 0.0 ) && (this.net.getSynapseMode() == SynapseMode.DYNAMIC))
    {
      neurons = this.net.neurons();
      if(neurons == null)
      {
        return;
      }

      for(neurons.start(); neurons.hasMore(); neurons.next())
      {
        synapseList = neurons.neuron().synapses(); 
        if(synapseList != null)
        {
          for(synapseList.start(); synapseList.hasMore(); synapseList.next())
          {
            // stochastic change yes or no
            if(this.uniRand.nextDouble() <= prob)
            {
              // yes
              synapse = synapseList.synapse();
              if(synapse.type() == SynapseType.EXCITATORY)
              {
                synapse.setSynapseType(SynapseType.INHIBITORY);
              }
              else
              {
                synapse.setSynapseType(SynapseType.EXCITATORY);
              }
            }
          }
        }
      }
    }
    return;
  }


  public void weightVar(double chgProb, double intens, double limit){
    NeuronList neurons;
    Synapse    synapse;
    double     value, delta;

    this.gaussRand.initGauss(0.0, intens);


    if((chgProb > 0) && 
        (this.net.getSynapseMode() == SynapseMode.CONVENTIONAL) )
    {
      neurons = this.net.neurons();
      if(neurons == null)
      {
        return;
      }

      for(neurons.start();neurons.hasMore();neurons.next())
      {
        SynapseList synapseList = neurons.neuron().synapses();
        if(synapseList != null)
        {
          for(synapseList.start();synapseList.hasMore();synapseList.next())
          {
            synapse = synapseList.synapse();

            if(synapse.getProcessMode() != ProcessMode.STATIC)
            {
              // weight can be varied
              if(this.uniRand.nextDouble() <= chgProb)
              {
                value = synapse.strength();
                delta = this.gaussRand.nextGauss();
                if(java.lang.Math.abs(value + delta) < java.lang.Math.abs(limit))
                {
                  synapse.setStrength(value + delta);
                }          
                else // make the absulte value decreasing, when the new value
                {     // is out of range
                  if(value < 0.0)
                  {
                    synapse.setStrength(Math.min(value + java.lang.Math.abs(delta),limit));
                  }
                  else if (value > 0.0)
                  {
                    synapse.setStrength(Math.max(value - java.lang.Math.abs(delta), -limit));
                  }

                }
              }
            }
          }
        }
      } 
    }
    return;
  }

  public void decayVar(double chgProb, double intens, double limit){
    NeuronList neurons;
    Neuron     neuron;
    double     value, delta;

    this.gaussRand.initGauss(0.0, intens);

    if(chgProb > 0){
      neurons = this.net.neurons();
      if(neurons == null)
      {
        return;
      }

      for(neurons.start();neurons.hasMore();neurons.next())
      {
        neuron = neurons.neuron();

        if((neuron.getProcessMode() != ProcessMode.STATIC) &&
            (neuron.getNeuronType() != NeuronType.INPUT)         )
        {
          if(this.uniRand.nextDouble() <= chgProb)
          { /* change the value really */
            value = neuron.getKappa();
            delta = this.gaussRand.nextGauss();


            if(java.lang.Math.abs(value + delta) < java.lang.Math.abs(limit))
            {
              neuron.setKappa(value + delta);
            }
            else // make the absulte value decreasing, when the new value
              // is out of range
            {
              if(value < 0.0)
              {
                neuron.setKappa(Math.min(value + java.lang.Math.abs(delta), limit));
              }
              else if(value > 0.0)
              {
                neuron.setKappa(Math.max(value - java.lang.Math.abs(delta), -limit));
              }
            }
          }

        }
      }
    }
  }

  public void biasVar(double chgProb, double intens, double limit){
    NeuronList neurons;
    Neuron     neuron;
    double     value, delta;

    this.gaussRand.initGauss(0.0, intens);

    if(chgProb > 0){
      neurons = this.net.neurons();
      if(neurons == null)
      {
        return;
      }

      for(neurons.start();neurons.hasMore();neurons.next())
      {
        neuron = neurons.neuron();
        if((neuron.getProcessMode() != ProcessMode.STATIC) &&
            (neuron.getNeuronType() != NeuronType.INPUT)         )
        {

          if(this.uniRand.nextDouble() <= chgProb)
          {/* change the value realy */
            value = neuron.getBias();
            delta = this.gaussRand.nextGauss();
            if(java.lang.Math.abs(value + delta) < java.lang.Math.abs(limit))
            {
              neuron.setBias(value + delta);
            }
            else // make the absulte value decreasing, when the new value
              // is out of range
            {
              if(value > 0.0)
              {
                neuron.setBias(Math.max(value - java.lang.Math.abs(delta), -limit));
              }
              else if(value < 0.0)
              {
                neuron.setBias(Math.max(value + java.lang.Math.abs(delta), limit));
              }

            }
          }
        }
      }
    }
  }



  /**
   * variation of the Alpha learn parameter for each neuron in the net.
   * This parameter is changed indepent of the neuron type id STATIC or not.
   * @param    double delta, double intens, double limit
   * @return   none
   */
  public void alphaVar(double chgProb, double intens, double limit, double min){
    NeuronList neurons;
    Neuron     neuron;
    double     value, delta;

    this.gaussRand.initGauss(0.0, intens);

    if(chgProb > 0)
    {

      neurons = this.net.neurons();
      if(neurons == null)
      {
        return;
      }

      for(neurons.start();neurons.hasMore();neurons.next())
      {
        neuron = neurons.neuron();
        if(neuron.getNeuronType() != NeuronType.INPUT)
        {
          if(this.uniRand.nextDouble() <= chgProb)
          {/* change the value realy */
            value = neuron.getAlpha() + this.gaussRand.nextGauss();
            neuron.setAlpha(value);
            if(value < min) 
            {
              neuron.setAlpha(min);
            }
            if(value > limit)
            {
              System.out.println("alpha limit: " + limit);
              neuron.setAlpha(limit);
            }
          }
        }
      }
    }
  }

  public double varVarGauss(
      double current,
      double prob,
      double delta,
      double max,
      double min)
  {
    double value = current;
    this.gaussRand.initGauss(0.0, delta);
    if(prob > 0 && this.uniRand.nextDouble() < prob)
    {
      value += this.gaussRand.nextGauss();
      if(value > max)
      {
        value = max;
      }

      if(value < min)
      {
        value = min;
      }
    }
    return value;
  }


  /**
   * variation of the Beta learn parameter for each neuron in the net.
   * This parameter is changed indepent of the neuron type id STATIC or not.
   * @param    double delta, double intens, double limit
   * @return   none
   */
  public void betaVar(double chgProb, double intens, double limit, double min){
    NeuronList neurons;
    Neuron     neuron;
    double     value, delta;

    this.gaussRand.initGauss(0.0, intens);

    if(chgProb > 0)
    {

      neurons = this.net.neurons();
      if(neurons == null)
      {
        return;
      }

      for(neurons.start();neurons.hasMore();neurons.next())
      {
        neuron = neurons.neuron();
        if(neuron.getNeuronType() != NeuronType.INPUT)
        {
          if(this.uniRand.nextDouble() <= chgProb)
          {/* change the value realy */
            value = neuron.getBeta() + this.gaussRand.nextGauss();
            neuron.setBeta(value);
            if(value < min) 
            {
              neuron.setBeta(min);
            }
            if(value > limit)
            {
              neuron.setBeta(limit);
            }
          }
        }
        if(neuron.getBeta() < 0)
        {
          neuron.setBeta(0);
        }
      }
    }
  }


  /**
   * variation of the Gamma learn parameter for each neuron in the net.
   * This parameter is changed indepent of the neuron type id STATIC or not.
   * @param    double delta, double intens, double limit
   * @return   none
   */
  public void gammaVar(double chgProb, double intens, double limit, double min){
    NeuronList neurons;
    Neuron     neuron;
    double     value, delta;

    this.gaussRand.initGauss(0.0, intens);

    if(chgProb > 0)
    {

      neurons = this.net.neurons();
      if(neurons == null)
      {
        return;
      }

      for(neurons.start();neurons.hasMore();neurons.next())
      {
        neuron = neurons.neuron();
        if(neuron.getNeuronType() != NeuronType.INPUT)
        {
          if(this.uniRand.nextDouble() <= chgProb)
          {/* change the value realy */
            value = neuron.getGamma() + this.gaussRand.nextGauss();
            neuron.setGamma(value);
            if(value < min) 
            {
              neuron.setGamma(min);
            }
            if(value > limit)
            {
              neuron.setGamma(limit);
            }
          }
        }
        if(neuron.getGamma() < 0)
        {
          neuron.setGamma(0);
        }

      }
    }
  }


  /**
   * variation of the Delta learn parameter for each neuron in the net.
   * This parameter is changed indepent of the neuron type id STATIC or not.
   * @param    double delta, double intens, double limit
   * @return   none
   */
  public void deltaVar(double chgProb, double intens, double limit, double min){
    NeuronList neurons;
    Neuron     neuron;
    double     value, delta;

    this.gaussRand.initGauss(0.0, intens);

    if(chgProb > 0)
    {

      neurons = this.net.neurons();
      if(neurons == null)
      {
        return;
      }

      for(neurons.start();neurons.hasMore();neurons.next())
      {
        neuron = neurons.neuron();
        if(neuron.getNeuronType() != NeuronType.INPUT)
        {
          if(this.uniRand.nextDouble() <= chgProb)
          {/* change the value realy */
            value = neuron.getDelta() + this.gaussRand.nextGauss();
            neuron.setDelta(value);
            if(value < min) 
            {
              neuron.setDelta(min);
            }
            if(value > limit)
            {
              neuron.setDelta(limit);
            }
          }
        }
        if(neuron.getDelta() < 0)
        {
          neuron.setDelta(0);
        }

      }
    }
  }






  /**
   * Stochastic insertion of synapses in this EvoObject.
   * The parameter prob is the probability of insertion, 
   * it must be between 0.0 and 1.0. The parameter intens
   * gives the deviation of gaussan distribution, the mean 
   * is fixed with 0.0. 
   *
   * @param double prob 
   * @param double intens
   */

//  public Synapse insSyn(double prob, double intens, int synapseInsertionMode){
//    double value;
//
//    NeuronList sourceList;
//    NeuronList targetList;
//    Neuron source;
//    Neuron target;
//
//    this.gaussRand.initGauss(0.0, intens);
//
//    if(prob > 0.0){
//      sourceList = this.net.neurons();
//      targetList = this.net.neurons();
//      if(sourceList == null) return null;
//
//      for(int n = 0; n < sourceList.size(); n++)
//      {
//        source = sourceList.neuron(n);
//
//        for(int m = 0; m < targetList.size(); m++)
//        {
//          target = targetList.neuron(m);
//
//          if((this.uniRand.nextDouble() <= prob) &&
//              (target.getNeuronType() != NeuronType.INPUT) &&
//              (! ((source.getNeuronType() == NeuronType.INPUT) &&
//                  (source.getProcessMode() == ProcessMode.STATIC)
//                 ) 
//              ))
//          {
//            /* insert synapses really */
//            if(target.getSynapse(source) == null)
//            {
//              /* insertion only if a synapse is not existing yet */
//              value = this.gaussRand.nextGauss();
//              return this.net.addSynapse(source, target, 
//                  value, 
//                  ProcessMode.DYNAMIC);
//
//
//              // System.out.println(value);
//            }
//          }
//        }
//      }
//    }
//    return null; // no synapse added
//  }

  public SynapseList insSyn(double prob, double intens, int synapseInsertionMode){
    double value;

    SynapseList sl = new SynapseList();
    NeuronList sourceList;
    NeuronList targetList;
    Neuron source;
    Neuron target;

    this.gaussRand.initGauss(0.0, intens);

    sourceList = this.net.neurons();
    targetList = this.net.neurons();

    if(sourceList == null) return null; // no neurons

    for(int n = 0; n < sourceList.size(); n++)
    {
      source = sourceList.neuron(n);

      if(source.getProcessMode() == ProcessMode.STATIC) 
      {
        continue; // static neuron, no synapses to be added
      }


      if(source.getNeuronType() == NeuronType.READ_BUFFER) 
      {
        continue; // if source is read buffer -> no synapse
      }

      for(int m = 0; m < targetList.size(); m++)
      {
        target = targetList.neuron(m);

        if(target.getProcessMode() == ProcessMode.STATIC) 
        {
          continue; // static neuron, no synapses to be added
        }


        if(target.getNeuronType() == NeuronType.INPUT) 
        {
          continue; // if target is input -> no synapse
        }

        if(target.getSynapse(source) == null) // no synapse, so lets check
        {

          if(this.uniRand.nextDouble() <= prob)
          {

            value = this.gaussRand.nextGauss();

            sl.add(this.net.addSynapse(source, target, value, 
                  ProcessMode.DYNAMIC));

          }
        }
      }
    }
    return sl;
  }

  /**
   * depending on the insertin probability <em> prob</em>
   * a new hidden neuron will be inserted, which has the connectivity
   * <em> con</em>; if the current net has already <em> maxHidden</em>
   * hidden neurons, no neuron will be inserted.
   * The  weights of new connections / synapses of the inserted neuron,
   * have stochastic initial values gaussan distributed mean  is 0.0 
   * derivation is <em> weightIntens</em>.
   * Decay and bias term of the new neuron are initialized with zero.
   *
   * @param    double prob, double weightIntens, double con, int maxHidden 
   * @return   none
   */
  public Vector insNeu(double prob, double weightIntens, double con, int
      maxHidden, int synapseInsertionMode,
      double alphaInit,
      double betaInit,
      double gammaInit,
      double deltaInit)
  {

    NeuronList newNeurons = new NeuronList();
    SynapseList newSynapses = new SynapseList();
    Vector returnValue = new Vector();
    returnValue.add(newNeurons);
    returnValue.add(newSynapses);
    NeuronList conNeurons;
    Neuron newNeuron;
    Neuron source;
    Neuron target;

    boolean connected = false;

    int m, countNeurons;
    int currentHidden;


    // initalizing of the random generator for the weights of the
    // new connections
    this.gaussRand.initGauss(0.0, weightIntens);

    if((prob <= 0.0) && (con <= 0.0))
    {
      return returnValue;
    }

    conNeurons = this.net.neurons();
    countNeurons = this.net.size();

    currentHidden = (this.net.getHiddenNeurons()).size();
    if( currentHidden >= maxHidden)
    {
      // net has already the maximum number of hidden neurons 
      return returnValue;
    }

    for(int n = 0; n < countNeurons; n++)
    {

      if( currentHidden >= maxHidden) // moved here. Keyan Zahedi 18.02.2006
      {
        // net has already the maximum number of hidden neurons 
        return returnValue;
      }

      if(this.uniRand.nextDouble() <= prob)
      {/* insert a new neuron */

        newNeuron = this.net.addNeuron(0,0,0,ProcessMode.DYNAMIC,NeuronType.HIDDEN);
        newNeuron.setKappa(0.0);
        newNeuron.setAlpha(alphaInit);
        newNeuron.setBeta(betaInit);
        newNeuron.setGamma(gammaInit);
        newNeuron.setDelta(deltaInit);
        newNeurons.add(newNeuron);

        // network has now one hidden neuron more
        currentHidden++;


        /* include new neuron as target */
        for(m = 0; m < countNeurons; m++)
        {
          source = conNeurons.neuron(m);

          // first test, if the current connection already exists
          if(newNeuron.getSynapse(source) == null)
          {
            if(this.uniRand.nextDouble() <= con)
            {
              if(source.getNeuronType() != NeuronType.READ_BUFFER)
              {
                if(source.getProcessMode() != ProcessMode.STATIC)
                {

                  Synapse newSynapse = this.net.addSynapse(source, newNeuron, 
                      this.gaussRand.nextGauss(), 
                      ProcessMode.DYNAMIC);

                  newSynapses.add(newSynapse);

                  if(source != newNeuron)
                  {
                    connected = true;
                  }
                }
              }
            }
          }
        }

        /* include new neuron as source */
        for(m = 0; m < countNeurons; m++)
        {
          target = conNeurons.neuron(m);

          // first test, if the current connection already exists
          if(target.getSynapse(newNeuron) == null)
          {
            /* but not, if the target neuron a input neuron */
            if(this.uniRand.nextDouble() <= con)
            {
              if(target.getNeuronType() != NeuronType.INPUT)
              {
                if(target.getProcessMode() != ProcessMode.STATIC)
                {
                  Synapse newSynapse = this.net.addSynapse(newNeuron, target, 
                      this.gaussRand.nextGauss(), 
                      ProcessMode.DYNAMIC);

                  newSynapses.add(newSynapse);

                  if(target != newNeuron)
                  {
                    connected = true;
                  }
                }
              }
            }
          }
        }

        /* test, if the new neuron got a connection, */
        /* which is not only a self-connection       */
        if(!connected)
        {
          this.net.delNeuron(newNeuron); // delete isolated neuron
          currentHidden--; // Keyan Zahedi 18.02.2006. If this neuron is
                           // deleted, at least try a new one
          newNeurons.remove(newNeuron);
        }  
      }
    } 
    return returnValue;
  }

  public void delSyn(double prob){
    boolean     isolated;
    NeuronList  sourceList;
    NeuronList  targetList;
    NeuronList  neurons;
    Neuron      neuron;
    Neuron      target;
    Neuron      source;
    Synapse     synapse;


    if(prob <= 0.0) return;

    targetList = this.net.neurons();
    sourceList = this.net.neurons();

    for(int m = 0; m < targetList.size(); m++)
    {
      target = targetList.neuron(m);
      if(target.getProcessMode() == ProcessMode.STATIC)
      {
        continue;
      }

      for(int n = 0; n < sourceList.size();n++)
      {
        source = sourceList.neuron(n);
        if(source.getProcessMode() == ProcessMode.STATIC)
        {
          continue;
        }

        synapse = target.getSynapse(source);

        if((synapse != null) && 
            (synapse.getProcessMode() == ProcessMode.DYNAMIC))
        {
          if(this.uniRand.nextDouble() <= prob)
          {
            this.net.delSynapse(source,target);
          }
        }
      }
    }

    targetList = this.net.neurons();
    sourceList = this.net.neurons();
    neurons    = this.net.neurons();
    /* delete isolated neurons                         */
    /* which means, hidden neurons wich are not source */
    /* either target of OTHER neurons                  */
    for(int m = 0;m < neurons.size();m++)
    {
      isolated = true;
      neuron = neurons.neuron(m);
      // changed 06.09.04. only delete isolated neurons, if the are dynamic
      if(neuron.getNeuronType() == NeuronType.HIDDEN &&
          neuron.getProcessMode() == ProcessMode.DYNAMIC) 
      {

        for(int n = 0; n < sourceList.size();n++)
        {
          source = sourceList.neuron(n);
          synapse = neuron.getSynapse(source);
          if((synapse != null) && (source != neuron))
          {
            isolated = false;
          }

        }

        if(isolated)
        {
          for(int n = 0; n < targetList.size(); n++)
          {
            target = targetList.neuron(n);
            synapse = target.getSynapse(neuron);
            if((synapse != null) && ( target != neuron))
            {
              isolated = false;
            }
          }
        }

        if(isolated)
        {
          this.net.delNeuron(neuron);
          m--;
        }
      }
    }
  }

  public void delNeu(double prob){
    NeuronList neurons;
    Neuron neuron;

    if(prob <= 0.0) return;

    neurons = this.net.neurons();

    for(int m = 0; m <  neurons.size();m++)
    {
      neuron = neurons.neuron(m);
      if(this.uniRand.nextDouble() <= prob)
      {
        /* deletes neuron and associated synapsis */
        if( (neuron.getNeuronType() == NeuronType.HIDDEN) &&
            (neuron.getProcessMode() == ProcessMode.DYNAMIC) )
        {
          this.net.delNeuron(neuron);
          m--;
        }
      }
    }
  }


  /**
   * @param    
   * @return   
   */
  public void cleanUp()
  {
    switch(net.getSynapseMode().mode())
    {
      case SynapseMode.SYNAPSE_MODE_DYNAMIC:
        removeNeuronsWithoutInputSynapses();
        break;
      case SynapseMode.SYNAPSE_MODE_CONVENTIONAL:
        break;
      default: // ERROR
        break;
    }
  }

  /**
   * recursivly remove all neurons, that do not have any input-synapse.
   * @param    
   * @return   
   */
  public void removeNeuronsWithoutInputSynapses()
  {
    boolean removedNeuron = false;
    NeuronList delNeurons = new NeuronList();
    for(net.neurons().start(); net.neurons().hasMore(); net.neurons().next())
    {
      Neuron neuron = net.neurons().neuron();
      SynapseList sl = neuron.synapses();
      // ueberlegen was mit den statischen neuronen passiert, wenn sie keine
      // input neuronen haben
      if(neuron.getNeuronType() == NeuronType.HIDDEN &&
          sl == null && neuron.getProcessMode() == ProcessMode.DYNAMIC)
      {
        removedNeuron = true;
        //net.delNeuron(neuron);
        delNeurons.add(neuron);
      }
    }

    for(delNeurons.start(); delNeurons.hasMore(); delNeurons.next())
    {
      net.delNeuron(delNeurons.neuron());
    }

    if(removedNeuron)
    {
      removeNeuronsWithoutInputSynapses();
    }

  }




  /**
   * sets the output perfomrance. The output performance is calculated by 
   * the simulator. The selection  or system performance takes into account 
   * the costs of synapses and <b>hidden</b> neurons. 
   * @param    double perf, double neuCost, double synCost 
   * @return   none
   */
  public void setOutPerf(double perf, double neuCost, double synCost){
    this.outPerf = perf; 
    this.sysPerf = perf - neuCost*((double) this.net.getHiddenNeurons().size()) 
      - synCost*(this.net.getSynapseCount());
    return;
  }

  /**
   * gets the output perfomrance. The output performance is calculated by 
   * the simulator. The selection  or system performance takes into account 
   * the costs of synapses and <b>hidden</b> neurons. 
   * @param    double perf
   * @return   none
   */
  public double getOutPerf(){
    return (this.outPerf) ;
  }


  /**
   * sets the count value. This non negative value number of this one indivudual network. 
   * @param    int count
   * @return   none
   */
  public void setCount(int count){
    if(count < 0)
    {
      this.count = 0;
    }
    else
    {
      this.count = count;
    }
  }

  /**
   * gets the count value. This non negative value number of this one indivudual network. 
   * @param    none
   * @return   int count 
   */
  public int getCount(){
    return this.count;
  }


  /**
   * increases the age value by one. The age value could be used to count network adaptation steps etc. 
   * @param    int age
   * @return   none
   */
  public void incAge(){
    this.age = this.age + 1;
  }

  /**
   * sets the PIN for identification.
   * @param    int nmb
   * @return   none
   */
  public void setPIN(int pin){
    this.myPIN = pin;
  }

  /**
   * gets the PIN for identification   
   * @param   none 
   * @return   int nmb
   */
  public int getPIN(){
    return this.myPIN;
  }

  /**
   * stores the PIN of the parents evoObject.
   * If parents PIN negative, the parent is not known.
   * @param    int nmb
   * @return   none
   */
  public void setParentsPIN(int pin){
    if(pin < 0)
    {
      this.parentsPIN = -1;
    }
    else
    {
      this.parentsPIN = pin;
    }
  }

  /**
   * gets the parents PIN for pedigree reconstruction.   
   * If parents PIN is negative, the parent is not known.
   * @param   none 
   * @return   int nmb
   */
  public int getParentsPIN(){
    return this.parentsPIN;
  }




  /**
   * sets the age value. The age value could be used to count network adaptation steps etc. 
   * @param    int age
   * @return   none
   */
  public void setAge(int age){
    if(age < 0)
    {
      this.age = 0;
    }
    else
    {
      this.age = age;
    }
  }

  /**
   * get the age value. The age value could be used to count network adaptation steps etc. 
   * @param    none
   * @return   int age
   */
  public int getAge(){
    return this.age;
  }


  /**
   * sets the perfomance value 
   * @param    double performance
   * @return   none
   */
  /*
     private void setPerformance(double value){
     performance = value; 
     return;
     }
     */

  /**
   * gets the system perfomrance. The system performance is calculated by 
   * output performance and takes into account 
   * the costs of synapses and <b>hidden</b> neurons. 
   * @param    none
   * @return   double perf
   */
  public double getPerformance(){
    return this.sysPerf;
  }

  /**
   * returns the network  
   * @param    none
   * @return   Net     net
   */
  public Net getNet(){
    return this.net;
  }

  /**
   * stores a new net
   * @param    Net     net
   * @return   none
   */
  public void setNet(Net newnet){
    this.net = newnet;
    return;
  }


  /**
   * Returns a human-readable string-representation of the evo-object.
   * @param    none 
   * @return   String - String representing the evo-object
   */
  public String toString()
  {
    String s = new String(
        "Network = \n" + this.net.toString() + 
        "Performance = " + this.getPerformance() +  "\n" +
        "Age = " + this.getAge() + "\n" +
        "count = " + this.getCount() + "\n"
        );  
    return s;
  }

  public String getGrammarNetString(){
    String s = new String (
        "<?xml version=\"1.0\" encoding=\"LATIN1\"?>"
        +"<!DOCTYPE Net ["
        + this.net.getXMLGrammar()
        +"]>"
        + this.net.toXML());
    s = s.replaceAll("\n"," ");
    return s;
  }

  public String getGrammarString(int spaces){
    String spaceString = new String();
    for(int i=0;i<spaces;i++)
    {
      spaceString = spaceString.concat(" ");
    }

    String s = new String (spaceString
        + spaceString + "<!ELEMENT EvoObject (Net+)>\n"
        + spaceString + "<!ATTLIST EvoObject \n"
        + spaceString + "          Index           CDATA #REQUIRED\n"
        + spaceString + "          OutPerf         CDATA #REQUIRED\n"
        + spaceString + "          SysPerf         CDATA #REQUIRED\n"
        + spaceString + "          Age             CDATA #REQUIRED\n"
        + spaceString + "          PIN             CDATA #IMPLIED\n"
        + spaceString + "          ParentPIN       CDATA #IMPLIED>\n"
        + this.net.getXMLGrammar(spaces + 1)
        );
    return s;
  }

  public String getGrammarString(){
    return getGrammarString(0);
  }


  public String toXML(int index, int spaces){
    String spaceString = new String();
    for(int i=0;i<spaces;i++)
    {
      spaceString = spaceString.concat(" ");
    }

    String s = new String (
        spaceString + "<EvoObject "  
        + " Index=\""      + index            + "\"" 
        + " OutPerf=\""    + this.outPerf     + "\"" 
        + " SysPerf=\""    + this.sysPerf     + "\""
        + " Age=\""        + this.age         + "\"" 
        + " PIN=\""        + this.myPIN       + "\""
        + " ParentsPIN=\"" + this.parentsPIN  + "\" >\n"
        + this.net.toXML(spaces + 2) + "\n"
        + spaceString + "</EvoObject>"
        );
    return s;
  }

  public String toXML(int index){
    return (toXML(index, 0));
  }



  /**
   * For Selfstest. Do <b> not call </b> as class method.
   * @param    none
   * @return   none
   */
  public static void main(String argv[]){
    Gauss g = new Gauss(0.0,1.0);
    Random n = new Random();

    EvoObject obj = new EvoObject(0,n,g);

    System.out.println("EvoObjects \n");
    System.out.println(obj.toString());
  }

}









