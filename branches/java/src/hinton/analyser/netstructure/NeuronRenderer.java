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
 * Created on 09.03.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package hinton.analyser.netstructure;

import hinton.analyser.Analyser;
import hinton.analyser.toolkit.InputComponent;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JToolTip;

import cholsey.Neuron;
import cholsey.NeuronType;


/**
 * @author rosemann
 *
 * This class represents the visual representation of a Neuron
 * Neurons are interpreted as the nodes of the digraph
 */
public abstract class NeuronRenderer extends ComponentRenderer
  implements Runnable,
             Scalable
{                                   
  protected int id;
  protected double activation;
  protected double output;
  protected double bias;
  protected int type;
  protected ArrayList locationObservers = new ArrayList();

  private Neuron neuron;
  private int    dragOffsetX = 0;
  private int    dragOffsetY = 0;
  private double currFactor  = 1;
  private double w,h,l,t;

  private Thread observerNotifier;

  public NeuronRenderer() {
    super();
  }

  public NeuronRenderer(VisualNet rendererContainer, Object initialData) {
    super(rendererContainer, initialData);
  }

  /* (non-Javadoc)
   * @see Hinton.analyser.netstructure.ComponentRenderer#initialize(java.lang.Object)
   */
  synchronized public void initialize(Object initialData) {
    try
    {
      this.updateData(initialData);
    }
    catch (Exception e)
    {
      //TODO remove printstacktrace
      e.printStackTrace();
      //Nothing
    }
  }

  /* (non-Javadoc)
   * @see Hinton.analyser.netstructure.ComponentRenderer#updateData(java.lang.Object)
   */
  synchronized public final void updateData(Object data)
    throws UnsupportedDataTypeException {
    Neuron neuron;
    try {
      neuron = (Neuron) data;

      this.neuron = neuron;
      this.id = neuron.id();
      this.activation = neuron.getActivation();
      this.output = neuron.getOutput();
      this.bias = neuron.getBias();
      this.type = neuron.getNeuronType().type();

    } catch (ClassCastException cc) {
      throw new UnsupportedDataTypeException("Data of type Cholsey.Neuron expected!");
    }
  }

  synchronized public final void paintComponent(Graphics g)
  {
    Graphics2D g2d           = (Graphics2D)g;
    Color      color;
    double     abs_output    = Math.abs(this.output);
    float      colorGradient = (float)(1.0d - abs_output);
    Shape      neuronShape   = this.getNeuronShape();


    colorGradient = Math.max(0.0f, Math.min(1.0f, colorGradient));
    if (this.output > 0)
    {
      color = new Color(1.0f, colorGradient, colorGradient);
    }
    else
    {
      color = new Color(colorGradient, colorGradient, 1.0f);
    }

    g.setColor(color);
    g2d.fill(neuronShape);
    switch (this.type)
    {
      case NeuronType.NEURON_TYPE_OUTPUT : g2d.setPaint(Color.GREEN);
                                           break;
      case NeuronType.NEURON_TYPE_INPUT  : g2d.setPaint(Color.RED);
                                           break;
      case NeuronType.NEURON_TYPE_HIDDEN : g2d.setPaint(Color.BLUE);
                                           break;                                          
    }
    g2d.setStroke(new BasicStroke(2.0f));
    g2d.draw(neuronShape);
    g2d.setColor(Color.BLACK);
  }

  /**
   * Returns the neuron represented by this renderer
   * @return the neuron represented by this renderer
   */
  public final Neuron getNeuron() {
    return this.neuron;
  }

  /**
   * This method unregisters a previous registered NeuronLocationObserver
   * @param locationObserver the NeuronLocationObserver to be unregistered
   */
  public final void unregisterLocationObserver(NeuronLocationObserver locationObserver) {
    this.locationObservers.remove(locationObserver);
  }

  /**
   * This method registers a NeuronLocationObserver
   * @param locationObserver the NeuronLocationObserver to be registered
   */
  public final void registerLocationObserver(NeuronLocationObserver locationObserver) {
    this.locationObservers.add(locationObserver);
  }

  /**
   * This method notifies the registered NeuronLocationObservers
   */
  protected final void callLocationObservers()
  {
    /*if (this.observerNotifier == null)
      {
      this.observerNotifier = new Thread(this);
      this.observerNotifier.start();
      }

      while (this.observerNotifier.isAlive())
      {
      Thread.yield();
      }*/
    NeuronLocationObserver[] lObserver =
      (NeuronLocationObserver[]) this.locationObservers.toArray(
                                                                new NeuronLocationObserver[0]);

    Rectangle location =
      new Rectangle(
          this.getX(),
          this.getY(),
          this.getWidth(),
          this.getHeight());

    for (int i = 0; i < lObserver.length; i++) {
      lObserver[i].computeLocationUpdate(location, this.id);
    }

  }

  public final void run()
  {
    try
    {
      NeuronLocationObserver[] lObserver =
        (NeuronLocationObserver[]) this.locationObservers.toArray(
                                                                  new NeuronLocationObserver[0]);

      Rectangle location =
        new Rectangle(
            this.getX(),
            this.getY(),
            this.getWidth(),
            this.getHeight());

      for (int i = 0; i < lObserver.length; i++) {
        lObserver[i].computeLocationUpdate(location, this.id);
      }
    }
    catch (Exception e)
    {

    }
  }

  public final void addNotify()
  {
    super.addNotify();
    this.w = this.getWidth();
    this.h = this.getHeight();
    this.l = this.getX();
    this.t = this.getY();
  }

  /*
   *  (non-Javadoc)
   * @see Hinton.analyser.netstructure.Scalable#scale(double)
   */
  public final void scale(double factor)
  {
    double scale = factor / this.currFactor;
    this.w = this.w * scale;
    this.h = this.h * scale;
    this.l = this.getX() * scale + 0.5d;
    this.t = this.getY() * scale + 0.5d;
    this.setSize((int)(this.w), (int)(this.h));
    this.setLocation((int)(this.l), (int)(this.t));
    this.currFactor = factor;
  }

  /**
   * @see hinton.analyser.netstructure.ComponentRenderer#getRendererTypeProperties()
   */
  public final InputComponent getRendererTypeProperties() {
    return getConcreteRenderingProperties();
  }

  /**
   * This method has to be implemented when subclassing NeuronRenderer.
   * 
   * @return the appearance of the Neuron as Shape
   */
  public abstract Shape getNeuronShape();

  /**
   * This method has to be implemented when subclassing NeuronRenderer.
   * 
   * @return user definable properties of the concrete NeuronRenderer
   */
  public abstract InputComponent getConcreteRenderingProperties();

  /**
   * This method has to be implemented when subclassing NeuronRenderer.
   * 
   * Junctures of a NeuronRenderer are used to connect a SynapseRenderer to a
   * neuron. The junctures are represented by an array of points in the
   * coordinate space of the container of the NeuronRenderer 
   * 
   * @return junctures of the NeuronRenderer
   */
  public abstract Point[] getJunctures();

  /**
   * This method has to be implemented when subclassing NeuronRenderer.
   * 
   * The method is meant to be used whenever the shape of the NeuronRenderer
   * changed and the junctures have to be recalculated
   */
  protected abstract void computeJunctures();



  /*
   *  (non-Javadoc)
   * @see java.awt.Component#setSize(java.awt.Dimension)
   */
  public final void setSize(Dimension d)
  {
    this.setSize(d.width, d.height);
  }

  /*
   *  (non-Javadoc)
   * @see java.awt.Component#setSize(int, int)
   */
  public final void setSize(int w, int h)
  {
    super.setSize(w, h);
    super.computeDescription(1.0d, 0.0d,
        new Point(getWidth()/2 - 5, getHeight()/2 + 5),
        String.valueOf((this.id + 1)));
    this.computeJunctures();
    this.callLocationObservers();
  }


  /* (non-Javadoc)
   * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
   */
  synchronized public final void mousePressed(MouseEvent event) {
    this.dragOffsetX = event.getX();
    this.dragOffsetY = event.getY();
  }

  /* (non-Javadoc)
   * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
   */
  synchronized public final void mouseDragged(MouseEvent e) {
    setLocation(
        e.getX() + this.getX() - this.dragOffsetX,
        e.getY() + this.getY() - this.dragOffsetY);
  }

  /* (non-Javadoc)
   * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
   */
  public final void mouseMoved(MouseEvent e) {
    // Nothing

  }


  /* (non-Javadoc)
   * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
   */
  public void mouseClicked(MouseEvent e) {
    // NOTHING
  }

  /* (non-Javadoc)
   * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
   */
  public void mouseEntered(MouseEvent e) {
    // NOTHING

  }

  /* (non-Javadoc)
   * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
   */
  public void mouseExited(MouseEvent e) {
    // NOTHING

  }

  /* (non-Javadoc)
   * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
   */
  public void mouseReleased(MouseEvent e) {
    // NOTHING

  }


  public final void setLocation(int x, int y) {
    super.setLocation(x, y);
    this.computeJunctures();
    this.callLocationObservers();
  }

  public final int hashCode() {
    return this.id;
  }

  public JToolTip createToolTip() {
    return new NeuronToolTip(this);
  }

  /**
   * @author rosemann
   *
   * The custom NeuronRenderer Tooltip
   */
  private static class NeuronToolTip extends JToolTip {
    NeuronRenderer nr;

    NeuronToolTip(NeuronRenderer nr) {
      super.setComponent(nr);
      super.setTipText("");
      this.nr = nr;
      this.setSize(250, 120);
    }

    public Dimension getPreferredSize() {
      return this.getSize();
    }

    public Dimension getMinimumSize() {
      return this.getSize();
    }

    public Dimension getMaximumSize() {
      return this.getSize();
    }

    public void paint(Graphics g) {

      g.setColor(new Color(165, 185, 255));
      g.fillRect(0, 0, this.getWidth(), this.getHeight());
      g.setColor(Color.BLACK);
      g.drawRect(0, 0, this.getWidth() - 1, 20);
      g.drawRect(0, 21, this.getWidth() - 1, this.getHeight() - 21);

      g.setFont(g.getFont().deriveFont(Font.BOLD));
      g.drawString("Neuron", 5, 15);
      g.drawString("ID", 5, 35);
      g.drawString("Name", 5, 45);
      g.drawString("Type", 5, 55);
      g.drawString("Activation", 5, 65);
      g.drawString("Output", 5, 75);
      g.drawString("Bias", 5, 85);
      g.drawString("Receptorlevel", 5, 95);
      g.drawString("Transmitterlevel", 5, 105);

      g.setFont(g.getFont().deriveFont(Font.PLAIN));
      g.drawString(String.valueOf((this.nr.id+1)), 105, 35);
      g.drawString(
          Analyser.getInstance().getNeuronName(this.nr.id),
          105,
          45);
      g.drawString(String.valueOf(this.nr.activation), 105, 65);
      g.drawString(String.valueOf(this.nr.output), 105, 75);
      g.drawString(String.valueOf(this.nr.bias), 105, 85);
      g.drawString(
          String.valueOf(nr.getNeuron().getReceptorLevel()),
          105,
          95);
      g.drawString(
          String.valueOf(nr.getNeuron().getTransmitterLevel()),
          105,
          105);

      switch (nr.type) {
        case NeuronType.NEURON_TYPE_INPUT :
          g.drawString(NeuronType.INPUT.toString(), 105, 55);
          break;
        case NeuronType.NEURON_TYPE_HIDDEN :
          g.drawString(NeuronType.HIDDEN.toString(), 105, 55);
          break;
        case NeuronType.NEURON_TYPE_OUTPUT :
          g.drawString(NeuronType.OUTPUT.toString(), 105, 55);
          break;
      }
    }
  }
}
