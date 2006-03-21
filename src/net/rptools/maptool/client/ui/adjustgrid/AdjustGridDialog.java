/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft
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
package net.rptools.maptool.client.ui.adjustgrid;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

import net.rptools.lib.swing.SwingUtil;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.form.FormAccessor;

public class AdjustGridDialog extends JDialog {

    private AdjustGridPanel adjustGridPanel = null;
	
	private boolean isOK;
	private JTextField gridSizeTextField = null;
	private JTextField offsetXTextField = null;
	private JTextField offsetYTextField = null;

	/**
     * This is the default constructor
     */
    public AdjustGridDialog(JFrame owner, BufferedImage image) {
        super(owner, "Adjust Grid", true);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
        		isOK = false;
        		setVisible(false);
        		dispose();
        	}
        });

        initialize();
        getAdjustGridPanel().setZoneImage(image);
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(500, 500);
        
        FormPanel panel = new FormPanel("net/rptools/maptool/client/ui/forms/adjustGridDialog.jfrm");

        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "cancel");
        panel.getActionMap().put("cancel", new CancelAction());

		AbstractButton okButton = panel.getButton("okButton");
        okButton.setAction(new OKAction());
        
		AbstractButton cancelButton = panel.getButton("cancelButton");
        cancelButton.setAction(new CancelAction());
        
        AdjustGridPanel adjustGridPanel = getAdjustGridPanel();
        
        gridSizeTextField = panel.getTextField("gridSize");
        gridSizeTextField.addActionListener(new UpdateAdjustGridPanelHandler());
        gridSizeTextField.setText(Integer.toString(adjustGridPanel.getGridSize()));
        gridSizeTextField.addFocusListener(new SelectTextListener(gridSizeTextField));
        
        offsetXTextField = panel.getTextField("xOffset");
        offsetXTextField.addActionListener(new UpdateAdjustGridPanelHandler());
        offsetXTextField.setText(Integer.toString(adjustGridPanel.getGridOffsetX()));
        offsetXTextField.addFocusListener(new SelectTextListener(offsetXTextField));

        offsetYTextField = panel.getTextField("yOffset");
        offsetYTextField.addActionListener(new UpdateAdjustGridPanelHandler());
        offsetYTextField.setText(Integer.toString(adjustGridPanel.getGridOffsetY()));
        offsetYTextField.addFocusListener(new SelectTextListener(offsetYTextField));

        FormAccessor accessor = panel.getFormAccessor();
        accessor.replaceBean("adjustGridPanel", adjustGridPanel);

        setLayout(new GridLayout());
        add(panel);
        
        getRootPane().setDefaultButton((JButton)okButton);
    }

    public void initialize(final int gridSize, final int gridOffsetX, final int gridOffsetY, final Color gridColor) {
    	
    	EventQueue.invokeLater(new Runnable(){

    		public void run() {
    	    	gridSizeTextField.setText(Integer.toString(gridSize));
    	    	offsetXTextField.setText(Integer.toString(gridOffsetX));
    	    	offsetYTextField.setText(Integer.toString(gridOffsetY));
    	    	getAdjustGridPanel().setGridColor(gridColor);
    		}
    	});
    }
    
    @Override
    public void setVisible(boolean b) {

    	if (getOwner() != null) {
    		SwingUtil.centerOver(this, getOwner());
    	}
    	
    	super.setVisible(b);
    }
    
	public boolean isOK() {
		return isOK;
	}

    /**
     * This method initializes adjustGridPanel	
     * 	
     * @return net.rptools.maptool.client.ui.adjustgrid.AdjustGridPanel	
     */
    private AdjustGridPanel getAdjustGridPanel() {
        if (adjustGridPanel == null) {
            adjustGridPanel = new AdjustGridPanel();
            adjustGridPanel.setBorder(BorderFactory.createLineBorder(Color.black));
            adjustGridPanel.setBackground(Color.white);
            adjustGridPanel.addPropertyChangeListener(new AdjustGridPanelChangeListener());
        }
        return adjustGridPanel;
    }

    public int getGridSize() {
    	return getAdjustGridPanel().getGridSize();
    }
    
    public int getGridOffsetX() {
    	return getAdjustGridPanel().getGridOffsetX();
    }
    
    public int getGridOffsetY() {
    	return getAdjustGridPanel().getGridOffsetY();
    }
    
    private class AdjustGridPanelChangeListener implements PropertyChangeListener {
    	
    	public void propertyChange(PropertyChangeEvent evt) {

    		if (AdjustGridPanel.PROPERTY_GRID_OFFSET_X.equals(evt.getPropertyName())) {
    			offsetXTextField.setText(evt.getNewValue().toString());
    		}
    		if (AdjustGridPanel.PROPERTY_GRID_OFFSET_Y.equals(evt.getPropertyName())) {
    			offsetYTextField.setText(evt.getNewValue().toString());
    		}
    		if (AdjustGridPanel.PROPERTY_GRID_SIZE.equals(evt.getPropertyName())) {
    			gridSizeTextField.setText(evt.getNewValue().toString());
    		}
    		
    	}
    }
    
    public void setGridSize(int gridSize) {
    	getAdjustGridPanel().setGridSize(gridSize);
    }

    public void setGridOffset(int offsetX, int offsetY) {
    	getAdjustGridPanel().setGridOffset(offsetX, offsetY);
    }
    
    private class UpdateAdjustGridPanelHandler implements ActionListener {

    	public void actionPerformed(ActionEvent e) {
    		
    		setGridSize(Integer.parseInt(gridSizeTextField.getText()));
    		setGridOffset(Integer.parseInt(offsetXTextField.getText()), Integer.parseInt(offsetYTextField.getText()));
    	}
    }

    private class SelectTextListener implements FocusListener {
 
    	private JTextComponent textComponent;
    	
    	public SelectTextListener(JTextComponent component) {
    		textComponent = component;
    	}
    	
    	public void focusGained(FocusEvent e) {
    	}
    	public void focusLost(FocusEvent e) {
    	}
    }
    
	////
	// ACTIONS
	private class OKAction extends AbstractAction {
		public OKAction() {
			putValue(Action.NAME, "OK");
		}
		public void actionPerformed(ActionEvent e) {
        	isOK = true;
        	setVisible(false);
        	dispose();
		}
	}
	private class CancelAction extends AbstractAction {
		public CancelAction() {
			putValue(Action.NAME, "Cancel");
		}
		public void actionPerformed(ActionEvent e) {
			isOK = false;
			setVisible(false);
        	dispose();
		}
	}
}
