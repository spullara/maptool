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

			// SQUARE
			new TokenFootprint("Medium", true, 1.0),
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
