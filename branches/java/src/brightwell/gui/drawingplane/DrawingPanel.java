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
package brightwell.gui.drawingplane;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Vector;

import javax.swing.JPanel;




public class DrawingPanel extends JPanel implements Printable
{


  private Font normalFont     = null;
  private Font thetaFont      = null;
  private Font subscriptFont  = null;

          //new TokEntry ("theta", '\u03b8', GREEK),
          //new TokEntry ("Theta", '\u0398', GREEK),

  private NumberFormat numberFormat = NumberFormat.getInstance(Locale.UK);

  private static final int BLOCK_SIZE = 20;
  private static final int BLOCK_SIZE_X = 25;
  private static final int BLOCK_SIZE_Y = 16;

  private final static int X_OFFSET = 50;
  private final static int Y_OFFSET = 50;

  private final static int X_TEXT_OFFSET = 25;
  private final static int Y_TEXT_OFFSET = 15;


  private double[] x = new double[2];
  private double[] y = new double[2];

  private boolean useBigBoints = false;

  private Graphics2D g = null;

  private BufferedImage offscreen = null;

  public DrawingPanel()
  {
    super();
  }

  public void setUseBigPoints(boolean useBigBoints)
  {
    this.useBigBoints = useBigBoints;
  }
  
  public int getXOffset()
  {
    return X_OFFSET;
  }

  public int getYOffset()
  {
    return Y_OFFSET;
  }


  public void drawProgressPointY(double y, Color color)
  {
    drawSmallPoint(x[0],y,color);
  }

  public void drawProgressPointX(double x, Color color)
  {
    drawSmallPoint(x, y[0], color);
  }


  public int print(Graphics gp, PageFormat pf, int pi)
    throws PrinterException {
      if (pi >= 1) {
        return Printable.NO_SUCH_PAGE;
      }
      gp.drawImage(offscreen,0,0, this);
      return Printable.PAGE_EXISTS;
    }

  public BufferedImage getImage()
  {
    return offscreen;
  }

  public void setRange(double[] x, double[] y)
  {
    this.x= x;
    this.y= y;
  }

  public int getDrawingWidth()
  {
    return super.getWidth() - 2 * X_OFFSET;
  }

  public int getDrawingHeight()
  {
    return super.getHeight() - 2 * Y_OFFSET;
  }

  public void drawPeriodBlock(int period, Color[] colors, int step)
  {
    if (period == 0)
    {
      return;
    }
    if ( g == null)
    {
      init();
    }

    String text = null;
    if(period == colors.length)
    {
      text = new String("ch");
    }
    else 
    {
      text = new String(""+period);
    }

    g.setColor(colors[Math.max(0,period-1)]);
    int xp = xCoord(x[1])+ (int)(X_TEXT_OFFSET/2);
    int yp = yCoord(y[1]) + (step-1) * (BLOCK_SIZE + 5);
    g.fillRect(xp, yp, BLOCK_SIZE, BLOCK_SIZE);
    g.setColor(Color.black);
    g.drawRect(xp, yp, BLOCK_SIZE, BLOCK_SIZE);
    if(period == colors.length)
    {
      g.setColor(Color.white);
    }
    g.drawString(text, xp+3, yp+13);
    super.repaint();
  }

  public void drawPeriodLegend(Vector periodVector, Color[] colors)
  {
    java.util.List list = new ArrayList();
    for(int i=0;i<periodVector.size();i++)
    {
      list.add((Integer)periodVector.elementAt(i));
    }

    Collections.sort(list, new Comparator() {
        public int compare(Object o1, Object o2) {
        int name1 = ((Integer)o1).intValue();
        int name2 = ((Integer)o2).intValue();
        return (name2 > name1)?0:1;}});

    for(int i=0;i<periodVector.size();i++)
    {
      int period = ((Integer)list.get(i)).intValue();
      drawPeriodBlock(period,colors,i);
    }
  }

  public void drawPoint(double x_p, double y_p) 
  {
    drawPoint(x_p, y_p, Color.black);
  }

  public void drawXLine(double height)
  {
    drawXLine(height, Color.black);
  }

  public void drawXLine(double height, Color color)
  {
    if (g==null)
    {
      init();
    }
    g.setColor(color);
    g.drawLine(xCoord(x[0]),yCoord(height),xCoord(x[1]),yCoord(height));
    g.drawString(numberFormat.format(height),
        (int)(xCoord(x[0]) -
        g.getFontMetrics().stringWidth(numberFormat.format(height))) -
        10,
        yCoord(height) - Y_TEXT_OFFSET + 20 );
    super.repaint(0,0,0,super.getWidth(),super.getHeight());
  }

  public void drawXLineNoLegend(double height, Color color)
  {
    if (g==null)
    {
      init();
    }
    g.setColor(color);
    g.drawLine(xCoord(x[0]),yCoord(height),xCoord(x[1]),yCoord(height));
    super.repaint(0,0,0,super.getWidth(),super.getHeight());
  }



  public void drawYLine(double height)
  {
    drawYLine(height, Color.black);
  }

  public void drawYLine(double height, Color color)
  {
    if (g==null)
    {
      init();
    }
    g.setColor(color);
    g.drawLine(xCoord(height),yCoord(y[0]),xCoord(height),yCoord(y[1]));
    g.drawString(numberFormat.format(height),
        (int)(xCoord(height) -
        g.getFontMetrics().stringWidth(numberFormat.format(height))/2.0),
        yCoord(y[0]) - Y_TEXT_OFFSET + 30 );
    super.repaint(0,0,0,super.getWidth(),super.getHeight());
  }


  public void drawLine(double x0_p, double y0_p, double x1_p, double y1_p)
  {
    drawLine( x0_p,  y0_p,  x1_p,  y1_p, Color.black);
  }

  public void drawLine(double x0_p, double y0_p, double x1_p, double y1_p, 
      Color color)
  {
    if(g == null)
    {
      init();
    }
    g.setColor(color);
    g.drawLine(xCoord(x0_p), yCoord(y0_p), xCoord(x1_p), yCoord(y1_p));
    int x1 = xCoord(x0_p);
    int x2 = xCoord(x1_p);
    int y1 = xCoord(y0_p);
    int y2 = xCoord(y1_p);
    super.repaint();
  }

  public void drawPoint(double x_p, double y_p, Color color)
  {
    if(useBigBoints)
    {
      drawBigPoint(x_p, y_p, color);
    }
    else
    {
      drawSmallPoint(x_p, y_p, color);
    }
  }

  public void drawSmallPoint(double x_p, double y_p, Color color) 
  {
    if (g == null)
    {
      init();
    }
    g.setColor(color);
    int xP = xCoord(x_p);
    int yP = yCoord(y_p);
    g.drawLine(xP,yP,xP,yP);
    super.repaint(0,xP,yP,1,1);
  }

  public void drawBigPoint(double x_p, double y_p, Color color) 
  {
    if (g == null)
    {
      init();
    }
    g.setColor(color);
    int xP = xCoord(x_p);
    int yP = yCoord(y_p);
    g.fillRect(xP-1,yP-1,3,3);
    super.repaint(0,xP-1,yP-1,3,3);
  }




  // MUST BE CALLED AFTER!!! the panel is visible!!!
  public void init()
  {
    normalFont = new Font("Arial", Font.BOLD, 15);
    thetaFont  = new Font("Arial", Font.BOLD, 20);
    subscriptFont = new Font("Arial", Font.BOLD, 12);

    offscreen = (BufferedImage)super.createImage(super.getWidth(),
        super.getHeight());
    g = (Graphics2D)offscreen.getGraphics();
    g.setColor(Color.WHITE);
    g.drawRect(0,0,super.getWidth(),super.getHeight());
    g.setColor(Color.BLACK);

    g.setFont(normalFont);

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_RENDERING,
        RenderingHints.VALUE_RENDER_QUALITY);

    super.repaint();
  }

  public void setXLabel(String string)
  {
    setXLabel(string, "", "");
  }

  public void setXLabel(String string, String subscript)
  {
    setXLabel(string, subscript, "");
  }

  public void setXLabel(String string, String subscript, String superscript)
  {
    if (g == null)
    {
      init();
    }


    g.setFont(thetaFont);

    int xC =
      (int)(super.getWidth() / 2d - g.getFontMetrics().stringWidth(string)/2d);

    g.drawString(string,
        xC,
        yCoord(y[0]) + 35);


    xC += (int)(g.getFontMetrics().stringWidth(string)/2d * 1.75);

    g.setFont(subscriptFont);

    g.drawString(subscript,
        xC,
        yCoord(y[0]) + 40);

    g.drawString(superscript,
        xC,
        yCoord(y[0]) + 26);

    g.setFont(normalFont);

    super.repaint();
  }

  public void setYLabel(String string)
  {
    setYLabel(string, "", "");
  }


  public void setYLabel(String string, String subscript)
  {
    setYLabel(string, subscript, "");
  }

  public void setYLabel(String string, String subscript, String superscript)
  {
    if (g == null)
    {
      init();
    }

    g.setFont(thetaFont);

    int yC = (int)(super.getHeight() / 2d) + 5;

    g.drawString(string,
        xCoord(x[0]) - 45,
        yC);

    int xC = xCoord(x[0]) - 45 + g.getFontMetrics().stringWidth(string);


    g.setFont(subscriptFont);

    g.drawString(subscript,
        xC,
        yC + 5);

    g.drawString(superscript,
        xC,
        yC - 7);


    g.setFont(normalFont);

    super.repaint();
  }
  public void drawLegend()
  {
    if (g == null)
    {
      init();
    }
    g.setColor(Color.BLACK);
    g.drawRect(
        xCoord(x[0]),yCoord(y[1]),
        super.getWidth()-2*X_OFFSET,
        super.getHeight()-2*Y_OFFSET);

    g.drawString(
        numberFormat.format(y[1]),
        xCoord(x[0]) - 10 -
        g.getFontMetrics().stringWidth(numberFormat.format(y[1])),
        yCoord(y[1]) + 5);


    g.drawString(
        numberFormat.format(y[0]),
        xCoord(x[0]) - 10 -
        g.getFontMetrics().stringWidth(numberFormat.format(y[0])),
        yCoord(y[0]) + 5);

    g.drawString(
        numberFormat.format(x[0]),
        xCoord(x[0]) - 
        (int)(g.getFontMetrics().stringWidth(numberFormat.format(x[0]))/2.0),
        yCoord(y[0]) + 25);

    g.drawString(
        numberFormat.format(x[1]),
        xCoord(x[1]) - 
        (int)(g.getFontMetrics().stringWidth(numberFormat.format(x[1]))/2.0),
        yCoord(y[0]) + 25);


//
//    g.drawString("("
//        + numberFormat.format(x[0])
//        + ","
//        + numberFormat.format(y[0])
//        + ")",
//        xCoord(x[0]) - X_TEXT_OFFSET,
//        yCoord(y[0]) + Y_TEXT_OFFSET);
//
//    g.drawString("("
//        + numberFormat.format(x[1])
//        + ","
//        + numberFormat.format(y[0])
//        + ")",
//        xCoord(x[1]) - X_TEXT_OFFSET,
//        yCoord(y[0]) + Y_TEXT_OFFSET);
//
    super.repaint();
  }

  public int xCoord(double xValue)
  {
    return (int)(0.5 + X_OFFSET + 
        (super.getWidth() - 2 * X_OFFSET) *
        (xValue - x[0])/(x[1]-x[0]));
  }

  public int yCoord(double yValue)
  {
    return super.getHeight() - (int)(
        0.5 +
        Y_OFFSET +
        (super.getHeight() - 2 * Y_OFFSET) *
        (yValue - y[0]) / (y[1]-y[0]));
  }

  public double reverseXCoord(int xCoord)
  {
    double xTmp = (xCoord - 56 ) / (double)(super.getWidth()-2*X_OFFSET);
    return x[0] + xTmp * (x[1] - x[0]);
  }

  public double reverseYCoord(int yCoord)
  {
    double yTmp = (yCoord - 80 ) / 
      (double)(super.getHeight()-2*Y_OFFSET);
    return y[1] - yTmp * (y[1] - y[0]);
  }


  public void paintComponent(Graphics g)
  {
    if(offscreen == null)
    {
      return;
    }
    g.drawImage(offscreen,0,0, this);
  }

  public void drawTransientPlotLegend(Vector legends, Color[] colors)
  {
    for(int i=0; i< legends.size(); i++)
    {
      drawTransientPlotBlock((String)legends.elementAt(i), colors[i], i);
    }
  }



  private void drawTransientPlotBlock(String text, Color color, int step)
  {
    if ( g == null)
    {
      init();
    }

    g.setColor(color);
    int xp = xCoord(x[1])+ (int)(X_TEXT_OFFSET/2);
    int yp = yCoord(y[1]) + (step-1) * (BLOCK_SIZE + 5);
    g.fillRect(xp, yp, BLOCK_SIZE_X, BLOCK_SIZE_Y);
    g.setColor(Color.black);
    g.drawRect(xp, yp, BLOCK_SIZE_X, BLOCK_SIZE_Y);
    g.drawString(text, xp+3, yp+14);
    super.repaint();
  }

  public void drawFilledCircle(double x, double y, double radius, Color color)
  {
    if (g==null)
    {
      init();
    }
    g.setColor(color);
    g.fillOval(xCoord(x) - (int)(radius/2.0),
        yCoord(y) - (int)(radius/2.0),
        (int)radius,
        (int)radius);
    super.repaint();
  }

  public void setTitle(String title)
  {
    if (g==null)
    {
      init();
    }
    g.setColor(Color.black);
    g.drawString(title, X_OFFSET,20);
    super.repaint();
  }  

  public void drawString(String string, int x, int y)
  {
    if (g==null)
    {
      init();
    }
    g.setColor(Color.black);
    g.drawString(string, x, y);
    super.repaint();
  }
}
