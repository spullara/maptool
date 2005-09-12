package net.rptools.maptool.client.ui;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JDialog;

public class AboutDialog extends JDialog {

	private JPanel jContentPane = null;
	private JPanel southPanel = null;
	private JPanel centerPanel = null;

	/**
	 * This is the default constructor
	 */
	public AboutDialog() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(489, 348);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getSouthPanel(), java.awt.BorderLayout.SOUTH);
			jContentPane.add(getCenterPanel(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes southPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSouthPanel() {
		if (southPanel == null) {
			southPanel = new JPanel();
		}
		return southPanel;
	}

	/**
	 * This method initializes centerPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getCenterPanel() {
		if (centerPanel == null) {
			centerPanel = new JPanel();
		}
		return centerPanel;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
