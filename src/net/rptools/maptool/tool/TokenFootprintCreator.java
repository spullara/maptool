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

import java.awt.Point;
import java.util.Arrays;
import java.util.List;

import net.rptools.lib.GUID;
import net.rptools.maptool.model.TokenFootprint;

import com.thoughtworks.xstream.XStream;

public class TokenFootprintCreator {
	
	public static void main(String[] args) {
		
		List<TokenFootprint> footprintList = Arrays.asList(new TokenFootprint[]{

				new TokenFootprint("-11", false, 0.086),
				new TokenFootprint("-10", false, 0.107),
				new TokenFootprint("-9", false, 0.134),
				new TokenFootprint("-8", false, 0.168),
				new TokenFootprint("-7", false, 0.210),
				new TokenFootprint("-6", false, 0.262),
				new TokenFootprint("-5", false, 0.328),
				new TokenFootprint("-4", false, 0.410),
				new TokenFootprint("-3", false, 0.512),
				new TokenFootprint("-2", false, 0.640),
				new TokenFootprint("-1", false, 0.800),
				new TokenFootprint("0", true, 1.000),
				new TokenFootprint("1", false, 1.200),
				new TokenFootprint("2", false, 1.440),
				new TokenFootprint("3", false, 1.728),
				new TokenFootprint("4", false, 2.074),
				new TokenFootprint("5", false, 2.488),
				new TokenFootprint("6", false, 2.986),
				new TokenFootprint("7", false, 3.583),
				new TokenFootprint("8", false, 4.300),
				new TokenFootprint("9", false, 5.160),
				new TokenFootprint("10", false, 6.192),
				new TokenFootprint("11", false, 7.430),
				new TokenFootprint("12", false, 8.916),
				new TokenFootprint("13", false, 10.699),
				new TokenFootprint("14", false, 12.839),
				new TokenFootprint("15", false, 15.407),
				new TokenFootprint("16", false, 18.488),
				new TokenFootprint("17", false, 22.186),
				new TokenFootprint("18", false, 26.623),
				new TokenFootprint("19", false, 31.948),
				new TokenFootprint("20", false, 38.338)
				
			// SQUARE
//			new TokenFootprint("Medium", true, 1.0),
//			new TokenFootprint("Large", squarePoints(2)),
//			new TokenFootprint("Huge", squarePoints(3)),
//			new TokenFootprint("Gargantuan", squarePoints(4)),
//			new TokenFootprint("Colossal", squarePoints(6)),

			// HEXES
//			new TokenFootprint("Medium", true, 1.0),
//			new TokenFootprint("Large", points(new int[][] {
//				{0, 1},
//				{1, 0},
//			})),
//			
//			new TokenFootprint("Large", points(new int[][] {
//				{0, 1},
//				{1, 0},
//				{-1, 0},
//				{-1, -1},
//				{0, -1},
//				{1, -1}
//			})),
					
//			new TokenFootprint("Large", points(new int[][] {
//					{},
//					{},
//					})),
//					
//			new TokenFootprint("Large", points(new int[][] {
//					{},
//					{},
//					})),
					
		});
		
		XStream xstream = new XStream();
		System.out.println(xstream.toXML(footprintList));
	}

	private static Point[] points(int[][] points) {
		
		Point[] pa = new Point[points.length];
		
		for (int i = 0; i < points.length; i++) {
			pa[i] = new Point(points[i][0], points[i][1]);
		}
		
		return pa;
	}
	
	private static Point[] squarePoints(int size) {
		
		Point[] pa = new Point[size*size-1];
		
		int indx = 0;
		for (int y = 0; y < size; y++) {
			
			for (int x = 0; x < size; x++) {
				if ( y == 0 && x == 0) {
					continue;
				}
				
				pa[indx] = new Point(x, y);
				indx ++;
			}
		}
		
		return pa;
	}

}
