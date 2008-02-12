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

package learningrules;

import cholsey.LearningRuleInterface;


import java.util.Vector;

import org.apache.log4j.Logger;

import util.misc.IseeLogger;

public class Empty implements LearningRuleInterface
{

  protected static Logger log = IseeLogger.getLogger(Empty.class);

  private final static double fixPoint = Math.sqrt(1d/3d);
  private final static double epsilon     = 0.0001;

  public String getName()
  {
    return "__Emtpy";
  }


  public void calculateLearningParameter(Vector netParameter)
  {
    double output           = ((Double)netParameter.elementAt(0)).doubleValue();
    double bias             = ((Double)netParameter.elementAt(1)).doubleValue();
    double transmitterLevel = ((Double)netParameter.elementAt(2)).doubleValue();
    double receptorLevel    = ((Double)netParameter.elementAt(3)).doubleValue();
    double alpha            = ((Double)netParameter.elementAt(4)).doubleValue();
    double beta             = ((Double)netParameter.elementAt(5)).doubleValue();
    double gamma            = ((Double)netParameter.elementAt(6)).doubleValue();
    double delta            = ((Double)netParameter.elementAt(7)).doubleValue();
    double my               = ((Double)netParameter.elementAt(8)).doubleValue();
    double ny               = ((Double)netParameter.elementAt(9)).doubleValue();

    // add you learning rule here

    log.info("no learning rule selected");
    receptorLevel     = 0;
    transmitterLevel  = 0;


    // wrting back values 
    netParameter.setElementAt(new Double(output),0);
    netParameter.setElementAt(new Double(bias),1);
    netParameter.setElementAt(new Double(transmitterLevel),2);
    netParameter.setElementAt(new Double(receptorLevel),3);
    netParameter.setElementAt(new Double(alpha),4);
    netParameter.setElementAt(new Double(beta),5);
    netParameter.setElementAt(new Double(gamma),6);
    netParameter.setElementAt(new Double(delta),7);
    netParameter.setElementAt(new Double(my),8);
    netParameter.setElementAt(new Double(ny),9);

  }


}

