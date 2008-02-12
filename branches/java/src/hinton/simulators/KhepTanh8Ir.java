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
public class KhepTanh8Ir extends SimCom 
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


  private double distance0; 
  private double distance1; 
  private double distance2; 
  private double distance3; 
  private double distance4; 
  private double distance5; 
  private double distance6; 
  private double distance7; 
  private double light0; 
  private double light1; 
  private double light2; 
  private double light3; 
  private double light4; 
  private double light5; 
  private double light6; 
  private double light7; 




  public String getSimName()
  {
    return "Khep. Tanh 8Ir 8Ldr";
  }

  public String getConfigFileName()
  {
    return "xml/simulator/single_khepera_2d_dyn.xml";
  }

  public void stop()
  {
  }

  public void nextTry()
  {
    reset();
  }

  public void setNewStartPosition()
  {
     // hier startbedingung erzeugen und in globale variablen schreiben
     // diese variablen kommen bei nextTry() zu simulator...

    try
    {
      pbuf.resetBuf();
      pbuf.writeInt(COMMAND_SET_POSITION);
      
      // hier das INITZEUG was in setNewStartPosition erzeugt wurde!
      //pbuf.writeFloat((float)startvalue1);
      //pbuf.writeFloat((float)processParameter.getMaxSpeed());
      
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
    com1.setPort(10000);
    com1.setServerPort(port);
    com1.initConnection();
    System.out.println("connected to Single Khepera 2D");
  }

  public void disconnect()
  {
    com1.close();
  }

  public void reset()
  {
    try
    {
      pbuf.resetBuf();
      pbuf.writeInt(COMMAND_RESET);
      
      // hier das INITZEUG was in setNewStartPosition erzeugt wurde!
      pbuf.writeFloat((float) 
          (processParameter.getFitnessFunction()).getFitnessValue() );
      
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
   * @see Hinton.Ambassador.RobotStruct
   */
  public void send(RobotStruct robotStruct)
  {
    double motorLeft, motorRight;

    robotStatus = RobotStatus.OK;
    try
    {
      pbuf.resetBuf();
      pbuf.writeInt(COMMAND_GET_DATA);
      
      //send net output as robot input      
      motorLeft = Double.parseDouble(robotStruct.getInputValue("MotorLeft").toString());
      motorRight = Double.parseDouble(robotStruct.getInputValue("MotorRight").toString());


      // postprocessing  (0,1) --> (-1,1) 
      //motorLeft = (2.0 * motorLeft) - 1.0;
      //motorRight = (2.0 * motorRight) - 1.0;


  
      pbuf.writeFloat((float)(speedFactor * motorLeft));
      pbuf.writeFloat((float)(speedFactor * motorRight));
      com1.writePacketBuffer(pbuf);


      // read new data
      pbuf.resetBuf();      
      com1.readPacketBuffer(pbuf);
      distance0 = pbuf.readFloat();
      distance1 = pbuf.readFloat();
      distance2 = pbuf.readFloat();
      distance3 = pbuf.readFloat();
      distance4 = pbuf.readFloat();
      distance5 = pbuf.readFloat();
      distance6 = pbuf.readFloat();
      distance7 = pbuf.readFloat();
      light0    = pbuf.readFloat();
      light1    = pbuf.readFloat();
      light2    = pbuf.readFloat();
      light3    = pbuf.readFloat();
      light4    = pbuf.readFloat();
      light5    = pbuf.readFloat();
      light6    = pbuf.readFloat();
      light7    = pbuf.readFloat();

      // mapping (-1,1) --> (0,1) 
      /*distance0 = 0.5*(1.0 + distance0);
      distance1 = 0.5*(1.0 + distance1);
      distance2 = 0.5*(1.0 + distance2);
      distance3 = 0.5*(1.0 + distance3);
      distance4 = 0.5*(1.0 + distance4);
      distance5 = 0.5*(1.0 + distance5);
      distance6 = 0.5*(1.0 + distance6);
      distance7 = 0.5*(1.0 + distance7);
      light0    = 0.5*(1.0 + light0);
      light1    = 0.5*(1.0 + light1);
      light2    = 0.5*(1.0 + light2);
      light3    = 0.5*(1.0 + light3);
      light4    = 0.5*(1.0 + light4);
      light5    = 0.5*(1.0 + light5);
      light6    = 0.5*(1.0 + light6);
      light7    = 0.5*(1.0 + light7);*/


      /*distance0 = (distance0 + distance1 + distance2) / 3.0;
      distance1 = (distance3 + distance4 + distance5) / 3.0;
      distance2 = (light0 + light1) / 2.0;
      distance3 = (light2 + light3) / 2.0;
      distance4 = (light4 + light5) / 2.0;
      distance5 = (light6 + light7) / 2.0;*/

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
    robotStruct.setOutputValue("Distance0",
             new Double(distance0)); // normed to -1;1
    robotStruct.setOutputValue("Distance1",
             new Double(distance1)); // normed to -1;1
    robotStruct.setOutputValue("Distance2",
             new Double(distance2)); // normed to -1;1
    robotStruct.setOutputValue("Distance3",
             new Double(distance3)); // normed to -1;1
    robotStruct.setOutputValue("Distance4",
             new Double(distance4)); // normed to -1;1
    robotStruct.setOutputValue("Distance5",
             new Double(distance5)); // normed to -1;1
    robotStruct.setOutputValue("Distance6",
             new Double(distance6)); // normed to -1;1
    robotStruct.setOutputValue("Distance7",
             new Double(distance7)); // normed to -1;1
    robotStruct.setOutputValue("Light0",
             new Double(light0)); // normed to -1;1
    robotStruct.setOutputValue("Light1",
             new Double(light1)); // normed to -1;1
    robotStruct.setOutputValue("Light2",
             new Double(light2)); // normed to -1;1
    robotStruct.setOutputValue("Light3",
             new Double(light3)); // normed to -1;1
    robotStruct.setOutputValue("Light4",
             new Double(light4)); // normed to -1;1
    robotStruct.setOutputValue("Light5",
             new Double(light5)); // normed to -1;1
    robotStruct.setOutputValue("Light6",
             new Double(light6)); // normed to -1;1
    robotStruct.setOutputValue("Light7",
             new Double(light7)); // normed to -1;1
    


  }
 
}
