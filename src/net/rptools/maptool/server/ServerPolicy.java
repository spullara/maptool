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
package net.rptools.maptool.server;

import net.rptools.maptool.client.AppPreferences;

public class ServerPolicy {

	private boolean strictTokenMovement;
	private boolean isMovementLocked;
	private boolean playersCanRevealVision;
	private boolean useIndividualViews;
	private boolean restrictedImpersonation;
	private boolean playersReceiveCampaignMacros;
	private boolean useToolTipsForDefaultRollFormat;
	
	public ServerPolicy() {
		// Default tool tip usage for inline rolls to user preferences.
		useToolTipsForDefaultRollFormat = AppPreferences.getUseToolTipForInlineRoll();
	}
	
	/**
	 * Whether token management can be done by everyone or 
	 * only the GM and assigned tokens 
	 * @return
	 */
	public boolean useStrictTokenManagement() {
		return strictTokenMovement;
	}
	
	public void setUseStrictTokenManagement(boolean strict) {
		strictTokenMovement = strict;
	}
	
	public boolean isMovementLocked() {
		return isMovementLocked;
	}
	
	public void setIsMovementLocked(boolean locked) {
		isMovementLocked = locked;
	}
	
	public void setPlayersCanRevealVision(boolean flag) {
		playersCanRevealVision = flag;
	}
	
	public boolean getPlayersCanRevealVision() {
		return playersCanRevealVision;
	}

	public boolean isUseIndividualViews() {
		return useIndividualViews;
	}

	public void setUseIndividualViews(boolean useIndividualViews) {
		this.useIndividualViews = useIndividualViews;
	}

	public boolean isRestrictedImpersonation () {
		return restrictedImpersonation;
	}
	
	public void setRestrictedImpersonation (boolean restrictimp) {
		restrictedImpersonation = restrictimp;
	}
	
	public boolean playersReceiveCampaignMacros () {
		return playersReceiveCampaignMacros;
	}
	
	public void setPlayersReceiveCampaignMacros (boolean flag) {
		playersReceiveCampaignMacros = flag;
	}
	
	/**
	 * Sets if ToolTips should be used instead of extended output for 
	 * [ ] rolls with no formatting option.
	 * @param flag true if tool tips should be used.
	 */
	public void setUseToolTipsForDefaultRollFormat(boolean flag) {
		useToolTipsForDefaultRollFormat = flag;
	}
	
	/**
	 * Gets if ToolTips should be used instead of extended output for 
	 * [ ] rolls with no formatting option.
	 * @returns true if tool tips should be used.
	 */
	public boolean getUseToolTipsForDefaultRollFormat() {
		return useToolTipsForDefaultRollFormat;
	}

}
