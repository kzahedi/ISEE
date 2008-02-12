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


package brightwell.gui.ranges;

import brightwell.analyser.DataStorage;
import brightwell.analyser.NamedNet;
import brightwell.analyser.NetLoader;

import brightwell.gui.Error;

import cholsey.Net;



import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import util.io.EvoTaskXMLHandler;
import util.io.ExFileFilter;
import util.misc.IseeLogger;
import util.misc.NetEdit;

public class RangesPanel extends JPanel implements ActionListener, ItemListener
{
  private static int MAX_NET_NAME_LENGTH = 15;
  private static Logger log = IseeLogger.getLogger(RangesPanel.class);

  private Net selectedNet = null;

  private NetEdit netEdit = null;

  private EvoTaskXMLHandler xmlHandler = new EvoTaskXMLHandler();

  private Component parent = null;
  
  private JPanel analyseRangePanel  = new JPanel();
  private JPanel windowSize         = new JPanel();
  private JPanel netParameter       = new JPanel();
  private JPanel netLoad            = new JPanel();
  private JPanel iterations         = new JPanel();
  private JPanel learning           = new JPanel();

  // **************************************************************************
  // ranges
  // **************************************************************************

  private JLabel xRangeLabel = new JLabel("x");
  private JLabel yRangeLabel = new JLabel("y");

  private JTextField xMinInput = new JTextField("-1",4);
  private JTextField xMaxInput = new JTextField("1",4);
  private JTextField yMinInput = new JTextField("-1.1",4);
  private JTextField yMaxInput = new JTextField("1.1",4);

  // **************************************************************************
  // window size
  // **************************************************************************
  private JLabel widthLabel = new JLabel("width");
  private JLabel heightLabel = new JLabel("height");
  private JLabel bigPointsCheckBoxLabel = new JLabel("big points");

  private JTextField widthInput  = new JTextField("400",4);
  private JTextField heightInput = new JTextField("320",4);
  private JCheckBox  bigPointsCheckBox = new JCheckBox();


  // **************************************************************************
  // net parameter
  // **************************************************************************

  private JLabel alphaLabel = new JLabel("alpha");
  private JLabel betaLabel  = new JLabel("beta");
  private JLabel gammaLabel = new JLabel("gamma");
  private JLabel deltaLabel = new JLabel("delta");
  private JLabel kappaLabel = new JLabel("kappa");

  private JTextField alphaInput = new JTextField("",4);
  private JTextField betaInput  = new JTextField("",4);
  private JTextField gammaInput = new JTextField("",4);
  private JTextField deltaInput = new JTextField("",4);
  private JTextField kappaInput = new JTextField("",4);

  // **************************************************************************
  // net load
  // **************************************************************************
  private JFileChooser fileDialog = new JFileChooser(".");
  private JLabel netLabel         = new JLabel("neural net");
  private JLabel populationLabel  = new JLabel("population");
  private JLabel generationLabel  = new JLabel("generation");
  private JLabel individualLabel  = new JLabel("individual");

  private JLabel predefinedNetsLabel  = new JLabel("predefined nets");

  private JTextField generationInput    = new JTextField("0",3);
  private JTextField populationInput    = new JTextField("0",3);
  private JTextField individualInput    = new JTextField("0",3);
  private JTextField netInput           = new JTextField("",7);

  private JButton viewNetButton         = new JButton("view");
  private JButton loadNetButton         = new JButton("load");
  private JButton selectNetButton       = new JButton("...");

  private JComboBox predefinedNetsComboBox = new JComboBox();

  private Vector netVector                 = new Vector();

  // **************************************************************************
  // iterations
  // **************************************************************************

  private JLabel convergenceLabel   = new JLabel("convergence");
  private JLabel drawLabel          = new JLabel("draw");
  private JLabel initActLabel       = new JLabel("inital act.");

  private JTextField convergenceInput   = new JTextField("200",4);
  private JTextField drawInput          = new JTextField("20",4);

  private JComboBox initialActivationComboBox = new JComboBox();

  // **************************************************************************
  // learning stuff
  // **************************************************************************
  private JLabel transmitterLabel = new JLabel("Transmitter");
  private JLabel receptorLabel    = new JLabel("Receptor");
  private JLabel synapseLabel     = new JLabel("Synapse");
  private JLabel liapunovLabel     = new JLabel("Liapunov");
  private JTextField yLowerTransmitterInput = new JTextField("-2",4);
  private JTextField yUpperTransmitterInput = new JTextField("10",4);
  private JTextField yLowerReceptorInput    = new JTextField("-2",4);
  private JTextField yUpperReceptorInput    = new JTextField("10",4);
  private JTextField yLowerSynapseInput     = new JTextField("-10",4);
  private JTextField yUpperSynapseInput     = new JTextField("10",4);
  private JTextField yLowerLiapunovInput     = new JTextField("-1",4);
  private JTextField yUpperLiapunovInput     = new JTextField("1",4);



  public RangesPanel(Component parent)
  {
    JTabbedPane tabbedPane = new JTabbedPane();
//    log.setLevel(Level.DEBUG);

    setupPanels();

    tabbedPane.addTab("Ranges", null, analyseRangePanel, "Analysis Range");
    tabbedPane.addTab("Iterations", null, iterations, "Different Iterations");
    tabbedPane.addTab("Window Size", null, windowSize, "Window Size");
    tabbedPane.addTab("Net Load", null, netLoad, "Load a Net Parameter");
    tabbedPane.addTab("R/T/S", null, learning, 
        "Receptors / Transmitter / Synapse");
    tabbedPane.addTab("Net Parameter", null, netParameter, "Global Net Parameter");


    add(tabbedPane);

  }

  private void setupPanels()
  {
    setupAnalyseRangePanel();
    setupWindowSizePanel();
    setupNetParameterPanel();
    setupNetLoadPanel();
    setupIterationsPanel();
    setupLearning();
  }

  private void setupLearning()
  {
    GridBagLayout gbl = new GridBagLayout();
    learning.setLayout(gbl);

    int row = -1;

    row++;
    GridBagConstraints receptorLabelConstraints = 
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints yLowerReceptorInputConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints yUpperReceptorInputConstraints = 
      new GridBagConstraints(2, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(receptorLabel,
        receptorLabelConstraints);
    gbl.setConstraints(yLowerReceptorInput,
        yLowerReceptorInputConstraints);
    gbl.setConstraints(yUpperReceptorInput,
        yUpperReceptorInputConstraints);

    learning.add(receptorLabel);
    learning.add(yLowerReceptorInput);
    learning.add(yUpperReceptorInput);

    row++;
    GridBagConstraints transmitterLabelConstraints = 
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints yLowerTransmitterInputConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints yUpperTransmitterInputConstraints = 
      new GridBagConstraints(2, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(transmitterLabel,
        transmitterLabelConstraints);
    gbl.setConstraints(yLowerTransmitterInput,
        yLowerTransmitterInputConstraints);
    gbl.setConstraints(yUpperTransmitterInput,
        yUpperTransmitterInputConstraints);

    learning.add(transmitterLabel);
    learning.add(yLowerTransmitterInput);
    learning.add(yUpperTransmitterInput);

    row++;
    GridBagConstraints synapseLabelConstraints = 
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints yLowerSynapseInputConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints yUpperSynapseInputConstraints = 
      new GridBagConstraints(2, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(synapseLabel,
        synapseLabelConstraints);
    gbl.setConstraints(yLowerSynapseInput,
        yLowerSynapseInputConstraints);
    gbl.setConstraints(yUpperSynapseInput,
        yUpperSynapseInputConstraints);

    learning.add(synapseLabel);
    learning.add(yLowerSynapseInput);
    learning.add(yUpperSynapseInput);

    row++;
    GridBagConstraints liapunovLabelConstraints = 
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints yLowerLiapunovInputConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints yUpperLiapunovInputConstraints = 
      new GridBagConstraints(2, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(liapunovLabel,
        liapunovLabelConstraints);
    gbl.setConstraints(yLowerLiapunovInput,
        yLowerLiapunovInputConstraints);
    gbl.setConstraints(yUpperLiapunovInput,
        yUpperLiapunovInputConstraints);

    learning.add(liapunovLabel);
    learning.add(yLowerLiapunovInput);
    learning.add(yUpperLiapunovInput);







  }

  private void setupIterationsPanel()
  {

    GridBagLayout gbl = new GridBagLayout();
    iterations.setLayout(gbl);

    int row = 0;

    // width input line
    GridBagConstraints convergenceLabelConstraints =
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints convergenceInputConstraints =
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(convergenceLabel, convergenceLabelConstraints);
    gbl.setConstraints(convergenceInput, convergenceInputConstraints);

    iterations.add(convergenceLabel);
    iterations.add(convergenceInput);


    row++;
    // heigth input line
    GridBagConstraints drawLabelConstraints =
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints drawInputConstraints =
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(drawLabel, drawLabelConstraints);
    gbl.setConstraints(drawInput, drawInputConstraints);

    iterations.add(drawLabel);
    iterations.add(drawInput);

    for(int i=0; i < DataStorage.INITIAL_ACTIVITY_MODES.length; i++)
    {
      initialActivationComboBox.addItem(
          DataStorage.INITIAL_ACTIVITY_MODES[i]);
    }

    // init act
    row++;
    GridBagConstraints initalActLabelConstraints =
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints initialActivationComboBoxConstraints =
      new GridBagConstraints(1, row, 3, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(initActLabel,initalActLabelConstraints);
    gbl.setConstraints(initialActivationComboBox,
        initialActivationComboBoxConstraints);

    iterations.add(initActLabel);
    iterations.add(initialActivationComboBox);


  }

  private void setupNetLoadPanel()
  {

    GridBagLayout gbl = new GridBagLayout();
    netLoad.setLayout(gbl);

    int row = 0;
    //  load net row
    GridBagConstraints netLabelConstraints =
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints netInputConstraints =
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(netLabel,netLabelConstraints);    
    gbl.setConstraints(netInput,netInputConstraints);

    //netLoad.add(netLabel);
    netLoad.add(netInput);

    // select net
    GridBagConstraints selectNetButtonConstraints =
      new GridBagConstraints(2, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(selectNetButton,selectNetButtonConstraints);

    netLoad.add(selectNetButton);


    // population row
    row++;
    GridBagConstraints populationLabelConstraints =
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints populationInputConstraints =
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    // generation row
    row++;
    GridBagConstraints generationLabelConstraints =
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints generationInputConstraints =
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    // individual row
    row++;
    GridBagConstraints individualLabelConstraints =
      new GridBagConstraints(0, row, 1,1,0,0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints individualInputConstraints =
      new GridBagConstraints(1, row, 1,1,0,0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5); 


    gbl.setConstraints(generationLabel,generationLabelConstraints);
    gbl.setConstraints(generationInput,generationInputConstraints);    
    
    gbl.setConstraints(populationLabel,populationLabelConstraints);
    gbl.setConstraints(populationInput,populationInputConstraints);    

    gbl.setConstraints(individualLabel,individualLabelConstraints);
    gbl.setConstraints(individualInput,individualInputConstraints);

//    netLoad.add(generationLabel);
//    netLoad.add(populationLabel);
//    netLoad.add(individualLabel);
//    netLoad.add(generationInput);
//    netLoad.add(populationInput);
//    netLoad.add(individualInput);

    netVector = (new NetLoader()).getNamedNets();

    // predefined nets
    predefinedNetsComboBox.addItem("");
    for(int i=0; i < netVector.size(); i++)
    {
      String name = ((NamedNet)netVector.elementAt(i)).getNetName().trim();
      if(name.length() > MAX_NET_NAME_LENGTH)
      {
        name = name.substring(0,MAX_NET_NAME_LENGTH-1) + "#";

      }
      predefinedNetsComboBox.addItem(name);
    }
    predefinedNetsComboBox.addItem("-- reload nets --");
    predefinedNetsComboBox.setSelectedIndex(0);
    predefinedNetsComboBox.addItemListener(this);

    // predifined nets row
    row++;
    GridBagConstraints predefinedNetsLabelConstraints =
      new GridBagConstraints(0, row, 1,1,0,0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints predefinedNetsComboBoxConstraints =
      new GridBagConstraints(1, row, 2,1,0,0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5); 


    gbl.setConstraints(predefinedNetsLabel, predefinedNetsLabelConstraints);
    gbl.setConstraints(predefinedNetsComboBox,
        predefinedNetsComboBoxConstraints);

    netLoad.add(predefinedNetsComboBox);
 
    row++;
    // load net
    GridBagConstraints viewNetButtonConstraints =
      new GridBagConstraints(2, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(viewNetButton,viewNetButtonConstraints);

    GridBagConstraints loadNetButtonConstraints =
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(loadNetButton,loadNetButtonConstraints);

    netLoad.add(viewNetButton);
    netLoad.add(loadNetButton);


    viewNetButton.addActionListener(this);
    loadNetButton.addActionListener(this);
    selectNetButton.addActionListener(this);



  }


  private void setupNetParameterPanel()
  {
    GridBagLayout gbl = new GridBagLayout();
    netParameter.setLayout(gbl);

    int row = 0;

    // alpha, beta input line
    GridBagConstraints alphaLabelConstraints =
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints alphaInputConstraints =
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(alphaLabel, alphaLabelConstraints);
    gbl.setConstraints(alphaInput, alphaInputConstraints);

    netParameter.add(alphaLabel);
    netParameter.add(alphaInput);

    GridBagConstraints betaLabelConstraints =
      new GridBagConstraints(2, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints betaInputConstraints =
      new GridBagConstraints(3, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(betaLabel, betaLabelConstraints);
    gbl.setConstraints(betaInput, betaInputConstraints);

    netParameter.add(betaLabel);
    netParameter.add(betaInput);

    row++;
    // gamma, beta input line
    GridBagConstraints gammaLabelConstraints =
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints gammaInputConstraints =
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(gammaLabel, gammaLabelConstraints);
    gbl.setConstraints(gammaInput, gammaInputConstraints);

    netParameter.add(gammaLabel);
    netParameter.add(gammaInput);

    GridBagConstraints deltaLabelConstraints =
      new GridBagConstraints(2, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints deltaInputConstraints =
      new GridBagConstraints(3, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(deltaLabel, deltaLabelConstraints);
    gbl.setConstraints(deltaInput, deltaInputConstraints);

    netParameter.add(deltaLabel);
    netParameter.add(deltaInput);

    row++;
    // kappa input line
    GridBagConstraints kappaLabelConstraints =
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints kappaInputConstraints =
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(kappaLabel, kappaLabelConstraints);
    gbl.setConstraints(kappaInput, kappaInputConstraints);

    netParameter.add(kappaLabel);
    netParameter.add(kappaInput);





  }

  private void setupAnalyseRangePanel()
  {
    GridBagLayout gbl = new GridBagLayout();
    analyseRangePanel.setLayout(gbl);

    int row = 0;

    // x input line
    GridBagConstraints xRangeLabelConstraints =
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints xMinInputConstraints =
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints xMaxInputConstraints =
      new GridBagConstraints(2, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(xRangeLabel, xRangeLabelConstraints);
    gbl.setConstraints(xMinInput, xMinInputConstraints);
    gbl.setConstraints(xMaxInput, xMaxInputConstraints);

    analyseRangePanel.add(xRangeLabel);
    analyseRangePanel.add(xMinInput);
    analyseRangePanel.add(xMaxInput);


    row++;
    // x input line
    GridBagConstraints yRangeLabelConstraints =
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints yMinInputConstraints =
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints yMaxInputConstraints =
      new GridBagConstraints(2, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(yRangeLabel, yRangeLabelConstraints);
    gbl.setConstraints(yMinInput, yMinInputConstraints);
    gbl.setConstraints(yMaxInput, yMaxInputConstraints);

    analyseRangePanel.add(yRangeLabel);
    analyseRangePanel.add(yMinInput);
    analyseRangePanel.add(yMaxInput);


  }
  
  private void setupWindowSizePanel()
  {
    GridBagLayout gbl = new GridBagLayout();
    windowSize.setLayout(gbl);

    int row = 0;
    // width input line
    GridBagConstraints widthLabelConstraints =
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints widthInputConstraints =
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(widthLabel, widthLabelConstraints);
    gbl.setConstraints(widthInput, widthInputConstraints);

    windowSize.add(widthLabel);
    windowSize.add(widthInput);


    row++;
    // heigth input line
    GridBagConstraints heightLabelConstraints =
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints heightInputConstraints =
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(heightLabel, heightLabelConstraints);
    gbl.setConstraints(heightInput, heightInputConstraints);

    windowSize.add(heightLabel);
    windowSize.add(heightInput);

    row++;
    // big points 
    GridBagConstraints bigPointsCheckBoxLabelConstraints =
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints bigPointsCheckBoxConstraints =
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    gbl.setConstraints(bigPointsCheckBoxLabel,
        bigPointsCheckBoxLabelConstraints);

    gbl.setConstraints(bigPointsCheckBox,
        bigPointsCheckBoxConstraints);

    windowSize.add(bigPointsCheckBoxLabel);
    windowSize.add(bigPointsCheckBox);

    bigPointsCheckBox.setSelected(false);


  }


  public void itemStateChanged(ItemEvent event)
  {
    log.debug("itemStateChanged()");
    if(event.getSource() == predefinedNetsComboBox)
    {
      log.debug("predefinedNetsComboBox.getSelectedIndex() = " 
          + predefinedNetsComboBox.getSelectedIndex());
      if(predefinedNetsComboBox.getSelectedIndex() ==
          predefinedNetsComboBox.getItemCount() - 1)
      {
        reloadPredefinedNets();
        return;
      }
      selectPredefinedNet();
      if(netEdit != null && netEdit.isVisible())
      {
        displayNet();
      }
    }
  }

  public void actionPerformed(ActionEvent event)
  {

    if(event.getSource() == selectNetButton)
    {
      selectNet();
    }

    if(event.getSource() == loadNetButton)
    {
      loadTheNet();
      updateNetView();
    }

    if(event.getSource() == viewNetButton)
    {
      displayNet();
    }
  }

  private void selectPredefinedNet()
  {
    if(predefinedNetsComboBox.getSelectedIndex() == 0)
    {
      selectedNet = null;
      return;
    }
    // predefined net selected. -1 because first index is empty
      selectedNet =
        ((Net)netVector.elementAt(predefinedNetsComboBox.getSelectedIndex()-1));
  }

  private void selectNet()
  {
    fileDialog.addChoosableFileFilter(
        new ExFileFilter("xml","Neural Net ( .xml)"));
    int returnVal = fileDialog.showOpenDialog(this);
    if(returnVal == JFileChooser.APPROVE_OPTION) {
      netInput.setText(fileDialog.getCurrentDirectory() 
          + System.getProperty("file.separator")
          + fileDialog.getSelectedFile().getName());
      loadTheNet();
      if(netEdit != null && netEdit.isVisible())
      {
        updateNetView();
      }
    }

  }


  private void loadTheNet()
  {

    String filename = netInput.getText().trim();
    if(filename.equals(""))
    {
      Error.netLoadError(parent);
      return;
    }

    selectedNet = xmlHandler.readNetFromFile(filename,0,0,0);

  }

  private void displayNet()
  {
    boolean noNetSelected =  
      (predefinedNetsComboBox.getSelectedIndex() == 0 &&
       netInput.getText().equals(""));


    if(noNetSelected)
    {
      selectedNet = new Net();
    }
    else if (predefinedNetsComboBox.getSelectedIndex() > 0)
    {
      selectPredefinedNet();
    }
    else
    {
      loadTheNet();
    }
    updateNetView();

  }


  private void updateNetView()
  {
    if(selectedNet == null)
    {
      return;
    }

    if(netEdit != null)
    {
      netEdit.setVisible(false);
    }

    netEdit = new NetEdit();
    netEdit.setNet(selectedNet);
    netEdit.setVisible(true);
    netEdit.pack();
  }

  public DataStorage getData(boolean checkForNet)
  {
    DataStorage dataStorage = new DataStorage();

    if(checkForNet && selectedNet == null)
    {
      Error.noNetLoaded(parent);
      return null;
    }

    dataStorage.setNet(selectedNet);
    
    dataStorage.setWindowSize(
        Integer.parseInt(widthInput.getText().trim()),
        Integer.parseInt(heightInput.getText().trim()));

    dataStorage.setXRange(
        Double.parseDouble(xMinInput.getText().trim()),
        Double.parseDouble(xMaxInput.getText().trim()));

    dataStorage.setYRange(
        Double.parseDouble(yMinInput.getText().trim()),
        Double.parseDouble(yMaxInput.getText().trim()));

    dataStorage.setTransmitterRange(
        Double.parseDouble(yLowerTransmitterInput.getText().trim()),
        Double.parseDouble(yUpperTransmitterInput.getText().trim()));

    dataStorage.setReceptorRange(
        Double.parseDouble(yLowerReceptorInput.getText().trim()),
        Double.parseDouble(yUpperReceptorInput.getText().trim()));

    dataStorage.setSynapseRange(
        Double.parseDouble(yLowerSynapseInput.getText().trim()),
        Double.parseDouble(yUpperSynapseInput.getText().trim()));

    dataStorage.setLiapunovRange(
        Double.parseDouble(yLowerLiapunovInput.getText().trim()),
        Double.parseDouble(yUpperLiapunovInput.getText().trim()));


    dataStorage.setConvergenceIterations(
        Integer.parseInt(convergenceInput.getText().trim()));

    dataStorage.setDrawIterations(
        Integer.parseInt(drawInput.getText().trim()));

    dataStorage.setInialActivityMode(
        initialActivationComboBox.getSelectedIndex());

    dataStorage.setUseBigPoints(
        bigPointsCheckBox.isSelected());

    // *************************************************************************
    // net paramter
    // *************************************************************************

    if(checkForNet)
    {
      if(!alphaInput.getText().trim().equals(""))
      {
        selectedNet.setAlpha(Double.parseDouble(alphaInput.getText().trim()));
      }

      if(!betaInput.getText().trim().equals(""))
      {
        selectedNet.setBeta(Double.parseDouble(betaInput.getText().trim()));
      }

      if(!gammaInput.getText().trim().equals(""))
      {
        selectedNet.setGamma(Double.parseDouble(gammaInput.getText().trim()));
      }

      if(!deltaInput.getText().trim().equals(""))
      {
        selectedNet.setDelta(Double.parseDouble(deltaInput.getText().trim()));
      }

      if(!kappaInput.getText().trim().equals(""))
      {
        selectedNet.setKappa(Double.parseDouble(kappaInput.getText().trim()));
      }
    }

    //log.debug("\n"+dataStorage.toString());

    return dataStorage;
  }

  private void reloadPredefinedNets()
  {
    log.info("reload nets");
    predefinedNetsComboBox.removeItemListener(this);
    predefinedNetsComboBox.removeAllItems();
    netVector = (new NetLoader()).getNamedNets();
    // predefined nets
    predefinedNetsComboBox.addItem("");
    for(int i=0; i < netVector.size(); i++)
    {
      predefinedNetsComboBox.addItem(
          ((NamedNet)netVector.elementAt(i)).getNetName());
    }
    predefinedNetsComboBox.addItem("-- reload nets --");
    predefinedNetsComboBox.setSelectedIndex(0);
    predefinedNetsComboBox.addItemListener(this);
    log.info("done");
  }

}
