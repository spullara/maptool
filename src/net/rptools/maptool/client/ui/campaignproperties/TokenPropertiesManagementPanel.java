package net.rptools.maptool.client.ui.campaignproperties;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.rptools.maptool.client.swing.AbeillePanel;
import net.rptools.maptool.model.Campaign;
import net.rptools.maptool.model.CampaignProperties;
import net.rptools.maptool.model.TokenProperty;

public class TokenPropertiesManagementPanel extends AbeillePanel<CampaignProperties> {

	private Map<String, List<TokenProperty>> tokenTypeMap;
	private String editingType;
	
	public TokenPropertiesManagementPanel() {
		super("net/rptools/maptool/client/ui/forms/tokenPropertiesManagementPanel.jfrm");
		
		panelInit();
	}
	
	public void copyCampaignToUI(CampaignProperties campaignProperties) {
		
		tokenTypeMap = new HashMap<String, List<TokenProperty>>(campaignProperties.getTokenTypeMap());
		
		updateTypeList();
		
	}
	
	public void copyUIToCampaign(Campaign campaign) {
		
		campaign.getTokenTypeMap().clear();
		campaign.getTokenTypeMap().putAll(tokenTypeMap);
	}
	
	public JList getTokenTypeList() {
		return (JList) getComponent("tokenTypeList");
	}
	
	public JTextField getTokenTypeName() {
		return (JTextField) getComponent("tokenTypeName");
	}
	
	public JButton getNewButton() {
		return (JButton) getComponent("newButton");
		
	}
	
	public JButton getUpdateButton() {
		return (JButton) getComponent("updateButton");
		
	}

	public JButton getRevertButton() {
		return (JButton) getComponent("revertButton");
		
	}
	
	public JTextArea getTokenPropertiesArea() {
		return (JTextArea) getComponent("tokenProperties");
	}
	
	public void initUpdateButton() {
		getUpdateButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				update();
			}
		});
	}
	
	public void initNewButton() {
		getNewButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				EventQueue.invokeLater(new Runnable() {
					public void run() {
						// This will force a reset
						getTokenTypeList().getSelectionModel().clearSelection();
						reset();
					}
				});
			}
		});
	}
	
	public void initRevertButton() {
		getRevertButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bind(editingType);
			}
		});
	}
	
	public void initTypeList() {
		
		getTokenTypeList().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}

				if (getTokenTypeList().getSelectedValue() == null) {
					reset();
				} else {
					bind((String)getTokenTypeList().getSelectedValue());
				}
			}
		});
		getTokenTypeList().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	private void bind(String type) {
		
		editingType = type;
		
		getTokenTypeName().setText(type != null ? type : "");
		getTokenTypeName().setEditable(!CampaignProperties.DEFAULT_TOKEN_PROPERTY_TYPE.equals(type));
		getTokenPropertiesArea().setText(type != null ? compileTokenProperties(tokenTypeMap.get(type)) : "");
	}
	
	private void update() {
		
		// Pull the old one one (rename)
		tokenTypeMap.remove(editingType);
		tokenTypeMap.put(getTokenTypeName().getText().trim(), parseTokenProperties(getTokenPropertiesArea().getText()));

		reset();
		
		updateTypeList();
	}
	
	private void reset() {
		
		bind((String)null);
	}
	
	private void updateTypeList() {
		
		getTokenTypeList().setModel(new TypeListModel());
	}
	
	private String compileTokenProperties(List<TokenProperty> propertyList) {

		// Sanity
		if (propertyList == null) {
			return "";
		}
		
		StringBuilder builder = new StringBuilder();
		
		for (TokenProperty property : propertyList) {
			if (property.isShowOnStateSheet()) {
				builder.append("*");
			}
			if (property.isOwnerOnly()) {
				builder.append("@");
			}
			if (property.isGMOnly()) {
				builder.append("#");
			}
			builder.append(property.getName());
			if (property.getShortName() != null) {
				builder.append(" (").append(property.getShortName()).append(")");
			}
			if (property.getDefaultValue() !=null)
			{
				builder.append(":").append(property.getDefaultValue());
			}
			builder.append("\n");
		}
		
		return builder.toString();
	}
	
	private List<TokenProperty> parseTokenProperties(String propertyText) {

		List<TokenProperty> propertyList = new ArrayList<TokenProperty>();
		BufferedReader reader = new BufferedReader(new StringReader(propertyText));
		String line = null;
		String defaultValue;
		
		try {
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0) {
					continue;
				}
				
				TokenProperty property = new TokenProperty();
				
				// Prefix
				while (true) {
					if (line.startsWith("*")) {
						property.setShowOnStatSheet(true);
						line = line.substring(1);
						continue;
					}
					if (line.startsWith("@")) {
						property.setOwnerOnly(true);
						line = line.substring(1);
						continue;
					}
					if (line.startsWith("#")) {
						property.setGMOnly(true);
						line = line.substring(1);
						continue;
					}
					
					// Ran out of special characters
					break;
				}
				
				// default value
				// had to do this here since the short name is not built 
				// to take advantage of multiple opening/closing parenthesis
				// in a single property line
				int indexDefault = line.indexOf(":");
				if (indexDefault > 0) {
					String defaultVal = line.substring(indexDefault+1, line.length()).trim();
					if (defaultVal.length() > 0) {
						property.setDefaultValue(defaultVal);
					}
					
					//remove the default value from the end of the string...
					line = line.substring(0, indexDefault);					
				}		
				// Suffix
				int index = line.indexOf("(");
				if (index > 0) {
					String shortName = line.substring(index+1, line.indexOf(")", index)).trim();
					if (shortName.length() > 0) {
						property.setShortName(shortName);
					}
					line = line.substring(0, index).trim();
				}
						
				property.setName(line);

				propertyList.add(property);
			}
			
		} catch (IOException ioe) {
			// If this happens, I'll check into the nearest insane asylum
			ioe.printStackTrace();
		}

		return propertyList;
	}
	
	private class TypeListModel extends AbstractListModel {
		public Object getElementAt(int index) {
			List<String> names = new ArrayList<String>(tokenTypeMap.keySet());
			Collections.sort(names);
			return names.get(index);
		}

		public int getSize() {
			return tokenTypeMap.size();
		}
	}
}
