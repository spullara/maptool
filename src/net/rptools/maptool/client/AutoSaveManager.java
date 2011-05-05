/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.rptools.maptool.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.Timer;

import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.Campaign;
import net.rptools.maptool.util.PersistenceUtil;

import org.apache.log4j.Logger;

/**
 * @author tylere
 * 
 *         Attempts to recover campaigns when the application crashes.
 */
public class AutoSaveManager implements ActionListener {
	private static final Logger log = Logger.getLogger(AutoSaveManager.class);
	private Timer autoSaveTimer;
	public static final File AUTOSAVE_FILE = new File(AppUtil.getAppHome("autosave"), "AutoSave" + AppConstants.CAMPAIGN_FILE_EXTENSION); //$NON-NLS-1$

	public void start() {
		restart();
	}

	/**
	 * Queries the auto-save increment from {@link AppPreferences} and starts a new timer.
	 */
	public void restart() {
		int interval = AppPreferences.getAutoSaveIncrement();

		//convert to milliseconds
		int delay = interval * 60 * 1000;
		if (log.isDebugEnabled())
			log.debug("Starting autosave manager; interval in seconds is " + (interval * 60)); //$NON-NLS-1$

		if (autoSaveTimer == null) {
			if (interval <= 0) { // auto-save is turned with <= 0
				return;
			} else {
				autoSaveTimer = new Timer(delay, this);
				autoSaveTimer.start(); // Create a new Timer and start it running...
			}
		} else {
			if (interval <= 0) {
				autoSaveTimer.stop(); // auto-save is off; stop the Timer first
				autoSaveTimer = null;
			} else {
				autoSaveTimer.setDelay(delay);
				autoSaveTimer.restart(); // Set the new delay and restart the Timer
			}
		}
	}

	/**
	 * Applications can use this to pause the timer. Each call toggles the state of the timer, or the {@link #restart()}
	 * method can be called at any time to reset and start the timer.
	 */
	public void pause() {
		if (autoSaveTimer != null && autoSaveTimer.isRunning())
			autoSaveTimer.stop();
	}

	public void actionPerformed(ActionEvent e) {
		// Don't autosave if we don't "own" the campaign
		if (!MapTool.isHostingServer() && !MapTool.isPersonalServer())
			return;

		if (AppState.isSaving()) {
			log.debug("Canceling autosave because user has initiated save operation"); //$NON-NLS-1$
			return;
		}
		AppState.setIsSaving(true);

		MapTool.getFrame().setStatusMessage(I18N.getString("AutoSaveManager.status.autoSaving"));
		long startCopy = System.currentTimeMillis();

		// This occurs on the event dispatch thread, so it's ok to mess with the models.
		// We need to clone the campaign so that we can save in the background, but
		// not have concurrency issues with the original model.
		//
		// NOTE: This is a cheesy way to clone the campaign, but it makes it so that I
		// don't have to keep all the various models' clone methods updated on each change.
		final Campaign campaign = new Campaign(MapTool.getCampaign());
		if (log.isInfoEnabled())
			log.info("Time to copy Campaign object (ms): " + (System.currentTimeMillis() - startCopy)); //$NON-NLS-1$

		// Now that we have a copy of the model, save that one
		// TODO: Replace this with a swing worker
		new Thread(new Runnable() {
			public void run() {
				long startSave = System.currentTimeMillis();
				try {
					PersistenceUtil.saveCampaign(campaign, AUTOSAVE_FILE);
					MapTool.getFrame().setStatusMessage(I18N.getText("AutoSaveManager.status.autoSaveComplete", System.currentTimeMillis() - startSave));
				} catch (IOException ioe) {
					MapTool.showError("AutoSaveManager.failed", ioe);
				} catch (Throwable t) {
					MapTool.showError("AutoSaveManager.failed", t);
				} finally {
					AppState.setIsSaving(false);
				}
			}
		}).start();
	}

	/**
	 * Removes any autosaved files
	 */
	public void purge() {
		if (AUTOSAVE_FILE.exists()) {
			AUTOSAVE_FILE.delete();
		}
	}

	/**
	 * Removes the campaignFile if it's from Autosave, forcing to save as new
	 */
	public void tidy() {
		if (AUTOSAVE_FILE.equals(AppState.getCampaignFile())) {
			AppState.setCampaignFile(null);
		}
		purge();
	}

	/**
	 * Check to see if autosave recovery is necessary.
	 */
	public void check() {
		if (AUTOSAVE_FILE.exists()) {
			boolean okay;
			okay = MapTool.confirm("msg.confirm.recoverAutosave", AUTOSAVE_FILE.lastModified());
			if (okay)
				AppActions.loadCampaign(AUTOSAVE_FILE);
		}
	}
}
