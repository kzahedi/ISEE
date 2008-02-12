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

package addon.tables;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

import util.io.EvoTaskXMLHandler;
import util.io.ExFileFilter;

import addon.netconverter.NetConverter;
import cholsey.LearningRuleClassLoader;
import cholsey.LearningRuleInterface;
import cholsey.Net;
import cholsey.Neuron;
import cholsey.NeuronList;
import cholsey.NeuronType;
import cholsey.ProcessMode;
import cholsey.SynapseMode;
import cholsey.Transferfunction;

/**
 * Class:       NeuralNetTable
 * Description: This Panel shows the interactive neuron-synapses-matrix of a
 *              specified neural net and gives all needed functions. 
 *              Simply add an instance of this Panel to your container and
 *              initialize it with your neural net.
 *
 * @see util.misc.Net
 */
public class NeuralNetTable extends JPanel implements ChangeListener,
    ItemListener, ActionListener, TableModelListener
{
  /** shown text in dynamic-net-mode of an existing synapse */
  public static final String TEXT_DYN_SYNAPSES = "exists";
  /** shown text in dynamic-net-mode of an excitatory synapse */
  public static final String TEXT_DYN_DELETE = "delete";
  /** shown text in dynamic-net-mode of an excitatory synapse */
  public static final String TEXT_DYN_EXCITATORY = "excitatory";
  /** shown text in dynamic-net-mode of an inhibitory synapse */
  public static final String TEXT_DYN_INHIBITORY = "inhibitory";
  /** shown text in dynamic-net-mode of an excitatory synapse (color)*/
  public static final String TEXT_DYN_EXCITATORY_COLOR = 
    "<html><font color=red>excit.</font>";
  /** shown text in dynamic-net-mode of an inhibitory synapse (color)*/
  public static final String TEXT_DYN_INHIBITORY_COLOR = 
    "<html><font color=blue>inhib.</font>";
  /** shown text in conventional-net-mode of an non-existing synapse */
  public static final String TEXT_CON_EMPTY    = "";
  /** shown text in dynamic-net-mode of an non-existing synapse */
  public static final String TEXT_DYN_EMPTY    = "";
  /** shown text of not-allowed synapse (to-input synapses) */
  public static final String TEXT_NA           = "--";
  /** shown text in export filefilter for memory optimzed c export */
  public static final String TEXT_EXPORT_M     = "Low Memory C ( .c )";
  /** shown text in export filefilter for Iconnect-c export */
  public static final String TEXT_EXPORT_C     = "Standard C ( .c )";
  /** shown text in export filefilter for Iconnect-c export */
  public static final String TEXT_EXPORT_IC    = "IConnect C ( .c )";
  /** shown text in export filefilter for ascii export */
  public static final String TEXT_EXPORT_ASCII = "ASCII ( .txt )";
  /** shown text in export filefilter for ysocnet export */
  public static final String TEXT_EXPORT_YSOCTNET = "YSocNet ( .mat )";
  /** shown text in export filefilter for standard gml */
  public static final String TEXT_EXPORT_GML = "GML ( .gml )";
  /** shown text in export filefilter for german team*/
  public static final String TEXT_EXPORT_GERMAN_TEAM = "GERMAN TEAM ( .cpp / .h)";
  /** shown text in export filefilter for brightwell net source file */
  public static final String TEXT_EXPORT_BRIGHTWELL_NET = " NamedNet ( .java )";
  /** shown text in export filefilter for yars controller source file */
  public static final String TEXT_EXPORT_YARS_CONTROLLER = " YARS Controller ( .cpp / .h )";
  /** shown text in all combos for select */
  public static final String TEXT_SELECT       = "-- select --";
  /** shown text in case of error */
  public static final String TEXT_ERROR        = "error";
  
  private NakedNeuralNetTable            table;
  private JComboBox                      synCombo;
  private JScrollPane                    scroll;
  private JButton                        addButton;
  private JButton                        delButton;
  private ArrayList                      menus;
  private JMenu                          editMenu                = new JMenu("Edit");
  private JMenu                          fileMenu                = new JMenu("File");
  private JMenu                          convertMenu             = new JMenu("Convert");
  private JMenu                          transferfunctionMenu    = new JMenu("Transferfunction");
  private JMenu                          learningRuleMenu        = new JMenu("SRN");
  private JMenuItem                      addInputNeuronMenu      = null;
  private JMenuItem                      addOutputNeuronMenu     = null;
  private JMenuItem                      addHiddenNeuronMenu     = null;
  private JMenuItem                      addReadBufferNeuronMenu = null;
  private JMenuItem                      removeNeuronMenu        = null;
  private JMenuItem                      cutSynapsesMenu         = null;
  private JMenuItem                      undoMenu                = null;
  private JMenuItem                      exportMenu              = null;
  private JMenuItem                      saveMenu                = null;
  private JRadioButtonMenuItem           convertToDynamicMenu    = null;
  private JRadioButtonMenuItem           convertToStaticMenu     = null;
  private JRadioButtonMenuItem           tanhMenu                = null;
  private JRadioButtonMenuItem           sigmMenu                = null;

  private ButtonGroup                    group                   = null;
  private ArrayList                      learningRulesMenuItems  = new ArrayList();
  private static LearningRuleClassLoader learningRuleClassLoader = new LearningRuleClassLoader();

  /**
   * Creates a NeuralNetTable with emtpy matrix and net.
   */
  public NeuralNetTable()
  {
    super();
    init();
  }

  /**
   * Creates a NeuralNetTable with given neural net, initializing the matrix.
   * 
   * @param neuralNet is the neural net to be shown
   */
  public NeuralNetTable(Net neuralNet)
  {
    this();
    this.setNeuralNet(neuralNet);
  }    
  
  /**
   * Sets a neural net to the matrix. The old matrix will be de-referenced
   * while the new one will be shown.
   *
   * @param neuralNet is the neural net to be shown
   */
  public void setNeuralNet(Net neuralNet)
  {
    table.setNeuralNet(neuralNet);    
    update();
  }

  /**
   * Return the reference of the neural net.   *
   * @return is the neural of the table
   */
  public Net getNeuralNet()
  {
    return this.table.model.getNeuralNet();
  }  
  
  /**
   * Adds a neuron to the neural net which type is ntype.
   *
   * @see cholsey.NeuronType
   * @param ntype is the neuron-type to be added
   */
  public void addNeuron(NeuronType ntype)
  {
    table.model.saveUndo();
    table.model.addNeuron(ntype);
    update();
  }

  /**
   * Deletes a neuron from the neural net which has the <b>internal</b> 
   * index i. <br>
   * Important: Here, we use only internal index-handling which is <b>independant</b> 
   * from the neuron-index you will find in the Neuron-class.
   *
   * @see cholsey.NeuronType
   * @param ntype is the neuron-type to be added
   */
  public void delNeuron(int i)
  {
    table.model.saveUndo();
    table.model.delNeuron(i);
    update();
  }

  /**
   * Removes a TableModelListener to the neural net table for
   * detecting changes.
   *
   * @param ntype is the neuron-type to be added
   */
  public void removeTableModelListener(TableModelListener tml)
  {
    table.model.removeTableModelListener(tml);
  }
  
  /**
   * Addes a TableModelListener to the neural net table for
   * detecting changes.
   *
   * @param ntype is the neuron-type to be added
   */
  public void addTableModelListener(TableModelListener tml)
  {
    table.model.addTableModelListener(tml);
  }
  

  public ArrayList getMenus()
  {
    return menus;
  }

  private void setupLearningRulesMenu()
  {
    learningRuleMenu.removeAll();
    Vector learningRuleClasses =
      learningRuleClassLoader.getClasses();

    group = new ButtonGroup();
    learningRulesMenuItems = new ArrayList();
    for(int i=0; i < learningRuleClasses.size();i++)
    {
      JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem(
          ((LearningRuleInterface)learningRuleClasses.elementAt(i)).getName());
      if(i==learningRuleClassLoader.getSelectedIndex())
      {
        rbMenuItem.setSelected(true);
      }
      group.add(rbMenuItem);
      learningRuleMenu.add(rbMenuItem);
      rbMenuItem.setMnemonic(KeyEvent.VK_1 + i);
      rbMenuItem.addActionListener(this);
      learningRulesMenuItems.add(rbMenuItem);
    }

  }
  /** Init neuron Table with all Listeners, Combos etc.) */
  private void init()
  {
    
    scroll      = new JScrollPane();
    table       = new NakedNeuralNetTable();
    menus       = new ArrayList();

    setupLearningRulesMenu();
    menus.add(fileMenu);
    menus.add(editMenu);
    menus.add(transferfunctionMenu);
    menus.add(convertMenu);
    menus.add(learningRuleMenu);
    editMenu.setMnemonic(KeyEvent.VK_E);
    editMenu.getAccessibleContext().setAccessibleDescription(
        "Edit Menu");
    fileMenu.setMnemonic(KeyEvent.VK_F);
    fileMenu.getAccessibleContext().setAccessibleDescription(
        "File Menu");
    transferfunctionMenu.setMnemonic(KeyEvent.VK_T);
    transferfunctionMenu.getAccessibleContext().setAccessibleDescription(
        "Transferfunction Menu");
    convertMenu.setMnemonic(KeyEvent.VK_C);
    convertMenu.getAccessibleContext().setAccessibleDescription(
        "Convert Menu");
    learningRuleMenu.setMnemonic(KeyEvent.VK_S);
    learningRuleMenu.getAccessibleContext().setAccessibleDescription(
        "Learning Rules menu, to reload and select rules");

    exportMenu = new JMenuItem("Export", KeyEvent.VK_E);
    saveMenu   = new JMenuItem("Save", KeyEvent.VK_S);
    exportMenu.addActionListener(this);
    saveMenu.addActionListener(this);
    fileMenu.add(exportMenu);
    fileMenu.add(saveMenu);

    undoMenu = new JMenuItem("Undo", KeyEvent.VK_U);
    undoMenu.addActionListener(this);
    editMenu.add(undoMenu);
    addInputNeuronMenu = new JMenuItem("Add Input Neuron", KeyEvent.VK_I);
    addInputNeuronMenu.addActionListener(this);
    editMenu.add(addInputNeuronMenu);
    addOutputNeuronMenu = new JMenuItem("Add Output Neuron", KeyEvent.VK_O);
    addOutputNeuronMenu.addActionListener(this);
    editMenu.add(addOutputNeuronMenu);

    addReadBufferNeuronMenu = new JMenuItem("Add Read-Buffer Neuron", KeyEvent.VK_R);
    addReadBufferNeuronMenu.addActionListener(this);
    editMenu.add(addReadBufferNeuronMenu);

    addHiddenNeuronMenu = new JMenuItem("Add Hidden Neuron", KeyEvent.VK_H);
    addHiddenNeuronMenu.addActionListener(this);
    editMenu.add(addHiddenNeuronMenu);

    editMenu.addSeparator();
    removeNeuronMenu = new JMenuItem("Remove Neurons", KeyEvent.VK_R);
    removeNeuronMenu.addActionListener(this);
    editMenu.add(removeNeuronMenu);
    editMenu.addSeparator();
    cutSynapsesMenu = new JMenuItem("Round Synapses", KeyEvent.VK_C);
    cutSynapsesMenu.addActionListener(this);
    editMenu.add(cutSynapsesMenu);

    convertToDynamicMenu = new JRadioButtonMenuItem("Dynamic");
    convertToDynamicMenu.setMnemonic(KeyEvent.VK_D);
    convertToStaticMenu = new JRadioButtonMenuItem("Static");
    convertToStaticMenu.setMnemonic(KeyEvent.VK_S);
    convertToDynamicMenu.addActionListener(this);
    convertToStaticMenu.addActionListener(this);
    convertMenu.add(convertToDynamicMenu);
    convertMenu.add(convertToStaticMenu);
    ButtonGroup convertGroup = new ButtonGroup();
    convertGroup.add(convertToStaticMenu);
    convertGroup.add(convertToDynamicMenu);

    tanhMenu = new JRadioButtonMenuItem("TANH");
    tanhMenu.setMnemonic(KeyEvent.VK_T);
    sigmMenu = new JRadioButtonMenuItem("SIGM");
    sigmMenu.setMnemonic(KeyEvent.VK_S);
    tanhMenu.addActionListener(this);
    sigmMenu.addActionListener(this);
    transferfunctionMenu.add(tanhMenu);
    transferfunctionMenu.add(sigmMenu);
    ButtonGroup transferfunctionGroup = new ButtonGroup();
    transferfunctionGroup.add(tanhMenu);
    transferfunctionGroup.add(sigmMenu);


        
    this.setLayout(new GridLayout(1,1));
    this.add(scroll, BorderLayout.CENTER);
    
    
    table.model.addTableModelListener(this);
    
    table.addMouseListener(new MouseAdapter()
    {
      TableColumn tc;
      int c, r;
      
      public void mousePressed(MouseEvent e)
      {
        if (e.getButton() != MouseEvent.BUTTON1)
        {          
          c = table.columnAtPoint(new Point(e.getX(), e.getY())); 
          r = table.rowAtPoint(new Point (e.getX(), e.getY()));
          if (table.getValueAt(r, c).equals(TEXT_NA)
              || table.getValueAt(r, c).equals(TEXT_DYN_EMPTY)
              || table.getValueAt(r, c).equals(TEXT_CON_EMPTY))
          {
            return;
          }
          if (c > table.model.getMaxHiddenIndex())
          {
            return;
          }
          DefaultCellEditor synCmbEdit;
   
          synCombo    = new JComboBox(); 
          synCombo.addItem("dynamic");
          synCombo.addItem("consistent");
          synCombo.addItem("static");
          synCmbEdit = new DefaultCellEditor(synCombo);
          synCmbEdit.setClickCountToStart(1);
          table.cancelEditing();
          tc = table.getColumn(table.getColumnName(c));
          tc.setCellEditor(synCmbEdit);
          table.editCellAt(r, c);
          synCombo.showPopup();
          tc.setCellEditor(new DefaultCellEditor(new JTextField()));
        }
        if(table.model.getNeuralNet().getSynapseMode() == SynapseMode.DYNAMIC)
        {
          if(e.getButton() == MouseEvent.BUTTON1)
          {
            c = table.columnAtPoint(new Point(e.getX(), e.getY())); 
            r = table.rowAtPoint(new Point (e.getX(), e.getY()));
            if (table.getValueAt(r, c).equals(TEXT_NA))
            {
              return;
            }
            if (c > table.model.getMaxHiddenIndex())
            {
              return;
            }

            DefaultCellEditor synCmbEdit;
            synCombo    = new JComboBox(); 
            synCombo.addItem(TEXT_DYN_DELETE);
            synCombo.addItem(TEXT_DYN_INHIBITORY);
            synCombo.addItem(TEXT_DYN_EXCITATORY);
            synCmbEdit = new DefaultCellEditor(synCombo);
            synCmbEdit.setClickCountToStart(1);
            table.cancelEditing();
            tc = table.getColumn(table.getColumnName(c));
            tc.setCellEditor(synCmbEdit);
            table.editCellAt(r, c);
            synCombo.showPopup();
            tc.setCellEditor(new DefaultCellEditor(new JTextField()));
          }
        }
      }        
    });
   
    update();
  }

  private void cutSynapses(int decimalPlace)
  {
      JOptionPane.showMessageDialog(this,
          "Not implemented yet","Not implemented yet",
          JOptionPane.ERROR_MESSAGE);
  }

  private void removeNeuronsFromString(String string)
  {
    NeuronList removeNeurons = new NeuronList();
    StringTokenizer st = new StringTokenizer(string,",");
    while (st.hasMoreTokens()) {
      String s = st.nextToken().trim();
      try
      {
        int index = Integer.parseInt(s);
        removeNeurons.add(table.model.getNeuralNet().neurons().neuron(index-1));
      }
      catch(NumberFormatException nfe)
      {
        StringTokenizer st2 = new StringTokenizer(s,"-");
        String first = st2.nextToken().trim();
        String last  = st2.nextToken().trim();
        int firstIndex = Integer.parseInt(first);
        int lastIndex = Integer.parseInt(last);
        for(int i=firstIndex-1; i < lastIndex; i++)
        {
          removeNeurons.add(table.model.getNeuralNet().neurons().neuron(i));
        }
      }
    }

    if(removeNeurons.size() > 0)
    {
      table.model.saveUndo();
    }
    for(removeNeurons.start(); removeNeurons.hasMore(); removeNeurons.next())
    {
      table.model.getNeuralNet().delNeuron(removeNeurons.neuron());
    }
    
  }

  /**
   * This method is invoked when a button like "add neuron" is called. 
   * Do not call this method directly, use instead "addButton.doClick()"
   */
  public void actionPerformed(ActionEvent e)
  {

    if (e.getSource() == tanhMenu)
    {
      table.model.saveUndo();
      table.model.getNeuralNet().setTransferfunction(
          Transferfunction.TANH);
      update();
      return;
    } 

    if (e.getSource() == sigmMenu)
    {
      table.model.saveUndo();
      table.model.getNeuralNet().setTransferfunction(
          Transferfunction.SIGM);
      update();
      return;
    }

    for(int index=0; index < learningRulesMenuItems.size(); index++)
    {
      JMenuItem m = (JMenuItem)learningRulesMenuItems.get(index);

      if(e.getSource() == m)
      {
        learningRuleClassLoader.setSelectedLearningRule(
            learningRulesMenuItems.indexOf(m));
        update();
        return; // done
      }
    }

    if (e.getSource() == addInputNeuronMenu)
    {
      String iString =  JOptionPane.showInputDialog("How many?", "1");
      try
      {

        int number = Integer.parseInt(iString);
        for(int i=0; i < number; i++)
        {
          table.model.saveUndo();
          table.model.getNeuralNet().addNeuron(NeuronType.INPUT);
        }
        update();
      }
      catch(NumberFormatException nfe)
      {

      }
      return;
    } 

    if (e.getSource() == addOutputNeuronMenu)
    {
      String iString =  JOptionPane.showInputDialog("How many?", "1");
      try
      {

        int number = Integer.parseInt(iString);
        for(int i=0; i < number; i++)
        {
          table.model.saveUndo();
          table.model.getNeuralNet().addNeuron(NeuronType.OUTPUT);
        }
        update();
      }
      catch(NumberFormatException nfe)
      {

      }
      return;
    } 

    if (e.getSource() == addReadBufferNeuronMenu)
    {
      String iString =  JOptionPane.showInputDialog("How many?", "1");
      try
      {

        int number = Integer.parseInt(iString);
        for(int i=0; i < number; i++)
        {
          table.model.saveUndo();
          table.model.getNeuralNet().addNeuron(NeuronType.READ_BUFFER);
        }
        update();
      }
      catch(NumberFormatException nfe)
      {

      }
      return;
    } 


    if (e.getSource() == addHiddenNeuronMenu)
    {
      String iString =  JOptionPane.showInputDialog("How many?", "1");
      try
      {

        int number = Integer.parseInt(iString);
        for(int i=0; i < number; i++)
        {
          table.model.saveUndo();
          table.model.getNeuralNet().addNeuron(NeuronType.HIDDEN);
        }
        update();
      }
      catch(NumberFormatException nfe)
      {

      }
      return;
    } 

    if (e.getSource() == removeNeuronMenu)
    {
      String iString = JOptionPane.showInputDialog("Give indices (1,2,4-5)", "1");
      removeNeuronsFromString(iString);
      update();
      return;
    } 

    if (e.getSource() == removeNeuronMenu)
    {
      String iString = JOptionPane.showInputDialog("Give decimal place", "3");
      cutSynapses(Integer.parseInt(iString));
      update();
      return;
    } 


    if (e.getSource() == convertToDynamicMenu)
    {
      this.table.convertTheNetToDynamic();
      update();
      return;
    }

    if (e.getSource() == convertToStaticMenu)
    {
      this.table.convertTheNetToStatic();
      update();
      return;
    }

    if (e.getSource() == exportMenu)
    {  
      NetConverter nc;
      String outputfilename;
      ExFileFilter ff1;
      ExFileFilter ff2;        
      ExFileFilter ff3;        
      ExFileFilter ff4;        
      ExFileFilter ff5;        
      ExFileFilter ff6;        
      ExFileFilter ff7;        
      ExFileFilter ff8;        
      ExFileFilter ff9;        
      JFileChooser fileDialog;

      ff1 = new ExFileFilter("c",TEXT_EXPORT_IC);
      ff2 = new ExFileFilter("c",TEXT_EXPORT_M);
      ff3 = new ExFileFilter("c",TEXT_EXPORT_C);
      ff4 = new ExFileFilter("txt",TEXT_EXPORT_ASCII);
      ff5 = new ExFileFilter("txt",TEXT_EXPORT_YSOCTNET);
      ff6 = new ExFileFilter("gml",TEXT_EXPORT_GML);
      ff7 = new ExFileFilter("cpp",TEXT_EXPORT_GERMAN_TEAM);
      ff8 = new ExFileFilter("java",TEXT_EXPORT_BRIGHTWELL_NET);
      ff9 = new ExFileFilter("cpp",TEXT_EXPORT_YARS_CONTROLLER);
      
      fileDialog = new JFileChooser(".");
      fileDialog.addChoosableFileFilter(ff1);
      fileDialog.addChoosableFileFilter(ff2);
      fileDialog.addChoosableFileFilter(ff3);
      fileDialog.addChoosableFileFilter(ff4);
      fileDialog.addChoosableFileFilter(ff5);
      fileDialog.addChoosableFileFilter(ff6);
      fileDialog.addChoosableFileFilter(ff7);
      fileDialog.addChoosableFileFilter(ff8);
      fileDialog.addChoosableFileFilter(ff9);
      int returnVal = fileDialog.showSaveDialog(this);
      if(returnVal == JFileChooser.APPROVE_OPTION) 
      {
        outputfilename = new String(fileDialog.getCurrentDirectory() +
            System.getProperty("file.separator") +
            fileDialog.getSelectedFile().getName());
        if(!outputfilename.endsWith(
              ((ExFileFilter)fileDialog.getFileFilter()).getExtension(0)))
        {
          outputfilename += "." +
              ((ExFileFilter)fileDialog.getFileFilter()).getExtension(0);
        }
        if (fileDialog.getFileFilter() == ff1)
        {
          nc = new NetConverter(NetConverter.STYLE_ICONNECT);
        }
        else if (fileDialog.getFileFilter() == ff2)
        {
          nc = new NetConverter(NetConverter.STYLE_C_LOW_MEM);
        }
        else if (fileDialog.getFileFilter() == ff3)
        {
          nc = new NetConverter(NetConverter.STYLE_STANDARD_C);
        }
        else if (fileDialog.getFileFilter() == ff4)
        {
          nc = new NetConverter(NetConverter.STYLE_ASCII);
        }
        else if (fileDialog.getFileFilter() == ff5)
        {
          nc = new NetConverter(NetConverter.STYLE_YSOCNET);
        }
        else if (fileDialog.getFileFilter() == ff6)
        {
          nc = new NetConverter(NetConverter.STYLE_GML);
        }
        else if (fileDialog.getFileFilter() == ff7)
        {
          nc = new NetConverter(NetConverter.STYLE_GERMAN_TEAM);
        }
        else if (fileDialog.getFileFilter() == ff8)
        {
          nc = new NetConverter(NetConverter.STYLE_NAMED_NET);
        }
        else if (fileDialog.getFileFilter() == ff9)
        {
          nc = new NetConverter(NetConverter.STYLE_YARS_CONTROLLER);
        }
        else
        {
          return;
        }
        nc.init(outputfilename);
        nc.setNet(table.model.getNeuralNet());
        nc.createCode();
        nc.deinit();
      }
      update();
      return;
    } 

    if(e.getSource() == undoMenu)
    {  
      table.model.undo();
      update();
      return;
    } 

    if (e.getSource() == saveMenu)
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
        eh.writeNetToFile(table.model.getNeuralNet(), outputfilename, 
            "Created with NeuralNetTable.");

      }
      update();
      return;
    }
  }

  /**
   * This method is invoked when a combo-box like "set transfer function" is called. 
   * Do not call this method directly, use instead "tranCombo.selectItem()"
   */
  public void itemStateChanged(ItemEvent e)
  {
    update();
  }

  /**
   * Updates the whole Table with its matrix and all other components (like
   * the combo-boxes). you should call this method every time you change
   * the neural net represented by this table directly. Unfortunately neural
   * nets do not support listeners, so please remember to call this update
   * yourself.
   */
  public void update()
  {
    int i;
    TableColumn tc;
    Color c;
    JComboBox cmb;

    table.update();

    cmb = new JComboBox();
    cmb.addItem("dynamic");
    cmb.addItem("consistent");
    cmb.addItem("static");


    for (i=0; i<table.getColumnCount(); i++)
    {
      if (table.model.isInputIndex(i))
      {
        c = table.iColor;
      } else
      if (table.model.isOutputIndex(i))
      {
        c = table.oColor;
      } else
      if (table.model.isReadIndex(i))
      {
        c = table.rColor;
      } else
      if (table.model.isHiddenIndex(i))
      {
        c = table.hColor;
      } else
      {
        c = table.xColor;
      }
      tc = table.getColumn(table.getColumnName(i));
      tc.setMinWidth(60);
      tc.setMaxWidth(110);
      tc.setHeaderRenderer(new NeuralNetHeaderRenderer(c, tc.getHeaderRenderer()));
      tc.setCellRenderer(new NeuralNetCellRenderer(table.dColor, table.cColor,
            table.sColor, tc.getCellRenderer()));
      //tc.sizeWidthToFit(); do not use! -> cells will be fixed!
      tc.setResizable(true);
      if (i == table.getRowCount())
      {
        tc.setCellEditor(new DefaultCellEditor(cmb));
      }
    }
    table.getTableHeader().setResizingAllowed(true);
    table.getTableHeader().setReorderingAllowed(false);
    scroll.setViewportView(table);
    scroll.setRowHeaderView(table.getRowHeader());
    scroll.setWheelScrollingEnabled(true);
    scroll.getViewport().addChangeListener(this);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setRowSelectionAllowed(false);
    table.setColumnSelectionAllowed(false);    
    undoMenu.setEnabled(table.model.hasUndo());
    if(table.model.getNeuralNet().getSynapseMode() == SynapseMode.DYNAMIC)
    {
      convertToDynamicMenu.setSelected(true);
    }
    else
    {
      convertToStaticMenu.setSelected(true);
    }

    if(table.model.getNeuralNet().getTransferfunction() == Transferfunction.TANH)
    {
      tanhMenu.setSelected(true);
    }
    else
    {
      sigmMenu.setSelected(true);
    }

    setupLearningRulesMenu();

    //showTest();
  }

  public void tableChanged(TableModelEvent e)
  {
    undoMenu.setEnabled(table.model.hasUndo());
  }
  
  private void showTest()
  {
    for (int i=0; i<table.model.getNeuralNet().size(); i++)
    {
      System.out.println(table.model.getNeuralNet().getNeuron(i));
    }
    for (int i=0; i<table.model.getNeuralNet().getInputNeurons().size(); i++)
    {
      System.out.println(table.model.getNeuralNet().getInputNeurons().neuron(i));
    }
  }

  /**
   * Called when the ScrollPane changes it's sate and forces a repaint.
   * Normally, this should not be nessesary, but in older versions of
   * java you will otherwise sometimes miss an graphical repaint.
   */
  public void stateChanged(ChangeEvent e)
  {
    this.repaint();
  }
  
  /**
   * Only for tests, do not call this method directly!
   */
  public static void main(String args[])
  {
    JPanel root;
    JFrame f;
    NeuralNetTable table;
    Net net;

    net = new Net();
    net.setTransferfunction(Transferfunction.SIGM);
    Neuron a = net.addNeuron(1,0,0,ProcessMode.DYNAMIC,NeuronType.INPUT);
    Neuron b = net.addNeuron(1,0,0,ProcessMode.DYNAMIC,NeuronType.OUTPUT);
    Neuron c = net.addNeuron(1,0,0,ProcessMode.DYNAMIC,NeuronType.HIDDEN);
    Neuron d = net.addNeuron(1,0,0,ProcessMode.DYNAMIC,NeuronType.HIDDEN);
    net.addSynapse(a,b,1,ProcessMode.DYNAMIC);
    net.addSynapse(a,c,1,ProcessMode.DYNAMIC);
    /*net.addSynapse(c,b,1,ProcessMode.DYNAMIC);
    net.addSynapse(b,c,1,ProcessMode.DYNAMIC);
    net.addSynapse(c,c,1,ProcessMode.DYNAMIC);
    net.addSynapse(b,d,1,ProcessMode.DYNAMIC);
    net.addSynapse(c,d,1,ProcessMode.DYNAMIC);*/

/*    
    Neuron e = net.addNeuron(1,0,0,ProcessMode.DYNAMIC,NeuronType.INPUT);
    Neuron g = net.addNeuron(1,0,0,ProcessMode.DYNAMIC,NeuronType.HIDDEN);
    Neuron h = net.addNeuron(1,0,0,ProcessMode.DYNAMIC,NeuronType.HIDDEN);
    Neuron i = net.addNeuron(1,0,0,ProcessMode.DYNAMIC,NeuronType.OUTPUT);*/


    
    table = new NeuralNetTable(net);
    f = new JFrame();
    f.setSize(400,300);
    root = (JPanel) f.getContentPane();
    root.add(table, BorderLayout.CENTER);

    f.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
        System.exit(0);
      }
    });
    f.show();
  }
 
}


