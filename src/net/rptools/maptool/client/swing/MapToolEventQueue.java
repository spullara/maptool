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
package net.rptools.maptool.client.swing;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.language.I18N;

import org.apache.log4j.Logger;

import com.jidesoft.dialog.JideOptionPane;

public class MapToolEventQueue extends EventQueue {
	private static final Logger log = Logger.getLogger(MapToolEventQueue.class);
	private static final JideOptionPane optionPane = new JideOptionPane(I18N.getString("MapToolEventQueue.details"), JOptionPane.ERROR_MESSAGE, JideOptionPane.CLOSE_OPTION); //$NON-NLS-1$

	@Override
	protected void dispatchEvent(AWTEvent event) {
		try {
			super.dispatchEvent(event);
		} catch (StackOverflowError soe) {
			log.error(soe, soe);
			optionPane.setTitle(I18N.getString("MapToolEventQueue.stackOverflow.title")); //$NON-NLS-1$
			optionPane.setDetails(I18N.getString("MapToolEventQueue.stackOverflow"));
			displayPopup();
		} catch (Throwable t) {
			log.error(t, t);
			optionPane.setTitle(I18N.getString("MapToolEventQueue.unexpectedError")); //$NON-NLS-1$
			optionPane.setDetails(toString(t));
			displayPopup();
		}
	}

	private static void displayPopup() {
		optionPane.setDetailsVisible(true);
		JDialog dialog = optionPane.createDialog(MapTool.getFrame(), I18N.getString("MapToolEventQueue.warning.title")); //$NON-NLS-1$
		dialog.setResizable(true);
		dialog.pack();
		dialog.setVisible(true);
	}

	private static String toString(Throwable t) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(out);
		t.printStackTrace(ps);
		ps.close();
		return out.toString();
	}
}
