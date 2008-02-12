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


package brightwell.gui.control;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ControlPanel extends JPanel implements ActionListener
{

  private JButton clearButton     = new JButton("clear");
  private JButton runButton       = new JButton("run");
  private JButton exitButton      = new JButton("exit");

  private JLabel  openWindows     = new JLabel("open windows");
  private JLabel  runningWindows  = new JLabel("running tools");

  private JTextField openWindowsT    = new JTextField("0",2);
  private JTextField runningWindowsT = new JTextField("0",2);


  public ControlPanel()
  {
    GridBagLayout gbl = new GridBagLayout();
    setLayout(gbl);

    setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder("Control Panel "),
          BorderFactory.createEmptyBorder(5,5,5,5)));


    GridBagConstraints openWindowsConstraints =
      new GridBagConstraints(0, 0, 2, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints openWindowsTConstraints =
      new GridBagConstraints(2, 0, 2, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(openWindows, openWindowsConstraints);
    gbl.setConstraints(openWindowsT, openWindowsTConstraints);

    add(openWindows);
    add(openWindowsT);

    GridBagConstraints runningWindowsConstraints =
      new GridBagConstraints(0, 1, 2, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints runningWindowsTConstraints =
      new GridBagConstraints(2, 1, 2, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(runningWindows, runningWindowsConstraints);
    gbl.setConstraints(runningWindowsT, runningWindowsTConstraints);

    add(runningWindows);
    add(runningWindowsT);

    GridBagConstraints runButtonConstraints =
      new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints clearButtonConstraints =
      new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints exitButtonConstraints =
      new GridBagConstraints(2, 2, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);


    gbl.setConstraints(runButton, runButtonConstraints);
    gbl.setConstraints(clearButton, clearButtonConstraints);
    gbl.setConstraints(exitButton, exitButtonConstraints);

    add(runButton);
    add(clearButton);
    add(exitButton);

    exitButton.addActionListener(this);

    openWindowsT.setEditable(false);
    runningWindowsT.setEditable(false);

    openWindowsT.setHorizontalAlignment(JTextField.RIGHT);
    runningWindowsT.setHorizontalAlignment(JTextField.RIGHT);


  }

  public JButton runButton()
  {
    return runButton;
  }

  public JButton clearButton()
  {
    return clearButton;
  }

  public void actionPerformed(ActionEvent event)
  {
    if(event.getSource() == exitButton)
    {
      System.out.println("You will come back");
      System.exit(0);
    }
  }

  public void setOpenWindows(int windows)
  {
    openWindowsT.setText(""+windows);
  }

  public void setRunningThreads(int threads)
  {
    runningWindowsT.setText(""+threads);
  }
}
