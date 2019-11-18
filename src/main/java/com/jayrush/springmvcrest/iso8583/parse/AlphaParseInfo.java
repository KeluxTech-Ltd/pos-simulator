// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.iso8583.parse;

import com.jayrush.springmvcrest.iso8583.CustomField;
import com.jayrush.springmvcrest.iso8583.CustomFieldEncoder;
import com.jayrush.springmvcrest.iso8583.IsoType;
import com.jayrush.springmvcrest.iso8583.IsoValue;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

public class AlphaParseInfo extends AlphaNumericFieldParseInfo
{
    public AlphaParseInfo(final int len) {
        super(IsoType.ALPHA, len);
    }
    
    @Override
    public <T> IsoValue<?> parseBinary(final int field, final byte[] buf, final int pos, final CustomField<T> custom) throws ParseException, UnsupportedEncodingException {
        if (pos < 0) {
            throw new ParseException(String.format("Invalid bin ALPHA field %d position %d", field, pos), pos);
        }
        if (pos + this.length > buf.length) {
            throw new ParseException(String.format("Insufficient data for bin %s field %d of length %d, pos %d", this.type, field, this.length, pos), pos);
        }
        try {
            if (custom == null) {
                return new IsoValue<Object>(this.type, new String(buf, pos, this.length, this.getCharacterEncoding()), this.length, null);
            }
            final T decoded = custom.decodeField(new String(buf, pos, this.length, this.getCharacterEncoding()));
            IsoValue<Object> isoValue;
            if (decoded == null) {
                final IsoType type;
                String val = null;
                isoValue = new IsoValue<Object>(this.type, val, this.length, null);
                type = this.type;
                val = new String(buf, pos, this.length, this.getCharacterEncoding());
            }
            else {
                isoValue = new IsoValue<Object>(this.type, decoded, this.length, (CustomFieldEncoder<Object>)custom);
            }
            return isoValue;
        }
        catch (IndexOutOfBoundsException ex) {
            throw new ParseException(String.format("Insufficient data for bin %s field %d of length %d, pos %d", this.type, field, this.length, pos), pos);
        }
    }
}
