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

/*
 * Created on 02.04.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package hinton.analyser.toolkit;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 * @author rosemann
 */
public abstract class InputValue extends InputComponent
{
  private   JLabel               inputDescr;
  private   JPanel               inputCont;
  protected JComponent           inputComp     = null;
  protected JComponent           inputComp2    = null;
  private   boolean              isInitialized = false;
  private   InputValueListener[] myListener    = null;

  protected InputValue(String caption)
  {
    this.name       = caption;
    this.inputDescr = new JLabel(this.name);
    this.inputCont  = new JPanel(new GridLayout(1,3,5,5));
    
    this.inputCont.add(inputDescr);
  }
  
  /*
   *  (non-Javadoc)
   * @see Hinton.analyser.toolkit.InputComponent#getInputComponent()
   */
  public final JComponent getInputComponent()
  {
    if (!this.isInitialized)
    {
      this.inputCont.add(inputComp);
      if(inputComp2 != null)
      {
        this.inputCont.add(inputComp2);
      }
      this.isInitialized = true;
    }
    inputComp.setInputVerifier(this.getInputVerifier());
    this.setComponentValue();
    return inputCont;
  }
  
  /*
   *  (non-Javadoc)
   * @see Hinton.analyser.toolkit.InputComponent#setName(java.lang.String)
   */
  public final void setName(String name)
  {
    super.setName(name);
    this.inputDescr.setText(name);
  }
  
  /**
   * Adds the InputValueListener inputValueListener to the listeners that have
   * to be notified if the value of the InputValue has changed
   * @param inputValueListener
   */
  public final void addInputValueListener(InputValueListener inputValueListener)
  {
    ArrayList listener;
    if (this.myListener != null)
    {
      listener = new ArrayList(Arrays.asList(this.myListener));
    } else {
      listener = new ArrayList();
    }
    listener.add(inputValueListener);
    this.myListener = (InputValueListener[])
                      listener.toArray(new InputValueListener[0]);
  }
  
  /*
   *  (non-Javadoc)
   * @see Hinton.analyser.toolkit.InputComponent#performUpdate()
   */
  public final void performUpdate()
  {
    if (updateValue() && this.myListener != null)
    {
      for (int i = 0; i < this.myListener.length; i++)
      {
        this.myListener[i].inputValueUpdated(this);
      }
    }
  }
  
  /**
   * This method has to be implemented when subclassing InputValue.
   * 
   * This method is called when performUpdate() is invoked
   * 
   * @return true if the value of the InputValue has change and the
   *         registered listenders have to be notified
   */
  public abstract boolean updateValue();
  
  /**
   * This method has to be implemented when subclassing InputValue.
   * 
   * This method is called when getInputComponent() is invoked to
   * set the value of the visual component to the value of the InputValue
   */
  protected abstract void setComponentValue();
  
  /**
   * This method has to be implemented when subclassing InputValue
   * 
   * Returns an InputVerifier for this InputValue
   * 
   * @return InputVerifier for this InputValue or
   *         null if none
   */
  protected abstract InputVerifier getInputVerifier();
  
  /* CLASS DEFINITIONS */
  
  /**
   * 
   * @author rosemann
   *
   * IBoolean is the implementation of an InputComponent for <i>boolean</i>
   * values
   */
  public static class IBoolean extends InputValue
  {
    /** the value of the InputValue.IBoolean */
    public  boolean   value;

    private IBoolean(String caption)
    {
      super(caption);
    }
    
    /** create a new InputValue with the given caption and value
     * 
     * @param caption the caption of the JComponent representing this InputValue
     * @param value the value of theis InputValue
     */
    public IBoolean(String caption, boolean value)
    {
      super(caption);
      this.value = value;
      this.inputComp  = new JCheckBox("", this.value);
    }
    
    /*
     *  (non-Javadoc)
     * @see Hinton.analyser.toolkit.InputValue#updateValue()
     */
    public boolean updateValue()
    {
      boolean ret;
      if (ret = (((JCheckBox)inputComp).isSelected() != this.value))
      {
        this.value = ((JCheckBox)inputComp).isSelected();
      }
      
      return ret; 
    }

    /* (non-Javadoc)
     * @see Hinton.analyser.netstructure.toolkit.InputValue#setComponentValue()
     */
    protected void setComponentValue() {
      ((JCheckBox)inputComp).setSelected(this.value);
    }

    /* (non-Javadoc)
     * @see Hinton.analyser.netstructure.toolkit.InputValue#getInputVerifier()
     */
    protected InputVerifier getInputVerifier() {
      // Specify no InputVerifier since JCheckBox can only Become true or false
      return null;
    }
    
  } 
  
  /**
   * 
   * @author rosemann
   *
   * IInteger is the implementation of an InputComponent for <i>int</i> values
   */
  public static class IInteger extends InputValue
  {
    public  int        value;
    private int        upperLimit = Integer.MAX_VALUE;
    private int        lowerLimit = Integer.MIN_VALUE;        
    
    private IInteger(String caption) 
    {
      super(caption);
    }
    
    /**
     * Create a new InputComponent for int values with the given caption and
     * value 
     * @param caption the caption of the JComponent representing this InputValue
     * @param value the value of the InputValue
     */
    public IInteger(String caption, int value)
    {
      super(caption);
      this.value = value;
      this.inputComp  = new JTextField(String.valueOf(this.value));
    }
    
    /**
     * Create a new InputComponent for int values with the given caption and
     * value which is limited by the limits of the interval
     * [lowerLimit, upperLimit] 
     * @param caption the caption of the JComponent representing this InputValue
     * @param value the value of this InputValue
     * @param lowerLimit the lower limit of the limiting interval
     * @param upperLimit the upper limit of the limiting interval
     */
    public IInteger(String caption, int value, int lowerLimit, int upperLimit)
    {
      super(caption);
      this.value = value;
      this.inputComp  = new JTextField(String.valueOf(this.value));
      this.lowerLimit = (lowerLimit <  upperLimit ? lowerLimit : upperLimit);
      this.upperLimit = (upperLimit >= lowerLimit ? upperLimit : lowerLimit);
    }
    
    /* (non-Javadoc)
     * @see Hinton.analyser.netstructure.toolkit.InputComponent#performUpdate()
     */
    public boolean updateValue()
    {
      boolean ret;
      int     txtFieldValue = java.lang.Integer.parseInt(
                                      ((JTextField)this.inputComp).getText());
      if (ret = (this.value != txtFieldValue))
      {
        this.value = txtFieldValue;
      }
      return ret;
    }
    
    public void setUpperLimit(int limit)
    {
      this.upperLimit = limit;
    }
    
    public void setLowerLimit(int limit)
    {
      this.lowerLimit = limit;
    }

    /* (non-Javadoc)
     * @see Hinton.analyser.netstructure.toolkit.InputValue#setComponentValue()
     */
    protected void setComponentValue() {
      ((JTextField)this.inputComp).setText(String.valueOf(this.value));   
    }

    /* (non-Javadoc)
     * @see Hinton.analyser.netstructure.toolkit.InputValue#getInputVerifier()
     */
    protected InputVerifier getInputVerifier() {
      
      return new IntegerInputVerifier(this.lowerLimit, this.upperLimit);
      
    }
    
    private static class IntegerInputVerifier extends InputVerifier
    {
      private int lowerLimit = Integer.MIN_VALUE;
      private int upperLimit = Integer.MAX_VALUE;
      
      public IntegerInputVerifier(int lowerLimit, int upperLimit)
      {
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
      }
      
      public boolean verify(JComponent input)
      {
        JTextField txt = (JTextField)input;
        try
        {
          int val = Integer.parseInt(txt.getText());
          return (lowerLimit <= val && val <= upperLimit);
        }
        catch (NumberFormatException nfe)
        {
          return false;
        }
      }
          
      public boolean shouldYieldFocus(JComponent input)
      {
        if (! verify(input))
        { 
          String messageText = "You have to enter an Integer value";
              
          if (this.lowerLimit != Integer.MIN_VALUE &&
              this.upperLimit != Integer.MAX_VALUE)
          {
            messageText += " inside the limits of the Interval [" 
                            + this.lowerLimit +
                            ", " + this.upperLimit + "]";
          }
              
          input.setInputVerifier(null);
          JOptionPane.showMessageDialog(input.getTopLevelAncestor(),
                                        messageText,
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
          input.setInputVerifier(this);
          return false;
        } else {
          return true;
        }
              
      }
        
  }
    
  }
  
  /**
   * 
   * @author rosemann
   *
   * IDouble is the implementation of an InputComponent for <i>double</i> values
   */
  public static class IDouble extends InputValue
  {
    public  double value;
    private double lowerLimit = Double.MIN_VALUE;
    private double upperLimit = Double.MAX_VALUE;        
    
    private IDouble(String caption) 
    {
      super(caption);
    }
    
    /**
     * Create a new InputComponent for double values with the given caption and
     * value 
     * @param caption the caption of the JComponent representing this InputValue
     * @param value the value of the InputValue
     */
    public IDouble(String caption, double value)
    {
      super(caption);
      this.value = value;
      this.inputComp  = new JTextField(String.valueOf(this.value));
    }
    
    /**
     * Create a new InputComponent for double values with the given caption and
     * value which is limited by the limits of the interval
     * [lowerLimit, upperLimit] 
     * @param caption the caption of the JComponent representing this InputValue
     * @param value the value of this InputValue
     * @param lowerLimit the lower limit of the limiting interval
     * @param upperLimit the upper limit of the limiting interval
     */
    public IDouble(String caption, double value,
                  double lowerLimit, double upperLimit)
    {
      super(caption);
      this.value = value;
      this.inputComp  = new JTextField(String.valueOf(this.value));
      this.lowerLimit = lowerLimit;
      this.upperLimit = upperLimit;
    }
    
    public void setUpperLimit(double limit)
    {
      this.upperLimit = limit;
    }
    
    public void setLowerLimit(double limit)
    {
      this.lowerLimit = limit;
    }
    
    /* (non-Javadoc)
     * @see Hinton.analyser.netstructure.toolkit.InputComponent#performUpdate()
     */
    public boolean updateValue()
    {   
      boolean ret;
      double  txtFieldValue = java.lang.Double.parseDouble(
                                   ((JTextField)this.inputComp).getText());
      if (ret = (this.value != txtFieldValue))
      {
        this.value = txtFieldValue;
      }
      return ret;
    }

    /* (non-Javadoc)
     * @see Hinton.analyser.netstructure.toolkit.InputValue#setComponentValue()
     */
    protected void setComponentValue() {
      ((JTextField)this.inputComp).setText(String.valueOf(this.value)); 
    }

    /* (non-Javadoc)
     * @see Hinton.analyser.netstructure.toolkit.InputValue#getInputVerifier()
     */
    protected InputVerifier getInputVerifier() {
      return new DoubleInputVerifier(this.lowerLimit, this.upperLimit);
    }
    
    private static class DoubleInputVerifier extends InputVerifier
    {
      private double lowerLimit = Double.MIN_VALUE;
      private double upperLimit = Double.MAX_VALUE;
  
      public DoubleInputVerifier(double lowerLimit, double upperLimit)
      {
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
      }
  
      public boolean verify(JComponent input)
      {
        JTextField txt = (JTextField)input;
        try
        {
          double val = Double.parseDouble(txt.getText());
          return (this.lowerLimit <= val && val <= this.upperLimit);
        }
        catch (NumberFormatException nfe)
        {
          return false;
        }
      }
      
      public boolean shouldYieldFocus(JComponent input)
      {
        if (! verify(input))
        { 
          String messageText = "You have to enter a Double value";
          
          if (this.lowerLimit != Double.MIN_VALUE &&
              this.upperLimit != Double.MAX_VALUE)
          {
            messageText += " inside the limits of the Interval [" 
                            + this.lowerLimit +
                            ", " + this.upperLimit + "]";
          }
          
          input.setInputVerifier(null);
          JOptionPane.showMessageDialog(input.getTopLevelAncestor(),
                                        messageText,
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
          input.setInputVerifier(this);
          return false;
        } else {
          return true;
        }
          
      }
    
    }
  }

  /**
   * 
   * @author rosemann
   *
   * IString is the implementation of an InputComponent for <i>String</i> values
   */
  public static class IString extends InputValue
  {
    /** the value of the InputValue */
    public  String       value;
    
    private IString(String caption) 
    {
      super(caption);
    }
    
    /**
     * Create a new InputComponent for String values with the given caption and
     * value 
     * @param caption the caption of the JComponent representing this InputValue
     * @param value the value of the InputValue
     */
    public IString(String caption, String value)
    {
      super(caption);
      this.value = value;
      this.inputComp  = new JTextField(this.value);
    }
    
    /* (non-Javadoc)
     * @see Hinton.analyser.netstructure.toolkit.InputComponent#performUpdate()
     */
    public boolean updateValue()
    {
      boolean ret;
      String  txtFieldValue = ((JTextField)this.inputComp).getText();
      
      if (ret = (this.value.compareTo(txtFieldValue) != 0))
      {
        this.value = txtFieldValue;
      }
      return ret;
    }

    /* (non-Javadoc)
     * @see Hinton.analyser.netstructure.toolkit.InputValue#setComponentValue()
     */
    protected void setComponentValue() {
      ((JTextField)this.inputComp).setText(this.value);   
    }

    /* (non-Javadoc)
     * @see Hinton.analyser.netstructure.toolkit.InputValue#getInputVerifier()
     */
    protected InputVerifier getInputVerifier() {
      // TODO Auto-generated method stub
      return null;
    }
    
  }

  /**
   * 
   * @author rosemann
   *
   * IObject is the implementation of an InputComponent for <i>Object</i> values
   */
  public static class IObject extends InputValue
  {
    public  Object   value;
    private Object[] choices; 

    private IObject(String caption) {
      super(caption);
    }
    
    /**
     * Create a new InputValue for Object values with the given caption and
     * possible values choices. The initial value is determined by the valueIdx
     * Object in the choices Array
     * @param caption the caption of the JComponent representing this InputValue
     * @param valueIdx the index of the initial value in the choices Array
     * @param choices array containing the possible values for this InputValue
     */
    public IObject(String caption, int valueIdx, Object[] choices)
    {
      super(caption);
      this.value     = choices[valueIdx];
      this.choices   = choices;
      this.inputComp = new JComboBox(this.choices);
    }

    /**
     * Create a new InputValue for Object values with the given caption and
     * possible values choices. The initial value is determined by the valueIdx
     * Object in the choices Array
     * @param caption the caption of the JComponent representing this InputValue
     * @param valueIdx the index of the initial value in the choices Array
     * @param choices array containing the possible values for this InputValue
     * @param choicesNames array containing the names for the possible values
     */
    public IObject(String caption, int valueIdx, Object[] choices,
                                                 String[] choicesNames)
    {
      super(caption);
      this.value     = choices[valueIdx];
      this.choices   = choices;
      this.inputComp = new JComboBox(choicesNames);
    }

    /* (non-Javadoc)
     * @see Hinton.analyser.netstructure.toolkit.InputValue#setComponentValue()
     */
    protected void setComponentValue()
    {
      int idx = -1;
      
      for (int i = 0; i < this.choices.length && idx == -1; i++)
      {
        if (this.choices[i].equals(this.value))
        {
          idx = i;
        }
      }
      ((JComboBox)this.inputComp).setSelectedIndex(idx);
    }

    /* (non-Javadoc)
     * @see Hinton.analyser.netstructure.toolkit.InputComponent#performUpdate()
     */
    public boolean updateValue()
    {
      boolean ret;
      Object  choosen = this.choices[
                            (((JComboBox)this.inputComp).getSelectedIndex())];
      if (ret = (!this.value.equals(choosen)))
      {
        this.value = choosen;
      }
      return ret;
    }

    /* (non-Javadoc)
     * @see Hinton.analyser.netstructure.toolkit.InputValue#getInputVerifier()
     */
    protected InputVerifier getInputVerifier() {
      // TODO Auto-generated method stub
      return null;
    }
  }

   /**
   * 
   * @author zahedi
   *
   * IString is the implementation of an InputComponent for <i>String</i> values
   */
  public static class IFileChooser extends InputValue implements ActionListener
  {
    /** the value of the InputValue */
    public  String       value;
    private   JFileChooser   fileDialog    = new JFileChooser(".");
    
    private IFileChooser(String caption) 
    {
      super(caption);
    }
    
    public void actionPerformed(ActionEvent event)
    {
      if(event.getSource() == inputComp2)
      {
        this.getFileName();
      }
    }

    private void getFileName()
    {
      int returnVal = fileDialog.showOpenDialog(null);
      if(returnVal == JFileChooser.APPROVE_OPTION) 
      {
        if (fileDialog.getSelectedFile().exists())
        {
          ((JTextField)this.inputComp).setText(
                 fileDialog.getCurrentDirectory() + File.separator 
               + fileDialog.getSelectedFile().getName());
          updateValue();
        }
        else
        {
        }
      }
    }
    /**
     * Create a new InputComponent for String values with the given caption and
     * value 
     * @param caption the caption of the JComponent representing this InputValue
     * @param value the value of the InputValue
     */
    public IFileChooser(String caption, String value)
    {
      super(caption);
      this.value = value;
      this.inputComp  = new JTextField(this.value, 5);
      this.inputComp2 = new JButton("...");
      ((JButton)inputComp2).addActionListener(this);
    }
    
    /* (non-Javadoc)
     * @see Hinton.analyser.netstructure.toolkit.InputComponent#performUpdate()
     */
    public boolean updateValue()
    {
      boolean ret;
      String  txtFieldValue = ((JTextField)this.inputComp).getText();
      
      if (ret = (this.value.compareTo(txtFieldValue) != 0))
      {
        this.value = txtFieldValue;
      }
      return ret;
    }

    /* (non-Javadoc)
     * @see Hinton.analyser.netstructure.toolkit.InputValue#setComponentValue()
     */
    protected void setComponentValue() {
      ((JTextField)this.inputComp).setText(this.value);   
    }

    /* (non-Javadoc)
     * @see Hinton.analyser.netstructure.toolkit.InputValue#getInputVerifier()
     */
    protected InputVerifier getInputVerifier() {
      // TODO Auto-generated method stub
      return null;
    }
    
  } 

}
