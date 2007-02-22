package net.rptools.maptool.transfer;

import java.io.Serializable;

public class AssetHeader implements Serializable {

	private Serializable id;
	private String name;
	private long size;
	
	public AssetHeader(Serializable id, String name, long size) {
		this.id = id;
		this.size = size;
		this.name = name;
	}

	public Serializable getId() {
		return id;
	}

	public long getSize() {
		return size;
	}
	
	public String getName() {
		return name;
	}
}
