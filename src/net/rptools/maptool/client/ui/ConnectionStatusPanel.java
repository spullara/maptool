/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.rptools.maptool.client.ui;

import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.rptools.lib.image.ImageUtil;
import net.rptools.maptool.language.I18N;

public class ConnectionStatusPanel extends JPanel {
	public enum Status {
		connected, disconnected, server
	}

	public static Icon disconnectedIcon;
	public static Icon connectedIcon;
	public static Icon serverIcon;

	private final JLabel iconLabel = new JLabel();

	static {
		try {
			disconnectedIcon = new ImageIcon(ImageUtil.getImage("net/rptools/maptool/client/image/computer_off.png")); //$NON-NLS-1$
			connectedIcon = new ImageIcon(ImageUtil.getImage("net/rptools/maptool/client/image/computer_on.png")); //$NON-NLS-1$
			serverIcon = new ImageIcon(ImageUtil.getImage("net/rptools/maptool/client/image/computer_server.png")); //$NON-NLS-1$
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public ConnectionStatusPanel() {
		setLayout(new GridLayout(1, 1));
		setStatus(Status.disconnected);
		add(iconLabel);
	}

	public void setStatus(Status status) {
		Icon icon = null;
		String tip = null;
		switch (status) {
		case connected:
			icon = connectedIcon;
			tip = "ConnectionStatusPanel.serverConnected"; //$NON-NLS-1$
			break;
		case server:
			icon = serverIcon;
			tip = "ConnectionStatusPanel.runningServer"; //$NON-NLS-1$
			break;
		default:
			icon = disconnectedIcon;
			tip = "ConnectionStatusPanel.notConnected"; //$NON-NLS-1$
		}
		iconLabel.setIcon(icon);
		setToolTipText(I18N.getString(tip));
	}
}
