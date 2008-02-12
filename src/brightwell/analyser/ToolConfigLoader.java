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

package brightwell.analyser;


import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

import util.misc.IseeLogger;

public class ToolConfigLoader
{
  private static Logger log = IseeLogger.getLogger(ToolConfigLoader.class);

  private static Document document;
  private Random random = new Random();
  private HashMap toolPriorities = new HashMap();

  private final static String configFile = "xml" 
    + File.separator 
    + "config" 
    + File.separator 
    + "brightwell" 
    + File.separator 
    + "ToolPriorityConfig.xml";
  
  public ToolConfigLoader()
  {
    read();
    fillHashmap();
  }

  private void read()
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(true);
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      log.info(configFile);
      document = builder.parse( new File(configFile) );
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

  private void fillHashmap()
  {
    NodeList nodes = document.getElementsByTagName("Tool");
    for(int i=0; i < nodes.getLength(); i++)
    {
      Node node = nodes.item(i);
      NamedNodeMap nodeAttribute = node.getAttributes();
      toolPriorities.put(
          nodeAttribute.getNamedItem("name").getNodeValue(),
          new Integer(Integer.parseInt(nodeAttribute.getNamedItem("priority").getNodeValue().trim())));
    log.info("added: " + nodeAttribute.getNamedItem("name").getNodeValue() + " -- " +
          Integer.parseInt(nodeAttribute.getNamedItem("priority").getNodeValue().trim()));
    }
  }

  public int getToolPriority(String toolName)
  {
    try
    {
      return ((Integer)toolPriorities.get(toolName)).intValue();
    }
    catch(NullPointerException npe)
    {
      return 1000 + random.nextInt();
    }
  }
}
