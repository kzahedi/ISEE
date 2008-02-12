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


package brightwell.tools;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

import org.jfree.ui.RefineryUtilities;

import util.misc.Edge;
import util.misc.Graph;
import util.misc.Node;

import brightwell.analyser.Tool;
import brightwell.gui.drawingplane.Chart3D;
import brightwell.gui.drawingplane.DrawingPlane;


public class Centrality extends Tool
{  
  private static String FILE_OUTPUT = "to file";

  private static String DEGREE_CENTRALITY_STRING      = "degree";
  private static String INDEGREE_CENTRALITY_STRING    = "in-degree";
  private static String OUTDEGREE_CENTRALITY_STRING   = "out-degree";
  private static String CLOSENESS_CENTRALITY_STRING   = "closeness";
  private static String BETWEENNESS_CENTRALITY_STRING = "betweenness";
  private static String AVERAGE_CENTRALITY_STRING     = "average";
 
  // **************************************************************************
  // predefined variables, that are accessable by default. no declaraion or
  // initialisation needed. they are just there.
  // **************************************************************************

  // **************************************************************************
  // boolean doAnalysis 
  //
  // is true as long as the tool should be running. is set false, when the user
  // presses the stop button
  // **************************************************************************

  // **************************************************************************
  // Net net 
  //
  // contains the neural net loaded or constructed by the user, or is null.
  // should not be accessed, when needsNet() returns false (see below)
  // **************************************************************************

  // **************************************************************************
  // int convergenceIterations 
  //
  // the number of convergence iterations, that the user has set for the
  // analysis, i.e. attractor map, feigenbaum
  // **************************************************************************
  
  // **************************************************************************   
  // int drawIterations 
  // 
  // the number of draw iteration, the user wants to be displayed, i.e.
  // feigenbaum
  // **************************************************************************   

  // **************************************************************************   
  // double xStart 
  //
  // the start value of the x-range, also the start value for the drawing
  // coordiante system, used by the drawing plane
  //
  // the drawing coordinate system is defined by:
  //  (xStart, yEnd)   +------+ (xEnd, yEnd)
  //                   |      |
  //                   |      |
  //  (xStart, yStart) +------+ (xEnd, yStart)
  //
  // **************************************************************************   

  // **************************************************************************   
  // double xEnd   
  //
  // the end value of the x-range, also the end value for the drawing
  // coordiante system, used by the drawing plane
  //
  // the drawing coordinate system is defined by:
  //  (xStart, yEnd)   +------+ (xEnd, yEnd)
  //                   |      |
  //                   |      |
  //  (xStart, yStart) +------+ (xEnd, yStart)
  //
  // **************************************************************************   
   

  // **************************************************************************   
  // double yStart 
  //
  // the start value of the y-range, also the start value for the drawing
  // coordiante system, used by the drawing plane
  //
  // the drawing coordinate system is defined by:
  //  (xStart, yEnd)   +------+ (xEnd, yEnd)
  //                   |      |
  //                   |      |
  //  (xStart, yStart) +------+ (xEnd, yStart)
  //
  // **************************************************************************   

  // **************************************************************************   
  // double yEnd   
  //
  // the end value of the y-range, also the end value for the drawing
  // coordiante system, used by the drawing plane
  //
  // the drawing coordinate system is defined by:
  //  (xStart, yEnd)   +------+ (xEnd, yEnd)
  //                   |      |
  //                   |      |
  //  (xStart, yStart) +------+ (xEnd, yStart)
  // **************************************************************************   

  // **************************************************************************  
  // double stepsX 
  //
  // the displaying width of the analysing panel, corresponding the number of
  // steps in x-direction, in pixels.
  //
  //     +-------------------+ ---
  //     |                   |  |
  //     |                   |  stepsY = height
  //     |                   |  |
  //     +-------------------+ ---
  //
  //     |-- stepsX = width--|
  // **************************************************************************  
   
  // **************************************************************************  
  // double stepsY 
  //
  // the displaying width of the analysing panel, corresponding the number of
  // steps in y-direction, in pixels.
  //
  //     +-------------------+ ---
  //     |                   |  |
  //     |                   |  stepsY = height
  //     |                   |  |
  //     +-------------------+ ---
  //
  //     |-- stepsX = width--|
  // **************************************************************************  

  // **************************************************************************
  // double dx 
  //
  // the step width in x direction. dx = (xEnd - yStart) / stepsX
  // **************************************************************************

  // **************************************************************************
  // double dy
  //
  // the step width in y direction. dy = (yEnd - yStart) / stepsY
  // **************************************************************************
    
  // **************************************************************************
  // end of predefined variables
  // **************************************************************************

  // reserve name for the drawinPlane that will be used to display data
  private DrawingPlane dp = null;

  // **************************************************************************
  // the following functions are required by Brightwell. These are the minimum
  // set of functions to define a tool within Brightwell
  // **************************************************************************

  /**
   * This function indicates Brightwell, if this tool needs a neural net. If a
   * neural net is needed, return true. If no neural net is needed return false.
   * Brightwell will ask for this value, when the run button is pressed. In case
   * a tool need a neural net, Brightwell will check if a net is selected or
   * created, and only proceeds, if so.
   * @return   true/false
   */
  public boolean needsNet()
  {
    return true;
  }

  /**
   * This function returns the name displayed in the tabbed pane. 
   */
  public String getToolName()
  {
    return "Centrality Tool";
  }

  /**
   * This function returns a description of the tool which is displayed, when
   * the mouse is placed over a tool name for some seconds.
   */
  public String getToolDescription()
  {
    return "Calculate Neuron Centralities";
  }

  /**
   * Initialising the Tool.
   *
   * <ul>
   *  <li> setup initial variables, if needed </li>
   *  <li> setup input panel if needed</li>
   *  <li> ... </li>
   * </ul>
   *
   * This function is called only onve, at the startup of Brightwell.
   *
   */
  public void init()
  {

    // all names are the names, which are displayed, and which reference to the
    // values of the selection field.
    // f.e. addString("your name","") displays: 
    // your name: ________ 
    // on the screen. if you want to access the string entered, you will have to
    // ask with
    // String string = getString("your name"), and you will receive the name
    // entered by the user. get-functions can only be called in the
    // doAnalysis-function (see below)

    // addCheckBox: name, initially true/false
    addCheckBox(DEGREE_CENTRALITY_STRING,false);

    // addCheckBox: name, initially true/false
    addCheckBox(INDEGREE_CENTRALITY_STRING,false);

    // addCheckBox: name, initially true/false
    addCheckBox(OUTDEGREE_CENTRALITY_STRING,false);

    // addCheckBox: name, initially true/false
    addCheckBox(CLOSENESS_CENTRALITY_STRING,false);

    // addCheckBox: name, initially true/false
    addCheckBox(BETWEENNESS_CENTRALITY_STRING,false);

    // addCheckBox: name, initially true/false
//    addCheckBox("flow-betweenness",false);

    // addCheckBox: name, initially true/false
    addCheckBox(AVERAGE_CENTRALITY_STRING,false);

    // addCheckBox: name, initially true/false
    //addFileChooser("file name","");
    addString("file name","");
    
    // addCheckBox: name, initially true/false
    addCheckBox(FILE_OUTPUT,false);

    // setToolPriority defined the order of the tools in the tabbed pane. tools
    // with lower numbers will appear first.
    setToolPriority(40);
  }


  /**
   * This function is called after all initialisation is done. In this function
   * the main analysis routine is processed. It is called, when the user presses
   * the run-button, and if the check for a net has been positive.
   * Include your analysis here.
   */
  public void doAnalysis()
  {

    Graph g = new Graph();
    g.createGraph(net);

    ArrayList  degreeCentrality           = new ArrayList();
    ArrayList  inDegreeCentrality         = new ArrayList();
    ArrayList  outDegreeCentrality        = new ArrayList();
    ArrayList  closenessCentrality        = new ArrayList();
    ArrayList  betweennessCentrality      = new ArrayList();
    ArrayList  averageCentrality          = new ArrayList();
//    ArrayList  flowBetweennessCentrality  = new ArrayList();

    boolean doAverageCentrality     = getCheckBox(AVERAGE_CENTRALITY_STRING);
    boolean doDegreeCentrality      = getCheckBox(DEGREE_CENTRALITY_STRING);
    boolean doIndegreeCentrality    = getCheckBox(INDEGREE_CENTRALITY_STRING);
    boolean doOutdegreeCentrality   = getCheckBox(OUTDEGREE_CENTRALITY_STRING);
    boolean doClosenessCentrality   = getCheckBox(CLOSENESS_CENTRALITY_STRING);
    boolean doBetweennessCentrality = 
      getCheckBox(BETWEENNESS_CENTRALITY_STRING);

    double numberOfCentralities = 0.0;

    Chart3D chart = new Chart3D("Centrality");

    if(doAverageCentrality)
    {
      ArrayList nodesList = g.getNodes();
      for(int i=0; i < nodesList.size(); i++)
      {
        Node n = (Node)nodesList.get(i);
        Node newNode = new Node();
        newNode.setName(n.getName());
        newNode.setValue(0.0);
        averageCentrality.add(newNode);
      }
    }

    if(doDegreeCentrality)
    {
      degreeCentrality          = calculateDegreeCentrality(g);
      normalise(degreeCentrality);
      if(doAverageCentrality)
      {
        numberOfCentralities += 1.0;
        addCentralilites(averageCentrality, degreeCentrality);
      }
      for(int index=0; index < degreeCentrality.size(); index++)
      {
        Node n = (Node)degreeCentrality.get(index);

        chart.addValue(n.getValue(), "Degree", n.getName());
        System.out.println("Degree: " + n.getName() + ": " + n.getValue());
      }
    }

    if(doIndegreeCentrality)
    {
      inDegreeCentrality        = calculateInDegreeCentrality(g);
      normalise(inDegreeCentrality);
      if(doAverageCentrality)
      {
        numberOfCentralities += 1.0;
        addCentralilites(averageCentrality, inDegreeCentrality);
      }
      for(int index=0; index < inDegreeCentrality.size(); index++)
      {
        Node n = (Node)inDegreeCentrality.get(index);

        chart.addValue(n.getValue(), "In-Degree", n.getName());
        System.out.println("In-Degree: " + n.getName() + ": " + n.getValue());
      }
    }

    if(doOutdegreeCentrality)
    {
      outDegreeCentrality       = calculateOutDegreeCentrality(g);
      normalise(outDegreeCentrality);
      if(doAverageCentrality)
      {
        numberOfCentralities += 1.0;
        addCentralilites(averageCentrality, outDegreeCentrality);
      }
      for(int index=0; index < outDegreeCentrality.size(); index++)
      {
        Node n = (Node)outDegreeCentrality.get(index);

        chart.addValue(n.getValue(), "Out-Degree", n.getName());
        System.out.println("Out-Degree: " + n.getName() + ": " + n.getValue());
      }
    }

    if(doClosenessCentrality)
    {
      closenessCentrality       = calculateClosenessCentrality(g);
      normalise(closenessCentrality);
      if(doAverageCentrality)
      {
        numberOfCentralities += 1.0;
        addCentralilites(averageCentrality, closenessCentrality);
      }
      for(int index=0; index < closenessCentrality.size(); index++)
      {
        Node n = (Node)closenessCentrality.get(index);

        chart.addValue(n.getValue(), "Closeness", n.getName());
        System.out.println("Closeness: " + n.getName() + ": " + n.getValue());
      }

    }
    
    if(doBetweennessCentrality)
    {
      betweennessCentrality     = calculateBetweennessCentrality(g);
      normalise(betweennessCentrality);
      if(doAverageCentrality)
      {
        numberOfCentralities += 1.0;
        addCentralilites(averageCentrality, betweennessCentrality);
      }
      for(int index=0; index < betweennessCentrality.size(); index++)
      {
        Node n = (Node)betweennessCentrality.get(index);

        chart.addValue(n.getValue(), "Betweenness", n.getName());
        System.out.println("Betweenness: " + n.getName() + ": " + n.getValue());
      }

    }

    if(doAverageCentrality)
    {
      for(int index=0; index < averageCentrality.size(); index++)
      {
        Node n = (Node)averageCentrality.get(index);
        chart.addValue(n.getValue()/numberOfCentralities,
            "Average", n.getName());
        System.out.println("Average: " + n.getName() + ": " 
            + (n.getValue()/numberOfCentralities));
      }
    }


//    if(getCheckBox("fbelow-betweenness"))
//    {
//      flowBetweennessCentrality = calculateFlowBetweennessCentrality(g);
//      normalise(flowBetweennessCentrality);
//    }
//


    chart.doneValues();
    chart.pack();
    RefineryUtilities.centerFrameOnScreen(chart);
    chart.setVisible(true);
    System.out.println("done with drawing");

  }


  // **************************************************************************
  // add centralities
  // **************************************************************************

  private void addCentralilites(ArrayList destination, ArrayList source)
  {
    for(int i=0; i < destination.size(); i++)
    {
      Node destinationNode = (Node)destination.get(i);
      Node sourceNode      = (Node)source.get(i);
      destinationNode.setValue(
          destinationNode.getValue() + sourceNode.getValue());
    }
  }

  // **************************************************************************
  // normalise
  // **************************************************************************

  private void normalise(ArrayList nodes)
  {
    double maxValue = 0;
    for(int index=0; index < nodes.size(); index++)
    {
      Node n = (Node)nodes.get(index);

      if(n.getValue() > maxValue)
      {
        maxValue = n.getValue();
      }
    }

    for(int index=0; index < nodes.size(); index++)
    {
      Node n = (Node)nodes.get(index);

      n.setValue(n.getValue() / maxValue);
    }
  }
  
  // **************************************************************************
  // end of must-have-functions
  // start with you own functions here
  // **************************************************************************

  // **************************************************************************
  // Degree Centrality
  // **************************************************************************
  private ArrayList calculateDegreeCentrality(Graph g)
  {
    System.out.println("calculateDegreeCentrality");
    ArrayList nodes = new ArrayList();


    ArrayList ns = g.getNodes();

    for(int index=0; index < ns.size(); index++)
    {
      Node n = (Node)ns.get(index);

      double dc = calculateNodeDegreeCentrality(n, ns.size());
      Node newNode = new Node();
      newNode.setName(n.getName());
      newNode.setValue(dc);
      nodes.add(newNode);
    }
    return nodes;
  }

  private double calculateNodeDegreeCentrality(Node n, int graphSize)
  {
    System.out.println("calculateNodeDegreeCentrality");
    int degree = 0;
    for(int index=0; index < n.getIncomingEdges().size(); index++)
    {
      Edge in = (Edge)n.getIncomingEdges().get(index);

      if(in.getSource() != in.getDestination()) // no recurrent connections
      {
        for(int outIndex=0; outIndex < n.getOutgoingEdges().size(); outIndex++)
        {
          Edge out = (Edge)n.getOutgoingEdges().get(outIndex);

          if(out.getSource() != out.getDestination()) // no recurrent connections
          {
            if(!out.equals(in))
            {
              degree++;
            }
          }
        }
      }
    }

    return (double)( (double)degree/(double)((graphSize-1)*(graphSize-1)));
  }

  // **************************************************************************
  // In Degree Centrality
  // **************************************************************************

  private ArrayList calculateInDegreeCentrality(Graph g)
  {
    System.out.println("calculateInDegreeCentrality");
    ArrayList nodes = new ArrayList();


    ArrayList ns = g.getNodes();

    int size = ns.size()-1;

    for(int index=0; index < ns.size(); index++)
    {
      Node n = (Node)ns.get(index);

      int indegree = n.getIncomingEdges().size();
      Node newNode = new Node();
      newNode.setName(n.getName());
      newNode.setValue((double)indegree/(double)size);
      nodes.add(newNode);
    }
    return nodes;
  }

  // **************************************************************************
  // Out Degree Centrality
  // **************************************************************************

  private ArrayList calculateOutDegreeCentrality(Graph g)
  {
    System.out.println("calculateOutDegreeCentrality");
    ArrayList nodes = new ArrayList();


    ArrayList ns = g.getNodes();

    int size = ns.size()-1;

    for(int index=0; index < ns.size(); index++)
    {
      Node n = (Node)ns.get(index);

      int outdegree = n.getOutgoingEdges().size();
      Node newNode = new Node();
      newNode.setName(n.getName());
      newNode.setValue((double)outdegree/(double)size);
      nodes.add(newNode);
    }
    return nodes;
  }


  // **************************************************************************
  // Closeness Centrality
  // **************************************************************************

  private void calculateDistances(double value, Node s, Node t, Graph g)
  {

    if(s == t) // at end of this 
    {
      if (value < s.getValue())
      {
        s.setValue(value);
      }
      return;
    }

    if(s.getValue() < 0d)
    {
      s.setValue(value);

      for(int index=0; index < s.getOutgoingEdges().size(); index++)
      {
        Edge e = (Edge)s.getOutgoingEdges().get(index);

        if(e.getDestination() != s)
        {
          calculateDistances((value + 1.0), e.getDestination(), t, g);
        }
      }
    }
  }

  private ArrayList calculateClosenessCentrality(Graph g)
  {
    System.out.println("calculateClosenessCentrality");
    ArrayList nodes = new ArrayList();

    int size = g.getNodes().size();


    for(int index=0; index < g.getNodes().size(); index++)
    {
      Node s = (Node)g.getNodes().get(index);

      Node newNode = new Node();
      nodes.add(newNode);
      newNode.setName(s.getName());
      newNode.setValue(0);

      // clean up graph first:

      for(int nIndex=0; nIndex < g.getNodes().size(); nIndex++)
      {
        Node n = (Node)g.getNodes().get(nIndex);

        n.setValue(-1); // -1 not a valid distance
      }

      for(int nIndex=0; nIndex < g.getNodes().size(); nIndex++)
      {
        Node t = (Node)g.getNodes().get(nIndex);

        if(s != t)
        {
          calculateDistances(0,s,t,g);
          if (t.getValue() < 0)
          {
            newNode.setValue(newNode.getValue() + (size*size));
          }
          else
          {
            newNode.setValue(newNode.getValue() + t.getValue());
          }
        }
      }
      newNode.setValue(1.0/newNode.getValue());
    }
    return nodes;
  }

  // **************************************************************************
  // betweenness centrality (nach ulrik brandes)
  // **************************************************************************

  private ArrayList calculateBetweennessCentrality(Graph g)
  {

    double[] c_b  = new double[g.size()];
    double[] sigma = new double[g.size()];
    double[] d = new double[g.size()];

    System.out.println("size: " + g.size());


    for(int i=0; i < g.size(); i++)
    {
      c_b[i] = 0d;
    }


    for(int i=0; i < g.size(); i++)
    {
      // init
      Node s = (Node)g.getNodes().get(i);

      Stack      S = new Stack();
      //LinkedList P = new LinkedList();
      LinkedList Q = new LinkedList();
      ArrayList  P = new ArrayList();
      Q.add(s);

      sigma[i] = 1d;
      d[i] = 0d;

      for(int j=0; j < g.size(); j++)
      {
        if(j != i)
        {
          sigma[j] = 0d;
          d[j] = -1d;
        }
        P.add(new LinkedList());
      }
      // init done

      while(!Q.isEmpty())
      {
        Node v = (Node)Q.removeFirst();
        int index_v = g.getNodes().indexOf(v);
        ArrayList outgoingEdges = v.getOutgoingEdges();
        S.push(v);
        for(int index=0; index < outgoingEdges.size(); index++)
        {
          Edge e = (Edge)outgoingEdges.get(index);

          Node w = (Node)e.getDestination();
          int index_w = g.getNodes().indexOf(w);
          if ( d[g.getNodes().indexOf(w)] < 0 )
          {
            Q.add(w);
            d[index_w] = d[index_v] + 1;
          }
          if(d[index_w] == d[index_v] + 1)
          {
            sigma[index_w] = sigma[index_w] + sigma[index_v];
            LinkedList wList = (LinkedList)P.get(index_w);
            if(!wList.contains(v))
            {
              wList.add(v);
            }
          }
        }
      }


      for(int ii=0; ii < g.size(); ii++)
      {
        d[ii] = 0;
      }

      while(!S.isEmpty())
      {
        Node w = (Node)S.pop();
        int index_w = g.getNodes().indexOf(w);
        LinkedList pw = (LinkedList)P.get(index_w);
        for(int ii=0; ii < pw.size(); ii++)
        {
          Node v = (Node)pw.get(ii);
          int index_v = g.getNodes().indexOf(v);
          d[index_v] = d[index_v] + sigma[index_v] / sigma[index_w] * (1 +
              d[index_w]);
          if( w != s)
          {
            c_b[index_w] = c_b[index_w] + d[index_w];
          }
        }

      }
    }

    ArrayList bC = new ArrayList();

    for(int i=0; i < g.size(); i++)
    {
      Node newNode = new Node();
      newNode.setName(((Node)g.getNodes().get(i)).getName());
      newNode.setValue(c_b[i]);
      bC.add(newNode);
    }

    return bC;

  }


  // **************************************************************************
  // flow-betweenness centrality
  // **************************************************************************

  private ArrayList calculateFlowBetweennessCentrality(Graph g)
  {

    double[] C_f = new double[g.size()];

    for(int i = 0; i < g.size(); i++)
    {
      C_f[i] = 0d;
    }

    for(int index=0; index < g.getNodes().size(); index++)
    {
      Node s = (Node)g.getNodes().get(index);

      for(int tIndex=0; tIndex < g.getNodes().size(); tIndex++)
      {
        Node t = (Node)g.getNodes().get(tIndex);

        if(s != t)
        {
          double[] c_f = maxFlowLiftToFrontAlgorithm(s,t,g);

          for(int vIndex=0; vIndex < g.getNodes().size(); vIndex++)
          {
            Node v = (Node)g.getNodes().get(vIndex);

            int index_v = g.getNodes().indexOf(v);
            C_f[index_v] += c_f[index_v];
          }

        }
      }
    }


    ArrayList fbC = new ArrayList();

    for(int i=0; i < g.size(); i++)
    {
      Node newNode = new Node();
      newNode.setName( ((Node)g.getNodes().get(i)).getName());
      newNode.setValue(C_f[i]);
      fbC.add(newNode);
    }

    return fbC;
  }

  private double[] maxFlowLiftToFrontAlgorithm(Node s, Node t, Graph g)
  {
    double[] c_f = new double[g.size()];

    double[]   h = new double[g.size()];

    double[]   e = new double[g.size()];
    double[][] f = new double[g.size()][g.size()];

    for(int i = 0; i < g.size(); i++)
    {
      c_f[i] = 0d;
    }

    initialisePreflow(g, s, h, e, f);



    return c_f;
  }


  private void initialisePreflow(Graph g, Node s, double[] h, double[] e,
      double[][]f )
  {

    int index_s = g.getNodes().indexOf(s);

    for(int nIndex=0; nIndex < g.getNodes().size(); nIndex++)
    {
      Node n = (Node)g.getNodes().get(nIndex);

      int index = g.getNodes().indexOf(n);
      h[index] = 0d;
      e[index] = 0d;

      for(int edgeIndex=0; edgeIndex < n.getIncomingEdges().size(); edgeIndex++)
      {
        Edge edge = (Edge)n.getIncomingEdges().get(edgeIndex);

        int source = g.getNodes().indexOf(edge.getSource());
        int destination = g.getNodes().indexOf(edge.getDestination());
        f[source][destination] = 0d;
        f[destination][source] = 0d;
      }
    }

    h[g.getNodes().indexOf(s)] = g.size();

    for(int index=0; index < s.getOutgoingEdges().size(); index++)
    {
      Edge edge = (Edge)s.getOutgoingEdges().get(index);

      int index_u = g.getNodes().indexOf(edge.getDestination());
      f[index_s][index_u] = edge.getValue();
      f[index_u][index_s] = -edge.getValue();
      e[index_u] = edge.getValue();
    }

  }


  private void discharge(Node u, double[] e, int index_u)
  {
  }
}
