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


package util.misc;

import java.util.ArrayList;


public class Node
{

  private String name = "";

  private ArrayList incommingEdges = new ArrayList();
  private ArrayList outgoingEdges  = new ArrayList();
  private double value = 0d;


  public Node()
  {

  }

  public void setValue(double value)
  {
    this.value = value;
  }

  public double getValue()
  {
    return value;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return name;
  }


  public void addIncomingEdge(Edge e)
  {
    incommingEdges.add(e);
  }

  public void addOutgoingEdge(Edge e)
  {
    outgoingEdges.add(e);
  }

  public ArrayList getIncomingEdges()
  {
    return incommingEdges;
  }

  public ArrayList getOutgoingEdges()
  {
    return outgoingEdges;
  }

  public String toString()
  {
    String s = new String();
    s = s.concat(name + "\n");
    s = s.concat("Incomming Edges:\n");
    for(int index=0; index < incommingEdges.size(); index++)
    {
      Edge e = (Edge)incommingEdges.get(index);

      s = s.concat("  " + e + "\n");
    }
    s = s.concat("Outgoing Edges:\n");
    for(int index=0; index < outgoingEdges.size(); index++)
    {
      Edge e = (Edge)outgoingEdges.get(index);

      s = s.concat("  " + e + "\n");
    }
    return s;
  }
}
