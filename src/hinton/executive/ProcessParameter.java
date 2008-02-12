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

package hinton.executive;

import java.util.Random;

import cholsey.Net;

/**
 * Implements the data-storeage class for the process parameter. Process
 * parameter are: 
 * <ul>
 * <li> cycles : the number of process cycles for the net </li>
 * <li> iterations : the number of iterations in each process cycles for the net
 * </li>
 * <li> net : the current net, which is to be processed </li>
 * <li> k0 - k3: evalutaions constants for the fitness function </li>
 * </ul>
 *
 * 
 */
public class ProcessParameter
{
  private int warmUpSteps = 0;  
  private int cycles      = 2000;
  private int iterations  = 1;
  private int tries       = 1;
  private int initialInterations = 3;
  private Net net         = null;
  private double[] constants = new double[4];
  private double maxSpeed = 5;
  private boolean display = false;
  private FitnessFunctionInterface fitnessFunction = null;
  private Random random = new Random();

  public ProcessParameter()
  {
    for(int i=0;i<constants.length;i++)
    {
      constants[i]=0;
    }
  }

  public void setFitnessFunction(FitnessFunctionInterface fitnessFunction)
  {
    this.fitnessFunction = fitnessFunction;
  }

  public void setRandomSeed(int seed)
  {
    long randomSeed = (long)seed;
    random = new Random(seed);
  }

  public double nextDouble()
  {
    return random.nextDouble();
  }

  public int nextInt()
  {
    return random.nextInt();
  }

  public int nextInt(int size)
  {
    return random.nextInt(size);
  }


  public float nextFloat()
  {
    return random.nextFloat();
  }


  public boolean nextBoolean()
  {
    return random.nextBoolean();
  }

  public void setMaxSpeed(double maxSpeed)
  {
    this.maxSpeed = maxSpeed;
  }

  public double getMaxSpeed()
  {
    return maxSpeed;
  }

  public void setConstants(double k0, double k1, double k2, double k3)
  {
    constants[0]=k0;
    constants[1]=k1;
    constants[2]=k2;
    constants[3]=k3;
  }


  public boolean getDisplay()
  {
    return display;
  }
  
  public double getConstant(int index)
  {
    if (constants == null)
    {
      return 1;
    }
    if(index >= constants.length)
    {
      return 1;
    }
    return constants[index];
  }

  public int cycles()
  {
    return cycles;
  }

  public int warmUpSteps()
  {
    return warmUpSteps;
  }

  public int iterations()
  {
    return iterations;
  }

  public int tries()
  {
    return tries;
  }

  public int getInitialIterations()
  {
    return initialInterations;
  }

  public FitnessFunctionInterface getFitnessFunction()
  {
    return fitnessFunction;
  }

  public Net net()
  {
    return net;
  }

  public void setDisplay(boolean display)
  {
    this.display = display;
  }

  public void setIterations(int iterations)
  {
    this.iterations = iterations;
  }

  public void setCycles(int cycles)
  {
    this.cycles = cycles;
  }

  public void setWarmUpSteps(int steps)
  {
    this.warmUpSteps = steps;
  }

  public void setTries(int tries)
  {
    this.tries = tries;
  }


  public void setNet(Net net)
  {
    this.net = net;
  }

  public void setInitialIterations(int initialInterations)
  {
    this.initialInterations = initialInterations;
  }
}
