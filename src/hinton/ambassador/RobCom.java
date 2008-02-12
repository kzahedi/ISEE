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
 * real robot (using the AmbassadorInterface).
 *
 * Every communication class should inherit from this class and implement
 * all abstract methods defined here. Please have a look at all protected
 * variables which you will need to use in your communication class.
 *
 * Important: If you create a constructor in your inherited class, you MUST call
 * the super()-constructor for correct initialisation of the communication!
 *
 */
public abstract class RobCom implements AmbassadorInterface
{
  /** contains all robot informations and MUST be updated each step */
  protected RobotStruct      robotStruct = new RobotStruct();
  /** contains all process-parameters */
  protected ProcessParameter processParameter = null;

  private Document           document;

  /**
   * Constructor wich MUST be called if it is overidden! So if you create your
   * own constructor, you must call super() before you do anything in it.
   */
  public RobCom()
  {
    extractRobotStruct();
  }

  /**
   * Does nothing because it is not needed for real robots.
   */
  public void setNewStartPosition()
  {
  }

  /**
   * Does nothing because it is not needed for real robots.
   */
  public void setPortIP(int port, String ip)
  {
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
   * Does nothing because it is not needed for real robots.
   */
  public void nextTry()
  {
  }

  /**
   * Does nothing because it is not needed for real robots.
   */
  public void setSpeedFactor(double speedFactor)
  {
  }

  /**
   * Does nothing because it is not needed for real robots.
   */
  public void reset()
  {
  }

  public abstract void update(RobotStruct robotStruct);

  public void update()
  {
    update(robotStruct);
  }

  public RobotStatus getRobotStatus()
  {
    return RobotStatus.OK;
  }

  /**
   * return always 0 because it is not needed for real robots.
   *
   * @return alway 0
   */
  public int getPreferredPort()
  {
    return 0;
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
      return getRobotName();
    }
    return new String("[" + getRobotName() + "]");
  }

  /**
   * Close the connection to the real robot.
   */
  public void close()
  {
    stop(); // first stop the robot ...
    closeConnection(); // ... then close the connection
  }

  /** 
   * Return the robotstruct
   * @see hinton.ambassador.RobotStruct
   */
  public RobotStruct robotStruct()
  {
    return robotStruct;
  }

  /**
   * inits the xml-reader, for the RobotStruct-settings
   * @see hinton.ambassador.RobotStruct
   */
  private void extractRobotStruct()
  {
    System.out.println(getName() + " looking for " + getCleanConfigFileName());
    try 
    {
      File file = new File(getCleanConfigFileName());
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setValidating(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      document = builder.parse( file );
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
      return;
    }

    getInputParameter();
    getOutputParameter();

  }

  private String getCleanConfigFileName()
  {
    String s;

    s = getConfigFileName();
    s.replace('/', File.separatorChar);
    s.replace('\\', File.separatorChar);

    return s;  
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

  // -----------------------------------------------------------------------------
  //    Name:     
  //
  //    Function:  gets the data from the xml file
  //
  //    Parameters      Flow    Description
  // -----------------------------------------------------------------------------
  // -----------------------------------------------------------------------------
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
   * Here the inherited class should return the path and name of the
   * config-XML-file.
   */
  public abstract String getConfigFileName();

  /**
   * Here the inherited class should do a clean closing of the connection.
   */
  public abstract void closeConnection();

  /**
   * Here the inherited class should return the robots name.
   */
  public abstract String getRobotName();


}
