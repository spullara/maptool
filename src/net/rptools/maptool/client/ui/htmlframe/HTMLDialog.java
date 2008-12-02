package net.rptools.maptool.client.ui.htmlframe;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.functions.MacroLinkFunction;
import net.rptools.maptool.model.Token;

@SuppressWarnings("serial")
public class HTMLDialog extends JDialog implements HTMLPanelContainer {
	
	
	private static Map<String, HTMLDialog> dialogs = new HashMap<String, HTMLDialog>();
	
	private final Map<String, String> macroCallbacks = new HashMap<String, String>();
	private boolean temporary;
	private boolean input;
	private HTMLPanel panel;
	private String name;
	private boolean canResize = true;
	private final Frame parent;
	
	
	/**
	 * Creates a HTMLDialog 
	 * @param parent The parent frame.
	 * @param name The name of the dialog.
	 * @param title The title of the dialog.
	 * @param undecorated If the dialog is decorated or not.
	 * @param width The width of the dialog.
	 * @param height The height of the dialog.
	 */
	private HTMLDialog(Frame parent, String name, String title, boolean undecorated, int width, int height) {
		super(parent, title, false);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeRequest();
			}
		});
		this.name = name;
		setUndecorated(undecorated);
		
		width = width < 100 ? 400: width;
		height = height < 50 ? 200 : width;
		setPreferredSize(new Dimension(width, height));

		panel = new HTMLPanel(this, !input, !undecorated);
		add(panel);
		pack();
		this.parent = parent;
		
		SwingUtil.centerOver(this, parent);
	}

	/**
	 * Shows the HTML Dialog. This will create a new dialog if the named dialog does not already exist.
	 * The width and height fields are ignored if the dialog has already been opened so that it will not
	 * override any resizing that the user may have done.
	 * @param name The name of the dialog.
	 * @param title The title for the dialog window .
	 * @param width The width in pixels of the dialog.
	 * @param height The height in pixels of the dialog.
	 * @param undecorated If the dialog is decorated or not.
	 * @param input Is the dialog an input only dialog.
	 * @param temp Is the dialog temporary.
	 * @param html The HTML to display in the dialog. 
	 * @return The dialog.
	 */
	static HTMLDialog showDialog(String name, String title, int width, int height, boolean undecorated, 
				boolean input, boolean temp, String html) {
		HTMLDialog dialog;
		if (dialogs.containsKey(name)) {
			dialog = dialogs.get(name);
			dialog.updateContents(html, temp, input);

		} else {
			dialog = new HTMLDialog(MapTool.getFrame(), name, title, undecorated, width, height);
			dialogs.put(name, dialog);
			dialog.updateContents(html, temp, input);
		}
//		dialog.canResize = false;
		dialog.setVisible(true);
		return dialog;
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
		for (HTMLDialog dialog : dialogs.values()) {
			if (dialog.isVisible()) {
				dialog.selectedChanged();
			}
		}
	}

	/**
	 * A new token has been impersonated or the impersonated token is cleared.
	 */
	public static void doImpersonatedChanged() {
		for (HTMLDialog dialog : dialogs.values()) {
			if (dialog.isVisible()) {
				dialog.impersonatedChanged();
			}
		}
	}

	/**
	 * One of the tokens has changed.
	 */
	public static void doTokenChanged(Token token) {
		for (HTMLDialog dialog : dialogs.values()) {
			if (dialog.isVisible()) {
				dialog.tokenChanged(token);
			}
		}
	}
	
	/**
	 * Updates the contents of the dialog.
	 * @param html The html contents of the dialog. 
	 * @param temp Is the dialog temporary or not.
	 * @param input Is it an input only dialog.
	 */
	private void updateContents(String html, boolean temp, boolean input) {
		this.input = input;
		this.temporary = temp;
		macroCallbacks.clear();
		panel.updateContents(html, input);
	}


	public void actionPerformed(ActionEvent e) {
		if (e instanceof HTMLPane.FormActionEvent) {
			HTMLPane.FormActionEvent fae = (HTMLPane.FormActionEvent) e;
			MacroLinkFunction.getInstance().runMacroLink(fae.getAction() + fae.getData());
			if (input) {
				closeRequest();
			}
		}

		if (e instanceof HTMLPane.ChangeTitleActionEvent) {
			this.setTitle(((HTMLPane.ChangeTitleActionEvent)e).getNewTitle());
		}

		if (e instanceof HTMLPane.RegisterMacroActionEvent) {
			HTMLPane.RegisterMacroActionEvent rmae = (HTMLPane.RegisterMacroActionEvent)e;
			macroCallbacks.put(rmae.getType(), rmae.getMacro());
		}
		
		if (e instanceof HTMLPane.MetaTagActionEvent) {
			HTMLPane.MetaTagActionEvent mtae = (HTMLPane.MetaTagActionEvent) e;
			if (mtae.getName().equalsIgnoreCase("input")) {
				Boolean val = Boolean.valueOf(mtae.getContent());
				panel.updateContents(val);
			} else if (mtae.getName().equalsIgnoreCase("onChangeToken")  ||
					   mtae.getName().equalsIgnoreCase("onChangeSelection") ||
					   mtae.getName().equalsIgnoreCase("onChangeImpersonated")) {
				macroCallbacks.put(mtae.getName(), mtae.getContent());
			} else if (mtae.getName().equalsIgnoreCase("width")) {
				if (canResize) {
					setSize(new Dimension(Integer.parseInt(mtae.getContent()), getHeight()));
					validate();
				}
			} else if (mtae.getName().equalsIgnoreCase("height")) {
				if (canResize) {
					setSize(new Dimension(getWidth(), Integer.parseInt(mtae.getContent())));
					SwingUtil.centerOver(this, parent);
					this.validate();
				}
			} else if (mtae.getName().equalsIgnoreCase("temporary")) {
				Boolean val = Boolean.valueOf(mtae.getContent());
				SwingUtil.centerOver(this, parent);
				temporary = val;
			}
		}
		
		
		if (e.getActionCommand().equals("Close")) {
			closeRequest();
		}
	}

	public void closeRequest() {
		setVisible(false);
		panel.flush();
		if (temporary) {
			dialogs.remove(this.name);
			dispose();
		}
	}

}