// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.iso8583.parse;

import com.jayrush.springmvcrest.iso8583.CustomField;
import com.jayrush.springmvcrest.iso8583.IsoType;
import com.jayrush.springmvcrest.iso8583.IsoValue;
import com.jayrush.springmvcrest.iso8583.util.Bcd;

import java.text.ParseException;

public class NumericParseInfo extends AlphaNumericFieldParseInfo
{
    public NumericParseInfo(final int len) {
        super(IsoType.NUMERIC, len);
    }
    
    @Override
    public <T> IsoValue<Number> parseBinary(final int field, final byte[] buf, final int pos, final CustomField<T> custom) throws ParseException {
        if (pos < 0) {
            throw new ParseException(String.format("Invalid bin NUMERIC field %d pos %d", field, pos), pos);
        }
        if (pos + this.length / 2 > buf.length) {
            throw new ParseException(String.format("Insufficient data for bin %s field %d of length %d, pos %d", this.type, field, this.length, pos), pos);
        }
        if (this.length < 19) {
            return new IsoValue<Number>(IsoType.NUMERIC, Bcd.decodeToLong(buf, pos, this.length), this.length, null);
        }
        try {
            return new IsoValue<Number>(IsoType.NUMERIC, Bcd.decodeToBigInteger(buf, pos, this.length), this.length, null);
        }
        catch (IndexOutOfBoundsException ex) {
            throw new ParseException(String.format("Insufficient data for bin %s field %d of length %d, pos %d", this.type, field, this.length, pos), pos);
        }
    }
}
