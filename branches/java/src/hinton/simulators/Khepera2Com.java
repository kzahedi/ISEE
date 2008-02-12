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

import util.net.DataGramCommunication;


/**
 * Communication Interface to the 2 dim. Khepera simulator. This class implements
 * the communication interface to the 2 dim. Khepera simulator programmed by Oliver
 * Michel <a href="om@alto.unice.fr"> om@alto.unice.de </a>. This
 * class is used via the SimulatorCommunicationInterface.
 * @see hinton.Ambassador.SimulatorCommunicationInterface
 */
public class Khepera2Com extends SimCom 
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

  private DataGramCommunication com = new
    DataGramCommunication(DataGramCommunication.CLIENT);
  private DataInputStream in = null;
  private DataOutputStream out = null;

  private float leftMotorSpeed        = 0;
  private float rightMotorSpeed       = 0;

  private final static int COMMAND_GET_DATA = 0;
  private final static int COMMAND_RESET = 1;
  private final static int COMMAND_SET_POSITION = 2;
  private final static int COMMAND_ROBOT_OK = 3;
  private final static int COMMAND_ROBOT_BUMPED = 4;

  private double[] ir = new double[8];
  private double[] light = new double[8];

  private double[] startPosition = new double[3];

  public String getSimName()
  {
    return "Khepera 2.0 old";
  }

  public String getConfigFileName()
  {
    return "xml/simulator/khepera2.0.xml";
  }

  public void stop()
  {
  }

  public void nextTry()
  {
  }


  /**
   * Not implemented yet. Set a new start position for every following robot. 
   */
  public void setNewStartPosition(){
    try
    {
      //************************************************************
      // mit processParameter.nextInt(), .nextDouble(), .nextBoolean bekommst
      // du neue random werte, die von evosun getriggert wurden
      //************************************************************
      com.writeInt(COMMAND_SET_POSITION);
      com.writeFloat(
          (float)((this.processParameter).getFitnessFunction()).getFitnessValue());
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Connect to the GDM RoboCup-Simulator. Uses the DataGramCommunictaion-Class.
   * The own port is set to 7020.
   * @see util.net.DataGramCommunication
   */
  public void connect()
  {
    com.setInetAddress(ip);
    com.setPort(7020);
    com.setServerPort(port);
    com.initConnection();
    System.out.println("Khepera 2.0 simulator");
  }

  public void disconnect()
  {
    com.close();
  }


  public void reset(){
    try 
    {
      com.writeInt(COMMAND_RESET); // reset
      com.writeFloat((float)((this.processParameter).getFitnessFunction()).getFitnessValue());

      System.out.print("HINTON:" + ((this.processParameter).getFitnessFunction()).getFitnessValue() + "\n");
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * resets the robot command followed by the current fitness
   */
  public void reset(float v)
  {
    try 
    {
      com.writeInt(COMMAND_RESET); // reset
    } 
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Reads from the RobotStruct and communicates with the simulator.
   * @param    robotStruct the data-storage of the robot data
   * @see hinton.ambassador.RobotStruct
   */
  public void send(RobotStruct robotStruct){
    robotStatus = RobotStatus.OK;
    try 
    {
      com.writeInt(COMMAND_GET_DATA);

      com.writeFloat((float)(speedFactor * robotStruct.getInputDouble("MotorLeft")));
      com.writeFloat((float)(speedFactor *
            robotStruct.getInputDouble("MotorRight")));


      ir[0]                 = com.readFloat();
      ir[1]                 = com.readFloat();
      /*
         ir[2]                 = com.readFloat();
         ir[3]                 = com.readFloat();
         ir[4]                 = com.readFloat();
         ir[5]                 = com.readFloat();
         ir[6]                 = com.readFloat();
         ir[7]                 = com.readFloat();
         light[0]              = com.readFloat();
         light[1]              = com.readFloat();
         light[2]              = com.readFloat();
         light[3]              = com.readFloat();
         light[4]              = com.readFloat();
         light[5]              = com.readFloat();
         light[6]              = com.readFloat();
         light[7]              = com.readFloat();
       */


      switch(com.readInt())
      {
        case COMMAND_ROBOT_BUMPED:
          robotStatus = RobotStatus.BUMPED;
          break;
        case COMMAND_ROBOT_OK:
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
  public void update(RobotStruct robotStruct){
    robotStruct.setOutputValue("MotorLeft", 
        new Double(leftMotorSpeed)); // normed to -1;1
    robotStruct.setOutputValue("MotorRight", 
        new Double(rightMotorSpeed)); // normed to -1;1
    robotStruct.setOutputValue("IR0", new Double(ir[0])); // normed to -1;1
    robotStruct.setOutputValue("IR1", new Double(ir[1])); // normed to -1;1
    /*
       robotStruct.setOutputValue("IR2", new Double(ir[2])); // normed to -1;1
       robotStruct.setOutputValue("IR3", new Double(ir[3])); // normed to -1;1
       robotStruct.setOutputValue("IR4", new Double(ir[4])); // normed to -1;1
       robotStruct.setOutputValue("IR5", new Double(ir[5])); // normed to -1;1
       robotStruct.setOutputValue("IR6", new Double(ir[6])); // normed to -1;1
       robotStruct.setOutputValue("IR7", new Double(ir[7])); // normed to -1;1
       robotStruct.setOutputValue("LIGHT0", new Double(light[0])); // normed to -1;1
       robotStruct.setOutputValue("LIGHT1", new Double(light[1])); // normed to -1;1
       robotStruct.setOutputValue("LIGHT2", new Double(light[2])); // normed to -1;1
       robotStruct.setOutputValue("LIGHT3", new Double(light[3])); // normed to -1;1
       robotStruct.setOutputValue("LIGHT4", new Double(light[4])); // normed to -1;1
       robotStruct.setOutputValue("LIGHT5", new Double(light[5])); // normed to -1;1
       robotStruct.setOutputValue("LIGHT6", new Double(light[6])); // normed to -1;1
       robotStruct.setOutputValue("LIGHT7", new Double(light[7])); // normed to -1;1
     */
  }

}
