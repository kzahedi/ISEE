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


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import util.misc.GenericClassLoader;

/**
 * This class represents a broker for the dynamic binding of fitness functions. 
 * @author Bjoern Mahn
 */
public class FitnessFunctionBroker
{
  /** subfolder where all fitness-classes can be found */
  public static final String FUNCTION_FOLDER = "fitnessfunctions";
  
  /** maximum length of a fitness name */
  public static final int MAX_NAME_LENGTH = 20;

  /** name of the hinton jar-file */
  public static final String JAR_FILE = "hinton.jar";

  /** list of all loaded fitness functions */
  private Vector functionList;
  
  /**
   * Constructor creates and initalises a new broker.
   */
  public FitnessFunctionBroker()
  {
    functionList = new Vector();
    reloadFitnessFunctionsList();    
  }

  /**
   * Forces the broker to rescan and reload all fitness-functions from folder
   * FUNCTION_FOLDER and binding them dynamically for immidiate use.
   */
  public void reloadFitnessFunctionsList()
  {

    functionList = new Vector();
    GenericClassLoader gcl = new GenericClassLoader(
        "hinton" + File.separator + "fitnessfunctions",
        "hinton" + File.separator + "fitnessfunctions");

    ArrayList objects = gcl.getObjects();

    System.out.println("number of loaded fitnessfunctions: " + objects.size());
    try
    {
      for(int i=0; i < objects.size(); i++)
      {
        Class c = (Class)(objects.get(i));
        FitnessFunction n = (FitnessFunction)c.newInstance();
        System.out.println("added: " + n.getName());
        functionList.add(n);
      }

    Collections.sort(functionList, 
        new Comparator()
        {
        public int compare(Object o1, Object o2)
        {
        return
        ((FitnessFunction)o1).getName().compareTo(((FitnessFunction)o2).getName());
        }
        });

    }
    catch(Exception e)
    {
      e.printStackTrace();
    }


  }

  /**
   * Returns a sortet list of all loaded and aviable fitness function names.
   *
   * @return the sortest list of alle loaded fitness function names
   * @see #containsFitnessFunction(String fitnessFunctionName)
   * @see #getFitnessFunction(String fitnessFunctionName)
   */
  public String[] getFitnessFunctionNames()
  {
    int i;
    String[] s;

    s = new String[functionList.size()];
    for (i=0; i<functionList.size(); i++)
    {
      s[i] = ((FitnessFunctionInterface) functionList.elementAt(i)).getName();
      if (s[i].length() > MAX_NAME_LENGTH)
      {
        s[i] = s[i].substring(0, MAX_NAME_LENGTH-1) + "#";
      }
    }
    return s;
  }

  /**
   * Checks wheather the given name is linked to an aviable (loaded) fitness
   * fitnessfunction.
   * @return true if name is ok
   * @see #getFitnessFunctionNames()
   * @see #getFitnessFunction(String fitnessFunctionName)
   */
  public boolean containsFitnessFuntion(String fitnessFunctionName)
  {
    int i;
    
    for (i=0; i<functionList.size(); i++)
    {
      if (fitnessFunctionName.equals(
          ((FitnessFunctionInterface) functionList.elementAt(i)).getName()))
      {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Return the fitness function which is linked to the given name.
   * 
   * @return the fitness function or NULL if name is not linked to a
   * fitness function
   * @see #getFitnessFunctionNames()
   * @see #containsFitnessFunction(String fitnessFunctionName)
   */
  public FitnessFunctionInterface getFitnessFunction(String fitnessFunctionName)
  {
    int i;
    
    for (i=0; i<functionList.size(); i++)
    {
      if (fitnessFunctionName.equals(
          ((FitnessFunctionInterface) functionList.elementAt(i)).getName()))
      {
        return (FitnessFunctionInterface) functionList.elementAt(i);
      }
    }
    return null;
  }


// -------------- TESTENVIRONMENT ------------------
  
  public static void main(String args[])
  {
    int i;
    String[] s;
    FitnessFunctionBroker ffb;
    
    ffb = new FitnessFunctionBroker();
    
    while (true)
    {


      s = ffb.getFitnessFunctionNames();
      for(i=0; i<s.length; i++)
      {
        System.out.println(i+". found: "+s[i]);
      }

      try
      {
        System.out.println("8 sec to change fitnessfunctionfiles and make");
        Thread.sleep(8000);
      } catch (Exception ex)
      {
        //...
      }

      ffb.reloadFitnessFunctionsList();
    }  
  }
}
