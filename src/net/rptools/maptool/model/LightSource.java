package net.rptools.maptool.model;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.rptools.lib.FileUtil;

import com.thoughtworks.xstream.XStream;

public class LightSource  {

	private List<Light> lightList;
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
	
	public double getMaxRange() {
		double range = 0;
		
		for (Light light : getLightList()) {
			range = Math.max(range, light.getRadius());
		}
		
		return range;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	public void setId(GUID id) {
		this.id = id;
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
		getLightList().add(source);
	}
	
	public void remove(Light source) {
		getLightList().remove(source);
	}
	
	public List<Light> getLightList() {
		if (lightList == null) {
			lightList = new LinkedList<Light>();
		}
		return lightList;
	}

	/**
	 * Area for a single light, subtracting any previous lights
	 */
	public Area getArea(Token token, Zone zone, Direction position, Light light) {
		Area area = light.getArea(token, zone);
		// TODO: This seems horribly inefficient
		// Subtract out the lights that are previously defined
		for (int i = getLightList().indexOf(light) -1; i >= 0; i--) {
			Light lessLight = getLightList().get(i);
			area.subtract(getArea(token, zone, position, lessLight.getArea(token, zone)));
		}
		return getArea(token, zone, position, area);
	}

	/**
	 * Area for all lights combined
	 */
	public Area getArea(Token token, Zone zone, Direction position) {
		Area area = new Area();
		for (Light light : getLightList()) {
			area.add(light.getArea(token, zone));
		}
		
		return getArea(token, zone, position, area);
	}

	private Area getArea(Token token, Zone zone, Direction position, Area area) {

		Grid grid = zone.getGrid();
		Rectangle footprintBounds = token.getFootprint(grid).getBounds(grid, grid.convert(new ZonePoint(token.getX(), token.getY())));

		int tx = 0;
		int ty = 0;
		switch (position) {
		case NW:
			tx -= footprintBounds.width/2;
			ty -= footprintBounds.height/2;
			break;
		case N:
			ty -= footprintBounds.height/2;
			break;
		case NE:
			tx += footprintBounds.width/2;
			ty -= footprintBounds.height/2;
			break;
		case W:
			tx -= footprintBounds.width/2;
			break;
		case CENTER:
			break;
		case E:
			tx += footprintBounds.width/2;
			break;
		case SW:
			tx -= footprintBounds.width/2;
			ty += footprintBounds.height/2;
			break;
		case S:
			ty += footprintBounds.height/2;
			break;
		case SE:
			tx += footprintBounds.width/2;
			ty += footprintBounds.height/2;
			break;
		}
		
		area.transform(AffineTransform.getTranslateInstance(tx, ty));
		return area;
	}

	public void render(Graphics2D g, Token token, Grid grid) {
		
	}

	public static Map<String, List<LightSource>> getDefaultLightSources() throws IOException {
		
		return (Map<String, List<LightSource>>) new XStream().fromXML(new String(FileUtil.loadResource("net/rptools/maptool/model/defaultLightSourcesMap.xml")));
	}
	
	public String toString() {
		return name;
	}

	
}
