/*
 * $Id$
 *
 * Copyright (C) 2005, Digital Motorworks LP, a wholly owned subsidiary of ADP.
 * The contents of this file are protected under the copyright laws of the
 * United States of America with all rights reserved. This document is
 * confidential and contains proprietary information. Any unauthorized use or
 * disclosure is expressly prohibited.
 */
package net.rptools.maptool.client;

import net.rptools.clientserver.ActivityListener;

/**
 */
public class ServerConnectionActivityListener implements ActivityListener {

    private int outboundCount;
    private int inboundCount;
    
    /* (non-Javadoc)
     * @see net.rptools.clientserver.ActivityListener#notify(net.rptools.clientserver.ActivityListener.Direction, net.rptools.clientserver.ActivityListener.State, int, int)
     */
    public void notify(Direction direction, State state, int totalTransferBytes, int transferedBytes) {

        System.out.println("TRANSFER: " + direction.name() + " " + state.name() + " total:" + totalTransferBytes + " curr:" + transferedBytes);
    }
    
    private void handleOutboundStarted() {
        
    }
    
    private void handleOutboundEnded() {
        
    }

    private void handleInboundStarted() {
        
    }
    
    private void handleInboundEnded() {
        
    }
    
}
