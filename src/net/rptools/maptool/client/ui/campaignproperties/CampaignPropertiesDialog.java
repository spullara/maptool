package net.rptools.maptool.client.ui.campaignproperties;

import java.awt.Color;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
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

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Campaign;
import net.rptools.maptool.model.CampaignProperties;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Light;
import net.rptools.maptool.model.LightSource;
import net.rptools.maptool.model.SightType;
import net.rptools.maptool.model.drawing.DrawableColorPaint;
import net.rptools.maptool.util.PersistenceUtil;
import net.rptools.maptool.util.StringUtil;

import com.jeta.forms.components.panel.FormPanel;

public class CampaignPropertiesDialog extends JDialog  {

	public enum Status {
		OK,
		CANCEL
	}

	private TokenPropertiesManagementPanel tokenPropertiesPanel;
	
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
		} else {
			MapTool.getFrame().repaint();
		}
		
		super.setVisible(b);
	}
	
	private void initialize() {
		
		setLayout(new GridLayout());
		formPanel = new FormPanel("net/rptools/maptool/client/ui/forms/campaignPropertiesDialog.jfrm");

		initTokenPropertiesDialog(formPanel);
		
		initOKButton();
		initCancelButton();
		initAddRepoButton();
		initDeleteRepoButton();

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
	
	private void initTokenPropertiesDialog(FormPanel panel) {

		tokenPropertiesPanel = new TokenPropertiesManagementPanel();
		
		panel.getFormAccessor("propertiesPanel").replaceBean("tokenPropertiesPanel", tokenPropertiesPanel);
		panel.reset();
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
		
		copyCampaignToUI(campaign.getCampaignProperties());
	}
	
	private void copyCampaignToUI(CampaignProperties campaignProperties) {
		
		tokenPropertiesPanel.copyCampaignToUI(campaignProperties);
		updateRepositoryList(campaignProperties);
		updateLightPanel(campaignProperties);
		updateSightPanel(campaignProperties);
//		updateTableList();
	}
	
	private void updateSightPanel(CampaignProperties properties) {
		
		StringBuilder builder = new StringBuilder();
		for (SightType sight : properties.getSightTypeMap().values()) {
			
			// Multiplier
			builder.append(sight.getName()).append(": ");
			if (sight.getMultiplier() != 1) {
				builder.append("x").append(StringUtil.formatDecimal(sight.getMultiplier()));
			}
			
			// Personal light
			if (sight.getPersonalLightSource() != null) {
				LightSource source = sight.getPersonalLightSource();
				
				double range = source.getMaxRange();
				builder.append("r").append(StringUtil.formatDecimal(range));
			}
			builder.append("\n");
		}
		
		getSightPanel().setText(builder.toString());
	}

	private void updateLightPanel(CampaignProperties properties) {
	
		StringBuilder builder = new StringBuilder();
		for (Entry<String, Map<GUID, LightSource>> entry : properties.getLightSourcesMap().entrySet()) {
			builder.append(entry.getKey());
			builder.append("\n----\n");
			
			for (LightSource lightSource : entry.getValue().values()) {
				builder.append(lightSource.getName()).append(" : ");
				
				for (Light light : lightSource.getLightList()) {
					builder.append(StringUtil.formatDecimal(light.getRadius()));
					
					if (light.getPaint() instanceof DrawableColorPaint) {
						Color color = (Color)light.getPaint().getPaint();
						builder.append(toHex(color));
					}
					
					builder.append(" ");
				}
				
				builder.append("\n");
			}
			
			builder.append("\n");
		}
		
		getLightPanel().setText(builder.toString());
	}
	
	private String toHex(Color color) {
		StringBuilder builder = new StringBuilder("#");
		
		builder.append(padLeft(Integer.toHexString(color.getRed()), '0', 2));
		builder.append(padLeft(Integer.toHexString(color.getGreen()), '0', 2));
		builder.append(padLeft(Integer.toHexString(color.getBlue()), '0', 2));
		
		return builder.toString();
	}
	
	private String padLeft(String str, char padChar, int length) {
		while (str.length() < length) {
			str = padChar + str;
		}
		return str;
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
		
		tokenPropertiesPanel.copyUIToCampaign(campaign);
		
		campaign.getRemoteRepositoryList().clear();
		for (int i = 0; i < getRepositoryList().getModel().getSize(); i++) {
			String repo = (String) getRepositoryList().getModel().getElementAt(i);
			campaign.getRemoteRepositoryList().add(repo);
		}
		
		commitLightMap();
		commitSightMap();
	}
	
	private void commitLightMap() {
		
		Map<String, SightType> sightMap = new HashMap<String, SightType>();
		BufferedReader reader = new BufferedReader(new StringReader(getSightPanel().getText()));
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {

				line = line.trim();
				
				// Blanks
				if (line.length() == 0 || line.indexOf(":") < 1) {
					continue;
				}

				// Parse line
				int split = line.indexOf(":");
				String label = line.substring(0, split).trim();
				String value = line.substring(split+1).trim();
				
				if (label.length() == 0 || value.length() == 0) {
					continue;
				}
				
				// Parse Details
				double magnifier = 0;
				LightSource personalLight = null;
				
				for (String arg : value.split("\\W")) {
					if (arg.startsWith("x")) {
						magnifier = Double.parseDouble(arg.substring(1));
					}
					if (arg.startsWith("r")) {
						personalLight = new LightSource();
						personalLight.add(new Light(0, Double.parseDouble(arg.substring(1)), 0, null));
					}
				}
				
				
				SightType sight = new SightType(label, magnifier, personalLight);
				
				// Store
				sightMap.put(label, sight);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		campaign.getCampaignProperties().setSightTypeMap(sightMap);
	}
	
	private void commitSightMap() {
		
	}
	
	public JEditorPane getLightPanel() {
		return (JEditorPane) formPanel.getTextComponent("lightPanel");
	}
	
	public JEditorPane getSightPanel() {
		return (JEditorPane) formPanel.getTextComponent("sightPanel");
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
				
				JFileChooser chooser = MapTool.getFrame().getLoadPropsFileChooser();

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
				
				JFileChooser chooser = MapTool.getFrame().getSavePropsFileChooser();
				
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
