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
 * Created on 11.03.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package hinton.analyser.netstructure.synapserenderer;

import hinton.analyser.netstructure.PersistentRendererOption;
import hinton.analyser.netstructure.SynapseRenderer;
import hinton.analyser.netstructure.VisualNet;
import hinton.analyser.toolkit.InputComponent;
import hinton.analyser.toolkit.InputValue;
import hinton.analyser.toolkit.InputValueListener;

import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * @author rosemann
 * 
 * This class implements the visual representation of a synapserenderer
 * as a traverse of straight lines
 */
public class SynapseLineRenderer extends    SynapseRenderer
																 implements InputValueListener  
{
	private Point   							dragStart;
	private int     							insertInLineSegment;
	private boolean 							isExistingPoint;
	private int                   draggedPointIndex = Integer.MAX_VALUE;
	private InputValue.IBoolean[] toggleSamplingPoints;
	private GeneralPath           shape;
	
	public SynapseLineRenderer() {}
	
	public SynapseLineRenderer(VisualNet rendererContainer,
													   Object    initialData)
	{
		super(rendererContainer, initialData);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see Hinton.analyser.netstructure.ComponentRenderer#getRendererName()
	 */
	public String getRendererName()
	{
		return "Straight Line Renderer";
	}
	
	
	/*
	 *  (non-Javadoc)
	 * @see Hinton.analyser.netstructure.SynapseRenderer#getSynapseShape()
	 */
	public Shape getSynapseShape()
	{
		return this.shape;
	}


	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.SynapseRenderer#locationUpdated()
	 */
	public void locationUpdated()
	{
		double vx, vy, vnx, vny, lengthV;
		Point lowerMid, upperMid, current;
		int    idx;
		int    minLeft = Integer.MAX_VALUE;
		int    minTop  = Integer.MAX_VALUE;
		int    maxLeft = Integer.MIN_VALUE;
		int    maxTop  = Integer.MIN_VALUE;
		
		try
		{
			/* set up the location and size so all samplingpoints are
			 * inside the component bounds
			 */	
			for (int i = 1; i < this.samplingPoints.length - 1; i++)
			{
				current = this.samplingPoints[i];
				if (minLeft > current.x)
				{
					minLeft = current.x;
				}
				if (minTop > current.y)
				{
					minTop = current.y;
				}
			}
			minLeft = (minLeft < 0 ? minLeft : 0);
			minTop  = (minTop  < 0 ? minTop  : 0);
			if (minLeft < 0 || minTop < 0)
			{
				for (int i = 0; i < this.samplingPoints.length; i++)
				{
					this.samplingPoints[i].translate(-minLeft, -minTop);	
				}
				this.setLocation(this.getX() + minLeft, this.getY() + minTop);
			}
			for (int i = 0; i < this.samplingPoints.length; i++)
			{
				current = this.samplingPoints[i];
				if (maxLeft < current.x)
				{
					maxLeft = current.x;
				}
				if (maxTop < current.y)
				{
					maxTop = current.y;
				}					
			}
			maxLeft += 20;
			maxTop  += 20;
			maxLeft = (maxLeft > this.getWidth()  ? maxLeft : this.getWidth());
			maxTop  = (maxTop  > this.getHeight() ? maxTop  : this.getHeight());
			if (maxLeft > this.getWidth() || maxTop > this.getHeight())
			{
				this.setSize(maxLeft, maxTop);
			}
			
			this.shape = new GeneralPath();
			
			for (int i = 0; i < this.samplingPoints.length - 1; i++)
			{
				this.shape.append(new Line2D.Float(this.samplingPoints[i],
													this.samplingPoints[i+1]), false);
			}
				
		}
		catch (Exception e)
		{
			//Nothing
		}
	
	}

	
	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.ComponentRenderer#getRenderingProperties()
	 */
	public InputComponent getConcreteRenderingProperties() {
		InputComponent.Collection myCollection;
		
		if (this.samplingPoints.length > 2)
		{
			myCollection = new InputComponent.Collection("Straight Line Renderer");
			
			this.toggleSamplingPoints = 
					 new InputValue.IBoolean[this.samplingPoints.length - 2];
		
			for (int i = 0; i < this.toggleSamplingPoints.length; i++)
			{
				this.toggleSamplingPoints[i] = new InputValue.IBoolean(
																			 "Remove Samplingpoint " + (i + 1),
																			 false);
			  this.toggleSamplingPoints[i].addInputValueListener(this);
				myCollection.addElement(this.toggleSamplingPoints[i]);																	 
			}
		}
		else
		{
			myCollection = null;
		}
		
		return myCollection;
	}
	
	/**
	 * Moves a new/existing sampling point 
	 */
	public void mouseDragged(MouseEvent e) {
		if (!this.isExistingPoint)
		{
			
			ArrayList sP = new ArrayList(Arrays.asList(this.samplingPoints));
			
			sP.add(this.insertInLineSegment, this.dragStart);
			
			
			this.isExistingPoint   = true;
			this.draggedPointIndex = this.insertInLineSegment;
			this.samplingPoints    = (Point[])sP.toArray(new Point[0]);
		}
		
		this.samplingPoints[this.draggedPointIndex].x = e.getX();
		this.samplingPoints[this.draggedPointIndex].y = e.getY();
		// to force redraw even though the Bounds haven't changed
		this.computeLocationUpdate(this.rendererContainer.getNeuronRenderer(
																				this.sourceNeuronID).getBounds(),
															 this.sourceNeuronID);
		this.repaint();
	}

	/**
	 * Determine if a new sampling point has to be added or an existing is to
	 * be moved
	 */
	public void mousePressed(MouseEvent e)
	{
		Line2D.Double current;
		double        currentDist;
		double        minimumDist = Double.MAX_VALUE;

		this.isExistingPoint = false;
		this.dragStart       = e.getPoint();
		
		for (int i = 0; i < this.samplingPoints.length; i++)
		{
			if (this.samplingPoints[i].distance((Point2D)this.dragStart) < 10)
			{
				this.isExistingPoint   = true;
				this.draggedPointIndex = i;
				break;
			}
		}
		
		if (!this.isExistingPoint)
		{
			for (int i = 0; i < this.samplingPoints.length - 1; i++)
			{
				current = new Line2D.Double(this.samplingPoints[i],
																		this.samplingPoints[i+1]);
				currentDist = current.ptSegDist((Point2D)this.dragStart);
				if (currentDist < minimumDist)
				{
					minimumDist = currentDist;
					this.insertInLineSegment = i + 1;
				}	
			}
		}
		
	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e)
	{
		this.draggedPointIndex = Integer.MAX_VALUE;
	}
	
	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.toolkit.InputValueListener#inputValueUpdated(Hinton.analyser.netstructure.toolkit.InputValue)
	 */
	public void inputValueUpdated(InputValue iv)
	{
		InputValue.IBoolean doRemove  = (InputValue.IBoolean)iv;
		int                 removeIdx = -1;
		
		if (doRemove.value)
		{
			for (int i = 0; removeIdx == -1 &&
										  i < this.toggleSamplingPoints.length;
					 i++)
			{
				if (doRemove.equals(this.toggleSamplingPoints[i]))
				{
					removeIdx = i+1;
				}
			}
			ArrayList sP  = new ArrayList(Arrays.asList(this.samplingPoints));
			sP.remove(removeIdx);
			ArrayList tSP = new ArrayList(Arrays.asList(this.toggleSamplingPoints));
			tSP.remove(removeIdx-1);
			
			this.samplingPoints = (Point[])sP.toArray(new Point[0]);
			this.toggleSamplingPoints = (InputValue.IBoolean[])tSP.toArray(
													new InputValue.IBoolean[0]);
													
			// to force redraw even though the Bounds haven't changed
			this.computeLocationUpdate(this.rendererContainer.getNeuronRenderer(
																						 this.sourceNeuronID).getBounds(),
																	  this.sourceNeuronID);
			this.repaint();
			
		}
		
	}

	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.ComponentRenderer#getPersistentOptions()
	 */
	public PersistentRendererOption[] getPersistentOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.ComponentRenderer#setPersistentOptions(Hinton.analyser.netstructure.PersistentRendererOption[])
	 */
	public void setPersistentOptions(PersistentRendererOption[] pro) {
		// TODO Auto-generated method stub
		
	}




}
