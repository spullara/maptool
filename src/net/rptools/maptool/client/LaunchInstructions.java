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

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class LaunchInstructions {

	private static final String USAGE = "<html><body width=\"400\">You are running MapTool with insufficient memory allocated (%dMB).<br><br>" + 
			"You may experience odd behavior, especially when connecting to or hosting a server.<br><br>  " +
			"MapTool will launch anyway, but it is recommended that you use one of the 'Launch' scripts instead.</body></html>";
	
	public static void main(String[] args) {
		long mem = Runtime.getRuntime().maxMemory();
		String msg = new String(String.format(USAGE, mem/(1024*1024)));
		
		/* 
		 * Asking for 256MB via the -Xmx256M switch doesn't guarantee that the amount 
		 * maxMemory() reports will be 256MB.  The actual amount seems to vary from PC to PC.  
		 * 200MB seems to be a safe value for now.  <Phergus>
		 */
		if (mem < 200*1024*1024) {
			JOptionPane.showMessageDialog(new JFrame(), msg, "Usage", JOptionPane.INFORMATION_MESSAGE);
		}
		MapTool.main(args);
	}
}
