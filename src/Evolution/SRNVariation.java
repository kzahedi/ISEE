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

import cholsey.Net;
import cholsey.Neuron;
import cholsey.Synapse;
import cholsey.SynapseList;
import cholsey.SynapseType;


public class SRNVariation 
{

  private static double SRN_alpha = 0;
  private static double SRN_beta  = 0;
  private static double SRN_gamma = 0;
  private static double SRN_delta = 0;

  private final static double MINIMUM_VALUE = 0.001;

  private static Random random = new Random();

  // **************************************************************************
  // done 17.02.2006
  // **************************************************************************
  public static void changeWeight(
      Net net,
      double synapseChangePropability)
  {
    for(net.neurons().start();
        net.neurons().hasMore();
        net.neurons().next())
    {
      Neuron neuron = net.neurons().neuron();
      SynapseList sl = neuron.synapses();
      if(sl != null)
      {
        for(sl.start(); sl.hasMore(); sl.next())
        {
          Synapse s = sl.synapse();
          if(random.nextDouble() <= synapseChangePropability)
          {
            s.toggle();
          }
        }
      }
    }
  }



  public static void insertNeuron(
      Net net,
      Neuron neuron)
  {
    neuron.setAlpha(SRN_alpha);
    neuron.setBeta(SRN_beta);
    neuron.setGamma(SRN_gamma);
    neuron.setDelta(SRN_delta);
  }

  public static void insertSynapse(
      Net net,
      SynapseList sl,
      int synapseInsertionMode)
  {

    if(sl == null || sl.size() == 0)
    {
      return;
    }

//    System.err.println("SRN synapse insertion mode not defined");
//    switch(synapseInsertionMode)
//    {
//      case EvoObject.SYN_INS_MODE_DEFAULT:
//        defaultMode(net, s);
//        break;
//      case EvoObject.SYN_INS_MODE_DALE:
//        dalesMode(net, s);
//        break;
//      case EvoObject.SYN_INS_MODE_DALE_2:
//        dalesMode2(net, s);
//        break;
//    }
  }

  public static void cleanUpParameters(Net net, int parameterMode)
  {
    switch(parameterMode)
    {
      case EvoObject.PARAMETER_MODE_DEFAULT:
        //setMinimumValues(net);
        break; // nothing
      case EvoObject.PARAMETER_MODE_C_EQ_D:
        setAllEqualGammaDelta(net);
        break;
      case EvoObject.PARAMETER_MODE_B_EQ_C_EQ_D:
        setAllEqualBetaGammaDelta(net);
        break;
      case EvoObject.PARAMETER_MODE_1_5_C_EQ_D:
        setAllEqualDeltaOnePointFiveTimesGamma(net);
        break;
    }
  }


  private static void setMinimumValues(Net net)
  {
    for(net.neurons().start();
        net.neurons().hasMore();
        net.neurons().next())
    {
      Neuron neuron = net.neurons().neuron();
      neuron.setAlpha(Math.max(MINIMUM_VALUE, neuron.getAlpha()));
      neuron.setBeta(Math.max(MINIMUM_VALUE, neuron.getBeta()));
      neuron.setGamma(Math.max(MINIMUM_VALUE, neuron.getGamma()));
      neuron.setDelta(Math.max(MINIMUM_VALUE, neuron.getDelta()));
    }

  }



  private static void defaultMode(Net net, Synapse s)
  {
    // add EXCITATORY and INHIBITORY synapses
    // 50 to 50 inihibitory or excitatory
    if( random.nextFloat() < 0.5)
    {
      s.setSynapseType(SynapseType.INHIBITORY);
    }
    else
    {
      s.setSynapseType(SynapseType.EXCITATORY);
    }
  }

  private static void dalesMode(Net net, Synapse synapse)
  {
    Neuron source = synapse.getSource();
    Neuron target = synapse.getDestination();
    if(target.getSynapse(source) != null)
    {
      return;
    }

    switch(source.getDaleMode())
    {
      case Neuron.NEURON_DALE_UNDEFINED:
        if(random.nextFloat() < 0.5)
        {
          synapse.setSynapseType(SynapseType.INHIBITORY);
          source.setDaleMode(Neuron.NEURON_DALE_INHIBITORY);
        }
        else
        {
          synapse.setSynapseType(SynapseType.EXCITATORY);
          source.setDaleMode(Neuron.NEURON_DALE_EXCITATORY);
        }
        break;
      case Neuron.NEURON_DALE_EXCITATORY:
        synapse.setSynapseType(SynapseType.EXCITATORY);
        break;
      case Neuron.NEURON_DALE_INHIBITORY:
        synapse.setSynapseType(SynapseType.INHIBITORY);
        break;
    }
  }


  // **************************************************************************
  // normalise over entire net
  // **************************************************************************
  private static void dalesMode2(Net net, Synapse synapse)
  {
    Neuron source = synapse.getSource();
    Neuron target = synapse.getDestination();
    if(target.getSynapse(source) != null)
    {
      return;
    }

    switch(source.getDaleMode())
    {
      case Neuron.NEURON_DALE_UNDEFINED:
        int inhibitory = 0;
        int excitatory = 0;
        for(net.neurons().start();
            net.neurons().hasMore();
            net.neurons().next())
        {
          Neuron neuron = net.neurons().neuron();
          SynapseList sl = neuron.synapses();
          if(sl != null)
          {
            for(sl.start(); sl.hasMore(); sl.next())
            {
              Synapse s = sl.synapse();
              if(s.type() == SynapseType.INHIBITORY)
              {
                inhibitory += 1;
              }
              else
              {
                excitatory += 1;
              }
            }
          }
          if( inhibitory < excitatory )
          {
            synapse.setSynapseType(SynapseType.INHIBITORY);
            source.setDaleMode(Neuron.NEURON_DALE_INHIBITORY);
          }
          else
          {
            synapse.setSynapseType(SynapseType.EXCITATORY);
            source.setDaleMode(Neuron.NEURON_DALE_EXCITATORY);
          }

        }
        break;
      case Neuron.NEURON_DALE_EXCITATORY:
        synapse.setSynapseType(SynapseType.EXCITATORY);
        break;
      case Neuron.NEURON_DALE_INHIBITORY:
        synapse.setSynapseType(SynapseType.INHIBITORY);
        break;
    }
  }

  private static void setAllEqualGammaDelta(Net net)
  {
    for(net.neurons().start();
        net.neurons().hasMore();
        net.neurons().next())
    {
      Neuron neuron = net.neurons().neuron();
      double value = Math.max(MINIMUM_VALUE, neuron.getGamma());
      neuron.setGamma(value);
      neuron.setDelta(value);
    }
  }

  private static void setAllEqualBetaGammaDelta(Net net)
  {
    for(net.neurons().start();
        net.neurons().hasMore();
        net.neurons().next())
    {
      Neuron neuron = net.neurons().neuron();
      double value = Math.max(MINIMUM_VALUE, neuron.getBeta());
      neuron.setBeta(value);
      neuron.setGamma(value);
      neuron.setDelta(value);
    }
  }

  private static void setAllEqualDeltaOnePointFiveTimesGamma(Net net)
  {
    for(net.neurons().start();
        net.neurons().hasMore();
        net.neurons().next())
    {
      Neuron neuron = net.neurons().neuron();
      double value = Math.max(MINIMUM_VALUE, neuron.getGamma());
      neuron.setGamma(value);
      neuron.setDelta(1.5*value);
    }

  }

  // **************************************************************************
  // all neurons have the same values
  // **************************************************************************


  public static void setInitialValue(double alphaInit, double betaInit, double
      gammaInit, double deltaInit)
  {
    SRN_alpha = alphaInit;
    SRN_beta = betaInit;
    SRN_gamma = gammaInit;
    SRN_delta = deltaInit;
  }

  public static void setAlpha(Net net, double alpha)
  {
    SRN_alpha = alpha;
    for(int index=0; index < net.neurons().size(); index++)
    {
      Neuron n = (Neuron)net.neurons().get(index);

      n.setAlpha(SRN_alpha);
    }
  }

  public static double getAlpha()
  {
    return SRN_alpha;
  }

  public static void setBeta(Net net, double beta)
  {
    SRN_beta = beta;
    for(int index=0; index < net.neurons().size(); index++)
    {
      Neuron n = (Neuron)net.neurons().get(index);

      n.setBeta(SRN_beta);
    }
  }

  public static double getBeta()
  {
    return SRN_beta;
  }

  public static void setGamma(Net net, double gamma)
  {
    SRN_gamma = gamma;
    for(int index=0; index < net.neurons().size(); index++)
    {
      Neuron n = (Neuron)net.neurons().get(index);

      n.setGamma(SRN_gamma);
    }
  }

  public static double getGamma()
  {
    return SRN_gamma;
  }

  public static void setDelta(Net net, double delta)
  {
    SRN_delta = delta;
    for(int index=0; index < net.neurons().size(); index++)
    {
      Neuron n = (Neuron)net.neurons().get(index);

      n.setDelta(SRN_delta);
    }
  }

  public static double getDelta()
  {
    return SRN_delta;
  }



  public static void variationLearingParamters(Net net, int parameterMode,
      double alphaProbability, double alphaVariation, 
      double alphaMax, double alphaMin,
      double betaProbability, double betaVariation, 
      double betaMax, double betaMin,
      double gammaProbability, double gammaVariation, 
      double gammaMax, double gammaMin,
      double deltaProbability, double deltaVariation, 
      double deltaMax, double deltaMin)
  {
    switch(parameterMode)
    {
      case EvoObject.PARAMETER_MODE_DEFAULT: // any changes
        changeDefault(net, 
            alphaProbability, alphaVariation, alphaMax, alphaMin,
            betaProbability, betaVariation, betaMax, betaMin,
            gammaProbability, gammaVariation, gammaMax, gammaMin,
            deltaProbability, deltaVariation, deltaMax, deltaMin);
        break; // nothing
      case EvoObject.PARAMETER_MODE_C_EQ_D:
        setAllEqualGammaDelta(net);
        break;
      case EvoObject.PARAMETER_MODE_B_EQ_C_EQ_D:
        setAllEqualBetaGammaDelta(net);
        break;
      case EvoObject.PARAMETER_MODE_1_5_C_EQ_D:
        setAllEqualDeltaOnePointFiveTimesGamma(net);
        break;
    }
  }

  private static void changeDefault( Net net, 
      double alphaProbability, double alphaVariation, 
      double alphaMax, double alphaMin,
      double betaProbability, double betaVariation, 
      double betaMax, double betaMin,
      double gammaProbability, double gammaVariation, 
      double gammaMax, double gammaMin,
      double deltaProbability, double deltaVariation, 
      double deltaMax, double deltaMin)
  {
    changeAlpha(alphaProbability, alphaVariation, alphaMax, alphaMin);
    changeBeta(betaProbability, betaVariation, betaMax, betaMin);
    changeGamma(gammaProbability, gammaVariation, gammaMax, gammaMin);
    changeDelta(deltaProbability, deltaVariation, deltaMax, deltaMin);

//    System.out.println("setting neuron values to: " 
//        + " " + SRN_alpha
//        + " " + SRN_beta
//        + " " + SRN_gamma
//        + " " + SRN_delta);
    for(net.neurons().start(); net.neurons().hasMore(); net.neurons().next())
    {
      Neuron n = net.neurons().neuron();
      n.setAlpha(SRN_alpha);
      n.setBeta(SRN_beta);
      n.setGamma(SRN_gamma);
      n.setDelta(SRN_delta);
    }
  }

  private static void changeAlpha(double alphaProbability, 
      double alphaVariation, double alphaMax, double alphaMin)
  {
    if (random.nextFloat() <= alphaProbability)
    {
      double deltaAlpha =  ( 2 * random.nextFloat() - 1 ) * alphaVariation;
      SRN_alpha += deltaAlpha;
      if(SRN_alpha > alphaMax)
      {
        SRN_alpha = alphaMax;
      }

      if(SRN_alpha < alphaMin)
      {
        SRN_alpha = alphaMin;
      }
    }
  }

  private static void changeBeta(double betaProbability, 
      double betaVariation, double betaMax, double betaMin)
  {
    if (random.nextFloat() <= betaProbability)
    {
      double deltaBeta =  ( 2 * random.nextFloat() - 1 ) * betaVariation;
      SRN_beta += deltaBeta;
      if(SRN_beta > betaMax)
      {
        SRN_beta = betaMax;
      }

      if(SRN_beta < betaMin)
      {
        SRN_beta = betaMin;
      }
    }
  }


  private static void changeGamma(double gammaProbability, 
      double gammaVariation, double gammaMax, double gammaMin)
  {
    if (random.nextFloat() <= gammaProbability)
    {
      double deltaGamma =  ( 2 * random.nextFloat() - 1 ) * gammaVariation;
      SRN_gamma += deltaGamma;
      if(SRN_gamma > gammaMax)
      {
        SRN_gamma = gammaMax;
      }

      if(SRN_gamma < gammaMin)
      {
        SRN_gamma = gammaMin;
      }
    }
  }


  private static void changeDelta(double deltaProbability, 
      double deltaVariation, double deltaMax, double deltaMin)
  {
    if (random.nextFloat() <= deltaProbability)
    {
      double deltaDelta =  ( 2 * random.nextFloat() - 1 ) * deltaVariation;
      SRN_delta += deltaDelta;
      if(SRN_delta > deltaMax)
      {
        SRN_delta = deltaMax;
      }

      if(SRN_delta < deltaMin)
      {
        SRN_delta = deltaMin;
      }
    }
  }





}
