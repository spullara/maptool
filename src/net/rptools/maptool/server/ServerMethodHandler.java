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
package net.rptools.maptool.server;

import net.rptools.clientserver.hessian.AbstractMethodHandler;
import net.rptools.maptool.client.MapToolClient;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.Campaign;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.DrawnElement;
import net.rptools.maptool.model.drawing.Pen;
import net.rptools.maptool.server.MapToolServer.COMMANDS;
import static net.rptools.maptool.server.MapToolServer.COMMANDS;
import net.rptools.maptool.util.MD5Key;

/**
 * @author drice
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ServerMethodHandler extends AbstractMethodHandler {
    private final MapToolServer server;
    
    public ServerMethodHandler(MapToolServer server) {
        this.server = server;
    }

    public void handleMethod(String id, String method, Object[] parameters) {
        COMMANDS cmd = Enum.valueOf(COMMANDS.class, method);
        System.out.println("ServerMethodHandler#handleMethod: " + id + " - " + cmd.name());

        Zone zone;
        switch (cmd) {
        case getCampaign:
            server.getConnection().callMethod(id, MapToolClient.COMMANDS.setCampaign.name(), server.getCampaign());
            break;
        case setCampaign:
            Campaign c = (Campaign) parameters[0];
            server.setCampaign(c);
            broadcast(id, MapToolClient.COMMANDS.setCampaign.name(), c);
            break;
        case getZone:
            server.getConnection().callMethod(id, MapToolClient.COMMANDS.putZone.name(), server.getCampaign().getZone((GUID) parameters[0]));
            break;
        case putZone:
            zone = (Zone) parameters[0];
            server.getCampaign().putZone(zone);
            broadcast(id, MapToolClient.COMMANDS.putZone.name(), zone);
            break;
        case removeZone:
            server.getCampaign().removeZone((GUID) parameters[0]);
            break;
        case putAsset:
            server.getCampaign().putAsset((Asset) parameters[0]);
            break;
        case getAsset:
            server.getConnection().callMethod(id, MapToolClient.COMMANDS.putAsset.name(), server.getCampaign().getAsset((MD5Key) parameters[0]));
            break;
        case removeAsset:
            server.getCampaign().removeAsset((GUID) parameters[0]);
            break;
        case putToken:
        	GUID zoneGUID = (GUID) parameters[0];
            zone = server.getCampaign().getZone(zoneGUID);
            Token token = (Token) parameters[1];
            zone.putToken(token);
            
            broadcast(id, MapToolClient.COMMANDS.putToken.name(), zoneGUID, token);
            break;
        case removeToken:
        	zoneGUID = (GUID) parameters[0];
        	GUID tokenGUID = (GUID) parameters[1];

            zone = server.getCampaign().getZone((GUID) parameters[0]);
        	zone.removeToken(tokenGUID);
        	
            server.getConnection().broadcastCallMethod(MapToolClient.COMMANDS.removeToken.name(), parameters);
        	
            break;
        case draw:
        	zoneGUID = (GUID) parameters[0];
        	Pen pen = (Pen) parameters[1];
        	Drawable drawable = (Drawable) parameters[2];
        	
            server.draw((GUID) parameters[0], (Pen) parameters[1], (Drawable) parameters[2]);
            server.getConnection().broadcastCallMethod(MapToolClient.COMMANDS.draw.name(), parameters);
            
            zone = server.getCampaign().getZone(zoneGUID);
            
            zone.addDrawable(new DrawnElement(drawable, pen));
            break;

        case setZoneGridSize:
        	
        	zoneGUID = (GUID) parameters[0];
        	int xOffset = ((Integer) parameters[1]).intValue();
        	int yOffset = ((Integer) parameters[2]).intValue();
        	int size = ((Integer) parameters[3]).intValue();
        	
            zone = server.getCampaign().getZone(zoneGUID);
        	zone.setGridSize(size);
        	zone.setGridOffsetX(xOffset);
        	zone.setGridOffsetY(yOffset);
            
            server.getConnection().broadcastCallMethod(MapToolClient.COMMANDS.setZoneGridSize.name(), parameters);
            break;
        }
        	
    }
    
    private void broadcast(String exclude, String method, Object... parameters) {
        server.getConnection().broadcastCallMethod(new String[] { exclude }, method, parameters);
    }

}
