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
package net.rptools.maptool.client.ui.tokenpanel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.util.ImageManager;

public class TokenListCellRenderer extends DefaultListCellRenderer {

    private BufferedImage image;
    private String name;
    
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof Token) {
            Token token = (Token) value;
            image = ImageManager.getImage(AssetManager.getAsset(token.getImageAssetId()), this);
            name = token.getName();
            
            setText(" "); // hack to keep the row height the right size
        }
        return this;
    }
    
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        
        if (image != null) {
            
            Dimension imageSize = new Dimension(image.getWidth(), image.getHeight());
            SwingUtil.constrainTo(imageSize, getSize().height);
            g.drawImage(image, 0, 0, imageSize.width, imageSize.height, this);
            g.drawString(name, imageSize.width + 2, g.getFontMetrics().getAscent());
        }
    }
}
