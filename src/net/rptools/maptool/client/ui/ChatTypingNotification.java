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

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Set;

import javax.swing.JPanel;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.client.AppStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.language.I18N;

import org.apache.commons.collections.map.LinkedMap;

@SuppressWarnings("serial")
public class ChatTypingNotification extends JPanel {
	/**
	 * This component is only made visible when there are notifications to be displayed. That means the first couple of
	 * IF statements in this method are redundant since paintComponent() will not be called unless the component is
	 * visible, and it will only be visible when there are notifications...
	 */
	@Override
	protected void paintComponent(Graphics g) {
//		System.out.println("Chat panel is painting itself...");
		if (AppPreferences.getTypingNotificationDuration() == 0) {
			return;
		}
		LinkedMap chatTypers = MapTool.getFrame().getChatNotificationTimers().getChatTypers();
		if (chatTypers == null || chatTypers.isEmpty()) {
			return;
		}
		Boolean showBackground = AppPreferences.getChatNotificationShowBackground();

		Graphics2D statsG = (Graphics2D) g.create();

		Font boldFont = AppStyle.labelFont.deriveFont(Font.BOLD);
		Font font = AppStyle.labelFont;
		FontMetrics valueFM = g.getFontMetrics(font);
		FontMetrics keyFM = g.getFontMetrics(boldFont);

		int PADDING7 = 7;
		int PADDING3 = 3;
		int PADDING2 = 2;

		BufferedImage img = AppStyle.panelTexture;
		int rowHeight = Math.max(valueFM.getHeight(), keyFM.getHeight());

		setBorder(null);
		int width = AppStyle.miniMapBorder.getRightMargin() + AppStyle.miniMapBorder.getLeftMargin();
		int height = getHeight() - PADDING2 + AppStyle.miniMapBorder.getTopMargin() + AppStyle.miniMapBorder.getBottomMargin();

		statsG.setFont(font);
		SwingUtil.useAntiAliasing(statsG);
		Rectangle bounds = new Rectangle(
				AppStyle.miniMapBorder.getLeftMargin(),
				height - getHeight() - AppStyle.miniMapBorder.getTopMargin(),
				getWidth() - width,
				getHeight() - AppStyle.miniMapBorder.getBottomMargin() - AppStyle.miniMapBorder.getTopMargin() + PADDING2);

		int y = bounds.y + rowHeight;
		rowHeight = Math.max(rowHeight, AppStyle.chatImage.getHeight());

		setSize(getWidth(), ((chatTypers.size() * (PADDING3 + rowHeight)) + AppStyle.miniMapBorder.getTopMargin() + AppStyle.miniMapBorder.getBottomMargin()));

		if (showBackground) {
			g.drawImage(img, 0, 0, getWidth(), getHeight() + PADDING7, this);
			AppStyle.miniMapBorder.paintAround(statsG, bounds);
		}
		Rectangle rightRow = new Rectangle(
				AppStyle.miniMapBorder.getLeftMargin() + PADDING7,
				AppStyle.miniMapBorder.getTopMargin() + PADDING7,
				AppStyle.chatImage.getWidth(),
				AppStyle.chatImage.getHeight());

		Set<?> keySet = chatTypers.keySet();
		@SuppressWarnings("unchecked")
		Set<String> playerTimers = (Set<String>) keySet;
		for (String playerNamer : playerTimers) {
			if (showBackground) {
				statsG.setColor(new Color(249, 241, 230, 140));
				statsG.fillRect(bounds.x + PADDING3, y - keyFM.getAscent(), (bounds.width - PADDING7 / 2) - PADDING3, rowHeight);
				statsG.setColor(new Color(175, 163, 149));
				statsG.drawRect(bounds.x + PADDING3, y - keyFM.getAscent(), (bounds.width - PADDING7 / 2) - PADDING3, rowHeight);
			}
			g.drawImage(AppStyle.chatImage, bounds.x + 5, y - keyFM.getAscent(), (int) rightRow.getWidth(), (int) rightRow.getHeight(), this);

			// Values
			statsG.setColor(MapTool.getFrame().getChatTypingLabelColor());
			statsG.setFont(boldFont);
			statsG.drawString(I18N.getText("msg.commandPanel.liveTyping", playerNamer), bounds.x + AppStyle.chatImage.getWidth() + PADDING7 * 2, y + 5);

			y += PADDING2 + rowHeight;
		}
		if (showBackground) {
			AppStyle.shadowBorder.paintWithin(statsG, bounds);
		} else {
			setOpaque(false);
		}
	}
}
