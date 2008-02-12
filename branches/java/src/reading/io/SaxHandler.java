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


package reading.io;

import java.io.FileReader;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import reading.Entry;


public class SaxHandler extends DefaultHandler
{
  private final static String SEARCH_FOR_GENERATION = "Generation";
  private final static String SEARCH_FOR_POPULATION = "Population";
  private final static String SEARCH_FOR_INDIVIDUAL = "EvoObject";

  private NumberFormat numberFormat = NumberFormat.getInstance(Locale.UK);

  private int currentNetIndex = -1;
  private int currentGenerationIndex = -1;
  private int currentPopulationIndex = -1;
  private int currentPinId = -1;

  private double currentSysPerformance = 0;
  private double currentOutPerformance = 0;
  private int currentAge = 0;

  private XMLReader xmlReader = null;

  private int _searchIndex = 0;

  private Vector returnVector = null;

  private Entry currentEntry = null;

  private int numberOfNeuron   = 0;
  private int numberOfSynapses = 0;
  
  public SaxHandler()
  {
    super();
    numberFormat.setMinimumFractionDigits(5);
    numberFormat.setMaximumFractionDigits(5);
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

  public Vector readFile(String filename)
  {
    returnVector = new Vector();
    try
    {
      xmlReader.parse(new InputSource(new FileReader(filename)));
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

    return returnVector;

  }

  public void startDocument()
  {
    //System.out.println("start document");
  }

  public void endDocument()
  {
    //System.out.println("end document");
  }

  public void startElement(String uri, String name, String qName, Attributes
      attributes)
  {
    if(qName.equals("Generation"))
    {
      currentGenerationIndex = Integer.parseInt(attributes.getValue("Index"));
      //elementNode = new DefaultMutableTreeNode("Generation " +
          //currentGenerationIndex);
    }

    if(qName.equals("Population"))
    {
      currentPopulationIndex = Integer.parseInt(attributes.getValue("Index"));
      //elementNode = new DefaultMutableTreeNode("Generation " +
          //currentGenerationIndex);
    }


    if(qName.equals("EvoObject"))
    {
      currentSysPerformance = Double.parseDouble(attributes.getValue("SysPerf"));
      currentOutPerformance = Double.parseDouble(attributes.getValue("OutPerf"));
      currentNetIndex = Integer.parseInt(attributes.getValue("Index"));
      currentAge = Integer.parseInt(attributes.getValue("Age"));
      currentPinId = Integer.parseInt(attributes.getValue("PIN")); 
    }

    if(qName.equals("Net"))
    {
      numberOfSynapses = 0;
      numberOfNeuron   = 0;
      currentEntry = new Entry(
          currentGenerationIndex,
          currentNetIndex,
          currentPopulationIndex,
          currentAge,
          currentSysPerformance,
          currentOutPerformance,
          currentPinId,
          0,
          0);

      returnVector.add(currentEntry);
    }

    if(qName.equals("Neuron"))
    {
      numberOfNeuron++;
    }
    if(qName.equals("Synapse"))
    {
      numberOfSynapses++;
    }

  }

  public void endElement(String uri, String name, String qName) throws
    SAXParseException
  {
    if(qName.equals("Net"))
    {
      //evoNode.add(elementNode);
      currentEntry.setNumberOfNeurons(numberOfNeuron);
      currentEntry.setNumberOfSynapses(numberOfSynapses);
    }
    if(qName.equals("Generation"))
    {
      //evoNode.add(elementNode);
    }
  }

  public String format(int i)
  {
    if(i < 1000)
    {
      return new String(" " +i);
    }
    if(i < 100)
    {
      return new String("  " +i);
    }
    if(i < 10)
    {
      return new String("   " +i);
    }
    return new String(""+i);
  }


}

