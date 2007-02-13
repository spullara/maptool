package net.rptools.maptool.client.ui;

import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.io.File;
import java.io.IOException;

import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.util.PersistenceUtil;

import net.rptools.lib.image.ImageUtil;

/*
 * A File chooser with an image preview panel
 */
public class PreviewPanelFileChooser extends JFileChooser {
	
	private JPanel previewWrapperPanel;
	private ImagePreviewPanel browsePreviewPanel;
	
	public PreviewPanelFileChooser() {
		this.setCurrentDirectory(AppPreferences.getLoadDir());	
		this.setAccessory(getPreviewWrapperPanel());
		this.addPropertyChangeListener(PreviewPanelFileChooser.SELECTED_FILE_CHANGED_PROPERTY,
				new FileSystemSelectionHandler());
	}
	
	private class FileSystemSelectionHandler implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			File previewFile = getImageFileOfSelectedFile();
			
			if (previewFile != null && !previewFile.isDirectory()) {
				try {	
					Image img = ImageUtil.getImage(previewFile);
					getPreviewPanel().setImage(img);
				} catch (IOException ioe) {
					getPreviewPanel().setImage(null);
				}
			} else {
				getPreviewPanel().setImage(null);
			}
		}
	}
	
	//Override if selected file is not also the image
	protected File getImageFileOfSelectedFile() {
		return getSelectedFile();
	}
	
	
	private JPanel getPreviewWrapperPanel() {
		if (previewWrapperPanel == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(1);
			gridLayout.setColumns(1);
			previewWrapperPanel = new JPanel();
			previewWrapperPanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(0, 5, 0, 0), BorderFactory
							.createTitledBorder(null, "Preview",
									TitledBorder.CENTER,
									TitledBorder.BELOW_BOTTOM, null, null)));
			previewWrapperPanel.setLayout(gridLayout);
			previewWrapperPanel.add(getPreviewPanel(), null);
		}
		return previewWrapperPanel;
	}
	
	private ImagePreviewPanel getPreviewPanel() {
		if (browsePreviewPanel == null) {

			browsePreviewPanel = new ImagePreviewPanel();
		}

		return browsePreviewPanel;
	}

}
