package net.rptools.maptool.tool;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;

import net.rptools.maptool.model.TokenFootprint;

import com.thoughtworks.xstream.XStream;

public class TokenFootprintCreator {
	
	public static void main(String[] args) {
		
		List<TokenFootprint> footprintList = Arrays.asList(new TokenFootprint[]{

			new TokenFootprint("Fine"),
			new TokenFootprint("Diminutive"),
			new TokenFootprint("Tiny"),
			new TokenFootprint("Small"),
			new TokenFootprint("Medium", true),
			new TokenFootprint("Large", points(2)),
			new TokenFootprint("Huge", points(3)),
			new TokenFootprint("Gargantuan", points(4)),
			new TokenFootprint("Colossal", points(6)),
		});
		
		XStream xstream = new XStream();
		System.out.println(xstream.toXML(footprintList));
	}
	
	private static Point[] points(int size) {
		
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
