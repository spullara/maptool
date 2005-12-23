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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.FlowLayout;
import javax.swing.JSlider;

public class AdjustGridDialog extends JDialog {

    private JPanel jContentPane = null;
    private AdjustGridPanel adjustGridPanel = null;
    private JPanel buttonPanel = null;
    private JButton okButton = null;
    private JPanel eastPanel = null;
    private JPanel southPanel = null;
    private JPanel southControlPanel = null;
    private JSlider gridCountXSlider = null;
    private JSlider gridCountYSlider = null;

    /**
     * This is the default constructor
     */
    public AdjustGridDialog() {
        super();
        initialize();
    }

    public void setZoneImage(BufferedImage image) {
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
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getAdjustGridPanel(), java.awt.BorderLayout.CENTER);
            jContentPane.add(getEastPanel(), java.awt.BorderLayout.EAST);
            jContentPane.add(getSouthPanel(), java.awt.BorderLayout.SOUTH);
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
                    System.exit(0);
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
            eastPanel.add(getGridCountYSlider(), java.awt.BorderLayout.EAST);
        }
        return eastPanel;
    }

    /**
     * This method initializes southPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getSouthPanel() {
        if (southPanel == null) {
            southPanel = new JPanel();
            southPanel.setLayout(new BorderLayout());
            southPanel.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
            southPanel.add(getSouthControlPanel(), java.awt.BorderLayout.NORTH);
        }
        return southPanel;
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
            southControlPanel.add(getGridCountXSlider(), java.awt.BorderLayout.NORTH);
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
            gridCountXSlider.setMinimum(1);
            gridCountXSlider.setMaximum(50);
            gridCountXSlider.setValue(10);
            gridCountXSlider.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent e) {
                    getAdjustGridPanel().setGridCountX(getGridCountXSlider().getValue());
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
            gridCountYSlider.setMaximum(50);
            gridCountYSlider.setValue(10);
            gridCountYSlider.setMinimum(1);
            gridCountYSlider.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent e) {
                    getAdjustGridPanel().setGridCountY(getGridCountYSlider().getValue());
                }
            });
        }
        return gridCountYSlider;
    }

    public static void main(String[] args) throws IOException {
        AdjustGridDialog d = new AdjustGridDialog();
        d.setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
        
        d.setZoneImage(ImageIO.read(new File("c:\\map.jpg")));
        
        d.setVisible(true);
    }
}
