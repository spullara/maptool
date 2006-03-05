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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
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
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JSpinner;

public class AdjustGridDialog extends JDialog {

    private JPanel jContentPane = null;
    private AdjustGridPanel adjustGridPanel = null;
    private JPanel buttonPanel = null;
    private JButton okButton = null;
    private JPanel eastPanel = null;
    private JPanel southControlPanel = null;
    private JSlider gridCountXSlider = null;
    private JSlider gridCountYSlider = null;
	private JButton cancelButton = null;
	
	private boolean isOK;
	public boolean isOK() {
		return isOK;
	}

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
        this.setContentPane(getJContentPane());
        getRootPane().setDefaultButton(getOkButton());
    }

    @Override
    public void setVisible(boolean b) {

    	if (getOwner() != null) {
    		SwingUtil.centerOver(this, getOwner());
    	}
    	
    	super.setVisible(b);
    }
    
    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.weighty = 1.0D;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.gridy = 0;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.gridwidth = 2;
            gridBagConstraints3.anchor = java.awt.GridBagConstraints.EAST;
            gridBagConstraints3.gridy = 2;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridy = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 1;
            gridBagConstraints1.fill = java.awt.GridBagConstraints.VERTICAL;
            gridBagConstraints1.gridy = 0;
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            
            JPanel panel = new JPanel(new BorderLayout());
            panel.setLayout(new GridBagLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
            panel.add(getEastPanel(), gridBagConstraints1);
            panel.add(getSouthControlPanel(), gridBagConstraints2);
            panel.add(getButtonPanel(), gridBagConstraints3);
            panel.add(getAdjustGridPanel(), gridBagConstraints);
            
            jContentPane.add(panel, java.awt.BorderLayout.CENTER);
            
            jContentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "cancel");
            jContentPane.getActionMap().put("cancel", new AbstractAction() {
            	public void actionPerformed(ActionEvent e) {
            		isOK = false;
            		setVisible(false);
            	}
            });
        }
        return jContentPane;
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

    /**
     * This method initializes buttonPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setAlignment(java.awt.FlowLayout.RIGHT);
            buttonPanel = new JPanel();
            buttonPanel.setLayout(flowLayout);
            buttonPanel.add(getOkButton(), null);
            buttonPanel.add(getCancelButton(), null);
        }
        return buttonPanel;
    }

    /**
     * This method initializes okButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getOkButton() {
        if (okButton == null) {
            okButton = new JButton();
            okButton.setText("OK");
            okButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                	isOK = true;
                	setVisible(false);
                }
            });
        }
        return okButton;
    }

    /**
     * This method initializes eastPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getEastPanel() {
        if (eastPanel == null) {
            eastPanel = new JPanel();
            eastPanel.setLayout(new BorderLayout());
            eastPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,5,5,5));
            eastPanel.add(getGridCountYSlider(), java.awt.BorderLayout.WEST);
            VerticalLabel label = new VerticalLabel("<html><body><b>Grid Count Y</b></body></html>", JLabel.CENTER);
            label.setRotation(VerticalLabel.ROTATE_LEFT);
            eastPanel.add(label, BorderLayout.EAST);
        }
        return eastPanel;
    }
    
    /**
     * This method initializes southControlPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getSouthControlPanel() {
        if (southControlPanel == null) {
            southControlPanel = new JPanel();
            southControlPanel.setLayout(new BorderLayout());
            southControlPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,5,5,5));
            southControlPanel.add(getGridCountXSlider(), java.awt.BorderLayout.NORTH);
            southControlPanel.add(new JLabel("<html><body><b>Grid Count X</b></body></html>", JLabel.CENTER), BorderLayout.SOUTH);
        }
        return southControlPanel;
    }

    /**
     * This method initializes gridCountXSlider	
     * 	
     * @return javax.swing.JSlider	
     */
    private JSlider getGridCountXSlider() {
        if (gridCountXSlider == null) {
            gridCountXSlider = new JSlider();
            gridCountXSlider.setMinimum(0);
            gridCountXSlider.setMaximum(100);
            gridCountXSlider.setValue(10);
            gridCountXSlider.setFocusable(false);
            gridCountXSlider.setPaintLabels(true);
            gridCountXSlider.setMajorTickSpacing(10);
            gridCountXSlider.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent e) {
                	int value = getGridCountXSlider().getValue();
                	
                	if (value < 2) {
                		getGridCountXSlider().setValue(2);
                	}
                    getAdjustGridPanel().setGridCountX(value);
                }
            });
            gridCountXSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            	public void mousePressed(java.awt.event.MouseEvent e) {
            		getAdjustGridPanel().setShowRows(false);
            		getAdjustGridPanel().repaint();
            	}
            	@Override
            	public void mouseReleased(MouseEvent e) {
            		getAdjustGridPanel().setShowRows(true);
            		getAdjustGridPanel().repaint();
            	}
            });
        }
        return gridCountXSlider;
    }

    /**
     * This method initializes gridCountYSlider	
     * 	
     * @return javax.swing.JSlider	
     */
    private JSlider getGridCountYSlider() {
        if (gridCountYSlider == null) {
            gridCountYSlider = new JSlider();
            gridCountYSlider.setOrientation(javax.swing.JSlider.VERTICAL);
            gridCountYSlider.setMaximum(100);
            gridCountYSlider.setValue(10);
            gridCountYSlider.setMinimum(0);
            gridCountYSlider.setFocusable(false);
            gridCountYSlider.setPaintLabels(true);
            gridCountYSlider.setMajorTickSpacing(10);
            gridCountYSlider.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent e) {
                	int value = getGridCountYSlider().getValue();

                	if (value < 2) {
                		getGridCountXSlider().setValue(2);
                	}
                	
                    getAdjustGridPanel().setGridCountY(value);
                }
            });
            gridCountYSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            	public void mousePressed(java.awt.event.MouseEvent e) {
            		getAdjustGridPanel().setShowCols(false);
            		getAdjustGridPanel().repaint();
            	}
            	@Override
            	public void mouseReleased(MouseEvent e) {
            		getAdjustGridPanel().setShowCols(true);
            		getAdjustGridPanel().repaint();
            	}
            });
        }
        return gridCountYSlider;
    }
    
    public Rectangle getGridBounds() {
    	return getAdjustGridPanel().getGridBounds();
    }
    
    public int getGridXCount () {
    	return getGridCountXSlider().getValue();
    }
    
    public int getGridYCount () {
    	return getGridCountYSlider().getValue();
    }

    /**
	 * This method initializes cancelButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText("Cancel");
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					isOK = false;
					setVisible(false);
				}
			});
		}
		return cancelButton;
	}

}
