/* The MIT License
 * 
 * Copyright (c) 2005 Jay Gorrell, David Rice, Trevor Croft
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation files 
 * (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN 
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */

package net.rptools.maptool.client.swing;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.text.Document;


/**
 * A text field that supports two tone text.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public class TwoToneTextField extends JTextField {
  
  /*---------------------------------------------------------------------------------------------
   * Instance Variables
   *-------------------------------------------------------------------------------------------*/

  /**
   * The second color drawn behind the foreground color
   */
  private Color twoToneColor = Color.DARK_GRAY;
  
  
  /*---------------------------------------------------------------------------------------------
   * Class Variables
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * The ui class id.
   */
  private static final String UI_CLASS_ID = "TwoToneTextFieldUI";
  
  /*---------------------------------------------------------------------------------------------
   * Constructors
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * Set the UI for the field.
   */
  static {
    UIManager.put(UI_CLASS_ID, TwoToneTextFieldUI.class.getName());
  }
  
  /**
   * Default constructor
   */
  public TwoToneTextField() {
    super();
  }
  
  /**
   * Create the field with the passed text.
   *
   * @param aText The text for the new field.
   */
  public TwoToneTextField(String aText) {
    super(aText);
  }
  
  /**
   * Create the field with a set number of columns
   * 
   * @param aColumns The number of columns for the new field.
   */
  public TwoToneTextField(int aColumns) {
    super(aColumns);
  }
  
  /**
   * Create a field with the passed text and number of columns
   * 
   * @param aText The text for the new field.
   * @param aColumns The number of columns for the new field.
   */
  public TwoToneTextField(String aText, int aColumns) {
    super(aText, aColumns);
  }
  
  /**
   * Create a field with the given document, text, and column count.
   * 
   * @param aDoc The document for the new field.
   * @param aText The text for the new field.
   * @param aColumns The number of columns for the new field.
   */
  public TwoToneTextField(Document aDoc, String aText, int aColumns) {
    super(aDoc, aText, aColumns);
  }
  
  /*---------------------------------------------------------------------------------------------
   * Instance Methods
   *-------------------------------------------------------------------------------------------*/

  /**
   * Get the twoToneColor for this TwoToneTextField.
   *
   * @return Returns the current value of twoToneColor.
   */
  public Color getTwoToneColor() {
    return twoToneColor;
  }

  /**
   * Set the value of twoToneColor for this TwoToneTextField.
   *
   * @param aTwoToneColor The twoToneColor to set.
   */
  public void setTwoToneColor(Color aTwoToneColor) {
    twoToneColor = aTwoToneColor;
  }

  /**
   * Gets the class ID for a UI.
   *
   * @return the string "TwoToneTextFieldUI"
   * @see JComponent#getUIClassID
   * @see UIDefaults#getUI
   */
  public String getUIClassID() {
      return UI_CLASS_ID;
  }
}
