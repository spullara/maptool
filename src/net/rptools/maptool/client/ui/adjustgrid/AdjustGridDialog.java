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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.KeyStroke;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.swing.VerticalLabel;
import javax.swing.JTextField;

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
        
        gridSizeTextField = panel.getTextField("gridSize");
        offsetXTextField = panel.getTextField("xOffset");
        offsetYTextField = panel.getTextField("yOffset");

        FormAccessor accessor = panel.getFormAccessor();
        accessor.replaceBean("adjustGridPanel", getAdjustGridPanel());

        setLayout(new GridLayout());
        add(panel);
        
        getRootPane().setDefaultButton((JButton)okButton);
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
        }
        return adjustGridPanel;
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
