package net.rptools.maptool.client.ui.campaignproperties;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import net.rptools.lib.MD5Key;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.MapToolUtil;
import net.rptools.maptool.client.swing.AbeillePanel;
import net.rptools.maptool.client.swing.ImageChooserDialog;
import net.rptools.maptool.client.ui.token.ImageAssetPanel;
import net.rptools.maptool.model.AssetManager;
import net.rptools.maptool.model.Campaign;
import net.rptools.maptool.model.LookupTable;
import net.rptools.maptool.model.LookupTable.LookupEntry;

public class LookupTablePanel extends AbeillePanel {

	private Campaign campaign;
	private ImageAssetPanel tableImageAssetPanel;
	private int defaultRowHeight;
	
	public LookupTablePanel() {
		super("net/rptools/maptool/client/ui/forms/lookuptablePanel.jfrm");
		
		initDeleteTableButton();
		initNewTableButton();
		initUpdateTableButton();
		initTableList();
		initTableImage();
		initTableDefinitionTable();
	}
	
	private void initTableDefinitionTable() {

		defaultRowHeight = getTableDefinitionTable().getRowHeight();
		
		getTableDefinitionTable().setDefaultRenderer(ImageAssetPanel.class, new ImageCellRenderer());
		getTableDefinitionTable().addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent e) {
				
				
				int column = getTableDefinitionTable().columnAtPoint(e.getPoint());
				if (column < 2) {
					return;
				}
				
				int row = getTableDefinitionTable().rowAtPoint(e.getPoint());
				
				String imageIdStr = (String) getTableDefinitionTable().getModel().getValueAt(row, column);
				
				// HACK: this is a hacky way to figure out if the button was pushed :P
				if (e.getPoint().x > getTableDefinitionTable().getSize().width - 15) {
					if (imageIdStr == null || imageIdStr.length() == 0) {
						// Add
						ImageChooserDialog chooserDialog = MapTool.getFrame().getImageChooserDialog();
						
						chooserDialog.setVisible(true);
						
						MD5Key imageId = chooserDialog.getImageId();
						if (imageId == null) {
							return;
						}
						
						imageIdStr = imageId.toString();
						
					} else {
						// Cancel
						imageIdStr = null;
					}
				} else if (e.getPoint().x > getTableDefinitionTable().getSize().width - 30) {
					// Add
					ImageChooserDialog chooserDialog = MapTool.getFrame().getImageChooserDialog();
					
					chooserDialog.setVisible(true);
					
					MD5Key imageId = chooserDialog.getImageId();
					if (imageId == null) {
						return;
					}
					
					imageIdStr = imageId.toString();
				}
				
				getTableDefinitionTable().getModel().setValueAt(imageIdStr, row, column);

				updateDefinitionTableRowHeights();
			}
		});
	}
	
	private void initTableImage() {

		tableImageAssetPanel = new ImageAssetPanel();
		tableImageAssetPanel.setPreferredSize(new Dimension(150, 150));
		tableImageAssetPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		replaceComponent("mainForm", "tableImage", tableImageAssetPanel);
	}

	public void attach(Campaign campaign) {
		this.campaign = campaign;

		updateTableList();
		getTableDefinitionTable().setModel(createLookupTableModel(null));
		updateDefinitionTableRowHeights();
		
	}
	
	public JTextField getTableNameTextField() {
		return (JTextField) getComponent("tableName");
	}

	public JTextField getTableRollTextField() {
		return (JTextField) getComponent("defaultTableRoll");
	}

	public JTable getTableDefinitionTable() {
		return (JTable) getComponent("definitionTable");
	}

	public JList getTableList() {
		return (JList) getComponent("tableList");
	}
	
	private void initDeleteTableButton() {
		JButton button = (JButton) getComponent("deleteTableButton");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String name = (String) getTableList().getSelectedValue();
				
				if (MapTool.confirm("Delete table '" + name + "'")) {
					campaign.getLookupTableMap().remove(name);
					updateTableList();
				}
			}
		});
	}

	private void initUpdateTableButton() {
		JButton button = (JButton) getComponent("updateTableButton");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String name = getTableNameTextField().getText().trim();
				if (name.length() == 0) {
					MapTool.showError("Must have a name");
					return;
				}
				
				TableModel tableModel = getTableDefinitionTable().getModel();
				if (tableModel.getRowCount() < 1) {
					MapTool.showError("Must have at least one row");
					return;
				}
				
				LookupTable lookupTable = new LookupTable(name);
				lookupTable.setRoll(getTableRollTextField().getText());
				lookupTable.setTableImage(tableImageAssetPanel.getImageId());

				MapToolUtil.uploadAsset(AssetManager.getAsset(tableImageAssetPanel.getImageId()));
				
				for (int i = 0; i < tableModel.getRowCount(); i++) {
					
					String range = ((String) tableModel.getValueAt(i, 0)).trim();
					String value = ((String) tableModel.getValueAt(i, 1)).trim();
					String imageId = (String) tableModel.getValueAt(i, 2);
					
					if (range.length() == 0) {
						continue;
					}
					
					int min = 0;
					int max = 0;
					
					int split = range.indexOf("-", range.charAt(0) == '-' ? 1 : 0); // Allow negative numbers
					try {
						if (split < 0) {
							min = Integer.parseInt(range);
							max = min;
						} else {
							min = Integer.parseInt(range.substring(0, split).trim());
							max = Integer.parseInt(range.substring(split+1).trim());
						}
					} catch (NumberFormatException nfe) {
						MapTool.showError("Could not parse range: " + range);
						return;
					}
					
					MD5Key image = null;
					if (imageId != null) {
						image = new MD5Key(imageId);
						MapToolUtil.uploadAsset(AssetManager.getAsset(image));
					}
					lookupTable.addEntry(min, max, value, image);
				}

				// This will override the table with the same name
				campaign.getLookupTableMap().put(name, lookupTable);
				
				getTableNameTextField().setText("");
				getTableDefinitionTable().setModel(createLookupTableModel(null));
				getTableRollTextField().setText("");
				
				updateTableList();
			}
		});
	}

	private void initNewTableButton() {
		JButton button = (JButton) getComponent("newTableButton");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				getTableNameTextField().setText("");
				getTableDefinitionTable().setModel(createLookupTableModel(null));
				updateDefinitionTableRowHeights();
				
				getTableNameTextField().requestFocusInWindow();
			}
		});
	}
	
	private void updateDefinitionTableRowHeights() {

		JTable table = getTableDefinitionTable();
		for (int row = 0; row < table.getRowCount(); row++) {
			
			String imageId = (String)table.getModel().getValueAt(row, 2);
			table.setRowHeight(row, imageId != null && imageId.length() > 0 ? 100 : defaultRowHeight);
		}
	}
	
	private void updateTableList() {
		
		final List<String> nameList = new ArrayList<String>();

		for (String name : campaign.getLookupTableMap().keySet()) {
			nameList.add(name);
		}
		
		Collections.sort(nameList);
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				DefaultListModel model = new DefaultListModel();
				for (String name : nameList) {
					model.addElement(name);
				}
				getTableList().setModel(model);
			}
		});
	}
	
	private void initTableList() {
		JList list = getTableList();
		list.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}

				String name = (String) getTableList().getSelectedValue();
				
				LookupTable lt = campaign.getLookupTableMap().get(name);
				
				getTableNameTextField().setText(lt != null ? lt.getName() : "");
				getTableRollTextField().setText(lt != null ? lt.getRoll() : "");
				getTableDefinitionTable().setModel(createLookupTableModel(lt));
				
				tableImageAssetPanel.setImageId(lt != null ? lt.getTableImage() : null);
				updateDefinitionTableRowHeights();

			}
		});
	}
	
	private LookupTableTableModel createLookupTableModel(LookupTable lookupTable) {

		List<List<String>> rows = new ArrayList<List<String>>();
		if (lookupTable != null) {
			for (LookupEntry entry : lookupTable.getEntryList()) {
				
				String range = entry.getMax() != entry.getMin() ? entry.getMin() + "-" + entry.getMax() : "" + entry.getMin();
				String value = entry.getValue();
				MD5Key imageId = entry.getImageId();
				
				rows.add(Arrays.asList(new String[]{range, value, imageId.toString()}));
			}
		}
			
		return new LookupTableTableModel(rows, "Range", "Value", "Image");
	}
	
	private static class ImageCellRenderer extends ImageAssetPanel implements TableCellRenderer {

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

			setImageId(value != null && ((String) value).length() > 0 ? new MD5Key((String)value) : null);
			
			return this;
		}
		
	}
	
	private static class LookupTableTableModel extends AbstractTableModel {
		
		private List<String> newRow = new ArrayList<String>();
		private List<List<String>> rowList;
		private String[] cols;

		public LookupTableTableModel(List<List<String>> rowList, String... cols) {
			this.rowList = rowList;
			this.cols = cols;
		}
		
		public int getColumnCount() {
			return cols.length;
		}
		public int getRowCount() {
			return rowList.size() + 1;
		}
		public Object getValueAt(int rowIndex, int columnIndex) {

			List<String> row = null;
			
			// Existing value
			if (rowIndex < rowList.size()) {
				row = rowList.get(rowIndex);
			} else {
				row = newRow;
			}
			
			return columnIndex < row.size() ? row.get(columnIndex) : "";
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

			boolean hasNewRow = false;
			List<String> row = null;
			if (rowIndex < rowList.size()) {
				row = rowList.get(rowIndex);
			} else {
				row = newRow;
				
				rowList.add(newRow);
				newRow = new ArrayList<String>();
				
				hasNewRow = true;
			}

			while (columnIndex >= row.size()) {
				row.add("");
			}
			
			row.set(columnIndex, (String)aValue);
			
			if (hasNewRow) {
				fireTableRowsInserted(rowList.size(), rowList.size());
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return columnIndex < 2 ? String.class : ImageAssetPanel.class;
		}
		
		@Override
		public String getColumnName(int column) {
			return cols[column];
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex != 2;
		}

	}
}