// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.iso8583.codecs;

import com.jayrush.springmvcrest.iso8583.CustomBinaryField;
import com.jayrush.springmvcrest.iso8583.util.Bcd;

public class LongBcdCodec implements CustomBinaryField<Long>
{
    private final boolean rightPadded;
    
    public LongBcdCodec() {
        this(false);
    }
    
    public LongBcdCodec(final boolean rightPadding) {
        this.rightPadded = rightPadding;
    }
    
    @Override
    public Long decodeBinaryField(final byte[] value, final int pos, final int length) {
        return this.rightPadded ? Bcd.decodeRightPaddedToLong(value, pos, length * 2) : Bcd.decodeToLong(value, pos, length * 2);
    }
    
    @Override
    public byte[] encodeBinaryField(final Long value) {
        final String s = Long.toString(value, 10);
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
    public Long decodeField(final String value) {
        return Long.parseLong(value, 10);
    }
    
    @Override
    public String encodeField(final Long value) {
        return value.toString();
    }
}
