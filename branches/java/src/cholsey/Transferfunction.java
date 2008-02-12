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
 
package cholsey;

import util.misc.Functions;

/**
 *  Implementation of Transferfunctions-enum. Transferfunctions are used by the
 *  neurons to calculate their output according the the activation. Two
 *  functions are included by now, the tanh, and standard sigmoid:
 *  <ul>
 *  <li> tanh(x) = (e(x)-e(-x))/(e(x)+e(-x)) </li>
 *  <li> sigm(x) = 1/(1+e(-x)) </li>
 *  </ul>
 *  For use see Cholsey.NeuronType
 *  @see cholsey.NeuronType
 *  @see cholsey.Neuron
 *  @see util.misc.Functions
 */
public final class Transferfunction
{
  /** Tanh will be used. */
  public final static int USE_TANH = 0;
  /** Standard sigmoid will be used. */
  public final static int USE_SIGM = 1;
  private int mode;
  private String name;
  private Transferfunction(String nm, int m) {name=nm; mode=m;};
  /**
   * Returns a human readable representation of the Transferfunction.
   * @param    none
   * @return   String string, human readable representation
   */

  public String toString() {return name;};
  /**
   * Returns a XML-valid readable representation of the Transferfunction.
   * @param    none
   * @return   String xml, XML-valid representation
   */
  public String toXML()
  {
    switch(mode)
    {
      case USE_SIGM:
        return "sigm";
      case USE_TANH:
        return "tanh";
    }
    return null;
  }
  /**
   * Returns the function used. Return value is one of the above
   * constants.
   * @param    none
   * @return   int mode
   * @see #USE_TANH
   * @see #USE_SIGM
   */
  public int mode() {return mode;};
  /** Implementation of the tanh transferfunction */
  public final static Transferfunction TANH = 
    new Transferfunction("Hyperbolic Tangent",USE_TANH);
  /** Implementation of the standard sigmoid transferfunction */
  public final static Transferfunction SIGM = 
    new Transferfunction("Standard Sigmoid",USE_SIGM);
  /** the list of all possible transferfunctions */
  public final static Transferfunction[] LIST =
  {
    TANH,
    SIGM
  };
  /**
   * Calculates accoding to the assigned function.
   * @param    x double x, value
   * @return   double f(x), value according to the assigned function
   */
  public double calculate(double x)
  {
    switch(mode)
    {
      case USE_TANH:
        return Functions.tanh(x); 
        //(Math.exp(x)-Math.exp(-x))/(Math.exp(x)+Math.exp(-x));
      case USE_SIGM:
        return Functions.sigmoid(x);
        //return (1/(1+Math.exp(-x)));
      default:
        System.out.println("Transferfunction: UNKOWN FUNCTION REQUEST\n");
        System.exit(-1);
        return -1;
    }
  }
  public static void main(String argv[])
  {
    Transferfunction tfh = Transferfunction.TANH;
    Transferfunction tfs = Transferfunction.SIGM;
    System.out.println("Created tfh = " + tfh.toString());
    System.out.println("tfh.calculate(0)   -> " + tfh.calculate(0));
    System.out.println("tfh.calculate(-10) -> " + tfh.calculate(-10));
    System.out.println("tfh.calculate(10)  -> " + tfh.calculate(10));
    System.out.println("Created tfs = " + tfs.toString());
    System.out.println("tfs.calculate(0)   -> " + tfs.calculate(0));
    System.out.println("tfs.calculate(-10) -> " + tfs.calculate(-10));
    System.out.println("tfs.calculate(10)  -> " + tfs.calculate(10));
    System.out.println("Test done.");
 }
}
