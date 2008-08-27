package net.rptools.maptool.client.swing;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

public class MessagePanelEditorKit extends HTMLEditorKit {

	private ViewFactory viewFactory = new MessagePanelViewFactory();

	private ImageLoaderCache imageCache = new ImageLoaderCache();

	public MessagePanelEditorKit() {
		viewFactory = new MessagePanelViewFactory();
	}
	
	@Override
	public ViewFactory getViewFactory() {
		return viewFactory;
	}
	
	public void flush() {
		imageCache.flush();
	}
	
	private class MessagePanelViewFactory extends HTMLFactory {

		@Override
		public View create(Element elem) {
			
	        AttributeSet attrs = elem.getAttributes();
	 	    Object elementName = attrs.getAttribute(AbstractDocument.ElementNameAttribute);
		    Object o = (elementName != null) ? null : attrs.getAttribute(StyleConstants.NameAttribute);
		    if (o instanceof HTML.Tag) {
				HTML.Tag kind = (HTML.Tag) o;
				if (kind == HTML.Tag.IMG) {
				    return new MessagePanelImageView(elem, imageCache);
				}
				if (kind == HTML.Tag.CONTENT) {
					return new TooltipView(elem);
				}
		    }

			return super.create(elem);
		}
	}
}
