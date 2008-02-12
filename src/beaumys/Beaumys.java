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

package beaumys;

import cholsey.Net;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import util.io.EvoTaskXMLHandler;
import util.misc.IseeLogger;

public class Beaumys 
{

  private static Logger log = IseeLogger.getLogger(Beaumys.class);
  private EvoTaskXMLHandler xmlHandler = new EvoTaskXMLHandler();
  private PrintWriter  out;

  public Beaumys(String argv[])
  {
    String inputFile = null;
    String outputFile = null;
    int    generations = -1;
    int    individuals = -1;

    if(argv.length != 8)
    {
      System.out.println("Usage: java -jar beaumy.jar -f <evotaskfile.xml>"
          +" -g <#generation> -i <#individuals in each generation>"
          +" -o <outputfile>");
      System.exit(0);
    }

    try
    {
      for(int i=0; i < argv.length; i++)
      {
        switch(argv[i].charAt(1))
        {
          case 'f' : 
            inputFile = argv[i+1];
            i++;
            break;
          case 'o' : 
            outputFile = argv[i+1];
            i++;
            break;
          case 'g' : 
            generations = Integer.parseInt(argv[i+1]);
            i++;
            break;
          case 'i' : 
            individuals = Integer.parseInt(argv[i+1]);
            i++;
            break;
        }
      }
    }
    catch(NumberFormatException nfe)
    {
      System.out.println("Usage: java -jar beaumy.jar -f <evotaskfile.xml>"
          +" -g <#generation> -i <#individuals in each generation>");
      System.out.println("Invalid number given."
          + " Please check the given parameters");
      System.exit(0);

    }

    if(inputFile == null || outputFile == null || generations == -1 || individuals == -1)
    {
      System.out.println("Usage: java -jar beaumy.jar -f <evotaskfile.xml>"
          +" -g <#generation> -i <#individuals in each generation>");
      System.out.println("Please check the given parameters");
      System.exit(0);

    }
    log.info("Parameters given: ");
    log.info("Filename      : " + inputFile);
    log.info("#Generations  : " + generations);
    log.info("#Individuals  : " + individuals);
    int lastGeneration = getLastGenerationIndexFromFiles(inputFile);
    log.debug("Last generation found: " + lastGeneration);
    int firstGeneration = Math.max(lastGeneration - generations, 0);
    log.debug("First generation will be: " + firstGeneration);

    extractNetworks(inputFile,
        outputFile,
        firstGeneration,
        lastGeneration,
        individuals);
  }

  private void extractNetworks(String inputFile, 
      String outputFile,
      int firstGeneration,
      int lastGeneration,
      int individuals)
  {
    PrintWriter out;
    try 
    {
      out = new PrintWriter(
          new OutputStreamWriter(
            new FileOutputStream(outputFile)));
    }
    catch (IOException ev)
    {
      ev.printStackTrace();
      return;
    }

    out.print(xmlHandler.getEvoGrammarXML());
    out.println("");
    out.println("  <Generation Index=\"0\">");
    out.println("    <Population Name=\"Beaumys\" Index=\"0\">");


    int index = 0;
    for(int i=firstGeneration; i <= lastGeneration; i++)
    {
      int netsPerGeneration = 0;
      for(int j=0; j < individuals; j++)
      {
        log.debug("reading generation " + i + " net " + j);
        Net net = null;
        try
        {
          net = xmlHandler.readNetFromFile(inputFile,i,0,j);
          if(net == null)
          {
            break;
          }
          netsPerGeneration++;
          out.println("      <EvoObject "  
              + " Index=\""      + index        + "\"" 
              + " OutPerf=\"0\"" 
              + " SysPerf=\"0\""
              + " Age=\"0\"" 
              + " PIN=\"0\""
              + " ParentsPIN=\"0\" >");

          out.println(net.toXML(6));
          out.println("      </EvoObject>");
          index++;
        }
        catch(Exception spe)
        {
          // nothing
        }
      }
      log.info("In generation " + i + " found "
          + netsPerGeneration + " nets");
    }
    out.println("    </Population>");
    out.println("  </Generation>");
    out.println("</Evolution>");
    out.close();

    log.info("Wrote " + index + "nets to file");

  }



  private int getLastGenerationIndexFromFiles(String inputFile)
  {
    String thePattern = "(.+)\"(\\d+)\"(.+)";
    Pattern pattern = Pattern.compile(thePattern, Pattern.CASE_INSENSITIVE);
    int lastIndex = -1;
    try 
    {
      BufferedReader in = new BufferedReader(new
          FileReader(new File(inputFile)));
      String line = in.readLine();
      while(line != null)
      {
        if(line.indexOf("Generation") != -1)
        {
          log.debug("found string \"" + line + "\"");
          //line.replaceAll(,"\\1");
          Matcher matcher = pattern.matcher(line);
          String replaced = matcher.replaceAll("$2");
          log.debug("replaced to \"" + replaced + "\"");
          try
          {
            lastIndex = Integer.parseInt(replaced);
          }
          catch(NumberFormatException nfe)
          {
            // nothing
          }
        }
        line = in.readLine();
      }
      in.close();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    return lastIndex;
  }

  public static void main(String argv[])
  {
    new Beaumys(argv);
  }
}
