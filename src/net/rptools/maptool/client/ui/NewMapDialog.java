package net.rptools.maptool.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.lib.util.ImageUtil;
import net.rptools.maptool.client.AppConstants;

public class NewMapDialog extends JDialog {

	private JPanel jContentPane = null;
	private JPanel bottomPanel = null;
	private JPanel buttonPanel = null;
	private JPanel eastPanel = null;
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

	/**
	 * This is the default constructor
	 */
	public NewMapDialog() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(600, 450);
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
			jContentPane.add(getEastPanel(), java.awt.BorderLayout.EAST);
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
	private JPanel getEastPanel() {
		if (eastPanel == null) {
			eastPanel = new JPanel();
			eastPanel.setLayout(new BorderLayout());
			eastPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(3,5,3,5));
			eastPanel.add(getPreviewPanel(), java.awt.BorderLayout.CENTER);
			eastPanel.add(getOptionsPanel(), java.awt.BorderLayout.SOUTH);
		}
		return eastPanel;
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
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.gridx = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridwidth = 2;
			gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridy = 2;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			optionsPanel = new JPanel();
			optionsPanel.setLayout(new GridBagLayout());
			optionsPanel.add(getUnboundedRadioButton(), gridBagConstraints2);
			optionsPanel.add(getBoundedRadioButton(), gridBagConstraints);
			optionsPanel.add(getAdjustGridButton(), gridBagConstraints1);
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
			typeTabbedPane.addTab("Library", null, getLibraryPanel(), null);
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
			imageFileChooser.set
			imageFileChooser.addPropertyChangeListener(new FileSystemSelectionHandler());
			
		}
		return imageFileChooser;
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
				
				imagePreviewPanel.setImage(selectedFile);
			}
		}
	}
	
	public static void main(String[] args) {
		
		NewMapDialog d = new NewMapDialog();
		SwingUtil.centerOnScreen(d);
		d.setVisible(true);
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
