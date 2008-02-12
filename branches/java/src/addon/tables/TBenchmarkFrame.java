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

package addon.tables;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;


/**
 * This class represents a small frame with a table for benchmarkoutput from the
 * time-benchmark fitness function.
 * robot.
 *
 */
public class TBenchmarkFrame extends JFrame
{
  public final static boolean SHOW_FITNESS_VALUE = true;

  private JPanel content;
  private JTable table;
  private JLabel title;

  private boolean _showFitnessValue = false;

  public TBenchmarkFrame()
  {
    this(false);
  }

  public TBenchmarkFrame(boolean showFitnessValue)
  {
    super("Timer Benchmark");
    this._showFitnessValue = showFitnessValue;

    title = new JLabel("Results:");

    table = new JTable(10,2);

    table.setValueAt("Cycles",0,0);
    table.setValueAt("Warmup",1,0);
    table.setValueAt("Steps ",2,0);
    table.setValueAt("Time (ms)"  ,3,0);
    table.setValueAt("Time/Cycles",4,0);
    table.setValueAt("Last Time (ms)",6,0);
    table.setValueAt("Last Time/Cycles",7,0);
    if(_showFitnessValue)
    {
      table.setValueAt("Current fitness",9,0);
    }
      
    content = (JPanel) this.getContentPane();
    content.setLayout(new BorderLayout());
    content.add(table, BorderLayout.CENTER);
    content.add(title, BorderLayout.NORTH);
    
    this.setSize(new Dimension(230,220));
    this.show();
  }

  public void setCycles(int cyc)
  {
    table.setValueAt("" + cyc,0,1);
  }
  
  public void setWarmup(int wu)
  {
    table.setValueAt("" + wu,1,1);
  }
  
  
  public void setSteps(int s)
  {
    table.setValueAt("" + s,2,1);
  }

  public void setTime(long t)
  {
    table.setValueAt("" + t,3,1);
  }
  
  public void setTimeCW(double tcw)
  {
    table.setValueAt("" + tcw,4,1);
  }

  public void setLastTime(long lt)
  {
    table.setValueAt("" + lt,6,1);
  }
  
  public void setLastTimeCW(double ltcw)
  {
    table.setValueAt("" + ltcw,7,1);
  }  

  public void setFitness(double fitness)
  {
    if(_showFitnessValue)
    {
      table.setValueAt("" + fitness,9,1);
    }
  }  

}
