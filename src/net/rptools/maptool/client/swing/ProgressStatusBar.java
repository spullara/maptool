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

import javax.swing.JFrame;
import javax.swing.JProgressBar;

/**
 */
public class ProgressStatusBar extends JProgressBar {

    private static final Dimension minSize = new Dimension(75, 10);
    
    int indeterminateCount = 0;
    int determinateCount = 0;
    int totalWork = 0;
    int currentWork = 0;
    
    public ProgressStatusBar() {
        setMinimum(0);
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
    
    public void startIndeterminate() {
        indeterminateCount ++;
        setIndeterminate(true);
    }
    
    public void endIndeterminate() {
        indeterminateCount --;
        if (indeterminateCount < 1) {
            setIndeterminate(false);
            
            indeterminateCount = 0;
        }
    }
    
    public void startDeterminate(int totalWork) {
        determinateCount ++;
        this.totalWork += totalWork;
        
        setMaximum(this.totalWork);
    }
    
    public void updateDeterminateProgress(int additionalWorkCompleted) {
        currentWork += additionalWorkCompleted;
        setValue(currentWork);
    }
    
    public void endDeterminate() {
        determinateCount --;
        if (determinateCount == 0) {
            totalWork = 0;
            currentWork = 0;
            
            setMaximum(0);
            setValue(0);
        }
    }
    
}
