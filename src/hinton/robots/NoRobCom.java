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


package hinton.robots;

import hinton.ambassador.RobCom;
import hinton.ambassador.RobotStruct;


/**
* This class represents the empty communication class between Hinton and a real
* robot. You may use this as template for your own communication as well :-).
*
*/
public class NoRobCom extends RobCom
{
 
  /**
   * Return the name of the robot.
   */
  public String getRobotName()
  {
    return " ";
  }

  /**
   * Return the path an filename of the XML-file
   */
  public String getConfigFileName()
  {
    return "xml/robot/norobcom.xml";
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
   * Is called when the robot will be disconnected. 
   */
  public void closeConnection()
  {
  }

  /**
   * Is called when the robot will be stopped. 
   */
  public void stop()
  {
  }

  /**
   * Is called each timestep to update the robostruct and commit the data to
   * hinton. 
   */
  public void update(RobotStruct robotStruct)
  {
  }

}
