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


package Evolution.Pole;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import util.io.EvoTaskXMLHandler;
import util.net.INetCommunication;

import Evolution.XmlFilter;
import cholsey.Net;




public class PoleClientPanel implements ActionListener{
  private Pole        pole          = null;
  private JPanel      panel         = new JPanel(false);


  private JLabel     portLab       = new JLabel("call for port: ");
  private JTextField portLabInp    = new JTextField(8);
  private JLabel     addressLab    = new JLabel("on machine: ");
  private JTextField addressLabInp = new JTextField(12);

  private JButton   openButton        = new JButton("open");
  private JButton   closeButton       = new JButton("close");

  private JLabel     pause = null;

  /* communication */
  private int               port      = 7000;
  private String            address   = "localhost";
  private INetCommunication client    = null; 
  private BufferedReader    evoOutput = null;
  private PrintWriter       evoInput  = null;


  /* file utils */
  private JLabel       netFileName            = new JLabel("test XML-file: ");
  private JTextField   netFileNameInp         = new JTextField(12);
  private JPanel       selectFileButtonLabel  = new JPanel();
  private ImageIcon    openIcon               = new ImageIcon(
      Toolkit.getDefaultToolkit().getImage(
        PoleClientPanel.class.getResource("images/open.gif")));

  private JButton      netFileSelButton       = new JButton("", openIcon);
  private JLabel       initialPopGenNmb       = new JLabel("pop.&gen.&rank:");
  private JTextField   initialGenNmbInp       = new JTextField(3);
  private JTextField   initialPopNmbInp       = new JTextField(3);
  private JTextField   initialRankNmbInp      = new JTextField(3);

  private int genNmb = 0;
  private int popNmb = 0;
  private int rankNmb = 0;


  /* evo simulization */
  JLabel       showLabel  = new JLabel("show all ");
  JRadioButton allButton  = new JRadioButton(" or only best ");    
  JRadioButton bestButton = new JRadioButton(" net ");



  /* test button */
  private JButton   testButton        = new JButton("test");


  public PoleClientPanel(Pole p){
    this.pole = p;


    GridBagLayout      layout = new GridBagLayout();
    GridBagConstraints c      = new GridBagConstraints();
    this.panel.setLayout(layout);

    this.panel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder("client setup and test controller"),
          BorderFactory.createEmptyBorder(5,5,5,5)));


    /* client setup - port */
    c.anchor = GridBagConstraints.WEST;
    c.gridx = 0;
    c.gridy = 0;
    layout.setConstraints(portLab,c);
    panel.add(portLab);

    c.gridx = 1;
    c.gridy = 0;
    c.gridwidth = 2;
    portLabInp.setText(String.valueOf(this.getPort()));
    layout.setConstraints(portLabInp,c);
    portLabInp.addActionListener(this);
    panel.add(portLabInp);

    /* space */
    pause = new JLabel(" ");
    c.gridx = 3;
    c.gridy = 0;
    layout.setConstraints(pause,c);
    panel.add(pause);


    /* open port */
    c.gridx = 4;
    c.gridy = 0;
    layout.setConstraints(openButton, c);
    openButton.addActionListener(this);
    panel.add(openButton);


    /* client setup - addresse */
    c.anchor = GridBagConstraints.WEST;
    c.gridx = 0;
    c.gridy = 1;
    layout.setConstraints(addressLab,c);
    panel.add(addressLab);

    c.gridx = 1;
    c.gridy = 1;
    c.gridwidth = 2;
    addressLabInp.setText(this.getAddress());
    layout.setConstraints(addressLabInp,c);
    addressLabInp.addActionListener(this);
    panel.add(addressLabInp);

    /* space */
    pause = new JLabel(" ");
    c.gridx = 3;
    c.gridy = 1;
    layout.setConstraints(pause,c);
    panel.add(pause);

    /* close port */
    c.gridx = 4;
    c.gridy = 1;
    closeButton.setEnabled(false);
    layout.setConstraints(closeButton, c);
    closeButton.addActionListener(this);
    panel.add(closeButton);

    /* horizontel space */
    pause = new JLabel("   ");
    c.gridx = 0;
    c.gridy = 2;
    layout.setConstraints(pause,c);
    panel.add(pause);


    /* xml file */
    c.anchor = GridBagConstraints.WEST;
    c.gridx = 0;
    c.gridy = 3;
    layout.setConstraints(netFileName,c);
    panel.add(netFileName);

    netFileNameInp.setEditable(false);
    c.anchor = GridBagConstraints.EAST;
    c.gridx = 1;
    c.gridy = 3;
    c.gridwidth = 2;
    layout.setConstraints(netFileNameInp,c);
    panel.add(netFileNameInp);

    netFileSelButton.addActionListener(this);
    selectFileButtonLabel.add(netFileSelButton);
    c.gridx = 3;
    c.gridy = 3;
    layout.setConstraints(selectFileButtonLabel,c);
    panel.add(selectFileButtonLabel);


    c.anchor = GridBagConstraints.WEST;
    c.gridx = 0;
    c.gridy = 4;
    layout.setConstraints(initialPopGenNmb,c);
    panel.add(initialPopGenNmb);

    c.anchor = GridBagConstraints.CENTER;
    c.gridx = 1;
    c.gridy = 4;
    initialPopNmbInp.addActionListener(this);
    initialPopNmbInp.setText(String.valueOf(this.popNmb));
    layout.setConstraints(initialPopNmbInp,c);
    panel.add(initialPopNmbInp);

    c.anchor = GridBagConstraints.EAST;
    c.gridx = 2;
    c.gridy = 4;
    initialGenNmbInp.addActionListener(this);
    initialGenNmbInp.setText(String.valueOf(this.genNmb));
    layout.setConstraints(initialGenNmbInp,c);
    panel.add(initialGenNmbInp);


    c.anchor = GridBagConstraints.EAST;
    c.gridx = 3;
    c.gridy = 4;
    initialRankNmbInp.addActionListener(this);
    initialRankNmbInp.setText(String.valueOf(this.rankNmb));
    layout.setConstraints(initialRankNmbInp,c);
    panel.add(initialRankNmbInp);


    /* test */
    c.gridx = 4;
    c.gridy = 4;
    layout.setConstraints(testButton, c);
    testButton.addActionListener(this);
    panel.add(testButton);


    /* show all or only the best */
    ButtonGroup radio = new ButtonGroup();
    radio.add(allButton);
    radio.add(bestButton);
    allButton.addActionListener(this);
    bestButton.addActionListener(this);

    bestButton.setSelected(true);
    pole.setPlotMode(pole.SHOW_BEST);

    c.anchor = GridBagConstraints.CENTER;
    c.gridx = 0;
    c.gridy = 5;
    layout.setConstraints(showLabel,c);
    panel.add(showLabel);

    c.anchor = GridBagConstraints.CENTER;
    c.gridx = 1;
    c.gridy = 5;
    c.gridwidth = 2;
    layout.setConstraints(allButton,c);
    panel.add(allButton);

    c.anchor = GridBagConstraints.EAST;
    c.gridx = 3;
    c.gridy = 5;
    layout.setConstraints(bestButton,c);
    panel.add(bestButton);






  }


  public JPanel getPanel(){
    return(this.panel);
  }


  public int getPort(){
    return (this.port);
  }

  public void setPort(int nmb){
    if((nmb < 7000)|| (nmb > 65536) )
    {
      this.port = 7000;
    }
    else
    {
      this.port = nmb;
    }
    return;
  }

  public void setAddress(String s){
    String ns = new String(s);
    this.address = ns;
  }

  public String getAddress(){
    String s = new String(this.address);
    return ( s );
  }

  private boolean openCommunication(){
    client = new INetCommunication(INetCommunication.CLIENT);

    client.setInetAddress(this.getAddress());
    client.setPort(this.getPort());
    client.initConnection();

    if (client.getReader() == null || client.getWriter() == null)
    {
      System.out.println("Reader or Writer not done. exit");
      return false;
    }

    this.evoOutput = new BufferedReader(new InputStreamReader(client.getReader()));
    this.evoInput  = new PrintWriter( new BufferedWriter( new OutputStreamWriter(client.getWriter())),true );
    this.pole.setBufferedReader(this.evoOutput);
    this.pole.setPrintWriter(this.evoInput);
    return true;
  }

  private void closeCommunication(){
    this.evoInput.println("CLOSE");
    client.close();
  }



  public void actionPerformed(ActionEvent e){
    int intVal;
    String strVal = null;

    if(e.getSource() ==  portLabInp)
    {
      intVal = Integer.parseInt(portLabInp.getText());
      this.setPort(intVal);
      portLabInp.setText(Integer.toString(this.getPort()));
    }

    if(e.getSource() == addressLabInp){
      strVal = addressLabInp.getText();
      this.setAddress(strVal);
      addressLabInp.setText((this.getAddress()));
    }

    if(e.getSource() == openButton){
      if(this.openCommunication())
      {
        pole.setMode(pole.EVO);

        openButton.setEnabled(false);
        closeButton.setEnabled(false);
        portLabInp.setEditable(false);
        addressLabInp.setEditable(false);
        new Thread(this.pole).start();

        allButton.setEnabled(false);
        bestButton.setEnabled(false);
        netFileSelButton.setEnabled(false); 
        testButton.setEnabled(false); 
        initialGenNmbInp.setEditable(false);      
        initialPopNmbInp.setEditable(false);      
        initialRankNmbInp.setEditable(false);      

      };
    }

    if((e.getSource() == this.pole) && (e.getActionCommand() == "CLOSE"))
    {
      this.closeCommunication();
      openButton.setEnabled(true);
      closeButton.setEnabled(false);
      portLabInp.setEditable(true);
      addressLabInp.setEditable(true);

      netFileSelButton.setEnabled(true);
      testButton.setEnabled(true);  
      initialGenNmbInp.setEditable(true);      
      initialPopNmbInp.setEditable(true);      
      initialRankNmbInp.setEditable(true);      

      allButton.setEnabled(true);
      bestButton.setEnabled(true);    

    }


    if(e.getSource() == closeButton){
      this.closeCommunication();
      openButton.setEnabled(true);
      closeButton.setEnabled(false);
      portLabInp.setEditable(true);
      addressLabInp.setEditable(true);

      netFileSelButton.setEnabled(true); 
      initialGenNmbInp.setEditable(true);      
      initialPopNmbInp.setEditable(true);

      allButton.setEnabled(true);
      bestButton.setEnabled(true);    

    }

    if(e.getSource() ==  initialGenNmbInp)
    {
      intVal = Integer.parseInt(initialGenNmbInp.getText());
      if(intVal < 0)
      {
        intVal=0;
      }
      this.genNmb = intVal;
      initialGenNmbInp.setText(Integer.toString(this.genNmb));
    }

    if(e.getSource() ==  initialPopNmbInp)
    {
      intVal = Integer.parseInt(initialPopNmbInp.getText());
      if(intVal < 0)
      {
        intVal=0;
      }
      this.popNmb = intVal;
      initialPopNmbInp.setText(Integer.toString(this.popNmb));
    }

    if(e.getSource() ==  initialRankNmbInp)
    {
      intVal = Integer.parseInt(initialRankNmbInp.getText());
      if(intVal < 0)
      {
        intVal=0;
      }
      this.rankNmb = intVal;
      initialRankNmbInp.setText(Integer.toString(this.rankNmb));
    }


    if(e.getSource() == netFileSelButton)
    {
      readNet();
    }



    if( (e.getSource() == testButton) ) {
      EvoTaskXMLHandler xml = new EvoTaskXMLHandler();

      openButton.setEnabled(false);
      closeButton.setEnabled(false);
      portLabInp.setEditable(false);
      testButton.setEnabled(false);
      addressLabInp.setEditable(false);
      netFileSelButton.setEnabled(false); 
      initialGenNmbInp.setEditable(false);      
      initialPopNmbInp.setEditable(false);      
      initialRankNmbInp.setEditable(false);      

      // read new net 
      pole.setTestController(xml.readNetFromFile(netFileNameInp.getText(),this.genNmb,this.popNmb, this.rankNmb));


      pole.setMode(pole.TEST);
      new Thread(this.pole).start();
      // this.pole.testFixedController();

    }

    if(e.getActionCommand() == "TEST_END")
    {
      openButton.setEnabled(true);
      closeButton.setEnabled(false);
      portLabInp.setEditable(true);
      addressLabInp.setEditable(true);
      testButton.setEnabled(true);
      netFileSelButton.setEnabled(true); 
      initialGenNmbInp.setEditable(true);      
      initialPopNmbInp.setEditable(true);      
      initialRankNmbInp.setEditable(true);      
    }

    if(e.getActionCommand() == "END_POP")
    {
      allButton.setEnabled(true);
      bestButton.setEnabled(true);    
    }

    if(e.getActionCommand() == "NEW_GEN")
    {
      allButton.setEnabled(false);
      bestButton.setEnabled(false);    
    }


    if(e.getSource() == allButton)
    {
      pole.setPlotMode(pole.SHOW_ALL);
    }

    if(e.getSource() == bestButton)
    {
      pole.setPlotMode(pole.SHOW_BEST);
    }


  }

  private void readNet(){
    int genNr;
    int popNr;

    JFileChooser myFileChooser = new JFileChooser(".");    
    myFileChooser.addChoosableFileFilter(new XmlFilter());

    int returnVal = myFileChooser.showOpenDialog(this.panel);
    if (returnVal == JFileChooser.APPROVE_OPTION) 
    {
      File initialFile = myFileChooser.getSelectedFile();
      if(initialFile.canRead())
      {
        EvoTaskXMLHandler xml = new EvoTaskXMLHandler();
        Net net;

        net = xml.readNetFromFile(initialFile.getPath(),this.genNmb,this.popNmb, this.rankNmb);

        if(net != null)
        {
          pole.setTestController(net);
          netFileNameInp.setText(initialFile.getPath());
        }
        else
        {
          netFileNameInp.setText("gen. or pop. nmb. wrong");
        }

      }
      else
      {
        netFileNameInp.setText("file corrupt");
      }
    }

  }
}










