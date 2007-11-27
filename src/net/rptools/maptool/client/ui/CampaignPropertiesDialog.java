package net.rptools.maptool.client.ui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Campaign;
import net.rptools.maptool.model.CampaignProperties;
import net.rptools.maptool.model.LookupTable;
import net.rptools.maptool.model.TokenProperty;
import net.rptools.maptool.util.PersistenceUtil;

import com.jeta.forms.components.panel.FormPanel;

public class CampaignPropertiesDialog extends JDialog  {

	public enum Status {
		OK,
		CANCEL
	}
	
	private Status status;
	
	private FormPanel formPanel;

	private Campaign campaign;
	
	public CampaignPropertiesDialog(JFrame owner) {
		super (owner, "Campaign Properties", true);
		setMinimumSize(new Dimension(450, 450));
		setPreferredSize(new Dimension(450, 450));
		
		initialize();
		
		setSize(600, 500);
	}

	public Status getStatus() {
		return status;
	}
	
	@Override
	public void setVisible(boolean b) {
		if (b) {
			SwingUtil.centerOver(this, MapTool.getFrame());
		}
		
		super.setVisible(b);
	}
	
	private void initialize() {
		
		setLayout(new GridLayout());
		formPanel = new FormPanel("net/rptools/maptool/client/ui/forms/campaignPropertiesDialog.jfrm");

		initOKButton();
		initCancelButton();
		initAddRepoButton();
		initDeleteRepoButton();
		initNewTableButton();
		initDeleteTableButton();
		initUpdateTableButton();
		initTableList();
		initImportButton();
		initExportButton();
		
		add(formPanel);
		
		// Escape key
		formPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
		formPanel.getActionMap().put("cancel", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});

		getRootPane().setDefaultButton(getOKButton());
		
	}
	
	public JTextField getNewServerTextField() {
		return formPanel.getTextField("newServer");
	}
	
	private void initTableList() {
		JList list = getTableList();
		list.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}

				String name = (String) getTableList().getSelectedValue();
				
				LookupTable lt = campaign.getLookupTableMap().get(name);
				
				getTableNameTextField().setText(lt != null ? lt.getName() : "");
				getTableDefinitionArea().setText(lt != null ? lt.toString() : "");
				getTableRollTextField().setText(lt != null ? lt.getRoll() : "");
			}
		});
	}
	
	private void initAddRepoButton() {
		JButton button = (JButton) formPanel.getButton("addRepoButton");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String newRepo = getNewServerTextField().getText();
				if (newRepo == null || newRepo.length() == 0) {
					return;
				}
				
				// TODO: Check for uniqueness
				((DefaultListModel)getRepositoryList().getModel()).addElement(newRepo);
			}
		});
	}

	public JTextField getTableNameTextField() {
		return formPanel.getTextField("tableName");
	}

	public JTextField getTableRollTextField() {
		return formPanel.getTextField("defaultTableRoll");
	}

	public JEditorPane getTableDefinitionArea() {
		return (JEditorPane) formPanel.getTextComponent("tableDefinition");
	}

	public JList getTableList() {
		return formPanel.getList("tableList");
	}
	
	private void initNewTableButton() {
		JButton button = (JButton) formPanel.getButton("newTableButton");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				getTableNameTextField().setText("");
				getTableDefinitionArea().setText("");
				
				getTableNameTextField().requestFocusInWindow();
			}
		});
	}

	private void initDeleteTableButton() {
		JButton button = (JButton) formPanel.getButton("deleteTableButton");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String name = (String) getTableList().getSelectedValue();
				
				if (MapTool.confirm("Delete table '" + name + "'")) {
					campaign.getLookupTableMap().remove(name);
					updateTableList();
				}
			}
		});
	}

	private void initUpdateTableButton() {
		JButton button = (JButton) formPanel.getButton("updateTableButton");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String name = getTableNameTextField().getText().trim();
				if (name.length() == 0) {
					MapTool.showError("Must have a name");
					return;
				}
				
				LookupTable lookupTable = new LookupTable(name);
				lookupTable.setRoll(getTableRollTextField().getText());
				
				String definition = getTableDefinitionArea().getText();
				String[] rows = definition.split("\n");
				for (String row : rows) {
					
					row = row.trim();
					
					if (row.length() == 0) {
						continue;
					}

					int split = row.indexOf("=");
					if (split < 1) {
						MapTool.showError("Could not parse line: " + row);
						return;
					}

					String rangeStr = row.substring(0, split).trim();
					String resultStr = row.substring(split+1).trim();
					
					int min = 0;
					int max = 0;
					
					split = rangeStr.indexOf("-", rangeStr.charAt(0) == '-' ? 1 : 0); // Allow negative numbers
					try {
						if (split < 0) {
							min = Integer.parseInt(rangeStr);
							max = min;
						} else {
							min = Integer.parseInt(rangeStr.substring(0, split).trim());
							max = Integer.parseInt(rangeStr.substring(split+1).trim());
						}
					} catch (NumberFormatException nfe) {
						MapTool.showError("Could not parse range: " + rangeStr);
						return;
					}
					
					lookupTable.addEntry(min, max, resultStr);
				}

				// This will override the table with the same name
				campaign.getLookupTableMap().put(name, lookupTable);
				
				getTableNameTextField().setText("");
				getTableDefinitionArea().setText("");
				getTableRollTextField().setText("");
				
				updateTableList();
			}
		});
	}

	public void initDeleteRepoButton() {
		JButton button = (JButton) formPanel.getButton("deleteRepoButton");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				int[] selectedRows = getRepositoryList().getSelectedIndices();
				Arrays.sort(selectedRows);
				for (int i = selectedRows.length-1; i >= 0; i--) {
					((DefaultListModel)getRepositoryList().getModel()).remove(selectedRows[i]);
				}
			}
		});
	}
	
	private void updateTableList() {
		
		final List<String> nameList = new ArrayList<String>();

		for (String name : campaign.getLookupTableMap().keySet()) {
			nameList.add(name);
		}
		
		Collections.sort(nameList);
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				DefaultListModel model = new DefaultListModel();
				for (String name : nameList) {
					model.addElement(name);
				}
				getTableList().setModel(model);
			}
		});
	}

	private void cancel() {
		status = Status.CANCEL;
		setVisible(false);
	}
	
	private void accept() {
		copyUIToCampaign();

		AssetManager.updateRepositoryList();
		
		status = Status.OK;
		setVisible(false);
	}
	
	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
		
		copyCampaignToUI(campaign.getCampaignProperties());
	}
	
	private void copyCampaignToUI(CampaignProperties properties) {
		
		parseTokenProperties(properties.getTokenPropertyList(Campaign.DEFAULT_TOKEN_PROPERTY_TYPE));
		updateRepositoryList(properties);
		updateTableList();
	}
	
	private void updateRepositoryList(CampaignProperties properties) {

		DefaultListModel model = new DefaultListModel();
		for (String repo : properties.getRemoteRepositoryList()) {
			model.addElement(repo);
		}
		getRepositoryList().setModel(model);
	}
	
	public JList getRepositoryList() {
		return formPanel.getList("repoList");
	}
	
	private void copyUIToCampaign() {
		
		campaign.putTokenType(Campaign.DEFAULT_TOKEN_PROPERTY_TYPE, compileTokenProperties());
		
		campaign.getRemoteRepositoryList().clear();
		for (int i = 0; i < getRepositoryList().getModel().getSize(); i++) {
			String repo = (String) getRepositoryList().getModel().getElementAt(i);
			campaign.getRemoteRepositoryList().add(repo);
		}
	}
	
	private void parseTokenProperties(List<TokenProperty> propertyList) {

		StringBuilder builder = new StringBuilder();
		
		for (TokenProperty property : propertyList) {
			if (property.isHighPriority()) {
				builder.append("*");
			}
			if (property.isOwnerOnly()) {
				builder.append("@");
			}
			builder.append(property.getName());
			if (property.getShortName() != null) {
				builder.append(" (").append(property.getShortName()).append(")");
			}
			builder.append("\n");
		}
		
		getTokenPropertiesTextArea().setText(builder.toString());
	}
	
	private List<TokenProperty> compileTokenProperties() {

		List<TokenProperty> propertyList = new ArrayList<TokenProperty>();
		BufferedReader reader = new BufferedReader(new StringReader(getTokenPropertiesTextArea().getText()));
		String line = null;
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
						property.setHighPriority(true);
						line = line.substring(1);
						continue;
					}
					if (line.startsWith("@")) {
						property.setOwnerOnly(true);
						line = line.substring(1);
						continue;
					}
					
					// Ran out of special characters
					break;
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
	
	public JTextArea getTokenPropertiesTextArea() {
		return (JTextArea) formPanel.getTextComponent("tokenProperties");
	}
	
	public JButton getOKButton() {
		return (JButton) formPanel.getButton("okButton");
	}
	
	private void initOKButton() {
		
		getOKButton().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				accept();
			}
		});
	}

	public JButton getCancelButton() {
		return (JButton) formPanel.getButton("cancelButton");
	}
	
	public JButton getImportButton() {
		return (JButton) formPanel.getButton("importButton");
	}
	
	public JButton getExportButton() {
		return (JButton) formPanel.getButton("exportButton");
	}
	
	private void initCancelButton() {
		
		getCancelButton().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				status = Status.CANCEL;
				setVisible(false);
			}
		});
	}
	
	private void initImportButton() {
		
		getImportButton().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser chooser = MapTool.getFrame().getLoadFileChooser();
				if (chooser.showOpenDialog(MapTool.getFrame()) != JFileChooser.APPROVE_OPTION) {
					return;
				}

				final File selectedFile = chooser.getSelectedFile();
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							CampaignProperties properties = PersistenceUtil.loadCampaignProperties(selectedFile);
							
							// TODO: Allow specifying whether it is a replace or merge
							MapTool.getCampaign().mergeCampaignProperties(properties);
							
							
							copyCampaignToUI(properties);
							
						} catch (IOException ioe) {
							ioe.printStackTrace();
							MapTool.showError("Could not load properties: " + ioe);
						}
					}
				});
			}
		});
	}
	
	private void initExportButton() {
		
		getExportButton().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
				// TODO: Remove this hack.  Specifically, make the export use a properties object
				// composed of the current dialog entries instead of directly from the campaign
				copyUIToCampaign();
				// END HACK
				
				JFileChooser chooser = MapTool.getFrame().getSaveFileChooser();
				if (chooser.showSaveDialog(MapTool.getFrame()) != JFileChooser.APPROVE_OPTION) {
					return;
				}
				
				File selectedFile = chooser.getSelectedFile();
				if (selectedFile.exists()) {
					if (!MapTool.confirm("Overwrite existing file?")) {
						return;
					}
				}
				
				try {
					PersistenceUtil.saveCampaignProperties(campaign, chooser.getSelectedFile());
					
					MapTool.showInformation("Properties Saved.");
				} catch (IOException ioe) {
					ioe.printStackTrace();
					MapTool.showError("Could not save properties: " + ioe);
				}
			}
		});
	}
	
}
