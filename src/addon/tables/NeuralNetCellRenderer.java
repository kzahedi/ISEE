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
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * Class:       NeuralNetCellRenderer
 * Description: This renderer knows how to show the cells of an
 *              NeuralNetTable.
 *
 * @see Addon.Tables.NeuralNetTable
 */
public class NeuralNetCellRenderer extends DefaultTableCellRenderer
{

  Component comp;
  TableCellRenderer r;
  JLabel l;
  Color c, cdynamic, cconsistent, cstatic;
  int i;
  String text;
  
  public NeuralNetCellRenderer(Color cdynamic, Color cconsistent,
        Color cstatic, TableCellRenderer renderer)
  {
    if (renderer == null)
    {
      this.r = new DefaultTableCellRenderer();
    } else
    {
      this.r = renderer;
    }
    this.cdynamic = cdynamic;
    this.cconsistent = cconsistent;
    this.cstatic = cstatic;
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
          boolean isSelected, boolean hasFocus, int row, int column)
  {
    comp = this.r.getTableCellRendererComponent(table, value, isSelected,
            hasFocus, row, column);
    try
    {
      l    = (JLabel) comp;
      text = "";
        
        if (value.equals("dynamic"))
        {
          i = 0;
        } 
        else if (value.equals("consistent"))
        {
          i = 1; 
        } 
        else if (value.equals("static"))
        {
          i = 2; 
        } 
        else if (value.equals(NeuralNetTable.TEXT_DYN_SYNAPSES))
        {
          i = 2; 
        } 
        else if (value.equals(NeuralNetTable.TEXT_NA) || value.equals(""))
        {
          i = -1; 
        } 
        else
        {          
          i = Integer.valueOf(String.valueOf(table.getValueAt
              (-row-1, -column-1))).intValue();
          text = "X";
        }
        switch (i)
        {
          case 0:  if (!text.equals("")) text = " (d)";
                     c = cdynamic;
                   break;
          case 1:  if (!text.equals("")) text = " (c)";
                     c = cconsistent; 
                   break;
          case 2:  if (!text.equals("")) text = " (s)"; 
                     c = cstatic; 
                   break;
          default: if (hasFocus)
                   {
                     c = Color.lightGray;
                   } 
                   else 
                   {
                     c = Color.white;                    
                   }
                   text = "";
        }
        l.setBorder(new LineBorder(c));          
        l.setText(String.valueOf(value)+text);
      return l;
    } 
    catch (Exception ex)
    {
      System.err.println("Rendering error: "+value);
      return comp;
    }    
  }

}
