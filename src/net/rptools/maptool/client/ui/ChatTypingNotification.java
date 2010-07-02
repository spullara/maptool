package net.rptools.maptool.client.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
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

@SuppressWarnings("serial")
public class ChatTypingNotification extends JPanel{


	protected void paintComponent(Graphics g) {
		
		if (MapTool.getFrame().getChatTypers() == null || MapTool.getFrame().getChatTypers().isEmpty() ){
			return;
		}
		
		Set<String> chatTypers = (MapTool.getFrame().getChatTypers());

		Boolean showBackground = AppPreferences.getChatNotificationShowBackground();//true;
		
		Graphics2D statsG = (Graphics2D) g.create();
		
		super.paintComponent(g);
		
		Composite oldComp = statsG.getComposite();
		this.setVisible(false);
		
		Font boldFont = AppStyle.labelFont.deriveFont(Font.BOLD);
		Font font = AppStyle.labelFont;
		FontMetrics valueFM = g.getFontMetrics(font);
		FontMetrics keyFM = g.getFontMetrics(boldFont);

		int PADDING7 = 7;
		int PADDING3 = 3;
		int PADDING2 = 2;

		BufferedImage img = AppStyle.panelTexture;
		int rowHeight = Math.max(valueFM.getHeight(), keyFM.getHeight());

		this.setBorder(null);
		int width = AppStyle.miniMapBorder.getRightMargin()
				+ AppStyle.miniMapBorder.getLeftMargin();
		int height = this.getHeight() - PADDING2
				+ AppStyle.miniMapBorder.getTopMargin()
				+ AppStyle.miniMapBorder.getBottomMargin();

		statsG.setFont(font);
		SwingUtil.useAntiAliasing(statsG);
		Rectangle bounds = new Rectangle(
				AppStyle.miniMapBorder.getLeftMargin(), height
						- this.getHeight()
						- AppStyle.miniMapBorder.getTopMargin(), this
						.getWidth()
						- width, this.getHeight()
						- AppStyle.miniMapBorder.getBottomMargin()
						- AppStyle.miniMapBorder.getTopMargin() + PADDING2);

		int y = bounds.y + rowHeight;
		rowHeight = Math.max(rowHeight, AppStyle.chatImage.getHeight());

		this.setSize(
						this.getWidth(),
						((chatTypers.size() * (PADDING3 + rowHeight))
								+ AppStyle.miniMapBorder.getTopMargin() + AppStyle.miniMapBorder
								.getBottomMargin()));

		if (showBackground) {
			g.drawImage(img, 0, 0, this.getWidth(),
					this.getHeight() + PADDING7, this);
			AppStyle.miniMapBorder.paintAround(statsG, bounds);
		}

		Rectangle rightRow = new Rectangle(AppStyle.miniMapBorder
				.getLeftMargin()
				+ PADDING7, AppStyle.miniMapBorder.getTopMargin() + PADDING7,
				AppStyle.chatImage.getWidth(), AppStyle.chatImage.getHeight());

		for(String playerNamer: chatTypers)
		{
			if (showBackground) {
				statsG.setColor(new Color(249, 241, 230, 140));
				statsG.fillRect(bounds.x + PADDING3, y - keyFM.getAscent(),
						(bounds.width - PADDING7 / 2) - PADDING3, rowHeight);
				statsG.setColor(new Color(175, 163, 149));
				statsG.drawRect(bounds.x + PADDING3, y - keyFM.getAscent(),
						(bounds.width - PADDING7 / 2) - PADDING3, rowHeight);
			}
			g.drawImage(AppStyle.chatImage, bounds.x + 5,
					y - keyFM.getAscent(), (int) rightRow.getWidth(),
					(int) rightRow.getHeight(), this);

			// Values

			
			statsG.setColor(MapTool.getFrame().getChatTypingLabelColor());
			statsG.setFont(boldFont);
			statsG.drawString((String) I18N.getText("msg.commandPanel.liveTyping", playerNamer), bounds.x
					+ AppStyle.chatImage.getWidth() + PADDING7 * 2, y + 5);

			y += PADDING2 + rowHeight;
		}
		
		chatTypers = null;
		if (showBackground) {
			AppStyle.shadowBorder.paintWithin(statsG, bounds);
		} else {
			this.setOpaque(false);
		}
		this.setVisible(true);

	}
}
