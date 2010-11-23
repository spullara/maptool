package net.rptools.maptool.client.swing;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.language.I18N;

import org.apache.log4j.Logger;

import com.jidesoft.dialog.JideOptionPane;

public class MapToolEventQueue extends EventQueue {

	private static final Logger log = Logger.getLogger(MapToolEventQueue.class);
	
    @Override
    protected void dispatchEvent(AWTEvent event) {
        try {
            super.dispatchEvent(event);
        } catch (Throwable e) {
        	log.error(e, e);
        	JideOptionPane optionPane = new JideOptionPane(I18N.getString("MapToolEventQueue.details"), JOptionPane.ERROR_MESSAGE, JideOptionPane.CLOSE_OPTION); //$NON-NLS-1$
            optionPane.setTitle(I18N.getString("MapToolEventQueue.unexpectedError")); //$NON-NLS-1$
            optionPane.setDetails(toString(e));
            JDialog dialog = optionPane.createDialog(MapTool.getFrame(), I18N.getString("MapToolEventQueue.warning")); //$NON-NLS-1$
            dialog.setResizable(true);
            dialog.pack();
            dialog.setVisible(true);
        }
    }

    private static String toString(Throwable t) {
    	ByteArrayOutputStream out = new ByteArrayOutputStream(); 
    	t.printStackTrace(new PrintStream(out));
    	return out.toString();
    }
}
