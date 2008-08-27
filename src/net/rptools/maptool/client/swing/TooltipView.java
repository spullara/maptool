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
