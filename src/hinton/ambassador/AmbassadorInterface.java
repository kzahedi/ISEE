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


package hinton.ambassador;


import hinton.executive.ProcessParameter;
/**
 * Interface for Simulator and Robot Communication. The functions introduced by
 * this inteface are needed by the Processor and ProcessorThread, so that the
 * proccessing of the net can be done, without knowing who is on the other end
 * of the line. In Detail: The Processor and the ProcessorThread need the output
 * of the robot (motor, sensors, ...) and feed this to the net, then return the
 * output values of the net to the robot. A robot can be a simulated or real
 * robot. This interface provides the functions that are neccessary for this
 * communication. This interface is used by SimCom and RobCom, which are
 * interfaces to the simulators, and (real) robots.
 * @see hinton.executive.Processor
 * @see hinton.executive.ProcessorThread
 * @see hinton.ambassador.SimCom
 * @see hinton.ambassador.RobCom
 * 
 */
public interface AmbassadorInterface 
{
  /** Return the Name of the Simulator or Robot */
  public String getName();
  /** Send back the output values of the net -&gt; input of the robot */
  public void update();
  /** Return the robotstruct
   * @see hinton.ambassador.RobotStruct
   */
  public RobotStruct robotStruct();
  /** Stop the robot and the communication */
  public void stop();
  /** start the connection */
  public void connect();
  /** shutdown the connection */
  public void disconnect();
  /** reset the robot position */
  public void reset();
  /** set new start position */
  public void setNewStartPosition();
  /** close the connection */
  public void close();
  /** preferred port */
  public int getPreferredPort();
  /** return robot status */
  public RobotStatus getRobotStatus();
  /** set max speed */
  public void setSpeedFactor(double speedFactor);
  /** next try */
  public void nextTry();
  /** set process parameter */
  public void setProcessParameter(ProcessParameter processParameter);
  /** set communication stuff */
  public void setPortIP(int port, String ip);
}
