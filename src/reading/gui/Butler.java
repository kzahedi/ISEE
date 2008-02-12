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

package reading.gui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Observer;
import java.util.Vector;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import reading.Entry;
import reading.io.SaxHandler;
import util.io.EvolutionSaxHandlerThread;
import util.io.EvolutionSaxInterface;
import util.io.ExFileFilter;
import util.misc.IseeLogger;
import util.misc.NetTreeView;

public class Butler extends JFrame implements ActionListener,
       ListSelectionListener, EvolutionSaxInterface
{

  private EvolutionSaxHandlerThread evst                      = null;
  private JTable                    entriesTable              = null;
  private JScrollPane               scrollpane                = new JScrollPane(null);
  private JMenuBar                  menuBar                   = null;
  private JMenu                     menuFile                  = null;
  private JMenu                     menuSort                  = null;
  private JMenu                     menuOptions               = null;
  private JMenuItem                 menuItemFileLoad          = null;
  private JMenuItem                 menuItemFileReload        = null;
  private JMenuItem                 menuItemOptionsCut        = null;
  private JMenuItem                 menuItemOptionsDuplicates = null;
  private JMenuItem                 menuItemSortSortAge       = null;
  private JMenuItem                 menuItemSortSortSysPerf   = null;
  private JMenuItem                 menuItemSortSortOutPerf   = null;
  private JMenuItem                 menuItemSortSortIndex     = null;
  private JMenuItem                 menuItemSortSortNeuron    = null;
  private JMenuItem                 menuItemSortSortSynapse   = null;
  private Vector                    entries                   = null;

  private JFileChooser              fileDialog                = new JFileChooser(".");

  private String                    evoTaskFile               = null;

  private Comparator                ageComparator             = null;
  private Comparator                indexComparator           = null;
  private Comparator                populationComparator      = null;
  private Comparator                sysPerfComparator         = null;
  private Comparator                outPerfComparator         = null;
  private Comparator                neuronComparator          = null;
  private Comparator                synapseComparator         = null;


  private static Logger             log                       = IseeLogger.getLogger(Butler.class);
  private Observer                  observer                  = null;

  public Butler(String loggerConfig, Observer observer)
  {
    this(loggerConfig);
    this.observer = observer;
  }

  public Butler(String loggerConfig)
  {
    // **************************************************************************
    // general stuff
    // **************************************************************************
    super("Reading");
    ageComparator = new Comparator() {
      public int compare(Object a, Object b)
      {
        int ca = ((Entry)a).getAge();
        int cb = ((Entry)b).getAge();
        if (ca - cb > 0 ) return -1;
        else              return  1;
      }
    };

    sysPerfComparator = new Comparator() {
      public int compare(Object a, Object b)
      {
        double ca = ((Entry)a).getSysPerf();
        double cb = ((Entry)b).getSysPerf();
        if (ca - cb > 0 ) return -1;
        else              return  1;
      }
    };

    outPerfComparator = new Comparator() {
      public int compare(Object a, Object b)
      {
        double ca = ((Entry)a).getOutPerf();
        double cb = ((Entry)b).getOutPerf();
        if (ca - cb > 0 ) return -1;
        else              return  1;
      }
    };

    indexComparator = new Comparator() {
      public int compare(Object a, Object b)
      {
        int ca = ((Entry)a).getGenerationIndex();
        int cb = ((Entry)b).getGenerationIndex();
        int da = ((Entry)a).getNetIndex();
        int db = ((Entry)b).getNetIndex();
        int ea = ((Entry)a).getPopulationIndex();
        int eb = ((Entry)b).getPopulationIndex();
        if (cb == ca)
        {
          if(ea == eb)
          {
            if (da - db > 0 ) return  1;
            if (db - da > 0 ) return -1;
          }
          if (ea - eb > 0 ) return  1;
          if (eb - ea > 0 ) return -1;
        }
        if (ca - cb > 0 ) return  -1;
        if (cb - ca > 0 ) return   1;
        return  1;
      }
    };

    neuronComparator = new Comparator() {
      public int compare(Object a, Object b)
      {
        double ca = ((Entry)a).getNumberOfNeurons();
        double cb = ((Entry)b).getNumberOfNeurons();
        if (ca - cb > 0 ) return -1;
        else              return  1;
      }
    };

    synapseComparator = new Comparator() {
      public int compare(Object a, Object b)
      {
        double ca = ((Entry)a).getNumberOfSynapses();
        double cb = ((Entry)b).getNumberOfSynapses();
        if (ca - cb > 0 ) return -1;
        else              return  1;
      }
    };


    this.addWindowListener(new WindowAdapter() { public void
        windowClosing(WindowEvent e) { System.exit(0); }});

    JOptionPane.showMessageDialog(this,
        "Reading - Visualising a Evolutionfile\n"
          + "Keyan (keyan@users.sourceforge.net)\n"
          + "http://sourceforge.net/projects/isee/\n\n"
          + "(c) 2002 Sankt Augustin, Germany\n"
          ,"About",
        JOptionPane.INFORMATION_MESSAGE);

    // make it visible

    setContentPane(scrollpane);
    menuBar   = new JMenuBar();
    menuFile  = new JMenu("File");
    menuSort  = new JMenu("Sort");
    menuOptions  = new JMenu("Options");

    menuItemFileLoad = new JMenuItem("Load");
    menuItemFileReload = new JMenuItem("Reload");
    menuItemOptionsCut = new JMenuItem("Cut");
    menuItemOptionsDuplicates = new JMenuItem("Remove Duplicates");
    menuItemSortSortAge = new JMenuItem("Sort by Age");
    menuItemSortSortIndex = new JMenuItem("Sort by Index");
    menuItemSortSortOutPerf = new JMenuItem("Sort by OutPerf");
    menuItemSortSortSysPerf = new JMenuItem("Sort by SysPerf");
    menuItemSortSortNeuron = new JMenuItem("Sort by Neuron");
    menuItemSortSortSynapse = new JMenuItem("Sort by Synapse");

    menuFile.add(menuItemFileLoad);
    menuFile.add(menuItemFileReload);
    menuOptions.add(menuItemOptionsCut);
    menuOptions.add(menuItemOptionsDuplicates);
    menuSort.add(menuItemSortSortIndex);
    menuSort.add(menuItemSortSortOutPerf);
    menuSort.add(menuItemSortSortAge);
    menuSort.add(menuItemSortSortSysPerf);
    menuSort.add(menuItemSortSortNeuron);
    menuSort.add(menuItemSortSortSynapse);

    menuItemFileLoad.addActionListener(this);
    menuItemFileReload.addActionListener(this);
    menuItemOptionsCut.addActionListener(this);
    menuItemOptionsDuplicates.addActionListener(this);
    menuItemSortSortAge.addActionListener(this);
    menuItemSortSortSysPerf.addActionListener(this);
    menuItemSortSortOutPerf.addActionListener(this);
    menuItemSortSortIndex.addActionListener(this);
    menuItemSortSortNeuron.addActionListener(this);
    menuItemSortSortSynapse.addActionListener(this);
    
    menuBar.add(menuFile);
    menuBar.add(menuOptions);
    menuBar.add(menuSort);
    setJMenuBar(menuBar);

    
    setBounds ( 100,100, 400,500);
  }

  public void actionPerformed(ActionEvent event)
  {

    if(event.getSource() == menuItemFileReload)
    {
      if(evoTaskFile != null)
      {
        loadEvoTaskFile(evoTaskFile);
      }
      return;
    }

    if(event.getSource() == menuItemOptionsCut)
    {
      cutSelectedNets();
      return;
    }

    if(event.getSource() == menuItemOptionsDuplicates)
    {
      cutDuplicates();
      return;
    }


    if(event.getSource() == menuItemFileLoad)
    {
      loadFile();
      return;
    }

    if(event.getSource() == menuItemSortSortAge)
    {
      sortByAge();
      log.debug("sorted by age");
      return;
    }

    if(event.getSource() == menuItemSortSortSysPerf)
    {
      sortBySysPerf();
      log.debug("sorted by sys perf");
      return;
    }

    if(event.getSource() == menuItemSortSortOutPerf)
    {
      sortByOutPerf();
      log.debug("sorted by out perf");
      return;
    }

    if(event.getSource() == menuItemSortSortIndex)
    {
      sortByIndex();
      log.debug("sorted by index");
      return;
    }

    if(event.getSource() == menuItemSortSortNeuron)
    {
      sortByNeuron();
      log.debug("sorted by neuron");
      return;
    }

    if(event.getSource() == menuItemSortSortSynapse)
    {
      sortBySynapse();
      log.debug("sorted by synapse");
      return;
    }



  }


  private void sortByAge()
  {
    Collections.sort(entries, ageComparator);
    updateTable();
  }

  private void sortBySysPerf()
  {
    Collections.sort(entries, sysPerfComparator);
    updateTable();
  }

  private void sortByOutPerf()
  {
    Collections.sort(entries, outPerfComparator);
    updateTable();
  }

  private void sortByIndex()
  {
    Collections.sort(entries, indexComparator);
    updateTable();
  }

  private void sortByNeuron()
  {
    Collections.sort(entries, neuronComparator);
    updateTable();
  }

  private void sortBySynapse()
  {
    Collections.sort(entries, synapseComparator);
    updateTable();
  }




  private void loadFile()
  {
    fileDialog.addChoosableFileFilter(new ExFileFilter("xml","EvoTask File ( .xml )"));
    int returnVal = fileDialog.showOpenDialog(this);
    if(returnVal == JFileChooser.APPROVE_OPTION) {
      loadEvoTaskFile(fileDialog.getCurrentDirectory() +
          System.getProperty("file.separator") +
          fileDialog.getSelectedFile().getName());
      setTitle("Reading - "  + fileDialog.getSelectedFile().getName());
    }
    return;
  }

  private void loadEvoTaskFile(String filename)
  {
    log.debug("loading " + filename);
    SaxHandler sh = new SaxHandler();
    entries = sh.readFile(filename);
    updateTable();
    evoTaskFile = filename;
  }

  private void updateTable()
  {
    entriesTable = vectorToTree(entries);
    scrollpane = new JScrollPane(entriesTable);
    setContentPane(scrollpane);
    setVisible(true);
  }

  private JTable vectorToTree(Vector v)
  {
    JTable table = null;
    
    Object[][] tableContent = new Object[v.size()][Entry.SIZE];
    String [] columnNames = {"PIN", "G-ID", "N-ID", "P-ID", "age", "s-perf.", "o-perf.", "#N", "#S"};

    for(int i=0; i < v.size(); i++)
    {
      Entry e = (Entry)v.elementAt(i);
      tableContent[i][0] = new Integer(e.getPinId());
      tableContent[i][1] = new Integer(e.getGenerationIndex());
      tableContent[i][2] = new Integer(e.getNetIndex());
      tableContent[i][3] = new Integer(e.getPopulationIndex());
      tableContent[i][4] = new Integer(e.getAge());
      tableContent[i][5] = new Double(e.getSysPerf());
      tableContent[i][6] = new Double(e.getOutPerf());
      tableContent[i][7] = new Integer(e.getNumberOfNeurons());
      tableContent[i][8] = new Integer(e.getNumberOfSynapses());
    }

    table = new JTable(tableContent, columnNames);

    ListSelectionModel rowSM = table.getSelectionModel();
    rowSM.addListSelectionListener(this);

    return table;

  }

  public void valueChanged(ListSelectionEvent event)
  {
    if(event.getValueIsAdjusting()) return;
    if(entriesTable.getSelectedRows().length > 1) return;

    log.debug(event.toString());

    ListSelectionModel lsm = (ListSelectionModel)event.getSource();
    int selectedRow = lsm.getMinSelectionIndex();
    System.out.print("Row " + selectedRow
        + " is now selected.");
    //NetTreeView net = new NetTreeView(NetTreeView.MODE_VIEW_NET);
    Entry entry = (Entry)entries.elementAt(selectedRow);
    evst = new EvolutionSaxHandlerThread(evoTaskFile,
        entry.getGenerationIndex(), 0, entry.getNetIndex(), this, this);
    Thread t = new Thread(evst);
    t.start();

  }

  public void netLoaded()
  {
    if(observer == null)
    {
      NetTreeView net = new NetTreeView(NetTreeView.MODE_VIEW_NET);
      net.updateView(evst.getNet());
    }
    else
    {
      observer.update(null, evst.getNet());
    }
  }

  public void cutSelectedNets()
  {
    int[] rows = entriesTable.getSelectedRows();
    for(int i=rows.length-1; i >= 0; i--)
    {
      entries.remove(rows[i]);
    }
    updateTable();
  }

  // from current selection top to bottom
  public void cutDuplicates()
  {
    Vector seenPins = new Vector(0);
    ArrayList toBeRemoved = new ArrayList(0);
    for(int index = 0; index < entries.size(); index++)
    {
      Entry e = (Entry)entries.get(index);
      if(seenPins.indexOf(new Integer(e.getPinId())) == -1)
      {
        seenPins.add(new Integer(e.getPinId()));
      }
      else
      {
        toBeRemoved.add(e);
      }
    }

    for(int index = 0; index < toBeRemoved.size(); index++)
    {
      Entry e = (Entry)toBeRemoved.get(index);
      entries.remove(e);
    }
    updateTable();
  }
}

