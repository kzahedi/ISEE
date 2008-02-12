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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

import cholsey.Net;

/**
 * Class:       NakedNeuralNetTable
 * Description: This Table is only for internal use, please use the class   
 *              <b>NeuralNetTable</b> from the same package.                        
 *
 * @see Addon.Tables.NeuralNetTable
 */
class NakedNeuralNetTable extends JTable
{
  /** define Colors for Neurons (Input, Output, Hidden, default) */
  public static final Color ICOLOR = Color.red;
  public static final Color OCOLOR = Color.green;
  public static final Color RCOLOR = Color.magenta;
  public static final Color HCOLOR = Color.blue;
  public static final Color XCOLOR = Color.lightGray;

  /** define Colors for processModes (dynamic, consistent, static) */
  public static final Color DCOLOR = Color.green;
  public static final Color CCOLOR = Color.blue;
  public static final Color SCOLOR = Color.red;
  
  protected NeuralNetTableModel model;
  protected Color               iColor;
  protected Color               oColor;
  protected Color               hColor;
  protected Color               rColor;
  protected Color               xColor;
  protected Color               dColor;
  protected Color               cColor;
  protected Color               sColor;

  /**
   * Creates a simple NeuralNetTable without any features.
   */
  public NakedNeuralNetTable()
  {
    super();
    model = new NeuralNetTableModel(this);
    this.setModel(model);
    this.setAutoResizeMode(AUTO_RESIZE_OFF);
    this.setRowHeight(25);
    this.iColor = ICOLOR;
    this.oColor = OCOLOR;
    this.rColor = RCOLOR;
    this.hColor = HCOLOR;
    this.xColor = XCOLOR;
    this.dColor = DCOLOR;
    this.cColor = CCOLOR;
    this.sColor = SCOLOR;
 }

  /**
   * Creates a simple NeuralNetTable without any features
   * an initializes it with a neural net.
   */
  public NakedNeuralNetTable(Net neuralNet)
  {
    this();
    setNeuralNet(neuralNet);
  }

  /**
   * Sets a specified neural net to the table.
   */
  public void setNeuralNet(Net neuralNet)
  {
    model.setNeuralNet(neuralNet);
  }
 
  /**
   * Creates and return a rowheader of the table.
   */
  public Component getRowHeader()
  {
    int width;
    int i;
    JPanel p, p2;
    JLabel l;
    FlowLayout fl;
    Color c;
    
    width = 0;
    p = new JPanel();
    fl = new FlowLayout();

    fl.setAlignment(FlowLayout.LEFT);
    fl.setHgap(0);
    fl.setVgap(0);
    p.setLayout(fl);

    /*for (i=0; i<this.getColumnCount(); i++)
    {
      width = Math.max(width, getColumn(getColumnName(i)).getWidth())+2;
    }*/
    width = getColumn(getColumnName(0)).getWidth()+2;
    for (i=0; i<this.getColumnCount(); i++)
    {
      if (model.isInputIndex(i))
      {
        c = iColor;
      } else
      if (model.isOutputIndex(i))
      {
        c = oColor;
      } else
      if (model.isReadIndex(i))
      {
        c = rColor;
      } else
      if (model.isHiddenIndex(i))
      {
        c = hColor;
      } else
      {
        c = xColor;
      }
      l = new JLabel(getColumnName(i));
      l.setFont(this.getTableHeader().getFont());
      l.setBorder(BorderFactory.createRaisedBevelBorder());
      l.setPreferredSize(new Dimension(width, this.getRowHeight()));
      l.setOpaque(true);
      l.setBackground(c);
      p.add(l); 
    }
    
    p.setPreferredSize(new Dimension(width, this.getRowHeight() *
          this.getRowCount())) ;

    p2 = new JPanel();
    p2.setLayout(new BorderLayout());
    p2.add(p, BorderLayout.NORTH);

    
    return p2;
  }

  public void cancelEditing()
  {
    int r,c;
    
    if (!isEditing())
    {
      return;
    }
    ((DefaultCellEditor)cellEditor).cancelCellEditing();
  }

  public void update()
  {
    model.update();
  }


  public void convertTheNetToDynamic()
  {
    model.convertTheNetToDynamic();
  }

  public void convertTheNetToStatic()
  {
    model.convertTheNetToStatic();
  }

}
