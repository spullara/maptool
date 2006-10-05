package net.rptools.maptool.client.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;

import com.jeta.forms.components.panel.FormPanel;

public class PreferencesDialog extends JDialog {

	private JCheckBox useTranslucentFogCheckBox;
	private JCheckBox newMapsHaveFOWCheckBox;
	private JCheckBox tokensStartSnapToGridCheckBox;
	private JCheckBox newMapsVisibleCheckBox;
	private JCheckBox newTokensVisibleCheckBox;
	private JCheckBox stampsStartSnapToGridCheckBox;
	private JCheckBox stampsStartFreeSizeCheckBox;
	private JCheckBox backgroundsStartSnapToGridCheckBox;
	private JCheckBox backgroundsStartFreeSizeCheckBox;
	
	public PreferencesDialog() {
		super (MapTool.getFrame(), "Preferences", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		FormPanel panel = new FormPanel("net/rptools/maptool/client/ui/forms/preferencesDialog.jfrm");

		JButton okButton = (JButton)panel.getButton("okButton");
		getRootPane().setDefaultButton(okButton);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		
		useTranslucentFogCheckBox = panel.getCheckBox("useTranslucentFogCheckBox");
		newMapsHaveFOWCheckBox = panel.getCheckBox("newMapsHaveFOWCheckBox");
		tokensStartSnapToGridCheckBox = panel.getCheckBox("tokensStartSnapToGridCheckBox");
		newMapsVisibleCheckBox = panel.getCheckBox("newMapsVisibleCheckBox");
		newTokensVisibleCheckBox = panel.getCheckBox("newTokensVisibleCheckBox");
		stampsStartFreeSizeCheckBox = panel.getCheckBox("stampsStartFreeSize");
		stampsStartSnapToGridCheckBox = panel.getCheckBox("stampsStartSnapToGrid");
		backgroundsStartFreeSizeCheckBox = panel.getCheckBox("backgroundsStartFreeSize");
		backgroundsStartSnapToGridCheckBox = panel.getCheckBox("backgroundsStartSnapToGrid");
		
		
		setInitialState();

		// And keep it updated
		useTranslucentFogCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setUseTranslucentFog(useTranslucentFogCheckBox.isSelected());
				
				ZoneRenderer renderer = MapTool.getFrame().getCurrentZoneRenderer();
				if (renderer != null) {
					renderer.flushFog();
				}
			}
		});
		newMapsHaveFOWCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setNewMapsHaveFOW(newMapsHaveFOWCheckBox.isSelected());
			}
		});
		tokensStartSnapToGridCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setTokensStartSnapToGrid(tokensStartSnapToGridCheckBox.isSelected());
			}
		});
		newMapsVisibleCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setNewMapsVisible(newMapsVisibleCheckBox.isSelected());
			}
		});
		newTokensVisibleCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setNewTokensVisible(newTokensVisibleCheckBox.isSelected());
			}
		});
		stampsStartFreeSizeCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setStampsStartFreesize(stampsStartFreeSizeCheckBox.isSelected());
			}
		});
		stampsStartSnapToGridCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setStampsStartSnapToGrid(stampsStartSnapToGridCheckBox.isSelected());
			}
		});
		backgroundsStartFreeSizeCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setBackgroundsStartFreesize(backgroundsStartFreeSizeCheckBox.isSelected());
			}
		});
		backgroundsStartSnapToGridCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AppPreferences.setBackgroundsStartSnapToGrid(backgroundsStartSnapToGridCheckBox.isSelected());
			}
		});
		
		add(panel);
		
		pack();
	}
	
	@Override
	public void setVisible(boolean b) {

		if (b) {
			SwingUtil.centerOver(this, MapTool.getFrame());
		}
		super.setVisible(b);
	}

	private void setInitialState() {
		
		useTranslucentFogCheckBox.setSelected(AppPreferences.getUseTranslucentFog());
		newMapsHaveFOWCheckBox.setSelected(AppPreferences.getNewMapsHaveFOW());
		tokensStartSnapToGridCheckBox.setSelected(AppPreferences.getTokensStartSnapToGrid());
		newMapsVisibleCheckBox.setSelected(AppPreferences.getNewMapsVisible());
		newTokensVisibleCheckBox.setSelected(AppPreferences.getNewTokensVisible());
		stampsStartFreeSizeCheckBox.setSelected(AppPreferences.getStampsStartFreesize());
		stampsStartSnapToGridCheckBox.setSelected(AppPreferences.getStampsStartSnapToGrid());
		backgroundsStartFreeSizeCheckBox.setSelected(AppPreferences.getBackgroundsStartFreesize());
		backgroundsStartSnapToGridCheckBox.setSelected(AppPreferences.getBackgroundsStartSnapToGrid());
		
	}
}
