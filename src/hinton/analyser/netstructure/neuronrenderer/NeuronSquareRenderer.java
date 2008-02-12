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
 * Created on 07.06.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package hinton.analyser.netstructure.neuronrenderer;

import hinton.analyser.netstructure.NeuronRenderer;
import hinton.analyser.netstructure.PersistentRendererOption;
import hinton.analyser.netstructure.VisualNet;
import hinton.analyser.toolkit.InputComponent;
import hinton.analyser.toolkit.InputValue;
import hinton.analyser.toolkit.InputValueListener;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;


/**
 * @author rosemann
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class NeuronSquareRenderer extends NeuronRenderer
																  implements InputValueListener
{
	private static final String EDGELENGTH_NAME = "Edge length";
	
	private Point[]             junctures;
	private Rectangle2D         shape;
	private InputValue.IInteger edgeLength;
	
	/**
	 * 
	 */
	public NeuronSquareRenderer() {
		super();
	}
	
	/*
	 * 
	 */
	public NeuronSquareRenderer(VisualNet rendererContainer, Object initialData) {
		super(rendererContainer, initialData);
		this.edgeLength = new InputValue.IInteger(EDGELENGTH_NAME, 30, 15, 60);
		this.edgeLength.addInputValueListener(this);
		this.setSize(this.edgeLength.value, this.edgeLength.value);
	}
	
	
	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.NeuronRenderer#getNeuronShape()
	 */
	public Shape getNeuronShape() {
		return this.shape;
	}
	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.NeuronRenderer#getConcreteRenderingProperties()
	 */
	public InputComponent getConcreteRenderingProperties() {
		return this.edgeLength;
	}
	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.NeuronRenderer#getJunctures()
	 */
	public Point[] getJunctures() {
		return this.junctures;
	}
	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.NeuronRenderer#computeJunctures()
	 */
	protected void computeJunctures() {
		int width  = this.getWidth();
		int height = this.getHeight();
		int left   = this.getX();
		int top    = this.getY();
		
		this.junctures = new Point[8];
		
		
		this.junctures[0] = new Point(left, top);
		this.junctures[1] = new Point(left + width / 2, top);
		this.junctures[2] = new Point(left + width, top);
		this.junctures[3] = new Point(left + width, top + height / 2);
		this.junctures[4] = new Point(left + width, top + height);
		this.junctures[5] = new Point(left + width / 2, top + height);
		this.junctures[6] = new Point(left, top + height);
		this.junctures[7] = new Point(left, top + height / 2);
		
		this.computeShape();
	}
	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.ComponentRenderer#getRendererName()
	 */
	public String getRendererName() {
		return "Neuron Square Renderer";
	}


	/* (non-Javadoc)
	 * @see Hinton.analyser.toolkit.InputValueListener#inputValueUpdated(Hinton.analyser.toolkit.InputValue)
	 */
	public void inputValueUpdated(InputValue iv)
	{
		this.setSize(this.edgeLength.value, this.edgeLength.value);
		this.computeJunctures();
	}
	
	private void computeShape()
	{
		if (this.shape == null)
		{
			this.shape = new Rectangle2D.Float();
		}
		this.shape.setRect(1, 1, this.getWidth() - 2, this.getHeight() - 2);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see Hinton.analyser.netstructure.ComponentRenderer#getPersistentOptions()
	 */
	public PersistentRendererOption[] getPersistentOptions()
	{
		return new PersistentRendererOption[] { 
				new PersistentRendererOption(EDGELENGTH_NAME, 
																		"" + this.edgeLength.value)
				};
	}
	
	/*
	 *  (non-Javadoc)
	 * @see Hinton.analyser.netstructure.ComponentRenderer#setPersistentOptions(Hinton.analyser.netstructure.PersistentRendererOption[])
	 */
	public void setPersistentOptions(PersistentRendererOption[] pro)
	{
		for (int i = 0; i < pro.length; i++)
		{
			if (pro[i].getKey().compareTo(EDGELENGTH_NAME) == 0)
			{
				try
				{
					this.edgeLength.value = Integer.parseInt(pro[i].getValue());
					this.inputValueUpdated(this.edgeLength);
				}
				catch (NumberFormatException nfe)
				{
					//Nothing, simply keep the default diameter
				}
			}
		}
	}


}
