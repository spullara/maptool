/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package net.rptools.maptool.client;

public class ClientCommand {

    public static enum COMMAND { 
    	startAssetTransfer,
    	updateAssetTransfer,
    	bootPlayer,
        setCampaign, 
        putZone, 
        removeZone, 
        putAsset, 
        getAsset,
        removeAsset, 
        putToken, 
        removeToken, 
        draw,
        clearAllDrawings,
        setZoneGridSize,
        setZoneVisibility,
        playerConnected,
        playerDisconnected,
        message,
        undoDraw,
        showPointer,
        hidePointer,
        movePointer,
        startTokenMove,
        stopTokenMove,
        toggleTokenMoveWaypoint,
        updateTokenMove,
        enforceZoneView,
        setZoneHasFoW,
        exposeFoW,
        hideFoW,
        setFoW,
        putLabel,
        removeLabel,
        enforceZone,
        setServerPolicy,
        addTopology,
        removeTopology,
        renameZone,
        updateCampaign,
        updateInitiative,
        updateTokenInitiative,
        setUseVision,
        updateCampaignMacros,
        setTokenLocation, // NOTE: This is to support third party token placement and shouldn't be depended on for general purpose token movement
        setLiveTypingLabel, // Experimental chat notification
        enforceNotification, // enforces notification of typing in the chat window
        exposePCArea
    };
}
