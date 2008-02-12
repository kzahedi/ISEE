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

package util.net;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This class implements the basic network communication method. 
 * @see hinton.broker.CenCom
 * @see hinton.ambassador.SimCom
 */
public class INetCommunication 
{

  public final static int SERVER = 0; // a server listens for a connection
  public final static int CLIENT = 1; // a client calls for a connection
  private int mode = CLIENT;
  private int port = 8080;
  private String addrString = null;
  private InetAddress inetAddr = null;
  private InputStream in  = null;
  private OutputStream    out = null;
  private ServerSocket serverSocket = null;
  private Socket clientSocket = null;

  public INetCommunication(int mode)
  {
    this.mode = mode;
  }

  public void close()
  {
    try
    {
      if(serverSocket != null)
      {
        serverSocket.close();
      }
      if(clientSocket != null)
      {
        clientSocket.close();
      }
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }


  /**
   * Return port.
   * @return returns the port
   */
  public int getPort()
  {
    return port;
  }
  /**
   * Sets the port of the server to connect to.
   * @param port The port on the server
   */
  public void setPort(int port)
  {
    this.port = port;
  }
  /**
   * Sets the string representation of the server.
   * @param  host A string representing the host ("localhost", "dixon.gmd.de",
   * ...")
   */
  public void setInetAddress(String addr)
  {
    this.addrString = addr;
  }


  public void initConnection()
  {
    switch(mode)
    {
      // ********************************************************************
      // server
      // ********************************************************************
      case SERVER:
        boolean notConected = true;
        while(notConected)
        {
          try 
          {
            System.out.println("trying " + port);
            serverSocket = new ServerSocket(port);
            System.out.println("waiting for client");
            clientSocket = serverSocket.accept();
            System.out.println("Server has found a client");
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();
            System.out.println("found a client");
            notConected = false;
          }
          catch(BindException e)
          {
            System.out.println("Port " + port + " used, trying " + (port +1));
            port = port + 1;
          }
          catch (IOException e)
          {
            in = null;
            out = null;
            System.out.println("SERVER: couldnt open sockets");
            return;
          }
        }
        break;
      // ********************************************************************
      // client
      // ********************************************************************
      case CLIENT:
        try 
        {
          inetAddr = InetAddress.getByName(addrString);
        }
        catch (UnknownHostException e)
        {
          System.out.println("CLIENT: host " + addrString + " unknown");
        }
        System.out.println("CLIENT: Trying to connect to " + inetAddr +":"+port);
        try 
        {
          clientSocket = new Socket(inetAddr, port);
          System.out.println("CLIENT: clientSocket: " +
              clientSocket.toString());
          in = clientSocket.getInputStream();
          out = clientSocket.getOutputStream();
        }
        catch (IOException e)
        {
          in = null;
          out = null;
          System.out.println("CLIENT: couldnt open socket");
          return;
        }
        break;
    }
    System.out.println("initConnection done.");
  }

  public InputStream getReader()
  {
    return in;
  }

  public OutputStream getWriter()
  {
    return out;
  }

  public static void main(String argv[])
  {

    if (argv[0].equals("server"))
    {
      INetCommunication server = new INetCommunication(SERVER);
      server.setPort(8080);
      server.initConnection();
      if (server.getReader() == null || server.getWriter() == null)
      {
        System.out.println("Reader or Writer not done. exit");
        System.exit(0);
      }
      InputStream is_in = server.getReader();
      BufferedReader localIn = new BufferedReader(
          new InputStreamReader(is_in));
      OutputStream   localOut = server.getWriter();
      while(true)
      {
        try 
        {
          String received = localIn.readLine();
          if (received == null)
          {
            break;
          }
          System.out.println("Received: " + received);
        }
        catch(IOException e)
        {
          System.out.println("catched unreadable string");
          server.initConnection();
          localIn  = new BufferedReader(new InputStreamReader(server.getReader()));
          localOut = server.getWriter();
        }
      }
    }

    if(argv[0].equals("client"))
    {
      INetCommunication client = new INetCommunication(CLIENT);
      client.setInetAddress("localhost");
      if(argv.length < 2)
      {
        client.setPort(8080);
      }
      else
      {
        client.setPort(Integer.parseInt(argv[1]));
      }
      client.initConnection();
      if (client.getReader() == null || client.getWriter() == null)
      {
        System.out.println("Reader or Writer not done. exit");
        System.exit(0);
      }
      InputStream localIn  = client.getReader();
      DataOutputStream  localOut = new DataOutputStream(client.getWriter());
      int i=0;
      
      while(true)
      {
        try 
        {
          System.out.println("Sending " + i);
          localOut.writeChars("line " + i + "\n");
          i++;
          //Thread.sleep(1000);
        }
        catch(IOException e)
        { }
      }
      
    }
  }
}






