package net.rptools.maptool.client.ui.htmlframe;

import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLEditorKit;

import net.rptools.maptool.client.swing.MessagePanelEditorKit;

@SuppressWarnings("serial")
class HTMLPaneEditorKit extends MessagePanelEditorKit {
	private final HTMLPaneViewFactory viewFactory;

	HTMLPaneEditorKit(HTMLPane htmlPane) {
		setUseMacroLinkToolTips(false);
		viewFactory = new HTMLPaneViewFactory(super.getViewFactory(), htmlPane);
	}

	@Override
	public ViewFactory getViewFactory() {
		return viewFactory;
	}

	@Override
	public HTMLEditorKit.Parser getParser() {
		return super.getParser();
	}
}
