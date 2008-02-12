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


package util.misc;

import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class EditNetFrame extends JFrame implements ActionListener
{
  private JPanel panel = new JPanel();
  private JTable networkTable = null;
  private JScrollPane tableScrollPane = null;
  public EditNetFrame()
  {
    // **************************************************************************
    // general stuff
    // **************************************************************************
    super("EditNetFrame");
    /*
    this.addWindowListener(new WindowAdapter() { public void
        windowClosing(WindowEvent e) {  }});
        */

    setBounds ( 100,100, 400,400);
    GridLayout layout = new GridLayout(1,2);
    JTable networkTable = new JTable(15,15);
    networkTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    tableScrollPane = new JScrollPane(networkTable);
    networkTable.setRowHeight(30);
    panel.add(tableScrollPane);
    panel.setLayout(layout);
    setContentPane(panel);
  }

  public void actionPerformed(ActionEvent event)
  {

  }

  public static void main(String argv[])
  {
    JDialog.setDefaultLookAndFeelDecorated(true);
    JFrame.setDefaultLookAndFeelDecorated(true);
    Toolkit.getDefaultToolkit().setDynamicLayout(true);
    System.setProperty("sun.awt.noerasebackground","true");

    try {
      javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme( 
          new javax.swing.plaf.metal.DefaultMetalTheme());
      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
    }  
    catch ( UnsupportedLookAndFeelException e ) {
      System.out.println ("Metal Look & Feel not supported on this platform.\n"
          +"Program Terminated");
      System.exit(0);
    }
    catch ( IllegalAccessException e ) {
      System.out.println ("Metal Look & Feel could not be accessed.\n" 
          + "Program Terminated");
      System.exit(0);
    }
    catch ( ClassNotFoundException e ) {
      System.out.println ("Metal Look & Feel could not found.\n" 
          + "Program Terminated");
      System.exit(0);
    }   
    catch ( InstantiationException e ) {
      System.out.println ("Metal Look & Feel could not be instantiated.\n" 
          + "Program Terminated");
      System.exit(0);
    }
    catch ( Exception e ) {
      System.out.println ("Unexpected error. \nProgram Terminated");
      e.printStackTrace();
      System.exit(0);
    }

    EditNetFrame EditNetFrame = new EditNetFrame();
    EditNetFrame.setVisible(true);

  }

}


