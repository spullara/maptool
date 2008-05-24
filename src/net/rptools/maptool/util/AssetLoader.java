package net.rptools.maptool.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.rptools.lib.FileUtil;
import net.rptools.maptool.model.Asset;

import com.thoughtworks.xstream.XStream;

public class AssetLoader {

	public static void main(String[] args) throws IOException {
		
		File file = new File("c:/documents and settings/trevor/desktop/1st file");
		
		XStream xs = new XStream();
		Asset asset = (Asset) xs.fromXML(new FileInputStream(file));
		
		File newFile = new File(file.getParent() + "/" + file.getName() + ".jpg");
		if (newFile.exists()) {
			newFile.delete();
		}
		System.out.println(newFile);
		newFile.createNewFile();
		OutputStream out = new FileOutputStream(newFile);
		FileUtil.copy(new ByteArrayInputStream(asset.getImage()), out);
		out.close();
		
		System.out.println("Done");
	}
}
