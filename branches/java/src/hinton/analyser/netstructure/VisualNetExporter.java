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
 * Created on 14.04.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package hinton.analyser.netstructure;

import hinton.analyser.toolkit.InputComponent;
import hinton.analyser.toolkit.InputValue;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import util.misc.DynamicClassLoader;


/**
 * @author rosemann
 *
 * The class VisualNetExporter implements an interface to
 * export networks in different file formates.
 * 
 * The export properties are set by a GUI-Dialog
 * 
 * To access different export file formats one can use the
 * class methods getAvailableExporterNames() and getExporter(String).
 */
public abstract class VisualNetExporter
{
  
  private static File    directory         = null;
  private static boolean isInitialized     = false;
  private static HashMap availableExporter;
  
  protected VisualNet  visualNet;
  protected File       fileName;
  private   JDialog    dlgNetExport;
  private   JLabel     lblCaptionFileName;
  private   JLabel     lblFileName;
  private   JButton    btnSave;
  private   JButton    btnClose;
  private   JButton    btnBrowse;
  private   JPanel     pnlProperties;
  private   InputComponent.Collection 
                       icc = new InputComponent.Collection("Export Properties");
  private   InputValue[] iv;
  
  public VisualNetExporter()
  {
    GridBagConstraints        gbc;

    this.dlgNetExport  = new JDialog(JOptionPane.getFrameForComponent(visualNet),
                                     "Export Net",
                                     true);
    this.btnBrowse          = new JButton("Browse");
    this.btnSave            = new JButton("Save");
    this.btnClose           = new JButton("Close");
    this.lblCaptionFileName = new JLabel("Filename");
    this.lblFileName        = new JLabel("");
    this.pnlProperties      = new JPanel(new GridLayout(1,1));

    this.dlgNetExport.getContentPane().setLayout(new GridBagLayout());

    this.iv = this.getProperties();

    if (this.iv != null)
    {
      for (int i = 0; i < this.iv.length; i++)
      {
        icc.addElement(this.iv[i]);
      }
    }
    this.pnlProperties.add(icc.getInputComponent());

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 5;
    gbc.gridheight = 2;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.anchor  = GridBagConstraints.NORTHWEST;
    gbc.fill    = GridBagConstraints.BOTH;
    this.dlgNetExport.getContentPane().add(this.pnlProperties,
                                           gbc);
                                       
    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 5;
    gbc.gridheight = 1;
    gbc.weightx = 1;
    gbc.weighty = 0;
    gbc.anchor  = GridBagConstraints.NORTHWEST;
    gbc.fill    = GridBagConstraints.HORIZONTAL;
    this.dlgNetExport.getContentPane().add(this.lblCaptionFileName,
                         gbc);

    lblFileName.setOpaque(true);
    lblFileName.setBackground(Color.WHITE);
    lblFileName.setPreferredSize(new Dimension(400, 25));
    gbc = new GridBagConstraints();                       
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 3;
    gbc.gridheight = 1;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.anchor  = GridBagConstraints.NORTHWEST;
    gbc.fill    = GridBagConstraints.HORIZONTAL;
    this.dlgNetExport.getContentPane().add(this.lblFileName,
                                           gbc);
                                       
    gbc = new GridBagConstraints();                       
    gbc.gridx = 3;
    gbc.gridy = 3;
    gbc.gridwidth = 2;
    gbc.gridheight = 1;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.anchor  = GridBagConstraints.NORTHEAST;
    gbc.fill    = GridBagConstraints.HORIZONTAL;
    this.btnBrowse.addActionListener(new ActionListener () {
      public void actionPerformed(ActionEvent e)
      {
        icc.performUpdate();
        setFileName();
      }
    });
    this.dlgNetExport.getContentPane().add(this.btnBrowse,
                                           gbc);
                                       
    gbc = new GridBagConstraints();                       
    gbc.gridx = 3;
    gbc.gridy = 4;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.anchor  = GridBagConstraints.NORTHEAST;
    gbc.fill    = GridBagConstraints.HORIZONTAL;              
    this.btnSave.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e)
      {
        btnSaveActionPerformed();
      }
    });
    this.dlgNetExport.getContentPane().add(this.btnSave,
                                           gbc);
                                       
                                       
    gbc = new GridBagConstraints();                       
    gbc.gridx = 4;
    gbc.gridy = 4;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.anchor  = GridBagConstraints.NORTHEAST;
    gbc.fill    = GridBagConstraints.HORIZONTAL;              
    this.btnClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e)
      {
        dlgNetExport.dispose();
      }
    });
    this.dlgNetExport.getContentPane().add(this.btnClose,
                                           gbc);
                                       
                                       
    this.dlgNetExport.pack();
                                  

  }
  
  public VisualNetExporter(VisualNet visualNet)
  {
    super();
    this.visualNet = visualNet;
  }
  
  /**
   * Event handler for the Save Button of the Exporter dialog.
   * 
   * If the save button is clicked exportNet() with the previously 
   * selected filename is called 
   *
   */
  private void btnSaveActionPerformed()
  {
    try
    {
      this.icc.performUpdate();
      
      if (this.fileName == null ||
          this.fileName.getName().length() == 0)
      {
        JOptionPane.showMessageDialog(this.dlgNetExport,
                                      "Please define a Filename");
        return;
      } else {
        exportNet(new FileOutputStream(this.fileName));
      }
    }
    catch (Exception e)
    {
      JOptionPane.showMessageDialog(this.dlgNetExport,
                            "Error saving File " + this.fileName.getPath());
      return;
    }
    
    this.dlgNetExport.dispose();
    
  }
  
  /**
   * Opens a SaveDialog from a JFileChooser to set the filename
   */
  private void setFileName()
  {
    FileFilter   ff          = new ExtensionFilter(
                                   this.getFileFormatExtension());
    JFileChooser fileChooser = new JFileChooser();
    
    if (ff != null)
    {
      fileChooser.setAcceptAllFileFilterUsed(false);
      fileChooser.setFileFilter(ff);
    }
    
    fileChooser.setCurrentDirectory(VisualNetExporter.directory);
    
    if (fileChooser.showSaveDialog(this.dlgNetExport) == 
        JFileChooser.APPROVE_OPTION)
    {
      VisualNetExporter.directory = fileChooser.getCurrentDirectory();
      this.fileName               = fileChooser.getSelectedFile();
      this.lblFileName.setText(this.fileName.getPath());
    }
  }
  
  /**
   * Sets the net to be exported
   * 
   * @param visualNet the net to be exported
   */
  private void setVisualNet(VisualNet visualNet)
  {
    this.visualNet = visualNet;
  }
  
  /**
   * This method shows a dialog to export the given
   * VisualNet
   * @param visualNet the visual representation of the net
   *                  to be exported                
   */
  public void showDialog()
  {
    this.lblFileName.setText("");
    this.dlgNetExport.show();
  }
  
  /**
   * This method has to be implemented when subclassing VisualNetExporter.
   * 
   * @return user definable parameters for the implemented export file format
   */ 
  protected abstract InputValue[] getProperties();
  
  /**
   * This method has to be implemented by subclassing VisualNetExporter.
   * Here you have to implement the code for saving in the given file format 
   * @param fos outputstream to write the VisualNet to 
   */
  protected abstract void exportNet(OutputStream fos) throws IOException;
  
  /**
   * This method has to be implemented when subclassing VisualNetExporter.
   * 
   * The export format name returned by this method is used by the factory
   * methods getAvailableExporterNames() and getExporter(String) to identify
   * the the class
   * 
   * @return name of the implemented export format 
   */
  public abstract String getExportFormatName();
  
  /**
   * This method has to be implemented when subclassing VisualNetExporter.
   * 
   * Gets the common fileextension for the implemented format (e.g JPEG, FIG...)
   * 
   * @return fileformat extension
   */
  protected abstract String getFileFormatExtension();
  
  /**
   * Classmethod to get the names of the available export file formats
   * 
   * The classes are searched in the subpackage <b>visualnetexporter</b>
   * and dynamically bound to hinton
   * 
   * @return names of the available export formates
   */
  public static String[] getAvailableExporterNames()
  {
    if (!VisualNetExporter.isInitialized)
    {
      VisualNetExporter.isInitialized = true;
      VisualNetExporter.initialize();
    }
    return (String[])VisualNetExporter.availableExporter.keySet().
                                       toArray(new String[0]);
  }
  
  /**
   * Classmethod to create an VisualNetExporter-Object of the given format
   * @param name name of the exporter
   * @param visualNet the VisualNet to be saved 
   * @return new instance of a VisualNetExporter
   *         null if no exporter of the given name is available
   */
  public static VisualNetExporter getExporter(String name, VisualNet visualNet)
  {
    VisualNetExporter exp = (VisualNetExporter)
                            VisualNetExporter.availableExporter.get(name);
    if (exp != null)
    {
      exp.setVisualNet(visualNet);
    }
    return exp;
  }
  
  /**
   * This method searches for VisualNetExporter implementations in the 
   * subpackage <b>visualnetexporter</b> and dynamically binds them to hinton
   */
  private static void initialize()
  {
    DynamicClassLoader dcl;
    String packageName = VisualNetExporter.class.getPackage().getName();
    VisualNetExporter expoter;
    String jarName     = VisualNetExporter.getJarName(VisualNetExporter.class);
  
    VisualNetExporter.availableExporter = new HashMap();
    try
    {
      File expoterFolder = new File(packageName.replace('.',
                                                        File.separatorChar) + 
                                   File.separatorChar + "visualnetexporter");
      File[] currentLayout;
      String currentFileName;
      ArrayList dummy = new ArrayList();
      Class  current = null;
      currentLayout = expoterFolder.listFiles();
      
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
                                      ".visualnetexporter."
                                      + currentFileName);
            }
            catch (NoClassDefFoundError ncdfe)
            {
        
            
                dcl = new DynamicClassLoader(
                                  expoterFolder.getPath(),
                                  null, false);
                current = dcl.loadClass("." + currentFileName);
            }
            VisualNetExporter.addExporter(current);
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
              "/visualnetexporter") &&
              je.getName().endsWith(".class"))
          {
            currentFileName = 
                    je.getName().substring(je.getName().lastIndexOf('/')+1);
            currentFileName = currentFileName.substring(0, 
                currentFileName.lastIndexOf('.'));
            
            try
            {
              current = Class.forName(packageName +
                                      ".visualnetexporter." +
                                      currentFileName);
            }
            catch (NoClassDefFoundError ncdfe)
            {
              dcl = new DynamicClassLoader(
                               expoterFolder.getAbsolutePath(),
                               jarName, true);
              current = dcl.loadClass("." + currentFileName);
            }
            VisualNetExporter.addExporter(current);
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
  private static void addExporter(Class expoterClass)
  {
    VisualNetExporter expoter;
    if (expoterClass != null &&
        expoterClass.getSuperclass().equals(VisualNetExporter.class) &&
        ! Modifier.isAbstract(expoterClass.getModifiers()))
    {
      try
      {
        expoter = (VisualNetExporter)expoterClass.newInstance();
        String name = (String)expoterClass.getDeclaredMethod(
                                "getExportFormatName",
                                new Class[] {}).invoke(expoter,
                                                       new Object[0]);
        VisualNetExporter.availableExporter.put(name, expoter);
      }
      catch (Exception e)
      {
        System.err.println("Skipping Exporter " + expoterClass.getName() + 
                           ", getExportFormatName() isn't implemented.");
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
  
  
  private static class ExtensionFilter extends FileFilter
  {
    String extension;
    
    private ExtensionFilter()
    {
    }
    
    public ExtensionFilter(String extension)
    {
      this.extension = extension;
    }
    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    public boolean accept(File f) {
      return (f.isDirectory() || f.getName().toUpperCase()
                                .endsWith(this.extension.toUpperCase()));
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    public String getDescription()
    {
      return this.extension.toUpperCase();
    }
  }
  
}
