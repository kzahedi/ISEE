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


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * This class implements the basic network communication method. Data is send in
 * <b> little endian </b> format!!
 * @see hinton.broker.CenCom
 * @see hinton.ambassador.SimCom
 */
public class DataGramCommunication 
{

  public final static int SERVER = 0; // a server listens for a connection
  public final static int CLIENT = 1; // a client calls for a connection
  private int mode = CLIENT;
  private int port = 8000;
  private int serverPort = 0;
  private String addrString = null;
  private InetAddress inetAddr = null;
  private InputStream in  = null;
  private OutputStream    out = null;
  private DatagramSocket socket = null;
  private DatagramSocket remoteSocket = null;
  private final static int BUFFER_SIZE = 1024;
  private byte[] buffer = null;
  private DatagramPacket datagram = null;


  public void close()
  {
    socket.close();
  }
  public void receive(DatagramPacket p) throws IOException
  {
    socket.receive(p);
  }

  public boolean isConnected()
  {
    return remoteSocket.isConnected();
  }

  public DataGramCommunication(int mode)
  {
    this.mode = mode;
    buffer = new byte[BUFFER_SIZE];
    buffer[0]=0;
    datagram = new DatagramPacket(buffer,0);
  }

  /**
   * CALL AFTER initConnection
   * @param   bufsie 
   */
  public void setReceiveBufferSize(int bufsize)
  {
    try
    {
      socket.setReceiveBufferSize(bufsize);
    }
    catch(SocketException se)
    {
      se.printStackTrace();
    }
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


  public void setServerPort(int serverPort)
  {
    this.serverPort = serverPort;
  }
  
  public void initConnection()
  {
    DatagramSocket serverSocket = null;
    DatagramSocket clientSocket = null;
    byte[] b = new byte[1];
    b[0]=0;
    DatagramPacket p = new DatagramPacket(b,1);
    boolean notConected = true;
    switch(mode)
    {
      // ********************************************************************
      // server
      // ********************************************************************
      case SERVER:
        System.out.println("waiting for client");
        notConected = true;
        while(notConected)
        {
          System.out.println("CLIENT: Trying to connect to " + inetAddr +":"+port);
          try 
          {
            serverSocket = new DatagramSocket(port);
            serverSocket.receive(p);
            serverSocket.connect(p.getSocketAddress());
            notConected = false;
          }
          catch(BindException e)
          {
            notConected = true;
            port = port + 1;
          }
          catch(SocketException e)
          {
            e.printStackTrace();
          }
          catch(IOException e)
          {
            e.printStackTrace();
          }
        }
        System.out.println("Server has found a client");
        socket = serverSocket;
        return;
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
        notConected = true;
        while(notConected)
        {
          System.out.println("CLIENT: Trying to connect to " + inetAddr +":"+port);
          try
          {
            p = new DatagramPacket(b,1);
            clientSocket = new DatagramSocket(port);
            System.out.println("trying to connect");
            clientSocket.connect(inetAddr,serverPort);
            System.out.println("CLIENT: clientSocket: " +
                clientSocket.toString());
            clientSocket.send(p);
            notConected = false;
          }
          catch (IOException e)
          {
            notConected = true;
            in = null;
            out = null;
            System.out.println("CLIENT: couldnt open socket");
            port++;
          }
        }
        socket = clientSocket;
        remoteSocket = serverSocket;
        break;
    }
    try 
    {
      socket.setReceiveBufferSize(4096);
    }
    catch(SocketException e)
    {
      e.printStackTrace();
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

  public void send(DatagramPacket p) throws IOException
  {
    socket.send(p);
  }

  public void writeInt(int i) throws IOException
  {
    buffer[0] = (byte) i;
    buffer[1] = (byte)(i >> 8);
    buffer[2] = (byte)(i >> 16);
    buffer[3] = (byte)(i >> 24);
    datagram.setData(buffer,0,4);
    socket.send(datagram);
  }

  public void writeFloat(float f) throws IOException
  {
    writeInt(Float.floatToIntBits(f));
  }

  public void writeFloatArray(float[] fArray, int size) throws IOException
  {
    int b_index = 0;
    int f = 0;
    for(int i=0;i<size;i++)
    {
      f = Float.floatToIntBits(fArray[i]);
      buffer[b_index++] = (byte) f;
      buffer[b_index++] = (byte)(f >> 8);
      buffer[b_index++] = (byte)(f >> 16);
      buffer[b_index++] = (byte)(f >> 24);
    }
    datagram.setData(buffer,0,b_index);
    socket.send(datagram);
  }

  public void writeByteArray(byte[] bArray, int size) throws IOException
  {
    datagram.setData(bArray,0,size);
    socket.send(datagram);
  }

  public void writePacketBuffer(PacketBuffer pbuf) throws IOException
  {
    writeByteArray(pbuf.getBuf(), pbuf.getBufSize());
  }

  public final int readInt() throws IOException
  {
    datagram.setLength(4);
    socket.receive(datagram);
    buffer = datagram.getData();
    return
      (buffer[3])      << 24 |
      (buffer[2]&0xff) << 16 |
      (buffer[1]&0xff) <<  8 |
      (buffer[0]&0xff);

  }

  public byte readByte() throws IOException
  {
    datagram.setLength(4);
    socket.receive(datagram);
    buffer = datagram.getData();
    System.out.println("" + buffer[0] + " " + buffer[1] + " " + buffer[2] + " "
        + buffer[3]);

    System.out.println("" + (0xff & buffer[0]) + " " + (0xff & buffer[1]) + " "
        + (0xff & buffer[2]) + " " + (0xff & buffer[3]));
    return (byte)(datagram.getData()[3] & 0xff);
  }

  public final float readFloat() throws IOException
  {
    return Float.intBitsToFloat(readInt());
  }

  public final float[] readFloatArray() throws IOException
  {
    datagram.setLength(BUFFER_SIZE);
    socket.receive(datagram);
    int arraySize = datagram.getLength() / 4; // 4 bytes = 1 float
    int bufferIndex = 0;
    float[] f = new float[arraySize];
    int integer = 0;
    for(int i=0;i<arraySize;i++)
    {
      bufferIndex = i*4;
      integer = 
        (buffer[bufferIndex+3])      << 24 |
        (buffer[bufferIndex+2]&0xff) << 16 |
        (buffer[bufferIndex+1]&0xff) <<  8 |
        (buffer[bufferIndex]&0xff);

      f[i] = Float.intBitsToFloat(integer);
    }
    return f;
  }

  public final byte[] readByteArray() throws IOException
  {
    datagram.setLength(BUFFER_SIZE);
    socket.receive(datagram);
    return datagram.getData();
  }

  public final PacketBuffer readPacketBuffer() throws IOException
  {
    datagram.setLength(BUFFER_SIZE);
    socket.receive(datagram);
    return new PacketBuffer(datagram.getData(), datagram.getLength());
  }

  /**
   * Faster because it does not create instance.
   */
  public final void readPacketBuffer(PacketBuffer pbuf) throws IOException
  {
    datagram.setLength(BUFFER_SIZE);
    socket.receive(datagram);
    pbuf.setBuf(datagram.getData());
  }

  public static void main(String argv[])
  {

    if (argv[0].equals("server"))
    {
      DataGramCommunication server = new DataGramCommunication(SERVER);
      server.setPort(8000);
      server.initConnection();
      byte[] b = new byte[1];
      float[] floatArray = new float[100];
      b[0] = 0;
      DatagramPacket p = new DatagramPacket(b,1);
      try 
      {
        for(int i=0;i<100;i++)
        {
          System.out.println("Received: " + server.readFloat());
        }
        floatArray = server.readFloatArray();
        for(int i=0;i<100;i++)
        {
          System.out.println("FloatArray[" + i + "]="+floatArray[i]);
        }
      }
      catch(IOException e)
      {
        e.printStackTrace();
      }
    }

    if(argv[0].equals("client"))
    {
      DataGramCommunication client = new DataGramCommunication(CLIENT);
      client.setInetAddress("localhost");
      client.setPort(8090);
      client.setServerPort(8000);
      client.initConnection();
      byte[] b = new byte[10];
      float[] floatArray = new float[100];
      b[0]=0;
      DatagramPacket p = new DatagramPacket(b,1);
      try 
      {
        for(int i=0;i<100;i++)
        {
          System.out.println("writing " + (i + 0.1234f));
          client.writeFloat((i + 0.1234f));
          floatArray[i] = (float)(i+0.12347f);
        }
        client.writeFloatArray(floatArray,100);
        client.close();
      }
      catch(IOException e)
      {
        e.printStackTrace();
      }
    }
  }
}
