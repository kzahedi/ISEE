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


/**
 * Created on 04.05.2004
 *
 * class EvoSun.java of package Evolution
 * 
 * create the start and main frame of the GUI of the evolutionary program.
 * Ask for the number of populations #p and creates object evoTask as instance 
 * of the class Evolution with  #p populations and a specific average populations 
 * size (here 35) for each population. The two main windows / panels 
 * (EvoCtrl and GobalPopulationParameter) are initalized with the object evoTask. 
 */
package Evolution;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class EvoSun extends JFrame
{

  Evolution evoTask;
  JPanel mainPanel = new JPanel();


  public EvoSun(String cfgFileNames[])
  {
    super();

    // know what that means!!!
    if (!System.getProperty("user.name").equals("aml")
        && !System.getProperty("user.name").equals("zahedi"))
    {
      JOptionPane.showMessageDialog(this,
          "EvoSun - an implementation of the algorithm\n"
              + ">>Evolution of Neural Systems by Stochastics Synthesis<<\n"
              + "(c) 2002 Sankt Augustin, Germany\n"
              + "Martin Huelse (aberyst@users.sourceforge.net)\n"
              + "http://sourceforge.net/projects/isee/", "About",
          JOptionPane.INFORMATION_MESSAGE);
    }


    /* initial number of populations of the evolutions */
    String input = JOptionPane.showInputDialog("How many pops?", "1");
    int value = Integer.parseInt(input);
    evoTask = new Evolution(value, 35);


    PopulationList pops = evoTask.getPopulations();
    Population pop = null;
    int i = 0;
    for (pops.start(); ((pops.hasMore()) && (i < cfgFileNames.length)); pops
        .next())
    {
      File cgfFile = new File(cfgFileNames[i]);
      i++;
      if (cgfFile != null)
      {
        pop = pops.currentPop();
        PopParamXml popParamXml = new PopParamXml(pop);
        popParamXml.fillFromXml(cgfFile);
      }
    }


    // main window
    setTitle("EvoSun - Implentation of the Algorithm: Evolution of Neural Systems by Stochatic Synthesis");
    //setBounds (50,50,750,230);

    addWindowListener(new WindowAdapter()
    {

      public void windowClosing(WindowEvent e)
      {
        System.exit(0);
      }
    });


    // make it visible
    GridBagConstraints c = new GridBagConstraints();
    GridBagLayout layout = new GridBagLayout();

    mainPanel.setLayout(layout);

    ImageIcon icon = new ImageIcon(
        Toolkit.getDefaultToolkit().getImage(
          LoggingPanel.class.getResource("images/mini-isee.gif")));

    JLabel label = new JLabel(icon);
    JPanel p1 = new JPanel();
    p1.add(label);
    c.gridx = 0;
    c.gridy = 0;
    c.anchor = GridBagConstraints.NORTHWEST;
    layout.setConstraints(p1, c);
    mainPanel.add(p1);


    /* I know it's a shame but, */
    /* the following order of creation the "GlobalPopsTabbedPane" first and */
    /* "EvoCtrlPanel" as second is important */
    /* otherwise you get null-pointer exception, because the "EvoCrtlPanel" */
    /* is accessing to the populations generated by the */
    /* "GlobalPopsTabbedPane" method */

    /* determine PopPanel */
    GlobalPopParameterFrame gobalPopPanel = new GlobalPopParameterFrame(evoTask);


    /* create EvoPanel */
    EvoCtrlPanel evoCtrlPanel = new EvoCtrlPanel(evoTask);
    c.fill = GridBagConstraints.BOTH;
    c.weightx = 0.5;
    c.weighty = 0.5;
    c.gridx = 1;
    c.gridy = 0;
    layout.setConstraints(evoCtrlPanel.getPanel(), c);
    mainPanel.add(evoCtrlPanel.getPanel());


    setContentPane(mainPanel);
//    setSize(800, 230);
    pack();
    setVisible(true);
  }

  public static void main(String argv[])
  {
//    JDialog.setDefaultLookAndFeelDecorated(true);
//    JFrame.setDefaultLookAndFeelDecorated(true);
//    Toolkit.getDefaultToolkit().setDynamicLayout(true);
//    System.setProperty("sun.awt.noerasebackground", "true");
//
//    try
//    {
//      javax.swing.plaf.metal.MetalLookAndFeel
//          .setCurrentTheme(new javax.swing.plaf.metal.DefaultMetalTheme());
//      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
//    } catch (UnsupportedLookAndFeelException e)
//    {
//      System.out.println("Metal Look & Feel not supported on this platform.\n"
//          + "Program Terminated");
//      System.exit(0);
//    } catch (IllegalAccessException e)
//    {
//      System.out.println("Metal Look & Feel could not be accessed.\n"
//          + "Program Terminated");
//      System.exit(0);
//    } catch (ClassNotFoundException e)
//    {
//      System.out.println("Metal Look & Feel could not found.\n"
//          + "Program Terminated");
//      System.exit(0);
//    } catch (InstantiationException e)
//    {
//      System.out.println("Metal Look & Feel could not be instantiated.\n"
//          + "Program Terminated");
//      System.exit(0);
//    } catch (Exception e)
//    {
//      System.out.println("Unexpected error. \nProgram Terminated");
//      e.printStackTrace();
//      System.exit(0);
//    }

    EvoSun evolution = new EvoSun(argv);
  }

}
