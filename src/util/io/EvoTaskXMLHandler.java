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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;
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


public class EvoTaskXMLHandler 
{

  private static Document document;
  private Net net = new Net();

  private static LearningRuleClassLoader learningRuleClassLoader = new
    LearningRuleClassLoader();

  public EvoTaskXMLHandler()
  {
  }


  public Net readNetFromFile(String filename, int genIdx, int popIdx, 
      int rankIdx)
  {
    read(new File(filename));
    return getNet(getNetNode(genIdx,popIdx,rankIdx));
  }


  /**
   *  Reads data from an xml file an puts it into the Document-class
   *  @param    File file  - xml file
   *  @return   none
   *  @see org.w3c.dom.Document
   */
  private void read(File file)
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(true);
    try 
    {
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
    } catch (ParserConfigurationException pce) 
    {
      // Parser with specified options can't be built
      pce.printStackTrace();
    } catch (IOException ioe) 
    {
      // I/O error
      ioe.printStackTrace();
    }
  }

  private Node getNetNode(int genIdx, int popIdx, int rankIdx)
  {
    Node evoObjNode = getEvoObjectNode(genIdx,popIdx,rankIdx);
    if(evoObjNode == null) return null;

    Node netNode = evoObjNode.getFirstChild();

    while(!(netNode.getNodeName().equals("Net")))
    {
      netNode = netNode.getNextSibling();
    }

    return netNode;
  }


  private Node getEvoObjectNode(int genIdx, int popIdx, int rankIdx)
  {
    Node popNode = getPopulationNode(genIdx, popIdx);
    if(popNode == null)
    {
      return null;  
    }

    Node netNode = popNode.getFirstChild();
    if(popNode.getNodeName().equals("EvoObject"))
    {
      rankIdx--;
    }
    while(rankIdx >= 0)
    {
      netNode = netNode.getNextSibling();
      if(netNode == null) 
      {
        System.out.println("In the selected population, we only have " +
            rankIdx + " individuals");
        return null;
      }
      if(netNode.getNodeName().equals("EvoObject"))
      {
        rankIdx--;
      }
    }
    if (rankIdx >= 0) return null;
    return netNode;
  }

  private Node getPopulationNode(int genIdx, int popIdx)
  {
    int index = popIdx;

    Node genNode = getGenerationNode(genIdx);
    if(genNode == null)
    {
      System.out.println("Did not find the right generation in XML-file\n");
      return null;
    }

    if(index < 0)
    {
      System.out.println("In the population index " + index + " is out of range\n");
      return null;
    }

    Node popNode = genNode.getFirstChild();
    if (popNode.getNodeName().equals("Population"))
    {
      index--;
    }

    while(index >= 0)
    {
      popNode = popNode.getNextSibling();
      if(popNode == null) 
      {
        System.out.println("In the selected Generation, we only have " +
            index + " populations\n");
        return null;
      }
      if(popNode.getNodeName().equals("Population"))
      {
        index--;
      }
    }

    if (index > 0) return null;
    return popNode;
  }

  private Node getGenerationNode(int genIdx)
  {
    NodeList genNodes = document.getElementsByTagName("Generation");


    if (genIdx < 0)
    {
      System.out.println("generation index start at 0 " +
          "so gen. idx. " + genIdx + "is out of range\n" );
      return null;
    }

    for(int i = 0; i < genNodes.getLength(); i++){
      if(Integer.parseInt(genNodes.item(i).getAttributes().getNamedItem("Index").getNodeValue().trim()) == genIdx)
        return genNodes.item(i);
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
        double alpha = 0;
        double beta = 0;
        double gamma = 0;
        double delta = 0;
        double my = 0;
        double ny = 0;

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
          alpha =
            Double.parseDouble(
                attributes.getNamedItem("Alpha").getNodeValue());
        }
        if(attributes.getNamedItem("Beta") != null)
        {
          beta =
            Double.parseDouble(
                attributes.getNamedItem("Beta").getNodeValue());
        }
        if(attributes.getNamedItem("Gamma") != null)
        {
          gamma =
            Double.parseDouble(
                attributes.getNamedItem("Gamma").getNodeValue());
        }
        if(attributes.getNamedItem("Delta") != null)
        {
          delta =
            Double.parseDouble(
                attributes.getNamedItem("Delta").getNodeValue());
        }
        if(attributes.getNamedItem("My") != null)
        {
          my =
            Double.parseDouble(
                attributes.getNamedItem("My").getNodeValue());
        }
        if(attributes.getNamedItem("Ny") != null)
        {
          ny =
            Double.parseDouble(
                attributes.getNamedItem("Ny").getNodeValue());
        }
        ProcessMode process = getProcessMode(
            attributes.getNamedItem("Process").getNodeValue());
        NeuronType neuronType = getNeuronType(
            attributes.getNamedItem("Layer").getNodeValue());

        Neuron n = net.addNeuron( bias,
            transmitterLevel,
            receptorLevel,
            process,
            neuronType);

        n.setAlpha(alpha);
        n.setBeta(beta);
        n.setGamma(gamma);
        n.setDelta(delta);
        n.setMy(my);
        n.setNy(ny);

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
            SynapseType synapseType = getSynapseType(
                attributes.getNamedItem("SynapseType").getNodeValue());
            Neuron sourceNode      = net.getNeuron(sourceNodeIndex);
            Neuron destinationNode = net.getNeuron(neuron_count);
            Synapse s = net.addSynapse(sourceNode, 
                destinationNode, strength, processMode);
            s.setSynapseType(synapseType);
          }
        }while( (synapseNode = synapseNode.getNextSibling()) != null);

        neuron_count++;
      }
    }while( (node = node.getNextSibling()) != null);
    net.setSynapseMode(synapseMode);
    return net;
  }

  private SynapseType getSynapseType(String synapseType)
  {
    if(synapseType == null || synapseType.equals(SynapseType.INHIBITORY.toXML()))
    {
      return SynapseType.INHIBITORY;
    }
    return SynapseType.EXCITATORY;
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


  public String getEvoGrammarXML(){
    String s = new String();

    s = s.concat("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

    s = s.concat("<!DOCTYPE Evolution [\n");

    s = s.concat("<!ELEMENT Evolution (Generation+)>\n");
    s = s.concat("<!ELEMENT Generation (Population+)>\n");
    s = s.concat("<!ATTLIST Generation\n");
    s = s.concat("          Index       CDATA #REQUIRED>\n");

    s = s.concat("<!ELEMENT Population (Comment?,EvoObject+)>\n");
    s = s.concat("<!ATTLIST Population\n");
    s = s.concat("          Name            CDATA #REQUIRED\n");
    s = s.concat("          Index           CDATA #REQUIRED>\n");

    s = s.concat("<!ELEMENT Comment (#PCDATA)>\n");
    s = s.concat("<!ELEMENT EvoObject (Net+)>\n");
    s = s.concat("<!ATTLIST EvoObject \n");
    s = s.concat("          Index           CDATA #REQUIRED\n");
    s = s.concat("          OutPerf         CDATA #REQUIRED\n");
    s = s.concat("          SysPerf         CDATA #REQUIRED\n");
    s = s.concat("          Age             CDATA #REQUIRED\n");
    s = s.concat("          PIN             CDATA #IMPLIED\n");
    s = s.concat("          ParentsPIN      CDATA #IMPLIED>\n");


    s = s.concat(net.getXMLGrammar());

    s = s.concat("]>\n");
    return s;
  }


  public boolean writeNetToFile(Net net, String filename, String comment)
  {
    File xmlFile = new File(filename);
    this.net = net;
    try
    {
      PrintWriter out = new PrintWriter(
          new OutputStreamWriter(
            new FileOutputStream(xmlFile)));

      out.println(getEvoGrammarXML());
      out.println("<Evolution>");
      out.println("  <Generation Index=\"0\">");
      out.println("    <Population Name=\"pop1\" Index=\"0\">");
      out.println("      <Comment>");
      out.println("        " + comment);
      out.println("        date: " + 
          (new  Date(System.currentTimeMillis())).toString());
      out.println("      </Comment>");
      out.println("      <EvoObject  Index=\"0\" OutPerf=\"0.0\" SysPerf=\"0.0\" " +
          "Age=\"0\" PIN=\"1\" ParentsPIN=\"0\" >");
      out.println(net.toXML(8));
      out.println("      </EvoObject>");
      out.println("    </Population>");
      out.println("  </Generation>");
      out.println("</Evolution>");

      out.close();
      return true;
    }
    catch(FileNotFoundException ex)
    {
      ex.printStackTrace();
      return false;
    }
  }


  public static void main(String argv[])
  {
    EvoTaskXMLHandler set = new EvoTaskXMLHandler();  


    Net n = set.readNetFromFile(argv[0], Integer.parseInt(argv[1]), 0, 0);

    if(n == null)
      System.out.println("no net\n");
    else
      System.out.println(n.toXML());


    System.out.println("Writing " + "/tmp/test_" + argv[0]);
    set.writeNetToFile(n, "/tmp/test_" + argv[0], "TEST");

  }


}











