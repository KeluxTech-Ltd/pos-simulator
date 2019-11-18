// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.Nibss.utils;

public class StringUtils
{
    public static void printHex(final byte[] bytes) {
        for (int j = 0; j < bytes.length; ++j) {
            System.out.format("%02X ", bytes[j]);
        }
        System.out.println();
    }
    
    public static byte[] hexStringToByteArray(final String s) {
        final int len = s.length();
        final byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte)((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
    
    public static String bytesToHex(final byte[] bytes) {
        if (bytes != null) {
            final char[] hexArray = "0123456789ABCDEF".toCharArray();
            final char[] hexChars = new char[bytes.length * 2];
            for (int j = 0; j < bytes.length; ++j) {
                final int v = bytes[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0xF];
            }
            return new String(hexChars);
        }
        return "";
    }
    
    public static final String padLeft(final String str, final int size, final char padChar) {
        final StringBuffer padded = new StringBuffer(str);
        while (padded.length() < size) {
            padded.insert(0, padChar);
        }
        return padded.toString();
    }
    
    public static final String padRight(final String str, final int size, final char padChar) {
        final StringBuffer padded = new StringBuffer(str);
        while (padded.length() < size) {
            padded.append(padChar);
        }
        return padded.toString();
    }
}
