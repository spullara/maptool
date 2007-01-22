package net.rptools.maptool.client.ui.commandpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import net.rptools.maptool.client.AppPreferences;
import ca.odell.renderpack.HTMLTableCellRenderer;

public class MessagePanel extends JPanel {

	private JTable messageTable;
	private JScrollPane scrollPane;
	
	public MessagePanel() {
		setLayout(new BorderLayout());
		
		messageTable = createMessageTable();
		
		scrollPane = new JScrollPane(messageTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(null);
		scrollPane.getViewport().setBackground(Color.white);
		
		add(BorderLayout.CENTER, scrollPane);
	}
	
	public void refreshRenderer() {
		messageTable.getColumnModel().getColumn(0).setCellRenderer(new MessageCellRenderer());
	}

	public String getMessagesText() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("<html><body>");
		for (int i = 0; i < messageTable.getModel().getRowCount(); i++) {
			
			builder.append("<div>\n\t");
			builder.append(messageTable.getModel().getValueAt(i, 0));
			builder.append("</div>\n");
		}
		builder.append("</body></html>");
		
		return builder.toString();
	}
	
	public void clearMessages() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				messageTable.setModel(new DefaultTableModel());
			}
		});
	}
	
	public void addMessage(final String message) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				synchronized(messageTable) {
					DefaultTableModel model = (DefaultTableModel) messageTable.getModel();
					model.addRow(new Object[]{message});
				}
			}
		});
	}

	private JTable createMessageTable() {
		messageTable = new JTable();
		messageTable.setModel(new DefaultTableModel(new Object[][]{}, new Object[]{""}));
		messageTable.setTableHeader(null);
		messageTable.setShowGrid(false);
		messageTable.setBackground(Color.white);
		
		// Always scroll to the bottom of the chat window on new messages
		messageTable.addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent e) {
			}
			public void componentMoved(ComponentEvent e) {
			}
			public void componentResized(ComponentEvent e) {
				messageTable.scrollRectToVisible(new Rectangle(0, messageTable.getSize().height, 1, 1));
			}
			public void componentShown(ComponentEvent e) {
			}
		});
		
		refreshRenderer();
		
		return messageTable;
	}
	
	private static class MessageCellRenderer extends HTMLTableCellRenderer {
		public MessageCellRenderer(){
			super(true);
			styleSheet.addRule("body { font-family: sans-serif; font-size: " + AppPreferences.getFontSize() + "pt}");
	    }
		
		@Override
		public void writeObject(StringBuffer buff, JTable table, Object value, boolean isSelected, boolean isFocused, int row, int col) {

			buff.append("<html><body>");
			buff.append(value);
			buff.append("</body></html>");
		}
	}
}
