/**
 * This class represents a single NumPad key used to perform movement operations on selected objects. For example, one
 * of these can be created to represent NumPad4. In this case, the (x,y) values provided would be (-1,0) on a square
 * grid to indicate movement of -1 along the X axis and 0 along the Y axis.
 */
package net.rptools.maptool.model;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.rptools.maptool.client.tool.PointerTool;

public class MovementKey extends AbstractAction {
	private static final long serialVersionUID = -4103031698708914986L;
	private final double dx, dy;
	private final PointerTool tool; // I'd like to store this in the Grid, but then it has to be final :(

	public MovementKey(PointerTool callback, double x, double y) {
		tool = callback;
		dx = x;
		dy = y;
	}

	@Override
	public String toString() {
		return "[" + dx + "," + dy + "]";
	}

	public void actionPerformed(ActionEvent e) {
		tool.handleKeyMove(dx, dy);
	}
}