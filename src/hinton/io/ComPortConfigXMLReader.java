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

package hinton.io;

// java stuff
import java.io.File;
import java.io.IOException;

import javax.comm.SerialPort;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class ComPortConfigXMLReader 
{
  private int baudRate = 38400;
  private int dataBits = SerialPort.DATABITS_8;
  private int stopBits = SerialPort.STOPBITS_2;
  private int parity   = SerialPort.PARITY_NONE;
  private int flowControlMode = SerialPort.FLOWCONTROL_NONE;
  private int timeout = 30000;
  private int port = 0;

  private static Document document;

  /**
   *  It is the default constructor
   *  @param    InputStream
   */
  public ComPortConfigXMLReader()
  {
  }

  public int getBaudRate()
  {
    return baudRate;
  }

  public int getDataBits()
  {
    return dataBits;
  }

  public int getStopBits()
  {
    return stopBits;
  }

  public int getParity()
  {
    return parity;
  }

  public int getTimeout()
  {
    return timeout;
  }

  public int getFlowControlMode()
  {
    return flowControlMode;
  }

  public int getPort()
  {
    return port;
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

    getElements();
  }

  public void setDataBits(int dataBitsInput)
  {
    switch(dataBitsInput)
    {
      case 5:
        dataBits = SerialPort.DATABITS_5;
        break;
      case 6:
        dataBits = SerialPort.DATABITS_6;
        break;
      case 7:
        dataBits = SerialPort.DATABITS_7;
        break;
      case 8:
        dataBits = SerialPort.DATABITS_8;
        break;
      default:
        dataBits = SerialPort.DATABITS_8;
        break;
    }
  }

  public void setStopBits(double stopBitsInput)
  {
    switch((int)(stopBitsInput*10))
    {
      case 10:
        stopBits = SerialPort.STOPBITS_1;
        break;
      case 15:
        stopBits = SerialPort.STOPBITS_1_5;
        break;
      case 20:
        stopBits = SerialPort.STOPBITS_2;
        break;
      default:
        stopBits = SerialPort.STOPBITS_2;
        break;
    }
  }

  public void setParity(String parityString)
  {
    if(parityString.toLowerCase().equals("mark"))
    {
      parity = SerialPort.PARITY_MARK;
      return;
    }
    if(parityString.toLowerCase().equals("even"))
    {
      parity = SerialPort.PARITY_EVEN;
      return;
    }
    if(parityString.toLowerCase().equals("odd"))
    {
      parity = SerialPort.PARITY_ODD;
      return;
    }
    if(parityString.toLowerCase().equals("space"))
    {
      parity = SerialPort.PARITY_SPACE;
      return;
    }
    // else
    parity = SerialPort.PARITY_NONE;
  }

  public void setFlowControlMode(String flowControlModeString)
  {
    if(flowControlModeString.toLowerCase().equals("rtscts_in"))
    {
      flowControlMode = SerialPort.FLOWCONTROL_RTSCTS_IN;
      return;
    }
    if(flowControlModeString.toLowerCase().equals("rtscts_out"))
    {
      flowControlMode = SerialPort.FLOWCONTROL_RTSCTS_OUT;
      return;
    }
    if(flowControlModeString.toLowerCase().equals("xonxoff_in"))
    {
      flowControlMode = SerialPort.FLOWCONTROL_XONXOFF_IN;
      return;
    }
    if(flowControlModeString.toLowerCase().equals("xonxoff_out"))
    {
      flowControlMode = SerialPort.FLOWCONTROL_XONXOFF_OUT;
      return;
    }
    // else
    flowControlMode = SerialPort.FLOWCONTROL_NONE;

  }
  
  private void getElements()
  {
    Node portNode = document.getElementsByTagName("Port").item(0);
    Node baudRateNode = document.getElementsByTagName("BaudRate").item(0);
    Node dataBitsNode = document.getElementsByTagName("DataBits").item(0);
    Node stopBitsNode = document.getElementsByTagName("StopBits").item(0);
    Node parityNode = document.getElementsByTagName("Parity").item(0);
    Node timeoutNode = document.getElementsByTagName("Timeout").item(0);
    Node flowControlModeNode =
      document.getElementsByTagName("FlowControl").item(0);

    port = Integer.parseInt(portNode.getFirstChild().getNodeValue().trim());
    baudRate = Integer.parseInt(baudRateNode.getFirstChild().getNodeValue().trim());
    setDataBits(Integer.parseInt(dataBitsNode.getFirstChild().getNodeValue().trim()));
    setStopBits(Double.parseDouble(stopBitsNode.getFirstChild().getNodeValue().trim()));
    setParity(parityNode.getFirstChild().getNodeValue().trim());
    timeout = Integer.parseInt(timeoutNode.getFirstChild().getNodeValue().trim());
    setFlowControlMode(flowControlModeNode.getFirstChild().getNodeValue().trim());
  }

  /*
   *  @param    xml-file
   *  @return   none
   *
   *  selftest. will read the given xml file. and print the elements of it
   */
  public static void main(String argv[])
  {
    System.out.print("Selftest of ComPortConfigXMLReader-class\n");
    if(argv.length == 0)
    {
      System.out.print("usage: java Hinton.IO.ComPortConfigXMLReader <filename>\n");
      System.exit(-1);
    }

    ComPortConfigXMLReader comConfig = new ComPortConfigXMLReader();
    comConfig.read(argv[0]);
    System.out.println("Port            " + comConfig.getPort());
    System.out.println("BaudRate        " + comConfig.getBaudRate());
    System.out.println("DataBits        " + comConfig.getDataBits());
    System.out.println("StopBits        " + comConfig.getStopBits());
    System.out.println("Parity          " + comConfig.getParity());
    System.out.println("Timeout         " + comConfig.getTimeout());
    System.out.println("FlowControlMode " + comConfig.getFlowControlMode());
  }
}
