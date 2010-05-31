/**
 * 
 */
package net.rptools.maptool.client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import net.rptools.maptool.client.ui.commandpanel.CommandPanel;

import org.apache.log4j.Logger;

/**
 * @author frank
 */
public class ChatAutoSave {
	private static Logger log = Logger.getLogger(ChatAutoSave.class);

	private static final ChatAutoSave self = new ChatAutoSave();
	private final Timer countdown;
	private final TimerTask task;
	private int delay;

	private ChatAutoSave() {
		log.debug("Creating chat log autosave timer");
		delay = AppPreferences.getChatAutosaveTime();
		countdown = new Timer();		// !daemon Timer; runs forever and prevents application shutdown
		task = new TimerTask() {
			@Override
			public void run() {
				log.debug("Chat log autosave countdown complete");
				countdown.schedule(this, delay);
				String filename = AppPreferences.getChatFilenameFormat();
				File chatlog;
				if (filename.indexOf(File.separator) == -1) {
					// If there is no separator, treat it as relative to the "autosave" directory
					chatlog = new File(AppUtil.getAppHome("autosave").toString() + "/" + filename);
				} else {
					// It's an absolute pathname due to the existence of the separator.
					chatlog = new File(filename);
				}
				log.info("Saving log to '" + chatlog.toString() + "'...");
				CommandPanel chat = MapTool.getFrame().getCommandPanel();
				FileWriter writer = null;
				try {
					writer = new FileWriter(chatlog);		// FIXME Literal filename right now
					writer.write(chat.getMessageHistory());
					log.info("Log saved");
				} catch (IOException e) {
					// If this happens should we track it and turn off the autosave?  Perhaps
					// after a certain number of consecutive failures?  Or maybe just lengthen
					// the amount of time between attempts in that case?  At a minimum we
					// should probably give the user a chance to turn it off as part of this
					// message box that pops up...
					MapTool.showWarning("msg.warn.failedAutoSavingMessageHistory", e);
				} finally {
					if (writer != null)
						try {
							writer.close();
						} catch (IOException e) {
							log.warn("Couldn't close chat log autosave?!");
						}
				}
			}
		};
	}

	private static ChatAutoSave getInstance() {
		return self;
	}

	public static void changeTimeout(int timeout) {
		getInstance().stop();
		getInstance().delay = timeout;
//		getInstance().start();
	}

	private void stop() {
		countdown.cancel();
	}

	private void start() {
		if (delay > 0)
			countdown.schedule(task, delay);
	}
}
