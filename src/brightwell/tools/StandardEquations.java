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


package brightwell.tools;

import brightwell.analyser.Tool;

import brightwell.gui.drawingplane.DrawingPlane;


import java.awt.Color;

import org.apache.log4j.Logger;

import util.misc.IseeLogger;

public class StandardEquations extends Tool
{ 
  private DrawingPlane dp = null;

  private boolean doColoring = false;
  private String name = null;

  private static Logger log = IseeLogger.getLogger(StandardEquations.class);

  private String[] comboBoxEntries =  
  {
    "rp(1-p)",
    "Peter de Jong",
    "playfield"
  };


  public boolean needsNet()
  {
    return false;
  }

  public void doAnalysis()
  {
    int index = getComboBoxIndex("equation");
    doColoring = getCheckBox("coloring");

    name = comboBoxEntries[index];

    dp = getNewWindow(name);

    switch(index)
    {
      case 0:
        standard();
        break;
      case 1:
        peterDeJong();
        break;
      case 2:
        playfield();
        break;
    }
  }

  public void init()
  {
    addComboBox("equation",comboBoxEntries,0);
    addCheckBox("coloring",false);
    addDouble("a",-Double.MAX_VALUE,Double.MAX_VALUE,1.4);
    addDouble("b",-Double.MAX_VALUE,Double.MAX_VALUE,-2.3);
    addDouble("c",-Double.MAX_VALUE,Double.MAX_VALUE,2.4);
    addDouble("d",-Double.MAX_VALUE,Double.MAX_VALUE,-2.1);
  }

  public String getToolName()
  {
    return "Standard Equations";
  }

  public String getToolDescription()
  {
    return "Visualise standard equations";
  }

  private void standard()
  {
    double p = 0;
    for(double x = xStart; x < xEnd; x = x + dx)
    {
      p = 0.2;
      for(int c= 0; c < convergenceIterations; c++)
      {
        p = x * p * ( 1 - p);
      }
      for(int d = 0; d < drawIterations; d++)
      {
        p = x * p * ( 1 - p);
        dp.drawPoint(x,p);
      }
      if(!doAnalysis)
      {
        break;
      }
    }
  }

  private void peterDeJong()
  {
    double x = 0;
    double y = 0;
    double x1 = 0;
    double y1 = 0;
    double a = getDouble("a");
    double b = getDouble("b");
    double c = getDouble("c");
    double d = getDouble("d");

    for(int i=0; i < convergenceIterations && doAnalysis; i++)
    {
      x1 = Math.sin(a * y) - Math.cos(b * x);
      y1 = Math.sin(c * x) - Math.cos(d * y);

      dp.drawPoint(x1,y1);

      x = x1;
      y = y1;
    }
  }


  private void playfield()
  {
    double x = 0;
    double y = 0;
    double z = 0;
    double x1 = 0;
    double y1 = 0;
    double z1 = 0;
    double a = getDouble("a");
    double b = getDouble("b");
    double c = getDouble("c");
    double d = getDouble("d");
    double minX = 0;
    double maxX = 0;
    double minY = 0;
    double maxY = 0;
    double minZ = 0;
    double maxZ = 0;

    for(int i=0; i < convergenceIterations && doAnalysis; i++)
    {
      x1 = Math.sin(a * y) - Math.cos(b * x);
      y1 = Math.sin(c * x) - Math.cos(d * y);
      z1 = x1 * y1;

      if(doColoring)
      {
        if(i == 0)
        {
          minX = x1;
          maxX = x1;
          minY = y1;
          maxY = y1;
          minZ = z1;
          maxZ = z1;
        }
        else
        {
          if( x1 < minX)
          {
            minX = x1;
          }
          if( x1 > maxX)
          {
            maxX = x1;
          }
          if( y1 < minY)
          {
            minY = y1;
          }
          if( y1 > maxY)
          {
            maxY = y1;
          }
          if( z1 < minZ)
          {
            minZ = z1;
          }
          if( z1 > maxZ)
          {
            maxZ = z1;
          }
        }
      }
      dp.drawPoint(x1,y1);

      x = x1;
      y = y1;
    }
    if(doColoring)
    {

      dp = getNewWindow(name);
      x = 0;
      y = 0;
      z = 0;  

      log.info("coloring"); 
      for(int i=0; i < convergenceIterations && doAnalysis; i++)
      {
        x1 = Math.sin(a * y) - Math.cos(b * x);
        y1 = Math.sin(c * x) - Math.cos(d * y);
        z1 = Math.sin(c * x) - Math.cos(b * z);

        float cr = (float)((x1 - minX) / (maxX - minX));
        float cg = (float)((y1 - minY) / (maxY - minY));
        float cb = (float)((z1 - minZ) / (maxZ - minZ));
        cr = (float)Math.min(Math.max(0f, x1), 1f);
        cg = (float)Math.min(Math.max(0f, y1), 1f);
        cb = (float)Math.min(Math.max(0f, z1), 1f);
        dp.drawPoint(x1,y1, new Color(cr,cg,cb));

        x = x1;
        y = y1;
      }
    }
    
    log.info("done");
  }
}
