package net.rptools.maptool.transfer;

import java.io.Serializable;

public class AssetHeader implements Serializable {

	private Serializable id;
	private long size;
	
	public AssetHeader(Serializable id, long size) {
		this.id = id;
		this.size = size;
	}

	public Serializable getId() {
		return id;
	}

	public long getSize() {
		return size;
	}
}
