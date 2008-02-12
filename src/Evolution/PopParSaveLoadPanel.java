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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class PopParSaveLoadPanel implements ActionListener{
    private JPanel       panel = new JPanel(false);
    private Population   pop = null;
    private JFileChooser myFileChooser = new JFileChooser(".");    

    private JLabel       loadSaveName  = new JLabel(" pop. param. : ");
    private ImageIcon    loadIcon      = new ImageIcon(
      Toolkit.getDefaultToolkit().getImage(
            LoggingPanel.class.getResource("images/Open24.gif")));
    private JButton      loadButton    = new JButton("load", loadIcon);
    private ImageIcon    saveIcon      = new ImageIcon(
      Toolkit.getDefaultToolkit().getImage(
            LoggingPanel.class.getResource("images/Save24.gif")));
    private JButton      saveButton    = new JButton("save", saveIcon);

    private JTextField   evalState = new JTextField(25);
   

    public PopParSaveLoadPanel(Population population){
  this.pop   = population;

    GridBagLayout      layout = new GridBagLayout();
  GridBagConstraints c      = new GridBagConstraints();
  this.panel.setLayout(layout);

  evalState.setText("eval. xxxx-th of xxxx indiv.");
  evalState.setEditable(false);
  c.anchor = GridBagConstraints.WEST;
  c.gridx = 0;
  c.weightx = 0.5;
  c.gridy = 0;
  layout.setConstraints(evalState,c);
  this.panel.add(evalState);



  //c.fill = GridBagConstraints.HORIZONTAL;
  c.anchor = GridBagConstraints.WEST;
  c.gridx = 1;
  c.weightx = 0.5;
  c.gridy = 0;
  layout.setConstraints(loadSaveName,c);
  this.panel.add(loadSaveName);

  //c.fill = GridBagConstraints.HORIZONTAL;
  //c.anchor = GridBagConstraints.WEST;
  c.gridx = 2;
  //c.weightx = 0.5;
  c.gridy = 0;
  layout.setConstraints(loadButton,c);
  this.panel.add(loadButton);
  loadButton.addActionListener(this);

  //c.fill = GridBagConstraints.HORIZONTAL;
  //c.anchor = GridBagConstraints.EAST;
  c.gridx = 3;
  //c.weightx = 0.5;
  c.gridy = 0;
  layout.setConstraints(saveButton,c);
  this.panel.add(saveButton);
  saveButton.addActionListener(this);

  this.pop.setPopParameterLoadSavePanel(this);

    }

    public JPanel getPanel(){
  return this.panel;
    }

    public void actionPerformed(ActionEvent e){
  int returnVal;

  if(e.getSource() == saveButton)
  {
      returnVal = myFileChooser.showSaveDialog(this.panel);
      if (returnVal == JFileChooser.APPROVE_OPTION) 
      {
    File selectedFile = myFileChooser.getSelectedFile();
    writePopParam(selectedFile);
      }    
      else 
      {
      }
      
  }

  if(e.getSource() == loadButton)
  {
      returnVal = myFileChooser.showOpenDialog(this.panel);
      if (returnVal == JFileChooser.APPROVE_OPTION) 
      {
    File selectedFile = myFileChooser.getSelectedFile();
    pop.fillPopParamXml(selectedFile);
      }    
      else 
      {
      }
  }

  if((e.getSource() == pop) && e.getActionCommand() == "INIT")
  {
      //saveButton.setEnabled(false);
      loadButton.setEnabled(false);
  }

  if((e.getSource() == pop) && e.getActionCommand() == "RESET")
  {
      saveButton.setEnabled(true);
      loadButton.setEnabled(true);
  }

  if((e.getSource() == pop) && e.getActionCommand() == "NEW_EVAL")
  {
      evalState.setText("eval. " + Integer.toString(this.pop.getEvalIndex()) + "-th from "
            + Integer.toString(this.pop.getEvalIndies()) + " indiv. of the "
            + Integer.toString(this.pop.getCurrentGenerationNmb()) + " gen.");
  }

  if((e.getSource() == pop) && e.getActionCommand() == "UPDATE")
  {
      evalState.setText("eval. " + Integer.toString(this.pop.getEvalIndex()) + "-th from "
            + Integer.toString(this.pop.getEvalIndies()) + " indiv. of the "
            + Integer.toString(this.pop.getCurrentGenerationNmb()) + " gen.");
  }


  

    }

    private void writePopParam(File file){
  PrintWriter outParam = null;

  try 
  {
      
      outParam = new PrintWriter(
    new OutputStreamWriter(
        new FileOutputStream(file)));
  }
  catch (IOException e)
  {
      e.printStackTrace();
  }

  outParam.print(pop.getPopParamXml());

  if(outParam != null)
  {
      outParam.close();
  }
    }
  

}





