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

package util.misc;


import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.tree.DefaultMutableTreeNode;

import util.io.EvoTaskXMLHandler;
import util.io.ExFileFilter;

import addon.tables.NeuralNetTable;
import cholsey.Net;
import cholsey.Neuron;
import cholsey.NeuronType;
import cholsey.ProcessMode;
import cholsey.Synapse;
import cholsey.SynapseMode;
import cholsey.Transferfunction;


public class NetEdit extends JFrame implements ActionListener,
  TableModelListener
{


  // **************************************************************************
  // different mode
  // **************************************************************************
  
  public final static int MODE_EDIT_NET = 0;
  public final static int MODE_VIEW_NET = 1;

  // **************************************************************************
  // stuff
  // **************************************************************************
  private DefaultMutableTreeNode netNode = new DefaultMutableTreeNode("Net");
  private JTree tree = new JTree(netNode);
  private JPanel panel = new JPanel();
  private JScrollPane scrollpane = new JScrollPane(tree);
  private JPanel neuronAddPanel = new JPanel();
  private JPanel neuronDelPanel = new JPanel();
  private JPanel neuronChangePanel = new JPanel();
  private JPanel synapseAddPanel = new JPanel();
  private JPanel synapseDelPanel = new JPanel();
  private JPanel globalPanel = new JPanel();


  // buttons
  private JButton addNeuronButton = new JButton("add neuron");
  private JButton delNeuronButton = new JButton("del neuron");
  private JButton changeNeuronButton = new JButton("change neuron");
  private JButton addSynapseButton = new JButton("add/change synapse");
  private JButton delSynapseButton = new JButton("del synapse");
  private JButton newNetButton    = new JButton("new net");
  private JButton saveButton    = new JButton("save");
  private JButton closeButton    = new JButton("close");

  // text field
  private JTextField neuronBiasInput = new JTextField("0",10);
  private JTextField neuronTransmitterLevelInput = new JTextField("0",10);
  private JTextField neuronReceptorLevelInput = new JTextField("0",10);
  private JTextField neuronActivationInput = new JTextField("0",10);

  private JTextField neuronChangeActivationInput = new JTextField("0",10);
  private JTextField neuronChangeBiasInput = new JTextField("0",10);
  private JTextField neuronChangeTransmitterLevelInput = new JTextField("0",10);
  private JTextField neuronChangeReceptorLevelInput = new JTextField("0",10);

  private JTextField synapseStrengthInput = new JTextField("1",10);

  // combo boxes
  private JComboBox neuronProcessModeComboBox = new JComboBox();
  private JComboBox neuronNeuronTypeComboBox = new JComboBox();
  private JComboBox neuronDelNeuronComboBox = new JComboBox();

  private JComboBox neuronChangeProcessModeComboBox = new JComboBox();
  private JComboBox neuronChangeNeuronTypeComboBox = new JComboBox();
  private JComboBox neuronChangeIndexComboBox = new JComboBox();

  private JComboBox synapseProcessModeComboBox = new JComboBox();
  private JComboBox synapseSynapseModeComboBox = new JComboBox();

  private JComboBox synapseAddSourceNeuronComboBox = new JComboBox();
  private JComboBox synapseAddDestinationNeuronComboBox = new JComboBox();
  private JComboBox synapseDelSourceNeuronComboBox = new JComboBox();
  private JComboBox synapseDelDestinationNeuronComboBox = new JComboBox();

  private JComboBox transferfunctionComboBox = new JComboBox();
  private NeuralNetTable neuralNetPane = new NeuralNetTable();


  private Net net = null;
  
  public NetEdit(int mode)
  {
    // **************************************************************************
    // general stuff
    // **************************************************************************
    super("NetEdit");
    /*
    this.addWindowListener(new WindowAdapter() { public void
        windowClosing(WindowEvent e) {  }});
        */
    JScrollPane globaleScrollPane;
    //setBounds ( 100,100, 700,550);


    JMenuBar menuBar = new JMenuBar();
    for(int index=0; index < neuralNetPane.getMenus().size(); index++)
    {
      JMenu m = (JMenu)neuralNetPane.getMenus().get(index);

      menuBar.add(m);
    }
    this.setJMenuBar(menuBar);

    switch(mode)
    {
      case MODE_EDIT_NET:
        GridLayout layout = new GridLayout(1,1);
        
        neuronBiasInput.setHorizontalAlignment(JTextField.RIGHT);
        neuronTransmitterLevelInput.setHorizontalAlignment(JTextField.RIGHT);
        neuronReceptorLevelInput.setHorizontalAlignment(JTextField.RIGHT);
        neuronActivationInput.setHorizontalAlignment(JTextField.RIGHT);
        synapseStrengthInput.setHorizontalAlignment(JTextField.RIGHT);


        for(int i=0;i<SynapseMode.SYNAPSE_MODE_LIST.length;i++)
        {
          synapseSynapseModeComboBox.addItem(
              SynapseMode.SYNAPSE_MODE_LIST[i].toString());
        }

        for(int i=0;i<ProcessMode.LIST.length;i++)
        {
          synapseProcessModeComboBox.addItem(ProcessMode.LIST[i].toString());
          neuronProcessModeComboBox.addItem(ProcessMode.LIST[i].toString());
          neuronChangeProcessModeComboBox.addItem(ProcessMode.LIST[i].toString());
        }

        for(int i=0;i<NeuronType.LIST.length;i++)
        {
          neuronNeuronTypeComboBox.addItem(NeuronType.LIST[i].toString());
          neuronChangeNeuronTypeComboBox.addItem(NeuronType.LIST[i].toString());
        }

        for(int i=0;i<Transferfunction.LIST.length;i++)
        {
          transferfunctionComboBox.addItem(Transferfunction.LIST[i].toString());
        }

        synapseSynapseModeComboBox.setSelectedIndex(0);

        neuralNetPane.addTableModelListener(this);
        saveButton.addActionListener(this);
        closeButton.addActionListener(this);
        newNetButton.addActionListener(this);
        addNeuronButton.addActionListener(this);
        delNeuronButton.addActionListener(this);
        changeNeuronButton.addActionListener(this);
        addSynapseButton.addActionListener(this);
        delSynapseButton.addActionListener(this);
        neuronChangeIndexComboBox.addActionListener(this);
        transferfunctionComboBox.addActionListener(this);

        updateNeuronSelectionComboBoxes();

        //panel.setLayout(layout);
        //globaleScrollPane = new JScrollPane(panel);
        JPanel contentPane = (JPanel)getContentPane();
        JSplitPane splitPane = new JSplitPane();
        contentPane.setLayout(layout);
        splitPane.add(neuralNetPane, JSplitPane.LEFT);
        splitPane.add(scrollpane, JSplitPane.RIGHT);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(1.0);
        //splitPane.setDividerSize(50);
        contentPane.add(splitPane);

        break; // end of MODE_EDIT_NET
      case MODE_VIEW_NET:

        BorderLayout bLayout = new BorderLayout();
        JPanel savePanel = new JPanel();
        saveButton.addActionListener(this);

        panel.setLayout(bLayout);
        panel.add(saveButton, BorderLayout.SOUTH);
        panel.add(scrollpane, BorderLayout.CENTER);
        globaleScrollPane = new JScrollPane(panel);
        setContentPane(globaleScrollPane);


        break;
    }
    
    
    pack();
  }

  public NetEdit()
  {
    this(MODE_EDIT_NET);
  }

  public void setupSynapsePanel()
  {
    int row = -1;
    GridBagLayout gbl = new GridBagLayout();

    JLabel synapseStrengthLabel = new JLabel("strength");
    JLabel synapseProcessModeLabel   = new JLabel("process mode");
    JLabel synapseSynapseModeLabel   = new JLabel("synapse mode");
    JLabel synapseAddDestinationNeuronLabel = new JLabel("destination");
    JLabel synapseAddSourceNeuronLabel = new JLabel("source");
    JLabel synapseDelDestinationNeuronLabel = new JLabel("destination");
    JLabel synapseDelSourceNeuronLabel = new JLabel("source");

    synapseAddPanel.setLayout(gbl);


    // synapse strength line
    row++;
    GridBagConstraints addSynapseButtonConstraints = 
      new GridBagConstraints(0, row, 2, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(addSynapseButton,addSynapseButtonConstraints);

    synapseAddPanel.add(addSynapseButton);

    // strength input line
    row++;

    GridBagConstraints synapseStrengthLabelConstraints = 
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints synapseStrengthInputConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(synapseStrengthLabel,synapseStrengthLabelConstraints);
    gbl.setConstraints(synapseStrengthInput,synapseStrengthInputConstraints);

    synapseAddPanel.add(synapseStrengthLabel);
    synapseAddPanel.add(synapseStrengthInput);

    // synapse process mode line
    /*
    row++;

    GridBagConstraints synapseProcessModeLabelConstraints = 
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints synapseProcessModeComboBoxConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(synapseProcessModeLabel,
        synapseProcessModeLabelConstraints);

    gbl.setConstraints(synapseProcessModeComboBox,
        synapseProcessModeComboBoxConstraints);


    synapseAddPanel.add(synapseProcessModeLabel);
    synapseAddPanel.add(synapseProcessModeComboBox);
    */

    if(System.getProperty("user.name").equals("zahedi"))
    {
      // synapse mode line
      row++;
      GridBagConstraints synapseSynapseModeLabelConstraints = 
        new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
            GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

      GridBagConstraints synapseSynapseModeComboBoxConstraints = 
        new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
            GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

      gbl.setConstraints(synapseSynapseModeLabel,
          synapseSynapseModeLabelConstraints);

      gbl.setConstraints(synapseSynapseModeComboBox,
          synapseSynapseModeComboBoxConstraints);


      synapseAddPanel.add(synapseSynapseModeLabel);
      synapseAddPanel.add(synapseSynapseModeComboBox);

    }

    // add source neuron
    row++;

    GridBagConstraints synapseAddSourceNeuronLabelConstraints = 
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints synapseAddSourceNeuronComboBoxConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(synapseAddSourceNeuronLabel,
        synapseAddSourceNeuronLabelConstraints);

    gbl.setConstraints(synapseAddSourceNeuronComboBox,
        synapseAddSourceNeuronComboBoxConstraints);


    synapseAddPanel.add(synapseAddSourceNeuronLabel);
    synapseAddPanel.add(synapseAddSourceNeuronComboBox);


    // add destination neuron
    row++;

    GridBagConstraints synapseAddDestinationNeuronLabelConstraints = 
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints synapseAddDestinationNeuronComboBoxConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(synapseAddDestinationNeuronLabel,
        synapseAddDestinationNeuronLabelConstraints);

    gbl.setConstraints(synapseAddDestinationNeuronComboBox,
        synapseAddDestinationNeuronComboBoxConstraints);


    synapseAddPanel.add(synapseAddDestinationNeuronLabel);
    synapseAddPanel.add(synapseAddDestinationNeuronComboBox);

    // **************************************************************************
    // new panel
    // **************************************************************************
    gbl = new GridBagLayout();
    synapseDelPanel.setLayout(gbl);

    // del synapse
    row=0;

    GridBagConstraints delSynapseButtonConstraints = 
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(delSynapseButton,delSynapseButtonConstraints);

    synapseDelPanel.add(delSynapseButton);

    // del source neuron
    row++;

    GridBagConstraints synapseDelSourceNeuronLabelConstraints = 
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints synapseDelSourceNeuronComboBoxConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(synapseDelSourceNeuronLabel,
        synapseDelSourceNeuronLabelConstraints);

    gbl.setConstraints(synapseDelSourceNeuronComboBox,
        synapseDelSourceNeuronComboBoxConstraints);


    synapseDelPanel.add(synapseDelSourceNeuronLabel);
    synapseDelPanel.add(synapseDelSourceNeuronComboBox);


    // del destination neuron
    row++;

    GridBagConstraints synapseDelDestinationNeuronLabelConstraints = 
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints synapseDelDestinationNeuronComboBoxConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(synapseDelDestinationNeuronLabel,
        synapseDelDestinationNeuronLabelConstraints);

    gbl.setConstraints(synapseDelDestinationNeuronComboBox,
        synapseDelDestinationNeuronComboBoxConstraints);


    synapseDelPanel.add(synapseDelDestinationNeuronLabel);
    synapseDelPanel.add(synapseDelDestinationNeuronComboBox);

    gbl = new GridBagLayout();
    globalPanel.setLayout(gbl);


    // net line
    row=0;

    GridBagConstraints newNetButtonConstraints = 
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints transferfunctionComboBoxCostraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);


    gbl.setConstraints(newNetButton,newNetButtonConstraints);
    gbl.setConstraints(transferfunctionComboBox,
        transferfunctionComboBoxCostraints);

    globalPanel.add(newNetButton);
    globalPanel.add(transferfunctionComboBox);

    // load & save
    row++;

    GridBagConstraints saveButtonConstraints = 
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(saveButton,saveButtonConstraints);

    globalPanel.add(saveButton);

    GridBagConstraints closeButtonConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(closeButton,closeButtonConstraints);

    globalPanel.add(closeButton);




  }

  public void setupNeuronPanel()
  {
    int row = -1;
    GridBagLayout gbl = new GridBagLayout();
    neuronAddPanel.setLayout(gbl);

    // labels
    JLabel neuronBiasLabel = new JLabel("bias");
    JLabel neuronProcessModeLabel = new JLabel("process mode");
    JLabel neuronNeuronTypeLabel = new JLabel("neuron layer");
    JLabel neuronTransmitterLevelLabel = new JLabel("transmitter");
    JLabel neuronReceptorLevelLabel = new JLabel("receptors");
    JLabel neuronActivationLabel = new JLabel("activation");

    JLabel neuronChangeBiasLabel = new JLabel("bias");
    JLabel neuronChangeProcessModeLabel = new JLabel("process mode");
    JLabel neuronChangeNeuronTypeLabel = new JLabel("neuron layer");
    JLabel neuronChangeTransmitterLevelLabel = new JLabel("transmitter");
    JLabel neuronChangeReceptorLevelLabel = new JLabel("receptors");
    JLabel neuronChangeActivationLabel = new JLabel("activation");

    // new net line
    row++;

    GridBagConstraints addNeuronButtonConstraints = 
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(addNeuronButton,addNeuronButtonConstraints);

    neuronAddPanel.add(addNeuronButton);

    // activation input line
    row++;

    GridBagConstraints neuronActivationLabelConstraints = 
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints neuronActivationInputConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(neuronActivationInput,neuronActivationInputConstraints);
    gbl.setConstraints(neuronActivationLabel,neuronActivationLabelConstraints);

    neuronAddPanel.add(neuronActivationInput);
    neuronAddPanel.add(neuronActivationLabel);


    // bias input line
    row++;

    GridBagConstraints neuronBiasLabelConstraints = 
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints neuronBiasInputConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(neuronBiasInput,neuronBiasInputConstraints);
    gbl.setConstraints(neuronBiasLabel,neuronBiasLabelConstraints);

    neuronAddPanel.add(neuronBiasInput);
    neuronAddPanel.add(neuronBiasLabel);

    //if(System.getProperty("user.name").equals("zahedi"))
    {
      // transmitter line
      row++;

      GridBagConstraints neuronTransmitterLevelLabelConstraints = 
        new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
            GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

      GridBagConstraints neuronTransmitterLevelInputConstraints = 
        new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
            GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

      gbl.setConstraints(neuronTransmitterLevelInput,
          neuronTransmitterLevelInputConstraints);

      gbl.setConstraints(neuronTransmitterLevelLabel,
          neuronTransmitterLevelLabelConstraints);

      neuronAddPanel.add(neuronTransmitterLevelLabel);
      neuronAddPanel.add(neuronTransmitterLevelInput);

      // receptor line
      row++;

      GridBagConstraints neuronReceptorLevelLabelConstraints = 
        new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
            GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

      GridBagConstraints neuronReceptorLevelInputConstraints = 
        new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
            GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

      gbl.setConstraints(neuronReceptorLevelInput,
          neuronReceptorLevelInputConstraints);

      gbl.setConstraints(neuronReceptorLevelLabel,
          neuronReceptorLevelLabelConstraints);

      neuronAddPanel.add(neuronReceptorLevelLabel);
      neuronAddPanel.add(neuronReceptorLevelInput);


    }

    // process mode line
    /*
    row++;

    GridBagConstraints neuronProcessModeLabelConstraints = 
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints neuronProcessModeConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);


    gbl.setConstraints(neuronProcessModeComboBox, neuronProcessModeConstraints);
    gbl.setConstraints(neuronProcessModeLabel, neuronProcessModeLabelConstraints);

    neuronAddPanel.add(neuronProcessModeComboBox);
    neuronAddPanel.add(neuronProcessModeLabel);
    */


    // process mode line
    row++;

    GridBagConstraints neuronNeuronTypeLabelConstraints = 
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints neuronNeuronTypeConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);


    gbl.setConstraints(neuronNeuronTypeComboBox, neuronNeuronTypeConstraints);
    gbl.setConstraints(neuronNeuronTypeLabel, neuronNeuronTypeLabelConstraints);

    neuronAddPanel.add(neuronNeuronTypeComboBox);
    neuronAddPanel.add(neuronNeuronTypeLabel);

    // **************************************************************************
    // new panel
    // **************************************************************************

    gbl = new GridBagLayout();
    neuronDelPanel.setLayout(gbl);

    // del neuron line
    row=0;
    GridBagConstraints delNeuronButtonConstraints = 
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints neuronDelNeuronComboBoxCostraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);



    gbl.setConstraints(delNeuronButton,delNeuronButtonConstraints);
    gbl.setConstraints(neuronDelNeuronComboBox,neuronDelNeuronComboBoxCostraints);

    neuronDelPanel.add(delNeuronButton);
    neuronDelPanel.add(neuronDelNeuronComboBox);
    // ************************************************************************** 
    // change neuron section
    // ************************************************************************** 
    gbl = new GridBagLayout();
    neuronChangePanel.setLayout(gbl);

    // chaneg neuron button line
    row=0;
    GridBagConstraints changeNeuronButtonConstraints = 
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints neuronChangeIndexComboBoxConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);


    gbl.setConstraints(changeNeuronButton,changeNeuronButtonConstraints);
    gbl.setConstraints(neuronChangeIndexComboBox,
        neuronChangeIndexComboBoxConstraints);

    neuronChangePanel.add(changeNeuronButton);
    neuronChangePanel.add(neuronChangeIndexComboBox);

    // activation input line
    row++;

    GridBagConstraints neuronChangeActivationLabelConstraints = 
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints neuronChangeActivationInputConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(neuronChangeActivationInput,neuronChangeActivationInputConstraints);
    gbl.setConstraints(neuronChangeActivationLabel,neuronChangeActivationLabelConstraints);

    neuronChangePanel.add(neuronChangeActivationInput);
    neuronChangePanel.add(neuronChangeActivationLabel);


    // bias input line
    row++;

    GridBagConstraints neuronChangeBiasLabelConstraints = 
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints neuronChangeBiasInputConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(neuronChangeBiasInput,neuronChangeBiasInputConstraints);
    gbl.setConstraints(neuronChangeBiasLabel,neuronChangeBiasLabelConstraints);

    neuronChangePanel.add(neuronChangeBiasInput);
    neuronChangePanel.add(neuronChangeBiasLabel);

    //if(System.getProperty("user.name").equals("zahedi"))
    {
      // transmitter line
      row++;

      GridBagConstraints neuronChangeTransmitterLevelLabelConstraints = 
        new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
            GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

      GridBagConstraints neuronChangeTransmitterLevelInputConstraints = 
        new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
            GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

      gbl.setConstraints(neuronChangeTransmitterLevelInput,
          neuronChangeTransmitterLevelInputConstraints);

      gbl.setConstraints(neuronChangeTransmitterLevelLabel,
          neuronChangeTransmitterLevelLabelConstraints);

      neuronChangePanel.add(neuronChangeTransmitterLevelLabel);
      neuronChangePanel.add(neuronChangeTransmitterLevelInput);

      // receptor line
      row++;

      GridBagConstraints neuronChangeReceptorLevelLabelConstraints = 
        new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
            GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

      GridBagConstraints neuronChangeReceptorLevelInputConstraints = 
        new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
            GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

      gbl.setConstraints(neuronChangeReceptorLevelInput,
          neuronChangeReceptorLevelInputConstraints);

      gbl.setConstraints(neuronChangeReceptorLevelLabel,
          neuronChangeReceptorLevelLabelConstraints);

      neuronChangePanel.add(neuronChangeReceptorLevelLabel);
      neuronChangePanel.add(neuronChangeReceptorLevelInput);


    }

    // process mode line
    /*
    row++;

    GridBagConstraints neuronChangeProcessModeLabelConstraints = 
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints neuronChangeProcessModeConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);


    gbl.setConstraints(neuronChangeProcessModeComboBox, neuronChangeProcessModeConstraints);
    gbl.setConstraints(neuronChangeProcessModeLabel, neuronChangeProcessModeLabelConstraints);

    neuronChangePanel.add(neuronChangeProcessModeComboBox);
    neuronChangePanel.add(neuronChangeProcessModeLabel);
    */


    // process mode line
    row++;

    GridBagConstraints neuronChangeNeuronTypeLabelConstraints = 
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints neuronChangeNeuronTypeConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);


    gbl.setConstraints(neuronChangeNeuronTypeComboBox,
        neuronChangeNeuronTypeConstraints);
    gbl.setConstraints(neuronChangeNeuronTypeLabel,
        neuronChangeNeuronTypeLabelConstraints);

    neuronChangePanel.add(neuronChangeNeuronTypeComboBox);
    neuronChangePanel.add(neuronChangeNeuronTypeLabel);



  }

  public void tableChanged(TableModelEvent tme)
  {
    updateView(neuralNetPane.getNeuralNet());
  }

  public void actionPerformed(ActionEvent e)
  {
    if(e.getSource() == closeButton)
    {
      setVisible(false);
      return;
    }

    if(e.getSource() == newNetButton)
    {
      net = new Net();
      net.setTransferfunction(
          Transferfunction.LIST[transferfunctionComboBox.getSelectedIndex()]);
      updateView(net);
      return;
    }

    if(e.getSource() == saveButton)
    {
      String outputfilename = new String();
      JFileChooser fileDialog = new JFileChooser(".");
      fileDialog.addChoosableFileFilter(new ExFileFilter("xml","Neural Net ( .xml )"));
      int returnVal = fileDialog.showSaveDialog(this);
      if(returnVal == JFileChooser.APPROVE_OPTION) {

        outputfilename = new String(fileDialog.getCurrentDirectory() +
            System.getProperty("file.separator") +
            fileDialog.getSelectedFile().getName());

        if(outputfilename.indexOf(".") == -1)
        {
          outputfilename = new String(outputfilename+".xml");
        }

        EvoTaskXMLHandler eh = new EvoTaskXMLHandler();
        eh.writeNetToFile(net, outputfilename, "Created with NetEdit.");
      }
    }
    
    if (net == null)
    {
      net = new Net();
    }

    if(e.getSource() == transferfunctionComboBox)
    {
      net.setTransferfunction(
          Transferfunction.LIST[transferfunctionComboBox.getSelectedIndex()]);
      updateView(net);
    }
    if(e.getSource() == changeNeuronButton)
    {
      if(neuronChangeIndexComboBox.getItemCount() == 0)
      {
        return;
      }
      Neuron neuron = net.neurons().neuron(
          neuronChangeIndexComboBox.getSelectedIndex());
      double bias = Double.parseDouble(neuronChangeBiasInput.getText());
      double transmitter =
        Double.parseDouble(neuronChangeTransmitterLevelInput.getText());
      double receptors = Double.parseDouble(neuronChangeReceptorLevelInput.getText());
      ProcessMode pm =
        ProcessMode.LIST[neuronChangeProcessModeComboBox.getSelectedIndex()];
      NeuronType nt =
        NeuronType.LIST[neuronChangeNeuronTypeComboBox.getSelectedIndex()];

      neuron.setBias(bias);
      neuron.setTransmitterLevel(transmitter);
      neuron.setReceptorLevel(receptors);
      neuron.setProcessMode(pm);
      net.setNeuronType(neuron,nt);
      neuron.setActivation(Double.parseDouble(neuronChangeActivationInput.getText()));
      updateView(net);
    }

    if(e.getSource() == neuronChangeIndexComboBox)
    {
      Neuron neuron =
        net.neurons().neuron(neuronChangeIndexComboBox.getSelectedIndex());
      
      neuronChangeBiasInput.setText("" + neuron.getBias());
      neuronChangeTransmitterLevelInput.setText(""+neuron.getTransmitterLevel());
      neuronChangeReceptorLevelInput.setText(""+neuron.getReceptorLevel()); 
      neuronChangeActivationInput.setText(""+neuron.getActivation()); 

      for(int i=0;i<ProcessMode.LIST.length;i++)
      {
        if( ProcessMode.LIST[i] == neuron.getProcessMode())
        {
          neuronChangeProcessModeComboBox.setSelectedIndex(i);
        }
      }

      for(int i=0;i<NeuronType.LIST.length;i++)
      {
        if( NeuronType.LIST[i] == neuron.getNeuronType())
        {
          neuronChangeNeuronTypeComboBox.setSelectedIndex(i);
        }
      }
    }

    if(e.getSource() == addNeuronButton)
    {
      double bias = Double.parseDouble(neuronBiasInput.getText());
      double transmitter = Double.parseDouble(neuronTransmitterLevelInput.getText());
      double receptors = Double.parseDouble(neuronReceptorLevelInput.getText());
      ProcessMode pm =
        ProcessMode.LIST[neuronProcessModeComboBox.getSelectedIndex()];
      NeuronType nt =
        NeuronType.LIST[neuronNeuronTypeComboBox.getSelectedIndex()];
      Neuron neuron = net.addNeuron( bias, transmitter, receptors, pm, nt);
      neuron.setActivation(Double.parseDouble(neuronActivationInput.getText()));
      updateView(net);
      updateNeuronSelectionComboBoxes();
      return;
    }

    if(e.getSource() == delNeuronButton)
    {
      if(neuronChangeIndexComboBox.getItemCount() == 0)
      {
        return;
      }
      net.delNeuron(neuronDelNeuronComboBox.getSelectedIndex());
      updateView(net);
      updateNeuronSelectionComboBoxes();
      return;
    }

    if(e.getSource() == addSynapseButton)
    {
      if(synapseAddSourceNeuronComboBox.getItemCount() == 0 ||
        synapseAddDestinationNeuronComboBox.getItemCount() == 0)
      {
        return;
      }
      int destIndex = synapseAddDestinationNeuronComboBox.getSelectedIndex() +
        net.getInputNeurons().size();
      int sourceIndex = synapseAddSourceNeuronComboBox.getSelectedIndex();
      double strength = Double.parseDouble(synapseStrengthInput.getText());
      ProcessMode pm =
        ProcessMode.LIST[synapseProcessModeComboBox.getSelectedIndex()];
      SynapseMode sm = 
        SynapseMode.SYNAPSE_MODE_LIST[
        synapseSynapseModeComboBox.getSelectedIndex()];

      Synapse synapse = net.neurons().neuron(destIndex).getSynapse(
          net.neurons().neuron(sourceIndex));
      if (synapse == null) // no such synapse
      {
        net.addSynapse(net.neurons().neuron(sourceIndex),
            net.neurons().neuron(destIndex),
            strength,
            pm);

      }
      else
      {
        System.out.println("NetEdit:: calling setStrength");
        synapse.setStrength(strength);
        synapse.setProcessMode(pm);
      }

      net.setSynapseMode(sm);
      updateView(net);
      return;
    }

    if(e.getSource() == delSynapseButton)
    {
      if(synapseDelSourceNeuronComboBox.getItemCount() == 0 ||
         synapseDelDestinationNeuronComboBox.getItemCount() == 0)
      {
        return;
      }
      int destIndex = synapseDelDestinationNeuronComboBox.getSelectedIndex() +
        net.getInputNeurons().size();
      int sourceIndex = synapseDelSourceNeuronComboBox.getSelectedIndex();
      net.delSynapse(sourceIndex,destIndex);
      updateView(net);
    }
  }

  public void updateView(Net net)
  {
    if(net == null)
    {
      net = new Net();
    }
    this.net = net;
    //updateNeuronSelectionComboBoxes();
    DefaultMutableTreeNode dummy = net.copy().toJTreeNode();
    if(dummy != null)
    {
      scrollpane.remove(tree);
      this.tree = new JTree(dummy);
      scrollpane.getViewport().setView(tree);
      setVisible(true);
    }
    //System.out.println("NEUES NETZ : \n" + net.toString());
  }

  public void setNet(Net net)
  {
    neuralNetPane.setNeuralNet(net);
  }

  public Net getNet() 
  {
    return net;
  }

  private void updateNeuronSelectionComboBoxes()
  {
    synapseAddSourceNeuronComboBox.removeAllItems();
    synapseAddDestinationNeuronComboBox.removeAllItems();
    synapseDelSourceNeuronComboBox.removeAllItems();
    synapseDelDestinationNeuronComboBox.removeAllItems();
    neuronDelNeuronComboBox.removeAllItems();
    neuronChangeIndexComboBox.removeAllItems();

    if(net == null)
    {
      return;
    }
    for(int i=0;i<net.neurons().size();i++)
    {
      String neuronString = new String ("Neuron " + (i+1));
      if(net.getNeuron(i).getNeuronType() != NeuronType.INPUT)
      {
        synapseAddDestinationNeuronComboBox.addItem(neuronString);
        synapseDelDestinationNeuronComboBox.addItem(neuronString);
      }
      synapseAddSourceNeuronComboBox.addItem(neuronString);
      synapseDelSourceNeuronComboBox.addItem(neuronString);
      neuronDelNeuronComboBox.addItem(neuronString);
      neuronChangeIndexComboBox.addItem(neuronString);
      transferfunctionComboBox.setSelectedIndex(net.getTransferfunction().mode());
    }
  }

  
  public static void main(String argv[])
  {
    JDialog.setDefaultLookAndFeelDecorated(true);
    JFrame.setDefaultLookAndFeelDecorated(true);
    Toolkit.getDefaultToolkit().setDynamicLayout(true);
    System.setProperty("sun.awt.noerasebackground","true");

    try {
      javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme( 
          new javax.swing.plaf.metal.DefaultMetalTheme());
      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
    }  
    catch ( UnsupportedLookAndFeelException e ) {
      System.out.println ("Metal Look & Feel not supported on this platform.\n"
          +"Program Terminated");
      System.exit(0);
    }
    catch ( IllegalAccessException e ) {
      System.out.println ("Metal Look & Feel could not be accessed.\n" 
          + "Program Terminated");
      System.exit(0);
    }
    catch ( ClassNotFoundException e ) {
      System.out.println ("Metal Look & Feel could not found.\n" 
          + "Program Terminated");
      System.exit(0);
    }   
    catch ( InstantiationException e ) {
      System.out.println ("Metal Look & Feel could not be instantiated.\n" 
          + "Program Terminated");
      System.exit(0);
    }
    catch ( Exception e ) {
      System.out.println ("Unexpected error. \nProgram Terminated");
      e.printStackTrace();
      System.exit(0);
    }

    NetEdit netTreeView = new NetEdit();
    netTreeView.setVisible(true);

    NetEdit netTreeView2 = new NetEdit(NetEdit.MODE_VIEW_NET);
    netTreeView2.setVisible(true);
  }

}


