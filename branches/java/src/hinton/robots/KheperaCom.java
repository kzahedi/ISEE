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

package hinton.robots;

import hinton.ambassador.RobCom;
import hinton.ambassador.RobotStruct;
import hinton.io.ComPortConfigXMLReader;
import hinton.io.SerialCom;
import hinton.io.SerialConnectionException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * This is an programmed interface to the Khepera Robot. It provides functions
 * to set the speed of the wheels, and to read the measured values of distance
 * and light sensors.
 */
public class KheperaCom extends RobCom 
{

  // Important:
  // ----------
  // YOU HAVE GOT ACCESS TO THE FOLLOWING VARIABLES:
  //
  // RobotStruct       robotStruct;
  // ProcessParameters processParameters


  // **************************************************************************
  // public constants
  // **************************************************************************
  /** choose no preprocessing. is the default value */
  public static final int PREPROCESSING_NONE  = 0;
  /** choose preprocessing designed for the mrc. that is: IR0 is the mean value
   * of the left 3 sensors, and IR1 is the mean of the right 3 sensors */
  public static final int PREPROCESSING_MRC   = 1;

  /** Number of infrared distance sensors */
  public final static int MAX_IR_SENSORS = 8;
  /** Number of light sensors */
  public final static int MAX_LIGHT_SENSORS = 8;

  private String configFile = new String("xml/robot/KheperaComConfig.xml");
  // **************************************************************************
  // private constants
  // **************************************************************************
  private int preprocessing = PREPROCESSING_NONE;

  private int left = 0;
  private int right = 0;

  private SerialCom serialCom = new SerialCom(SerialCom.SERIAL_0);

  private DataInputStream in   = null;
  private DataOutputStream out = null;

  private double[] infraredSensors = null;
  private double[] lightSensors = null;

  /**
   * default construct. does nothing
   */
  public KheperaCom()
  {
    super();
    setPreprocessing(PREPROCESSING_MRC);
  }

  public KheperaCom(String configFile)
  {
    super();
    this.configFile = configFile;
  }

  /**
   * default construct. set the desired pre-processing
   * @see #setPreprocessing
   */
  public KheperaCom(int preprocessing)
  {
    super();
    setPreprocessing(preprocessing);
  }

  public String getRobotName()
  {
    return "Khepera";
  }

  public String getConfigFileName()
  {
    return "xml/robot/khepera_robot.xml";
  }


  /**
   * Opens the serial port and initialises the needed arrays.
   * @see hinton.io.SerialCom
   */
  public void connect()
  {
    try 
    {
      ComPortConfigXMLReader configReader = new ComPortConfigXMLReader();
      configReader.read(configFile);

      serialCom.setBaudRate(configReader.getBaudRate());
      serialCom.setDataBits(configReader.getDataBits());
      serialCom.setStopBits(configReader.getStopBits());
      serialCom.setSerialPort(configReader.getPort());
      serialCom.setParity(configReader.getParity());
      serialCom.setTimeout(configReader.getTimeout());
      serialCom.setFlowControlMode(configReader.getFlowControlMode());
      System.out.println("Serial setting : \n" + serialCom.toString());


      serialCom.open();
    }
    catch (SerialConnectionException e)
    {
      e.printStackTrace();
      serialCom.close();
    }

    in = new DataInputStream(serialCom.getInputStream());
    out = new DataOutputStream(serialCom.getOutputStream());

    infraredSensors = new double[MAX_IR_SENSORS];
    for(int i=0;i<MAX_IR_SENSORS;i++)
    {
      infraredSensors[i]=0;
    }

    lightSensors = new double[MAX_LIGHT_SENSORS];
    for(int i=0;i<MAX_LIGHT_SENSORS;i++)
    {
      lightSensors[i]=0;
    }
  }

  public void disconnect()
  {

  }

  /**
   * Sets the preprocessing. Value is one of the constants defined above.
   * @param preprocessing is one of the above constants
   * @see #PREPROCESSING_NONE
   * @see #PREPROCESSING_MRC
   */
  public void setPreprocessing(int preprocessing)
  {
    this.preprocessing = preprocessing;
  }

  /**
   * Closes the Serial Port.
   */
  public void closeConnection()
  {
    serialCom.close();
  }

  /**
   * Stops the Robot. 
   */
  public void stop()
  {
    try 
    {
      out.writeBytes("D,0,0\n");
      in.readLine();
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Sets the speed of the robot, and receives sensor values. Sets the speed
   * that was given by setSpeed and reads the infrared and light sensors. The
   * values can then be read using the functions getIRValue and
   * getLightSensorValue.
   * @see #getIRValue(int i)
   * @see #getLightSensorValue(int i)
   * 
   */
  public void update(RobotStruct robotStruct)
  {
    left = (int)(processParameter.getMaxSpeed()*(new Double(
            robotStruct.getInputValue("MotorLeft").toString()).doubleValue()));
    right = (int)(processParameter.getMaxSpeed()*(new Double(
            robotStruct.getInputValue("MotorRight").toString()).doubleValue()));
    try 
    {
      while(in.available() > 0)
      {
        in.readLine();
      }
  //System.out.println("D,"+left+","+right);
      out.writeBytes("D,"+left+","+right+"\n");
      in.readLine(); // goes nowhere
      out.writeBytes("N\n");
      String irString    = in.readLine();
      out.writeBytes("O\n");
      String lightString = in.readLine();
      System.out.println("Received: " + irString + " " + lightString);
      StringTokenizer infrared = new StringTokenizer(irString,",");
      StringTokenizer light = new StringTokenizer(lightString,",");
      infrared.nextToken(); // because the first token is a letter
      light.nextToken();    // because the first token is a letter
      for(int i=0;i<MAX_IR_SENSORS;i++)
      {
        String token = infrared.nextToken();
        infraredSensors[i] = Integer.parseInt(token);
      }
      for(int i=0;i<MAX_LIGHT_SENSORS;i++)
      {
        lightSensors[i] = Integer.parseInt(light.nextToken());
      }
    }
    catch(IOException e)
    {
      e.printStackTrace();
      serialCom.close();
    }
    catch(NoSuchElementException e)
    {
      e.printStackTrace();
      update(robotStruct);
    }

  // **************************************************************************
  // preprocessing ... programmed net and robot dependent
  // **************************************************************************
    switch (preprocessing)
    {
      case PREPROCESSING_MRC:
        int average = 0;
        // left sensor is average of left sensors
        for(int i=0;i<3;i++)
        {
          average += getIRValue(i);
        }
        average = (int)((double)average/3d);
        infraredSensors[0] = average; 
        average = 0;
        // right sensor is average of right sensors
        for(int i=3;i<6;i++)
        {
          average += getIRValue(i);
        }
        average = (int)((double)average/3d);
        infraredSensors[1] = average; 

        // map to -1:1
        infraredSensors[0] = ((infraredSensors[0]-510) / 510);
        infraredSensors[1] = ((infraredSensors[1]-510) / 510);
        System.out.println("infraredSensors[0] = " + infraredSensors[0]
                         + "infraredSensors[1] = " + infraredSensors[1]);
    }

  // put data back into the struct
  // ir0 is left average
  // ir1 is right average
    for(int i=0;i<KheperaCom.MAX_IR_SENSORS;i++)
    {
      robotStruct.setOutputValue("IR"+i, 
          new Double(getIRValue(i)).toString());
    }
  }

  /**
   * Returns the value of the infrared sensor given by <code> index </code>. 
   * @param    index index of the sensor, 0 &le; index &lt; MAX_IR_SENSORS
   * @return   -1, if index &gt; MAX_IR_SENSORS, int value else
   * @see #MAX_IR_SENSORS
   */
  public double getIRValue(int index)
  {
    if (index>=MAX_IR_SENSORS)
    {
      return -1;
    }
    return infraredSensors[index];
  }

  /**
   * Returns the value of the ambient light sensor given by <code> index </code>. 
   * @param    index index of the sensor, 0 &le; index &lt; MAX_LIGHT_SENSORS
   * @return   -1, if index &gt; MAX_LIGHT_SENSORS, int value else
   * @see #MAX_LIGHT_SENSORS
   */
  public double getLightSensorValue(int index)
  {
    if (index>=MAX_IR_SENSORS)
    {
      return -1;
    }
    return infraredSensors[index];
  }


  /**
   * Human readable string representation of the infrared sensor measurements.
   * @return  String
   */
  public void infraredToString()
  {
    System.out.print("IR-Sensors: ");
    for(int i=0;i<MAX_IR_SENSORS;i++)
    {
      System.out.print(infraredSensors[i] + " ");
    }
    System.out.println("");
  }

  /**
   * Human readable string representation of the ambient light sensor measurements.
   * @return  String
   */
  public void lightToString()
  {
    System.out.print("Light-Sensors: ");
    for(int i=0;i<MAX_LIGHT_SENSORS;i++)
    {
      System.out.print(lightSensors[i] + " ");
    }
    System.out.println("");
  }


  /**
   * selftest function, should <b> not </b> be called as member function.
   */
  public static void main(String argv[])
  {
  }
}
