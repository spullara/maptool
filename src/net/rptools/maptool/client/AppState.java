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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;

public class AppState {
	public static final String USE_DOUBLE_WIDE_PROP_NAME = "useDoubleWide";

	private static boolean showGrid = false;
	private static boolean showCoordinates = false;
	private static boolean showTokenNames = false;
	private static boolean linkPlayerViews = false;
	private static boolean useDoubleWideLine = true;
	private static boolean showMovementMeasurements = true;
	private static boolean enforceNotification = false;
	private static File campaignFile;
	private static int gridSize = 1;
	private static boolean showAsPlayer = false;
	private static boolean showLightSources = false;
	private static boolean zoomLocked = false;

	private static boolean collectProfilingData = false;
	private static boolean isSaving = false;

	private static PropertyChangeSupport changeSupport = new PropertyChangeSupport(AppState.class);

	public static void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public static void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public static boolean isCollectProfilingData() {
		return collectProfilingData;
	}

	public static void setCollectProfilingData(boolean flag) {
		collectProfilingData = flag;
	}

	public static int getGridSize() {
		return gridSize;
	}

	public static void setGridSize(int size) {
		gridSize = size;
	}

	public static boolean useDoubleWideLine() {
		return useDoubleWideLine;
	}

	public static void setUseDoubleWideLine(boolean useDoubleWideLine) {
		boolean old = AppState.useDoubleWideLine;
		AppState.useDoubleWideLine = useDoubleWideLine;
		changeSupport.firePropertyChange(USE_DOUBLE_WIDE_PROP_NAME, old, useDoubleWideLine);
	}

	public static boolean isShowGrid() {
		return showGrid;
	}

	public static void setShowGrid(boolean flag) {
		showGrid = flag;
	}

	public static boolean isShowCoordinates() {
		return showCoordinates;
	}

	public static boolean isZoomLocked() {
		return zoomLocked;
	}

	public static void setZoomLocked(boolean zoomLock) {
		boolean oldVal = AppState.zoomLocked;
		AppState.zoomLocked = zoomLock;
		changeSupport.firePropertyChange("zoomLock", oldVal, zoomLock);
	}

	public static void setShowCoordinates(boolean flag) {
		showCoordinates = flag;
	}

	public static void setShowTokenNames(boolean flag) {
		showTokenNames = flag;
	}

	public static boolean isShowTokenNames() {
		return showTokenNames;
	}

	public static boolean isPlayerViewLinked() {
		return linkPlayerViews;
	}

	public static void setPlayerViewLinked(boolean flag) {
		linkPlayerViews = flag;
	}

	public static File getCampaignFile() {
		return campaignFile;
	}

	public static void setCampaignFile(File campaignFile) {
		AppState.campaignFile = campaignFile;
	}

	public static void setShowMovementMeasurements(boolean show) {
		showMovementMeasurements = show;
	}

	public static boolean getShowMovementMeasurements() {
		return showMovementMeasurements;
	}

	public static boolean isShowAsPlayer() {
		return showAsPlayer;
	}

	public static void setShowAsPlayer(boolean showAsPlayer) {
		AppState.showAsPlayer = showAsPlayer;
	}

	public static boolean isShowLightSources() {
		return showLightSources;
	}

	public static void setShowLightSources(boolean show) {
		showLightSources = show;
	}

	public synchronized static void setIsSaving(boolean saving) {
		isSaving = saving;
	}

	public synchronized static boolean isSaving() {
		return isSaving;
	}

	public static boolean isNotificationEnforced() {
		return enforceNotification;
	}

	public static void setNotificationEnforced(boolean enforce) {
		enforceNotification = enforce;
	}

}
