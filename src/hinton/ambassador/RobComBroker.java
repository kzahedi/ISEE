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


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import util.misc.GenericClassLoader;

/**
 * This class represents a broker for all real robot communication classes. 
 *
 * It searches communication class-files an binds them dynamically to hinton.
 *
 */
public final class RobComBroker
{
  /** subfolder where all Robotulation-com-classes can be found */
  public static final String ROBOT_FOLDER = "robots";
  
  /** maximum length of a robcom name */
  public static final int MAX_NAME_LENGTH = 20;

  /** name of the hinton jar-file */
  public static final String JAR_FILE = "hinton.jar";

  /** list of all loaded  robcoms */
  private Vector robotComList;
  
  /**
   * Constructor creates and initalises a new broker.
   */
  public RobComBroker()
  {
    robotComList = new Vector();
    reloadRobotComList();    
  }

  /**
   * Forces the broker to rescan and reload all communication classes from folder
   * ROBOT_FOLDER and binds them dynamically for immidiate use.
   */
  public void reloadRobotComList()
  {
    robotComList = new Vector();
    GenericClassLoader gcl = new GenericClassLoader(
        "hinton" + File.separator + "robots",
        "hinton" + File.separator + "robots");

    ArrayList objects = gcl.getObjects();

    System.out.println("number of loaded communication classes: " + objects.size());
    try
    {
      for(int i=0; i < objects.size(); i++)
      {
        Class c = (Class)(objects.get(objects.size()-i-1));
        RobCom n = (RobCom)c.newInstance();
        robotComList.add(n);
      }

    Collections.sort(robotComList, 
        new Comparator()
        {
        public int compare(Object o1, Object o2)
        {
        return
        ((RobCom)o1).getName().compareTo(((RobCom)o2).getName());
        }
        });

    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Returns a sortet list of all loaded and aviable  robcom names.
   *
   * @return the sortest list of alle loaded  robcom names
   * @see #containsRobCom(String  robComName)
   * @see #getRobCom(String  robComName)
   */
  public String[] getRobotComNames()
  {
    int i;
    String[] s;

    s = new String[robotComList.size()];
    for (i=0; i<robotComList.size(); i++)
    {
      s[i] = ((AmbassadorInterface) robotComList.elementAt(i)).getName(); 
      if (s[i].length() > MAX_NAME_LENGTH)
      {
        s[i] = s[i].substring(0, MAX_NAME_LENGTH-1) + "#";
      }
    }
    return s;
  }

  /**
   * Checks wheather the given name is linked to an aviable (loaded) fitness
   *  robcom.
   * @return true if name is ok
   * @see #getRobComNames()
   * @see #getRobCom(String  robComName)
   */
  public boolean containsRobotCom(String robotComName)
  {
    int i;
    
    for (i=0; i<robotComList.size(); i++)
    {
      if (robotComName.equals(
          ((AmbassadorInterface) robotComList.elementAt(i)).getName()))
      {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Return the  robcom which is linked to the given name.
   * 
   * @return the  robcom or NULL if name is not linked to a
   *  robcom
   * @see #getRobComNames()
   * @see #containsRobCom(String robComName)
   */
  public AmbassadorInterface getRobotCom(String robotComName)
  {
    int i;
    
    for (i=0; i<robotComList.size(); i++)
    {
      if (robotComName.equals(
          ((AmbassadorInterface) robotComList.elementAt(i)).getName()))
      {
        return (AmbassadorInterface) robotComList.elementAt(i);
      }
    }
    return null;
  }


// -------------- TESTENVIRONMENT ------------------
  
  public static void main(String args[])
  {
    int i;
    String[] s;
    RobComBroker scb;
    
    scb = new RobComBroker();
    
    while (true)
    {


      s = scb.getRobotComNames();
      for(i=0; i<s.length; i++)
      {
        System.out.println(i+". found: "+s[i]);
      }

      try
      {
        System.out.println("8 sec to change  robcomfiles and make");
        Thread.sleep(8000);
      } catch (Exception ex)
      {
        //...
      }

      scb.reloadRobotComList();
    }  
  }
}  
