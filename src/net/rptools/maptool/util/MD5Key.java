/*
 * $Id$
 *
 * Copyright (C) 2005, Digital Motorworks LP, a wholly owned subsidiary of ADP.
 * The contents of this file are protected under the copyright laws of the
 * United States of America with all rights reserved. This document is
 * confidential and contains proprietary information. Any unauthorized use or
 * disclosure is expressly prohibited.
 */
package net.rptools.maptool.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the MD5 key for a certain set of data.
 * Can be used in maps as keys.
 */
public class MD5Key {

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
