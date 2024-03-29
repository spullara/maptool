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

import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class SightType {
	private String name;
	private double multiplier;
	private LightSource personalLightSource;
	private ShapeType shape;
	private int arc = 0;
	private float distance = 0;
	private int offset = 0;

	public int getOffset() {
		return this.offset;
	}

	public void setOffset(int offset2) {
		this.offset = offset2;
	}

	public float getDistance() {
		return this.distance;
	}

	public void setDistance(float range) {
		this.distance = range;
	}

	public ShapeType getShape() {
		return shape != null ? shape : ShapeType.CIRCLE;
	}

	public void setShape(ShapeType shape) {
		this.shape = shape;
	}

	public SightType() {
		// For serialization
	}

	public SightType(String name, double multiplier, LightSource personalLightSource) {
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

	public void setArc(int arc) {
		this.arc = arc;
	}

	public int getArc() {
		return arc;
	}

	public Area getVisionShape(Token token, Zone zone) {
		float visionRange = getDistance();
		int visionDistance = zone.getTokenVisionInPixels();
		Area visibleArea = new Area();

		// FIXME This next formula is identical to the one in zone.getTokenVisionInPixels() called two lines above!!
		visionRange = (visionRange == 0) ? visionDistance : visionRange * zone.getGrid().getSize() / zone.getUnitsPerCell();

		//now calculate the shape and return the shaped Area to the caller
		switch (getShape()) {
		case CIRCLE:
			visibleArea = new Area(new Ellipse2D.Double(-visionRange, -visionRange, visionRange * 2, visionRange * 2));
			break;
		case SQUARE:
			visibleArea = new Area(new Rectangle2D.Double(-visionRange, -visionRange, visionRange * 2, visionRange * 2));
			break;
		case CONE:
			if (token.getFacing() == null) {
				token.setFacing(0);
			}
			int offsetAngle = getOffset();
			int arcAngle = getArc();
			//TODO: confirm if we want the offset to be positive-counter-clockwise, negative-clockwise or vice versa
			//simply a matter of changing the sign on offsetAngle
			Area tempvisibleArea = new Area(new Arc2D.Double(-visionRange, -visionRange, visionRange * 2, visionRange * 2, 360.0 - (arcAngle / 2.0) + (offsetAngle * 1.0), arcAngle, Arc2D.PIE));
			// Rotate
			tempvisibleArea = tempvisibleArea.createTransformedArea(AffineTransform.getRotateInstance(-Math.toRadians(token.getFacing())));

			Area footprint = new Area(token.getFootprint(zone.getGrid()).getBounds(zone.getGrid()));
			footprint = footprint.createTransformedArea(AffineTransform.getTranslateInstance(-footprint.getBounds().getWidth() / 2, -footprint.getBounds().getHeight() / 2));
			visibleArea.add(footprint);
			visibleArea.add(tempvisibleArea);
			break;
		default:
			visibleArea = new Area(new Ellipse2D.Double(-visionRange, -visionRange, visionRange * 2, visionRange * 2));
			break;
		}
		return visibleArea;
	}
}
