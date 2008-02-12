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
  * Implements a Enumeration with PopState-elements. Extends Vector
 */

package Evolution;

import java.util.Vector;
/**
 *  This class implements a wrapper to the vector class. <br>
 *  
 */

public class PopDyn extends Vector{
    
    int index = 0;
    /**
     * default constructor
     */
    public PopDyn(){
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
    public PopState currentState(){
  return (PopState)elementAt(index);
    }
    
    /**
     * Returns the current PopDyn and increases the index.
     * @param    none
     * @return   PopDyn current object
     * @see #net()
     */
    public PopState next(){
  PopState pd;
  if(index < size())
  {
      pd = (PopState)elementAt(index);
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
    public PopState state(int index){
  return (PopState)elementAt(index);
    }
    
    
    /**
     * Returns true if the end of the list is not reached yet.
     * @param    none
     * @return   true, if there a still some neurons to follow, false otherwise
     */
    public boolean hasMore(){
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
    public void start(){
  index = 0;
    }


    /* statistic method  */

    public double getHighestPopSize(){
  PopState state = null;
  double currentPopSize = java.lang.Double.NEGATIVE_INFINITY;
  double returnPopSize = java.lang.Double.NEGATIVE_INFINITY;

  if(size() == 0)
  {
      return (java.lang.Double.NaN);
  }
  
  for(int i = 0; i < size(); i++)
  {
      state = this.state(i);
      currentPopSize = state.getCurrentPopSize(); 
      if( currentPopSize > returnPopSize)
      {
    returnPopSize = currentPopSize; 
      }
  }
  return returnPopSize;
    }

    public double getLowestPopSize(){
  PopState state = null;
  double currentPopSize = java.lang.Double.POSITIVE_INFINITY;
  double returnPopSize = java.lang.Double.POSITIVE_INFINITY;

  if(size() == 0)
  {
      return (java.lang.Double.NaN);
  }
  
  for(int i = 0; i < size(); i++)
  {
      state = this.state(i);
      currentPopSize = state.getCurrentPopSize(); 
      if( currentPopSize < returnPopSize)
      {
    returnPopSize = currentPopSize; 
      }
  }
  return returnPopSize;
  
    }


    public double getHighestVarAge(){
  PopState state = null;
  double currentVarAge = java.lang.Double.NEGATIVE_INFINITY;
  double returnVarAge = java.lang.Double.NEGATIVE_INFINITY;

  if(size() == 0)
  {
      return (java.lang.Double.NaN);
  }
  
  for(int i = 0; i < size(); i++)
  {
      state = this.state(i);
      currentVarAge = state.getVarAge(); 
      if( currentVarAge > returnVarAge)
      {
    returnVarAge = currentVarAge; 
      }
  }
  return returnVarAge;
    }

    public double getLowestVarAge(){
  PopState state = null;
  double currentVarAge = java.lang.Double.POSITIVE_INFINITY;
  double returnVarAge = java.lang.Double.POSITIVE_INFINITY;

  if(size() == 0)
  {
      return (java.lang.Double.NaN);
  }
  
  for(int i = 0; i < size(); i++)
  {
      state = this.state(i);
      currentVarAge = state.getVarAge(); 
      if( currentVarAge < returnVarAge)
      {
    returnVarAge = currentVarAge; 
      }
  }
  return returnVarAge;
    }


    

    public double getHighestAvgAge(){
  PopState state = null;
  double currentAvgAge = java.lang.Double.NEGATIVE_INFINITY;
  double returnAvgAge = java.lang.Double.NEGATIVE_INFINITY;

  if(size() == 0)
  {
      return (java.lang.Double.NaN);
  }
  
  for(int i = 0; i < size(); i++)
  {
      state = this.state(i);
      currentAvgAge = state.getAvgAge(); 
      if( currentAvgAge > returnAvgAge)
      {
    returnAvgAge = currentAvgAge; 
      }
  }
  return returnAvgAge;
    }

    public double getLowestAvgAge(){
  PopState state = null;
  double currentAvgAge = java.lang.Double.POSITIVE_INFINITY;
  double returnAvgAge = java.lang.Double.POSITIVE_INFINITY;

  if(size() == 0)
  {
      return (java.lang.Double.NaN);
  }
  
  for(int i = 0; i < size(); i++)
  {
      state = this.state(i);
      currentAvgAge = state.getAvgAge(); 
      if( currentAvgAge < returnAvgAge)
      {
    returnAvgAge = currentAvgAge; 
      }
  }
  return returnAvgAge;
    }


    public double getHighestAgeBest(){
  PopState state = null;
  double currentAge = java.lang.Double.NEGATIVE_INFINITY;
  double returnAge = java.lang.Double.NEGATIVE_INFINITY;

  if(size() == 0)
  {
      return (java.lang.Double.NaN);
  }
  
  for(int i = 0; i < size(); i++)
  {
      state = this.state(i);
      currentAge = state.getAgeBest(); 
      if( currentAge > returnAge)
      {
    returnAge = currentAge; 
      }
  }
  return returnAge;
    }


    public double getLowestAgeBest(){
  PopState state = null;
  double currentAge = java.lang.Double.POSITIVE_INFINITY;
  double returnAge = java.lang.Double.POSITIVE_INFINITY;

  if(size() == 0)
  {
      return (java.lang.Double.NaN);
  }
  
  for(int i = 0; i < size(); i++)
  {
      state = this.state(i);
      currentAge = state.getAgeBest(); 
      if( currentAge < returnAge)
      {
    returnAge = currentAge; 
      }
  }
  return returnAge;
    }


    public double getHighestAgeOldest(){
  PopState state = null;
  double currentAge = java.lang.Double.NEGATIVE_INFINITY;
  double returnAge = java.lang.Double.NEGATIVE_INFINITY;

  if(size() == 0)
  {
      return (java.lang.Double.NaN);
  }
  
  for(int i = 0; i < size(); i++)
  {
      state = this.state(i);
      currentAge = state.getAgeOldest(); 
      if( currentAge > returnAge)
      {
    returnAge = currentAge; 
      }
  }
  return returnAge;
    }


    public double getLowestAgeOldest(){
  PopState state = null;
  double currentAge = java.lang.Double.POSITIVE_INFINITY;
  double returnAge  = java.lang.Double.POSITIVE_INFINITY;

  if(size() == 0)
  {
      return (java.lang.Double.NaN);
  }

  for(int i = 0; i < size(); i++)
  {
      state = this.state(i);
      currentAge = state.getAgeOldest(); 
      if( currentAge < returnAge)
      {
    returnAge = currentAge; 
      }
  }
  return returnAge;

    }


    public double getHighestMaxOutPerf(){
  PopState state = null;
  double currentVal = java.lang.Double.NEGATIVE_INFINITY;
  double returnVal = java.lang.Double.NEGATIVE_INFINITY;

  if(size() == 0)
  {
      return (java.lang.Double.NaN);
  }
  
  for(int i = 0; i < size(); i++)
  {
      state = this.state(i);
      currentVal = state.getMaxOutPerf(); 
      if( currentVal > returnVal)
      {
    returnVal = currentVal; 
      }
  }
  return returnVal;
    }

    public double getLowestMaxOutPerf(){
  PopState state = null;
  double currentVal = java.lang.Double.POSITIVE_INFINITY;
  double returnVal = java.lang.Double.POSITIVE_INFINITY;

  if(size() == 0)
  {
      return (java.lang.Double.NaN);
  }
  
  for(int i = 0; i < size(); i++)
  {
      state = this.state(i);
      currentVal = state.getMaxOutPerf(); 
      if( currentVal < returnVal)
      {
    returnVal = currentVal; 
      }
  }
  return returnVal;
    }

    public double getHighestMaxPerf(){
  PopState state = null;
  double currentVal = java.lang.Double.NEGATIVE_INFINITY;
  double returnVal = java.lang.Double.NEGATIVE_INFINITY;

  if(size() == 0)
  {
      return (java.lang.Double.NaN);
  }
  
  for(int i = 0; i < size(); i++)
  {
      state = this.state(i);
      currentVal = state.getMaxSysPerf(); 
      if( currentVal > returnVal)
      {
    returnVal = currentVal; 
      }
  }
  return returnVal;
    }

    public double getLowestMaxPerf(){
  PopState state = null;
  double currentVal = java.lang.Double.POSITIVE_INFINITY;
  double returnVal = java.lang.Double.POSITIVE_INFINITY;

  if(size() == 0)
  {
      return (java.lang.Double.NaN);
  }
  
  for(int i = 0; i < size(); i++)
  {
      state = this.state(i);
      currentVal = state.getMaxSysPerf(); 
      if( currentVal < returnVal)
      {
    returnVal = currentVal; 
      }
  }
  return returnVal;
    }


    public double getLowestAvgSysPerf(){
  PopState state = null;
  double currentVal = java.lang.Double.POSITIVE_INFINITY;
  double returnVal = java.lang.Double.POSITIVE_INFINITY;

  if(size() == 0)
  {
      return (java.lang.Double.NaN);
  }
  
  for(int i = 0; i < size(); i++)
  {
      state = this.state(i);
      currentVal = state.getAvgSysPerf(); 
      if( currentVal < returnVal)
      {
    returnVal = currentVal; 
      }
  }
  return returnVal;
    }

    public double getHighestAvgSysPerf(){
  PopState state = null;
  double currentVal = java.lang.Double.NEGATIVE_INFINITY;
  double returnVal = java.lang.Double.NEGATIVE_INFINITY;

  if(size() == 0)
  {
      return (java.lang.Double.NaN);
  }
  
  for(int i = 0; i < size(); i++)
  {
      state = this.state(i);
      currentVal = state.getAvgSysPerf(); 
      if( currentVal > returnVal)
      {
    returnVal = currentVal; 
      }
  }
  return returnVal;
    }

    public double getHighestVarSysPerf(){
  PopState state = null;
  double currentVal = java.lang.Double.NEGATIVE_INFINITY;
  double returnVal = java.lang.Double.NEGATIVE_INFINITY;

  if(size() == 0)
  {
      return (java.lang.Double.NaN);
  }
  
  for(int i = 0; i < size(); i++)
  {
      state = this.state(i);
      currentVal = state.getVarSysPerf(); 
      if( currentVal > returnVal)
      {
    returnVal = currentVal; 
      }
  }
  return returnVal;
    }

    public double getHighestNmbHiddenBest(){
  PopState state = null;
  double currentVal = java.lang.Double.NEGATIVE_INFINITY;
  double returnVal = java.lang.Double.NEGATIVE_INFINITY;

  if(size() == 0)
  {
      return (java.lang.Double.NaN);
  }
  
  for(int i = 0; i < size(); i++)
  {
      state = this.state(i);
      currentVal = state.getNmbHiddenBest(); 
      if( currentVal > returnVal)
      {
    returnVal = currentVal; 
      }
  }
  return returnVal;
    }

    public double getLowestNmbHiddenBest(){
  PopState state = null;
  double currentVal = java.lang.Double.POSITIVE_INFINITY;
  double returnVal = java.lang.Double.POSITIVE_INFINITY;

  if(size() == 0)
  {
      return (java.lang.Double.NaN);
  }
  
  for(int i = 0; i < size(); i++)
  {
      state = this.state(i);
      currentVal = state.getNmbHiddenBest(); 
      if( currentVal < returnVal)
      {
    returnVal = currentVal; 
      }
  }
  return returnVal;
    }

    public double getHighestAvgNmbHidden(){
  PopState state = null;
  double currentVal = java.lang.Double.NEGATIVE_INFINITY;
  double returnVal = java.lang.Double.NEGATIVE_INFINITY;

  if(size() == 0)
  {
      return (java.lang.Double.NaN);
  }
  
  for(int i = 0; i < size(); i++)
  {
      state = this.state(i);
      currentVal = state.getAvgNmbHidden(); 
      if( currentVal > returnVal)
      {
    returnVal = currentVal; 
      }
  }
  return returnVal;
    }

    public double getLowestAvgNmbHidden(){
  PopState state = null;
  double currentVal = java.lang.Double.POSITIVE_INFINITY;
  double returnVal = java.lang.Double.POSITIVE_INFINITY;

  if(size() == 0)
  {
      return (java.lang.Double.NaN);
  }
  
  for(int i = 0; i < size(); i++)
  {
      state = this.state(i);
      currentVal = state.getAvgNmbHidden(); 
      if( currentVal < returnVal)
      {
    returnVal = currentVal; 
      }
  }
  return returnVal;
    }

    public double getHighestVarNmbHidden(){
  PopState state = null;
  double currentVal = java.lang.Double.NEGATIVE_INFINITY;
  double returnVal = java.lang.Double.NEGATIVE_INFINITY;

  if(size() == 0)
  {
      return (java.lang.Double.NaN);
  }
  
  for(int i = 0; i < size(); i++)
  {
      state = this.state(i);
      currentVal = state.getVarNmbHidden(); 
      if( currentVal > returnVal)
      {
    returnVal = currentVal; 
      }
  }
  return returnVal;
    }


    public double getHighestNmbSynapsesBest(){
  PopState state = null;
  double currentVal = java.lang.Double.NEGATIVE_INFINITY;
  double returnVal = java.lang.Double.NEGATIVE_INFINITY;

  if(size() == 0)
  {
      return (java.lang.Double.NaN);
  }
  
  for(int i = 0; i < size(); i++)
  {
      state = this.state(i);
      currentVal = state.getNmbSynapsesBest(); 
      if( currentVal > returnVal)
      {
    returnVal = currentVal; 
      }
  }
  return returnVal;
    }

    public double getLowestNmbSynapsesBest(){
  PopState state = null;
  double currentVal = java.lang.Double.POSITIVE_INFINITY;
  double returnVal = java.lang.Double.POSITIVE_INFINITY;

  if(size() == 0)
  {
      return (java.lang.Double.NaN);
  }
  
  for(int i = 0; i < size(); i++)
  {
      state = this.state(i);
      currentVal = state.getNmbSynapsesBest(); 
      if( currentVal < returnVal)
      {
    returnVal = currentVal; 
      }
  }
  return returnVal;
    }


    public double getHighestVarNmbSynapses(){
  PopState state = null;
  double currentVal = java.lang.Double.NEGATIVE_INFINITY;
  double returnVal = java.lang.Double.NEGATIVE_INFINITY;

  if(size() == 0)
  {
      return (java.lang.Double.NaN);
  }
  
  for(int i = 0; i < size(); i++)
  {
      state = this.state(i);
      currentVal = state.getVarNmbSynapses(); 
      if( currentVal > returnVal)
      {
    returnVal = currentVal; 
      }
  }
  return returnVal;
    }

    public double getHighestAvgNmbSynapses(){
  PopState state = null;
  double currentVal = java.lang.Double.NEGATIVE_INFINITY;
  double returnVal = java.lang.Double.NEGATIVE_INFINITY;

  if(size() == 0)
  {
      return (java.lang.Double.NaN);
  }
  
  for(int i = 0; i < size(); i++)
  {
      state = this.state(i);
      currentVal = state.getAvgNmbSynapses(); 
      if( currentVal > returnVal)
      {
    returnVal = currentVal; 
      }
  }
  return returnVal;
    }

    public double getLowestAvgNmbSynapses(){
  PopState state = null;
  double currentVal = java.lang.Double.POSITIVE_INFINITY;
  double returnVal = java.lang.Double.POSITIVE_INFINITY;

  if(size() == 0)
  {
      return (java.lang.Double.NaN);
  }
  
  for(int i = 0; i < size(); i++)
  {
      state = this.state(i);
      currentVal = state.getAvgNmbSynapses(); 
      if( currentVal < returnVal)
      {
    returnVal = currentVal; 
      }
  }
  return returnVal;
    }


    
    /**
     * Human readable representation of the list. For every net invoke toString
     * and return the stuff.
     * @param    none
     * @return   String - human readable representation
     */
    public String toString(){
  String s = new String();
  
  s = "# " +
      "01:maxOutPerf\t" +
      "02:maxSysPerf\t" +
      "03:avgSysPerf\t" +
      "04:varSysPerf\t" +
      "05:avgPopSize\t" +
      "06:popSize\t" +
      "07:offsprings\t" +
      "08:ageBest\t" +
      "09:avgAge\t" +
      "10:varAge\t" +
      "11:ageOldest\t" +
      "12:nmbHiddenBest\t" +
      "13:nmbSynapsesBest\t" +
      "14:avgNmbHidden\t" +
      "15:varNmbHidden\t" +
      "16:avgNmbSynapses\t" +
      "17:varNmbSynapses\t" +
      "18:insNeuProb\t" +
      "19:delNeuProb\t" +
      "20:insSynProb\t" +
      "21:delSynProb\t" +
      "22:connect\t" +
      "23:costN\t" +
      "24:costS\t" +
      "25:birthGamma\t" +
      "26:const0\t" +
      "27:const1\t" +
      "28:const2\t" +
      "29:const3\n";    
  
  for(int i=0;i<size();i++)
  {
      PopState state = this.state(i);
      s = s.concat(state.toString() );
  }
  return s;
  
  
    }
    
    /**
     * For Selfstest. Do <b> not call </b> as class method.
     * @param    none
     * @return   none
     */
    public static void main(String argv[]){
  PopDyn pd   = new PopDyn();
  PopState   state = new PopState();
  pd.add(state);
  System.out.println(pd.toString());
    }
    

}








