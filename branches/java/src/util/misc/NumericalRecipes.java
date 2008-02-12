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


package util.misc;


/*

  LU Decomposition:
  Reference: Numerical Recipes in C, pp. 39-45

  This program solves equations of the form Ax=b

  LUDCMP:
  void ludcmp(a, n, indx, d)
  Given an n x n matrix a[1..n][1..n], this routine replaces it by
  the LU decomposition of a row-wise permutation of itself. a and
  n are input. a is output in which the rows are arranged in a 
  particular order. indx[1..n] is an output vector which records the
  row permutation effected by partial pivoting. d is an output as
  +1 or -1, depending on whether the number of row interchanges was
  even or odd respectively. This routine is used in combination with
  lubksb to solve linear equations or invert a matrix.

  LUBKSB
  void lubksb(a, n, indx, b)
  Solves a set of linear equations A.X = B. Here a[1..n][1..n] is 
  input, not as the input matrix A but as its LU decomposition, 
  determined by the routine ludcmp. b[1..n] is input as the 
  right-hand side vector B,, and returns with the solution vector
  X. a, n, and indx are not modified by this routine  and can be
  left in place for successive calls with different right hand sides 
  b. This routine takes into account the possiblitiy that b will begin 
  with many zero elements so it is efficient for use with matrix 
  inverson too.


  Solving a set of linear equations A x = b
  
  float **a, *b, d;
  int n, *indx;
  ....    mem allocation and read a, b, code here
  ludcmp(a,n,indx,&d);
  lubksb(a,n,indx,b);

  The anser x will be given back in b. The original matrix A would be 
  destroyed. If another system is to be solved with the same A but
  different b, repeat ONLY lubksb(a,n,indx,b); Use the A that was 
  returned by the first call to lubksb

  The small example program below solves the linear system A x = b for
  A =  [1 2
    3 4]
  b =  [2
    5]

  IMPORTANT NOTE:
  the routines are written such that the data should be in index 1..n and
  NOT 0..n-1. i.e. the top right element of matrix A is at location a[1][1].
  The bottom left element is at location a[n][n], where n is the size of
  the matrix. Therefore, arguments to malloc and calloc are n+1;
*/


public class NumericalRecipes
{

  private final static int MSIZE = 2; // for test routine

  private final static double TINY = 1.0e-20; // for ludcmp

  public static double ludcmp(double[][] a, int n, int[] indx)
  {
    int imax = -100;
    double big, dum, sum, temp;
    double[] vv;
    double d = 0;


    vv = new double[n];
    d = 1.0;


    for (int i=0; i < n; i++)
    {
      big=0.0;
      for (int j=0; j < n; j++)
      {
        temp=Math.abs(a[i][j]);
        if (temp > big)
        {
          big=temp;
        }
      }
      if (big == 0.0)
      {
        System.out.println("Singular martix in routine LUDCMP");
      }
      vv[i] = 1.0/big;
    }


    for (int j=0; j < n;j++)
    {
      for (int i=0; i<j-1; i++)
      {
        sum=a[i][j];
        for (int k=0;k<i;k++)
        {
          sum -= a[i][k]*a[k][j];
        }
        a[i][j]=sum;
      }
      big=0.0;
      for (int i=j; i < n; i++)
      {
        sum=a[i][j];
        for (int k=0;k<j;k++)
        {
          sum -= a[i][k] * a[k][j];
        }
        a[i][j]=sum;
        if ( (dum=vv[i]*Math.abs(sum)) >= big)
        {
          big = dum;
          imax=i;
        }
      }

      if (j != imax)
      {
        for (int k=0;k<n;k++)
        {
          dum = a[imax][k];
          a[imax][k]=a[j][k];
          a[j][k]=dum;
        }
        d = -(d);
        vv[imax]=vv[j];
      }
      indx[j]=imax;
      if (a[j][j] == 0.0) a[j][j]=TINY;
      if (j != n)
      {
        dum=1.0/(a[j][j]);
        for (int i=j+1;i<n;i++)
          a[i][j] *= dum;
      }
    }
    return d;
  }

  public static void lubksb(double[][] a, int n, int[] indx, double[] b)
  {
    int i,ii=-1,ip,j;
    double sum;

    for (i=0;i<n;i++) 
    {
      ip=indx[i];
      sum=b[ip];
      b[ip]=b[i];
      if (ii != -1)
      {
        for (j=ii;j<=i-1;j++)
        {
          sum -= a[i][j]*b[j];
        }
      }
      else if (sum != 0.0)
      {
        ii=i;
      }
      b[i]=sum;
    }
    for (i=n-1;i>=0;i--) 
    {
      sum=b[i];
      for (j=i+1;j<n;j++)
      {
        sum -= a[i][j]*b[j];
      }
      b[i]=sum/a[i][i];
    }
  }                       

  public static void main(String argv[])
  {
    double[][] a    = new double[MSIZE][MSIZE];
    double[]   b    = new double[MSIZE];
    int        n    = 0;
    int[]      indx = new int[MSIZE];
    double     d    = 0;

    /* Test Data */
    a[0][0]=1.0;
    a[0][1]=2.0;
    a[1][0]=3.0;
    a[1][1]=4.0;

    n=MSIZE;

    b[0]=2;
    b[1]=5;

    D.bug("********* BEFORE ***********");
    for(int i=0; i < MSIZE; i++)
    {
      for(int j=0; j < MSIZE; j++)
      {
        D.bug("a["+i+"]["+j+"] = " + a[i][j]);
      }
    }
    for(int i=0; i < MSIZE; i++)
    {
        D.bug("b["+i+"] = " + b[i]);
    }
    D.bug("********* LUDCMP ***********");
    d = NumericalRecipes.ludcmp(a,n,indx);
    D.bug("d : " + d);
    for(int i=0; i < MSIZE; i++)
    {
      for(int j=0; j < MSIZE; j++)
      {
        D.bug("a["+i+"]["+j+"] = " + a[i][j]);
      }
    }
    for(int i=0; i < MSIZE; i++)
    {
      D.bug("indx["+i+"] = " + indx[i]);
    }

    D.bug("********* LUBKSB ***********");
    for(int i=0; i < MSIZE; i++)
    {
      D.bug("b["+i+"] = " + b[i]);
    }

    NumericalRecipes.lubksb(a,n,indx,b);
    for(int i=0; i < MSIZE; i++)
    {
      for(int j=0; j < MSIZE; j++)
      {
        D.bug("a["+i+"]["+j+"] = " + a[i][j]);
      }
    }
    for(int i=0; i < MSIZE; i++)
    {
        D.bug("b["+i+"] = " + b[i]);
    }

  }
}
