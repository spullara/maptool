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
import java.util.Set;

import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.Campaign;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Label;
import net.rptools.maptool.model.Pointer;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.Pen;
import net.rptools.maptool.util.MD5Key;

public interface ServerCommand {

    public static enum COMMAND {
        setCampaign, 
        getZone, 
        putZone, 
        removeZone, 
        putAsset, 
        getAsset,
        removeAsset, 
        putToken, 
        removeToken, 
        draw,
        setZoneGridSize,
        message,
        undoDraw,
        showPointer,
        hidePointer,
        startTokenMove,
        stopTokenMove,
        toggleTokenMoveWaypoint,
        updateTokenMove,
        setZoneVisibility,
        enforceZoneView,
        setZoneHasFoW,
        exposeFoW,
        hideFoW,
        putLabel,
        removeLabel,
        sendTokensToBack,
        bringTokensToFront
    };

    public void setZoneHasFoW(GUID zoneGUID, boolean hasFog);
    public void exposeFoW(GUID zoneGUID, Area area);
    public void hideFoW(GUID zoneGUID, Area area);
    public void enforceZoneView(GUID zoneGUID, int x, int y, int zoomIndex);
    public void setCampaign(Campaign campaign);
    public void getZone(GUID zoneGUID);
    public void putZone(Zone zone);
    public void removeZone(GUID zoneGUID);
    public void setZoneVisibility(GUID zoneGUID, boolean visible);
    public void putAsset(Asset asset);
    public void getAsset(MD5Key assetID);
    public void removeAsset(MD5Key assetID);
    public void putToken(GUID zoneGUID, Token token);
    public void removeToken(GUID zoneGUID, GUID tokenGUID);
    public void putLabel(GUID zoneGUID, Label label);
    public void removeLabel(GUID zoneGUID, GUID labelGUID);
    public void draw(GUID zoneGUID, Pen pen, Drawable drawable);
    public void undoDraw(GUID zoneGUID, GUID drawableGUID);
    public void setZoneGridSize(GUID zoneGUID, int xOffset, int yOffset, int size);
    public void message(String message);
    public void showPointer(String player, Pointer pointer);
    public void hidePointer(String player);
    public void startTokenMove(String playerId, GUID zoneGUID, GUID tokenGUID, Set<GUID> tokenList);
    public void updateTokenMove(GUID zoneGUID, GUID tokenGUID, int x, int y);
    public void stopTokenMove(GUID zoneGUID, GUID tokenGUID);
    public void toggleTokenMoveWaypoint(GUID zoneGUID, GUID tokenGUID, int x, int y);
    public void sendTokensToBack(GUID zoneGUID, Set<GUID> tokenSet);
    public void bringTokensToFront(GUID zoneGUID, Set<GUID> tokenSet);
}
