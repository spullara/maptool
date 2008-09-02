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

import java.awt.image.ImageObserver;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AssetPanelModel implements PropertyChangeListener {

    private ImageFileTreeModel imageFileTreeModel;

    private List<ImageObserver> observerList = new CopyOnWriteArrayList<ImageObserver>();
    
    public AssetPanelModel() {
        imageFileTreeModel = new ImageFileTreeModel();
    }
    
    public ImageFileTreeModel getImageFileTreeModel() {
        return imageFileTreeModel;
    }
    
    public void removeRootGroup(Directory dir) {
        
        imageFileTreeModel.removeRootGroup(dir);
        dir.removePropertyChangeListener(this);
    }
    
    public void addRootGroup(Directory dir) {
    	
    	if (imageFileTreeModel.containsRootGroup(dir)) {
    		return;
    	}
    	
        dir.addPropertyChangeListener(this);
        imageFileTreeModel.addRootGroup(dir);
    }
    
    public void addImageUpdateObserver(ImageObserver observer) {
        if (!observerList.contains(observer)) {
            observerList.add(observer);
        }
    }
    
    public void removeImageUpdateObserver(ImageObserver observer) {
        observerList.remove(observer);
    }
    
    ////
    // PROPERTY CHANGE LISTENER
    public void propertyChange(PropertyChangeEvent evt) {

        for (ImageObserver observer : observerList) {
            observer.imageUpdate(null, ImageObserver.ALLBITS, 0, 0, 0, 0);
        }
    }
}
