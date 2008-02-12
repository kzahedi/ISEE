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
 * Created on 14.04.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package hinton.analyser.netstructure.visualnetexporter;

import hinton.analyser.netstructure.NeuronRenderer;
import hinton.analyser.netstructure.SynapseRenderer;
import hinton.analyser.netstructure.VisualNet;
import hinton.analyser.netstructure.VisualNetExporter;
import hinton.analyser.toolkit.InputValue;

import java.awt.Point;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import cholsey.NeuronType;
import cholsey.Synapse;


/**
 * @author rosemann
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class XFigExporter extends VisualNetExporter
{
	private static final String	SYNAPSE_SPLINE = "Spline";
	private static final String SYNAPSE_LINE   = "Straight Line";
	private static final int    RED            = 19;
	private static final int    GREEN          = 13;
	private static final int    BLUE           =  9;
	private static final int    BLACK          =  0;
	private static final int    WHITE          =  7;
	
	
	private InputValue.IInteger pixelPerInch;
	private InputValue.IBoolean drawDescr;
	private InputValue.IObject  synapseStyle;
	private InputValue[] properties;
	
	private int          figUnitsPerInch = 1200;
	private XFigNeuron[] xFigNeuron;
  private DecimalFormat df = new DecimalFormat("0.000");
	private DecimalFormatSymbols dfs = new DecimalFormatSymbols();

	public XFigExporter()
	{
		super();
		dfs.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(dfs);
	}
	
	public XFigExporter(VisualNet visualNet)
	{
		super(visualNet);
		dfs.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(dfs);
	}
	
	
	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.VisualNetExporter#exportNet()
	 */
	protected void exportNet(OutputStream fos) throws IOException
	{
		String           xfigFile = "";
		
		/* Generate Header Information */
		xfigFile += generateXFigHeader();
		/* Generate XFig Representation of Neurons */
		xfigFile += generateXFigNeurons();
		/* Generate XFig Representation of Synapses */
		xfigFile += generateXFigSynapses();
		
		
		fos.write(xfigFile.getBytes());
		fos.flush();
		fos.close();
	}
	
	public String getExportFormatName()
	{
		return "XFig Version 3.2";
	}

	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.VisualNetExporter#getFileFormatExtension()
	 */
	protected String getFileFormatExtension()
	{
		return "FIG";
	}
	
	protected InputValue[] getProperties()
	{
		if (this.pixelPerInch == null)
		{
			this.pixelPerInch = new InputValue.IInteger(
																								 "Pixel per Inch (ppi)",
																								 75,
																								 20, 200);
			this.drawDescr    = new InputValue.IBoolean(
																								 "Draw Description",
																								 true);
			this.synapseStyle = new InputValue.IObject(
																								 "Synapse Style",
																								 0,
																								 new String []
																													{ 
																														XFigExporter.SYNAPSE_SPLINE,
																														XFigExporter.SYNAPSE_LINE
																											}
																								 );
			this.properties = new InputValue[3];
			this.properties[0] = this.drawDescr;
			this.properties[1] = this.synapseStyle;
			this.properties[2] = this.pixelPerInch;
		}
		
		
		return this.properties;
			                    
	}
	
	private int convertToXFig(double arg0)
	{
		return (int)(arg0 * this.figUnitsPerInch / this.pixelPerInch.value);
	}
	
	private String generateXFigHeader()
	{
		String xfigFile = "";
		
		xfigFile += "#FIG 3.2\n";
		xfigFile += "Portrait\n";
		xfigFile += "Center\n";
		xfigFile += "Metric\n";
		xfigFile += "A4\n";
		xfigFile += "100.00\n";
		xfigFile += "Single\n";
		xfigFile += "-2\n";
		xfigFile += "1200 2\n";
		
		return xfigFile;
	}
	
	private String generateXFigNeurons()
	{
		int            nrCount  = this.visualNet.getNet().size();
		String         xfigFile = "";
		NeuronRenderer nr;
		
		this.xFigNeuron = new XFigNeuron[nrCount];
		
		for (int i = 0; i < nrCount; i++)
		{
			int neuronColor;
			nr = this.visualNet.getNeuronRenderer(i);
		
			this.xFigNeuron[i] = new XFigNeuron();
			this.xFigNeuron[i].cmx = this.convertToXFig(
															 nr.getX() + (nr.getWidth() / 2.0d));
			this.xFigNeuron[i].cmy = this.convertToXFig(
															 nr.getY() + (nr.getHeight() / 2.0d));
			this.xFigNeuron[i].r   = this.convertToXFig(
													Math.min(nr.getWidth(), nr.getHeight())/2.0d);
												
			switch (nr.getNeuron().getNeuronType().type())
			{
				case NeuronType.NEURON_TYPE_INPUT : neuronColor = XFigExporter.RED;
																					  break;
				case NeuronType.NEURON_TYPE_HIDDEN: neuronColor = XFigExporter.BLUE;
																					  break;
				case NeuronType.NEURON_TYPE_OUTPUT: neuronColor = XFigExporter.GREEN;
																						break;
				default:                            neuronColor = XFigExporter.BLACK;
			}
		
			xfigFile += generateXFigCircle(this.xFigNeuron[i].cmx,
										   this.xFigNeuron[i].cmy,
										   this.xFigNeuron[i].r,
										   neuronColor);
			if (this.drawDescr.value)
			{	
				xfigFile += generateXFigText(this.xFigNeuron[i].cmx,
											 this.xFigNeuron[i].cmy,
											 0.0d,
											 String.valueOf(nr.getNeuron().id() + 1));
			}
		}
		
		return xfigFile;
	}
	
	private String generateXFigSynapses()
	{
		int               c;
		String            xfigFile = "";
		Synapse           s;
		SynapseRenderer[] sr       = this.visualNet.getSynapseRenderer();
		
		for (int i = 0; i < sr.length; i++)
		{
	
			s   = sr[i].getSynapse();
	
			if (s.strength() < 0)
			{
				c = XFigExporter.BLUE;
			}
			else
			{
				c = XFigExporter.RED;
			}
	
			if (s.getSource().id() != s.getDestination().id())
			{
				xfigFile += this.generateXFigSynapse(s, c);	
			}
			else
			{
				xfigFile += this.generateXFigSelfReactSynapse(s, c);
			}	
		}
		
		return xfigFile;
	}
	
	private String generateXFigSynapse(Synapse s, int c)
	{
		int             p1x, p1y, p2x, p2y, ctrlpx, ctrlpy;
		double          vx, vy, len;
		String          xfigFile = "";
		SynapseRenderer sr       = this.visualNet.getSynapseRenderer(s);
		Point[] points           = sr.getSamplingPoints();
		Point[] ctrlPts;
		
		p1x = this.convertToXFig(sr.getX() + points[0].x);
		p1y = this.convertToXFig(sr.getY() + points[0].y);
		p2x = this.convertToXFig(sr.getX() + points[points.length-1].x);
		p2y = this.convertToXFig(sr.getY() + points[points.length-1].y);
		
		vx  = p2x - p1x;
		vy  = p2y - p1y;
		len = Math.sqrt(vx*vx + vy*vy);
		vx /= len;
		vy /= len;
		
		ctrlpx = (int)(p1x + len/2 * vx + this.convertToXFig(5) * -vy);
		ctrlpy = (int)(p1y + len/2 * vy + this.convertToXFig(5) *  vx); 
		
		if (points.length < 3)
		{
			ctrlPts = new Point[] { new Point(ctrlpx, ctrlpy) };
		}
		else
		{
			ctrlPts = new Point[points.length - 2];
			for (int i = 0; i < ctrlPts.length; i++)
			{
				ctrlPts[i] = new Point();
				ctrlPts[i].x = this.convertToXFig(sr.getX() + points[i+1].x);
				ctrlPts[i].y = this.convertToXFig(sr.getY() + points[i+1].y);
				
			}
		}

		if (((String)this.synapseStyle.value).compareTo(XFigExporter.SYNAPSE_LINE) == 0)
		{
				xfigFile += generateXFigPolyline(p1x, p1y,
																			 p2x, p2y,
																			 ctrlPts,
																			 c);
		}
		else
		{
				xfigFile += generateXFigSpline(p1x, p1y,
																			 p2x, p2y,
																			 ctrlPts,
																			 c);
		}

		if (this.drawDescr.value)
		{													
			xfigFile += generateXFigText((int)(ctrlpx +
																				 this.convertToXFig(12) * -vy),
															 (int)(ctrlpy +
																		 this.convertToXFig(12) *  vx),
																	 -Math.atan2(vy, vx),
																	 df.format(s.strength()));
		}
			
		return xfigFile;
	}
	
	private String generateXFigArrow()
	{
		String arrow;
				 /*
				  *   +-------------------------> Arrow Type Closed Triangle
				  *   | +-----------------------> Arrow Style Filled With Color
				  *   | |    +------------------> Arrow thickness
				  *   | |    |      +-----------> Arrow width
				  *   | |    |      |      +----> Arrow height
				  *   | |    |      |      |                   */
		arrow = "	1 1 1.00 105.00 210.00\n";
		
		return arrow;
	}
	
	
	private String generateXFigSelfReactSynapse(Synapse s, int c)
	{
		int             p1x, p1y, p2x, p2y, p3x, p3y;
		double          d, cmx, cmy;
		String          xfigFile = "";
		XFigExporter.XFigNeuron xfNeuron;
		
		xfNeuron = this.xFigNeuron[s.getSource().id()];
		
		d   = xfNeuron.r / Math.sqrt(2);
		cmx = xfNeuron.cmx + xfNeuron.r + d;
		cmy = xfNeuron.cmy;

		p1x = (int)(xfNeuron.cmx + 0.7071 * xfNeuron.r);
		p1y = (int)(xfNeuron.cmy - 0.7071 * xfNeuron.r);
		p3x = p1x;
		p3y = (int)(xfNeuron.cmy + 0.7071 * xfNeuron.r);  

		p2x = (int)(cmx + (xfNeuron.r + (xfNeuron.r-d)));
		p2y = (int)(cmy);

		xfigFile += this.generateXFigArc(cmx, cmy,
																		 p1x, p1y,
																		 p2x, p2y,
																		 p3x, p3y,
																		 c);
														 
		if (this.drawDescr.value)
		{
			xfigFile += generateXFigText(p2x + 5,
														 p2y,
																 0.0d,
																 df.format(s.strength()));
		}
		
		return xfigFile;
	}
	
	private String generateXFigPolyline(int p1x, int p1y,
	                                    int p2x, int p2y,
	                                    Point[] ctrlPts,
	                                    int c)
	{
		String polyLine = "";
		
						 /*
		 					*  +--------------------------------------------> Type Polygon
		 					*  | +------------------------------------------> Subtype Polyline
		 					*  | | +----------------------------------------> Linestyle default
		 					*  | | | +--------------------------------------> Linethickness
		 					*  | | | |     +--------------------------------> Color
		 					*  | | | |     |     +--------------------------> fillcolor NONE
		 					*  | | | |     |     |  +-----------------------> depth (DEFAULT)
		 					*  | | | |     |     |  | +---------------------> penstyle (UNUSED)
		 					*  | | | |     |     |  | |  +------------------> area_fill (NONE)
		 					*  | | | |     |     |  | |  |     +------------> style_val (DEFAULT)
		 					*  | | | |     |     |  | |  |     | +----------> join_style(DEFAULT)
		 					*  | | | |     |     |  | |  |     | | +--------> cap_style (DEFAULT)
		 					*  | | | |     |     |  | |  |     | | |  +-----> radius (unused)
		 					*  | | | |     |     |  | |  |     | | |  | +---> forward_arrow ON
		 					*  | | | |     |     |  | |  |     | | |  | | +-> backward_arrow OFF
						  *  | | | |     |     |  | |  |     | | |  | | |                  */
 		polyLine += "2 1 0 1 " + c + " 7 51 0 -1 0.000 0 0 -1 1 0";
 		polyLine += " " + (ctrlPts.length + 2) + "\n";  // Pointcount
 		
		polyLine += this.generateXFigArrow();
				
		polyLine += "   " + p1x    + " " + p1y;        						// Line Start
		for (int i = 0; i < ctrlPts.length; i++)
		{
			polyLine +=   " " + ctrlPts[i].x + " " + ctrlPts[i].y;  // Line CtrlPoint
		}
		polyLine +=   " " + p2x    + " " + p2y + "\n"; 						// Line End
 		
 		
		
		return polyLine;
	}	
	
	private String generateXFigSpline(int p1x, int p1y,
	                                  int p2x, int p2y,
	                                  Point[] ctrlPts,
	                                  int c)
	{
		
		String spline = "";
		
		       /*
		        *  +-----------------------------------------> Type Spline
		        *  | +---------------------------------------> Subtype open Inter.
		        *  | | +-------------------------------------> Linestyle default
		        *  | | | +-----------------------------------> Linethickness
		        *  | | | |     +-----------------------------> Color
		        *  | | | |     |     +-----------------------> fillcolor NONE
		        *  | | | |     |     |  +--------------------> depth (DEFAULT)
		        *  | | | |     |     |  | +------------------> penstyle (UNUSED)
		        *  | | | |     |     |  | |  +---------------> area_fill (NONE)
		        *  | | | |     |     |  | |  |     +---------> style_val (DEFAULT)
		        *  | | | |     |     |  | |  |     | +-------> cap_style (DEFAULT)
		        *  | | | |     |     |  | |  |     | | +-----> forward_arrow ON
		        *  | | | |     |     |  | |  |     | | | +---> backward_arrow OFF
		        *  | | | |     |     |  | |  |     | | | |                  */
		spline += "3 2 0 1 " + c + " 7 51 0 -1 0.000 0 1 0";
		spline += " " + (ctrlPts.length + 2) + "\n";  // Pointcount
		
		spline += this.generateXFigArrow();
		
		spline += "   " + p1x    + " " + p1y;        						// Spline Start
		for (int i = 0; i < ctrlPts.length; i++)
		{
			spline +=   " " + ctrlPts[i].x + " " + ctrlPts[i].y;  // Spline CtrlPoint
		}
		spline +=   " " + p2x    + " " + p2y + "\n"; 						// Spline End
		
					 
		spline += "	 0.000"; // P1 angular Point
		for (int i = 0; i < ctrlPts.length; i++)
		{
			spline += " -1.000"; // Interpolated Ctrl-Pt.
		}
		spline += "0.000\n"; // P2 angular Point
		
		
		return spline;
	}
	
	private String generateXFigText(int px, int py,
																	double ang,
																	String text)
	{
		String ftext = "";
		int w = 120 * text.length();
		int h = Math.max(120, (int)Math.ceil(w * Math.sin(ang)));
		    w = Math.max(120, (int)Math.ceil(w * Math.cos(ang)));
		
				  /*  +------------------------------------------------------> Type Text
				   *  | +----------------------------------------------------> Subtype center justified 
				   *  | | +--------------------------------------------------> color
				   *  | | |  +-----------------------------------------------> Depth (DEFAULT)
				   *  | | |  | +---------------------------------------------> pen_style (DEFAULT)
				   *  | | |  | | +-------------------------------------------> font (DEFAULT)
				   *  | | |  | | |  +----------------------------------------> fontsize
				   *  | | |  | | |  |                  +---------------------> angle radiants
				   *  | | |  | | |  |                  |     +---------------> font-flags (DEFAULT)
				   *  | | |  | | |  |                  |     |     +---------> height
				   *  | | |  | | |  |                  |     |     |       +-> width
				   *  | | |  | | |  |                  |     |     |       |                        */
		ftext += "4 1 0 50 0 0 12 " + df.format(ang) + " 4 " + h + " " + w;
		
		ftext += " " + px + " " + py;  // TextPosition
		ftext += " " + text + "\\001\n";  // Text
		
		return ftext;
	}
	
	
	private String generateXFigArc(double cmx, double cmy,
																 int p1x, int p1y,
														     int p2x, int p2y,
														     int p3x, int p3y,
														     int c)
	{
		
		String arc = "";
			  /*
			   *  +-------------------------------------------> Type Arc
			   *  | +-----------------------------------------> Subtype Open Ended
			   *  | | +---------------------------------------> linestyle (DEFAULT)
			   *  | | | +-------------------------------------> linethickness (DEFAULT)
			   *  | | | |     +-------------------------------> color
			   *  | | | |     |     +-------------------------> fill_color (NONE)
			   *  | | | |     |     |  +----------------------> depth (DEFAULT)
			   *  | | | |     |     |  | +--------------------> penstyle (DEFAULT)
			   *  | | | |     |     |  | |  +-----------------> areafill (NO FILL)
			   *  | | | |     |     |  | |  |     +-----------> style_val (DEFAULT)
			   *  | | | |     |     |  | |  |     | +---------> cap_style (DEFAULT)
			   *  | | | |     |     |  | |  |     | | +-------> direction (CLOCKWISE)
			   *  | | | |     |     |  | |  |     | | | +-----> forwardarrow ON
			   *  | | | |     |     |  | |  |     | | | | +---> backwardarrow OFF
			   *  | | | |     |     |  | |  |     | | | | |                      */
		arc += "5 1 0 1 " + c + " 7 51 0 -1 0.000 0 0 1 0";
		
		arc += " " + df.format(cmx) + " " + df.format(cmy); // Circle Center
		arc += " " + p1x            + " " + p1y;            // Start Point
		arc += " " + p2x            + " " + p2y;            // Controll Point
		arc += " " + p3x            + " " + p3y + "\n";     // End Point
		
		arc += this.generateXFigArrow();
		
		
		return arc;
	}														     
	
	private String generateXFigCircle(int cmx, int cmy, int r, int c)
	{
		String circ = "";
		
		
		/*       +----------------------------------------> Indicates Type Ellipse
		 *       | +--------------------------------------> Indicates Suptype Circle
		 *       | | +------------------------------------> Linestyle default
		 *       | | | +----------------------------------> Linethickness
		 *       | | | |     +----------------------------> Color
		 *       | | | |     |     +----------------------> fillcolor NONE
		 *       | | | |     |     |  +-------------------> depth (DEFAULT)
		 *       | | | |     |     |  | +-----------------> penstyle (UNUSED)
		 *       | | | |     |     |  | |  +--------------> area_fill (NONE)
		 *       | | | |     |     |  | |  |     +--------> style_val (DEFAULT)
		 *       | | | |     |     |  | |  |     | +------> direction (ALWAYS 1)
		 *       | | | |     |     |  | |  |     | |     +> angle (DEFAULT)
		 *       | | | |     |     |  | |  |     | |     |         */
		circ += "1 3 0 1 " + c + " 7 50 0 20 0.000 1 0.000";
		circ += " " + cmx       + " " + cmy; // Center
		circ += " " + r         + " " + r;   // Radius
		circ += " " + cmx       + " " + cmy; // "1st point entered"
		circ += " " + (cmx + r) + " " + cmy; // "last point entered"
		circ += "\n";
		
		return circ;
	}
	
	static class XFigNeuron
	{
		public int cmx;
		public int cmy;
		public int r;
	}

}
