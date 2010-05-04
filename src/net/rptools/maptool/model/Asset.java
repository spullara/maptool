/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.rptools.maptool.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import net.rptools.lib.MD5Key;
import net.rptools.maptool.client.MapTool;

import com.thoughtworks.xstream.annotations.XStreamConverter;

/**
 * The binary representation of an image.
 */
public class Asset {
	private MD5Key id;
	private String name;
	private String extension;

	@XStreamConverter(AssetImageConverter.class)
	private byte[] image;

	protected Asset() {
	}

	public Asset(String name, byte[] image) {
		this.image = image;
		this.name = name;
		if (image != null) {
			this.id = new MD5Key(image);
			extension = null;
			getImageExtension();
		}
	}

	public Asset(MD5Key id) {
		this.id = id;
	}

	public MD5Key getId() {
		return id;
	}

	public void setId(MD5Key id) {
		this.id = id;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
		extension = null;
		getImageExtension();
	}

	public String getImageExtension() {
		if (extension == null) {
			extension = "";
			try {
				if (image != null && image.length >= 4) {
					InputStream is = new ByteArrayInputStream(image);
					ImageInputStream iis = ImageIO.createImageInputStream(is);
					Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
					if (readers.hasNext()) {
						ImageReader reader = readers.next();
						reader.setInput(iis);
						extension = reader.getFormatName().toLowerCase();
					}
				}
			} catch (IOException e) {
				MapTool.showError("IOException?!", e);	// Can this happen??
			}
		}
		return extension;
	}

	public String getName() {
		return name;
	}

	public boolean isTransfering() {
		return AssetManager.isAssetRequested(id);
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Asset)) {
			return false;
		}
		Asset asset = (Asset) obj;
		return asset.getId().equals(getId());
	}
}
