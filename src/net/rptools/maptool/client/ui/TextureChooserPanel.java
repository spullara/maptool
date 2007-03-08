package net.rptools.maptool.client.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import net.rptools.lib.FileUtil;
import net.rptools.lib.swing.AbstractPaintChooserPanel;
import net.rptools.lib.swing.ColorPicker;
import net.rptools.lib.swing.ImagePanel;
import net.rptools.lib.swing.ImagePanelModel;
import net.rptools.lib.swing.SelectionListener;
import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.client.AppUtil;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.util.ImageManager;

public class TextureChooserPanel extends AbstractPaintChooserPanel {

	private ColorPicker colorPicker;
	private ImagePanel imagePanel;
	
	public TextureChooserPanel(ColorPicker picker) {
		setLayout(new GridLayout());

		colorPicker = picker;

		imagePanel = new ImagePanel();
		imagePanel.setSelectionMode(ImagePanel.SelectionMode.SINGLE);
		imagePanel.setModel(new TextureChooserImagePanelModel());
		imagePanel.addSelectionListener(new SelectionListener(){
			public void selectionPerformed(List<Object> selectedList) {
				if (selectedList == null || selectedList.size() == 0) {
					return;
				}
				colorPicker.getPaintChooser().setPaint(new AssetPaint((Asset)selectedList.get(0)));
			}
		});
		imagePanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {

					final List<Object> selectedList = imagePanel.getSelectedIds();
					if (selectedList == null || selectedList.size() == 0) {
						return;
					}

					JPopupMenu menu = new JPopupMenu();
					menu.add(new JMenuItem(new AbstractAction() {
						{
							putValue(NAME, "Delete from palette");
						}
						public void actionPerformed(ActionEvent e) {
							removeTexture((Asset)selectedList.get(0));
							colorPicker.getPaintChooser().setPaint(Color.black);
						}
					}));
					
					menu.show(imagePanel, e.getX(), e.getY());
				}
			}
		});
		imagePanel.setBackground(Color.white);	
		imagePanel.setPreferredSize(new Dimension(400, 400));
		add(new JScrollPane(imagePanel));
	}
	


	public void addTexture(Asset asset) {
		((TextureChooserImagePanelModel)imagePanel.getModel()).addAsset(asset);
		repaint();
	}
	
	private void removeTexture(Asset asset) {
		((TextureChooserImagePanelModel)imagePanel.getModel()).removeAsset(asset);
		repaint();
	}
	
	@Override
	public String getDisplayName() {
		return "Texture";
	}

	private static class TextureChooserImagePanelModel implements ImagePanelModel {

		private List<Asset> textureList = new ArrayList<Asset>();
		public TextureChooserImagePanelModel() {

			try {
				Asset asset = new Asset("Cobblestone", FileUtil.loadResource("net/rptools/lib/resource/image/texture/cobblestone.jpg"));
				textureList.add(asset);
								
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			
			loadSavedTextures();
		}
		
		public void addAsset(Asset asset) {
			if (!textureList.contains(asset)) {
				textureList.add(asset);
				saveNewTextureAsset(asset); // save the file
				setSavedTextures(); // remember the texture
			}
		}
		
		public void removeAsset(Asset asset) {
			if (textureList.contains(asset)) {
				textureList.remove(asset);
				removeTextureAsset(asset); // delete the file
				setSavedTextures(); // forget the texture
			}
		}
		
		private void removeTextureAsset(Asset asset)  {
			File file = getTextureFile(asset);
			if (file.exists()) {
				file.delete();
			}
		}
		
		private void saveNewTextureAsset(Asset asset) {
			
			File textureFile = getTextureFile(asset);
            
			// Running this in its own thread appears to only get run when the panel is opened,
			// as opposed to when the texture is added... so if a texture is added, and the panel
			// is never opened, it won't get saved.  Need to correct IF we want it to run in it's own thread
			
			//new Thread() {
               // public void run() {
                    
					try {
						// write the image data
						OutputStream out = new FileOutputStream(textureFile);
						out.write(asset.getImage());
						out.close();
		
					} catch (IOException ioe) {
						System.err.println("Could not save the texture: " + ioe);
						return;
					}
               // }
           // }.start();

		}
		
		private File getTextureFile(Asset asset) {
			return new File(AppUtil.getAppHome("paintTextures"), AssetManager.getAssetCacheFile(asset).getName());
            
		}
		
		private void setSavedTextures() {
			List<File> textureFiles = new ArrayList<File>();
			for (ListIterator<Asset> iter = textureList.listIterator(); iter.hasNext();) {
				textureFiles.add( getTextureFile( iter.next() ) );
			}
			AppPreferences.setSavedPaintTextures(textureFiles);
		}
		
		private void loadSavedTextures() {
			List<File> textureFiles = AppPreferences.getSavedPaintTextures();
			
			if (textureFiles.size() != 0) {
				File nextTexture;
				Asset asset;
				for (ListIterator<File> iter = textureFiles.listIterator(); iter.hasNext();) {
					
					nextTexture = iter.next();
					//textureName = FileUtil.getNameWithoutExtension(nextTexture);
					if(nextTexture.exists()) {
						try {
							byte[] data = FileUtil.loadFile(nextTexture);
							asset = new Asset("texture", data);
							textureList.add(asset);
							
						} catch (IOException ioe) {
							ioe.printStackTrace();		
						}
					}
					else {
						// In case the file has been removed manually
						iter.remove();
					}
				}
			}
		}
		
		public String getCaption(int index) {
			return null;
		}
		public Object getID(int index) {
			return index >= 0 ? textureList.get(index) : null;
		}
		public Image getImage(int index) {
			return ImageManager.getImageAndWait(textureList.get(index));
		}
		public Image getImage(Object ID) {
			return ImageManager.getImageAndWait((Asset) ID);
		}
		public int getImageCount() {
			return textureList.size();
		}
		public Transferable getTransferable(int index) {
			return null;
		}
	}
}
