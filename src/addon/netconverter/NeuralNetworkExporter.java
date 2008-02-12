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


package addon.netconverter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import cholsey.Net;

/**
 * Class:       NetConverter 
 * Description: :-)
 */

public abstract class NeuralNetworkExporter
{
  private Net         net;
  private PrintWriter out;
  private File        outFile;
  private String      filename = null;

  public boolean init(Net net, String filename)
  {
    this.net = net;
    try
    {
      outFile = new File(filename);
      out     = new PrintWriter(new FileOutputStream(outFile));
      this.filename = filename;
    } 
    catch (Exception ex)
    {
      ex.printStackTrace();      
      return false;
    }
    return true;
  }

  public boolean deinit()
  {
    try
    {
      out.flush();
      out.close();
    } 
    catch (Exception ex)
    {
      ex.printStackTrace();      
      return false;
    }
    return true;
  }

  private void createCode(Net net, String filename)
  {
    init(net, filename);
    export();
    deinit();
  }

  public abstract void export();
  public abstract String getName();
  public abstract String getExtension();

}
