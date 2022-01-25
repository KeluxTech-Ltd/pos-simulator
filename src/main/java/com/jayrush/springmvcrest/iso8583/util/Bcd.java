// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.iso8583.util;

import java.math.BigInteger;

public final class Bcd
{
    private Bcd() {
    }
    
    public static long decodeToLong(final byte[] buf, final int pos, final int length) throws IndexOutOfBoundsException {
        if (length > 18) {
            throw new IndexOutOfBoundsException("Buffer too big to decode as long");
        }
        long l = 0L;
        long power = 1L;
        for (int i = pos + length / 2 + length % 2 - 1; i >= pos; --i) {
            l += (buf[i] & 0xF) * power;
            power *= 10L;
            l += ((buf[i] & 0xF0) >> 4) * power;
            power *= 10L;
        }
        return l;
    }
    
    public static long decodeRightPaddedToLong(final byte[] buf, final int pos, final int length) throws IndexOutOfBoundsException {
        if (length > 18) {
            throw new IndexOutOfBoundsException("Buffer too big to decode as long");
        }
        long l = 0L;
        long power = 1L;
        int end = pos + length / 2 + length % 2 - 1;
        if ((buf[end] & 0xF) == 0xF) {
            l += (buf[end] & 0xF0) >> 4;
            power *= 10L;
            --end;
        }
        for (int i = end; i >= pos; --i) {
            l += (buf[i] & 0xF) * power;
            power *= 10L;
            l += ((buf[i] & 0xF0) >> 4) * power;
            power *= 10L;
        }
        return l;
    }
    
    public static void encode(final String value, final byte[] buf) {
        int charpos = 0;
        int bufpos = 0;
        if (value.length() % 2 == 1) {
            buf[0] = (byte)(value.charAt(0) - '0');
            charpos = 1;
            bufpos = 1;
        }
        while (charpos < value.length()) {
            buf[bufpos] = (byte)(value.charAt(charpos) - '0' << 4 | value.charAt(charpos + 1) - '0');
            charpos += 2;
            ++bufpos;
        }
    }
    
    public static void encodeRightPadded(final String value, final byte[] buf) {
        int bufpos = 0;
        int charpos = 0;
        int limit = value.length();
        if (limit % 2 == 1) {
            --limit;
        }
        while (charpos < limit) {
            buf[bufpos] = (byte)(value.charAt(charpos) - '0' << 4 | value.charAt(charpos + 1) - '0');
            charpos += 2;
            ++bufpos;
        }
        if (value.length() % 2 == 1) {
            buf[bufpos] = (byte)(value.charAt(limit) - '0' << 4 | 0xF);
        }
    }
    
    public static BigInteger decodeToBigInteger(final byte[] buf, final int pos, final int length) throws IndexOutOfBoundsException {
        final char[] digits = new char[length];
        int start = 0;
        int i = pos;
        if (length % 2 != 0) {
            digits[start++] = (char)((buf[i] & 0xF) + 48);
            ++i;
        }
        while (i < pos + length / 2 + length % 2) {
            digits[start++] = (char)(((buf[i] & 0xF0) >> 4) + 48);
            digits[start++] = (char)((buf[i] & 0xF) + 48);
            ++i;
        }
        return new BigInteger(new String(digits));
    }
    
    public static BigInteger decodeRightPaddedToBigInteger(final byte[] buf, final int pos, final int length) throws IndexOutOfBoundsException {
        final char[] digits = new char[length];
        int start = 0;
        for (int i = pos, limit = pos + length / 2 + length % 2; i < limit; ++i) {
            digits[start++] = (char)(((buf[i] & 0xF0) >> 4) + 48);
            final int r = buf[i] & 0xF;
            digits[start++] = ((r == 15) ? ' ' : ((char)(r + 48)));
        }
        return new BigInteger(new String(digits, 0, start).trim());
    }
    
    public static int parseBcdLength(final byte b) {
        return ((b & 0xF0) >> 4) * 10 + (b & 0xF);
    }
    
    public static int parseBcdLength2bytes(final byte[] b, final int offset) {
        return ((b[offset] & 0xF0) >> 4) * 1000 + (b[offset] & 0xF) * 100 + ((b[offset + 1] & 0xF0) >> 4) * 10 + (b[offset + 1] & 0xF);
    }
}
