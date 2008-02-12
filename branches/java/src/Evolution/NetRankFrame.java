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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class NetRankFrame extends JFrame  implements ActionListener{
  Evolution evo = null;
  //  PopulationList pops = null;
  
  
  public NetRankFrame(Evolution evoTask) {
    super();
    
    this.evo  = evoTask;
    // this.pops = this.evo.getPopulations();
    
    setTitle("Net Rank");
    setBounds (50,50, 450,800);
    
    //addWindowListener(new WindowAdapter() {
    //  public void windowClosing(WindowEvent e) {System.exit(0);}
    //  });
    
    NetRankPanel blackboard = new NetRankPanel(this.evo); 
    

    setContentPane(blackboard.getPanel());
    setVisible(false);

    this.evo.setNetRankWin(this);
  }
  
  
  public void actionPerformed(ActionEvent e){
    ;;
  }
  
  public void switchVisible(){
    this.setVisible(!this.isVisible());
  }


  
  public static void main(String[] args) {
    /*
      PopulationList list = new PopulationList();
      Population pop = new Population("test", 45);
      list.add(pop);
      NetRankFrame monitor = new NetRankFrame(list);
    */
  }
}















