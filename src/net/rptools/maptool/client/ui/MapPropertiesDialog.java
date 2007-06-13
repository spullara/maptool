package net.rptools.maptool.client.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

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

import net.rptools.lib.swing.PaintChooser;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Grid;
import net.rptools.maptool.model.GridFactory;
import net.rptools.maptool.model.HexGridHorizontal;
import net.rptools.maptool.model.HexGridVertical;
import net.rptools.maptool.model.SquareGrid;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.drawing.DrawablePaint;
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
	private MapPreviewPanel imagePreviewPanel;
	
	private FormPanel formPanel;
	
	private DrawablePaint backgroundPaint;
	private Asset mapAsset;
	private DrawablePaint fogPaint;
	
	private Zone zone;

	private PaintChooser paintChooser;
	
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
		}
		
		super.setVisible(b);
	}
	
	private void initialize() {
		
		setLayout(new GridLayout());
		formPanel = new FormPanel("net/rptools/maptool/client/ui/forms/mapPropertiesDialog.jfrm");

		initDistanceTextField();
		
		initOKButton();
		initCancelButton();

		initBackgroundButton();
		initFogButton();
		initMapButton();
		
		initMapPreviewPanel();

		initDistanceTextField();
		initPixelsPerCellTextField();

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

		// Color picker
		paintChooser = new PaintChooser();
		TextureChooserPanel textureChooserPanel = new TextureChooserPanel(paintChooser, MapTool.getFrame().getAssetPanel().getModel());
		paintChooser.addPaintChooser(textureChooserPanel);

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
		
		fogPaint = zone.getFogPaint();
		backgroundPaint = zone.getBackgroundPaint();
		mapAsset = AssetManager.getAsset(zone.getMapAssetId());
	}
	
	private void copyUIToZone() {
		
		zone.setName(getNameTextField().getText().trim());
		zone.setUnitsPerCell(Integer.parseInt(getDistanceTextField().getText()));
		
		zone.getGrid().setSize(Integer.parseInt(getPixelsPerCellTextField().getText()));

		zone.setFogPaint(fogPaint);
		
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
		previewPanel.add(getMapPreviewPanel());
		
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

	private void initBackgroundButton() {
		getBackgroundButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
	}
	
	private void initMapButton() {
		getMapButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		
	}

	private void initFogButton() {
		getFogButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				Paint paint = paintChooser.choosePaint(MapTool.getFrame(), fogPaint != null ? fogPaint.getPaint() : null);
				if (paint != null) {
					fogPaint = DrawablePaint.convertPaint(paint);
				}
				updatePreview();
			}
		});
		
	}
	
	private void updatePreview() {
		getMapPreviewPanel().repaint();
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
	
//	private PreviewPanelFileChooser getImageFileChooser() {
//		if (imageFileChooser == null) {
//			imageFileChooser = new PreviewPanelFileChooser();
//			imageFileChooser.setFileFilter(new FileFilter() {
//				@Override
//				public boolean accept(File f) {
//					return f.isDirectory()
//							|| AppConstants.IMAGE_FILE_FILTER.accept(f
//									.getAbsoluteFile(), f.getName());
//				}
//
//				@Override
//				public String getDescription() {
//					return "Images only";
//				}
//			});
//			if (lastFilePath != null) {
//				imageFileChooser.setCurrentDirectory(lastFilePath);
//			}
//		}
//		return imageFileChooser;
//	}
	
	private MapPreviewPanel getMapPreviewPanel() {
		if (imagePreviewPanel == null) {

			imagePreviewPanel = new MapPreviewPanel();
			imagePreviewPanel.setPreferredSize(new Dimension(150, 150));
			imagePreviewPanel.setMinimumSize(new Dimension(150, 150));
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
	
//	private JComponent createImageExplorerPanel() {
//
//		final AssetPanel assetPanel = new AssetPanel("imageExplorer", MapTool.getFrame().getAssetPanel().getModel());
//		assetPanel.addImageSelectionListener(new SelectionListener() {
//			public void selectionPerformed(List<Object> selectedList) {
//				// There should be exactly one
//				if (selectedList.size() != 1) {
//					return;
//				}
//
//				Integer imageIndex = (Integer) selectedList.get(0);
//
//				if (getBackgroundAsset() != null) {
//					// Tighten memory usage
//					ImageManager.flushImage(getBackgroundAsset());
//				}
//				setBackgroundAsset(assetPanel.getAsset(imageIndex), ImageManager.getImageAndWait(assetPanel.getAsset(imageIndex)));
//			}
//		});
//
//		return assetPanel;
//	}
//	
//	getBackgroundButton().addActionListener(new ActionListener(){
//		public void actionPerformed(ActionEvent e) {
//			if (getImageFileChooser().showOpenDialog(NewMapDialog.this) == JFileChooser.APPROVE_OPTION) {
//				File imageFile = getImageFileChooser().getSelectedFile();
//				if (imageFile == null || imageFile.isDirectory()) {
//					return;
//				}
//				
//				lastFilePath = new File(imageFile.getParentFile() + "/.");
//				try {
//					Asset asset = AssetManager.createAsset(imageFile);
//					setBackgroundAsset(asset, getImageFileChooser().getSelectedThumbnailImage());
//				} catch (IOException ioe) {
//					setBackgroundAsset(null, null);
//				}
//			}
//		}
//	});

	private class MapPreviewPanel extends JComponent {
		
		@Override
		protected void paintComponent(Graphics g) {

			Dimension size = getSize();
			Graphics2D g2d = (Graphics2D)g;
			
			g.setColor(Color.gray);
			g.fillRect(0, 0, size.width, size.height);
			
			// Tile
			if (backgroundPaint != null) {
				g2d.setPaint(backgroundPaint.getPaint());
				g.fillRect(0, 0, size.width, size.height);
			}
			
			// Fog
			if (fogPaint != null) {
				g2d.setPaint(fogPaint.getPaint());
				g.fillRect(0, 0, size.width, 10);
				g.fillRect(0, 0, 10, size.height);
				g.fillRect(0, size.height-10, size.width, 10);
				g.fillRect(size.width-10, 0, 10, size.height);
			}
			
			// Map
			if (mapAsset != null) {
				BufferedImage image = ImageManager.getImageAndWait(mapAsset);
				Dimension imgSize = new Dimension(image.getWidth(), image.getHeight());
				SwingUtil.constrainTo(imgSize, size.width-10-10-10, size.height-10-10-10);
				g.drawImage(image, 30, 30, imgSize.width, imgSize.height, this);
			}
		}
	}
	
}
