package net.rptools.maptool.client.ui.campaignproperties;

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
import net.rptools.maptool.client.ui.lookuptable.EditLookupTablePanel;
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
	
	private EditLookupTablePanel lookupTablePanel;

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

		initImportButton();
		initExportButton();

		lookupTablePanel = new EditLookupTablePanel();
		
		formPanel.getFormAccessor("lookuptableTab").replaceBean("lookuptablePanel", lookupTablePanel);
		
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
		
		lookupTablePanel.attach(campaign);
		
		copyCampaignToUI(campaign.getCampaignProperties());
	}
	
	private void copyCampaignToUI(CampaignProperties properties) {
		
		parseTokenProperties(properties.getTokenPropertyList(Campaign.DEFAULT_TOKEN_PROPERTY_TYPE));
		updateRepositoryList(properties);
//		updateTableList();
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
