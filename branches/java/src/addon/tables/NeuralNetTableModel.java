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


package addon.tables;

import java.awt.Component;
import java.util.Stack;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import cholsey.Net;
import cholsey.Neuron;
import cholsey.NeuronType;
import cholsey.ProcessMode;
import cholsey.Synapse;
import cholsey.SynapseList;
import cholsey.SynapseMode;
import cholsey.SynapseType;

/**
 * Class:       NeuralNetTableModel
 * Description: This tablemodel connects the graphical view of a neural
 *              net with its data representation.
 *
 * @see Addon.Tables.NeuralNetTable
 */
public class NeuralNetTableModel extends AbstractTableModel
{
  private static final int MODE         = 0;
  private static final int BIAS         = 1;
  private static final int ACTIVITY     = 2;
  private static final int TRANSMITTER  = 3;
  private static final int RECEPTOR     = 4;
  private static final int ALPHA        = 5;
  private static final int BETA         = 6;
  private static final int GAMMA        = 7;
  private static final int DELTA        = 8;
  private static final int MY           = 9;
  private static final int NY           = 10;

  /** the net */
  private Net neuralNet;
  /** the parental component for message-output */
  private Component parent;
  /** undostack */
  private Stack undoStack;

  /**
   * Creates a model with its parental component (for message output).
   *
   * @param parent is the parental component
   */
  public NeuralNetTableModel(Component parent)
  {
    neuralNet = new Net();
    undoStack = new Stack();
    this.parent = parent;
  }

  /**
   * Creates a model, initializes it with the given neural net and with
   * its parental component (for message output).
   *
   * @param neuralNet is the neural net for the table
   * @param parent is the parental component
   */
  public NeuralNetTableModel(Net neuralNet, Component parent)
  {
    this(parent);
    this.setNeuralNet(neuralNet);
  }

  /**
   * Sets the neural net of the table to the given one. The old
   * neural net will be de-refenced after this method.
   *
   * @param neuralNet is the neural net for the table
   */
  public void setNeuralNet(Net neuralNet)
  {
    this.neuralNet = neuralNet;
    undoStack.removeAllElements();
    update();
  }

  /**
   * Returns the reference of the internal neural net.
   *
   * @return is the reference of the internal neural net
   */
  public Net getNeuralNet()
  {
    return this.neuralNet;
  }

  /**
   * Returns the number of columns of the table.
   *
   * @return is the number of columns of the table
   */
  public int getColumnCount()
  {
    if (neuralNet.getSynapseMode() == SynapseMode.DYNAMIC)
    {
      return neuralNet.size() + 11;
    } 
    else
    {
      return neuralNet.size() + 5;
    }
  }

  /**
   * Returns the number of rows of the table.
   *
   * @return is the number of rows of the table
   */
  public int getRowCount()
  {
    return neuralNet.size();
  }

  /**
   * Returns the name of the table column.
   *
   * @param col is the number of the column which name is searched
   * @return is the name of the table column
   */
  public String getColumnName(int col)
  {
    final int i = neuralNet.size();

    if (col == i)   return "Mode";
    if (col == i+1) return "Bias";
    if (col == i+2) return "Activity";
    if (col == i+3) return "Transmitter";
    if (col == i+4) return "Receptors";
    if (col == i+5) return "Alpha";
    if (col == i+6) return "Beta";
    if (col == i+7) return "Gamma";
    if (col == i+8) return "Delta";
    if (col == i+9) return "My";
    if (col == i+10) return "Ny";
    else            return "Neuron"+(col+1);
  }

  /**
   * Returns the value of a specified cell.
   *
   * @param col is the number of the column
   * @param row is the number of the row
   * @return is the value of the specified cell
   */
  public Object getValueAt(int row, int col)
  {
    Neuron source, dest;
    Synapse syn;
    boolean negative;

    source = null;
    dest   = null;


    if (row < 0 && col < 0)
    {
      negative = true;
      row = -row - 1;
      col = -col - 1;
    } 
    else
    {
      negative = false;
    }


    //no synapses TO input neurons
    if (isInputIndex(row) && col <= getMaxHiddenIndex())
    {
      return NeuralNetTable.TEXT_NA;
    }

    if (col <= getMaxHiddenIndex() && col <= getMaxReadBufferIndex() && col > getMaxOutputIndex())
    {
      return NeuralNetTable.TEXT_NA;
    }


    //identify dest from row
    if (isInputIndex(row))
    {
      dest = (Neuron) neuralNet.getInputNeurons().elementAt(row);
    }
    if (isOutputIndex(row))
    {
      dest = (Neuron) neuralNet.getOutputNeurons().
                          elementAt(row - getMaxInputIndex()-1);
    }
    if (isReadIndex(row))
    {
      dest = (Neuron) neuralNet.getReadBufferNeurons().
                          elementAt(row - getMaxOutputIndex()-1);
    }
    if (isHiddenIndex(row))
    {
      dest = (Neuron) neuralNet.getHiddenNeurons().
                          elementAt(row - getMaxReadBufferIndex()-1);
    }

    //identify source from col
    if (isInputIndex(col))
    {
      source = (Neuron) neuralNet.getInputNeurons().elementAt(col);
    }
    if (isOutputIndex(col))
    {
      source = (Neuron) neuralNet.getOutputNeurons().
                          elementAt(col - getMaxInputIndex()-1);
    }
    if (isReadIndex(col))
    {
      source = (Neuron) neuralNet.getReadBufferNeurons().
                          elementAt(col - getMaxOutputIndex()-1);
    }
    if (isHiddenIndex(col))
    {
      source = (Neuron) neuralNet.getHiddenNeurons().
                          elementAt(col - getMaxReadBufferIndex()-1);
    }

    // dest not found -> return error
    if (dest == null)
    {
      return NeuralNetTable.TEXT_ERROR;
    }

    //MODE
    if (col == neuralNet.size()+ MODE)
    {
        if (dest.getProcessMode().mode() == ProcessMode.PROCESS_DYNAMIC)
        {
          return "dynamic";
        } 
        else if (dest.getProcessMode().mode() == ProcessMode.PROCESS_CONSISTENT)
        {
          return "consistent";
        } 
        else if (dest.getProcessMode().mode() == ProcessMode.PROCESS_STATIC)
        {
          return "static";
        } 
        else 
        {
          return "dynamic";
        }
    }

    if (negative && col > getMaxHiddenIndex())
    {
      return ""+-1;
    }

    //BIAS
    if (col == neuralNet.size() + BIAS)
    {
      return ""+dest.getBias();
    }

    //ACTIVITY
    if (col == neuralNet.size() + ACTIVITY)
    {
      return ""+dest.getActivation();
    }

    //TRANSMITTER
    if (col == neuralNet.size() + TRANSMITTER)
    {
      return ""+dest.getTransmitterLevel();
    }

    //RECEPTORS
    if (col == neuralNet.size() + RECEPTOR)
    {
      return ""+dest.getReceptorLevel();
    }

    //ALPHA
    if (col == neuralNet.size() + ALPHA)
    {
      return ""+dest.getAlpha();
    }

    //BETA
    if (col == neuralNet.size() + BETA)
    {
      return ""+dest.getBeta();
    }

    //GAMMA
    if (col == neuralNet.size() + GAMMA)
    {
      return ""+dest.getGamma();
    }

    //DELTA
    if (col == neuralNet.size() + DELTA)
    {
      return ""+dest.getDelta();
    }

    //MY
    if (col == neuralNet.size() + MY)
    {
      return ""+dest.getMy();
    }

    //NY
    if (col == neuralNet.size() + NY)
    {
      return ""+dest.getNy();
    }

    //source not found -> return error
    if (source == null)
    {
      return NeuralNetTable.TEXT_ERROR;
    }

    syn = dest.getSynapse(source);

    if (syn == null)
    {
      if (neuralNet.getSynapseMode() == SynapseMode.DYNAMIC)
      {
        return NeuralNetTable.TEXT_DYN_EMPTY;
      } 
      else
      {
        return NeuralNetTable.TEXT_CON_EMPTY;
      }
    }
    if (negative)
    {
      if (syn.getProcessMode().mode() == ProcessMode.PROCESS_DYNAMIC)
      {
        return ""+0;
      } 
      else if (syn.getProcessMode().mode() == ProcessMode.PROCESS_CONSISTENT)
      {
        return ""+1;
      } 
      else if (syn.getProcessMode().mode() == ProcessMode.PROCESS_STATIC)
      {
        return ""+2;
      } 
      else
      {
        return ""+0;
      }
    } 
    else
    {
      if (neuralNet.getSynapseMode() == SynapseMode.DYNAMIC)
      {
        //return NeuralNetTable.TEXT_DYN_SYNAPSES;
        if(syn.type() == SynapseType.EXCITATORY)
        {
          return NeuralNetTable.TEXT_DYN_EXCITATORY_COLOR;
        }
        else
        {
          return NeuralNetTable.TEXT_DYN_INHIBITORY_COLOR;
        }
      } 
      else
      {
        return ""+syn.strength();
      }
    }
  }

  /**
   * Returns the class of a specified cell. Should in this case always be
   * "String"
   *
   * @param c is the number of the column
   * @return is the class of the specified cell
   */
  public Class getColumnClass(int c)
  {
    return getValueAt(0, c).getClass();
  }

  /**
   * Returns wether a cell is editable or not.
   *
   * @param r is the number of the row
   * @param c is the number of the column
   * @return is TRUE if cell will be editable
   */
  public boolean isCellEditable(int row, int col)
  {
    boolean ret;

    ret = true;
    ret = ret && !(row <= getMaxInputIndex() && col <= getMaxHiddenIndex());
    ret = ret && !(col <= getMaxHiddenIndex() && col <= getMaxReadBufferIndex() && col > getMaxOutputIndex());
    //ret = ret && !(neuralNet.getSynapseMode() == SynapseMode.DYNAMIC
    //               && row <= getMaxHiddenIndex()
    //               && col <= getMaxHiddenIndex());
    return ret;
  }

  /**
   * Sets the value of a specified cell.
   *
   * @param col is the number of the column
   * @param row is the number of the row
   * @param value is the value to be set
   */
  public void setValueAt(Object value, int row, int col)
  {
    Neuron  source, dest;
    Synapse syn;
    double  val;
    boolean empty;
    int     synmode;

    source = null;
    dest   = null;
    synmode= -1;

    if (value.equals(""))
    {
      empty = true;
      val   = 0;
    } 
    else if (col == neuralNet.size())
    {
      empty = false;
      val   = 0;
    } 
    else if (neuralNet.getSynapseMode() == SynapseMode.DYNAMIC
        && col <= getMaxHiddenIndex())
    {
      empty = false;
      val   = 0;
      String stringValue = (String)value;
      if(stringValue.equals(NeuralNetTable.TEXT_DYN_EXCITATORY))
      {
        val = 1;
      }
      else if (stringValue.equals(NeuralNetTable.TEXT_DYN_INHIBITORY))
      {
        val = -1;
      }
      else if (stringValue.equals(NeuralNetTable.TEXT_DYN_DELETE))
      {
        val = 0;
        empty = true;
      }
    } 
    else
    {
      empty = false;
      try
      {
        val = Double.valueOf(String.valueOf(value)).doubleValue();
      } 
      catch (Exception ex)
      {
        if (value.equals("dynamic"))
        {
          val     = 0;
          synmode = 0;
        } 
        else if (value.equals("consistent"))
        {
          val     = 0;
          synmode = 1;

        } 
        else if (value.equals("static"))
        {
          val     = 0;
          synmode = 2;

        } 
        else
        {
          JOptionPane.showMessageDialog(parent, "Not a value: "+value, "Error",
                                      JOptionPane.ERROR_MESSAGE);
          return;
        }
      }
    }

    if (isInputIndex(row) && col <= getMaxHiddenIndex())
    {
      return;
    }

    //identify dest from row
    if (isInputIndex(row))
    {
      dest = (Neuron) neuralNet.getInputNeurons().elementAt(row);
    }
    if (isOutputIndex(row))
    {
      dest = (Neuron) neuralNet.getOutputNeurons().
                          elementAt(row - getMaxInputIndex()-1);
    }
    if (isReadIndex(row))
    {
      dest = (Neuron) neuralNet.getReadBufferNeurons().
                          elementAt(row - getMaxOutputIndex()-1);
    }
    if (isHiddenIndex(row))
    {
      dest = (Neuron) neuralNet.getHiddenNeurons().
                          elementAt(row - getMaxReadBufferIndex()-1);
    }

    //identify dest from col
    if (isInputIndex(col))
    {
      source = (Neuron) neuralNet.getInputNeurons().elementAt(col);
    }
    if (isOutputIndex(col))
    {
      source = (Neuron) neuralNet.getOutputNeurons().
                          elementAt(col - getMaxInputIndex()-1);
    }
    if (isReadIndex(col))
    {
      source = (Neuron) neuralNet.getReadBufferNeurons().
                          elementAt(col - getMaxOutputIndex()-1);
    }
    if (isHiddenIndex(col))
    {
      source = (Neuron) neuralNet.getHiddenNeurons().
                          elementAt(col - getMaxReadBufferIndex()-1);
    }

    //NEURON MODE
    if (col == neuralNet.size() + MODE)
    {
      saveUndo();
      if (value.equals("dynamic"))
      {
        dest.setProcessMode(ProcessMode.DYNAMIC);
      } 
      else if (value.equals("consistent"))
      {
        dest.setProcessMode(ProcessMode.CONSISTENT);
      } 
      else if (value.equals("static"))
      {
        dest.setProcessMode(ProcessMode.STATIC);
      } 
      else
      {
        dest.setProcessMode(ProcessMode.DYNAMIC);
      }
    } 
    else if (col == neuralNet.size() + BIAS) //BIAS
    {
      saveUndo();
      dest.setBias(val);
    } 
    else if (col == neuralNet.size() + ACTIVITY) //ACTIVITY
    {
      dest.setActivation(val);
    } 
    else if (col == neuralNet.size() + TRANSMITTER) //TRANSMITTER
    {
      dest.setTransmitterLevel(val);
    } 
    else if (col == neuralNet.size() + RECEPTOR) //RECEPTORS
    {
      dest.setReceptorLevel(val);
    } 
    else if (col == neuralNet.size() + ALPHA) //ALPHA
    {
      dest.setAlpha(val);
    } 
    else if (col == neuralNet.size() + BETA) //BETA
    {
      dest.setBeta(val);
    } 
    else if (col == neuralNet.size() + GAMMA) //GAMMA
    {
      dest.setGamma(val);
    }
    else if (col == neuralNet.size()+DELTA) //DELTA
    {
      dest.setDelta(val);
    } 
    else if (col == neuralNet.size()+MY) //MY
    {
      dest.setMy(val);
    } 
    else if (col == neuralNet.size()+NY) //NY
    {
      dest.setNy(val);
    } 
    else if (dest == null || source == null) // NO DEST OR SOURCE
    {
      //error
      return;
    } 
    else
    {
      saveUndo();
      syn = dest.getSynapse(source);

      if (syn != null && synmode == 0)
      {
        syn.setProcessMode(ProcessMode.DYNAMIC);
      } 
      else if (syn != null && synmode == 1)
      {
        syn.setProcessMode(ProcessMode.CONSISTENT);
      } 
      else if (syn != null && synmode == 2)
      {
        syn.setProcessMode(ProcessMode.STATIC);
      } 
      else if (empty)
      {
        if (syn != null)
        {
          neuralNet.delSynapse(source, dest);
        }
      } 
      else
      {
        if (syn != null)
        {
          if(neuralNet.getSynapseMode() == SynapseMode.DYNAMIC)
          {
            if(val < 0) 
            {
              syn.setSynapseType(SynapseType.INHIBITORY);
            }
            else
            {
              syn.setSynapseType(SynapseType.EXCITATORY);
            }
          }
          else
          {
            syn.setStrength(val);
          }
        } 
        else
        {
          if(neuralNet.getSynapseMode() == SynapseMode.DYNAMIC)
          {
            if(val < 0) 
            {
              Synapse s =
                neuralNet.addSynapse(source, dest, val, ProcessMode.DYNAMIC);
              s.setSynapseType(SynapseType.INHIBITORY);
            }
            else
            {
              Synapse s =
                neuralNet.addSynapse(source, dest, val, ProcessMode.DYNAMIC);
              s.setSynapseType(SynapseType.EXCITATORY);
            }
          }
          else
          {
            neuralNet.addSynapse(source, dest, val, ProcessMode.DYNAMIC);
          }
        }
      }
    }

    fireTableCellUpdated(row, col);
    //fireTableDataChanged();
  }

  /**
   * Returns wether the index is an input neuron or not.
   * Indizes are beginning at 0. The first are input neurons
   * the next are output neurons an then there will be hidden neuron-indizes.
   * Attention: we use internal index-handling which is independant from
   * the index you will find in the Neuron-class.
   *
   * @param i is the index of the neuron
   * @return is TRUE if the index is an input neuron
   */
  public boolean isInputIndex(int index)
  {
    return index <= getMaxInputIndex();
  }

 /**
   * Returns wether the index is an output neuron or not.
   * Indizes are beginning at 0. The first are input neurons
   * the next are output neurons an then there will be hidden neuron-indizes.
   * Attention: we use internal index-handling which is independant from
   * the index you will find in the Neuron-class.
   *
   * @param i is the index of the neuron
   * @return is TRUE if the index is an output neuron
   */
  public boolean isOutputIndex(int index)
  {
    return index >  getMaxInputIndex()
        && index <= getMaxOutputIndex();
  }

 /**
   * Returns wether the index is a read-buffer neuron or not.
   * Indizes are beginning at 0. The first are input neurons
   * the next are output neurons an then there will be read-buffer neuron-indizes.
   * Attention: we use internal index-handling which is independant from
   * the index you will find in the Neuron-class.
   *
   * @param i is the index of the neuron
   * @return is TRUE if the index is a read-buffer neuron
   */
  public boolean isReadIndex(int index)
  {
    return index >  getMaxOutputIndex()
        && index <= getMaxReadBufferIndex();
  }

 /**
   * Returns wether the index is a hidden neuron or not.
   * Indizes are beginning at 0. The first are input neurons
   * the next are output neurons an then there will be hidden neuron-indizes.
   * Attention: we use internal index-handling which is independant from
   * the index you will find in the Neuron-class.
   *
   * @param i is the index of the neuron
   * @return is TRUE if the index is a hidden neuron
   */
  public boolean isHiddenIndex(int index)
  {
    return index >  getMaxReadBufferIndex()
        && index <= getMaxHiddenIndex();
  }

  /**
   * Returns the last index is an input neuron.
   * Indizes are beginning at 0. The first are input neurons
   * the next are output neurons an then there will be hidden neuron-indizes.
   * Attention: we use internal index-handling which is independant from
   * the index you will find in the Neuron-class.
   *
   * @return is the last index is an input neuron, i+1 will be an output neuron
   */
  public int getMaxInputIndex()
  {
    return neuralNet.getInputNeurons().size()-1;
  }

  /**
   * Returns the last index is an output neuron.
   * Indizes are beginning at 0. The first are input neurons
   * the next are output neurons an then there will be hidden neuron-indizes.
   * Attention: we use internal index-handling which is independant from
   * the index you will find in the Neuron-class.
   *
   * @return is the last index is an output neuron, i+1 will be a hidden neuron
   */
  public int getMaxOutputIndex()
  {
     return (getMaxInputIndex()
             + neuralNet.getOutputNeurons().size());
  }

  /**
   * Returns the last index is an read-buffer neuron.
   * Indizes are beginning at 0. The first are input neurons
   * the next are read-buffer neurons an then there will be hidden neuron-indizes.
   * Attention: we use internal index-handling which is independant from
   * the index you will find in the Neuron-class.
   *
   * @return is the last index is an read-buffer neuron, i+1 will be a hidden neuron
   */
  public int getMaxReadBufferIndex()
  {
     return (getMaxOutputIndex()
             + neuralNet.getReadBufferNeurons().size());
  }

  /**
   * Returns the last index is a hidden neuron.
   * Indizes are beginning at 0. The first are input neurons
   * the next are output neurons an then there will be hidden neuron-indizes.
   * Attention: we use internal index-handling which is independant from
   * the index you will find in the Neuron-class.
   *
   * @return is the last index is a hidden neuron, i+1 will be an invalid index
   * of neurons
   */
  public int getMaxHiddenIndex()
  {
     return (getMaxReadBufferIndex()
             + neuralNet.getHiddenNeurons().size());
  }

  /**
   * Adds a neuron to the neural net which type is ntype.
   *
   * @see cholsey.NeuronType
   * @param ntype is the neuron-type to be added
   */
  public void addNeuron(NeuronType ntype)
  {
    neuralNet.addNeuron(0, 0, 0, ProcessMode.DYNAMIC, ntype);
    update();
  }

  /**
   * Deletes a neuron from the neural net which has the <b>internal</b>
   * index i. <br>
   * Important: Here, we use only internal index-handling which is <b>independant</b>
   * from the neuron-index you will find in the Neuron-class.
   *
   * @see cholsey.NeuronType
   * @param ntype is the neuron-type to be added
   */
  public void delNeuron(int i)
  {
    Neuron neuron;

    neuron = null;
    if (isInputIndex(i))
    {
      neuron = (Neuron) neuralNet.getInputNeurons().elementAt(i);
    } 
    else if (isOutputIndex(i))
    {
      neuron = (Neuron) neuralNet.getOutputNeurons().
                          elementAt(i - getMaxInputIndex()-1);
    } 
    else if (isReadIndex(i))
    {
      neuron = (Neuron) neuralNet.getReadBufferNeurons().
                          elementAt(i - getMaxInputIndex()-1);
    } 
    else if (isHiddenIndex(i))
    {
      neuron = (Neuron) neuralNet.getHiddenNeurons().
                          elementAt(i - getMaxOutputIndex()-1);
    }
    if (neuron != null)
    {
      neuralNet.delNeuron(neuron);
      update();
    }
  }

  /**
   * Saves the recent neural net to the undo-stack for later undoing.
   */
  public void saveUndo()
  {
    undoStack.addElement(neuralNet.copy());
  }

  /**
   * Return wheather there is an undo avaiable or not.
   */
  public boolean hasUndo()
  {
    return !undoStack.isEmpty();
  }

  public void undo()
  {
    if (undoStack.isEmpty())
    {
      return;
    }
    this.neuralNet.setContent((Net) undoStack.pop());
    update();
  }

  /**
   * Forces an data-update for the table
   */
  public void update()
  {
    fireTableStructureChanged();
  }


  public void convertTheNetToDynamic()
  {
    neuralNet.setSynapseMode(SynapseMode.CONVENTIONAL); // to get the strength
    for(neuralNet.neurons().start();
        neuralNet.neurons().hasMore();
        neuralNet.neurons().next())
    {
      Neuron n = neuralNet.neurons().neuron();
      n.setAlpha(1.0);
      n.setBeta(0.01);
      n.setGamma(0.01);
      n.setDelta(0.02);
      n.setMy(0.015);
      n.setNy(0.015);
      SynapseList sl = neuralNet.neurons().neuron().synapses();
      if(sl != null)
      {
        for(sl.start(); sl.hasMore(); sl.next())
        {
          Synapse s = sl.synapse();
          if(s.strength() > 0)
          {
            s.setSynapseType(SynapseType.EXCITATORY);
          }
          else
          {
            s.setSynapseType(SynapseType.INHIBITORY);
          }
        }
      }
    }
    neuralNet.setSynapseMode(SynapseMode.DYNAMIC);
  }

  public void convertTheNetToStatic()
  {
    for(neuralNet.neurons().start();
        neuralNet.neurons().hasMore();
        neuralNet.neurons().next())
    {
      SynapseList synapses = neuralNet.neurons().neuron().synapses();
      if (synapses != null)
      {
        for(synapses.start();
            synapses.hasMore();
            synapses.next())
        {
          // setStrength only works for the static strength value
          synapses.synapse().setStrength(synapses.synapse().strength());
        }
      }
    }
    neuralNet.setSynapseMode(SynapseMode.CONVENTIONAL);

  }

}


