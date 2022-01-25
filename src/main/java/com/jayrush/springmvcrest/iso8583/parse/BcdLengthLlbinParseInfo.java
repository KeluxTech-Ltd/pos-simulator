// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.iso8583.parse;

import com.jayrush.springmvcrest.iso8583.IsoType;

public class BcdLengthLlbinParseInfo extends LlbinParseInfo
{
    public BcdLengthLlbinParseInfo() {
        super(IsoType.LLBCDBIN, 0);
    }
    
    @Override
    protected int getLengthForBinaryParsing(final byte b) {
        final int length = super.getLengthForBinaryParsing(b);
        return (length % 2 == 0) ? (length / 2) : (length / 2 + 1);
    }
}
