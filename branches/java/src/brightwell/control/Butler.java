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


package brightwell.control;

import brightwell.analyser.DataStorage;
import brightwell.analyser.Tool;

import brightwell.gui.analyser.AnalyserPanel;
import brightwell.gui.control.ControlPanel;
import brightwell.gui.drawingplane.DrawingPlane;
import brightwell.gui.ranges.RangesPanel;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import util.io.EvolutionSaxInterface;
import util.misc.IseeLogger;

public class Butler extends JFrame 
                    implements ActionListener, EvolutionSaxInterface
{

  private RangesPanel rangesPanel = null;
  private ControlPanel controlPanel = null;
  private AnalyserPanel analyserPanel = null;
  private DataStorage storage = null;
  private Vector runningTools = new Vector();
  private Vector windowList = new Vector();
  private Vector threads = new Vector();

  private static Logger log = IseeLogger.getLogger(Butler.class);

  public Butler(String loggerConfig)
  {
    // **************************************************************************
    // general stuff
    // **************************************************************************
    super("Brightwell");
    this.addWindowListener(new WindowAdapter() { public void
        windowClosing(WindowEvent e) { System.exit(0); }});

    if(!System.getProperty("user.name").equals("zahedi"))
    {
      JOptionPane.showMessageDialog(this,
          "brightwell.BrightwellMain - Visualising the dynamics of a Neural Net\n" +
          "(c) 2004 Sankt Augustin, Germany\n"
          + "Keyan (keyan@users.sourceforge.net)\n"
          + "http://sourceforge.net/projects/isee/"
          ,"About",
          JOptionPane.INFORMATION_MESSAGE);
    }

    rangesPanel = new RangesPanel(this);
    controlPanel = new ControlPanel();
    analyserPanel = new AnalyserPanel(this);

    // make it visible
    JPanel panel = new JPanel();
    GridBagLayout gbl = new GridBagLayout();
    panel.setLayout(gbl);
    int row = 0;
    // width input line
    GridBagConstraints analyserPanelConstraints =
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    row++;
    GridBagConstraints rangesPanelConstraints =
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    row++;
    GridBagConstraints controlPanelConstraints =
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);


    gbl.setConstraints(analyserPanel, analyserPanelConstraints);
    gbl.setConstraints(rangesPanel, rangesPanelConstraints);
    gbl.setConstraints(controlPanel, controlPanelConstraints);

    panel.add(analyserPanel);
    panel.add(rangesPanel);
    panel.add(controlPanel);
    setContentPane(panel);

    //setSize(getMinimumSize());
    pack();

    controlPanel.runButton().addActionListener(this);
    controlPanel.clearButton().addActionListener(this);
  }

  /**
   * perfons action on the events of the buttons etc
   * @param    event 
   */
  public void actionPerformed(ActionEvent event)
  { 
    if(event.getSource() == controlPanel.clearButton())
    {
      killAllRunningProcesses();
      closeAllOpenWindows();
    }

    if(event.getSource() == controlPanel.runButton())
    {
      //analyserPanel.setData(rangesPanel.getData());
      storage = rangesPanel.getData(analyserPanel.tool().needsNet());
      if(storage != null)
      {
        Tool currentTool = (Tool)analyserPanel.tool().clone();
        currentTool.setDataStorage(storage);
        runningTools.add(currentTool);
        updateControlPanel();
        Thread t = new Thread(currentTool);
        threads.add(t);
        t.start();
        log.debug("Running tool " + currentTool.getToolName());
      }
    }
  }
  
  /**
   * @param    
   * @return   
   */
  public void netLoaded()
  { }

  // called when a tool is done
  public void finished(Tool tool)
  {
    threads.removeElementAt(runningTools.indexOf(tool));
    runningTools.remove(tool);
    updateControlPanel();
    log.debug("tools left   = " + runningTools.size());
    log.debug("threads left = " + threads.size());
  }

  // called when a tool is done
  public void closed(DrawingPlane dp)
  {
    windowList.remove(dp);
    updateControlPanel();
    log.debug("windows " + windowList.size());
  }

  public void addWindow(DrawingPlane dp)
  {
    windowList.add(dp);
    updateControlPanel();
  }


  private void updateControlPanel()
  {
    controlPanel.setRunningThreads(runningTools.size());
    controlPanel.setOpenWindows(windowList.size());
  }

  private void closeAllOpenWindows()
  {
    for(int i=0; i < windowList.size(); i++)
    {
      DrawingPlane dp = (DrawingPlane)windowList.elementAt(i);
      dp.dispose();
    }
  }

  private void killAllRunningProcesses()
  {
    for(int i=0; i < runningTools.size(); i++)
    {
      Tool tool = (Tool)runningTools.elementAt(i);
      tool.stopIt();
    }
  }

}

