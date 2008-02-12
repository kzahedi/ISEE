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
 * Created on 08.04.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package hinton.analyser.netstructure;

import hinton.analyser.toolkit.InputComponent;
import hinton.analyser.toolkit.InputValue;
import hinton.analyser.toolkit.InputValueListener;

import java.awt.Shape;
import java.awt.geom.QuadCurve2D;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import util.misc.DynamicClassLoader;





/**
 * @author rosemann
 *
 * SynapseRendererFactory is a Factory class for creating
 * SynapseRenderer.
 * 
 * The SynapseRenderers are searched in the subpackage <b>synapserenderer</b>
 * and dynamically bound to hinton
 */
public class SynapseRendererFactory
{
  
  private static  SynapseRendererFactory instance = null;
  private static final SynapseRenderer DEFAULT_RENDERER     = 
    new SynapseRendererFactory.SynapseCircularSegmentRenderer();
  private HashMap synapseRenderer;
 
  private SynapseRendererFactory()
  {
    DynamicClassLoader dcl;
    String packageName = this.getClass().getPackage().getName();
    SynapseRenderer renderer;
    String jarName     = this.getJarName();
  
    this.synapseRenderer = new HashMap();
    this.synapseRenderer.put(
        SynapseRendererFactory.DEFAULT_RENDERER.getRendererName(),
        SynapseRendererFactory.DEFAULT_RENDERER.getClass());
    try
    {
      File synapserendererFolder = new File(packageName.replace('.',
                                                        File.separatorChar) + 
                                   File.separatorChar + "synapserenderer");
      File[] currentRenderer;
      String currentFileName;
    
      Class  current = null;
      currentRenderer = synapserendererFolder.listFiles();
      
      /*
       * Search FileSystem Folder for SynapseRenderer
       */
      if (currentRenderer != null)
      {
        for (int i = 0; i < currentRenderer.length; i++)
        {
          currentFileName = currentRenderer[i].getName();
          if (currentFileName.endsWith(".class"))
          {
      
            currentFileName = currentFileName.substring(0,
                                              currentFileName.lastIndexOf("."));
            try
            {
        
              current = Class.forName(packageName + 
                                      ".synapserenderer."
                                      + currentFileName);
            }
            catch (NoClassDefFoundError ncdfe)
            {
        
            
                dcl = new DynamicClassLoader(
                                  synapserendererFolder.getAbsolutePath(),
                                  null, false);
                current = dcl.loadClass("." + currentFileName);
            }
            this.addSynapseRenderer(current);
          }
        }
      }
      /*
       * Search Jar-File, if exists, for Layoutalgorithms
       */
      if (jarName != null)
      {
        JarFile jf = new JarFile(jarName);
        Enumeration jes = jf.entries();
        
        while (jes.hasMoreElements())
        {
          JarEntry je = (JarEntry)jes.nextElement();
          
          if (je.getName().startsWith(packageName.replace('.', '/') +
              "/synapserenderer") &&
              je.getName().endsWith(".class"))
          {
            currentFileName = 
                    je.getName().substring(je.getName().lastIndexOf('/')+1);
            currentFileName = currentFileName.substring(0, 
                currentFileName.lastIndexOf('.'));
            try
            {
              current = Class.forName(packageName +
                                      ".synapserenderer." +
                                      currentFileName);
            }
            catch (NoClassDefFoundError ncdfe)
            {
              
              dcl = new DynamicClassLoader(
                               synapserendererFolder.getPath(),
                               jarName, true);
              current = dcl.loadClass("." + currentFileName);
              
            }
            this.addSynapseRenderer(current);
          }
        }
      }

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  
    
  }

  /**
   * This method returns the singleton instance of SynapseRendererFactory
   * @return instance of SynapseRendererFactory
   */ 
  public static SynapseRendererFactory getInstance()
  {
    if (SynapseRendererFactory.instance == null)
    {
      SynapseRendererFactory.instance = new SynapseRendererFactory();
    }
    return SynapseRendererFactory.instance;
  }
  
  /**
   * Returns the names of the available SynapseRenderers
   * @return available SynapseRenderer
   */
  public String[] getAvailableRendererNames()
  {
    return (String[])this.synapseRenderer.keySet().toArray(new String[0]);
  }
  
  public String getDefaultRendererName()
  {
    return SynapseRendererFactory.DEFAULT_RENDERER.getRendererName();
  }
  
  /**
   * This method creates a new SynapseRenderer of the given name
   * with the given initial data
   * 
   * @param rendererName name of the SynapseRenderer to be created
   * @param rendererContainer the related VisualNet
   * @param initialData the data the newly created SynapseRenderer is
   *                    initialized with
   * @return new SynapseRenderer of the type specified by the given name
   * @throws UnsupportedDataTypeException
   */
  public SynapseRenderer createRendererByName(String rendererName,
                                           VisualNet rendererContainer,
                                           Object    initialData)
                         throws UnsupportedDataTypeException
  {
    SynapseRenderer ret = null;
    Constructor     c;
    Class           renderer = (Class)this.synapseRenderer.get(rendererName);
    
    if (renderer != null)
    {
      try
      {
        c = renderer.getConstructor(new Class[] { VisualNet.class, 
                                                  Object.class });
        ret = (SynapseRenderer)
              c.newInstance(new Object[] { rendererContainer, initialData });
        
      }
      catch (NoSuchMethodException e)
      {
        // can't occur since SynapseRenderer declares a Constructor of that
        // signature
      } catch (IllegalArgumentException e) {
        // can't occur 
      } catch (InstantiationException e) {
        // classes contained in this.synapseRenderer are not abstract
      } catch (IllegalAccessException e) {
        // Constructor is public
      } catch (InvocationTargetException e) {
        // Target Constructor can throw an UnsupportedDataTypeException
        throw new UnsupportedDataTypeException(e.toString());
      }                                                
      
    }
    return ret;
  }
  
  /*
   * Add only those classes to the list of available renderer, that
   * are subclasses of SynapseRenderer and are not declared abstract 
   */
  private void addSynapseRenderer(Class srClass)
  {
    SynapseRenderer sr;
    if (srClass != null &&
        srClass.getSuperclass().equals(SynapseRenderer.class) &&
        ! Modifier.isAbstract(srClass.getModifiers()))
    {
      try
      {
        sr = (SynapseRenderer)srClass.newInstance();
        String name = (String)srClass.getDeclaredMethod("getRendererName",
                                new Class[] {}).invoke(sr,
                                                       new Object[0]);
        this.synapseRenderer.put(name, srClass);
      }
      catch (Exception e)
      {
        // do nothing, if the Method <getRendererName> isn't implemented 
        //             simply skip that Renderer
        System.err.println("Skipping Renderer " + srClass.getName() + 
                           ", getRendererName() isn't implemented.");
      }

    }
  }
  
  /*
   * Get the JarFileName that contains the class current or null
   * if none
   */
  private String getJarName()
  {
    String name         = null;
    /* Separator character in a Jar-File is '/' system independent */
    String myName       = this.getClass().getName().replace('.', '/');
    String resourceName = this.getClass().getResource(
                                      "/" + myName + ".class").toString();
    int    begin, end;
    
    if (resourceName.startsWith("jar:"))
    {
      end   = resourceName.indexOf('!');
      begin = resourceName.substring(0, end).lastIndexOf('/');
      name = resourceName.substring(begin + 1, end);
    }
    else
    {
      return null;
    }
    return name;
  }
  
  
  /**
   * @author rosemann
   *
   * This class implements a visual representation of a synapse as a circular
   * segment which is handled as the default representation
   */
  public static class SynapseCircularSegmentRenderer extends SynapseRenderer
                                  implements InputValueListener
  {

    private QuadCurve2D         arc       = new QuadCurve2D.Double();
    private InputValue.IInteger arcHeight = 
                   new InputValue.IInteger("Segment height",
                                           SynapseCircularSegmentRenderer.DEFAULT_ARC_HEIGHT,
                                           1,
                                           40);
    
    private static int  DEFAULT_ARC_HEIGHT = 12;
    
    public SynapseCircularSegmentRenderer() {}
    
    public SynapseCircularSegmentRenderer(VisualNet rendererContainer, Object initialData) {
      super(rendererContainer, initialData);
      arc = new QuadCurve2D.Double();
      arcHeight.addInputValueListener(this);
    }
    
    /*
     *  (non-Javadoc)
     * @see Hinton.analyser.netstructure.ComponentRenderer#getRendererName()
     */
    public String getRendererName()
    {
      return "Circular Segment Renderer";
    }


    /*
     *  (non-Javadoc)
     * @see Hinton.analyser.netstructure.SynapseRenderer#getSynapseShape()
     */
    public Shape getSynapseShape()
    {
      return arc;
    }

    /* (non-Javadoc)
     * @see Hinton.analyser.netstructure.SynapseRenderer#locationUpdated()
     */
    public void locationUpdated()
    {
      double      vx, vy, lengthV, h, q, a,
                  vnx, vny, anchX, anchY;
      int         idx;
     
      try
      {
        idx     = this.samplingPoints.length - 1;
      
        vx      = this.samplingPoints[idx].x -
                  this.samplingPoints[0].x;
        vy      = this.samplingPoints[idx].y -
                  this.samplingPoints[0].y;
        lengthV = Math.sqrt(vx*vx + vy*vy);
        vx      = vx / lengthV;
        vy      = vy / lengthV;
        lengthV = lengthV / 2.0d;
        anchX   = this.samplingPoints[0].x + 
                  vx * lengthV;
        anchY   = this.samplingPoints[0].y +
                  vy * lengthV;
        vnx     = -vy;
        vny     =  vx;
    
        a = this.arcHeight.value;
          
        arc.setCurve((double)this.samplingPoints[0].x,
                     (double)this.samplingPoints[0].y,
                     anchX + a * vnx,
                     anchY + a * vny,
                     (double)this.samplingPoints[idx].x,
                     (double)this.samplingPoints[idx].y); 
      }
      catch (Exception e)
      {
        e.toString();
      }
    }


    
    /* (non-Javadoc)
     * @see Hinton.analyser.netstructure.ComponentRenderer#getRenderingProperties()
     */
    public InputComponent getConcreteRenderingProperties() {
      InputComponent.Collection myCollection =
        new InputComponent.Collection("Circular Segment Renderer");
      myCollection.addElement(this.arcHeight);
      return myCollection;
    }
    

    /* (non-Javadoc)
     * @see Hinton.analyser.netstructure.toolkit.InputValueListener#inputValueUpdated(Hinton.analyser.netstructure.toolkit.InputValue)
     */
    public void inputValueUpdated(InputValue iv)
    {
      this.locationUpdated();
    }

    /* (non-Javadoc)
     * @see Hinton.analyser.netstructure.ComponentRenderer#getPersistentOptions()
     */
    public PersistentRendererOption[] getPersistentOptions() {
      // TODO Auto-generated method stub
      return null;
    }

    /* (non-Javadoc)
     * @see Hinton.analyser.netstructure.ComponentRenderer#setPersistentOptions(Hinton.analyser.netstructure.PersistentRendererOption[])
     */
    public void setPersistentOptions(PersistentRendererOption[] pro) {
      // TODO Auto-generated method stub
      
    }

  }
  
}
