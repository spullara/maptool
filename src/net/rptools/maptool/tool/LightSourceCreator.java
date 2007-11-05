package net.rptools.maptool.tool;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.rptools.maptool.model.Light;
import net.rptools.maptool.model.LightSource;
import net.rptools.maptool.model.drawing.DrawableColorPaint;

import com.thoughtworks.xstream.XStream;

public class LightSourceCreator {

	public static void main(String[] args) {
		
		
		List<LightSource> lightSourceList = new ArrayList<LightSource>();

		lightSourceList.add(createLightSource("Candle", 5, 360));
		lightSourceList.add(createLightSource("Lamp", 15, 360));
		lightSourceList.add(createLightSource("Lantern, Hooded", 30, 360));
		lightSourceList.add(createLightSource("Torch", 20, 360));
		lightSourceList.add(createLightSource("Everburning", 20, 360));
		lightSourceList.add(createLightSource("Sunrod", 30, 360));

		XStream xstream = new XStream();
		System.out.println(xstream.toXML(lightSourceList));
		
	}
	
	private static LightSource createLightSource(String name, double radius, double arcAngle) {

		LightSource source = new LightSource(name);

		source.add(new Light(0, 5, arcAngle, new DrawableColorPaint(new Color(255, 255, 0, 50))));
		source.add(new Light(0, radius, arcAngle, null));
		source.add(new Light(0, radius*2, arcAngle, new DrawableColorPaint(new Color(0, 0, 0, 100))));
		
		return source;
	}
}
