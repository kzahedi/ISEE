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

import java.awt.Color;

import brightwell.analyser.Tool;
import brightwell.gui.drawingplane.DrawingPlane;

public class Mandelbrot extends Tool
{ 

  private DrawingPlane dp = null;

  public String getToolName()
  {
    return "Mandelbrot";
  }

  public String getToolDescription()
  {
    return "Mandelbrot - classics";
  }

  public boolean needsNet()
  {
    return false;
  }

  public void init()
  {
    addCheckBox("just press run", false);
  }

  public void doAnalysis()
  {
    dp = getNewWindow("Mandelbrot");

    for(double y = yStart; y < yEnd && doAnalysis; y = y + dy)
    {
      for(double x = xStart; x < xEnd && doAnalysis; x = x + dx)
      {
        calculateMagnetude(x,y);
      }
    }
  }

  private void calculateMagnetude(double x, double y)
  {
    double magnitude = 0;
    double zReal = x;
    double zComplex = y;
    double zRealNew = 0;
    double zComplexNew = 0;
    for(int i = 0; i < convergenceIterations && doAnalysis; i++)
    {
      zRealNew = zReal * zReal - zComplex * zComplex + x;
      zComplexNew = 2 * zReal * zComplex + y;

      zReal = zRealNew;
      zComplex = zComplexNew;

      magnitude = zReal * zReal + zComplex + zComplex;

      if(magnitude > 4)
      {
        dp.drawPoint(x,y, new Color(i * (Integer.MAX_VALUE/convergenceIterations) ));
        //System.out.println("" + i + "-th Iteration: Magnitude : " + magnitude);
        return;
      }
    }
    dp.drawPoint(x,y, Color.black);
  }
}
