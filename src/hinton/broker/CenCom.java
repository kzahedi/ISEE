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


package hinton.broker;

import hinton.executive.ProcessParameter;
import hinton.gui.ProcessParameterDialog;

import java.io.IOException;
import java.util.Random;

import javax.swing.JTextField;

import util.net.INetCommunication;

import cholsey.Net;
import cholsey.Neuron;
import cholsey.NeuronType;
import cholsey.ProcessMode;
import cholsey.Transferfunction;

import com.mindprod.ledatastream.LEDataInputStream;
import com.mindprod.ledatastream.LEDataOutputStream;

/**
 *  Implementation of the communication interface to the objectiv c CEN
 *  programmed by Ulrich Steinmetz. This class provides the TCP/IP communication
 *  interface to the evolution programm CEN in it objectiv c version.
 *
 */
public class CenCom implements EvoComInterface
{
 
  private INetCommunication com = new INetCommunication(INetCommunication.CLIENT);


  public CenCom(){}

  private JTextField iterationsInput = null;
  private JTextField cyclesInput     = null;
  private int cylcles = 0;
  
  final private int  REQUEST_INIT = 1;  
  final private int  REQUEST_RESET = 2; 
  final private int  REQUEST_WITH_DATA = 3;
  final private int  REQUEST_DATA = 4;
  final private int  REQUEST_LOAD_NET = 6;
  final private int  REQUEST_EVALUATE = 7;
  final private int  REQUEST_GET_FULL_DATA_FOR = 8;
  final private int  REQUEST_FINISH = 9;
  final private int  REQUEST_INIT_WITH_DATA = 10; 

  private boolean connected = false;
  private LEDataInputStream in = null;
  private LEDataOutputStream out = null;
  private Net net = null;
  private boolean running = false;
  private ProcessParameter processParameter = new ProcessParameter();
  private ProcessParameterDialog processParameterDialog = null;
  private double fitnessValue = 0;

  private boolean newGenerationFlag = false;

  public boolean autoRun()
  {
    return false;
  }

  public String getName()
  {
    return "CEN";
  }
  public boolean newGeneration()
  {
    boolean returnval =  newGenerationFlag;
    newGenerationFlag = false;
    return returnval;
  }

  public void clearNewGenerationFlag()
  {
    newGenerationFlag = false;
  }
 
  public void setProcessParameterDialog(ProcessParameterDialog processParameterDialog)
  {
    this.processParameterDialog = processParameterDialog;
  }

 
  public void setProcessParameter(ProcessParameter processParameter)
  {
    this.processParameter = processParameter;
  }
  /**
   * Returns the ProcessParameter
   * @return  Returns the ProcessParameter-class
   * @see hinton.executive.ProcessParameter
   */
  public ProcessParameter getProcessParameter()
  {
    return processParameter;
  }
  /**
   * Returns if the cen-communication is still running. Return true if still
   * running, false if CEN has send FINISH.
   * @return   boolean value, indicating if the communication is still running
   * (no abort by cen-program)
   */
  public boolean isRunning()
  {
    return running;
  }
  /**
   * Returns the currently received net.
   * @return  the received net
   */
  public Net getNet()
  {
    return net;
  }
  
  /**
   * Sets the fitnessValue that will be communicated the next time.
   * @param    fitnessValue
   */
  public void setFitnessValue(double fitnessValue)
  {
    this.fitnessValue = fitnessValue;
  }

  public void setIterationsInput(JTextField iterationsInput)
  {
    this.iterationsInput = iterationsInput;
  }
 
  public void setCyclesInput(JTextField cyclesInput)
  {
    this.cyclesInput = cyclesInput;
  }
  
 
  /**
   * Starts the communication with CEN. That is, it looks for the specified port
   * on the specified machine identified by the internetaddress. It then does
   * the handshake.
   * @param    ip, the ip address, either "123.456.789.10" or "dixon.gmd.de",
   * "localhost" etc. Given as String.
   * @param    port, int value, the destination port
   * @return   none
   */
  public void connect(String ip, int port)
  {
    com.setInetAddress(ip);
    com.setPort(port);
    com.initConnection();
    in  = new LEDataInputStream(com.getReader());
    out = new LEDataOutputStream(com.getWriter());

    if (com.getReader() == null || com.getWriter() == null)
    {
      System.out.println("No server found\n");
      connected = false;
      return;
    }
    try 
    {
      // send him the version number
      out.writeInt(2);
      out.flush();
    }
    catch (IOException e)
    {
      connected = false;
      return;
    }
    connected = true;
    running = true;
  }

  /**
   * Communicate until receive net or FINSIH. Does all the communication until a
   * net is received or CEN did send a FINISH.
   */
  public void communicate()
  {
    if (in == null || out == null)
    {
      return;
    }
    try 
    {
      Random random = new Random();
      int numTraceData = 0;

      int c = 0 ;
      while((c = in.readInt())!=-1)
      {
        running = true;
        switch(c)
        {
          case REQUEST_INIT:
            // cen wants the size of the array of positions and the array of
            // positions
            //System.out.println("REQUEST_INIT received");
            out.writeInt(3*4);
            out.writeInt(123);
            out.writeInt(456);
            out.writeInt(789);
            newGenerationFlag = true;
            out.flush();
            //System.out.println("Did send size=3*4 pose=(1,2,3)");
            break;

          case REQUEST_RESET:
            //System.out.println("\nREQUEST_RESET received");
            //System.out.println("Sending numTraceData " + numTraceData++);
            out.writeInt(numTraceData);
            //System.out.println("done");
            break;
          case REQUEST_WITH_DATA:
            //System.out.println("\nREQUEST_WITH_DATA received");
            break;

          case REQUEST_DATA:
            //System.out.println("\nREQUEST_DATA received");
            int timeMaxNr = in.readInt();
            int displayStyle = in.readInt();
            int maxSpeed = in.readInt();
            double K0 = in.readDouble();
            double K1 = in.readDouble();
            double K2 = in.readDouble();
            double K3 = in.readDouble();
            processParameter.setCycles(timeMaxNr);
            if(displayStyle == 1)
            {
              processParameter.setDisplay(true);
            }
            else
            {
              processParameter.setDisplay(false);
            }
            processParameter.setMaxSpeed(maxSpeed);
            processParameter.setConstants(K0,K1,K2,K3);
            // System.out.println("Received data:");
            // System.out.println("timeMaxNr : " + timeMaxNr);
            // System.out.println("displayStyle : " + displayStyle);
            // System.out.println("maxSpeed : " + maxSpeed);
            // System.out.println("K0 : " + K0);
            // System.out.println("K1 : " + K1);
            // System.out.println("K2 : " + K2);
            // System.out.println("K3 : " + K3);
            if(iterationsInput != null)
            {
              iterationsInput.setText(""+1);
            }
            if(cyclesInput != null && cylcles != timeMaxNr)
            {
              cyclesInput.setText(""+timeMaxNr);
              cylcles = timeMaxNr; // to save some time
            }
            break;

          case REQUEST_LOAD_NET:
            net = new Net();
            //System.out.println("\nREQUEST_LOAD_NET received");
            int neuron_count = in.readInt();
            int synapse_count = in.readInt();
            int transferfunction = in.readInt();
            int net_mask = in.readInt(); // ???

            net.setTransferfunction(
                (transferfunction == 0)?
                Transferfunction.TANH:
                Transferfunction.SIGM);
            // System.out.println( neuron_count + " Neurons and " + synapse_count
                //+ " Synapses to expect");
            // System.out.println("Transferfunction : " + tran_name);
            for(int neuron_index = 0; neuron_index<neuron_count; neuron_index++)
            {
              //System.out.println("*** Neuron " + (neuron_index+1) + " ***");
              double bias  = in.readDouble();
              double gamma = in.readDouble();
              double activation = in.readDouble();
              double output = in.readDouble();
              char type = (char)in.readByte();
              byte t = in.readByte();
              t = in.readByte();
              t = in.readByte();
              NeuronType neuronType = null;
              switch (type)
              {
                case 'h' : 
                  neuronType = NeuronType.HIDDEN;
                  break;
                case 'i':
                  neuronType = NeuronType.INPUT;
                  break;
                case 'o':
                  neuronType = NeuronType.OUTPUT;
                  break;
                default: // ERROR
                  System.out.println("CenCom: UNKNOWN NEURON TYPE");
                  System.exit(-1);
              }
              //System.out.println("bias: " + bias + " gamma: " + gamma + 
                  //" activation: " + activation + " output: " + output +
                  //" type: " + (char)type);
              Neuron newNeuron = net.addNeuron( bias,
                  0,
                  0,
                  ProcessMode.DYNAMIC,
                  neuronType);
              newNeuron.setActivation(activation);
            }

            for(int synapse_index=0; synapse_index < synapse_count;
                synapse_index++)
            {
              int to   = (int)in.readInt();
              int from = (int)in.readInt();
              double strength = in.readDouble();
              net.addSynapse(net.getNeuron(from), 
                  net.getNeuron(to),
                  strength,
                  ProcessMode.DYNAMIC);
            }
            //System.out.println(net.toXML());
            processParameter.setNet(net);
            return;
            //break; // not reached. java says thats an error

          case REQUEST_EVALUATE:
            //System.out.println("\nREQUEST_EVALUATE received");
            //out.writeDouble(random.nextDouble());
            out.writeDouble(fitnessValue);
            //System.out.println("Did send fake performance");
            break;

          case REQUEST_GET_FULL_DATA_FOR:
            //System.out.println("\nREQUEST_GET_FULL_DATA_FOR received");
            // send back something empty
            int num = in.readInt();
            out.writeLong(1); // len
            for(int i=0;i<16;i++)
            {
              out.writeShort(0);
            }
            for(int i=0;i<2;i++)
            {
              out.writeShort(0);
            }
            for(int i=0;i<2;i++)
            {
              out.writeFloat(0);
            }
            break;

          case REQUEST_FINISH:
            //System.out.println("\nREQUEST_FINISH received");
            running = false;
            return;
            //break;

          case REQUEST_INIT_WITH_DATA:
            //System.out.println("\nREQUEST_INIT_WITH_DATA received");
            break;
          default:
            //System.out.print(c + " ");
        }
      }
    }
    catch (IOException e)
    {
      connected = false;
    }
  }

  /**
   * Selftest. Not to be calles as member function. Program takes the port and
   * the internetaddress as parameters. If no parameter is given, then
   * "localhost" and 7000 are chosen as default values
   * @param    ip String like "123.345.567.678", "dixon.gmd.de" or "localhost"
   * @param    port the server-port
   */
  public static void main(String argv[])
  {
    CenCom cc = new CenCom();
    if (argv.length==0)
    {
      cc.connect("localhost",7000);
    }
    else 
    {
      cc.connect(argv[0],Integer.parseInt(argv[1]));
    }
    cc.communicate();
  }

}
