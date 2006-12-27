package net.rptools.maptool.client.ui;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;

import net.rptools.lib.image.ImageUtil;
import net.rptools.maptool.client.tool.MeasureTool;
import net.rptools.maptool.client.tool.PointerTool;
import net.rptools.maptool.client.tool.StampTool;
import net.rptools.maptool.client.tool.TextTool;
import net.rptools.maptool.client.tool.drawing.ConeTemplateTool;
import net.rptools.maptool.client.tool.drawing.FreehandExposeTool;
import net.rptools.maptool.client.tool.drawing.FreehandTool;
import net.rptools.maptool.client.tool.drawing.LineTemplateTool;
import net.rptools.maptool.client.tool.drawing.LineTool;
import net.rptools.maptool.client.tool.drawing.OvalExposeTool;
import net.rptools.maptool.client.tool.drawing.OvalTool;
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
		setFloatable(false);
		setRollover(true);
		
		this.toolbox = toolbox;
		optionPanel = new JPanel(new CardLayout());
		optionPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED), BorderFactory.createEmptyBorder(2, 2, 2, 2)));
		
		add(createButton("net/rptools/maptool/client/image/tool/PointerBlue16.png", createPointerPanel()));
		add(createButton("net/rptools/maptool/client/image/tool/FreehandLine16.png", createDrawPanel()));
		add(createButton("net/rptools/maptool/client/image/tool/RadiusTemplate.png", createTemplatePanel()));
		add(createButton("net/rptools/maptool/client/image/tool/FOGRectangle16.png", createFogPanel()));
		add(createButton("net/rptools/maptool/client/image/tool/RectangleBlue16.png", createTopologyPanel()));
		add(Box.createHorizontalStrut(10));
		add(optionPanel);
		add(Box.createHorizontalGlue());
		
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
		panel.add(PolygonTopologyTool.class);	

		panel.fill();
		return panel;
	}
	
	private JToggleButton createButton(final String icon, final OptionPanel panel) {
		final JToggleButton button = new JToggleButton();
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
