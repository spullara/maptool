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
package net.rptools.maptool.client;

import net.rptools.clientserver.hessian.AbstractMethodHandler;
import net.rptools.maptool.client.MapToolClient.COMMANDS;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Campaign;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Player;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.DrawnElement;
import net.rptools.maptool.model.drawing.Pen;


/**
 * @author drice
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ClientMethodHandler extends AbstractMethodHandler {
    private final MapToolClient client;
    
    public ClientMethodHandler(MapToolClient client) {
        this.client = client;
    }

    public void handleMethod(String id, String method, Object[] parameters) {
        COMMANDS cmd = Enum.valueOf(COMMANDS.class, method);
        //System.out.println("ClientMethodHandler#handleMethod: " + cmd.name());
        
        switch (cmd) {
        case setCampaign:
        	Campaign campaign = (Campaign) parameters[0];
        	MapToolClient.setCampaign(campaign);
            break;
        case putZone:
        	Zone zone = (Zone) parameters[0];
        	MapToolClient.getCampaign().putZone(zone);
        	MapToolClient.setCurrentZoneRenderer(ZoneRendererFactory.newRenderer(zone));
            break;
        case removeZone:
            break;
        case putAsset:
            AssetManager.putAsset((Asset) parameters[0]);
            MapToolClient.getInstance().repaint();
            break;
        case removeAsset:
            break;
        case putToken:
        	GUID zoneGUID = (GUID) parameters[0];
        	zone = MapToolClient.getCampaign().getZone(zoneGUID);
        	Token token = (Token) parameters[1];
        	
        	zone.putToken(token);
        	
        	// TODO: be more scientific about this
        	MapToolClient.getInstance().repaint();
            break;
        case removeToken:
        	zoneGUID = (GUID) parameters[0];
        	zone = MapToolClient.getCampaign().getZone(zoneGUID);
        	GUID tokenGUID = (GUID) parameters[1];

        	zone.removeToken(tokenGUID);
        	
        	MapToolClient.getInstance().repaint();
        	break;
        case draw:
        	
        	zoneGUID = (GUID) parameters[0];
            Pen pen = (Pen) parameters[1];
        	Drawable drawable = (Drawable) parameters[2];

        	zone = MapToolClient.getCampaign().getZone(zoneGUID);
        	
        	zone.addDrawable(new DrawnElement(drawable, pen));
        	
        	MapToolClient.getInstance().repaint();
            break;
//        case setZone:
//        	MapToolClient.setBackgroundPanel(new ZoneRenderer((Zone)parameters[0]));
//           break;
        case setZoneGridSize:
        	
        	zoneGUID = (GUID) parameters[0];
        	int xOffset = ((Integer) parameters[1]).intValue();
        	int yOffset = ((Integer) parameters[2]).intValue();
        	int size = ((Integer) parameters[3]).intValue();
        	
        	zone = MapToolClient.getCampaign().getZone(zoneGUID);
        	zone.setGridSize(size);
        	zone.setGridOffsetX(xOffset);
        	zone.setGridOffsetY(yOffset);
        	
        	MapToolClient.getInstance().repaint();
        	break;

        case playerConnected:
        	
        	MapToolClient.getInstance().addPlayer((Player) parameters[0]);
        	MapToolClient.getInstance().repaint();
        	break;

        case playerDisconnected:
        	
        	MapToolClient.getInstance().removePlayer((Player) parameters[0]);
        	MapToolClient.getInstance().repaint();
        	break;
        	
        	
        }
        
        	
    }

}
