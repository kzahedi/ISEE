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

package hinton.ambassador;

import hinton.executive.ProcessParameter;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


/**
 * This class represents the abstract container of a communication to a
 * simulator (using the AmbassadorInterface).
 *
 * Every communication class should inherit from this class and implement
 * all abstract methods defined here. Please have a look at all protected
 * variables which you will need to use in your communication class.
 *
 * Important: If you create a constructor in your inherited class, you MUST call
 * the super()-constructor for correct initialisation of the communication!
 *
 */
public abstract class SimCom implements AmbassadorInterface
{
 
  /** contains all robot informations and MUST be updated each step */
  protected RobotStruct      robotStruct = new RobotStruct();
  /** contains the state of the robot and MUST be updated each step */
  protected RobotStatus      robotStatus = RobotStatus.OK;
  /** contains all process-parameters */
  protected ProcessParameter processParameter = null;  
  /** contains the port (TCP/UDP) for your connection */
  protected int              port = 0;
  /** contains the IP (TCP/UDP) for your connection */
  protected String           ip = null;
  /** contains the speed value entered in the hinton GUI */
  protected double           speedFactor;

  private Document           document;
  private int                preferredPort = 0;

  /**
   * Constructor wich MUST be called if it is overidden! So if you create your
   * own constructor, you must call super() before you do anything in it.
   */
  public SimCom()
  {
    extractRobotStruct();
  }

  /**
   * Return the robot state. 
   */
  public RobotStatus getRobotStatus()
  {
    return robotStatus;
  }

  /**
   * Sets the process parameters.
   *
   * @param processParameter are the new process parameters
   */
  public void setProcessParameter(ProcessParameter processParameter)
  {
    this.processParameter = processParameter;
  }
  
  /**
   * Sets the speed factor. 
   *
   * @param speedFactor is the new speed factor
   */
  public void setSpeedFactor(double speedFactor)
  {
    this.speedFactor = speedFactor;
  }

  /**
   * Sets the speed factor. 
   */
  public int getPreferredPort()
  {
    return preferredPort;
  }

  /**
   * Close your communication. This method actually does nothing because the GUI
   * does not support any connection-closing to simulators. You may
   * override it if you need to close your connections. 
   */
  public void close()
  {
  }

  /**
   * Set the IP-communication stuff.
   */
  public void setPortIP(int port, String ip)
  {
    this.port = port;
    this.ip = ip;
  }

  /**
   * This method is call each timestep. It calls first send() and then update()
   * in your inherited class.
   */
  public final void update()
  {
    send(robotStruct);
    update(robotStruct);
  }
 
  /**
   * Inits the xml-reader.
   */
  private void extractRobotStruct()
  {
    System.out.print(getName() + " looking for " + getCleanConfigFileName());
    try
    {
      File file = new File(getCleanConfigFileName());
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setValidating(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      document = builder.parse( file );
      System.out.println(" ... found");
    } 
    catch (SAXException sxe) 
    {
      // Error generated during parsing
      Exception  x = sxe;
      if (sxe.getException() != null)
      {
        x = sxe.getException();
      }
      x.printStackTrace();
    } 
    catch (ParserConfigurationException pce)
    {
      // Parser with specified options can't be built
      pce.printStackTrace();
    } 
    catch (IOException ioe) 
    {
      // I/O error
      //ioe.printStackTrace();
      System.out.println(" ... not found");
      return;
    }

    getInputParameter();
    getOutputParameter();
    getPortNumber();
    
  }

  private String getCleanConfigFileName()
  {
    String s;

    s = getConfigFileName();
    s.replace('/', File.separatorChar);
    s.replace('\\', File.separatorChar);

    return s;  
  }

  private void getPortNumber()
  {
    Node node = document.getElementsByTagName("CommunicationPort").item(0);
    preferredPort = Integer.parseInt(node.getFirstChild().getNodeValue().trim());
  }
  /**
   * reads the input parameter out of the document-tree
   * @param    
   * @return   
   */
  private void getInputParameter()
  {
    Node node = document.getElementsByTagName("InputParameter").item(0);
    Node parameter = node.getFirstChild();
    while(parameter != null)
    {
      if(parameter.getNodeName().equals("Parameter"))
      {
        robotStruct.addInputKey(
            parameter.getAttributes().getNamedItem("Name").getNodeValue());
      }
      parameter = parameter.getNextSibling();
    }
  }
  
  private void getOutputParameter()
  {
    Node node = document.getElementsByTagName("OutputParameter").item(0);
    Node parameter = node.getFirstChild();
    while(parameter != null)
    {
      if(parameter.getNodeName().equals("Parameter"))
      {
        robotStruct.addOutputKey(
            parameter.getAttributes().getNamedItem("Name").getNodeValue());
      }
      parameter = parameter.getNextSibling();
    }
  }

  /** 
   * Return the robotstruct
   * @see hinton.ambassador.RobotStruct
   */
  public RobotStruct getStruct()
  {
    return robotStruct;
  }

  /**
   * Returns the name of the simulator and adds [] if there was no XML-file
   * found for this simulator.
   */
  public String getName()
  {
    if( robotStruct.inputKeysSize() > 0 &&
        robotStruct.outputKeysSize() > 0)
    {
      return getSimName();
    }
    return new String("[" + getSimName() + "]");
  }

  /** 
   * Return the robotstruct.
   * @see hinton.ambassador.RobotStruct
   */
  public RobotStruct robotStruct()
  {
    return robotStruct;
  }

  /**
   * Returns always the name of the simulator.
   */
  public String name()
  {
    return getSimName();
  }

  /**
   * Here the inherited class should return the simulators name.
   */
  public abstract String getSimName();

  /**
   * Here the inherited class should return the path and name of the
   * config-XML-file.
   */
  public abstract String getConfigFileName();

  /**
   * Here the inherited class should update all simulation data (see example),
   * before the send()-method will send all communication data.
   */
  public abstract void update(RobotStruct roboStruct);

  /**
   * Here the inherited class should send all communication data (see examples);
   * this is called after the update()-method.
   */
  public abstract void send(RobotStruct roboStruct);
  

}
