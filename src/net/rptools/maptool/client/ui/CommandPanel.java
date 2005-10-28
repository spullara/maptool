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
package net.rptools.maptool.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

import net.rptools.maptool.client.AppActions;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.MacroManager;
import net.rptools.maptool.client.swing.TwoToneTextField;
import net.rptools.maptool.client.swing.TwoToneTextPane;
import net.rptools.maptool.model.ObservableList;

/**
 * The command panel consists of a field to enter commands and a
 * pane to display the results. Commands are passed to the 
 * <code>MacroManager</code> for processing. The executed 
 * commands update the message list which is displayed on the 
 * message panel.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public class CommandPanel extends JPanel implements Observer, ActionListener {
  
  /*---------------------------------------------------------------------------------------------
   * Instance Variables
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * The text pane that shows messages
   */
  private TwoToneTextPane messagePanel;
  
  /**
   * The pane used to scroll the message panel.
   */
  private JScrollPane messageScrollPane;
  
  /**
   * The input field for commands.
   */
  private TwoToneTextField cmdField;
  
  /**
   * The default style for the message panel
   */
  private Style defaultStyle;
  
  /*---------------------------------------------------------------------------------------------
   * Class Variables
   *------------------------------------------------------------------------------------------*/
  
  /**
   * The default style name for the message panels
   */
  private static final String DEFAULT_STYLE_NAME = "defaultStyle";
  
  /**
   * Command used to scroll the message panel up one line.
   */
  public static final String SCROLL_UP_COMMAND = "scrollUp";
    
  /**
   * Command used to scroll the message panel down one line.
   */
  public static final String SCROLL_DOWN_COMMAND = "scrollDown";
  
  /**
   * Command used to scroll the message panel to the bottom line.
   */
  public static final String SCROLL_BOTTOM_COMMAND = "scrollBottom";
  
  /*---------------------------------------------------------------------------------------------
   * Constructors
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * Create a new panel with field, message panel, and 
   * scrolling components.
   */
  public CommandPanel() {
    setOpaque(false);
    setBackground(null);
    
    // Create the message panel and observe the message list for changes
    messagePanel = new TwoToneTextPane();
    messagePanel.setEditable(false);
    messagePanel.setBackground(null);
    messagePanel.setOpaque(false);
    MapTool.getMessageList().addObserver(this);
    
    // Create the default style and apply it
    defaultStyle = createStyle(DEFAULT_STYLE_NAME);
    TwoToneTextPane.setFont(defaultStyle, new Font("SansSerif", Font.PLAIN, 10));
    TwoToneTextPane.setTwoToneColor(defaultStyle, Color.BLACK);
    StyleConstants.setForeground(defaultStyle, Color.YELLOW);
    StyleConstants.setFirstLineIndent(defaultStyle, -7.2F);
    messagePanel.setParagraphAttributes(defaultStyle, true);
    
    // Create the text field
    cmdField = new TwoToneTextField();
    cmdField.setBackground(null);
    cmdField.setOpaque(false);
    cmdField.setVisible(false);
    cmdField.setForeground(Color.YELLOW);
    cmdField.setTwoToneColor(Color.BLACK);
    
    // Add the actions to the command field
    ActionMap actions = cmdField.getActionMap();
    actions.put(AppActions.COMMIT_COMMAND_ID, AppActions.COMMIT_COMMAND);
    actions.put(AppActions.ENTER_COMMAND_ID, AppActions.ENTER_COMMAND);
    InputMap inputs = cmdField.getInputMap();
    inputs.put(KeyStroke.getKeyStroke("ESCAPE"), AppActions.CANCEL_COMMAND_ID);
    inputs.put(KeyStroke.getKeyStroke("ENTER"), AppActions.COMMIT_COMMAND_ID);
    
    // Create a scroll pane for the text area
    messageScrollPane = new JScrollPane(messagePanel);
    messageScrollPane.setOpaque(false);
    messageScrollPane.setBackground(null);
    messageScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
    messageScrollPane.setBorder(null);
    JViewport vp = messageScrollPane.getViewport();
    vp.setOpaque(false);
    vp.setBackground(null);

    // Add the scroll buttons to a west panel
    JPanel scrollButtonPanel = new JPanel();
    scrollButtonPanel.setOpaque(false);
    scrollButtonPanel.setBackground(null);
    scrollButtonPanel.setLayout(new BoxLayout(scrollButtonPanel, BoxLayout.Y_AXIS));
    scrollButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
    JButton button = new JButton(getIcon("scroll_up.gif"));
    button.setMargin(new Insets(0,0,0,0));
    button.setBorderPainted(false);
    button.setActionCommand(SCROLL_UP_COMMAND);
    button.addActionListener(this);
    scrollButtonPanel.add(button);
    scrollButtonPanel.add(Box.createVerticalStrut(1));
    button = new JButton(getIcon("scroll_down.gif"));
    button.setMargin(new Insets(0,0,0,0));
    button.setBorderPainted(false);
    button.setActionCommand(SCROLL_DOWN_COMMAND);
    button.addActionListener(this);
    scrollButtonPanel.add(button);
    scrollButtonPanel.add(Box.createVerticalStrut(1));
    button = new JButton(getIcon("scroll_bottom.gif"));
    button.setMargin(new Insets(0,0,0,0));
    button.setBorderPainted(false);
    button.setActionCommand(SCROLL_BOTTOM_COMMAND);
    button.addActionListener(this);
    scrollButtonPanel.add(Box.createVerticalStrut(1));
    scrollButtonPanel.add(button);
    
    // Add the components to the layout
    setLayout(new BorderLayout());
    add(BorderLayout.CENTER, messageScrollPane);
    add(BorderLayout.SOUTH, cmdField);
    add(BorderLayout.WEST, scrollButtonPanel);
  }
  
  /*---------------------------------------------------------------------------------------------
   * Instance Methods
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * Read an icon from the jar
   * 
   * @param name Icon's name.
   * @return An icon created from the file with the passed name.
   */
  private Icon getIcon(String name) {
    try {
      return new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("net/rptools/maptool/client/image/" + name)));
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
  
  /**
   * Start the typing of a command in the command field.
   */
  public void startCommand() {
    cmdField.setVisible(true);
    cmdField.setText("/");
    cmdField.requestFocus();
    validate();
    scrollToEnd();
  }
  
  /**
   * Scroll the message panel to the end position.
   */
  public void scrollToEnd() {
    try {
      int last = messagePanel.getDocument().getEndPosition().getOffset() - 1;
      if (last > 0)
        messagePanel.scrollRectToVisible(messagePanel.modelToView(last));
    } catch (BadLocationException e) {
      System.err.println("Problem scroling to end position");
    } // endtry
  }
  
  /**
   * Scroll the pane up or down
   * 
   * @param direction Direction to scroll: 1 for down and -1 for up.
   */
  public void scroll(int direction) {
    JScrollBar scrollBar = messageScrollPane.getVerticalScrollBar();
    BoundedRangeModel model = scrollBar.getModel();
    model.setValue(model.getValue() + scrollBar.getUnitIncrement(direction) * direction);
  }
  
  /**
   * Execute the command in the command field.
   */
  public void commitCommand() {
    String text = cmdField.getText().trim();
    if (text.charAt(0) == '/')
      text = text.substring(1);
    MacroManager.executeMacro(text);
    cancelCommand();
  }
  
  /**
   * Cancel the current command in the command field. 
   */
  public void cancelCommand() {
    cmdField.setVisible(false);
    cmdField.setText("");    
    validate();
    scrollToEnd();
  }
  
  /**
   * Clear all of the text in the message panel
   */
  public void clearMessagePanel() {
    try {
      Document doc = messagePanel.getDocument();
      doc.remove(0, doc.getLength());
    } catch (BadLocationException e) {
      throw new IllegalStateException("This should not happen!");
    } // endtry
  }
  
  /**
   * Reset the message panel to match the text in the message list.
   */
  public void resetMessagePanel() {
    try {
      clearMessagePanel();
      Document doc = messagePanel.getDocument();
      Iterator<String> i = MapTool.getMessageList().iterator();
      while (i.hasNext())
        doc.insertString(doc.getLength(), i.next(), null);
    } catch (BadLocationException e) {
      throw new IllegalStateException("This should not happen!");
    } // endtry
  }
  
  /**
   * Append the passed text as a new paragraph at the end of
   * the message panel.
   * 
   * @param text The text being added
   */
  public void appendToMessagePanel(String text) {
    try {
      Document doc = messagePanel.getDocument();
      int len = doc.getLength();
      doc.insertString(len, (len > 0 ? "\n" : "") + text, null);
      scrollToEnd();
    } catch (BadLocationException e) {
      throw new IllegalStateException("This should not happen!");
    } // endtry
  }
  
  /**
   * Use this to create a new style with the default settings. 
   * The style that is returned can then be modified as needed. 
   * 
   * @param styleName The style name to be created.
   * @return The new style.
   */
  public Style createStyle(String styleName) {
    if (messagePanel.getStyle(styleName) != null) 
      throw new IllegalArgumentException("The style '" + styleName + "' has already been created");
    Style style = messagePanel.addStyle(styleName, defaultStyle);
    return style;
  }
  
  /**
   * Get the named style
   * 
   * @param styleName The name of the style to retrieve.
   * @return The style found or <code>null</code> if there is not 
   * style with the passed name.
   */
  public Style getStyle(String styleName) {
    return messagePanel.getStyle(styleName);
  }
  
  /*---------------------------------------------------------------------------------------------
   * Observer Interface Methods
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * The list of messages has changed, handle the update.
   * 
   * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
   */
  public void update(Observable o, Object arg) {
    ObservableList<String> textList = MapTool.getMessageList();   
    ObservableList.Event event = (ObservableList.Event)arg; 
    switch (event) {
    case append:
      appendToMessagePanel(textList.get(textList.size() - 1));
      break;
    case add:
    case remove:
      resetMessagePanel();
      break;
    case clear:
      clearMessagePanel();
      break;
    default:
      throw new IllegalArgumentException("Unknown event: " + event);
    } // endswitch
  }
  
  /*---------------------------------------------------------------------------------------------
   * ActionListener Interface Methods
   *-------------------------------------------------------------------------------------------*/
  
  /**
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent aE) {
    if (SCROLL_BOTTOM_COMMAND.equals(aE.getActionCommand()))
      scrollToEnd();
    else if (SCROLL_DOWN_COMMAND.equals(aE.getActionCommand()))
      scroll(1);
    else if (SCROLL_UP_COMMAND.equals(aE.getActionCommand()))
      scroll(-1);
  }
}
