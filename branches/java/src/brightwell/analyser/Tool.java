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


package brightwell.analyser;

import brightwell.control.Butler;
import brightwell.gui.drawingplane.DrawingPlane;
import brightwell.gui.Error;

import cholsey.Net;

import hinton.analyser.toolkit.InputComponent;
import hinton.analyser.toolkit.InputValue;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import util.misc.IseeLogger;

public abstract class Tool implements Runnable, KeyListener, Cloneable
{

  protected boolean doAnalysis = true;
  protected Net net = null;
  protected int convergenceIterations = 0;
  protected int drawIterations = 0;
  protected double xStart = 0;
  protected double xEnd   = 0;

  protected double yStart = 0;
  protected double yEnd   = 0;
  protected Butler parent = null;
    
  protected double stepsX = 0;
  protected double stepsY = 0;

  protected double dx = 0;
  protected double dy = 0;

  private boolean keySpacePressed = false;
 
  //private static Logger toolLogger = IseeLogger.getLogger(Tool.class);

  protected DataStorage dataStorage = null;
  private InputComponent.Collection inputCollection = null;
  private JPanel panel = new JPanel();
  private int priority = 0;
  private Vector windowList = new Vector();
  private Hashtable hashtable = new Hashtable();

  public abstract boolean needsNet();
  public abstract void init();
  public abstract String getToolName();
  public abstract String getToolDescription();
  public abstract void doAnalysis();
 
  public Tool()
  {
    inputCollection = new InputComponent.Collection("");
  }


  public void setParent(Butler parent)
  {
    this.parent = parent;
  }

  public void addDouble(
      String name, double minValue, double maxValue, double defaultValue)
  {
    InputValue.IDouble doubleInput= new InputValue.IDouble(name, 
        defaultValue, minValue, maxValue);

    inputCollection.addElement(doubleInput);

    hashtable.put(name, doubleInput);

  }

  public void addComboBox(
      String name, String[] entries, int selectedIndex)
  {

    String[] indices = new String[entries.length];
    for(int i=0; i < entries.length; i++)
    {
      indices[i] = new String(""+i);
    }

    InputValue.IObject comboBox= new InputValue.IObject(name, selectedIndex,
        indices, entries);

    hashtable.put(name, comboBox);

    inputCollection.addElement(comboBox);

  }

  public void addCheckBox(String name, boolean initialValue)
  {
    InputValue.IBoolean bool  = new InputValue.IBoolean(name, initialValue);

    inputCollection.addElement(bool);

    hashtable.put(name, bool);
  }

  public void addString(String name, String initialValue)
  {
    InputValue.IString  string  = new InputValue.IString(name, initialValue);

    inputCollection.addElement(string);

    hashtable.put(name, string);

  }

  public void addFileChooser(String name, String initialValue)
  {
    InputValue.IFileChooser  string  = new InputValue.IFileChooser(name, initialValue);

    inputCollection.addElement(string);

    hashtable.put(name, string);

  }


  public void addInteger(
      String name, int minValue, int maxValue, int defaultValue)
  {
    InputValue.IInteger integer  = new InputValue.IInteger(name, 
        defaultValue, minValue, maxValue);

    inputCollection.addElement(integer);

    hashtable.put(name, integer);

  }

  public JPanel getPanel()
  {
    panel.removeAll();
    panel.add(inputCollection.getInputComponent());

    return panel;
  }


  public void run()
  {
    inputCollection.performUpdate();
    doAnalysis = true;
    doAnalysis();
    doAnalysis = false;
    parent.finished(this);
  }

  public int getToolPriority()
  {
    return priority;
  }

  public void setDataStorage(DataStorage dataStorage)
  {
    this.dataStorage = dataStorage;

    xStart = dataStorage.getXRange()[0];
    xEnd   = dataStorage.getXRange()[1];

    yStart = dataStorage.getYRange()[0];
    yEnd   = dataStorage.getYRange()[1];

    stepsX = dataStorage.getWindowSize()[0];
    stepsY = dataStorage.getWindowSize()[1];

    dx = (xEnd-xStart)/(double)stepsX;
    dy = (yEnd-yStart)/(double)stepsY;

    if(dataStorage.getNet() != null)
    {
      net = dataStorage.getNet().copy();
    }
    convergenceIterations = dataStorage.getConvergenceIterations();
    drawIterations = dataStorage.getDrawIterations();
  }

  public void closeAllWindows()
  {
    for(int i=0; i < windowList.size(); i++)
    {
      DrawingPlane d = (DrawingPlane)windowList.elementAt(i);
      d.dispose();
    }
  }
  public Vector getWindowList()
  {
    return windowList;
  }
 
  protected void setToolPriority(int priority)
  {
    this.priority = priority;
  }

  protected void addWindow(DrawingPlane dp)
  {
    windowList.add(dp);
  }

  public DrawingPlane getNewPeekWindow()
  {
    return getNewTransmitterWindow(getToolName());
  }

  public DrawingPlane getNewPeekWindow(String name)
  {
    double[] xRange = {0, dataStorage.getWindowSize()[0] };
    DrawingPlane dp = new DrawingPlane(
        name,
        dataStorage.getWindowSize()[0], 
        dataStorage.getWindowSize()[1], 
        xRange,
        dataStorage.getYRange(), this);

    dp.setUseBigPoints(dataStorage.getUseBigPoints());

    dp.addWindowListener(new WindowAdapter()
        {
        public void windowClosed(WindowEvent e)
        {
        //toolLogger.debug("windowClosed");
        parent.closed((DrawingPlane)(e.getSource()));
        }
        public void windowClosing(WindowEvent e)
        {
        //toolLogger.debug("windowClosing");
        ((DrawingPlane)e.getSource()).dispose();
        }
        });
    parent.addWindow(dp);
    dp.setVisible(true);
    dp.drawLegend();
    return dp;
  }
  public DrawingPlane getNewTransmitterWindow()
  {
    return getNewTransmitterWindow(getToolName());
  }

  public DrawingPlane getNewTransmitterWindow(String name)
  {
    return getNewTransmitterWindow(name, "");
  }

  public DrawingPlane getNewTransmitterWindow(String name, 
      String defaultFilename)
  {
    DrawingPlane dp = new DrawingPlane(
        name,
        dataStorage.getWindowSize()[0], 
        dataStorage.getWindowSize()[1], 
        dataStorage.getXRange(), 
        dataStorage.getTransmitterRange(), this,
        defaultFilename);
    dp.setUseBigPoints(dataStorage.getUseBigPoints());

    dp.addWindowListener(new WindowAdapter()
        {
        public void windowClosed(WindowEvent e)
        {
        //toolLogger.debug("windowClosed");
        parent.closed((DrawingPlane)(e.getSource()));
        }
        public void windowClosing(WindowEvent e)
        {
        //toolLogger.debug("windowClosing");
        ((DrawingPlane)e.getSource()).dispose();
        }
        });
    parent.addWindow(dp);
    dp.setVisible(true);
    dp.drawLegend();
    return dp;
  }


  public DrawingPlane getNewReceptorWindow()
  {
    return getNewReceptorWindow(getToolName());
  }

  public DrawingPlane getNewReceptorWindow(String name)
  {
    return getNewReceptorWindow(name, "");
  }

  public DrawingPlane getNewReceptorWindow(String name, String defaultFilename)
  {
    DrawingPlane dp = new DrawingPlane(
        name,
        dataStorage.getWindowSize()[0], 
        dataStorage.getWindowSize()[1], 
        dataStorage.getXRange(), 
        dataStorage.getReceptorRange(), this,
        defaultFilename);
    dp.setUseBigPoints(dataStorage.getUseBigPoints());

    dp.addWindowListener(new WindowAdapter()
        {
        public void windowClosed(WindowEvent e)
        {
        //toolLogger.debug("windowClosed");
        parent.closed((DrawingPlane)(e.getSource()));
        }
        public void windowClosing(WindowEvent e)
        {
        //toolLogger.debug("windowClosing");
        ((DrawingPlane)e.getSource()).dispose();
        }
        });
    parent.addWindow(dp);
    dp.setVisible(true);
    dp.drawLegend();
    return dp;
  }

  public DrawingPlane getNewLiapunovWindow()
  {
    return getNewLiapunovWindow(getToolName());
  }

  public DrawingPlane getNewLiapunovWindow(String name)
  {
    return getNewLiapunovWindow(name, "");
  }

  public DrawingPlane getNewLiapunovWindow(String name, String defaultFilename)
  {
    DrawingPlane dp = new DrawingPlane(
        name,
        dataStorage.getWindowSize()[0], 
        dataStorage.getWindowSize()[1], 
        dataStorage.getXRange(), 
        dataStorage.getLiapunovRange(), this,
        defaultFilename);
    dp.setUseBigPoints(dataStorage.getUseBigPoints());

    dp.addWindowListener(new WindowAdapter()
        {
        public void windowClosed(WindowEvent e)
        {
        //toolLogger.debug("windowClosed");
        parent.closed((DrawingPlane)(e.getSource()));
        }
        public void windowClosing(WindowEvent e)
        {
        //toolLogger.debug("windowClosing");
        ((DrawingPlane)e.getSource()).dispose();
        }
        });
    parent.addWindow(dp);
    dp.setVisible(true);
    dp.drawLegend();
    return dp;
  }

  public DrawingPlane getNewSynapseWindow()
  {
    return getNewSynapseWindow(getToolName());
  }

  public DrawingPlane getNewSynapseWindow(String name)
  {
    return getNewSynapseWindow(name, "");
  }

  public DrawingPlane getNewSynapseWindow(String name, String defaultFilename)
  {
    DrawingPlane dp = new DrawingPlane(
        name,
        dataStorage.getWindowSize()[0], 
        dataStorage.getWindowSize()[1], 
        dataStorage.getXRange(), 
        dataStorage.getSynapseRange(), this,
        defaultFilename);
    dp.setUseBigPoints(dataStorage.getUseBigPoints());

    dp.addWindowListener(new WindowAdapter()
        {
        public void windowClosed(WindowEvent e)
        {
        //toolLogger.debug("windowClosed");
        parent.closed((DrawingPlane)(e.getSource()));
        }
        public void windowClosing(WindowEvent e)
        {
        //toolLogger.debug("windowClosing");
        ((DrawingPlane)e.getSource()).dispose();
        }
        });
    parent.addWindow(dp);
    dp.setVisible(true);
    dp.drawLegend();
    return dp;
  }

//  public DrawingPlane3D getNewWindow3D(String name)
//  {
//    DrawingPlane3D frame = new DrawingPlane3D();
//    frame.setBounds(new Rectangle(400,400));
//    frame.setVisible(true);
//    return frame;
//
//  }
//

  public DrawingPlane getNewWindow()
  {
    return getNewWindow(getToolName());
  }

  public DrawingPlane getNewWindow(String name, 
      double xMin, double xMax,
      double yMin, double yMax)
  {
    double[] yRange = 
    {
      yMin,
      yMax
    };
    double[] xRange = 
    {
      xMin,
      xMax
    };

    DrawingPlane dp = new DrawingPlane(
        name,
        dataStorage.getWindowSize()[0], 
        dataStorage.getWindowSize()[1], 
        xRange,
        yRange, this);
    dp.setUseBigPoints(dataStorage.getUseBigPoints());

    dp.addWindowListener(new WindowAdapter()
        {
        public void windowClosed(WindowEvent e)
        {
        //toolLogger.debug("windowClosed");
        parent.closed((DrawingPlane)(e.getSource()));
        }
        public void windowClosing(WindowEvent e)
        {
        //toolLogger.debug("windowClosing");
        ((DrawingPlane)e.getSource()).dispose();
        }
        });
    parent.addWindow(dp);
    dp.setVisible(true);
    dp.drawLegend();
    return dp;

  }
  public DrawingPlane getNewWindow(String name, double yMin, double yMax)
  {
    double[] yRange = 
    {
      yMin,
      yMax
    };

    DrawingPlane dp = new DrawingPlane(
        name,
        dataStorage.getWindowSize()[0], 
        dataStorage.getWindowSize()[1], 
        dataStorage.getXRange(), 
        yRange, this);
    dp.setUseBigPoints(dataStorage.getUseBigPoints());

    dp.addWindowListener(new WindowAdapter()
        {
        public void windowClosed(WindowEvent e)
        {
        //toolLogger.debug("windowClosed");
        parent.closed((DrawingPlane)(e.getSource()));
        }
        public void windowClosing(WindowEvent e)
        {
        //toolLogger.debug("windowClosing");
        ((DrawingPlane)e.getSource()).dispose();
        }
        });
    parent.addWindow(dp);
    dp.setVisible(true);
    dp.drawLegend();
    return dp;

  }

  public DrawingPlane getNewWindow(String name)
  {
    return getNewWindow(name, "");
  }

  public DrawingPlane getNewWindow(String name, String defaultFilename)
  {
    DrawingPlane dp = new DrawingPlane(
        name,
        dataStorage.getWindowSize()[0], 
        dataStorage.getWindowSize()[1], 
        dataStorage.getXRange(), 
        dataStorage.getYRange(), this,
        defaultFilename);
    dp.setUseBigPoints(dataStorage.getUseBigPoints());

    dp.addWindowListener(new WindowAdapter()
        {
        public void windowClosed(WindowEvent e)
        {
        //toolLogger.debug("windowClosed");
        parent.closed((DrawingPlane)(e.getSource()));
        }
        public void windowClosing(WindowEvent e)
        {
        //toolLogger.debug("windowClosing");
        ((DrawingPlane)e.getSource()).dispose();
        }
        });
    parent.addWindow(dp);
    dp.setVisible(true);
    dp.drawLegend();
    return dp;
  }

  public void stopIt()
  {
    doAnalysis = false;
  }

 
  // **************************************************************************
  // key listening section
  // **************************************************************************
  public void keyTyped(KeyEvent e)
  { }

  public void keyReleased(KeyEvent e)
  {
    if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
    {
      //toolLogger.debug("escaped catched");
      doAnalysis = false;
    }

  }

  public void keyPressed(KeyEvent e)
  {
    if(e.getKeyCode() == KeyEvent.VK_SPACE)
    {
      //toolLogger.debug("space pressed");
      keySpacePressed = true;
    }
  }

  // **************************************************************************
  // get function
  // **************************************************************************

  public int getComboBoxIndex(String key)
  {
    checkForKey(key);
    InputValue.IObject comboBox = (InputValue.IObject)hashtable.get(key);
    return Integer.parseInt((String)comboBox.value);
  }

  public String getString(String key)
  {
    checkForKey(key);
    InputValue.IString string = (InputValue.IString)hashtable.get(key);
    return string.value;
  }

  public String getFileChooser(String key)
  {
    checkForKey(key);
    InputValue.IFileChooser string = (InputValue.IFileChooser)hashtable.get(key);
    return string.value;
  }


  public double getDouble(String key)
  {
    checkForKey(key);
    InputValue.IDouble doubleValue = (InputValue.IDouble)hashtable.get(key);
    return doubleValue.value;
  }

  public int getInteger(String key)
  {
    checkForKey(key);
    InputValue.IInteger integer = (InputValue.IInteger)hashtable.get(key);
    return integer.value;
  }


  public boolean getCheckBox(String key)
  {
    InputValue.IBoolean bool = (InputValue.IBoolean)hashtable.get(key);
    return bool.value;
  }


  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch(CloneNotSupportedException cnse)
    {
      cnse.printStackTrace();
    }
    return null;
  }

  private void checkForKey(Object key)
  {
    if(hashtable.get(key) == null)
    {
      Error.unknownKey(parent, (String)key);
    }
  }

  protected boolean spacePressed()
  {
    boolean returnvalue = keySpacePressed;
    keySpacePressed = false;
    return returnvalue;
  }
}
