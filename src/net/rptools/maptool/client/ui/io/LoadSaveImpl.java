/**
 * 
 */
package net.rptools.maptool.client.ui.io;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import javax.swing.JOptionPane;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.token.BarTokenOverlay;
import net.rptools.maptool.client.ui.token.BooleanTokenOverlay;
import net.rptools.maptool.model.Campaign;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.LightSource;
import net.rptools.maptool.model.LookupTable;
import net.rptools.maptool.model.MacroButtonProperties;
import net.rptools.maptool.model.SightType;
import net.rptools.maptool.model.TokenProperty;

/**
 * @author crash
 *
 */
public class LoadSaveImpl {
	private Campaign cmpgn;
	private UIBuilder.TreeModel model;
	private static Map<String, DataTemplate> registry;
	
	static {
		registry = new HashMap<String, DataTemplate>();
	}

	/**
	 * <p>
	 * This method is used by other subsystems to register with us so we know they exist!
	 * This allows us to call them when the user wants to load/save data.
	 * </p>
	 * 
	 * @param dt the {@code DataTemplate} child class that represents the subsystem
	 */
	public void addToRegistry(DataTemplate dt) {
		registry.put(dt.getSubsystemName(), dt);
	}

	public void saveApplication() {
		cmpgn = MapTool.getCampaign();

		// Create the load/save dialog form.
		// Populate it with maps, tables, macros, and so forth.
		// Display it to the user with either a LOAD or SAVE button.
		// When they're done, write the selected components out.
		UIBuilder form = new UIBuilder(MapTool.getFrame());
		model = form.getTreeModel();
		new DataTemplate() {
			public String getSubsystemName() { return "built-in"; }
			public void prepareForDisplay() {
				addDataObjects("Campaign/Properties/Token Properties",	 cmpgn.getTokenTypeMap());
				addDataObjects("Campaign/Properties/Repositories", cmpgn.getRemoteRepositoryList());
				addDataObjects("Campaign/Properties/Sights", cmpgn.getSightTypeMap());
				addDataObjects("Campaign/Properties/Lights", cmpgn.getLightSourcesMap());
				addDataObjects("Campaign/Properties/States", cmpgn.getTokenStatesMap());
				addDataObjects("Campaign/Properties/Bars", cmpgn.getTokenBarsMap());
				addDataObjects("Campaign/Properties/Tables", cmpgn.getLookupTableMap());
				addDataObjects("Campaign/CampaignMacros", cmpgn.getMacroButtonPropertiesArray());
			}
		}.populateModel(model);
		for (Iterator<String> iter = registry.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			DataTemplate dt = (DataTemplate) registry.get(name);
			dt.populateModel(model);
		}

		if (false) {
			addTokenProperties(model);
			addRepositories(model);
			addSights(model);
			addLights(model);
			addStates(model);
			addBars(model);
			addLookupTables(model);
			addMacros(model);
		}
		form.expandAndSelectAll(true);
		form.setVisible(true);
		if (form.getStatus() == JOptionPane.OK_OPTION) {
			// Clicked OK to perform a load/save operation.
		}
	}
	private void addTokenProperties(UIBuilder.TreeModel model) {
		Map<String, List<TokenProperty>> types = cmpgn.getTokenTypeMap();
		Set<String> names = types.keySet();
		String[] typeNames = new String[names.size()];
		names.toArray(typeNames);
		Arrays.sort(typeNames);

		for (int index = 0; index < typeNames.length; index++) {
			List<TokenProperty> oneSet = types.get(typeNames[index]);
			model.addNode("Campaign/Properties/Token Properties", new MaptoolNode(typeNames[index], oneSet));
		}
	}
	private void addRepositories(UIBuilder.TreeModel model) {
		List<String> repos = cmpgn.getRemoteRepositoryList();
		String[] repoNames = new String[repos.size()];
		repos.toArray(repoNames);
		Arrays.sort(repoNames);
		
		for (int index = 0; index < repoNames.length; index++) {
			model.addNode("Campaign/Properties/Repositories", new MaptoolNode(repoNames[index], repoNames[index]));
		}
	}
	private void addSights(UIBuilder.TreeModel model) {
		Map<String, SightType> sights = cmpgn.getSightTypeMap();
		Set<String> names = sights.keySet();
		String[] sightNames = new String[names.size()];
		names.toArray(sightNames);
		Arrays.sort(sightNames);
		
		for (int index = 0; index < sightNames.length; index++) {
			SightType sight = sights.get(sightNames[index]);
			model.addNode("Campaign/Properties/Sights", new MaptoolNode(sightNames[index], sight));
		}
	}
	private void addLights(UIBuilder.TreeModel model) {
		Map<String, Map<GUID, LightSource>> lights = cmpgn.getLightSourcesMap();
		Set<String> names = lights.keySet();
		String[] lightNames = new String[names.size()];
		names.toArray(lightNames);
		Arrays.sort(lightNames);
		
		for (int index = 0; index < lightNames.length; index++) {
			Map<GUID, LightSource> light = lights.get(lightNames[index]);
			model.addNode("Campaign/Properties/Lights", new MaptoolNode(lightNames[index], light));
		}
	}
	private void addStates(UIBuilder.TreeModel model) {
		Map<String, BooleanTokenOverlay> states = cmpgn.getTokenStatesMap();
		Set<String> names = states.keySet();
		String[] stateNames = new String[names.size()];
		names.toArray(stateNames);
		Arrays.sort(stateNames);

		for (int index = 0; index < stateNames.length; index++) {
			BooleanTokenOverlay state = states.get(stateNames[index]);
			model.addNode("Campaign/Properties/States", new MaptoolNode(stateNames[index], state));
		}
	}
	private void addBars(UIBuilder.TreeModel model) {
		Map<String, BarTokenOverlay> bars = cmpgn.getTokenBarsMap();
		Set<String> names = bars.keySet();
		String[] barNames = new String[names.size()];
		names.toArray(barNames);
		Arrays.sort(barNames);
		
		for (int index = 0; index < barNames.length; index++) {
			BarTokenOverlay bar = bars.get(barNames[index]);
			model.addNode("Campaign/Properties/Bars", new MaptoolNode(barNames[index], bar));
		}
	}
	private void addLookupTables(UIBuilder.TreeModel model) {
		Map<String, LookupTable> tables = cmpgn.getLookupTableMap();
		Set<String> names = tables.keySet();
		String[] tableNames = new String[names.size()];
		names.toArray(tableNames);
		Arrays.sort(tableNames);

		for (int index = 0; index < tableNames.length; index++) {
			LookupTable lookup = tables.get(tableNames[index]);
			model.addNode("Campaign/Properties/Tables", new MaptoolNode(tableNames[index], lookup));
		}
	}
	private void addMacros(UIBuilder.TreeModel model) {
		List<MacroButtonProperties> macros = cmpgn.getMacroButtonPropertiesArray();
		Map<String, MacroButtonProperties> global = new HashMap<String, MacroButtonProperties>();
		Map<String, MacroButtonProperties> campaign = new HashMap<String, MacroButtonProperties>();
		Map<String, MacroButtonProperties> other = new HashMap<String, MacroButtonProperties>();

		for (MacroButtonProperties macro : macros) {
			String loc = macro.getSaveLocation();
			if (loc.equals("GlobalPanel"))
				global.put(macro.getLabel(), macro);
			else if (loc.equals("CampaignPanel"))
				campaign.put(macro.getLabel(), macro);
			else {
				System.out.println("Ignoring " + loc + " macro button property");
				other.put(loc, macro);
			}
		}
		oneMacroCategory("Campaign/Properties/Macros/Global", global, model);
		oneMacroCategory("Campaign/Properties/Macros/Campaign", campaign, model);
		oneMacroCategory("Campaign/Properties/Macros/Other", other, model);
	}
	private void oneMacroCategory(String where, Map<String, MacroButtonProperties> map, UIBuilder.TreeModel model) {
		Set<String> set = map.keySet();
		String[] names = new String[set.size()];
		set.toArray(names);
		Arrays.sort(names);

		for (int index = 0; index < names.length; index++) {
			MacroButtonProperties macro = map.get(names[index]);
			model.addNode(where, new MaptoolNode(names[index], macro));
		}
	}
}
