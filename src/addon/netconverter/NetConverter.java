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


package addon.netconverter;


import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import util.io.EvoTaskXMLHandler;

import cholsey.LearningRuleClassLoader;
import cholsey.Net;
import cholsey.Neuron;
import cholsey.NeuronType;
import cholsey.Synapse;
import cholsey.SynapseList;
import cholsey.SynapseMode;
import cholsey.SynapseType;
import cholsey.Transferfunction;

/**
 * Class:       NetConverter 
 * Description: :-)
 */

public class NetConverter
{
  private NumberFormat numberFormat = NumberFormat.getInstance(Locale.UK);
  private final static int NEURON_DIST = 75;

  public static final int STYLE_STANDARD_C      = 0;
  public static final int STYLE_ICONNECT        = 1;
  public static final int STYLE_C_LOW_MEM       = 2;
  public static final int STYLE_ASCII           = 3;
  public static final int STYLE_YSOCNET         = 4;
  public static final int STYLE_GML             = 5;
  public static final int STYLE_GERMAN_TEAM     = 6;
  public static final int STYLE_NAMED_NET       = 7;
  public static final int STYLE_YARS_CONTROLLER = 8;

  private int         style;
  private Net         net;
  private PrintWriter out;
  private File        outFile;
  private int         neurons;
  private int         i_neurons;
  private int         o_neurons;
  private String      filename = null;

  private static LearningRuleClassLoader learningRuleClassLoader = new
    LearningRuleClassLoader();

  public NetConverter(int style)
  {
    this.style = style;
    numberFormat.setMinimumFractionDigits(2);
    numberFormat.setMaximumFractionDigits(2);
  }

  public boolean init(String filename)
  {
    try
    {
      outFile = new File(filename);
      out     = new PrintWriter(new FileOutputStream(outFile));
      this.filename = filename;
    } 
    catch (Exception ex)
    {
      ex.printStackTrace();      
      return false;
    }
    return true;
  }

  public boolean deinit()
  {
    try
    {
      out.flush();
      out.close();
    } 
    catch (Exception ex)
    {
      ex.printStackTrace();      
      return false;
    }
    return true;
  }

  public void setNet(Net net)
  {
    this.net = net;
    initNet();
  }
  
  public boolean loadNetFromFile(String file, int gen, int pop, int ind)
  {
    EvoTaskXMLHandler exmlh;

    exmlh = new EvoTaskXMLHandler();

    net = exmlh.readNetFromFile(file, gen, pop, ind);

    initNet();

    return net != null;
  }

  public void createCode()
  {
    switch (style)
    {
      case STYLE_STANDARD_C:
        createHeader();
        createStandardCDeclaration();
        createStandardCInit();
        createStandardCBody();
        createStandardCIOInterface();
        createStandardCMapfunctions();
        createStandardCMain();
        break;
      case STYLE_ICONNECT:
        createHeader();
        createIConnectHeader();
        createIConnectDeclaration();
        createIConnectTypeInfo();
        createIConnectInit();
        createIConnectExecute();
        createIConnectDone();
        break;
      case STYLE_C_LOW_MEM:
        createHeader();
        createCLowMemDeklaration();
        createCLowMemBody();
        createStandardCIOInterface();
        createStandardCMapfunctions();
        break;
      case STYLE_ASCII:
        createAsciiHeader();
        createAsciiBody();
        break;
      case STYLE_YSOCNET:
        createYSocNetHeader();
        createYSocNetBody();
        break;
      case STYLE_GML:
        createGMLHeader();
        createGMLBody();
        createGMLTail();
        break;
      case STYLE_NAMED_NET:
        createNamedNet();
        break;
      case STYLE_YARS_CONTROLLER:
        createYarsControllerCPPFile();
        out.flush();
        out.close();
        filename = filename.substring(0, filename.indexOf(".")) + ".h";
        outFile = new File(filename);
        try
        {
          out     = new PrintWriter(new FileOutputStream(outFile));
        } 
        catch (Exception ex)
        {
          ex.printStackTrace();      
          return;
        }
        createYarsControllerHeaderFile();
        break;
      case STYLE_GERMAN_TEAM:
        createGTCPPFile();
        out.flush();
        out.close();
        filename = filename.substring(0, filename.indexOf(".")) + ".h";
        outFile = new File(filename);
        try
        {
          out     = new PrintWriter(new FileOutputStream(outFile));
        } 
        catch (Exception ex)
        {
          ex.printStackTrace();      
          return;
        }
        createGTHeaderFile();
        break;
    }
  }

  private void initNet()
  {
    if (net != null)
    {
      neurons   = net.size() + 1;
      i_neurons = net.getInputNeurons().size();
      o_neurons = net.getOutputNeurons().size();
    }
    else
    {
      neurons   = 1;
      i_neurons = 0;
      o_neurons = 0;
    }
  }

  private void createAsciiHeader()
  {

  }

  private void createAsciiBody()
  {
    for(net.getInputNeurons().start(); 
        net.getInputNeurons().hasMore();
        net.getInputNeurons().next())
    {
      Neuron n = net.getInputNeurons().neuron();
      out.print("INPUT  Neuron " + (n.id()+1));
      out.println("       Bias   " + n.getBias());
    }
    for(net.getOutputNeurons().start(); 
        net.getOutputNeurons().hasMore();
        net.getOutputNeurons().next())
    {
      Neuron n = net.getOutputNeurons().neuron();
      out.print("OUTPUT Neuron " + (n.id()+1));
      out.println("       Bias   " + n.getBias());
      for(n.synapses().start();n.synapses().hasMore();n.synapses().next())
      {
        Synapse s = n.synapses().synapse();
        out.println("   Synapse " + (s.getSource().id()+1) + " -> " 
            + (s.getDestination().id()+1) + " : " + s.strength());
      }
    }
    for(net.getHiddenNeurons().start(); 
        net.getHiddenNeurons().hasMore();
        net.getHiddenNeurons().next())
    {
      Neuron n = net.getHiddenNeurons().neuron();
      out.print("HIDDEN Neuron " + (n.id()+1));
      out.println("       Bias   " + n.getBias());
      for(n.synapses().start();n.synapses().hasMore();n.synapses().next())
      {
        Synapse s = n.synapses().synapse();
        out.println("   Synapse " + (s.getSource().id()+1) + " -> " 
            + (s.getDestination().id()+1) + " : " + s.strength());
      }
    }



  }

  private void createIConnectHeader()
  {
    int i;

    out.println("//TRIGGER=T_AND;");
    for (i=0; i<i_neurons; i++)
    {
      out.println("input trigger InputNeuron" + i 
          + "(\"TYPEINFO\", \"TypeInfo\", \"DOUBLE[]\", \"TIME_DOMAIN\");");
    }
    for (i=0; i<o_neurons; i++)
    {
      out.println("output OutputNeuron" + i 
          + "(\"TYPEINFO\", \"TypeInfo\", \"DOUBLE[]\", \"TIME_DOMAIN\");");
    }
    out.println("");
  }

  private void createHeader()
  {
    out.println("//********************************************");
    out.println("//*              HEADER                      *");
    out.println("//*                                          *");
    out.println("//* This is a neural network implementation  *");
    out.println("//* autogenerated by NetConverter.           *");
    out.println("//*                                          *");
    out.println("//* Converted from file:                     *");
    out.println("//* Date:                                    *");
    out.println("//*                                          *");
    out.println("//* Neurons  :                               *");
    out.println("//* I-Neurons:                               *");
    out.println("//* O-Neurons:                               *");
    out.println("//* H-Neurons:                               *");
    out.println("//* Synapses :                               *");
    out.println("//*                                          *");
    out.println("//* This converter is written by Bjoern Mahn *");
    out.println("//* Sankt Augustin, Germany                  *");
    out.println("//* June 2003                                *");
    out.println("//*                                          *");
    out.println("//********************************************");
    out.println("");
  }

  private void createStandardCDeclaration()
  {
    out.println("//#include <math.h>");
    out.println("");
    out.println("//********************************************");
    out.println("//*            DECLARATION                   *");
    out.println("//*                                          *");
    out.println("//* nWeight  : weight-matrix                 *");
    out.println("//* nOutput1 : neuron output buffer 1        *");
    out.println("//* nOutput2 : neuron output buffer 2        *");
    out.println("//*                                          *");
    out.println("//********************************************");

    out.println("float nWeight[" + ((neurons*neurons)) + "];");
    out.println("float nOutput1[" + (neurons) + "];");
    out.println("float nOutput2[" + (neurons) + "];");
    out.println("int i;");
    out.println("int j;");
    out.println("int ni;");
    out.println("int nj;");
    out.println("");
  }

  private void createIConnectDeclaration()
  {
    out.println("//********************************************");
    out.println("//*            DECLARATION                   *");
    out.println("//*                                          *");
    out.println("//* nWeight  : weight-matrix                 *");
    out.println("//* nOutput1 : neuron output buffer 1        *");
    out.println("//* nOutput2 : neuron output buffer 2        *");
    out.println("//*                                          *");
    out.println("//********************************************");

    out.println("double nWeight[" + ((neurons*neurons)) + "];");
    out.println("double nOutput1[" + (neurons) + "];");
    out.println("double nOutput2[" + (neurons) + "];");
    out.println("int i;");
    out.println("int j;");
    out.println("");
  }

  private void createCLowMemDeklaration()
  {
    out.println("//#include <math.h>");
    out.println("");
    out.println("//********************************************");
    out.println("//*            DECLARATION                   *");
    out.println("//*                                          *");
    out.println("//* nOutput1 : neuron output buffer 1        *");
    out.println("//* nOutput2 : neuron output buffer 2        *");
    out.println("//*                                          *");
    out.println("//********************************************");

    out.println("float nOutput1[" + (neurons-1) + "];");
    out.println("float nOutput2[" + (neurons-1) + "];");
    out.println("");
  }

  private void createIConnectTypeInfo()
  {
    int i;

    out.println("//********************************************");
    out.println("//*            SET TYPE INFO                 *");
    out.println("//********************************************");

    out.println("SetTypeinfo()");
    out.println("{");
    for (i=0; i<o_neurons; i++)
    {
      out.println("  ti_copy(OutputNeuron" +i+ ", InputNeuron0);");
      out.println("  ti_setname(\"OutputNeuron" +i+ "\", OutputNeuron" +i+ ");");
    }
    out.println("}");
    out.println("");
  }

  private void createInitContent()
  {
    int i,j;
    double val;

    for (i=0; i<neurons; i++)
    {
      for (j=0; j<neurons; j++)
      {
        if (i == (neurons-1))
        {
          out.println("  nWeight[" + (i*neurons + j)+ "] = 0.0;  //to bias neuron");
        }
        else if (j == (neurons-1))
        {
          val = net.getNeuron(i).getBias();

          out.println("  nWeight[" + (i*neurons + j)+ "] = " + val 
              + ";  //bias to:" + i);;
        }
        else
        {
          Synapse s = net.getNeuron(i).getSynapse(net.getNeuron(j));
          if (s == null)
          {
            val = 0.0; 
          }
          else
          {
            val = s.strength();
          }
          out.println("  nWeight[" + (i*neurons + j)+ "] = " + val 
              + ";  //from:" + j + ", to:" + i);
        }
      }
    }
  }

  private void createInitHeader()
  {
    out.println("//********************************************");
    out.println("//*                INIT                      *");
    out.println("//*                                          *");
    out.println("//* Init the weight-matrix. This function    *");
    out.println("//* MUST be called manually at the beginning *");
    out.println("//*                                          *");
    out.println("//********************************************");
  }

  private void createStandardCInit()
  {
    createInitHeader();
    out.println("//init weights");
    out.println("void ninit()");
    out.println("{");
    createInitContent();
    out.println("}");
    out.println("");
  }

  private void createIConnectInit()
  {
    createInitHeader();
    out.println("//init weights");
    out.println("init");
    out.println("{");
    createInitContent();
    out.println("}");
    out.println("");
  }

  private void createIConnectExecute()
  {
    out.println("//********************************************");
    out.println("//*                 EXECUTE                  *");
    out.println("//********************************************");
    out.println("execute");
    out.println("{");
    createIConnectReadInput();
    createIConnectSetBias();
    createIConnectUpdate();
    createIConnectWriteOutput();
    out.println("}");
    out.println("");
  }

  private void createStandardCBody()
  {
    out.println("//********************************************");
    out.println("//*                 BODY                     *");
    out.println("//*                                          *");
    out.println("//* nGetOutput: return the netoutput         *");
    out.println("//*                                          *");
    out.println("//* nSetInput:  writes the input to the net  *");
    out.println("//*                                          *");
    out.println("//* nUpdate: calculates neurons activations  *");
    out.println("//*                                          *");
    out.println("//* nRun:runs ONE step of the neural network *");
    out.println("//*      process. BEFORE you call run, you   *");
    out.println("//*      should write the sensor values to   *");
    out.println("//*      the net with nSetInput()            *");
    out.println("//*      AFTER the run function finishes,    *");
    out.println("//*      you will get the output values      *");
    out.println("//*      from the net by using nGetOutput()  *");
    out.println("//*                                          *");
    out.println("//* If neccessary, you may edit these        *");
    out.println("//* function:                                *");
    out.println("//*                                          *");
    out.println("//* nTransf: transfer function, can be       *");
    out.println("//*          implemented manually            *");
    out.println("//*                                          *");
    out.println("//********************************************");
    createStandardCTransferFunction();
    createStandardCSetBias();
    createStandardCUpdate();
    createStandardCRun();
    out.println("");
  }

  private void createCLowMemBody()
  {
    out.println("//********************************************");
    out.println("//*                 BODY                     *");
    out.println("//*                                          *");
    out.println("//* gGetOutput: return the netoutput         *");
    out.println("//*                                          *");
    out.println("//* nSetInput:  writes the input to the net  *");
    out.println("//*                                          *");
    out.println("//* nRun:runs ONE step of the neural network *");
    out.println("//*      process. BEFORE you call run, you   *");
    out.println("//*      should write the sensor values to   *");
    out.println("//*      the net with nSetInput()            *");
    out.println("//*      AFTER the run function finishes,    *");
    out.println("//*      you will get the output values      *");
    out.println("//*      from the net by using nGetOutput()  *");
    out.println("//*                                          *");
    out.println("//********************************************");
    createCLowMemRun();
    out.println("");
  }

  private void createStandardCTransferFunction()
  {
    out.println("//transfer function");
    out.println("float nTransf(float val)");
    out.println("{");
    if (net.getTransferfunction() == Transferfunction.TANH)
    {
      out.println("  return tanh(val);");
    }
    if (net.getTransferfunction() == Transferfunction.SIGM)
    {
      out.println("  return (tanh(val)+1.0)/2.0; // using standard sigmoid");
    }
    out.println("}");
    out.println("");
  }

  private void createIConnectReadInput()
  {
    int i;

    out.println("  //read input");
    for (i=0; i<i_neurons; i++)
    {
      out.println("  nOutput1[" + i + "] = InputNeuron" + i + "[0];");
    }
    out.println("");
  }

  private void createIConnectSetBias()
  {
    out.println("  //set bias");
    out.println("  nOutput1[" + (neurons-1) + "] = 1.0;");
    out.println("");
  }

  private void createStandardCSetBias()
  {
    out.println("//setBias");
    out.println("void nSetBias()");
    out.println("{");
    out.println("  nOutput1[" + (neurons-1) + "] = 1.0;");
    out.println("}");
    out.println("");
  }

  private void createIConnectWriteOutput()
  {
    int i;

    out.println("  //write output");
    for (i=0; i<o_neurons; i++)
    {
      out.println("  OutputNeuron" + i + " << nOutput1["+ (i+i_neurons) + "];");

    }
    out.println("");
  }

  private void createUpdateContent()
  {
    out.println("  //calculate to buffer");
    out.println("  for (ni=0; ni<" + (neurons) + "; ni++)");
    out.println("  {");
    out.println("    nOutput2[ni] = 0.0;");
    out.println("    for (nj=0; nj<" + (neurons) + "; nj++)");
    out.println("    {");
    out.println("      nOutput2[ni] = nOutput2[ni] + nOutput1[nj] * nWeight[(ni*" 
                                + (neurons) + ")+nj];");
    out.println("    }");
    out.println("  }");
    out.println("");
  }

  private void createStandardCUpdateTransf()
  {
    out.println("  //calculate transfer function (only for non-inputs)");
    out.println("  for (ni=" + (i_neurons) + "; ni<" + (neurons-1) + "; ni++)");
    out.println("  {");
    out.println("    nOutput1[ni] = nTransf(nOutput2[ni]);");
    out.println("  }");
    out.println("");
  }

  private void createIConnectUpdateTransf()
  {
    out.println("  //calculate transfer function (only for non-inputs)");
    out.println("  for (i=" + (i_neurons) + "; i<" + (neurons-1) + "; i++)");
    out.println("  {");
    if (net.getTransferfunction() == Transferfunction.TANH)
    {
      out.println("    nOutput1[i] = tanh(nOutput2[i]);");
    } 
    else if (net.getTransferfunction() == Transferfunction.SIGM)
    {
      out.println("    nOutput1[i] = (tanh(nOutput2[i])+1.0)/2.0;");
    }
    out.println("  }");
    out.println("");
  }

  private void createCLowMemRun()
  {
    int i, j;
    Synapse syn;
    Neuron  neuron;
    NumberFormat nf;

    nf = NumberFormat.getInstance(Locale.UK);
    nf.setMinimumFractionDigits(1);
    nf.setMaximumFractionDigits(3);
    
    out.println("//run net");
    out.println("void nRun()");
    out.println("{");
    for (i=i_neurons; i<neurons-1; i++)
    {
      neuron = net.getNeuron(i);
      out.print("  nOutput2[" + i + "] = ");
      for (j=0; j<neurons-1; j++)
      {
        syn = neuron.getSynapse(net.getNeuron(j));
        if (syn != null)
        {
          out.print("(nOutput1[" + j + "] * " 
              + nf.format(syn.strength()) + ") + ");
        }
      }      
      out.println("(" + neuron.getBias() + ");");
    }

    out.println("");
    for (i=i_neurons; i<neurons-1; i++)
    {
      if (net.getTransferfunction() == Transferfunction.TANH)
      {
        out.println("  nOutput1[" + i + "] = tanh(nOutput2[" + i + "]);");
      }
      else if (net.getTransferfunction() == Transferfunction.SIGM)
      {
        out.println("  nOutput1[" + i + "] = (tanh(nOutput2[" + i + "])+1.0)/2.0;");
      }
      
    }
    out.println("}");
    out.println("");
  }

  
  private void createStandardCUpdate()
  {
    out.println("//update net");
    out.println("void nUpdate()");
    out.println("{");
    createUpdateContent();
    createStandardCUpdateTransf();
    out.println("}");    
    out.println("");
  }

  private void createIConnectUpdate()
  {
    out.println("  //update net");
    createUpdateContent();
    createIConnectUpdateTransf();
  }

  private void createStandardCRun()
  {
    out.println("//run net");
    out.println("void nRun()");
    out.println("{");
    out.println("  nSetBias();");
    out.println("  nUpdate();");
    out.println("}");
    out.println("");
  }

  private void createStandardCIOInterface()
  {
    out.println("void nSetInput(int nr, float value)");
    out.println("{");
    out.println("  nOutput1[nr] = value;");
    out.println("}");
    out.println("");
    out.println("float nGetOutput(int nr)");
    out.println("{");
    out.println("  return nOutput1[nr + " + i_neurons + "];");
    out.println("}");
    out.println("");
  }

  private void createStandardCMapfunctions()
  {
    out.println("float nLinearTanhMap(int minVal, int maxVal, int value)");
    out.println("{");
    out.println("  float diff;");
    out.println("");
    out.println("  diff = maxVal - minVal;");
    out.println("");
    out.println("  return (2.0/diff * value) + (1.0 - ((maxVal * 2.0)/diff));");
    out.println("}");
    out.println("");
    out.println("int nLinearTanhRemap(int minVal, int maxVal, float value)");
    out.println("{");
    out.println("  float diff;");
    out.println("");
    out.println("  diff = maxVal - minVal;");
    out.println("");
    out.println("  return (minVal + (((value + 1.0)/2.0) * diff));");
    out.println("}");
    out.println("");
  }

  private void createStandardCMain()
  {
    out.println("//********************************************");
    out.println("//*                 MAIN                     *");
    out.println("//********************************************");
    out.println("//int main (int argc, char **argv)");
    out.println("//{");
    out.println("//  return 0;"); 
    out.println("//}");
    out.println("");
  }

  private void createIConnectDone()
  {
    out.println("//********************************************");
    out.println("//*                 DONE                     *");
    out.println("//********************************************");
    out.println("done");
    out.println("{");
    out.println("  ;"); 
    out.println("}");
    out.println("");
  }


  private void test()
  {
    init("test.c");
    loadNetFromFile("./xml/nets/khepera/mrc_newgrammar.xml", 0, 0, 0);
    createCode();
    deinit();
    System.exit(0);
  }


  public static void main(String args[])
  {
    NetConverter nc;
    boolean error;

    System.out.println("NetConverter by Bjoern Mahn");
    System.out.println("June 2003.");
    System.out.println("");
    error = false;
    if (args.length != 3)
    {
      System.out.println("Too less parameters!");
      error = true;
    }
    else
    {
      if (args[0].equals("-i"))
      {
        nc = new NetConverter(STYLE_ICONNECT);
      }
      else if (args[0].equals("-c"))
      {
        nc = new NetConverter(STYLE_STANDARD_C);
      }
      else if (args[0].equals("-m"))
      {
        nc = new NetConverter(STYLE_C_LOW_MEM);
      }
      else if (args[0].equals("-gt"))
      {
        nc = new NetConverter(STYLE_GERMAN_TEAM);
      }
      else if (args[0].equals("-nn"))
      {
        nc = new NetConverter(STYLE_NAMED_NET);
      }
      else
      {
        System.out.println("option " + args[0] + " not known!");
        error = true;
        nc    = null;
      }
      if (!error)
      {
        if (!nc.init(args[2]))
        {
          System.out.println("targetfile initialization " + args[2] + " failed!");
          error = true;
        }
        else
        {
          if (!nc.loadNetFromFile((args[1]), 0, 0, 0))
          {
            System.out.println("could not find (gen=0, pop=0, ind=0) net or"
                + " netfile: " + args[1]);
            error = true;
          }
          else
          {
            System.out.print("Generating code from " + args[1] 
                + " to " + args[2] + "...");
            nc.createCode();
            System.out.println("...finished. Everything was ok. Thanks!");
            System.out.println("");
          }
          nc.deinit();
        }
      }
    }
    if (error)
    {
      System.out.println("usage:");
      System.out.println("  java AddOn.NetConverter.NetConverter -c source.xml dest.c");
      System.out.println("  java AddOn.NetConverter.NetConverter -i source.xml dest.c");
      System.out.println("  java AddOn.NetConverter.NetConverter -m source.xml dest.c");
      System.out.println("  java AddOn.NetConverter.NetConverter -gt source.xml dest.c");
      System.out.println("");
    }

    //(new NetConverter(NetConverter.STYLE_STANDARD_C)).test();
  }


  private void createYSocNetHeader()
  {
    // later
  }

  private void createYSocNetBody()
  {
    String line = null;
    double[][] weight = new double[net.size()][net.size()];

    for(net.neurons().start(); net.neurons().hasMore(); net.neurons().next())
    {
      Neuron neuron = net.neurons().neuron();
      if(neuron.synapses() != null)
      {
        line = new String();
        for(neuron.synapses().start(); neuron.synapses().hasMore();
            neuron.synapses().next())
        {
          Synapse synapse = neuron.synapses().synapse();
          weight[synapse.getSource().id()]
                      [synapse.getDestination().id()] = synapse.strength();
        }
      }
    }

    for(int i=0; i < net.size(); i++)
    {
      for(int j=0; j < net.size(); j++)
      {
        out.print(weight[i][j] + " ");
      }
      out.println();
    }
  }

  private void createGMLHeader()
  {
    out.println("graph [");
    out.println("  directed 1");
  }

  private void createGMLBody()
  {
    
    int i = 0;
    for(net.getInputNeurons().start(); 
        net.getInputNeurons().hasMore(); 
        net.getInputNeurons().next())
    {
      Neuron neuron = net.getInputNeurons().neuron();
      out.println("  node [");
      out.println("    id "    + neuron.id());
      out.println("    label \"" + neuron.id() + "\"");
      out.println("    graphics [");
      out.println("      type  \"ellipse\"");
      out.println("      x " + (100 + i * NEURON_DIST));
      out.println("      y 100");
      out.println("      w 30.0");
      out.println("      h 30.0");
      out.println("      fill \"#FF0000\"");
      out.println("    ] ");
      out.println("  ]");
      i++;
    }
    i=0;
    for(net.getOutputNeurons().start(); 
        net.getOutputNeurons().hasMore(); 
        net.getOutputNeurons().next())
    {
      Neuron neuron = net.getOutputNeurons().neuron();
      out.println("  node [");
      out.println("    id "    + neuron.id());
      out.println("    label \"" + neuron.id() + "\"");
      out.println("    graphics [");
      out.println("      x " + (100 + i * NEURON_DIST));
      out.println("      y 300");
      out.println("      w 30.0");
      out.println("      h 30.0");
      out.println("      type  \"ellipse\"");
      out.println("      fill \"#00FF00\"");
      out.println("    ] ");
      out.println("  ]");
      i++;
    }
    i=0;
    for(net.getHiddenNeurons().start(); 
        net.getHiddenNeurons().hasMore(); 
        net.getHiddenNeurons().next())
    {
      Neuron neuron = net.getHiddenNeurons().neuron();
      out.println("  node [");
      out.println("    id "    + neuron.id());
      out.println("    label \"" + neuron.id() + "\"");
      out.println("    graphics [");
      out.println("      x " + (100 + i * NEURON_DIST));
      out.println("      y 200");
      out.println("      w 30.0");
      out.println("      h 30.0");
      out.println("      type  \"ellipse\"");
      out.println("      fill \"#0000BB\"");
      out.println("    ] ");
      out.println("  ]");
      i++;
    }

    for(net.neurons().start(); net.neurons().hasMore(); net.neurons().next())
    {
      if(net.neurons().neuron().synapses() != null)
      {
        Neuron neuron = net.neurons().neuron();
        for(neuron.synapses().start(); neuron.synapses().hasMore();
            neuron.synapses().next())
        {
          Synapse synapse = neuron.synapses().synapse();
          out.println("  edge [");
          out.println("    source " + synapse.getSource().id());
          out.println("    target " + synapse.getDestination().id());
//          out.println("    label \"" + numberFormat.format(synapse.strength()) 
//                                     + "\"");
          if(net.getSynapseMode() == SynapseMode.DYNAMIC)
          {
            if(synapse.type() == SynapseType.INHIBITORY)
            {
              out.println("    graphics [");
              out.println("      targetArrow \"delta\"");
              out.println("      width 4");
              out.println("      fill  \"#0000ff\"");
              out.println("    ] ");
              out.println("    LabelGraphics [");
              //out.println("      text  \"-\"");
              out.println("      fontSize  12");
              out.println("      fontName  \"Dialog\"");
              out.println("      model \"six_pos\"");
              out.println("      position  \"tail\"");
              out.println("    ]");
            }
            else
            {
              out.println("    graphics [");
              out.println("      targetArrow \"delta\"");
              out.println("      width 4");
              out.println("      fill  \"#ff0000\"");
              out.println("    ] ");
              out.println("    LabelGraphics [");
              //out.println("      text  \"-\"");
              out.println("      fontSize  12");
              out.println("      fontName  \"Dialog\"");
              out.println("      model \"six_pos\"");
              out.println("      position  \"tail\"");
              out.println("    ]");
            }
          }
          else
          {
            out.println("    graphics [");
            out.println("      targetArrow \"delta\"");
            out.println("      width 4");
            if(synapse.strength() < 0)
            {
              out.println("      fill  \"#0000ff\"");
            }
            else
            {
              out.println("      fill  \"#00ff00\"");
            }
            out.println("    ] ");


            out.println("    LabelGraphics [");
            out.println("      text  \"" + numberFormat.format(synapse.strength())
                + "\"");
            out.println("    ] ");
          }
          out.println("  ]");
        }
      }
    }
  }

  private void createGMLTail()
  {
    out.println("]");
  }

  private void createGTCPPFile()
  {
    out.println("/**");
    out.println(" * @file GermanTeamCode.cpp");
    out.println(" * ");
    out.println(" * Implementation of class GermanTeamCode");
    out.println(" *");
    out.println(" * @author Keyan Zahedi - automatically created by ISEE ");
    out.println(" * Date: " +  (new Date(System.currentTimeMillis()).toString()));
    out.println(" */");
    out.println("");
    out.println("#include \"GermanTeamCode.h\"");
    out.println("#include \"Tools/Player.h\"");
    out.println("#include \"Tools/RobotConfiguration.h\"");
    out.println("#include \"Tools/Streams/InStreams.h\"");
    out.println("#include \"Tools/Actorics/RobotDimensions.h\"");
    out.println("#include \"Tools/Debugging/Debugging.h\"");
    out.println("#include \"Tools/Debugging/DebugDrawings.h\"");
    out.println("#include \"Tools/Debugging/GenericDebugData.h\"");
    out.println("#include \"Platform/SystemCall.h\"");
    out.println("#include \"Tools/Math/Geometry.h\"");
    out.println("#include \"Tools/FieldDimensions.h\"");
    out.println("#include <string.h>");
    out.println("");
    out.println("");
    out.println("#define M_PI  3.14159265358979323846  // taken from math.h");
    out.println("");
    out.println("");
    out.println("");
    out.println("GermanTeamCode::GermanTeamCode(WalkingEngineInterfaces& interfaces)");
    out.println(": WalkingEngine(interfaces)");
    out.println("{");
    out.println("");
    out.println("");
    out.println("  steps    = 0;");
    out.println("  activity = new double[" + net.size() + "];");
    out.println("  output   = new double[" + net.size() + "];");
    for(int i=0; i < net.size(); i++)
    {
      out.println("  activity[" + i + "] = 0;");
    }
    for(int i=0; i < net.size(); i++)
    {
      out.println("  output[" + i + "] = 0;");
    }
    out.println("");
    out.println("");
    out.println("}");
    out.println("");
    out.println("");
    out.println("");
    out.println("GermanTeamCode::~GermanTeamCode()");
    out.println("{");
    out.println("}");
    out.println("");
    out.println("");
    out.println("");
    out.println("");
    out.println(" // motor mapping, neuron output in, motor command out");
    out.println("double GermanTeamCode::map(double value, double outStartVal_, double outEndVal_)");
    out.println("{");
    out.println("  double inStartVal_ = -1;");
    out.println("  double inEndVal_   = +1;");
    out.println("  double a_ = (outStartVal_-outEndVal_)/(inStartVal_-inEndVal_);");
    out.println("  double b_ = outStartVal_- ( (inStartVal_*(outStartVal_-outEndVal_))/(inStartVal_-inEndVal_) );");
    out.println("  return ( (a_*value) + b_);");
    out.println("}");
    out.println("");
    out.println("");
    out.println("");
    out.println("bool GermanTeamCode::handleMessage(InMessage& message)");
    out.println("{");
    out.println("  bool handled = false;");
    out.println("  GenericDebugData d;");
    out.println("");
    out.println("  switch(message.getMessageID())");
    out.println("  {");
    out.println("    case idYetAnotherInvKinParams:");
    out.println("      message.bin >> d;");
    out.println("      if(d.id == GenericDebugData::yetAnotherInvKinParams)");
    out.println("      {   ");
    out.println("        OUTPUT(idText,text,\"generic debug message (GermanTeamCode) handled by module GermanTeamCode\");");
    out.println("      }");
    out.println("      handled = true;");
    out.println("      break;");
    out.println("    case idYetAnotherInvKinPaws:");
    out.println("      message.bin >> d;");
    out.println("      if(d.id == GenericDebugData::yetAnotherInvKinPaws)");
    out.println("      {   ");
    out.println("        OUTPUT(idText,text,\"generic debug message (GermanTeamCode) handled by module GermanTeamCode\");");
    out.println("      }");
    out.println("      handled = true;");
    out.println("      break;");
    out.println("  }");
    out.println("  return handled;");
    out.println("}");
    out.println("");
    out.println("");
    out.println("");
    out.println("bool GermanTeamCode::executeParameterized(JointData& jointData,");
    out.println("    const WalkRequest& walkRequest)");
    out.println("{  ");

    for(int i=0; i < net.size(); i ++)
    {
      out.println("  activity[" + i + "] =  " +
          net.neurons().neuron(i).getBias() + "; // bias");
      for(int j=0; j < net.size(); j++)
      {
        Synapse s = net.neurons().neuron(i).getSynapse(
              net.neurons().neuron(j));
        if(s != null)
        {
          out.println("  activity[" + i + "] += output[" + j + 
              "] * " + s.strength() + "; // " + j + " -> " + i);
        }

      }
    }
    for(int i=0; i < net.size(); i ++)
    {
      out.println("  output[" + i + "] = tanh(activity[" + i + "]);");
    }


    out.println("  jointData.data[JointData::legFR1] = "
        + "toMicroRad(map(output[0], -2.007, 2.007));");
    out.println("  jointData.data[JointData::legFR2] = "
        + "toMicroRad(map(output[1], -0.1745, 0.1745));");
    out.println("  jointData.data[JointData::legFR3] = "
        + "toMicroRad(map(output[2], -0.436, 0.436));");

    out.println("  jointData.data[JointData::legHR1] = "
        + "toMicroRad(map(output[3], 2.007, -2.007));");
    out.println("  jointData.data[JointData::legHR2] = "
        + "toMicroRad(map(output[4], -0.1745, 0.1745));");
    out.println("  jointData.data[JointData::legHR3] = "
        + "toMicroRad(map(output[5], -0.436, +0.436));");

   
    out.println("  jointData.data[JointData::legHL1] = "
        + "toMicroRad(map(output[6], 2.007, -2.007));");
    out.println("  jointData.data[JointData::legHL2] = "
        + "toMicroRad(map(output[7], 0.1745, -0.1745));");
    out.println("  jointData.data[JointData::legHL3] = "
        + "toMicroRad(map(output[8], -0.436, +0.436));");


    out.println("  jointData.data[JointData::legFL1] = "
        + "toMicroRad(map(output[9], -2.007, 2.007));");
    out.println("  jointData.data[JointData::legFL2] = "
        + "toMicroRad(map(output[10], 0.1745, -0.1745));");
    out.println("  jointData.data[JointData::legFL3] = "
        + "toMicroRad(map(output[11], -0.436, +0.436));");


    out.println("  jointData.data[JointData::neckTilt] = jointDataInvalidValue;");
    out.println("  jointData.data[JointData::headPan] = jointDataInvalidValue;");
    out.println("  jointData.data[JointData::headTilt] = jointDataInvalidValue;");

    out.println("  return true;");
    out.println("}");
    out.println("");
    out.println("");
    out.println("");
  }

  private void createGTHeaderFile()
  {
    out.println("/**");
    out.println("* @file GermanTeamCode.h");
    out.println("* ");
    out.println("*");
    out.println("* @author Keyan Zahedi - automatically generated by ISEE");
    out.println("*/");
    out.println("");
    out.println("#ifndef __GermanTeamCode_h_");
    out.println("#define __GermanTeamCode_h_");
    out.println("");
    out.println("#include \"WalkingEngine.h\"");
    out.println("#include \"Tools/Actorics/Kinematics.h\"");
    out.println("#include \"Tools/Evolution/Individual.h\"");
    out.println("");
    out.println("#include <string.h>");
    out.println("");
    out.println("");
    out.println("");
    out.println("");
    out.println("/**");
    out.println("* @class GermanTeamCode");
    out.println("*");
    out.println("*");
    out.println("* @author Keyan Zahedi");
    out.println("*/");
    out.println("class GermanTeamCode : public WalkingEngine");
    out.println("{");
    out.println("public:");
    out.println("/**");
    out.println("* Constructor");
    out.println("* @param interfaces The paramters of the WalkingEngine module.");
    out.println("  */");
    out.println("  GermanTeamCode(WalkingEngineInterfaces& interfaces);");
    out.println("  ");
    out.println("  /**");
    out.println("  * Destructor");
    out.println("  */");
    out.println("  ~GermanTeamCode();");
    out.println("  ");
    out.println("  /** Executes the engine */");
    out.println("  virtual bool executeParameterized(JointData& jointData, const WalkRequest& walkRequest);");
    out.println("  ");
    out.println("  /** ");
    out.println("  * Called from a MessageQueue to distribute messages ");
    out.println("  * @param message The message that can be read.");
    out.println("  * @return true if the message was read (handled).");
    out.println("  */");
    out.println("  virtual bool handleMessage(InMessage& message);");
    out.println("  double map(double value, double outStartVal_, double outEndVal_);");
    out.println("  ");
    out.println("");
    out.println("");
    out.println("private:");
    out.println("");
    out.println("");
    out.println("  int steps;");
    out.println("  double *activity;");
    out.println("  double *output;");
    out.println("");
    out.println("");
    out.println("");
    out.println("};");
    out.println("");
    out.println("");
    out.println("");
    out.println("#endif// __GermanTeamCode_h_");
  }

  private void createYarsControllerCPPFile()
  {
    String name = filename.substring(0, filename.indexOf("."));
    name = name.replaceFirst(".*/","");
    out.println("#include <" + name + ".h>");
    out.println("");
    out.println("using namespace std;");
    out.println("");
    out.println("void " + name + "::init()");
    out.println("{");
    out.println("  activity = new double[" + net.size() + "];");
    out.println("  output   = new double[" + net.size() + "];");
    for(int i=0; i < net.size(); i++)
    {
      out.println("  activity[" + i + "] = 0;");
    }
    for(int i=0; i < net.size(); i++)
    {
      out.println("  output[" + i + "] = 0;");
    }
    out.println("}");
    out.println("");
    out.println("void " + name + "::deInit()");
    out.println("{");
    out.println("  delete []activity;");
    out.println("  delete []output;");
    out.println("}");
    out.println("");
    out.println("void " + name + "::update()");
    out.println("{");
    out.println("}");
    out.println("");
    out.println("");
    out.println("// the class factories");
    out.println("extern \"C\" RobotController* create() {");
    out.println("  RobotController *controller = new " + name);
    out.println("  controller->init();");
    out.println("  return controller;");
    out.println("}");

    out.println("");
    out.println("extern \"C\" void destroy(RobotController* controller) {");
    out.println("  delete controller;");
    out.println("}");
  }

  private void createYarsControllerHeaderFile()
  {
    String name = filename.substring(0, filename.indexOf("."));
    name = name.replaceFirst(".*/","");
    out.println("// C++ Interface: " + name);
    out.println("//");
    out.println("// Author: Created by NetConverter");
    out.println("//");
    out.println("// Date: " +  (new Date(System.currentTimeMillis()).toString()));
    out.println("//");
    out.println("// Copyright: See COPYING file that comes with this distribution");
    out.println("//");
    out.println("//");
    out.println("");
    out.println("#ifndef __" + name.toUpperCase() +"_H__");
    out.println("#define __" + name.toUpperCase() +"_H__");
    out.println("");
    out.println("#include <cmath>");
    out.println("#include <string> ");
    out.println("#include <iostream>");
    out.println("#include <vector>");
    out.println("#include <math.h>");
    out.println("#include <stdio.h>");
    out.println("");
    out.println("#include <base/RobotController.hpp>");
    out.println("");
    out.println("using namespace std;");
    out.println("");
    out.println("class " + name + " : public RobotController");
    out.println("{");
    out.println(" ");
    out.println("  public:");
    out.println("    virtual void init();");
    out.println("    virtual void deInit();");
    out.println("    virtual void update();");
    out.println("");
    out.println("");
    out.println(" private:");
    out.println("");
    out.println("  double *activity;");
    out.println("  double *output;");
    out.println("};");
    out.println("");
    out.println("#endif // __BRAITENBERG_H__");
  }


  private void createNamedNet()
  {
    String name = filename.substring(0, filename.indexOf("."));
    name = name.replaceFirst(".*/","");
    out.println("package brightwell.nets;");
    out.println("");
    out.println("import cholsey.*;");
    out.println("");
    out.println("import brightwell.analyser.NamedNet;");
    out.println("");
    out.println("public class " + name + " extends NamedNet");
    out.println("{");
    out.println("");
    out.println("  private static LearningRuleClassLoader learningRuleClassLoader = new");
    out.println("    LearningRuleClassLoader();");
    out.println("");
    out.println("  public String getNetName()");
    out.println("  {");
    out.println("    return \"" + name +"\";");
    out.println("  }");
    out.println("  ");
    out.println("  public " + name + "()");
    out.println("  {");
    out.println("");
    out.println("    Neuron n = null;");
    out.println("    Synapse s = null;");
    switch(net.getTransferfunction().mode())
    {
      case Transferfunction.USE_TANH:
        out.println("    setTransferfunction(Transferfunction.TANH);");
        break;
      case Transferfunction.USE_SIGM:
        out.println("    setTransferfunction(Transferfunction.SIGM);");
        break;
    }
    switch(net.getSynapseMode().mode())
    {
      case SynapseMode.SYNAPSE_MODE_DYNAMIC:
        out.println("    setSynapseMode(SynapseMode.DYNAMIC);");
        out.println("    learningRuleClassLoader.setSelectedLearningRule(" 
            + learningRuleClassLoader.getSelectedIndex() + ");");
        break;
      case SynapseMode.SYNAPSE_MODE_CONVENTIONAL:
        out.println("    setSynapseMode(SynapseMode.CONVENTIONAL);");
        break;
    }
    for(net.neurons().start(); net.neurons().hasMore(); net.neurons().next())
    {
      Neuron n = net.neurons().neuron();
      switch(n.getNeuronType().type())
      {
        case NeuronType.NEURON_TYPE_INPUT:
          out.println("    n = addNeuron(NeuronType.INPUT);");
          break;
        case NeuronType.NEURON_TYPE_OUTPUT:
          out.println("    n = addNeuron(NeuronType.OUTPUT);");
          break;
        case NeuronType.NEURON_TYPE_HIDDEN:
          out.println("    n = addNeuron(NeuronType.HIDDEN);");
          break;
      }
      out.println("    n.setBias(" + n.getBias() + ");");
      out.println("    n.setAlpha(" + n.getAlpha() + ");");
      out.println("    n.setBeta(" + n.getBeta() + ");");
      out.println("    n.setGamma(" + n.getGamma() + ");");
      out.println("    n.setDelta(" + n.getDelta() + ");");
      out.println("    n.setKappa(" + n.getKappa() + ");");
      out.println("    n.setTransmitterLevel(" + n.getTransmitterLevel() 
          + ");");
      out.println("    n.setReceptorLevel(" + n.getReceptorLevel() 
          + ");");
    }
    for(net.neurons().start(); net.neurons().hasMore(); net.neurons().next())
    {
      Neuron n = net.neurons().neuron();
      SynapseList synapses = n.synapses();
      if(synapses != null)
      {
        for(synapses.start(); synapses.hasMore(); synapses.next())
        {
          Synapse s = synapses.synapse();
          int sourceIndex = s.getSource().id();
          int destinationIndex = s.getDestination().id();
          out.println("    s = addSynapse("); 
          out.println("         neurons().neuron(" + sourceIndex + "),");
          out.println("         neurons().neuron(" + destinationIndex + "),");
          out.println("         " + s.strength() + ");");
          switch(s.type().type())
          {
            case SynapseType.SYNAPSE_TYPE_EXCITATORY:
              out.println("    s.setSynapseType(SynapseType.EXCITATORY);");
            break;
            case SynapseType.SYNAPSE_TYPE_INHIBITORY:
              out.println("    s.setSynapseType(SynapseType.INHIBITORY);");
            break;
          }
        }
      }
    }
    out.println("    ");
    out.println("  }");
    out.println("}");
  }

}





//package addon.netconverter;
//
//import java.text.*;
//import java.util.*;
//import java.lang.String;
//
//import java.io.*;
//
//import cholsey.Net;
//import cholsey.Neuron;
//import cholsey.NeuronType;
//import cholsey.Synapse;
//import cholsey.SynapseList;
//import cholsey.SynapseMode;
//import cholsey.SynapseType;
//import cholsey.Transferfunction;
//import indy.io.*;
//import cholsey.*;
//import indy.util.*;
//
//import org.apache.log4j.*;
//
///**
// * Class:       NetConverter 
// * Description: :-)
// *
// *             
// * @author Keyan Zahedi
// */
//public class NetConverter
//{
//  private static Logger log = 
//    Logger.getLogger("addons.netconverter.NetConverter");
//
//  private Vector exporter = new Vector();
//  private String[] names = null;
//
//  public NetConverter(int style)
//  {
//    ArrayList objects = null;
//
//    GenericClassLoader gcl = new GenericClassLoader(
//        "hinton" + File.separator + "exporter",
//        "hinton" + File.separator + "exporter");
//
//    objects = gcl.getObjects();
//    names = new String[objects.size()];
//
//    log.info("number of loaded exporter: " + objects.size());
//
//    try
//    {
//      for(int i=0; i < objects.size(); i++)
//      {
//        Class c = (Class)(objects.get(i));
//        NeuralNetworkExporter n = (NeuralNetworkExporter)c.newInstance();
//        log.info("added: " + n.getClass());
//        exporter.add(n);
//        names[i] = n.getName();
//      }
//    }
//    catch(Exception e)
//    {
//      e.printStackTrace();
//    }
//
//
//  }
//
//
//}
//
