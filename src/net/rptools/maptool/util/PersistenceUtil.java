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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.rptools.maptool.model.Campaign;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

/**
 * @author trevor
 */
public class PersistenceUtil {

	public static void saveCampaign(Campaign campaign, File campaignFile) throws IOException {
		
		// This is a veeeeeery primitive form of perstence, and will be changing very soon
		OutputStream os = new FileOutputStream(campaignFile);
		HessianOutput out = new HessianOutput(os);

		out.writeObject(campaign);
		os.close();		
	}
	
	public static Campaign loadCampaign(File campaignFile) throws IOException {
		
		Campaign campaign = null;
		
		InputStream is = new FileInputStream(campaignFile);
		HessianInput in = new HessianInput(is);

		campaign = (Campaign) in.readObject(null);
		is.close();
		
		return campaign;
	}
}
