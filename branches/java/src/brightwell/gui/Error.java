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

package brightwell.gui;

import java.awt.Component;

import javax.swing.JOptionPane;


public class Error
{
  public static void netLoad(Component parent)
  {
      JOptionPane.showMessageDialog(parent,
          "<html>Net load failed. Please check <br>" +
          "Generation / Population / Net Index <br>" +
          "or the content of the file.",
          "Net Load Error",
          JOptionPane.ERROR_MESSAGE);


  }
  public static void noConfigFile(String filename, Component parent)
  {
      JOptionPane.showMessageDialog(parent,
          "<html> No Config File<br> " + filename + 
          "<br> found. Using default values.",
          "Config File Error",
          JOptionPane.ERROR_MESSAGE);
  }

  public static void noNetLoaded(Component parent)
  {
      JOptionPane.showMessageDialog(parent,
          "Please Load/Select or Create A Net","NET ERROR",
          JOptionPane.ERROR_MESSAGE);
  }

  public static void parseNumberError(Component parent)
  {
      JOptionPane.showMessageDialog(parent,
          "<html>Input fileds do not contain numbers. <br>" +
          "Please check all field." ,"NET ERROR",
          JOptionPane.ERROR_MESSAGE);
  }

  public static void netLoadError(Component parent)
  {
      JOptionPane.showMessageDialog(parent,
          "<html>Please select a net to be loaded. <br>" +
          "Net load failed." ,"NET ERROR",
          JOptionPane.ERROR_MESSAGE);
  }

  public static void feigenBaumInputError(Component parent)
  {
      JOptionPane.showMessageDialog(parent,
          "<html>Please choose either bias or weight <br>" +
          "for the feigenbaum diagram." ,"NET ERROR",
          JOptionPane.ERROR_MESSAGE);
  }

  public static void attractorParseError(Component parent)
  {
      JOptionPane.showMessageDialog(parent,
          "<html>An Error occured during the parsing of<br>" +
          "the bias/weight input string." ,"NET ERROR",
          JOptionPane.ERROR_MESSAGE);
  }

  public static void unknownKey(Component parent, String key)
  {
      JOptionPane.showMessageDialog(parent,
          "<html>Unknow key \"" + key +"\" used in parameter request<br>" +
          "Please check the init-function." ,"NET ERROR",
          JOptionPane.ERROR_MESSAGE);
  }

  public static void enterPeekNeuron(Component parent)
  {
      JOptionPane.showMessageDialog(parent,
          "<html>Please enter a neuron index.<br>Either I&lt;n>, O&lt;n>, " 
          +" H&lt;n> for any <br>input,"
          + " output or hidden neuron,<br> "
          + " or <index> for the n-th neuron in the"
          + " net",
          "NET ERROR",
          JOptionPane.ERROR_MESSAGE);
  }

  public static void parseErrorGenPopInd(Component parent)
  {
      JOptionPane.showMessageDialog(parent,
          "<html>Please check indices in NetLoad Panel. <br>"
          + "one of the input fields generation / <br>"
          + "population or individual is not an integer",
          "Parse Error",
          JOptionPane.ERROR_MESSAGE);
  }

  public static void parseErrorMisc(Component parent, String message)
  {
      JOptionPane.showMessageDialog(parent,
          "<html>" + message + "<br>", "Error",
          JOptionPane.ERROR_MESSAGE);
  }

}
