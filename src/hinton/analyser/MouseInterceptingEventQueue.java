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
 * Created on 28.04.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package hinton.analyser;

import hinton.analyser.netstructure.VisualNet;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;




/**
 * @author rosemann
 *
 * This class is a utility class for Analyser to intercept
 * MouseEvents for the net structure frame
 */
public class MouseInterceptingEventQueue extends EventQueue
{

	public MouseInterceptingEventQueue()
	{
		super();
		Toolkit.getDefaultToolkit().getSystemEventQueue().push(this);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.awt.EventQueue#dispatchEvent(java.awt.AWTEvent)
	 */
	public void dispatchEvent(AWTEvent e)
	{
		Component  c;
		Component  ce;
		MouseEvent me;
		if (e instanceof MouseEvent)
		{
			me = (MouseEvent)e;
			if (me.getID() == MouseEvent.MOUSE_CLICKED && 
					me.getSource() instanceof Container)
			{
				c  = me.getComponent();
				ce = SwingUtilities.getDeepestComponentAt(c, me.getX(), me.getY());
				if (ce instanceof VisualNet.ToolTipDelegationPanel)
				{
					Analyser.getInstance().intercept(me);
					return;
				}
			}
		}
		try
		{
			super.dispatchEvent(e);
		}
		catch (Exception exc)
		{
			
		}
	}
	
	/**
	 * This method is used to post a previously intercepted event
	 * @param e the previously intercepted event
	 */
	public void postIntercepted(AWTEvent e)
	{
		super.dispatchEvent(e);
	}
}
