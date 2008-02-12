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
 * Created on 05.04.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package hinton.analyser.netstructure;

import java.awt.Point;
import java.awt.Polygon;

/**
 * @author rosemann
 *
 * This class represents different appearance Synapseendstyles
 * 
 * Currently a FilledArrow and a Biologically style is implemented 
 */
public abstract class SynapseEndStyle
{
	/** FilledArrow lineend */
	public static SynapseEndStyle   FILLED_ARROW = new SynapseEndStyle.FilledArrow();
	/** Biologically lineend */
	public static SynapseEndStyle   BIOLOGICALLY = new SynapseEndStyle.Biologically();
	/** default lineend */
	public static SynapseEndStyle   DEFAULT      = SynapseEndStyle.FILLED_ARROW;
	/** array of styles */
	public static SynapseEndStyle[] STYLES       = { SynapseEndStyle.FILLED_ARROW,
		                                               SynapseEndStyle.BIOLOGICALLY };
	/**
	 * This method has to be implemented when subclassing SynapseEndStyle.
	 * 
	 * Returns the name of the SynapseEndStyle
	 * 
	 * @return SynapseEndStyle-Name
	 */
	public abstract String getName();
	
	/**
	 * This method has to be implemented when subclassing SynapseEndStyle.
	 * 
	 * Returns a Polygon representing the Synapse end for the given location,
	 * orientation and strength
	 * 
	 * @param vx x component of the orientation vector
	 * @param vy y component of the orientation vector
	 * @param location the location of the synapseend
	 * @param strength the strength of the synapse
	 * @return lineend for the given location, orientation and strength
	 */
	public abstract Polygon getLineEnd(double vx, double vy,
																		 Point location, double strength);
	
	public final String toString()
	{
		return getName();
	}
	
	/**
	 * @author rosemann
	 *
	 * Class representing the synapse end as a filled arrow
	 */
	private static class FilledArrow extends SynapseEndStyle
	{
		public String getName()
		{
			return "Filled Arrow";
		}

		/* (non-Javadoc)
		 * @see Hinton.analyser.netstructure.SynapseEndStyle#getLineEnd(int, int, java.awt.Point, double)
		 */
		public Polygon getLineEnd(double vx, double vy, Point target, double strength)
		{		
			double lengthV, anchX, anchY, vnx, vny;
			Polygon ret = new Polygon();	
	
			lengthV = Math.sqrt(vx*vx + vy*vy);
			vx      = vx / lengthV;
			vy      = vy / lengthV;

			/* Move Arrowlength steps from the line end to get 
			 * an anchor for the points to be drawn
			 */
			anchX  = target.x + 16 * vx;
			anchY  = target.y + 16 * vy;

			/*
			 * produce the left normal and add Point to the Polygon 
			 */
			vnx    = -vy;
			vny    = vx;
			ret.addPoint((int)(anchX + 8*vnx),
												  (int)(anchY + 8*vny));

			/*
			 * produce the right normal and add Point to the Polygon 
			 */	
			vnx    = vy;
			vny    = -vx;
			ret.addPoint((int)(anchX + 8*vnx),
												  (int)(anchY + 8*vny));
			/*
			 * add endpoint to the Polygon
			 */
			ret.addPoint(target.x,
			 						 target.y);
			 						 
			return ret;
		}
		

	}
	
	/**
	 * Class representing the biologically synapseend
	 */
	private static class Biologically extends SynapseEndStyle
	{
		public String getName()
		{
			return "Biologically";
		}

		/* (non-Javadoc)
		 * @see Hinton.analyser.netstructure.SynapseEndStyle#getLineEnd(int, int, java.awt.Point, double)
		 */
		public Polygon getLineEnd(double vx, double vy, Point target, double strength)
		{		
			double lengthV, anchX, anchY, vnx, vny;
			Polygon ret = new Polygon();	

			lengthV = Math.sqrt(vx*vx + vy*vy);
			vx      = vx / lengthV;
			vy      = vy / lengthV;

			
			if (strength > 0)
			{
				/*
				 * produce the left normal and add Point to the Polygon 
				 */
				vnx    = -vy;
				vny    =  vx;
				ret.addPoint((int)(target.x + 8*vnx),
										 (int)(target.y + 8*vny));
	
				/*
				 * produce the right normal and add Point to the Polygon 
				 */	
				vnx    =  vy;
				vny    = -vx;
				ret.addPoint((int)(target.x + 8*vnx),
										 (int)(target.y + 8*vny));
				
				/* Move Arrowlength steps from the line end to get 
				 * an anchor for the points to be drawn
				 */
				anchX  = target.x + 16 * vx;
				anchY  = target.y + 16 * vy;
				/*
				 * add endpoint to the Polygon
				 */
				ret.addPoint((int)anchX,
										 (int)anchY);
			}
			else				
			{
				ret.addPoint(target.x,
										 target.y);
				ret.addPoint((int)(target.x +  (8 - (8 / Math.sqrt(2))) * vx + (8 / Math.sqrt(2)) * vy),
				             (int)(target.y +  (8 - (8 / Math.sqrt(2))) * vy + (8 / Math.sqrt(2)) * -vx));
				ret.addPoint((int)(target.x +  8 * vx +   8 * vy),
				             (int)(target.y +  8 * vy +   8 * -vx));
				ret.addPoint((int)(target.x + (8 + (8 / Math.sqrt(2))) * vx + (8 / Math.sqrt(2)) * vy),
				             (int)(target.y + (8 + (8 / Math.sqrt(2))) * vy + (8 / Math.sqrt(2)) * -vx));
				ret.addPoint((int)(target.x + 16 * vx),
										 (int)(target.y + 16 * vy));
				ret.addPoint((int)(target.x + (8 + (8 / Math.sqrt(2))) * vx + (8 / Math.sqrt(2)) * -vy),
				             (int)(target.y + (8 + (8 / Math.sqrt(2))) * vy + (8 / Math.sqrt(2)) * vx));
				ret.addPoint((int)(target.x +  8 * vx +   8 * -vy),
										 (int)(target.y +  8 * vy +   8 * vx));
				ret.addPoint((int)(target.x +  (8 - (8 / Math.sqrt(2))) * vx + (8 / Math.sqrt(2)) * -vy),
										 (int)(target.y +  (8 - (8 / Math.sqrt(2))) * vy + (8 / Math.sqrt(2)) * vx));
				
			}
		 						 
			return ret;
		}
	

	}
}
