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


package Evolution;


/**
 *  Implements enumeration of evaluation-types.
 *  There is no public constructor  to this class. 
 *  EvaluationTypes can only be used in one of the following cases:
 *  <ul>
 *  <li> EvaluationType neuronType = EvaluationTypes.POLE </li>
 *  <li> EvaluationType neuronType = EvaluationTypes.HINTEN </li>
 *  <li> EvaluationType neuronType = EvaluationTypes.ONE2MANY </li>
 *  </ul>
 *
 */

public final class EvaluationType
{

    public final static int EVALUATION_TYPE_ONE2ONE     = 0;
    public final static int EVALUATION_TYPE_ONE2MANY    = 1;
    public final static int EVALUATION_TYPE_MIN_SYNC    = 2;
    public final static int EVALUATION_TYPE_MAX_SYNC    = 3;


    private int mode;
    private String name;
    private EvaluationType(String nm, int m) {name=nm; mode=m;};


    /**
     * Returns a human readable representation of the EvaluationType.
     * @param    none
     * @return   String string, human readable representation
     */
    public String toString() 
  {
      return name;
  }

    /**
     * Returns a XML-valid readable representation of the EvaluationType.
     * @param    none
     * @return   String xml, XML-valid representation
     */
    public String toXML()
  {
      switch(mode)
      {
    case EVALUATION_TYPE_ONE2ONE:
        return "1:1";
    case EVALUATION_TYPE_ONE2MANY:
        return "1:N";
    case EVALUATION_TYPE_MIN_SYNC:
        return "MIN_SYN";
    case EVALUATION_TYPE_MAX_SYNC:
        return "MAX_SYN";
      }
      return null;
  }
    

    /**
     * Returns the type of this neuron. Return value is one of the above
     * constants.
     * @param    none
     * @return   int mode
     * @see #EVALUATION_TYPE_ONE2ONE
     * @see #EVALUATION_TYPE_ONE2MANY
     * @see #EVALUATION_TYPE_MIN_SYNC
     * @see #EVALUATION_TYPE_MAX_SYNC
     */
    public int type() 
  {
      return mode;
  }
    
    /* Impements the evaluation type  for the connection to hinton (1D-pole balancer) for each population */
    public final static EvaluationType ONE2ONE = new EvaluationType("1:1",EVALUATION_TYPE_ONE2ONE);

    /* Impements the evaluation type  for parallel evaluation of individuals for only one population */
    public final static EvaluationType ONE2MANY    = new EvaluationType("1:N",EVALUATION_TYPE_ONE2MANY);
    
    /* Impements the simplest evaluation type of  synchron evaluation for more than one population  */
    /*  normalized to the minimum of the populations according to avg. and current population size  */
    public final static EvaluationType MIN_SYNC    = new EvaluationType("MIN_SYN",EVALUATION_TYPE_MIN_SYNC);

    /* Impements the almost simplest evaluation type of  synchron evaluation for more than one population  */
    /*  normalized to the maximum of the populations according to avg. and current population size  */
    public final static EvaluationType MAX_SYNC    = new EvaluationType("MAX_SYN",EVALUATION_TYPE_MAX_SYNC);

    
    /** list of all possible neuron types */
  public final static EvaluationType[] LIST = 
  {
      ONE2ONE,
      ONE2MANY,
      MIN_SYNC,
      MAX_SYNC
  };


}








