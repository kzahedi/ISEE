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

package hinton.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class represents a statuspanel and -filelogger combined with the
 * funny view of the castle at program startup :-) 
 *
 * It searches communication class-files an binds them dynamically to hinton.
 *
 */
public class StatusDialog extends JDialog
{
  
  private long   wait;
  private JLabel status, gif;
  private JPanel s, c, sn, ss, sc;

  private StatusOutputStream sos;
  
  public StatusDialog(long stepWait)
  {
    super();
    
    
    FlowLayout fl = new FlowLayout(FlowLayout.LEFT);
    this.wait = stepWait;

    status = new JLabel("init... ");
    c      = new JPanel();
    s      = new JPanel();
    sn     = new JPanel();
    ss     = new JPanel();
    sc     = new JPanel();

    c.setLayout(new FlowLayout());
    c.add(new JLabel("Hinton.Executor - Neural Net execution & Evaluation"));
    c.add(new JLabel("      (c) 2002 Sankt Augustin,Germany  "));
    c.add(new JLabel("Keyan (keyan@users.sourceforge.net)"));
    c.add(new JLabel("http://sourceforge.net/projects/isee/\n\n"));
    
    s.setLayout(new BorderLayout());
    s.add      (sn, BorderLayout.NORTH );
    s.add      (ss, BorderLayout.SOUTH );
    s.add      (sc, BorderLayout.CENTER);
    
    ss.setLayout(fl);
    ss.add(status);
    
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(c, BorderLayout.CENTER);
    this.getContentPane().add(s, BorderLayout.SOUTH );

    
    this.setTitle("Hinton");
    this.setResizable(false);
    //this.setSize(550,350);
    this.pack();

    sos = new StatusOutputStream();
    System.setOut(new PrintStream(sos));

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); 
    int x = (int)(screenSize.getWidth()/2.0d);
    int y  = (int)(screenSize.getHeight()/2.0d);
    x = x - (int)(this.getWidth()/2.0d);
    y = y - (int)(this.getHeight()/2.0d);
    this.setLocation(x,y);

    this.show();
  }

  public void interWait()
  {
    if (!this.isShowing())
    {
      sos.kill();
      sos = null;
      return;
    }
    try
    {
      Thread.sleep(wait);
    } catch (Exception ex)
    {
    }
  }

  class StatusOutputStream extends OutputStream
  {
    private PrintStream std;
    private boolean eol;
    
    public StatusOutputStream()
    {
      std = System.out;
      eol = false;
    }

    public void kill()
    {
      System.setOut(std);
    }
    
    public void write (int b)
    {
      if (eol)
      {        
        interWait();
        status.setText("init... ");
      }
      if ((char)b != '\n')
      {
        status.setText(status.getText() + (char)b);
        eol = false;
      } else
      {
        eol = true;
      }
      std.write(b);
    }
  }

}
