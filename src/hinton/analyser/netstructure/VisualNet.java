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

/*
 * Created on 11.03.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package hinton.analyser.netstructure;

import hinton.analyser.Analyser;
import hinton.analyser.NetObserver;
import hinton.analyser.toolkit.InputDialog;
import hinton.analyser.toolkit.InputValue;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolTip;
import javax.swing.KeyStroke;
import javax.swing.OverlayLayout;
import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import util.io.ExFileFilter;

import cholsey.Net;
import cholsey.Neuron;
import cholsey.NeuronList;
import cholsey.Synapse;
import cholsey.SynapseList;
import cholsey.SynapseMode;


/**
 * VisualNet is the component to visualize a neural network of the type
 * Cholsey.Net as a directed graph
 */
public class VisualNet extends    JInternalFrame
                       implements NetObserver,
                                  NeuronLocationObserver,
                                  MouseListener,
                                  MouseMotionListener
{
  private static final Integer SELECTION_DISPLAY_LAYER = new Integer(2);
  private static final Integer NEURON_DISPLAY_LAYER    = new Integer(1);
  private static final Integer SYNAPSE_DISPLAY_LAYER   = new Integer(0);
  
  private Net                    net;
  private HashMap                renderedNeurons;
  private HashMap                renderedSynapses;
  private int                    netSynapseMode;
  private SynapseRendererFactory srf;
  private NeuronRendererFactory  nrf;
  
  private boolean            isInitialized    = false;
  private boolean            isInfluenceMode  = false;
  private int                influenceNeuronID;
  private Net                lastNet;
  private JPanel             neuronLayer      = new JPanel();
  private JPanel             synapseLayer     = new JPanel();
  private JPanel             forLayout        = new JPanel();
  private JMenuBar           menuBar          = new JMenuBar();
  private JMenu              mnuLayout        = new JMenu();
  private JMenu              mnuExport        = new JMenu();
  private JMenu              mnuFile          = new JMenu();
  private JMenuItem          mniOpenLayout;
  private JMenuItem          mniSaveLayout;
  private JMenu              mnuZoom;
  private JMenuItem          mniZoomIn;
  private JMenuItem          mniZoomOut;
  private ToolTipDelegationPanel selectionLayer   = new ToolTipDelegationPanel();
  private JScrollPane        scrollPane;
  private JLayeredPane       container        = new JLayeredPane();
  private ComponentRenderer  draggingRenderer  = null;
  private ComponentRenderer  mouseOverRenderer = null;
  
  private NeuronRenderer[]   selection        = null;
  private Rectangle          selectionArea    = null;
  private SelectionState     selectionState   = SelectionState.NOSELECTION;
  private Point              selectionDragOff = null;
  
  private double             currScaleFactor  = 1.0d;
  
  private VisualNet()
  {
  }
  
  /**
   * Create a new visualization for the given net 
   * @param net the net to be visualized
   */
  public VisualNet(Net net)
  {
    super("Net Structure", true, true, true, true);
    
    this.mnuLayout.setText("Layout");
    this.createLayoutMenu();
    this.mnuExport.setText("Export");
    this.createExporterMenu();
    this.mnuFile.setText("File");
    this.mniOpenLayout = new JMenuItem("Load Layout");
    this.mniOpenLayout.addActionListener(new ActionListener () {
      public void actionPerformed(ActionEvent e)
      {
        load();
      }
    });
    this.mniSaveLayout = new JMenuItem("Save Layout");
    this.mniSaveLayout.addActionListener(new ActionListener () {
      public void actionPerformed(ActionEvent e)
      {
        save();
      }
    });
    this.mnuZoom = new JMenu("Zoom");
    this.mniZoomIn = new JMenuItem("Zoom IN");
    this.mniZoomIn.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_PLUS, InputEvent.CTRL_MASK));
    this.mniZoomIn.addActionListener(new ActionListener () {
      public void actionPerformed(ActionEvent e)
      {
        
        if (currScaleFactor < 2.0d)
        {
          currScaleFactor += 0.1;
          setScaling();
        }
      }
    });
    this.mniZoomOut = new JMenuItem("Zoom OUT");
    this.mniZoomOut.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_MINUS, InputEvent.CTRL_MASK));
    this.mniZoomOut.addActionListener(new ActionListener () {
      public void actionPerformed(ActionEvent e)
      {
        
        if (currScaleFactor > 0.5)
        {
          currScaleFactor -= 0.1;
          setScaling();
        }
      }
    });
    this.mnuZoom.add(this.mniZoomIn);
    this.mnuZoom.add(this.mniZoomOut);
    
    this.mnuFile.add(this.mniOpenLayout);
    this.mnuFile.add(this.mniSaveLayout);
    this.menuBar.add(this.mnuFile);
    this.menuBar.add(this.mnuLayout);
    this.menuBar.add(this.mnuExport);
    this.menuBar.add(this.mnuZoom);
    this.setJMenuBar(this.menuBar);
    
    
    this.container.setLayout(new OverlayLayout(container));
    
    this.selectionLayer.setOpaque(false);
    this.selectionLayer.setMinimumSize(this.getSize());
    this.container.add(selectionLayer, SELECTION_DISPLAY_LAYER);
    
    this.neuronLayer.setOpaque(false);
    this.neuronLayer.setMinimumSize(this.getSize());
    this.container.add(neuronLayer, NEURON_DISPLAY_LAYER);
    
    this.synapseLayer.setOpaque(true);
    this.synapseLayer.setBackground(Color.WHITE);
    this.synapseLayer.setMinimumSize(this.getSize());
    this.container.add(synapseLayer, SYNAPSE_DISPLAY_LAYER);
    
    this.scrollPane = new JScrollPane(container);
    this.forLayout.setLayout(new GridLayout(1,1));
    this.forLayout.add(this.scrollPane);
    this.getContentPane().add(this.forLayout, BorderLayout.CENTER);
  
    this.selectionLayer.setLayout(null);  
    this.selectionLayer.addMouseListener(this);
    this.selectionLayer.addMouseMotionListener(this);
    
    this.neuronLayer.setLayout(null);
    this.synapseLayer.setLayout(null);
    this.setNet(net);
  
    this.resizeLayers();
      
  }
  
  
  /*
   *  (non-Javadoc)
   * @see java.awt.Component#setSize(java.awt.Dimension)
   */
  public void setSize(Dimension d)
  {
    super.setSize(d);
    neuronLayer.setSize(d);
    synapseLayer.setSize(d);
  }
  
  /*
   *  (non-Javadoc)
   * @see java.awt.Component#setSize(int, int)
   */
  public void setSize(int width, int height)
  {
    super.setSize(width, height);
    neuronLayer.setSize(width, height);
    synapseLayer.setSize(width, height);
  }
  
  private void setScaling()
  {
    NeuronRenderer[] nr = this.getNeuronRenderer();
    for (int i = 0; i < nr.length; i++)
    {
      nr[i].scale(this.currScaleFactor);
    }
  }
  
  /*
   *  (non-Javadoc)
   * @see Hinton.analyser.netstructure.NeuronLocationObserver#computeLocationUpdate(java.awt.Rectangle, int)
   */
  public void computeLocationUpdate(Rectangle location, int neuronID)
  {
    int left, right, top, bottom;
    boolean resize   = false;
    boolean relocate = false;
    Dimension d;
    
    left   = this.container.getX();
    top    = this.container.getY();
    right  = this.container.getWidth();
    bottom = this.container.getHeight();
    
    if (!this.container.getBounds().contains(location)) 
    {
      if (location.width + location.x > this.container.getWidth())
      {
        resize = true;
        right = location.width + location.x + 50;
      }
      if (location.height + location.y > this.container.getHeight())
      {
        resize = true;
        bottom = location.height + location.y + 50;
      }
      if (resize)
      {
        d = new Dimension(right, bottom);
        this.container.setSize(d);
        this.container.setMinimumSize(d);
        this.container.setPreferredSize(d);
      }
      
      d = this.container.getSize();
      if (location.x < 0)
      {
        left       = location.x - 50;
        d.width   -= left;
        relocate   = true;
      }
      if (location.y < 0)
      {
        top        = location.y - 50;
        d.height  -= top;
        relocate   = true;
      }
      
      if (relocate)
      { 
        this.container.setLocation(left, top);
        this.container.setSize(d);
        this.container.setMinimumSize(d);
        this.container.setPreferredSize(d);
      }
    }
  }
  
  /* (non-Javadoc)
   * @see Hinton.analyser.netstructure.NetObserver#setNet(Cholsey.Net)
   */
  public void setNet(Net net)
  { 
    NeuronRenderer  neuronRenderer  = null;
    SynapseRenderer synapseRenderer = null;
    Neuron          currentNeuron;
    Synapse         currentSynapse;
    String          defaultNeuronRenderer;
    String          defaultSynapseRenderer;
    NeuronList      neuronList   = new NeuronList();
    SynapseList     synapseList  = new SynapseList();
    Neuron[]        neurons;
    Synapse[]       synapses;
    
    this.currScaleFactor = 1.0d;
    this.stopInfluenceMode();
    SynapseRenderer.resetNetSynapseStrength();
    
    this.net              = net;
    if (net != null && net.getSynapseMode() != null)
    {
      this.netSynapseMode   = net.getSynapseMode().mode();
    }
    
    if (this.renderedNeurons != null)
    {
      this.renderedNeurons.clear();
      this.renderedSynapses.clear();
    }
    else
    {
      this.renderedNeurons = new HashMap();
      this.renderedSynapses = new HashMap();
    }
    this.synapseLayer.setVisible(false);
    this.neuronLayer.setVisible(false);
    this.neuronLayer.removeAll();
    this.synapseLayer.removeAll();
    this.neuronLayer.setVisible(true);
    this.synapseLayer.setVisible(true);
    this.srf              = SynapseRendererFactory.getInstance();
    this.nrf              = NeuronRendererFactory.getInstance();
    
    if (net == null) return;

    defaultSynapseRenderer = this.srf.getDefaultRendererName();
    defaultNeuronRenderer  = this.nrf.getDefaultRendererName();
    
    neuronList.addAll(net.neurons());   
    neurons = (Neuron[])neuronList.toArray(new Neuron[0]);
    for (int i = 0; i < neurons.length; i++)
    {
      currentNeuron  = neurons[i];
      neuronRenderer = null;      
      try
      {
        neuronRenderer = this.nrf.createRendererByName(defaultNeuronRenderer,
                               this,
                               currentNeuron);
      }
      catch (Exception e)
      {
          e.printStackTrace();
      }
      neuronRenderer.setLocation((int)(Math.random() * 600),
                                (int)(Math.random() * 600));
      neuronRenderer.registerLocationObserver(this);
      neuronLayer.add(neuronRenderer);
      this.renderedNeurons.put(String.valueOf(currentNeuron.id()),
                                 neuronRenderer);
        
      if (currentNeuron.synapses() != null)
      {
        synapseList.addAll(currentNeuron.synapses());
      }
    }
    
    synapses = (Synapse[])synapseList.toArray(new Synapse[0]);
    for (int i = 0; i < synapses.length; i++)
    {
      currentSynapse = synapses[i];
      try
      {
        synapseRenderer = this.srf.createRendererByName(defaultSynapseRenderer,
                               this,
                               currentSynapse);
      }
      catch (Exception e)
      {
        System.out.println("Creating Synapse Renderer " + e.toString());
        e.printStackTrace();
        // Can't occur in this case
      }
      
      try
      {
        if (synapseRenderer != null)
        {
          synapseLayer.add(synapseRenderer);
          this.renderedSynapses.put(currentSynapse, synapseRenderer);
        }
      }
      catch (Exception e)
      {
        System.err.println("Adding Synapse: " + e.toString());
        e.printStackTrace();
      }
    }
    for (int i = 0; i < this.net.size(); i++)
    {
      this.getNeuronRenderer(i).setLocation(
          this.getNeuronRenderer(i).getLocation());
    }
    
  }

  /* (non-Javadoc)
   * @see Hinton.analyser.netstructure.NetObserver#netUpdate()
   */
  public void netUpdate(Net net)
  {
    NeuronRenderer  neuronRenderer;
    SynapseRenderer synapseRenderer;
    Neuron          currentNeuron;
    Synapse         currentSynapse;
    NeuronList      neuronList;
    SynapseList     synapseList;
    Neuron[]        neurons;  // This Variable is needed cause NeuronList 
                              // has no Multithreading ability 
    Synapse[]       synapses; // This Variable is needed cause SynapseList 
                              // has no Multithreading ability 
    Net             updateViewNet;
    
    if (!this.isInfluenceMode)
    {
      updateViewNet = net;
    }
    else
    {
      Analyser.getInstance().pause();
      updateViewNet = this.getInfluenceModeNet();
      this.lastNet = net.copy();
      Analyser.getInstance().resume();
    }
    
    
    neuronList = updateViewNet.neurons();
    neurons = (Neuron[])neuronList.toArray(new Neuron[0]);
    for (int i = 0; i < neurons.length; i++)
    {
      currentNeuron = neurons[i];
      try
      {
        neuronRenderer = getNeuronRenderer(currentNeuron.id());
        neuronRenderer.updateData(currentNeuron);

        if (this.netSynapseMode == SynapseMode.SYNAPSE_MODE_DYNAMIC)
        {
          synapseList = currentNeuron.synapses();
          if (synapseList != null)
          {
            synapses = (Synapse[])synapseList.toArray(new Synapse[0]);
            for (int j = 0; j < synapses.length; j++)
            {
              currentSynapse = synapses[j];
              try
              {
                synapseRenderer = (SynapseRenderer)this.renderedSynapses.get(
                                                        currentSynapse);
                synapseRenderer.updateData(currentSynapse);
              }
              catch (Exception e)
              {
                System.err.println("Synapse update:" + e.toString());
              }
            }
          }     
        }

      }
      catch (Exception e)
      {
        System.err.println("Neuron update");
      }
    }
  }
  
  /**
   * Returns the neuron renderer for the given neuron id
   * @param neuronID 
   * @return NeuronRenderer for the given neuron id
   */
  public final NeuronRenderer getNeuronRenderer(int neuronID)
  {
    return (NeuronRenderer)this.renderedNeurons.get(String.valueOf(neuronID));
  }

  /**
   * Returns the synapse renderer for the given synapse
   * @param synapse
   * @return SynapseRenderer for the given synapse
   */
  public final SynapseRenderer getSynapseRenderer(Synapse synapse)
  {
    return (SynapseRenderer)this.renderedSynapses.get(synapse);
  }
  
  /**
   * Returns the currently visualized net
   * @return the currently visualized net
   */
  public final Net getNet()
  {
    return this.net;
  }
  
  /**
   * Returns the NeuronRenderer of all neurons of the visualized net
   * @return NeuronRenderers of all neurons of the visualized net
   */
  public final NeuronRenderer[] getNeuronRenderer()
  {
    return (NeuronRenderer[])this.renderedNeurons.values().toArray(
                                                        new NeuronRenderer[0]); 
  }

  /**
   * Returns the SynapseRenderer of all synapses of the visiualized net
   * @return SynapseRenderer of all synapses of the visualized net
   */
  public final SynapseRenderer[] getSynapseRenderer()
  {
    return (SynapseRenderer[])this.renderedSynapses.values().toArray(
                                                        new SynapseRenderer[0]);
  }
  
  /**
   * Switch to the influence mode for the given neuron id
   * 
   * In influence mode the difference of the output of the neurons of
   * the net is shown if the neuron with the given id is absent
   * 
   * @param neuronID
   */
  public void setInfluenceMode(int neuronID)
  {
    InfluenceMode im = InfluenceMode.getInstance(this);
    if (!this.isInfluenceMode)
    {
      this.isInfluenceMode   = true;
      this.influenceNeuronID = neuronID;
      im.setLocation(this.scrollPane.getViewport().getViewPosition().x + 10,
          this.scrollPane.getViewport().getViewPosition().y + 10);
      im.setSize(350, 50);
      im.setVisible(true);
      this.selectionLayer.add(im);
      this.selectionLayer.validate();
      this.selectionLayer.repaint(0);
    }
  }
  
  /**
   * Stop the influence mode
   */
  public void stopInfluenceMode() 
  {
    if (this.isInfluenceMode)
    {
      this.isInfluenceMode   = false;
      this.influenceNeuronID = -1;
      this.lastNet           = null;
      this.selectionLayer.remove(InfluenceMode.getInstance(this));
      this.selectionLayer.validate();
      this.selectionLayer.repaint(0);
    }
    
  } 
  
  
  private Net getInfluenceModeNet()
  {
    Net clone, influenceNet;
    double maxDifference;
    double upperLimit;
    double lowerLimit;
    double output;
    if (!this.isInfluenceMode ||
        this.influenceNeuronID >= this.net.size() ||
        this.lastNet == null)
    {
      return this.net;
    }
  
    influenceNet = this.net.copy(); //actual Net
    
    
    for (int i = 0; i < this.lastNet.getInputNeurons().size(); i++)
    {
      this.lastNet.setInputNeuronValue(i, influenceNet.getInputNeuronValue(i));
    }
    this.lastNet.getNeuron(this.influenceNeuronID).setOutput(0.0d);
    this.lastNet.process();          //net from the last step processed
                                     //with the output of the influenceNeuron
                                     //set to 0

    
  
    upperLimit    = this.net.getTransferfunction().calculate(100);
    lowerLimit    = this.net.getTransferfunction().calculate(-100);
    maxDifference = upperLimit - lowerLimit;
                    
    for (int i = 0; i < this.lastNet.size(); i++)
    {
      if (i != this.influenceNeuronID)
      {
        output = this.net.getNeuron(i).getOutput() - 
                 this.lastNet.getNeuron(i).getOutput();
        output = (output > 0 ? output : -output);
        output = output / maxDifference;
        influenceNet.getNeuron(i).setOutput(output);
      }
      else
      {
        influenceNet.getNeuron(i).setOutput(-1.0d);
      }
    }

    return influenceNet;
  }
  
  private void load()
  {
    JFileChooser           jfc      = new JFileChooser();
    File                   loadFile;
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder        db;
    Document               doc;
    
    jfc.setAcceptAllFileFilterUsed(false);
    jfc.setFileFilter(new ExFileFilter("xml", "VisualNet Layout (.xml)"));
    if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
    {
      loadFile = jfc.getSelectedFile();
      
      try
      {
        db  = dbf.newDocumentBuilder();
        doc = db.parse(loadFile);
        if (doc.getDoctype().getName().compareTo("VisualNet") == 0)
        {
          Node n = doc.getDocumentElement();
          NamedNodeMap nnm = n.getAttributes();
          if (Integer.parseInt(nnm.getNamedItem("NeuronCount").getNodeValue()) ==
        this.net.size() &&
              Integer.parseInt(nnm.getNamedItem("SynapseCount").getNodeValue()) ==
        this.net.getSynapseCount())
          {
            loadNeuronRenderer(n);
            loadSynapseRenderer(n);
          }
          else
          {
            JOptionPane.showMessageDialog(this,
        "The choosen layout is not compatible with the currently loaded net");
          }
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    this.resizeLayers();
  }
  
  private void save()
  {
    String           xml;
    Iterator         keys = this.renderedSynapses.keySet().iterator();
    JFileChooser     jfc  = new JFileChooser();
    File             saveFile;
    FileOutputStream fos;
  
    xml  = this.getXMLHeader();
  
    for (int i = 0; i < this.renderedNeurons.size(); i++)
    {
      xml += this.getXMLNeuron(this.getNeuronRenderer(i));
    }
    
    while (keys.hasNext())
    {
      xml += this.getXMLSynapse(
              (SynapseRenderer)this.renderedSynapses.get(keys.next()));
                                              
    }
  
    xml += this.getXMLFooter();
    
    jfc.setAcceptAllFileFilterUsed(false);
    jfc.setFileFilter(new ExFileFilter("xml", "VisualNet Layout (.xml)"));
    if (jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
    {
      saveFile = jfc.getSelectedFile();
      
      try
      {
        fos = new FileOutputStream(saveFile);
        fos.write(xml.getBytes());
        fos.flush();
        fos.close();
      }
      catch (Exception e)
      {
        JOptionPane.showMessageDialog(this, "Error saving current Layout!");
      }
    }
  }

  private void loadNeuronRenderer(Node n)
  {
    NodeList                   nl = n.getChildNodes();
    NodeList                   options;
    ArrayList                  pro;
    NamedNodeMap               nnm;
    NeuronRenderer             nr;
    InputValue.IObject         dmy;

    for (int i = 0; i < nl.getLength(); i++)
    {
      if (nl.item(i).getNodeName().compareTo("NeuronRenderer") == 0)
      {
        nnm = nl.item(i).getAttributes();
        nr  = this.getNeuronRenderer(Integer.parseInt(nnm.getNamedItem("ID")
                                                     .getNodeValue()));
        dmy = new InputValue.IObject("", 0, new String[] {
                            nnm.getNamedItem("RendererName").getNodeValue() });
        this.performNeuronRendererChanged(nr, dmy);
        nr  = this.getNeuronRenderer(Integer.parseInt(nnm.getNamedItem("ID")
                                                     .getNodeValue()));
        nr.setLocation(
           Integer.parseInt(nnm.getNamedItem("x").getNodeValue()),
           Integer.parseInt(nnm.getNamedItem("y").getNodeValue()));
        
        options = nl.item(i).getChildNodes();   
        pro = new ArrayList();
        for (int j = 0; j < options.getLength(); j++)
        {
          if (options.item(j).getNodeName().compareTo("RendererOption") == 0)
          {
            pro.add(new PersistentRendererOption(
              options.item(j).getAttributes().getNamedItem("key").getNodeValue(),
              options.item(j).getAttributes().getNamedItem("value").getNodeValue()));
          }
        }
        if (pro.size() > 0)
        {
          nr.setPersistentOptions((PersistentRendererOption[])pro.toArray(
              new PersistentRendererOption[0]));
        }
      }
    }
  }

  private void loadSynapseRenderer(Node n)
  {
    NodeList           nl  = n.getChildNodes();
    NodeList           sp;
    NamedNodeMap       nnm;
    SynapseRenderer    sr;
    ArrayList          pro;
    InputValue.IObject dmy;
    Neuron             neuron;
    SynapseList        sl;
    Synapse            synapse = null;
    Point[]            points;
    Point              p;
    ArrayList          alPoints = new ArrayList();
    int                k;
    Synapse[]          synapses;

    for (int i = 0; i < nl.getLength(); i++)
    {
      if (nl.item(i).getNodeName().compareTo("SynapseRenderer") == 0)
      {
        nnm    = nl.item(i).getAttributes();
        neuron = this.net.getNeuron(
                      Integer.parseInt(
                      nnm.getNamedItem("DestinationNeuronID").getNodeValue()));
        sl = neuron.synapses();
        if (sl != null)
        {
          synapses = (Synapse[])sl.toArray(new Synapse[0]);
          for (int j = 0; j < synapses.length; j++)
          {
            if (synapses[j].getSource().id() ==
              Integer.parseInt(
              nnm.getNamedItem("SourceNeuronID").getNodeValue()))
            {
              synapse = synapses[j];
            }
          }
        }
        sr = this.getSynapseRenderer(synapse);
        
        dmy = new InputValue.IObject("", 0, new String[] {
                  nnm.getNamedItem("RendererName").getNodeValue() });
        
        this.performSynapseRendererChanged(sr, dmy);
        sr = this.getSynapseRenderer(synapse);
        
        sp     = nl.item(i).getChildNodes();
        pro = new ArrayList();
        alPoints.clear();
        for (int j = 0; j < sp.getLength(); j++)
        {
          if (sp.item(j).getNodeName().compareTo("SamplingPoint") == 0)
          {
            nnm = sp.item(j).getAttributes();
            p   = new Point();
            p.x = Integer.parseInt(nnm.getNamedItem("x").getNodeValue());
            p.y = Integer.parseInt(nnm.getNamedItem("y").getNodeValue()); 
            alPoints.add(p);
          }
              
          
          
          if (sp.item(j).getNodeName().compareTo("RendererOption") == 0)
          {
            pro.add(new PersistentRendererOption(
              sp.item(j).getAttributes().getNamedItem("key").getNodeValue(),
              sp.item(j).getAttributes().getNamedItem("value").getNodeValue()));
          }
          
          if (pro.size() > 0)
          {
            sr.setPersistentOptions((PersistentRendererOption[])pro.toArray(
                new PersistentRendererOption[0]));
          }
        } 
        points = (Point[])alPoints.toArray(new Point[0]);
        sr.setSamplingPoints(points);
      }
    }
  }

  private String getXMLHeader()
  {
    String xml = "";
    
    xml  = "<?xml version=\"1.0\" encoding=\"LATIN1\"?>\n";
    xml += "<!DOCTYPE VisualNet [\n";
    xml += "<!ELEMENT VisualNet (NeuronRenderer+, SynapseRenderer*)>\n";
    xml += "<!ATTLIST VisualNet\n";
    xml += "      NeuronCount           CDATA #REQUIRED\n";
    xml += "      SynapseCount          CDATA #REQUIRED>\n";
    xml += "<!ELEMENT NeuronRenderer (RendererOption*)>\n";
    xml += "<!ATTLIST NeuronRenderer\n";
    xml += "      ID                    CDATA #REQUIRED\n";
    xml += "      x                     CDATA #REQUIRED\n";
    xml += "      y                     CDATA #REQUIRED\n";
    xml += "      RendererName          CDATA #REQUIRED>\n";
    xml += "<!ELEMENT RendererOption EMPTY>\n";
    xml += "<!ATTLIST RendererOption\n";
    xml += "      key                   CDATA #REQUIRED\n";
    xml += "      value                 CDATA #REQUIRED>\n";
    xml += "<!ELEMENT SynapseRenderer (SamplingPoint+, RendererOption*)>\n";
    xml += "<!ATTLIST SynapseRenderer\n";
    xml += "      SourceNeuronID        CDATA #REQUIRED\n";
    xml += "      DestinationNeuronID   CDATA #REQUIRED\n";
    xml += "      RendererName          CDATA #REQUIRED>\n";
    xml += "<!ELEMENT SamplingPoint EMPTY>\n";
    xml += "<!ATTLIST SamplingPoint\n";
    xml += "      x                     CDATA #REQUIRED\n";
    xml += "      y                     CDATA #REQUIRED>\n";
    xml += "]>\n\n";
  
    xml += "<VisualNet";
    xml += " NeuronCount=\"" + this.net.size() + "\"";
    xml += " SynapseCount=\"" + this.net.getSynapseCount() + "\"";
    xml += ">\n";
  
    return xml;
  }
  
  private String getXMLFooter()
  {
    return "</VisualNet>\n";
  }
  
  private String getXMLNeuron(NeuronRenderer nr)
  {
    String xml = "";
    PersistentRendererOption[] pro = nr.getPersistentOptions();
    
    xml  = "<NeuronRenderer";
    xml += " ID=\"" + (nr.getNeuron().id() + 1) + "\"";
    xml += " x=\"" + nr.getX() + "\"";
    xml += " y=\"" + nr.getY() + "\"";
    xml += " RendererName=\"" + nr.getRendererName() + "\">\n";
    
    if (pro != null)
    {
      for (int i = 0; i < pro.length; i++)
      {
        xml += pro[i].getXML();
      }
    }
    xml += "</NeuronRenderer>";
    return xml;
  }
  
  private String getXMLSynapse(SynapseRenderer sr)
  {
    String  xml             = "";
    Synapse s               = sr.getSynapse();
    Point[] samplingPoints  = sr.getSamplingPoints();
    PersistentRendererOption[] pro = sr.getPersistentOptions();
  
    xml  = "<SynapseRenderer";
    xml += " SourceNeuronID=\"" + (s.getSource().id() + 1) + "\"";
    xml += " DestinationNeuronID=\"" + (s.getDestination().id() + 1) + "\"";
    xml += " RendererName=\"" + sr.getRendererName() + "\"";
    xml += ">\n";
    for (int i = 0; i < samplingPoints.length; i++)
    {
      xml += "\t<SamplingPoint";
      xml += " x=\"" + samplingPoints[i].x + "\"";
      xml += " y=\"" + samplingPoints[i].y + "\"";
      xml += "/>\n";
    }
    
    if (pro != null)
    {
      for (int i = 0; i < pro.length; i++)
      {
        xml += pro[i].getXML();
      }
    }
    
    xml += "</SynapseRenderer>\n";
  
    return xml; 
  }
  
  private void createExporterMenu()
  {
    String[]  exporter = VisualNetExporter.getAvailableExporterNames();
    JMenuItem current;
    
    for (int i = 0; i < exporter.length; i++)
    {
      current = new JMenuItem(exporter[i]);
      current.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e)
        {
          showExporter(e);
        }
      });
      this.mnuExport.add(current);
    } 
  }
  
  private void createLayoutMenu()
  {
    String[] layout = GraphLayouter.getAvailableLayoutNames();
    JMenuItem current;
    
    for (int i = 0; i < layout.length; i++)
    {
      current = new JMenuItem(layout[i]);
      current.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e)
        {
          double oldScale = currScaleFactor;
          currScaleFactor = 1.0d;
          setScaling();
          showLayout(e);    
          currScaleFactor = oldScale;
          setScaling();
        }
      });
      this.mnuLayout.add(current);
    }
  }
  
  private void showExporter(ActionEvent e)
  {
    JMenuItem eventSource = (JMenuItem)e.getSource();
    VisualNetExporter exp = VisualNetExporter.getExporter(
                                              eventSource.getText(),
                                              this);    
    exp.showDialog();
  }
  
  private void showLayout(ActionEvent e)
  {
    JMenuItem eventSource = (JMenuItem)e.getSource();
    GraphLayouter layout = GraphLayouter.getLayout(eventSource.getText());
                                                
    layout.showDialog(this);
    this.resizeLayers();
  }
  
  
  private InputValue.IObject getRendererChoice(
                              ComponentRenderer componentRenderer)
  {
    String[]           rn;
    int                idx;
    InputValue.IObject rendererChoice = null;
       if (componentRenderer instanceof SynapseRenderer)
    {
      rn = this.srf.getAvailableRendererNames();
      Arrays.sort(rn);
      
      idx            = Arrays.binarySearch(rn,
                                           componentRenderer.getRendererName());
      idx            = (idx < 0 ? 0 : idx);
      rendererChoice = new InputValue.IObject("Synapse Renderer", idx, rn);   
                
    }
    else if (componentRenderer instanceof NeuronRenderer)
    {
      rn = this.nrf.getAvailableRendererNames();
      Arrays.sort(rn);
      
      idx            = Arrays.binarySearch(rn,
                                           componentRenderer.getRendererName());
      idx            = (idx < 0 ? 0 : idx);
      rendererChoice = new InputValue.IObject("Neuron Renderer", idx, rn);          
    }
    return rendererChoice;
  }
  
  private void performRendererChanged(ComponentRenderer componentRenderer,
                    InputValue.IObject re)
  {
       if (componentRenderer instanceof SynapseRenderer &&
          componentRenderer.getRendererName().compareTo((String)re.value) != 0)
    {
      performSynapseRendererChanged((SynapseRenderer)componentRenderer, re);
    }
    else if (componentRenderer instanceof NeuronRenderer &&
           componentRenderer.getRendererName().compareTo((String)re.value) != 0)
    {
      performNeuronRendererChanged((NeuronRenderer)componentRenderer, re);
    } 
  }
  
  private void performSynapseRendererChanged(SynapseRenderer synapseRenderer,
                                            InputValue.IObject re)
  {
    Synapse[] keys = (Synapse[])
                      this.renderedSynapses.keySet().toArray(new Synapse[0]);
    for (int i = 0; i < keys.length; i++)
    {
      if (this.renderedSynapses.get(keys[i]).equals(synapseRenderer))
      {
        this.getNeuronRenderer(keys[i].getSource().id()).
                              unregisterLocationObserver(synapseRenderer);
        this.getNeuronRenderer(keys[i].getDestination().id()).
                              unregisterLocationObserver(synapseRenderer);
        this.synapseLayer.remove(synapseRenderer);
        try
        {
          synapseRenderer = this.srf.createRendererByName(
                                  (String)re.value,
                                  this,
                                  keys[i]);
        }
        catch (Exception co)
        {
          try
          {
            synapseRenderer = this.srf.createRendererByName(
                                    this.srf.getDefaultRendererName(),
                                    this,
                                    keys[i]);
          }
          catch (Exception usd)
          {
            
          }
        }
        this.synapseLayer.add(synapseRenderer);
        synapseRenderer.computeLocationUpdate(
            this.getNeuronRenderer(keys[i].getSource().id()).getBounds(),
            keys[i].getSource().id());
        this.renderedSynapses.remove(keys[i]);
        this.renderedSynapses.put(keys[i], synapseRenderer);
      }
    }
  }

  private void performNeuronRendererChanged(NeuronRenderer neuronRenderer,
                        InputValue.IObject re)
  {
    Synapse  current;
    Iterator it;
    Point    location;
    String[] keys = (String[])
                    this.renderedNeurons.keySet().toArray(new String[0]);
    for (int i = 0; i < keys.length; i++)
    {
      if (this.renderedNeurons.get(keys[i]).equals(neuronRenderer))
      {
        location = neuronRenderer.getLocation();
        this.neuronLayer.remove(neuronRenderer);
        try
        {
          neuronRenderer = this.nrf.createRendererByName(
                                    (String)re.value,
                                    this,
                                    this.net.getNeuron(
                                         Integer.parseInt(keys[i])));
        }
        catch (Exception co)
        {
          try
          {
            neuronRenderer = this.nrf.createRendererByName(
                                      nrf.getDefaultRendererName(),
                                      this,
                                      this.net.getNeuron(
                                          Integer.parseInt(keys[i])));
          }
          catch (Exception e)
          {
            
          }
        }

        neuronRenderer.setLocation(location);
        neuronRenderer.registerLocationObserver(this);
        this.neuronLayer.add(neuronRenderer);
        this.renderedNeurons.remove(keys[i]);
        this.renderedNeurons.put(keys[i], neuronRenderer);
        
        // Update SynapseRenderer to observe the new NeuronRenderer
        it = this.renderedSynapses.keySet().iterator();
        while (it.hasNext())
        {
          current = (Synapse)it.next();
          if (current.getSource().id()      == Integer.parseInt(keys[i]) ||
              current.getDestination().id() == Integer.parseInt(keys[i]))
          {
            neuronRenderer.registerLocationObserver(
                              (SynapseRenderer)
                              this.renderedSynapses.get(current));
          } // if (selected NeuronRenderer is source/destination of synapse) 
        } // while (update SynapseRenderer) 
      } // if (componentRenderer = this.renderedNeurons[x]
    } // for (all renderedNeurons)
  }
  
  private void resizeLayers()
    {
      int i;
      int MIN_LEFT   = Integer.MAX_VALUE;
      int MIN_TOP    = Integer.MAX_VALUE;
      int MAX_WIDTH  = Integer.MIN_VALUE;
      int MAX_HEIGHT = Integer.MIN_VALUE;
    
      Rectangle      currentL;  
      Dimension      newDimension;
      Component      currentC;
      NeuronRenderer nr;
    
      for (i = 0; i < this.neuronLayer.getComponentCount(); i++)
      {
        currentL = this.neuronLayer.getComponent(i).getBounds();
        if (currentL.x < MIN_LEFT)
        {
          MIN_LEFT = currentL.x;
        }
        if (currentL.y < MIN_TOP)
        {
          MIN_TOP = currentL.y;
        }
        if (currentL.x + currentL.width > MAX_WIDTH)
        {
          MAX_WIDTH = currentL.x + currentL.width;
        }
        if (currentL.y + currentL.height > MAX_HEIGHT)
        {
          MAX_HEIGHT = currentL.y + currentL.height;
        }
      }
    
      MAX_WIDTH  = MAX_WIDTH - MIN_LEFT;
      MAX_HEIGHT = MAX_HEIGHT - MIN_TOP;
      newDimension = new Dimension(MAX_WIDTH + 100, MAX_HEIGHT + 100);
    
  
    
      this.container.setSize(newDimension);
      this.container.setPreferredSize(newDimension);
      this.container.setMinimumSize(newDimension);
    
      for (i = 0; i < this.getNeuronRenderer().length; i++)
      {
        nr = this.getNeuronRenderer(i);
        nr.setLocation(nr.getLocation().x - MIN_LEFT + 50,
                       nr.getLocation().y - MIN_TOP + 50);
      }
      for (i = 0; i < this.getNeuronRenderer().length; i++)
      {
        nr = this.getNeuronRenderer(i);
        nr.setLocation(nr.getLocation().x,
                       nr.getLocation().y);
      }
    
    }
  
  /* (non-Javadoc)
   * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
   */
  public void mouseClicked(MouseEvent e) {
    this.selectionState = SelectionState.NOSELECTION;
    this.selectionLayer.selection = null;
    
    if (this.draggingRenderer == null)
    {
      ComponentRenderer selectedRenderer = 
                                      this.getComponentRendererAt(e.getPoint());
      
      
      
      if (selectedRenderer == null ) return;
      
      if (SwingUtilities.isRightMouseButton(e) &&
               e.getClickCount() == 1)
      {
        if (selectedRenderer.getRenderingProperties() != null)
        {
          InputValue.IObject  re = getRendererChoice(selectedRenderer);
          InputDialog dialog = new InputDialog(selectedRenderer, "Properties");
          dialog.addElement(selectedRenderer.getRenderingProperties());
          dialog.addElement(re);
          dialog.show();
          performRendererChanged(selectedRenderer, re);           
        }
      }
      else 
      {
        selectedRenderer.mouseClicked(e);
      }
    }
    
  }

  /* (non-Javadoc)
   * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
   */
  public void mouseEntered(MouseEvent e) {
    // TODO Auto-generated method stub
    
  }

  /* (non-Javadoc)
   * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
   */
  public void mouseExited(MouseEvent e) {
    // TODO Auto-generated method stub
    
  }

  /* (non-Javadoc)
   * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
   */
  public void mousePressed(MouseEvent e) {
    ComponentRenderer selectedRenderer = 
                          this.getComponentRendererAt(e.getPoint());
    
    if (selectedRenderer != null &&
        this.selectionState == SelectionState.NOSELECTION)
    {
      
      this.draggingRenderer = selectedRenderer;
      e.translatePoint(-selectedRenderer.getLocation().x,
                       -selectedRenderer.getLocation().y);
      selectedRenderer.mousePressed(e);
    }
    else if (this.selectionState == SelectionState.NOSELECTION ||
        !this.selectionArea.contains(e.getPoint()))
    {
      this.draggingRenderer = null;
      if (SwingUtilities.isLeftMouseButton(e))
      {
        this.selectionDragOff = e.getPoint();
        this.selectionArea  = new Rectangle(e.getX(), e.getY(),
            0, 0);
        this.selectionLayer.selection = this.selectionArea;
        this.selectionState = SelectionState.SELECTING;
      }
    }
    else if (this.selectionState == SelectionState.SELECTED &&
             this.selectionArea.contains(e.getPoint()))
    {
      this.selectionState   = SelectionState.DRAGGING;
      this.selectionDragOff = e.getPoint();
    }
    else if (this.selectionState == SelectionState.SELECTED &&
             !this.selectionArea.contains(e.getPoint()))
    {
      this.selectionState = SelectionState.NOSELECTION;
    }
    
  }

  /* (non-Javadoc)
   * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
   */
  public void mouseReleased(MouseEvent e) {
    ArrayList dmyselection;
    if (this.draggingRenderer != null)
    {
      e.translatePoint(-this.draggingRenderer.getLocation().x,
                       -this.draggingRenderer.getLocation().y);
      this.draggingRenderer.mouseReleased(e);
      this.draggingRenderer = null;
    } 
    else if (this.selectionState == SelectionState.SELECTING)
    {
      this.selectionState = SelectionState.SELECTED;
      dmyselection = new ArrayList();
      for (int i = 0; i < this.net.size(); i++)
      {
        if (this.selectionArea.contains(this.getNeuronRenderer(i).getBounds()))
        {
          dmyselection.add(this.getNeuronRenderer(i));
        }
      }
      if (dmyselection.size() > 0)
      {
        this.selection = (NeuronRenderer[])dmyselection.toArray(
            new NeuronRenderer[0]);
        this.selectionState = SelectionState.SELECTED;
      }
      else
      {
        this.selectionState = SelectionState.NOSELECTION;
        this.selectionLayer.selection = null;
        this.selectionLayer.repaint(0);
      }
    }
    else if (this.selectionState == SelectionState.DRAGGING)
    {
      this.selectionState = SelectionState.SELECTED;
    }

    
  }

  /* (non-Javadoc)
   * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
   */
  public void mouseDragged(MouseEvent e) {  
    if (this.draggingRenderer != null)
    {
      e.translatePoint(-this.draggingRenderer.getLocation().x,
                       -this.draggingRenderer.getLocation().y);
      this.draggingRenderer.mouseDragged(e);  
    }
    else if (this.selectionState == SelectionState.SELECTING)
    {
      if (e.getX() > this.selectionDragOff.x)
      {
        this.selectionArea.width = e.getX() - this.selectionDragOff.x;
      }
      else if (e.getX() < this.selectionDragOff.x)
      {
        this.selectionArea.width = this.selectionDragOff.x - e.getX();
        this.selectionArea.x     = e.getX();
      }
      
      if (e.getY() > this.selectionDragOff.y)
      {
        this.selectionArea.height = e.getY() - this.selectionDragOff.y;
      }
      else if (e.getY() < this.selectionDragOff.y)
      {
        this.selectionArea.height = this.selectionDragOff.y - e.getY();
        this.selectionArea.y      = e.getY();
      }
      this.selectionLayer.repaint(0);
    }
    else if (this.selectionState == SelectionState.DRAGGING)
    {
      for (int i = 0; i < this.selection.length; i++)
      {
        this.selection[i].setLocation((e.getX() -
                    this.selectionDragOff.x) + this.selection[i].getX(),
                    (e.getY() -
                    this.selectionDragOff.y) + this.selection[i].getY());
      }
      this.selectionArea.setLocation(
          (e.getX() - this.selectionDragOff.x) + this.selectionArea.x,
          (e.getY() - this.selectionDragOff.y) + this.selectionArea.y);
      this.selectionDragOff = e.getPoint();
      this.selectionLayer.repaint(0);
    }
  }

  /* (non-Javadoc)
   * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
   */
  public void mouseMoved(MouseEvent e) {
    if (this.draggingRenderer == null)
    {
      
      ComponentRenderer selectedRenderer = 
                        this.getComponentRendererAt(e.getPoint());

    
      if (selectedRenderer != null)
      {
        if (this.mouseOverRenderer != selectedRenderer)
        {
          if (this.mouseOverRenderer != null)
          {
            this.mouseOverRenderer.setSelected(false);
            this.mouseOverRenderer.repaint(0);
          }
          this.mouseOverRenderer = selectedRenderer;
          this.mouseOverRenderer.setSelected(true);
          this.mouseOverRenderer.repaint(0);
          this.selectionLayer.myToolTip = null;
        }
        if (this.selectionLayer.myToolTip == null)
        {
          this.selectionLayer.setToolTipText("");
          this.selectionLayer.myToolTip = selectedRenderer.createToolTip();
          this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
      } 
      else
      {
        if (this.mouseOverRenderer != null)
        {
          this.mouseOverRenderer.setSelected(false);
          this.mouseOverRenderer.repaint(0);
          this.mouseOverRenderer = null;
        }
        this.selectionLayer.myToolTip = null;
        this.selectionLayer.setToolTipText(null);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }
    }
  }
  
  public Component getComponentAt(Point p)
  {
    Component c = this.getComponentRendererAt(p);
    if (c == null)
    {
      c = super.getComponentAt(p);
    }
    return c;
  }
  
  public Component getComponentAt(int x, int y)
  {
    return this.getComponentAt(new Point(x,y));
  }
  
  /**
   * Returns the ComponentRenderer (Synapse/Neuron) at the given point
   * this method is necessary since the components overlap each other
   * @param proofPoint the point
   * @return the componont "containing" the point or null if none
   */
  public final ComponentRenderer getComponentRendererAt(Point proofPoint)
  {
    Point   location;
    Point2D.Float proof      = new Point2D.Float();
    ComponentRenderer selectedRenderer  = null;
    SynapseRenderer[] mySynapseRenderer = (SynapseRenderer[])
                this.renderedSynapses.values().toArray(new SynapseRenderer[0]);
    double dist      = 5.0d;
    double currDist;

    try
    {
      selectedRenderer = (ComponentRenderer)
                         this.neuronLayer.findComponentAt(proofPoint);
    }
    catch (ClassCastException e)
    {
      selectedRenderer = null;
    }
    
    if (selectedRenderer == null)
    {       
      for (int i = 0; i < mySynapseRenderer.length; i++)
      {
        location = mySynapseRenderer[i].getLocation();
        if (mySynapseRenderer[i].contains(proofPoint.x - location.x,
                          proofPoint.y - location.y))
        {
          proof.x = proofPoint.x - location.x;
          proof.y = proofPoint.y - location.y;
          if ((currDist = mySynapseRenderer[i].getMinimumOutlineDistance(proof))
               < dist)
          {
            dist = currDist;
            selectedRenderer = mySynapseRenderer[i];
          }
        }
      }
    }

    return selectedRenderer;      
  }
  
  public static class ToolTipDelegationPanel extends JPanel
  {
    public JToolTip  myToolTip = new JToolTip();
    public Rectangle selection;
    
    public JToolTip createToolTip()
    {
      return this.myToolTip;
    }
    
    public final void paint(Graphics g)
    {
      Graphics2D g2d = (Graphics2D)g;
      super.paint(g);
      if (selection != null)
      {
        //g2d.setXORMode(Color.BLACK);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(0.5f));
        g2d.draw(this.selection);
      }
    }
  }
  
  private static class InfluenceMode extends JPanel
                              implements AdjustmentListener
  {
    private static InfluenceMode instance;
    
    private VisualNet visualNet;
    private JLabel    inInfluenceMode;
    private JButton   stopInfluence;
    
    private InfluenceMode(VisualNet visNet)
    {
      super();
      this.visualNet = visNet;
      this.setOpaque(false);
      this.inInfluenceMode = new JLabel("INFLUENCE MODE    ");
      // make it BIG and red
      this.inInfluenceMode.setForeground(Color.RED);
      this.inInfluenceMode.setFont(
           this.inInfluenceMode.getFont().deriveFont(24.0f));
      this.inInfluenceMode.setOpaque(false);
      
      this.stopInfluence = new JButton("Stop");
      this.stopInfluence.setForeground(Color.RED);
      this.stopInfluence.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e)
        {
          visualNet.stopInfluenceMode();
        }
      });
      this.inInfluenceMode.setVisible(true);
      this.stopInfluence.setVisible(true);
      this.add(this.inInfluenceMode);
      this.add(this.stopInfluence);
      
      visNet.scrollPane.getHorizontalScrollBar().addAdjustmentListener(this);
      visNet.scrollPane.getVerticalScrollBar().addAdjustmentListener(this);
      
    }

    /* (non-Javadoc)
     * @see java.awt.event.AdjustmentListener#adjustmentValueChanged(java.awt.event.AdjustmentEvent)
     */
    public void adjustmentValueChanged(AdjustmentEvent e) {
      Point viewOffset = 
          this.visualNet.scrollPane.getViewport().getViewPosition();
      this.setLocation(viewOffset.x + 10, viewOffset.y + 10);
      
    }
    
    public static InfluenceMode getInstance(VisualNet visualNet)
    {
      if (InfluenceMode.instance == null)
      {
        InfluenceMode.instance = new InfluenceMode(visualNet);
      }
      return InfluenceMode.instance;
    }
  }
  
  private static class SelectionState
  {
    public static final SelectionState NOSELECTION = new SelectionState();
    public static final SelectionState SELECTING   = new SelectionState();
    public static final SelectionState SELECTED    = new SelectionState();
    public static final SelectionState DRAGGING    = new SelectionState();
    
    private SelectionState() {};
  }
}
