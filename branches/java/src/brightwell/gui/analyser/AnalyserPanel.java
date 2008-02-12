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


package brightwell.gui.analyser;

import brightwell.analyser.Tool;
import brightwell.analyser.ToolLoader;

import brightwell.control.Butler;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;

import util.misc.IseeLogger;

public class AnalyserPanel extends JPanel implements ActionListener
{

  private static Logger log = IseeLogger.getLogger(AnalyserPanel.class);

  private JTabbedPane tabbedPane = new JTabbedPane();
  private Vector tools = new Vector();
  private JPanel rootPanel = new JPanel();
  private JButton reloadButton = new JButton("reload tools");
  private Butler parent = null;

  public AnalyserPanel(Butler parent)
  {
    this.parent = parent;

//    tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
    rootPanel.setLayout(new BorderLayout());

    tools = (new ToolLoader()).loadToolClasses();

    for(int i = 0; i < tools.size(); i++)
    {
      Tool tool = (Tool)tools.elementAt(i);
      tool.setParent(parent);
      tabbedPane.addTab(tool.getToolName(), null, tool.getPanel(), 
          "Analysis Range");

    }
    rootPanel.add(tabbedPane);
    rootPanel.add(reloadButton, BorderLayout.SOUTH);
    add(rootPanel);
    reloadButton.addActionListener(this);
  }


  public Tool tool()
  {
    Tool tool = null;

    tool = (Tool)tools.elementAt(tabbedPane.getSelectedIndex());

    return tool;
  }

  public void actionPerformed(ActionEvent event)
  {
    if(event.getSource() == reloadButton)
    {
      log.info("reloding tools");
      tabbedPane.removeAll();
      tools = (new ToolLoader()).loadToolClasses();

      for(int i = 0; i < tools.size(); i++)
      {
        Tool tool = (Tool)tools.elementAt(i);
        tool.setParent(parent);
        tabbedPane.addTab(tool.getToolName(), null, tool.getPanel(), 
            tool.getToolDescription());

      }
      log.info("done");
    }
  }

}

