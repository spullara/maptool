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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import net.rptools.maptool.model.Campaign;
import net.rptools.maptool.util.PersistenceUtil;

/**
 * @author tylere
 * 
 * Attempts to recover campaigns when the application crashes.
 */
public class AutoSaveManager implements ActionListener {
	
	private Timer autoSaveTimer;
	public static final File AUTOSAVE_FILE = new File(AppUtil.getAppHome("autosave"), "AutoSave.cmpgn");
	
	/**
	 * Begins the autosave feature and sets it's interval
	 */
	public void setInterval(int intervalInMinutes)
	{
		
		//convert to milliseconds
		int delay = intervalInMinutes*60*1000;
		
		if (autoSaveTimer == null) {
			if(intervalInMinutes <= 0) {
				return;
			} else {
				autoSaveTimer = new Timer(delay, this);
				autoSaveTimer.start();
			}		
		} else {
			if(intervalInMinutes <= 0) {
				autoSaveTimer.stop();
				autoSaveTimer = null;
			} else {
				autoSaveTimer.setDelay(delay);
				autoSaveTimer.restart();
			}	
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		Campaign campaign = MapTool.getCampaign();

		try {
			MapTool.getFrame().setStatusMessage("Autosaving campaign ...");
			PersistenceUtil.saveCampaign(campaign, AUTOSAVE_FILE);
			MapTool.getFrame().setStatusMessage("Autosave complete");
		} catch (IOException ioe) {
			MapTool.showError("Autosave failed: " + ioe);
		}
	}
	
	/**
	 * Removes any autosaved files
	 */
	public void purge() {

		if( AUTOSAVE_FILE.exists() ) {
			AUTOSAVE_FILE.delete();
		}	
	}
	
	/**
	 * Removes the campaignFile if it's from Autosave, forcing to save as new
	 */
	public void tidy() {
		if( AppState.getCampaignFile() == AUTOSAVE_FILE ) {
			AppState.setCampaignFile(null);
		}
		purge();
	}
	
	/**
	 * Check to see if autosave recovery is necessary
	 */
	public void check() {
		if( AUTOSAVE_FILE.exists() ) {
			if( JOptionPane.showConfirmDialog(
				MapTool.getFrame(), 
					"An autosave file exists, would you like to retrieve it?", 
					"Autosave Recovery", 
					JOptionPane.OK_OPTION) == JOptionPane.OK_OPTION) {
					
				AppActions.loadCampaign(AUTOSAVE_FILE);
			}
		}	
	}

}
