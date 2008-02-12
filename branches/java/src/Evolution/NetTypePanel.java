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


package Evolution;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import cholsey.LearningRuleClassLoader;
import cholsey.LearningRuleInterface;
import cholsey.SynapseMode;
import cholsey.Transferfunction;


public class NetTypePanel implements ActionListener{
  private JPanel panel = new JPanel(false);
  private Population pop = null;
  private boolean editable = true;
  private JFileChooser myFileChooser = new JFileChooser(".");    

  private static LearningRuleClassLoader learningRuleClassLoader = new
    LearningRuleClassLoader();

  JLabel     nmbInpNeuLab    = new JLabel(" input neurons  : ");
  JTextField nmbInpNeuLabInp = new JTextField(6);
  JLabel     nmbOutNeuLab    = new JLabel(" output neurons : ");
  JTextField nmbOutNeuLabInp = new JTextField(6);

  JLabel    transLab      = new JLabel(" transfer fct.: ");
  String[]  transStrings  = { "tanh", "sigmoid"};
  JComboBox transList     = new JComboBox(transStrings);

  JLabel       initialFileName       = new JLabel(" initial XML-file : ");
  JTextField   initialFileNameInp    = new JTextField(12);
  JPanel       selectFileButtonLabel = new JPanel();

  ImageIcon openIcon          = new ImageIcon(
      Toolkit.getDefaultToolkit().getImage(
            LoggingPanel.class.getResource("images/open.gif")));

  JButton   initFileSelButton = new JButton("select file ...", openIcon);

  JLabel       initialPopNmb       = new JLabel(" pop. nr.:    ");
  JLabel       initialGenNmb       = new JLabel(" gen. nr.: ");
  JLabel       initialIdxNmb       = new JLabel(" index nr.: ");
  JTextField   initialGenNmbInp    = new JTextField(5);
  JTextField   initialPopNmbInp    = new JTextField(5);
  JTextField   initialIdxNmbInp    = new JTextField(5);

  JLabel       fileInitLab    = new JLabel(" pop. init. with  : ");
  JRadioButton fileInitButton = new JRadioButton("from file");    
  JRadioButton emptyInitButton = new JRadioButton("empty net or");

  JLabel       netTypeLab                = new JLabel(" net type  : ");
  JRadioButton conventionalNetTypeButton = new JRadioButton("conv. ");    
  JRadioButton dynamicalNetTypeButton    = new JRadioButton("dyn.");

  JLabel       learningRuleLabel         = new JLabel(" rule");
  JComboBox    learningRuleComboBox      = new JComboBox();



  public NetTypePanel(Population population){
    this.pop   = population;
    this.editable = true;

    /* give the population this control panel for net type and structure */
    this.pop.setCtrlNetTypePanel(this);



    GridBagLayout      layout = new GridBagLayout();
    GridBagConstraints c      = new GridBagConstraints();
    this.panel.setLayout(layout);

    panel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder("neuron type and initial structure"),
          BorderFactory.createEmptyBorder(5,5,5,5)));


    for(int i=0; i < learningRuleClassLoader.getClasses().size(); i++)
    {
      learningRuleComboBox.addItem(
          ((LearningRuleInterface)learningRuleClassLoader.getClasses().elementAt(i)).getName());
    }


    /* text and input fields */
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 1;
    layout.setConstraints(nmbInpNeuLab,c);
    panel.add(nmbInpNeuLab);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 1;
    nmbInpNeuLabInp.addActionListener(this);
    nmbInpNeuLabInp.setText(String.valueOf(pop.getNmbInpNeu()));
    layout.setConstraints(nmbInpNeuLabInp,c);
    panel.add(nmbInpNeuLabInp);


    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 2;
    c.gridy = 1;
    layout.setConstraints(nmbOutNeuLab,c);
    panel.add(nmbOutNeuLab);


    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 3;
    c.gridy = 1;
    nmbOutNeuLabInp.addActionListener(this);
    nmbOutNeuLabInp.setText(String.valueOf(pop.getNmbOutNeu()));
    layout.setConstraints(nmbOutNeuLabInp,c);
    panel.add(nmbOutNeuLabInp);

    /* transfer function */
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 4;
    c.gridy = 1;
    layout.setConstraints(transLab,c);
    panel.add(transLab);

    if(pop.getTransferfunction() == Transferfunction.TANH)
    {
      transList.setSelectedIndex(0);      
    }
    else
    {
      transList.setSelectedIndex(1);
    }
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 5;
    c.gridy = 1;
    transList.addActionListener(this);
    layout.setConstraints(transList,c);
    panel.add(transList);

    /* initial structure */
    initialFileNameInp.setEditable(false);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 2;
    layout.setConstraints(initialFileName,c);
    panel.add(initialFileName);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 2;
    initialFileNameInp.setText(pop.getInitialFileName());
    layout.setConstraints(initialFileNameInp,c);
    panel.add(initialFileNameInp);

    initFileSelButton.addActionListener(this);
    selectFileButtonLabel.add(initFileSelButton);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 2;
    c.gridy = 2;
    layout.setConstraints(selectFileButtonLabel,c);
    panel.add(selectFileButtonLabel);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 3;
    c.gridy = 2;
    layout.setConstraints(initialPopNmb,c);
    panel.add(initialPopNmb);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 3;
    c.gridy = 3;
    initialPopNmbInp.addActionListener(this);
    initialPopNmbInp.setText(String.valueOf(pop.getInitialPopNmb()));
    layout.setConstraints(initialPopNmbInp,c);
    panel.add(initialPopNmbInp);


    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 4;
    c.gridy = 2;
    layout.setConstraints(initialGenNmb,c);
    panel.add(initialGenNmb);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 4;
    c.gridy = 3;
    initialGenNmbInp.addActionListener(this);
    initialGenNmbInp.setText(String.valueOf(pop.getInitialGenNmb()));
    layout.setConstraints(initialGenNmbInp,c);
    panel.add(initialGenNmbInp);



    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 5;
    c.gridy = 2;
    layout.setConstraints(initialIdxNmb,c);
    panel.add(initialIdxNmb);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 5;
    c.gridy = 3;
    initialIdxNmbInp.addActionListener(this);
    initialIdxNmbInp.setText(String.valueOf(pop.getInitialIdxNmb()));
    layout.setConstraints(initialIdxNmbInp,c);
    panel.add(initialIdxNmbInp);


    /* radio buttons */
    fileInitButton.setMnemonic(KeyEvent.VK_B);
    emptyInitButton.setMnemonic(KeyEvent.VK_C);
    if(this.pop.fileInitActive())
    {
      fileInitButton.setSelected(true);

      initFileSelButton.setEnabled(true);
      transList.setEnabled(false);

      /* make manual structure setings uneditable */
      nmbInpNeuLabInp.setText("x");
      nmbInpNeuLabInp.setEditable(false);
      nmbOutNeuLabInp.setText("x");
      nmbOutNeuLabInp.setEditable(false);

      /* make file setings editable */
      initialGenNmbInp.setText(String.valueOf(pop.getInitialGenNmb()));
      initialGenNmbInp.setEditable(true);
      initialPopNmbInp.setText(String.valueOf(pop.getInitialPopNmb()));
      initialPopNmbInp.setEditable(true);     
      initialIdxNmbInp.setText(String.valueOf(pop.getInitialIdxNmb()));
      initialIdxNmbInp.setEditable(true);     
    }
    else
    {
      emptyInitButton.setSelected(true);

      initFileSelButton.setEnabled(false);

      transList.setEnabled(true);

      /* make manual setings editable */
      nmbInpNeuLabInp.setText(String.valueOf(pop.getNmbInpNeu()));
      nmbInpNeuLabInp.setEditable(true);
      nmbOutNeuLabInp.setText(String.valueOf(pop.getNmbOutNeu()));
      nmbOutNeuLabInp.setEditable(true);

      /* file setings uneditable  */
      initialFileNameInp.setText("x");
      initialGenNmbInp.setText("x");
      initialGenNmbInp.setEditable(false);
      initialPopNmbInp.setText("x");
      initialPopNmbInp.setEditable(false);
      initialIdxNmbInp.setText("x");
      initialIdxNmbInp.setEditable(false);
    }



    ButtonGroup radio = new ButtonGroup();
    radio.add(emptyInitButton);
    radio.add(fileInitButton);
    emptyInitButton.addActionListener(this);
    fileInitButton.addActionListener(this);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 0;
    layout.setConstraints(fileInitLab,c);
    panel.add(fileInitLab);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 0;
    layout.setConstraints(emptyInitButton,c);
    panel.add(emptyInitButton);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 2;
    c.gridy = 0;
    layout.setConstraints(fileInitButton,c);
    panel.add(fileInitButton);


    /* neuron type */
    ButtonGroup netTypeRadio = new ButtonGroup();
    netTypeRadio.add(conventionalNetTypeButton);
    netTypeRadio.add(dynamicalNetTypeButton);
    conventionalNetTypeButton.addActionListener(this);
    dynamicalNetTypeButton.addActionListener(this);

    if(pop.getSynMode() == SynapseMode.CONVENTIONAL)
    {
      conventionalNetTypeButton.setSelected(true);
    }
    else
    {
      dynamicalNetTypeButton.setSelected(true);
    }

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 6;
    layout.setConstraints(netTypeLab,c);
    panel.add(netTypeLab);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 6;
    layout.setConstraints(conventionalNetTypeButton,c);
    panel.add(conventionalNetTypeButton);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 2;
    c.gridy = 6;
    layout.setConstraints(dynamicalNetTypeButton,c);
    panel.add(dynamicalNetTypeButton);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 7;
    layout.setConstraints(learningRuleLabel,c);
    panel.add(learningRuleLabel);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 7;
    layout.setConstraints(learningRuleComboBox,c);
    panel.add(learningRuleComboBox);
    learningRuleComboBox.addActionListener(this);

  }


  public JPanel getPanel(){
    return this.panel;
  }


  public void actionPerformed(ActionEvent e){
    int intVal;

    if(e.getSource() == learningRuleComboBox)
    {
      pop.setSelectedLearningRule(learningRuleComboBox.getSelectedIndex());
      return;
    }

    if(e.getSource() == initFileSelButton)
    {
      if((this.pop.fileInitActive()) && this.editable)
      {
        myFileChooser.addChoosableFileFilter(new XmlFilter());

        int returnVal = myFileChooser.showOpenDialog(this.panel);
        if (returnVal == JFileChooser.APPROVE_OPTION) 
        {
          File initialFile = myFileChooser.getSelectedFile();
          pop.setInitialFileName(initialFile.getPath());
          if(initialFile.canRead())
          {
            initialFileNameInp.setText(this.pop.getInitialFileName());
            System.out.println("set pop: " + pop.getName() 
                + " initial file to " + pop.getInitialFileName());
          }
          else
          {
            JOptionPane.showMessageDialog(panel,"selected file '"+ initialFile.getPath() + "' is unreadable\nset default file.");
            pop.setInitialFileName("default.xml");
            initialFileNameInp.setText(pop.getInitialFileName());
            System.out.println("set pop: " + pop.getName() 
                + " initial file to " + pop.getInitialFileName());
          }
        } 
        else 
        {
          pop.setInitialFileName("default.xml");
          initialFileNameInp.setText(pop.getInitialFileName());
          System.out.println("set pop: " + pop.getName() 
              + " initial file to " + pop.getInitialFileName());
        }
      }
    }


    if(e.getSource() == initialGenNmbInp)
    {
      intVal = Integer.parseInt(initialGenNmbInp.getText());
      this.pop.setInitialGenNmb(intVal);
      initialGenNmbInp.setText(Integer.toString(this.pop.getInitialGenNmb()));

      System.out.println(pop.getName() + 
          " : set 'initial gen. nmb.' to " + 
          Integer.toString(this.pop.getInitialGenNmb()) );
    }

    if(e.getSource() == initialPopNmbInp)
    {
      intVal = Integer.parseInt(initialPopNmbInp.getText());
      this.pop.setInitialPopNmb(intVal);
      initialPopNmbInp.setText(Integer.toString(this.pop.getInitialPopNmb()));

      System.out.println(pop.getName() + 
          " : set 'initial pop. nmb.' to " + 
          Integer.toString(this.pop.getInitialPopNmb()) );
    }

    if(e.getSource() == initialIdxNmbInp)
    {
      intVal = Integer.parseInt(initialIdxNmbInp.getText());
      this.pop.setInitialIdxNmb(intVal);
      initialIdxNmbInp.setText(Integer.toString(this.pop.getInitialIdxNmb()));

      System.out.println(pop.getName() + 
          " : set 'initial index ' to " + 
          Integer.toString(this.pop.getInitialIdxNmb()) );
    }

    if(e.getSource() ==  nmbInpNeuLabInp)
    {
      intVal = Integer.parseInt(nmbInpNeuLabInp.getText());
      this.pop.setNmbInpNeu(intVal);
      nmbInpNeuLabInp.setText(Integer.toString(this.pop.getNmbInpNeu()));

      System.out.println(pop.getName() + 
          " : set 'mnb. inp. neurons' to " + 
          Integer.toString(this.pop.getNmbInpNeu()) );
    }

    if(e.getSource() ==  nmbOutNeuLabInp)
    {
      intVal = Integer.parseInt(nmbOutNeuLabInp.getText());
      this.pop.setNmbOutNeu(intVal);
      nmbOutNeuLabInp.setText(Integer.toString(this.pop.getNmbOutNeu()));

      System.out.println(pop.getName() + 
          " : set 'mnb. output neurons' to " + 
          Integer.toString(this.pop.getNmbOutNeu()) );
    }


    if(e.getSource() == transList)
    {

      /* let the combo-box and transferfaunction unchanged */
      if(this.pop.fileInitActive())
      {
        if(pop.getTransferfunction() == Transferfunction.TANH)
        {
          transList.setSelectedIndex(0);
        }
        else
        {
          transList.setSelectedIndex(1);
        }
        return;
      }


      /* change transferfunction  */
      if(transList.getSelectedIndex() == 0)
      {
        /* tanh is selected */
        pop.setTransferfunction(Transferfunction.TANH);
        System.out.println(pop.getName() + 
            ": transfer function is now: " +
            (pop.getTransferfunction()).toXML()); 
      }
      else
      {
        /* tanh is selected */
        transList.setSelectedIndex(1);
        pop.setTransferfunction(Transferfunction.SIGM);
        System.out.println(pop.getName() + 
            ": transfer function is now: " +
            (pop.getTransferfunction()).toXML()); 
      }

    }



    if(e.getSource() == emptyInitButton)
    {
      pop.deactivateFileInit();
      emptyInitButton.setSelected(true);

      initFileSelButton.setEnabled(false);

      transList.setEnabled(true);

      /* make manual setings editable */
      nmbInpNeuLabInp.setText(String.valueOf(pop.getNmbInpNeu()));
      nmbInpNeuLabInp.setEditable(true);
      nmbOutNeuLabInp.setText(String.valueOf(pop.getNmbOutNeu()));
      nmbOutNeuLabInp.setEditable(true);

      /* file setings uneditable  */
      initialFileNameInp.setText("no file init");
      //initialGenNmbInp.setText(String.valueOf(pop.getInitialGenNmb()));
      initialGenNmbInp.setText("x");
      initialGenNmbInp.setEditable(false);
      //initialPopNmbInp.setText(String.valueOf(pop.getInitialPopNmb()));
      initialPopNmbInp.setText("x");
      initialPopNmbInp.setEditable(false);
      initialIdxNmbInp.setText("x");
      initialIdxNmbInp.setEditable(false);
    }



    if(e.getSource() == fileInitButton)
    {
      pop.activateFileInit();
      fileInitButton.setSelected(true);

      initFileSelButton.setEnabled(true);

      transList.setEnabled(false);

      /* make manual structure setings uneditable */
      nmbInpNeuLabInp.setText(String.valueOf(pop.getNmbInpNeu()));
      nmbInpNeuLabInp.setEditable(false);
      nmbOutNeuLabInp.setText(String.valueOf(pop.getNmbOutNeu()));
      nmbOutNeuLabInp.setEditable(false);

      /* make file setings editable */
      initialFileNameInp.setText(pop.getInitialFileName());
      initialGenNmbInp.setText(String.valueOf(pop.getInitialGenNmb()));
      initialGenNmbInp.setEditable(true);
      initialPopNmbInp.setText(String.valueOf(pop.getInitialPopNmb()));
      initialPopNmbInp.setEditable(true);     
      initialIdxNmbInp.setText(String.valueOf(pop.getInitialIdxNmb()));
      initialIdxNmbInp.setEditable(true);     

    }

    if(e.getSource() == conventionalNetTypeButton)
    {
      pop.setSynMode(SynapseMode.CONVENTIONAL);
    }

    if(e.getSource() == dynamicalNetTypeButton)
    {
      pop.setSynMode(SynapseMode.DYNAMIC);
    }







    /* update event form the class Population */
    /* some parameters have changed and get visible */
    if((e.getSource() == pop) && (e.getActionCommand() == "INIT")){

      this.editable = false;

      initFileSelButton.setEnabled(false);

      nmbInpNeuLabInp.setText(String.valueOf(pop.getNmbInpNeu()));
      nmbOutNeuLabInp.setText(String.valueOf(pop.getNmbOutNeu()));
      nmbInpNeuLabInp.setEditable(false);
      nmbOutNeuLabInp.setEditable(false);

      if(pop.getTransferfunction() == Transferfunction.TANH)
      {
        transList.setSelectedIndex(0);
      }
      else
      {
        transList.setSelectedIndex(1);
      }
      transList.setEnabled(false);

      initialGenNmbInp.setEditable(false);
      initialPopNmbInp.setEditable(false);
      initialIdxNmbInp.setEditable(false);

      fileInitButton.setEnabled(false);
      emptyInitButton.setEnabled(false);


      /* show new values */
      if(this.pop.fileInitActive())
      {
        /* update  uneditable file seting  parameters */
        initialFileNameInp.setText(pop.getInitialFileName());
        initialGenNmbInp.setText(String.valueOf(pop.getInitialGenNmb()));
        initialPopNmbInp.setText(String.valueOf(pop.getInitialPopNmb()));
        initialIdxNmbInp.setText(String.valueOf(pop.getInitialIdxNmb()));
      }
      else
      {
        /* update  uneditable file seting  parameters */
        if(this.fileInitButton.isSelected() == true)
        {
          emptyInitButton.setSelected(true);
          fileInitButton.setSelected(false);
          initialFileNameInp.setText("no file init possible");
          initialGenNmbInp.setText("x");
          initialPopNmbInp.setText("x");
          initialIdxNmbInp.setText("x");
        }
      }

      if(pop.getSynMode() == SynapseMode.CONVENTIONAL)
      {
        conventionalNetTypeButton.setSelected(true);
      }
      else
      {
        dynamicalNetTypeButton.setSelected(true);
      }
      dynamicalNetTypeButton.setEnabled(false);
      conventionalNetTypeButton.setEnabled(false);


    }


    /* update event form the class Population */
    /* some parameters have changed and get visible */
    if((e.getSource() == pop) && (e.getActionCommand() == "REFRESH")){


      nmbInpNeuLabInp.setText(String.valueOf(pop.getNmbInpNeu()));
      nmbOutNeuLabInp.setText(String.valueOf(pop.getNmbOutNeu()));

      if(pop.getTransferfunction() == Transferfunction.TANH)
      {
        transList.setSelectedIndex(0);
      }
      else
      {
        transList.setSelectedIndex(1);
      }

      if(this.pop.fileInitActive())
      {
        fileInitButton.setSelected(true);

        transList.setEnabled(false);

        /* make manual structure setings uneditable */
        nmbInpNeuLabInp.setEditable(false);
        nmbOutNeuLabInp.setEditable(false);

        /* make file setings editable */
        initFileSelButton.setEnabled(true);
        initialFileNameInp.setText(pop.getInitialFileName());
        initialGenNmbInp.setText(String.valueOf(pop.getInitialGenNmb()));
        initialGenNmbInp.setEditable(true);
        initialPopNmbInp.setText(String.valueOf(pop.getInitialPopNmb()));
        initialPopNmbInp.setEditable(true);     
        initialIdxNmbInp.setText(String.valueOf(pop.getInitialIdxNmb()));
        initialIdxNmbInp.setEditable(true);     

      }
      else
      {
        emptyInitButton.setSelected(true);

        transList.setEnabled(true);

        /* make manual setings editable */
        nmbInpNeuLabInp.setEditable(true);
        nmbOutNeuLabInp.setEditable(true);

        /* file setings uneditable  */
        initFileSelButton.setEnabled(false);
        initialFileNameInp.setText("no file init");
        //initialGenNmbInp.setText(String.valueOf(pop.getInitialGenNmb()));
        initialGenNmbInp.setText("x");
        initialGenNmbInp.setEditable(false);
        //initialPopNmbInp.setText(String.valueOf(pop.getInitialPopNmb()));
        initialPopNmbInp.setText("x");
        initialPopNmbInp.setEditable(false);
        initialIdxNmbInp.setText("x");
        initialIdxNmbInp.setEditable(false);

      }



      if(pop.getSynMode() == SynapseMode.CONVENTIONAL)
      {
        conventionalNetTypeButton.setSelected(true);
      }
      else
      {
        dynamicalNetTypeButton.setSelected(true);
      }
    }





    if((e.getSource() == pop) && (e.getActionCommand() == "RESET")){

      this.editable = true;

      nmbInpNeuLabInp.setText(String.valueOf(pop.getNmbInpNeu()));
      nmbOutNeuLabInp.setText(String.valueOf(pop.getNmbOutNeu()));
      nmbInpNeuLabInp.setEnabled(true);
      nmbOutNeuLabInp.setEnabled(true);

      if(pop.getTransferfunction() == Transferfunction.TANH)
      {
        transList.setSelectedIndex(0);
      }
      else
      {
        transList.setSelectedIndex(1);
      }
      transList.setEnabled(true);

      fileInitButton.setEnabled(true);
      emptyInitButton.setEnabled(true);



      if(this.pop.fileInitActive())
      {
        fileInitButton.setSelected(true);

        transList.setEnabled(false);

        /* make manual structure setings uneditable */
        nmbInpNeuLabInp.setEditable(false);
        nmbOutNeuLabInp.setEditable(false);

        /* make file setings editable */
        initFileSelButton.setEnabled(true);
        initialFileNameInp.setText(pop.getInitialFileName());
        initialGenNmbInp.setText(String.valueOf(pop.getInitialGenNmb()));
        initialGenNmbInp.setEditable(true);
        initialPopNmbInp.setText(String.valueOf(pop.getInitialPopNmb()));
        initialPopNmbInp.setEditable(true);     
        initialIdxNmbInp.setText(String.valueOf(pop.getInitialIdxNmb()));
        initialIdxNmbInp.setEditable(true);     

      }
      else
      {
        emptyInitButton.setSelected(true);

        transList.setEnabled(true);

        /* make manual setings editable */
        nmbInpNeuLabInp.setEditable(true);
        nmbOutNeuLabInp.setEditable(true);

        /* file setings uneditable  */
        initFileSelButton.setEnabled(false);
        initialFileNameInp.setText("no file init");
        //initialGenNmbInp.setText(String.valueOf(pop.getInitialGenNmb()));
        initialGenNmbInp.setText("x");
        initialGenNmbInp.setEditable(false);
        //initialPopNmbInp.setText(String.valueOf(pop.getInitialPopNmb()));
        initialPopNmbInp.setText("x");
        initialPopNmbInp.setEditable(false);
        initialIdxNmbInp.setText("x");
        initialIdxNmbInp.setEditable(false);

      }

      if(pop.getSynMode() == SynapseMode.CONVENTIONAL)
      {
        conventionalNetTypeButton.setSelected(true);
      }
      else
      {
        dynamicalNetTypeButton.setSelected(true);
      }
      dynamicalNetTypeButton.setEnabled(true);
      conventionalNetTypeButton.setEnabled(true);
    }

  }
}















