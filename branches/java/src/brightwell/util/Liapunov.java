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


package brightwell.util;

import brightwell.analyser.DataStorage;
import brightwell.analyser.Tool;
import brightwell.gui.drawingplane.DrawingPlane;
import brightwell.gui.Error;

import cholsey.Net;
import cholsey.Neuron;
import cholsey.Synapse;
import cholsey.SynapseList;
import cholsey.SynapseMode;
import cholsey.Transferfunction;


import java.awt.Color;
import java.awt.Component;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;

import util.misc.Functions;
import util.misc.IseeLogger;
import util.misc.Matrix;

public class Liapunov 
{

  private int           _iterations        = 0;
  private int           _dimension         = -1;
  private double[][]    _qMatrix           = null;
  private double[][]    _rMatrix           = null;
  private double[][]    _dMatrix           = null;
  private double[][]    _jacobiMatrix      = null;
  private double[]      _liapunovExponents = null;
  private Net           _net               = null;

  private static Logger logger             = IseeLogger.getLogger(Liapunov.class);

  public Liapunov(Net net)
  {
    logger.debug("Liapunov constructor called with net parameter");
    this._net = net;
    this._dimension = _net.size();
    reset();
  }

  public double[] getLiapunovExponents()
  {
    logger.debug("getLiapunovExponents called");
    debug("Liapunov exponents are", _liapunovExponents);
    return _liapunovExponents;
  }

  public void reset()
  {
    initialiseCaluclation();
  }

  public void calculate(int currentIteration)
  {
      logger.debug("calling - in loop iteration " + currentIteration + " - begin");
      logger.debug("calling - in loop iteration " + currentIteration + " - calculateDeviationMatrix");
      _jacobiMatrix = calculateDeviationMatrix();
      debug("calling - in loop iteration " + currentIteration + " - calculateDeviationMatrix (Ableitungsmatrix) - result:", _jacobiMatrix);

      logger.debug("calling - in loop iteration " + currentIteration + " - matrix multiplication d = _jacobiMatrix * _qMatrix");
      _dMatrix = Matrix.multiply(_jacobiMatrix, _qMatrix);
      debug("calling - in loop iteration " + currentIteration + " - matrix multiplication d = _jacobiMatrix * _qMatrix - result", _dMatrix);

      logger.debug("calling - in loop iteration " + currentIteration + " - matrix qr whatever _qMatrix * _rMatrix = _dMatrix");
      _rMatrix = Matrix.QR(_dMatrix, _qMatrix);
      debug("calling - in loop iteration " + currentIteration + " - matrix qr whatever _qMatrix * _rMatrix = _dMatrix - result of _rMatrix", _rMatrix);
      debug("calling - in loop iteration " + currentIteration + " - matrix qr whatever _qMatrix * _rMatrix = _dMatrix - result of _qMatrix", _qMatrix);

      logger.debug("calling - in loop iteration " + currentIteration + " - iteration liapunov");
      liapunovIteration(_rMatrix, currentIteration);
      debug("Liapunov exponents", _liapunovExponents);

  }


  // **************************************************************************
  // initialise function
  // **************************************************************************


  private void initialiseLiapunovExponents()
  {
    _liapunovExponents = new double[_dimension];
    for(int i=0;i<_dimension;i++)
    {
      _liapunovExponents[i]=0.0;
    }
  }


  private void initialiseCaluclation()
  {
    _qMatrix      = new double[_dimension][_dimension];
    _rMatrix      = new double[_dimension][_dimension];
    _dMatrix      = new double[_dimension][_dimension];
    _jacobiMatrix = new double[_dimension][_dimension];
    logger.debug("initialiseCaluclation - start");

    logger.debug("initialiseCaluclation - calling initialiseLiapunovExponents");
    initialiseLiapunovExponents();
    debug("Liapunov exponents", _liapunovExponents);

    logger.debug("initialiseCaluclation - initialising _qMatrix");
    Matrix.initialiseDiagonalMatrix(_qMatrix);
    debug("q Matrix", _qMatrix);

    logger.debug("initialiseCaluclation - initialising _rMatrix");
    Matrix.initialiseDiagonalMatrix(_rMatrix);
    debug("r Matrix", _rMatrix);


    logger.debug("initialiseCaluclation - initialising _dMatrix");
    Matrix.initialiseDiagonalMatrix(_dMatrix);
    debug("d Matrix", _dMatrix);

    logger.debug("initialiseCaluclation - initialising _jacobiMatrix ");
    Matrix.initialiseZeroMatrix(_jacobiMatrix);
    debug("_jacobiMatrix", _jacobiMatrix);

    logger.debug("initialiseCaluclation - end");
  }

  
  private void liapunovIteration(double[][] matrix, int iteration)
  {
    double rb = 0;
    double la = 0;
    for(int i=0; i < _dimension; i++)
    {
      rb = Math.abs(matrix[i][i]);
      if(rb<=0.000000001){
        rb=0.000001;
      }
      la = _liapunovExponents[i];
      _liapunovExponents[i] = ( (double)(iteration) / (double)(iteration+1)) * la + Math.log(rb)/(double)(iteration+1);
    }
  }

  private double[][] calculateDeviationMatrix()
  {
    double[][] result = new double[_dimension][_dimension];

//    result[0][0] = 0.6 
//      + _net.neurons().neuron(0).getSynapse(_net.neurons().neuron(0)).strength() 
//      * Functions.sigmoid1(_net.neurons().neuron(0).getActivation());
//
//    result[0][1] = _net.neurons().neuron(0).getSynapse(_net.neurons().neuron(1)).strength() 
//      * Functions.sigmoid1(_net.neurons().neuron(1).getActivation());
//
//    result[1][0] = _net.neurons().neuron(1).getSynapse(_net.neurons().neuron(0)).strength()
//      * Functions.sigmoid1(_net.neurons().neuron(0).getActivation());
//
//    result[1][1] = 0.6 
//      + _net.neurons().neuron(1).getSynapse(_net.neurons().neuron(1)).strength()
//      * Functions.sigmoid1(_net.neurons().neuron(1).getActivation());
   
    for(int i=0; i < _dimension; i++)
    {
      for(int j=0; j < _dimension; j++)
      {
        Synapse s = _net.neurons().neuron(i).getSynapse(_net.neurons().neuron(j));
        double w           = 0;
        if(s != null)
        {
          w = s.strength(); // w_ij
        }
        double oDerivative = 0;
        switch(_net.getTransferfunction().mode())
        {
          case Transferfunction.USE_TANH:
            oDerivative = Functions.tanh1(_net.neurons().neuron(j).getActivation());
            break;
          case Transferfunction.USE_SIGM:
            oDerivative = Functions.sigmoid1(_net.neurons().neuron(j).getActivation());
            break;
        }
        logger.debug("result[" + i + "][" + j + "] (old) = " + result[i][j]);
        result[i][j] = w * oDerivative;
        if(i == j) 
        {
          result[i][j] += _net.neurons().neuron(j).getKappa();
        }
        logger.debug("result[" + i + "][" + j + "] (new) = " + result[i][j]);
      }
    }
  

    return result;
  }

  // **************************************************************************
  // debugging functions
  // **************************************************************************

  private void debug(Net _net)
  {
    logger.debug("Neuron 1: " + _net.neurons().neuron(0).getActivation());
    logger.debug("Neuron 2: " + _net.neurons().neuron(1).getActivation());
  }


  private void debug(String string, double[] vector)
  {
    int _dimension = vector.length;
    logger.debug(string);
    String s = "";
    for(int i=0; i < _dimension; i++)
    {
      s = s.concat("  " + vector[i]);
    }
    logger.debug(s);
  }

  public void debug(String string, double[][] A)
  {
    logger.debug(string);
    for(int i=0; i < _dimension; i++)
    {
      String s = "";
      for(int j=0; j < _dimension; j++)
      {
        s = s.concat("  " + A[i][j]);
      }
      logger.debug(s);
    }
  }


}

