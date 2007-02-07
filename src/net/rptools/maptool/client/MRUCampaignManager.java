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
	private JMenu mruMenu;
	private List<File> mruCampaigns;	
		
	public MRUCampaignManager(JMenu menu) {
		mruMenu = menu;
		mruCampaigns = new ArrayList<File>(DEFAULT_MAX_MRU+1);
		loadMruCampaignList();
	}
	
	/**
	 * Returns the MRU Campaigns menu item and sub-menu
	 */	
	public JMenu GetMRUMenu() {
		return mruMenu;
	}
	
	/**
	 * Adds a new Campaign to the MRU list, then resorts the list and updates the menu
	 */
	public void addMRUCampaign(File newCampaign) {
		
		//don't add the autosave recovery file until it is resaved
		if(newCampaign == AutoSaveManager.AUTOSAVE_FILE)
			return;
		
		if( mruCampaigns.size() == 0 ) {
			mruCampaigns.add(newCampaign);
		}
		else {
			List<File> newMruList = new ArrayList<File>(DEFAULT_MAX_MRU+1);
			newMruList.add(newCampaign);
			for (ListIterator<File> iter = mruCampaigns.listIterator(); iter.hasNext();) {
				File next = iter.next();
				if (newMruList.size() == DEFAULT_MAX_MRU)
					break;
				else if ( next.equals(newCampaign) )
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
		
		if (mruCampaigns.size() == 0){
			mruMenu.add( new JMenuItem("<empty>"));
		}
		else {
			int i=1;
			for (ListIterator<File> iter = mruCampaigns.listIterator(); iter.hasNext();) {
				if(i > DEFAULT_MAX_MRU){
					break;
				}
				File nextFile = iter.next();
				// Check to see if the file has been deleted
				if(nextFile.exists()) {
					Action action = new AppActions.OpenMRUCampaign(nextFile, i++);
					mruMenu.add(action);
				}
				else {
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
