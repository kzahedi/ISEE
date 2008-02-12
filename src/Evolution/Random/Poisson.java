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
 *  Poisson implements poisson distributed pseudorandom numbers. <br>
 *  This disrtibution is derived from the uniform distributed 
 *  pseudorandom stream numbers of the class random.
 *
 *
 *
 */

public class Poisson {
    private Random rand = null;

    public Poisson(Random uni){
  this.rand = uni;
    }

    public int nextPoisson(double mean){
  double limit   = java.lang.Math.exp(-java.lang.Math.abs(mean));
  double product = 1.0;
  int    i       = -1;

  do{
      i++;
      product = product * rand.nextDouble();
  }while(product > limit);

  return i;
    }



    public static void main(String argv[]){
      Random uni = new Random();
      Poisson p = new Poisson(uni);
      double v;
      int s, n;
      
      for(v = 0; v < 2; v = v + 0.05)
      {
  System.out.println("p( " + v + ")= " + p.nextPoisson(v));
      }
      
    }

}









