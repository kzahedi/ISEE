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
 * This class represents a broker for all simulation communication classes. 
 *
 * It searches communication class-files an binds them dynamically to hinton.
 *
 */
public final class SimComBroker
{
  /** subfolder where all Simulation-com-classes can be found */
  public static final String SIM_FOLDER = "simulators";
  
  /** maximum length of a robcom name */
  public static final int MAX_NAME_LENGTH = 20;

  /** name of the hinton jar-file */
  public static final String JAR_FILE = "hinton.jar";

  /** list of all loaded simcoms */
  private Vector simComList;

  /**
   * Constructor creates and initalises a new broker.
   */
  public SimComBroker()
  {
    simComList = new Vector();
    reloadSimComList();    
  }

  /**
   * Forces the broker to rescan and reload all communication classes from folder
   * SIM_FOLDER and binds them dynamically for immidiate use.
   */
//  public void reloadSimComList()
//  {
//    File f;     
//    JarFile jf;
//    Class c;
//    String[] s;
//    String  fs;
//    
//    int i, j, k;
//    boolean found, isJar;
//               
//    fs = "hinton" + File.separator + SIM_FOLDER;
//    
//    f = new File(fs);
//    if (!f.exists() || !f.isDirectory())
//    {
//      System.out.print("There is no directory named "
//          + fs + ". Now looking for " 
//          + JAR_FILE + "...");
//      f = new File(JAR_FILE);
//      if (!f.exists())
//      {
//        System.out.println("...not found: We do not have any"
//            + " simulation communications now!");
//        return;
//      }
//      System.out.println("...found!");
//      //list content of jar
//      simComList.removeAllElements();
//      try
//      {
//        jf = new JarFile(JAR_FILE);
//
//      } catch (Exception ex)
//      {
//        return;
//      }
//      Enumeration e = jf.entries();
//      Vector      v = new Vector();
//      while (e.hasMoreElements())
//      {
//        ZipEntry ze = (ZipEntry) e.nextElement();
//        if (ze.getName().startsWith(fs))
//        {
//          v.addElement(ze.getName().substring(ze.getName().lastIndexOf(File.separator)));
//        }        
//      }
//      s = new String[v.size()];      
//      for (i=0; i<s.length; i++)
//      {
//        s[i] = (String) v.elementAt(i);
//      }
//      f = new File(fs);
//      isJar = true;
//    } else
//    {
//      //list content of folder
//      simComList.removeAllElements();
//      s     = f.list();    
//      isJar = false;
//    }
//    
//    for(i=0; i<s.length; i++)
//    {
//      if (!s[i].endsWith(".class"))
//      {
//        //System.out.println("Skipped non-class-file: " + s[i]);
//        continue;
//      }
//      try
//      {
//        s[i] = s[i].substring(0, s[i].indexOf(".class"));
//
//        c = (new DynamicClassLoader(f.getPath(), JAR_FILE, isJar)).
//            loadClass(s[i]);
//        
//        if (c.getSuperclass().equals(SimCom.class))
//        {
//          Object o;
//          
//          System.out.print("Added communication (simulation): "); 
//          o = c.newInstance();
//          found = false;
//          for (k=0; k<simComList.size(); k++)
//          {
//            if (
//                ((AmbassadorInterface)o).getName().compareTo(
//                   ((AmbassadorInterface)simComList.elementAt(k)).getName())
//                 < 0)
//            {
//              found = true;
//              simComList.insertElementAt(o, k);
//              break;
//            }
//          }
//          if (!found)
//          {
//            simComList.addElement(o);  
//          }
//        }
//      } catch (Exception ex)
//      {
//        // error!
//        ex.printStackTrace();
//      }
//    }
//  }

  /**
   * Returns a sortet list of all loaded and aviable simcom names.
   *
   * @return the sortest list of alle loaded simcom names
   * @see #containsSimCom(String simComName)
   * @see #getSimCom(String simComName)
   */
  public String[] getSimComNames()
  {
    int i;
    String[] s;

    s = new String[simComList.size()];
    for (i=0; i<simComList.size(); i++)
    {
      s[i] = ((AmbassadorInterface) simComList.elementAt(i)).getName(); 
      if (s[i].length() > MAX_NAME_LENGTH)
      {
        s[i] = s[i].substring(0, MAX_NAME_LENGTH-1) + "#";
      }
    }
    return s;
  }

  /**
   * Checks wheather the given name is linked to an aviable (loaded) simcom   
   * @return true if name is ok
   * @see #getSimComNames()
   * @see #getSimCom(String simComName)
   */
  public boolean containsSimCom(String simComName)
  {
    int i;
    
    for (i=0; i<simComList.size(); i++)
    {
      if (simComName.equals(
          ((AmbassadorInterface) simComList.elementAt(i)).getName()))
      {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Return the simcom which is linked to the given name.
   * 
   * @return the simcom or NULL if name is not linked to a simcom
   * @see #getSimComNames()
   * @see #containsSimCom(String simComName)
   */
  public AmbassadorInterface getSimCom(String simComName)
  {
    int i;
   
    for (i=0; i<simComList.size(); i++)
    {
      if (simComName.equals(
          ((AmbassadorInterface) simComList.elementAt(i)).getName()))
      {
        return (AmbassadorInterface) simComList.elementAt(i);
      }
    }
    return null;
  }


// -------------- TESTENVIRONMENT ------------------
  
  public static void main(String args[])
  {
    int i;
    String[] s;
    SimComBroker scb;
    
    scb = new SimComBroker();
    
    while (true)
    {


      s = scb.getSimComNames();
      for(i=0; i<s.length; i++)
      {
        System.out.println(i+". found: "+s[i]);
      }

      try
      {
        System.out.println("8 sec to change simcomfiles and make");
        Thread.sleep(8000);
      } catch (Exception ex)
      {
        //...
      }

      scb.reloadSimComList();
    }  
  }


  public void reloadSimComList()
  {
    simComList = new Vector();
    GenericClassLoader gcl = new GenericClassLoader(
        "hinton" + File.separator + "simulators",
        "hinton" + File.separator + "simulators");

    ArrayList objects = gcl.getObjects();

    System.out.println("number of loaded communication classes: " + objects.size());
    try
    {
      for(int i=0; i < objects.size(); i++)
      {
        Class c = (Class)(objects.get(i));
        SimCom n = (SimCom)c.newInstance();
        if(n.getName().equals(""))
        {
          simComList.insertElementAt(n,0);
        }
        else
        {
          simComList.add(n);
        }
      }

    Collections.sort(simComList, 
        new Comparator()
        {
        public int compare(Object o1, Object o2)
        {
        return
        ((SimCom)o1).getName().compareTo(((SimCom)o2).getName());
        }
        });
 
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

}  
