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
 * Created on 06.04.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package hinton.analyser.toolkit;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * @author rosemann
 *
 * This class provides a JDialog to display InputComponents.
 * It has a JButton <i>OK</i> which is connected to the method
 * performUpdate of its elements.
 */
public class InputDialog extends JDialog
											   implements ActionListener
{
	private JPanel           inputComponentPanel = new JPanel();
	private JButton          btnOK               = new JButton("OK");
	private JButton          btnCancel           = new JButton("Cancel");
	private InputComponent[] elements;
	
	/**
	 * Create a new InputDialog with the JComponent as the JDialog owner
	 * and the given Dialogtitle title
	 * @param owner the owner of the Dialog
	 * @param title the Dialogtitle
	 */
	public InputDialog(JComponent owner, String title)
	{
		super(JOptionPane.getFrameForComponent(owner), title, true);
		Insets             insets = new Insets(5, 5, 5, 5);
		GridBagConstraints gbc    = new GridBagConstraints();
		
		this.setResizable(false);
		this.setLocationRelativeTo(owner);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.getContentPane().setLayout(new GridBagLayout());
		this.elements = new InputComponent[0];
		
		this.inputComponentPanel.setLayout(new GridBagLayout());
		gbc.gridx     = 0;
		gbc.gridy     = 0;
		gbc.gridwidth = 3;
		gbc.fill      = GridBagConstraints.BOTH;
		gbc.anchor    = GridBagConstraints.NORTHWEST;
		gbc.weightx   = 1.0d;
		gbc.weighty   = 0.9d;
		gbc.insets    = insets;
		this.getContentPane().add(this.inputComponentPanel, gbc);
		
		gbc.gridx     = 1;
		gbc.gridy     = 1;
		gbc.gridwidth = 1;
		gbc.fill      = GridBagConstraints.NONE;
		gbc.anchor    = GridBagConstraints.EAST;
		gbc.weightx   = 1.0d;
		gbc.weighty   = 0.1d;
		gbc.insets    = insets;
		this.getContentPane().add(this.btnOK, gbc);
		
		gbc.gridx     = 2;
		gbc.gridy     = 1;
		gbc.gridwidth = 1;
		gbc.fill      = GridBagConstraints.NONE;
		gbc.anchor    = GridBagConstraints.EAST;
		gbc.weightx   = 0.0d;
		gbc.weighty   = 0.1d;
		gbc.insets    = insets;
		this.getContentPane().add(this.btnCancel, gbc);
		
		this.btnOK.addActionListener(this);
		this.btnCancel.addActionListener(this);
	}
	
	
	

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == this.btnOK)
		{
			for (int i = 0; i < this.elements.length; i++)
			{
				this.elements[i].performUpdate();
			}
		}	
		this.dispose();
	}
	
	
	/**
	 * Adds the InputComponent element to the InputDialog
	 * @param element the element to be added
	 */
	public void addElement(InputComponent element)
	{
		ArrayList tempElements = new ArrayList(Arrays.asList(this.elements));
		tempElements.add(element);
		this.elements = (InputComponent[])tempElements.toArray(
																									 new InputComponent[0]);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.Component#show()
	 */
	public void show() {
		GridBagConstraints gbc = new GridBagConstraints();
			
		inputComponentPanel.removeAll();
		for (int i = 0; i < this.elements.length; i++)
		{
			gbc.gridx   = 0;
			gbc.gridy   = i;
			gbc.fill    = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			this.inputComponentPanel.add(this.elements[i].getInputComponent(), gbc);
		}
		this.pack();
		super.show();
	}

}

