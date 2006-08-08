/* The MIT License
 * 
 * Copyright (c) 2005 David Rice, Trevor Croft
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation files 
 * (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software, 
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN 
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */
package net.rptools.maptool.client;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Zone;

public class MapToolUtil {

    private static Random random = new Random ( System.currentTimeMillis() );

    private static AtomicInteger nextTokenId = new AtomicInteger(1);
    
    public static int getRandomNumber ( int max )
    {
        return getRandomNumber ( 0, max );
    }
    
    public static int getRandomNumber ( int min, int max )
    {
        return (int)(( ( max - min ) * random.nextDouble() ) + min);
    }
    
    public static float getRandomRealNumber ( float max )
    {
        return getRandomRealNumber ( 0, max );
    }
    
    public static float getRandomRealNumber ( float min, float max )
    {
        return (float)( ( max - min ) * random.nextDouble() ) + min;
    }
    
    public static boolean percentageCheckAbove ( int percentage )
    {
        return (random.nextDouble()* 100) > percentage;
    }
    
    public static boolean percentageCheckBelow ( int percentage )
    {
        double roll = random.nextDouble()* 100; 
        return roll < percentage;
    }

    public static final Pattern ENDS_IN_INDEX = Pattern.compile(" \\d$");
    public static String nextTokenId(Zone zone, String baseName) {

      // Create a name
    	if (baseName == null) {
	    	int nextId = nextTokenId.getAndIncrement();
	    	char ch = (char)('a' + MapTool.getPlayerList().indexOf(MapTool.getPlayer()));
	    	return ch + Integer.toString(nextId);
    	}
    	
      // Check for an already numbered name
      baseName = baseName.trim();
      Matcher matcher = ENDS_IN_INDEX.matcher(baseName);
      int index = 0;
      if (matcher.find()) {
        index = Integer.parseInt(baseName.substring(matcher.start() + 1));
        baseName = baseName.substring(0, matcher.start()).trim();
      }
      
      // Find a valid index, if the index > 0 then add it to the name
    	String name = baseName + (index == 0 ? "" : (" " + index));
    	List<Token> tokenList = zone.getAllTokens();
OUTTER: 
      while (true) {  // Repeat until the name is unique       
        for (Token token : tokenList) {
          if (token.getName() != null && token.getName().equals(name)) {
            index ++;
            name = baseName + " " + index; 
            continue OUTTER;
          } // endif
        } // endfor
        break;
      } // endwhile
    	return name;
    }
    
    public static boolean isDebugEnabled() {
    	return System.getProperty("MAPTOOL_DEV") != null;
    }
}
