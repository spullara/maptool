package net.rptools.maptool.client.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.rptools.maptool.client.AppConstants;
import net.rptools.maptool.client.AppPreferences;
import net.rptools.maptool.client.AppSetup;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.RemoteFileDownloader;
import net.rptools.maptool.client.WebDownloader;
import net.rptools.maptool.client.swing.AbeillePanel;
import net.rptools.maptool.client.swing.GenericDialog;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.AssetManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdesktop.swingworker.SwingWorker;

import com.jidesoft.swing.FolderChooser;

public class AddResourceDialog extends AbeillePanel<AddResourceDialog.Model> {

	private static final Logger log = Logger.getLogger(AddResourceDialog.class);
	
	private static final String LIBRARY_URL = "http://library.rptools.net/1.3";
	private static final String LIBRARY_LIST_URL = LIBRARY_URL + "/listArtPacks";
	
	public enum Tab {
		LOCAL,
		WEB,
		RPTOOLS
	}
	
	private GenericDialog dialog;
	private Model model;
	private boolean downloadLibraryListInitiated;

	private boolean install = false;
	
	
	public AddResourceDialog() {
		super("net/rptools/maptool/client/ui/forms/addResourcesDialog.jfrm");
		
		setPreferredSize(new Dimension(550, 300));
		
		panelInit();
	}
	
	public boolean getInstall() {
		return install;
	}
	
	public void showDialog() {
		dialog = new GenericDialog("Add Resource to Library", MapTool.getFrame(), this);

		model = new Model();

		bind(model);

		getRootPane().setDefaultButton(getInstallButton());
		dialog.showDialog();
	}
	
	public Model getModel() {
		return model;
	}
	
	public JButton getInstallButton() {
		return (JButton) getComponent("installButton");
	}
	
	public JTextField getBrowseTextField() {
		return (JTextField) getComponent("@localDirectory");
	}
	
	public JList getLibraryList() {
		return (JList) getComponent("@rptoolsList");
	}
	
	public void initLibraryList() {
		JList list = getLibraryList();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		list.setModel(new MessageListModel(I18N.getText("dialog.addresource.downloading")));
	}

	public void initTabPane() {
		
		final JTabbedPane tabPane = (JTabbedPane) getComponent("tabPane");
		
		tabPane.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				// Hmmm, this is fragile (breaks if the order changes) rethink this later
				switch(tabPane.getSelectedIndex()) {
				case 0: model.tab = Tab.LOCAL; break;
				case 1: model.tab = Tab.WEB; break;
				case 2: model.tab = Tab.RPTOOLS; downloadLibraryList(); break;
				}
			}
		});
	}
	
	public void initLocalDirectoryButton() {
		final JButton button = (JButton) getComponent("localDirectoryButton");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				FolderChooser folderChooser = new FolderChooser();
				folderChooser.setCurrentDirectory(MapTool.getFrame().getLoadFileChooser().getCurrentDirectory());
                folderChooser.setRecentListVisible(false);
                folderChooser.setFileHidingEnabled(true);
                folderChooser.setDialogTitle(I18N.getText("msg.title.loadAssetTree"));
                
                int result = folderChooser.showOpenDialog(button.getTopLevelAncestor());
                if (result == FolderChooser.APPROVE_OPTION) {				
                	File root = folderChooser.getSelectedFolder();
    				getBrowseTextField().setText(root.getAbsolutePath());
                }
			}
		});
	}
	
	public void initInstallButton() {
		JButton button = (JButton) getComponent("installButton");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				install = true;
				if (commit()) {
					close();
				}
			}
		});
	}

	public void initCancelButton() {
		JButton button = (JButton) getComponent("cancelButton");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
	}

	private void downloadLibraryList() {
		if (downloadLibraryListInitiated) {
			return;
		}
		
		// This pattern is safe because it is only called on the EDT
		downloadLibraryListInitiated = true;
		
		new SwingWorker<Object, Object>() {
			
			ListModel model;
			
			@Override
			protected Object doInBackground() throws Exception {

				WebDownloader downloader = new WebDownloader(new URL(LIBRARY_LIST_URL));
				
				String result = downloader.read();
				if (result == null) {
					model = new MessageListModel(I18N.getText("dialog.addresource.errorDownloading"));
					return null;
				}

				DefaultListModel listModel = new DefaultListModel();

				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(result.getBytes())));
					String line = null;
					while ((line = reader.readLine()) != null) {
						listModel.addElement(new LibraryRow(line));
					}
					
					model = listModel;
				} catch (Throwable t) {
					log.error("unable to parse library list", t);
					model = new MessageListModel(I18N.getText("dialog.addresource.errorDownloading"));
				}
				return null;
			}
			
			protected void done() {
				getLibraryList().setModel(model);
			}
		}.execute();
	}
	
	@Override
	public boolean commit() {
		if (!super.commit()) {
			return false;
		}
		
		// Add the resource
		URL url = null;
		String name = "";
		switch (model.getTab()) {
		case LOCAL:
			if (StringUtils.isEmpty(model.getLocalDirectory())) {
				MapTool.showMessage("dialog.addresource.warn.filenotfound", "Error", JOptionPane.ERROR_MESSAGE, model.getLocalDirectory());
				return false;
			}
			File root = new File(model.getLocalDirectory());
			if (!root.exists()) {
				MapTool.showMessage("dialog.addresource.warn.filenotfound", "Error", JOptionPane.ERROR_MESSAGE, model.getLocalDirectory());
				return false;
			}
			if (!root.isDirectory()) {
				MapTool.showMessage("dialog.addresource.warn.directoryrequired", "Error", JOptionPane.ERROR_MESSAGE, model.getLocalDirectory());
				return false;
			}

			MapTool.getFrame().addAssetRoot(root);
			AssetManager.searchForImageReferences(root, AppConstants.IMAGE_FILE_FILTER);
			AppPreferences.addAssetRoot(root);
			
			return true;
		case WEB:
			try {
				url = new URL(model.getUrl());
			} catch (MalformedURLException e) {
				MapTool.showMessage("dialog.addresource.warn.invalidurl", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			
			break;
		case RPTOOLS:
			
			try {
				LibraryRow row = (LibraryRow) getLibraryList().getSelectedValue();
				
				url = new URL(LIBRARY_URL + "/" + row.path);
				name = row.name;
			} catch (MalformedURLException e) {
				MapTool.showMessage("dialog.addresource.warn.invalidurl", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}

			break;
		}
		
		final URL libraryUrl = url;
		final String libraryName = name;
		new SwingWorker<Object, Object>() {
			
			@Override
			protected Object doInBackground() throws Exception {

				RemoteFileDownloader downloader = new RemoteFileDownloader(libraryUrl);
				
				try {
					File tmpFile = downloader.read();
					
					System.out.println(tmpFile.getName() + " - " + tmpFile.length());
	
					AppSetup.installLibrary(libraryName, tmpFile.toURL());
					
					tmpFile.delete();
				} catch (IOException e) {
					log.error("Error downloading library: " + e, e);
					MapTool.showInformation("dialog.addresource.warn.couldnotload");
				}
				return null;
			}
		}.execute();
		
		return true;
	}
	
	private void close() {
		unbind();
		dialog.closeDialog();
	}
	
	private static class LibraryRow {
		
		private String name;
		private String path;
		private int size;
		
		public LibraryRow(String row) {
			String[] data = row.split("\\|");
			
			name = data[0];
			path = data[1];
			size = Integer.parseInt(data[2]);
		}
		
		@Override
		public String toString() {
			
			return "<html><b>" + name + "</b> <i>(" + getSizeString() + ")</i>";
		}
		
		private String getSizeString() {
			NumberFormat format = NumberFormat.getNumberInstance();
			if (size < 1000) {
				return format.format(size) + " bytes";
			}
			
			if (size < 1000000) {
				return format.format(size/1000) + " k";
			}
			
			return format.format(size/1000000) + " mb";
		}
	}
	
	public static class Model {
		
		private String localDirectory;
		private String url;
		private Tab tab = Tab.LOCAL;
		
		public String getLocalDirectory() {
			return localDirectory;
		}
		public void setLocalDirectory(String localDirectory) {
			this.localDirectory = localDirectory;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public Tab getTab() {
			return tab;
		}
		public void setTab(Tab tab) {
			this.tab = tab;
		}
		
	}
	
	private class MessageListModel extends AbstractListModel {

		private String message;
		
		public MessageListModel(String message) {
			this.message = message;
		}
		
		public Object getElementAt(int index) {
			return message;
		}
		
		public int getSize() {
			return 1;
		}
	}
}
