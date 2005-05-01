/*
 * Created on Mar 8, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.rptools.maptool.client.tool.drawing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import net.rptools.maptool.client.ZoneRenderer;
import net.rptools.maptool.client.tool.ToolHelper;
import net.rptools.maptool.model.drawing.Pen;

/**
 * @author trevor
 */
public class OvalFillTool extends OvalTool {

	public OvalFillTool() {
	    try {
	        setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("net/rptools/maptool/client/image/Tool_Draw_Circle_Fill.gif"))));
	    } catch (IOException ioe) {
	        ioe.printStackTrace();
	    }
	}

	// TODO: Consolidate this better with the oval tool
    public void paintOverlay(ZoneRenderer renderer, Graphics2D g) {
        if (oval != null) {
        	Pen pen = getPen();
        	
            if (pen.isEraser()) {
                pen = new Pen(pen);
                pen.setEraser(false);
                pen.setColor(Color.white.getRGB());
                pen.setBackgroundColor(Color.white.getRGB());
            }

            oval.draw(g, pen, 0, 0);
            ToolHelper.drawBoxedMeasurement(renderer, g, oval.getStartPoint(), oval.getEndPoint(), false);
        }
    }
	
    /* (non-Javadoc)
	 * @see net.rptools.maptool.client.tool.drawing.OvalTool#getPen()
	 */
	protected Pen getPen() {
		
		Pen pen = super.getPen();
		pen.setBackgroundMode(Pen.MODE_SOLID);
		
		return pen;
	}
}
