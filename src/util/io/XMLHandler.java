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

// java stuff
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cholsey.LearningRuleClassLoader;
import cholsey.Net;
import cholsey.Neuron;
import cholsey.NeuronType;
import cholsey.ProcessMode;
import cholsey.Synapse;
import cholsey.SynapseMode;
import cholsey.SynapseType;
import cholsey.Transferfunction;

/**
 * Provides an wrapper to the XML stuff.
 * Input must be a <underline> valid </underline> xml evolution file.
 */
public class XMLHandler 
{

  private static LearningRuleClassLoader learningRuleClassLoader = new
    LearningRuleClassLoader();

  private static Document document;

  /**
   *  It is the default constructor
   *  @param    InputStream
   */
  public XMLHandler()
  {
  }

  public void read(String file)
  {
    read(new File(file));
  }
  /**
   *  Reads data from an xml file an puts it into the Document-class
   *  @param    File file  - xml file
   *  @return   none
   *  @see org.w3c.dom.Document
   */
  public void read(File file)
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(true);
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      document = builder.parse( file );
    } catch (SAXException sxe) {
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
  }

  public void read(InputStream is)
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(true);
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      document = builder.parse( is );
    } catch (SAXException sxe) {
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
  }


  public Node getPopulationNode(int index)
  {
    NodeList popNodes = document.getElementsByTagName("Population");
    if (index >= popNodes.getLength())
    {
      System.out.println("Population index " + index + " exceeds number of "
          + "populations " + popNodes.getLength());
      return null;
    }

    Node node =  document.getElementsByTagName("Population").item(index);
    //System.out.println("return node " + node.toString());
    return node;

  }

  /**
   * Returns the Net-Node, indentfied by popublation and net-index. The index
   * start with 0, so the first net in the first population is identified by the
   * pair (0,0)
   * @param population_index int population_index, the index of the
   * population
   * @param net_index int net_index, the index of the net
   * @return  Node net_node. The node of the specified net. The node is part of
   * the document-model
   * @see org.w3c.dom.Document
   */
  public Node getNetNode(int population_index, int net_index)
  {
    int individuals = 0;
    Node population_node = getPopulationNode(population_index);
    if(population_node == null)
    {
      return null;
    }
    Node net_node = population_node.getFirstChild();
    if (net_node.getNodeName().equals("Net"))
    {
      net_index--;
    }
    while(net_index >= 0)
    {
      net_node = net_node.getNextSibling();
      if(net_node == null) 
      {
        System.out.println("In the selected Generation, we only have " +
            individuals + " individuals, not " + (net_index+individuals));
        return null;
      }
      if(net_node.getNodeName().equals("Net"))
      {
        net_index--;
        individuals++;
      }
    }
    if (net_index >= 0) return null;
    /*
       System.out.println("net_node name: " + net_node.getNodeName());
       System.out.println("net_node  " + net_node.toString());
     */
    return net_node;
  }

  private Transferfunction getTransferfunction(String transferfunction)
  {
    if(transferfunction.equals("tanh"))
    {
      return Transferfunction.TANH;
    }
    else if(transferfunction.equals("sigm"))
    {
      return Transferfunction.SIGM;
    }
    else
    {
      System.out.println("XMLHandler: UNKOWN TRANSFERFUNCTION!");
      System.exit(-1);
    }
    return null;
  }

  private SynapseMode getSynapseMode(String synapseMode)
  {
    if(synapseMode.equals("conventional"))
    {
      return SynapseMode.CONVENTIONAL;
    }
    else if(synapseMode.equals("dynamic"))
    {
      return SynapseMode.DYNAMIC;
    }
    else
    {
      System.out.println("XMLHandler: UNKOWN SYNAPSESMODE!");
      System.exit(-1);
    }
    return null;
  }

  private ProcessMode getProcessMode(String processMode)
  {
    if(processMode.equals("dynamic"))
    {
      return ProcessMode.DYNAMIC;
    }
    else if(processMode.equals("consistent"))
    {
      return ProcessMode.CONSISTENT;
    }
    else if(processMode.equals("static"))
    {
      return ProcessMode.STATIC;
    }
    else
    {
      System.out.println("XMLHandler: UNKOWN PROCESS_MODE!");
      System.exit(-1);
    }
    return null;
  }

  private NeuronType getNeuronType(String neuronType)
  {
    if(neuronType.equals("input"))
    {
      return NeuronType.INPUT;
    }
    else if(neuronType.equals("output"))
    {
      return NeuronType.OUTPUT;
    }
    else if(neuronType.equals("read-buffer"))
    {
      return NeuronType.READ_BUFFER;
    }
    else if(neuronType.equals("hidden"))
    {
      return NeuronType.HIDDEN;
    }
    else
    {
      System.out.println("XMLHandler: UNKOWN NEURON_TYPE!");
      System.exit(-1);
    }
    return null;
  }


  /**
   *  Extracts the data from the xml-data
   *  @param    netNode Node netNode. A node from the document-mode. 
   *  @return   Net net, the neural net
   *
   *  @see org.w3c.dom.Document
   * @see cholsey.Net
   */
  public Net getNet(Node netNode)
  {
    if (netNode == null) 
    {
      return null; // nothing to get, if there is no net :)
    }
    Net net = new Net();
    int neuron_count = 0;
    // there is only one
    NamedNodeMap netAttributes = netNode.getAttributes();
    Transferfunction transferfunction = getTransferfunction(
        netAttributes.getNamedItem("Transferfunction").getNodeValue());
    SynapseMode synapseMode = getSynapseMode(
        netAttributes.getNamedItem("SynapseMode").getNodeValue());
    net.setTransferfunction(transferfunction);

    if(netAttributes.getNamedItem("LearningRule") != null)
    {
      learningRuleClassLoader.setSelectedLearningRule(
          netAttributes.getNamedItem("LearningRule").getNodeValue());
    }
    else
    {
      System.out.println("LearningRule not found");
    }


    Vector v = new Vector(0);
    if(netAttributes.getNamedItem("Properties") != null)
    {
      String s = netAttributes.getNamedItem("Properties").getNodeValue();
      StringTokenizer st = new StringTokenizer(s,",");
      while(st.hasMoreTokens())
      {
        v.add(new Double(Double.parseDouble(st.nextToken().trim())));
      }
      net.setProperties(v);
    }


// **************************************************************************
// read neurons
// **************************************************************************

    Node node = netNode.getFirstChild();
    while(!node.getNodeName().equals("Neuron"))
    {
      node = node.getNextSibling();
    }
    do
    {
      if (node.getNodeName().equals("Neuron"))
      {
        NamedNodeMap attributes = node.getAttributes();
        double bias =
          Double.parseDouble(
              attributes.getNamedItem("Bias").getNodeValue());
        double transmitterLevel = 0;
        double receptorLevel = 0;
        double kappa = 0;
        double alpha = 0;
        double beta  = 0;
        double gamma = 0;
        double delta = 0;

        if(attributes.getNamedItem("TransmitterLevel") != null)
        {
          transmitterLevel=Double.parseDouble(
              attributes.getNamedItem("TransmitterLevel").getNodeValue());
        }
        if(attributes.getNamedItem("ReceptorLevel") != null)
        {
          receptorLevel =
            Double.parseDouble(
                attributes.getNamedItem("ReceptorLevel").getNodeValue());
        }
        if(attributes.getNamedItem("Alpha") != null)
        {
          alpha         =
            Double.parseDouble(
                attributes.getNamedItem("Alpha").getNodeValue());
        }
        if(attributes.getNamedItem("Beta") != null)
        {
          beta          =
            Double.parseDouble(
                attributes.getNamedItem("Beta").getNodeValue());
        }
        if(attributes.getNamedItem("Gamma") != null)
        {
          gamma         =
            Double.parseDouble(
                attributes.getNamedItem("Gamma").getNodeValue());
        }
        if(attributes.getNamedItem("Delta") != null)
        {
          delta         =
            Double.parseDouble(
                attributes.getNamedItem("Delta").getNodeValue());
        }
        if(attributes.getNamedItem("Kappa") != null)
        {
          kappa=Double.parseDouble(
              attributes.getNamedItem("Kappa").getNodeValue());
        }
        ProcessMode process = getProcessMode(
            attributes.getNamedItem("Process").getNodeValue());
        NeuronType neuronType = getNeuronType(
            attributes.getNamedItem("Layer").getNodeValue());

        Neuron n =net.addNeuron( bias,
            transmitterLevel,
            receptorLevel,
            process,
            neuronType);
        n.setKappa(kappa);
        n.setAlpha(alpha);
        n.setBeta(beta);
        n.setGamma(gamma);
        n.setDelta(delta);
      }

    }while( (node = node.getNextSibling()) != null);
//System.out.println("");

// **************************************************************************
// read synapses
// **************************************************************************
    neuron_count=0;
    node = netNode.getFirstChild();
    while(!node.getNodeName().equals("Neuron"))
    {
      node = node.getNextSibling();
    }
    do
    {
      if (node.getNodeName().equals("Neuron"))
      {
        NodeList synapsesList = node.getChildNodes();
        Node synapseNode = synapsesList.item(0);
        do
        {
          if (synapseNode.getNodeType() == Document.ELEMENT_NODE)
          {
            NamedNodeMap attributes = synapseNode.getAttributes();
            int sourceNodeIndex = Integer.parseInt(
                attributes.getNamedItem("Source").getNodeValue());
            double strength = Double.parseDouble(
                attributes.getNamedItem("Strength").getNodeValue());
            ProcessMode processMode = getProcessMode(
                attributes.getNamedItem("Process").getNodeValue());

            String synapseType = null;
            if(attributes.getNamedItem("SynapseType") != null)
            {
              synapseType = attributes.getNamedItem("SynapseType").getNodeValue();
            }
            Neuron sourceNode      = net.getNeuron(sourceNodeIndex);
            Neuron destinationNode = net.getNeuron(neuron_count);
            Synapse s = net.addSynapse(sourceNode, destinationNode, strength,
                processMode);
            if(synapseType != null)
            {
              if(synapseType.equals(SynapseType.INHIBITORY.toXML()))
              {
                s.setSynapseType(SynapseType.INHIBITORY);
              }
              if(synapseType.equals(SynapseType.EXCITATORY.toXML()))
              {
                s.setSynapseType(SynapseType.EXCITATORY);
              }
            }
          }
        }while( (synapseNode = synapseNode.getNextSibling()) != null);

        neuron_count++;
      }
    }while( (node = node.getNextSibling()) != null);
    net.setSynapseMode(synapseMode);
    return net;

  }

  /**
   * Reads a net out of a population from a file. Read the netIndex-th net from
   * the populationIndex-th population from the file "filename".
   * @param    filename the name of the xml file
   * @param    populationIndex the index of the population (starts with 0)
   * @param    netIndex the index of the net in the population (starts with 0)
   * @return   null, if one of the indexes is wrong, else the net 
   */
  public Net readNetFromFile(String filename, int populationIndex, int netIndex)
  {
    read(new File(filename));
    return getNet(getNetNode(populationIndex,netIndex));
  }

  public Net readNetFromInputStream(InputStream is)
  {
    read(is);
    Node netNode = document.getElementsByTagName("Net").item(0);
    return getNet(netNode);
  }

  /*
   *  @param    xml-file
   *  @return   none
   *
   *  selftest. will read the given xml file. and print the elements of it
   */
  public static void main(String argv[])
  {
    System.out.print("Selftest of XMLHandler-class\n");
    if(argv.length < 3 )
    {
      System.out.print("usage: java Hinton.IO.XMLHandler <filename> <pop_index>"
          +" <net_index>\n");
      System.exit(-1);
    }

    XMLHandler reading = new XMLHandler();
    /*
       Net net = new Net();
       reading.read(new File(argv[0]));
       Node netNode = reading.getNetNode(1,0);
       do 
       {
       if (netNode.getNodeName().equals("Net"))
       {
       net = reading.getNet(netNode); // later Net
       }
       }while((netNode = netNode.getNextSibling())!=null);
     */
    System.out.println("your command:\n" + reading.readNetFromFile(argv[0],
          Integer.parseInt(argv[1]), Integer.parseInt(argv[2])).toString());
  }

};
