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

public class ToolLoader
{
  private static Logger log = IseeLogger.getLogger(ToolLoader.class);

  private static ToolConfigLoader toolConfigLoader = new ToolConfigLoader();

  public Vector loadToolClasses()
  {
    Vector tools = new Vector();
    ArrayList objects = null;

    GenericClassLoader gcl = new GenericClassLoader(
        "brightwell" + File.separator + "tools",
        "brightwell" + File.separator + "tools");

    objects = gcl.getObjects();

    log.info("number of loaded tools: " + objects.size());

    try
    {
      for(int i=0; i < objects.size(); i++)
      {
        Class c = (Class)(objects.get(i));
        Tool t = (Tool)c.newInstance();
        log.info("added: " + t.getClass());
        t.init();
        t.setToolPriority(toolConfigLoader.getToolPriority(t.getClass().getName()));
        tools.add(t);
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
      return null;
    }

    Collections.sort(tools, 
        new Comparator()
        {
        public int compare(Object o1, Object o2)
        {
        return ((Tool)o1).getToolPriority() - ((Tool)o2).getToolPriority();
        }
        });
    

    return tools;

  }
}
