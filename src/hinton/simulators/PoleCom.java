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
* This class represents a communication between Hinton and the SingleKhepera2D
* robot.
*
*/
public class PoleCom extends SimCom
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


  private double in0; 
  private double in1; 
  private double in2; 
  private double in3; 
  private double in4; 
  private double in5; 
  private double in6; 




  public String getSimName()
  {
    return "Pole";
  }

  public String getConfigFileName()
  {
    return "xml/simulator/pole.xml";
  }

  public void stop()
  {
  }

  public void nextTry()
  {
    try
    {
      pbuf.resetBuf();
      pbuf.writeInt(COMMAND_NEXT_TRY);
      com1.writePacketBuffer(pbuf); 
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  public void setNewStartPosition()
  {
     // hier startbedingung erzeugen und in globale variablen schreiben
     // diese variablen kommen bei nextTry() zu simulator...

    try
    {
      pbuf.resetBuf();
      pbuf.writeInt(COMMAND_SET_POSITION);
      pbuf.writeInt(processParameter.cycles());
      pbuf.writeInt(processParameter.warmUpSteps());
      com1.writePacketBuffer(pbuf); 
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Connect to the simulator. Uses the DataGramCommunictaion-Class.
   * This own port is set to 7777.
   * @see util.net.DataGramCommunication
   */
  public void connect()
  {
    com1.setInetAddress(ip);
    com1.setPort(7001);
    com1.setServerPort(port);
    com1.initConnection();
    System.out.println("connected to Pole");
  }

  public void disconnect()
  {
    com1.close();
  }

  public void reset()
  {
    processParameter.net().reset();
    try
    {
      pbuf.resetBuf();
      pbuf.writeInt(COMMAND_RESET);
      com1.writePacketBuffer(pbuf); 
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Reads from the RobotStruct and communicates with the simulator.
   * @param    robotStruct the data-storage of the robot data
   * @see hinton.ambassador.RobotStruct
   */
  public void send(RobotStruct robotStruct)
  {
    robotStatus = RobotStatus.OK;
    try
    {
      pbuf.resetBuf();
      pbuf.writeInt(COMMAND_GET_DATA);
      
      //send net output as robot input      
      if(Double.isNaN(robotStruct.getInputDouble("Motor")))
      {
        robotStatus = RobotStatus.BUMPED;
        return;
      }


      pbuf.writeFloat(
          (Float.parseFloat(robotStruct.getInputValue("Motor").toString())));
      pbuf.writeFloat(
          (float)processParameter.getFitnessFunction().getFitnessValue());
      com1.writePacketBuffer(pbuf);


      // read new data
      pbuf.resetBuf();      
      com1.readPacketBuffer(pbuf);
      in0 = pbuf.readFloat();
      in1 = pbuf.readFloat();
      in2 = pbuf.readFloat();
      in3 = pbuf.readFloat();
      in4 = pbuf.readFloat();
      in5 = pbuf.readFloat();
      in6 = pbuf.readFloat();

      switch(pbuf.readInt())
      {
        case COMMAND_ROBOT_BUMPED:
          robotStatus = RobotStatus.BUMPED;
          break;
        //case COMMAND_NEXT_TRY:
        //robotStatus = RobotStatus.NEXT_TRY;
        //  nextTry();
        //  break;
        default:
          robotStatus = RobotStatus.OK;
          break;
      }

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
    robotStruct.setOutputValue("Location Cart", new Double(in0));
    robotStruct.setOutputValue("Pole Angle", new Double(in1));
    robotStruct.setOutputValue("Rate Location Cart", new Double(in2));
    robotStruct.setOutputValue("Rate Pole Angle", new Double(in3));
    robotStruct.setOutputValue("ProzentualeAuslenkungPole", new Double(in4));
    robotStruct.setOutputValue("ProzentualeAuslenkungCart", new Double(in5));
    robotStruct.setOutputValue("ForceIntegral", new Double(in6));
  }
 
}
