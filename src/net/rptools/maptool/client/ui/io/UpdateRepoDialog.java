/**
 * 
 */
package net.rptools.maptool.client.ui.io;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.rptools.lib.net.FTPLocation;
import net.rptools.lib.net.Location;
import net.rptools.lib.swing.SwingUtil;
import net.rptools.maptool.client.MapTool;

import com.jeta.forms.components.panel.FormPanel;
import com.jidesoft.swing.CheckBoxListWithSelectable;

/**
 * @author crash
 *
 */
@SuppressWarnings("serial")
public class UpdateRepoDialog extends JDialog {
	private final String UPDATE_REPO_DIALOG = "net/rptools/maptool/client/ui/forms/updateRepoDialog.jfrm";
	private final FormPanel form = new FormPanel(UPDATE_REPO_DIALOG);
	private int status = -1;
	private CheckBoxListWithSelectable list;
	private FTPLocation location;

	private JTextField saveTo;
	private JTextField hostname;
	private JTextField directory;
	private JTextField username;
	private JPasswordField password;

	public UpdateRepoDialog(JFrame frame, List<String> repos, Location loc) {
		super(frame, "Update Repository Dialog Test", true);
		add(form);
		initFields();
		initFTPLocation(loc);

		list.setListData(repos.toArray());
		list.selectAll();
		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
					int index = list.locationToIndex(e.getPoint());
					Object o = list.getModel().getElementAt(index);
					saveTo.setText( o.toString() );
					URL url = null;
					try {
						url = new URL(o.toString());
//						System.out.println("URL object contains: " + url);
						hostname.setText( url.getHost() );
					} catch (MalformedURLException e1) {
						e1.printStackTrace();
					}
				}
			}
		};
		list.addMouseListener(mouseListener);

		AbstractButton btn = form.getButton("ok");
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setStatus(JOptionPane.OK_OPTION);
				UpdateRepoDialog.this.setVisible(false);
			}
		});
		btn = form.getButton("cancel");
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setStatus(JOptionPane.CANCEL_OPTION);
				UpdateRepoDialog.this.setVisible(false);
			}
		});
	}

	protected void initFields() {
		list = (CheckBoxListWithSelectable) form.getComponentByName("checkboxList");
		saveTo = form.getTextField("saveTo");
		hostname = form.getTextField("hostname");
		directory = form.getTextField("directory");
		username = form.getTextField("username");
		password = (JPasswordField) form.getComponentByName("password");
	}

	protected void initFTPLocation(Location loc) {
		if (loc instanceof FTPLocation) {
			location = (FTPLocation) loc;
			// Copy the fields into the GUI
			hostname.setText(location.getHostname());
			directory.setText(location.getPath());
			username.setText(location.getUsername());
			password.setText(location.getPassword());
		} else {
			// We'll assign a default for hostname when the user selects a repository.
			if (MapTool.isDevelopment()) {
				hostname.setText("www.eec.com");
				username.setText("u35755092-maptool");
				password.setText("lootpam");
			}
		}
	}

	public FormPanel getForm() {
		return form;
	}

	public FTPLocation getFTPLocation() {
		if (location == null) {
			location = new FTPLocation(getUsername(), getPassword(), getHostname(), getDirectory());
		}
		return location;
	}

	public String getSaveToRepository() {
		return saveTo.getText();
	}

	public String getHostname() {
		return hostname.getText();
	}

	public String getDirectory() {
		String s = directory.getText();
		if (s == null || s.length() == 0)
			s = "/";
		return s;
	}

	public String getUsername() {
		return username.getText();
	}

	public String getPassword() {
		return new String(password.getPassword());
	}

	public List<String> getSelectedRepositories() {
		Object[] objects = list.getSelectedObjects();
		List<String> repoList = new ArrayList<String>(objects.length);
		for (int i = 0; i < objects.length; i++) {
			Object s = objects[i];
//			System.out.println("repoList[" + i + "] = " + s.toString() + ", type = " + s.getClass().getCanonicalName());
			repoList.add(s.toString());
		}
		return repoList;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int s) {
		status = s;
	}

	public void setVisible(boolean b) {
		if (b) {
			SwingUtil.centerOver(this, MapTool.getFrame());
		}
		super.setVisible(b);
	}
}
