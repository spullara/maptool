package net.rptools.maptool.client.ui.htmlframe;

import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.FormView;
import javax.swing.text.html.HTML;

public class HTMLPaneFormView extends FormView {
	private HTMLPane htmlPane;
	
	/**
	 * Creates a new HTMLPaneFormView.
	 * @param elem The element this is a view for.
	 * @param pane The HTMLPane this element resides on.
	 */
	public HTMLPaneFormView(Element elem, HTMLPane pane) {
		super(elem);
		htmlPane = pane;
	}

	
	@Override
	protected void submitData(String data) {
		// Find the form
		Element formElement = null;
		for (Element e = getElement(); e != null; e = e.getParentElement()) {
			if (e.getAttributes().getAttribute(StyleConstants.NameAttribute) == HTML.Tag.FORM) {
				formElement = e;
				break;
			}
		}
		
		if (formElement != null) {
			AttributeSet att = formElement.getAttributes();
			String action = att.getAttribute(HTML.Attribute.ACTION).toString();
			String method;
			if (att.getAttribute(HTML.Attribute.METHOD) != null) {
				method = att.getAttribute(HTML.Attribute.METHOD).toString();
			} else {
				method = "get";
			}
			action = action == null ? "" : action;
			method = method == null ? "" : method.toLowerCase();
			htmlPane.doSubmit(method, action, data);
		}
		
		
	}
	
	@Override
	protected void imageSubmit(String data) {
		submitData(data);
	}
	

}
