/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package net.rptools.maptool.client.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JComponent;

import net.rptools.clientserver.ActivityListener;
import net.rptools.lib.image.ImageUtil;
import net.rptools.maptool.client.swing.Animatable;
import net.rptools.maptool.client.swing.AnimationManager;
import net.rptools.maptool.language.I18N;

/**
 * @author trevor
 */
public class ActivityMonitorPanel extends JComponent implements ActivityListener, Animatable {

	private static final int PADDING = 5;
	private static final int ON_DELAY = 100;
	
	private boolean receiving;
	private boolean transmitting;

	private boolean receiveComplete;
	private boolean transmitComplete;
	
	private static Image transmitOn;
	private static Image transmitOff;
	
	private static Image receiveOn;
	private static Image receiveOff;

	private static long receiveStart;
	private static long transmitStart;
	
	private static Dimension prefSize;

	static {
		
		try {
			transmitOn  = ImageUtil.getImage("net/rptools/maptool/client/image/transmitOn.png"); //$NON-NLS-1$
			transmitOff = ImageUtil.getImage("net/rptools/maptool/client/image/activityOff.png"); //$NON-NLS-1$
	
			receiveOn  = ImageUtil.getImage("net/rptools/maptool/client/image/receiveOn.png"); //$NON-NLS-1$
			receiveOff = ImageUtil.getImage("net/rptools/maptool/client/image/activityOff.png"); //$NON-NLS-1$
			
			int width = Math.max(transmitOn.getWidth(null), transmitOff.getWidth(null)) + Math.max(receiveOn.getWidth(null), receiveOff.getWidth(null));
			int height = Math.max(transmitOn.getHeight(null), transmitOff.getHeight(null)) + Math.max(receiveOn.getHeight(null), receiveOff.getHeight(null));
			
			prefSize = new Dimension(width + (PADDING * 2) + 2, height);
		} catch (IOException ioe) {
			// TODO: handle this better
			ioe.printStackTrace();
		}
		
	}
	
	public ActivityMonitorPanel() {
		setToolTipText(I18N.getString("ActivityMonitorPanel.colorDefinition")); //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g) {
		
		Image receiveImg = receiving ? receiveOn : receiveOff;
		Image transmitImg = transmitting ? transmitOn : transmitOff;
		
		g.drawImage(receiveImg, PADDING, (getSize().height - receiveImg.getHeight(null))/2, this);
		g.drawImage(transmitImg, getSize().width - transmitImg.getWidth(null) - PADDING, (getSize().height - transmitImg.getHeight(null))/2, this);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		return getMinimumSize();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#getMinimumSize()
	 */
	public Dimension getMinimumSize() {
		return prefSize;
	}
	
	////
	// ANIMATABLE
	
	public void animate() {
		
		long now = System.currentTimeMillis();
		boolean turnOff = false;
		
		if (transmitting && transmitComplete && now > transmitStart + ON_DELAY) {
			transmitting = false;
			turnOff = true;
		}

		if (receiving && receiveComplete && now > receiveStart + ON_DELAY) {
			turnOff = true;
			receiving = false;
		}
		
		if (!transmitting && !receiving && turnOff) {
			AnimationManager.removeAnimatable(this);
			repaint();
		}
	}
	
	//// 
	// ACTIVITY LISTENER
	
	/* (non-Javadoc)
	 * @see net.rptools.clientserver.ActivityListener#notify(net.rptools.clientserver.ActivityListener.Direction, net.rptools.clientserver.ActivityListener.State, int, int)
	 */
	public void notify(Direction direction, State state, int total, int current) {

		switch (direction) {
			case Inbound: {
				
				switch(state) {
				case Start: {
					receiving = true; 
					receiveComplete = false;
					receiveStart = System.currentTimeMillis();

					AnimationManager.addAnimatable(this);
					
					repaint();
					break;
				}
				case Complete: {
					receiveComplete = true;
					
					if (System.currentTimeMillis() > receiveStart + ON_DELAY) {
						receiving = false;
					}
					repaint();
					break;
				}
				default: return;
				}
				
				break;
			}
			
			case Outbound: {
				
				switch(state) {
				case Start: {
					transmitting = true;
					transmitComplete = false;
					transmitStart = System.currentTimeMillis();

					AnimationManager.addAnimatable(this);

					repaint();
					break;
				}
				case Complete: {
					transmitComplete = true;
					
					if (System.currentTimeMillis() > transmitStart + ON_DELAY) {
						transmitting = false;
					}
					repaint();
					break;
				}
				default: return;
				}
				
				break;
			}
		}
		
	}
}
