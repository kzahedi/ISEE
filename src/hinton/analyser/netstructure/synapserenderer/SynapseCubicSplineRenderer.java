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
 * Created on 07.06.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package hinton.analyser.netstructure.synapserenderer;

import hinton.analyser.netstructure.PersistentRendererOption;
import hinton.analyser.netstructure.SynapseRenderer;
import hinton.analyser.netstructure.VisualNet;
import hinton.analyser.toolkit.InputComponent;
import hinton.analyser.toolkit.InputValue;
import hinton.analyser.toolkit.InputValueListener;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * @author rosemann
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SynapseCubicSplineRenderer extends SynapseRenderer
																			  implements InputValueListener
{
	
	private double[][] A;
	private double[]   deltay;
	private double[]   deltax;
	private double[]   t;
	private double[][] Sx, Sy;
	private Point      upperleft;
	private Point      lowerright;
	
	private Point   		dragStart;
	private int     		insertInLineSegment;
	private boolean 		isExistingPoint;
	private int         draggedPointIndex = Integer.MAX_VALUE;
	private InputValue.IBoolean[] toggleSamplingPoints;
	private GeneralPath shape;
	
	
	/**
	 * 
	 */
	public SynapseCubicSplineRenderer() {
		super();
		this.upperleft = new Point(0,0);
		this.lowerright = new Point(0,0);
	}
	/**
	 * @param rendererContainer
	 * @param initialData
	 */
	public SynapseCubicSplineRenderer(VisualNet rendererContainer,
			Object initialData) {
		super(rendererContainer, initialData);
		this.upperleft = new Point(0,0);
		this.lowerright = new Point(0,0);
	}
	
	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.SynapseRenderer#getSynapseShape()
	 */
	public Shape getSynapseShape() {
		if (this.shape == null)
		{
			this.computeSpline();
		}
		return this.shape;
	}
	
	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.SynapseRenderer#getConcreteRenderingProperties()
	 */
	public InputComponent getConcreteRenderingProperties() {
		InputComponent.Collection myCollection;
		
		if (this.samplingPoints.length > 2)
		{
			myCollection = new InputComponent.Collection("Cubic Spline Renderer");
			
			this.toggleSamplingPoints = 
					 new InputValue.IBoolean[this.samplingPoints.length - 2];
		
			for (int i = 0; i < this.toggleSamplingPoints.length; i++)
			{
				this.toggleSamplingPoints[i] = new InputValue.IBoolean(
																			 "Remove Samplingpoint " + (i + 1),
																			 false);
			  this.toggleSamplingPoints[i].addInputValueListener(this);
				myCollection.addElement(this.toggleSamplingPoints[i]);																	 
			}
		}
		else
		{
			myCollection = null;
		}
		
		return myCollection;
	}
	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.SynapseRenderer#locationUpdated()
	 */
	public void locationUpdated() {
		Rectangle shapebounds;
		AffineTransform af;
		this.computeSpline();
		int lb, ub;
		
		try
		{
			if (this.lowerright != null && this.upperleft != null)
			{
				if (this.upperleft.x < 0)
				{
					lb = this.upperleft.x;
				}
				else
				{
					lb = 0;
				}
				
				if (this.upperleft.y < 0)
				{
					ub = this.upperleft.y;
				}
				else
				{
					ub = 0;
				}
				shapebounds = new Rectangle(this.getX() + lb, this.getY() + ub,
																	this.lowerright.x - this.upperleft.x,
																	this.lowerright.y - this.upperleft.y);
			
				this.setBounds(shapebounds);
				if (lb != 0 || ub != 0);
				{
					af = AffineTransform.getTranslateInstance(-lb, -ub);
					this.shape.transform(af);
					for (int i = 0; i < this.samplingPoints.length; i++)
					{
						this.samplingPoints[i].translate(-lb, -ub);
					}
				}
				
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.ComponentRenderer#getRendererName()
	 */
	public String getRendererName() {
		return "Cubic Spline Renderer";
	}
	
	private void computeSpline()
	{
		Point2D.Double ctrl1, ctrl2;
		CubicCurve2D segment;
		double x, y;
		double[] dx = new double[this.samplingPoints.length];
		double[] dy = new double[this.samplingPoints.length];
		double lastarclength = 0.0;
		
		for (int i = 0; i < this.samplingPoints.length; i++)
		{
			dx[i] = this.samplingPoints[i].x;
			dy[i] = this.samplingPoints[i].y;
		}
		
		
		if (this.samplingPoints.length > 2)
		{
			this.computeDistances(true);
			/*do
			{*/
				lastarclength = this.t[this.t.length - 1];
				this.computeCoefficientMatrix();
				this.computeResultVectors();
				this.performLRfactorisation();
				this.performLd();
				this.Sx = this.computePolynomials(dx, this.deltax);
				this.Sy = this.computePolynomials(dy, this.deltay);
			/*	this.computeDistances(false);
			}	while (Math.abs(lastarclength - this.t[this.t.length - 1]) > 0.5d);*/
			ctrl1 = new Point2D.Double();
			ctrl2 = new Point2D.Double();
			this.shape = new GeneralPath();
			
			Point2D.Double curr = new Point2D.Double();
			this.upperleft = new Point();
			this.lowerright = new Point();
			this.shape.moveTo(this.samplingPoints[0].x, this.samplingPoints[0].y);
			for (int k = 1; k < this.samplingPoints.length; k++)
			{					
				double j = 0.1;
				while (j < this.t[k] - this.t[k-1])
				{
					curr.x = this.getValue(this.Sx[k-1], j);
					curr.y = this.getValue(this.Sy[k-1], j);
					j += 1;
					
					if (curr.x < this.upperleft.x)
					{
						this.upperleft.x = (int)curr.x - 50;
					}
					
					if (curr.y < this.upperleft.y)
					{
						this.upperleft.y = (int)curr.y - 50;
					}
					
					if (curr.x > this.lowerright.x)
					{
						this.lowerright.x = (int)(curr.x + 50);
					}
					
					if (curr.y > this.lowerright.y)
					{
						this.lowerright.y = (int)(curr.y + 50);
					}
					
					this.shape.lineTo((float)curr.x, (float)curr.y);
					this.shape.moveTo((float)curr.x, (float)curr.y);
				}
			}
		}
		else
		{
			this.upperleft = this.getLocation();
			this.lowerright = new Point(this.getX() + this.getWidth(),
					this.getY() + this.getHeight());
			this.shape = new GeneralPath();
			this.shape.append(new Line2D.Double(this.samplingPoints[0].x,
																		      this.samplingPoints[0].y,
																		      this.samplingPoints[1].x,
																		      this.samplingPoints[1].y), true);
		}
	}
	
	private void computeDistances(boolean init)
	{
		Point2D.Double last, curr;
		
		this.t = new double[this.samplingPoints.length];
		this.t[0] = 0.0;
		if (init)
		{
			for (int i = 1; i < this.samplingPoints.length; i++)
			{
				this.t[i] = this.t[i-1] +
						this.samplingPoints[i].distance(this.samplingPoints[i-1]);
			}
		}
		else
		{
			last = new Point2D.Double();
			curr = new Point2D.Double();
			for (int i = 1; i < this.samplingPoints.length; i++)
			{
				this.t[i] = this.t[i-1];
				last.x = this.samplingPoints[i-1].x;
				last.y = this.samplingPoints[i-1].y;
				double j = 0.1;
				while (j < this.t[i] - this.t[i-1])
				{
					curr.x = this.getValue(this.Sx[i-1], j);
					curr.y = this.getValue(this.Sy[i-1], j);
					this.t[i] = curr.distance(last);
					j += 0.1;
					last.x = curr.x;
					last.y = curr.y;
				}
				curr.x = this.samplingPoints[i].x;
				curr.y = this.samplingPoints[i].y;
				this.t[i] += curr.distance(last);
			}
		}
		
		
		
	}
	
	private void computeCoefficientMatrix()
	{
		int i, j;
		
		this.A = new double[this.samplingPoints.length - 2]
											 [this.samplingPoints.length - 2];
		
		for (i = 0; i < this.A.length; i++)
		{
			for (j = 0; j < this.A[i].length; j++)
			{
				switch (j - i)
				{
					case -1 : this.A[i][j] = this.t[i+1] - this.t[i]; 
										break;
					case  0 : this.A[i][j] = 2.0d*(this.t[i+2] - this.t[i]);
										break;
					case  1 : this.A[i][j] = this.t[i+2] - this.t[i+1];
										break;
					default : this.A[i][j] = 0.0d;
				}
			}
		}
	}
	
	private void computeResultVectors()
	{
		int i, j;
		double hi, hi_1;
		
		this.deltax = new double[this.samplingPoints.length-2];
		this.deltay = new double[this.samplingPoints.length-2];
		
		for (j = 0, i = 1; j < this.deltax.length; j++, i++)
		{
			hi   = this.t[i] - this.t[i-1];
			hi_1 = this.t[i+1] - this.t[i];
			
			this.deltax[j]  = (this.samplingPoints[i+1].x - this.samplingPoints[i].x) / 
										hi_1;
			this.deltax[j] -= (this.samplingPoints[i].x - this.samplingPoints[i-1].x) / 
										hi;
			this.deltax[j] *= 3;
			
			this.deltay[j]  = (this.samplingPoints[i+1].y - this.samplingPoints[i].y) / 
										hi_1;
			this.deltay[j] -= (this.samplingPoints[i].y - this.samplingPoints[i-1].y) / 
										hi;
			this.deltay[j] *= 3;
		}
	}
	
	
	private void performLRfactorisation()
	{
		int i, j, k;
  	for (i = 0; i < this.A.length - 1; i++)
    {
    	for (j = i+1; j < this.A.length; j++)
      {
				this.A[j][i] = this.A[j][i] / this.A[i][i];
        for (k = i+1; k < this.A[j].length; k++)
        {
        	if (this.A[i][k] != 0.0d)
          {
        		this.A[j][k] = this.A[j][k] - this.A[j][i] * this.A[i][k];
          }
        }
      }
    }
	}
	
	private void performLd()
	{
		int i, j, k;
  	for (i = 0; i < this.A.length - 1; i++)
    {
    	for (j = i+1; j < this.A.length; j++)
      {
    		this.deltax[j] = this.deltax[j] - A[j][i] * this.deltax[i];
    		this.deltay[j] = this.deltay[j] - A[j][i] * this.deltay[i];
      }
    }
	}
	
	private double[][] computePolynomials(double[] d, double delta[])
	{
		int i, j, k;
		double[] a = new double[this.samplingPoints.length - 1];
		double[] b = new double[this.samplingPoints.length + 1];
		double[] c = new double[this.samplingPoints.length - 1];
		double[][] polynom = new double[this.samplingPoints.length - 1][4]; 
		
		b[0] = 0.0d;
		b[this.samplingPoints.length] = 0.0d;

		for (j = this.A.length - 1; j >= 0; j--)
		{
			b[j+1] = delta[j];
			for (k = j + 1; k < this.A[j].length; k++)
			{
				b[j+1] -= this.A[j][k] * b[k+1];
			}
			b[j+1] /= this.A[j][j];
		}
		
		
		for (i = 1; i < this.samplingPoints.length; i++)
		{
			a[i-1] = (b[i] - b[i-1]) / (3 * (this.t[i] - this.t[i-1]));
		}
		
		//ci = (di+1-di)/(xi+1-xi) - (bi+1-bi)(xi+1-xi)/3 - bi(xi+1-xi)     
		for (i = 0; i < c.length; i++)
		{
			
			c[i]  = (d[i+1] - d[i]);
			c[i] /= (this.t[i+1] - this.t[i]);
			c[i] -= (b[i+1] - b[i])*(this.t[i+1] - this.t[i])/3.0d;
			c[i] -= b[i]*(this.t[i+1] - this.t[i]);
		}
		
		for (i = 0; i < polynom.length; i++)
		{
			polynom[i][0] = d[i]; //d
			polynom[i][1] = c[i];
			polynom[i][2] = b[i];
			polynom[i][3] = a[i];
		}
		
		return polynom;
	}
	
	private double getValue(double[] polynom, double x)
	{
		double sum = polynom[polynom.length-1];
		
		for (int i = polynom.length-2; i >= 0; i--)
		{
			sum = sum*x + polynom[i];
		}
		return sum;
	}
	
	public static void main(String[] args)
	{
		SynapseCubicSplineRenderer scsr = new SynapseCubicSplineRenderer();
		scsr.samplingPoints = new Point[4];
		
		scsr.samplingPoints[0] = new Point(0,1);
		scsr.samplingPoints[1] = new Point(2,-3);
		scsr.samplingPoints[2] = new Point(4,12);
		scsr.samplingPoints[3] = new Point(1, 0);
		scsr.computeSpline();
/*		scsr.A = new double[3][3];
		scsr.deltax = new double[3];
		scsr.deltay = new double[3];
		
		scsr.A[0][0] = 1; 
		scsr.A[0][1] = 2;
		scsr.A[0][2] = 3;
		
		scsr.A[1][0] = 4;
		scsr.A[1][1] = 5;
		scsr.A[1][2] = 7;
		
		scsr.A[2][0] = 7;
		scsr.A[2][1] = 8;
		scsr.A[2][2] = 9;
		
		scsr.deltax[0] = 3;
		scsr.deltax[1] = 7;
		scsr.deltax[2] = 5;
		
		scsr.performLRfactorisation();
		scsr.performLd();
		
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				System.out.print(scsr.A[i][j] + "  ");
			}
			System.out.println(" || " + scsr.deltax[i] );
		}
		
		double[] b = new double[5];
		b[0] = 0.0d;
		b[4] = 0.0d;

		for (int j = scsr.A.length - 1; j >= 0; j--)
		{
			b[j+1] = scsr.deltax[j];
			for (int k = j + 1; k < scsr.A[j].length; k++)
			{
				b[j+1] -= scsr.A[j][k] * b[k+1];
			}
			b[j+1] /= scsr.A[j][j];
			System.out.println("b[" + j + "] = " + b[j+1]);
		}*/
		
		
		System.out.println("Value: " + 
				scsr.getValue(new double[] { 1.0, 2.0, 7.0, 4.0 }, 3.0));
	}
	
	
	/**
	 * Moves a new/existing sampling point 
	 */
	public void mouseDragged(MouseEvent e) {
		if (!this.isExistingPoint)
		{
			
			ArrayList sP = new ArrayList(Arrays.asList(this.samplingPoints));
			
			sP.add(this.insertInLineSegment, this.dragStart);
			
			
			this.isExistingPoint   = true;
			this.draggedPointIndex = this.insertInLineSegment;
			this.samplingPoints    = (Point[])sP.toArray(new Point[0]);
		}
		
		this.samplingPoints[this.draggedPointIndex].x = e.getX();
		this.samplingPoints[this.draggedPointIndex].y = e.getY();
		// to force redraw even though the Bounds haven't changed
		this.computeLocationUpdate(this.rendererContainer.getNeuronRenderer(
																				this.sourceNeuronID).getBounds(),
															 this.sourceNeuronID);
		this.repaint();
	}

	/**
	 * Determine if a new sampling point has to be added or an existing is to
	 * be moved
	 */
	public void mousePressed(MouseEvent e)
	{
		Line2D.Double current;
		double        currentDist;
		double        minimumDist = Double.MAX_VALUE;

		this.isExistingPoint = false;
		this.dragStart       = e.getPoint();
		
		for (int i = 0; i < this.samplingPoints.length; i++)
		{
			if (this.samplingPoints[i].distance((Point2D)this.dragStart) < 10)
			{
				this.isExistingPoint   = true;
				this.draggedPointIndex = i;
				break;
			}
		}
		
		if (!this.isExistingPoint)
		{
			for (int i = 0; i < this.samplingPoints.length - 1; i++)
			{
				current = new Line2D.Double(this.samplingPoints[i],
																		this.samplingPoints[i+1]);
				currentDist = current.ptSegDist((Point2D)this.dragStart);
				if (currentDist < minimumDist)
				{
					minimumDist = currentDist;
					this.insertInLineSegment = i + 1;
				}	
			}
		}
		
	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e)
	{
		this.draggedPointIndex = Integer.MAX_VALUE;
	}
	
	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.toolkit.InputValueListener#inputValueUpdated(Hinton.analyser.netstructure.toolkit.InputValue)
	 */
	public void inputValueUpdated(InputValue iv)
	{
		InputValue.IBoolean doRemove  = (InputValue.IBoolean)iv;
		int                 removeIdx = -1;
		
		if (doRemove.value)
		{
			for (int i = 0; removeIdx == -1 &&
										  i < this.toggleSamplingPoints.length;
					 i++)
			{
				if (doRemove.equals(this.toggleSamplingPoints[i]))
				{
					removeIdx = i+1;
				}
			}
			ArrayList sP  = new ArrayList(Arrays.asList(this.samplingPoints));
			sP.remove(removeIdx);
			ArrayList tSP = new ArrayList(Arrays.asList(this.toggleSamplingPoints));
			tSP.remove(removeIdx-1);
			
			this.samplingPoints = (Point[])sP.toArray(new Point[0]);
			this.toggleSamplingPoints = (InputValue.IBoolean[])tSP.toArray(
													new InputValue.IBoolean[0]);
													
			// to force redraw even though the Bounds haven't changed
			this.computeLocationUpdate(this.rendererContainer.getNeuronRenderer(
																						 this.sourceNeuronID).getBounds(),
																	  this.sourceNeuronID);
			this.repaint();
			
		}
		
	}
	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.ComponentRenderer#getPersistentOptions()
	 */
	public PersistentRendererOption[] getPersistentOptions() {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.ComponentRenderer#setPersistentOptions(Hinton.analyser.netstructure.PersistentRendererOption[])
	 */
	public void setPersistentOptions(PersistentRendererOption[] pro) {
		// TODO Auto-generated method stub
		
	}
}
