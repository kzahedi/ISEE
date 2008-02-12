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

package brightwell.gui.colorscales;

import java.awt.Color;

public class ColorScale 
{

  private double _minValue = 0.0;
  private double _maxValue = 0.0;

  public void ColorScale(int minValue, int maxValue)
  {
    ColorScale((double)minValue, (double)maxValue);
  }

  public void ColorScale(double minValue, double maxValue)
  {
    _minValue = minValue;
    _maxValue = maxValue;
  }

  public Color getColor(int value)
  {
    return getColor((double) value);
  }

  public Color getColor(double value)
  {
    float r = 0;
    float g = 0;
    float b = 0;
    return null;
  }

}

