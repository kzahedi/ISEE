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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;

public class SerialCom 
{
    private CommPortIdentifier portId;
    private SerialPort sPort;
    private int baudRate = 38400;
    private int databits = SerialPort.DATABITS_8;
    private int stopbits = SerialPort.STOPBITS_2;
    private int parity   = SerialPort.PARITY_NONE;
    private int flowControl = SerialPort.FLOWCONTROL_NONE;
    private int timeout = 30000;
    private OutputStream os = null;
    private InputStream is = null;
    private boolean open = false;
    private static Enumeration portList; 
    public static final int SERIAL_0 = 0;
    public static final int SERIAL_1 = 1;
    private int port;

    public SerialCom(int port)
    {
      this.port = port;
    }

    public void setBaudRate(int baudRate)
    {
      this.baudRate = baudRate;
    }

    public void setDataBits(int databits)
    {
      this.databits = databits;
    }

    public void setStopBits(int stopbits)
    {
      this.stopbits = stopbits;
    }

    public void setSerialPort(int port)
    {
      this.port = port;
    }

    public void setParity(int parity)
    {
      this.parity = parity;
    }

    public void setTimeout(int timeout)
    {
      this.timeout = timeout;
    }

    public void setFlowControlMode(int flowControl)
    {
      this.flowControl = flowControl;
    }

    public String comPortName()
    {
      Properties p = System.getProperties();
      String osName = p.getProperty("os.name").toLowerCase();
      if(osName.indexOf("linux") != -1)
      {
        return new String("/dev/ttyS" + port);
      }
      return new String("COM" + (port+1));
    }
    
    public void open() throws SerialConnectionException
    {
      // Obtain a CommPortIdentifier object for the port you want to open.
      portList = CommPortIdentifier.getPortIdentifiers();
      // und ihre Namen ausgeben:
      System.out.println("List of known ports:");
      while (portList.hasMoreElements())
      {
        portId = (CommPortIdentifier) portList.nextElement();
        System.out.println(portId.getName());
      }
      System.out.println("List done:");
      try {
        portId = CommPortIdentifier.getPortIdentifier(comPortName());
      } catch (NoSuchPortException e) {
        e.printStackTrace();
        throw new SerialConnectionException(e.getMessage());
      }

      // Open the port represented by the CommPortIdentifier object. Give
      // the open call a relatively long timeout of 30 seconds to allow
      // a different application to reliquish the port if the user 
      // wants to.
      try {
        sPort = (SerialPort)portId.open("SerialCom", timeout);
      } catch (PortInUseException e) {
        throw new SerialConnectionException(e.getMessage());
      }

      // Set the parameters of the connection. If they won't set, close the
      // port before throwing an exception.
      try {
        setConnectionParameters();
      } catch (SerialConnectionException e) { 
        sPort.close();
        throw e;
      }

      // Open the input and output streams for the connection. If they won't
      // open, close the port before throwing an exception.
      try {
        os = sPort.getOutputStream();
        is = sPort.getInputStream();
      } catch (IOException e) {
        sPort.close();
        throw new SerialConnectionException("Error opening i/o streams");
      }

      // Set receive timeout to allow breaking out of polling loop during
      // input handling.
      try {
        sPort.enableReceiveTimeout(30);
      } catch (UnsupportedCommOperationException e) {
      }

      open = true;
      System.out.println("serial com opened");

    }

    /**
      Sets the connection parameters to the setting in the parameters object.
      If set fails return the parameters object to origional settings and
      throw exception.
     */
    public void setConnectionParameters() throws SerialConnectionException {

      // Set connection parameters, if set fails return parameters object
      // to original state.
      try {
        sPort.setSerialPortParams(baudRate,
            databits,
            stopbits,
            parity);
      } catch (UnsupportedCommOperationException e) {
        throw new SerialConnectionException("Unsupported parameter");
      }

      // Set flow control.
      try {
        sPort.setFlowControlMode(flowControl);
      } catch (UnsupportedCommOperationException e) {
        throw new SerialConnectionException("Unsupported flow control");
      }
    }


    /**
      Close the port and clean up associated elements.
     */
    public void close() {
      // If port is alread closed just return.
      if (!open) {
        return;
      }

      // Check to make sure sPort has reference to avoid a NPE.
      if (sPort != null) {
        try {
          // close the i/o streams.
          os.close();
          is.close();
        } catch (IOException e) {
          System.err.println(e);
        }

        // Close the port.
        sPort.close();

      }

      open = false;
    }

    public OutputStream getOutputStream()
    {
      return os;
    }

    public InputStream getInputStream()
    {
      return is;
    }

    public String toString()
    {
      String s = new String();
      s = s.concat("baudRate    " + baudRate + "\n");
      s = s.concat("databits    " + databits + "\n");
      s = s.concat("stopbits    " + stopbits + "\n");
      s = s.concat("parity      " + parity + "\n");
      s = s.concat("flowControl " + flowControl + "\n");
      s = s.concat("timeout     " + timeout + "\n");
      s = s.concat("port        " + port + "\n");
      return s;
    }
    
    public static void main(String argv[])
    {
      SerialCom sc = new SerialCom(SerialCom.SERIAL_0);
      DataInputStream in = null;
      DataOutputStream out = null;
      try
      {
      sc.open();
      in = new DataInputStream(sc.getInputStream());
      out = new DataOutputStream(sc.getOutputStream());
      while (in.available() > 0)
      {
        System.out.println(in.readLine());
      }
      String n = new String("N\n");
      out.writeBytes(n);
      out.flush();
      System.out.println("\""+in.readLine()+"\"");
      sc.close();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
      catch(SerialConnectionException e)
      {
        e.printStackTrace();
      }
    }
}
