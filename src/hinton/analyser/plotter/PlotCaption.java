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

import hinton.analyser.ModifiedNeuronOutputList;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cholsey.Neuron;


/**
 * @author rosemann
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PlotCaption extends JComponent
{
	private ColorChooseButton ccb;
	private JLabel            lblDescr;
	private JCheckBox         chkOutput;
	private JTextField        txtOutput;
	private Object            plotObject;
	private PlotColor         npc;
	
	private static HashMap    modifyOutputCheckbox;
	
	public PlotCaption(String descr, Object plotObj)
	{
		super();
		GridBagConstraints gbc;
		
		if (PlotCaption.modifyOutputCheckbox == null)
		{
			PlotCaption.modifyOutputCheckbox = new HashMap();
		}
		
		super.setBorder(LineBorder.createBlackLineBorder());
		super.setLayout(new GridBagLayout());
		
		this.npc        = PlotColor.getInstance();
		this.plotObject = plotObj;
		
		gbc = new GridBagConstraints();
		gbc.gridx      = 0;
		gbc.gridy      = 0;
		gbc.weightx    = 0.0d;
		gbc.gridheight = 1;
		gbc.anchor     = GridBagConstraints.WEST;
		gbc.insets     = new Insets(1, 1, 1, 1);
		this.ccb = new ColorChooseButton(plotObject);

		this.ccb.setPreferredSize(new Dimension(20, 20));
		this.ccb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				Color newColor = JColorChooser.showDialog(ccb,
																									"Set Plotcolor",
																									npc.getColor(plotObject));
				if (newColor != null)
				{
					npc.setColor(plotObject, newColor);
				} 
			}
		});
		this.add(this.ccb, gbc);
		
		this.lblDescr = new JLabel(descr);
		gbc = new GridBagConstraints();
		gbc.gridx      = 1;
		gbc.gridy      = 0;
		gbc.weightx    = 1.0d;
		gbc.gridwidth  = 2;
		gbc.anchor     = GridBagConstraints.WEST;
		gbc.fill       = GridBagConstraints.HORIZONTAL;
		gbc.insets     = new Insets(1, 1, 1, 1);
		this.add(this.lblDescr, gbc);
		
		if (plotObj instanceof Neuron)
		{
			this.chkOutput = new JCheckBox("Set Output");
			ArrayList chkboxes = (ArrayList)PlotCaption.modifyOutputCheckbox.get(
																						 plotObj);
			if (chkboxes == null)
			{
				chkboxes = new ArrayList();
				PlotCaption.modifyOutputCheckbox.put(plotObj, chkboxes);
			}
			chkboxes.add(new WeakReference(this.chkOutput, new ReferenceQueue()));
			
			this.chkOutput.setSelected(false);
			gbc = new GridBagConstraints();
			gbc.gridx      = 0;
			gbc.gridy      = 1;
			gbc.weightx    = 1.0d;
			gbc.weighty    = 0.0d;
			gbc.gridwidth  = 2;
			gbc.anchor     = GridBagConstraints.WEST;
			gbc.fill       = GridBagConstraints.NONE;
			gbc.insets     = new Insets(1, 1, 1, 1);
			this.chkOutput.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e)
				{
					PlotCaption.setOutputSelected(chkOutput.isSelected(), 
							((Neuron)plotObject).id());
					if (chkOutput.isSelected())
					{
						txtOutput.setText("x");
						txtOutput.setEnabled(true);
					}
					else
					{
						ModifiedNeuronOutputList.getInstance().removeNeuronOutput(
								((Neuron)plotObject).id());
						txtOutput.setEnabled(false);
					}
				}
			});
			this.add(this.chkOutput, gbc);
		
			this.txtOutput = new JTextField("x");
			this.txtOutput.setAlignmentX(JTextField.RIGHT_ALIGNMENT);
			this.txtOutput.setEnabled(false);
			this.txtOutput.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					ModifiedNeuronOutputList.getInstance().setNeuronOutput(
							((Neuron)plotObject).id(),
									txtOutput.getText());
				}
			});
			
			this.txtOutput.addFocusListener(new FocusListener() {
	
				public void focusGained(FocusEvent e) {
					txtOutput.setText(
									ModifiedNeuronOutputList.getInstance().
									getNeuronOutput(((Neuron)plotObject).id()));
				}
	
				public void focusLost(FocusEvent e) {
					ModifiedNeuronOutputList.getInstance().setNeuronOutput(
									((Neuron)plotObject).id(),
									txtOutput.getText());
					
				}
			});
			gbc = new GridBagConstraints();
			gbc.gridx      = 2;
			gbc.gridy      = 1;
			gbc.weightx    = 0.0d;
			gbc.weighty    = 0.5d;
			gbc.gridheight = 1;
			gbc.anchor     = GridBagConstraints.WEST;
			gbc.fill       = GridBagConstraints.HORIZONTAL;
			gbc.insets     = new Insets(1, 1, 1, 1);
			this.add(this.txtOutput, gbc);
		}
	}
	
	private static void setOutputSelected(boolean selected, int neuron)
	{
		Integer       key      = new Integer(neuron);
		JCheckBox     chkBox;
		WeakReference wr;
		ArrayList     chkBoxes = (ArrayList)PlotCaption.
									    									modifyOutputCheckbox.get(key);
		ArrayList     remove   = new ArrayList();
		if (chkBoxes != null)
		{
			for (int i = 0; i < chkBoxes.size(); i++)
			{
				wr = (WeakReference)chkBoxes.get(i);
				if (!wr.isEnqueued())
				{
					chkBox = (JCheckBox)wr.get();
					chkBox.setSelected(selected);
				}
				else
				{
						remove.add(wr);
				}
			}
			for (int i = 0; i < remove.size(); i++)
			{
				chkBoxes.remove(remove.get(i));
			}
		}
		
		
	}
	
	public static void newNet()
	{
		if (PlotCaption.modifyOutputCheckbox != null)
		{
			PlotCaption.modifyOutputCheckbox.clear();
		}
	}
	
	private class ColorChooseButton extends JButton
	{
		public PlotColor npc;
		public Object    plotObject;
		
		public ColorChooseButton(Object plotObj)
		{
			super();
			this.plotObject = plotObj;
			this.npc = PlotColor.getInstance();
		}
		
		public void paint(Graphics g)
		{
			int left = (int)(super.getWidth()/2.0d) - 5;
			int top  = (int)(super.getHeight()/2.0d) - 5;
			
			Graphics2D g2d = (Graphics2D)g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
													 RenderingHints.VALUE_ANTIALIAS_ON);
			super.paint(g);
			g2d.setColor(this.npc.getColor(this.plotObject));
			g2d.fillOval(left,
								   top,
								   10,
								   10);
		}
	}
}
