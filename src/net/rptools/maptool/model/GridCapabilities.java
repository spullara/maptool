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

public interface GridCapabilities {
	/**
	 * Whether the parent grid type supports snap-to-grid. Some may not, such as the Gridless grid type.
	 * 
	 * @return
	 */
	public boolean isSnapToGridSupported();

	/**
	 * Whether the parent grid type supports automatic pathing from point A to point B. Usually true except for the
	 * Gridless grid type.
	 * 
	 * @return
	 */
	public boolean isPathingSupported();

	/**
	 * Whether ...
	 * 
	 * @return
	 */
	public boolean isPathLineSupported();

	/**
	 * Whether the parent grid supports the concept of coordinates to be placed on the grid. Generally this requires a
	 * grid type that has some notion of "cell size", which means Gridless need not apply. ;-)
	 * 
	 * @return
	 */
	public boolean isCoordinatesSupported();

	/**
	 * The secondary dimension should be linked to changes in the primary dimension but the primary dimension is
	 * independent of the secondary.
	 */
	public boolean isSecondDimensionAdjustmentSupported();
}
