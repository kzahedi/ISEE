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


package Evolution.Random;


import java.util.Random;


/**
 *  Gauss implements gaussian distributed pseudorandom numbers. <br>
 *  This disrtibution bases on the uniform distributed 
 *  pseudorandom stream numbers of the class random.
 *
 *
 *
 */



public class Gauss {

  private double sigma;
  private double mean;
  private double eps;
  private Random rand = null;

  public Gauss(double mean, double sigma){
    this.rand = new Random();
    this.eps = java.lang.Double.MIN_VALUE; // smallest positive nonzero value of type double
    this.mean = mean;
    this.sigma = sigma;
  }

  public void initGauss(double mean, double sigma){
    this.mean = mean;
    this.sigma = sigma;
  }

  public double nextGauss(){
    double z, x,y, radius;

    radius = 1.0;
    do{
      x = 2.0 * rand.nextDouble() - 1.0;
      y = 2.0 * rand.nextDouble() - 1.0;
      radius = x*x + y*y;
    }while((radius >= 1.0)|| (radius < eps));
    z = mean + sigma*x*java.lang.Math.sqrt(-2.0*java.lang.Math.log(radius) / radius);
    return z;
  }

}










