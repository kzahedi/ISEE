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


/**
 * Implements a Enumeration with EvoObject-elements. Etends Vector
 */

package Evolution;


import java.util.Vector;

import util.io.XMLHandler;

import cholsey.Net;
/**
 *  This class implements a wrapper to the vector class. <br>
 *  Example: <br>
 *  <code>
 Population p = new Population();

 EvoObjectList objects = p.offspring(); <br>
 for(objects.start(); objects.hasMore(); objects.next()) <br>
 {<br>
 &nbsp;&nbsp;&nbsp;EvoObject object = objects.object();<br>
 &nbsp;&nbsp;&nbsp;Net net = objects.network();<br>
 &nbsp;&nbsp;&nbsp;double performance = objects.performance();<br>
 &nbsp;&nbsp;&nbsp;...<br>
 }
 </code>
 */

public class EvoObjectList extends Vector
{

  int index = 0;
  /**
   * default constructor
   */
  public EvoObjectList()
  {
    super();
  }

  // **************************************************************************  
  // object functions
  // **************************************************************************  


  public void sortedAdd(EvoObject newObj){
    EvoObject obj;
    boolean   inserted = false;
    int i, vecSize;

    vecSize = size();
    for(i=0;i < vecSize; i++)
    {
      obj = (EvoObject)elementAt(i); 
      if(newObj.getPerformance() > obj.getPerformance())
      {
        insertElementAt(newObj,i);
        return;
      }
    }
    insertElementAt(newObj,vecSize);
    return;
  }

  /**
   * Returns the currently selected evo-object. In a for-loop <br>
   * for(nl.start();nl.hasMore();nl.next()) <br>
   * {<br>
   * &nbsp;&nbsp; EvoObject o = nl.object();<br>
   * }
   * @param    none
   * @return   the current selected evoobject
   */
  public EvoObject object() 
  {
    return (EvoObject)elementAt(index);
  }


  /**
   * Returns the current evoobject and increases the index.
   * @param    none
   * @return   the current object
   * @see #net()
   */
  public EvoObject next()
  {
    EvoObject returnObject;
    if(index < size())
    {
      returnObject = (EvoObject)elementAt(index);
      index++;
      return returnObject;
    }
    return null;
  }

  /**
   * Returns the i-th evo-object. This function is a wrapper to the
   * Vector.elementAt(i) function.
   * @param    index int index, the index of the wanted net
   * @return   EvoObject object, the object selected
   */
  public EvoObject object(int index)
  {
    return (EvoObject)elementAt(index);
  }
  // **************************************************************************
  // flow control functions
  // **************************************************************************
  /**
   * Returns true if the end of the list is not reached yet.
   * @param    none
   * @return   true, if there a still some neurons to follow, false otherwise
   * @see #net()
   */
  public boolean hasMore()
  {
    if (index < size())
    {
      return true;
    }
    return false;
  }

  /**
   * Sets the index to the first evo-object in the list.
   * @param    none
   * @return   none
   * @see #net()
   */
  public void start()
  {
    index = 0;
  }






  // **************************************************************************
  // output functions
  // **************************************************************************

  /**
   * Human readable representation of the list. For every net invoke toString
   * and return the stuff.
   * @param    none
   * @return   String - human readable representation
   */
  public String toString()
  {
    String s = new String();
    for(int i=0;i<size();i++)
    {
      EvoObject obj = object(i);
      s = s.concat(obj.toString() + "\n");
    }
    return s;
  }

  // **************************************************************************
  // self test
  // **************************************************************************

  /**
   * For Selfstest. Do <b> not call </b> as class method.
   * @param    none
   * @return   none
   */
  public static void main(String argv[])
  {
    NetList nl = new NetList();

    XMLHandler xml = new XMLHandler();

    Net n;

    for(int i=0;i<10;i++)
    {
      n = xml.readNetFromFile("xml/mrc.xml",0,0);
      nl.add(n);
    }

    int i = 0;
    for(nl.start();nl.hasMore();nl.next())
    {
      i++;
      n = nl.net();
      System.out.println(i + ".) \n" +  n.toString());
    }

  }

}




















