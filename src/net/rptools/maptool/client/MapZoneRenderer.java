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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import net.rptools.maptool.model.Zone;

public class MapZoneRenderer extends ZoneRenderer {

    private int     width;
    private int     height;
    private boolean sizeInitialized;

    public MapZoneRenderer (Zone zone) {
        super(zone);
    }
    
    public Point getCellAt(int x,int y) {
        
        Point p = super.getCellAt(x, y);
        
        if (p.x < 0 || p.y < 0 || p.x > (width / zone.getGridSize()) || p.y > (height / zone.getGridSize())) {
            p = null;
        }
        
        return null;
    }
    
    public BufferedImage getBackgroundImage() {
        
        BufferedImage image = super.getBackgroundImage();
        if (!sizeInitialized && image != null) {
            
            width = image.getWidth(this);
            height = image.getHeight(this);
        }
        
        return image;
    }
    
    protected void renderBorder(Graphics g) {
        
        Dimension size = getSize();
        
        // Scale
        float scale = scaleArray[scaleIndex];
        int w = (int)(width * scale);
        int h = (int)(height * scale);

        // Border
        if (offsetX > 0) {
            g.setColor(Color.black);
            g.fillRect(0, 0, offsetX, size.height);
        }
        if (offsetY > 0) {
            g.setColor(Color.black);
            g.fillRect(0, 0, size.width, offsetY);
        }
        if (w + offsetX < size.width) {
            g.setColor(Color.black);
            g.fillRect(w + offsetX, 0, size.width, size.height);
        }
        if (h + offsetY < size.height) {
            g.setColor(Color.black);
            g.fillRect(0, h + offsetY, size.width, size.height);
        }
        
        ClientStyle.boardBorder.paintAround((Graphics2D) g, offsetX, offsetY, w, h);
    }
    
    protected void renderBoard(Graphics g) {

        Dimension size = getSize();
        
        // Scale
        float scale = scaleArray[scaleIndex];
        int w = (int)(width * scale);
        int h = (int)(height * scale);

        if (offsetX > size.width - EDGE_LIMIT) {
            offsetX = size.width - EDGE_LIMIT;
        }
        
        if (offsetX + w < EDGE_LIMIT) {
            offsetX = EDGE_LIMIT - w;
        }
        
        if (offsetY > size.height - EDGE_LIMIT) {
            offsetY = size.height - EDGE_LIMIT;
        }
        
        if (offsetY + h < EDGE_LIMIT) {
            offsetY = EDGE_LIMIT - h;
        }
        
        // Map
        g.drawImage(backgroundImage, offsetX, offsetY, w, h, this);
    }
    
    protected void renderGrid(Graphics g) {
        
        float scale = scaleArray[scaleIndex];
        int w = (int)(width * scale);
        int h = (int)(height * scale);

        float gridSize = zone.getGridSize() * scale;

        // Render grid
        if (showGrid) {
            g.setColor(gridColor);

            int x = offsetX + (int) (zone.getGridOffsetX() * scaleArray[scaleIndex]);
            int y = offsetY + (int) (zone.getGridOffsetY() * scaleArray[scaleIndex]);

            for (float row = 0; row < h + gridSize; row += gridSize) {
                
                int theY = Math.min(offsetY + h, Math.max((int)row + y, offsetY));
                int theX = Math.max(x, offsetX);
                
                g.drawLine(theX, theY, theX + w, theY);
            }

            for (float col = 0; col < w + gridSize; col += gridSize) {
                
                int theX = Math.min(offsetX + w, Math.max(x + (int)col, offsetX));
                int theY = Math.max(y, offsetY);

                g.drawLine(theX, theY, theX, theY + h);
            }
        }
        

    }    
}
