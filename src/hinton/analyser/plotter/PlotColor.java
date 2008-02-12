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

/*
 * Created on 26.04.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package hinton.analyser.plotter;

import hinton.analyser.Analyser;
import hinton.analyser.NetObserver;

import java.awt.Color;
import java.util.HashMap;

import cholsey.Net;


/**
 * @author rosemann
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PlotColor implements NetObserver
{
	private static       PlotColor instance;
	private static final Color     defaultColor = Color.BLACK;
	
	private HashMap colors;
	
	
	private PlotColor()
	{
		colors = new HashMap();
		Analyser.getInstance().registerNetObserver(this);
	}
	
	public static PlotColor getInstance()
	{
		if (PlotColor.instance == null)
		{
			PlotColor.instance = new PlotColor();
		}
		return PlotColor.instance;
	}
	
	public Color getColor(Object plotObject)
	{
		if (!this.colors.containsKey(plotObject))
		{
			this.colors.put(plotObject, PlotColor.defaultColor);
		}
		return (Color)this.colors.get(plotObject);
	}
	
	public void setColor(Object plotObject, Color color)
	{
		this.colors.put(plotObject, color);
	}

	/* (non-Javadoc)
	 * @see Hinton.analyser.NetObserver#setNet(Cholsey.Net)
	 */
	public void setNet(Net net) {
		this.colors = new HashMap();
		
	}

	/* (non-Javadoc)
	 * @see Hinton.analyser.NetObserver#netUpdate(Cholsey.Net)
	 */
	public void netUpdate(Net net) {
		// TODO Auto-generated method stub
		
	}
	
}
