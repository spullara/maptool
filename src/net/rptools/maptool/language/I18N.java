/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package net.rptools.maptool.language;

import java.awt.AWTKeyStroke;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.KeyStroke;

import net.rptools.maptool.client.AppActions;
import net.rptools.maptool.client.MapTool;

import org.apache.log4j.Logger;

/**
 * This class is the front-end for all string handling. The goal is for all text
 * to be external from the application source so that translations can be done
 * without editing source code. To that end, this class will locate the
 * <b>i18n.properties</b> file for the current locale and read the string values
 * from it, returning the results.
 * 
 * As MapTool uses a base name for the string and extensions for alternate
 * pieces (such as <code>action.loadMap</code> as the base and
 * <code>action.loadMap.accel</code> as the menu accelerator key) there are
 * different methods used to return the different components.
 * 
 * The ResourceBundle name is <b>net.rptools.maptool.language.i18n</b>.
 * 
 * @author tcroft
 */

public class I18N {
	private static ResourceBundle BUNDLE = ResourceBundle.getBundle("net.rptools.maptool.language.i18n");

	private static final Logger log = Logger.getLogger(I18N.class);
	
	/**
	 * Returns the String that results from a lookup within the properties file.
	 * 
	 * @param key
	 *            the component to search for
	 * @return the String found or <code>null</code>
	 */
	public static String getString(String key) {
		try {
			return BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return null;
		}
	}

	/**
	 * Returns the text associated with the given key after removing any menu
	 * mnemonic. So for the key <code>action.loadMap</code> that has the value
	 * "&Load Map" in the properties file, this method returns "Load Map".
	 * 
	 * @param key
	 *            the component to search for
	 * @return the String found with mnemonics removed, or the input key if not
	 *         found
	 */
	public static String getText(String key) {
		String value = getString(key);
		if (value == null) {
			log.debug("Cannot find key '" + key + "' in properties file.");
			return key;
		}
		return value.replaceAll("\\&", "");
	}

	/**
	 * Functionally identical to <code>getText(String key)</code> except that
	 * this one bundles the formatting calls into this code. This version of the
	 * method is truly only needed when the string being retrieved contains
	 * parameters. In MapTool, this commonly means the player's name or a
	 * filename. See the "Parameterized Strings" section of the
	 * <b>i18n.properties</b> file for example usage. Full documentation for
	 * this technique can be found under {@link MessageFormat#format}.
	 * 
	 * @param key
	 *            the <code>propertyKey</code> to use for lookup in the
	 *            properties file
	 * @param args
	 *            parameters needed for formatting purposes
	 * @return the formatted String
	 */
	public static String getText(String key, Object... args) {
		// If the key doesn't exist in the file, the key becomes the format and
		// nothing will be substituted; it's a little extra work, but is not the normal case
		// anyway.
		String msg = MessageFormat.format(getText(key), args);
		return msg;
	}

	/**
	 * Returns the character to use as the menu mnemonic for the given key. This
	 * method searches the properties file for the given key. If the value
	 * contains an ampersand ("&amp;") the character following the ampersand is
	 * converted to uppercase and returned.
	 * 
	 * @param key
	 *            the component to search for
	 * @return the character to use as the mnemonic (as an <code>int</code>)
	 */
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

	/**
	 * Returns the text associated with the given key that is to be used as the
	 * menu accelerator for the Action. This method uses the same key as used by
	 * the prior methods, but appends the string ACCELERATOR_EXTENSION to the
	 * end. This allows a single key to be composed of multiple (optional)
	 * parts.
	 * 
	 * Note that the modifier key used by the platform to initiate the sequence
	 * is not included in the properties file. This is because the key changes
	 * from platform to platform. For example, on Windows the key is the Control
	 * key, while on Mac OSX the key is the Command key. The AWT Toolkit
	 * represents these differently ("ctrl" on Windows and "meta" on OSX) so our
	 * only choice is to eliminate the modifiers from the properties file and
	 * add them back in programmatically. The application does this by
	 * retrieving the platform's <code>menuShortcut</code> and saving it in a
	 * <code>final</code> field. The value of that field is used as the modifier
	 * throughout the application. The properties file may still specify other
	 * modifiers, such as "Shift" or "Alt".
	 * 
	 * @param key
	 *            the component to search for
	 * @return the String value of the given key's accelerator
	 */
	public static String getAccelerator(String key) {
		return getString(key + ACCELERATOR_EXTENSION);
	}

	private static final String DESCRIPTION_EXTENSION = ".description";

	/**
	 * Returns the description text for the given key. This text normally
	 * appears in the statusbar of the main application frame. As described by
	 * the {@link #getAccelerator(String)} method, the input key has the string
	 * DESCRIPTION_EXTENSION appended to it.
	 * 
	 * @param key
	 * @return
	 */
	public static String getDescription(String key) {
		return getString(key + DESCRIPTION_EXTENSION);
	}

	/**
	 * Returns a JMenu object to represent the given Action key. The key is used
	 * to locate the menu text in the properties file. The menu mnemonic is
	 * extracted as well and assigned to the JMenu object that is then returned.
	 * 
	 * @param key
	 *            the component to search for
	 * @return the JMenu created from the key's property values
	 */
	public static JMenu createMenu(String key) {
		JMenu menu = new JMenu(getText(key));
		int mnemonic = getMnemonic(key);
		if (mnemonic != -1 && !MapTool.MAC_OS_X) {
			menu.setMnemonic(mnemonic);
		}

		return menu;
	}

	/**
	 * <p>
	 * Set all of the I18N values on an Action by retrieving said values from
	 * the properties file.
	 * </p>
	 * <p>
	 * This is a compatibility function that calls {@link #setAction(String, Action, boolean)}.
	 * </p>
	 * 
	 * @param key
	 *            Key used to look up values
	 * @param action
	 *            Action being modified
	 */
	public static void setAction(String key, Action action) {
		setAction(key, action, true);
	}

	/**
	 * <p>
	 * Set all of the I18N values on an Action by retrieving said values from
	 * the properties file.
	 * </p>
	 * <p>
	 * Uses the <code>key</code> as the index for the properties file to set
	 * the <code>Action.NAME</code> field of <b>action</b>.
	 * </p>
	 * <p>
	 * The string
	 * used for the <code>NAME</code> is searched for an ampersand ("&amp;")
	 * to determine the mnemonic used by any menu item (no mnemonic is set
	 * if there is no ampersand).
	 * </p>
	 * <p>
	 * The <code>key</code> string has "<code>.accel</code>"
	 * appended to it and the properties file is searched again, this time to obtain a
	 * string representing the shortcut key.
	 * </p>
	 * <p>
	 * If <b>addMenuShortcut</b> is
	 * <code>true</code> then the proper shortcut key for the platform is added
	 * to the modifiers for the keystroke ({@link AppActions#menuShortcut} and
	 * any menu items that do not require modifiers, such as {@link AppActions#ZOOM_IN}).
	 * </p>
	 * @param key String to use as an index into the <b>i18n.properties</b> file
	 * @param action Action used to store the retrieved settings
	 * @param addMenuShortcut whether to add the platform's menu shortcut key mask
	 * (usually <code>true</code>)
	 */
	public static void setAction(String key, Action action, boolean addMenuShortcut) {
		action.putValue(Action.NAME, getText(key));
		int mnemonic = getMnemonic(key);
		if (mnemonic != -1)
			action.putValue(Action.MNEMONIC_KEY, mnemonic);
		String accel = getAccelerator(key);
		if (accel != null) {
			KeyStroke k = KeyStroke.getKeyStroke(accel);
			if (k == null) {
				System.out.println("Bad accelerator '" + accel + "' for " + key);
			} else if (addMenuShortcut) {
				int modifiers = k.getModifiers() | AppActions.menuShortcut;
				if (k.getKeyCode() != 0)
					k = KeyStroke.getKeyStroke(k.getKeyCode(), modifiers);
				else
					k = KeyStroke.getKeyStroke(k.getKeyChar(), modifiers);
			}
			action.putValue(Action.ACCELERATOR_KEY, k);
//			System.err.println("I18N.setAction(\"" + key + "\") = " + k);
		}
		String description = getDescription(key);
		if (description != null)
			action.putValue(Action.SHORT_DESCRIPTION, description);
	}
}
