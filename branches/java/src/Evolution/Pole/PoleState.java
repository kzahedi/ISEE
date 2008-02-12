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

public class PoleState{

    private double forceSignal;
    private double locCar;
    private double angPole;
    private double vCar;
    private double vAng;
    private double aCar;
    private double aAng;
    
    public PoleState(){
  forceSignal = 0.0;
  locCar  = 0.0;
  angPole  = 0.0;
  vCar  = 0.0;
  vAng  = 0.0;
  aCar  = 0.0;
  aAng  = 0.0;
    }
    
    public void fill(double force, 
         double loc,  double ang, 
         double vCar, double vAng,
         double aCar, double aAng){
  
  forceSignal = force;
  locCar      = loc;
  angPole     = ang;
  vCar        = vCar;
  vAng        = vAng;
  aCar        = aCar;
  aAng        = aAng;
    }
    
    public double[] getValues(){
  double[] v = new double[7];
  v[0] = forceSignal;
  v[1] = locCar;
  v[2] = angPole;
  v[3] = vCar;
  v[4] = vAng;
  v[5] = aCar;
  v[6] = aAng;
  return v;
    }
    
    public void setAclAng(double v){
  aAng = v;
    }
    
    public double getAclAng(){
  return aAng;
    }
    
    public void setAclCar(double v){
  aCar = v;
    }

    public double getAclCar(){
  return aCar;
    }
    
    public void setVelAng(double v){
  vAng = v;
    }
    
    public double getVelAng(){
  return vAng;
    }
    
    
    public void setVelCar(double v){
  vCar = v;
    }
    
    public double getVelCar(){
  return vCar;
    }
    
    
    public void setAng(double v){
  angPole = v;
    }
    
    public double getAng(){
  return angPole;
    }
    
    public void setLoc(double v){
  locCar = v;
    }
    
    public double getLoc(){
  return locCar;
    }
    
    public void setForceSignal(double v){
  forceSignal = v;
    }
    
    public double getForceSignal(){
  return forceSignal;
    }

    public String toString(){
  String s = new String(  
      Double.toString(forceSignal) + "\t" +
      Double.toString(locCar) + "\t" +
      Double.toString(angPole) + "\t" +
      Double.toString(vCar) + "\t" +
      Double.toString(vAng) + "\t" +
      Double.toString(aCar) + "\t" +
      Double.toString(aAng) + "\n");
  return s;
    }
}





