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
  * Implements a Enumeration with PopDyn-elements. Extends Vector
 */
package Evolution;

import java.util.Vector;
/**
 *  This class implements a wrapper to the vector class. <br>
 */

public class PopDynList extends Vector
{

  int index = 0;
  /**
   * default constructor
   */
  public PopDynList()
  {
    super();
  }


  /**
   * Returns the currently selected PopDyn-object. In a for-loop <br>
   * for(nl.start();nl.hasMore();nl.next()) <br>
   * {<br>
   * &nbsp;&nbsp; PopDyn pd = nl.currentState();<br>
   * }
   * @param    none
   * @return   the current selected evoobject
   */
  public PopDyn currentDyn() 
  {
    return (PopDyn)elementAt(index);
  }

  /**
   * Returns the current PopDyn and increases the index.
   * @param    none
   * @return   PopDyn current object
   * @see #net()
   */
  public PopDyn next()
  {
    PopDyn pd;
    if(index < size())
    {
      pd = (PopDyn)elementAt(index);
      index++;
      return pd;
    }
    return null;
  }



  /**
   * Returns the i-th PopDyn-object. This function is a wrapper to the
   * Vector.elementAt(i) function.
   * @param    index int index, the index of the wanted net
   * @return   PopDyn object, the object selected
   */
  public PopDyn popDyn(int index)
  {
    return (PopDyn)elementAt(index);
  }


  /**
   * Returns true if the end of the list is not reached yet.
   * @param    none
   * @return   true, if there a still some neurons to follow, false otherwise
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
   */
  public void start()
  {
    index = 0;
  }


  /**
   * Human readable representation of the list. For every net invoke toString
   * and return the stuff.
   * @param    none
   * @return   String - human readable representation
   */
  public String toString()
  {
    String s = new String();

    /*
    s = "maxSysPerf\t" +
  "avgSysPerf\t" +
  "varSysPerf\t" +
  "nmbHiddenBest\t" +
  "nmbSynapsesBest\t" +
  "avgNmbHidden\t" +
  "varNmbHidden\t" +
  "avgNmbSynapses\t" +
  "varNmbSynapses\t" +
  "insNeuProb\t" +
  "delNeuProb\t" +
  "insSynProb\t" +
  "delSynProb\t" +
  "connect\t" +
  "costN\t" +
  "costS\t" +
  "birthGamma\n";    
    */

    for(int i=0;i<size();i++)
    {
      PopDyn pd = this.popDyn(i);
      s = s.concat(pd.toString() + "\n");
    }
    return s;
  }

  /**
   * For Selfstest. Do <b> not call </b> as class method.
   * @param    none
   * @return   none
   */
  public static void main(String argv[])
  {
      PopDynList l = new PopDynList();
      PopDyn     pd = new PopDyn();
      l.add(pd);
      System.out.println(l.toString());
  }
}



