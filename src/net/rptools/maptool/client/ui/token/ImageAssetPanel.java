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
package net.rptools.maptool.client.ui.token;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import net.rptools.lib.MD5Key;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.TransferableHelper;
import net.rptools.maptool.client.swing.ImageChooserDialog;
import net.rptools.maptool.model.Asset;
import net.rptools.maptool.util.ImageManager;

public class ImageAssetPanel extends JPanel implements DropTargetListener {
	private MD5Key imageId;

	private JButton cancelButton;
	private JButton addButton;

	private ImageObserver[] observers;

	private boolean allowEmpty = true;

	public ImageAssetPanel() {
		new DropTarget(this, this);

		init();
	}

	private void init() {
		setLayout(new BorderLayout());

		add(BorderLayout.NORTH, createNorthPanel());

		setImageId(null);
	}

	private JPanel createNorthPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		panel.setOpaque(false);

		panel.add(getAddButton());
		panel.add(getCancelButton());

		return panel;
	}

	public JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton(new ImageIcon(AppStyle.cancelButton));
			cancelButton.setContentAreaFilled(false);
			cancelButton.setBorderPainted(false);
			cancelButton.setFocusable(false);
			cancelButton.setMargin(new Insets(0, 0, 0, 0));

			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setImageId(null);
				}
			});
		}
		return cancelButton;
	}

	public JButton getAddButton() {
		if (addButton == null) {
			addButton = new JButton(new ImageIcon(AppStyle.addButton));
			addButton.setContentAreaFilled(false);
			addButton.setBorderPainted(false);
			addButton.setFocusable(false);
			addButton.setMargin(new Insets(0, 0, 0, 0));

			addButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ImageChooserDialog chooserDialog = MapTool.getFrame().getImageChooserDialog();
					chooserDialog.setVisible(true);

					MD5Key imageId = chooserDialog.getImageId();
					if (imageId == null) {
						return;
					}
					setImageId(imageId);
				}
			});
		}
		return addButton;
	}

	public MD5Key getImageId() {
		return imageId;
	}

	public void setAllowEmptyImage(boolean allow) {
		allowEmpty = allow;
	}

	public void setImageId(MD5Key sheetAssetId, ImageObserver... observers) {
		this.imageId = sheetAssetId;
		this.observers = observers != null && observers.length > 0 ? observers : new ImageObserver[] { this };

		getCancelButton().setVisible(allowEmpty && sheetAssetId != null);

		revalidate();
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		Dimension size = getSize();
		((Graphics2D) g).setPaint(new TexturePaint(AppStyle.panelTexture, new Rectangle(0, 0, AppStyle.panelTexture.getWidth(), AppStyle.panelTexture.getHeight())));
		g.fillRect(0, 0, size.width, size.height);

		if (imageId == null) {
			return;
		}
		BufferedImage image = ImageManager.getImage(imageId, observers);

		Dimension imgSize = new Dimension(image.getWidth(), image.getHeight());
		SwingUtil.constrainTo(imgSize, size.width - 8, size.height - 8);

		g.drawImage(image, (size.width - imgSize.width) / 2, (size.height - imgSize.height) / 2, imgSize.width, imgSize.height, this);
	}

	////
	// DROP TARGET LISTENER
	public void dragEnter(DropTargetDragEvent dtde) {
	}

	public void dragExit(DropTargetEvent dte) {
	}

	public void dragOver(DropTargetDragEvent dtde) {
	}

	public void drop(DropTargetDropEvent dtde) {
		Transferable t = dtde.getTransferable();
		if (!(TransferableHelper.isSupportedAssetFlavor(t)
				|| TransferableHelper.isSupportedTokenFlavor(t))
				|| (dtde.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) == 0) {
			dtde.rejectDrop(); // Not a supported flavor or not a copy/move
			System.out.println(" Couldn't figure out the drop");
			return;
		}
		dtde.acceptDrop(dtde.getDropAction());

		List<Object> assets = TransferableHelper.getAsset(dtde.getTransferable());
		if (assets == null || assets.isEmpty() || !(assets.get(0) instanceof Asset)) {
			return;
		}
		setImageId(((Asset) assets.get(0)).getId());
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
	}
}
