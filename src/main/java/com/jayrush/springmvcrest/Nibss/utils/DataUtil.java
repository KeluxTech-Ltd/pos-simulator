// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.Nibss.utils;

import java.nio.ByteBuffer;
import java.util.*;

public class DataUtil
{
    public static String bytesToHex(final byte[] bytes) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();
        final char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; ++j) {
            final int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0xF];
        }
        return new String(hexChars);
    }
    
    public static String leftZeroPad(final int value) {
        return leftZeroPad(value, 7);
    }
    
    public static String leftZeroPad(final int value, final int length) {
        final String valueString = String.valueOf(value);
        final int formatLen = length - valueString.length();
        if (formatLen > 0) {
            return String.format("%0" + formatLen + "d", value);
        }
        return valueString;
    }
    
    public static String timeLocalTransaction(final Date transDate) {
        final Calendar myCal = Calendar.getInstance();
        final TimeZone timeZone = TimeZone.getTimeZone("Africa/Luanda");
        myCal.setTimeZone(timeZone);
        return String.format("%02d%02d%02d", myCal.get(11), myCal.get(12), myCal.get(13));
    }
    
    public static String transmissionDateAndTime(final Date transDate) {
        final Calendar myCal = Calendar.getInstance();
        final TimeZone timeZone = TimeZone.getTimeZone("Africa/Luanda");
        myCal.setTimeZone(timeZone);
        return String.format("%02d%02d%02d%02d%02d", myCal.get(2) + 1, myCal.get(5), myCal.get(11), myCal.get(12), myCal.get(13));
    }
    
    public static String dateLocalTransaction(final Date transDate) {
        final Calendar myCal = Calendar.getInstance();
        final TimeZone timeZone = TimeZone.getTimeZone("Africa/Luanda");
        myCal.setTimeZone(timeZone);
        return String.format("%02d%02d", myCal.get(2) + 1, myCal.get(5));
    }
    
    public static Map<String, String> parseTLV(final String tlv) {
        if (tlv == null || tlv.length() % 2 != 0) {
            throw new RuntimeException("Invalid tlv, null or odd length");
        }
        final HashMap<String, String> hashMap = new HashMap<String, String>();
        int i = 0;
        while (i < tlv.length()) {
            try {
                String key = tlv.substring(i, i += 2);
                if ((Integer.parseInt(key, 16) & 0x1F) == 0x1F) {
                    key += tlv.substring(i, i += 2);
                }
                String len = tlv.substring(i, i += 2);
                int length = Integer.parseInt(len, 16);
                if (length > 127) {
                    final int bytesLength = length - 128;
                    len = tlv.substring(i, i += bytesLength * 2);
                    length = Integer.parseInt(len, 16);
                }
                length *= 2;
                final String value = tlv.substring(i, i += length);
                hashMap.put(key, value);
            }
            catch (Exception ex) {}
        }
        return hashMap;
    }
    
    public static short bytesToShort(final byte[] bytes) {
        return ByteBuffer.wrap(bytes).getShort();
    }
    
    public static byte[] shortToBytes(final short value) {
        return new byte[] { (byte)(value >> 8), (byte)value };
    }
    
    public static String formatAmount(final String amount) {
        final int amountInKobo = (int)(Double.parseDouble(amount) * 100.0);
        return String.format("%012d", amountInKobo);
    }
}
