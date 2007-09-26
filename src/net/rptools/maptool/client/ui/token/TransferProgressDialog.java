/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft, Jay Gorrell
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

package net.rptools.maptool.client.ui.token;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.swing.AbeillePanel;
import net.rptools.maptool.client.swing.GenericDialog;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.transfer.AssetConsumer;
import net.rptools.maptool.transfer.ConsumerListener;

/**
 * This dialog is used to display all of the assets being transferred
 */
public class TransferProgressDialog extends AbeillePanel<Token> implements ConsumerListener {

	private GenericDialog dialog;
	
	public TransferProgressDialog() {
		super("net/rptools/maptool/client/ui/forms/transferProgressDialog.jfrm");

		panelInit();
	}
	
	public void showDialog() {
		dialog = new GenericDialog("Assets in Transit", MapTool.getFrame(), this, false){
			@Override
			public void showDialog() {
				MapTool.getAssetTransferManager().addConsumerListener(TransferProgressDialog.this);
				super.showDialog();
			}
			@Override
			public void closeDialog() {
				MapTool.getAssetTransferManager().removeConsumerListener(TransferProgressDialog.this);
				super.closeDialog();
			}
		};
		
		getRootPane().setDefaultButton(getCloseButton());
		dialog.showDialog();
	}
	
	public JButton getCloseButton() {
		return (JButton) getComponent("closeButton");
	}
	
	public JTable getTransferTable() {
		return (JTable) getComponent("transferTable");
	}

	public void initCloseButton() {
		getCloseButton().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dialog.closeDialog();
			}
		});
	}
	
	private void updateTransferTable() {
		
		final TransferTableModel model = new TransferTableModel();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				getTransferTable().setModel(model);
			}
		});
	}
	
	public void initTransferTable() {
		getTransferTable().setBackground(Color.white);
		updateTransferTable();
	}
	
	private static class TransferTableModel extends AbstractTableModel {

		private List<AssetConsumer> consumerList = MapTool.getAssetTransferManager().getAssetConsumers();
		
		public TransferTableModel() {
			
		}
		
		public int getColumnCount() {
			return 3;
		}
		public int getRowCount() {
			return consumerList.size();
		}
		public Object getValueAt(int rowIndex, int columnIndex) {
			
			AssetConsumer consumer = consumerList.get(rowIndex);
			
			switch(columnIndex) {
			case 0: return consumer.getId();
			case 1: return consumer.getSize();
			case 2: return NumberFormat.getPercentInstance().format(consumer.getPercentComplete());
			}
			
			return null;
		}
		@Override
		public String getColumnName(int column) {
			switch(column) {
			case 0: return "ID";
			case 1: return "Size";
			case 2: return "Progress";
			}
			return "";
		}
	}
	
	////
	// CONSUMER LISTENER
	public void assetComplete(Serializable id, String name, File data) {
		updateTransferTable();
	}
	public void assetUpdated(Serializable id) {
		updateTransferTable();
	}
}
