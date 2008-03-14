/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation files 
 * (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN 
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */
package net.rptools.maptool.client.tool;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.jeta.forms.components.colors.JETAColorWell;

import net.rptools.lib.image.ImageUtil;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.AppStyle;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ScreenPoint;
import net.rptools.maptool.client.swing.AbeillePanel;
import net.rptools.maptool.client.ui.zone.ZoneOverlay;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.Label;
import net.rptools.maptool.model.ZonePoint;

/**
 */
public class TextTool extends DefaultTool implements ZoneOverlay {

	private Label selectedLabel;
	
	private int dragOffsetX;
	private int dragOffsetY;
	private boolean isDragging;
	private boolean selectedNewLabel;
	
	public TextTool () {
        try {
            setIcon(new ImageIcon(ImageUtil.getImage("net/rptools/maptool/client/image/tool/text-blue.png")));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
	@Override
	protected void attachTo(ZoneRenderer renderer) {
		renderer.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		super.attachTo(renderer);
		
		selectedLabel = null;
	}

	@Override
	protected void detachFrom(ZoneRenderer renderer) {
		renderer.setCursor(Cursor.getDefaultCursor());
		super.detachFrom(renderer);
	}
	
    @Override
    public String getTooltip() {
        return "Put text onto the zone";
    }
    
    @Override
    public String getInstructions() {
    	return "tool.label.instructions";
    }
    
    public void paintOverlay(ZoneRenderer renderer, Graphics2D g) {
    	
    	if (selectedLabel != null && renderer.getLabelBounds(selectedLabel) != null) {
    		AppStyle.selectedBorder.paintWithin(g, renderer.getLabelBounds(selectedLabel));
    	}
    }

    @Override
    protected void installKeystrokes(Map<KeyStroke, Action> actionMap) {
    	super.installKeystrokes(actionMap);
    	
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), new AbstractAction() {
			public void actionPerformed(ActionEvent e) {

				if (selectedLabel != null) {
					renderer.getZone().removeLabel(selectedLabel.getId());
		    		MapTool.serverCommand().removeLabel(renderer.getZone().getId(), selectedLabel.getId());
					selectedLabel = null;
		    		repaint();
				}
			}
		});
    }
    
    ////
    // MOUSE
    @Override
    public void mousePressed(MouseEvent e) {
    	
		Label label = renderer.getLabelAt(e.getX(), e.getY());
		if (label != selectedLabel) {
			selectedNewLabel = true;
			renderer.repaint();
		} else {
			selectedNewLabel = false;
		}

		if (label != null) {
	    	ScreenPoint sp = ScreenPoint.fromZonePoint(renderer, label.getX(), label.getY());
	    	
			dragOffsetX = (int)(e.getX() - sp.x);
			dragOffsetY = (int)(e.getY() - sp.y);
		}

		super.mousePressed(e);
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {

    	if (SwingUtilities.isLeftMouseButton(e)) {

    		if (!isDragging) {
	    		Label label = renderer.getLabelAt(e.getX(), e.getY());
	    		if (label == null) {
	    			
	    			if (selectedLabel == null) {
		        		ZonePoint zp = new ScreenPoint(e.getX(), e.getY()).convertToZone(renderer);
		        		label = new Label("", zp.x, zp.y);
		        		selectedLabel = label;
	    			} else {
	    				selectedLabel = null;
	    				renderer.repaint();
	    				return;
	    			}
	    		} else {
	    			if (selectedNewLabel) {
	    				selectedLabel = label;
	    				renderer.repaint();
	    				return;
	    			}
	    		}
	
	    		EditLabelDialog dialog = new EditLabelDialog(label);
	    		dialog.setVisible(true);
	    		
	    		if (!dialog.isAccepted()) {
	    			return;
	    		}
	    		
	    		renderer.getZone().putLabel(label);
    		}        	
    		
        	if (selectedLabel != null) {
	    		MapTool.serverCommand().putLabel(renderer.getZone().getId(), selectedLabel);
	    		
	    		renderer.repaint();
        	}
    	}
    	
    	isDragging = false;
    	
    	super.mouseReleased(e);
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
    	
    	super.mouseDragged(e);

    	if (!isDragging) {
    		// Setup
        	Label label = renderer.getLabelAt(e.getX(), e.getY());
        	if (selectedLabel == null || selectedLabel != label) {
        		selectedLabel = label;
        	}

        	if (selectedLabel == null || SwingUtilities.isRightMouseButton(e)) {
        		return;
        	}
    	}

    	isDragging = true;
    	
    	ZonePoint zp = new ScreenPoint(e.getX() - dragOffsetX, e.getY() - dragOffsetY).convertToZone(renderer);
    	
    	selectedLabel.setX(zp.x);
    	selectedLabel.setY(zp.y);
    	
    	renderer.repaint();
    	
    }
    
    public class EditLabelDialog extends JDialog {
    	
    	private boolean accepted;

    	public EditLabelDialog(Label label) {
    		super(MapTool.getFrame(), "Edit Label", true);
    		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    		
    		EditLabelPanel panel = new EditLabelPanel(this); 
    		panel.bind(label);
    		
    		add(panel);
    		
    		getRootPane().setDefaultButton(panel.getOKButton());

    		pack();
    	}
    	
    	public boolean isAccepted() {
    		return accepted;
    	}
    	
    	@Override
    	public void setVisible(boolean b) {
    		if (b) {
    			SwingUtil.centerOver(this, getOwner());
    		}
    		super.setVisible(b);
    	}
    }
    
    public class EditLabelPanel extends AbeillePanel<Label> {

    	private EditLabelDialog dialog;
    	
    	public EditLabelPanel(EditLabelDialog dialog) {
    		super("net/rptools/maptool/client/ui/forms/editLabelDialog.jfrm");

    		this.dialog = dialog;
    		
    		panelInit();
    		
    		getLabelTextField().setSelectionStart(0);
    		getLabelTextField().setSelectionEnd(getLabelTextField().getText().length());
    		getLabelTextField().setCaretPosition(getLabelTextField().getText().length());
    	}
    	
    	@Override
    	public void bind(Label model) {
    		getColorWell().setColor(model.getForegroundColor());
    		super.bind(model);
    	}

    	@Override
    	public boolean commit() {
    		getModel().setForegroundColor(getColorWell().getColor());
    		return super.commit();
    	}
    	
    	public JETAColorWell getColorWell() {
    		return (JETAColorWell) getComponent("foregroundColor");
    	}
    	
    	public JTextField getLabelTextField() {
    		return (JTextField)getComponent("@label");
    	}
    	
    	public JButton getOKButton() {
    		return (JButton) getComponent("okButton");
    	}
    	
    	public void initOKButton() {
    		getOKButton().addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    				dialog.accepted = true;
    				commit();
    				close();
    			}
    		});
    	}

    	public void initCancelButton() {
    		((JButton)getComponent("cancelButton")).addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    				close();
    			}
    		});
    	}

    	private void close() {
    		dialog.setVisible(false);
    	}
    }
}
