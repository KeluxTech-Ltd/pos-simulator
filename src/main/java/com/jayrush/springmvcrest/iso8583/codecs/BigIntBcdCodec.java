// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.iso8583.codecs;

import com.jayrush.springmvcrest.iso8583.CustomBinaryField;
import com.jayrush.springmvcrest.iso8583.util.Bcd;

import java.math.BigInteger;

public class BigIntBcdCodec implements CustomBinaryField<BigInteger>
{
    private final boolean rightPadded;
    
    public BigIntBcdCodec() {
        this(false);
    }
    
    public BigIntBcdCodec(final boolean rightPadded) {
        this.rightPadded = rightPadded;
    }
    
    @Override
    public BigInteger decodeBinaryField(final byte[] value, final int pos, final int len) {
        return this.rightPadded ? Bcd.decodeRightPaddedToBigInteger(value, pos, len * 2) : Bcd.decodeToBigInteger(value, pos, len * 2);
    }
    
    @Override
    public byte[] encodeBinaryField(final BigInteger value) {
        final String s = value.toString(10);
        final byte[] buf = new byte[s.length() / 2 + s.length() % 2];
        if (this.rightPadded) {
            Bcd.encodeRightPadded(s, buf);
        }
        else {
            Bcd.encode(s, buf);
        }
        return buf;
    }
    
    @Override
    public BigInteger decodeField(final String value) {
        return new BigInteger(value, 10);
    }
    
    @Override
    public String encodeField(final BigInteger value) {
        return value.toString(10);
    }
}
