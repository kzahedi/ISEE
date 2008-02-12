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

import hinton.ambassador.AmbassadorInterface;
import hinton.broker.EvoComInterface;
import hinton.gui.LastNetDialog;

import javax.swing.JLabel;



/**
 * Implements the processing thread.
 * 
 */
public class ProcessorThread implements Runnable 
{
  private Processor process = null;
  private EvoComInterface evoCom = null;
  private ProcessParameter processParameter = null;
  private JLabel status = null;
  private boolean keepOnRunning = true;
  private FitnessFunctionInterface ff = null;
  private AmbassadorInterface ambassador = null;
  private LastNetDialog lastNetDialog = null;
  public ProcessorThread(
      Processor process,
      EvoComInterface evoCom,
      JLabel status,
      LastNetDialog lastNetDialog)
  {
    this.process = process;
    this.evoCom  = evoCom;
    this.status  = status;
    this.lastNetDialog = lastNetDialog;
  }

  public void stop()
  {
    keepOnRunning = false;
    process.stop();
    System.out.println("ProcessorThread: stopped");
    process.getAmbassador().stop();
    status.setText("stopped");
  }

  public void setProcessParameter(ProcessParameter processParameter)
  {
    this.processParameter = processParameter;
  }


  private void runSingleMode()
  {
    // needed because global keepOnRunning is read twice and MUST contain in
    // both cases the same value
    boolean syncKeepOnRunning;    

    process.setDrawEveryStep(true);
    ambassador = process.getAmbassador();
    ambassador.setNewStartPosition();
    ambassador.reset();
    syncKeepOnRunning = keepOnRunning;

    while(syncKeepOnRunning)
    {
      process.setParameter(processParameter);
      process.run();
      ff = process.getFitnessFunction();
      if(lastNetDialog.isVisible())
      {
        lastNetDialog.update(ff.getFitnessValue(), process.getCountedCylces());
      }
      
      //commented by bjoern mahn, PLEASE TEST!
      //ambassador.setNewStartPosition();
      //ambassador.nextTry();

      // added by bjoern mahn, PLEASE TEST!
      syncKeepOnRunning = keepOnRunning;
      if (syncKeepOnRunning)
      {
        ambassador.setNewStartPosition();
        ambassador.nextTry();
      }
    }
    // reset the ambassador, as the net-processing is over
    // commented by bjoern mahn, PLEASE TEST!
    // ambassador.reset(); 

  }

  private void runEvolutionMode()
  {
    double bestFitnessValue = 0;
    ambassador = process.getAmbassador();
    process.setDrawEveryStep(false);

    ff = process.getFitnessFunction();

    while(keepOnRunning && evoCom.isRunning())
    {
      evoCom.communicate(); // get a new net
      if(evoCom.newGeneration())
      {
        evoCom.clearNewGenerationFlag();
        ambassador.setNewStartPosition();
        lastNetDialog.resetPanel();
      }
      processParameter = evoCom.getProcessParameter();
      process.setParameter(processParameter);
      // used to be at the end of the function processor.run()
      // reset the ambassador, as the net-processing is over
      ambassador.reset();
      ff.reset();
      
      process.run(); // runs a net
      if(lastNetDialog.isVisible())
      {
        lastNetDialog.update(ff.getFitnessValue(), process.getCountedCylces());
      }
      System.out.println("FitnessFunction: " + ff.toString());
      evoCom.setFitnessValue(ff.getFitnessValue());
      if (ff.getFitnessValue() > bestFitnessValue)
      {
        bestFitnessValue = ff.getFitnessValue();
      }
    }
  }
  
  public void run()
  {
    keepOnRunning = true;
    status.setText("running");
    // if evoCom == null -> net was loaded over the "load net" button, that
    // means there is no communication to any evoCom needed, all the rest is
    // done by the processor anyway
    if (evoCom == null)
    {
      runSingleMode();
    }
    // communication with evoCom must be organised
    else
    {
      runEvolutionMode();
    }
    process.getAmbassador().stop();
    status.setText("stopped");
  }

  public ProcessParameter getProcessParameter()
  {
    return processParameter;
  }

}

