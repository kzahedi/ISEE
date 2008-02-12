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

package hinton.fitnessfunctions;

import hinton.ambassador.RobotStruct;
import hinton.executive.FitnessFunction;
import addon.tables.TBenchmarkFrame;

/**
 * This fitness function represents a timer benchmark for analysing your
 * simulator speed. 
 *
 * @author Bjoern Mahn
 */
public class TimerBenchmark extends FitnessFunction
{
  private static TBenchmarkFrame tbf;
  
  private long timer;
  private int  cyc;
  private int  wu;
  private int  count;
  

  /** 
   * Name of my fintess 
   */
  public String getName()
  {
    return "Timer Benchmark";
  }

  /** 
   * Init from my Robot-Struct 
   */
  protected void setInitValues(RobotStruct robotStruct)
  {   
    count = 0;
    cyc   = getProcessParameter().cycles();
    wu    = getProcessParameter().warmUpSteps();
    
    if (tbf == null || !tbf.isShowing())
    {
      tbf = new TBenchmarkFrame();
    }
    timer = System.currentTimeMillis();
    fitnessValue = 0;

    tbf.setCycles(cyc);
    tbf.setWarmup(wu);
    tbf.setTime(0);
    tbf.setTimeCW(0);
  }

  /**
   * Calculate fitness each timestep 
   */
  protected void calculateStep(RobotStruct robotStruct)
  {
    count++;
    fitnessValue = System.currentTimeMillis() - timer;
    tbf.setTime((long)fitnessValue);
    tbf.setTimeCW(fitnessValue / count);

    if (count >= (cyc-1))
    {
      tbf.setLastTime((long)fitnessValue);
      tbf.setLastTimeCW(fitnessValue/cyc);
    }
    tbf.setSteps(count);
  }

  /**
   * Reset fitness calculation
   */
  protected void doReset()
  {
  }

}


