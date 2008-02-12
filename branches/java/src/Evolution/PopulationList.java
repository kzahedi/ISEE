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
  * Implements a Enumeration with Population-elements. Etends Vector
 */


package Evolution;

import java.util.Random;
import java.util.Vector;

import Evolution.Random.Gauss;
import Evolution.Random.Poisson;

/**
 *  This class implements a wrapper to the vector class. <br>
 *  Example: <br>
 *  <code>
  Evolution EvoTask = new Evolution();

  PopulationList pops = EvoTask.Populations(); <br>
  for(pops.start(); pops.hasMore(); pops.next()) <br>
  {<br>
   &nbsp;&nbsp;&nbsp;Population pop              = pops.object();<br>
   &nbsp;&nbsp;&nbsp;double     max_performance  =  pop.max_performance();<br>
   &nbsp;&nbsp;&nbsp;double     mean_performance =  pop.mean_performance();<br>
   &nbsp;&nbsp;&nbsp;...<br>
  }
  </code>
 *  
 */


public class PopulationList extends Vector
{

  int index = 0;
  /**
   * default constructor
   */
  public PopulationList()
  {
    super();
  }

  // **************************************************************************  
  // object functions
  // **************************************************************************  
  /**
   * Returns the currently selected Population-object. In a for-loop <br>
   * for(pops.start();pops.hasMore();pops.next()) <br>
   * {<br>
   * &nbsp;&nbsp; Population pop = pops.object();<br>
   * }
   * @param    none
   * @return   the current selected population
   */
  public Population currentPop() 
  {
    return (Population)elementAt(index);
  }
  /**
   * Returns the current population and increases the index.
   * @param    none
   * @return   the current population
   */
  public Population next()
  {
    Population returnPopulation;

    if(index < size())
    {
      returnPopulation = (Population)elementAt(index);
      index++;
      return returnPopulation;
    }
    return null;
  }

  /**
   * Returns the i-th population in list. This function is a wrapper to the
   * Vector.elementAt(i) function.
   * @param    index int index, the index of the wanted net
   * @return   Population object, the object selected
   */
  public Population getPop(int index)
  {
    return (Population)elementAt(index);
  }
  // **************************************************************************
  // flow control functions
  // **************************************************************************
  /**
   * Returns true if the end of the list is not reached yet.
   * @param    none
   * @return   true, if there a still some populations to follow, false otherwise
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
    s = s.concat("poplations are: \n");
    for(int i=0;i<size();i++)
    {
      Population pop = this.getPop(i);
      s = s.concat("name: " + pop.getName() + " with size " + pop.getPopSize() + "\n");
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
      Evolution e = new Evolution();
      
      Random uniRand = new Random();
      Gauss gauss = new Gauss(0.0,1.0);
      Poisson poisson = new Poisson(uniRand);

      
      
      PopulationList pops = new PopulationList();
      Population pop;
      
      for(int i=0;i<10;i++)
      {
    pop = new Population(e,"population " + i, 35, uniRand, gauss, poisson );
    pops.add(pop);
      }
     
      int i = 0;
      for(pops.start();pops.hasMore();pops.next())
      {
    i++;
    pop = pops.currentPop();
    System.out.print(i + ".) \n");
      }

      System.out.println(pops.toString());
  }
    

}


