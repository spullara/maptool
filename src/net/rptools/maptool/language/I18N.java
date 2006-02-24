package net.rptools.maptool.language;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.KeyStroke;

public class I18N {
	private static ResourceBundle BUNDLE = ResourceBundle.getBundle("net.rptools.maptool.language.i18n");
	
	public static String getString(String key) {
		try {
			return BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return null;
		}
	}
	
	public static String getText(String key) {
		String value = getString(key);
		if (value == null) return key;
		
		return value.replaceAll("\\&", "");
	}
	
	public static int getMnemonic(String key) {
		String value = getString(key);
		if (value == null || value.length() < 2)
			return -1;

		int index = value.indexOf('&');
		if (index != -1 && index + 1 < value.length()) {
			return Character.toUpperCase(value.charAt(index + 1));
		}

		return -1;
	}
	
	private static final String ACCELERATOR_EXTENSION = ".accel";
	
	public static String getAccelerator(String key) {
		return getString(key + ACCELERATOR_EXTENSION);
	}
	
	private static final String DESCRIPTION_EXTENSION = ".description";

	public static String getDescription(String key) {
		return getString(key + DESCRIPTION_EXTENSION);
	}

  public static JMenu createMenu(String key) {
      JMenu menu = new JMenu(getText(key));
      int mnemonic = getMnemonic(key);
      if (mnemonic != -1) {
          menu.setMnemonic(mnemonic);
      }
  
      return menu;
  }
  
  /**
   * Set all of the I18N values on an action.
   * 
   * @param key Key used to look up values
   * @param action Action being modified.
   */
  public static void setAction(String key, Action action) {
    action.putValue(Action.NAME, getText(key));
    int mnemonic = getMnemonic(key);
    if (mnemonic != -1) action.putValue(Action.MNEMONIC_KEY, mnemonic);
    String accel = getAccelerator(key);
    if (accel != null) action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(accel));
    String description = getDescription(key);
    if (description != null) action.putValue(Action.SHORT_DESCRIPTION, description);
  }

}
