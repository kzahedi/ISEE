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


package hinton.simulators;

import hinton.ambassador.RobotStruct;
import hinton.ambassador.SimCom;


/**
* This class represents the empty communication class between Hinton and a 
* simulator. You may use this as template for your own communication as well :-).
*
*/
public class NoSimCom extends SimCom 
{
  
  /**
   * Return the name of the simulator.
   */
  public String getSimName()
  {
    return " ";
  }

  /**
   * Return the path an filename of the XML-file
   */
  public String getConfigFileName()
  {
    return "xml/simulator/nosimcom.xml";
  }

  /**
   * Is called when the simulation will be stopped. 
   */
  public void stop()
  {
  }

  /**
   * Is called for the next try. 
   */
  public void nextTry()
  {
  }

  /**
   * Init Robot.
   */
  public void setNewStartPosition()
  {
  }

  /**
   * Is called when the robot will be connected. 
   */
  public void connect()
  {
  }

  public void disconnect()
  {

  }

  /**
   * Init Robot.
   */
  public void reset()
  {
  }

  /**
   * Here you should update all simulation data (see example),
   * before the send()-method will send all communication data.
   */
  public void send(RobotStruct robotStruct)
  {
  }

  /**
   * Here you should send all communication data (see examples);
   * this is called after the update()-method.
   */
  public void update(RobotStruct robotStruct)
  {
  }
}
