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

/**
 * This class represents a packet buffer for comfortable IP communication.
 * You may add your int, floats and byte first to a buffer and write it one to
 * a socket. To save time 
 *
 */

import java.io.IOException;

public final class PacketBuffer 
{
  private int    maxBufSize;
  private int    bufSize;
  private int    pointer;
  private byte[] buf;
  
  /**
   * Create new PacketBuffer.
   */
  public PacketBuffer()
  {
    this.bufSize    = -1;
    this.maxBufSize = 1024;
    clearBuf();
    resetBuf();
  }

  /**
   * Create new PacketBuffer with given params and resets its read-write pointer
   * to 0.
   */
  public PacketBuffer(byte[] buf, int size)
  {
    this.buf        = buf;
    this.bufSize    = size;
    this.pointer    = 0;
    this.maxBufSize = buf.length;
  }

  /**
   * Sets the internal Buffer and the read-write pointer to its beginning (0).
   */
  public void setBuf(byte[] buf)
  {
    this.buf        = buf;
    this.bufSize    = buf.length;
    this.maxBufSize = buf.length;
    this.pointer    = 0;
  }

  /**
   * Return the WHOLE buffer, wich should be read to getBufSize() and written to
   * getMaxBufSize().
   */
  public byte[] getBuf()
  {
    return this.buf;
  }

  /**
   * Returns the size of valid domain of the buffer, wich was written to. This
   * means: If you put data into the buffer, this size will increase with
   * poistion of the read-write pointer.
   *
   * Note: this function only give you the VALID buffer mark. The function
   * getMaxBufferSize() will give you the maximum you can use.
   */
  public int getBufSize()
  {
    if (bufSize < 0)
    {
      return pointer;
    } else
    {
      return bufSize;
    }
  }

  /**
   * Makes a hard-clear of the buffer with reallocating it. You should avoid
   * this function and better use resetBuf().
   */
  public void clearBuf()
  {
    buf = new byte[maxBufSize];
  }

  /**
   * Makes a soft-clear of the buffe with resetting only the read-write pointer
   * and bufsize mark. Before reusing for writing your buffer you should always
   * call this function. It is very fast because it only modifies pointers.
   */
  public void resetBuf()
  {
    pointer =  0;
    bufSize = -1;
  }

  /**
   * Sets the maximum buffer size wich is 1024 as default. This function calls
   * clearBuf() after resizing.
   */
  public void setMaxBufSize(int maxBufSize)
  {
    this.maxBufSize = maxBufSize;
    clearBuf();
  }

  /**
   * Gets the maximum buffer size wich does NOT indicate how many useful data
   * was put into the buffer. It only gives you the maximum usable buffer size
   * wich is 1024 as default. You should use getBufferSize() to determine the
   * size of the used buffer for your reading attempt.
   */
  public int getMaxBufSize()
  {
    return maxBufSize;
  }

  /**
   * Writes a byte into the buffer an modifies the read-write pointer. 
   */
  public void writeByte(byte b)
  {
    buf[pointer] = b;
    pointer++;
  }

  /**
   * Writes an integer into the buffer an modifies the read-write pointer. 
   */
  public void writeInt(int i)
  {
    buf[pointer]     = (byte)  i;
    buf[pointer + 1] = (byte) (i >> 8);
    buf[pointer + 2] = (byte) (i >> 16);
    buf[pointer + 3] = (byte) (i >> 24);
    
    pointer += 4;
  }

  /**
   * Writes a float into the buffer an modifies the read-write pointer. 
   */
  public void writeFloat(float f)
  {
   writeInt(Float.floatToIntBits(f));
  }

  /**
   * Reads a byte from the buffer an modiefies the read-write pointer. 
   */
  public byte readByte() throws IOException 
  {
    if (pointer >= maxBufSize)
    {
      throw new IOException("End of Packetbuffer reached!");
    }
    
    pointer++;
    return buf[pointer-1];
  }

  /**
   * Reads an integer from the buffer an modiefies the read-write pointer. 
   */
  public int readInt() throws IOException 
  {
    if (pointer > maxBufSize)
    {
      throw new IOException("End of Packetbuffer reached or too short for an integer!");
    }

    pointer += 4;
    return 
      (buf[pointer - 1])      << 24 |
      (buf[pointer - 2]&0xff) << 16 |
      (buf[pointer - 3]&0xff) <<  8 |
      (buf[pointer - 4]&0xff);
  }

  /**
   * Reads a float from the buffer an modiefies the read-write pointer. 
   */
  public float readFloat() throws IOException 
  {
    if (pointer > maxBufSize)
    {
      throw new IOException("End of Packetbuffer reached or too short for a float!");
    }
    return Float.intBitsToFloat(readInt());
  }

  
// ----------------------------------------------------------------------------
// TESTS-main
// Call ist with first with "server"-argument and then with "client"argument
// ----------------------------------------------------------------------------
  public static void main(String argv[])
  {
    if (argv[0].equals("server"))
    {
      DataGramCommunication server;
      PacketBuffer pbuf;

      pbuf   = new PacketBuffer();
      server = new DataGramCommunication(DataGramCommunication.SERVER);
      server.setPort(8000);
      server.initConnection();
      try 
      {
        server.readPacketBuffer(pbuf);

        for(int i=0;i<100;i++)
        {
          System.out.println("Received: " + pbuf.readFloat());
        }
       
        server.close();
      }
      catch(IOException e)
      {
        e.printStackTrace();
      }
    }

    if(argv[0].equals("client"))
    {
      PacketBuffer pbuf;
      DataGramCommunication client;
      
      pbuf   = new PacketBuffer();
      client = new DataGramCommunication(DataGramCommunication.CLIENT);
      client.setInetAddress("localhost");
      client.setPort(8010);
      client.setServerPort(8000);
      client.initConnection();
      try 
      {
        for(int i=0;i<100;i++)
        {
          System.out.println("writing " + (i + 0.1234f));
          pbuf.writeFloat((i + 0.1234f));
        }
        client.writePacketBuffer(pbuf);

        client.close();
      }
      catch(IOException e)
      {
        e.printStackTrace();
      }
    }
  }
}
