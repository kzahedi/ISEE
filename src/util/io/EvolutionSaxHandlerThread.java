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


package util.io;

import java.awt.Component;
import java.io.FileReader;
import java.util.Vector;

import javax.swing.ProgressMonitor;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import cholsey.LearningRuleClassLoader;
import cholsey.Net;
import cholsey.Neuron;
import cholsey.NeuronType;
import cholsey.ProcessMode;
import cholsey.Synapse;
import cholsey.SynapseMode;
import cholsey.SynapseType;
import cholsey.Transferfunction;


public class EvolutionSaxHandlerThread 
             extends DefaultHandler 
             implements Runnable
{
  private static LearningRuleClassLoader learningRuleClassLoader = new
    LearningRuleClassLoader();

  private final static String SEARCH_FOR_GENERATION = "Generation";
  private final static String SEARCH_FOR_POPULATION = "Population";
  private final static String SEARCH_FOR_INDIVIDUAL = "EvoObject";

  private ProgressMonitor progressMonitor =  null;

  private final static int GENERATION_INDEX = 0;
  private final static int POPULATION_INDEX = 1;
  private final static int INDIVIDUAL_INDEX = 2;

  private Net net = null;
  private Net _net = null;

  private Vector synapseList = new Vector();
  private Vector netVector = new Vector();

  private int _neuronIndex = -1;
  private String _filename = null;

  private EvolutionSaxInterface _consumer = null;


  private int[] _wanted= new int[3];

  private String[] _searchPatterns = 
  {
    SEARCH_FOR_GENERATION,
    SEARCH_FOR_POPULATION,
    SEARCH_FOR_INDIVIDUAL
  };

  private XMLReader xmlReader = null;

  private int _searchIndex = 0;

  public EvolutionSaxHandlerThread(
      String filename,
      int generationIndex, 
      int populationIndex,
      EvolutionSaxInterface consumer,
      Component parent)
  {
    this(filename, generationIndex, populationIndex, -1, consumer, parent);
  }

  public EvolutionSaxHandlerThread(
      String filename,
      int generationIndex, 
      int populationIndex, 
      int individualIndex,
      EvolutionSaxInterface consumer,
      Component parent)
  {
    super();
    progressMonitor = new ProgressMonitor(
        parent, "Loading Net", "Generation...", 0, 100);
    //progressMonitor.setModal(false);
    progressMonitor.setMillisToDecideToPopup(10);
    progressMonitor.setMillisToPopup(100);
    synapseList = new Vector();
    netVector = new Vector();
    _filename = filename;
    _wanted[INDIVIDUAL_INDEX] = individualIndex;
    _wanted[GENERATION_INDEX] = generationIndex;
    _wanted[POPULATION_INDEX] = populationIndex;
    _consumer = consumer;
    progressMonitor.setMaximum(generationIndex);
    try
    {
      xmlReader =
        XMLReaderFactory.createXMLReader("org.apache.crimson.parser.XMLReaderImpl" );
      xmlReader.setContentHandler(this);
      xmlReader.setErrorHandler(this);
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  public void run()
  {
    try
    {
      xmlReader.parse(new InputSource(new FileReader(_filename)));
    }
    catch(SAXParseException e)
    {
      //System.out.println("done");
      //e.printStackTrace();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    _consumer.netLoaded();
  }


  public void startDocument()
  {
    //System.out.println("start document");
  }

  public void endDocument()
  {
    //System.out.println("end document");
  }

  public void createNet(Attributes attributes)
  {
    synapseList = new Vector();
    _neuronIndex = -1;
    String transferfunctionString = attributes.getValue("Transferfunction");
    String synapseModeString      = attributes.getValue("SynapseMode");
    String learningValueString    = attributes.getValue("LearningRule");
    
    net = new Net();

    if(synapseModeString.equals(SynapseMode.CONVENTIONAL.toXML()))
    {
      net.setSynapseMode(SynapseMode.CONVENTIONAL);
    }
    else
    {
      net.setSynapseMode(SynapseMode.DYNAMIC);
    }

    if(transferfunctionString.equals(Transferfunction.TANH.toXML()))
    {
      net.setTransferfunction(Transferfunction.TANH);
    }
    else
    {
      net.setTransferfunction(Transferfunction.SIGM);
    }

    if(learningValueString != null)
    {
      learningRuleClassLoader.setSelectedLearningRule(learningValueString);
    }
  }

  public void addNeuron(Attributes attributes)
  {
    _neuronIndex++;
    double bias = Double.parseDouble(attributes.getValue("Bias"));
    String neuronTypeString = attributes.getValue("Layer");
    String processModeString = attributes.getValue("Process");
    double transmitter = 0;
    int daleMode = 0;
    if(attributes.getValue("TransmitterLevel") != null)
    {
      transmitter =
        Double.parseDouble(attributes.getValue("TransmitterLevel"));
    }
    double receptors = 0;
    if(attributes.getValue("ReceptorLevel") != null)
    {
      receptors = Double.parseDouble(attributes.getValue("ReceptorLevel"));
    } 
    double alpha = 0;    
    if(attributes.getValue("Alpha") != null)
    {
      alpha = Double.parseDouble(attributes.getValue("Alpha"));
    }
    double beta = 0; 
    if(attributes.getValue("Beta") != null)
    {
      beta = Double.parseDouble(attributes.getValue("Beta"));
    }
    double gamma = 0; 
    if(attributes.getValue("Gamma") != null)
    {
      gamma = Double.parseDouble(attributes.getValue("Gamma"));
    }
    double delta = 0; 
    if(attributes.getValue("Delta") != null)
    {
      delta = Double.parseDouble(attributes.getValue("Delta"));
    }
    double my = 0; 
    if(attributes.getValue("My") != null)
    {
      my = Double.parseDouble(attributes.getValue("My"));
    }
    double ny = 0; 
    if(attributes.getValue("Ny") != null)
    {
      ny = Double.parseDouble(attributes.getValue("Ny"));
    }


    if(attributes.getValue("DaleMode") != null)
    {
      if(attributes.getValue("DaleMode").equals("undefined"))
      {
        daleMode = Neuron.NEURON_DALE_UNDEFINED;
      }
      if(attributes.getValue("DaleMode").equals("inhibitory"))
      {
        daleMode = Neuron.NEURON_DALE_INHIBITORY;
      }
      if(attributes.getValue("DaleMode").equals("excitatory"))
      {
        daleMode = Neuron.NEURON_DALE_EXCITATORY;
      }
    }


    NeuronType neuronType = null;
    ProcessMode processMode = null;

    if (neuronTypeString.equals(NeuronType.HIDDEN.toXML()))
    {
      neuronType = NeuronType.HIDDEN;
    }
    if (neuronTypeString.equals(NeuronType.INPUT.toXML()))
    {
      neuronType = NeuronType.INPUT;
    }
    if (neuronTypeString.equals(NeuronType.READ_BUFFER.toXML()))
    {
      neuronType = NeuronType.READ_BUFFER;
    }
    if (neuronTypeString.equals(NeuronType.OUTPUT.toXML()))
    {
      neuronType = NeuronType.OUTPUT;
    }

    if(processModeString.equals(ProcessMode.CONSISTENT.toXML()))
    {
      processMode = ProcessMode.CONSISTENT;
    }
    if(processModeString.equals(ProcessMode.DYNAMIC.toXML()))
    {
      processMode = ProcessMode.DYNAMIC;
    }
    if(processModeString.equals(ProcessMode.STATIC.toXML()))
    {
      processMode = ProcessMode.STATIC;
    }
    Neuron n = net.addNeuron(bias, transmitter, receptors, processMode, neuronType);
    n.setAlpha(alpha);
    n.setBeta(beta);
    n.setGamma(gamma);
    n.setDelta(delta);
    n.setMy(my);
    n.setNy(ny);
    n.setDaleMode(daleMode);
  }
  
  public void addSynapse(Attributes attributes)
  {
    Vector synapse = new Vector();
    int sourceIndex = Integer.parseInt(attributes.getValue("Source"));
    double strength = Double.parseDouble(attributes.getValue("Strength"));
    String processModeString = attributes.getValue("Process");
    String synapseTypeString = attributes.getValue("SynapseType");
    ProcessMode processMode = null;
    SynapseType synapseType = null;
    if(processModeString.equals(ProcessMode.CONSISTENT.toXML()))
    {
      processMode = ProcessMode.CONSISTENT;
    }
    if(processModeString.equals(ProcessMode.DYNAMIC.toXML()))
    {
      processMode = ProcessMode.DYNAMIC;
    }
    if(processModeString.equals(ProcessMode.STATIC.toXML()))
    {
      processMode = ProcessMode.STATIC;
    }

   
    if(synapseTypeString != null)
    {
      if(synapseTypeString.equals(SynapseType.INHIBITORY.toXML()))
      {
        synapseType = SynapseType.INHIBITORY;
      }

      if(synapseTypeString.equals(SynapseType.EXCITATORY.toXML()))
      {
        synapseType = SynapseType.EXCITATORY;
      }
    }


    synapse.add(new Integer(_neuronIndex)); // destination index
    synapse.add(new Integer(sourceIndex));  // source index
    synapse.add(new Double(strength));      // strength
    synapse.add(processMode);
    synapse.add(synapseType);
    
    synapseList.add(synapse);

  }

  public void addSynapseListToNet()
  {
    for(int i=0;i<synapseList.size();i++)
    {
      Vector synapseVector = (Vector)synapseList.elementAt(i);
      int destinationIndex = ((Integer)synapseVector.elementAt(0)).intValue();
      int sourceIndex = ((Integer)synapseVector.elementAt(1)).intValue();
      double strength = ((Double)synapseVector.elementAt(2)).doubleValue();
      ProcessMode processMode = ((ProcessMode)synapseVector.elementAt(3));
      SynapseType synapseType = ((SynapseType)synapseVector.elementAt(4));

      Neuron destinationNeuron = net.neurons().neuron(destinationIndex);
      Neuron sourceNeuron = net.neurons().neuron(sourceIndex);

      Synapse synapse = new Synapse(sourceNeuron, destinationNeuron, strength,
          processMode);
      destinationNeuron.addSynapse(synapse);
      if(synapseType != null)
      {
        synapse.setSynapseType(synapseType);
      }

    }
    synapseList = new Vector();
  }

  public void addNetToListOfNets()
  {
    netVector.add(net);
  }

  
  public void parseString(String name, Attributes attributes)
  {
    if(name.equals("Net"))
    {
      createNet(attributes);
    }
    if(name.equals("Neuron"))
    {
      addNeuron(attributes);
    }
    if(name.equals("Synapse"))
    {
      addSynapse(attributes);
    }
  }
  
  public void startElement(String uri, String name, String qName, Attributes
      attributes)
  {
    int currentGenerationIndex = 0;
    // we dont use uri
    if(_searchIndex < 0) 
    {
      return;
    }

    if(_searchIndex > INDIVIDUAL_INDEX ) 
    {
      parseString(qName, attributes);
      return;
    }

    if(qName.equals("Generation"))
    {
      currentGenerationIndex = Integer.parseInt(attributes.getValue("Index"));
      progressMonitor.setProgress(currentGenerationIndex);
      progressMonitor.setNote("Generation " + currentGenerationIndex + "/" +
          _wanted[GENERATION_INDEX]);
    }

    if(qName.equals(_searchPatterns[_searchIndex]))
    {
      if(_searchIndex == INDIVIDUAL_INDEX &&
         _wanted[_searchIndex] == -1)
      {
        _searchIndex++;
        return;
      }
      if(Integer.parseInt(attributes.getValue("Index")) ==
          _wanted[_searchIndex])
      {
        _searchIndex++;
      }
    }
  }

  public void endElement(String uri, String name, String qName) throws
    SAXParseException
  {
    if(_searchIndex > 2 && qName.equals("Net"))
    {
      addSynapseListToNet();
      addNetToListOfNets();
      if(_wanted[INDIVIDUAL_INDEX] != -1)
      {
        _searchIndex = -1;
        throw new SAXParseException("done reading",null); // not nice, but does it
      }
    }
    if(_searchIndex > 2 && _wanted[INDIVIDUAL_INDEX] == -1 &&
        qName.equals("Population"))
    {
      _searchIndex = -1;
      throw new SAXParseException("done reading",null); // not nice, but does it
    }
  }

  public Net getNet()
  {
    return net;
  }

  public Vector getNetVector()
  {
    return netVector;
  }


  public static void main (String argv[]) throws Exception
  {

    String filename = argv[0];
    int generationIndex = Integer.parseInt(argv[1]);
    int populationIndex = Integer.parseInt(argv[2]);
    int individualIndex = Integer.parseInt(argv[3]);
    if(individualIndex == -1)
    {
      EvolutionSaxHandlerThread evolutionSaxHandlerThread = new
        EvolutionSaxHandlerThread( filename, generationIndex, populationIndex,
            null, null);
      Thread t = new Thread(evolutionSaxHandlerThread);
      t.run();
      try
      {
        t.join();
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
      Vector nets = evolutionSaxHandlerThread.getNetVector();
      for(int i=0;i<nets.size();i++)
      {
        Net net = (Net)nets.elementAt(i);
        System.out.println(net.toXML());
      }

    }
    else
    {
      /*
      Net net = evolutionSaxHandlerThread.readNet(filename, generationIndex,
          populationIndex,
          individualIndex);
      if(net == null)
      {
        System.out.println("net not found");
      }
      else
      {
        System.out.println(net.toXML());
      }
      */
    }

  }
}

