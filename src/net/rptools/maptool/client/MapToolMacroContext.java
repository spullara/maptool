/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package net.rptools.maptool.client;

public class MapToolMacroContext {
	/** The name of the macro being executed. */
	private final String name;

	/** Where the macro comes from. */
	private final String source;

	/** Is the macro trusted or not. */
	private final boolean trusted;

	/** The index of the button that was clicked on to fire of this macro*/
	private int macroButtonIndex;

	/**
	 * Creates a new Macro Context.
	 * @param name The name of the macro.
	 * @param source The source location of the macro.
	 * @param trusted Is the macro trusted or not.
	 */
	public MapToolMacroContext(String name, String source, boolean trusted) {
		this(name, source, trusted, -1);
	}

	/**
	 * Creates a new Macro Context.
	 * @param name The name of the macro.
	 * @param source The source location of the macro.
	 * @param trusted Is the macro trusted or not.
	 * @param macroButtonIndex The index of the button that ran this command.
	 */
	public MapToolMacroContext(String name, String source, boolean trusted, int macroButtonIndex) {
		this.name = name;
		this.source = source;
		this.trusted = trusted;
		this.macroButtonIndex = macroButtonIndex;
	}



	/**
	 * Gets the name of the macro context.
	 * @return the name of the macro context.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the source location of the macro context.
	 * @return the source location of the macro context.
	 */
	public String getSouce() {
		return source;
	}

	/**
	 * Gets if the macro context is trusted or not.
	 * @return if the macro context is trusted or not.
	 */
	public boolean isTrusted() {
		return trusted;
	}


	/**
	 * Gets the index of the macro button that this macro is in
	 * @return the index of the macro button.
	 */
	public int getMacroButtonIndex() {
		return macroButtonIndex;
	}
}
