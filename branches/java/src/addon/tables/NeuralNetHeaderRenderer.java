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


package addon.tables;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 * Class:       NeuralNetHeaderRenderer
 * Description: This renderer knows how to show the tableheaders of an
 *              NeuralNetTable.
 *
 * @see Addon.Tables.NeuralNetTable
 */
public class NeuralNetHeaderRenderer extends DefaultTableCellRenderer
{

  Component comp;
  TableCellRenderer r;
  JLabel l;
  Color c;
  
  public NeuralNetHeaderRenderer(Color c, TableCellRenderer renderer)
  {
    if (renderer == null)
    {
      this.r = (new JTableHeader()).getDefaultRenderer();
    } else
    {
      this.r = renderer;
    }
    this.c = c;
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
          boolean isSelected, boolean hasFocus, int row, int column)
  {
    comp = this.r.getTableCellRendererComponent(table, value, isSelected,
            hasFocus, row, column);
    try
    {
      l = (JLabel) comp;
      l.setText(String.valueOf(value));
      l.setOpaque(true);
      l.setBackground(c);
      return l;
    } catch (Exception ex)
    {
      return comp;
    }    
  }
}
