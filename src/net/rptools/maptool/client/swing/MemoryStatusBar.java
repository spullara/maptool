/*
 * $Id$
 *
 * Copyright (C) 2005, Digital Motorworks LP, a wholly owned subsidiary of ADP.
 * The contents of this file are protected under the copyright laws of the
 * United States of America with all rights reserved. This document is
 * confidential and contains proprietary information. Any unauthorized use or
 * disclosure is expressly prohibited.
 */
package net.rptools.maptool.client.swing;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

/**
 */
public class MemoryStatusBar extends JProgressBar {

    private static final Dimension minSize = new Dimension(75, 10);
    
    private static final DecimalFormat format = new DecimalFormat("#,##0.#");
    
    public MemoryStatusBar() {
        setMinimum(0);
        setStringPainted(true);

        new Thread() {
        	public void run() {
        		
        		while (true) {
        			
        			update();
        			try {
        				Thread.sleep(1000);
        			} catch (InterruptedException ie) {
        				break;
        			}
        		}
        	}
        }.start();
        
        addMouseListener(new MouseAdapter(){
        	
        	public void mouseClicked(java.awt.event.MouseEvent e) {
        		
        		System.gc();
        		update();
        	}
        });
    }
    
    /* (non-Javadoc)
     * @see javax.swing.JComponent#getMinimumSize()
     */
    public Dimension getMinimumSize() {
        return minSize;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.JComponent#getPreferredSize()
     */
    public Dimension getPreferredSize() {
        return getMinimumSize();
    }
    
    private void update() {
    	
    	double totalMegs = Runtime.getRuntime().totalMemory()/(1024 * 1024);
    	double freeMegs = Runtime.getRuntime().freeMemory()/(1024 * 1024);
    	setMaximum((int)totalMegs);
    	setValue((int)(totalMegs - freeMegs));
    	setString(format.format(totalMegs - freeMegs) + "M/" + format.format(totalMegs) + "M");
    }
}
