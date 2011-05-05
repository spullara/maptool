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
package net.rptools.maptool.client.ui.macrobuttons.buttons;

import java.io.Serializable;

import net.rptools.maptool.model.MacroButtonProperties;

public class TransferData implements Serializable {
		
	public int index=0;
	public String command="";
	public String colorKey="";
	public String hotKey="";
	public String label="";
	public String group="";
	public String sortby="";
	public boolean autoExecute=true;
	public boolean includeLabel=false;
	public boolean applyToTokens=true;
	public String fontColorKey="";
	public String fontSize="";
	public String minWidth="";
	public String maxWidth="";
	public String panelClass="";
	public String toolTip="";
	
	public TransferData(MacroButton button) {
		MacroButtonProperties prop = button.getProperties();
		this.index = prop.getIndex();
		this.label = prop.getLabel();
		this.command = prop.getCommand();
		this.colorKey = prop.getColorKey();
		this.hotKey = prop.getHotKey();
		this.group = prop.getGroup();
		this.sortby = prop.getSortby();
		this.autoExecute = prop.getAutoExecute();
		this.includeLabel = prop.getIncludeLabel();
		this.applyToTokens = prop.getApplyToTokens();
		this.panelClass = button.getPanelClass();
		this.fontColorKey = prop.getFontColorKey();
		this.fontSize = prop.getFontSize();
		this.minWidth = prop.getMinWidth();
		this.maxWidth = prop.getMaxWidth();
		this.toolTip = prop.getToolTip();
	}

}
