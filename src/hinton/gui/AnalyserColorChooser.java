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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class AnalyserColorChooser extends JFrame implements ChangeListener,
  ActionListener
{

  private JColorChooser colorChooser = new JColorChooser();

  private Vector inputColors = new Vector();
  private Vector outputColors = new Vector();
  private Vector hiddenColors = new Vector();
  private JPanel neuronPanel = new JPanel();
  private JPanel outputPanel = new JPanel();
  private JPanel hiddenPanel = new JPanel();

  private JComboBox inputColorsComboBox  = new JComboBox();
  private JButton   addInputColorButton  = new JButton("new color");
  private JComboBox outputColorsComboBox = new JComboBox();
  private JButton   addOutputColorButton = new JButton("new color");
  private JComboBox hiddenColorsComboBox = new JComboBox();
  private JButton   addHiddenColorButton = new JButton("new color");
  private JButton   closeButton          = new JButton("close");


  private int selectedGroup = 0;



  public AnalyserColorChooser(Vector inputColors, Vector outputColors, Vector
      hiddenColors)
  {
    super("AnalyserColorChooser");

    JPanel panel = new JPanel();
    GridBagLayout gbl = new GridBagLayout();
    panel.setLayout(new BorderLayout());

    JPanel selectionPanel = new JPanel();

    GridBagConstraints selectionPanelConstraints = 
      new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints colorChooserConstraints = 
      new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);


    gbl.setConstraints(selectionPanel,selectionPanelConstraints);
    gbl.setConstraints(colorChooser,colorChooserConstraints);
    
    panel.add(selectionPanel,BorderLayout.CENTER);
    panel.add(colorChooser, BorderLayout.SOUTH);
    setContentPane(panel);

    setSelectionPanel(selectionPanel);


    colorChooser.getSelectionModel().addChangeListener(this);

    this.inputColors  = inputColors;
    this.outputColors = outputColors;
    this.hiddenColors = hiddenColors;

    addInputColorButton.addActionListener(this);
    addOutputColorButton.addActionListener(this);
    addHiddenColorButton.addActionListener(this);
    closeButton.addActionListener(this);
    inputColorsComboBox.addActionListener(this);
    outputColorsComboBox.addActionListener(this);
    hiddenColorsComboBox.addActionListener(this);


    setSize(getMinimumSize());

  }

  public void updateView()
  {
    setup(neuronPanel);
  }

  public void stateChanged(ChangeEvent e)
  {
    StringContainer s = null;
    if(inputColorsComboBox.hasFocus())
    {
      inputColors.setElementAt(colorChooser.getColor(),
          inputColorsComboBox.getSelectedIndex());
      s = (StringContainer)inputColorsComboBox.getSelectedItem();
      s.setString( "<html> <font color=#" 
          + Integer.toHexString(colorChooser.getColor().getRGB()).substring(2)
          + "> Input Neuron " + inputColorsComboBox.getSelectedIndex() 
          + " Color </font></html>");
      inputColorsComboBox.repaint();
      return;
    }

    if(outputColorsComboBox.hasFocus())
    {
      outputColors.setElementAt(colorChooser.getColor(),
          outputColorsComboBox.getSelectedIndex());
      s = (StringContainer)outputColorsComboBox.getSelectedItem();
      s.setString( "<html> <font color=#" 
          + Integer.toHexString(colorChooser.getColor().getRGB()).substring(2)
          + "> Output Neuron " + outputColorsComboBox.getSelectedIndex() 
          + " Color </font></html>");
      outputColorsComboBox.repaint();
      return;
    }

    if(hiddenColorsComboBox.hasFocus())
    {

      hiddenColors.setElementAt(colorChooser.getColor(),
          hiddenColorsComboBox.getSelectedIndex());
      s = (StringContainer)hiddenColorsComboBox.getSelectedItem();
      s.setString( "<html> <font color=#" 
          + Integer.toHexString(colorChooser.getColor().getRGB()).substring(2)
          + "> Hidden Neuron " + hiddenColorsComboBox.getSelectedIndex() 
          + " Color </font></html>");
      hiddenColorsComboBox.repaint();
      return;
    }

  }


  private void setSelectionPanel(JPanel selectionPanel)
  {
    setup(neuronPanel);
    selectionPanel.add(neuronPanel);
  }

  private void setup(JPanel neuronPanel)
  {

    GridBagLayout gbl = new GridBagLayout();
    int row = 0;

    neuronPanel.removeAll();
    neuronPanel.setLayout(gbl);

    neuronPanel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder("Choose Color"),
          BorderFactory.createEmptyBorder(5,5,5,5)));



    GridBagConstraints inputColorsComboBoxConstraints = 
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints addInputColorButtonConstraints = 
      new GridBagConstraints(1, row++, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints outputColorsComboBoxConstraints = 
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints addOutputColorButtonConstraints = 
      new GridBagConstraints(1, row++, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints hiddenColorsComboBoxConstraints = 
      new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints addHiddenColorButtonConstraints = 
      new GridBagConstraints(1, row++, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);

    GridBagConstraints closeButtonConstraints = 
      new GridBagConstraints(1, row++, 1, 1, 0, 0, GridBagConstraints.WEST,
          GridBagConstraints.NONE, new Insets(5,5,5,5),5,5);



    gbl.setConstraints(inputColorsComboBox,inputColorsComboBoxConstraints);
    gbl.setConstraints(addInputColorButton,addInputColorButtonConstraints);

    gbl.setConstraints(outputColorsComboBox,outputColorsComboBoxConstraints);
    gbl.setConstraints(addOutputColorButton,addOutputColorButtonConstraints);

    gbl.setConstraints(hiddenColorsComboBox,hiddenColorsComboBoxConstraints);
    gbl.setConstraints(addHiddenColorButton,addHiddenColorButtonConstraints);
    gbl.setConstraints(closeButton,closeButtonConstraints);

    if(inputColors != null)
    {
      inputColorsComboBox.removeAllItems();
      for(int i=0;i<inputColors.size();i++)
      {
        Color color = (Color)inputColors.elementAt(i);
        inputColorsComboBox.addItem(new StringContainer("<html> <font color=#" 
            + Integer.toHexString(color.getRGB()).substring(2)
            + "> Input Neuron " + i + " Color </font></html>"));
      }
    }

    neuronPanel.add(inputColorsComboBox);
    neuronPanel.add(addInputColorButton);

    if(outputColors != null)
    {
      outputColorsComboBox.removeAllItems();
      for(int i=0;i<outputColors.size();i++)
      {
        Color color = (Color)outputColors.elementAt(i);
        outputColorsComboBox.addItem(new StringContainer("<html> <font color=#" 
            + Integer.toHexString(color.getRGB()).substring(2)
            + "> Output Neuron " + i + " Color </font></html>"));
      }
    }

    neuronPanel.add(outputColorsComboBox);
    neuronPanel.add(addOutputColorButton);

    if(hiddenColors != null)
    {
      hiddenColorsComboBox.removeAllItems();
      for(int i=0;i<hiddenColors.size();i++)
      {
        Color color = (Color)hiddenColors.elementAt(i);
        hiddenColorsComboBox.addItem(new StringContainer("<html> <font color=#" 
            + Integer.toHexString(color.getRGB()).substring(2)
            + "> Hidden Neuron " + i + " Color </font></html>"));
      }
    }

    neuronPanel.add(hiddenColorsComboBox);
    neuronPanel.add(addHiddenColorButton);

    neuronPanel.add(closeButton);

  }

  public void actionPerformed(ActionEvent e)
  {
    if(e.getSource() == inputColorsComboBox)
    {
      colorChooser.setColor(
          (Color)inputColors.elementAt(inputColorsComboBox.getSelectedIndex()));
      selectedGroup = 0; // input neurons
      return;
    }

    if(e.getSource() == outputColorsComboBox)
    {
      colorChooser.setColor(
          (Color)outputColors.elementAt(outputColorsComboBox.getSelectedIndex()));
      selectedGroup = 1; // output neurons
      return;
    }

    if(e.getSource() == hiddenColorsComboBox)
    {
      colorChooser.setColor(
          (Color)hiddenColors.elementAt(hiddenColorsComboBox.getSelectedIndex()));
      selectedGroup = 2; // output neurons
      return;
    }


    if(e.getSource() == addInputColorButton)
    {
      inputColors.add(Color.blue);
      inputColorsComboBox.addItem(new StringContainer("<html> <font color=#" 
            + Integer.toHexString(Color.blue.getRGB()).substring(2)
            + "> Input Neuron " + (inputColors.size()-1) + " Color </font></html>"));
      inputColorsComboBox.setSelectedIndex(inputColors.size()-1);
      inputColorsComboBox.requestFocus();
      return;
    }

    if(e.getSource() == addOutputColorButton)
    {
      outputColors.add(Color.blue);
      outputColorsComboBox.addItem(new StringContainer("<html> <font color=#" 
            + Integer.toHexString(Color.blue.getRGB()).substring(2)
            + "> Output Neuron " + (outputColors.size()-1) + " Color </font></html>"));
      outputColorsComboBox.setSelectedIndex(outputColors.size()-1);
      outputColorsComboBox.requestFocus();
      return;
    }

    if(e.getSource() == addHiddenColorButton)
    {
      hiddenColors.add(Color.blue);
      hiddenColorsComboBox.addItem(new StringContainer("<html> <font color=#" 
            + Integer.toHexString(Color.blue.getRGB()).substring(2)
            + "> Hidden Neuron " + (hiddenColors.size()-1) + " Color </font></html>"));
      hiddenColorsComboBox.setSelectedIndex(hiddenColors.size()-1);
      hiddenColorsComboBox.requestFocus();
      return;
    }


    if(e.getSource() == closeButton)
    {
      setVisible(false);
    }

  }

  public class StringContainer 
  {
    private String s = null;

    public StringContainer(String s)
    {
      this.s = s;
    }

    public String toString()
    {
      return s;
    }

    public void setString(String s)
    {
      this.s = s;
    }
  }
  
}

