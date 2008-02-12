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
 * Created on 08.04.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package hinton.analyser.netstructure;

import hinton.analyser.toolkit.InputComponent;
import hinton.analyser.toolkit.InputValue;
import hinton.analyser.toolkit.InputValueListener;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import util.misc.DynamicClassLoader;




/**
 * @author rosemann
 *
 * NeuronRendererFactory is a Factory class for creating
 * NeuronRenderer.
 * 
 * The NeuronRenderers are searched in the subpackage <b>neuronrenderer</b>
 * and dynamically bound to hinton
 */
public class NeuronRendererFactory
{
	private static final NeuronRenderer DEFAULT_RENDERER = 
			new NeuronRendererFactory.NeuronCircleRenderer();
	private static  NeuronRendererFactory instance = null;
	private HashMap neuronRenderer;
 
	private NeuronRendererFactory()
	{
		DynamicClassLoader dcl;
		String packageName = this.getClass().getPackage().getName();
		NeuronRenderer renderer;
		String jarName     = this.getJarName();
	
		this.neuronRenderer = new HashMap();
		this.neuronRenderer.put(
				NeuronRendererFactory.DEFAULT_RENDERER.getRendererName(),
				NeuronRendererFactory.DEFAULT_RENDERER.getClass());
		try
		{
			File neuronrendererFolder = new File(packageName.replace('.',
																												File.separatorChar) + 
																	 File.separatorChar + "neuronrenderer");
			File[] currentRenderer;
			String currentFileName;
			//ArrayList dummy = new ArrayList();
			Class  current = null;
			currentRenderer = neuronrendererFolder.listFiles();
			
			/*
			 * Search FileSystem Folder for NeuronRenderer
			 */
			if (currentRenderer != null)
			{
				for (int i = 0; i < currentRenderer.length; i++)
				{
					currentFileName = currentRenderer[i].getName();
					if (currentFileName.endsWith(".class"))
					{
			
						currentFileName = currentFileName.substring(0,
																						  currentFileName.lastIndexOf("."));
						try
						{
				
							current = Class.forName(packageName + 
																			".neuronrenderer."
																			+ currentFileName);
						}
						catch (NoClassDefFoundError ncdfe)
						{
				
						
								dcl = new DynamicClassLoader(
																	neuronrendererFolder.getAbsolutePath(),
																	null, false);
												
								current = dcl.loadClass("." + currentFileName);
						}
						this.addNeuronRenderer(current);
					}
				}
			}
			/*
			 * Search Jar-File, if exists, for Layoutalgorithms
			 */
			if (jarName != null)
			{
				JarFile jf = new JarFile(jarName);
				Enumeration jes = jf.entries();
				
				while (jes.hasMoreElements())
				{
					JarEntry je = (JarEntry)jes.nextElement();
					
					if (je.getName().startsWith(packageName.replace('.', '/') +
							"/neuronrenderer") &&
							je.getName().endsWith(".class"))
					{
						currentFileName = 
										je.getName().substring(je.getName().lastIndexOf('/')+1);
						currentFileName = currentFileName.substring(0, 
								currentFileName.lastIndexOf('.'));
						try
						{
							current = Class.forName(packageName +
																			".neuronrenderer." +
																			currentFileName);
						}
						catch (Exception ncdfe)
						{
							dcl = new DynamicClassLoader(
															 neuronrendererFolder.getPath(),
															 jarName, true);
							current = dcl.loadClass("." + currentFileName);
						}
						this.addNeuronRenderer(current);
					}
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	
		
	}

	/**
	 * This method returns the singleton instance of NeuronRendererFactory
	 * @return instance of NeuronRendererFactory
	 */	
	public static NeuronRendererFactory getInstance()
	{
		if (NeuronRendererFactory.instance == null)
		{
			NeuronRendererFactory.instance = new NeuronRendererFactory();
		}
		return NeuronRendererFactory.instance;
	}
	
	/**
	 * Returns the names of the available NeuronRenderers
	 * @return available NeuronRenderer
	 */
	public String[] getAvailableRendererNames()
	{
		return (String[])this.neuronRenderer.keySet().toArray(new String[0]);
	}
	
	public String getDefaultRendererName()
	{
		return NeuronRendererFactory.DEFAULT_RENDERER.getRendererName();
	}
	
	/**
	 * This method creates a new NeuronRenderer of the given name
	 * with the given initial data
	 * 
	 * @param rendererName name of the NeuronRenderer to be created
	 * @param rendererContainer the related VisualNet
	 * @param initialData the data the newly created NeuronRenderer is
	 * 										initialized with
	 * @return new NeuronRenderer of the type specified by the given name
	 * @throws UnsupportedDataTypeException
	 */
	public NeuronRenderer createRendererByName(String rendererName,
	                                        VisualNet rendererContainer,
	                                        Object    initialData)
	                       throws UnsupportedDataTypeException
	{
		NeuronRenderer ret = null;
		Constructor     c;
		Class           renderer = (Class)this.neuronRenderer.get(rendererName);
		
		if (renderer != null)
		{
			try
			{
				c = renderer.getConstructor(new Class[] { VisualNet.class, 
					                                        Object.class });
				ret = (NeuronRenderer)
							c.newInstance(new Object[] { rendererContainer, initialData });
				
			}
			catch (NoSuchMethodException e)
			{
				// can't occur since SynapseRenderer declares a Constructor of that
				// signature
			} catch (IllegalArgumentException e) {
				// can't occur 
			} catch (InstantiationException e) {
				// classes contained in this.synapseRenderer are not abstract
			} catch (IllegalAccessException e) {
				// Constructor is public
			} catch (InvocationTargetException e) {
				// Target Constructor can throw an UnsupportedDataTypeException
				throw new UnsupportedDataTypeException(e.toString());
			}					                                       
			
		}
		return ret;
	}
	
	/*
	 * Add only those classes to the list of available renderer, that
	 * are subclasses of NeuronRenderer and are not declared abstract 
	 */
	private void addNeuronRenderer(Class nrClass)
	{
		NeuronRenderer nr;
		if (nrClass != null &&
			  nrClass.getSuperclass().equals(NeuronRenderer.class) &&
			  ! Modifier.isAbstract(nrClass.getModifiers()))
		{
			try
			{
				nr = (NeuronRenderer)nrClass.newInstance();
				String name = (String)nrClass.getDeclaredMethod("getRendererName",
																new Class[] {}).invoke(nr,
																										   new Object[0]);
				this.neuronRenderer.put(name, nrClass);
			}
			catch (Exception e)
			{
				// do nothing, if the Method <getRendererName> isn't implemented 
				//             simply skip that Renderer
				System.err.println("Skipping Renderer " + nrClass.getName() + 
													 ", getRendererName() isn't implemented.");
			}

		}
	}
	
	/*
	 * Get the JarFileName that contains the class current or null
	 * if none
	 */
	private String getJarName()
	{
		String name         = null;
		/* Separator character in a Jar-File is '/' system independent */
		String myName       = this.getClass().getName().replace('.', '/');
		String resourceName = this.getClass().getResource(
																			"/" + myName + ".class").toString();
		int    begin, end;
		
		if (resourceName.startsWith("jar:"))
		{
			end   = resourceName.indexOf('!');
			begin = resourceName.substring(0, end).lastIndexOf('/');
			name = resourceName.substring(begin + 1, end);
		}
		else
		{
			return null;
		}
		return name;
	}
	
	/**
	 * @author rosemann
	 *
	 * This class implements a representation of neurons as a circle
	 * 
	 * THIS IS THE DEFAULT REPRESENTATION
	 */
	public static class NeuronCircleRenderer extends    NeuronRenderer
																	  implements InputValueListener
	{
		private static final String    DIAMETER_NAME  = "Neuron Diameter";
		private static final Dimension MINIMUM_SIZE   = new Dimension(17, 17);
		private static final Dimension PREFERRED_SIZE = new Dimension(32, 32);
		private static final Dimension MAXIMUM_SIZE   = new Dimension(62, 62);
		private static final float     BORDER_WIDTH   = 2.0f;

		private Point[]             junctures;
		private InputComponent.Collection renderCollection = null;
		private InputValue.IInteger diameter    = new InputValue.IInteger(
																					    DIAMETER_NAME, 30,
																					    15, 60);
		
		public NeuronCircleRenderer() {}
		
		public NeuronCircleRenderer(VisualNet rendererContainer,
															  Object    initialData)
		{
			super(rendererContainer, initialData);
			this.diameter.addInputValueListener(this);
			this.setSize(this.diameter.value+2, this.diameter.value+2);
		}
		
		/*
		 *  (non-Javadoc)
		 * @see Hinton.analyser.netstructure.NeuronRenderer#getNeuronShape()
		 */
		public Shape getNeuronShape()
		{
			return new Ellipse2D.Double(1, 1, this.getWidth() - 2, this.getWidth() - 2);
		}
		
		public Dimension getMinimumSize()
		{
			return NeuronCircleRenderer.MINIMUM_SIZE;
		}

		public Dimension getMaximumSize()
		{
			return NeuronCircleRenderer.MAXIMUM_SIZE;
		}

		public Dimension getPreferredSize()
		{
			return NeuronCircleRenderer.PREFERRED_SIZE;
		}

		/*
		 *  (non-Javadoc)
		 * @see Hinton.analyser.netstructure.NeuronRenderer#getJunctures()
		 */
		public Point[] getJunctures()
		{
			if (this.junctures == null)
			{
				computeJunctures();
			}
			return this.junctures;
		}
		
		/*
		 *  (non-Javadoc)
		 * @see Hinton.analyser.netstructure.NeuronRenderer#computeJunctures()
		 */
		protected void computeJunctures()
		{
			int     locationX = this.getLocation().x;
			int     locationY = this.getLocation().y;
			int     offsetY   = (int)(this.getSize().height/2.0d);
			int     offsetX   = 1;
			int     cmx       = (int)(this.getSize().width/2.0d);
			int     cmy       = offsetY;
			int     xy;
			int     diameter  = (this.getWidth() - 2);
			

			this.junctures = new Point[8];
			
			this.junctures[0] = new Point(offsetX + locationX,
												 					  offsetY + locationY);
			this.junctures[1] = new Point(this.getSize().width - 1 + locationX,
												 						this.junctures[0].y);
			this.junctures[2] = new Point(1 + (int)(diameter / 2.0d) 
																							+ locationX,
			                              1 + locationY);
			this.junctures[3] = new Point(this.junctures[2].x,
			                              this.getSize().height - 1 + locationY);
			                              
			xy = (int)((1.0d / Math.sqrt(2.0d)) * (diameter / 2.0d));
			
			this.junctures[4] = new Point(cmx + xy + locationX,
			                              cmy + xy + locationY);
			this.junctures[5] = new Point(this.junctures[4].x,
			                              cmy - xy + locationY);
			this.junctures[6] = new Point(cmx - xy + locationX,
			                              this.junctures[5].y);
			this.junctures[7] = new Point(this.junctures[6].x,
			                              this.junctures[4].y);
			         
		}
		
		/* (non-Javadoc)
		 * @see Hinton.analyser.netstructure.ComponentRenderer#getRendererName()
		 */
		public String getRendererName() {
			return "Neuron Circle Renderer";
		}

		/* (non-Javadoc)
		 * @see Hinton.analyser.netstructure.ComponentRenderer#getRenderingProperties()
		 */
		public InputComponent getConcreteRenderingProperties()
		{
			if (renderCollection == null)
			{
				renderCollection = new InputComponent.Collection("Neuron Circle Renderer");
				renderCollection.addElement(this.diameter);
			}	
			return renderCollection;
		}
		
		/*
		 *  (non-Javadoc)
		 * @see Hinton.analyser.netstructure.ComponentRenderer#getPersistentOptions()
		 */
		public PersistentRendererOption[] getPersistentOptions()
		{
			return new PersistentRendererOption[] { 
					new PersistentRendererOption(DIAMETER_NAME, "" + this.diameter.value)
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
				if (pro[i].getKey().compareTo(DIAMETER_NAME) == 0)
				{
					try
					{
						this.diameter.value = Integer.parseInt(pro[i].getValue());
						this.inputValueUpdated(this.diameter);
					}
					catch (NumberFormatException nfe)
					{
						//Nothing, simply keep the default diameter
					}
				}
			}
		}

		/* (non-Javadoc)
		 * @see Hinton.analyser.netstructure.toolkit.InputValueListener#inputValueUpdated(Hinton.analyser.netstructure.toolkit.InputValue)
		 */
		public void inputValueUpdated(InputValue iv) {
			if (iv == this.diameter)
			{
				this.setSize(this.diameter.value + 2, this.diameter.value + 2);
			}
		}
	}

}
