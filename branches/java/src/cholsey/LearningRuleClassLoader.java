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

package cholsey;


import java.util.ArrayList;
import java.util.Vector;

import org.apache.log4j.Logger;

import util.misc.GenericClassLoader;
import util.misc.IseeLogger;

public class LearningRuleClassLoader
{
  private static Logger log = IseeLogger.getLogger(LearningRuleClassLoader.class);

  private static int selectedIndex = -1;

  private static Vector learningRules = new Vector();

  public LearningRuleClassLoader()
  {
    loadLearningRuleClasses();
  }

  public Vector getClasses()
  {
    return learningRules;
  }

  public Vector loadLearningRuleClasses()
  {
    learningRules.removeAllElements();
    ArrayList objects = null;

    GenericClassLoader gcl = new
      GenericClassLoader("learningrules","learningrules");

    objects = gcl.getObjects();

    log.info("number of loaded learning rules: " + objects.size());

    try
    {
      for(int i=0; i < objects.size(); i++)
      {
        Class c = (Class)(objects.get(i));
        LearningRuleInterface t = (LearningRuleInterface)c.newInstance();
        learningRules.add(t);
        log.debug("add new learning rule: " + t.getName());
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
      return null;
    }

    return learningRules;

  }

  public LearningRuleInterface getSelectedLearningRule()
  {
    if(selectedIndex == -1 || selectedIndex >= learningRules.size())
    {
      return null;
    }

    return (LearningRuleInterface)learningRules.elementAt(selectedIndex);
  }

  public int getSelectedIndex()
  {
    return selectedIndex;
  }

  public void setSelectedLearningRule(int index)
  {
    selectedIndex = index;
    log.debug("by Int: setting learning rule to: " + 
        ((LearningRuleInterface)learningRules.elementAt(selectedIndex)).getName());
  }


  public void setSelectedLearningRule(String learningRule)
  {
    for(int i=0; i < learningRules.size(); i++)
    {
      if(learningRule.trim().equals(
            ((LearningRuleInterface)learningRules.elementAt(i)).getName()))
      {
        selectedIndex = i;
      }
    }
    log.debug("by String: setting learning rule to: " + 
        ((LearningRuleInterface)learningRules.elementAt(selectedIndex)).getName());
    log.debug("by String: index : " + selectedIndex);
  }
}
