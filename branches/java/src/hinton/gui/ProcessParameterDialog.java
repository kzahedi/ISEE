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

package hinton.gui;

import hinton.executive.ProcessParameter;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class ProcessParameterDialog extends JFrame implements ActionListener
{

  private JTextField cyclesInput = new JTextField("",10);
  private JTextField warmUpStepsInput = new JTextField("",10);
  private JTextField iterationsInput = new JTextField("",10);
  private JTextField initIterationsInput = new JTextField("",10);
  private JTextField triesInput = new JTextField("",10);
  private JTextField c1Input = new JTextField("",10);
  private JTextField c2Input = new JTextField("",10);
  private JTextField c3Input = new JTextField("",10);
  private JTextField c4Input = new JTextField("",10);
  private JTextField maxSpeedInput = new JTextField("",10);
  private JComboBox displayInput = new JComboBox();

  private JLabel cyclesLabel = new JLabel("cycles");
  private JLabel warmUpStepsLabel = new JLabel("warmUpSteps");
  private JLabel iterationsLabel = new JLabel("iterations");
  private JLabel initIterationsLabel = new JLabel("initial iterations");
  private JLabel triesLabel = new JLabel("tries");
  private JLabel c1Label = new JLabel("c1");
  private JLabel c2Label = new JLabel("c2");
  private JLabel c3Label = new JLabel("c3");
  private JLabel c4Label = new JLabel("c4");
  private JLabel maxSpeedLabel = new JLabel("maxSpeed");
  private JLabel displayLabel = new JLabel("display");
  private JLabel randomSeedLabel = new JLabel("randomSeed");

  private JButton readButton = new JButton("read");
  private JButton writeButton = new JButton("write");
  private JButton closeButton = new JButton("close");

 
  private JPanel           rootPanel                = new JPanel();
  private ProcessParameter processParameter = null;

  public ProcessParameterDialog(ProcessParameter processParameter)
  {
    // **************************************************************************
    // general stuff
    // **************************************************************************
    super("ProcessParameter");
    /*
    this.addWindowListener(new WindowAdapter() { public void
        windowClosing(WindowEvent e) {  }});
        */

    this.processParameter = processParameter;
    setPanel(rootPanel);
    updatePanel();
    getContentPane().add(rootPanel);
    setSize(450,650);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); 
    Dimension frameSize = getSize();
    int x = (int)(screenSize.getWidth()/2.0d);
    int y  = (int)(screenSize.getHeight()/2.0d);
    x = x - (int)(frameSize.getWidth()/2.0d);
    y = y - (int)(frameSize.getHeight()/2.0d);
    setLocation(x,y);

    readButton.addActionListener(this);
    writeButton.addActionListener(this);
    closeButton.addActionListener(this);

  }


  public void actionPerformed(ActionEvent e)
  {

    if(e.getSource() == readButton)
    {
      updatePanel();
      return;
    }

    if(e.getSource() == writeButton)
    {
      updateProcessParameter();
      return;
    }

    if(e.getSource() == closeButton)
    {
      setVisible(false);
      return;
    }
  }


  private void updateProcessParameter()
  {
    processParameter.setCycles(Integer.parseInt(cyclesInput.getText()));
    processParameter.setWarmUpSteps(Integer.parseInt(warmUpStepsInput.getText()));
    processParameter.setIterations(Integer.parseInt(iterationsInput.getText()));
    processParameter.setInitialIterations(
        Integer.parseInt(initIterationsInput.getText()));
    processParameter.setTries(Integer.parseInt(triesInput.getText()));
    processParameter.setConstants(Double.parseDouble(c1Input.getText()),
        Double.parseDouble(c2Input.getText()),
        Double.parseDouble(c3Input.getText()),
        Double.parseDouble(c4Input.getText()));
    processParameter.setMaxSpeed(Double.parseDouble(maxSpeedInput.getText()));
    processParameter.setDisplay(displayInput.getSelectedIndex()==0?true:false);
  }

  public void updatePanel()
  {
    cyclesInput.setText(""+processParameter.cycles());
    warmUpStepsInput.setText(""+processParameter.warmUpSteps());
    iterationsInput.setText(""+processParameter.iterations());
    initIterationsInput.setText(""+processParameter.getInitialIterations());
    triesInput.setText(""+processParameter.tries());
    c1Input.setText(""+processParameter.getConstant(0));
    c2Input.setText(""+processParameter.getConstant(1));
    c3Input.setText(""+processParameter.getConstant(2));
    c4Input.setText(""+processParameter.getConstant(3));
    maxSpeedInput.setText(""+processParameter.getMaxSpeed());
    displayInput.setSelectedIndex( (processParameter.getDisplay())?0:1);
  }

  private void savePanel()
  {

  }

  private void setPanel(JPanel panel)
  {
    GridBagLayout gbl = new GridBagLayout();

    displayInput.addItem("true");
    displayInput.addItem("false");

    int row = -1;
    // cycles line
    GridBagConstraints cyclesLabelConstraints = 
      new GridBagConstraints(0, ++row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
    GridBagConstraints cyclesInputConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    // warm up steps
    GridBagConstraints warmUpStepsLabelConstraints = 
      new GridBagConstraints(0, ++row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
    GridBagConstraints warmUpStepsInputConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    // iterations
    GridBagConstraints iterationsLabelConstraints = 
      new GridBagConstraints(0, ++row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
    GridBagConstraints iterationsInputConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    // initIterations
    GridBagConstraints initIterationsLabelConstraints = 
      new GridBagConstraints(0, ++row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
    GridBagConstraints initIterationsInputConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);


    // tries line
    GridBagConstraints triesLabelConstraints = 
      new GridBagConstraints(0, ++row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
    GridBagConstraints triesInputConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    // maxSpeed line
    GridBagConstraints maxSpeedLabelConstraints = 
      new GridBagConstraints(0, ++row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
    GridBagConstraints maxSpeedInputConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);


    // c1 line
    GridBagConstraints c1LabelConstraints = 
      new GridBagConstraints(0, ++row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
    GridBagConstraints c1InputConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);


    // c2 line
    GridBagConstraints c2LabelConstraints = 
      new GridBagConstraints(0, ++row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
    GridBagConstraints c2InputConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);


    // c3 line
    GridBagConstraints c3LabelConstraints = 
      new GridBagConstraints(0, ++row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
    GridBagConstraints c3InputConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);


    // c4 line
    GridBagConstraints c4LabelConstraints = 
      new GridBagConstraints(0, ++row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
    GridBagConstraints c4InputConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);


    // display line
    GridBagConstraints displayLabelConstraints = 
      new GridBagConstraints(0, ++row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
    GridBagConstraints displayInputConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    // read write line
    GridBagConstraints readButtonConstraints = 
      new GridBagConstraints(0, ++row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);
    GridBagConstraints writeButtonConstraints = 
      new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    // close line
    GridBagConstraints closeButtonConstraints = 
      new GridBagConstraints(1, ++row, 1, 1, 0, 0, GridBagConstraints.CENTER,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);


    gbl.setConstraints(cyclesLabel,cyclesLabelConstraints);
    gbl.setConstraints(cyclesInput,cyclesInputConstraints);

    gbl.setConstraints(warmUpStepsLabel,warmUpStepsLabelConstraints);
    gbl.setConstraints(warmUpStepsInput,warmUpStepsInputConstraints);

    gbl.setConstraints(iterationsLabel,iterationsLabelConstraints);
    gbl.setConstraints(iterationsInput,iterationsInputConstraints);

    gbl.setConstraints(initIterationsLabel,initIterationsLabelConstraints);
    gbl.setConstraints(initIterationsInput,initIterationsInputConstraints);

    gbl.setConstraints(triesLabel,triesLabelConstraints);
    gbl.setConstraints(triesInput,triesInputConstraints);

    gbl.setConstraints(c1Label,c1LabelConstraints);
    gbl.setConstraints(c1Input,c1InputConstraints);

    gbl.setConstraints(c2Label,c2LabelConstraints);
    gbl.setConstraints(c2Input,c2InputConstraints);

    gbl.setConstraints(c3Label,c3LabelConstraints);
    gbl.setConstraints(c3Input,c3InputConstraints);

    gbl.setConstraints(c4Label,c4LabelConstraints);
    gbl.setConstraints(c4Input,c4InputConstraints);

    gbl.setConstraints(maxSpeedLabel,maxSpeedLabelConstraints);
    gbl.setConstraints(maxSpeedInput,maxSpeedInputConstraints);

    gbl.setConstraints(displayLabel,displayLabelConstraints);
    gbl.setConstraints(displayInput,displayInputConstraints);

    gbl.setConstraints(readButton,readButtonConstraints);
    gbl.setConstraints(writeButton,writeButtonConstraints);

    gbl.setConstraints(closeButton,closeButtonConstraints);

    panel.add(cyclesInput);
    panel.add(cyclesLabel);

    panel.add(warmUpStepsInput);
    panel.add(warmUpStepsLabel);

    panel.add(iterationsInput);
    panel.add(iterationsLabel);

    panel.add(initIterationsInput);
    panel.add(initIterationsLabel);

    panel.add(iterationsInput);
    panel.add(iterationsLabel);

    panel.add(triesInput);
    panel.add(triesLabel);
 
    panel.add(c1Input);
    panel.add(c1Label);
 
    panel.add(c2Input);
    panel.add(c2Label);
 
    panel.add(c3Input);
    panel.add(c3Label);
 
    panel.add(c4Input);
    panel.add(c4Label);
    
    panel.add(maxSpeedInput);
    panel.add(maxSpeedLabel);
    
    panel.add(displayInput);
    panel.add(displayLabel);
    
    panel.add(readButton);
    panel.add(writeButton);

    panel.add(closeButton);

    panel.setLayout(gbl);
  }
}


