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

package brightwell;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import releaseinfo.IseeReleaseInfo;
import brightwell.control.Butler;


/**
 * Implements the graphical user interface to the analysis of dynamical systems.
 */
public class BrightwellMain 
{
  
  public static void main(String argv[])
  {
    if(argv.length > 0 && argv[0].trim().equals("--version"))
    {
      System.out.println(
          "Brightwell - Neural Net anaysis\n"
          + "(c) 2002 Sankt Augustin, Germany\n"
          + "Keyan (keyan@users.sourceforge.net)\n"
          + "http://sourceforge.net/projects/isee/\n\n"
          + "compiled by    " + IseeReleaseInfo.getCompiledBy() + "\n"
          + "compiled with  " + IseeReleaseInfo.getCompiledWith() +"\n"
          + "compiled at    " + IseeReleaseInfo.getBuildDate() +"\n"
          + "revision       " + IseeReleaseInfo.getRevisionNum());
      System.exit(0);
    }
    if(!System.getProperty("user.name").equals("zahedi"))
    {
      JOptionPane.showMessageDialog(null,
          "Brightwell - Neural Net anaysis\n"
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
    //
//    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

    //JDialog.setDefaultLookAndFeelDecorated(true);
    //JFrame.setDefaultLookAndFeelDecorated(true);
    //Toolkit.getDefaultToolkit().setDynamicLayout(true);
    //System.setProperty("sun.awt.noerasebackground","true");

    try 
    {
      if((System.getProperty("os.name")).equals("Linux"))
      {
        //UIManager.setLookAndFeel(new TonicLookAndFeel());
        //PlasticXPLookAndFeel.setMyCurrentTheme(
        //   themeManager.getCurrentTheme().getJGoodiesTheme() );
        //UIManager.setLookAndFeel( new PlasticXPLookAndFeel() );
        //UIManager.setLookAndFeel("de.muntjak.tinylookandfeel.TinyLookAndFeel");
        //TinyLookAndFeel.class.getResource("./themes/YQ Plastic.theme");
        //UIManager.setLookAndFeel("com.shfarr.ui.plaf.fh.FhLookAndFeel"); 
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

    Butler butler = null;
    if(argv.length == 0)
    {
      butler = new Butler(null);
    }
    else
    {
      butler = new Butler(argv[0]);
    }
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = butler.getSize();
    int x = (int)(screenSize.getWidth() - frameSize.getWidth());
    int y = (int)(screenSize.getHeight() - frameSize.getHeight());
    butler.setLocation(x,y);
    butler.setVisible(true);
  }


}

