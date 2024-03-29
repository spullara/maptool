/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package net.rptools.maptool.tool;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rptools.maptool.model.Light;
import net.rptools.maptool.model.LightSource;
import net.rptools.maptool.model.ShapeType;
import net.rptools.maptool.model.drawing.DrawableColorPaint;

import com.thoughtworks.xstream.XStream;

public class LightSourceCreator {
	public static void main(String[] args) {
		Map<String, List<LightSource>> lightSourcesMap = new HashMap<String, List<LightSource>>();
		
		List<LightSource> lightSourceList = new ArrayList<LightSource>();

		lightSourceList.add(createD20LightSource("Candle - 5", 5, 360));
		lightSourceList.add(createD20LightSource("Lamp - 15", 15, 360));
		lightSourceList.add(createD20LightSource("Torch - 20", 20, 360));
		lightSourceList.add(createD20LightSource("Everburning - 20", 20, 360));
		lightSourceList.add(createD20LightSource("Lantern, Hooded - 30", 30, 360));
		lightSourceList.add(createD20LightSource("Sunrod - 30", 30, 360));

		lightSourcesMap.put("D20", lightSourceList);
		
		lightSourceList = new ArrayList<LightSource>();
		
		lightSourceList.add(createLightSource("5", 5, 360));
		lightSourceList.add(createLightSource("15", 15, 360));
		lightSourceList.add(createLightSource("20", 20, 360));
		lightSourceList.add(createLightSource("30", 30, 360));
		lightSourceList.add(createLightSource("40", 40, 360));
		lightSourceList.add(createLightSource("60", 60, 360));

		lightSourcesMap.put("Generic", lightSourceList);
		
		XStream xstream = new XStream();
		System.out.println(xstream.toXML(lightSourcesMap));
		
	}
	
	private static LightSource createLightSource(String name, double radius, double arcAngle) {

		LightSource source = new LightSource(name);

//		source.add(new Light(0, 5, arcAngle, new DrawableColorPaint(new Color(255, 255, 0, 50))));
		source.add(new Light(ShapeType.CIRCLE, 0, radius, arcAngle, null));
		
		return source;
	}

	private static LightSource createD20LightSource(String name, double radius, double arcAngle) {

		LightSource source = new LightSource(name);

//		source.add(new Light(0, 5, arcAngle, new DrawableColorPaint(new Color(255, 255, 0, 50))));
		source.add(new Light(ShapeType.CIRCLE, 0, radius, arcAngle, null));
		source.add(new Light(ShapeType.CIRCLE, 0, radius*2, arcAngle, new DrawableColorPaint(new Color(0, 0, 0, 100))));
		
		return source;
	}
}
