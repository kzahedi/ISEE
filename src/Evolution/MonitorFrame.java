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

package Evolution;

import javax.swing.JFrame;

public class MonitorFrame extends JFrame{
  Evolution evo = null;
  MonitorPanel monitor = null;


  public MonitorFrame(Evolution evoTask) {
    super();
    
    this.evo  = evoTask;
    
    setTitle("population dynamics monitor");
    setBounds (50,50, 800,300);
    
    
    //addWindowListener(new WindowAdapter() {
    //  public void windowClosing(WindowEvent e) {System.exit(0);}
    //});
    
    
    monitor = new MonitorPanel(this.evo); 
    setContentPane(monitor.getPanel());
    setResizable(true);
    setVisible(false);

    this.evo.setMonitorWin(this);

    //monitor.init();
  }
  

  public void switchVisible(){
    if(this.isVisible())
    {
      this.setVisible(!this.isVisible());
    }
    else
    {
      this.setVisible(!this.isVisible());
      monitor.init();
    }
    
  }


}













