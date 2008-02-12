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
 * Created on 04.05.2004
 *
 * class Evolution of package Evolution
 * 
 */
package Evolution;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Vector;

import util.io.EvoTaskXMLHandler;

import Evolution.Random.Gauss;
import Evolution.Random.Poisson;



public class Evolution  implements Runnable{

  /* simulator data */
  private int warmUpSpan = 0;
  private int evalTestSteps  = 2000;
  private EvaluationType evalType = EvaluationType.ONE2ONE;
  private int numberEvalTasks = 2;

  /* the set of populations */
  private int popNumb;
  private PopulationList pops = new PopulationList();

  /* logging  */
  private File         bestNetsFile;
  private int          countBest;
  private PrintWriter  out;

  /* control parameters */
  private boolean doEvolution = false;
  private int generation = 0;
  private boolean evoIsRunning = false;

  /* global pseudo random generators */
  private Random  uniRand;
  private Gauss   gauss;
  private Poisson poisson;
  private Random  intRand = new Random();

  /* control panels */
  private ActionListener evoTaskCtrlPanel = null;
  private ActionListener evoCtrlPanel     = null;
  private ActionListener evalCtrlPanel    = null;

  /* monitors */
  private int            popIndex;
  private ActionListener netRankPanel = null;
  private ActionListener monitorPanel = null;
  private ActionListener loggingPanel = null;

  /* winidows */
  private NetRankFrame netRankWin = null; 
  private MonitorFrame monitorWin = null; 
  private GlobalPopParameterFrame popParaWin = null; 



  public Evolution (){
    uniRand = new Random();
    gauss   = new Gauss(0.0,1.0);
    poisson = new Poisson(uniRand);

    doEvolution = false;
    generation = 0;

    countBest = 1;
    bestNetsFile = null;

    popNumb = 1;
    popIndex = 0;

    Population pop = new Population(this, "pop1", 50, uniRand, gauss, poisson);
    pops.add(pop);
  }

  public Evolution (int numPops, int sizePop){
    this();

    Population pop;


    pops.clear();
    if(numPops < 1)
    {
      popNumb = 1;
    }
    else
    {
      popNumb = numPops;
    };

    for(int i = 0; i < popNumb; i++){
      pop = new Population(this, "pop" + (i+1), sizePop, uniRand, gauss, poisson);
      pops.add(pop);
    };

    this.popIndex = 0;
  }

  public void setTaskCtrlPanel(ActionListener l){
    this.evoTaskCtrlPanel = l;
  }

  public void setEvoCtrlPanel(ActionListener l){
    this.evoCtrlPanel = l;
  }

  public void setEvalCtrlPanel(ActionListener l){
    this.evalCtrlPanel = l;
  }

  /*
     public ActionListener getCtrlPanel(){
     return (this.evoCtrlPanel);
     }
   */



  /*
   * All populations are set to the same average population size.
   * This size is the minimum of the population size over all populations.
   * This is needed for the MIN_SYNC (minimal synchron update) evaluation.
   * @param none
   * @return none
   */
  private void setPopToGlobMinSize(){
    int minPopSize = Integer.MAX_VALUE;
    Population pop = null; 

    for(this.pops.start(); this.pops.hasMore(); this.pops.next())
    {
      if(minPopSize > (pops.currentPop()).getPopSize())
      {
        minPopSize = (pops.currentPop()).getPopSize();
      }
    }
    for(this.pops.start(); this.pops.hasMore(); this.pops.next())
    {
      pop = pops.currentPop(); 
      pop.setPopSize(minPopSize);
      // may be the avg. population was changed before in this function
      //  update the online parameter
      pop.popRefresh();
    }
  }


  /*
   * All populations are set to the same average population size.
   * This size is the maximum of the population size over all populations.
   * This is needed for the MAX_SYNC (maximal synchron update) evaluation.
   * @param none
   * @return none
   */
  private void setPopToGlobMaxSize(){
    int maxPopSize = Integer.MIN_VALUE;
    Population pop = null; 

    for(this.pops.start(); this.pops.hasMore(); this.pops.next())
    {
      if(maxPopSize < (pops.currentPop()).getPopSize())
      {
        maxPopSize = (pops.currentPop()).getPopSize();
      }
    }
    for(this.pops.start(); this.pops.hasMore(); this.pops.next())
    {
      pop = pops.currentPop(); 
      pop.setPopSize(maxPopSize);
      // may be the avg. population was changed before in this function
      //  update the online parameter
      pop.popRefresh();
    }
  }


  /*
   * initializing of all populations by the current parameter.
   * @param none
   * @return none
   */
  public void evoInit(){
    Population pop = null; 

    generation = 0;

    /* reparing the populations for special evaluations methods */
    if(this.evalType == EvaluationType.MIN_SYNC)
    {
      setPopToGlobMinSize();
    }
    if(this.evalType == EvaluationType.MAX_SYNC)
    {
      setPopToGlobMaxSize();
    }


    for(this.pops.start(); this.pops.hasMore(); this.pops.next())
    {
      pop = pops.currentPop();
      pop.popInit();
    }



    evoTaskCtrlPanel.actionPerformed(new ActionEvent(this,20,"INIT"));  
    evoCtrlPanel.actionPerformed(new ActionEvent(this,21,"INIT"));  
    loggingPanel.actionPerformed(new ActionEvent(this,21,"INIT"));
    evalCtrlPanel.actionPerformed(new ActionEvent(this,21,"INIT"));

      // open logging file 
    openLoggingFile();
  }

  /*
   * reset each parameter of each populations.
   * @param none
   * @return none
   */
  public void evoReset(){
    Population pop = null; 



    for(this.pops.start(); this.pops.hasMore(); this.pops.next())
    {
      pop = pops.currentPop();
      pop.popReset();
    }

    evoTaskCtrlPanel.actionPerformed(new ActionEvent(this,21,"RESET")); 
    loggingPanel.actionPerformed(new ActionEvent(this,21,"RESET"));
    evalCtrlPanel.actionPerformed(new ActionEvent(this,21,"RESET"));

      // close logging file
    closeLoggingFile();
  }



  public void myStop(){
    System.out.println("stop1:" + this.doEvolution);
    this.doEvolution = false;
    System.out.println("stop2:" + this.doEvolution);
  }

  public void run(){
    Population pop = null; 

    this.doEvolution = true;
    this.evoIsRunning = true;

      // life loop
    while(this.doEvolution){
      //System.out.println("EVO-loop:" + this.doEvolution);
      this.evoStep();
    }

    this.evoIsRunning = false;
    evoTaskCtrlPanel.actionPerformed(new ActionEvent(this,61,"END_STEP"));  
  }


  public void evoStep(){
      //Population pop = null; 

      //System.out.println("step..." + this.getGenNmb() + ". Generation   ");

    evoCtrlPanel.actionPerformed(new ActionEvent(this,21,"NEW_GEN")); 

      //System.out.println("do reprod.  ");
    this.reproduction();
      //System.out.println("do vary   ");
    this.variation();
      //System.out.println("do eva   ");
    this.evaluation();
      //System.out.println("do selection   ");
    this.selection();

      //special post-processing according to a special evaluation methode
    this.postProcessing();

      // logging of all relevant data of the current populations 
      //  basically  for the online monitoring of evolution dynamics
    this.popDynUpdate();

      // logging
    this.writeCurrentEvoStep();

    this.generation++;

      // update monitors
    this.netRankPanel.actionPerformed(new ActionEvent(this,17,"EVO_REFRESH"));  
    this.monitorPanel.actionPerformed(new ActionEvent(this,17,"EVO_REFRESH"));  
    this.evoTaskCtrlPanel.actionPerformed(new ActionEvent(this,22,"STEP")); 
  }

  public void evoSingleStep(){
    evoStep();
  }


  /* repruduction, variation, selection operators  */
  private void reproduction(){
    for(this.pops.start(); this.pops.hasMore(); this.pops.next())
    {
      Population pop = pops.currentPop();
      pop.reproduction();
    }
  }

  private void variation(){
    for(this.pops.start(); this.pops.hasMore(); this.pops.next())
    {
      Population pop = pops.currentPop();
      pop.variation();
    }
  }

  private void selection(){
    /* reparing the populations for special evaluations methods */
    if(this.evalType == EvaluationType.MIN_SYNC)
    {
      setPopToGlobMinSize();
    }
    if(this.evalType == EvaluationType.MAX_SYNC)
    {
      setPopToGlobMaxSize();
    }

    for(this.pops.start(); this.pops.hasMore(); this.pops.next())
    {
      Population pop = pops.currentPop();
      pop.selection();

      //System.out.print("#popsize: " + pop.getCurrentPopSize() +" #");
    }
  }

  private void evaluation(){
    Vector myTasks = new Vector();
    Thread task = null;

    for(this.pops.start(); this.pops.hasMore(); this.pops.next())
    {
      Population pop = pops.currentPop();
      pop.setRandomSeed(intRand.nextInt());
      pop.setCurrentGenerationNmb(this.generation);

      task = new Thread(pop);
      myTasks.add(task);
      task.start();

      //pop.evaluation();
    }

    for(int i = 0; i < myTasks.size(); i++)
    {
      task = (Thread)myTasks.elementAt(i);
      try{
        task.join();
      }
      catch(InterruptedException e)
      {
        ;;
      }
    }
  }


  /* 
   * special selection post-processing for special
   * evaluations methods. Up to now its is only necessary for
   * the evaluation type MIN_SYNC and MAX_SYNC.
   * @param none
   * @return none
   */        
  private void postProcessing(){
      //cut the size of current populations to its minimum 
      // for the MIN_SYNC evaluation method
    if(this.evalType == EvaluationType.MIN_SYNC)
    {
      this.cutPopsToSize(this.getCurrentMinOfPopSize());
    }

      // increase the size of current populations to its maximum 
      // for the MAX_SYNC evaluation method
    if(this.evalType == EvaluationType.MAX_SYNC)
    {
      this.increasePopsToSize(this.getCurrentMaxOfPopSize());
    }

  }

  private void popDynUpdate(){
    for(this.pops.start(); this.pops.hasMore(); this.pops.next())
    {
      (pops.currentPop()).popDynUpdate();
    }

  }



      // function for special evaluation type post-processing

  /* 
   * decrease the current population size of each population to 
   * 'size' individual networks 
   * @param int size
   * @return none
   */        
  private void cutPopsToSize(int size){
    for(this.pops.start(); this.pops.hasMore(); this.pops.next())
    {
      (pops.currentPop()).cutPopToSize(size);
    }
  }

  /* 
   * increase the current population size of each population to 
   * 'size' individual networks
   * @param none
   * @return none
   */        
  private void increasePopsToSize(int size){
    for(this.pops.start(); this.pops.hasMore(); this.pops.next())
    {
      (pops.currentPop()).incPopToSize(size);
    }
  }


  /*
   * Gets the size of the smallest population in the evo-task.
   * This size is needed to decease the current population size 
   * of all populations in the evo-task to this minimal size.
   * This is needed for the MIN_SYNC (maximal synchron update) evaluation.
   * @param none
   * @return int size
   */
  private int getCurrentMinOfPopSize(){
    int minSize = Integer.MAX_VALUE;
    int currentSize;

    for(this.pops.start(); this.pops.hasMore(); this.pops.next())
    {
      currentSize = (pops.currentPop()).getCurrentPopSize();
      if(currentSize < minSize)
      {
        minSize = currentSize;
      }
    }
    return minSize;
  }

  /*
   * Gets the size of the largest population in the evo-task.
   * This size is needed to incease the current population size 
   * of all populations in the evo-task to this maximal size.
   * This is needed for the MAX_SYNC (maximal synchron update) evaluation.
   * @param none
   * @return int size
   */
  private int getCurrentMaxOfPopSize(){
    int maxSize = Integer.MIN_VALUE;
    int currentSize;

    for(this.pops.start(); this.pops.hasMore(); this.pops.next())
    {
      currentSize = (pops.currentPop()).getCurrentPopSize();
      if(currentSize > maxSize)
      {
        maxSize = currentSize;
      }
    }
    return maxSize;
  }

  /* logging file handling */

  public void openLoggingFile(){
    EvoTaskXMLHandler xml = new EvoTaskXMLHandler();

    try 
    {
      out = new PrintWriter(
          new OutputStreamWriter(
            new FileOutputStream(bestNetsFile.getPath())));
    }
    catch (IOException ev)
    {
      ev.printStackTrace();
    }

    out.print(xml.getEvoGrammarXML());
    out.print("<Evolution>\n");
  }

  public void closeLoggingFile(){
    out.print("</Evolution>\n");
    out.close();
  }

  public void writeCurrentEvoStep(){
    out.print("  <Generation Index=\""+ this.generation + "\">\n");

    int counter;
    Population pop;
    int i=0;
    for(this.pops.start();this.pops.hasMore();this.pops.next())
    {
      pop = this.pops.currentPop();

      out.print("    <Population Name=\"" + pop.getName() + "\" Index=\"" + i + "\">\n");
      out.print("       <Comment>\n");
      out.print("           no comment\n");
      out.print("       </Comment>\n");

      // the best n nets
      EvoObjectList indies = pop.getParents();
      EvoObject obj = null;

      if(this.countBest < indies.size() )
      {
        counter  = this.countBest;
      }
      else
      {
        counter = indies.size();
      }

      for(int j = 0; j < counter; j++)
      {
        obj = indies.object(j);
        out.print(obj.toXML(j,10));
        out.print("\n");
      }

      // log the oldest network / evoObject too
      obj = pop.getOldestEvoObject();
      out.print(obj.toXML(counter,10));
      out.print("\n");

      out.print("    </Population>\n");
      i++;
    }

    out.print("  </Generation>\n");
    System.out.flush();
    out.flush();

  }




  /* set and get functions */

  public void setNetRankWin(NetRankFrame frame){
    this.netRankWin = frame;
  }

  public NetRankFrame getNetRankWin(){
    return (this.netRankWin);
  }

  public void setMonitorWin(MonitorFrame frame){
    this.monitorWin = frame;
  }

  public MonitorFrame getMonitorWin(){
    return (this.monitorWin);
  }

  public void setPopParaWin(GlobalPopParameterFrame frame){
    this.popParaWin = frame;
  }

  public GlobalPopParameterFrame getPopParaWin(){
    return (this.popParaWin);
  }




  /* 
   * sets the number of parallel evaluation tasks, which one population 
   * can distributed on. This number is only defined, iff the 
   * EvaluationType == ONE2MANY, which is only if the evolution process
   * contains only one population
   * @param int nmb
   * @return none
   */    
  public void setNumberEvalTasks(int n){
    if(n < 2)
    {     
      this.numberEvalTasks = 2;
    }
    else
    {
      this.numberEvalTasks = n;
    }
  }

  /* 
   * sets the number of parallel evaluation tasks, see method 'setNumberEvalTask(int i)'
   * @param int nmb
   * @return none
   */    
  public int getNumberEvalTasks(){
    return (this.numberEvalTasks);
  }



  /* 
   * sets the evaluation type
   * @param int nmb
   * @return none
   */    
  public void setEvalType(EvaluationType type){
    if((type == EvaluationType.MIN_SYNC) && (this.popNumb == 1))
    {
      this.evalType = EvaluationType.ONE2ONE;
    }
    else
    {
      this.evalType = type;
    }
  }

  public EvaluationType getEvalType(){
    return (this.evalType);
  }

  /* 
   * file for best nets logging
   * return value != null-pointer iff. the file is writeable, 
   * @param String 
   * @return none
   */    
  public File getBestNetsFile(){
    return(this.bestNetsFile);
  }

  /* 
   * sets file for best nets logging
   * @param String 
   * @return none
   */    
  public void  setBestNetsFile(File f){
    this.bestNetsFile = f;
  }



  public int getCountBest(){
    return this.countBest;
  }


  public void setCountBest(int value){
    if(value < 1)
    {
      this.countBest = 1;
    }
    else
    {
      this.countBest = value;
    }
  }



  public boolean doEvolution(){
    return(this.doEvolution);
  }

  public void setCtrlLoggingPanel(ActionListener panel){
    this.loggingPanel = panel;
  }

  public void setNetRankPanel(ActionListener panel){
    this.netRankPanel = panel;
    netRankPanel.actionPerformed(new ActionEvent(this,30,"INIT"));
  }

  public ActionListener getNetRankPanel(){
    return (this.netRankPanel);
  }



  public void setMonitorPanel(ActionListener panel){
    this.monitorPanel = panel;
  }

  public ActionListener getMonitorPanel(){
    return (this.monitorPanel);
  }


  /* 
   * return value is the index of population in the population list, 
   * it indicates which population should get visible for the user in the gui.
   * @param int index
   * @return none
   */
  public void setPopIndex(int index){
    if(index < 0){
      this.popIndex = 0;
    }
    if(index >= this.popNumb){
      this.popIndex = this.popNumb - 1;
    }
    this.popIndex = index;
  }

  /* 
   * sets the index of population in the population list, 
   * the index indicates which population should get visible for the user in the gui.
   * @param int index
   * @return none
   */
  public int getPopIndex(){
    return  (this.popIndex);
  }

  /* 
   * return value is the generation number. 
   * it is increased by one  during one evolution step. 
   * @param none
   * @return int age
   */
  public int getGenNmb(){
    return generation;
  }


  /* 
   * return value is the numbe of the first 
   * time steps of evaluation without calculation any peformance.
   * @param none
   * @return int timeSteps
   */
  public int getWarmUpSpan(){
    return warmUpSpan;
  }


  /* 
   * set the number of the first time steps of evaluation
   * without calculation any performance.
   * @param int timeSteps
   * @return none
   */
  public void setWarmUpSpan(int c){
    if(c < 0)
    {
      warmUpSpan = 0;
    }
    else
    {
      warmUpSpan = c; 
    }
    return;
  }




  /* 
   * return value is the number of  
   * time steps of evaluation during performance is  calculated.
   * @param none
   * @return int evalTestSteps
   */
  public int getEvalTestSteps(){
    return evalTestSteps;
  }


  /* 
   * set the number of the time steps of evaluation
   *  calculation any per.
   * @param int evalTestSteps
   * @return none
   */
  public void setEvalTestSteps(int c){
    if(c < 1)
    {
      evalTestSteps = 1;
    }
    else
    {
      evalTestSteps = c; 
    }
    return;
  }





  /*
   * return values is the number of populations in this process.
   * @param none
   * @return int
   */
  public int getPopNumb(){
    return popNumb;
  }

  /*
   * return value is the list of current populations. 
   * @param none 
   * @return PopulationList
   */
  public PopulationList getPopulations(){
    return  pops;
  }


  public static void main(String argv[]){
    Evolution task = new Evolution(3, 20);

    System.out.print("evolution works!\n");


  };
}



