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
 * Created on 19.04.2004
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;


/**
 * @author rosemann
 * This class implements a VisualNetExporter for bitmap file
 * Format such as JPEG and PNG
 */
public class ImageExporter extends VisualNetExporter
{

	private InputValue.IObject  format;
	private InputValue.IObject  scale;
	private InputValue.IBoolean antialias;

	public ImageExporter()
	{
		super();
	}
	/**
	 * @param visualNet
	 */
	public ImageExporter(VisualNet visualNet) {
		super(visualNet);
	}

	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.VisualNetExporter#getProperties()
	 */
	protected InputValue[] getProperties() {
		if (this.format == null)
		{
			this.format = new InputValue.IObject("Image format",
																					0,
																					ImageIO.getWriterFormatNames());
			this.scale  = new InputValue.IObject("Size",
																					 3,
																					 new Double[] { new Double(0.25d),
																												  new Double(0.5d),
																													new Double(0.75d),
																													new Double(1.0d),
																												  new Double(1.25d) },
																					 new String[] { "25%",
																													"50%",
																													"75%",
																													"100%",
																													"125%" } );
			this.antialias = new InputValue.IBoolean("Antialiasing",
                                               true);

																																						
		}
		return new InputValue[] { this.format,
															this.scale,
															this.antialias };
	}

	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.VisualNetExporter#exportNet(java.io.OutputStream)
	 */
	protected void exportNet(OutputStream fos) throws IOException {
			Dimension         d  = this.getDimension();
			double            s  = ((Double)this.scale.value).doubleValue();
			BufferedImage     bi = new BufferedImage(d.width,
			                                         d.height,
			                                         BufferedImage.TYPE_INT_RGB);

			BufferedImage     out = new BufferedImage((int)(d.width  * s),
																						    (int)(d.height * s),
																						    BufferedImage.TYPE_INT_RGB);
			Graphics2D        g2d = bi.createGraphics();
		  NeuronRenderer[]  nr;
		  SynapseRenderer[] sr;
			
			if (this.antialias.value)
			{
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
				                     RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING, 
														 RenderingHints.VALUE_RENDER_QUALITY);
				g2d.setRenderingHint(RenderingHints.KEY_DITHERING,
														 RenderingHints.VALUE_DITHER_ENABLE);
			}


			g2d.setColor(Color.WHITE);
			g2d.fillRect(0,0, bi.getWidth(), bi.getHeight());

			sr = this.visualNet.getSynapseRenderer();
			for (int i = 0; i < sr.length; i++)
			{
				sr[i].paint(g2d.create(sr[i].getX(), sr[i].getY(),
				                    sr[i].getWidth(), sr[i].getHeight()));
			}		
			
			nr = this.visualNet.getNeuronRenderer();
			for (int i = 0; i < nr.length; i++)
			{
				nr[i].paint(g2d.create(nr[i].getX(), nr[i].getY(),
														nr[i].getWidth(), nr[i].getHeight()));
			}
		
			
			g2d = out.createGraphics();
			if (this.antialias.value)
			{
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
												 		 RenderingHints.VALUE_ANTIALIAS_ON);
			}
			

			g2d.scale(s, s);
			g2d.drawImage(bi, null, 0, 0);

			ImageIO.write(out,
										(String)this.format.value,
										fos);		  
	}

	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.VisualNetExporter#getExportFormatName()
	 */
	public String getExportFormatName() {
		return "Image Exporter";
	}

	/* (non-Javadoc)
	 * @see Hinton.analyser.netstructure.VisualNetExporter#getFileFormatExtension()
	 */
	protected String getFileFormatExtension() {
		if (this.format != null)
		{
			return (String)this.format.value;
		}
		return "";
	}

	/**
	 * determines the size of the smallest rectangle bordering the
	 * visualized digraph
	 * @return graph bordering rectangle
	 */
	private Dimension getDimension()
	{
		Dimension        d  = new Dimension();
		NeuronRenderer[] nr = this.visualNet.getNeuronRenderer();
		int              w, h;
	
				d.width  = 0;
				d.height = 0;
		for (int i = 0; i < nr.length; i++)
		{
			if ((w = nr[i].getX() + nr[i].getWidth()) > d.width)
			{
				d.width  = w + 100;
			}
			if ((h = nr[i].getY() + nr[i].getHeight()) > d.height)
			{
				d.height = h + 100;
			}
		}
		return d;
	}
}
