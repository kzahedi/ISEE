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


package newbury;

import cholsey.Net;



import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import util.io.EvoTaskXMLHandler;
import util.misc.IseeLogger;

public class NewBury 
{
  private static Logger log = IseeLogger.getLogger(NewBury.class);
  private EvoTaskXMLHandler xmlHandler = new EvoTaskXMLHandler();
  private PrintWriter  out;

  public NewBury(String argv[])
  {
    ArrayList nets = new ArrayList();

    if(argv.length < 3)
    {
      System.out.println("java -jar newbury.jar -o output.xml ");
      System.exit(-1);
    }

    if(argv[0].trim().equals("-o") == false)
    {
      System.out.println("java -jar newbury.jar -o output.xml ");
      System.exit(-1);
    }

    String outputfilename = argv[1];

    for(int i=2; i < argv.length; i++)
    {
      log.debug("reading file: " + argv[i]);
      Net net = xmlHandler.readNetFromFile(argv[i],0,0,0);
      if(net != null)
      {
        nets.add(net);
        log.debug("LOADED File " + argv[i]);
      }
      else
      {
        log.debug("File " + argv[i] + " failed --> not included.");
      }
    }

    log.debug("writing " + nets.size() + " nets to file: " + outputfilename);

    try 
    {
      out = new PrintWriter(
          new OutputStreamWriter(
            new FileOutputStream(outputfilename)));
    }
    catch (IOException ev)
    {
      ev.printStackTrace();
    }

    out.print(xmlHandler.getEvoGrammarXML());
    out.println("");
    out.println("  <Generation Index=\"0\">");
    out.println("    <Population Name=\"NewBury\" Index=\"0\">");

    for(int index=0; index < nets.size(); index++)
    {
      Net n = (Net)nets.get(index);

      log.debug("writing net: " + index);
      out.println("      <EvoObject "  
        + " Index=\""      + index        + "\"" 
        + " OutPerf=\"0\"" 
        + " SysPerf=\"0\""
        + " Age=\"0\"" 
        + " PIN=\"0\""
        + " ParentsPIN=\"0\" >");

      out.println(n.toXML(6));
      out.println("      </EvoObject>");
    }
    out.println("    </Population>");
    out.println("  </Generation>");
    out.println("</Evolution>");
    out.close();

  }

  public static void main(String argv[])
  {
    new NewBury(argv);
  }
}
