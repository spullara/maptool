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
package net.rptools.maptool.model;

public class SightType {

	private String name;
	private double multiplier;
	private LightSource personalLightSource;
	private ShapeType shape; 
	private int arc = 0;
	private float distance = 0;
	private int offset = 0;
	
	public int getOffset()
	{
		return this.offset;
	}

	public void setOffset(int offset2)
	{
		this.offset = offset2;
	}

	public float getDistance()
	{
		return this.distance;
	}

	public void setDistance(float range)
	{
		this.distance = range;
	}

	public ShapeType getShape() {
		return shape;
	}

	public void setShape(ShapeType shape) {
		this.shape = shape;
	}

	public SightType() {
		// For serialization
	}
	
	public SightType(String name, double multiplier, LightSource personalLightSource) 
	{
		this(name, multiplier, personalLightSource, ShapeType.CIRCLE);
	}
	
	public SightType(String name, double multiplier, LightSource personalLightSource, ShapeType shape) {
		this.name = name;
		this.multiplier = multiplier;
		this.personalLightSource = personalLightSource;
		this.shape = shape;
	}
	public SightType(String name, double multiplier, LightSource personalLightSource, ShapeType shape, int arc) {
		this.name = name;
		this.multiplier = multiplier;
		this.personalLightSource = personalLightSource;
		this.shape = shape;
		this.arc = arc;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getMultiplier() {
		return multiplier;
	}
	public void setMultiplier(double multiplier) {
		this.multiplier = multiplier;
	}

	public boolean hasPersonalLightSource() {
		return personalLightSource != null;
	}
	
	public LightSource getPersonalLightSource() {
		return personalLightSource;
	}
	
	public void setPersonalLightSource(LightSource personalLightSource) {
		this.personalLightSource = personalLightSource;
	}

	public void setArc(int arc)
	{
		this.arc = arc;
	}

	public int getArc()
	{
		return arc;
	}
	
}
