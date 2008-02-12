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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import cholsey.SynapseMode;
import cholsey.Transferfunction;




/**
 *  PopParamXml implements save and load  function from file of parameter  of one population.  <br>
 *
 *
 *
 *
 */



public class PopParamXml {
  private Document document = null;
  private Population pop = null;


  public PopParamXml(Population pop){
    this.pop = pop;
  }



  public void fillFromXml(File file){
    double dblValue;
    int intValue;
    Element data = null;

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(true);
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      document = builder.parse( file );
    }
    catch (FileNotFoundException e)
    {
      System.out.println("No Config File xml/config/dynamics.xml found \n"
          +"using default values");
      return;
    }
    catch (SAXException sxe) {
      // Error generated during parsing
      Exception  x = sxe;
      if (sxe.getException() != null)
      {
        x = sxe.getException();
      }
      x.printStackTrace();
    } catch (ParserConfigurationException pce) {
      // Parser with specified options can't be built
      pce.printStackTrace();
    } catch (IOException ioe) {
      // I/O error
      ioe.printStackTrace();
    }
    Element configData = document.getDocumentElement();
    if(configData == null)
    {
      System.out.println("ups!!");
      return;
    }


    // load initial structure 
    this.pop.deactivateFileInit();
    data = (Element)document.getElementsByTagName("InitialStructure").item(0);
    intValue = Integer.parseInt(data.getAttribute("numInpNeuron").trim());
    this.pop.setNmbInpNeu(intValue);

    intValue = Integer.parseInt(data.getAttribute("numOutNeuron").trim());
    this.pop.setNmbOutNeu(intValue);

    if( (data.getAttribute("transferfunction")).equals("tanh") )
      this.pop.setTransferfunction(Transferfunction.TANH);
    else
      this.pop.setTransferfunction(Transferfunction.SIGM);

    if( (data.getAttribute("synapseMode")).equals("conventional") )
      this.pop.setSynMode(SynapseMode.CONVENTIONAL);
    else
      this.pop.setSynMode(SynapseMode.DYNAMIC);



    // load structure variation parameter
    data = (Element)document.getElementsByTagName("Combinatorial").item(0); 
    dblValue = Double.parseDouble(data.getAttribute("InsNeu").trim());
    this.pop.setInsNeuProb(dblValue);
    dblValue = Double.parseDouble(data.getAttribute("DelNeu").trim());
    this.pop.setDelNeuProb(dblValue);
    dblValue = Double.parseDouble(data.getAttribute("InsSyn").trim());
    this.pop.setInsSynProb(dblValue);
    dblValue = Double.parseDouble(data.getAttribute("DelSyn").trim());
    this.pop.setDelSynProb(dblValue);
    dblValue = Double.parseDouble(data.getAttribute("Connect").trim());
    this.pop.setConnProb(dblValue);
    intValue = Integer.parseInt(data.getAttribute("MaxHidden").trim());
    this.pop.setMaxHidNeuNmb(intValue);


    // load real value  variation parameter
    data = (Element)document.getElementsByTagName("Weight").item(0); 
    dblValue = Double.parseDouble(data.getAttribute("limit").trim());
    this.pop.setWeightLim(dblValue);
    dblValue = Double.parseDouble(data.getAttribute("strength").trim());
    this.pop.setChangeWeightIntens(dblValue);
    dblValue = Double.parseDouble(data.getAttribute("prob").trim());
    this.pop.setChangeWeightProb(dblValue);

    data = (Element)document.getElementsByTagName("Bias").item(0); 
    dblValue = Double.parseDouble(data.getAttribute("limit").trim());
    this.pop.setBiasLim(dblValue);
    dblValue = Double.parseDouble(data.getAttribute("strength").trim());
    this.pop.setChangeBiasIntens(dblValue);
    dblValue = Double.parseDouble(data.getAttribute("prob").trim());
    this.pop.setChangeBiasProb(dblValue);

    data = (Element)document.getElementsByTagName("Decay").item(0); 
    dblValue = Double.parseDouble(data.getAttribute("limit").trim());
    this.pop.setDecayLim(dblValue);
    dblValue = Double.parseDouble(data.getAttribute("strength").trim());
    this.pop.setChangeDecayIntens(dblValue);
    dblValue = Double.parseDouble(data.getAttribute("prob").trim());
    this.pop.setChangeDecayProb(dblValue);

    // load real value  variation learn parameter

    // load evaluation parameter
    data = (Element)document.getElementsByTagName("Costs").item(0); 
    dblValue = Double.parseDouble(data.getAttribute("Neurons").trim());
    this.pop.setCostNeu(dblValue);
    dblValue = Double.parseDouble(data.getAttribute("Synapses").trim());
    this.pop.setCostSyn(dblValue);

    data = (Element)document.getElementsByTagName("Constants").item(0);
    dblValue = Double.parseDouble(data.getAttribute("C0").trim());
    this.pop.setC0(dblValue);
    dblValue = Double.parseDouble(data.getAttribute("C1").trim());
    this.pop.setC1(dblValue);
    dblValue = Double.parseDouble(data.getAttribute("C2").trim());
    this.pop.setC2(dblValue);
    dblValue = Double.parseDouble(data.getAttribute("C3").trim());
    this.pop.setC3(dblValue);

    data = (Element)document.getElementsByTagName("Communication").item(0);
    intValue = Integer.parseInt(data.getAttribute("ServerPort").trim());
    this.pop.setPortNumb(intValue);


    // load selection parameter
    data = (Element)document.getElementsByTagName("Selection").item(0);
    intValue = Integer.parseInt(data.getAttribute("PopSize").trim());
    this.pop.setPopSize(intValue);
    dblValue = Double.parseDouble(data.getAttribute("BirthGamma").trim());
    this.pop.setBirthGamma(dblValue);
    intValue = Integer.parseInt(data.getAttribute("SaveBest").trim());
    this.pop.setSaveNBest(intValue);

    double alphaInitial = 1.0;
    double alphaProbability = 0.0;
    double alphaMin = 0.0;
    double alphaMax = 1.0;
    double alphaVariation = 0.1;

    double betaInitial = 0.01;
    double betaProbability = 0.0;
    double betaMin = 0.0001;
    double betaMax = 0.2;
    double betaVariation = 0.05;

    double gammaInitial = 0.01;
    double gammaProbability = 0.0;
    double gammaMin = 0.0001;
    double gammaMax = 0.2;
    double gammaVariation = 0.01;

    double deltaInitial = 0.02;
    double deltaProbability = 0.0;
    double deltaMin = 0.0001;
    double deltaMax = 0.2;
    double deltaVariation = 0.01;

    data = (Element)document.getElementsByTagName("Alpha").item(0);
    if(data != null)
    {
      alphaInitial = Double.parseDouble(data.getAttribute("initial").trim());
      alphaProbability = Double.parseDouble(data.getAttribute("probability").trim());
      alphaMin = Double.parseDouble(data.getAttribute("min").trim());
      alphaMax = Double.parseDouble(data.getAttribute("max").trim());
      alphaVariation = Double.parseDouble(data.getAttribute("variation").trim());
    }

    data = (Element)document.getElementsByTagName("Beta").item(0);
    if(data != null)
    {
      betaInitial = Double.parseDouble(data.getAttribute("initial").trim());
      betaProbability = Double.parseDouble(data.getAttribute("probability").trim());
      betaMin = Double.parseDouble(data.getAttribute("min").trim());
      betaMax = Double.parseDouble(data.getAttribute("max").trim());
      betaVariation = Double.parseDouble(data.getAttribute("variation").trim());
    }

    data = (Element)document.getElementsByTagName("Gamma").item(0);
    if(data != null)
    {
      gammaInitial = Double.parseDouble(data.getAttribute("initial").trim());
      gammaProbability = Double.parseDouble(data.getAttribute("probability").trim());
      gammaMin = Double.parseDouble(data.getAttribute("min").trim());
      gammaMax = Double.parseDouble(data.getAttribute("max").trim());
      gammaVariation = Double.parseDouble(data.getAttribute("variation").trim());
    }

    data = (Element)document.getElementsByTagName("Delta").item(0);
    if(data != null)
    {
      deltaInitial = Double.parseDouble(data.getAttribute("initial").trim());
      deltaProbability = Double.parseDouble(data.getAttribute("probability").trim());
      deltaMin = Double.parseDouble(data.getAttribute("min").trim());
      deltaMax = Double.parseDouble(data.getAttribute("max").trim());
      deltaVariation = Double.parseDouble(data.getAttribute("variation").trim());
    }

    pop.setLearningParameter(
        alphaMax, alphaMin, alphaInitial, alphaProbability, alphaVariation,
        betaMax, betaMin, betaInitial, betaProbability, betaVariation,
        gammaMax, gammaMin, gammaInitial, gammaProbability, gammaVariation,
        deltaMax, deltaMin, deltaInitial, deltaProbability, deltaVariation);

    pop.updatePanel();


    this.pop.popRefresh();
  }


  public String getXml(){
    String s = new String();

    s = s.concat(getPopParamGrammar());
    s = s.concat("<PopParam>\n");
    s = s.concat(this.getInitalStructureXml());
    s = s.concat(this.getVariationParamXml());
    s = s.concat(this.getEvaluationParamXml());
    s = s.concat(this.getSelectionParamXml());
    s = s.concat("</PopParam>\n");
    return s;
  }


  private String getInitalStructureXml(){
    String s = new String();
    EvoObject obj;
    s = s.concat("     <InitialStructure ");
    s = s.concat(" numInpNeuron =\"" + this.pop.getNmbInpNeu() + "\"");
    s = s.concat(" numOutNeuron =\"" + this.pop.getNmbOutNeu() + "\"");

    if(this.pop.getTransferfunction() == Transferfunction.TANH)
      s = s.concat(" transferfunction =\"tanh\"");
    else
      s = s.concat(" transferfunction =\"sigm\"");

    if(this.pop.getSynMode() == SynapseMode.CONVENTIONAL)
      s = s.concat(" synapseMode =\"conventional\"/>\n");
    else
      s = s.concat(" synapseMode =\"dynamic\"/>\n");
    return s;
  }

  private String getVariationParamXml(){
    String s = new String();
    s = s.concat("     <Variation>\n");
    s = s.concat(getCombVariationParamXml());
    s = s.concat(getRealVariationParamXml());
    s = s.concat(getHomeoKinParamXml());
    s = s.concat(getLearnParamXml());
    s = s.concat("     </Variation>\n");
    return s;
  }

  private String getCombVariationParamXml(){
    String s = new String();
    s = s.concat("          <Combinatorial   ");
    s = s.concat(" InsNeu = \"" + Double.toString(this.pop.getInsNeuProb()) + "\"");
    s = s.concat(" DelNeu = \"" + Double.toString(this.pop.getDelNeuProb()) + "\"");
    s = s.concat(" InsSyn = \"" + Double.toString(this.pop.getInsSynProb()) + "\"");
    s = s.concat(" DelSyn = \"" + Double.toString(this.pop.getDelSynProb()) + "\"");
    s = s.concat(" Connect = \"" + Double.toString(this.pop.getConnProb()) + "\"");
    s = s.concat(" MaxHidden = \"" + Integer.toString(this.pop.getMaxHidNeuNmb()) + "\"/>\n");
    return s;
  }
  private String getRealVariationParamXml(){
    String s = new String();
    s = s.concat("          <RealValue>\n");
    s = s.concat(getWeightVarParamXml());
    s = s.concat(getBiasVarParamXml());
    s = s.concat(getDecayVarParamXml());
    s = s.concat("          </RealValue>\n");
    return s;
  }


  private String getWeightVarParamXml(){
    String s = new String();
    s = s.concat("                <Weight  ");
    s = s.concat(" prob = \"" + this.pop.getChangeWeightProb() + "\"");
    s = s.concat(" strength = \"" + this.pop.getChangeWeightIntens() + "\"");
    s = s.concat(" limit = \"" + this.pop.getWeightLim() + "\"/>\n");
    return s;
  }

  private String getBiasVarParamXml(){
    String s = new String();
    s = s.concat("                <Bias    ");
    s = s.concat(" prob = \"" + this.pop.getChangeBiasProb() + "\"");
    s = s.concat(" strength = \"" + this.pop.getChangeBiasIntens() + "\"");
    s = s.concat(" limit = \"" + this.pop.getBiasLim() + "\"/>\n");
    return s;
  }


  private String getDecayVarParamXml(){
    String s = new String();
    s = s.concat("                <Decay   ");
    s = s.concat(" prob = \"" + this.pop.getChangeDecayProb() + "\"");
    s = s.concat(" strength = \"" + this.pop.getChangeDecayIntens() + "\"");
    s = s.concat(" limit = \"" + this.pop.getDecayLim() + "\"/>\n");
    return s;
  }

  private String getLearnParamXml(){
    String s = new String();
    s = s.concat("          <LearnParam>\n");
    s = s.concat(pop.getLearnParameterXml());
    s = s.concat("          </LearnParam>\n");
    return s;
  }


  private String getHomeoKinParamXml(){
    String s = new String();
    s = s.concat("          <Homeokinese>\n");
    s = s.concat(getModFreqVarParamXml());
    s = s.concat(getModAmplVarParamXml());
    s = s.concat(getLearnRateVarParamXml());
    s = s.concat(getOffSetVarParamXml());
    s = s.concat("          </Homeokinese>\n");
    return s;
  }

  private String getModFreqVarParamXml(){
    String s = new String();
    s = s.concat("                <ModFreq   ");
    s = s.concat(" prob = \"" + 0.0 + "\"");
    s = s.concat(" strength = \"" + 0.0 + "\"");
    s = s.concat(" limit = \"" + 0.0 + "\"/>\n");
    return s;
  }

  private String getModAmplVarParamXml(){
    String s = new String();
    s = s.concat("                <ModAmpl   ");
    s = s.concat(" prob = \"" + 0.0 + "\"");
    s = s.concat(" strength = \"" + 0.0 + "\"");
    s = s.concat(" limit = \"" + 0.0 + "\"/>\n");
    return s;
  }

  private String getLearnRateVarParamXml(){
    String s = new String();
    s = s.concat("                <LearnRate ");
    s = s.concat(" prob = \"" + 0.0 + "\"");
    s = s.concat(" strength = \"" + 0.0 + "\"");
    s = s.concat(" limit = \"" + 0.0 + "\"/>\n");
    return s;
  }

  private String getOffSetVarParamXml(){
    String s = new String();
    s = s.concat("                <OffSet    ");
    s = s.concat(" prob = \"" + 0.0 + "\"");
    s = s.concat(" strength = \"" + 0.0 + "\"");
    s = s.concat(" limit = \"" + 0.0 + "\"/>\n");
    return s;
  }


  private String getEvaluationParamXml(){
    String s = new String();
    s = s.concat("     <Evaluation>\n");
    s = s.concat(getCostsParamXml());
    s = s.concat(getConstantsParamXml());
    s = s.concat(getCommParamXml());
    s = s.concat("     </Evaluation>\n");
    return s;
  }

  private String getCostsParamXml(){
    String s = new String();
    s = s.concat("          <Costs ");
    s = s.concat(" Neurons = \"" + Double.toString(this.pop.getCostNeu()) + "\"");
    s = s.concat(" Synapses = \"" + Double.toString(this.pop.getCostSyn()) + "\"/>\n");
    return s;
  }

  private String getConstantsParamXml(){
    String s = new String();
    s = s.concat("          <Constants  ");
    s = s.concat(" C0 = \"" + Double.toString(this.pop.getC0()) + "\"");
    s = s.concat(" C1 = \"" + Double.toString(this.pop.getC1()) + "\"");
    s = s.concat(" C2 = \"" + Double.toString(this.pop.getC2()) + "\"");
    s = s.concat(" C3 = \"" + Double.toString(this.pop.getC3()) + "\"/>\n");
    return s;
  }
  private String getCommParamXml(){
    String s = new String();
    s = s.concat("          <Communication ");
    s = s.concat(" ServerPort = \"" + Integer.toString(this.pop.getPortNumb()) + "\"/>\n");
    return s;
  }

  private String getSelectionParamXml(){
    String s = new String();
    s = s.concat("     <Selection  ");
    s = s.concat(" PopSize = \"" + Integer.toString(this.pop.getPopSize()) + "\"");
    s = s.concat(" BirthGamma = \"" + Double.toString(this.pop.getBirthGamma()) + "\"");
    s = s.concat(" SaveBest  = \"" + Integer.toString(this.pop.getSaveNBest()) + "\"/>\n");
    return s;
  }


  private String getPopParamGrammar(){
    String s = new String();

    s = s.concat("<?xml version=\"1.0\" encoding=\"LATIN1\"?>\n");
    s = s.concat("<!DOCTYPE PopParam [\n"); 

    s = s.concat("<!ELEMENT PopParam (InitialStructure, Variation, Evaluation, Selection)>\n");

    s = s.concat("<!ELEMENT InitialStructure EMPTY>\n");
    s = s.concat("<!ATTLIST InitialStructure\n");
    s = s.concat("    numInpNeuron      CDATA #REQUIRED\n");
    s = s.concat("    numOutNeuron      CDATA #REQUIRED\n");
    s = s.concat("    transferfunction  CDATA #REQUIRED\n");
    s = s.concat("    synapseMode       CDATA #REQUIRED\n");
    s = s.concat(">\n");

    s = s.concat("<!ELEMENT Neuron (Synapse*)>\n");
    s = s.concat("<!ATTLIST Neuron\n");
    s = s.concat("      Bias                CDATA #REQUIRED\n");
    s = s.concat("      TransmitterLevel    CDATA #IMPLIED\n");
    s = s.concat("      ReceptorLevel       CDATA #IMPLIED\n");
    s = s.concat("      Layer               CDATA #REQUIRED\n");
    s = s.concat("      Process             CDATA #REQUIRED\n");
    s = s.concat(">\n");

    s = s.concat("<!ELEMENT Synapse EMPTY>\n");
    s = s.concat("<!ATTLIST Synapse\n");
    s = s.concat("      Source              CDATA #REQUIRED\n");
    s = s.concat("      Strength            CDATA #REQUIRED\n");
    s = s.concat("      Process             CDATA #REQUIRED\n");
    s = s.concat(">\n");


    s = s.concat("<!ELEMENT Variation  (Combinatorial, RealValue, Homeokinese, LearnParam?)>\n");
    s = s.concat("<!ELEMENT Combinatorial EMPTY>\n");
    s = s.concat("<!ATTLIST Combinatorial\n");
    s = s.concat("          InsNeu             CDATA #REQUIRED\n");
    s = s.concat("          DelNeu             CDATA #REQUIRED\n");
    s = s.concat("          InsSyn             CDATA #REQUIRED\n");
    s = s.concat("          DelSyn             CDATA #REQUIRED\n");
    s = s.concat("          Connect            CDATA #REQUIRED\n");
    s = s.concat("          MaxHidden          CDATA #REQUIRED\n");
    s = s.concat(">\n");
    s = s.concat("<!ELEMENT RealValue   (Weight, Bias, Decay)>\n");
    s = s.concat("<!ELEMENT Weight EMPTY>\n");
    s = s.concat("<!ATTLIST Weight\n");
    s = s.concat("          prob             CDATA #REQUIRED\n");
    s = s.concat("          strength         CDATA #REQUIRED\n");
    s = s.concat("          limit            CDATA #REQUIRED\n");
    s = s.concat(">\n");
    s = s.concat("<!ELEMENT Bias EMPTY>\n");
    s = s.concat("<!ATTLIST Bias\n");
    s = s.concat("          prob             CDATA #REQUIRED\n");
    s = s.concat("          strength         CDATA #REQUIRED\n");
    s = s.concat("          limit            CDATA #REQUIRED\n");
    s = s.concat(">\n");
    s = s.concat("<!ELEMENT Decay EMPTY>\n");
    s = s.concat("<!ATTLIST Decay\n");
    s = s.concat("          prob             CDATA #REQUIRED\n");
    s = s.concat("          strength         CDATA #REQUIRED\n");
    s = s.concat("          limit            CDATA #REQUIRED\n");
    s = s.concat(">\n");

    s = s.concat("<!ELEMENT Homeokinese (ModFreq, ModAmpl, LearnRate, OffSet)>\n");
    s = s.concat("<!ELEMENT ModFreq EMPTY>\n");
    s = s.concat("<!ATTLIST ModFreq\n");
    s = s.concat("          prob             CDATA #REQUIRED\n");
    s = s.concat("          strength         CDATA #REQUIRED\n");
    s = s.concat("          limit            CDATA #REQUIRED\n");
    s = s.concat(">\n");
    s = s.concat("<!ELEMENT ModAmpl EMPTY>\n");
    s = s.concat("<!ATTLIST ModAmpl\n");
    s = s.concat("          prob             CDATA #REQUIRED\n");
    s = s.concat("          strength         CDATA #REQUIRED\n");
    s = s.concat("          limit            CDATA #REQUIRED\n");
    s = s.concat(">\n");
    s = s.concat("<!ELEMENT LearnRate EMPTY>\n");
    s = s.concat("<!ATTLIST LearnRate\n");
    s = s.concat("          prob             CDATA #REQUIRED\n");
    s = s.concat("          strength         CDATA #REQUIRED\n");
    s = s.concat("          limit            CDATA #REQUIRED\n");
    s = s.concat(">\n");
    s = s.concat("<!ELEMENT OffSet EMPTY>\n");
    s = s.concat("<!ATTLIST OffSet\n");
    s = s.concat("          prob             CDATA #REQUIRED\n");
    s = s.concat("          strength         CDATA #REQUIRED\n");
    s = s.concat("          limit            CDATA #REQUIRED\n");
    s = s.concat(">\n");


    s = s.concat("<!ELEMENT LearnParam (Alpha?, Beta?, Gamma?, Delta?)>\n");
    s = s.concat("<!ELEMENT Alpha EMPTY>\n");
    s = s.concat("<!ATTLIST Alpha\n");
    s = s.concat("          min                CDATA #REQUIRED\n");
    s = s.concat("          max                CDATA #REQUIRED\n");
    s = s.concat("          initial            CDATA #REQUIRED\n");
    s = s.concat("          probability        CDATA #REQUIRED\n");
    s = s.concat("          variation         CDATA #REQUIRED\n");
    s = s.concat(">\n");
    s = s.concat("<!ELEMENT Beta  EMPTY>\n");
    s = s.concat("<!ATTLIST Beta\n");
    s = s.concat("          min                CDATA #REQUIRED\n");
    s = s.concat("          max                CDATA #REQUIRED\n");
    s = s.concat("          initial            CDATA #REQUIRED\n");
    s = s.concat("          probability        CDATA #REQUIRED\n");
    s = s.concat("          variation         CDATA #REQUIRED\n");
    s = s.concat(">\n");
    s = s.concat("<!ELEMENT Gamma EMPTY>\n");
    s = s.concat("<!ATTLIST Gamma\n");
    s = s.concat("          min                CDATA #REQUIRED\n");
    s = s.concat("          max                CDATA #REQUIRED\n");
    s = s.concat("          initial            CDATA #REQUIRED\n");
    s = s.concat("          probability        CDATA #REQUIRED\n");
    s = s.concat("          variation         CDATA #REQUIRED\n");
    s = s.concat(">\n");
    s = s.concat("<!ELEMENT Delta EMPTY>\n");
    s = s.concat("<!ATTLIST Delta\n");
    s = s.concat("          min                CDATA #REQUIRED\n");
    s = s.concat("          max                CDATA #REQUIRED\n");
    s = s.concat("          initial            CDATA #REQUIRED\n");
    s = s.concat("          probability        CDATA #REQUIRED\n");
    s = s.concat("          variation         CDATA #REQUIRED\n");
    s = s.concat(">\n");






    s = s.concat("<!ELEMENT Evaluation (Costs, Constants, Communication)>\n");
    s = s.concat("<!ELEMENT Costs EMPTY>\n");
    s = s.concat("<!ATTLIST Costs\n");
    s = s.concat("      Neurons            CDATA #REQUIRED\n");
    s = s.concat("      Synapses           CDATA #REQUIRED\n");
    s = s.concat(">\n");
    s = s.concat("<!ELEMENT Constants EMPTY>\n");
    s = s.concat("<!ATTLIST Constants\n");
    s = s.concat("      C0           CDATA #REQUIRED\n");
    s = s.concat("      C1           CDATA #REQUIRED\n");
    s = s.concat("      C2           CDATA #REQUIRED\n");
    s = s.concat("      C3           CDATA #REQUIRED\n");
    s = s.concat(">\n");
    s = s.concat("<!ELEMENT Communication EMPTY>\n");
    s = s.concat("<!ATTLIST Communication\n");
    s = s.concat("      ServerPort            CDATA #REQUIRED\n");
    s = s.concat(">\n");


    s = s.concat("<!ELEMENT Selection  EMPTY>\n");
    s = s.concat("<!ATTLIST Selection\n");
    s = s.concat("      PopSize              CDATA #REQUIRED\n");
    s = s.concat("      BirthGamma           CDATA #REQUIRED\n");
    s = s.concat("      SaveBest            CDATA #REQUIRED\n");
    s = s.concat(">\n");

    s = s.concat("\n]>\n");
    return s;
  }


}


