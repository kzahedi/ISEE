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

public final class RobotStatus
{
  public final static int ROBOT_OK = 0;
  public final static int ROBOT_BUMPED = 1;
  public final static int ROBOT_NEXT_TRY = 2;
  private String name = null;
  private int status = 0;
  private RobotStatus(String nm, int m) {name=nm; status=m;};

  public String toString() {return name;};
  /**
   * Returns a XML-valid readable representation of the RobotStatus.
   * @param    none
   * @return   String xml, XML-valid representation
   */
  public String toXML()
  {
    return name;
  }

  public int status() {return status;};
  public final static RobotStatus OK = 
    new RobotStatus("Robot OK",ROBOT_OK);
  public final static RobotStatus BUMPED = 
    new RobotStatus("Robot BUMPED",ROBOT_BUMPED);
  public final static RobotStatus NEXT_TRY = 
    new RobotStatus("Robot NEXT TRY",ROBOT_NEXT_TRY);

  public final static RobotStatus[] LIST =
  {
    OK,
    BUMPED,
    NEXT_TRY
  };
}
