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


package util.misc;

/**
 *  A collection of function often needed by the net.
 *  @see      java.lang.Math
 */
public final class Functions
{

  /**
   * Standard sigmoid function. <br> sigmoid(x) = 1/(1+exp(-x))
   */
  public static double sigmoid(double x)
  {
    return (1/(1+Math.exp(-x)));
  }

  /**
   * Hyperbolic Tangens. <br> (exp(x) - exp(-x)) / (exp(x) - exp(-x))
   */
  public static double tanh(double x)
  {
    return (Math.exp(x)-Math.exp(-x))/(Math.exp(x)+Math.exp(-x));
  }

  /**
   * Hyperbolic Tangens Derivative. <br> tanh'(x)= 1 - tan^2(x) 
   */ 
  public static double tanh1(double x)
  {
    return 1 - (Math.tan(x) * Math.tanh(x));
  }


  /**
   * First derivative of the standard sigmoid. <br>
   * sigmoid(x) * ( 1 - sigmoid(x))
   */
  public static double sigmoid1(double x)
  {
    return sigmoid(x) * (1-sigmoid(x));
  }

  /**
   * Second derivative of the standard sigmoid. <br>
   * sigmoid1(x) - 2 * sigmoid(x) * sigmoid1(x)
   */
  public static double sigmoid2(double x)
  {
    return sigmoid1(x) - 2 * sigmoid(x) * sigmoid1(x);
  }

  /**
   * Third derivative of the standard sigmoid. <br>
   * sigmoid2(x) - 2* (sigmoid1(x) * sigmoid1(x) + sigmoid(x) * sigmoid2(x) 
   */
  public static double sigmoid3(double x)
  {
    return sigmoid2(x) - 2* 
      (sigmoid1(x) * sigmoid1(x) 
       + sigmoid(x) *sigmoid2(x));
  }

  /**
   * Signum funcion.
   * @return   -1 if x < 0, 1 else
   */
  public static int sign(double x)
  {
    if (x < 0 ) 
    {
      return -1;
    }
    return 1;
  }

}

