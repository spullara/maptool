package net.rptools.maptool.client.ui.htmlframe;

import java.awt.AWTEventMulticaster;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.StringReader;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JEditorPane;
import javax.swing.ToolTipManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.functions.MacroLinkFunction;
import net.rptools.parser.ParserException;

@SuppressWarnings("serial")
public class HTMLPane extends JEditorPane {
	
	private ActionListener actionListeners;
	private HTMLPaneEditorKit editorKit;
	
	HTMLPane() {
		registerEditorKitForContentType("text/html", "net.rptools.maptool.client.ui.htmlframe.HTMLPaneEditorKit");
		editorKit = new HTMLPaneEditorKit(this);
		setEditorKit(editorKit);
		setContentType("text/html");
		setEditable(false);
		
		addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if (e.getURL() != null) {
						MapTool.showDocument(e.getURL().toString());
					} else {
						Matcher m = Pattern.compile("([^:]*)://([^/]*)/([^?]*)(?:\\?(.*))?").matcher(e.getDescription());
						if (m.matches()) {
							if (m.group(1).equalsIgnoreCase("macro")) {
								MacroLinkFunction.getInstance().runMacroLink(e.getDescription());
							}
						}
					}				
				}
			} 
		});
		
		ToolTipManager.sharedInstance().registerComponent(this);
				
	}
	
	
	public void addActionListener(ActionListener listener) {
		actionListeners = AWTEventMulticaster.add(actionListeners, listener);
	}
	
	public void removeActionListener(ActionListener listener) {
		actionListeners = AWTEventMulticaster.remove(actionListeners, listener);
	}
	
	
	/**
	 * Handle a submit.
	 * @param method The method of the submit.
	 * @param action The action for the submit.
	 * @param data The data from the form.
	 */
	void doSubmit(String method, String action, String data) {
		if (actionListeners != null) {
			actionListeners.actionPerformed(new FormActionEvent(method, action, data));
		}
	}
	
	/**
	 * Handle a change in title.
	 * @param title The title to change to.
	 */
	private void doChangeTitle(String title) {
		if (actionListeners != null) {
			actionListeners.actionPerformed(new ChangeTitleActionEvent(title));
		}
	}

	/**
	 * Handle a request to register a macro callback.
	 * @param type The type of event.
	 * @param link The link to the macro.
	 */
	private void doRegisterMacro(String type, String link) {
		if (actionListeners != null) {
			actionListeners.actionPerformed(new RegisterMacroActionEvent(type, link));
		}
	}
	
	
	/**
	 * Handle any meta tag information in the html.
	 * @param name the name of the meta tag.
	 * @param content the content of the meta tag.
	 */
	private void handleMetaTag(String name, String content) {
		if (actionListeners != null) {
			actionListeners.actionPerformed(new MetaTagActionEvent(name, content));
		}
	}


	@Override
	public void setText(String text) {
		// Set up the default style sheet
		
		HTMLDocument document = (HTMLDocument)getDocument();
		
		StyleSheet style = document.getStyleSheet();
		style.addRule("body { font-family: sans-serif; font-size: " + AppPreferences.getFontSize() + "pt; background: #ECE9D8}");
		style.addRule("div {margin-bottom: 5px}");
		style.addRule("span.roll {background:#efefef}");

	    HTMLEditorKit.Parser parse = editorKit.getParser();
	    try {
	    	super.setText("");
			parse.parse(new StringReader(text), new ParserCallBack(), true);
		} catch (IOException e) {
			// Do nothing, we should not get an io exception on string
		}

		
		
		// We use ASCII control characters to mark off the rolls so that there's no limitation on what (printable) characters the output can include
		text = text.replaceAll("\036([^\036\037]*)\037([^\036]*)\036", "<span class='roll' title='&#171; $1 &#187;'>$2</span>");
		text = text.replaceAll("\036\01u\02([^\036]*)\036", "&#171; $1 &#187;");
		text = text.replaceAll("\036([^\036]*)\036", "&#171;<span class='roll' style='color:blue'>&nbsp;$1&nbsp;</span>&#187;");

		// Auto inline expansion
		text = text.replaceAll("(^|\\s)(http://[a-zA-Z0-9_\\.%-/~?]+)", "$1<a href=\"$2\">$2</a>");
		super.setText(text);
		
		
	    

	}
	
	/**
	 * Class that listens for form events. 
	 *
	 */
	public class FormActionEvent extends ActionEvent {
		private final String method;
		private final String action;
		private final String data;
		
		private FormActionEvent(String method, String action, String data) {
			super(HTMLPane.this, 0, "submit");
			
			this.method = method;
			this.action = action;
			this.data  = data;
		}
		
		
		public String getMethod() {
			return method;
		}
		
		public String getAction() {
			return action;
		}
		
		public String getData() {
			return data;
		}
		
	}
	
	
	/**
	 * Action event for changing title of the container.
	 */
	public class ChangeTitleActionEvent extends ActionEvent {
		private final String newTitle;
		
		public ChangeTitleActionEvent(String title) {
			super(HTMLPane.this, 0, "changeTitle");
			newTitle = title;
		}
		
		/**
		 * Gets the new title.
		 * @return
		 */
		public String getNewTitle() {
			return newTitle;
		}
	}
	
	
	public class MetaTagActionEvent extends ActionEvent {
		private final String name;
		private final String content;
		
		public MetaTagActionEvent(String name, String content) {
			super(HTMLPane.this, 0, "metaTag");
			this.name = name;
			this.content = content;
		}
		
		/**
		 * Gets the name of the meta tag.
		 * @return the name of the meta tag.
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * Gets the content for the meta tag.
		 * @return the content of the meta tag.
		 */
		public String getContent() {
			return content;
		}
		
		
	}

	
	/**
	 * Action event for registering a macro
	 */
	public class RegisterMacroActionEvent extends ActionEvent {
		private final String type;
		private final String macro;
		
		RegisterMacroActionEvent(String type, String macro) {
			super(HTMLPane.this, 0, "registerMacro");
			this.type = type;
			this.macro = macro;
		}
		
		/**
		 * Gets the type of macro to register.
		 * @return the type of macro.
		 */
		public String getType() {
			return type;
		}
		
		/**
		 * Gets the link to the macro.
		 * @return the link to the macro.
		 */
		public String getMacro() {
			return macro;
		}
		
		
	}
	
	
	
	/**
	 * Class that deals with html parser callbacks.
	 */
	class ParserCallBack extends HTMLEditorKit.ParserCallback {
		private Stack<HTML.Tag> tagStack = new Stack<HTML.Tag>();
		
		
		public void handleStartTag(HTML.Tag tag, MutableAttributeSet attributes, int position) {
			tagStack.push(tag);
			if (tag == HTML.Tag.LINK) {
				handleLinkTag(attributes);
			} else if (tag == HTML.Tag.META) {
				handleMetaTag(attributes);
			}			
		}
		
		public void handleEndTag(HTML.Tag tag, int position) {
			tagStack.pop();			
		}
		
		public void handleText(char[] text, int position) {
			if (tagStack.peek() == HTML.Tag.TITLE) {
				doChangeTitle(String.valueOf(text));
			}		
		}
		
		public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attributes, int pos) {
			if (tag == HTML.Tag.LINK) {
				handleLinkTag(attributes);
			} else if (tag == HTML.Tag.META) {
				handleMetaTag(attributes);
			}
		}
		
		public void handleError(String errorMsg, int pos){
		}

				
		/**
		 * Handles meta tags.
		 * @param attributes the attributes for the tag.
		 */
		void handleMetaTag(MutableAttributeSet attributes) {
			Object name = attributes.getAttribute(HTML.Attribute.NAME);
			Object content = attributes.getAttribute(HTML.Attribute.CONTENT);
			
			if (name != null && content != null) {
				HTMLPane.this.handleMetaTag(name.toString(), content.toString());
			}
		}
		

		/**
		 * Handles all the actions for a HTML Link tag.
		 * @param attributes The attributes for the tag.
		 */
		void handleLinkTag(MutableAttributeSet attributes) {
			Object rel = attributes.getAttribute(HTML.Attribute.REL);
			Object type = attributes.getAttribute(HTML.Attribute.TYPE);
			Object href = attributes.getAttribute(HTML.Attribute.HREF);

			if (rel != null && type != null && href != null) {
				if (rel.toString().equalsIgnoreCase("stylesheet")) {
					String[] vals = href.toString().split("@");
					if (vals.length != 2) {
						return;
					}
					try {
						String cssText = MapTool.getParser().getTokenLibMacro(vals[0], vals[1]);
						HTMLDocument document = (HTMLDocument)getDocument();
						StyleSheet style = document.getStyleSheet();
						style.loadRules(new StringReader(cssText), null);						
					} catch (ParserException e) {
						// Do nothing
					} catch (IOException e) {
						// Do nothing
					}
				} else if (type.toString().equalsIgnoreCase("macro")) {
					if (rel.toString().equalsIgnoreCase("onChangeImpersonated")) {
						doRegisterMacro("onChangeImpersonated", href.toString());
					} else if (rel.toString().equalsIgnoreCase("onChangeSelection")) {
						doRegisterMacro("onChangeSelection", href.toString());					
					} else if (rel.toString().equalsIgnoreCase("onChangeToken")) {
						doRegisterMacro("onChangeToken", href.toString());
					}
				}
			}
		}
	}
	
	

}