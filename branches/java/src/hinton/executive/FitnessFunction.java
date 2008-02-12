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

import hinton.ambassador.RobotStruct;


/**
 * Implementation of the Fitnessfunction. This class provides various number ob
 * fitnessfunctions, which can be selected to evaluate the running net.
 *
 */
public abstract class FitnessFunction implements FitnessFunctionInterface
{
  protected boolean calculateFitness;

  private boolean bumped;

  protected double  fitnessValue;

  protected ProcessParameter processParameter;

  public abstract String getName();  

  protected abstract void doReset();

  protected abstract void setInitValues(RobotStruct robotStruct);

  protected abstract void calculateStep(RobotStruct robotStruct);


  public FitnessFunction()
  {
    //nothing
  }

  public final void reset()
  {
    bumped = false;
    calculateFitness = false;
    fitnessValue = 0;
    doReset();
  }

  public final void setProcessParameter(ProcessParameter processParameter)
  {
    this.processParameter = processParameter;
  }

  public final ProcessParameter getProcessParameter()
  {
    return this.processParameter;  
  }

  /**
   * Calculate the fitness function. This function must be called after each
   * process step of the net. The fitness function can only use information that
   * is captured in the robotStruct data-struct.
   * @param    robotStruct the struct with all the information to calculate the
   * fitness
   * @see hinton.ambassador.RobotStruct
   */
  public final void calculate(RobotStruct robotStruct)
  {
    double x,y,a,b;

    if(calculateFitness == false)
    {
      calculateFitness = true;
      setInitValues(robotStruct);
      calculateStep(robotStruct);
      return;
    }
    calculateStep(robotStruct);
  }

  /**
   * Returns the current fitness value.
   * @return   the current fitness value
   */
  public final double getFitnessValue()
  {
    return fitnessValue;
  }

  public final void setFitnessValue(double fitnessValue)
  {
    this.fitnessValue = fitnessValue;
  }

  public final String toString()
  {
    return getName();
  }

  public void setBumped()
  {
    this.bumped = true;
  }

  public boolean bumped()
  {
    return bumped;
  }

  public void resetBumped()
  {
    bumped = false;
  }
}
