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
package net.rptools.maptool.model;

import java.awt.Color;

public class Label {

    private GUID id;
    private String label;
    private int x, y;
    private boolean showBackground;
    private int foregroundColor;
    
    public Label() {
        this("");
    }
    
    public Label(String label) {
        this(label, 0, 0);
    }

    public Label(String label, int x, int y) {
        id = new GUID();
        this.label = label;
        this.x = x;
        this.y = y;
        showBackground = true;
    }
    
    public Label(Label label) {
    	this(label.label, label.x, label.y);
    }
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public GUID getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

	public boolean isShowBackground() {
		return showBackground;
	}

	public void setShowBackground(boolean showBackground) {
		this.showBackground = showBackground;
	}

	public Color getForegroundColor() {
		return new Color(foregroundColor);
	}

	public void setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor.getRGB();
	}
    
}
