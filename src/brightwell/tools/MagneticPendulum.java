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

public class MagneticPendulum extends Tool
{ 


  private final static double MAX_DISTANCE = 0.0001; // when we stop calculating

  private final static int X_COORD = 0;
  private final static int Y_COORD = 1;

  private int numberOfMagnets = 3;
  private double distance_to_center = 0.25;

  private Color[] yColor = 
  {
    Color.black,
    Color.white
  };

  private final static Color[] MAGNET_COLOR =
  {
    new Color(170, 8, 8),
    new Color(13, 170, 8),
    new Color(11,8,170),
    new Color(209,209,10),
    new Color(4,209,195),
    new Color(160,17,146),
    new Color(11,74,122),
    new Color(102,70,163)
  };

  private double[][] magnetPos = { {  0,  -0.25},
                                   {  0.25 * Math.cos(Math.PI / 6d), 
                                      0.25 * Math.sin(Math.PI / 6d)},
                                   { -0.25 * Math.cos(Math.PI / 6d), 
                                      0.25 * Math.sin(Math.PI / 6d)}};
  private double frictionR   =  0.2;
  private double springC     =  0.5;
  private double distanceD   =  0.25;

  private double dt = 1;

  private DrawingPlane dp = null;

  public boolean needsNet()
  {
    return false;
  }

  public void doAnalysis()
  {
    dp = getNewWindow("Magnetic Pendulum");
    dt = getDouble("dt");

    numberOfMagnets = getInteger("#magnets");

    distance_to_center = getDouble("distance");

    magnetPos = new double[numberOfMagnets][2];
    // setting up the magnets
    for(int i = 0; i < numberOfMagnets; i++)
    {
      magnetPos[i][0] = distance_to_center * Math.sin( i * ( 2*Math.PI /
            (double)numberOfMagnets));
      magnetPos[i][1] = distance_to_center * Math.cos( i * ( 2*Math.PI /
            (double)numberOfMagnets));
    }

    double xi = 0;
    double yi = 0;
    for(int x = 0; x < stepsX && doAnalysis; x++)
    {
      xi = xStart + x * dx;
      for(int y = 0; y < stepsY && doAnalysis; y++)
      {
        yi = yStart + y * dy;
        dp.drawProgressPointY(yi, yColor[ (x+1) % 2 ]);
        calculatePendulum(xi, yi);
      }
    }
    dp.drawLegend();

  }

  public void init()
  {
    addDouble("dt",0,Double.MAX_VALUE,0.01);
    addInteger("#magnets",0,Integer.MAX_VALUE,3);
    addDouble("distance",0,Double.MAX_VALUE,0.25);
  }

  public String getToolName()
  {
    return "MagneticPendulum";
  }

  public String getToolDescription()
  {
    return "Fractal basins of magnetic pendulum";
  }

  private double fX(double x2, double x1, double y1)
  {
    double term1 = -frictionR * x2 - springC * x1;

    double sum = 0;
    for(int i = 0; i < magnetPos.length; i++)
    {
      sum +=
        (magnetPos[i][0] - x1) /
        Math.pow(
            Math.sqrt(
              Math.pow(magnetPos[i][0] - x1, 2) +
              Math.pow(magnetPos[i][1] - y1, 2) +
              Math.pow(distanceD, 2)),
            (magnetPos.length));
    }

    return term1 + sum;
  }

  private double fY(double y2, double x1, double y1)
  {
    double term1 = -frictionR * y2 - springC * y1;

    double sum = 0;
    for(int i = 0; i < magnetPos.length; i++)
    {
      sum +=
        (magnetPos[i][1] - y1) /
        Math.pow(
            Math.sqrt(
              Math.pow(magnetPos[i][0] - x1, 2) +
              Math.pow(magnetPos[i][1] - y1, 2) +
              Math.pow(distanceD, 2)),
            (magnetPos.length));
    }

    return term1 + sum;
  }

  // x[0] = x1, x[1] = x2
  // y[0] = y1, y[1] = y2
  // 2 var runge kutta 
  private void calculate(double[] x, double y[])
  {

    double a1 = x[1];
    double b1 = fX(x[1], x[0], y[0]);

    double c1 = y[1];
    double d1 = fY(y[1], x[0], y[0]);

    
    double a2 =     x[1] + 0.5 * dt * b1;
    double b2 = fX( x[1] + 0.5 * dt * b1, 
                    x[0] + 0.5 * dt * a1, 
                    y[0] + 0.5 * dt * c1);
    
    double c2 =     y[1] + 0.5 * dt * d1;
    double d2 = fY( y[1] + 0.5 * dt * d1, 
                    x[0] + 0.5 * dt * a1, 
                    y[0] + 0.5 * dt * c1);

 
    double a3 =     x[1] + 0.5 * dt * b2;
    double b3 = fX( x[1] + 0.5 * dt * b2, 
                    x[0] + 0.5 * dt * a2, 
                    y[0] + 0.5 * dt * c2);
    
    double c3 =     y[1] + 0.5 * dt * d2;
    double d3 = fY( y[1] + 0.5 * dt * d2, 
                    x[0] + 0.5 * dt * a2, 
                    y[0] + 0.5 * dt * c2);

 
    double a4 =     x[1] + dt * b3;
    double b4 = fX( x[1] + dt * b3, 
                    x[0] + dt * a3, 
                    y[0] + dt * c3);
    
    double c4 =     y[1] + dt * d3;
    double d4 = fY( y[1] + dt * d3, 
                    x[0] + dt * a3, 
                    y[0] + dt * c3);


    x[0] = x[0] + (dt / 6d) * ( a1 + 2 * a2 + 2 * a3 + a4);
    x[1] = x[1] + (dt / 6d) * ( b1 + 2 * b2 + 2 * b3 + b4);
    y[0] = y[0] + (dt / 6d) * ( c1 + 2 * c2 + 2 * c3 + c4);
    y[1] = y[1] + (dt / 6d) * ( d1 + 2 * d2 + 2 * d3 + d4);

  }


  private void calculatePendulum(double x, double y)
  {
    double xVector[]    = new double[2];
    double yVector[]    = new double[2];
    double x1old        = x;
    double x2old        = x;

    double y1old        = y;
    double y2old        = y;

    double x1new        = x;
    double x2new        = x;

    double y1new        = y;
    double y2new        = y;

    //double dt           = magneticPendulumPanel.dt
    
    //System.out.println(x + " " + y);

    xVector[0] = x;
    xVector[1] = 0;
    yVector[0] = y;
    yVector[1] = 0;

    for(int i=0; i < convergenceIterations; i++)
//    while(Math.abs(x1new - x1old) > MAX_DISTANCE ||
//          Math.abs(y1new - y1old) > MAX_DISTANCE)
    {
      calculate(xVector, yVector);

      //System.out.println(xVector[0] + " " + yVector[0]);
    }
    //System.out.println();
    //System.out.println();
    //System.out.println();

    dp.drawPoint(x, y, MAGNET_COLOR[getMagnetIndex(xVector[0],yVector[0])]);
    //dp.drawFilledCircle(x, y, 10,
        //MAGNET_COLOR[getMagnetIndex(xVector[0],yVector[0])]);
  }

  private int getMagnetIndex(double x, double y)
  {
    double dist = Double.POSITIVE_INFINITY;
    double tmp = 0;
    int index = -1;
    for(int i=0; i < magnetPos.length; i++)
    {
      tmp = Math.sqrt(
          Math.pow(x - magnetPos[i][0], 2)+
          Math.pow(y - magnetPos[i][1], 2));
      if(tmp < dist)
      {
        dist = tmp;
        index = i;
      }
    }
    return index;
  }


}

