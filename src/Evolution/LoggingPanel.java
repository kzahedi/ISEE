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
import java.io.File;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class LoggingPanel implements ActionListener{
  private JPanel       panel = new JPanel(false);
  private Evolution    evo = null;
  private JFileChooser myFileChooser = new JFileChooser(".");    

  private JTextField   bestNetsFileNameInp   = new JTextField(10);
  private JPanel       selectNetsFileButton  = new JPanel();
  private ImageIcon    openIcon = new ImageIcon(
      Toolkit.getDefaultToolkit().getImage(
            LoggingPanel.class.getResource("images/Open24.gif")));
  private JButton      bestNetsFileSelButton = new JButton("open", openIcon);

  private JLabel     countBest    = new JLabel(" save best n = ");
  private JTextField countBestInp = new JTextField(6);


  public LoggingPanel(Evolution evo){
    this.evo   = evo;

    /* give the population this control panel for logging data */
    this.evo.setCtrlLoggingPanel(this);


    GridBagLayout      layout = new GridBagLayout();
    GridBagConstraints c      = new GridBagConstraints();
    this.panel.setLayout(layout);


    this.panel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder("logging data"),
          BorderFactory.createEmptyBorder(5,5,5,5)));


    /* looging file  */
    bestNetsFileNameInp.setEditable(false);

    bestNetsFileSelButton.addActionListener(this);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 0;

    Date date = new Date(System.currentTimeMillis());
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    String year = "" + calendar.get(calendar.YEAR);
    String month = ((calendar.get(calendar.MONTH)+1) < 10 ? 
        "0" + (calendar.get(calendar.MONTH)+1) :
        "" + (calendar.get(calendar.MONTH)+1));
    String day = ((calendar.get(calendar.DAY_OF_MONTH)) < 10 ? 
        "0" + (calendar.get(calendar.DAY_OF_MONTH)) :
        "" + (calendar.get(calendar.DAY_OF_MONTH)));
    String hour = ((calendar.get(calendar.HOUR_OF_DAY)) < 10 ? 
        "0" + (calendar.get(calendar.HOUR_OF_DAY)) :
        "" + (calendar.get(calendar.HOUR_OF_DAY)));
    String minute = ((calendar.get(calendar.MINUTE)) < 10 ? 
        "0" + (calendar.get(calendar.MINUTE)) :
        "" + (calendar.get(calendar.MINUTE)));

    this.evo.setBestNetsFile(
        new File("evotask" 
          + "_" 
          + year
          + month
          + day
          + "_"
          + hour
          + minute
          + ".xml"));
    bestNetsFileNameInp.setText(this.evo.getBestNetsFile().getPath());


    layout.setConstraints(bestNetsFileNameInp,c);
    this.panel.add(bestNetsFileNameInp);

    selectNetsFileButton.add(bestNetsFileSelButton);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 0;
    layout.setConstraints(selectNetsFileButton,c);
    this.panel.add(selectNetsFileButton);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 1;
    layout.setConstraints(countBest,c);
    this.panel.add(countBest);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 1;
    countBestInp.addActionListener(this);
    countBestInp.setText(String.valueOf(evo.getCountBest()));
    layout.setConstraints(countBestInp,c);
    this.panel.add(countBestInp);
  }


  public JPanel getPanel(){
    return this.panel;
  }


  public void actionPerformed(ActionEvent e){
    double dblVal;
    int    intVal;
    File selectedFile = null;
    int returnVal;
    int n;

    if(e.getSource() == bestNetsFileSelButton)
    {
      returnVal = myFileChooser.showOpenDialog(this.panel);
      if (returnVal == JFileChooser.APPROVE_OPTION) 
      {
        selectedFile = myFileChooser.getSelectedFile();
        this.evo.setBestNetsFile(selectedFile);
        bestNetsFileNameInp.setText(this.evo.getBestNetsFile().getAbsolutePath());
      }    
      else 
      {
        bestNetsFileNameInp.setText("no file selected");
      }

      // test selected file 
      if(selectedFile.exists())
      { 
        JOptionPane.showMessageDialog(this.panel,
            "selected file already exists,\n"+
            "it is going to be overwritten\n"+
            "when next run will be started\n ",
            "About",
            JOptionPane.INFORMATION_MESSAGE);

      } 
    }




    if(e.getSource() ==  countBestInp)
    {
      intVal = Integer.parseInt(countBestInp.getText());
      this.evo.setCountBest(intVal);
      countBestInp.setText(Integer.toString(this.evo.getCountBest()));

      System.out.println(" : save n best, n =  " + 
          Double.toString(this.evo.getCountBest()) );

    }



    if((e.getSource() ==  this.evo) && (e.getActionCommand() == "INIT"))
    {
      bestNetsFileSelButton.setEnabled(false);
    }


    if((e.getSource() ==  this.evo) && (e.getActionCommand() == "RESET"))
    {
      bestNetsFileSelButton.setEnabled(true);
    }


  }


}












