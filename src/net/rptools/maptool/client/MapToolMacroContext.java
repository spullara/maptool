package net.rptools.maptool.client;

import java.util.Stack;

public class MapToolMacroContext {

	/** The name of the macro being executed. */
	private final String name;
	
	/** Where the macro comes from. */
	private final String source;
	
	/** Is the macro trusted or not. */
	private final boolean trusted;
	
	
	/** 
	 * Creates a new Macro Context.
	 * @param name The name of the macro.
	 * @param source The source location of the macro.
	 * @param trusted Is the macro trusted or not.
	 */
	public MapToolMacroContext(String name, String source, boolean trusted) {
		this.name = name;
		this.source = source;
		this.trusted = trusted;
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
}
