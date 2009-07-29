package net.rptools.maptool.client;

import java.io.File;

import junit.framework.TestCase;

public class AppUtilTest extends TestCase {

	public void testGetDataDir() throws Exception {
		
		AppUtil.reset();
		assertEquals(new File(System.getProperty("user.home") + "/.maptool"), AppUtil.getDataDir());
		
		AppUtil.reset();
		System.setProperty(AppUtil.DATADIR_PROPERTY_NAME, ".maptool2");
		assertEquals(new File(System.getProperty("user.home") + "/.maptool2"), AppUtil.getDataDir());
		
		AppUtil.reset();
		System.setProperty(AppUtil.DATADIR_PROPERTY_NAME, "/data/test");
		assertEquals(new File("/data/test"), AppUtil.getDataDir());
		
		AppUtil.reset();
		System.setProperty(AppUtil.DATADIR_PROPERTY_NAME, "c:\\foo\\bar");
		assertEquals(new File("c:\\foo\\bar"), AppUtil.getDataDir());
		
		
	}
}
