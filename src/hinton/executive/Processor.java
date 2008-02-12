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
import hinton.ambassador.RobotStatus;
import hinton.ambassador.RobotStruct;
import hinton.analyser.Analyser;
import cholsey.Net;

/**
 * Implementation of the net-processing class. This class provides methods to
 * process the neural net and the communication with the ambassadors/robots as
 * well as the brokers.
 *
 * @see hinton.ambassador
 * @see hinton.broker
 * 
 */
public class Processor 
{

  private ProcessParameter processParameter = null;
  private AmbassadorInterface ambassador = null;
  private RobotStruct robotStruct = null;
  private boolean keepOnRunning = true;
  private Analyser analyser = null;
  private final static int MAX_NUMBER_OF_LOSERS = 20;
  private int numberOfLosers = 0;
  private double bestFitnessValue = -1;
  private boolean updateAnalyseEverStep = false;
  private boolean drawEverStep = false;
  private int cyclesSurvived = 0;

  private final static int ROBOT_OK     = 0;
  private final static int ROBOT_BUMPED = 1;

  private FitnessFunctionInterface ff = null;


  public Processor()
  {

  }

  public void setDrawEveryStep(boolean drawEverStep)
  {
    this.drawEverStep = drawEverStep;
  }

  public void setUpdateAnalyseEverStep(boolean updateAnalyseEverStep)
  {
    this.updateAnalyseEverStep = updateAnalyseEverStep;
  }

  public void setFitnessFunction(FitnessFunctionInterface ff)
  {
    this.ff = ff;
  }

  public void setAmbassador(AmbassadorInterface ai)
  {
    this.ambassador = ai;
    this.robotStruct = ai.robotStruct();
  }

  public int getCountedCylces()
  {
    return cyclesSurvived;
  }

  public void setAnalyser(Analyser analyser)
  {
    this.analyser = analyser;
    if(this.analyser != null && processParameter != null && 
        processParameter.net() != null)
    {
      this.analyser.setNet(processParameter.net());
      this.analyser.setProcessParameter(processParameter);
    }
  }

  public void run()
  {
    int cycles        = processParameter.cycles();
    int iterations    = processParameter.iterations();
    int tries         = processParameter.tries();
    int warmUpSteps   = processParameter.warmUpSteps();
    int initialInterations = processParameter.getInitialIterations();
    double speed      = processParameter.getMaxSpeed();

    double accumulatedFitness = 0; // adding up the fitness for each try

    int inputSize     = 0;
    int outputSize    = 0;
    int robotStatus  = ROBOT_OK;
    keepOnRunning = true;

    System.out.println("****************************************");
    System.out.println("Tries        : " + tries);
    System.out.println("Cycles       : " + cycles);
    System.out.println("Iterations   : " + iterations);
    System.out.println("Speed Factor : " + speed);
    System.out.println("k0 - k3      : " + processParameter.getConstant(0) 
        + ", " + processParameter.getConstant(1) 
        + ", " + processParameter.getConstant(2) 
        + ", " + processParameter.getConstant(3));


    // problem if no ambassdor is set. at least on ambassdor must be there,
    // either robot or simulator, otherwise processing the net doesnt really
    // make sence
    if (ambassador == null)
    {
      System.out.println("NO ROB OR SIM COM SELECTED\n");
      System.exit(-1);
    }
    // get the current neural net
    Net net = processParameter.net();
    ambassador.setSpeedFactor(processParameter.getMaxSpeed());

    // reset the net before starting (14.02.2006, Keyan Zahedi)
    net.reset();

    // determine the number of inputs. if the number of input neurons exceeds
    // the number of output-values of the robot, then the last few neuron will
    // not receive any input.
    // if the number of robot output-values exceeds the number of input neurons,
    // then only the first few output-values will be passed through to the
    // input-neurons
    inputSize = Math.min(net.getInputNeurons().size(),
        robotStruct.outputKeysSize());

    // determine the number of output-neurons. if the number of output neurons
    // exceeds the number of input-devices of the robot, than the last
    // output-neurons will no be taken into account. if the number of
    // input-devices of the robot exceeds the number of output-neurons, than the
    // last few input-devices will not be feed by any output-neuron
    outputSize = Math.min(net.getOutputNeurons().size(),
        robotStruct.inputKeysSize());


    // tries for the network
    for(int tried=0; tried < tries && keepOnRunning; tried++)
    {
      // next robot, if robot has crashed into the wall (not if next try)
      // deprecated
      
      // changed 13.07.2006 Keyan Zahedi
//      if(robotStatus == ROBOT_BUMPED)
//      {
//        robotStatus = ROBOT_OK;
//        break;
//      }

      // process the net at least one time, as long as the stop button is not
      // pressed
      cyclesSurvived = 0;
      for(int i=0;i < cycles && keepOnRunning;i++) // at least one cycle
      {
        cyclesSurvived++;
        // get new data from the ambassador
        ambassador.update();

        /*
        // hand the newest data to the fitnessfunction class -> calculate
        // fitnessfunction
        ff.calculate(robotStruct);
         */


        /*
        // if robot has bumped -> stop this run, and fitness is 0
        // if robot has next try -> stop, and go on with next try
        if(ambassador.getRobotStatus() == RobotStatus.BUMPED)
        {
        //System.out.println("Robot has bumped");
        robotStatus = ROBOT_BUMPED;
        break;
        }

        if( ambassador.getRobotStatus() == RobotStatus.NEXT_TRY)
        {
        robotStatus = ROBOT_OK;
        //System.out.println("Robot has new try");
        break;
        }
         */


        // for every possible input-neuron <-> robot-output-device mapping do:
        // set the i-th input-neuron to the assigned output-device
        for(int k=0;k<inputSize;k++)
        {
          net.setInputNeuronValue(k,Double.parseDouble(""+
                robotStruct.getOutputValue(
                  robotStruct.getOutputObject(k))));
        }

        /**  OLD ANALYSER STUFF
        // if brain surgeon is open
        if(analyser.isVisible())
        {
        analyser.updateNet();
        }
         */
        /* NEW ANALYSER */
        if (analyser.isVisible())
        {
          analyser.modifyNet();
        }


        if(i == 0) // first iteration of the net
        {
          for(int initialNetIterations = 0; 
              initialNetIterations < initialInterations;
              initialNetIterations++)
          {
            net.process();
            //System.out.println("cycle:" + i + " initialNetIterations: " +
            //    initialInterations);
          }
        }


        // process the net at least one time
        for(int j=0;j<Math.max(iterations,1)&&keepOnRunning;j++) // at least one iteration
        {
          net.process();
        }

        /** OLD ANALYSER STUFF
        // if brain surgeon is open
        if(analyser.isVisible())
        {
        analyser.updateNet();
        }
         */
        /* NEW ANALYSER */
        if (analyser.isVisible())
        {
          analyser.modifyNet();
        }

        // for every output-neruon <-> robot-input-device mapping:
        // hand the i-th output-neruon value to the assigned input-device
        for(int k=0;k<outputSize;k++)
        {
          robotStruct.setInputValue(
              robotStruct.getInputObject(k),
              new String(""+net.getOutputNeuronValue(k)));
        }

        if(i >= warmUpSteps)
        {
          // hand the newest data to the fitnessfunction class -> calculate
          // fitnessfunction
          ff.calculate(robotStruct);
          if(ff.bumped())
          {
            robotStatus = ROBOT_BUMPED;
            ff.resetBumped();
            break;
          }
        }

        // should be after the fitness function is called, so that the last data
        // reaches it ...
        // if robot has bumped -> stop this run, and fitness is 0
        // if robot has next try -> stop, and go on with next try
        if(ambassador.getRobotStatus() == RobotStatus.BUMPED)
        {
          //System.out.println("Robot has bumped");
          robotStatus = ROBOT_BUMPED;
          break;
        }

        if( ambassador.getRobotStatus() == RobotStatus.NEXT_TRY)
        {
          robotStatus = ROBOT_OK;
          //System.out.println("Robot has new try");
          break;
        }

        // hand the newest data to the analyser device
        if(analyser != null && analyser.isVisible())
        {
          /** OLD ANALYSER STUFF
            analyser.step(net);

            if(drawEverStep && analyser.isVisible())
            {
            analyser.draw();
            }
           */
          // WHAT IS THIS?!
          while (!analyser.step(net) && keepOnRunning)
          {
            try
            {
              Thread.sleep(8);
            }
            catch (Exception e)
            {
              // Nothing
            }
          }
        }
      } // end of cycles

      accumulatedFitness += ff.getFitnessValue();

      System.out.println("Fitness so far " + accumulatedFitness 
          + " ( + " + ff.getFitnessValue() + ")");
      System.out.println("Lived for " + cyclesSurvived + " cycles"); 

      ff.reset();

      // show data if robot is better than the last, or if no fitnessfunction is
      // used
      if (ff == null ||
          ff.getFitnessValue() > bestFitnessValue)
        //||
        //ff.mode() == ff.FITNESS_NO_FITNESS_FUNCTION)
      {
        bestFitnessValue = ff.getFitnessValue();
        /** OLD ANALYSER STUFF
          if(analyser != null && analyser.isVisible())
          {
          analyser.setFitnessValue(bestFitnessValue);
          analyser.draw();
          }*/
        numberOfLosers = 0;

      }
      else 
      {
        // too may losers -> show the next robot
        if ((++numberOfLosers > MAX_NUMBER_OF_LOSERS) || 
            ( analyser != null && analyser.isVisible() == false))
        {
          bestFitnessValue = -1;
        }
      }

      if(tries > 1 && 
        (tried < tries - 1))
      {
        ambassador.nextTry();
      }

    } // end of tries

    ff.setFitnessValue(accumulatedFitness);

    /** OLD ANALYSER STUFF */
    if(analyser != null && analyser.isVisible())
    {
      // reset the analyser, as the net-processing is over
      analyser.reset();
    }

  }

  public AmbassadorInterface getAmbassador()
  {
    return ambassador;
  }

  public void stop()
  {
    System.out.println("Processor: stopped");
    keepOnRunning = false;
  }

  public void setParameter(ProcessParameter processParameter)
  {
    this.processParameter = processParameter;
    if (this.analyser != null)
    {
      this.analyser.setProcessParameter(processParameter);
    }
  }

  public FitnessFunctionInterface getFitnessFunction()
  {
    return ff;
  }
}
