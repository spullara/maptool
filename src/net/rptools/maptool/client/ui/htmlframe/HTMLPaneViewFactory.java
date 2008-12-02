package net.rptools.maptool.client.ui.htmlframe;

import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.Element;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit.HTMLFactory;

public class HTMLPaneViewFactory extends HTMLFactory {
	
	/** The view factory to delegate unknown tags to. */
	private final ViewFactory viewFactory;
	
	/** The HTML Pane that we belong to, required for processing form events. */
	private final HTMLPane htmlPane;

	/**
	 * Creates a new HTMLPaneViewFactory.
	 * @param delegate The view factory to delegate unknown tags to.
	 * @param formPane The HTMLPane that we are creating tags for.
	 */
	public HTMLPaneViewFactory(ViewFactory delegate, HTMLPane formPane) {
		viewFactory = delegate;
		htmlPane = formPane;
	}
	
	/**
	 * Creates a new HTMLPaneViewFactory.
	 * @param formPane The HTMLPane that we are creating tags for.
	 */
	public HTMLPaneViewFactory(HTMLPane formPane) {
		this(null, formPane);
	}

	/**
	 * Creates a view for the specified element.
	 * @param element The element to create the view for.
	 * @return the view for the element.
	 */
	@Override
	public View create(Element element) {
		HTML.Tag tagType = (HTML.Tag)element.getAttributes().getAttribute(StyleConstants.NameAttribute);
		
		if (tagType == HTML.Tag.INPUT || tagType == HTML.Tag.SELECT ||
				tagType == HTML.Tag.TEXTAREA) {
			return new HTMLPaneFormView(element, htmlPane);
		} else {
			if (viewFactory != null) {
				return viewFactory.create(element);
			} else {
				return super.create(element);
			}
		}
	}
}
