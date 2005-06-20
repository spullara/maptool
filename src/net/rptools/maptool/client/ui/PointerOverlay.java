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
package net.rptools.maptool.client.ui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.rptools.common.util.ImageUtil;
import net.rptools.maptool.model.Pointer;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.util.GraphicsUtil;

/**
 */
public class PointerOverlay implements ZoneOverlay {

	private List<PointerPair> pointerList = new ArrayList<PointerPair>();
	private static BufferedImage POINTER_IMAGE;
	
	static {
		try {
		 POINTER_IMAGE = ImageUtil.getImage("net/rptools/maptool/client/image/pointer.png");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
	}
	
	public void paintOverlay(ZoneRenderer renderer, Graphics2D g) {
		
		Zone zone = renderer.getZone();

		for (int i = 0; i < pointerList.size(); i++) {
			PointerPair p = pointerList.get(i);
			if (p.pointer.getZoneGUID().equals(zone.getId())) {
				
				Point point = renderer.convertZoneToScreen(p.pointer.getX(), p.pointer.getY());
				g.drawImage(POINTER_IMAGE, point.x, point.y - POINTER_IMAGE.getHeight(), null);

				GraphicsUtil.drawBoxedString(g, p.player, point.x + POINTER_IMAGE.getWidth() - 5, point.y - POINTER_IMAGE.getHeight()+3);
			}
		}
		
	}

	public void addPointer(String player, Pointer pointer) {
		pointerList.add(new PointerPair(player, pointer));
	}
	
	public void removePointer(String player) {
		
		for (int i = 0; i < pointerList.size(); i++) {
			
			if (pointerList.get(i).player.equals(player)) {
				pointerList.remove(i);
			}
		}
	}
	
	private class PointerPair {
		Pointer pointer;
		String player;
		
		PointerPair (String player, Pointer pointer) {
			this.pointer = pointer;
			this.player = player;
		}
	}
}
