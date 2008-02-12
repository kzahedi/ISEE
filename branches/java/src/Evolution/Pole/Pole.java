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


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringBufferInputStream;
import java.util.Random;

import util.io.XMLHandler;

import cholsey.Net;
import cholsey.Neuron;
import cholsey.NeuronList;
import cholsey.Transferfunction;


public class Pole  implements Runnable{

  public final static int EVO  = 0; // a server listens for a connection
  public final static int TEST = 1; // a client calls for a connection

  public final static int SHOW_ALL  = 0; // a server listens for a connection
  public final static int SHOW_BEST = 1; // a client calls for a connection

  private Random rand = null;

  private Net    controller;
  private Net    testController;

  /* system boundary conditions */
  private double timeStep;
  private int    timeQuietNr;
  private int    timeMaxNr;
  private int    timeTestNr;

  private double cost_force;
  private double bonus_loc_cart;
  private double bonus_ang_pole;

  private double max_init_loc_cart; 
  private double max_init_ang_pole;

  private double max_loc_cart; 
  private double max_ang_pole;

  private double outputPerf;

  /* system variables */
  private int    simTime;
  private double loc_cart;
  private double ang_pole;
  private double rate_loc_cart;
  private double rate_ang_pole;
  private double rate_rate_ang_pole;
  private double rate_rate_loc_cart;
  private double force_cart;

  private double force_integral;
  private double time_integral;

  private double n0_force;
  private double n1_force;



  /* CONSTANTS */
  private final static double GRAV    =  9.81;
  private final static double M_CART  =  1.0;
  private final static double M_POLE  =  0.1;
  private final static double L_POLE  =  0.5;
  private final static double MU_CART =  0.0005;   // 5 e-4;
  private final static double MU_POLE =  0.000002; // 2e-6;
  private final static double F_CART  = 10.0;


  /* control panels */
  ActionListener clientPanel = null;
  ActionListener phasePanel = null;
  ActionListener firstReturnPanel = null;
  ActionListener parameterPanel = null;

  /* simulation mode */
  private int simMode = 1;
  private int plotMode = 0;
  private boolean doTest;


  /* communication */
  private PrintWriter  evoInput = null;
  private BufferedReader  evoOutput = null;


  /* logging of the current and the best turn */
  private PoleLogList bestTurn = new PoleLogList();
  private PoleLogList currentTurn = new PoleLogList();


  public Pole(){
    rand = new Random();

    this.timeStep    = 0.01;
    this.timeQuietNr = 3;
    this.timeMaxNr   = 1000;
    this.timeTestNr  = 1000;

    this.cost_force     = 0.0001;
    this.bonus_loc_cart = 4.0;
    this.bonus_ang_pole = 6.0;

    this.max_init_loc_cart = 1.8; 
    this.max_init_ang_pole = Math.PI / 15.0;

    this.max_loc_cart = 2.4; 
    this.max_ang_pole = Math.PI / 12.0;

    this.controller = null;

    this.outputPerf = java.lang.Double.POSITIVE_INFINITY; 
    this.force_integral = 0.0;
    this.time_integral = 0.0;
  }



  public void run(){
    if(simMode == this.EVO)
    {
      evaluateControllers();
    }
    if(simMode == this.TEST)
    {
      testFixedController();
    }
  }


  public void myStop(){
    doTest = false;
  }

  public void setPrintWriter(PrintWriter  inp){
    this.evoInput = inp;
  }
  public PrintWriter getPrintWriter(){
    return(this.evoInput);
  }

  public void setBufferedReader(BufferedReader out){
    this.evoOutput = out;
  }
  public BufferedReader getBufferedReader(){
    return(this.evoOutput);
  }

  public void setPhasePanel(ActionListener panel){
    this.phasePanel = panel;
  }

  public  ActionListener getPhasePanel(){
    return (this.phasePanel);
  }

  public void setFirstReturnPanel(ActionListener panel){
    this.firstReturnPanel = panel;
  }

  public  ActionListener getFirstReturnPanel(){
    return this.firstReturnPanel;
  }

  public  ActionListener getParameterPanel(){
    return this.parameterPanel;
  }
  public void setParameterPanel(ActionListener panel){
    this.parameterPanel = panel;
  }


  public void testFixedController(){
    clearBestTurn();
    clearCurrentTurn();
    firstReturnPanel.actionPerformed(new ActionEvent(this,300, "NEW_TURN"));
    phasePanel.actionPerformed(new ActionEvent(this,300, "NEW_TURN"));
    evaluate(testController);
    clientPanel.actionPerformed(new ActionEvent(this,300, "TEST_END"));
  }


  public void evaluateControllers(){
    XMLHandler reading = new XMLHandler();
    Net currentNet = null;
    boolean doEvaluate = true;
    String str;
    Random rand = new Random();

    double currentSysPerf = java.lang.Double.NEGATIVE_INFINITY;
    double bestSysPerf = java.lang.Double.NEGATIVE_INFINITY;

    int warmUp;
    int evalTestSteps;


    while(doEvaluate){
      try{
        // wait for a control string wich is hopefully readable
        str = this.evoOutput.readLine();

        // a new generation is comming, 
        // clear all loggings of the previous generation
        if(str.equals("NEXT_GEN"))
        {

          currentSysPerf = java.lang.Double.NEGATIVE_INFINITY;
          bestSysPerf = java.lang.Double.NEGATIVE_INFINITY;
          clearBestTurn();
          clearCurrentTurn();
          clientPanel.actionPerformed(new ActionEvent(this,300, "NEW_GEN"));
          parameterPanel.actionPerformed(new ActionEvent(this,301, "NEW_GEN"));


          // receive warm up span
          str =  this.evoOutput.readLine();
          warmUp = Integer.parseInt(str);
          // receive test steps
          str =  this.evoOutput.readLine();
          evalTestSteps = Integer.parseInt(str);

          this.setTimeMaxNr(warmUp + evalTestSteps);
          this.setTimeTestNr(evalTestSteps);


          // recive C0
          str =  this.evoOutput.readLine();
          // recive C1
          str =  this.evoOutput.readLine();
          // recive C2
          str =  this.evoOutput.readLine();
          // recive C3
          str =  this.evoOutput.readLine();

        }


        // connection is closed by the evolution program
        if(str.equals("CLOSE"))
        {
          doEvaluate = false;
        }

        // a new individual is coming
        if(str.equals("NEXT_INDY"))
        {

          clearCurrentTurn();
          if(plotMode == SHOW_ALL)
          {
            firstReturnPanel.actionPerformed(new ActionEvent(this,300, "NEW_TURN"));
            phasePanel.actionPerformed(new ActionEvent(this,300, "NEW_TURN"));
          };

          // recive network grammar
          str =  this.evoOutput.readLine();

          // receive network
          currentNet =  reading.readNetFromInputStream(new StringBufferInputStream(str));

          // evaluate received net
          evaluate(currentNet);

          // send the calculated output performance 
          this.evoInput.println(getOutputPerf());

          // get the system performance string "SYS_PERF"
          str = this.evoOutput.readLine();
          // get the system performance value 
          str = this.evoOutput.readLine();

          // now should the pole simulator have all perf. information to the current net

          currentSysPerf = Double.parseDouble(str);

          // logging the best individual
          if(plotMode == SHOW_BEST)
          {
            if(currentSysPerf > bestSysPerf)
            {
              bestSysPerf = currentSysPerf;
              saveBestTurn();
            }
          }
        }

        // information of the end of the population, 
        // which means, the whole generation is evaluated
        // and can the best run be plotted
        if(str.equals("END_POP"))
        {
          if(plotMode == SHOW_BEST)
          {
            firstReturnPanel.actionPerformed(new ActionEvent(this,300, "DRAW_BEST"));
            phasePanel.actionPerformed(new ActionEvent(this,300, "DRAW_BEST"));
          }
          clientPanel.actionPerformed(new ActionEvent(this,300, "END_POP"));
        }
      }
      catch (IOException e)
      { 
        System.out.println("not readable");
      }
    }
    // the doEvaluation tag is set to false, the connection will close 
    clientPanel.actionPerformed(new ActionEvent(this,5, "CLOSE"));
  }


  public void setClientPanel(ActionListener p){
    clientPanel = p;
  }





  public void evaluate(Net n){
    boolean dead = false;

    double system_val[]     = new double[4];
    double controller_out[] = new double[1];

    if(n == null)
    {
      this.setOutputPerf(java.lang.Double.POSITIVE_INFINITY);
      return;
    }
    this.setController(n);

    this.init();

    for(this.simTime = 0; (this.simTime < timeMaxNr) && (!dead); this.simTime++)
    {

      /* get system values */
      system_val[0] = loc_cart / max_loc_cart;
      system_val[1] = ang_pole / max_ang_pole;
      system_val[2] = rate_loc_cart / max_loc_cart;
      system_val[3] = rate_ang_pole / max_ang_pole;


      /* feed and update the net */     
      this.updateController(system_val);

      /* get controller output */
      this.n0_force = this.n1_force;
      this.n1_force =  this.readController();

      if(this.simTime >= this.timeQuietNr )
      {
        this.force_cart = F_CART * this.n1_force;
      }
      else
      {
        this.force_cart = 0.0;
      }

      dead = this.updatePoleSystem();

      /* update graphics and loogings */
      if((this.simMode == EVO) && (plotMode == SHOW_BEST))
      {
        logState();
      }

      if((this.simMode == TEST) || (plotMode == SHOW_ALL))
      {
        firstReturnPanel.actionPerformed(new ActionEvent(this,300, "NEW_POINTS"));
        phasePanel.actionPerformed(new ActionEvent(this,300, "NEW_POINTS"));
      }


      /* logging for performance calculation */
      if(this.simTime > (this.timeMaxNr - this.timeTestNr))
      {
        this.outputPerf = this.outputPerf + 
          ( (  1.0 - java.lang.Math.abs(this.ang_pole) / this.max_ang_pole) * this.bonus_ang_pole
            + (1.0 - java.lang.Math.abs(this.loc_cart) /this.max_loc_cart) * this.bonus_loc_cart
          ) * this.timeStep;
      }
    }

    /* calculate output performance */
    this.time_integral  = this.time_integral + this.timeStep;
    this.force_integral = this.force_integral + java.lang.Math.abs(this.force_cart)*this.timeStep;
    this.outputPerf = this.outputPerf - (this.cost_force * this.force_integral / this.time_integral);
  }


  public void init(){
    this.simTime = 0;
    this.loc_cart = 2.0*(rand.nextDouble() - 0.5)*max_init_loc_cart;
    this.ang_pole = 2.0*(rand.nextDouble() - 0.5)*max_init_ang_pole;
    this.rate_loc_cart = 0.0;
    this.rate_ang_pole = 0.0;

    this.controller.randomInitActivity();

    this.outputPerf = 0.0;
    this.force_integral = 0.0;
    this.time_integral = 0.0;
    this.force_cart = 0.0;
  }


  public void updateController(double v[]){
    NeuronList nl = null;
    nl = this.controller.getInputNeurons();

    if(nl.size() == 2)
    {
      nl.neuron(0).setActivation(v[0]);
      nl.neuron(1).setActivation(v[1]);
    }

    if(nl.size() == 4)
    {
      nl.neuron(0).setActivation(v[0]);
      nl.neuron(1).setActivation(v[1]);
      nl.neuron(2).setActivation(v[2]);
      nl.neuron(3).setActivation(v[3]);
    }

    this.controller.process();

  }

  public double readController(){
    NeuronList nl = null;
    Neuron     n  = null;
    nl = this.controller.getOutputNeurons();
    n = nl.neuron(0);

    if(this.controller.getTransferfunction() == Transferfunction.TANH)
    {
      return n.getOutput();
    }
    else
    {
      return ((2*n.getOutput()) - 1.0);
    }


  }

  public boolean updatePoleSystem(){
    boolean dead = false;

    double co = Math.cos(ang_pole);
    double si = Math.sin(ang_pole);

    double summand1, summand2, zaehler, nenner;

    /*
       rate_rate_ang_pole = 
       (GRAV * si 
       - co * (this.force_cart + 
       M_POLE  * L_POLE * this.rate_ang_pole * this.rate_ang_pole * si + 
       MU_CART * fsgn (this.rate_loc_cart)) / (M_CART + M_POLE)
       - (MU_POLE * this.rate_ang_pole) / (M_POLE * L_POLE)
       ) 
       / ( L_POLE * (4.0 / 3.0 - 
       (M_POLE * co * co / (M_CART + M_POLE))) );
       */

    // rate_rate_ang_pole

    summand1 = (M_POLE + M_CART)*GRAV*si ;
    summand2 = co*(this.force_cart 
        - (MU_POLE * this.rate_ang_pole)
        - (MU_CART * fsgn(this.rate_loc_cart)) 
        + (M_POLE  * L_POLE * this.rate_ang_pole * this.rate_ang_pole * si) );
    zaehler = summand1 - summand2;
    nenner = L_POLE*(((4.0 / 3.0)*(M_POLE + M_CART)) - M_POLE*co*co );
    rate_rate_ang_pole = zaehler / nenner;


    /*
       rate_rate_loc_cart =
       (this.force_cart + 
       M_POLE * L_POLE * (this.rate_ang_pole * this.rate_ang_pole * si -  
       rate_rate_ang_pole * co) - 
       MU_CART * fsgn(this.rate_loc_cart) ) / (M_CART + M_POLE);     
       */


    summand1 = ( this.rate_ang_pole * this.rate_ang_pole * si) 
      - (rate_rate_ang_pole * co ) 
      - (MU_CART * fsgn(this.rate_loc_cart)) ;
    summand2 = M_POLE * L_POLE * summand1;
    zaehler = this.force_cart + summand2;
    rate_rate_loc_cart = zaehler / (M_CART + M_POLE);


    // System.out.println("######" + rate_rate_loc_cart);

    this.rate_ang_pole +=   this.timeStep *  rate_rate_ang_pole;
    this.rate_loc_cart +=   this.timeStep *  rate_rate_loc_cart;

    this.ang_pole += this.timeStep * this.rate_ang_pole;
    this.loc_cart += this.timeStep * this.rate_loc_cart;


    if (this.loc_cart > this.max_loc_cart)
    {
      loc_cart = this.max_loc_cart;
      dead = true;
      //System.out.println("1 DEAD");
    }

    if (this.loc_cart < (0.0 - this.max_loc_cart))
    {
      loc_cart = 0.0 - this.max_loc_cart;
      dead = true;
      //System.out.println("2 DEAD");
    }

    if (this.ang_pole > this.max_ang_pole)
    {
      ang_pole = this.max_ang_pole;
      dead = true;
      //System.out.println("3 DEAD");
    }

    if (this.ang_pole < (0.0 - this.max_ang_pole))
    {
      ang_pole = 0.0 - this.max_ang_pole;
      dead = true;
      //System.out.println("4 DEAD");
    }
    return dead;
  }

  private double fsgn(double v){
    if(v > 0.0) return 1.0;
    if(v < 0.0) return (-1.0);
    return 0.0;
  }


  /* logging the best turns */
  private void logState(){
    PoleState state = new PoleState();

    state.setForceSignal(force_cart);
    state.setLoc(loc_cart);
    state.setAng(ang_pole);
    state.setVelCar(rate_loc_cart);
    state.setVelAng(rate_ang_pole);
    state.setAclCar(rate_rate_loc_cart);
    state.setAclAng(rate_rate_ang_pole);
    this.currentTurn.add(state);
  }

  private void saveBestTurn(){
    PoleState state;

    this.bestTurn.clear();

    for(this.currentTurn.start(); this.currentTurn.hasMore(); this.currentTurn.next())
    {
      state = this.currentTurn.state();
      this.bestTurn.add(state);
    }
    this.currentTurn.clear();

  }

  public void clearCurrentTurn(){
    this.currentTurn.clear();
  }

  public void clearBestTurn(){
    this.bestTurn.clear();
  }

  public PoleLogList getCurrentTurn(){
    return this.currentTurn;
  }

  public PoleLogList getBestTurn(){
    return this.bestTurn;
  }


  /* gets and sets */

  public double getN0ForceSignal(){
    return n0_force;
  }

  public double getN1ForceSignal(){
    return n1_force;
  }


  public void setPlotMode(int v){
    if(v <= 0)
    {
      plotMode = this.SHOW_ALL;
    }
    else
    {
      plotMode = this.SHOW_BEST;
    }
  }



  public void setMode(int v){
    if(v <= 0){
      simMode = this.EVO;
    }
    else
    {
      simMode = this.TEST;
    }
  }

  public double getSimTime(){
    return simTime;
  }


  public double getForceCar(){
    return this.force_cart;
  }



  public double getLocCar(){
    return this.loc_cart;
  }
  public double getAngPole(){
    return this.ang_pole;
  }

  public double getRateLocCar(){
    return this.rate_loc_cart;
  }

  public double getRateAngPole(){
    return this.rate_ang_pole;
  }



  public void setTestController(Net n){
    this.testController = n;
  }

  public void setController(Net n){
    this.controller = n;
  }



  public void setOutputPerf(double value ){
    this.outputPerf =  value;
  }


  public double getOutputPerf(){
    return (this.outputPerf);
  }



  public void setMaxAngPole(double value){
    if(value < 0.00000001)
    {
      this.max_ang_pole = 0.261799;
    }
    else
    {
      this.max_ang_pole = value;
    }

    if(this.max_ang_pole < this.max_init_ang_pole)
    {
      this.max_ang_pole = this.max_init_ang_pole;
    }
  }

  public double getMaxAngPole(){
    return (this.max_ang_pole);
  }


  public void setMaxLocCar(double value){
    if(value < 0.5)
    {
      this.max_loc_cart = 2.4;
    }
    else 
    {
      this.max_loc_cart = value;
    }

    if(this.max_loc_cart < this.max_init_loc_cart)
    {
      this.max_loc_cart = this.max_init_loc_cart;
    }

  }

  public double getMaxLocCar(){
    return (this.max_loc_cart);
  }

  public void setMaxInitAngPole(double value){
    if((value > max_ang_pole) || (value < 0.0))
    {
      this.max_init_ang_pole = max_ang_pole;
    }
    else
    {
      this.max_init_ang_pole = value;
    }
  }

  public double getMaxInitAngPole(){
    return (this.max_init_ang_pole);
  }

  public void setMaxInitLocCar(double value){
    if((value > max_loc_cart) || (value < 0.0))
    {
      this.max_init_loc_cart = this.max_loc_cart;
    }
    else
    {
      this.max_init_loc_cart = value;
    }
  }

  public double getMaxInitLocCar(){
    return (this.max_init_loc_cart);
  }



  public void setBonusAngPole(double value){
    if(value < 0.0)
    {
      this.bonus_ang_pole = 0.0;
    }
    else
    {
      this.bonus_ang_pole = value;
    }
  }


  public double getBonusAngPole(){
    return (this.bonus_ang_pole);
  }


  public void setBonusLocCar(double value){
    if(value < 0.0)
    {
      this.bonus_loc_cart = 0.0;
    }
    else
    {
      this.bonus_loc_cart = value;
    }
  }

  public double getBonusLocCar(){
    return (this.bonus_loc_cart);
  }


  public void setCostForce(double value){
    if(value < 0.0)
    {
      this.cost_force = 0.0;
    }
    else
    {
      this.cost_force = value;
    }
  }

  public double getCostForce(){
    return (this.cost_force);
  }


  public void setTimeTestNr(int value ){
    if(value > this.timeMaxNr)
    {
      this.timeTestNr = this.timeMaxNr;
    }
    else
    {
      this.timeTestNr = value;
    }
  }


  public int getTimeTestNr(){
    return(timeTestNr);
  }


  public void setTimeMaxNr(int value){
    if(value <= 0)
    {
      this.timeMaxNr = 10;
    }
    else
    {
      this.timeMaxNr = value;
      if(this.timeMaxNr < this.timeTestNr){
        this.timeMaxNr = this.timeTestNr;
      }

    }
  }

  public int getTimeMaxNr(){
    return (this.timeMaxNr);
  }


  public int getQuietSteps(){
    return (this.timeQuietNr);
  }

  public double getTimeStep(){
    return (this.timeStep);
  }

  public void setTimeStep(double value){
    if(value <= 0.0001)
    {
      this.timeStep = 0.0001;
    }
    else
    {
      this.timeStep = value;
    }
  }





}













