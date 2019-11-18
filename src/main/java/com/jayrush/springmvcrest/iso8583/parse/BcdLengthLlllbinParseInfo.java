// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.iso8583.parse;

import com.jayrush.springmvcrest.iso8583.IsoType;

public class BcdLengthLlllbinParseInfo extends LlllbinParseInfo
{
    public BcdLengthLlllbinParseInfo() {
        super(IsoType.LLLLBCDBIN, 0);
    }
    
    @Override
    protected int getLengthForBinaryParsing(final byte[] buf, final int pos) {
        final int length = super.getLengthForBinaryParsing(buf, pos);
        return (length % 2 == 0) ? (length / 2) : (length / 2 + 1);
    }
}
