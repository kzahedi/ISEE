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


package reading;


public class Entry
{
  public final static int SIZE = 9;
  private int netIndex         = -1;
  private int age              = -1;
  private int generationIndex  = -1;
  private int populationIndex  = -1;
  private int numberOfNeurons  = -1;
  private int numberOfSynapses = -1;
  private double sysPerf       = -1;
  private double outPerf       = -1;
  private int pinId            = -1;

  public Entry(
      int generationIndex,
      int netIndex,
      int populationIndex,
      int age,
      double sysPerf,
      double outPerf,
      int pinId,
      int numberOfNeurons,
      int numberOfSynapses)
  {
    this.generationIndex = generationIndex;
    this.netIndex = netIndex;
    this.populationIndex = populationIndex;
    this.age = age;
    this.sysPerf = sysPerf;
    this.outPerf = outPerf;
    this.pinId = pinId;
    this.numberOfNeurons = numberOfNeurons;
    this.numberOfSynapses = numberOfSynapses;
  }

  public int getNumberOfNeurons()
  {
    return numberOfNeurons;
  }

  public void setNumberOfNeurons(int numberOfNeurons)
  {
    this.numberOfNeurons = numberOfNeurons;
  }


  public int getNumberOfSynapses()
  {
    return numberOfSynapses;
  }

  public void setNumberOfSynapses(int numberOfSynapses)
  {
    this.numberOfSynapses = numberOfSynapses;
  }


  public int getGenerationIndex()
  {
    return generationIndex;
  }

  public void setGenerationIndex(int generationIndex)
  {
    this.generationIndex = generationIndex;
  }


  public int getNetIndex()
  {
    return netIndex;
  }

  public void setNetIndex(int netIndex)
  {
    this.netIndex = netIndex;
  }



  public int getAge()
  {
    return age;
  }

  public void setAge(int age)
  {
    this.age = age;
  }



  public double getSysPerf()
  {
    return sysPerf;
  }

  public void setSysPerf(double sysPerf)
  {
    this.sysPerf = sysPerf;
  }



  public double getOutPerf()
  {
    return outPerf;
  }

  public void setOutPerf(double outPerf)
  {
    this.outPerf = outPerf;
  }

  public void setPopulationIndex(int populationIndex)
  {
    this.populationIndex = populationIndex;
  }

  public int getPopulationIndex()
  {
    return populationIndex;
  }

  public int getPinId()
  {
    return this.pinId;
  }

}
