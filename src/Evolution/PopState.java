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
  * Implements a data structure wich contain the statistical parameter 
  * over performance, structure and evolution parameter to one generation.
 */

package Evolution;

import java.text.NumberFormat;
import java.util.Locale;

import cholsey.Net;

public class PopState{
  /* ouput format */
  private NumberFormat numberFormat = NumberFormat.getInstance(Locale.UK);
  
  /* performance statistics */
  private double maxOutPerf;
  private double maxSysPerf;
  private double avgSysPerf;
  private double varSysPerf;
  
  /* structure statistics */
  private double avgPopSize;
  private double currentPopSize;
  private double currentOffsprings;
  private double nmbHiddenBest;
  private double nmbSynapsesBest;
  private double ageBest;
  private double ageOldest;
  private double avgNmbHidden;
  private double varNmbHidden;
  private double avgNmbSynapses;
  private double varNmbSynapses;
  private double avgAge;
  private double varAge;
  
  
  /* control parameter */
  private double insNeuProb;
  private double delNeuProb;
  private double insSynProb;
  private double delSynProb;
  private double connect;
  private double costN;
  private double costS;
  private double birthGamma;
  
  /* fitness */
  private double const0;
  private double const1;
  private double const2;
  private double const3;



    
   
    public PopState(){
  
  maxOutPerf = java.lang.Double.NaN;
  maxSysPerf = java.lang.Double.NaN;
  avgSysPerf = java.lang.Double.NaN;
  varSysPerf = java.lang.Double.NaN;

  avgPopSize = java.lang.Double.NaN;
  currentPopSize = java.lang.Double.NaN;
  nmbHiddenBest   = java.lang.Double.NaN;
  nmbSynapsesBest = java.lang.Double.NaN;
  ageBest         = java.lang.Double.NaN;
  avgNmbHidden = java.lang.Double.NaN;
  varNmbHidden = java.lang.Double.NaN;
  avgNmbSynapses = java.lang.Double.NaN;
  varNmbSynapses = java.lang.Double.NaN;
  avgAge     = java.lang.Double.NaN;
  varAge     = java.lang.Double.NaN;

  insNeuProb = java.lang.Double.NaN;
  delNeuProb = java.lang.Double.NaN;
  insSynProb = java.lang.Double.NaN;
  delSynProb = java.lang.Double.NaN;
  connect = java.lang.Double.NaN;
  costN = java.lang.Double.NaN;
  costS = java.lang.Double.NaN;
  birthGamma = java.lang.Double.NaN;

  const0 = java.lang.Double.NaN;
  const1 = java.lang.Double.NaN;
  const2 = java.lang.Double.NaN;
  const3 = java.lang.Double.NaN;

  
  /*
  maxSysPerf = 0;
  avgSysPerf = 0;
  varSysPerf = 0;
 
  nmbHiddenBest = 0;
  nmbSynapsesBest = 0;
  avgNmbHidden = 0;
  varNmbHidden = 0;
  avgNmbSynapses = 0;
  varNmbSynapses = 0;

  insNeuProb = 0;
  delNeuProb = 0;
  insSynProb = 0;
  delSynProb = 0;
  connect = 0;
  costN = 0;
  costS = 0;
  birthGamma = 0;
  */

    }

    /* set and get functions  */

  public void setConst( double v, int index){
    if(index == 0)
    {
      this.const0 = v;
    }
    else if(index == 1)
    {
      this.const1 = v;
    }
    else if(index == 2)
    {
      this.const2 = v;
    }
    else if(index == 3)
    {
      this.const3 = v;
    }
  }

  public double getConst(int index){
    if(index == 0)
    {
      return this.const0;
    }
    else if(index == 1)
    {
      return this.const1;
    }
    else if(index == 2)
    {
      return this.const2;
    }
    else if(index == 3)
    {
      return this.const3;
    }
    return java.lang.Double.NaN;
  }





    public void setCurrentOffsprings(double v){
  if(v < 1)
  {
      this.currentOffsprings = 0.0;
  }
  else
  {
      this.currentOffsprings = v;
  }
    }

    public double getCurrentOffsprings(){
  return this.currentOffsprings; 
    }





    public void setCurrentPopSize(double v){
  if(v < 1)
  {
      this.currentPopSize = 0.0;
  }
  else
  {
      this.currentPopSize = v;
  }
    }

    public double getCurrentPopSize(){
  return this.currentPopSize; 
    }



    public void setVarAge(double v){
  if(v < 0.0)
  {
      this.varAge = java.lang.Double.NaN;
  }
  else
  {
      this.varAge = v;
  }
    }

    public double getVarAge(){
  return (this.varAge);
    }

    public void setAvgAge(double v){
  if(v < 0.0)
  {
      this.avgAge = java.lang.Double.NaN;
  }
  else
  {
      this.avgAge = v;
  }
    }

    public double getAvgAge(){
  return (this.avgAge);
    }


    public void setAgeBest(double v){
  if(v < 0.0)
  {
      this.ageBest = java.lang.Double.NaN;
  }
  else
  {
      this.ageBest = v;
  }
    }

    public double getAgeBest(){
  return this.ageBest;
    }


    public void setAgeOldest(double v){
  if(v < 0.0)
  {
      this.ageOldest = java.lang.Double.NaN;
  }
  else
  {
      this.ageOldest = v;
  }
    }

    public double getAgeOldest(){
  return this.ageOldest;
    }


    public void setMaxSysPerf(double v){
  this.maxSysPerf = v;
    }

    public double getMaxSysPerf(){
  return this.maxSysPerf;
    }


    public void setAvgSysPerf(double v){
  this.avgSysPerf = v;
    }

    public double getAvgSysPerf(){
  return this.avgSysPerf;
    }


    public void setVarSysPerf(double v){
  this.varSysPerf = v;
    }


    public double getVarSysPerf(){
  return this.varSysPerf;
    }



    public void setNmbHiddenBest(double v){
  this.nmbHiddenBest = v;
    }

    public double getNmbHiddenBest(){
  return this.nmbHiddenBest;
    }


    public void setNmbSynapsesBest(double v){
  this.nmbSynapsesBest = v;
    }

    public double getNmbSynapsesBest(){
  return this.nmbSynapsesBest;
    }

    public void setAvgNmbHidden(double v){
  this.avgNmbHidden = v;
    }

    public double getAvgNmbHidden(){
  return this.avgNmbHidden;
    }

    public void setVarNmbHidden(double v){
  this.varNmbHidden = v;
    }

    public double getVarNmbHidden(){
  return this.varNmbHidden;
    }

    public void setAvgNmbSynapses(double v){
  this.avgNmbSynapses = v;
    }

    public double getAvgNmbSynapses(){
  return this.avgNmbSynapses;
    }

    public void setVarNmbSynapses(double v){
  this.varNmbSynapses = v;
    }

    public double getVarNmbSynapses(){
  return this.varNmbSynapses;
    }

    public void setInsNeuProb(double v){
  this.insNeuProb = v;
    }

    public double getInsNeuProb(){
  return this.insNeuProb;
    }

    public void setDelNeuProb(double v){
  this.delNeuProb = v;
    }

    public double getDelNeuProb(){
  return this.delNeuProb;
    }

    public void setInsSynProb(double v){
  this.insSynProb = v;
    }

    public double getInsSynProb(){
  return this.insSynProb;
    }

    public void setDelSynProb(double v){
  this.delSynProb = v;
    }

    public double getDelSynProb(){
  return this.delSynProb;
    }

    public void setConnect(double v){
  this.connect = v;
    }

    public double getConnect(){
  return this.connect;
    }

    public void setCostN(double v){
  this.costN = v;
    }

    public double getCostN(){
  return this.costN;
    }

    public void setCostS(double v){
  this.costS = v;
    }

    public double getCostS(){
  return this.costS;
    }

    public void setBirthGamma(double v){
  this.birthGamma = v;
    }

    public double getBirthGamma(){
  return this.birthGamma;
    }

    public double getMaxOutPerf(){
  return this.maxOutPerf;
    }

    
    public void fillPopState(Population p){
  Net n = null;
  double dblVal;

  /* to the current population performance */
  this.maxOutPerf = p.bestOutPerf();
  this.maxSysPerf = p.bestPerformance();
  this.avgSysPerf = p.avgPerformance();
  this.varSysPerf = p.sqrtVarPerformance();

  
  this.avgPopSize = p.getPopSize();
  dblVal = (double)  p.getCurrentPopSize();
  this.currentPopSize = dblVal;
  this.currentOffsprings = dblVal -  ((double) p.getCurrentParents());

  /* to the structure */  
  n = p.getBestNet();
  if(n != null)
  {
      this.nmbHiddenBest   = ((double) (n.getHiddenNeurons()).size());
      this.nmbSynapsesBest = ((double)  n.getSynapseCount());
  }
  else
  {
      this.nmbHiddenBest   = java.lang.Double.NaN;
      this.nmbSynapsesBest = java.lang.Double.NaN;
  }

  int age = p.getAgeOfBest();
  if(age == -1)
  {
      this.ageBest  = java.lang.Double.NaN;
  }
  else
  {
      this.ageBest = ((double) age);
  }

  this.avgAge = p.avgAge();
  this.varAge = java.lang.Math.sqrt(p.varAge());

        age = p.getAgeOfOldest();
  if(age == -1)
  {
      this.ageOldest = java.lang.Double.NaN;
  }
  else
  {
      this.ageOldest = ((double) age);
  }




  this.avgNmbHidden = p.avgNmbHidden();
  this.varNmbHidden = java.lang.Math.sqrt(p.varNmbHidden());
  this.avgNmbSynapses = p.avgNmbSynapses();
  this.varNmbSynapses = java.lang.Math.sqrt(p.varNmbSynapses());

  /* parameter */
  this.insNeuProb = p.getInsNeuProb();
  this.delNeuProb = p.getDelNeuProb();
  this.insSynProb = p.getInsSynProb();
  this.delSynProb = p.getInsSynProb();
  this.connect = p.getConnProb();
  this.costN = p.getCostNeu();
  this.costS = p.getCostSyn();
  this.birthGamma = p.getBirthGamma();

  /* fitness consts */
  this.const0 = p.getC0();
  this.const1 = p.getC1();
  this.const2 = p.getC2();
  this.const3 = p.getC3();
  
  return;
    }


    public String toString(){
  String s = new String(
      String.valueOf(numberFormat.format(this.maxOutPerf)) + "\t" +
      String.valueOf(numberFormat.format(this.maxSysPerf)) + "\t" +
      String.valueOf(numberFormat.format(this.avgSysPerf)) + "\t" +
      String.valueOf(numberFormat.format(this.varSysPerf)) + "\t" +
      String.valueOf(numberFormat.format(this.avgPopSize)) + "\t" +
      String.valueOf(numberFormat.format(this.currentPopSize)) + "\t" +
      String.valueOf(numberFormat.format(this.currentOffsprings)) + "\t" +
      String.valueOf(numberFormat.format(this.ageBest)) + "\t" +
      String.valueOf(numberFormat.format(this.avgAge)) + "\t" +
      String.valueOf(numberFormat.format(this.varAge)) + "\t" +
      String.valueOf(numberFormat.format(this.ageOldest)) + "\t" +
      String.valueOf(numberFormat.format(this.nmbHiddenBest)) + "\t" +
      String.valueOf(numberFormat.format(this.nmbSynapsesBest)) + "\t" +
      String.valueOf(numberFormat.format(this.avgNmbHidden)) + "\t" +
      String.valueOf(numberFormat.format(this.varNmbHidden)) + "\t" +
      String.valueOf(numberFormat.format(this.avgNmbSynapses)) + "\t" +
      String.valueOf(numberFormat.format(this.varNmbSynapses)) + "\t" +
      String.valueOf(numberFormat.format(this.insNeuProb)) + "\t" +
      String.valueOf(numberFormat.format(this.delNeuProb)) + "\t" +
      String.valueOf(numberFormat.format(this.insSynProb)) + "\t" +
      String.valueOf(numberFormat.format(this.delSynProb)) + "\t" +
      String.valueOf(numberFormat.format(this.connect)) + "\t" +
      String.valueOf(numberFormat.format(this.costN)) + "\t" +
      String.valueOf(numberFormat.format(this.costS)) + "\t" +
      String.valueOf(numberFormat.format(this.birthGamma)) + "\t" +
      String.valueOf(numberFormat.format(this.const0)) + "\t" +   
      String.valueOf(numberFormat.format(this.const1)) + "\t" +  
      String.valueOf(numberFormat.format(this.const2)) + "\t" +  
      String.valueOf(numberFormat.format(this.const3)) + "\n"       
      );
  return s;
    }

  /**
   * For Selfstest. Do <b> not call </b> as class method.
   * @param    none
   * @return   none
   */
  public static void main(String argv[])
  {
      PopState  state = new PopState();
      System.out.println(state.toString());
  }

}











