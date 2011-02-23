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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * @author tylere
 */
public class MRUCampaignManager {
	//To increase max mru's need to update mnemonics code
	private static final int DEFAULT_MAX_MRU = 9;
	private final JMenu mruMenu;
	private List<File> mruCampaigns;

	public MRUCampaignManager(JMenu menu) {
		mruMenu = menu;
		mruCampaigns = new ArrayList<File>(DEFAULT_MAX_MRU + 1);
		loadMruCampaignList();
	}

	/**
	 * Returns the MRU Campaigns menu item and sub-menu
	 */
	public JMenu getMRUMenu() {
		return mruMenu;
	}

	/**
	 * Adds a new Campaign to the MRU list, then resort the list and update the menu
	 */
	public void addMRUCampaign(File newCampaign) {
		// FIXME (this coupling is too tight; change the calling function to avoid this call entirely)
		//don't add the autosave recovery file until it is resaved
		if (newCampaign == AutoSaveManager.AUTOSAVE_FILE)
			return;

		if (mruCampaigns.isEmpty()) {
			mruCampaigns.add(newCampaign);
		} else {
			// This code would be much simpler, but too late in the 1.3 cycle for this change.
//			LinkedList<File> newMruList = new LinkedList<File>(mruCampaigns);
//			newMruList.removeFirstOccurrence(newCampaign);
//			newMruList.addFirst(newCampaign);
//			while (newMruList.size() > DEFAULT_MAX_MRU)
//				newMruList.removeLast();
			ArrayList<File> newMruList = new ArrayList<File>(DEFAULT_MAX_MRU + 1);
			newMruList.add(newCampaign);
			for (ListIterator<File> iter = mruCampaigns.listIterator(); iter.hasNext();) {
				File next = iter.next();
				if (newMruList.size() == DEFAULT_MAX_MRU)
					break;
				else if (next.equals(newCampaign))
					continue;
				else
					newMruList.add(next);
			}
			mruCampaigns = newMruList;
		}
		resetMruMenu();
	}

	private void resetMruMenu() {
		mruMenu.removeAll();
		addMRUsToMenu();
		saveMruCampaignList();
	}

	private void addMRUsToMenu() {
		if (mruCampaigns.isEmpty()) {
			mruMenu.add(new JMenuItem("[empty]"));
		} else {
			int i = 1;
			for (ListIterator<File> iter = mruCampaigns.listIterator(); iter.hasNext();) {
				if (i > DEFAULT_MAX_MRU) {
					break;
				}
				File nextFile = iter.next();
				// Check to see if the file has been deleted
				if (nextFile.exists()) {
					Action action = new AppActions.OpenMRUCampaign(nextFile, i++);
					mruMenu.add(action);
				} else {
					iter.remove();
				}
			}
		}
	}

	private void saveMruCampaignList() {
		AppPreferences.setMruCampaigns(mruCampaigns);
	}

	private void loadMruCampaignList() {
		mruCampaigns = AppPreferences.getMruCampaigns();
		addMRUsToMenu();
	}
}
