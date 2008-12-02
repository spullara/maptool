package net.rptools.maptool.client.ui.htmlframe;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import net.rptools.maptool.client.AppStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.functions.MacroLinkFunction;
import net.rptools.maptool.model.Token;

import com.jidesoft.docking.DockContext;
import com.jidesoft.docking.DockableFrame;

@SuppressWarnings("serial")
public class HTMLFrame extends DockableFrame implements HTMLPanelContainer {
	

	private static final Map<String, HTMLFrame> frames = new HashMap<String, HTMLFrame>();
	private final Map<String, String> macroCallbacks = new HashMap<String, String>();

	private HTMLPanel panel;

	
	/**
	 * Creates a new HTMLFrame and displays it or displays an existing frame.
	 * The width and height are ignored for existing frames so that they will not
	 * override the size that the player may have resized them to.
	 * @param name The name of the frame.
	 * @param title The title of the frame.
	 * @param width The width of the frame in pixels.
	 * @param height The height of the frame in pixels.
	 * @param html The html to display in the frame.
	 * @return The HTMLFrame that is displayed.
	 */
	static HTMLFrame showFrame(String name, String title, int width, int height, String html) {
		HTMLFrame frame;

		if (frames.containsKey(title)) {
			frame = frames.get(name);
			frame.updateContents(html);
			frame.setVisible(true);
			frame.getDockingManager().showFrame(title);
		} else {
			frame = new HTMLFrame(MapTool.getFrame(), name, title, width, height);
			frames.put(name, frame);
			frame.updateContents(html);
		}
		return frame;
	}


	/** 
	 * Creates a new HTMLFrame.
	 * @param parent The parent of this frame. 
	 * @param name the name of the frame.
	 * @param title The title of the frame.
	 * @param width
	 * @param height
	 */
	private HTMLFrame(Frame parent, String name, String title, int width,
			int height) {
		super(title, new ImageIcon(AppStyle.chatPanelImage));

		// Only set size on creation so we dont override players resizing.
		width = width < 100 ? 400 : width;
		height = height < 50 ? 200 : height;
		setPreferredSize(new Dimension(width, height));
		panel = new HTMLPanel(this, true, true); // closeOnSubmit is true so we dont get close button
		add(panel);
		this.getContext().setInitMode(DockContext.STATE_FLOATING);
		MapTool.getFrame().getDockingManager().addFrame(this);
		this.setVisible(true);
	}



	/**
	 * Updates the html contents of the frame.
	 * @param html the html contents.
	 */
	public void updateContents(String html) {
		macroCallbacks.clear();
		panel.updateContents(html, true); // input is true so we dont get close button		
	}

	
	/**
	 * The selected token list has changed.
	 */
	private void selectedChanged() {
		if (macroCallbacks.get("onChangeSelection") != null) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					MacroLinkFunction.getInstance().runMacroLink(macroCallbacks.get("onChangeSelection"));
				}
			});
		}
	}

	/**
	 * A new token has been impersonated or the impersonated token is cleared.
	 */
	private void impersonatedChanged() {
		if (macroCallbacks.get("onChangeImpersonated") != null) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					MacroLinkFunction.getInstance().runMacroLink(macroCallbacks.get("onChangeImpersonated"));
				}
			});
		}
	}

	/**
	 * One of the tokens has changed.
	 */
	private void tokenChanged(final Token token) {
		if (macroCallbacks.get("onChangeToken") != null) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					MacroLinkFunction.getInstance().runMacroLink(macroCallbacks.get("onChangeToken") + token.getId().toString());
				}
			});
		}
	}
	
	/**
	 * The selected token list has changed.
	 */
	public static void doSelectedChanged() {
		for (HTMLFrame frame : frames.values()) {
			if (frame.isVisible()) {
				frame.selectedChanged();
			}
		}
	}

	/**
	 * A new token has been impersonated or the impersonated token is cleared.
	 */
	public static void doImpersonatedChanged() {
		for (HTMLFrame frame : frames.values()) {
			if (frame.isVisible()) {
				frame.impersonatedChanged();
			}
		}
	}

	/**
	 * One of the tokens has changed.
	 */
	public static void doTokenChanged(Token token) {
		for (HTMLFrame frame : frames.values()) {
			if (frame.isVisible()) {
				frame.tokenChanged(token);
			}
		}
	}	
	
	
	
	public void closeRequest() {
		// Nothing to see here, move along.
	}

	public void actionPerformed(ActionEvent e) {
		if (e instanceof HTMLPane.FormActionEvent) {
			HTMLPane.FormActionEvent fae = (HTMLPane.FormActionEvent) e;
			MacroLinkFunction.getInstance().runMacroLink(fae.getAction() + fae.getData());
		} 

		if (e instanceof HTMLPane.RegisterMacroActionEvent) {
			HTMLPane.RegisterMacroActionEvent rmae = (HTMLPane.RegisterMacroActionEvent)e;
			macroCallbacks.put(rmae.getType(), rmae.getMacro());
		}
		
		if (e instanceof HTMLPane.ChangeTitleActionEvent) {
			this.setTitle(((HTMLPane.ChangeTitleActionEvent)e).getNewTitle());
		}
		if (e instanceof HTMLPane.MetaTagActionEvent) {
			HTMLPane.MetaTagActionEvent mtae = (HTMLPane.MetaTagActionEvent) e;
			if (mtae.getName().equalsIgnoreCase("onChangeToken")  ||
					   mtae.getName().equalsIgnoreCase("onChangeSelection") ||
					   mtae.getName().equalsIgnoreCase("onChangeImpersonated")) {
				macroCallbacks.put(mtae.getName(), mtae.getContent());
			}
		}

		
		if (e.getActionCommand().equals("Close")) {
			closeRequest();
		}
	}

}