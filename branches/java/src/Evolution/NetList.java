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
  * Implements a Enumeration with net-elements. Etends Vector
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

  p.addNet(new Net);

  NetList networks = p.networks(); <br>
  for(networks.start(); networks.hasMore(); networks.next()) <br>
  {<br>
   &nbsp;&nbsp;&nbsp;Net net = networks.network();<br>
   &nbsp;&nbsp;&nbsp;...<br>
  }
  </code>
 *  
 */

public class NetList extends Vector
{

  int index = 0;
  /**
   * default constructor
   */
  public NetList()
  {
    super();
  }

  // **************************************************************************  
  // neuron functions
  // **************************************************************************  
  /**
   * Returns the currently selected neuron. In a for-loop <br>
   * for(nl.start();nl.hasMore();nl.next()) <br>
   * {<br>
   * &nbsp;&nbsp; Net n = nl.network();<br>
   * }
   * @param    none
   * @return   the current selected network
   */
  public Net net() 
  {
    return (Net)elementAt(index);
  }
  /**
   * Returns the current net and increases the index.
   * @param    none
   * @return   the current net
   * @see #net()
   */
  public Net next()
  {
    Net returnNet;
    if(index < size())
    {
      returnNet = (Net)elementAt(index);
      index++;
      return returnNet;
    }
    return null;
  }

  /**
   * Returns the i-th network. This function is a wrapper to the
   * Vector.elementAt(i) function.
   * @param    index int index, the index of the wanted net
   * @return   Net net, the net selected
   */
  public Net net(int index)
  {
    return (Net)elementAt(index);
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
   * Sets the index to the first net in the list.
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
      Net n = net(i);
      s = s.concat(n.toString() + "\n");
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




















