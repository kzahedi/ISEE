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
 * Created on 01.04.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package hinton.analyser.toolkit;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

/**
 * @author rosemann
 */
public abstract class InputComponent
{
	protected String              name;

	/**
	 * Returns the name (caption) of the InputComponent
	 * @return the name of the InputComponent
	 */
	public final String getName()
	{
		return name;
	}
	
	/**
	 * Sets the name (caption) of the InputComponent to name
	 * @param name the name of the InputComponent
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * This method has to be implemented when subclassing InputComponent.
	 * 
	 * This method returns a JComponent that represents the InputComponent
	 * 
	 * @return the JComponent representing the InputComponent
	 */
	public abstract JComponent getInputComponent();
	
	/**
	 * This method has to be implemented when subclassing InputComponent.
	 * 
	 * Updates the value of this InputComponent 
	 */
	public abstract void performUpdate();
	
	
	/**
	 * 
	 * @author rosemann
	 *
	 * This class provides a Collection of InputComponents which Elements
	 * are grouped by a border
	 */
	public static class Collection extends InputComponent
	{
		private InputComponent[] elements;
		private JPanel					 inputComp;
		
		/**
		 * Create a new InputCollection with the given name
		 * @param name the name of the InputCollection. It is displayed as 
		 * 						 the caption of the elementgrouping TitledBorder
		 */
		public Collection(String name)
		{
			this.name     = name;
			this.elements = new InputComponent[0];
			inputComp     = new JPanel(new GridBagLayout());
			inputComp.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED),
																			     this.name));
		}
		
		/* (non-Javadoc)
		 * @see Hinton.analyser.netstructure.toolkit.InputComponent#getInputComponent()
		 */
		public JComponent getInputComponent() {
			GridBagConstraints gbc = new GridBagConstraints();
			
			inputComp.removeAll();
			for (int i = 0; i < this.elements.length; i++)
			{
				gbc.gridx   = 0;
				gbc.gridy   = i;
				gbc.fill    = GridBagConstraints.HORIZONTAL;
				gbc.weightx = 1;
				this.inputComp.add(this.elements[i].getInputComponent(), gbc);
			}
			return inputComp;
		}

		/* (non-Javadoc)
		 * @see Hinton.analyser.netstructure.toolkit.InputComponent#performUpdate()
		 */
		public void performUpdate()
		{
			for (int i = 0; i < this.elements.length; i++)
			{
				this.elements[i].performUpdate();
			}
		}		
		
		/**
		 * This method adds the InputComponent element to this Collection
		 * @param element
		 */
		public void addElement(InputComponent element)
		{
			ArrayList tempElements = new ArrayList(Arrays.asList(this.elements));
			tempElements.add(element);
			this.elements = (InputComponent[])tempElements.toArray(
																										 new InputComponent[0]);
		}
	}
	
	public static void main(String[] args)
	{
		String[] choices = { "1", "2", "3" };
		Object   choiceRef;
		String[] choicesNames = { "Auswahl 1", "Auswahl 2", "Auswahl 3" };
		InputComponent.Collection ic = new InputComponent.Collection("Test Collection 1");
		InputComponent.Collection ic2 = new InputComponent.Collection("Test Collection 2");
		javax.swing.JFrame testframe = new javax.swing.JFrame("InputValuesTest!");
		InputValue.IDouble  dtest  = new InputValue.IDouble("Test Double", 3.14d, Math.PI/2.0d, Math.PI);
		InputValue.IInteger itest  = new InputValue.IInteger("Test Integer", -4086, -5000, -1);
		InputValue.IBoolean btest  = new InputValue.IBoolean("Test Boolean", false);
		InputValue.IString  stest  = new InputValue.IString("Test String", "Test String");
		InputValue.IObject  otest  = new InputValue.IObject("Test Object", 0, choices, choicesNames);
		
		ic2.addElement(dtest);
		ic2.addElement(itest);
		ic2.addElement(btest);
		ic.addElement(stest);
		ic.addElement(ic2);
		ic.addElement(otest);
		
		testframe.getContentPane().add(ic.getInputComponent());
		testframe.pack();
		testframe.setVisible(true);
		
	}
}
