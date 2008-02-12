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

package util.io;

import java.io.File;
import java.util.Vector;

import javax.swing.filechooser.FileFilter;


/**
 * Expandable File Filter
 * 
 */
public class ExFileFilter extends FileFilter {

  //public final static String xml = "xml";
  public Vector extensions  = new Vector();
  public String explanation = new String("Evolution-File (.xml)");


  public ExFileFilter()
  {
    extensions.add("xml"); // default file filter
  }

  public String getExtension(int index)
  {
    return (String)extensions.elementAt(index);
  }

  public ExFileFilter(String defaultExtension, String explanation)
  {
    extensions.add(defaultExtension); // default file filter
    this.explanation = explanation;
  }

  public void addExtension(String extension)
  {
    extensions.add(extension);
  }

  public void clearAllExtensions()
  {
    extensions.removeAllElements();
  }


  public void setExplanation(String explanation)
  {
    this.explanation = explanation;
  }

  // Accept all directories and all xml files.
  public boolean accept(File f) {
    if (f.isDirectory()) {
      if(f.getName().equals("CVS"))
      {
        return false;
      }
      else
      {
        return true;
      }
    }

    String extension = this.getExtension(f);
    if (extension != null) {
      for(int i=0; i < extensions.size();i++)
      {
        if (extension.equals((String)extensions.elementAt(i)) ) {
          return true;
        }
      }
    }
    return false;
  }

  // The description of this filter
  public String getDescription() {
    return explanation;
  }

  public static String getExtension(File f) {
    String ext = null;
    String s = f.getName();
    int i = s.lastIndexOf('.');

    if (i > 0 &&  i < s.length() - 1) {
      ext = s.substring(i+1).toLowerCase();
    }
    return ext;
  }

}
