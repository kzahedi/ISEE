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
 * Created on 19.03.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package hinton.analyser.netstructure;

import hinton.analyser.toolkit.InputComponent;
import hinton.analyser.toolkit.InputValue;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import util.misc.DynamicClassLoader;


/**
 * @author rosemann
 *
 * The class GraphLayouter implements an interface to
 * layout networks with different algorithms.
 * 
 * The layout algorithm is processed and parametrised by a
 * GUI-Dialog.
 * 
 * To access different Layout algorithms one can use the
 * class methods getAvailableLayoutNames() and getLayout(String).
 */
public abstract class GraphLayouter
{
  private static boolean isInitialized = false;
  private static HashMap availableLayouts;
  
  
  private JDialog dlgGraphLayout;
  private JPanel  pnlLayoutProperties;
  private JButton btnLayout;
  private JButton btnClose;
  private InputComponent.Collection 
                   icc = new InputComponent.Collection("Layout Properties");
  private InputValue[] iv;
  
  private VisualNet visualNet;
  
  protected GraphLayouter()
  {
    GridBagConstraints gbc;
    
    this.dlgGraphLayout      = new JDialog((Frame)null, "Layout graph", true);
    this.pnlLayoutProperties = new JPanel(new GridLayout(1,1));
    this.btnLayout           = new JButton("Layout");
    this.btnClose            = new JButton("Close");
    
    this.dlgGraphLayout.getContentPane().setLayout(new GridBagLayout());
    this.iv = this.getProperties();
    
    
    for (int i = 0; i < iv.length; i++)
    {
      this.icc.addElement(iv[i]);
    }
    this.pnlLayoutProperties.add(icc.getInputComponent());

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 3;
    gbc.gridheight = 1;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.anchor  = GridBagConstraints.NORTHWEST;
    gbc.fill    = GridBagConstraints.BOTH;
    this.dlgGraphLayout.getContentPane().add(this.pnlLayoutProperties,
                                             gbc);

    this.btnLayout.addActionListener(new ActionListener () {
      public void actionPerformed(ActionEvent e)
      {
        Point[] sp = { new Point(0,0), new Point(0,0) };
        Point[] orig;
        SynapseRenderer[] sr = visualNet.getSynapseRenderer();
        icc.performUpdate();
        computeInitialState(visualNet);
        layoutGraph(visualNet);
        for (int i = 0; i < sr.length; i++)
        {
          orig = sr[i].getSamplingPoints();
          sp[0] = orig[0];
          sp[1] = orig[orig.length - 1];
          sr[i].setSamplingPoints(sp);
        }
        dlgGraphLayout.dispose();
      }
    });   
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weightx = 1;
    gbc.weighty = 0;
    gbc.anchor  = GridBagConstraints.EAST;
    gbc.fill    = GridBagConstraints.HORIZONTAL;
    this.dlgGraphLayout.getContentPane().add(this.btnLayout,
                                             gbc);
                                             
    this.btnClose.addActionListener(new ActionListener () {
      public void actionPerformed(ActionEvent e)
      {
        dlgGraphLayout.dispose();
      }
    });   
    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weightx = 0;
    gbc.weighty = 0;
    gbc.anchor  = GridBagConstraints.EAST;
    gbc.fill    = GridBagConstraints.HORIZONTAL;
    this.dlgGraphLayout.getContentPane().add(this.btnClose,
                                             gbc);                                             
      
    this.dlgGraphLayout.pack();
  }
  
  /**
   * This method shows a dialog for layouting the given
   * VisualNet 
   * @param visualNet the visual representation of the net
   *                  to be layouted                
   */
  public final void showDialog(VisualNet visualNet)
  {
    this.visualNet = visualNet;
    this.dlgGraphLayout.show();
  }
  
  /**
   * This method has to be implemented by subclassing GraphLayouter.
   * Here you have to implement the layout algorithm. 
   * @param visualNet the visual representation of the net 
   *                  to be layouted
   */
  protected abstract void layoutGraph(VisualNet visualNet);
  
  /**
   * This method has to be implemented by subclassing GraphLayouter.
   * Here you can place code to produce an initial state for the implemented
   * algorithm depending on the given visualNet
   * 
   * This method is called before layoutGraph()
   * 
   * @param visualNet the visual representation of the net
   *                  for which the initial state is produced
   */
  protected abstract void computeInitialState(VisualNet visualNet);
  
  /**
   * This method has to be implemented by subclassing GraphLayouter.
   * 
   * The layoutname returned by this method is used by the factory methods
   * getAvailableLayoutNames() and getLayout(String) to identify the algorithm
   * 
   * @return name of the implemented layout algorithm 
   */
  public abstract String getLayoutName();

  /**
   * This method has to be implemented by subclassing GraphLayouter.
   * 
   * @return user definable parameters of the implemented layout algorithm
   */ 
  protected abstract InputValue[] getProperties();
  
  /**
   * Classmethod to get the names of the available Layout algorithms.
   * 
   * The layout algorithms are searched in the subpackage <b>graphlayouter</b>
   * and dynamically bound to hinton
   * 
   * @return names of the available layout algorithms
   */
  public static String[] getAvailableLayoutNames()
  {
    if (!GraphLayouter.isInitialized)
    {
      GraphLayouter.isInitialized = true;
      GraphLayouter.initialize();
    }
    return (String[])GraphLayouter.availableLayouts.keySet().
                                       toArray(new String[0]);
  }

  /**
   * Classmethod to create an GraphLayouter-Object with the given algorithm
   * @param name name of the layout algorithm, see getAvailableLayoutNames()
   * @return new instance of a GraphLayouter
   *         null if no algorithm of the given name is available
   */
  public static GraphLayouter getLayout(String name)
  {
    GraphLayouter layout = (GraphLayouter)
                            GraphLayouter.availableLayouts.get(name);
    try
    {
      return (GraphLayouter)layout.getClass().newInstance();
    }
    catch (Exception e)
    {
      return null;
    }
  }

  /**
   * This method searches for GraphLayouter implementations in the subpackage
   * <b>graphlayouter</b> and dynamically binds them to hinton
   *
   */
  private static void initialize()
  {
    DynamicClassLoader dcl;
    //String packageName = GraphLayouter.class.getPackage().getName();
    String packageName = "hinton.analyser.netstructure";
    //System.out.println("packageName: " + packageName);
    GraphLayouter layout;
    String jarName     = GraphLayouter.getJarName(GraphLayouter.class);
  
    GraphLayouter.availableLayouts = new HashMap();
    try
    {
      File layoutFolder = new File(packageName.replace('.',
                                                        File.separatorChar) + 
                                   File.separatorChar + "graphlayouter");
      File[] currentLayout;
      String currentFileName;
      ArrayList dummy = new ArrayList();
      Class  current = null;
      currentLayout = layoutFolder.listFiles();
      
      /*
       * Search FileSystem Folder for Layoutalgorithms
       */
      if (currentLayout != null)
      {
        for (int i = 0; i < currentLayout.length; i++)
        {
          currentFileName = currentLayout[i].getName();
          if (currentFileName.endsWith(".class"))
          {
      
            currentFileName = currentFileName.substring(0,
                                              currentFileName.lastIndexOf("."));
            try
            {
        
              current = Class.forName(packageName + 
                                      ".graphlayouter."
                                      + currentFileName);
            }
            catch (NoClassDefFoundError ncdfe)
            {
        
            
                dcl = new DynamicClassLoader(
                                  layoutFolder.getAbsolutePath(),
                                  null, false);
                current = dcl.loadClass("." + currentFileName);
            }
            GraphLayouter.addLayout(current);
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
              "/graphlayouter") &&
              je.getName().endsWith(".class"))
          {
            currentFileName = 
                    je.getName().substring(je.getName().lastIndexOf('/')+1);
            currentFileName = currentFileName.substring(0, 
                currentFileName.lastIndexOf('.'));
            try
            {
              current = Class.forName(packageName +
                                      ".graphlayouter." +
                                      currentFileName);
            }
            catch (Exception ncdfe)
            {
              dcl = new DynamicClassLoader(
                               layoutFolder.getPath(),
                               jarName, true);
              current = dcl.loadClass("." + currentFileName);
            }
            GraphLayouter.addLayout(current);
          }
        }
      }

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  
  
  }
  
  /*
   * Add only those classes to the list of available exporters, that
   * are subclasses of GraphLayout and are not declared abstract 
   */
  private static void addLayout(Class layoutClass)
  {
    GraphLayouter layout;
    if (layoutClass != null &&
        layoutClass.getSuperclass().equals(GraphLayouter.class) &&
        ! Modifier.isAbstract(layoutClass.getModifiers()))
    {
      try
      {
        layout = (GraphLayouter)layoutClass.newInstance();
        String name = (String)layoutClass.getDeclaredMethod("getLayoutName",
                                new Class[] {}).invoke(layout,
                                                       new Object[0]);
        GraphLayouter.availableLayouts.put(name, layout);
      }
      catch (Exception e)
      {
        // do nothing, if the Method <getLayoutName> isn't implemented 
        //             simply skip that Renderer
        System.err.println("Skipping Layout " + layoutClass.getName() + 
                           ", getLayoutName() isn't implemented.");
      }

    }
  }
  
  /*
   * Get the JarFileName that contains the class current or null
   * if none
   */
  private static String getJarName(Class current)
  {
    String name         = null;
    /* Separator character in a Jar-File is '/' system independent */
    String myName       = current.getName().replace('.', '/');
    String resourceName = current.getResource(
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
  
}
