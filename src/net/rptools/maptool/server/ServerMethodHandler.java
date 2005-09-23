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

import java.awt.geom.Area;

import net.rptools.clientserver.hessian.AbstractMethodHandler;
import net.rptools.maptool.client.ClientCommand;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Campaign;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Label;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.DrawnElement;
import net.rptools.maptool.model.drawing.Pen;
import net.rptools.maptool.util.MD5Key;

/**
 * @author drice
 */
public class ServerMethodHandler extends AbstractMethodHandler {
    private final MapToolServer server;
    
    public ServerMethodHandler(MapToolServer server) {
        this.server = server;
    }

    public void handleMethod(String id, String method, Object[] parameters) {
        ServerCommand.COMMAND cmd = Enum.valueOf(ServerCommand.COMMAND.class, method);
        //System.out.println("ServerMethodHandler#handleMethod: " + id + " - " + cmd.name());

        Zone zone;
    	GUID zoneGUID;

    	switch (cmd) {
        case setZoneHasFoW:
        	
        	zoneGUID = (GUID) parameters[0];
        	boolean hasFog = (Boolean) parameters[1];
        	
            zone = server.getCampaign().getZone(zoneGUID);
            zone.setHasFog(hasFog);

            server.getConnection().broadcastCallMethod(ClientCommand.COMMAND.setZoneHasFoW.name(), parameters);
        	break;
        	
        case exposeFoW:
        	
        	zoneGUID = (GUID) parameters[0];
            Area area = (Area) parameters[1];

            zone = server.getCampaign().getZone(zoneGUID);
            zone.exposeArea(area);

        	server.getConnection().broadcastCallMethod(ClientCommand.COMMAND.exposeFoW.name(), parameters);
        	break;
        	
        case hideFoW:
        	
        	zoneGUID = (GUID) parameters[0];
            area = (Area) parameters[1];

            zone = server.getCampaign().getZone(zoneGUID);
            zone.hideArea(area);

        	server.getConnection().broadcastCallMethod(ClientCommand.COMMAND.hideFoW.name(), parameters);
        	break;
        
        case enforceZoneView:
        	
        	server.getConnection().broadcastCallMethod(ClientCommand.COMMAND.enforceZoneView.name(), parameters);
        	break;
        case setCampaign:
            Campaign c = (Campaign) parameters[0];
            server.setCampaign(c);
            broadcast(id, ClientCommand.COMMAND.setCampaign.name(), c);
            break;
        case getZone:
            server.getConnection().callMethod(id, ClientCommand.COMMAND.putZone.name(), server.getCampaign().getZone((GUID) parameters[0]));
            break;
        case putZone:
            zone = (Zone) parameters[0];
            server.getCampaign().putZone(zone);
            broadcast(id, ClientCommand.COMMAND.putZone.name(), zone);
            break;
        case removeZone:
            server.getCampaign().removeZone((GUID) parameters[0]);
            // TODO: broadcast to clients
            break;
        case putAsset:
            AssetManager.putAsset((Asset) parameters[0]);
            break;
        case getAsset:
            server.getConnection().callMethod(id, ClientCommand.COMMAND.putAsset.name(), AssetManager.getAsset((MD5Key) parameters[0]));
            break;
        case removeAsset:
            AssetManager.removeAsset((MD5Key) parameters[0]);
            break;
        case putToken:
            zoneGUID = (GUID) parameters[0];
            zone = server.getCampaign().getZone(zoneGUID);
            Token token = (Token) parameters[1];
            zone.putToken(token);
            
            broadcast(id, ClientCommand.COMMAND.putToken.name(), zoneGUID, token);
            break;
        case removeToken:
            zoneGUID = (GUID) parameters[0];
            GUID tokenGUID = (GUID) parameters[1];

            zone = server.getCampaign().getZone((GUID) parameters[0]);
            zone.removeToken(tokenGUID);
            
            server.getConnection().broadcastCallMethod(ClientCommand.COMMAND.removeToken.name(), parameters);
            break;
        case putLabel:
            zoneGUID = (GUID) parameters[0];
            zone = server.getCampaign().getZone(zoneGUID);
            Label label = (Label) parameters[1];
            zone.putLabel(label);
            
            broadcast(id, ClientCommand.COMMAND.putLabel.name(), zoneGUID, label);
            break;
        case removeLabel:
            zoneGUID = (GUID) parameters[0];
            GUID labelGUID = (GUID) parameters[1];

            zone = server.getCampaign().getZone((GUID) parameters[0]);
            zone.removeLabel(labelGUID);
            
            server.getConnection().broadcastCallMethod(ClientCommand.COMMAND.removeLabel.name(), parameters);
            break;
        case draw:
        	zoneGUID = (GUID) parameters[0];
        	Pen pen = (Pen) parameters[1];
        	Drawable drawable = (Drawable) parameters[2];
        	
            server.getConnection().broadcastCallMethod(ClientCommand.COMMAND.draw.name(), parameters);
            
            zone = server.getCampaign().getZone(zoneGUID);
            
            zone.addDrawable(new DrawnElement(drawable, pen));
            break;

        case undoDraw:
          zoneGUID = (GUID) parameters[0];
          GUID drawableId = (GUID)parameters[1];
          server.getConnection().broadcastCallMethod(ClientCommand.COMMAND.undoDraw.name(), zoneGUID, drawableId);
          zone = server.getCampaign().getZone(zoneGUID);
          zone.removeDrawable(drawableId);
          break;
          
        case setZoneVisibility:
        	
        	zoneGUID = (GUID) parameters[0];
        	boolean visible = (Boolean) parameters[1];
        	server.getCampaign().getZone(zoneGUID).setVisible(visible);
        	
        	server.getConnection().broadcastCallMethod(ClientCommand.COMMAND.setZoneVisibility.name(), parameters);
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
            
            server.getConnection().broadcastCallMethod(ClientCommand.COMMAND.setZoneGridSize.name(), parameters);
            break;
        case message:
            server.getConnection().broadcastCallMethod(ClientCommand.COMMAND.message.name(), parameters);
            break;
            
        case showPointer:
            server.getConnection().broadcastCallMethod(ClientCommand.COMMAND.showPointer.name(), parameters);
        	break;
        	
        case hidePointer:
            server.getConnection().broadcastCallMethod(ClientCommand.COMMAND.hidePointer.name(), parameters);
        	break;
        case startTokenMove:
            broadcast(id, ClientCommand.COMMAND.startTokenMove.name(), parameters);
        	break;
        case stopTokenMove:
            broadcast(id, ClientCommand.COMMAND.stopTokenMove.name(), parameters);
        	break;
        case updateTokenMove:
            broadcast(id, ClientCommand.COMMAND.updateTokenMove.name(), parameters);
            break;
        case addTokenMoveWaypoint:
            broadcast(id, ClientCommand.COMMAND.addTokenMoveWaypoint.name(), parameters);
            break;
        }
        	
    }
    
    private void broadcast(String exclude, String method, Object... parameters) {
        server.getConnection().broadcastCallMethod(new String[] { exclude }, method, parameters);
    }

}
