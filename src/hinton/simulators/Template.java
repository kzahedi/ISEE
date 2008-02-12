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


package hinton.simulators;

import hinton.ambassador.RobotStatus;
import hinton.ambassador.RobotStruct;
import hinton.ambassador.SimCom;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import util.net.DataGramCommunication;
import util.net.PacketBuffer;



/**
* This class represents a Communication Template 
*/
public class Template extends SimCom 
{

  // Important:
  // ----------
  // YOU HAVE GOT ACCESS TO THE FOLLOWING VARIABLES:
  //
  // RobotStruct       robotStruct;
  // RobotStatus       robotStatus;
  // int               port;
  // String            ip;
  // ProcessParameters processParameters;
  // double            speedFactor;
  

  private DataGramCommunication com1 = new
    DataGramCommunication(DataGramCommunication.CLIENT);
  private DataInputStream in = null;
  private DataOutputStream out = null;
  private PacketBuffer pbuf = new PacketBuffer();

  private Random random = new Random();

  private final static int COMMAND_GET_DATA = 0;
  private final static int COMMAND_RESET = 1;
  private final static int COMMAND_SET_POSITION = 2;
  // for binary checks use 2^n
  private final static int COMMAND_ROBOT_OK               = 3;
  private final static int COMMAND_ROBOT_BUMPED           = 4;
  private final static int COMMAND_NEXT_TRY               = 5;

  public String getSimName()
  {
    return "Template";
  }

  public String getConfigFileName()
  {
    return "xml/simulator/template.xml";
  }

  public void stop()
  {
  }

  public void nextTry()
  {
  }

  public void setNewStartPosition()
  {
  }

  /**
   * Connect to the GDM RoboCup-Simulator. Uses the DataGramCommunictaion-Class.
   * The own port is set to 7020.
   * @see util.net.DataGramCommunication
   */
  public void connect()
  {
    com1.setInetAddress(ip);
    com1.setPort(7020);
    com1.setServerPort(port);
    com1.initConnection();
    System.out.println("connected to template");
  }

  public void disconnect()
  {
    com1.close();
  }

  public void reset()
  {
  }

  /**
   * Reads from the RobotStruct and communicates with the simulator.
   * @param    robotStruct the data-storage of the robot data
   * @see hinton.ambassador.RobotStruct
   */
  public void send(RobotStruct robotStruct)
  {
    int i;
    
    pbuf.resetBuf();
    robotStatus = RobotStatus.OK;
    try
    {
      for (i=0; i< 100; i++)
      {
        pbuf.writeInt(i);
      }
      for (i=0; i< 100; i++)
      {
        pbuf.writeFloat((float) (i + 0.001*i));
      }
      com1.writePacketBuffer(pbuf);
      System.out.println(pbuf.getBufSize() +" Bytes written.");
      System.out.println("----------------------------------");
      System.out.println("Reading back:");
      com1.readPacketBuffer(pbuf);
      for (i=0; i< 100; i++)
      {
        System.out.println("Read int  : "+pbuf.readInt());
      }
      System.out.println("----------------------------------");
      for (i=0; i< 100; i++)
      {
        System.out.println("Read float: "+pbuf.readFloat());
      }
      System.out.println("**********************************");
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Fills the RobotStruct with the current data.
   * @param    robotStruct is filled with the current values
   */
  public void update(RobotStruct robotStruct)
  {
  }

}
