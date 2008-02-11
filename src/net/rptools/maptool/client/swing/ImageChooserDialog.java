package net.rptools.maptool.client.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.rptools.lib.MD5Key;
import net.rptools.lib.swing.SelectionListener;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.assetpanel.AssetPanel;

public class ImageChooserDialog extends JDialog {

	private MD5Key imageId = null;
	private AssetPanel imageChooser = new AssetPanel("imageAssetPanel", MapTool.getFrame().getAssetPanel().getModel());
	
	public ImageChooserDialog(JFrame owner) {
		super(owner, "Choose Image", true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				imageId = null;
				setVisible(false);
			}
		});

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(BorderLayout.CENTER, imageChooser);
		panel.add(BorderLayout.SOUTH, createButtonPanel());
		
		setContentPane(panel);
		setSize(400, 500);
		SwingUtil.centerOver(this, getOwner());
		
		imageChooser.addImageSelectionListener(new SelectionListener() {
			public void selectionPerformed(List<Object> selected) {
				if (selected.size() < 0 || (Integer)selected.get(0) < 0) {
					return;
				}
				
				imageId = imageChooser.getAsset((Integer)selected.get(0)).getId();
			}
		});
	}
	
	public MD5Key getImageId() {
		return imageId;
	}
	
	private JPanel createButtonPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		panel.add(createOKButton());
		panel.add(createCancelButton());
		
		return panel;
	}
	
	private JButton createOKButton() {
		JButton button = new JButton("OK");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				setVisible(false);
			}
		});
		
		return button;
	}
	
	private JButton createCancelButton() {
		JButton button = new JButton("Cancel");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				imageId = null;
				setVisible(false);
			}
		});
		
		return button;
	}
}
