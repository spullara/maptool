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
package net.rptools.maptool.client.tool;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.Zone;

import com.jeta.forms.components.panel.FormPanel;

public class LayerSelectionDialog extends JPanel {

	private final FormPanel panel;
	private JList list;
	private final LayerSelectionListener listener;
	private final Zone.Layer[] layerList;

	public LayerSelectionDialog(Zone.Layer[] layerList, LayerSelectionListener listener) {
		panel = new FormPanel("net/rptools/maptool/client/ui/forms/layerSelectionDialog.xml");
		this.listener = listener;
		this.layerList = layerList;
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		getLayerList();

		setLayout(new GridLayout(1, 1));
		add(panel);
	}

	public void fireViewSelectionChange() {

		int index = list.getSelectedIndex();
		if (index >= 0 && listener != null) {
			listener.layerSelected((Zone.Layer) list.getModel().getElementAt(index));
		}
	}

	public void updateViewList() {
		getLayerList().setSelectedValue(MapTool.getFrame().getCurrentZoneRenderer().getActiveLayer(), true);
	}

	private JList getLayerList() {

		if (list == null) {
			list = panel.getList("layerList");

			DefaultListModel model = new DefaultListModel();
			for (Zone.Layer layer : layerList) {
				model.addElement(layer);
			}

			list.setModel(model);
			list.addListSelectionListener(new ListSelectionListener() {

				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting()) {
						return;
					}

					fireViewSelectionChange();
				}
			});
			list.setSelectedIndex(0);
		}

		return list;
	}

	public void setSelectedLayer(Zone.Layer layer) {
		list.setSelectedValue(layer, true);
	}

	public static interface LayerSelectionListener {
		public void layerSelected(Zone.Layer layer);
	}
}
