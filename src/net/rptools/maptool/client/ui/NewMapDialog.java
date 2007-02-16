package net.rptools.maptool.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import net.rptools.lib.image.ImageUtil;
import net.rptools.lib.swing.SelectionListener;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppConstants;
import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.assetpanel.AssetPanel;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Grid;
import net.rptools.maptool.model.GridFactory;
import net.rptools.maptool.model.HexGrid;
import net.rptools.maptool.model.SquareGrid;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.ZoneFactory;
import net.rptools.maptool.util.ImageManager;

import com.jeta.forms.components.panel.FormPanel;

public class NewMapDialog extends JDialog  {

	public enum Status {
		OK,
		CANCEL
	}
	
	private static File lastFilePath;
	
	private Status status;
	
	private PreviewPanelFileChooser imageFileChooser;
	private JButton okButton;
	private ImagePreviewPanel imagePreviewPanel;
	
	private JRadioButton hexRadio;
	private JRadioButton squareRadio;
	private JRadioButton boundedRadio;
	private JRadioButton unboundedRadio;
	
	private JTextField nameTextField;
	private JTextField distancePerCellTextField;
	
	private JDialog imageExplorerDialog;
	
	private Asset selectedAsset;
	
	public NewMapDialog(JFrame owner) {
		super (owner, "New Map", true);
		
		initialize();
	}
	
	public Status getStatus() {
		return status;
	}
	
	@Override
	public void setVisible(boolean b) {
		if (b) {
			SwingUtil.centerOver(this, MapTool.getFrame());
		} else {
			getImagePreviewPanel().setImage(null);
		}
		super.setVisible(b);
	}
	
	public Asset getSelectedAsset() {
		return selectedAsset;
	}
	
	public void setSelectedAsset(Asset asset, Image thumbImage) {
		selectedAsset = asset;

		if (asset != null && thumbImage == null) {
			// TODO: Really need a thumbnail manager for assets
			thumbImage = ImageManager.getImageAndWait(asset);
			ImageManager.flushImage(asset); 
		}
		getImagePreviewPanel().setImage(thumbImage);
		
		okButton.setEnabled(selectedAsset != null);
	}
	
	private void initialize() {
		
		setLayout(new GridLayout());
		FormPanel panel = new FormPanel("net/rptools/maptool/client/ui/forms/newMapDialog.jfrm");

		initNameTextField(panel);
		initDistanceTextField(panel);
		
		initOKButton(panel);
		initCancelButton(panel);
		
		initBrowseButton(panel);
		initExplorerButton(panel);
		
		initPreviewPanel(panel);

		initDistanceTextField(panel);
		
		initHexRadio(panel);
		initSquareRadio(panel);
		initUnboundedRadio(panel);
		initBoundedRadio(panel);
		
		add(panel);
		
		pack();
		
		// Escape key
		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
		panel.getActionMap().put("cancel", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
		
		getRootPane().setDefaultButton(okButton);
	}

	private void cancel() {
		status = Status.CANCEL;
		setVisible(false);
	}
	
	private void accept() {
		if (selectedAsset != null) {
			
			// Keep track of the image
			if (!AssetManager.hasAsset(selectedAsset)) {
				AssetManager.putAsset(selectedAsset);
				MapTool.serverCommand().putAsset(selectedAsset);
			}

			// Create the zone
			Zone zone = ZoneFactory.createZone(getZoneType(), getZoneName(), getZoneDistancePerCell(), selectedAsset.getId());
			zone.setGrid(getZoneGrid());

			zone.setGridColor(AppConstants.DEFAULT_GRID_COLOR.getRGB());
			
			MapTool.addZone(zone);
		}

		status = Status.OK;
		setVisible(false);
	}
	
	private void initHexRadio(FormPanel panel) {
		hexRadio = panel.getRadioButton("hexRadio");
		hexRadio.setSelected(GridFactory.isHex(AppPreferences.getDefaultGridType()));
	}
	
	private void initSquareRadio(FormPanel panel) {
		squareRadio = panel.getRadioButton("squareRadio");
		squareRadio.setSelected(GridFactory.isSquare(AppPreferences.getDefaultGridType()));
		
	}
	
	private void initBoundedRadio(FormPanel panel) {
		boundedRadio = panel.getRadioButton("boundedRadio");
		boundedRadio.setSelected(true);
	}
	
	private void initUnboundedRadio(FormPanel panel) {
		unboundedRadio = panel.getRadioButton("unboundedRadio");
	}
	
	private void initDistanceTextField(FormPanel panel) {
		
		distancePerCellTextField = panel.getTextField("distance");
		distancePerCellTextField.setText("5");
	}
	
	private void initPreviewPanel(FormPanel panel) {
		
		JPanel previewPanel = panel.getPanel("previewPanel");
		previewPanel.setBorder(BorderFactory.createLineBorder(Color.darkGray));
		previewPanel.setLayout(new GridLayout());
		previewPanel.add(getImagePreviewPanel());
	}
	
	private void initNameTextField(FormPanel panel) {
		
		nameTextField = panel.getTextField("name");
	}
	
	private void initOKButton(FormPanel panel) {
		
		okButton = (JButton) panel.getButton("okButton");
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				accept();
			}
		});
		okButton.setEnabled(selectedAsset != null);
	}
	
	private void initBrowseButton(FormPanel panel) {
		
		JButton button = (JButton) panel.getButton("browseButton");
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (getImageFileChooser().showOpenDialog(NewMapDialog.this) == JFileChooser.APPROVE_OPTION) {
					File imageFile = getImageFileChooser().getSelectedFile();
					if (imageFile == null || imageFile.isDirectory()) {
						return;
					}
					
					lastFilePath = new File(imageFile.getParentFile() + "/.");
					try {
						Asset asset = AssetManager.createAsset(imageFile);
						setSelectedAsset(asset, getImageFileChooser().getSelectedThumbnailImage());
					} catch (IOException ioe) {
						setSelectedAsset(null, null);
					}
				}
			}
		});
	}
	
	private void initExplorerButton(FormPanel panel) {
		
		JButton button = (JButton) panel.getButton("explorerButton");
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				getImageExplorerDialog().setVisible(true);
			}
		});
	}
	
	private void initCancelButton(FormPanel panel) {
		
		JButton button = (JButton) panel.getButton("cancelButton");
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				status = Status.CANCEL;
				setVisible(false);
			}
		});
	}
	
	private PreviewPanelFileChooser getImageFileChooser() {
		if (imageFileChooser == null) {
			imageFileChooser = new PreviewPanelFileChooser();
			imageFileChooser.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return f.isDirectory()
							|| AppConstants.IMAGE_FILE_FILTER.accept(f
									.getAbsoluteFile(), f.getName());
				}

				@Override
				public String getDescription() {
					return "Images only";
				}
			});
			if (lastFilePath != null) {
				imageFileChooser.setCurrentDirectory(lastFilePath);
			}
		}
		return imageFileChooser;
	}
	
	private ImagePreviewPanel getImagePreviewPanel() {
		if (imagePreviewPanel == null) {

			imagePreviewPanel = new ImagePreviewPanel();
		}

		return imagePreviewPanel;
	}
	
	private JDialog getImageExplorerDialog() {
		if (imageExplorerDialog == null) {
			imageExplorerDialog = new ImageExplorerDialog();
			
		}
		
		return imageExplorerDialog;
	}
	
	public int getZoneType() {
		return boundedRadio.isSelected() ? Zone.MapType.MAP : Zone.MapType.INFINITE;
	}
	
	public String getZoneName() {
		return nameTextField.getText();
	}
	
	public int getZoneDistancePerCell() {
		try {
			// TODO: Handle this in validation
			return Integer.parseInt(distancePerCellTextField.getText());
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}
	
	public Grid getZoneGrid() {
		return hexRadio.isSelected() ? new HexGrid() : new SquareGrid();
	}
	
	public void setZoneType(int type) {
		switch (type) {
		case Zone.MapType.INFINITE:
			unboundedRadio.setSelected(true);
			break;
		case Zone.MapType.MAP:
			boundedRadio.setSelected(true);
			break;
		}
	}
	
	////
	// IMAGE EXPLORER DIALOG
	private class ImageExplorerDialog extends JDialog {
		
		private Asset asset;
		
		public ImageExplorerDialog() {
			super (NewMapDialog.this, "Select an image", true);
			setLayout(new BorderLayout());
			add(BorderLayout.CENTER, createImageExplorerPanel());
			add(BorderLayout.SOUTH, createButtonBar());
			setSize(300, 400);
		}
		
		@Override
		public void setVisible(boolean b) {
			if (b) {
				SwingUtil.centerOver(this, NewMapDialog.this);
				
			}
			super.setVisible(b);
		}
		
		private JPanel createButtonBar() {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			panel.add(createOKButton());
			panel.add(createCancelButton());
			
			return panel;
		}

		private JButton createOKButton() {
			JButton button = new JButton("OK");
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (asset != null) {
						setSelectedAsset(asset, null);
					}
					setVisible(false);
				}
			});
			getRootPane().setDefaultButton(button);
			
			return button;
		}

		private JButton createCancelButton() {
			JButton button = new JButton("Cancel");
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});
			
			return button;
		}
		
		private JPanel createImageExplorerPanel() {
			JPanel imageExplorerPanel = new JPanel();
			imageExplorerPanel.setLayout(new BorderLayout());

			final AssetPanel assetPanel = new AssetPanel("imageExplorer", MapTool.getFrame().getAssetPanel().getModel());
			assetPanel.addImageSelectionListener(new SelectionListener() {
				public void selectionPerformed(List<Object> selectedList) {
					// There should be exactly one
					if (selectedList.size() != 1) {
						return;
					}

					Integer imageIndex = (Integer) selectedList.get(0);

					asset = assetPanel.getAsset(imageIndex);
				}
			});

			imageExplorerPanel.add(BorderLayout.CENTER, assetPanel);
			return imageExplorerPanel;
		}
	}


//	@Override
//	public void setVisible(boolean b) {
//
//		if (b) {
//			setSelectedFile(null);
//			SwingUtil.centerOver(this, getOwner());
//		}
//		super.setVisible(b);
//	}
//    public void reset() {
//        
//        returnAsset = null;
//        selectedAsset = null;
//        selectedFile = null;
//        
//        getImageFileChooser().setSelectedFile(null);
//    }
//	
//	public void accept() {
//        try {
//            returnAsset = selectedAsset != null ? selectedAsset : AssetManager.createAsset(selectedFile);
//        } catch (IOException ioe) {
//            MapTool.showError("Could not load asset: " + ioe);
//            returnAsset = null;
//        }
//
//        setVisible(false);
//	}
//	
//	public void cancel() {
//		
//		selectedFile = null;
//        selectedAsset = null;
//        returnAsset = null;
//        
////        gridBounds = null;
//        
//		setVisible(false);
//	}
//	/**
//	 * This method initializes imageExplorerPanel	
//	 * 	
//	 * @return javax.swing.JPanel	
//	 */
//	private void setSelectedFile(File file) {
//		
//		selectedFile = file;
//        selectedAsset = null;
//        
//		imagePreviewPanel.setImage(selectedFile);
//		
//		getOkButton().setEnabled(file != null);
//		//getAdjustGridButton().setEnabled(file != null && getBoundedRadioButton().isSelected());
//	}
//	
//	private void setSelectedAsset(Asset asset) {
//		
//		selectedAsset = asset;
//		selectedFile = null;
//		
//		getOkButton().setEnabled(asset != null);
//		//getAdjustGridButton().setEnabled(asset != null && getBoundedRadioButton().isSelected());
//	}
//

//	
//	////
//	// WINDOW LISTENER
//	public void windowActivated(WindowEvent e) {
//	    getNameTextField().requestFocus();
//    }
//	public void windowClosed(WindowEvent e) {}
//	public void windowClosing(WindowEvent e) {
//        cancel();
//    }
//	public void windowDeactivated(WindowEvent e) {}
//	public void windowDeiconified(WindowEvent e) {}
//	public void windowIconified(WindowEvent e) {}
//	public void windowOpened(WindowEvent e) {}
//
//	/**
//	 * This method initializes previewWrapperPanel	
//	 * 	
//	 * @return javax.swing.JPanel	
//	 */
//	private JPanel getPreviewWrapperPanel() {
//		if (previewWrapperPanel == null) {
//			GridLayout gridLayout = new GridLayout();
//			gridLayout.setRows(1);
//			gridLayout.setColumns(1);
//			previewWrapperPanel = new JPanel();
//			previewWrapperPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0), BorderFactory.createTitledBorder(null, "Preview", TitledBorder.CENTER, TitledBorder.BELOW_BOTTOM, null, null)));
//			previewWrapperPanel.setLayout(gridLayout);
//			previewWrapperPanel.add(getPreviewPanel(), null);
//		}
//		return previewWrapperPanel;
//	}
}
