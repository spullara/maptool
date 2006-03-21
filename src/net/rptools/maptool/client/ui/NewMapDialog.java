package net.rptools.maptool.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import net.rptools.lib.image.ImageUtil;
import net.rptools.lib.swing.SelectionListener;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppConstants;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.adjustgrid.AdjustGridDialog;
import net.rptools.maptool.client.ui.assetpanel.AssetPanel;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Zone;

public class NewMapDialog extends JDialog implements WindowListener {

	private JPanel jContentPane = null;
	private JPanel bottomPanel = null;
	private JPanel buttonPanel = null;
	private JPanel northPanel = null;
	private JPanel optionsPanel = null;
	private JRadioButton unboundedRadioButton = null;
	private JRadioButton boundedRadioButton = null;
	private JButton okButton = null;
	private JButton cancelButton = null;
	private JTabbedPane typeTabbedPane = null;
	private JPanel imageExplorerPanel = null;
	private JPanel filesystemPanel = null;
	private JPanel libraryPanel = null;
	private JButton adjustGridButton = null;
	private JFileChooser imageFileChooser = null;
	private ImagePreviewWindow imagePreviewPanel;
	
	private JPanel textOptionPanel = null;
	private JLabel nameLabel = null;
	private JTextField nameTextField = null;
	private JFormattedTextField feetPerCellTextField = null;
	private JPanel previewWrapperPanel = null;
	private JPanel eastPanel = null;
	private JPanel spacerPanel = null;
	private JPanel attributePanel = null;

    private File selectedFile;
    private Asset selectedAsset;
    
    private Asset returnAsset;
	private JPanel row3Panel = null;
	private JPanel row2Panel = null;
	private JLabel fpcLabel = null;
	private JLabel spacerLabel = null;
	
//	private Rectangle gridBounds;
//	private int gridCountX;
//	private int gridCountY;

	private int gridSize;
	private int gridOffsetX;
	private int gridOffsetY;
	private Color gridColor;
	
	/**
	 * This is the default constructor
	 */
	public NewMapDialog(JFrame owner) {
		super(owner, true);
		initialize();
		
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(this);
        
        // Escape key
        getJContentPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),"cancel");
        getJContentPane().getActionMap().put("cancel", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        });
        
        getRootPane().setDefaultButton(getOkButton());
	}

	
	
//	public Rectangle getGridBounds() {
//		return gridBounds;
//	}
//
//
//
//	public void setGridBounds(Rectangle gridBounds) {
//		this.gridBounds = gridBounds;
//	}
//
//
//
//	public int getGridCountX() {
//		return gridCountX;
//	}
//
//
//
//	public void setGridCountX(int gridCountX) {
//		this.gridCountX = gridCountX;
//	}
//
//
//
//	public int getGridCountY() {
//		return gridCountY;
//	}
//
//
//
//	public void setGridCountY(int gridCountY) {
//		this.gridCountY = gridCountY;
//	}
//

	public int getGridSize() {
		return gridSize;
	}
    public int getGridOffsetX() {
    	return gridOffsetX;
    }
    
    public int getGridOffsetY() {
    	return gridOffsetY;
    }

    public Color getGridColor() {
    	return gridColor;
    }

	@Override
	public void setVisible(boolean b) {

		if (b) {
			setSelectedFile(null);
			SwingUtil.centerOver(this, getOwner());
		}
		super.setVisible(b);
	}
	
	public int getZoneType() {
		return boundedRadioButton.isSelected() ? Zone.Type.MAP : Zone.Type.INFINITE;
	}
    
    public String getZoneName() {
        return getNameTextField().getText();
    }
	
    public int getZoneFeetPerCell() {
        return Integer.parseInt(getFeetPerCellTextField().getText());
    }
    
	public Asset showDialog() {
		
        reset();
		setVisible(true);
        return returnAsset;
	}
    
    public void reset() {
        
        returnAsset = null;
        selectedAsset = null;
        selectedFile = null;
        
        getImageFileChooser().setSelectedFile(null);
    }
	
	public void accept() {
        try {
            returnAsset = selectedAsset != null ? selectedAsset : AssetManager.createAsset(selectedFile);
        } catch (IOException ioe) {
            MapTool.showError("Could not load asset: " + ioe);
            returnAsset = null;
        }

        setVisible(false);
	}
	
	public void cancel() {
		
		selectedFile = null;
        selectedAsset = null;
        returnAsset = null;
        
//        gridBounds = null;
        
		setVisible(false);
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(565, 506);
		this.setEnabled(true);
		this.setTitle("New Map");
		this.setContentPane(getJContentPane());
		
		ButtonGroup group = new ButtonGroup();
		group.add(unboundedRadioButton);
		group.add(boundedRadioButton);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			BorderLayout borderLayout = new BorderLayout();
			borderLayout.setHgap(5);
			borderLayout.setVgap(5);
			jContentPane = new JPanel();
			jContentPane.setLayout(borderLayout);
			//jContentPane.add(getEastPanel(), java.awt.BorderLayout.EAST);
			jContentPane.add(getBottomPanel(), java.awt.BorderLayout.SOUTH);
			jContentPane.add(getNorthPanel(), java.awt.BorderLayout.NORTH);
			jContentPane.add(getTypeTabbedPane(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes bottomPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getBottomPanel() {
		if (bottomPanel == null) {
			bottomPanel = new JPanel();
			bottomPanel.setLayout(new BorderLayout());
			bottomPanel.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
		}
		return bottomPanel;
	}

	/**
	 * This method initializes buttonPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(java.awt.FlowLayout.RIGHT);
			buttonPanel = new JPanel();
			buttonPanel.setLayout(flowLayout);
			buttonPanel.add(getOkButton(), null);
			buttonPanel.add(getCancelButton(), null);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes eastPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getNorthPanel() {
		if (northPanel == null) {
			northPanel = new JPanel();
			northPanel.setLayout(new BorderLayout());
			northPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(3,5,3,5));
			northPanel.add(getOptionsPanel(), java.awt.BorderLayout.SOUTH);
		}
		return northPanel;
	}

	/**
	 * This method initializes previewPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JComponent getPreviewPanel() {
		if (imagePreviewPanel == null) {
			
			imagePreviewPanel = new ImagePreviewWindow();
		}
		
		return imagePreviewPanel;
	}

	/**
	 * This method initializes optionsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getOptionsPanel() {
		if (optionsPanel == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints11.gridwidth = 1;
			gridBagConstraints11.weightx = 1.0D;
			gridBagConstraints11.ipadx = 0;
			gridBagConstraints11.insets = new java.awt.Insets(0,5,0,5);
			gridBagConstraints11.anchor = java.awt.GridBagConstraints.NORTHWEST;
			gridBagConstraints11.gridy = 1;
			optionsPanel = new JPanel();
			optionsPanel.setLayout(new GridBagLayout());
			optionsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,0,0,0));
			optionsPanel.add(getTextOptionPanel(), gridBagConstraints11);
		}
		return optionsPanel;
	}

	/**
	 * This method initializes unboundedRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getUnboundedRadioButton() {
		if (unboundedRadioButton == null) {
			unboundedRadioButton = new JRadioButton();
			unboundedRadioButton.setText("Unbounded");
			unboundedRadioButton.setSelected(false);
			unboundedRadioButton.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					if (unboundedRadioButton.isSelected()) {
						adjustGridButton.setEnabled(false);
					}
				}
			});
		}
		return unboundedRadioButton;
	}

	/**
	 * This method initializes boundedRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getBoundedRadioButton() {
		if (boundedRadioButton == null) {
			boundedRadioButton = new JRadioButton();
			boundedRadioButton.setText("Bounded");
			boundedRadioButton.setSelected(true);
			boundedRadioButton.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					if (boundedRadioButton.isSelected()) {
						//adjustGridButton.setEnabled(true);
					}
				}
			});
		}
		return boundedRadioButton;
	}

	/**
	 * This method initializes okButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText("OK");
			okButton.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					accept();
				}
			});
		}
		return okButton;
	}

	/**
	 * This method initializes cancelButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText("Cancel");
			cancelButton.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					cancel();
				}
			});
		}
		return cancelButton;
	}

	/**
	 * This method initializes typeTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getTypeTabbedPane() {
		if (typeTabbedPane == null) {
			typeTabbedPane = new JTabbedPane();
			typeTabbedPane.addTab("Filesystem", null, getFilesystemPanel(), null);
			typeTabbedPane.addTab("Images", null, getImageExplorerPanel(), null);
			//typeTabbedPane.addTab("Library", null, getLibraryPanel(), null);
		}
		return typeTabbedPane;
	}

	/**
	 * This method initializes imageExplorerPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getImageExplorerPanel() {
		if (imageExplorerPanel == null) {
			imageExplorerPanel = new JPanel();
			imageExplorerPanel.setLayout(new BorderLayout());
			
			final AssetPanel assetPanel = new AssetPanel("imageExplorer", MapTool.getFrame().getAssetPanel().getModel());
			assetPanel.addImageSelectionListener(new SelectionListener() {
				public void selectionPerformed(List<Object> selectedList) {
					// There should be exactly one
					if (selectedList.size() != 1) {
						return;
					}
					
					Integer imageIndex = (Integer) selectedList.get(0);
					
					setSelectedAsset(assetPanel.getAsset(imageIndex));
				}
			});
			
			imageExplorerPanel.add(BorderLayout.CENTER, assetPanel);
		}
		return imageExplorerPanel;
	}

	/**
	 * This method initializes filesystemPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getFilesystemPanel() {
		if (filesystemPanel == null) {
			filesystemPanel = new JPanel();
			filesystemPanel.setLayout(new BorderLayout());
			filesystemPanel.add(getImageFileChooser(), java.awt.BorderLayout.NORTH);
		}
		return filesystemPanel;
	}

	/**
	 * This method initializes libraryPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getLibraryPanel() {
		if (libraryPanel == null) {
			libraryPanel = new JPanel();
		}
		return libraryPanel;
	}

	/**
	 * This method initializes adjustGridButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAdjustGridButton() {
		if (adjustGridButton == null) {
			adjustGridButton = new JButton();
			adjustGridButton.setText("Adjust Grid");
			adjustGridButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					
					try {
						BufferedImage image = selectedFile != null ? ImageIO.read(selectedFile) : ImageIO.read(new ByteArrayInputStream(selectedAsset.getImage()));
						AdjustGridDialog agd = new AdjustGridDialog(MapTool.getFrame(), image);
						agd.initialize(AppConstants.DEFAULT_GRID_SIZE, 0, 0, AppConstants.DEFAULT_GRID_COLOR);
						
						agd.setVisible(true);
						if (agd.isOK()) {
							gridSize = agd.getGridSize();
							gridOffsetX = agd.getGridOffsetX();
							gridOffsetY = agd.getGridOffsetY();
							gridColor = agd.getGridColor();
							
//							gridBounds = agd.getGridBounds();
//							gridCountX = agd.getGridXCount();
//							gridCountY = agd.getGridYCount();
						} else {
//							gridBounds = null;
						}
						
					} catch (IOException ioe) {
						MapTool.showError("Could not create the image");
						ioe.printStackTrace();
					}
				}
			});
		}
		return adjustGridButton;
	}

	/**
	 * This method initializes imageFileChooser	
	 * 	
	 * @return javax.swing.JFileChooser	
	 */
	private JFileChooser getImageFileChooser() {
		if (imageFileChooser == null) {
			imageFileChooser = new JFileChooser();
			imageFileChooser.setControlButtonsAreShown(false);
			imageFileChooser.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return f.isDirectory() || AppConstants.IMAGE_FILE_FILTER.accept(f.getAbsoluteFile(), f.getName());
				}
				@Override
				public String getDescription() {
					return "Images only";
				}
			});
			imageFileChooser.addPropertyChangeListener(new FileSystemSelectionHandler());
			imageFileChooser.setAccessory(getPreviewWrapperPanel());
		}
		return imageFileChooser;
	}
	
	private void setSelectedFile(File file) {
		
		selectedFile = file;
        selectedAsset = null;
        
		imagePreviewPanel.setImage(selectedFile);
		
		getOkButton().setEnabled(file != null);
		//getAdjustGridButton().setEnabled(file != null && getBoundedRadioButton().isSelected());
	}
	
	private void setSelectedAsset(Asset asset) {
		
		selectedAsset = asset;
		selectedFile = null;
		
		getOkButton().setEnabled(asset != null);
		//getAdjustGridButton().setEnabled(asset != null && getBoundedRadioButton().isSelected());
	}

	private class ImagePreviewWindow extends JComponent {
		
		private BufferedImage img;
		
		public ImagePreviewWindow() {
			setPreferredSize(new Dimension(150, 100));
			setMinimumSize(new Dimension(150, 100));
		}
		
		public void setImage(File file) {
			if (file == null) {
				img = null;
				repaint();
				return;
			}
			
			try {
				img = ImageUtil.getImage(file);
			} catch (IOException ioe) {
				img = null;
			}
			repaint();
		}
		
		@Override
		protected void paintComponent(Graphics g) {

			// Image
			Dimension size = getSize();
			if (img != null) {
				Dimension imgSize = new Dimension(img.getWidth(), img.getHeight());
				SwingUtil.constrainTo(imgSize, size.width, size.height);

				// Border
				int x = (size.width - imgSize.width)/2;
				int y = (size.height - imgSize.height)/2;
				
				g.drawImage(img, x, y, imgSize.width, imgSize.height, null);
				g.setColor(Color.black);
				g.drawRect(x, y, imgSize.width-1, imgSize.height-1);
			}	

		}
	}
	
	private class FileSystemSelectionHandler implements PropertyChangeListener {
		
		public void propertyChange(PropertyChangeEvent evt) {

			if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
				File selectedFile = getImageFileChooser().getSelectedFile();
				
				setSelectedFile(selectedFile);
			}
		}
	}
	
	////
	// WINDOW LISTENER
	public void windowActivated(WindowEvent e) {
	    getNameTextField().requestFocus();
    }
	public void windowClosed(WindowEvent e) {}
	public void windowClosing(WindowEvent e) {
        cancel();
    }
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}

	/**
	 * This method initializes textOptionPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getTextOptionPanel() {
		if (textOptionPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 1;
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints3.insets = new java.awt.Insets(0,0,3,0);
			gridBagConstraints3.gridy = 1;
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 2;
			gridBagConstraints21.insets = new java.awt.Insets(0,0,3,0);
			gridBagConstraints21.gridy = 0;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 1;
			gridBagConstraints12.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints12.gridy = 3;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints5.gridy = 0;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.gridwidth = 1;
			gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints5.insets = new java.awt.Insets(0,0,3,0);
			gridBagConstraints5.gridx = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints4.insets = new java.awt.Insets(0,0,3,5);
			nameLabel = new JLabel();
			nameLabel.setText("Name:");
			textOptionPanel = new JPanel();
			textOptionPanel.setLayout(new GridBagLayout());
			textOptionPanel.add(nameLabel, gridBagConstraints4);
			textOptionPanel.add(getNameTextField(), gridBagConstraints5);
			textOptionPanel.add(getRow3Panel(), gridBagConstraints12);
			textOptionPanel.add(getAdjustGridButton(), gridBagConstraints21);
			textOptionPanel.add(getRow2Panel(), gridBagConstraints3);
		}
		return textOptionPanel;
	}

	/**
	 * This method initializes nameTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNameTextField() {
		if (nameTextField == null) {
			nameTextField = new JTextField();
			nameTextField.setColumns(25);
		}
		return nameTextField;
	}

	/**
	 * This method initializes feetPerCellTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getFeetPerCellTextField() {
		if (feetPerCellTextField == null) {
			feetPerCellTextField = new JFormattedTextField(new Integer(1000));
			feetPerCellTextField.setColumns(3);
			feetPerCellTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
            feetPerCellTextField.setValue(Zone.DEFAULT_FEET_PER_CELL);
		}
		return feetPerCellTextField;
	}

	/**
	 * This method initializes previewWrapperPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPreviewWrapperPanel() {
		if (previewWrapperPanel == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(1);
			gridLayout.setColumns(1);
			previewWrapperPanel = new JPanel();
			previewWrapperPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0), BorderFactory.createTitledBorder(null, "Preview", TitledBorder.CENTER, TitledBorder.BELOW_BOTTOM, null, null)));
			previewWrapperPanel.setLayout(gridLayout);
			previewWrapperPanel.add(getPreviewPanel(), null);
		}
		return previewWrapperPanel;
	}

	/**
	 * This method initializes eastPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getEastPanel() {
		if (eastPanel == null) {
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 1;
			gridBagConstraints6.gridy = 2;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.gridy = 0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.weighty = 1.0D;
			gridBagConstraints2.gridy = 3;
			eastPanel = new JPanel();
			eastPanel.setLayout(new GridBagLayout());
			eastPanel.add(getSpacerPanel(), gridBagConstraints2);
			eastPanel.add(getPreviewWrapperPanel(), gridBagConstraints1);
			eastPanel.add(getAttributePanel(), gridBagConstraints6);
		}
		return eastPanel;
	}

	/**
	 * This method initializes spacerPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSpacerPanel() {
		if (spacerPanel == null) {
			spacerPanel = new JPanel();
		}
		return spacerPanel;
	}

	/**
	 * This method initializes attributePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getAttributePanel() {
		if (attributePanel == null) {
			attributePanel = new JPanel();
			attributePanel.setLayout(new BoxLayout(getAttributePanel(), BoxLayout.Y_AXIS));
		}
		return attributePanel;
	}

	/**
	 * This method initializes row3Panel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getRow3Panel() {
		if (row3Panel == null) {
			spacerLabel = new JLabel();
			spacerLabel.setText("  ");
			FlowLayout flowLayout3 = new FlowLayout();
			flowLayout3.setAlignment(java.awt.FlowLayout.LEFT);
			flowLayout3.setVgap(0);
			flowLayout3.setHgap(0);
			row3Panel = new JPanel();
			row3Panel.setLayout(flowLayout3);
			row3Panel.add(getBoundedRadioButton(), null);
			row3Panel.add(spacerLabel, null);
			row3Panel.add(getUnboundedRadioButton(), null);
		}
		return row3Panel;
	}

	/**
	 * This method initializes row2Panel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getRow2Panel() {
		if (row2Panel == null) {
			FlowLayout flowLayout4 = new FlowLayout();
			flowLayout4.setHgap(0);
			flowLayout4.setVgap(0);
			fpcLabel = new JLabel();
			fpcLabel.setText("   Feet per cell");
			row2Panel = new JPanel();
			row2Panel.setLayout(flowLayout4);
			row2Panel.add(getFeetPerCellTextField(), null);
			row2Panel.add(fpcLabel, null);
		}
		return row2Panel;
	}
	
}  //  @jve:decl-index=0:visual-constraint="10,10"
