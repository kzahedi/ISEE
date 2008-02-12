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


package hinton;

import com.martiansoftware.jsap.*;

import hinton.control.Butler;
import hinton.gui.StatusDialog;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import releaseinfo.IseeReleaseInfo;






/**
 *  Implementation of the main. Program is started by java Hinton.HintonMain.
 * 
 */
public class HintonMain 
{
  public static void main(String argv[])
  {

    JSAP jsap = new JSAP(); 

    Switch version = new Switch("version")
      .setShortFlag('v')
      .setLongFlag("version");
    version.setHelp("Prints version information.");

    FlaggedOption netOption = new FlaggedOption("net")
      .setStringParser(JSAP.STRING_PARSER)
      .setRequired(false) 
      .setShortFlag('n') 
      .setLongFlag("net");
    netOption.setHelp("Neural net to load.");

    FlaggedOption generationOption = new FlaggedOption("generation")
      .setStringParser(JSAP.INTEGER_PARSER)
      .setDefault("0")
      .setRequired(false) 
      .setShortFlag('g') 
      .setLongFlag("generation");
    generationOption.setHelp("Generation index.");

    FlaggedOption populationOption = new FlaggedOption("population")
      .setStringParser(JSAP.INTEGER_PARSER)
      .setDefault("0")
      .setRequired(false) 
      .setShortFlag('p') 
      .setLongFlag("population");
    populationOption.setHelp("Population index.");

    FlaggedOption inidiviualOption = new FlaggedOption("individual")
      .setStringParser(JSAP.INTEGER_PARSER)
      .setDefault("0")
      .setRequired(false) 
      .setShortFlag('i') 
      .setLongFlag("individual");
    inidiviualOption.setHelp("Individual index.");

    FlaggedOption configFileOption = new FlaggedOption("configure file")
      .setStringParser(JSAP.STRING_PARSER)
      .setRequired(false) 
      .setShortFlag('c') 
      .setLongFlag("config");
    configFileOption.setHelp("Load configure file.");




    try
    {
      jsap.registerParameter(version);
      jsap.registerParameter(configFileOption);
      jsap.registerParameter(netOption);
      jsap.registerParameter(generationOption);
      jsap.registerParameter(populationOption);
      jsap.registerParameter(inidiviualOption);
    }
    catch(JSAPException jse)
    {
      jse.printStackTrace();
      System.exit(-1);
    }

    JSAPResult config = jsap.parse(argv);

    if (!config.success()) {

      System.err.println();

      // print out specific error messages describing the problems
      // with the command line, THEN print usage, THEN print full
      // help.  This is called "beating the user with a clue stick."
      for (java.util.Iterator errs = config.getErrorMessageIterator();
          errs.hasNext();) {
        System.err.println("Error: " + errs.next());
          }

      System.err.println();
      System.err.println("Usage: java "
          + HintonMain.class.getName());
      System.err.println("                "
          + jsap.getUsage());
      System.err.println();
      System.err.println(jsap.getHelp());
      System.exit(1);
    }

    if(config.getBoolean("version"))
    {
          System.out.println(
              "Hinton.Executor - Neural Net execution & evalutaion\n"
          + "(c) 2002 Sankt Augustin, Germany\n"
          + "Keyan (keyan@users.sourceforge.net)\n"
          + "http://sourceforge.net/projects/isee/\n\n"
          + "compiled by    " + IseeReleaseInfo.getCompiledBy() + "\n"
          + "compiled with  " + IseeReleaseInfo.getCompiledWith() +"\n"
          + "compiled at    " + IseeReleaseInfo.getBuildDate() +"\n"
          + "build number   " + IseeReleaseInfo.getBuildNumber() +"\n"
          + "revision       " + IseeReleaseInfo.getRevisionNum());

      System.exit(0);
    }

    //  makes dialog look and feel
//    JDialog.setDefaultLookAndFeelDecorated(true);
//    JFrame.setDefaultLookAndFeelDecorated(true);
//    Toolkit.getDefaultToolkit().setDynamicLayout(true);
//    System.setProperty("sun.awt.noerasebackground","true");

    try 
    {
      if((System.getProperty("os.name")).equals("Linux"))
      {
        //UIManager.setLookAndFeel(new TonicLookAndFeel());
      }
      else
      {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
      }
    }  
    catch ( UnsupportedLookAndFeelException e ) 
    {
      System.out.println ("Metal Look & Feel not supported on this platform.\n");
      //System.exit(0);
    }
//    catch ( IllegalAccessException e ) 
//    {
//      System.out.println ("Metal Look & Feel could not be accessed.\n" 
//          + "Program Terminated");
//      System.exit(0);
//    }
//    catch ( ClassNotFoundException e ) 
//    {
//      System.out.println ("Metal Look & Feel could not found.\n" 
//          + "Program Terminated");
//      System.exit(0);
//    }   
//    catch ( InstantiationException e ) 
//    {
//      System.out.println ("Metal Look & Feel could not be instantiated.\n" 
//          + "Program Terminated");
//      System.exit(0);
//    }
    catch ( Exception e ) 
    {
      System.out.println ("Unexpected error. \nProgram Terminated");
      e.printStackTrace();
      System.exit(0);
    }

    if(!System.getProperty("user.name").equals("zahedi"))
    {
      JOptionPane.showMessageDialog(null,
          "Hinton.Executor - Neural Net execution & evalutaion\n"
          + "(c) 2002 Sankt Augustin, Germany\n"
          + "Keyan (keyan@users.sourceforge.net)\n"
          + "http://sourceforge.net/projects/isee/\n\n"
          + "compiled by     " + IseeReleaseInfo.getCompiledBy() + "\n"
          + "compiled with  " + IseeReleaseInfo.getCompiledWith() +"\n"
          + "compiled at      " + IseeReleaseInfo.getBuildDate() +"\n"
          + "revision            " + IseeReleaseInfo.getRevisionNum() +"\n"
          ,"About",
          JOptionPane.PLAIN_MESSAGE);
    }
 

    StatusDialog sd = new StatusDialog(0);
    Butler frame = new Butler();

    sd.hide();


    if(config.getString("configure file") != null)
    {
      frame.loadConfig(config.getString("configure file"));
    }
    if(config.getString("net") != null)
    {
      frame.loadCommandLineNet(config.getString("net"),config.getInt("generation"), config.getInt("population"), config.getInt("population"));
    }

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); 
    Dimension frameSize = frame.getSize();
    int x = (int)(screenSize.getWidth()/2.0d);
    int y  = (int)(screenSize.getHeight()/2.0d);
    x = x - (int)(frameSize.getWidth()/2.0d);
    y = y - (int)(frameSize.getHeight()/2.0d);
    frame.setLocation(x,y);
    frame.setVisible(true);

  }
}
