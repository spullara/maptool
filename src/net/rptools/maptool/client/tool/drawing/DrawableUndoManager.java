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

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.Pen;

/**
 * This class controls the undo/redo behavior for drawables.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public class DrawableUndoManager /* implements ModelChangeListener */{

	/**
	 * Swing's undo/redo support
	 */
	private final UndoManager manager = new UndoManager();

	/**
	 * The one and only undo manager
	 */
	private static DrawableUndoManager singletonInstance = new DrawableUndoManager();

	public DrawableUndoManager() {
		// All we need to do is register for the Zone.Event.DRAWABLE_REMOVED event so that we can
		// eliminate that drawable out of the undo list.  As of SVN 5771, this is only used by the
		// CLEAR ALL DRAWINGS function.  See ServerMethodHandler.clearAllDrawings()

		// Using an event handler is the right way to do this, but the DrawableUndoManager would
		// need to register with every zone as they are created.  Tying into that process is too much
		// intrusion this late in the game.  Instead, I'll modify the Zone class to invoke this class
		// whenever a drawable is removed.  The coupling is too tight, but it'll work for 1.3.
		//MapTool.getFrame().getCurrentZoneRenderer().getZone().addModelChangeListener(this);
	}

	/**
	 * Add a drawable to the undo set.
	 * 
	 * @param zoneId
	 *            The zone to render the drwable on.
	 * @param pen
	 *            The pen used to draw.
	 * @param drawable
	 *            The drawable just drawn.
	 */
	public void addDrawable(GUID zoneId, Pen pen, Drawable drawable) {
		manager.addEdit(new DrawableUndoableEdit(zoneId, pen, drawable));
		System.out.println("Adding drawable '" + drawable + "' to zone " + zoneId);
		net.rptools.maptool.client.AppActions.UNDO_DRAWING.isAvailable();
		net.rptools.maptool.client.AppActions.REDO_DRAWING.isAvailable();
	}

	/**
	 * Undo the last edit if one exists.
	 */
	public void undo() {
		if (!manager.canUndo())
			return;
		manager.undo();
	}

	/**
	 * Redo the last undo if one exists.
	 */
	public void redo() {
		if (!manager.canRedo())
			return;
		manager.redo();
	}

	/**
	 * Invoked when the user activates the "Clear All Drawings" menu option.
	 */
	public void clear() {
		manager.discardAllEdits();
	}

	public UndoManager getUndoManager() {
		return manager;
	}

	/**
	 * Class used to undo/redo drawables. Only the drawing client can undo/redo their stuff.
	 * 
	 * @author jgorrell
	 * @version $Revision$ $Date$ $Author$
	 */
	private static class DrawableUndoableEdit extends AbstractUndoableEdit {

		/**
		 * Id of the zone modified.
		 */
		private final GUID zoneId;

		/**
		 * The pen used to modify it.
		 */
		private final Pen pen;

		/**
		 * What had been drawn.
		 */
		private final Drawable drawable;

		/**
		 * Create the undoable edit.
		 * 
		 * @param aZoneId
		 *            Id of zone that renders the drawable.
		 * @param aPen
		 *            The pen for drawing.
		 * @param aDrawable
		 *            The drawable rendered.
		 */
		public DrawableUndoableEdit(GUID aZoneId, Pen aPen, Drawable aDrawable) {
			zoneId = aZoneId;
			pen = aPen;
			drawable = aDrawable;
		}

		/**
		 * To undo, send the drawable id to the server <code>drawUndo</code> command.
		 * 
		 * @see javax.swing.undo.UndoableEdit#undo()
		 */
		@Override
		public void undo() throws CannotUndoException {
			super.undo();
			System.out.println("Calling undo...");

			// Tell the server to undo the drawable.
			MapTool.serverCommand().undoDraw(zoneId, drawable.getId());
		}

		/**
		 * @see javax.swing.undo.UndoableEdit#redo()
		 */
		@Override
		public void redo() throws CannotRedoException {
			super.redo();
			System.out.println("Calling redo...");

			// Render the drawable again, but don't add it to the undo manager.
			MapTool.serverCommand().draw(zoneId, pen, drawable);
		}
	}

	/**
	 * Get the one and only instance of the drawable undo manager.
	 * 
	 * @return The undo manager.
	 */
	public static DrawableUndoManager getInstance() {
		return singletonInstance;
	}
}