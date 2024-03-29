/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *  
 *	http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package net.rptools.maptool.client.ui.htmlframe;

import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
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
		View view;		// For debugging purposes (no easy way to see a return value in Eclipse)

		if (tagType == HTML.Tag.INPUT || tagType == HTML.Tag.SELECT ||
				tagType == HTML.Tag.TEXTAREA) {
			view = new HTMLPaneFormView(element, htmlPane);
		} else {
			if (viewFactory != null) {
				view = viewFactory.create(element);
			} else {
				view = super.create(element);
			}
		}
		return view;
	}
}
