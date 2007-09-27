package net.rptools.maptool.client.ui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.plaf.basic.BasicToolBarUI;

import net.rptools.lib.image.ImageUtil;
import net.rptools.maptool.client.tool.FacingTool;
import net.rptools.maptool.client.tool.GridTool;
import net.rptools.maptool.client.tool.MeasureTool;
import net.rptools.maptool.client.tool.PointerTool;
import net.rptools.maptool.client.tool.StampTool;
import net.rptools.maptool.client.tool.TextTool;
import net.rptools.maptool.client.tool.drawing.ConeTemplateTool;
import net.rptools.maptool.client.tool.drawing.FreehandExposeTool;
import net.rptools.maptool.client.tool.drawing.FreehandTool;
import net.rptools.maptool.client.tool.drawing.HollowOvalTopologyTool;
import net.rptools.maptool.client.tool.drawing.HollowRectangleTopologyTool;
import net.rptools.maptool.client.tool.drawing.LineTemplateTool;
import net.rptools.maptool.client.tool.drawing.LineTool;
import net.rptools.maptool.client.tool.drawing.OvalExposeTool;
import net.rptools.maptool.client.tool.drawing.OvalTool;
import net.rptools.maptool.client.tool.drawing.OvalTopologyTool;
import net.rptools.maptool.client.tool.drawing.PolyLineTopologyTool;
import net.rptools.maptool.client.tool.drawing.PolygonExposeTool;
import net.rptools.maptool.client.tool.drawing.PolygonTopologyTool;
import net.rptools.maptool.client.tool.drawing.RadiusTemplateTool;
import net.rptools.maptool.client.tool.drawing.RectangleExposeTool;
import net.rptools.maptool.client.tool.drawing.RectangleTool;
import net.rptools.maptool.client.tool.drawing.RectangleTopologyTool;

public class ToolbarPanel extends JToolBar {

	private ButtonGroup buttonGroup = new ButtonGroup();
	private JPanel optionPanel;
	private Toolbox toolbox;
	
	public ToolbarPanel(Toolbox toolbox) {
		setRollover(true);
		
		this.toolbox = toolbox;
		optionPanel = new JPanel(new CardLayout());
		
		final OptionPanel pointerGroupOptionPanel = createPointerPanel();
		final JToggleButton pointerGroupButton = createButton("net/rptools/maptool/client/image/tool/pointer-blue.png", "net/rptools/maptool/client/image/tool/pointer-blue-off.png", pointerGroupOptionPanel, "Interaction Tools"); 
		
		pointerGroupButton.setSelected(true);
		pointerGroupOptionPanel.activate();
		
		final JSeparator vertSplit = new JSeparator(JSeparator.VERTICAL);
		final Component vertSpacer = Box.createHorizontalStrut(10);
		
		final JSeparator horizontalSplit = new JSeparator(JSeparator.HORIZONTAL);
		horizontalSplit.setVisible(false);
		final Component horizontalSpacer = Box.createVerticalStrut(10);
		horizontalSpacer.setVisible(false);
		
		add(pointerGroupButton);
		add(createButton("net/rptools/maptool/client/image/tool/draw-blue.png", "net/rptools/maptool/client/image/tool/draw-blue-off.png", createDrawPanel(), "Drawing Tools"));
		add(createButton("net/rptools/maptool/client/image/tool/temp-blue-cone.png", "net/rptools/maptool/client/image/tool/temp-blue-off.png", createTemplatePanel(), "Template Tools"));
		add(createButton("net/rptools/maptool/client/image/tool/fog-blue.png", "net/rptools/maptool/client/image/tool/fog-blue-off.png", createFogPanel(), "Fog of War tools"));
		add(createButton("net/rptools/maptool/client/image/tool/eye-blue.png", "net/rptools/maptool/client/image/tool/eye-blue-off.png", createTopologyPanel(), "Topology tools"));
		add(createZoneSelectionButton());
		add(vertSplit);
		add(horizontalSplit);
		add(vertSpacer);
		add(horizontalSpacer);
		add(optionPanel);
		
		// Non visible tools
		toolbox.createTool(GridTool.class);
		toolbox.createTool(FacingTool.class);
	
		addPropertyChangeListener("orientation", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				
				int orientation = (Integer) evt.getNewValue();
				
				horizontalSplit.setVisible(orientation == JToolBar.VERTICAL);
				horizontalSpacer.setVisible(orientation == JToolBar.VERTICAL);
				
				vertSplit.setVisible(orientation == JToolBar.HORIZONTAL);
				vertSpacer.setVisible(orientation == JToolBar.HORIZONTAL);
			}
		});
		
	}
	
	private JButton createZoneSelectionButton() {
		final JButton button = new JButton(new ImageIcon(getClass().getClassLoader().getResource("net/rptools/maptool/client/image/tool/btn-world-off.png")));
		button.setSelectedIcon(new ImageIcon(getClass().getClassLoader().getResource("net/rptools/maptool/client/image/tool/btn-world.png")));
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

		return panel;
	}
	
	private OptionPanel createDrawPanel() {
		OptionPanel panel = new OptionPanel();
		
		panel.add(FreehandTool.class);
		panel.add(LineTool.class);
		panel.add(RectangleTool.class);
		panel.add(OvalTool.class);
		panel.add(TextTool.class);

		return panel;
	}
	
	private OptionPanel createTemplatePanel() {
		OptionPanel panel = new OptionPanel();
		
		panel.add(RadiusTemplateTool.class);
		panel.add(ConeTemplateTool.class);
		panel.add(LineTemplateTool.class);

		return panel;
	}
	
	private OptionPanel createFogPanel() {
		OptionPanel panel = new OptionPanel();
		
		panel.add(RectangleExposeTool.class);
		panel.add(OvalExposeTool.class);
		panel.add(PolygonExposeTool.class);
		panel.add(FreehandExposeTool.class);

		return panel;
	}
	
	private OptionPanel createTopologyPanel() {
		OptionPanel panel = new OptionPanel();
		
		panel.add(RectangleTopologyTool.class);
		panel.add(HollowRectangleTopologyTool.class);
		panel.add(OvalTopologyTool.class);
		panel.add(HollowOvalTopologyTool.class);		
		panel.add(PolygonTopologyTool.class);
		panel.add(PolyLineTopologyTool.class);
		
		return panel;
	}
	
	private JToggleButton createButton(final String icon, final String offIcon, final OptionPanel panel, String tooltip) {
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
			button.setIcon(new ImageIcon(ImageUtil.getImage(offIcon)));
			button.setSelectedIcon(new ImageIcon(ImageUtil.getImage(icon)));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
        

		optionPanel.add(panel, icon);
		
		buttonGroup.add(button);
		
		return button;
	}

	private class OptionPanel extends JToolBar {

		private Class<? extends Tool> firstTool;
		private Class<? extends Tool> currentTool;
		
		public OptionPanel() {
			setFloatable(false);
			setRollover(true);
			setBorder(null);
			setBorderPainted(false);
			
			ToolbarPanel.this.addPropertyChangeListener("orientation", new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					setOrientation((Integer)evt.getNewValue());
				}
			});
		}

		public void add(Class<? extends Tool> toolClass) {
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
		
		private void activate() {
			
			if (currentTool == null) {
				currentTool = firstTool;
			}

			toolbox.setSelectedTool(currentTool);
		}
	}
}
