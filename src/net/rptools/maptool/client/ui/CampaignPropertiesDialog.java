package net.rptools.maptool.client.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.Campaign;
import net.rptools.maptool.model.TokenProperty;

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
		
		pack();
	}

	public Status getStatus() {
		return status;
	}
	
	@Override
	public void setVisible(boolean b) {
		if (b) {
			SwingUtil.centerOver(this, MapTool.getFrame());
		} else {
			if (status == Status.OK) {

				// TODO: Push info to the server
			}
		}
		
		super.setVisible(b);
	}
	
	private void initialize() {
		
		setLayout(new GridLayout());
		formPanel = new FormPanel("net/rptools/maptool/client/ui/forms/campaignPropertiesDialog.jfrm");

		initOKButton();
		initCancelButton();
		
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

	private void cancel() {
		status = Status.CANCEL;
		setVisible(false);
	}
	
	private void accept() {
		copyUIToCampaign();
		
		status = Status.OK;
		setVisible(false);
	}
	
	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
		
		copyCampaignToUI();
	}
	
	private void copyCampaignToUI() {
		
		parseTokenProperties(campaign.getTokenPropertyList(Campaign.DEFAULT_TOKEN_PROPERTY_TYPE));
	}
	
	private void copyUIToCampaign() {
		
		campaign.putTokenType(Campaign.DEFAULT_TOKEN_PROPERTY_TYPE, compileTokenProperties());
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
	
	private void initCancelButton() {
		
		getCancelButton().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				status = Status.CANCEL;
				setVisible(false);
			}
		});
	}
	
}
