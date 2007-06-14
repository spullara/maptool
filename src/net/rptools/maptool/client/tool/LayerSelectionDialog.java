package net.rptools.maptool.client.tool;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.rptools.maptool.model.Zone;

import com.jeta.forms.components.panel.FormPanel;

public class LayerSelectionDialog extends JPanel {
	
	private FormPanel panel;
	private JList list;
	private LayerSelectionListener listener;
	private Zone.Layer[] layerList;
	
	public LayerSelectionDialog(Zone.Layer[] layerList, LayerSelectionListener listener) {
		panel = new FormPanel("net/rptools/maptool/client/ui/forms/layerSelectionDialog.jfrm");
		this.listener = listener;
		this.layerList = layerList;
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		getLayerList();
		
		setLayout(new GridLayout(1, 1));
		add(panel);
	}

	public void updateViewSelection() {
		
		int index = list.getSelectedIndex();
		if (index >= 0 && listener != null) {
			listener.layerSelected((Zone.Layer) list.getModel().getElementAt(index));
		}
	}

	private JList getLayerList() {
		
		if (list == null) {
			list = panel.getList("layerList");
			
			DefaultListModel model = new DefaultListModel();
			for (Zone.Layer layer : layerList) {
				model.addElement(layer);
			}

			list.setModel(model);
			list.addListSelectionListener(new ListSelectionListener(){

				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting()) {
						return;
					}
					
					updateViewSelection();
				}
			});
			list.setSelectedIndex(0);
		}
		
		return list;
	}
	
	public static interface LayerSelectionListener {
		public void layerSelected(Zone.Layer layer);
	}
}
