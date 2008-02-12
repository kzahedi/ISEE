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


package Evolution.Pole;

import java.util.Vector;
/**
 *  This class implements a wrapper to the vector class. <br>
 *  
 */

public class PoleLogList extends Vector{

    int index = 0;
    /**
     * default constructor
     */
    public PoleLogList(){
  super();
    }

  public PoleState state() 
  {
    return (PoleState)elementAt(index);
  }


  /**
   * Returns the current pole state and increases the index.
   * @param    none
   * @return   the current object
   */
  public PoleState next()
  {
    PoleState returnObject;
    if(index < size())
    {
      returnObject = (PoleState)elementAt(index);
      index++;
      return returnObject;
    }
    return null;
  }


  /**
   * Returns the i-th pole state -object. This function is a wrapper to the
   * Vector.elementAt(i) function.
   * @param    index int index, the index of the wanted net
   * @return   PoleState object, the object selected
   */
  public PoleState state(int index)
  {
    return (PoleState)elementAt(index);
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
      PoleState obj = state(i);
      s = s.concat("#force\tlocCar\tangPole\tvelCar\tvelAng\taclCar\taclAng\n");
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
      PoleLogList l = new PoleLogList();

    for(int i=0;i<10;i++)
    {
  PoleState o = new PoleState();
  l.add(o);
    }

    System.out.println(l.toString());

  }


}






