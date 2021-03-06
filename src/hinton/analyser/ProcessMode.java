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
 * Created on 20.04.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package hinton.analyser;

/**
 * @author rosemann
 *
 * This class encapsulates the ProcessModes for the Analyser
 */
public class ProcessMode 
{
	/** Object representing the continous processing */
	public static final ProcessMode CONTINUOUS = new ProcessMode();
	/** Object representing the one step processing */
	public static final ProcessMode ONESTEP    = new ProcessMode();
	/** Object representing the paused mode */
	public static final ProcessMode PAUSED     = new ProcessMode();
	
	private ProcessMode()
	{
	}
}
