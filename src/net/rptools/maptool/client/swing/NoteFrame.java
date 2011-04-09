/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *  
 *	http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package net.rptools.maptool.client.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

public class NoteFrame extends JFrame {

	private JTextComponent noteArea;
	private JButton clearButton;
	private JButton closeButton;
	
	public NoteFrame() {
		setPreferredSize(new Dimension(300, 300));
		initUI();
	}
	
	private void initUI() {
		setLayout(new BorderLayout());
		
		add(BorderLayout.CENTER, new JScrollPane(getNoteArea()));
		add(BorderLayout.SOUTH, createButtonBar());
	}
	
	public JTextComponent getNoteArea() {
		if (noteArea == null) {
			noteArea = new JTextArea();
			noteArea.setBorder(BorderFactory.createLineBorder(Color.black));
		}
		
		return noteArea;
	}
	
	private JPanel createButtonBar() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		panel.add(BorderLayout.WEST, getClearButton());
		panel.add(BorderLayout.EAST, getCloseButton());
		
		return panel;
	}
	
	public JButton getClearButton() {
		if (clearButton == null) {
			clearButton = new JButton("Clear");
			clearButton.addActionListener(new ActionListener(){ 
				public void actionPerformed(ActionEvent e) {
					getNoteArea().setText("");
				}
			});
		}
		
		return clearButton;
	}
	
	public JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton("Close");
			closeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					processWindowEvent( new WindowEvent( NoteFrame.this, WindowEvent.WINDOW_CLOSING) );
				}
			});
		}
		
		return closeButton;
	}
	
	public String getText() {
		return getNoteArea().getText();
	}
	
	public void addText(String text) {
		getNoteArea().setText(getNoteArea().getText() + text +"\n");
		getNoteArea().setCaretPosition(getNoteArea().getText().length());
	}
	

}
