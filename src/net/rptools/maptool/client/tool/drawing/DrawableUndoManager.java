/* --- Copyright 2005 The Cobalt Group, Inc. All rights reserved --- */

package net.rptools.maptool.client.tool.drawing;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import net.rptools.maptool.client.MapToolClient;
import net.rptools.maptool.client.ZoneRenderer;
import net.rptools.maptool.client.ZoneRendererFactory;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.drawing.Drawable;
import net.rptools.maptool.model.drawing.DrawnElement;
import net.rptools.maptool.model.drawing.Pen;
import net.rptools.maptool.server.MapToolServer;

/**
 * This class controls the undo/redo behavior for drawables.
 * 
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public class DrawableUndoManager {

  /**
   * Swing's undo/redo support
   */
  private UndoManager manager = new UndoManager();
  
  /**
   * The command used to undo on menus, etc.
   */
  private UndoCommand undoCommand;

  /**
   * The command used to redo on menus, etc.
   */
  private RedoCommand redoCommand;
  
  /**
   * The one and only undo manager
   */
  private static DrawableUndoManager singletonInstance = new DrawableUndoManager();
  
  /**
   * Add a drawable to the undo set.
   * 
   * @param zoneId The zone to render the drwable on.
   * @param pen The pen used to draw.
   * @param drawable The drawable just drawn.
   */
  public void addDrawable(GUID zoneId, Pen pen, Drawable drawable) {
    manager.addEdit(new DrawableUndoableEdit(zoneId, pen, drawable));
    undoCommand.updateState();
    redoCommand.updateState();
  }
  
  /**
   * Undo the last edit if one exists.   
   */
  public void undo() {
    if (!manager.canUndo()) return;
    manager.undo();
  }
  
  /**
   * Redo the last undo if one exists.
   */
  public void redo() {
    if (!manager.canRedo()) return;
    manager.redo();
  }
  
  /**
   * Lazy creation of undo command.
   * 
   * @return The one and only undo command
   */
  public UndoCommand getUndoCommand() {
    if (undoCommand == null) {
      undoCommand = new UndoCommand();
    }
    return undoCommand;
  }

  /**
   * Lazy creation of redo command.
   * 
   * @return The one and only redo command
   */
  public RedoCommand getRedoCommand() {
    if (redoCommand == null) {
      redoCommand = new RedoCommand();
    }
    return redoCommand;
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
    private GUID zoneId;
    
    /**
     * The pen used to modify it.
     */
    private Pen pen;

    /**
     * What had been drawn.
     */
    private Drawable drawable;
    
    /**
     * Create the undoable edit.
     * 
     * @param aZoneId Id of zone that renders the drawable.
     * @param aPen The pen for drawing.
     * @param aDrawable The drawable rendered. 
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

      // Tell the server to undo the drawable.
      if (MapToolClient.isConnected()) {
        MapToolClient.getInstance().getConnection().callMethod(MapToolServer.COMMANDS.undoDraw.name(), zoneId, drawable.getId());
      } else {
        Zone zone = MapToolClient.getCampaign().getZone(zoneId);
        zone.removeDrawable(drawable.getId());		
		MapToolClient.getInstance().repaint();
      }
    }
    
    /**
     * @see javax.swing.undo.UndoableEdit#redo()
     */
    @Override
    public void redo() throws CannotRedoException {
      super.redo();
      
      // Render the drawable again, but don't add it to the undo manager.
      if (MapToolClient.isConnected()) {
        MapToolClient.getInstance().getConnection().callMethod(MapToolServer.COMMANDS.draw.name(), zoneId, pen, drawable);
      } else {
        Zone zone = MapToolClient.getCampaign().getZone(zoneId);
        zone.addDrawable(new DrawnElement(drawable, pen));
		MapToolClient.getInstance().repaint();
      } 
    }
  }

  /**
   * Command to use when creating an undo draw menu item or button.
   */
  private class UndoCommand extends AbstractAction {
    
    /**
     * Set the common properties
     */
    public UndoCommand() {
      putValue(NAME, "Undo Draw");
      putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_U));
      putValue(ACCELERATOR_KEY, KeyStroke.getAWTKeyStroke("ctrl Z"));
      updateState();
    }
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      singletonInstance.undo();
      updateState();
	  redoCommand.updateState();
    }
    
    /**
     * Change the enabled state to match the undo manager.
     */
    public void updateState() {
      setEnabled(singletonInstance.manager.canUndo());
    }
  }

  
  /**
   * Command to use when creating an redo draw menu item or button.
   * 
   * @author jgorrell
   * @version $Revision$ $Date$ $Author$
   */
  private class RedoCommand extends AbstractAction {
    
    /**
     * Set the common properties
     */
    public RedoCommand() {
      putValue(NAME, "Redo Draw");
      putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
      putValue(ACCELERATOR_KEY, KeyStroke.getAWTKeyStroke("ctrl Y"));
      updateState();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      singletonInstance.redo();
      updateState();
	  undoCommand.updateState();
    }

    /**
     * Change the enabled state to match the undo manager.
     */
    public void updateState() {
      setEnabled(singletonInstance.manager.canRedo());
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