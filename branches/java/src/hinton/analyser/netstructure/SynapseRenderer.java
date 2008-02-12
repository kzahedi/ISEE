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

import hinton.analyser.toolkit.InputComponent;
import hinton.analyser.toolkit.InputValue;
import hinton.analyser.toolkit.InputValueListener;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import javax.swing.JToolTip;

import cholsey.Synapse;
import cholsey.SynapseMode;
import cholsey.SynapseType;



/**
 * @author rosemann
 * This class implements the visual representation of a Synapse.
 * Synapses are interpreted as the edges of the digraph
 */
public abstract class SynapseRenderer extends    ComponentRenderer
                                      implements NeuronLocationObserver
                                                
{

  protected int     synapseMode;
  protected int     sourceNeuronID;
  protected int     destinationNeuronID;
  protected double  strength;
  protected Point   sourceNeuronPosition;
  protected Point   destinationNeuronPosition;
  protected Point[] samplingPoints;
  
  protected static final float DEFAULT_LINE_WIDTH = 6.0f;
  protected static InputValue.IObject ses = new InputValue.IObject(
                                              "Synapse-End Style",
                                              0, 
                                              SynapseEndStyle.STYLES);
  protected static InputValue.IInteger lwidth = new InputValue.IInteger(
                                              "Maximum Line Width",
                                              (int)DEFAULT_LINE_WIDTH, 1, 15);
  static
  {
    SynapseRenderer.lwidth.addInputValueListener(new InputValueListener() {
      public void inputValueUpdated(InputValue iv) {
        SynapseRenderer.setMaxLineWidth(
            (float)((InputValue.IInteger)SynapseRenderer.lwidth).value);
      }
    });
  }
  
  private   int     selfReactionRadius = SynapseRenderer.DEFAULT_RADIUS; 
  private   Synapse synapse;  
  private   Polygon endStyle;
  private   double  currFactor = 1;
  
  
  private   static final int   DEFAULT_RADIUS     = 20;
  
  protected static double maxNetSynapseStrength = 1;
  protected static float  maxLineWidth          = 
                                  (float)SynapseRenderer.lwidth.value;
  public static float  lineWidthStep         = 
                          (float)(SynapseRenderer.maxLineWidth /
                                  SynapseRenderer.maxNetSynapseStrength);

  public SynapseRenderer() 
  {
    super();
  }
                                  
  public SynapseRenderer(VisualNet rendererContainer,
                         Object    initialData)
  {
    super(rendererContainer, initialData);
    this.samplingPoints = new Point[2];
    this.samplingPoints[0] = new Point(0,0);
    this.samplingPoints[1] = new Point(0,0);
    ses.addInputValueListener(new InputValueListener() {
      public void inputValueUpdated(InputValue iv)
      {
        lineEndStyleUpdated();
      }
    }); 
  }
  
  
  
  
  /**
   * Draws a Synapse with selfreacting character.
   * Painting of non-selfreacting synapses is deligated to
   * paintNonSelfReact(Graphics g) 
   */
  public final void paintComponent(Graphics g)
  {
    Graphics2D  g2d      = (Graphics2D)g;
    float       penWidth = 
              (float)(Math.abs(this.strength) * SynapseRenderer.lineWidthStep);
    BasicStroke pen      = new BasicStroke(penWidth);
    int         diameter = this.rendererContainer.
                           getNeuronRenderer(this.sourceNeuronID).getWidth();
    if(this.synapseMode == SynapseMode.SYNAPSE_MODE_DYNAMIC)
    {
      g2d.setColor((this.synapse.type().type() ==
            SynapseType.SYNAPSE_TYPE_EXCITATORY ? Color.RED : Color.BLUE));
    }
    else
    {
      g2d.setColor((this.strength > 0 ? Color.RED : Color.BLUE));
    }
    g2d.setStroke(pen);
    
    if (this.sourceNeuronID == this.destinationNeuronID)
    {
    
      
      g2d.drawOval((int)(42 + diameter/2.0d - penWidth),
                   (int)(42/* + diameter/2.0d - penWidth*/),
                   (int)(diameter * 1.1d - penWidth),
                   (int)(diameter * 1.1d - penWidth));
                   
    }
    else
    {
      g2d.draw(this.getSynapseShape());
      g2d.fill(this.endStyle);
      if (this.isSelected())
      {
        g2d.setColor(Color.YELLOW);
        for (int i = 0; i < this.samplingPoints.length; i++)
        {
          g2d.fillRect(this.samplingPoints[i].x - 3,
                       this.samplingPoints[i].y - 3,
                       6, 6);
        }
      }
    }
    
  }
  
  /**
   * Returns the samplingPoints of this SynapseRenderer
   * @return samplingPoints
   */
  public final Point[] getSamplingPoints()
  {
    Point[] ret = new Point[this.samplingPoints.length];
    
    System.arraycopy(this.samplingPoints, 0, ret, 0, ret.length);
    
    return ret;
  }
  
  /**
   * Sets the samplingPoints of this SynapseRenderer
   * @param samplingPoints progression of the SynapseRenderer
   */
  public final void setSamplingPoints(Point[] samplingPoints)
  {
    this.samplingPoints = new Point[samplingPoints.length];
    
    System.arraycopy(samplingPoints, 0, 
                     this.samplingPoints, 0, 
                     this.samplingPoints.length);
                     
    //to force redraw even though the Bounds haven't changed
    this.computeLocationUpdate(this.rendererContainer.getNeuronRenderer(
                              this.sourceNeuronID).getBounds(),
                              this.sourceNeuronID);
    this.repaint();
    
  }
  
  /**
   * This method has to be implemnted when subclassing SynapseRenderer
   * 
   * @return the shape of the synapse to be rendered
   */
  public abstract Shape getSynapseShape();
  
  

  /**
   * This method has to be implemented when subclassing SynapseRenderer.
   * 
   * @return user definable properties of the concrete SynapseRenderer
   */
  public abstract InputComponent getConcreteRenderingProperties();
  
  /**
   * This method has to be implemented when subclassing SynapseRenderer.
   * 
   * This method is called whenever the dimension of the SynapseRenderer
   * has to be recalculated. 
   * 
   * Here you have to place code to check if all samplingpoints are inside
   * the componentbounds
   */
  public abstract void locationUpdated();
  
  /**
   * Set the maximumpenwidth of a synapseRenderer
   * @param maxWidth
   */
  public static final void setMaxLineWidth(float maxWidth)
  {
    SynapseRenderer.maxLineWidth  = maxWidth;
    SynapseRenderer.lineWidthStep = (float)(SynapseRenderer.maxLineWidth /
                                    SynapseRenderer.maxNetSynapseStrength);

  }
  
  /**
   * set the strength of the strongest synapse in the net
   * 
   * The maximum synapse strength is used to calculate the penwidth
   * for a synapse to be drawn 
   * 
   * @param maxStrength
   */
  private static final void setMaxNetSynapseStrength(double maxStrength)
  {
    SynapseRenderer.maxNetSynapseStrength = maxStrength;
  }
  
  /**
   * Reset the maximum synpase strength for the net.
   */
  public static final void resetNetSynapseStrength()
  {
    SynapseRenderer.maxNetSynapseStrength = 0;
  }
  
  /*
   * (non-Javadoc)
   * @see Hinton.analyser.netstructure.ComponentRenderer#initialize(java.lang.Object)
   */
  public final void initialize(Object initialData)
  {
    VisualNet      visNet;
    NeuronRenderer neuronRenderer;
    Point          start, end;
    Point[]        cut;
    Synapse        synapse;
    
    try
    {
      this.updateData(initialData);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      //Nothing
    }
    this.samplingPoints    = new Point[2];
    this.samplingPoints[0] = new Point(0,0);
    this.samplingPoints[1] = new Point(0,0);
    visNet                 = this.rendererContainer;

    neuronRenderer = visNet.getNeuronRenderer(this.sourceNeuronID);
    neuronRenderer.registerLocationObserver(this);
    this.sourceNeuronPosition = neuronRenderer.getLocation();
              
    neuronRenderer = visNet.getNeuronRenderer(this.destinationNeuronID);
    if (this.destinationNeuronID != this.sourceNeuronID)
    {
      neuronRenderer.registerLocationObserver(this);
    }
    this.destinationNeuronPosition = neuronRenderer.getLocation();
    computeLocationUpdate(neuronRenderer.getBounds(), this.destinationNeuronID);
  
  } 

  /* (non-Javadoc)
   * @see Hinton.analyser.netstructure.ComponentRenderer#updateData(java.lang.Object)
   */
  public final void updateData(Object data) 
      throws UnsupportedDataTypeException 
  {
    Synapse        synapse;
        
    try
    { 
      synapse = (Synapse)data;
      this.synapse             = synapse;
      this.synapseMode         = synapse.mode().mode();
      this.sourceNeuronID      = synapse.getSource().id();
      this.destinationNeuronID = synapse.getDestination().id();
      this.strength            = synapse.strength();
      
      this.setDescription();
      
      if (Math.abs(this.strength) > SynapseRenderer.maxNetSynapseStrength)
      {
        SynapseRenderer.maxNetSynapseStrength = Math.abs(this.strength);    
        SynapseRenderer.lineWidthStep         =
                        (float)(SynapseRenderer.maxLineWidth /
                                SynapseRenderer.maxNetSynapseStrength);
      }
    }
    catch (ClassCastException ccf)
    {
      ccf.printStackTrace();
      throw new UnsupportedDataTypeException(
        "Data of type Cholsey.Synapse expected!");
    }
        
  }
  /**
   * Returns the synapse represented by this SynapseRenderer
   * @return synpase represented by this synapseRenderer
   */
  public final Synapse getSynapse()
  {
    return this.synapse;
  }
  
  /**
   * This method is called to calculate the required dimension of
   * the SynapseRenderer Component and the location of the first and
   * last samplingPoint
   * @param start location of the sourceNeuron
   * @param end   location of the destinationNeuron
   */
  protected final void computeDimension(Point start, Point end)
  {
    int    left, top, width, height;
    int    offset = 42;
    int    dim;
    int    endPointIdx;
    
    left   = Math.min(start.x, end.x) - offset;
    top    = Math.min(start.y, end.y) - offset;
    width  = Math.abs(start.x - end.x);
    height = Math.abs(start.y - end.y);
    
    if (this.destinationNeuronID == this.sourceNeuronID)
    {
      dim = offset + 
            (int)(this.rendererContainer.
                 getNeuronRenderer(this.sourceNeuronID).getWidth() * 3.0d); 
    } else {
      dim = 2*offset;
    }
    width  += dim;
    height += dim; 
    
    
    if (this.samplingPoints == null)
    {
      this.samplingPoints = new Point[0];
    }
    if (this.samplingPoints[0] == null ||
        this.samplingPoints[1] == null)
    {
      this.samplingPoints[0] = new Point(0,0);
      this.samplingPoints[1] = new Point(0,0);
    }
    
    if (this.sourceNeuronID != this.destinationNeuronID)
    {
      endPointIdx = this.samplingPoints.length - 1;
      /* Calculate horizontal Position of start and end point relative
        to the Clip-Area of the component and the horizontal position and
        width of the component */
      if (end.x > start.x)
      {
        this.samplingPoints[0].x           = offset;
        this.samplingPoints[endPointIdx].x = end.x - start.x + offset; 
      }
      else
      {
        this.samplingPoints[0].x           = start.x - end.x + offset;
        this.samplingPoints[endPointIdx].x = offset;
      }
      
      /* ...and now the vertical stuff */
      if (end.y > start.y)
      {
        this.samplingPoints[0].y           = offset;
        this.samplingPoints[endPointIdx].y = end.y - start.y + offset;
        
      }
      else
      {
        this.samplingPoints[0].y           = start.y - end.y + offset;
        this.samplingPoints[endPointIdx].y = offset;
      }
      for (int i = 1; i < this.samplingPoints.length -1; i++)
      {
        this.samplingPoints[i].translate(this.getX() - left,
                                         this.getY() - top);
      }
    }
    this.setLocation(left, top);
    this.setSize(width, height);
  }
  
  /*
   *  (non-Javadoc)
   * @see Hinton.analyser.netstructure.NeuronLocationObserver#computeLocationUpdate(java.awt.Rectangle, int)
   */
  public final void computeLocationUpdate(Rectangle location,
                                                        int neuronID)
  { 
    VisualNet      visNet;
    NeuronRenderer neuronRendererSource;
    NeuronRenderer neuronRendererDestination;
    Point[]        sjunctures, djunctures;
    Point          current;
    double         currentDistance;
    double         minDistance;
    
    visNet                 = this.rendererContainer;
    
    
    neuronRendererSource       = 
              visNet.getNeuronRenderer(this.sourceNeuronID);
    neuronRendererDestination  = 
              visNet.getNeuronRenderer(this.destinationNeuronID);
    try
    {
    if (this.sourceNeuronID != this.destinationNeuronID)
    {
      djunctures                 = neuronRendererDestination.getJunctures();
      sjunctures                 = neuronRendererSource.getJunctures();
                    
      minDistance = Double.MAX_VALUE;
      current = this.samplingPoints[1].getLocation();
      current.translate(this.getX(), this.getY());
      for (int i = 0; i < sjunctures.length; i++)
      {
        currentDistance = current.distance(sjunctures[i]);
              //neuronRendererDestination.getLocation().distance(sjunctures[i]);
        if (currentDistance < minDistance)
        {
          minDistance = currentDistance;
          this.sourceNeuronPosition = sjunctures[i];
        }
      }
      
                  
      minDistance = Double.MAX_VALUE;
      current = this.samplingPoints[this.samplingPoints.length-2].getLocation();
      current.translate(this.getX(), this.getY());
      for (int i = 0; i < djunctures.length; i++)
      {
        currentDistance = current.distance(djunctures[i]);
        
              //neuronRendererSource.getLocation().distance(djunctures[i]);
        if (currentDistance < minDistance)
        {
          minDistance = currentDistance;
          this.destinationNeuronPosition = djunctures[i];
        }
      }
    }
    else
    {
      this.sourceNeuronPosition      = neuronRendererSource.getLocation();
      this.destinationNeuronPosition = this.sourceNeuronPosition;
    }
                  
    computeDimension(this.sourceNeuronPosition,
                     this.destinationNeuronPosition);
    }
    catch (Exception e)
    {
      
    }
    
    if (this.sourceNeuronID != this.destinationNeuronID)
    {      
      locationUpdated();
    }
    
    this.setDescription();
    this.setEndstyle();

  }
  
  /**
   * 
   * This method is meant to return the minimum distance to the visible edges 
   * of the SynapseRenderer, e.g. the minimum distance from point to the line
   * connecting the source and destination NeuronRenderer, 
   * if the SynapseRenderer is implemented as a straight line.
   * 
   * The method is used to decide if a SynapseRenderer is the source of
   * a MouseEvent
   * 
   * @param point 
   * @return minimal distance to the given point
   */
  public final double getMinimumOutlineDistance(Point2D point)
  {
    boolean       isNear = false;
    BufferedImage im = new BufferedImage(this.getWidth(),
        this.getHeight(), BufferedImage.TYPE_INT_RGB);
    Graphics2D    g2d = im.createGraphics();
    
    g2d.setBackground(Color.BLACK);
    g2d.setColor(Color.RED);
    g2d.setStroke(new BasicStroke(10.0f));
    if (this.destinationNeuronID != this.sourceNeuronID)
    {
      g2d.draw(this.getSynapseShape());
    }
    else
    {
      int         diameter = this.rendererContainer.
       getNeuronRenderer(this.sourceNeuronID).getWidth();
      g2d.drawOval((int)(42 + diameter/2.0d - 5),
           (int)(42/* + diameter/2.0d - penWidth*/),
           (int)(diameter * 1.1d),
           (int)(diameter * 1.1d));
    }
    
    
    isNear = (Color.RED.getRGB() == 
        im.getRGB((int)point.getX(), (int)point.getY()));
    
    
    if (isNear)
    {
      return 0.0d;
    }
    else
    {
      return Double.MAX_VALUE;
    }
  }
  
  
  private void setEndstyle()
  {
    int idx;
    int vx, vy;
    
    idx = this.samplingPoints.length - 1;
    /* Set SynapseEndStyle */
    vx            = this.samplingPoints[idx - 1].x -
                    this.samplingPoints[idx].x;
    vy            = this.samplingPoints[idx - 1].y -
                    this.samplingPoints[idx].y;                       
    this.endStyle = ((SynapseEndStyle)SynapseRenderer.ses.value).
        getLineEnd(vx, vy,
        this.samplingPoints[idx],
        this.strength); 
  }
  
  private void setDescription()
  {
    try
    {
    double vx, vy, vnx, vny;
    double lengthV;
    int    countSP = this.samplingPoints.length - 1;
    int    idx1;
    int    idx2;
    int    x, y;
    boolean isSwap = false;
    Point  p;
    
    if (countSP % 2 == 0)
    {
      countSP--;
    }
    idx1 = (int)Math.floor(countSP / 2.0d);
    idx2 = idx1 + 1;
    //o.B.d.A (what is this in english?) point[idx1].x < point[idx2].x
    //this guaranties, that the description is not upside down
    if (this.samplingPoints[idx2].x < this.samplingPoints[idx1].x)
    {
      x      = idx1;
      idx1   = idx2;
      idx2   = x;
      isSwap = true;
    }
    
    if (this.sourceNeuronID != this.destinationNeuronID)
    {
      vx      = this.samplingPoints[idx2].x -
                this.samplingPoints[idx1].x;
      vy      = this.samplingPoints[idx2].y -
                this.samplingPoints[idx1].y;
      x       = (int)((this.samplingPoints[idx2].x + this.samplingPoints[idx1].x) 
                      / 2.0d);
      y       = (int)((this.samplingPoints[idx2].y + this.samplingPoints[idx1].y) 
                      / 2.0d);
      
      lengthV = Math.sqrt(vx*vx + vy*vy);
      vx      = vx / lengthV;
      vy      = vy / lengthV;     
      
      p =  new Point((int)(x - 25 * vx),
                     (int)(y - 25 * vy));
  
      lengthV = Math.sqrt(vx*vx + vy*vy);
      if (!isSwap)
      {
        vnx = -vy / lengthV;
        vny =  vx / lengthV;
      }
      else
      {
        vnx =  vy / lengthV;
        vny = -vx / lengthV;
      }
      lengthV = 25;
      
      p.x += vnx * lengthV;
      p.y += vny * lengthV;
    
    } 
    else
    {
      p = new Point(
                42 + 
                this.rendererContainer.getNeuronRenderer(
                              this.sourceNeuronID).getWidth(),
                42);
      vx = 1.0d;
      vy = 0.0d;
    }
    
    DecimalFormat   df = new DecimalFormat("0.000");
    if (!Double.isNaN(vx) && !Double.isNaN(vy))
    {
      computeDescription(vx, vy, p, df.format(this.strength));
    }
    }
    catch (Exception e)
    {
      
    }
  }
  
  /*
   *  (non-Javadoc)
   * @see javax.swing.JComponent#createToolTip()
   */
  public JToolTip createToolTip()
  {
    return new SynapseToolTip(this);
  }


  /* (non-Javadoc)
   * @see Hinton.analyser.netstructure.ComponentRenderer#getRenderingProperties()
   */
  public InputComponent getRendererTypeProperties() {
    InputComponent.Collection myCollection = 
            new InputComponent.Collection("Synapse Renderer");
    InputComponent localProps = getConcreteRenderingProperties();
    myCollection.addElement(SynapseRenderer.ses);
    myCollection.addElement(SynapseRenderer.lwidth);
    
    if (localProps != null)
    {
      myCollection.addElement(localProps);
    }
    return myCollection;
  }

  public final void lineEndStyleUpdated()
  {
    // to force redraw even though the Bounds haven't changed
    this.computeLocationUpdate(this.rendererContainer.getNeuronRenderer(
                               this.sourceNeuronID).getBounds(),
                               this.sourceNeuronID);
    this.repaint();
  }
  
  /* (non-Javadoc)
   * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
   */
  public void mouseDragged(MouseEvent e) {
    // NOTHING
    
  }

  /* (non-Javadoc)
   * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
   */
  public void mouseMoved(MouseEvent e) {
    // NOTHING    
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
   * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
   */
  public void mousePressed(MouseEvent e) {
    // NOTHING
    
  }

  /* (non-Javadoc)
   * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
   */
  public void mouseReleased(MouseEvent e) {
    // NOTHING
    
  }
  
  /**
   *
   * @author rosemann
   *
   * The custom SynapseRenderer ToolTip
   */
  private static class SynapseToolTip extends JToolTip
  {
    SynapseRenderer sr;
  
    SynapseToolTip(SynapseRenderer sr)
    {
      super.setComponent(sr);
      super.setTipText("");
      this.sr = sr;
      this.setSize(250, 75);  
    }
  
    public Dimension getPreferredSize()
    {
      return this.getSize();
    }
  
    public Dimension getMinimumSize()
    {
      return this.getSize();
    }
  
    public Dimension getMaximumSize()
    {
      return this.getSize();
    }
    
  
    public void paintComponent(Graphics g)
    {
      g.setColor(new Color(255, 255, 175));
      g.fillRect(0, 0, this.getWidth(), this.getHeight());
      g.setColor(Color.BLACK);
      g.drawRect(0, 0,  this.getWidth()-1, 20);
      g.drawRect(0, 21, this.getWidth()-1, this.getHeight() - 21);
    
      g.setFont(g.getFont().deriveFont(Font.BOLD));
      g.drawString("Synapse", 5, 15);
      g.drawString("Synapsemode", 5, 35);
      g.drawString("Strength", 5, 45);
      g.drawString("Source", 5, 55);
      g.drawString("Destination", 5, 65);
      
    
      g.setFont(g.getFont().deriveFont(Font.PLAIN));
      switch (this.sr.synapseMode)
      {
        case SynapseMode.SYNAPSE_MODE_CONVENTIONAL :
                g.drawString(SynapseMode.CONVENTIONAL.toString(), 105, 35);
                break;
        case SynapseMode.SYNAPSE_MODE_DYNAMIC :
                g.drawString(SynapseMode.DYNAMIC.toString(), 105, 35);
                break;        
      }
      g.drawString(String.valueOf(this.sr.strength), 105, 45);
      g.drawString(String.valueOf(this.sr.sourceNeuronID), 105, 55);
      g.drawString(String.valueOf(this.sr.destinationNeuronID), 105, 65);
    }
  }

}
