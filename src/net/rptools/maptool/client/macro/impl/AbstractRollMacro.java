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
package net.rptools.maptool.client.macro.impl;

import java.net.URL;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.macro.Macro;

public abstract class AbstractRollMacro  implements Macro {

    protected String roll(String roll) {
        
        try {
        	String text = roll + " => "+ rollInternal(roll);
            
            return text;
        } catch (Exception e) {
            MapTool.addLocalMessage("<b>Unknown roll '" + roll + "', use #d#+#</b>");
            return null;
        }
    }

    private static final Pattern INLINE_ROLL = Pattern.compile("\\[\\s*(\\d[^\\]]*)\\]");
    public static String inlineRoll(String line) {
        Matcher m = INLINE_ROLL.matcher(line);
        StringBuffer buf = new StringBuffer();
   		while( m.find()) {
       		m.appendReplacement(buf, "[roll "+m.group(1) + " => " + rollInternal(m.group(1))+"]" );
       	}
   		m.appendTail(buf);

   		return buf.toString();
    }

    private static final Random RANDOM = new Random();
    private static final Pattern BASIC_ROLL = Pattern.compile("(\\d*)\\s*d\\s*(\\d*)(\\s*[\\+,\\-]\\s*\\d+)?");
    protected static String rollInternal(String roll) {
        
        Matcher m = BASIC_ROLL.matcher(roll);
        if (!m.matches()) {
            throw new IllegalArgumentException("Invalid roll '" + roll + "'");
        }

        int count = m.group(1) != null && m.group(1).length() > 0 ? Integer.parseInt(m.group(1)) : 1;
        int dice = Integer.parseInt(m.group(2));
        int modifier = 0;
        if (m.group(3) != null) {
            String modStr = m.group(3).replace('+', ' ');
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < modStr.length(); i++) {
                char ch = modStr.charAt(i);
                switch(ch) {
                case '0': case '1': case '2': case '3': case '4': case '5': 
                case '6': case '7': case '8': case '9': case '-':
                    builder.append(ch);
                }
            }
            modifier = Integer.parseInt(builder.toString());
        }
        
        return roll(count, dice, modifier);
    }

    protected static String roll(int count, int dice, int modifier) {

    	StringBuilder builder = new StringBuilder();

    	if (count > 1 || modifier != 0) {
    		
        	if (modifier != 0) {
        		builder.append("(");
        	}
        	
            int result = 0;
            for (int i = 0; i < count; i++) {
                int roll = rollDice(dice);
                
                if (builder.length() > (modifier != 0 ? 1 : 0)) {
                	builder.append(" + ");
                }

                builder.append(roll);

                result += roll;
            }
            
            if (modifier != 0) {
            	builder.append(") + ");
            	builder.append(modifier);
            	result += modifier;
            }
            
            builder.append(" => ").append(result);
    	} else {
    		builder.append(rollDice(dice) + modifier);
    	}
        
        return builder.toString();
    }
    
    protected static int rollDice(int dice) {
    	return (int)(dice * RANDOM.nextFloat()) + 1;
    }

//    public static void main(String[] args) {
//        
//        for (int i = 0; i < 10; i++) {
//            roll("d2 + 2");
//        }
//        
//        for (int i = 0; i < 10; i++) {
//            roll("2d4");
//        }
//        
//        for (int i = 0; i < 10; i++) {
//            roll("2d4+4");
//        }
//    }
}
