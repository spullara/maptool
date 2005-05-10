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

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the MD5 key for a certain set of data.
 * Can be used in maps as keys.
 */
public class MD5Key implements Serializable {

    private static MessageDigest md5Digest;
    
    String id;
    
    static {
        try {
            md5Digest = MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException e) {
            // TODO: handle this more gracefully
            e.printStackTrace();
        }
    }
    
    public MD5Key(){}
    
    public MD5Key (byte[] data) {
        id = encodeToHex(digestData(data));
    }
    
    public String toString() {
        return id;
    }
    
    public boolean equals(Object obj) {
        if (!(obj instanceof MD5Key)) {
            return false;
        }
        
        return id.equals(((MD5Key) obj).id);
    }
    
    public int hashCode() {
        return id.hashCode();
    }
    
    private static synchronized byte[] digestData(byte[] data) {
        
        md5Digest.reset();
        
        md5Digest.update(data);
        
        return md5Digest.digest();
    }

    private static String encodeToHex(byte[] data) {
        
        StringBuilder strbuild = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            
            String hex = Integer.toHexString(data[i]);
            if (hex.length() < 2) {
                strbuild.append("0");
            }
            if (hex.length() > 2) {
                hex = hex.substring(hex.length()-2);
            }
            strbuild.append(hex);
        }
        
        return strbuild.toString();
    }
}
