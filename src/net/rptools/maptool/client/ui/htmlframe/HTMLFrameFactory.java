package net.rptools.maptool.client.ui.htmlframe;

import java.awt.EventQueue;

import net.rptools.lib.AppEvent;
import net.rptools.lib.AppEventListener;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.ModelChangeEvent;
import net.rptools.maptool.model.ModelChangeListener;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.Zone.Event;

public class HTMLFrameFactory {
	
	private HTMLFrameFactory() {
	}

	private static HTMLFrameFactory.Listener listener;
	
	/**
	 * Shows a dialog or frame based on the options.
	 * @param name The name of the dialog or frame.
	 * @param isFrame Is it a frame. 
	 * @param properties The properties that determine the attributes of the frame or dialog.
	 * @param html The html contents of frame or dialog.
	 */
	public static void show(String name, boolean isFrame, String properties, String html) {
		
		if (listener == null) {
			listener = new HTMLFrameFactory.Listener();
			/*EventQueue.invokeLater(
					new Runnable() {
						public void run() {
							// TODO Auto-generated method stub
							MapTool.getFrame().getCurrentZoneRenderer().getZone().addModelChangeListener(listener);
						}
				}
			);*/
		}
		
		boolean input = false;
		boolean temporary = false;
		int width = -1;
		int height = -1;
		String title = name;
		boolean undecorated = false;

		if (properties != null && properties.length() > 0) {
			String[] opts = properties.split(";");
			
			for (String opt : opts) {
				String[] vals = opt.split("=");
				String key = vals[0].trim();
				String value = vals.length > 1 ? vals[1].trim() : "";
				if (key.equalsIgnoreCase("input")) {
					try {
						int v = Integer.parseInt(value);
						if (v != 0) {
							input = true;
						}
					} catch (NumberFormatException e) {
						// Do nothing
					}
				} else if (key.equals("temporary")) {
					try {
						int v = Integer.parseInt(value);
						if (v != 0) {
							temporary = true;
						}
					} catch (NumberFormatException e) {
						// Do nothing
					}					
				} else if (key.equalsIgnoreCase("width")) {
					try {
						width = Integer.parseInt(value);
					} catch (NumberFormatException e) {
						// Do nothing
					}					
				} else if (key.equalsIgnoreCase("height")) {
					try {
						height = Integer.parseInt(value);
					} catch (NumberFormatException e) {
						// Do nothing
					}					
				} else if (key.equalsIgnoreCase("title")) {
					title = value;
				} else if (key.equalsIgnoreCase("undecorated")) {
					try {
						int v = Integer.parseInt(value);
						if (v != 0) {
							undecorated = true;
						}
					} catch (NumberFormatException e) {
						// Do nothing
					}										
				} 
			}
		}
		
		if (isFrame) {
			HTMLFrame.showFrame(name, title, width, height, html);
		} else {
			HTMLDialog.showDialog(name, title, width, height, input, undecorated, temporary, html);
		}
	}
	
	/**
	 * The list of selected tokens changed.
	 */
	public static void selectedListChanged() {
		HTMLFrame.doSelectedChanged();
		HTMLDialog.doSelectedChanged();
	}
	
	/**
	 * A new token has been impersonated or cleared.
	 */
	public static void impersonateToken() {
		HTMLFrame.doImpersonatedChanged();
		HTMLDialog.doImpersonatedChanged();
	}
	
	
	/**
	 * One of the tokens has changed.
	 */
	public static void tokenChanged(Token token) {
		HTMLFrame.doTokenChanged(token);
		HTMLDialog.doTokenChanged(token);
	}
	
	
	public static class Listener implements ModelChangeListener, AppEventListener {

		public Listener() {
			MapTool.getEventDispatcher().addListener(this, MapTool.ZoneEvent.Activated);
			MapTool.getFrame().getCurrentZoneRenderer().getZone().addModelChangeListener(this);
		}

		public void modelChanged(ModelChangeEvent event) {
			if (event.eventType == Event.TOKEN_CHANGED) {
				Token token = (Token) event.getArg();
				if (MapTool.getFrame().getCurrentZoneRenderer().getSelectedTokenSet().contains(token)) {
					selectedListChanged();
				}
				
				if (MapTool.getFrame().getCommandPanel().getIdentity().equals(token.getName())) {
					impersonateToken();
				}
			
				tokenChanged(token);
			}
		}

		public void handleAppEvent(AppEvent event) {
			Zone oldZone = (Zone)event.getOldValue();
			Zone newZone = (Zone)event.getNewValue();
			
			if (oldZone != null) {
				oldZone.removeModelChangeListener(this);
			}

			newZone.addModelChangeListener(this);
		}		
	}

}
