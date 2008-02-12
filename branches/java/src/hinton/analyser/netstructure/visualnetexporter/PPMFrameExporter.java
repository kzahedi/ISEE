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

/*
 * Created on 01.06.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package hinton.analyser.netstructure.visualnetexporter;

import hinton.analyser.Analyser;
import hinton.analyser.NetObserver;
import hinton.analyser.netstructure.NeuronRenderer;
import hinton.analyser.netstructure.SynapseRenderer;
import hinton.analyser.netstructure.VisualNetExporter;
import hinton.analyser.toolkit.InputValue;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import cholsey.Net;


/**
 * @author rosemann
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PPMFrameExporter extends VisualNetExporter
                              implements NetObserver
{
  private static final Integer NETSTRUCTGRAB = new Integer(0);
  private static final Integer SCREENGRAB    = new Integer(1);
  
  private boolean             grabFrames = true;
  private String              fileNameBase;
  private LinkedList          queuedFrames;
  private int                 savedFrames;
  private int                 currWidth;
  private int                 currHeight;
  private LinkedList          bufferedImages;
  private NeuronRenderer[]    nr;
  private SynapseRenderer[]   sr;
  private int[]               buffer;
  private PPMFrameExporter    me;
  private InputValue.IInteger frameBuffer;
  private InputValue.IObject  grabSource;
  private InputValue[]        properties;
  private Rectangle           screen;
  private Robot               robot;
  private Object              sync;
  
  /* (non-Javadoc)
   * @see Hinton.analyser.netstructure.VisualNetExporter#getProperties()
   */
  protected InputValue[] getProperties() {
    if (this.frameBuffer == null)
    {
      this.frameBuffer = new InputValue.IInteger("Amount of frames to buffer",
          10, 2, 100);
      this.grabSource  = new InputValue.IObject("Grab frames from",
          0,
          new Integer[] { PPMFrameExporter.NETSTRUCTGRAB, 
                          PPMFrameExporter.SCREENGRAB },
          new String[] { "Netstructure View", "Screen" } );
      this.properties  = new InputValue[] { this.frameBuffer,
                                            this.grabSource};
    }
    return this.properties;
  }
  /* (non-Javadoc)
   * @see Hinton.analyser.netstructure.VisualNetExporter#exportNet(java.io.OutputStream)
   */
  protected void exportNet(OutputStream fos) throws IOException {
    Thread  saver;
    boolean isOutOfMemory = false;
    int     i             = 0;
    Dimension d;
    
    fos.close();
    if (this.grabSource.value.equals(PPMFrameExporter.NETSTRUCTGRAB))
    {
      d              = this.getDimension();
      this.currWidth      = d.width;
      this.currHeight     = d.height;
      
    }
    else
    {
      try
      {
        this.robot      = new Robot();
      }
      catch (Exception e)
      {
        JOptionPane.showMessageDialog(this.visualNet, 
            "Grabbing frames from screen is not possible.");
        return;
      }
      this.screen     = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
      this.currWidth  = this.screen.width;
      this.currHeight = this.screen.height;
    }
    this.sr             = this.visualNet.getSynapseRenderer();
    this.nr             = this.visualNet.getNeuronRenderer();
    this.buffer         = new int[this.currWidth*this.currHeight];
    this.bufferedImages = new LinkedList();
    this.queuedFrames   = new LinkedList();
    this.savedFrames    = 0;
    this.fileNameBase   = super.fileName.getPath().substring(
        0, super.fileName.getPath().lastIndexOf('.'));
    this.grabFrames     = true;
    this.me             = this;
    this.sync           = new Object();
    
    try
    {
      this.bufferedImages.add(new BufferedImage(this.currWidth,
          this.currHeight, BufferedImage.TYPE_INT_RGB));
      this.bufferedImages.add(new BufferedImage(this.currWidth,
          this.currHeight, BufferedImage.TYPE_INT_RGB));
    }
    catch (OutOfMemoryError oom)
    {
      isOutOfMemory = true;
    }
    i = 2;
    if (this.grabSource.value.equals(PPMFrameExporter.NETSTRUCTGRAB))
    {
      while (!isOutOfMemory && i < this.frameBuffer.value)
      {
        try
        {
          this.bufferedImages.add(new BufferedImage(this.currWidth,
              this.currHeight, BufferedImage.TYPE_INT_RGB));
          i++;
        }
        catch (OutOfMemoryError oom)
        {
          isOutOfMemory = true;
        }
      }
    }
    
    System.out.println("Buffered: " + this.bufferedImages.size());
    if (isOutOfMemory && this.bufferedImages.size() <= 1)
    {
      JOptionPane.showMessageDialog(this.visualNet,
          "Not enough memory to grab frames.");
      this.bufferedImages.clear();
      return;
    }
    
    saver   = new Thread(new Runnable() {
      public void run()
      {
        BufferedImage curr;
        int    num;
        while (queuedFrames.size() != 0 || grabFrames)
        {
          if (queuedFrames.size() != 0)
          {
            curr = (BufferedImage)queuedFrames.removeFirst();
            num  = savedFrames;
            saveFrame(curr, num);
            savedFrames++;
          }
        }
      }
    });
    Analyser.getInstance().registerNetObserver(this);
    saver.start();
    
    JOptionPane.showMessageDialog(super.visualNet,
        "Press OK to stop framegrabbing (" + this.currWidth + "x" +
        this.currHeight + ")");
    
    // Stop capturing after OK is pressed
    this.grabFrames = false;
    Analyser.getInstance().unregisterNetObserver(this);
    try
    {
      saver.join();
    }
    catch (Exception e)
    {
      
    }
    this.bufferedImages.clear();
    System.gc();
    
  }
  /* (non-Javadoc)
   * @see Hinton.analyser.netstructure.VisualNetExporter#getExportFormatName()
   */
  public String getExportFormatName() {
    return "PPM Framegrabber";
  }
  /* (non-Javadoc)
   * @see Hinton.analyser.netstructure.VisualNetExporter#getFileFormatExtension()
   */
  protected String getFileFormatExtension() {
    return "PPM";
  }
  
  
  private void saveFrame(BufferedImage bi, int num)
  {
    BufferedOutputStream fos;
    
    try
    {
      fos = new BufferedOutputStream(new FileOutputStream(this.fileNameBase + 
          this.fill(num) + ".ppm"), 2048);
      
      bi.getRGB(0,0,this.currWidth, this.currHeight, this.buffer, 0,
          this.currWidth);  
      
      fos.write("P6\n".getBytes());
      fos.write((this.currWidth + " " + this.currHeight
          + "\n").getBytes());
      fos.write("255\n".getBytes());
      
      
      
      for (int i = 0; i < this.buffer.length; i++)
      {
        fos.write((byte)((this.buffer[i] >> 16) & 0xff));
        fos.write((byte)((this.buffer[i] >>  8) & 0xff));
        fos.write((byte)(this.buffer[i] & 0xff));
      }
      bi.flush();
      this.markImageUnused(bi);
      // Wake up waiting Thread
      synchronized(this.sync)
      {
        this.sync.notify();
      }
      fos.flush();
      fos.close();
    } catch (Exception e) {
      
    }
  }
  
  private String fill(int num)
  {
    String ret = "" + num;
    while (ret.length() < 4)
    {
      ret = "0" + ret;
    }
    return ret;
  }
  
  private final void grabFrame()
  {
    BufferedImage     bi  = this.getImage();
    Graphics2D        g2d = bi.createGraphics();
    
    if (this.grabSource.value.equals(PPMFrameExporter.NETSTRUCTGRAB))
    {
      
      g2d.setColor(Color.WHITE);
      g2d.fillRect(0,0, this.currWidth, this.currHeight);
    
      for (int i = 0; i < sr.length; i++)
      {
      
        this.sr[i].paint(g2d.create(sr[i].getX(), sr[i].getY(),
                            sr[i].getWidth(), sr[i].getHeight()));
      
      }   
      
      
      for (int i = 0; i < nr.length; i++)
      {
      
        this.nr[i].paint(g2d.create(nr[i].getX(), nr[i].getY(),
                            nr[i].getWidth(), nr[i].getHeight()));
      
      }
      
    }
    else
    {
      bi = this.robot.createScreenCapture(this.screen);
      Runtime.getRuntime().runFinalization();
      System.gc();
    }
    bi.flush();
    this.queuedFrames.add(bi);
    
  }
  
  private final BufferedImage getImage()
  {
    while (this.bufferedImages.size() == 0)
    {
      try
      {
        // Wait until bufferedImages-size is > 0
        synchronized (this.sync)
        {
          this.sync.wait(); 
        }
      }
      catch (Exception e)
      {
        
      }
    }
    
    return (BufferedImage)this.bufferedImages.removeFirst();
  }
  
  private final void markImageUnused(BufferedImage image)
  {
    this.bufferedImages.addLast(image);
  }
  
  /**
   * determines the size of the smallest rectangle bordering the
   * visualized graph
   * @return graph bordering rectangle
   */
  private Dimension getDimension()
  {
    Dimension        d  = new Dimension();
    NeuronRenderer[] nr = this.visualNet.getNeuronRenderer();
    int              w, h;
  
        d.width  = 0;
        d.height = 0;
    for (int i = 0; i < nr.length; i++)
    {
      if ((w = nr[i].getX() + nr[i].getWidth()) > d.width)
      {
        d.width  = w + 100;
      }
      if ((h = nr[i].getY() + nr[i].getHeight()) > d.height)
      {
        d.height = h + 100;
      }
    }
    return d;
  }
  /* (non-Javadoc)
   * @see Hinton.analyser.NetObserver#setNet(Cholsey.Net)
   */
  public void setNet(Net net) {
    // TODO Auto-generated method stub
    
  }
  /* (non-Javadoc)
   * @see Hinton.analyser.NetObserver#netUpdate(Cholsey.Net)
   */
  public void netUpdate(Net net) {
    this.grabFrame();   
  }
  

}
