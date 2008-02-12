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

import java.util.Vector;

import util.net.DataGramCommunication;
import util.net.PacketBuffer;


public class YarsCom extends SimCom 
{
  private DataGramCommunication com = new DataGramCommunication(
      DataGramCommunication.CLIENT);
  private PacketBuffer pbuf = new PacketBuffer();

  /** communication packet ids */
  private final static int COM_END              =  -1;
  private final static int COM_ID_PING_REQ      =   0;
  private final static int COM_ID_PING_ACK      =   1;
  private final static int COM_ID_HANDSK_REQ    =  10;
  private final static int COM_ID_HANDSK_ACK    =  11;
  private final static int COM_ID_SENSORS_REQ   =  20;
  private final static int COM_ID_SENSORS_ACK   =  21;
  private final static int COM_ID_MOTORS_REQ    =  30;
  private final static int COM_ID_MOTORS_ACK    =  31;
  private final static int COM_ID_DATA_REQ      =  40;
  private final static int COM_ID_DATA_ACK      =  41;
  private final static int COM_ID_RESET_REQ     =  50;
  private final static int COM_ID_RESET_ACK     =  51;
  private final static int COM_ID_INIT_REQ      =  60;
  private final static int COM_ID_INIT_ACK      =  61;

  /** handshake packet tags */
  private final static int COM_HS_TAG_ROBOT     =   0;
  private final static int COM_HS_TAG_SEGCHAIN  = 100;
  private final static int COM_HS_TAG_SEGMENT   = 200;
  private final static int COM_HS_TAG_MOTOR     = 300;
  private final static int COM_HS_TAG_SENSOR    = 400;
 
  /** names for input und output parameters (see readParamNames()) */
  private static String PARAM_NAME_ROBOT    = "Robot";
  private static String PARAM_NAME_SEGCHAIN = "Chain";
  private static String PARAM_NAME_SEGMENT  = "Seg";
  private static String PARAM_NAME_SENSOR   = "Sensor";
  private static String PARAM_NAME_MOTOR    = "Motor";
  
  private boolean namesRead = false;
  private Vector motorName;
  private Vector sensorName;
  private int robot;

  private int simTime = 0;
  private cholsey.Net net;  

 
  /**
   * Return the name of the simulator.
   */
  public String getSimName()
  {
    return "Yars";
  }

  /**
   * Return the path an filename of the XML-file
   */
  public String getConfigFileName()
  {
    return "xml/simulator/yars.xml";
  }

  /**
   * Is called when the simulation will be stopped. 
   */
  public void stop()
  {
  }

  /**
   * Is called for the next try. 
   */
  public void nextTry()
  {
    reset();
  }

  /**
   * Init Robot.
   */
  public void setNewStartPosition()
  {
    pbuf.resetBuf();
    pbuf.writeInt(COM_ID_INIT_REQ);    
    pbuf.writeInt(robot);    
    pbuf.writeInt(processParameter.nextInt());    
    pbuf.writeInt(COM_END);    
    try
    {
      com.writePacketBuffer(pbuf);
    } 
    catch (Exception ex)
    {
      System.out.println("Communication IO error (init)! Stop simulation.");
      ex.printStackTrace();
      return;
    }
    try
    {
      pbuf.resetBuf();
      com.readPacketBuffer(pbuf);
      if (pbuf.readInt() != COM_ID_INIT_ACK)
      {
        throw new Exception("NO INIT ACK RECIEVED!");
      }
    }
    catch (Exception ex)
    {
      System.out.println("Communication IO error (init)! Stop simulation.");
      ex.printStackTrace();
    }
  }

  /**
   * Parse handshake packet an gives the names to the neurons
   */
  private void parseHandshake(PacketBuffer pbuf) throws Exception
  {
    int i;
    
    i = pbuf.readInt();
    if (i != COM_ID_HANDSK_ACK)
    {
      throw new Exception("\n\nNO HANDSHAKE ACK RECIEVED! (" + i 
          + "). This simulator port is already in use with an other Hinton!\n\n\n" );
    }

    sensorName = new Vector();
    motorName  = new Vector();
   
    subParseHandshake(pbuf, -1, -1, -1, -1, -1);

    rebuildRobotStruct();
    
    System.out.println("\n This HINTON manages robot number: " + robot);
  }

  private void readParamNames()
  {
    if (namesRead)
    {
      return;
    }
    namesRead = true;

    PARAM_NAME_ROBOT    = robotStruct.getInputObject(0);
    PARAM_NAME_SEGCHAIN = robotStruct.getInputObject(1);
    PARAM_NAME_SEGMENT  = robotStruct.getInputObject(2);
    PARAM_NAME_SENSOR   = robotStruct.getInputObject(3);
    PARAM_NAME_MOTOR    = robotStruct.getInputObject(4);
  }
  
  private void rebuildRobotStruct()
  {
    int i;

    readParamNames();

    robotStruct.removeAllInputKeys();
    robotStruct.removeAllOutputKeys();
    
    for (i=0; i<sensorName.size(); i++)
    {
      robotStruct.addOutputKey((String) sensorName.elementAt(i));
      System.out.println("Adding \"" 
          + (String) sensorName.elementAt(i) 
          + "\" as output key");
    }
    for (i=0; i<motorName.size(); i++)
    {
      robotStruct.addInputKey((String) motorName.elementAt(i));
      System.out.println("Adding \"" 
          + (String) motorName.elementAt(i) 
          + "\" as input key");
    }

    // robotStruct keys must NEVER be empty
    if (motorName.size() == 0)
    {
      robotStruct.addInputKey("nothingIn");
    }

    if (sensorName.size() == 0)
    {
      robotStruct.addOutputKey("nothingOut");
    }
  }

  /**
   * Recursive sub parsing handshake packet
   */
  private void subParseHandshake(PacketBuffer pbuf, int robot, 
      int segchain, int segment, int motor, int sensor) throws Exception
  {
    int i;

    i = pbuf.readInt();

    if (i == COM_END)
    {
      // ok, if we only have ONE robot -> delete robot-text in all names
      // the same for bodies
//      if (robot == 0)
//      {
//        for (i=0; i<sensorName.size(); i++)
//        {
//          sensorName.setElementAt(((String) sensorName.elementAt(i)).
//              replaceAll(PARAM_NAME_ROBOT + "0", ""), i);
//        }
//        for (i=0; i<motorName.size(); i++)
//        {
//          motorName.setElementAt(((String) motorName.elementAt(i)).
//              replaceAll(PARAM_NAME_ROBOT + "0", ""), i);
//        }
//      }
      return;
    }
    
    if (i == COM_HS_TAG_ROBOT)
    {
      System.out.println("\n" + PARAM_NAME_ROBOT + (robot+1));
      subParseHandshake(pbuf, robot + 1, -1, -1, -1, -1);
      return;
    }

    if (i == COM_HS_TAG_SEGCHAIN)
    {
      System.out.println("   " + PARAM_NAME_SEGCHAIN + (segchain+1));
      subParseHandshake(pbuf, robot, segchain + 1, -1, -1, -1);
      return;
    }

    if (i == COM_HS_TAG_SEGMENT)
    {
      System.out.println("      " + PARAM_NAME_SEGMENT + (segment+1));
      subParseHandshake(pbuf, robot, segchain, segment + 1, -1, -1);
      return;
    }
   
    if (i == COM_HS_TAG_MOTOR)
    {
      System.out.println("         " + PARAM_NAME_MOTOR + (motor+1));
      motorName.addElement(""
          //+ PARAM_NAME_ROBOT + robot
          + PARAM_NAME_SEGCHAIN + segchain
          + PARAM_NAME_SEGMENT + segment
          + PARAM_NAME_MOTOR + (motor + 1));
      subParseHandshake(pbuf, robot, segchain, segment, motor + 1, sensor);
      this.robot = robot;
      return;
    }

    if (i == COM_HS_TAG_SENSOR)
    {
      System.out.println("         " + PARAM_NAME_SENSOR + (sensor+1));
      sensorName.addElement(""
          //+ PARAM_NAME_ROBOT + robot
          + PARAM_NAME_SEGCHAIN + segchain
          + PARAM_NAME_SEGMENT + segment
          + PARAM_NAME_SENSOR + (sensor + 1));
      subParseHandshake(pbuf, robot, segchain, segment, motor, sensor + 1);
      this.robot = robot;
      return;
    }
  }
  
  /**
   * Is called when the robot will be connected. 
   */
  public void connect()  
  {
    int i;
   
    // init communication
    System.out.print("Connecting to YARS-ODE simulator..."); 
    com.setInetAddress(ip);
    com.setPort(7020);
    com.setServerPort(port);
    com.initConnection();
    System.out.println("... ok."); 

    // do handshake and init robotStruct
    System.out.print("Handshake: receiving robot construction information..."); 
    pbuf.resetBuf();
    pbuf.writeInt(COM_ID_HANDSK_REQ);    
    pbuf.writeInt(COM_END);    
    try
    {
      System.out.print("Handshake: send COM_ID_HANDSK_REQ"); 
      com.writePacketBuffer(pbuf);
      pbuf.resetBuf();
      System.out.print("Handshake: reading answer");
      com.readPacketBuffer(pbuf);

      System.out.print("Handshake: parsing answer");
      parseHandshake(pbuf); 
    } 
    catch (Exception ex)
    {
      System.out.println("... FAILED: IOERROR"); 
      ex.printStackTrace();
      return;
    }
    
    System.out.println("... ok."); 
  }

  public void disconnect()
  {

  }

  /**
   * Init Robot.
   */
  public void reset()
  {
    pbuf.resetBuf();
    pbuf.writeInt(COM_ID_RESET_REQ);    
    pbuf.writeInt(robot);    
    pbuf.writeInt(COM_END);    
    try
    {
      com.writePacketBuffer(pbuf);
    } 
    catch (Exception ex)
    {
      System.out.println("Communication IO error (reset)! Stop simulation.");
      ex.printStackTrace();
      return;
    }
    try
    {
      pbuf.resetBuf();
      com.readPacketBuffer(pbuf);
      if (pbuf.readInt() != COM_ID_RESET_ACK)
      {
        throw new Exception("NO RESET ACK RECIEVED!");
      }
    }
    catch (Exception ex)
    {
      System.out.println("Communication IO error (reset)! Stop simulation.");
      ex.printStackTrace();
    }
    robotStruct.reset();
    simTime = 0;
  }

  /**
   * Here you should update all simulation data (see example),
   * before the send()-method will send all communication data.
   */
  public void send(RobotStruct robotStruct)
  {
    int i;

    //check for NaN
    if(motorName != null)
    {
      for (i=0; i<motorName.size(); i++) // send all motors
      {
        if(Double.isNaN(
              robotStruct.getInputDouble(
                (String)motorName.elementAt(i))))
        {
          robotStatus = RobotStatus.BUMPED;
          return;
        }
      }
    }

    if(simTime < 1)
    {
      net = processParameter.net();
      for(net.neurons().start();net.neurons().hasMore();net.neurons().next())
      {
        net.neurons().neuron().setActivation(0);
      }
    }

    simTime++;
    try
    {
      pbuf.resetBuf();
      pbuf.writeInt(COM_ID_DATA_REQ);    // packet id
      pbuf.writeInt(robot);                  // robot number
      pbuf.writeInt(motorName.size());   // amount of motors
      for (i=0; i<motorName.size(); i++) // send all motors
      {
        pbuf.writeFloat((float)(robotStruct.getInputDouble(
                ((String)motorName.elementAt(i)))));

        //System.out.println((float)(robotStruct.getInputDouble(
        //        ((String)motorName.elementAt(i)))));
      }
      pbuf.writeInt(COM_END);    
      com.writePacketBuffer(pbuf);
    }
    catch (Exception ex)
    {
      System.out.println("Communication IO error (send)! Stop simulation.");
      ex.printStackTrace();
    }
  }

  /**
   * Here you should send all communication data (see examples);
   * this is called after the update()-method.
   */
  public void update(RobotStruct robotStruct)
  {
    int i;
     //check for NaN
    if(motorName != null)
    {
      for (i=0; i<motorName.size(); i++) // send all motors
      {
        if(Double.isNaN(
              robotStruct.getInputDouble(
                (String)motorName.elementAt(i))))
        {
          robotStatus = RobotStatus.BUMPED;
          return;
        }
      }
    }

 
    robotStatus = RobotStatus.OK;

    try
    {
      pbuf.resetBuf();
      com.readPacketBuffer(pbuf);

      if (pbuf.readInt() != COM_ID_DATA_ACK) // correct packet
      {
        throw new Exception("No DATA ACK recieved!");
      }

      int robotID = pbuf.readInt();
      if (robotID != robot) // robot number correct?
      {
        throw new Exception("WRONG ROBOT NUMBER ID: expected: " 
            + robot + " received: " + robotID );   
      }

      if (pbuf.readInt() != 0) // robot status
      {
        robotStatus = RobotStatus.BUMPED;
      }

      int sensorSize = pbuf.readInt();
      if (sensorSize != sensorName.size()) // sensor amount  correct?
      {
        throw new Exception("UNEXPECTED SENSOR SIZE. expected: " + sensorName.size() + " received: " + sensorSize);   
      }

      for (i=0; i<sensorName.size(); i++) // recieve all sensors
      {
        robotStruct.setOutputValue(((String) sensorName.elementAt(i)), 
            new Double((double) pbuf.readFloat()));
      }
    }
    catch (Exception ex)
    {
      System.out.println("Communication IO error (read)! Stop simulation.");
      ex.printStackTrace();
    }
  }
}
