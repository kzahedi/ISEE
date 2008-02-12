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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
/**
 * Implementation of parameter-interface between the neural net and the
 * simulator/roboter. This class provides functions that map the i-th
 * input neuron the the robot output-parameter &lt;parameter&gt; ("IR0", "IR1",
 * ...). The same for ouput neuron n and the input-parameter &lt;parameter&gt;
 * of the robot ("MotorLeft", "MotorRight"). This is done, so that the net can
 * put/read its value according to the index of the neuron, and the
 * robot/simulator can read/put its values according to the name of the
 * source/destination. In this class "<code>output</code>" corresponds to output
 * values of the robot ("IR1", "IR0", ...) and input corresponds to input values
 * of the robot ("MotorLeft", "MotorRight", ...).
 * 
 */
public class RobotStruct 
{
  private Hashtable outputParameter = new Hashtable(); // like position, speed ...
  private Hashtable inputParameter  = new Hashtable(); // like left motor, right motor
  private Vector inputMap         = new Vector();  // map output neurons to robot input
                                           // the index of the key corresponds
                                           // to the index of the neuron. the
                                           // object in the vector corresponds
                                           // to the key in the
                                           // inputParameter-hashmap
  private Vector outputMap        = new Vector();  // map input neurons to robot output,
                                           // see inputParameter


  /**
   * Default constructor. Does nothing.
   */
  public RobotStruct()
  {

  }

/**
  * Add a name that corresponds to an output of the robot. That is for example,
  * the distance value of IR could be named "IR0".
  * @param   Name string 
  */
  public void addOutputKey(String name)
  {
    outputParameter.put(name, new Double(0));
    outputMap.add(name);
  }

  /**
   * Adds a name of an input device of the robot. For example, the input of the
   * left motor (its speed) could be named "MotorLeft".
   * @param  name string
   */
  public void addInputKey(String name)
  {
    inputParameter.put(name, new Double(0));
    inputMap.add(name);
  }

  /**
   * Removes all output devices of the robot.
   */
  public void removeAllOutputKeys()
  {
    inputParameter.clear(); 
    inputMap.clear();
  }
  
  /**
   * Removes all input devices of the robot.
   */
  public void removeAllInputKeys()
  {
    outputParameter.clear(); 
    outputMap.clear();
  }
  
  /**
   * Returns a humanreadable string of all the input device names.
   */
  public String inputKeysToString()
  {
    return inputParameter.toString();
  }

  /**
   * Returns a humanreadable string of all the output device names.
   */
  public String outputKeysToString()
  {
    return outputParameter.toString();
  }

  /**
   * Returns a the number of output devices of the robot.
   */
  public int outputKeysSize()
  {
    return outputParameter.size();
  }

  /**
   * Returns a the number of input devices of the robot.
   */
  public int inputKeysSize()
  {
    return inputParameter.size();
  }
 
  /**
   * Returns a enumeartion of all input devices.
   * @see java.util.Enumeration
   */
  public Enumeration inputKeys()
  {
    return inputMap.elements();
  }

  /**
   * Returns a enumeartion of all output devices.
   * @see java.util.Enumeration
   */
  public Enumeration outputKeys()
  {
    return outputMap.elements();
  }
  
  /**
   * Returns the name of the i-th input-device, which is connected to the value
   * of the i-th output-neuron of the nn. That is how this is used in the
   * Executer-Implementation
   * @param    index i
   * @return   String name
   */
  public String getInputObject(int i)
  {
    if(i >= inputMap.size())
    {
      return null;
    }
    return (String)inputMap.elementAt(i);
  }
  
  /**
   * Returns the name of the i-th output-device, which is connected to the value
   * of the i-th input-neuron of the nn. That is how this is used in the
   * Executer-Implementation.
   * @param    index i
   * @return   String name
   */
  public String getOutputObject(int i)
  {
    if(i >= outputMap.size())
    {
      return null;
    }
    return (String)outputMap.elementAt(i);
  }


  /**
   * Returns the value that was assigned to the input device named <code> key
   * </code>
   * @param    key the name of the input device ("MotorLeft", "MotorRight")
   * @return   Object representing the value, that should be assigned to the
   * device
   */
  public Object getInputValue(String key)
  {
    return inputParameter.get(key);
  }

  /**
   * Returns the double value that was assigned to the input device given by
   * <code> key </code>
   */
  public double getInputDouble(String key)
  {
    return (Double.parseDouble(inputParameter.get(key).toString()));
  }

  /**
   * Returns the value of the output-device.
   */
  public Object getOutputValue(String key)
  {
    return outputParameter.get(key);
  }

  /**
   * Returns the double value that was assigned to the output device given by
   * <code> key </code>
   */
  public double getOutputDouble(String key)
  {
    return ((Double)outputParameter.get(key)).doubleValue();
  }


  /**
   * Set the value of the output device given by key to the value given by
   * <code> value </code>. Is done only within the class, not for the real
   * robot. RobCom-device must then read from here, and really set the value.
   */
  public void setOutputValue(String key, Object value)
  {
    outputParameter.put(key,value);
  }

  /**
   * Set the value of the input device given by key to the value given by
   * <code> value </code>. Is done only within the class, not for the real
   * robot. RobCom-device must then read from here, and really set the value.
   */
  public void setInputValue(String key, Object value)
  {
    inputParameter.put(key,value);
  }

  /**
   * Set the value of the i-th output device to the value o.
   */
  public void setOutputMap(int i, Object o)
  {
    outputMap.setElementAt(o,i);
  }

  /**
   * Set the value of the i-th intput device to the value o.
   */
  public void setInputMap(int i, Object o)
  {
    inputMap.setElementAt(o,i);
  }
  /**
   * Reset all input and output values to zero.
   */
  public void reset()
  {
    for(int i=0;i<inputMap.size();i++)
    {
      String key = (String)inputMap.elementAt(i);
      inputParameter.put(key, new Double(0.0));
    }
    for(int i=0;i<outputMap.size();i++)
    {
      String key = (String)outputMap.elementAt(i);
      outputParameter.put(key, new Double(0.0));
    }
  }
}



