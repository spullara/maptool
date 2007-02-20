package net.rptools.maptool.transfer;

import java.io.File;
import java.io.Serializable;

public interface ConsumerListener {

	public void assetComplete(Serializable id, File data);
}