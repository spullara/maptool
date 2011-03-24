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
package net.rptools.maptool.client.tool.drawing;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

/**
 * Tool for drawing freehand lines.
 */
public class FreehandTool extends AbstractLineTool implements MouseMotionListener {
	private static final long serialVersionUID = 3904963036442998837L;

	public FreehandTool() {
		try {
			setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("net/rptools/maptool/client/image/tool/draw-blue-freehndlines.png"))));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		// Don't ever show measurement drawing with freehand tool
		drawMeasurementDisabled = true;
	}

	@Override
	public String getTooltip() {
		return "tool.freehand.tooltip";
	}

	@Override
	public String getInstructions() {
		return "tool.freehand.instructions";
	}

	////
	// MOUSE LISTENER
	@Override
	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			startLine(e);
			setIsEraser(isEraser(e));
		}
		super.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			stopLine(e);
		}
		super.mouseReleased(e);
	}

	////
	// MOUSE MOTION LISTENER
	@Override
	public void mouseDragged(java.awt.event.MouseEvent e) {
		addPoint(e);
		super.mouseDragged(e);
	}
}
