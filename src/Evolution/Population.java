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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import util.io.EvolutionSaxHandler;
import util.misc.RWLock;
import util.net.INetCommunication;

import Evolution.Random.Gauss;
import Evolution.Random.Poisson;
import cholsey.LearningRuleClassLoader;
import cholsey.Net;
import cholsey.Neuron;
import cholsey.NeuronList;
import cholsey.NeuronType;
import cholsey.ProcessMode;
import cholsey.Synapse;
import cholsey.SynapseList;
import cholsey.SynapseMode;
import cholsey.Transferfunction;

/**
 *  Populations implements management system of a set of networks.  <br>
 *
 *
 *
 *
 */



public class Population implements Runnable{

  private static LearningRuleClassLoader learningRuleClassLoader = new
    LearningRuleClassLoader();

  private Evolution evo = null;

  private int synapseInsertionMode = EvoObject.SYN_INS_MODE_DEFAULT;
  private int parameterMode        = EvoObject.PARAMETER_MODE_DEFAULT;

  /* individuals set */
  private  EvoObjectList parents;
  private  EvoObjectList offspring;

  /* globals */
  private String popName;
  private int    popSize;           // average population size
  private int    currentGeneration;
  private int    lastNetPIN;


  /* global neuro modules setings */
  private Transferfunction transferfunction;
  private SynapseMode      synapseMode;
  private int              inpNeurons;
  private int              outNeurons;
  private File             initialFile;
  private int              initialPopNmb;
  private int              initialGenNmb;
  private int              initialIdxNmb;
  private boolean          fileInit;


  /* parameter of the structure mutation */
  private  double insertNeuronProb;
  private  double insertSynProb;
  private  double deleteNeuronProb;
  private  double deleteSynProb;
  private  double connectivity;
  private  int    maxHiddenNeurons;

  /* parameter of real-value optimization / mutation */
  private  double changeBiasProb;
  private  double changeBiasIntens;
  private  double limitBias;
  private  double changeDecayProb;
  private  double changeDecayIntens;
  private  double limitDecay;
  private  double changeWeightProb;
  private  double changeWeightIntens;
  private  double limitWeight;

  /* general learn parameter  */
  private double alphaMax;
  private double alphaMin;
  private double alphaInitial;
  private double alphaProbability;
  private double alphaVariation;

  private double betaMax;
  private double betaMin;
  private double betaInitial;
  private double betaProbability;
  private double betaVariation;

  private double gammaMax;
  private double gammaMin;
  private double gammaInitial;
  private double gammaProbability;
  private double gammaVariation;

  private double deltaMax;
  private double deltaMin;
  private double deltaInitial;
  private double deltaProbability;
  private double deltaVariation;





  /* evaluation data & state */
  private double costNeuron;
  private double costSynapse;
  private int    port;
  private int    warmUpSpan;
  private int    evalTestSteps;
  private int    indyCount;
  private int    allIndies;

  /* simple communication */
  private INetCommunication server;
  private PrintWriter       simInput;
  private BufferedReader    simOutput; 

  /* one-to-many communication */
  private int     evalIndex;
  private RWLock  lock         = new RWLock();
  private Vector  serverVec    = new Vector();
  private Vector  simInputVec  = new Vector();
  private Vector  simOutputVec = new Vector();


  /* auxiliary constants */
  private double c0;
  private double c1;
  private double c2;
  private double c3;

  /* selection data */
  private double  birthGamma;
  private double  deathProb;
  private int     saveNBest;


  /* control panel for population */
  private ImageIcon      icon             = null;
  private ActionListener popParLoadSavePanel = null;
  private ActionListener ctrlNetTypePanel = null;
  private ActionListener ctrlSelectionPanel = null;
  private ActionListener ctrlEvaluationPanel = null;
  private ActionListener ctrlMutationPanel = null;
  private ActionListener monitorNetRankPanel = null;
  private ActionListener monitorPanel  = null;
  private ActionListener ctrlLearnPanel = null;



  /* population statistics */
  PopDyn popDyn = new PopDyn();


  /* global pseudo random generators */
  private Random  uniRand;
  private Gauss   gaussRand;
  private Poisson poissonRand;

  private int randomSeed = 0;  // random Seed for all Hinton-Clients





  /*  constructors */
  public Population(Evolution evo, Random uni, Gauss gauss, Poisson poisson){

    this.evo         = evo;
    this.uniRand     = uni;
    this.gaussRand   = gauss;
    this.poissonRand = poisson;

    popName             = new String("Population");
    transferfunction    = Transferfunction.TANH;
    synapseMode         = SynapseMode.CONVENTIONAL;
    inpNeurons          = 4;
    outNeurons          = 2;
    initialFile         = new File("./ini.xml");
    initialGenNmb       = 0;
    initialPopNmb       = 0;
    initialIdxNmb       = 0;
    fileInit            = false;

    lastNetPIN = 0;

    parents    = new EvoObjectList();
    offspring  = new EvoObjectList();
    popSize    = 50;
    birthGamma = 0.55;
    deathProb  = 0.0;

    maxHiddenNeurons = 100;

    insertNeuronProb = 0.2;
    insertSynProb    = 0.3;
    deleteNeuronProb = 0.4;
    deleteSynProb    = 0.6;
    connectivity     = 0.5;

    changeBiasProb     = 0.4;
    changeBiasIntens   = 0.3;
    limitBias          = 10.0;
    changeDecayProb    = 0.0;
    changeDecayIntens  = 0.0;
    limitDecay         = 0.0;
    changeWeightProb   = 0.9;
    changeWeightIntens = 0.2;
    limitWeight        = 20.0;


    alphaProbability = 0.0;
    alphaVariation  = 0.0;

    betaProbability = 0.0;
    betaVariation  = 0.0;

    gammaProbability = 0.0;
    gammaVariation  = 0.0;

    deltaProbability = 0.0;
    deltaVariation  = 0.0;



    costNeuron  = 2.5;
    costSynapse = 4.5;
    port  = 7000;
    warmUpSpan = 11;
    evalTestSteps = 111;

    saveNBest = 1;

    c0 = 1.0;
    c1 = 1.1;
    c2 = 1.2;
    c3 = 1.3;

    currentGeneration = 0;




  };

  public Population(Evolution evo, String name, int popsize, Random uni, Gauss gauss, Poisson poisson){
    this(evo, uni, gauss, poisson);
    this.setPopSize(popsize);
    this.setName(name);
  };


  public void updatePanel()
  {
    if(ctrlLearnPanel != null)
    {
      ((LearnParameterDialog)ctrlLearnPanel ).updatePanel(
                                                          alphaMax,
                                                          alphaMin,
                                                          alphaInitial,
                                                          alphaProbability,
                                                          alphaVariation,
                                                          betaMax,
                                                          betaMin,
                                                          betaInitial,
                                                          betaProbability,
                                                          betaVariation,
                                                          gammaMax,
                                                          gammaMin,
                                                          gammaInitial,
                                                          gammaProbability,
                                                          gammaVariation,
                                                          deltaMax,
                                                          deltaMin,
                                                          deltaInitial,
                                                          deltaProbability,
                                                          deltaVariation);
    }
  }

  public void setLearningParameter(
      double alphaMax,
      double alphaMin,
      double alphaInitial,
      double alphaProbability,
      double alphaVariation,
      double betaMax,
      double betaMin,
      double betaInitial,
      double betaProbability,
      double betaVariation,
      double gammaMax,
      double gammaMin,
      double gammaInitial,
      double gammaProbability,
      double gammaVariation,
      double deltaMax,
      double deltaMin,
      double deltaInitial,
      double deltaProbability,
      double deltaVariation)
  {
    this.alphaMax = alphaMax;
    this.alphaMin = alphaMin;
    this.alphaInitial = alphaInitial;
    this.alphaProbability = alphaProbability;
    this.alphaVariation = alphaVariation;
    this.betaMax = betaMax;
    this.betaMin = betaMin;
    this.betaInitial = betaInitial;
    this.betaProbability = betaProbability;
    this.betaVariation = betaVariation;
    this.gammaMax = gammaMax;
    this.gammaMin = gammaMin;
    this.gammaInitial = gammaInitial;
    this.gammaProbability = gammaProbability;
    this.gammaVariation = gammaVariation;
    this.deltaMax = deltaMax;
    this.deltaMin = deltaMin;
    this.deltaInitial = deltaInitial;
    this.deltaProbability = deltaProbability;
    this.deltaVariation = deltaVariation;

    SRNVariation.setInitialValue(
        alphaInitial,
        betaInitial,
        gammaInitial,
        deltaInitial);

//    System.out.println("alphaMax = " + alphaMax);
//    System.out.println("alphaMin = " + alphaMin);
//    System.out.println("alphaInitial = " + alphaInitial);
//    System.out.println("alphaProbability = " + alphaProbability);
//    System.out.println("alphaVariation = " + alphaVariation);
//    System.out.println("betaMax = " + betaMax);
//    System.out.println("betaMin = " + betaMin);
//    System.out.println("betaInitial = " + betaInitial);
//    System.out.println("betaProbability = " + betaProbability);
//    System.out.println("betaVariation = " + betaVariation);
//    System.out.println("gammaMax = " + gammaMax);
//    System.out.println("gammaMin = " + gammaMin);
//    System.out.println("gammaInitial = " + gammaInitial);
//    System.out.println("gammaProbability = " + gammaProbability);
//    System.out.println("gammaVariation = " + gammaVariation);
//    System.out.println("deltaMax = " + deltaMax);
//    System.out.println("deltaMin = " + deltaMin);
//    System.out.println("deltaInitial = " + deltaInitial);
//    System.out.println("deltaProbability = " + deltaProbability);
//    System.out.println("deltaVariation = " + deltaVariation);

  }




  /* events to the specific control panel  */
  public void setPopParameterLoadSavePanel(ActionListener panel){
    this.popParLoadSavePanel = panel;
  }

  public void setCtrlNetTypePanel(ActionListener ctrlPanel){
    this.ctrlNetTypePanel = ctrlPanel;
  }

  public void setCtrlSelectionPanel(ActionListener ctrlPanel){
    this.ctrlSelectionPanel = ctrlPanel;
  }
  public void setCtrlEvaluationPanel(ActionListener ctrlPanel){
    this.ctrlEvaluationPanel = ctrlPanel;
  }
  public ActionListener getCtrlEvaluationPanel(){
    return(this.ctrlEvaluationPanel);
  }
  public void setCtrlMutationPanel(ActionListener ctrlPanel){
    this.ctrlMutationPanel = ctrlPanel;
  }

  public void setMonitorNetRankPanel(ActionListener monitorPanel){
    this.monitorNetRankPanel = monitorPanel;
  }

  public void setMonitorPanel(ActionListener monitorPanel){
    this.monitorPanel = monitorPanel;
  }

  public void setCtrlLearnPanel(ActionListener ctrlPanel){
    this.ctrlLearnPanel = ctrlPanel;
  }






  public void popInit(){
    this.lastNetPIN = 0;
    this.popStructureInit();
    // opens the tcp/ip connection the other side
    this.popCommunicationInit();
    this.popDynInit();

    /* send an event to a specific control panel */
    /* because some values were changed */
    popParLoadSavePanel.actionPerformed(new ActionEvent(this,14,"INIT"));
    ctrlNetTypePanel.actionPerformed(new ActionEvent(this,15,"INIT"));
    ctrlEvaluationPanel.actionPerformed(new ActionEvent(this,17,"INIT"));
    monitorNetRankPanel.actionPerformed(new ActionEvent(this,18,"INIT"));
    monitorPanel.actionPerformed(new ActionEvent(this,19,"INIT"));
    ctrlMutationPanel.actionPerformed(new ActionEvent(this,312,"INIT"));
    ctrlSelectionPanel.actionPerformed(new ActionEvent(this,311,"INIT"));
  }


  public void popReset(){
    // close the connection by sending a "CLOSE"
    popCommunicationReset();
    popParLoadSavePanel.actionPerformed(new ActionEvent(this,22,"RESET"));
    ctrlNetTypePanel.actionPerformed(new ActionEvent(this,20,"RESET"));
    ctrlEvaluationPanel.actionPerformed(new ActionEvent(this,17,"RESET"));
    ctrlMutationPanel.actionPerformed(new ActionEvent(this,17,"RESET"));
  }


  public void popRefresh(){
    if(ctrlNetTypePanel != null)
    {
      ctrlNetTypePanel.actionPerformed(new ActionEvent(this,40,"REFRESH"));
    }

    if(ctrlEvaluationPanel != null)
    {
      ctrlEvaluationPanel.actionPerformed(new ActionEvent(this,42,"REFRESH"));
    }

    if(ctrlMutationPanel != null)
    {
      ctrlMutationPanel.actionPerformed(new ActionEvent(this,43,"REFRESH"));
    }

    if(ctrlSelectionPanel != null)
    {
      ctrlSelectionPanel.actionPerformed(new ActionEvent(this,44,"REFRESH"));
    }


    if(ctrlLearnPanel != null)
    {
      ctrlLearnPanel.actionPerformed(new ActionEvent(this,45,"REFRESH"));
    }
  }


  public void popUpdate(){
    popParLoadSavePanel.actionPerformed(new ActionEvent(this,400,"UPDATE"));
  }


  private void popCommunicationInit(){
    if(this.evo.getEvalType() == EvaluationType.MIN_SYNC)
    {
      simpleCommunicationInit();
      return;
    }
    if(this.evo.getEvalType() == EvaluationType.MAX_SYNC)
    {
      simpleCommunicationInit();
      return;
    }
    if(this.evo.getEvalType() == EvaluationType.ONE2ONE)
    {
      simpleCommunicationInit();
      return;
    }
    if(this.evo.getEvalType() == EvaluationType.ONE2MANY)
    {
      multiCommunicationInit();
      return;
    }
    System.out.println("attention: no evaluation opening at all");
  }

  private void popCommunicationReset(){
    if(this.evo.getEvalType() == EvaluationType.MIN_SYNC)
    {
      simpleCommunicationReset();
      return;
    }
    if(this.evo.getEvalType() == EvaluationType.MAX_SYNC)
    {
      simpleCommunicationReset();
      return;
    }
    if(this.evo.getEvalType() == EvaluationType.ONE2ONE)
    {
      simpleCommunicationReset();
      return;
    }
    if(this.evo.getEvalType() == EvaluationType.ONE2MANY)
    {
      multiCommunicationReset();
      return;
    }
    System.out.println("attention: no evaluation closing at all");
  }


  private void simpleCommunicationReset(){
    this.simInput.println("CLOSE");
    this.server.close();
  }


  private void simpleCommunicationInit(){
    this.server = new INetCommunication(INetCommunication.SERVER);
    server.setPort(this.getPortNumb());
    server.initConnection();
    if (server.getReader() == null || server.getWriter() == null)
    {
      System.out.println("Reader or Writer not done. exit");
      System.exit(0);
    }
    this.setPortNumb(this.server.getPort());
    ctrlEvaluationPanel.actionPerformed(new ActionEvent(this,25,"PORT_CHANGED"));

    this.simOutput = new BufferedReader(new InputStreamReader(this.server.getReader()));
    this.simInput  = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.server.getWriter())) ,true);
  }


  private void multiCommunicationInit(){
    int countSim = this.evo.getNumberEvalTasks();
    INetCommunication singleServer = null;
    PrintWriter       singleSimInput = null;
    BufferedReader    singleSimOutput = null; 

    this.serverVec.clear();
    this.simInputVec.clear();
    this.simOutputVec.clear();

    // open countSim servers
    for(int i=0; i < countSim; i++)
    {
      singleServer = new INetCommunication(INetCommunication.SERVER);
      singleServer.setPort(this.getPortNumb() + i);

      System.out.println("1:N evaluation use port nmb.: " + this.getPortNumb() + i);

      singleServer.initConnection();
      if (singleServer.getReader() == null || singleServer.getWriter() == null)
      {
        System.out.println("Reader or Writer not done. exit");
        System.exit(0);
      }
      singleSimOutput = new BufferedReader(new InputStreamReader(singleServer.getReader()));
      singleSimInput  = new PrintWriter(new BufferedWriter(new OutputStreamWriter(singleServer.getWriter())) ,true);

      this.serverVec.add(singleServer);
      this.simInputVec.add(singleSimInput);
      this.simOutputVec.add(singleSimOutput);
    }
  }

  private void multiCommunicationReset(){
    int countSim = this.evo.getNumberEvalTasks();
    INetCommunication singleServer = null;
    PrintWriter       singleSimInput = null;
    BufferedReader    singleSimOutput = null; 

    // close countSim servers
    if(this.serverVec.size() > 0){
      for(int i=0; i < countSim; i++)
      {
        singleServer   = (INetCommunication) serverVec.elementAt(i);
        singleSimInput = (PrintWriter)       simInputVec.elementAt(i);

        singleSimInput.println("CLOSE");
        singleServer.close();
      }
    }

    this.serverVec.clear();
    this.simInputVec.clear();
    this.simOutputVec.clear();
  }

  public void setSelectedLearningRule(int index)
  {
    learningRuleClassLoader.setSelectedLearningRule(index);
  }




  /*
   * initializing of a population - from file or by the empty structure.
   * we have one parent but popSize offsprings, which all are equal to this parent.
   * @param none
   * @return none
   */
  public void popStructureInit(){
    EvoObject obj; 
    int i;

    /* clear parent and offsprings */
    parents.clear();
    offspring.clear();

    /* initializing by file or with the empty structure  */
    /*   and add them as the one individual network with */
    /*   with popSize instances in the parent list       */
    if(fileInit)
    {

      if(initialIdxNmb < 0)
      {
        // initialization with all nets of the given gen. and pop.
        /*
        i = 0;
        do{
        obj = getSingleInitialStructureFromXMLfile(this.initialFile.getPath(),
        this.initialGenNmb,
        this.initialPopNmb,
        i);
        if(obj != null)
        {
        parents.add(obj);
        System.out.print("add " + i + "-th net\n");
        };
        i++;
        }while(obj != null);
         */
        EvolutionSaxHandler xml = new EvolutionSaxHandler();
        Vector nets = xml.readGeneration(
            this.initialFile.getPath(),
            this.initialGenNmb,
            this.initialPopNmb);
        for(int netIndex = 0; netIndex < nets.size(); netIndex++)
        {
          EvoObject netObj = new EvoObject(this.lastNetPIN++, this.uniRand,
              this.gaussRand);
          Net net = (Net)nets.elementAt(netIndex);
          System.out.println(net.toXML());
          netObj.setNet(net);
          netObj.setOutPerf(java.lang.Double.NEGATIVE_INFINITY,costNeuron,costSynapse);
          netObj.setAge(0);
          netObj.setCount(1);
          System.out.print("add " + netIndex + "-th net\n");
          parents.add(netObj);
        }
        JOptionPane.showMessageDialog(null,"Init done",
            "Init done",
            JOptionPane.PLAIN_MESSAGE);

        // initialization with only one special net
        if(parents.size() == 0){
          obj = this.getSingleInitialStructureFromXMLfile();
          if(obj ==  null)
          {
            obj = this.getEmptyInitialStructure();
            this.deactivateFileInit();
          }
        }
        else
        {
          return;
        }
      }
      else
      {
        // initialization with only one special net
        obj = this.getSingleInitialStructureFromXMLfile();
        if(obj ==  null)
        {
          obj = this.getEmptyInitialStructure();
          this.deactivateFileInit();
        }
      }
    }
    else
    {
      obj = this.getEmptyInitialStructure();
    }

    obj.setOutPerf(java.lang.Double.NEGATIVE_INFINITY,costNeuron,costSynapse);
    obj.setAge(0);
    obj.setCount(this.popSize);
    parents.add(obj);

  }


  /** 
   * Creates the initial evoObject from a given XML-file.
   * This structure is the start point for the evolution process.
   * @param  none
   * @return EvoObject obj
   */
  public EvoObject getSingleInitialStructureFromXMLfile(String path, int gen, int pop, int idx){
    EvolutionSaxHandler xml = new EvolutionSaxHandler();
    Net net;
    NeuronList nl;
    EvoObject obj = new EvoObject(this.lastNetPIN++, this.uniRand, this.gaussRand);

    if(initialFile.canRead() == false)
    {
      return null;
    }

    net = xml.readNet(path, gen, pop, idx);

    if(net == null)
    {
      System.out.print("Net is null\n");
      return null;
    }

    /* set synapse mode */
    this.synapseMode = net.getSynapseMode();

    /* set new number of input neurons */
    nl = net.getInputNeurons();
    this.inpNeurons = nl.size();
    /* set new number of output neurons */
    nl = net.getOutputNeurons();
    this.outNeurons  = nl.size();
    /* set transfer function  */  
    this.transferfunction = net.getTransferfunction();

    obj.setNet(net);
    obj.setOutPerf(java.lang.Double.NEGATIVE_INFINITY,costNeuron,costSynapse);
    obj.setAge(0);
    obj.setCount(1);

    return obj;
  }


  /** 
   * Creates the initial evoObject from a given XML-file.
   * This structure is the start point for the evolution process.
   * @param  none
   * @return EvoObject obj
   */
  public EvoObject getSingleInitialStructureFromXMLfile(){
    EvolutionSaxHandler xml = new EvolutionSaxHandler();
    Net net;
    NeuronList nl;
    EvoObject obj = new EvoObject(this.lastNetPIN++,this.uniRand, this.gaussRand);

    if(initialFile.canRead() == false)
    {
      return null;
    }

    net = xml.readNet(this.initialFile.getPath(),
        this.initialGenNmb,
        this.initialPopNmb,
        this.initialIdxNmb);


    if(net == null)
    {
      System.out.print("here I am\n");
      return null;
    }

    /* set synapse mode */
    this.synapseMode = net.getSynapseMode();

    /* set new number of input neurons */
    nl = net.getInputNeurons();
    this.inpNeurons = nl.size();
    /* set new number of output neurons */
    nl = net.getOutputNeurons();
    this.outNeurons  = nl.size();
    /* set transfer function  */  
    this.transferfunction = net.getTransferfunction();

    obj.setNet(net);
    obj.setOutPerf(java.lang.Double.NEGATIVE_INFINITY,costNeuron,costSynapse);
    obj.setAge(0);
    obj.setCount(this.popSize);

    return obj;
  }



  /** 
   * Creates the initial evoObject from the current parameter values -
   * acoording to number of input and  output neurons, neuron type, transfer function.
   * @param  none
   * @return EvoObject obj
   */
  public EvoObject getEmptyInitialStructure(){
    Net net = new Net();
    Neuron neuron;
    EvoObject obj = null; 

    if((inpNeurons > 0) || (outNeurons > 0))
    {
      obj = new EvoObject(this.lastNetPIN++,this.uniRand, this.gaussRand);

      /* set synapse mode*/
      net.setSynapseMode(this.synapseMode);

      /* add input neurons */
      for(int i = 0; i < this.inpNeurons; i++)
      {
        neuron = net.addNeuron(0,0,0,ProcessMode.DYNAMIC,NeuronType.INPUT);
        neuron.setKappa(0.0);
        neuron.setAlpha(alphaInitial);
        neuron.setBeta(betaInitial);
        neuron.setGamma(gammaInitial);
        neuron.setDelta(deltaInitial);
      }

      /* output neurons */
      for(int i = 0; i < this.outNeurons; i++)
      {
        neuron = net.addNeuron(0,0,0,ProcessMode.DYNAMIC,NeuronType.OUTPUT);
        neuron.setKappa(0.0);
        neuron.setAlpha(alphaInitial);
        neuron.setBeta(betaInitial);
        neuron.setGamma(gammaInitial);
        neuron.setDelta(deltaInitial);
      }

      /* set transferfunction */
      net.setTransferfunction(this.transferfunction);

      /* set homeokinese default parameter  */
      double modFreq = 0.0;
      double modAmpl = 0.0;
      double learnRate = 0.0;
      double velOffSet = 0.0;
      Vector homeoKineseParam = new Vector();
      homeoKineseParam.add(0,new Double(modFreq));
      homeoKineseParam.add(1,new Double(modAmpl));
      homeoKineseParam.add(2,new Double(learnRate));
      homeoKineseParam.add(3,new Double(velOffSet));
      net.setProperties(homeoKineseParam); 


      /* set evoObject parameter */
      obj.setOutPerf(java.lang.Double.MIN_VALUE,costNeuron,costSynapse);
      obj.setNet(net);
      obj.setAge(0);
      obj.setCount(this.popSize);
    }
    return obj;
  }



  /* repoduction, variation, selection operators */

  /** 
   * Implementation of the reproduction operator for one population.
   * The procedure deletes the current offspring list and  copies 
   * each object of the parent list to the offspring list. 
   * @param  none
   * @return none
   */
  public void reproduction(){
    this.offspring.clear();
    EvoObjectList temp = new EvoObjectList();
    EvoObject seed = null;

    // offspring is simply the count-times copy of the parent
    for(this.parents.start(); this.parents.hasMore(); this.parents.next())
    {
      /* parent gets older */
      seed = this.parents.object();
      seed.incAge();


      if(seed.getCount() > 0)
      {
        for(int i=0;i< seed.getCount();i++)
        {
          EvoObject newLife = new EvoObject(seed,this.lastNetPIN++, this.uniRand, this.gaussRand); 
          newLife.setOutPerf(java.lang.Double.MIN_VALUE,costNeuron,costSynapse);
          newLife.setAge(0);
          newLife.setCount(0);
          offspring.add(newLife);
        }
        seed.setCount(0);
        temp.add(seed);
      };

    };

    this.parents.clear();
    for(temp.start(); temp.hasMore(); temp.next())
    {
      this.parents.add(temp.object());
    }
    temp.clear();


  }

  private void variationConventional(EvoObject obj)
  {
    /* real-value mutation */
    if(changeWeightProb > 0.0)
    {
      obj.weightVar(changeWeightProb,changeWeightIntens,limitWeight);
    }


    if(changeBiasProb > 0.0){
      obj.biasVar(changeBiasProb,changeBiasIntens,limitBias);
    }

    if(changeDecayProb > 0.0){
      obj.decayVar(changeDecayProb,changeDecayIntens,limitDecay);
    }

    /* combinatorial mutation */
    if(insertSynProb > 0.0){
      obj.insSyn(this.insertSynProb, this.changeWeightIntens,
          synapseInsertionMode);
    }

    if(insertNeuronProb > 0.0){

      obj.insNeu(insertNeuronProb,
          this.changeWeightIntens,
          connectivity,
          maxHiddenNeurons,
          synapseInsertionMode,
          alphaInitial,
          betaInitial,
          gammaInitial,
          deltaInitial);
    }

    if(deleteSynProb > 0.0){
      obj.delSyn(deleteSynProb);
    }

    if(deleteNeuronProb > 0.0){
      obj.delNeu(deleteNeuronProb);
    }

    if(alphaProbability > 0.0){
      obj.alphaVar(this.alphaProbability,this.alphaVariation,this.alphaMax,
          this.alphaMin);
    }
    if(betaProbability > 0.0){
      obj.betaVar(this.betaProbability,this.betaVariation,this.betaMax,
          this.betaMin);
    }
    if(gammaProbability > 0.0){
      obj.gammaVar(this.gammaProbability,this.gammaVariation,this.gammaMax,
          this.gammaMin);
    }
    if(deltaProbability > 0.0){
      obj.deltaVar(this.deltaProbability,this.deltaVariation,this.deltaMax,
          this.deltaMin);
    }
    obj.cleanUp();
  }

  private void variationSRN(EvoObject obj)
  {
    Net net = obj.getNet();
    /* real-value mutation */
    if(changeWeightProb > 0.0)
    {
      // toggles if prob < changeWeightProb
      SRNVariation.changeWeight(net,changeWeightProb);
    }

    if(changeBiasProb > 0.0)
    {
      // can be kept (17.02.2006)
      obj.biasVar(changeBiasProb,changeBiasIntens,limitBias);
    }

    if(changeDecayProb > 0.0)
    {
      // can be kept (17.02.2006)
      obj.decayVar(changeDecayProb,changeDecayIntens,limitDecay);
    }

    /* combinatorial mutation */
    if(insertSynProb > 0.0)
    {
      // changed 17.02.2006
      SynapseList sl = obj.insSyn(this.insertSynProb, this.changeWeightIntens,
          synapseInsertionMode);
      // calls dales mode or others
      SRNVariation.insertSynapse(net, sl, synapseInsertionMode);
    }

    if(insertNeuronProb > 0.0)
    {
      Vector returnValue = obj.insNeu(insertNeuronProb,
          this.changeWeightIntens,
          connectivity,
          maxHiddenNeurons,
          synapseInsertionMode,
          alphaInitial,
          betaInitial,
          gammaInitial,
          deltaInitial);

      NeuronList neurons   = ((NeuronList)returnValue.elementAt(0));
      SynapseList synapses = ((SynapseList)returnValue.elementAt(1));

      // nothing
      for(int index=0; index < neurons.size(); index++)
      {
        Neuron n = (Neuron)neurons.get(index);

        SRNVariation.insertNeuron(net, n);
      }
      SRNVariation.insertSynapse(net, synapses, synapseInsertionMode);
    }

    if(deleteSynProb > 0.0){
      // checked 18.2.2006
      obj.delSyn(deleteSynProb);
    }

    if(deleteNeuronProb > 0.0){
      // checked 18.2.2006
      obj.delNeu(deleteNeuronProb);
    }

    if(alphaProbability > 0.0){
      // checked 18.2.2006
      SRNVariation.setAlpha(net,
          obj.varVarGauss(
            SRNVariation.getAlpha(),
            alphaProbability,
            alphaVariation,
            alphaMax,
            alphaMin));
    }

    if(betaProbability > 0.0){
      // checked 18.2.2006
      SRNVariation.setBeta(net,
          obj.varVarGauss(
            SRNVariation.getBeta(),
            betaProbability,
            betaVariation,
            betaMax,
            betaMin));
    }

    if(gammaProbability > 0.0){
      // checked 18.2.2006
      SRNVariation.setGamma(net,
          obj.varVarGauss(
            SRNVariation.getGamma(),
            gammaProbability,
            gammaVariation,
            gammaMax,
            gammaMin));
    }

    if(deltaProbability > 0.0){
      // checked 18.2.2006
      SRNVariation.setDelta(net,
          obj.varVarGauss(
            SRNVariation.getDelta(),
            deltaProbability,
            deltaVariation,
            deltaMax,
            deltaMin));
    }

    obj.removeNeuronsWithoutInputSynapses();
  }
  /** 
   * Implementation of the variation operator for one population.
   * Mutations are insert, delere neurons and synapsis and the variation
   * of real-value parameters, like weights, decay and bias. 
   * In fact this just the variation of the conevtional neuron type.
   * @param  none
   * @return none
   */
  public void variation()
  {
    EvoObject obj;
    switch(this.synapseMode.mode())
    {
      case SynapseMode.SYNAPSE_MODE_CONVENTIONAL:
        for(this.offspring.start(); this.offspring.hasMore(); this.offspring.next())
        {
          obj = this.offspring.object();
          variationConventional(obj);
        }
        break;
      case SynapseMode.SYNAPSE_MODE_DYNAMIC:
        for(this.offspring.start(); this.offspring.hasMore(); this.offspring.next())
        {
          obj = this.offspring.object();
          variationSRN(obj);
        }
        break;
    }
    return;
  }

  public void selection(){
    double sqr_varPerf, maxPerf, birth_sum, norm, birthRate, perf; 
    int i, nmbOffsprings;
    EvoObjectList temp = new EvoObjectList();
    EvoObject obj  = null;

    maxPerf     = this.bestPerformance();
    sqr_varPerf = this.sqrtVarPerformance();
    birth_sum   = this.birthSum();

    // normalization with the average population size
    norm = ((double) this.popSize) / birth_sum;

    for(parents.start();parents.hasMore();parents.next())
    {
      obj   = parents.object();
      perf  = obj.getPerformance();

      birthRate = java.lang.Math.exp(-this.birthGamma*((maxPerf - perf)/sqr_varPerf))*norm;
      nmbOffsprings = this.poissonRand.nextPoisson(birthRate);
      obj.setCount(nmbOffsprings);
      EvoObject o = new EvoObject(obj,this.uniRand, this.gaussRand);
      temp.sortedAdd(o);
    }


    for(offspring.start();offspring.hasMore();offspring.next())
    {
      obj   = offspring.object();
      perf  = obj.getPerformance();

      birthRate = java.lang.Math.exp(-this.birthGamma*(maxPerf - perf)/sqr_varPerf)*norm;
      nmbOffsprings = this.poissonRand.nextPoisson(birthRate);
      obj.setCount(nmbOffsprings);
      EvoObject o = new EvoObject(obj,this.uniRand, this.gaussRand);
      temp.sortedAdd(o);
    }

    parents.clear();
    offspring.clear();
    i = 0;
    for(temp.start(); temp.hasMore(); temp.next())
    {

      obj = temp.object();

      i++;
      if((i <= this.saveNBest) && 
          (obj.getCount() < 1)    )
      {
        obj.setCount(1);
      }

      if(obj.getCount() > 0)
      {
        // EvoObject o = new EvoObject(obj,this.lastNetPIN++,this.uniRand, this.gaussRand);
        parents.add(obj);
      }

    }
    temp.clear();

    System.gc();
  }





  public void setRandomSeed(int randomSeed)
  {
    this.randomSeed = randomSeed;
  }

  public void run(){
    this.evaluation();
  }


  public void evaluation(){
    if(this.evo.getEvalType() == EvaluationType.MIN_SYNC)
    {
      simpleEvaluation();
      return;
    }
    if(this.evo.getEvalType() == EvaluationType.MAX_SYNC)
    {
      simpleEvaluation();
      return;
    }
    if(this.evo.getEvalType() == EvaluationType.ONE2ONE)
    {
      simpleEvaluation();
      return;
    }
    if(this.evo.getEvalType() == EvaluationType.ONE2MANY)
    {
      multiEvaluation();
      return;
    }
    System.out.println("attention: no evaluation at all");
  }


  private void multiEvaluation(){
    EvoObject obj;
    double dblVal;
    // int indyCount;

    int countSim = this.evo.getNumberEvalTasks();
    INetCommunication singleServer = null;
    PrintWriter       singleSimInput = null;
    BufferedReader    singleSimOutput = null; 
    EvalObjectSlot    sim  = null;
    Vector            simVec = new Vector();

    Vector myTasks = new Vector();
    Thread task = null;


    int i;

    // send to all simulators the new generation
    for(i=0; i < countSim; i++)
    { 
      singleSimInput = (PrintWriter) simInputVec.elementAt(i);

      // evaluation of a generation starts
      singleSimInput.println("NEXT_GEN");

      // send life span and constants
      singleSimInput.println(Integer.toString(this.getWarmUpSpan()));
      singleSimInput.println(Integer.toString(this.getEvalTestSteps()));
      System.out.println("testSTEPS: " + this.getEvalTestSteps() );
      singleSimInput.println(Double.toString(this.getC0()));
      singleSimInput.println(Double.toString(this.getC1()));
      singleSimInput.println(Double.toString(this.getC2()));
      singleSimInput.println(Double.toString(this.getC3()));
      singleSimInput.println(Integer.toString(this.randomSeed));
    }

    // set the evaluation state
    this.allIndies = this.parents.size() + this.offspring.size();
    this.indyCount = 0;

    this.startParallEval();
    for(i=0; i < countSim; i++){
      singleSimOutput = (BufferedReader) simOutputVec.elementAt(i);
      singleSimInput =  (PrintWriter)    simInputVec.elementAt(i);
      sim = new EvalObjectSlot(this, singleSimInput, singleSimOutput);

      task = new Thread(sim);
      myTasks.add(task);
      task.start();
    }

    for(i = 0; i < myTasks.size(); i++)
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




    // send to all simulators the end of population / generation
    for(i=0; i < countSim; i++)
    { 
      singleSimInput = (PrintWriter) simInputVec.elementAt(i);
      // all networks are evaluated form the other side 
      // send a the information of the end of the current evaluation 
      singleSimInput.println("END_POP");

    }


  }


  public void startParallEval(){
    this.evalIndex = 0;
  }


  public EvoObject nextEvalObject(){
    this.lock.getWriteLock();

    EvoObject obj = null;
    int i = 0; 

    // System.out.println("evalIndex = " + this.evalIndex);

    for(this.parents.start(); this.parents.hasMore(); this.parents.next())
    {
      if(this.evalIndex == i)
      {
        obj = this.parents.object();
        this.indyCount = this.evalIndex + 1;
        popParLoadSavePanel.actionPerformed(new ActionEvent(this,40,"NEW_EVAL"));
        this.evalIndex++;
        this.lock.releaseLock();
        return obj;
      }
      i++;
    }
    for(this.offspring.start(); this.offspring.hasMore(); this.offspring.next())
    {
      if(this.evalIndex == i)
      {
        obj = this.offspring.object();
        this.indyCount = this.evalIndex + 1;
        popParLoadSavePanel.actionPerformed(new ActionEvent(this,40,"NEW_EVAL"));
        this.evalIndex++;
        this.lock.releaseLock();
        return obj;
      }
      i++;
    }
    this.lock.releaseLock();
    return obj;
  }


  private void simpleEvaluation(){
    EvoObject obj;
    double dblVal;
    int indyCount;

    // evaluation of a generation starts
    this.simInput.println("NEXT_GEN");
    // send life span and constants
    simInput.println(Integer.toString(this.getWarmUpSpan()));
    simInput.println(Integer.toString(this.getEvalTestSteps()));
    simInput.println(Double.toString(this.getC0()));
    simInput.println(Double.toString(this.getC1()));
    simInput.println(Double.toString(this.getC2()));
    simInput.println(Double.toString(this.getC3()));
    simInput.println(Integer.toString(this.randomSeed));

    // set the evaluation state
    this.allIndies = this.parents.size() + this.offspring.size();
    this.indyCount = 0;

    for(this.parents.start(); this.parents.hasMore(); this.parents.next())
    {
      obj = this.parents.object();

      // give to the other side, that a new network is comming 
      this.simInput.println("NEXT_INDY");

      // send the dtd grammar followed by the network in xml-format
      //System.out.println(obj.getGrammarNetString());
      simInput.println(obj.getGrammarNetString());

      this.indyCount++;
      popParLoadSavePanel.actionPerformed(new ActionEvent(this,40,"NEW_EVAL"));

      try{
        // receive the output performance, coming from the other side
        String str = this.simOutput.readLine();
        dblVal = Double.parseDouble(str);
        // save system performance in the current evoObject structure 
        // system performance calculation included
        obj.setOutPerf(dblVal, this.costNeuron, this.costSynapse);
      }
      catch (IOException e)
      { 
        System.out.println(this.getName() + " can't read parent fitness value");
      }

      // send the information that as next the calculates system performance is coming
      this.simInput.println("SYS_PERF");
      // send the system performance
      simInput.println(Double.toString(obj.getPerformance()));

    }

    // do exactly the same for the offspring individuals
    for(this.offspring.start(); this.offspring.hasMore(); this.offspring.next())
    {
      obj = this.offspring.object();

      this.simInput.println("NEXT_INDY");
      simInput.println(obj.getGrammarNetString());

      this.indyCount++;
      popParLoadSavePanel.actionPerformed(new ActionEvent(this,40,"NEW_EVAL"));

      try{
        String str = this.simOutput.readLine();
        dblVal = Double.parseDouble(str);
        obj.setOutPerf(dblVal, this.costNeuron, this.costSynapse);
      }
      catch (IOException e)
      { 
        System.out.println(this.getName() + " can't read offspring fitness value");
      }

      this.simInput.println("SYS_PERF");
      simInput.println(Double.toString(obj.getPerformance()));

    }

    // all networks are evaluated form the other side 
    // send a the information of the end of the current evaluation 
    this.simInput.println("END_POP");

  }


  public void monitoring(){
    ;;
  }


  // population manipulations 

  /**
   * decrease the population size of this population to 'size'. 
   * This is basically done by the remove of offsprings starting 
   * with that parent, which has lowest perfomance. Notice, that 
   * this function has to be called after selection and before 
   * reproduction operator.
   * @param  int size
   * @return none
   */
  public void cutPopToSize(int maxSize){
    int count = 0;
    int tmpCount = 0;
    int currentSize = 0;
    EvoObject obj_old = null;
    EvoObject obj_new = null;
    EvoObjectList temp = new EvoObjectList();
    boolean moreParents = true;

    for(this.parents.start(); (this.parents.hasMore() && moreParents); this.parents.next()){
      obj_old = obj_new;
      obj_new = this.parents.object();
      count  = obj_new.getCount();

      if((currentSize + count + 1) < maxSize){
        // more Parents
        temp.sortedAdd(obj_new);
        currentSize = currentSize + count + 1;
        moreParents = true;
      }else if ((currentSize + count + 1) == maxSize){
        // ready! no more parents
        temp.sortedAdd(obj_new);
        currentSize = currentSize + count + 1;
        moreParents = false;
      }else if ((currentSize + 1) == maxSize){
        // there is now one parent, which would survive without an offspring
        // this should be avoided, so, last parent gets one offspring more 
        // and this parent dies;
        if(obj_old != null){
          tmpCount = obj_old.getCount();
          obj_old.setCount(tmpCount + 1);
        }else{
          System.out.println("evaluation type MIN_SYNC:");
          System.out.println("there is something wrong, can't cut the populations to the requiered size");
        };
        moreParents = false;
      }else{
        // the next parent will suvive, but its offspring are too many
        tmpCount = maxSize - currentSize - 1;
        if(tmpCount < 1){
          System.out.println("evaluation type MIN_SYNC:");
          System.out.println("there is something wrong,");
          System.out.println("can not cut the populations to the requiered size");  
        }else{
          obj_new.setCount(tmpCount);
          temp.sortedAdd(obj_new); 
        }
        moreParents = false;
      };
    };
    this.parents.clear();
    for(temp.start(); temp.hasMore(); temp.next()){
      this.parents.sortedAdd(temp.object());
    }
    temp.clear();
  }

  /**
   * increase the population size of this population to 'size'. 
   * This is basically done by the increase of offsprings starting 
   * with that parent, which has highest perfomance. Notice, that 
   * this function has to be called after selection and before 
   * reproduction operator.
   * @param  int size
   * @return none
   */
  public void incPopToSize(int minSize){
    int count = 0;
    int total = 0;
    boolean moreParents = true;
    EvoObject obj; 

    total = getCurrentPopSize();

    if(total == minSize){
      return;
    }

    if(total > minSize){
      cutPopToSize(minSize);  
      return;
    }

    while(total < minSize)
    {
      for(this.parents.start(); this.parents.hasMore(); this.parents.next()){
        obj = parents.object();

        if(total < minSize)
        {
          count = obj.getCount();
          obj.setCount(count + 1);
          total++;
        }
        else
        {
          return;
        }
      }
    }
    return;
  }



  /* statistics */


  /** 
   * Logging of all relevant data of the current population state. (Mainly, for 
   * the monitoring of the evolution dynamics.)
   * @param  none
   * @return none
   */
  public void popDynUpdate(){
    PopState popState = new PopState();
    popState.fillPopState(this);
    this.popDyn.add(popState);
  }

  private void popDynInit(){
    this.popDyn.clear();
  }


  public PopDyn getPopDyn(){
    return (this.popDyn);
  }

  private double birthSum(){
    double sqr_varPerf, maxPerf, birth_sum; 
    EvoObject obj = null;

    maxPerf     = this.bestPerformance();
    sqr_varPerf = this.sqrtVarPerformance();


    birth_sum = 0.0;

    for(this.parents.start(); this.parents.hasMore(); this.parents.next())
    {
      obj = parents.object();
      birth_sum = birth_sum + 
        (java.lang.Math.exp(-this.birthGamma*(maxPerf - obj.getPerformance() )/sqr_varPerf) ); 
    }

    for(this.offspring.start(); this.offspring.hasMore(); this.offspring.next())
    {

      obj = offspring.object();
      birth_sum = birth_sum + 
        (java.lang.Math.exp(-this.birthGamma*(maxPerf - obj.getPerformance())/sqr_varPerf)); 
    }

    return birth_sum;
  }


  /**
   * gets the worst/lowest perfomance value of the whole population (parent and offspring). 
   * @param  none
   * @return double  worstPerf
   */
  public double worstPerformance(){
    double worse = java.lang.Double.POSITIVE_INFINITY;
    double value;

    for(parents.start(); parents.hasMore(); parents.next())
    {
      value = parents.object().getPerformance();
      if(value <  worse)
      {
        worse = value;
      }
    }

    for(offspring.start(); offspring.hasMore(); offspring.next())
    {
      value = offspring.object().getPerformance();
      if(value <  worse)
      {
        worse = value;
      }
    }

    return worse;
  }

  /**
   * gets the best/greatest system  perfomance value of the whole population (parent and offspring). 
   * @param  none
   * @return double  bestPerf
   */
  public double bestPerformance(){
    double better = java.lang.Double.NEGATIVE_INFINITY;
    double value;

    for(parents.start(); parents.hasMore(); parents.next())
    {
      value = parents.object().getPerformance();
      if(value >  better)
      {
        better = value;
      }
    }

    for(offspring.start(); offspring.hasMore(); offspring.next())
    {
      value = offspring.object().getPerformance();
      if(value >  better)
      {
        better = value;
      }
    }

    return better;
  }


  /**
   * gets the best/greatest output  perfomance value of the whole population (parent and offspring). 
   * @param  none
   * @return double  bestPerf
   */
  public double bestOutPerf(){
    double better = java.lang.Double.NEGATIVE_INFINITY;
    double value;

    for(parents.start(); parents.hasMore(); parents.next())
    {
      value = parents.object().getOutPerf();
      if(value >  better)
      {
        better = value;
      }
    }

    for(offspring.start(); offspring.hasMore(); offspring.next())
    {
      value = offspring.object().getOutPerf();
      if(value >  better)
      {
        better = value;
      }
    }

    return better;
  }

  /**
   * gets the average over the s-perfomance values of the whole population (parent and offspring). 
   * @param  none
   * @return double  mean
   */
  public double avgPerformance(){
    double sum;
    int count;

    sum = 0.0;
    count = 0;
    for(parents.start(); parents.hasMore(); parents.next())
    {
      sum  = sum + parents.object().getPerformance();
      count++;
    }

    for(offspring.start(); offspring.hasMore(); offspring.next())
    {
      sum = sum + offspring.object().getPerformance();
      count++;
    }
    return ( sum / ((double) count) );
  }

  /**
   * gets the variation  over the s-perfomance values of the whole population (parent and offspring). 
   * @param  none
   * @return double var
   */
  public double sqrtVarPerformance(){
    double sum, mean, value;
    int count;

    sum = 0.0;
    count = 0;
    mean = this.avgPerformance();

    for(parents.start(); parents.hasMore(); parents.next())
    {
      value = parents.object().getPerformance();
      sum = sum + ( (value - mean)*(value - mean) );
      count++;
    }

    for(offspring.start(); offspring.hasMore(); offspring.next())
    {
      value = offspring.object().getPerformance();
      sum = sum + ( (value - mean)*(value - mean) );
      count++;
    }
    return java.lang.Math.sqrt( (sum / ((double) (count - 1))) );
  }



  /**
   * gets the average over the o-perfomance values of the whole population (parent and offspring). 
   * @param  none
   * @return double  mean
   */
  public double avgOutPerf(){
    double sum;
    int count_all, count;

    sum = 0.0;
    count = 0;
    for(parents.start(); parents.hasMore(); parents.next())
    {
      sum = sum + parents.object().getOutPerf();
      count++;
    }

    for(offspring.start(); offspring.hasMore(); offspring.next())
    {
      sum = sum +  offspring.object().getOutPerf();
      count++;
    }
    return ( sum / ((double) count));
  }

  /**
   * gets the square variation  over the o-perfomance values of the whole population (parent and offspring). 
   * @param  none
   * @return double var
   */
  public double sqrtVarOutPerf(){
    double sum, mean, value;
    int count;

    sum = 0.0;
    count = 0;
    mean = this.avgOutPerf();

    for(parents.start(); parents.hasMore(); parents.next())
    {
      value = parents.object().getOutPerf();
      sum = sum + ( (value - mean)*(value - mean) );
      count++;
    }

    for(offspring.start(); offspring.hasMore(); offspring.next())
    {
      value = offspring.object().getOutPerf();
      sum = sum + ( (value - mean)*(value - mean) );
      count++;
    }
    return java.lang.Math.sqrt(( sum / ((double) (count - 1) )));
  }




  /**
   * Average number of hidden neurons in the population after selection.
   * @param  none
   * @return double avg
   */
  public double avgNmbHidden(){
    double     avg        = 0.0;
    EvoObject  obj        = null;
    Net        net        = null;
    NeuronList hiddens    = null;

    int        sumHiddens = 0;
    int        count      = 0;

    for(parents.start(); parents.hasMore(); parents.next())
    {
      obj = parents.object();

      net = obj.getNet();
      hiddens = net.getHiddenNeurons();

      if(hiddens != null)
      {
        sumHiddens = sumHiddens + hiddens.size();
        count++;
      }
    }

    avg = ((double) sumHiddens) / ((double) count);
    return avg;
  }

  /**
   * deviation of the number of hiddens in the population after selection
   * @param  none
   * @return double avg
   */
  public double varNmbHidden(){
    double     var        = 0.0;
    EvoObject  obj        = null;
    Net        net        = null;
    NeuronList hiddens    = null;

    double     sumHiddens = 0;
    int        count      = 0;

    double     avgHiddens = this.avgNmbHidden();

    for(parents.start(); parents.hasMore(); parents.next())
    {
      obj = parents.object();
      net = obj.getNet();
      hiddens = net.getHiddenNeurons();

      if(hiddens != null)
      {
        sumHiddens = sumHiddens + 
          ( (hiddens.size() - avgHiddens )*(hiddens.size() - avgHiddens) );
        count++;
      }
    }
    var =  sumHiddens / ( (double) (count - 1) );
    return var;
  }


  /**
   * Average number of synapses in the population after selection.
   * @param  none
   * @return double avg
   */
  public double avgNmbSynapses(){
    double     avg        = 0.0;
    EvoObject  obj        = null;
    Net        net        = null;

    int        sumSynapses = 0;
    int        nmbSyn      = 0;
    int        count       = 0;

    for(parents.start(); parents.hasMore(); parents.next())
    {
      obj = parents.object();

      net    = obj.getNet();
      nmbSyn = net.getSynapseCount();

      sumSynapses = sumSynapses + nmbSyn;
      count++;
    }

    avg = ((double) sumSynapses) / ((double) count);
    return avg;
  }

  /**
   * deviation of the number of synapses in the population after selection
   * @param  none
   * @return double avg
   */
  public double varNmbSynapses(){
    double var = 0.0;
    EvoObject  obj        = null;
    Net        net        = null;

    double     sumSynapses = 0.0;
    int        nmbSyn      = 0;
    int        count       = 0;

    double     meanNmbSyn  = this.avgNmbSynapses();

    for(parents.start(); parents.hasMore(); parents.next())
    {
      obj = parents.object();
      net    = obj.getNet();
      nmbSyn = net.getSynapseCount();

      sumSynapses = sumSynapses + ( (nmbSyn - meanNmbSyn)*(nmbSyn - meanNmbSyn) );
      count++;
    }

    var = sumSynapses / ((double) (count - 1));

    return var;
  }


  /**
   * Average age of the population after selection.
   * @param  none
   * @return double avgAge
   */
  public double avgAge(){
    double     avg        = 0.0;
    EvoObject  obj        = null;

    int        sumAge = 0;
    int        age      = 0;
    int        count       = 0;

    for(parents.start(); parents.hasMore(); parents.next())
    {
      obj = parents.object();
      age = obj.getAge();

      sumAge = sumAge + age;
      count++;
    }

    avg = ((double) sumAge) / ((double) count);
    return avg;
  }

  /**
   * Deviation of age of the population after selection.
   * @param  none
   * @return double avg
   */
  public double varAge(){
    double var = 0.0;
    EvoObject  obj        = null;

    double     sumAge = 0.0;
    int        age    = 0;
    int        count  = 0;

    double     meanAge  = this.avgAge();

    for(parents.start(); parents.hasMore(); parents.next())
    {
      obj = parents.object();
      age    = obj.getAge();

      sumAge = sumAge + ( (age - meanAge)*(age - meanAge) );
      count++;
    }
    var = sumAge / ((double) (count - 1) );
    return var;
  }





  /* get and set functions */


  public int getEvalIndex(){
    return (this.indyCount);
  }

  public int getEvalIndies(){
    return (this.allIndies);
  }


  /**
   *
   */
  public void setIcon(ImageIcon icon){
    this.icon = icon;
  }

  public ImageIcon getIcon(){
    return this.icon;
  }




  /** 
   * the return value n is the number of networks, which  pass to the next generation per definition,
   * n >= 0.
   * @param  none
   * @return int
   */
  public int getSaveNBest(){
    return this.saveNBest;
  }

  /** 
   * set the number of networks, which  pass to the next generation per definition,
   * n >= 0.
   * @param  int n
   * @return none
   */
  public  void setSaveNBest(int n){
    if(n < 0)
    {
      this.saveNBest = 0;
    }
    else
    {
      this.saveNBest = n;
    }


  }




  /** 
   * gives the population initializing mode. 
   * If return value is 'true'  the population is initialized by a
   * structure stored in a XML file. If return value is 'false' 
   * population is initialized by the empty structure, wich contains 
   * only input and output neurons.
   * @param  none
   * @return boolean
   */
  public boolean fileInitActive(){
    return fileInit;
  };

  /**
   * sets the initializing mode to 'true'. 
   * If return value is 'true'  the population is initialized by a
   * structure stored in a XML file. If return value is 'false' 
   * population is initialized by the empty structure, wich contains 
   * only input and output neurons.
   * @param  none
   * @return none
   */
  public void activateFileInit(){
    fileInit = true;
    return;
  }

  /**
   * sets the initializing mode to 'false'. 
   * If return value is 'true'  the population is initialized by a
   * structure stored in a XML file. If return value is 'false' 
   * population is initialized by the empty structure, wich contains 
   * only input and output neurons.
   */
  public void deactivateFileInit(){
    fileInit = false;
    return;
  }


  /** 
   * get the birth gamma value.
   * This value determines the stochastic selection process.
   * @param  none
   * @return double value
   */
  public double getBirthGamma(){
    return birthGamma;
  }

  /** 
   * set the birth gamma value.
   * This value determines the stochastic selection process.
   * @param  double value
   * @return none
   */
  public void setBirthGamma(double value){
    if(value < 0.0)
    {
      birthGamma = 0.0;
      return;
    }
    birthGamma = value;
    return ;
  }

  public Net getBestNet(){
    EvoObject obj;
    double maxPerf = this.bestPerformance();

    for(parents.start(); parents.hasMore(); parents.next())
    {
      obj = parents.object();
      if(maxPerf <= obj.getPerformance())
      {
        return obj.getNet();
      }
    }
    return null;
  }


  public int getAgeOfBest(){
    EvoObject obj;
    double maxPerf = this.bestPerformance();

    for(parents.start(); parents.hasMore(); parents.next())
    {
      obj = parents.object();
      if(maxPerf <= obj.getPerformance())
      {
        return obj.getAge();
      }
    }
    return -1;
  }


  public int getAgeOfOldest(){
    EvoObject obj;
    int age = 0;

    for(parents.start(); parents.hasMore(); parents.next())
    {
      obj = parents.object();
      if(age <= obj.getAge())
      {
        age = obj.getAge();
      }
    }
    return age;
  }


  public EvoObject getOldestEvoObject(){
    EvoObject obj = null;
    EvoObject returnObj = null;
    int age = -1;

    for(parents.start(); parents.hasMore(); parents.next())
    {
      obj = parents.object();
      if(age < obj.getAge())
      {
        age = obj.getAge();
        returnObj = obj;
      }
    }
    return returnObj;
  }


  /** 
   * set the index of the initial structure - according to the
   * evolution-XML grammar.
   * @param  int idx
   * @return none
   */
  public void setInitialIdxNmb(int nmb){
    if(nmb < 0)
    {
      initialIdxNmb = -1;
    }
    else
    {
      initialIdxNmb = nmb;
    }
    return;
  }

  /** 
   * get the index of the initial structure - according to the 
   * evolution-XML grammar.
   * @param  none
   * @return int idxNmb
   */
  public int getInitialIdxNmb(){
    return initialIdxNmb;
  }


  /** 
   * set the generation number of the initial structure.
   * @param  int nmb
   * @return none
   */
  public void setInitialGenNmb(int nmb){
    if(nmb < 0)
    {
      initialGenNmb = 0;
    }
    else
    {
      initialGenNmb = nmb;
    }
    return;
  }

  /** 
   * get the generation number of the initial structure.
   * @param  none
   * @return int genNmb
   */
  public int getInitialGenNmb(){
    return initialGenNmb;
  }


  /** 
   * set the population number of the initial structure. -
   * which is supposed to be stored in a XML file.
   * @param  int nmb
   * @return none
   */
  public void setInitialPopNmb(int nmb){
    if(nmb < 0)
    {
      initialPopNmb = 0;
    }
    else
    {
      initialPopNmb = nmb;
    }
    return;
  }

  /** 
   * get the population number of the initial structure. -
   * which is supposed to be stored in a XML file.
   * @param  none
   * @return int genNmb
   */
  public int getInitialPopNmb(){
    return initialPopNmb;
  }

  /** 
   * set the full file name of the initial structure.
   * @param  String name
   * @return none
   */
  public void setInitialFileName(String name){
    initialFile = new File(name);
    return ;
  }

  /** 
   * get the full file name of the initial structure.
   * @param  none
   * @return String name
   */
  public String getInitialFileName(){
    return initialFile.getPath();
  }


  /** 
   * get the number of input neurons for the nets.
   * @param  none
   * @return int  
   */
  public int getNmbInpNeu(){
    return inpNeurons;
  }

  /** 
   * set the number of input neurons for the nets.
   * @param  int nmb
   * @return none 
   */
  public void setNmbInpNeu(int nmb){
//    if(nmb < 1) 
//    {
//      inpNeurons = 1;
//    }
//    else
//    {
      inpNeurons = nmb;
//    }
    return;
  }

  /** 
   * get the number of output neurons for the nets.
   * @param  none
   * @return int  
   */
  public int getNmbOutNeu(){
    return outNeurons;
  }

  /** 
   * set the number of output neurons for the nets.
   * @param  int nmb
   * @return none 
   */
  public void setNmbOutNeu(int nmb){
    if(nmb < 1) 
    {
      outNeurons = 1;
    }
    else
    {
      outNeurons = nmb;
    }
    return;
  }



  /** 
   * set the maximal absolute value of a neuron bias.
   * @param  double value
   * @return none 
   */
  public void setBiasLim(double value ){
    if(value == 0) 
    {
      limitBias = 0.0;
      return;
    }
    if(value < 0.0)
    {
      limitBias = -1.0*value;
      return;
    }
    limitBias = value;
    return;

  }

  /** 
   * get the maximal absolute value of a neuron bias.
   * @param  none
   * @return double value 
   */
  public double getBiasLim(){
    return limitBias;
  }



  /** 
   * set the maximal absolute value of a neuron decay.
   * @param  double value
   * @return none 
   */
  public void setDecayLim(double value ){

    if((value >= 0.0) && (value < 1.0)) 
    {
      limitDecay = value;
    }
    else
    {
      limitDecay = 0.0;
    }
    return;
  }

  /** 
   * get the maximal absolute value of a neuron decay.
   * @param  none
   * @return double value 
   */
  public double getDecayLim(){
    return limitDecay;
  }


  /** 
   * set the maximal absolute value of a weight in the network.
   * @param  double value
   * @return none 
   */
  public void setWeightLim(double value ){
    if(value == 0) 
    {
      limitWeight = 0.0;
      return;
    }
    if(value < 0.0)
    {
      limitWeight = -1.0*value;
      return;
    }
    limitWeight = value;
    return;
  }

  /** 
   * get the maximal absolute value of a weight in the network.
   * @param  none
   * @return double value 
   */
  public double getWeightLim(){
    return limitWeight;
  }

  /** 
   * get the maximal number of hidden neurons in a network.
   * @param  none
   * @return int value 
   */
  public int getMaxHidNeuNmb(){
    return maxHiddenNeurons;
  }

  /** 
   * set the maximal number of hidden neurons in a network.
   * @param  int number
   * @return none
   */
  public void setMaxHidNeuNmb(int nmb){
    if(nmb < 0)
    {
      maxHiddenNeurons = 0;   
    }
    else
    {
      maxHiddenNeurons = nmb;
    }
    return;
  }








  /** 
   * get the value of the auxiliary constant c3.
   * @param  none
   * @return double value 
   */
  public double getC3(){
    return c3;
  }

  /** 
   * set the auxiliary constant c3.
   * @param  double value 
   * @return none
   */
  public void setC3(double value){
    c3 = value;
    return;
  }


  /** 
   * get the value of the auxiliary constant c2.
   * @param  none
   * @return double value 
   */
  public double getC2(){
    return c2;
  }

  /** 
   * set the constant auxiliary c2.
   * @param  double value 
   * @return none
   */
  public void setC2(double value){
    c2 = value;
    return;
  }

  /** 
   * get the value of the auxiliary constant c1.
   * @param  none
   * @return double value 
   */
  public double getC1(){
    return c1;
  }

  /** 
   * set the auxiliary constant c1.
   * @param  double value 
   * @return none
   */
  public void setC1(double value){
    c1 = value;
    return;
  }

  /** 
   * get the value of the auxiliary constant c0.
   * @param  none
   * @return double value 
   */
  public double getC0(){
    return c0;
  }

  /** 
   * set the auxiliary constant c0.
   * @param  double value 
   * @return none
   */
  public void setC0(double value){
    c0 = value;
    return;
  }


  /** 
   * set the cost of a single neuron.
   * @param  double value 
   * @return none
   */
  public void setCostNeu(double value){
    //    if(value < 0.0)
    //    {
    //      costNeuron = 0.0;
    //    }
    //    else
    //    {
    costNeuron = value;
    //    }
    return;
  }

  /** 
   * set the cost of a single synapse.
   * @param  double value 
   * @return none
   */
  public void setCostSyn(double value){
    //    if(value < 0.0)
    //    {
    //      costSynapse = 0.0;
    //    }
    //    else
    //    {
    costSynapse = value;
    //    }
    return;
  }



  /** 
   * get the cost of a single synapse.
   * @param  none
   * @return double value 
   */
  public double getCostSyn(){
    return costSynapse;
  }

  /** 
   * get the cost of a single neuron.
   * @param  none
   * @return double value 
   */
  public double getCostNeu(){
    return costNeuron;
  }

  /** 
   * get the used transfer function of the  population.
   * @param  none
   * @return transferfunction 
   */
  public Transferfunction getTransferfunction(){
    return this.transferfunction;
  }

  /** 
   * get the synapse mode of the  population.
   * @param  none
   * @return SynapseMode
   */
  public SynapseMode getSynMode(){
    return this.synapseMode;
  }

  /** 
   * set the transfer function for the whole population.
   * @param transferfunction  function
   * @return none
   */
  public void setTransferfunction(Transferfunction fct){
    this.transferfunction = fct;
    return; 
  }

  /** 
   * set the synapse mode for the whole population.
   * @param SynapseMode  mode
   * @return none
   */
  public void setSynMode(SynapseMode mode){
    synapseMode = mode;
    if(ctrlMutationPanel != null)
    {
      ctrlMutationPanel.actionPerformed(new ActionEvent(this,70,"SYNMODE_CHANGED"));
    }
    if(learningRuleClassLoader.getSelectedIndex() == -1)
    {
      learningRuleClassLoader.setSelectedLearningRule(0);
    }
    return;
  }


  /**
   *  get name of the population
   * @param none
   * @return String name
   */
  public String getName(){
    return popName;
  };


  /**
   *  set name of the population
   * @param String name
   * @return none
   */
  public void setName(String name){
    popName =  name;

    return;
  };





  /**
   * sets  intensity of the weight change.
   * @param    double delta
   * @return   none
   */
  public void setChangeWeightIntens(double delta){
    if(delta < 0.0)
    {
      changeWeightIntens = 0.0;
    }
    else
    {
      changeWeightIntens = delta;
    }
    return;
  }

  /**
   * return value is the intensity of the  weight change.
   * @param    none
   * @return   double delta
   */
  public double getChangeWeightIntens(){
    return changeWeightIntens;
  }





  /**
   * sets  the probability of weight change.
   * @param    double prob
   * @return   none
   */
  public void setChangeWeightProb(double prob){
    if(prob < 0.0)
    {
      changeWeightProb = 0.0;
    }
    else if (prob > 1.0)
    {
      changeWeightProb = 1.0;
    }
    else
    {
      changeWeightProb = prob;
    }
    return;
  }

  /**
   * return value is the probability of weight change.
   * @param    none
   * @return   double prob
   */
  public double getChangeWeightProb(){
    return changeWeightProb;
  }




  /**
   * sets  intensity of the neuron decay change.
   * @param    double delta
   * @return   none
   */
  public void setChangeDecayIntens(double delta){
    if(delta < 0.0)
    {
      changeDecayIntens = 0.0;
    }
    else
    {
      changeDecayIntens = delta;
    }
    return;
  }

  /**
   * return value is the intensity of the  neuron decay change.
   * @param    none
   * @return   double delta
   */
  public double getChangeDecayIntens(){
    return changeDecayIntens;
  }


  /**
   * sets  the probability of neuron decay change.
   * @param    double prob
   * @return   none
   */
  public void setChangeDecayProb(double prob){
    if(prob < 0.0) 
    {
      changeDecayProb = 0.0;
    }
    else if (prob > 1.0)
    {
      changeDecayProb = 1.0;
    }else
    {
      changeDecayProb = prob;
    }
    return;
  }

  /**
   * return value is the probability of neuron decay change.
   * @param    none
   * @return   double prob
   */
  public double getChangeDecayProb(){
    return changeDecayProb;
  }




  /**
   * sets  intensity of the neuron bias change.
   * @param    double delta
   * @return   none
   */
  public void setChangeBiasIntens(double delta){
    if(delta < 0.0)
    {
      changeBiasIntens = 0.0;
    }
    else
    {
      changeBiasIntens = delta;
    }
    return;
  }


  /**
   * return value is the intensity of the  neuron bias change.
   * @param    none
   * @return   double delta
   */
  public double getChangeBiasIntens(){
    return changeBiasIntens;
  }

  /**
   * sets  the probability of the change of a neuron bias.
   * @param    double prob
   * @return   none
   */
  public void setChangeBiasProb(double prob){
    if(prob < 0.0)
    {
      changeBiasProb = 0.0;
    }
    else if (prob > 1.0)
    {
      changeBiasProb = 1.0;
    }
    else
    {
      changeBiasProb = prob;
    }
    return;
  }

  /**
   * return value is the probability of the change of a neuron bias.
   * @param    none
   * @return   double prob
   */
  public double getChangeBiasProb(){
    return changeBiasProb;
  }









  /**
   * sets  the probability of the deletion of a connection
   * between two neurons.
   * @param    double prob
   * @return   none
   */
  public void setDelSynProb(double prob){
    if((prob < 0.0) || (prob > 1.0) )
    {
      deleteSynProb = 0.0;
    }
    else
    {
      deleteSynProb = prob;
    }
    return;
  };

  /**
   * sets the probability of the deletion of a neuron.
   * @param    double prob
   * @return   none
   */
  public void setDelNeuProb(double prob){
    if((prob < 0.0) || (prob > 1.0) )
    {
      deleteNeuronProb = 0.0;
    }
    else
    {
      deleteNeuronProb = prob;
    }
    return;
  };

  /**
   * sets the probability of the insertion of a new connection.
   * @param    double prob
   * @return   none
   */
  public void setInsSynProb(double prob){
    if(prob < 0.0)
    {
      insertSynProb = 0.0;
      return;
    }
    if(prob > 1.0)
    {
      insertSynProb = 1.0;
      return;
    }
    insertSynProb = prob;
    return;
  };

  /**
   * sets is the probability of the insertion of a new neuron.
   * @param    double prob
   * @return   none
   */
  public void setInsNeuProb(double prob){
    if(prob < 0.0) 
    {
      insertNeuronProb = 0.0;
      return;
    }
    if(prob > 1.0)
    {
      insertNeuronProb = 1.0;
      return;
    }
    insertNeuronProb = prob;
    return;
  };


  /**
   * sets the death probability, which deteremines the survival of the 
   * the parent individuals.
   * @param    double prob
   * @return   none
   */
  public void setDeathProb(double prob){
    if(prob < 0.0)
    {
      this.deathProb = 0.0;
      return;
    }
    if(prob > 1.0)
    {
      this.deathProb = 1.0;
      return;
    }
    this.deathProb = prob;
    return;
  }


  /**
   * return value is the probability of the suvival of parent inidividuals.
   * @param    none
   * @return   double deleteSynProb
   */
  public double getDeathProb(){
    return this.deathProb;
  }


  /**
   * return value is the probability of the deletion of a connection
   * between two neurons.
   * @param    none
   * @return   double deleteSynProb
   */
  public double getDelSynProb(){
    return deleteSynProb;
  };

  /**
   * return value is the probability of the deletion of a neuron.
   * @param    none
   * @return   double deleteNeuronProb
   */
  public double getDelNeuProb(){
    return deleteNeuronProb;
  };

  /**
   * return value is the probability of the insertion of a new connection.
   * @param    none
   * @return   double insertNeuronProb
   */
  public double getInsSynProb(){
    return insertSynProb;
  };

  /**
   * return value is the probability of the insertion of a new neuron.
   * @param    none
   * @return   double insertNeuronProb
   */
  public double getInsNeuProb(){
    return insertNeuronProb;
  };


  /**
   * return value is the cennection probability of a new inserted neuron.
   * @param    none
   * @return   double insertNeuronProb
   */
  public double getConnProb(){
    return connectivity;
  };


  /**
   * sets is the cennection probability of a new inserted neuron.
   * @param    none
   * @return   double insertNeuronProb
   */
  public void setConnProb(double prob){
    if(prob < 0)
    {
      connectivity = 0.0;
    }
    else
    {
      if(prob > 1.0)
      {
        connectivity = 1.0;
      }
      else
      {
        connectivity = prob;
      }
    }

    return;
  };



  /**
   * due to the wish of monitoring the current generation
   * during the velautaion of the population
   * @param    int genNmb
   * @return   none
   */
  public void setCurrentGenerationNmb(int genNmb){
    if(genNmb < 0)
    {
      this.currentGeneration = 0;
    }
    else
    {
      this.currentGeneration = genNmb;
    }

  }

  /**
   * due to the wish of monitoring the current generation
   * during the velautaion of the population
   * @param    int genNmb
   * @return   none
   */
  public int getCurrentGenerationNmb(){
    return this.currentGeneration;
  }

  /**
   * return value is the average population size.
   * @param    none
   * @return   int popsize
   */
  public int getPopSize(){
    return popSize;
  }

  /**
   * set the average population size, which should be at least 10.
   * If not so, average population size is set to 10.
   * @param    int size
   * @return   none
   */
  public void setPopSize(int n){
    if(n < 5 ) 
    {
      popSize = 5;
    }
    else
    {
      popSize = n;
    }
  }

  /* 
   * return value is the port number.
   * @param none
   * @return int portNumber
   */
  public int getPortNumb(){
    return this.port;
  }

  /* 
   * set  the port number.
   * @param int portNumber
   * @return none
   */
  public void setPortNumb(int nmb){
    if((nmb < 7000)|| (nmb > 65536) )
    {
      this.port = 7000;
    }
    else
    {
      this.port = nmb;
    }
    return;
  }

  /* 
   * return value is the time steps of evaluation without perf. calculation.
   * @param none
   * @return int timeSteps
   */
  public int getWarmUpSpan(){
    return this.warmUpSpan;
  }


  /* 
   * set the value of the time steps of evaluation without perf. calculation.
   * @param int timeSteps
   * @return none
   */
  public void setWarmUpSpan(int c){
    if(c < 0)
    {
      this.warmUpSpan = 0;
    }
    else
    {
      this.warmUpSpan = c; 
    }
    return;
  }


  /* 
   * return value is the time steps of evaluation with perf. calculation.
   * @param none
   * @return int timeSteps
   */
  public int getEvalTestSteps(){
    return this.evalTestSteps;
  }


  /* 
   * set the value of the time steps of evaluation with perf. calculation.
   * @param int timeSteps
   * @return none
   */
  public void setEvalTestSteps(int c){
    if(c < 1)
    {
      this.evalTestSteps = 1;
    }
    else
    {
      this.evalTestSteps = c; 
    }
    return;
  }



  /**
   * Returns the list of evo-object, which represents the parent population
   * @param    none
   * @return   EvoObjectList parents
   */
  public EvoObjectList getParents(){
    return parents;
  }

  /**
   * Returns the list of evo-objects, which represents offsprings population
   * @param    none
   * @return   EvoObjectList offsprings
   */
  public EvoObjectList getOffspring(){
    return offspring;
  }


  /*
   * Size of the current population, parents + offsprings (notice, 
   * to get right results one has to call this function after selection 
   * and before reproduction operations). 
   * @param    none
   * @return   int count
   */
  public int getCurrentPopSize(){
    int count;
    EvoObject obj;

    // count the counts of each parent, which is the number of offsprings,
    // and the parent itself,
    // so, we get the population size after selection and before reproduction 
    count = 0;
    for(this.parents.start(); this.parents.hasMore(); this.parents.next())
    {
      obj = this.parents.object();
      if(obj.getCount() > 0 )
      {
        count = count + obj.getCount() + 1;  // offsprings + parent
      }
    }
    return count;
  }

  /*
   * Number of parent indiviuums at the current population, notice, 
   * to get right results one has to call this function after selection 
   * and before reproduction operations. 
   * @param    none
   * @return   int count
   */
  public int getCurrentParents(){
    int count;
    EvoObject obj;

    // count the parents, which have at least one offspring,
    // so, we get the population size after selection and before reproduction 
    count = 0;
    for(this.parents.start(); this.parents.hasMore(); this.parents.next())
    {
      obj = this.parents.object();
      if(obj.getCount() > 0 )
      {
        count = count + 1;  
      }
    }
    return count;
  }



  public String getPopParamXml(){
    PopParamXml popParamXml = new PopParamXml(this);
    return (popParamXml.getXml());
  }

  public void fillPopParamXml(File file){
    PopParamXml popParamXml = new PopParamXml(this);
    popParamXml.fillFromXml(file);
  }

  public void setSynapseInsertionMode(int synapseInsertionMode)
  {
    this.synapseInsertionMode = synapseInsertionMode;
  }

  public void setParameterMode(int parameterMode)
  {
    this.parameterMode = parameterMode;
  }




  public static void main(String argv[]){
    Evolution e = new Evolution();
    Random  u = new Random();
    Gauss   g = new Gauss(0.0,1.0);
    Poisson p = new Poisson(u);

    EvoObject obj = new EvoObject(0,u,g);
    Population pop = new Population(e,u,g,p);

    System.out.print("population exits!\n");

    obj = pop.getEmptyInitialStructure();
    System.out.print(obj.toString()); 
  };



  // class is used for parallel evaluation of indies of one population;
  // global evaluation data are already sent and so its only for the 
  // exchange of the evoobject data prfdomance and the net
  private class EvalObjectSlot implements Runnable{
    private Population        pop;
    private EvoObject         obj;
    private PrintWriter       simInput;
    private BufferedReader    simOutput;  

    public EvalObjectSlot(Population pop, PrintWriter simInp, BufferedReader simOut){
      this.pop       = pop;
      this.simInput  = simInp;
      this.simOutput = simOut;
      this.obj       = null;
    }


    public void eval(){
      double dblVal;
      int j = 0;

      this.obj = this.pop.nextEvalObject();

      while(this.obj != null)
      {

        // give to the other side, that a new network is comming 
        this.simInput.println("NEXT_INDY");

        // send the dtd grammar followed by the network in xml-format
        //System.out.println(this.obj.getGrammarNetString());
        this.simInput.println(this.obj.getGrammarNetString());

        try{
          // receive the output performance, coming from the other side
          String str = this.simOutput.readLine();
          dblVal = Double.parseDouble(str);
          // save system performance in the current evoObject structure 
          // system performance calculation included
          obj.setOutPerf(dblVal, this.pop.getCostNeu(), this.pop.getCostSyn());
        }
        catch (IOException e)
        { 
          System.out.println("multi-eval.:  can't read parent fitness value");
        }

        // send the information that as next the calculates system performance is coming
        this.simInput.println("SYS_PERF");
        // send the system performance
        this.simInput.println(Double.toString(this.obj.getPerformance()));

        this.obj = this.pop.nextEvalObject();
      };
    }


    public void run(){
      eval();
    }
  }

  public String getLearnParameterXml()
  {
    String s = new String();
    s = s.concat("<Alpha\n");
    s = s.concat("  min=\"" + alphaMin  + "\"\n");
    s = s.concat("  max=\"" + alphaMax  + "\"\n");
    s = s.concat("  initial=\"" + alphaInitial  + "\"\n");
    s = s.concat("  probability=\"" + alphaProbability  + "\"\n");
    s = s.concat("  variation=\"" + alphaVariation  + "\"\n");
    s = s.concat("/>\n");
    s = s.concat("<Beta\n");
    s = s.concat("  min=\"" + betaMin  + "\"\n");
    s = s.concat("  max=\"" + betaMax  + "\"\n");
    s = s.concat("  initial=\"" + betaInitial  + "\"\n");
    s = s.concat("  probability=\"" + betaProbability  + "\"\n");
    s = s.concat("  variation=\"" + betaVariation  + "\"\n");
    s = s.concat("/>\n");
    s = s.concat("<Gamma\n");
    s = s.concat("  min=\"" + gammaMin  + "\"\n");
    s = s.concat("  max=\"" + gammaMax  + "\"\n");
    s = s.concat("  initial=\"" + gammaInitial  + "\"\n");
    s = s.concat("  probability=\"" + gammaProbability  + "\"\n");
    s = s.concat("  variation=\"" + gammaVariation  + "\"\n");
    s = s.concat("/>\n");
    s = s.concat("<Delta\n");
    s = s.concat("  min=\"" + deltaMin  + "\"\n");
    s = s.concat("  max=\"" + deltaMax  + "\"\n");
    s = s.concat("  initial=\"" + deltaInitial  + "\"\n");
    s = s.concat("  probability=\"" + deltaProbability  + "\"\n");
    s = s.concat("  variation=\"" + deltaVariation  + "\"\n");
    s = s.concat("/>\n");
    return s;
  }

}






