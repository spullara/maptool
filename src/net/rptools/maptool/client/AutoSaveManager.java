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

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import com.caucho.hessian.io.HessianOutput;

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

	private boolean working;
	
	public void start() {
		restart();
	}
	
	/**
	 */
	public void restart()
	{
		int interval = AppPreferences.getAutoSaveIncrement();
		
		//convert to milliseconds
		int delay = interval*60*1000;
		
		if (autoSaveTimer == null) {
			if(interval <= 0) {
				return;
			} else {
				autoSaveTimer = new Timer(delay, this);
				autoSaveTimer.start();
			}		
		} else {
			if(interval <= 0) {
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
		if (working) {
			return;
		}
		
		// Don't autosave if we don't "own" the campaign
		if (!MapTool.isHostingServer() && !MapTool.isPersonalServer()) {
			return;
		}

		MapTool.getFrame().setStatusMessage("Autosaving campaign ...");
		
		// This occurs on the event dispatch thread, so it's ok to mess with the models
		// We need to clone the campaign so that we can save in the background, but
		// not have concurrency issues with the original model
		// NOTE: This is a cheesy way to clone the campaign, but it makes it so that I
		// don't have to keep all the various models' clone method updated on each change 
		working = true;
		long start = System.currentTimeMillis();
		final Campaign campaign = new Campaign(MapTool.getCampaign());
		//System.out.println("Time: " + (System.currentTimeMillis() - start));

		// Now that we have a copy of the model, save that one
		// TODO: Replace this with a swing worker
		new Thread(new Runnable(){
			public void run() {
				try {
					PersistenceUtil.saveCampaign(campaign, AUTOSAVE_FILE);
					MapTool.getFrame().setStatusMessage("Autosave complete");
				} catch (IOException ioe) {
					MapTool.showError("Autosave failed: " + ioe);
				}
				
				working = false;
			}
		}).start();
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
