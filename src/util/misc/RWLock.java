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


public class RWLock
{
  private int givenLocks;
  private int waitingWriters;
  public static boolean TRACE = false;
    
  private Object mutex;
  
  
  public RWLock()
  {
    mutex = new Object();
    givenLocks = 0;
    waitingWriters = 0;
  }
  
  public void getReadLock()
  {
    synchronized(mutex)
    {

      try
      {
        while((givenLocks == -1) || (waitingWriters != 0))
        {
          if(TRACE)
            System.out.println(Thread.currentThread().toString() + "waiting for readlock");
          mutex.wait();
        }
      }
      catch(java.lang.InterruptedException e)
      {
        System.out.println(e);
      }
      
      givenLocks++;
      
      if(TRACE)
        System.out.println(Thread.currentThread().toString() + " got readlock, GivenLocks = " + givenLocks);
      
    }
  }
  
  public void getWriteLock()
  {
    synchronized(mutex)
    {
      waitingWriters++;
      try
      {
        while(givenLocks != 0)
        {
          if(TRACE)
            System.out.println(Thread.currentThread().toString() + "waiting for writelock");
          mutex.wait();
        }
      }
      catch(java.lang.InterruptedException e)
      {
        System.out.println(e);
      }
      
      waitingWriters--;
      givenLocks = -1;
  
      if(TRACE)
        System.out.println(Thread.currentThread().toString() + " got writelock, GivenLocks = " + givenLocks);
    }
  }
  
  
  public void releaseLock()
  { 
    
    synchronized(mutex)
    {

      if(givenLocks == 0)
        return;
        
      if(givenLocks == -1)
        givenLocks = 0;
      else
        givenLocks--;
      
      if(TRACE)
        System.out.println(Thread.currentThread().toString() + " released lock, GivenLocks = " + givenLocks);

      mutex.notifyAll();
    }
  }


}



