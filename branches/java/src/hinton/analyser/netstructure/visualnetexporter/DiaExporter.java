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
 * Created on 17.06.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package hinton.analyser.netstructure.visualnetexporter;

import hinton.analyser.netstructure.NeuronRenderer;
import hinton.analyser.netstructure.SynapseRenderer;
import hinton.analyser.netstructure.VisualNetExporter;
import hinton.analyser.toolkit.InputValue;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import cholsey.NeuronType;


/**
 * @author rosemann
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class DiaExporter extends VisualNetExporter {
	
	private static final Point2D.Double[] CONNECTOR_POS = { 
																				new Point2D.Double( 1.00d,  0.00d),
																				new Point2D.Double( 0.92d, -0.38d),
																				new Point2D.Double( 0.70d, -0.70d),
																				new Point2D.Double( 0.38d, -0.92d),
																				new Point2D.Double( 0.00d, -1.00d),
																				new Point2D.Double(-0.38d, -0.92d),
																				new Point2D.Double(-0.70d, -0.70d),
																				new Point2D.Double(-0.92d, -0.38d),
																				new Point2D.Double(-1.00d,  0.00d),
																				new Point2D.Double(-0.92d,  0.38d),
																				new Point2D.Double(-0.70d,  0.70d),
																				new Point2D.Double(-0.38d,  0.92d),
																				new Point2D.Double( 0.00d,  1.00d),
																				new Point2D.Double( 0.38d,  0.92d),
																				new Point2D.Double( 0.70d,  0.70d),
																				new Point2D.Double( 0.92d,  0.38d) };
	
	private static final DecimalFormat DF = new DecimalFormat("0.00");
	private static final DecimalFormatSymbols DFS = new DecimalFormatSymbols();
	static
	{
		DiaExporter.DFS.setDecimalSeparator('.');
		DiaExporter.DF.setDecimalFormatSymbols(DiaExporter.DFS);
	}
	
	private InputValue.IInteger ppi;
	private InputValue[] properties;
	
	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.VisualNetExporter#getProperties()
	 */
	protected InputValue[] getProperties() {
		if (this.properties == null)
		{
			this.ppi = new InputValue.IInteger("Pixel per Inch",
																				 20, 10, 100);
			this.properties = new InputValue[] { this.ppi };
		}
		
		return this.properties;
	}
	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.VisualNetExporter#exportNet(java.io.OutputStream)
	 */
	protected void exportNet(OutputStream fos) throws IOException {
		String           format;
		NeuronRenderer[] nr = this.visualNet.getNeuronRenderer();
		SynapseRenderer[] sr = this.visualNet.getSynapseRenderer();
		
		
		format = this.getDiaHeader();
		
		for (int i = 0; i < nr.length; i++)
		{
			switch (nr[i].getNeuron().getNeuronType().type())
			{
				case NeuronType.NEURON_TYPE_INPUT : 
							format += this.getDiaNeuron(nr[i].getNeuron().id(), Color.RED);
							break;
				case NeuronType.NEURON_TYPE_OUTPUT : 
							format += this.getDiaNeuron(nr[i].getNeuron().id(), Color.GREEN);
							break;
				case NeuronType.NEURON_TYPE_HIDDEN : 
							format += this.getDiaNeuron(nr[i].getNeuron().id(), Color.BLUE);
							break;					
			}
		}
		
		for (int i = 0; i < sr.length; i++)
		{
			if (sr[i].getSynapse().strength() < 0)
			{
				format += this.getDiaSynapse(sr[i].getSynapse().getSource().id(),
																	   sr[i].getSynapse().getDestination().id(),
																		 Color.BLUE,
																		 sr[i].getSynapse().strength());
			}
			else
			{
				format += this.getDiaSynapse(sr[i].getSynapse().getSource().id(),
					   												 sr[i].getSynapse().getDestination().id(),
																		 Color.RED,
																		 sr[i].getSynapse().strength());
			}
		}
		
		format += this.getDiaFooter();
		
		fos.write(format.getBytes());
		fos.flush();
		fos.close();
	}
	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.VisualNetExporter#getExportFormatName()
	 */
	public String getExportFormatName() {
		return "DIA Exporter";
	}
	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.VisualNetExporter#getFileFormatExtension()
	 */
	protected String getFileFormatExtension() {
		return "DIA";
	}
	
	private String getDiaSynapse(int sid, int did, Color color, double weight)
	{
		String ret = "";
		
		int sc, dc;
		double sx, sy, dx, dy, srr;
		NeuronRenderer snr, dnr;
		double dmyx, dmyy;
		double dist, currDist;
		
		snr = this.visualNet.getNeuronRenderer(sid);
		dnr = this.visualNet.getNeuronRenderer(did);
		sc  = 0;
		dc  = 0;
		
		dmyx = dnr.getX() - snr.getX();
		dmyy = dnr.getY() - snr.getY();
		dist = Double.MAX_VALUE;
		for (int i = 0; i < DiaExporter.CONNECTOR_POS.length; i++)
		{
			currDist = Point2D.distance(dmyx, dmyy, 
					DiaExporter.CONNECTOR_POS[i].x, DiaExporter.CONNECTOR_POS[i].y);
			if (dist > currDist)
			{
				dist = currDist;
				sc   = i;
			}
		}
		
		dmyx = snr.getX() - dnr.getX();
		dmyy = snr.getY() - dnr.getY();
		dist = Double.MAX_VALUE;
		for (int i = 0; i < DiaExporter.CONNECTOR_POS.length; i++)
		{
			currDist = Point2D.distance(dmyx, dmyy, 
					DiaExporter.CONNECTOR_POS[i].x, DiaExporter.CONNECTOR_POS[i].y);
			if (dist > currDist)
			{
				dist = currDist;
				dc   = i;
			}
		}
		if (sid != did)
		{
			sx = snr.getX() + snr.getWidth()/2.0d + 
			     ((snr.getWidth()/2.0d) * DiaExporter.CONNECTOR_POS[sc].x);
			sy = snr.getY() + snr.getHeight()/2.0d + 
	         ((snr.getHeight()/2.0d) * DiaExporter.CONNECTOR_POS[sc].y);
			dx = dnr.getX() + dnr.getWidth()/2.0d + 
					 ((dnr.getWidth()/2.0d) * DiaExporter.CONNECTOR_POS[dc].x);
			dy = dnr.getY() + dnr.getHeight()/2.0d + 
	    		 ((dnr.getHeight()/2.0d) * DiaExporter.CONNECTOR_POS[dc].y);
			sx = this.convertToDia(sx);
			sy = this.convertToDia(sy);
			dx = this.convertToDia(dx);
			dy = this.convertToDia(dy);
			
			ret  = "<dia:object type=\"Standard - Line\" version=\"0\" id=\"S" + 
						 sid + did + "\">";
			ret += this.getDiaAttribute("obj_pos", this.getDiaPointValue(sx, sy));
			ret += this.getDiaAttribute("obj_bb", this.getDiaRectangleValue(sx,sy,dx,dy));
			ret += this.getDiaAttribute("conn_endpoints",
						 this.getDiaPointValue(sx, sy) + this.getDiaPointValue(dx, dy));
			ret += this.getDiaAttribute("numcp", this.getDiaIntValue(1));
			ret += this.getDiaAttribute("line_color", this.getDiaColorValue(color));
			ret += this.getDiaAttribute("end_arrow", this.getDiaEnum(3));
			ret += this.getDiaAttribute("end_arrow_length", this.getDiaRealValue(0.8d));
			ret += this.getDiaAttribute("end_arrow_width", this.getDiaRealValue(0.8d));
			ret += "<dia:connections>";
		  ret += "<dia:connection handle=\"0\" to=\"O" + sid + 
							"\" connection=\"" + sc + "\"/>";
		  ret += "<dia:connection handle=\"1\" to=\"O" + did + 
							"\" connection=\"" + dc + "\"/>";
		  ret += "</dia:connections>";
			
			ret += "</dia:object>";
			
			ret += "<dia:object type=\"Standard - Text\" version=\"0\" id=\"T" + 
			       sid + did + "\">";
			ret += this.getDiaAttribute("obj_pos",
						 this.getDiaPointValue((sx + dx)/2, (sy + dy)/2));
			ret += this.getDiaAttribute("obj_bb",
					   this.getDiaRectangleValue((sx + dx)/2, (sy+dy)/2,
					   													 (sx + dx)/2+0.5, (sy + dy)/2+0.5));
		  ret += this.getDiaText(DiaExporter.DF.format(weight),
		  											(sx + dx)/2, (sy + dy)/2);
		  ret += "<dia:connections>";
		  ret += "<dia:connection handle=\"0\" to=\"S" + sid + did + 
							"\" connection=\"0\"/>";
		  ret += "</dia:connections>";
		  ret += "</dia:object>";
		}
		else
		{
			sx = snr.getX() + snr.getWidth()/2.0d + 
			     ((snr.getWidth()/2.0d) * DiaExporter.CONNECTOR_POS[14].x);
			sy = snr.getY() + snr.getHeight()/2.0d + 
		      ((snr.getHeight()/2.0d) * DiaExporter.CONNECTOR_POS[14].y);
			dx = dnr.getX() + dnr.getWidth()/2.0d + 
					 ((dnr.getWidth()/2.0d) * DiaExporter.CONNECTOR_POS[2].x);
			dy = dnr.getY() + dnr.getHeight()/2.0d + 
		 		 ((dnr.getHeight()/2.0d) * DiaExporter.CONNECTOR_POS[2].y);
			sx = this.convertToDia(sx);
			sy = this.convertToDia(sy);
			dx = this.convertToDia(dx);
			dy = this.convertToDia(dy);
			
			srr = this.convertToDia(snr.getWidth() * 1.2);
	
			ret  = "<dia:object type=\"Standard - Arc\" version=\"0\" id=\"S"
						 + sid + "\">";
		  ret += this.getDiaAttribute("obj_pos", this.getDiaPointValue(sx, sy));
		  ret += this.getDiaAttribute("abj_bb",
		  		   this.getDiaRectangleValue(sx, sy, sx + srr, dy));
		  ret += this.getDiaAttribute("conn_endpoints",
		  		   this.getDiaPointValue(sx, sy) + this.getDiaPointValue(dx, dy));
		  ret += this.getDiaAttribute("curve_distance", this.getDiaRealValue(srr));
		  ret += this.getDiaAttribute("arc_color", this.getDiaColorValue(color));
			ret += this.getDiaAttribute("end_arrow", this.getDiaEnum(3));
			ret += this.getDiaAttribute("end_arrow_length", this.getDiaRealValue(0.8d));
			ret += this.getDiaAttribute("end_arrow_width", this.getDiaRealValue(0.8d));
			ret += "<dia:connections>";
			ret += "<dia:connection handle=\"0\" to=\"O" + sid + 
						 "\" connection=\"" + 14 + "\"/>";
			ret += "<dia:connection handle=\"1\" to=\"O" + did + 
						 "\" connection=\"" + 2 + "\"/>";
			ret += "</dia:connections>";
			ret += "</dia:object>";
			
			sx = snr.getX() + snr.getWidth()/2.0d + 
			     ((snr.getWidth()/2.0d) * DiaExporter.CONNECTOR_POS[0].x);
			sy = snr.getY() + snr.getHeight()/2.0d + 
		     ((snr.getHeight()/2.0d) * DiaExporter.CONNECTOR_POS[0].y);
			
			ret += "<dia:object type=\"Standard - Text\" version=\"0\" id=\"T" + 
			      sid + did + "\">";
			ret += this.getDiaAttribute("obj_pos",
						 this.getDiaPointValue(sx, sy));
			ret += this.getDiaAttribute("obj_bb",
					   this.getDiaRectangleValue(sx, sy, sx+0.5, sy+0.5));
			ret += this.getDiaText(DiaExporter.DF.format(weight), sx, sy);
			ret += "<dia:connections>";
			ret += "<dia:connection handle=\"0\" to=\"O" + sid + 
							"\" connection=\"0\"/>";
			ret += "</dia:connections>";
			ret += "</dia:object>";
		}
		return ret;
	}
	
	private String getDiaNeuron(int id, Color color)
	{
		String ret;
		NeuronRenderer nr = this.visualNet.getNeuronRenderer(id);
		double left       = this.convertToDia(nr.getX());
		double top        = this.convertToDia(nr.getY());
		double width      = this.convertToDia(nr.getWidth());
		double height     = this.convertToDia(nr.getHeight());
		
		
		ret  = "<dia:object type=\"Flowchart - Ellipse\" version=\"0\" id=\"O" + 
					 id + "\">";
		ret += this.getDiaAttribute("obj_pos", 
					 this.getDiaPointValue(left, top));
		ret += this.getDiaAttribute("obj_bb",
		       this.getDiaRectangleValue(left, top, left+width, top+height));															
		ret += this.getDiaAttribute("elem_corner",
					 this.getDiaPointValue(left, top));
		ret += this.getDiaAttribute("elem_width",
					 this.getDiaRealValue(width));
		ret += this.getDiaAttribute("elem_height",
					 this.getDiaRealValue(height));
		ret += this.getDiaAttribute("show_background",
					 this.getDiaBooleanValue(true));
		ret += this.getDiaAttribute("border_color", this.getDiaColorValue(color));
		ret += this.getDiaAttribute("padding",this.getDiaRealValue(0.35d));
		ret += this.getDiaText("" + id + "", left + width/2.0d, top + height/2.0d);
		ret += "</dia:object>";
			
		return ret;
	}
	
	
	private double convertToDia(double value)
	{
		return value / this.ppi.value;
	}
	
	private String getDiaText(String text, double x, double y)
	{
		String ret;
		
		ret = "<dia:composite type=\"text\">";
		ret += this.getDiaAttribute("string", this.getDiaStringValue(text));
		ret += this.getDiaAttribute("font",
						"<dia:font family=\"sans\" style=\"0\" name=\"Courier\"/>");
	  ret += this.getDiaAttribute("height", this.getDiaRealValue(0.8d));
	  ret += this.getDiaAttribute("pos", this.getDiaPointValue(x, y));
	  ret += this.getDiaAttribute("color", this.getDiaColorValue(Color.BLACK));
	  ret += this.getDiaAttribute("alignment", this.getDiaEnum(1));
	  ret += "</dia:composite>";
	  
	  return this.getDiaAttribute("text", ret);
	}
	
	
	private String getDiaAttribute(String name, String value)
	{
		String ret;
		
	  ret  = "<dia:attribute name=\"" + name + "\">";
	  ret += value;
	  ret += "</dia:attribute>";
	  
		return ret;
	}
	
	private String getDiaIntValue(int value)
	{
		return "<dia:int val=\"" + value + "\"/>";
	}
	
	private String getDiaRealValue(double value)
	{
		return "<dia:real val=\"" + DiaExporter.DF.format(value) + "\"/>"; 
	}
	
	private String getDiaStringValue(String value)
	{
		return "<dia:string>#" + value + "#</dia:string>";
	}
	
	private String getDiaPointValue(double x, double y)
	{
		return "<dia:point val=\"" + DiaExporter.DF.format(x) + "," +
				DiaExporter.DF.format(y) + "\"/>";
	}
	
	private String getDiaRectangleValue(double left, double top,
																		  double right, double bottom)
	{
		return "<dia:rectangle val=\"" + 
						DiaExporter.DF.format(left) + "," + 
						DiaExporter.DF.format(top) + ";" +
						DiaExporter.DF.format(right) + "," +
						DiaExporter.DF.format(bottom) + "\"/>";
	}
	
	private String getDiaColorValue(Color color)
	{
		String c = Integer.toHexString(color.getRGB() & 0xffffff);
		
		while (c.length() < 6)
		{
			c = "0" + c;
		}
		return "<dia:color val=\"#" +	c + "\"/>";
	}
	
	private String getDiaBooleanValue(boolean val)
	{
		return "<dia:boolean val=\"" + ( val ? "true" : "false") + "\"/>";
	}
	
	private String getDiaEnum(int value)
	{
		return "<dia:enum val=\"" + value + "\"/>";
	}
	
	
	private String getDiaHeader()
	{
		String ret;
		
		ret  = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		ret += "<dia:diagram xmlns:dia=\"http://www.lysator.liu.se/~alla/dia/\">";
		ret += "<dia:diagramdata>";
		ret += "<dia:attribute name=\"background\">";
		ret += "<dia:color val=\"#ffffff\"/>";
		ret += "</dia:attribute>";
		ret += "<dia:attribute name=\"paper\">";
		ret += "<dia:composite type=\"paper\">";
		ret += "<dia:attribute name=\"name\">";
		ret += "<dia:string>#A4#</dia:string>";
		ret += "</dia:attribute>";
		ret += "<dia:attribute name=\"tmargin\">";
		ret += "<dia:real val=\"2.8222\"/>";
		ret += "</dia:attribute>";
		ret += "<dia:attribute name=\"bmargin\">";
		ret += "<dia:real val=\"2.8222\"/>";
		ret += "</dia:attribute>";
		ret += "<dia:attribute name=\"lmargin\">";
		ret += "<dia:real val=\"2.8222\"/>";
		ret += "</dia:attribute>";
		ret += "<dia:attribute name=\"rmargin\">";
		ret += "<dia:real val=\"2.8222\"/>";
		ret += "</dia:attribute>";
		ret += "<dia:attribute name=\"is_portrait\">";
		ret += "<dia:boolean val=\"true\"/>";
		ret += "</dia:attribute>";
		ret += "<dia:attribute name=\"scaling\">";
		ret += "<dia:real val=\"1\"/>";
		ret += "</dia:attribute>";
		ret += "<dia:attribute name=\"fitto\">";
		ret += "<dia:boolean val=\"false\"/>";
		ret += "</dia:attribute>";
		ret += "</dia:composite>";
		ret += "</dia:attribute>";
		ret += "<dia:attribute name=\"grid\">";
		ret += "<dia:composite type=\"grid\">";
		ret += "<dia:attribute name=\"width_x\">";
		ret += "<dia:real val=\"1\"/>";
		ret += "</dia:attribute>";
		ret += "<dia:attribute name=\"width_y\">";
		ret += "<dia:real val=\"1\"/>";
		ret += "</dia:attribute>";
		ret += "<dia:attribute name=\"visible_x\">";
		ret += "<dia:int val=\"1\"/>";
		ret += "</dia:attribute>";
		ret += "<dia:attribute name=\"visible_y\">";
		ret += "<dia:int val=\"1\"/>";
		ret += "</dia:attribute>";
		ret += "</dia:composite>";
		ret += "</dia:attribute>";
		ret += "<dia:attribute name=\"guides\">";
		ret += "<dia:composite type=\"guides\">";
		ret += "<dia:attribute name=\"hguides\"/>";
		ret += "<dia:attribute name=\"vguides\"/>";
		ret += "</dia:composite>";
		ret += "</dia:attribute>";
		ret += "</dia:diagramdata>";
		ret += "<dia:layer name=\"Hintergrund\" visible=\"true\">";
				
		return ret;
	}
	
	private String getDiaFooter()
	{
		return "</dia:layer></dia:diagram>\n";
  }
}
