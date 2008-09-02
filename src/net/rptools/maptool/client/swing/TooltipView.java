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

public class TooltipView extends InlineView {

    /**
     * Constructs a new view wrapped on an element.
     *
     * @param elem the element
     */
    public TooltipView(Element elem) {
    	super(elem);
    }
    
    @Override
    public String getToolTipText(float x, float y, Shape allocation) {
    	AttributeSet att;
	    	
	    att = (AttributeSet)getElement().getAttributes().getAttribute(HTML.Tag.A);
    	if (att != null)
	    	return (String)att.getAttribute(HTML.Attribute.HREF);
    	
    	att = (AttributeSet)getElement().getAttributes().getAttribute(HTML.Tag.SPAN);
    	if (att != null)
	    	return (String)att.getAttribute(HTML.Attribute.TITLE);
    	
    	return null;
    }
}
