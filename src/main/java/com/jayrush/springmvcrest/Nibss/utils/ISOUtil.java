// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.Nibss.utils;

public class ISOUtil
{
    public static final String[] hexStrings;
    
    public static String hexor(final String op1, final String op2) {
        final byte[] xor = xor(hex2byte(op1), hex2byte(op2));
        return hexString(xor);
    }
    
    public static byte[] hex2byte(final String s) {
        if (s.length() % 2 == 0) {
            return hex2byte(s.getBytes(), 0, s.length() >> 1);
        }
        return hex2byte("0" + s);
    }
    
    public static byte[] hex2byte(final byte[] b, final int offset, final int len) {
        final byte[] d = new byte[len];
        for (int i = 0; i < len * 2; ++i) {
            final int shift = (i % 2 == 1) ? 0 : 4;
            final byte[] array = d;
            final int n = i >> 1;
            array[n] |= (byte)(Character.digit((char)b[offset + i], 16) << shift);
        }
        return d;
    }
    
    public static String hexString(final byte[] b) {
        final StringBuilder d = new StringBuilder(b.length * 2);
        for (final byte aB : b) {
            d.append(ISOUtil.hexStrings[aB & 0xFF]);
        }
        return d.toString();
    }
    
    public static byte[] xor(final byte[] op1, final byte[] op2) {
        byte[] result;
        if (op2.length > op1.length) {
            result = new byte[op1.length];
        }
        else {
            result = new byte[op2.length];
        }
        for (int i = 0; i < result.length; ++i) {
            result[i] = (byte)(op1[i] ^ op2[i]);
        }
        return result;
    }
    
    static {
        hexStrings = new String[256];
        for (int i = 0; i < 256; ++i) {
            final StringBuilder d = new StringBuilder(2);
            char ch = Character.forDigit((byte)i >> 4 & 0xF, 16);
            d.append(Character.toUpperCase(ch));
            ch = Character.forDigit((byte)i & 0xF, 16);
            d.append(Character.toUpperCase(ch));
            ISOUtil.hexStrings[i] = d.toString();
        }
    }
}
