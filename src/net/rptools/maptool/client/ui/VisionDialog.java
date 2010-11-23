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
package net.rptools.maptool.client.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTextField;

import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Vision;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.model.vision.BlockyRoundVision;
import net.rptools.maptool.model.vision.FacingConicVision;
import net.rptools.maptool.model.vision.RoundVision;

import com.jeta.forms.components.panel.FormPanel;

public class VisionDialog extends JDialog {

	private JTextField nameTextField;
	private JTextField distanceTextField;
	private JCheckBox enabledCheckBox;
	private JComboBox typeCombo;

	public VisionDialog(Zone zone, Token token) {
		this(zone, token, null);
	}

	public VisionDialog(Zone zone, Token token, Vision vision) {
		super(MapTool.getFrame(), "Vision", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		FormPanel panel = new FormPanel("net/rptools/maptool/client/ui/forms/visionDialog.jfrm");

		initNameTextField(panel, vision);
		initEnabledCheckBox(panel, vision);
		initDistanceTextField(panel, vision);
		initTypeCombo(panel, token, vision);

		initDeleteButton(panel, token, vision);
		initOKButton(panel, zone, token);
		initCancelButton(panel);

		setContentPane(panel);
		pack();
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			SwingUtil.centerOver(this, MapTool.getFrame());
		}
		super.setVisible(b);
	}

	private void initNameTextField(FormPanel panel, Vision vision) {
		nameTextField = panel.getTextField("name");
		nameTextField.setText(vision != null ? vision.getName() : "");
	}

	private void initEnabledCheckBox(FormPanel panel, Vision vision) {
		enabledCheckBox = panel.getCheckBox("enabled");
		enabledCheckBox.setSelected(vision == null || vision.isEnabled());
	}

	private void initDistanceTextField(FormPanel panel, Vision vision) {
		distanceTextField = panel.getTextField("distance");
		distanceTextField.setText(vision != null ? Integer.toString(vision.getDistance()) : "");
	}

	private void initTypeCombo(FormPanel panel, Token token, Vision vision) {
		typeCombo = panel.getComboBox("typeCombo");
		Object[] list = null;
		if (vision != null) {
			list = new Object[] { vision };
		} else {
			list = new Object[] { new RoundVision(), new FacingConicVision(), new BlockyRoundVision() };
		}
		typeCombo.setModel(new DefaultComboBoxModel(list));
		typeCombo.setEnabled(vision == null);
		typeCombo.setSelectedIndex(0);
	}

	private void initOKButton(FormPanel panel, final Zone zone, final Token token) {
		JButton button = (JButton) panel.getButton("okButton");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (commit(zone, token)) {
					close();
				}
			}
		});
		getRootPane().setDefaultButton(button);
	}

	private void initDeleteButton(FormPanel panel, final Token token, final Vision vision) {
		JButton button = (JButton) panel.getButton("deleteButton");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				token.removeVision(vision);
				close();
			}
		});
		button.setEnabled(vision != null);
	}

	private void initCancelButton(FormPanel panel) {
		JButton button = (JButton) panel.getButton("cancelButton");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});

	}

	private boolean commit(Zone zone, Token token) {
		Vision vision = (Vision) typeCombo.getSelectedItem();

		if (distanceTextField.getText().trim().length() == 0) {
			MapTool.showError("Distance Text Field is empty, enter a distance.");
			return false;
		}

		int distance = 0;
		try {
			distance = Integer.parseInt(distanceTextField.getText());
		} catch (NumberFormatException nfex) {
			MapTool.showError("Distance must be numeric");
			return false;
		}
		vision.setName(nameTextField.getText());
		vision.setEnabled(enabledCheckBox.isSelected());
		vision.setDistance(distance);

//		token.addVision(vision);

		MapTool.serverCommand().putToken(zone.getId(), token);

		return true;
	}

	private void close() {
		setVisible(false);
	}
}
