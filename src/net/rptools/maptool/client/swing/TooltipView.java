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
package net.rptools.maptool.client.swing;

import java.awt.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.client.functions.MacroLinkFunction;

public class TooltipView extends InlineView {

	private boolean mlToolTips;
    /**
     * Constructs a new view wrapped on an element.
     *
     * @param elem the element
     */
    public TooltipView(Element elem, boolean macroLinkToolTips) {
    	super(elem);
    	mlToolTips = macroLinkToolTips;
    }
    
    @Override
    public String getToolTipText(float x, float y, Shape allocation) {
    	AttributeSet att;
	    	
	    att = (AttributeSet)getElement().getAttributes().getAttribute(HTML.Tag.A);
    	if (att != null) {
    		String href = att.getAttribute(HTML.Attribute.HREF).toString();
    		if (href.startsWith("macro:")) {
    			boolean isInsideChat = mlToolTips;
    			boolean allowToolTipToShow = ! AppPreferences.getSuppressToolTipsForMacroLinks();
    			if (isInsideChat && allowToolTipToShow) {
    				return MacroLinkFunction.getInstance().macroLinkToolTip(href);
    			} 
    			// if we are not displaying macro link tooltips let if fall through so that any span tooltips will be displayed
    		} else  {
    			return href;
    		}
    	}
    	
    	att = (AttributeSet)getElement().getAttributes().getAttribute(HTML.Tag.SPAN);
    	if (att != null)
	    	return (String)att.getAttribute(HTML.Attribute.TITLE);
    	
    	return null;
    }
}
