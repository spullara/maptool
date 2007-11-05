package net.rptools.maptool.model;

import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import net.rptools.lib.FileUtil;
import net.rptools.maptool.model.TokenFootprint.OffsetTranslator;

import com.thoughtworks.xstream.XStream;

public class LightSource {

	private List<Light> lightList = new LinkedList<Light>();
	private String name;
	private GUID id;
	
	public LightSource() {
		// for serialization
	}
	
	public LightSource(String name) {
		id = new GUID();
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LightSource)) {
			return false;
		}
		
		return ((LightSource)obj).id.equals(id);
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	public GUID getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void add(Light source) {
		lightList.add(source);
	}
	
	public void remove(Light source) {
		lightList.remove(source);
	}
	
	public Area getArea(Token token, Grid grid) {

		Area area = new Area();
		for (Light source : lightList) {
			area.add(source.getArea(token, grid));
		}
		
		return area;
	}

	public void render(Graphics2D g, Token token, Grid grid) {
		
	}

	public static List<LightSource> getDefaultLightSources() throws IOException {
		
		return (List<LightSource>) new XStream().fromXML(new String(FileUtil.loadResource("net/rptools/maptool/model/squareGridFootprints.xml")));
	}
	
	public String toString() {
		return name;
	}
	
	////
	// XSTREAM
	private Object readResolve() {
		lightList = new LinkedList<Light>();
		return this;
	}
	
}
