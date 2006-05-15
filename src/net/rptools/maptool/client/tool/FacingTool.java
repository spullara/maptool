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
package net.rptools.maptool.client.tool;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;

/**
 */
public class FacingTool extends DefaultTool {

	private double facing;
	
	public FacingTool () {
		// Non tool-bar tool ... atm
    }
    
    @Override
    public String getTooltip() {
        return "Set the token facing";
    }
    
    @Override
    public String getInstructions() {
    	return "tool.facing.instructions";
    }
    
    @Override
    protected void installKeystrokes(Map<KeyStroke, Action> actionMap) {
    	super.installKeystrokes(actionMap);
    	
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), new AbstractAction() {
			public void actionPerformed(ActionEvent e) {

				if (MapTool.confirm("Are you sure you want to delete the facing of the selected tokens ?")) {
					for (GUID tokenGUID : renderer.getSelectedTokenSet()) {
						Token token = renderer.getZone().getToken(tokenGUID);
						if (token == null) {
							continue;
						}
						
						token.setFacing(null);
					}
					
					// Go back to the pointer tool
					resetTool();
				}
			}
		});
    }
    
    ////
    // MOUSE
    @Override
    public void mouseMoved(MouseEvent e) {
    	super.mouseMoved(e);
    	
    	
    }
    
    @Override
    public void mousePressed(MouseEvent e) {

    	Integer facing = (int)Math.toDegrees(this.facing);

		for (GUID tokenGUID : renderer.getSelectedTokenSet()) {
			Token token = renderer.getZone().getToken(tokenGUID);
			if (token == null) {
				continue;
			}
			
			token.setFacing(facing);
		}
		
		// Go back to the pointer tool
		resetTool();
    	
    }
}
