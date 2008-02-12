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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringBufferInputStream;

import util.io.XMLHandler;
import util.net.INetCommunication;

import cholsey.Net;

public class EvoSunCom implements EvoComInterface
{

  private INetCommunication com = new INetCommunication(INetCommunication.CLIENT);

  private XMLHandler xmlHandler = new XMLHandler();

  private Net net = null;
  private boolean running = false;
  private ProcessParameter processParameter = new ProcessParameter();
  private ProcessParameterDialog processParameterDialog = null;
  private double fitnessValue = 0;
  private int port = 0;
  private String ip = null;

  private PrintWriter out = null;
  private BufferedReader in = null;

  private boolean newGenerationFlag = false;

  public boolean autoRun()
  {
    return true;
  }

  public boolean newGeneration() 
  {
    return newGenerationFlag;
  }
  public void clearNewGenerationFlag()
  {
    newGenerationFlag = false;
  }

  public void setProcessParameter(ProcessParameter processParameter)
  {
    this.processParameter = processParameter;
  }

  public void setProcessParameterDialog(ProcessParameterDialog processParameterDialog)
  {
    this.processParameterDialog = processParameterDialog;
  }


  public ProcessParameter getProcessParameter()
  {
    return processParameter;
  }

  public boolean isRunning()
  {
    return running;
  }

  public Net getNet()
  {
    return net;
  }

  public void setFitnessValue(double fitnessValue)
  {
    String bla = null;
    this.fitnessValue = fitnessValue;
    try
    {
      out.println(fitnessValue);
      //this.evoInput.println(getOutputPerf());

      // get the system performance
      bla = in.readLine(); // command = "SYS_PERF"
      bla = in.readLine(); // modifier for fitness, for visualising the best
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }

  }

  public void connect(String ip, int port)
  {
    running = false;
    this.ip = ip;
    this.port = port;
    com.setInetAddress(ip);
    com.setPort(port);
    com.initConnection();

    if (com.getReader() == null || com.getWriter() == null)
    {
      System.out.println("Reader or Writer not done. exit");
      return;
    }

    in  = new BufferedReader(new InputStreamReader(com.getReader()));
    out = new PrintWriter( new BufferedWriter( new
          OutputStreamWriter(com.getWriter())),true );


    running = true;
  }

  public void communicate()
  {
    if (running == false) 
    {
      connect(ip, port);
    }

    try{

      String command = in.readLine();

      //System.out.println("Received command: \"" + command + "\"");

      if(command.equals("NEXT_GEN"))
      {
        newGenerationFlag = true;
    
        processParameter.setWarmUpSteps(Integer.parseInt(in.readLine().trim())); // warm up steps
        processParameter.setCycles(Integer.parseInt(in.readLine().trim())); // eval. steps
        processParameter.setConstants(
            Double.parseDouble(in.readLine().trim()),
            Double.parseDouble(in.readLine().trim()),
            Double.parseDouble(in.readLine().trim()),
            Double.parseDouble(in.readLine().trim()));   // the four constants
        processParameter.setRandomSeed(Integer.parseInt(in.readLine().trim()));



        communicate();



        return;
      }


      // connection is closed by the evolution program
      if(command.equals("CLOSE"))
      {
        running = false;
        com.close();
      }

      // a new individual is coming
      if(command.equals("NEXT_INDY"))
      {
   
        net =  xmlHandler.readNetFromInputStream(
            new StringBufferInputStream(in.readLine()));

        processParameter.setNet(net);
        if(processParameterDialog != null)
        {
          processParameterDialog.updatePanel();
        }

        //System.out.println("received net:\n" + net.toXML());


      }


      if(command.equals("END_POP"))
      {
        communicate();
        return;
      }
      // draw the best
      /*
      if(str.equals("END_POP"))
      {
        if(plotMode == SHOW_BEST)
        {
          firstReturnPanel.actionPerformed(new ActionEvent(this,300, "DRAW_BEST"));
          phasePanel.actionPerformed(new ActionEvent(this,300, "DRAW_BEST"));
        }
        clientPanel.actionPerformed(new ActionEvent(this,300, "END_POP"));
      }
      */
    }
    catch (IOException e)
    { 
      e.printStackTrace();
      System.out.println("not readable");
    }

    //System.out.println("END OF COMMUNICATION");

    return;
  }

  public String getName()
  {
    return "EvoSun";
  }
}






