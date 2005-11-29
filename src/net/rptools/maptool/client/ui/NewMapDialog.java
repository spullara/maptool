package net.rptools.maptool.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.lib.util.ImageUtil;
import net.rptools.maptool.client.AppConstants;
import net.rptools.maptool.model.Zone;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.GridLayout;

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
	
	private File selectedFile;
	private JPanel textOptionPanel = null;
	private JLabel nameLabel = null;
	private JLabel feetPerCellLabel = null;
	private JTextField nameTextField = null;
	private JTextField feetPerCellTextField = null;
	private JPanel typePanel = null;
	private JPanel previewWrapperPanel = null;
	private JPanel mapButtonPanel = null;

	/**
	 * This is the default constructor
	 */
	public NewMapDialog(JFrame owner) {
		super(owner, true);
		initialize();
		
		addWindowListener(this);
	}

	@Override
	public void setVisible(boolean b) {

		if (b) {
			setSelectedImage(null);
			SwingUtil.centerOver(this, getOwner());
		}
		super.setVisible(b);
	}
	
	public int getZoneType() {
		return boundedRadioButton.isSelected() ? Zone.Type.MAP : Zone.Type.INFINITE;
	}
	
	public File showDialog() {
		
		setVisible(true);
		return selectedFile;
	}
	
	public void accept() {
		setVisible(false);
	}
	
	public void cancel() {
		
		selectedFile = null;
		setVisible(false);
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(480, 450);
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
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
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
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.gridx = 2;
			gridBagConstraints31.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints31.gridy = 1;
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
			optionsPanel.add(getTextOptionPanel(), gridBagConstraints11);
			optionsPanel.add(getPreviewWrapperPanel(), gridBagConstraints31);
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
						adjustGridButton.setEnabled(true);
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
			typeTabbedPane.addTab("Images", null, getImageExplorerPanel(), null);
			typeTabbedPane.addTab("Filesystem", null, getFilesystemPanel(), null);
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
			filesystemPanel.add(getCancelButton(), java.awt.BorderLayout.SOUTH);
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
			
		}
		return imageFileChooser;
	}
	
	private void setSelectedImage(File file) {
		
		selectedFile = file;
		imagePreviewPanel.setImage(selectedFile);
		
		getOkButton().setEnabled(file != null);
	}

	private class ImagePreviewWindow extends JComponent {
		
		private BufferedImage img;
		
		public ImagePreviewWindow() {
			setPreferredSize(new Dimension(100, 100));
			setMinimumSize(new Dimension(100, 100));
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
				g.drawImage(img, 0, 0, imgSize.width, imgSize.height, null);

				// Border
				g.setColor(Color.black);
				g.drawRect(0, 0, imgSize.width-1, imgSize.height-1);
			}	

		}
	}
	
	private class FileSystemSelectionHandler implements PropertyChangeListener {
		
		public void propertyChange(PropertyChangeEvent evt) {

			if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
				File selectedFile = getImageFileChooser().getSelectedFile();
				
				setSelectedImage(selectedFile);
			}
		}
	}
	
	////
	// WINDOW LISTENER
	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {
		cancel();
	}
	public void windowClosing(WindowEvent e) {}
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
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 2;
			gridBagConstraints.gridy = 2;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints7.gridy = 2;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.gridy = 1;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints6.gridx = 1;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridy = 0;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.gridwidth = 2;
			gridBagConstraints5.gridx = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints4.insets = new java.awt.Insets(0,0,0,5);
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints3.insets = new java.awt.Insets(0,0,0,5);
			gridBagConstraints3.gridy = 1;
			feetPerCellLabel = new JLabel();
			feetPerCellLabel.setText("Feet per cell:");
			nameLabel = new JLabel();
			nameLabel.setText("Name:");
			textOptionPanel = new JPanel();
			textOptionPanel.setLayout(new GridBagLayout());
			textOptionPanel.add(nameLabel, gridBagConstraints4);
			textOptionPanel.add(feetPerCellLabel, gridBagConstraints3);
			textOptionPanel.add(getNameTextField(), gridBagConstraints5);
			textOptionPanel.add(getFeetPerCellTextField(), gridBagConstraints6);
			textOptionPanel.add(getTypePanel(), gridBagConstraints7);
			textOptionPanel.add(getMapButtonPanel(), gridBagConstraints);
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
			feetPerCellTextField = new JTextField();
		}
		return feetPerCellTextField;
	}

	/**
	 * This method initializes typePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getTypePanel() {
		if (typePanel == null) {
			FlowLayout flowLayout1 = new FlowLayout();
			flowLayout1.setAlignment(java.awt.FlowLayout.LEFT);
			flowLayout1.setVgap(0);
			flowLayout1.setHgap(0);
			typePanel = new JPanel();
			typePanel.setLayout(flowLayout1);
			typePanel.add(getBoundedRadioButton(), null);
			typePanel.add(getUnboundedRadioButton(), null);
		}
		return typePanel;
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
			previewWrapperPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Preview", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			previewWrapperPanel.setLayout(gridLayout);
			previewWrapperPanel.add(getPreviewPanel(), null);
		}
		return previewWrapperPanel;
	}

	/**
	 * This method initializes mapButtonPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMapButtonPanel() {
		if (mapButtonPanel == null) {
			mapButtonPanel = new JPanel();
			mapButtonPanel.add(getAdjustGridButton(), null);
		}
		return mapButtonPanel;
	}
	
}  //  @jve:decl-index=0:visual-constraint="10,10"
