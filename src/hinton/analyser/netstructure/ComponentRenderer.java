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
 * Created on 09.03.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package hinton.analyser.netstructure;

import hinton.analyser.toolkit.InputComponent;
import hinton.analyser.toolkit.InputValue;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JComponent;


/**
 * ComponentRenderer is the abstract class representing the visual
 * representation of the components (Neurons, Synapses) of a
 * Neural Network
 * 
 * @author rosemann
 */
public abstract class ComponentRenderer extends    JComponent
																			  implements MouseListener,
																			             MouseMotionListener
{
	protected VisualNet   rendererContainer;
	protected Properties  renderingProperties = new Properties();
	private   boolean     isSelected          = false;
	private   ArrayList   mouseEventObservers = new ArrayList();
	private   GlyphVector descr;
	private   Point       descrPosition       = new Point(0,0);
	
	private   static Font                descrFont = new Font(null,
																														Font.PLAIN,
	                                                          12);
	protected static InputValue.IBoolean showDescr = new InputValue.IBoolean(
																												"Show Description",
																												true);      
	
	/**
	 * Default Constructor is used by the Renderer Factories only 
	 */
	public ComponentRenderer()
	{
		super();
	}
	
  /**
	 * Standard Constructor which must be called by classes
	 * inherited from this class
	 */
	public ComponentRenderer(VisualNet rendererContainer,
													 Object    initialData)
	{
		super();
		super.setOpaque(false);
		super.addMouseListener(this);
		super.addMouseMotionListener(this);
	
		this.rendererContainer   = rendererContainer;
		this.renderingProperties = new Properties();
		this.mouseEventObservers = new ArrayList();
		
		this.initialize(initialData);
	}
	
	
	/**
	 * returns the Name of the Renderer
	 * @return renderer name
	 */
	public abstract String getRendererName();

	/**
	 * {@link javax.swing.JComponent}
   */	
	public abstract void paintComponent(Graphics g);
	
	/**
	 * Initialize the Renderer with the given initialData
	 * @param initialData
	 */
	public abstract void initialize(Object initialData);

	/**
   * returns the rendering properties depending on the specific type
   * of the renderer for this ComponentRenderer (Neuron-/SynapseRenderer)
   * 
   * @return Rendering Properties or NULL if none
	 */
	public abstract InputComponent getRendererTypeProperties();	

	/**
   * updates the data of the Object which is visual represented by
   * this ComponentRenderer
   */
	public abstract void updateData(Object data)
											 throws UnsupportedDataTypeException;
	
	/**
	 * returns the optional settings of the renderer, that have to be saved
	 * if the layout of the whole net is saved
	 */
	public abstract PersistentRendererOption[] getPersistentOptions();
	
	/**
	 * this method is called when a renderer is loaded from disk, to set
	 * the options correctly
	 * @param pro
	 */
	public abstract void setPersistentOptions(PersistentRendererOption[] pro);
	
	/**
	 * Set the selected state to the given value
	 * @param selected
	 */
	public final void setSelected(boolean selected)
	{
		this.isSelected = selected;
	}
	
	/**
	 * @return true if selected/false else
	 */
	protected boolean isSelected()
	{
		return this.isSelected;
	}
	
	/**
	 * returns the rendering properties for this ComponentRenderer
	 * 
	 * @return Rendering Properties or NULL if none
	 */
	public final InputComponent getRenderingProperties()
	{
		InputComponent.Collection
		               renderingProperties = new InputComponent.Collection("");
		InputComponent typeProperties      = getRendererTypeProperties();
		
		renderingProperties.addElement(ComponentRenderer.showDescr);
		if (typeProperties != null)
		{
			renderingProperties.addElement(typeProperties);
		}
		return renderingProperties;
	}
											
	public final void paint(Graphics g)
	{
		super.paint(g);
		if (ComponentRenderer.showDescr.value)
		{
			this.drawDescription(g);
		}
	}
	
	private final void drawDescription(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		if (this.descr != null)
		{
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
			                     RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2d.setColor(Color.BLACK);
			g2d.drawGlyphVector(this.descr,
													this.descrPosition.x,
													this.descrPosition.y);
		}
		
	}
	
	/**
	 * calculates the Position and orientation of the renderer description
	 * @param vx x component of the orientation vector
	 * @param vy y component of the orientation vector
	 * @param location the location where the description is to be shown
	 * @param text description text
	 */
	public final void computeDescription(double vx, double vy,
																			 Point location, String text)
	{
		try
		{
			double lengthV, vnx, vny;
			lengthV = Math.sqrt(vx*vx + vy*vy);
			vnx     = -vx / lengthV;
			vny     = -vy / lengthV;
				
			AffineTransform rot = AffineTransform.getRotateInstance(
																						Math.atan2(vy, vx));
			this.descr = descrFont.createGlyphVector(new FontRenderContext(
																							 rot, true, false),
																							 text);
			
			for (int i = 0; i < this.descr.getNumGlyphs(); i++)
			{
				this.descr.setGlyphTransform(i, rot);
			}
			
		  this.descrPosition.x = location.x;
		  this.descrPosition.y = location.y;	
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}	
	}
	
}
