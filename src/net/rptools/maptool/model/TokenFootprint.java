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

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class represents the set of cells a token occupies based on its size.
 * Each token is assumed to take up at least one cell, additional cells are indicated
 * by cell offsets assuming the occupied cell is at 0, 0 
 */
public class TokenFootprint {

	private Set<Point> cellSet = new HashSet<Point>();
	
	private String name;
	private GUID id;
	private boolean isDefault;
	private double scale = 1;
	
	private transient List<OffsetTranslator> translatorList = new LinkedList<OffsetTranslator>();
	
	public TokenFootprint() {
		// for serialization
	}
	
	public TokenFootprint(String name, boolean isDefault, double scale, Point... points) {
		this.name = name;
		id = new GUID();
		this.isDefault = isDefault;
		this.scale = scale;
		
		for (Point p : points) {
			cellSet.add(p);
		}
		
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public void addOffsetTranslator(OffsetTranslator translator) {
		translatorList.add(translator);
	}
	
	public Set<CellPoint> getOccupiedCells(CellPoint centerPoint) {
		Set<CellPoint> occupiedSet = new HashSet<CellPoint>();

		// Implied
		occupiedSet.add(centerPoint);
		
		// Relative
		for (Point offset : cellSet) {
			CellPoint cp = new CellPoint(centerPoint.x + offset.x, centerPoint.y + offset.y);
			for (OffsetTranslator translator : translatorList) {
				translator.translate(centerPoint, cp);
			}
			occupiedSet.add(cp);
		}
		
		return occupiedSet;
	}
	
	public TokenFootprint(String name, Point... points) {
		this(name, false, 1, points);
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	
	public boolean isDefault() {
		return isDefault;
	}
	
	public GUID getId() {
		return id;
	}
	
	public String getName() { 
		return name;
	}

	public Rectangle getBounds(Grid grid) {
		return getBounds(grid, null);
	}
	
	public double getScale() {
		return scale;
	}
	
	/**
	 * Return a rectangle that exactly bounds the footprint, values are in ZonePoint space
	 */
	public Rectangle getBounds(Grid grid, CellPoint cell) {

		cell = cell != null ? cell : new CellPoint(0, 0);
		Rectangle bounds = new Rectangle(grid.getBounds(cell));
		
		for (CellPoint cp : getOccupiedCells(cell)) {
			
			bounds.add(grid.getBounds(cp));
		}

		bounds.x += grid.getOffsetX();
		bounds.y += grid.getOffsetY();
		
		return bounds;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TokenFootprint)) {
			return false;
		}
		
		return ((TokenFootprint)obj).id.equals(id);
	}
	
	private Object readResolve() {
		translatorList = new LinkedList<OffsetTranslator>();
		return this;
	}

	public static interface OffsetTranslator {
		public void translate(CellPoint originPoint, CellPoint offsetPoint);
	}
	
}
