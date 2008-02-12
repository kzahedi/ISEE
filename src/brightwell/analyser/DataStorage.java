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

package brightwell.analyser;


import cholsey.Net;
import cholsey.Neuron;
import cholsey.SynapseMode;
import cholsey.Transferfunction;

public class DataStorage 
{

  public final static int INITIAL_ACTIVITY_RANDOM       = 0;
  public final static int INITIAL_ACTIVITY_USER_DEFINED = 1;

  public final static String[] INITIAL_ACTIVITY_MODES =
  {
    "random",
    "user defined"
  };

  private Net chaosNet = null;

  private double[] xRange = 
  {
    0.0,
    0.0,
  };

  private double[] yRange = 
  {
    0.0,
    0.0
  };

  private double[] chaosRange =
  {
    -1.0,
     1.0
  };

  private double[] transmitterRange =
  {
    -1.0,
     1.0
  };

  private double[] receptorRange =
  {
    -1.0,
     1.0
  };

  private double[] synapseRange =
  {
    -1.0,
     1.0
  };

  private double[] liapunovRange =
  {
    -1.0,
     1.0
  };





  private int convergenceIterations = 0;
  private int drawIterations = 0;

  private Net net = null;

  private int initialActivit = INITIAL_ACTIVITY_RANDOM;

  private int[] windowSize = 
  {
    100,
    100
  };

  private boolean useBigBoints = false;


  public DataStorage()
  {
    this.chaosNet = new Net();
    chaosNet.setTransferfunction(Transferfunction.SIGM);
    chaosNet.setSynapseMode(SynapseMode.CONVENTIONAL);
    Neuron neuron = chaosNet.addNeuron();
    chaosNet.addSynapse(neuron, neuron, -16);
    neuron.setKappa(0.8);
    neuron.setBias(5);
  }
  

  // **************************************************************************
  // get
  // **************************************************************************

  public boolean getUseBigPoints()
  {
    return useBigBoints;
  }

  // return the value of the net mapped to the chaosRange intervall
  public double getChaosValue()
  {
    return  chaosRange[0] + 
      ( chaosNet.getNeuron(0).getOutput() * ( chaosRange[1] - chaosRange[0]));
  }

  public double getChaosMinValue()
  {
    return chaosRange[0];
  }

  public double getChaosMaxValue()
  {
    return chaosRange[1];
  }

  public double[] getChaosRange()
  {
    return chaosRange;
  }

  public double[] getReceptorRange()
  {
    return receptorRange;
  }


  public double[] getSynapseRange()
  {
    return synapseRange;
  }

  public double[] getLiapunovRange()
  {
    return liapunovRange;
  }



  public double[] getTransmitterRange()
  {
    return transmitterRange;
  }


  public Net getChaoticNet()
  {
    return chaosNet;
  }
  
  public double[] getXRange()
  {
    return xRange;
  }

  public double[] getYRange()
  {
    return yRange;
  }

  public int getConvergenceIterations()
  {
    return convergenceIterations;
  }

  public int getDrawIterations()
  {
    return drawIterations;
  }


  public Net getNet() 
  {
    return net;
  }

  public int getInialActivityMode()
  {
    return initialActivit;
  }

  public int[] getWindowSize()
  {
    return windowSize;
  }

  // **************************************************************************
  // set
  // **************************************************************************
  public void setUseBigPoints(boolean useBigBoints)
  {
    this.useBigBoints = useBigBoints;
  }

  public void setChaosMinMax(double min, double max)
  {
    chaosRange[0] = min;
    chaosRange[1] = max;
  }

  public void setSynapseRange(double min, double max)
  {
    synapseRange[0] = min;
    synapseRange[1] = max;
  }

  public void setLiapunovRange(double min, double max)
  {
    liapunovRange[0] = min;
    liapunovRange[1] = max;
  }


  public void setReceptorRange(double min, double max)
  {
    receptorRange[0] = min;
    receptorRange[1] = max;
  }

  public void setTransmitterRange(double min, double max)
  {
    transmitterRange[0] = min;
    transmitterRange[1] = max;
  }


  public void setXRange(double left, double right)
  {
    xRange[0]=left;
    xRange[1]=right;
  }

  public void setYRange(double lower, double upper)
  {
    yRange[0]=lower;
    yRange[1]=upper;
  }

  public void setConvergenceIterations(int convergenceIterations)
  {
    this.convergenceIterations = convergenceIterations;
  }

  public void setDrawIterations(int drawIterations)
  {
    this.drawIterations = drawIterations;
  }


  public void setNet(Net net) 
  {
    this.net = net;
  }

  public void setInialActivityMode(int initialActivit)
  {
    this.initialActivit = initialActivit;
  }

  public void setWindowSize(int width, int height)
  {
    windowSize[0] = width;
    windowSize[1] = height;
  }

  // **************************************************************************
  // output
  // **************************************************************************
  public String toString()
  {
    String string = new String();
    string=string.concat("Xrange from " 
        + xRange[0] 
        + " to " + xRange[1] 
        + "\n");
    string=string.concat("Yrange from " 
        + yRange[0] 
        + " to " 
        + yRange[1] 
        + "\n");

    string=string.concat("SynapseRange from " 
        + synapseRange[0] 
        + " to " 
        + synapseRange[1] 
        + "\n");

    string=string.concat("ReceptorRange from " 
        + receptorRange[0] 
        + " to " 
        + receptorRange[1] 
        + "\n");

    string=string.concat("TransmitterRange from " 
        + transmitterRange[0] 
        + " to " 
        + transmitterRange[1] 
        + "\n");



    string=string.concat("Draw iterations : " 
        + drawIterations 
        + "\n");
    string=string.concat("Convergence iterations : " 
        + convergenceIterations 
        + "\n");
    string=string.concat("initals activity mode : " 
        + initialActivit 
        + " = " + INITIAL_ACTIVITY_MODES[initialActivit]
        + "\n");
    string=string.concat("synapse mode : " 
        + net.getSynapseMode().toString() 
        + "\n");
    string=string.concat("window size : " 
        + windowSize[0] 
        +"x"
        +windowSize[1] 
        + "\n");
    string=string.concat("net :\n " 
        + net.toString() 
        + "\n");
    return string;
  }

}
