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

package Evolution.Pole;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Vector;

import javax.swing.JPanel;


public class PoleDrawPanel extends JPanel{

  private NumberFormat numberFormat = NumberFormat.getInstance(Locale.UK);

  private static final int BLOCK_SIZE = 10;

  private final static int X_OFFSET = 20;
  private final static int Y_OFFSET = 20;

  private final static int X_TEXT_OFFSET = 20;
  private final static int Y_TEXT_OFFSET = 12;


  private double[] x = new double[2];
  private double[] y = new double[2];

  private Graphics g = null;

  private BufferedImage offscreen = null;

  public PoleDrawPanel(){
    super();
  }

  public int getXOffset(){
    return X_OFFSET;
  }

  public int getYOffset(){
    return Y_OFFSET;
  }


  public BufferedImage getImage(){
    return offscreen;
  }

  public void setRange(double[] x, double[] y){
    this.x= x;
    this.y= y;
  }

  public int getDrawingWidth(){
    return super.getWidth() - 2 * X_OFFSET;
  }

  public int getDrawingHeight(){
    return super.getHeight() - 2 * Y_OFFSET;
  }

  public void drawPeriodBlock(int period, Color[] colors, int step){
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



  public void drawPeriodLegend(Vector periodVector, Color[] colors){
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


  public void drawPoint(double x_p, double y_p){
    if (g == null)
    {
      init();
    }

    g.setColor(Color.BLACK);
    int xP = xCoord(x_p);
    int yP = yCoord(y_p);
    g.drawLine(xP,yP,xP,yP);
    super.repaint(0,xP,yP,1,1);
  }



  public void drawXLine(double height){
    if (g==null)
    {
      init();
    }

    g.setColor(Color.black);
    g.drawLine(xCoord(x[0]),yCoord(height),xCoord(x[1]),yCoord(height));
    g.drawString(numberFormat.format(height),
        xCoord(x[0]) - X_TEXT_OFFSET - 10,
        yCoord(height) - Y_TEXT_OFFSET + 20 );
    super.repaint(0,0,0,super.getWidth(),super.getHeight());
  }

  public void drawYLine(double height){
    if (g==null)
    {
      init();
    }
    g.setColor(Color.black);
    g.drawLine(xCoord(height),yCoord(y[0]),xCoord(height),yCoord(y[1]));
    g.drawString(numberFormat.format(height),
        xCoord(height) - X_TEXT_OFFSET + 5,
        yCoord(y[0]) - Y_TEXT_OFFSET + 30 );
    super.repaint(0,0,0,super.getWidth(),super.getHeight());
  }



  public void drawPoint(double x_p, double y_p, Color color){
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

  public void drawBigPoint(double x_p, double y_p, Color color) {
    if (g == null)
    {
      init();
    }

    g.setColor(color);
    int xP = xCoord(x_p);
    int yP = yCoord(y_p);
    g.fillRect(xP-1,yP-1,3,3);
    super.repaint(0,xP,yP,1,1);
  }

  public void drawBigPoint(double x_p, double y_p) {
    if (g == null)
    {
      init();
    }

    g.setColor(Color.black);
    int xP = xCoord(x_p);
    int yP = yCoord(y_p);
    g.fillRect(xP-1,yP-1,3,3);
    super.repaint(0,xP,yP,1,1);
  }




  // MUST BE CALLED AFTER!!! the panel is visible!!!
  public void init(){
    offscreen = (BufferedImage)super.createImage(super.getWidth(), super.getHeight());
    g = offscreen.getGraphics();
    //g.setColor(Color.WHITE);
    //g.drawRect(0,0,super.getWidth(),super.getHeight());
    //g.setColor(Color.BLACK);
    //super.repaint();
  }


  public void drawLegend(){

    if (g == null)
    {
      init();
    }

    g.clearRect(0,0,super.getWidth(),super.getHeight());

    g.setColor(Color.BLACK);
    g.drawRect(
        xCoord(x[0]),yCoord(y[1]),
        super.getWidth()-2*X_OFFSET,
        super.getHeight()-2*Y_OFFSET);

    g.drawString(numberFormat.format(y[1]),
        xCoord(x[0]) - X_TEXT_OFFSET,
        yCoord(y[1]) - Y_TEXT_OFFSET + 5);

    g.drawString("("
        + numberFormat.format(x[0])
        + ","
        + numberFormat.format(y[0])
        + ")",
        xCoord(x[0]) - X_TEXT_OFFSET,
        yCoord(y[0]) + Y_TEXT_OFFSET);

    g.drawString(numberFormat.format(x[1]),
        xCoord(x[1]) - 40,
        yCoord(y[0]) + Y_TEXT_OFFSET );

    super.repaint();
  }



  public int xCoord(double xValue){
    return (int)(0.5 + X_OFFSET + 
        (super.getWidth() - 2 * X_OFFSET) *
        (xValue - x[0])/(x[1]-x[0]));
  }



  public int yCoord(double yValue){
    return super.getHeight() - (int)(
        0.5 +
        Y_OFFSET +
        (super.getHeight() - 2 * Y_OFFSET) *
        (yValue - y[0]) / (y[1]-y[0]));
  }



  public double reverseXCoord(int xCoord){
    double xTmp = (xCoord - 56 ) / (double)(super.getWidth()-2*X_OFFSET);
    return x[0] + xTmp * (x[1] - x[0]);
  }

  public double reverseYCoord(int yCoord){
    double yTmp = (yCoord - 80 ) / 
      (double)(super.getHeight()-2*Y_OFFSET);
    return y[1] - yTmp * (y[1] - y[0]);
  }


  public void paintComponent(Graphics g){
    if(offscreen == null)
    {
      return;
    }
    g.drawImage(offscreen,0,0, this);
  }

  public void paintComponent(){
    paintComponent(this.g);
  }
}

