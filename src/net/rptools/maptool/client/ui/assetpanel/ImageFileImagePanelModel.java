/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package net.rptools.maptool.client.ui.assetpanel;

import java.awt.Color;
import java.awt.Image;
import java.awt.Paint;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import net.rptools.lib.FileUtil;
import net.rptools.lib.image.ImageUtil;
import net.rptools.lib.swing.ImagePanelModel;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.TransferableAsset;
import net.rptools.maptool.client.TransferableToken;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.util.ImageManager;
import net.rptools.maptool.util.PersistenceUtil;

public class ImageFileImagePanelModel implements ImagePanelModel {

	private static final Color TOKEN_BG_COLOR = new Color(255, 250, 205);
	private static Image rptokenDecorationImage;
	
	static {
		try {
			rptokenDecorationImage = ImageUtil.getImage("net/rptools/maptool/client/image/rptokIcon.png");
		} catch (IOException ioe) {
			rptokenDecorationImage = null;
		}
	}
	
	private Directory dir;
    private String filter;
	private List<File> fileList;
    
	public ImageFileImagePanelModel(Directory dir) {
		this.dir = dir;
		refresh();
	}

	public void setFilter(String filter) {
		this.filter = filter.toUpperCase();
		refresh();
	}
	
	public int getImageCount() {
		return fileList.size();
	}

	public Paint getBackground(int index) {
		return Token.isTokenFile(fileList.get(index).getName()) ? TOKEN_BG_COLOR : null;
	}
	
	public Image[] getDecorations(int index) {
		return Token.isTokenFile(fileList.get(index).getName()) ? new Image[]{rptokenDecorationImage} : null;
	}
	
	public Image getImage(int index) {

        Image image = null;
		if (dir instanceof AssetDirectory) {
			
			image = ((AssetDirectory) dir).getImageFor(fileList.get(index));
		}

		return image != null ?  image : ImageManager.UNKNOWN_IMAGE;
	}

	public Transferable getTransferable(int index) {
		Asset asset = null;
		
		File file = fileList.get(index);
		if (file.getName().toLowerCase().endsWith(Token.FILE_EXTENSION)) {
			
			try {
				Token token = PersistenceUtil.loadToken(file);
				
				return new TransferableToken(token);
			} catch (IOException ioe) {
				ioe.printStackTrace();
				MapTool.showError("Could not load that token: " + ioe);
				return null;
			}
		}
		
		if (dir instanceof AssetDirectory) {
			asset = getAsset(index);
            
            if (asset == null) {
                return null;
            }
			
			// Now is a good time to tell the system about it
			AssetManager.putAsset(asset);
		}
		
		return asset != null ? new TransferableAsset(asset) : null;
	}
    
    public String getCaption(int index) {
    	if (index < 0 || index >= fileList.size()) {
    		return null;
    	}
    	
    	String name = fileList.get(index).getName();
        return FileUtil.getNameWithoutExtension(name);
    }
    
    public Object getID(int index) {
        return new Integer(index);
    }
    
    public Image getImage(Object ID) {
        return getImage(((Integer)ID).intValue());
    }
    
    public Asset getAsset(int index) {
    	if (index < 0) {
    		return null;
    	}
    	
        try {
            Asset asset = AssetManager.createAsset(fileList.get(index));
    		return asset;
        } catch (IOException ioe) {
            return null;
        }
    }
    
    private void refresh() {
    	fileList = new ArrayList<File>();
    	fileList.addAll(dir.getFiles());
    	if (filter != null && filter.length() > 0) {
	    	for (ListIterator<File> iter = fileList.listIterator(); iter.hasNext();) {
	    		File file = iter.next();
	    		if (!file.getName().toUpperCase().contains(filter)) {
	    			iter.remove();
	    		}
	    	}
    	}
    	Collections.sort(fileList, filenameComparator);
    }
    
    private static Comparator<File> filenameComparator = new Comparator<File>() {
    	public int compare(File o1, File o2) {
    		return o1.getName().compareToIgnoreCase(o2.getName());
    	}
    };
}
