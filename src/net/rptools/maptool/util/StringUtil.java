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
package net.rptools.maptool.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Tylere
 */
public class StringUtil {
	
	public static String formatDecimal(double value) {
		return String.format(value == (int)value ? "%.0f" : "%.2f", value);
	}

	public static String wrapText(String string, int wrapLength, int startPosition, String wrapChar) {

		StringBuilder wrappedString = new StringBuilder();;
		String subString;
		int newlinePos;
		int length = string.length();
		
		if ( length - startPosition <= wrapLength ) {
			return string;
		}
		
		while(length - startPosition > wrapLength) {

			// look ahead one char (wrapLength + 1) in case it is a space or newline
			subString = string.substring(startPosition, startPosition + wrapLength + 1);
			// restart if newline character is found
			newlinePos = subString.lastIndexOf(wrapChar);
			if (newlinePos == -1) {
				// if there's no line break, then find the first space to break the line
				newlinePos = subString.lastIndexOf(" ");
				if(newlinePos == -1) {
					// if there are no spaces, then force the line break within the word.
					newlinePos = wrapLength-1; // -1 because of 0 start point of position
				}
			}

			wrappedString.append(subString.substring(0, newlinePos));
			wrappedString.append(wrapChar);
			startPosition += newlinePos+1;

		}
		
		// add the remainder of the string
		wrappedString.append( string.substring(startPosition) );	

		return wrappedString.toString();
	}
	
	/**
	 * Gets copy of <b>string</b> wrapped with '\n' character
	 * a the wraplength or the nearest space between words.
	 * @param string The multiline string to be wrapped
	 * @param wrapLength the number of characters before wrapping
	 */
	public static String wrapText(String string, int wrapLength) {
		return wrapText(string, wrapLength, 0, "\n");
	}
	
	/**
	 * Whether the string is null or all whitespace chars
	 */
	public static boolean isEmpty(String string) {
		return string == null || string.trim().length() == 0;
	}
	
	public static int countOccurances(String source, String str) {
		
		int count = 0;
		int index = 0;
		while ((index = source.indexOf(str, index)) >= 0) {
			count ++;
			index += str.length();
		}
		return count;
	}
	
	public static List<String> getWords(String line) {
		
		List<String> list = new ArrayList<String>();

		while (line != null && line.trim().length() > 0) {

			line = line.trim();
			System.out.println("'" + line + "'");
			List<String> split = splitNextWord(line);

			String nextWord = split.get(0);
			line = split.get(1);

			if (nextWord == null) {
				continue;
			}
			
			list.add(nextWord);
		}
		
		return list;
	}
	
	public static  String getFirstWord(String line) {
		List<String> split = splitNextWord(line);
		return split != null ? split.get(0) : null;
	}
	
	public static  List<String> splitNextWord(String line) {
		
		line = line.trim();
		if (line.length() == 0) {
			return null;
		}
		
		StringBuilder builder = new StringBuilder();
		
		boolean quoted = line.charAt(0) == '"';

		int start = quoted ? 1 : 0;
		int end = start;
		for (; end < line.length(); end++) {
			
			char c = line.charAt(end);
			if (quoted) {
				if (c == '"') {
					break;
				}
			} else {
				if (Character.isWhitespace(c)) {
					break;
				}
			}
			
			builder.append(c);
		}
		
		return Arrays.asList(new String[]{line.substring(start, end), line.substring(Math.min(end+1, line.length()))});
	}	
}
