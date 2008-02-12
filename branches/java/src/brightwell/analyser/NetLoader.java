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

package brightwell.analyser;


import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import org.apache.log4j.Logger;

import util.misc.GenericClassLoader;
import util.misc.IseeLogger;

public class NetLoader
{
  private static Logger log = IseeLogger.getLogger(NetLoader.class);

  public Vector getNamedNets()
  {
    Vector nets = new Vector();
    ArrayList objects = null;

    GenericClassLoader gcl = new GenericClassLoader(
        "brightwell" + File.separator + "nets",
        "brightwell" + File.separator + "nets");

    objects = gcl.getObjects();

    log.info("number of loaded nets: " + objects.size());
    try
    {
      for(int i=0; i < objects.size(); i++)
      {
        Class c = (Class)(objects.get(i));
        NamedNet n = (NamedNet)c.newInstance();
        nets.add(n);
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
      return null;
    }


    Collections.sort(nets, 
        new Comparator()
        {
        public int compare(Object o1, Object o2)
        {
        return
        ((NamedNet)o1).getNetName().compareTo(((NamedNet)o2).getNetName());
        }
        });
    

    return nets;

  }
}
