package net.rptools.maptool.client.ui;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import net.rptools.lib.image.ImageUtil;
import net.rptools.maptool.client.AppListeners;
import net.rptools.maptool.client.ZoneActivityListener;
import net.rptools.maptool.client.tool.FacingTool;
import net.rptools.maptool.client.tool.GridTool;
import net.rptools.maptool.client.tool.MeasureTool;
import net.rptools.maptool.client.tool.PointerTool;
import net.rptools.maptool.client.tool.StampTool;
import net.rptools.maptool.client.tool.TextTool;
import net.rptools.maptool.client.tool.drawing.ConeTemplateTool;
import net.rptools.maptool.client.tool.drawing.FreehandExposeTool;
import net.rptools.maptool.client.tool.drawing.FreehandTool;
import net.rptools.maptool.client.tool.drawing.HollowRectangleTopologyTool;
import net.rptools.maptool.client.tool.drawing.LineTemplateTool;
import net.rptools.maptool.client.tool.drawing.LineTool;
import net.rptools.maptool.client.tool.drawing.OvalExposeTool;
import net.rptools.maptool.client.tool.drawing.OvalTool;
import net.rptools.maptool.client.tool.drawing.PolyLineTopologyTool;
import net.rptools.maptool.client.tool.drawing.PolygonExposeTool;
import net.rptools.maptool.client.tool.drawing.PolygonTopologyTool;
import net.rptools.maptool.client.tool.drawing.RadiusTemplateTool;
import net.rptools.maptool.client.tool.drawing.RectangleExposeTool;
import net.rptools.maptool.client.tool.drawing.RectangleTool;
import net.rptools.maptool.client.tool.drawing.RectangleTopologyTool;
import net.rptools.maptool.model.Zone;

public class ToolbarPanel extends JToolBar {

	private ButtonGroup buttonGroup = new ButtonGroup();
	private JPanel optionPanel;
	private Toolbox toolbox;
	
	public ToolbarPanel(Toolbox toolbox) {
		setFloatable(false);
		setRollover(true);
		
		this.toolbox = toolbox;
		optionPanel = new JPanel(new CardLayout());
		
		add(createButton("net/rptools/maptool/client/image/tool/pointer.png", createPointerPanel(), "Interaction Tools"));
		add(createButton("net/rptools/maptool/client/image/tool/pencil.png", createDrawPanel(), "Drawing Tools"));
		add(createButton("net/rptools/maptool/client/image/tool/template.png", createTemplatePanel(), "Template Tools"));
		add(createButton("net/rptools/maptool/client/image/tool/fogsqr.png", createFogPanel(), "Fog of War tools"));
		add(createButton("net/rptools/maptool/client/image/tool/eye.png", createTopologyPanel(), "Topology tools"));
		add(Box.createHorizontalStrut(10));
		add(new JSeparator(JSeparator.VERTICAL));
		add(Box.createHorizontalStrut(10));
		add(optionPanel);
		add(Box.createHorizontalGlue());
		add(Box.createHorizontalStrut(10));
		add(new JSeparator(JSeparator.VERTICAL));
		add(Box.createHorizontalStrut(10));
		add(createZoneNameLabel());
		add(Box.createHorizontalStrut(5));
		add(createZoneSelectionButton());
		
		// Non visible tools
		toolbox.createTool(GridTool.class);
		toolbox.createTool(FacingTool.class);
		
	}
	
	private JLabel createZoneNameLabel() {
		
		final JLabel label = new JLabel("", JLabel.RIGHT);
		label.setMinimumSize(new Dimension(150, 10));
		label.setPreferredSize(new Dimension(150, 16));
		AppListeners.addZoneListener(new ZoneActivityListener() {
			public void zoneActivated(Zone zone) {
				String name = zone.getName();
				if (name == null || name.length() == 0) {
					name = "Map";
				}
				label.setText(name);
			}
			public void zoneAdded(Zone zone) {
			}
		});
		
		return label;
	}
	
	private JButton createZoneSelectionButton() {
		final JButton button = new JButton(new ImageIcon(getClass().getClassLoader().getResource("net/rptools/maptool/client/image/application_double.png")));
		button.setToolTipText("Select Map");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ZoneSelectionPopup popup = new ZoneSelectionPopup();
				popup.show(button, button.getSize().width - popup.getPreferredSize().width , 0);
				
			}
		});
		return button;
	}
	
	private OptionPanel createPointerPanel() {
		OptionPanel panel = new OptionPanel();
		panel.add(PointerTool.class);
		panel.add(StampTool.class);
		panel.add(MeasureTool.class);

		panel.fill();
		return panel;
	}
	
	private OptionPanel createDrawPanel() {
		OptionPanel panel = new OptionPanel();
		
		panel.add(FreehandTool.class);
		panel.add(LineTool.class);
		panel.add(RectangleTool.class);
		panel.add(OvalTool.class);
		panel.add(TextTool.class);

		panel.fill();
		return panel;
	}
	
	private OptionPanel createTemplatePanel() {
		OptionPanel panel = new OptionPanel();
		
		panel.add(RadiusTemplateTool.class);
		panel.add(ConeTemplateTool.class);
		panel.add(LineTemplateTool.class);

		panel.fill();
		return panel;
	}
	
	private OptionPanel createFogPanel() {
		OptionPanel panel = new OptionPanel();
		
		panel.add(RectangleExposeTool.class);
		panel.add(OvalExposeTool.class);
		panel.add(PolygonExposeTool.class);
		panel.add(FreehandExposeTool.class);

		panel.fill();
		return panel;
	}
	
	private OptionPanel createTopologyPanel() {
		OptionPanel panel = new OptionPanel();
		
		panel.add(RectangleTopologyTool.class);
		panel.add(HollowRectangleTopologyTool.class);
		panel.add(PolygonTopologyTool.class);	
		panel.add(PolyLineTopologyTool.class);	

		panel.fill();
		return panel;
	}
	
	private JToggleButton createButton(final String icon, final OptionPanel panel, String tooltip) {
		final JToggleButton button = new JToggleButton();
		button.setToolTipText(tooltip);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (button.isSelected()) {
					panel.activate();
					((CardLayout)optionPanel.getLayout()).show(optionPanel, icon);
				}
			}
		});
		try {
			button.setIcon(new ImageIcon(ImageUtil.getImage(icon)));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
        

		optionPanel.add(panel, icon);
		
		buttonGroup.add(button);
		
		return button;
	}

	private class OptionPanel extends JToolBar {

		private Class firstTool;
		private Class currentTool;
		
		public OptionPanel() {
			setLayout(new GridBagLayout());
//			setLayout(new FlowLayout(FlowLayout.LEFT));
			setFloatable(false);
			setRollover(true);
			setBorder(null);
			setBorderPainted(false);
		}

		public void add(Class toolClass) {
			if (firstTool == null) {
				firstTool = toolClass;
			}
			
			final Tool tool = toolbox.createTool(toolClass); 
			tool.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (tool.isSelected()) {
						currentTool = tool.getClass();
					}
				}
			});
			
			add(tool);
		}
		
		@Override
		public Component add(Component comp) {

			GridBagConstraints constraints = new GridBagConstraints();
			constraints.gridx = getComponentCount();
			constraints.gridy = 1;
			
			super.add(comp, constraints);
			return comp;
		}
		
		public void fill() {

			GridBagConstraints constraints = new GridBagConstraints();
			constraints.gridx = getComponentCount();
			constraints.gridy = 1;
			constraints.weightx = 1;
			
			super.add(new JLabel(), constraints);
		}
		
		private void activate() {
			
			if (currentTool == null) {
				currentTool = firstTool;
			}

			toolbox.setSelectedTool(currentTool);
		}
	}
}
