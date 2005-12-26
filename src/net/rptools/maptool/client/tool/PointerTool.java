/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation files 
 * (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN 
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */
package net.rptools.maptool.client.tool;

import java.io.IOException;

import javax.swing.ImageIcon;

import net.rptools.lib.image.ImageUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.language.I18N;

/**
 */
public class PointerTool extends DefaultTool {

	public PointerTool () {
        try {
            setIcon(new ImageIcon(ImageUtil.getImage("net/rptools/maptool/client/image/tool/PointerBlue16.png")));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
	@Override
	public String getInstructions() {
		return I18N.getText("tool.pointer.instructions");
	}
	
    @Override
    public String getTooltip() {
        return "Pointer tool";
    }
}
