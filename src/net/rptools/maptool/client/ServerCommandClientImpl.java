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

import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.Campaign;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Pointer;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.Pen;
import net.rptools.maptool.server.ServerCommand;
import net.rptools.maptool.util.MD5Key;

public class ServerCommandClientImpl implements ServerCommand {

    private TimedEventQueue movementUpdateQueue = new TimedEventQueue(500);
	
	public ServerCommandClientImpl() {
		movementUpdateQueue.start();
	}
    
    public void setCampaign(Campaign campaign) {
        makeServerCall(COMMAND.setCampaign, campaign);
    }

    public void getZone(GUID zoneGUID) {
        makeServerCall(COMMAND.getZone, zoneGUID);
    }

    public void putZone(Zone zone) {
        makeServerCall(COMMAND.putZone, zone);
    }

    public void removeZone(GUID zoneGUID) {
        makeServerCall(COMMAND.removeZone, zoneGUID);
    }

    public void putAsset(Asset asset) {
        makeServerCall(COMMAND.putAsset, asset);
    }

    public void getAsset(MD5Key assetID) {
        makeServerCall(COMMAND.getAsset, assetID);
    }

    public void removeAsset(MD5Key assetID) {
        makeServerCall(COMMAND.removeAsset, assetID);
    }

    public void putToken(GUID zoneGUID, Token token) {
        makeServerCall(COMMAND.putToken, zoneGUID, token);
    }

    public void removeToken(GUID zoneGUID, GUID tokenGUID) {
        makeServerCall(COMMAND.removeToken, zoneGUID, tokenGUID);
    }

    public void draw(GUID zoneGUID, Pen pen, Drawable drawable) {
        makeServerCall(COMMAND.draw, zoneGUID, pen, drawable);
    }

    public void undoDraw(GUID zoneGUID, GUID drawableGUID) {
        makeServerCall(COMMAND.undoDraw, zoneGUID, drawableGUID);
    }

    public void setZoneGridSize(GUID zoneGUID, int xOffset, int yOffset, int size) {
        makeServerCall(COMMAND.setZoneGridSize, zoneGUID, xOffset, yOffset, size);
    }

    public void message(String message) {
    	if (!MapTool.isConnected()) {
    		MapTool.addMessage("Not connected.");
    	}
        makeServerCall(COMMAND.message, message);
    }

	public void showPointer(String player, Pointer pointer) {
		makeServerCall(COMMAND.showPointer, player, pointer);
	}
	
	public void hidePointer(String player) {
		makeServerCall(COMMAND.hidePointer, player);
	}

	public void startTokenMove(String playerId, GUID zoneGUID, GUID tokenGUID, Set<GUID> tokenList) {
		makeServerCall(COMMAND.startTokenMove, playerId, zoneGUID, tokenGUID, tokenList);
	}

	public void stopTokenMove(GUID zoneGUID, GUID tokenGUID) {
        movementUpdateQueue.flush();
		makeServerCall(COMMAND.stopTokenMove, zoneGUID, tokenGUID);
	}
	
	public void updateTokenMove(GUID zoneGUID, GUID tokenGUID, int x, int y) {
		movementUpdateQueue.enqueue(COMMAND.updateTokenMove, zoneGUID, tokenGUID, x, y);
	}
	
	private static void makeServerCall(ServerCommand.COMMAND command, Object... params) {
        if (!MapTool.isConnected()) {return;}
        
        MapTool.getConnection().callMethod(command.name(), params);
    }
    
    /**
     * Some events become obsolete very quickly, such as dragging a token
     * around.  This queue always has exactly one element, the more current
     * version of the event.  The event is then dispatched at some time interval.
     * If a new event arrives before the time interval elapses, it is replaced.
     * In this way, only the most current version of the event is released.
     */
    private static class TimedEventQueue extends Thread {
        
        ServerCommand.COMMAND command;
        Object[] params;
        
        long delay;

		Object sleepSemaphore = new Object();
		
        public TimedEventQueue(long millidelay) {
            delay = millidelay;
        }
        
        public synchronized void enqueue (ServerCommand.COMMAND command, Object... params) {
            
            this.command = command;
            this.params = params;
        }

        public synchronized void flush() {
            
            if (command != null) {
                makeServerCall(command, params);
            }
            command = null;
            params = null;
        }
        
        public void run() {
            
            while(true) {
             
                flush();
                synchronized (sleepSemaphore) {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        // nothing to do
                    }
                }
            }
        }
    }
}
