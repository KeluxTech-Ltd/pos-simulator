// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.iso8583.util;

public final class HexCodec
{
    static final char[] HEX;
    
    private HexCodec() {
    }
    
    public static String hexEncode(final byte[] buffer, final int start, final int length) {
        if (buffer.length == 0) {
            return "";
        }
        int holder = 0;
        final char[] chars = new char[length * 2];
        int pos = -1;
        for (int i = start; i < start + length; ++i) {
            holder = (buffer[i] & 0xF0) >> 4;
            chars[++pos * 2] = HexCodec.HEX[holder];
            holder = (buffer[i] & 0xF);
            chars[pos * 2 + 1] = HexCodec.HEX[holder];
        }
        return new String(chars);
    }
    
    public static byte[] hexDecode(final String hex) {
        if (hex == null || hex.length() == 0) {
            return new byte[0];
        }
        if (hex.length() < 3) {
            return new byte[] { (byte)(Integer.parseInt(hex, 16) & 0xFF) };
        }
        int count = hex.length();
        int nibble = 0;
        if (count % 2 != 0) {
            ++count;
            nibble = 1;
        }
        final byte[] buf = new byte[count / 2];
        char c = '\0';
        int holder = 0;
        int pos = 0;
        for (int i = 0; i < buf.length; ++i) {
            for (int z = 0; z < 2 && pos < hex.length(); ++z) {
                c = hex.charAt(pos++);
                if (c >= 'A' && c <= 'F') {
                    c -= '7';
                }
                else if (c >= '0' && c <= '9') {
                    c -= '0';
                }
                else if (c >= 'a' && c <= 'f') {
                    c -= 'W';
                }
                if (nibble == 0) {
                    holder = c << 4;
                }
                else {
                    holder |= c;
                    buf[i] = (byte)holder;
                }
                nibble = 1 - nibble;
            }
        }
        return buf;
    }
    
    static {
        HEX = "0123456789ABCDEF".toCharArray();
    }
}
