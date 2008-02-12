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

package hinton.broker;

import hinton.executive.ProcessParameter;
import hinton.gui.ProcessParameterDialog;
import cholsey.Net;

public interface EvoComInterface
{
  public boolean newGeneration();
  public void clearNewGenerationFlag();
  public void setProcessParameter(ProcessParameter processParameter);
  public void setProcessParameterDialog(ProcessParameterDialog
      processParameterDialog);
  public ProcessParameter getProcessParameter();
  public boolean isRunning();
  public Net getNet();
  public void setFitnessValue(double fitnessValue);
  public void connect(String ip, int port);
  public void communicate();
  public boolean autoRun(); /* only true if evoprog supports autorun */
  public String getName();

  public EvoComInterface[] LIST =
  {
    new CenCom(),
    new EvoSunCom()
  };
}
