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

import java.util.Set;

import net.rptools.clientserver.hessian.AbstractMethodHandler;
import static net.rptools.maptool.client.ClientCommand.COMMAND.draw;
import static net.rptools.maptool.client.ClientCommand.COMMAND.hidePointer;
import static net.rptools.maptool.client.ClientCommand.COMMAND.message;
import static net.rptools.maptool.client.ClientCommand.COMMAND.playerConnected;
import static net.rptools.maptool.client.ClientCommand.COMMAND.playerDisconnected;
import static net.rptools.maptool.client.ClientCommand.COMMAND.putAsset;
import static net.rptools.maptool.client.ClientCommand.COMMAND.putToken;
import static net.rptools.maptool.client.ClientCommand.COMMAND.putZone;
import static net.rptools.maptool.client.ClientCommand.COMMAND.removeAsset;
import static net.rptools.maptool.client.ClientCommand.COMMAND.removeToken;
import static net.rptools.maptool.client.ClientCommand.COMMAND.removeZone;
import static net.rptools.maptool.client.ClientCommand.COMMAND.setCampaign;
import static net.rptools.maptool.client.ClientCommand.COMMAND.setZoneGridSize;
import static net.rptools.maptool.client.ClientCommand.COMMAND.showPointer;
import static net.rptools.maptool.client.ClientCommand.COMMAND.undoDraw;
import net.rptools.maptool.client.ui.ZoneRenderer;
import net.rptools.maptool.client.ui.ZoneRendererFactory;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Campaign;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Player;
import net.rptools.maptool.model.Pointer;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.DrawnElement;
import net.rptools.maptool.model.drawing.Pen;
import static net.rptools.maptool.server.ServerCommand.COMMAND.startTokenMove;
import static net.rptools.maptool.server.ServerCommand.COMMAND.stopTokenMove;


/**
 * @author drice
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ClientMethodHandler extends AbstractMethodHandler {
    
    public ClientMethodHandler() {
    }

    public void handleMethod(String id, String method, Object[] parameters) {
        ClientCommand.COMMAND cmd = Enum.valueOf(ClientCommand.COMMAND.class, method);
        //System.out.println("ClientMethodHandler#handleMethod: " + cmd.name());
        
        switch (cmd) {
        case setCampaign:
        	Campaign campaign = (Campaign) parameters[0];
        	MapTool.setCampaign(campaign);
            break;
        case putZone:
        	Zone zone = (Zone) parameters[0];
        	MapTool.getCampaign().putZone(zone);
        	MapTool.getFrame().setCurrentZoneRenderer(ZoneRendererFactory.newRenderer(zone));
            break;
        case removeZone:
            break;
        case putAsset:
            AssetManager.putAsset((Asset) parameters[0]);
            MapTool.getFrame().repaint();
            break;
        case removeAsset:
            break;
        case putToken:
        	GUID zoneGUID = (GUID) parameters[0];
        	zone = MapTool.getCampaign().getZone(zoneGUID);
        	Token token = (Token) parameters[1];
        	
        	zone.putToken(token);
        	
        	MapTool.getFrame().repaint();
            break;
        case removeToken:
        	zoneGUID = (GUID) parameters[0];
        	zone = MapTool.getCampaign().getZone(zoneGUID);
        	GUID tokenGUID = (GUID) parameters[1];

        	zone.removeToken(tokenGUID);
        	
        	MapTool.getFrame().repaint();
        	break;
        case draw:
        	
        	zoneGUID = (GUID) parameters[0];
            Pen pen = (Pen) parameters[1];
        	Drawable drawable = (Drawable) parameters[2];

        	zone = MapTool.getCampaign().getZone(zoneGUID);
        	
        	zone.addDrawable(new DrawnElement(drawable, pen));
        	
        	MapTool.getFrame().repaint();
            break;
        
        case undoDraw:
          zoneGUID = (GUID) parameters[0];
          GUID drawableId = (GUID)parameters[1];
          zone = MapTool.getCampaign().getZone(zoneGUID);
          zone.removeDrawable(drawableId);

		  if (MapTool.getFrame().getCurrentZoneRenderer().getZone().getId().equals(zoneGUID) && zoneGUID != null) {
			  MapTool.getFrame().getCurrentZoneRenderer().repaint();
		  }

		  break;
          
        case setZoneGridSize:
        	
        	zoneGUID = (GUID) parameters[0];
        	int xOffset = ((Integer) parameters[1]).intValue();
        	int yOffset = ((Integer) parameters[2]).intValue();
        	int size = ((Integer) parameters[3]).intValue();
        	
        	zone = MapTool.getCampaign().getZone(zoneGUID);
        	zone.setGridSize(size);
        	zone.setGridOffsetX(xOffset);
        	zone.setGridOffsetY(yOffset);
        	
        	MapTool.getFrame().repaint();
        	break;

        case playerConnected:
        	
        	MapTool.addPlayer((Player) parameters[0]);
        	MapTool.getFrame().repaint();
        	break;

        case playerDisconnected:
        	
        	MapTool.removePlayer((Player) parameters[0]);
        	MapTool.getFrame().repaint();
        	break;
            
        case message:
            String message = (String) parameters[0];
            MapTool.addMessage(message);
        	break;
            
        case showPointer:
        	MapTool.getFrame().getPointerOverlay().addPointer((String) parameters[0], (Pointer) parameters[1]);
        	MapTool.getFrame().repaint();
        	break;
        	
        case hidePointer:
        	MapTool.getFrame().getPointerOverlay().removePointer((String) parameters[0]);
        	MapTool.getFrame().repaint();
        	break;
        	
        case startTokenMove:
			
			String playerId = (String) parameters[0];
			zoneGUID = (GUID) parameters[1];
			GUID keyToken = (GUID) parameters[2];
			Set<GUID> selectedSet = (Set<GUID>) parameters[3];
			
			ZoneRenderer renderer = MapTool.getFrame().getZoneRenderer(zoneGUID);
			renderer.addMoveSelectionSet(playerId, keyToken, selectedSet, true);
			
        	break;
        case stopTokenMove:

			zoneGUID = (GUID) parameters[0];
			keyToken = (GUID) parameters[1];
			
			renderer = MapTool.getFrame().getZoneRenderer(zoneGUID);
			renderer.removeMoveSelectionSet(keyToken);

			break;
        case updateTokenMove:

			zoneGUID = (GUID) parameters[0];
			keyToken = (GUID) parameters[1];
			
			int x = ((Integer) parameters[2]).intValue();
			int y = ((Integer) parameters[3]).intValue();
			
			
			renderer = MapTool.getFrame().getZoneRenderer(zoneGUID);
			renderer.updateMoveSelectionSet(keyToken, new ZonePoint(x, y));

			break;
        }
        
        	
    }

}
