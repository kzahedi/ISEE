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
 * Created on 23.04.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package hinton.analyser.plotter;

import hinton.analyser.Analyser;
import hinton.analyser.NetObserver;
import hinton.analyser.toolkit.InputDialog;
import hinton.analyser.toolkit.InputValue;
import hinton.analyser.toolkit.InputValueListener;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import cholsey.Net;
import cholsey.Neuron;
import cholsey.NeuronList;
import cholsey.Synapse;
import cholsey.SynapseList;
import cholsey.SynapseMode;


/**
 * @author rosemann
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class FirstReturnPlotter extends JInternalFrame
										 implements NetObserver
{
	private static File           saveDir;
	
	private JTextField            txtUpperLimit;
	private JTextField            txtLowerLimit;
	private PlotPanel             plotPanel;
	private JPanel                captionPanel;
	private JScrollPane           captionScroll;
	private JPanel                leftSide;
	private JSplitPane            toggleCaption;
	private Net                  	net;
	private HashMap               plotObjects;
	private JCheckBox             chkBoxAutoAdjust;
	private HashMap               plottableObjects;
	private DecimalFormat         df;
	
	
	private JMenuBar              plotterMenuBar;
	private JMenu                 mnuPlot;
	private JMenuItem             mniRefresh;
	private JMenuItem             mniProperties;
	private JMenu                 mnuPlotObjects;
	private JMenuItem             mniExport;
	
	
	public FirstReturnPlotter(String title, Net net)
	{
		super(title, true, true, true, true);
		GridBagConstraints gbc;
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		this.df = new DecimalFormat("0.00", dfs);
		
		this.plotObjects = new HashMap();
		this.net         = net;
		this.plottableObjects = new HashMap();
		
				
		this.plotterMenuBar = new JMenuBar();
		this.mnuPlot = new JMenu("Plot");
		this.mnuPlotObjects = createPlotObjectsMenu();


		this.mniProperties = new JMenuItem("Properties");
		this.mniProperties.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				setPlotProperties();
			}
		});
		this.mniExport = new JMenuItem("Save value table");
		this.mniExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				saveValueTable();
			}
		});
		
		this.mniRefresh = new JMenuItem("Refresh Plotmenu");
		this.mniRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				refreshPlotMenu();
			}
		});
		
		this.mnuPlot.add(this.mnuPlotObjects);
		this.mnuPlot.add(this.mniRefresh);
		this.mnuPlot.add(this.mniProperties);
		this.mnuPlot.add(this.mniExport);
		this.plotterMenuBar.add(this.mnuPlot);
		this.setJMenuBar(this.plotterMenuBar);
		
		
		this.getContentPane().setLayout(new GridLayout(1,1));
		this.leftSide = new JPanel();
		this.leftSide.setLayout(new GridBagLayout());
		
		
		this.txtUpperLimit = new JTextField("1.00");
		gbc = new GridBagConstraints();
		gbc.gridx      = 0;
		gbc.gridy      = 0;
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.anchor     = GridBagConstraints.NORTHEAST;
		gbc.fill       = GridBagConstraints.HORIZONTAL;
		gbc.insets     = new Insets(15, 0, 0, 5);
		this.leftSide.add(this.txtUpperLimit, gbc);
		this.txtUpperLimit.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e)
			{
				updateLimit(e);
			}
		});
		
		this.txtLowerLimit = new JTextField("-1.00");
		gbc = new GridBagConstraints();
		gbc.gridx      = 0;
		gbc.gridy      = 1;
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.anchor     = GridBagConstraints.SOUTHEAST;
		gbc.fill       = GridBagConstraints.HORIZONTAL;
		gbc.insets     = new Insets(0, 0, 15, 5);
		this.leftSide.add(this.txtLowerLimit, gbc);
		this.txtLowerLimit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				updateLimit(e);
			}
		});
		
						
		this.plotPanel = new PlotPanel(this.txtLowerLimit, this.txtUpperLimit);
		this.plotPanel.setVisible(true);
		this.plotPanel.setOpaque(true);
		this.plotPanel.setBackground(Color.WHITE);
		gbc = new GridBagConstraints();
		gbc.gridx      = 1;
		gbc.gridy      = 0;
		gbc.gridwidth  = 1;
		gbc.gridheight = 2;
		gbc.weightx    = 1.0d;
		gbc.weighty    = 1.0d;
		gbc.anchor     = GridBagConstraints.CENTER;
		gbc.fill       = GridBagConstraints.BOTH;
		gbc.insets     = new Insets(5, 5, 5, 5);
		this.leftSide.add(this.plotPanel, gbc);
		this.setName(this.getTitle());
		this.setTitle(this.getTitle() + " [" + this.plotPanel.shownValuesCount.value + " values]");
		
		this.chkBoxAutoAdjust = new JCheckBox("Autoadjust Y-Axis");
		this.chkBoxAutoAdjust.setSelected(false);
		this.chkBoxAutoAdjust.addChangeListener(new ChangeListener ()
		{
			public void stateChanged(ChangeEvent e)
			{
				boolean selected = chkBoxAutoAdjust.isSelected();
				
				plotPanel.autoAdjust = selected;
			
				txtLowerLimit.setEnabled(!selected);
				txtUpperLimit.setEnabled(!selected);
			}
		});
		gbc = new GridBagConstraints();
		gbc.gridx      = 1;
		gbc.gridy      = 2;
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.anchor     = GridBagConstraints.WEST;
		gbc.fill       = GridBagConstraints.NONE;
		gbc.insets     = new Insets(5, 5, 5, 5);
		this.leftSide.add(this.chkBoxAutoAdjust, gbc);
		
		this.captionPanel = new JPanel();
		gbc = new GridBagConstraints();
		gbc.gridx      = 2;
		gbc.gridy      = 0;
		gbc.gridwidth  = 1;
		gbc.gridheight = 2;
		gbc.weighty    = 1;
		gbc.anchor     = GridBagConstraints.CENTER;
		gbc.fill       = GridBagConstraints.VERTICAL;
		this.captionPanel.setLayout(new FlowLayout());
		this.captionScroll = new JScrollPane(this.captionPanel);
		this.captionScroll.setMinimumSize(new Dimension(50,50));
		this.toggleCaption = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
																				this.leftSide,
																				this.captionScroll);
		this.toggleCaption.setOneTouchExpandable(true);
		this.toggleCaption.setResizeWeight(1.0d);
		this.getContentPane().add(this.toggleCaption);

		this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
		this.addInternalFrameListener(new MyInternalFrameListener());
	}
	
	private void updateLimit(ActionEvent e)
	{
		double val;
		
		try
		{
			if (e.getSource() == this.txtUpperLimit)
			{
				val = Double.parseDouble(this.txtUpperLimit.getText());
				this.plotPanel.upperLimit = val;
			} else {
				val = Double.parseDouble(this.txtLowerLimit.getText());
				this.plotPanel.lowerLimit = val;
			}
		}
		catch (NumberFormatException nfe)
		{
			this.txtUpperLimit.setText(this.df.format(this.plotPanel.upperLimit));
			this.txtLowerLimit.setText(this.df.format(this.plotPanel.lowerLimit));
		}
	}
	
	private void setPlotProperties()
	{
		InputDialog properties = new InputDialog(this, "Plotproperties");
		
		InputValue.IString title = new InputValue.IString("Frame Title",
																											this.getName());
		title.addInputValueListener(new InputValueListener() {
			public void inputValueUpdated(InputValue iv)
			{
				setName(((InputValue.IString)iv).value);
				setTitle(getName() +
				         " [" + plotPanel.shownValuesCount.value + " values]");
			}
		});
		this.plotPanel.shownValuesCount.setUpperLimit(
				Analyser.getInstance().getProcessParameter().cycles());
		this.plotPanel.shownValuesCount.addInputValueListener(
																		new InputValueListener() {
			public void inputValueUpdated(InputValue iv)
			{
				setTitle(getName() +
								 " [" + plotPanel.shownValuesCount.value + " values]");
			}
		});
		
		properties.addElement(this.plotPanel.drawValueLines);
		properties.addElement(this.plotPanel.drawStyle);
		properties.addElement(this.plotPanel.shownValuesCount);
		properties.addElement(title);
		properties.show();
	}
	
	private void saveValueTable()
	{
		JFileChooser          jfc = new JFileChooser();
		File                  savename;
		PrintStream           out;
		int                   size;
		PlotData.PlotValues[] values;
		
		if (this.plotPanel.plotValues == null ||
		    this.plotPanel.plotValues.length == 0)
		{
			// nothing to save
			return;
		}
		Analyser.getInstance().pause();	
		
		if (FirstReturnPlotter.saveDir != null)
		{
			jfc.setCurrentDirectory(FirstReturnPlotter.saveDir);
		}
		try
		{
			if (jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
			{
				savename = jfc.getSelectedFile();	
				savename = savename.getCanonicalFile();
				out = new PrintStream(new FileOutputStream(savename), true);
				values = this.plotPanel.plotValues;
				size   = values[0].values.size();
				
				//Save column headings
				out.print("#");
				for (int i = 0; i < values.length; i++)
				{
					out.print(this.getDescr(values[i].plotObject) + ";");
				}
				out.println();
				
				for (int i = 0; i < size; i++)
				{
					for (int j = 0; j < values.length; j++)
					{
						out.print(values[j].values.get(i).toString() + " ");
					}
					out.println();
				}
				out.close();
			}
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(this,
																		"An error occurs saving value table.\n" + 
																		"(" + e.toString() + ")");
		}
		finally
		{
			Analyser.getInstance().resume();
		}
	
	}
	
	private JMenu createPlotObjectsMenu()
	{
		JMenu    mnuAll     = new JMenu("Plot");
		JMenu    mnuInput   = new JMenu("Input Neurons");
		JMenu    mnuOutput  = new JMenu("Output Neurons");
		JMenu    mnuHidden  = new JMenu("Hidden Neurons");
		JMenu    mnuSynapse = new JMenu("Synapses");
		
		JCheckBoxMenuItem current;
		
		NeuronList  nl;
		SynapseList sl         = new SynapseList();
		int      nID;
		Neuron[]  inputNeurons  = new Neuron[0];
		Neuron[]  outputNeurons = new Neuron[0];
		Neuron[]  hiddenNeurons = new Neuron[0];
		Synapse[] synapses      = new Synapse[0];
		
		final Neuron[] cast = new Neuron[0];

		if (this.net == null) return new JMenu();		

		if ((nl = this.net.getInputNeurons()) != null &&
				nl.size() > 0)
		{
			inputNeurons = (Neuron[])nl.toArray(cast);
			
			for (int i = 0; i < inputNeurons.length; i++)
			{
				nID = inputNeurons[i].id();
				current = new JCheckBoxMenuItem(
									Analyser.getInstance().getNeuronName(nID));
				current.setSelected(this.plotObjects.containsKey(inputNeurons[i])); 
				final Object plotObject = inputNeurons[i];
				current.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{
						togglePlotObject((JCheckBoxMenuItem)e.getSource(), plotObject);						
					}
				});
				mnuInput.add(current);
				
				if (inputNeurons[i].synapses() != null)
				{
					sl.addAll(inputNeurons[i].synapses());
				}
				
				this.plottableObjects.put(inputNeurons[i], current);
			}
			mnuAll.add(mnuInput);
		}
		if ((nl = this.net.getOutputNeurons()) != null &&
				nl.size() > 0)
		{
			outputNeurons = (Neuron[])nl.toArray(cast);

			for (int i = 0; i < outputNeurons.length; i++)
			{
				nID     = outputNeurons[i].id();
				current = new JCheckBoxMenuItem(
									Analyser.getInstance().getNeuronName(nID));
				current.setSelected(this.plotObjects.containsKey(outputNeurons[i]));
				final Object plotObject = outputNeurons[i];
				current.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{
						togglePlotObject((JCheckBoxMenuItem)e.getSource(), plotObject);			
					}
				});
				mnuOutput.add(current);
				
				
				if (outputNeurons[i].synapses() != null)
				{
					sl.addAll(outputNeurons[i].synapses());
				}
				this.plottableObjects.put(outputNeurons[i], current);
			}
			mnuAll.add(mnuOutput);
		}
		if ((nl = this.net.getHiddenNeurons()) != null &&
				nl.size() > 0)
		{
			hiddenNeurons = (Neuron[])nl.toArray(cast);

			for (int i = 0; i < hiddenNeurons.length; i++)
			{
				nID     = hiddenNeurons[i].id();
				current = new JCheckBoxMenuItem(
									Analyser.getInstance().getNeuronName(nID));
				current.setSelected(this.plotObjects.containsKey(hiddenNeurons[i]));
				final Object plotObject = hiddenNeurons[i];
				current.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{
						togglePlotObject((JCheckBoxMenuItem)e.getSource(), plotObject);
					}
				});
				mnuHidden.add(current);
				
				if (hiddenNeurons[i].synapses() != null)
				{
					sl.addAll(hiddenNeurons[i].synapses());
				}
				this.plottableObjects.put(hiddenNeurons[i], current);
			}
			mnuAll.add(mnuHidden);
		}
		if (this.net.getSynapseMode().mode() == SynapseMode.SYNAPSE_MODE_DYNAMIC &&
				sl.size() > 0)
		{
			int j = 0;
			synapses = (Synapse[])sl.toArray(synapses);
			for (int i = 0; i < synapses.length; i++)
			{
				if (synapses[i].mode().mode() == SynapseMode.SYNAPSE_MODE_DYNAMIC)
				{
					current = new JCheckBoxMenuItem("Synapse " +
							synapses[i].getSource().id() + " -> " +
			        synapses[i].getDestination().id());
					current.setSelected(this.plotObjects.containsKey(synapses[i]));
					final Object plotObject = synapses[i];
					current.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e)
						{
							togglePlotObject((JCheckBoxMenuItem)e.getSource(), plotObject);
						}
					});
					mnuSynapse.add(current);
					this.plottableObjects.put(synapses[i], current);
					j++;
				}
			}
			if (j != 0)
			{
				mnuAll.add(mnuSynapse);
			}
		}

		return mnuAll;
	}
	
	private void refreshPlotMenu()
	{
		this.mnuPlot.removeAll();
		this.plotterMenuBar.removeAll();
		this.mnuPlotObjects = this.createPlotObjectsMenu();
		this.mnuPlot.add(this.mnuPlotObjects);
		this.mnuPlot.add(this.mniRefresh);
		this.mnuPlot.add(this.mniProperties);
		this.mnuPlot.add(this.mniExport);
		this.plotterMenuBar.add(this.mnuPlot);
		this.setJMenuBar(this.plotterMenuBar);
		this.repaint();
	
	}
	
	public void addObject(Object plotObject)
	{
		JCheckBoxMenuItem jcbmi = (JCheckBoxMenuItem)
				this.plottableObjects.get(plotObject);
		
		if (jcbmi != null && !jcbmi.isSelected())
		{
			// Use the ActionListener from the MenuItem
			jcbmi.doClick();
		}
	}
	

	private void togglePlotObject(JCheckBoxMenuItem item, Object plotObject)
	{
		if (item.isSelected())
		{
			this.addPlotValues(plotObject);
		}
		else
		{
			this.removePlotValues(plotObject);
		}
	}
	
	/* (non-Javadoc)
	 * @see Hinton.analyser.NetObserver#setNet(Cholsey.Net)
	 */
	synchronized public void setNet(Net net) {
		this.net = net;
		PlotCaption.newNet();
		this.dispose();
	}
	/* (non-Javadoc)
	 * @see Hinton.analyser.NetObserver#netUpdate(Cholsey.Net)
	 */
	synchronized public void netUpdate(Net net) {
		
		//Nothing
	}
	
	private void addPlotValues(Object plotObject)
	{
		String descr = "";
		PlotCaption newCaption;
		ArrayList oldPlot  = new ArrayList(Arrays.asList(this.plotPanel.plotValues));
		ArrayList oldQueue = new ArrayList(
				Arrays.asList(this.plotPanel.queuedValues));
		Analyser  analyser = Analyser.getInstance();
		analyser.pause();
		try
		{
			PlotData.PlotValues plotValues = 
				PlotData.getInstance().getPlotValueStream(plotObject);
			
			if (!oldPlot.contains(plotValues))
			{
				oldPlot.add(plotValues);
				oldQueue.add(new PlotPanel.DoubleQueue(
						this.plotPanel.shownValuesCount.value));
				
				descr = this.getDescr(plotObject);
			
				newCaption = new PlotCaption(descr, plotValues.plotObject);
			
				this.plotPanel.queuedValues = (PlotPanel.DoubleQueue[])
						oldQueue.toArray(new PlotPanel.DoubleQueue[0]);
				this.plotPanel.plotValues = (PlotData.PlotValues[])oldPlot.toArray(
																		new PlotData.PlotValues[0]);
				
				this.addCaptionToPanel(newCaption);
				this.plotObjects.put(plotValues.plotObject, newCaption);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			analyser.resume();
		}				
	}
	
	private String getDescr(Object plotObject)
	{
		String descr = "";
		if (plotObject instanceof Neuron)
		{
			descr = Analyser.getInstance().getNeuronName(
					((Neuron)plotObject).id());
		}
		else if (plotObject instanceof Synapse)
		{
			descr = "Synapse " + ((Synapse)plotObject).getSource().id() + " -> " +
			        ((Synapse)plotObject).getDestination().id();
		}
		return descr;
	}


	private void removePlotValues(Object plotObject)
	{
		PlotCaption caption = (PlotCaption)this.plotObjects.get(plotObject);
		ArrayList oldPlot = new ArrayList(Arrays.asList(this.plotPanel.plotValues));
		ArrayList oldQueue = new ArrayList(
													Arrays.asList(this.plotPanel.queuedValues));
		Analyser analyser = Analyser.getInstance();
		analyser.pause();
		PlotData.PlotValues plotValues = 
												PlotData.getInstance().getPlotValueStream(plotObject);
		analyser.resume();

		if (oldPlot.contains(plotValues))
		{
			oldQueue.remove(oldPlot.indexOf(plotValues));
			oldPlot.remove(plotValues);
			this.plotObjects.remove(plotValues.plotObject);
			this.plotPanel.queuedValues = (PlotPanel.DoubleQueue[])
					oldQueue.toArray(new PlotPanel.DoubleQueue[0]);
			this.plotPanel.plotValues = (PlotData.PlotValues[])oldPlot.toArray(
					new PlotData.PlotValues[0]);											 
			this.removeCaptionFromPanel(caption);
		}
		
	}
	
	

	private void addCaptionToPanel(PlotCaption caption)
	{
		Component[]  comp  = this.captionPanel.getComponents();
		GridBagConstraints gbc = new GridBagConstraints();
		
		this.captionPanel.removeAll();
		this.captionPanel.setLayout(new GridBagLayout());
		
		gbc.gridx   = 0;
		gbc.gridy   = GridBagConstraints.RELATIVE;
		gbc.weightx = 1.0d;
		gbc.fill    = GridBagConstraints.HORIZONTAL;
		gbc.anchor  = GridBagConstraints.NORTH;
		gbc.insets  = new Insets(1, 1, 1, 1);
		
		if (comp != null)
		{
			for (int i = 0; i < comp.length; i++)
			{
				this.captionPanel.add(comp[i], gbc);
			}
		}
		
		gbc.weighty = 1.0d;
		this.captionPanel.add(caption, gbc);
		this.captionPanel.doLayout();
		this.captionPanel.updateUI();
	}
	
	private void removeCaptionFromPanel(PlotCaption caption)
	{
		boolean      found = false;
		Component[]  comp  = this.captionPanel.getComponents();
		GridBagConstraints gbc = new GridBagConstraints();
		
		this.captionPanel.removeAll();
		this.captionPanel.setLayout(new GridBagLayout());
		
		gbc.gridx   = 0;
		gbc.gridy   = GridBagConstraints.RELATIVE;
		gbc.weightx = 1.0d;
		gbc.fill    = GridBagConstraints.HORIZONTAL;
		gbc.anchor  = GridBagConstraints.NORTH;
		gbc.insets  = new Insets(1, 1, 1, 1);
		
		if (comp != null)
		{
			for (int i = 0; i < comp.length; i++)
			{
				
				if (!comp[i].equals(caption))
				{
					if ((i == comp.length-1) ||
							(i == comp.length-2 && !found))
					{
						gbc.weighty = 1.0d;
					}	
					this.captionPanel.add(comp[i], gbc);
				}
				else
				{
					found = true;
				}
			}
		}
		this.captionPanel.doLayout();
		this.captionPanel.updateUI();
	}
	

	
	
	private static class PlotPanel extends JPanel
	{
		public static final Integer DRAWSTYLE_POINTS 				= new Integer(1);
		public static final Integer DRAWSTYLE_LINE   				= new Integer(2);
		public static final Integer DRAWSTYLE_POINTSANDLINE = new Integer(3);
		
		public PlotData.PlotValues[]   plotValues;
		public PlotPanel.DoubleQueue[] queuedValues;
		public InputValue.IBoolean     drawValueLines;
		public InputValue.IObject      drawStyle;
		public InputValue.IInteger     shownValuesCount;
		public JTextField              upperLimitCaption;
		public JTextField              lowerLimitCaption;
		public DecimalFormat           df;
		public boolean                 autoAdjust;
		public double                  lowerLimit = -1;
		public double                  upperLimit = 1;
		public PlotColor         npc;
		
		public PlotPanel(JTextField lowerLimitCaption, 
										 JTextField upperLimitCaption)
		{
			super();
			DecimalFormatSymbols dfs = new DecimalFormatSymbols();
			dfs.setDecimalSeparator('.');
			this.df = new DecimalFormat("0.00", dfs);
			super.setBorder(LineBorder.createBlackLineBorder());
			this.npc = PlotColor.getInstance();
			this.plotValues = new PlotData.PlotValues[0];
			this.queuedValues = new PlotPanel.DoubleQueue[0];
			this.autoAdjust = false;
			this.upperLimitCaption = upperLimitCaption;
			this.lowerLimitCaption = lowerLimitCaption;
			
			
			this.drawStyle = new InputValue.IObject(
																			"Plotmode",
																			1,
																			new Integer[]
																					{ 
																					PlotPanel.DRAWSTYLE_POINTS,
																				  PlotPanel.DRAWSTYLE_LINE,
																				  PlotPanel.DRAWSTYLE_POINTSANDLINE
																					},
																			new String[]
																					{
																					"Points",
																					"Polyline",
																					"Polyline with Points"
																					});
																					
			this.shownValuesCount = new InputValue.IInteger("Number of Datapoints",
																										  100,
																										  10, 1000);
			this.shownValuesCount.addInputValueListener(new InputValueListener() {
				public void inputValueUpdated(InputValue iv) 
				{
					InputValue.IInteger iiv = (InputValue.IInteger)iv;
					for (int i = 0; i < queuedValues.length; i++)
					{
						queuedValues[i] = new PlotPanel.DoubleQueue(iiv.value);
					}
				}
			});
	
			this.drawValueLines = new InputValue.IBoolean("Draw value lines",
																										true);
					
			this.setToolTipText("0.00");			
			
																							  									
		}
	
		
		public String getToolTipText(MouseEvent e)
		{
			double        yScale  = (super.getHeight() - 20) / 
														  (this.upperLimit - this.lowerLimit);
			double        offsetY = 10 + this.upperLimit * yScale;
			double        y       = (double)e.getY();
			
			y = offsetY - y;
			y = y / yScale;
			
			if (this.plotValues != null && this.plotValues.length != 0)
			{
				int    size      = this.plotValues[0].values.size();
				int    startIdx  = size -
		     			    				 this.shownValuesCount.value;
				
			  startIdx = (startIdx < 0 ? 0 : startIdx);
			  startIdx += (int)(e.getX() * this.shownValuesCount.value) / this.getWidth();
			  startIdx++;
			  return startIdx + ";" + this.df.format(y);
			}										  
			
			
			return this.df.format(y);
		
		}
		
		private void drawValueLines(Graphics g, double offsetY)
		{
			int x2 = super.getWidth();
			int y  = super.getHeight() - 10;
			g.setColor(Color.LIGHT_GRAY);
			
			g.drawLine(0,           10, x2,           10); // upperLimit
			g.drawLine(0, (int)offsetY, x2, (int)offsetY); // 0.0
			g.drawLine(0,            y, x2,            y); // lowerLimit
		}
		
		public void doLayout()
		{
			super.doLayout();
			this.clearQueues();
		}
		
		public void clearQueues()
		{
			for (int i = 0; i < this.queuedValues.length; i++)
			{
				this.queuedValues[i].clear();
			}
		}

		
		public void paint(Graphics g)
		{
			super.paint(g);
			try
			{
			if (this.plotValues == null || this.plotValues.length == 0) return;
			
			double yScale    = (super.getHeight() - 20) / 
											   (this.upperLimit - this.lowerLimit);
			double stepWidth = (double)super.getWidth() /
										     (double)(this.shownValuesCount.value - 1);
			double offsetY   = 10 + this.upperLimit * yScale;
			int    size      = this.plotValues[0].values.size();
			int    startIdx  = size -
	     			    				 this.shownValuesCount.value;
			double    offsetX;
			double value1, value2;
			boolean calc1, calc2;
			
			if (this.plotValues[0].values.size()
					< Math.abs(this.queuedValues[0].tail - this.queuedValues[0].head))
			{
				this.clearQueues();
			}
			startIdx = (startIdx < 0 ? 0 : startIdx);
			
			if (this.drawValueLines.value)
			{
				this.drawValueLines(g, offsetY);
			}

			
			
			for (int i = 0; i < this.plotValues.length; i++)
			{
				g.setColor(this.npc.getColor(this.plotValues[i].plotObject));
				value2 = 0.0d;
				offsetX = 0.0d;
				
				for (int j = startIdx; j < size - 1; j++)
				{
					if (j < this.plotValues[i].values.size())
					{
						if (j == startIdx)
						{
							if (this.queuedValues[i].isEmpty())
							{
								value1 = 
									((Double)this.plotValues[i].values.get(j)).doubleValue();
								if (this.autoAdjust)
								{
									if (value1 > this.upperLimit)
									{
										this.upperLimit = value1 + 0.1d;
										this.upperLimitCaption.setText(df.format(this.upperLimit));
										this.clearQueues();
										return;
									}
									if (value1 < this.lowerLimit)
									{
										this.lowerLimit = value1 - 0.1d;
										this.lowerLimitCaption.setText(df.format(this.lowerLimit));
										this.clearQueues();
										return;
									}
								}
								value1 = offsetY - yScale*value1;
								this.queuedValues[i].put(value1);
								this.queuedValues[i].get(); // Let the queue look empty
							}
							else
							{
								value1 = this.queuedValues[i].get();
							}
						}
						else
						{
							value1 = value2;
						}
						
						if (this.queuedValues[i].isEmpty())
						{
							value2 = ((Double)
									this.plotValues[i].values.get(j+1)).doubleValue();
							if (this.autoAdjust)
							{
								if (value2 > this.upperLimit)
								{
									this.upperLimit = value2 + 0.1d;
									this.upperLimitCaption.setText(df.format(this.upperLimit));
									this.clearQueues();
									return;
								}
								if (value2 < this.lowerLimit)
								{
									this.lowerLimit = value2 - 0.1d;
									this.lowerLimitCaption.setText(df.format(this.lowerLimit));
									this.clearQueues();
									return;
								}
							}
							value2 = offsetY - yScale*value2;
							this.queuedValues[i].put(value2);
							this.queuedValues[i].get(); // Let the queue look empty
						}
						else
						{
							value2 = this.queuedValues[i].get();
						}
						
						
						if (this.drawStyle.value.equals(PlotPanel.DRAWSTYLE_LINE) ||
								this.drawStyle.value.equals(PlotPanel.DRAWSTYLE_POINTSANDLINE))
						{							
						  //g.drawLine((int)(offsetX + 0.5d),(int)(value1),(int)(offsetX + stepWidth + 0.5d),(int)(value2));
						  g.drawOval((int)(value1),(int)(value2),1,1);
						}
						if (this.drawStyle.value.equals(PlotPanel.DRAWSTYLE_POINTS) ||
								this.drawStyle.value.equals(PlotPanel.DRAWSTYLE_POINTSANDLINE))
						{
						  // g.drawRect((int)(offsetX + 0.5d) - 2, (int)value1 - 2, 4, 4);
						  g.drawOval((int)(value1),(int)(value2),1,1);
						}
					
					}
					offsetX += stepWidth;
				}
				this.queuedValues[i].setPositions();
			}
			}
			catch (Exception e)
			{
				e.printStackTrace();//Nothing
			}
		} // paint()/1
		
		private static class DoubleQueue
		{
			private double[] values;
			private int      size;
			private int      tail;
			private int      head;
			private int      currIdx;
			
			public DoubleQueue(int size)
			{
				this.size    = size;
				this.values  = new double[size];
				this.head    = 0;
				this.tail    = 0;
				this.currIdx = -1;
			}
			
			public boolean isEmpty()
			{
				return (this.head == this.tail) ||
					(this.currIdx == this.head);
			}
			
			public double get()
			{
				double ret = 0.0d;
				
				if (this.currIdx == -1)
				{
					this.currIdx = this.tail;
				}
				
				ret = this.values[this.currIdx];
				this.currIdx++;
				if (this.currIdx == this.values.length)
				{
					this.currIdx = 0;
				}
				return ret;
			}
			
			public void put(double value)
			{
				this.values[this.head] = value;
				this.head++;
				if (this.head == this.size)
				{
					this.head = 0;
				}
			}
			
			public void setPositions()
			{
				if (this.head + 1 == this.tail ||
						(this.head == this.size - 1 && this.tail == 0))
				{
					this.tail++;
					if (this.tail == this.size)
					{
						this.tail = 0;
					}
				}
				this.currIdx = -1;	
			}
			
			public void clear()
			{
				this.head    = 0;
				this.tail    = 0;
				this.currIdx = -1;
			}
			
		}
		
		
	} // class plotPanel
	
	private class MyInternalFrameListener extends InternalFrameAdapter
	{
		
		 /* (non-Javadoc)
		 * @see javax.swing.event.InternalFrameListener#internalFrameClosing(
		 * 												javax.swing.event.InternalFrameEvent)
		 */
		public void internalFrameClosing(InternalFrameEvent e) {
			Analyser.getInstance().firstReturnMapClosed((FirstReturnPlotter)e.getInternalFrame());
			super.internalFrameClosing(e);
		}

	} // class MyInternalFrameListener

}
