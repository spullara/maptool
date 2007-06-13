package net.rptools.maptool.client.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import net.rptools.lib.swing.SelectionListener;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppConstants;
import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.assetpanel.AssetPanel;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.Grid;
import net.rptools.maptool.model.GridFactory;
import net.rptools.maptool.model.HexGridHorizontal;
import net.rptools.maptool.model.HexGridVertical;
import net.rptools.maptool.model.SquareGrid;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.util.ImageManager;

import com.jeta.forms.components.panel.FormPanel;
import com.jeta.forms.gui.form.FormAccessor;

public class MapPropertiesDialog extends JDialog  {

	private static final int AUTO_REPEAT_THRESHOLD = 200;
	
	public enum Status {
		OK,
		CANCEL
	}
	
	private static File lastFilePath;
	
	private Status status;
	
	private PreviewPanelFileChooser imageFileChooser;
	private ImagePreviewPanel imagePreviewPanel;
	
	private FormPanel formPanel;
	
	private Asset backgroundAsset;
	
	private Zone zone;
	
	public MapPropertiesDialog(JFrame owner) {
		super (owner, "Map Properties", true);
		
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
			getImagePreviewPanel().setImage(null);
		}
		
		super.setVisible(b);
	}
	
	public Asset getBackgroundAsset() {
		return backgroundAsset;
	}
	
	public void setBackgroundAsset(Asset asset, Image thumbImage) {
		backgroundAsset = asset;

		if (asset != null && thumbImage == null) {
			// TODO: Really need a thumbnail manager for assets
			thumbImage = ImageManager.getImageAndWait(asset);
			ImageManager.flushImage(asset); 
		}
		getImagePreviewPanel().setImage(thumbImage);

		if (getZoneName().length() == 0) {
			getNameTextField().setText(asset.getName());
		}
		
		getOKButton().setEnabled(backgroundAsset != null);
		
		initRepeatCheckBox();
	}
	
	private void initialize() {
		
		setLayout(new GridLayout());
		formPanel = new FormPanel("net/rptools/maptool/client/ui/forms/mapPropertiesDialog.jfrm");

		initDistanceTextField();
		
		initOKButton();
		initCancelButton();
		
		initMapPreviewPanel();

		initDistanceTextField();
		initPixelsPerCellTextField();

		initRepeatCheckBox();
		
		initHexHoriRadio();
		initHexVertRadio();
		initSquareRadio();
		
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
//		if (backgroundAsset != null) {
//			
//			// Keep track of the image
//			if (!AssetManager.hasAsset(backgroundAsset)) {
//				AssetManager.putAsset(backgroundAsset);
//				MapTool.serverCommand().putAsset(backgroundAsset);
//			}
//
//			// Create the zone
//			Zone zone = ZoneFactory.createZone(getZoneName(), getZoneDistancePerCell(), null, backgroundAsset.getId());
//			zone.setGrid(getZoneGrid());
//
//			zone.setGridColor(AppConstants.DEFAULT_GRID_COLOR.getRGB());
//			
//			MapTool.addZone(zone);
//		}
//
		copyUIToZone();
		
		status = Status.OK;
		setVisible(false);
	}
	
	public JCheckBox getRepeatCheckBox() {
		return formPanel.getCheckBox("repeat");
	}
	
	private void initRepeatCheckBox() {
		if (getBackgroundAsset() != null) {
			BufferedImage image = ImageManager.getImageAndWait(getBackgroundAsset());
			getRepeatCheckBox().setSelected(image.getWidth() < AUTO_REPEAT_THRESHOLD || image.getHeight() < AUTO_REPEAT_THRESHOLD);
		}
	}

	public JRadioButton getHexHorizontalRadio() {
		return formPanel.getRadioButton("hexHoriRadio");
	}
	
	public JRadioButton getHexVerticalRadio() {
		return formPanel.getRadioButton("hexVertRadio");
	}
	
	public JRadioButton getSquareRadio() {
		return formPanel.getRadioButton("squareRadio");
	}

	public void setZone(Zone zone) {
		this.zone = zone;
		
		copyZoneToUI();
	}
	
	private void copyZoneToUI() {
		
		getNameTextField().setText(zone.getName());
		getDistanceTextField().setText(Integer.toString(zone.getUnitsPerCell()));
		getPixelsPerCellTextField().setText(Integer.toString(zone.getGrid().getSize()));
		
		getHexHorizontalRadio().setSelected(zone.getGrid() instanceof HexGridHorizontal);
		getHexVerticalRadio().setSelected(zone.getGrid() instanceof HexGridVertical);
		getSquareRadio().setSelected(zone.getGrid() instanceof SquareGrid);
	}
	
	private void copyUIToZone() {
		
		zone.setName(getNameTextField().getText().trim());
		zone.setUnitsPerCell(Integer.parseInt(getDistanceTextField().getText()));
		
		zone.getGrid().setSize(Integer.parseInt(getPixelsPerCellTextField().getText()));

		// TODO: Handle grid type changes
	}
	
	private void initHexHoriRadio() {
		getHexHorizontalRadio().setSelected(GridFactory.isHexHorizontal(AppPreferences.getDefaultGridType()));
	}
	
	private void initHexVertRadio() {
		getHexVerticalRadio().setSelected(GridFactory.isHexVertical(AppPreferences.getDefaultGridType()));
	}
	
	private void initSquareRadio() {
		getSquareRadio().setSelected(GridFactory.isSquare(AppPreferences.getDefaultGridType()));
		
	}
	
	public JTextField getDistanceTextField() {
		return formPanel.getTextField("distance");
	}

	private void initDistanceTextField() {
		
		getDistanceTextField().setText("5");
	}
	
	private void initMapPreviewPanel() {

		FormAccessor accessor = formPanel.getFormAccessor("previewPanel");
		JPanel previewPanel = new JPanel(new GridLayout());
		previewPanel.setBorder(BorderFactory.createLineBorder(Color.darkGray));
		previewPanel.add(getImagePreviewPanel());
		
		accessor.replaceBean("mapPreviewPanel", previewPanel);
	}
	
	public JTextField getNameTextField() {
		
		return formPanel.getTextField("name");
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
		getOKButton().setEnabled(backgroundAsset != null);
	}

	public JButton getBackgroundButton() {
		return (JButton) formPanel.getButton("backgroundButton");
	}
	
	public JButton getMapButton() {
		return (JButton) formPanel.getButton("mapButton");
	}
	
	public JButton getFogButton() {
		return (JButton) formPanel.getButton("fogButton");
	}
	
	public JButton getBrowseButton() {
		return (JButton) formPanel.getButton("browseButton");
	}
	
//	private void initBrowseButton() {
//		
//		getBrowseButton().addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent e) {
//				if (getImageFileChooser().showOpenDialog(NewMapDialog.this) == JFileChooser.APPROVE_OPTION) {
//					File imageFile = getImageFileChooser().getSelectedFile();
//					if (imageFile == null || imageFile.isDirectory()) {
//						return;
//					}
//					
//					lastFilePath = new File(imageFile.getParentFile() + "/.");
//					try {
//						Asset asset = AssetManager.createAsset(imageFile);
//						setBackgroundAsset(asset, getImageFileChooser().getSelectedThumbnailImage());
//					} catch (IOException ioe) {
//						setBackgroundAsset(null, null);
//					}
//				}
//			}
//		});
//	}
	
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
			imagePreviewPanel.setPreferredSize(new Dimension(120, 120));
		}

		return imagePreviewPanel;
	}
	
	public JTextField getPixelsPerCellTextField() {
		return formPanel.getTextField("pixelsPerCell");
	}
	
	private void initPixelsPerCellTextField() {
		getPixelsPerCellTextField().setText(Integer.toString(AppPreferences.getDefaultGridSize()));
	}
	
	public String getZoneName() {
		return getNameTextField().getText();
	}
	
	public int getZoneDistancePerCell() {
		try {
			// TODO: Handle this in validation
			return Integer.parseInt(getDistanceTextField().getText());
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}
	
	public Grid getZoneGrid() {
		Grid grid = null;
		if (getHexHorizontalRadio().isSelected()) {
			grid = new HexGridHorizontal();
		}
		if (getHexVerticalRadio().isSelected()) {
			grid = new HexGridVertical();
		}
		else {
			grid = new SquareGrid();
		}
		
		grid.setSize(Integer.parseInt(getPixelsPerCellTextField().getText()));
		
		return grid;
	}
	
	private JComponent createImageExplorerPanel() {

		final AssetPanel assetPanel = new AssetPanel("imageExplorer", MapTool.getFrame().getAssetPanel().getModel());
		assetPanel.addImageSelectionListener(new SelectionListener() {
			public void selectionPerformed(List<Object> selectedList) {
				// There should be exactly one
				if (selectedList.size() != 1) {
					return;
				}

				Integer imageIndex = (Integer) selectedList.get(0);

				if (getBackgroundAsset() != null) {
					// Tighten memory usage
					ImageManager.flushImage(getBackgroundAsset());
				}
				setBackgroundAsset(assetPanel.getAsset(imageIndex), ImageManager.getImageAndWait(assetPanel.getAsset(imageIndex)));
			}
		});

		return assetPanel;
	}
	

}