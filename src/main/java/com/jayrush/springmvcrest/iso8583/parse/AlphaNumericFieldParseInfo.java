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

public abstract class AlphaNumericFieldParseInfo extends FieldParseInfo
{
    public AlphaNumericFieldParseInfo(final IsoType t, final int len) {
        super(t, len);
    }
    
    @Override
    public <T> IsoValue<?> parse(final int field, final byte[] buf, final int pos, final CustomField<T> custom) throws ParseException, UnsupportedEncodingException {
        if (pos < 0) {
            throw new ParseException(String.format("Invalid ALPHA/NUM field %d position %d", field, pos), pos);
        }
        if (pos + this.length > buf.length) {
            throw new ParseException(String.format("Insufficient data for %s field %d of length %d, pos %d", this.type, field, this.length, pos), pos);
        }
        try {
            String _v = new String(buf, pos, this.length, this.getCharacterEncoding());
            if (_v.length() != this.length) {
                _v = new String(buf, pos, buf.length - pos, this.getCharacterEncoding()).substring(0, this.length);
            }
            if (custom == null) {
                return new IsoValue<Object>(this.type, _v, this.length, null);
            }
            final T decoded = custom.decodeField(_v);
            return (decoded == null) ? new IsoValue<Object>(this.type, _v, this.length, null) : new IsoValue<Object>(this.type, decoded, this.length, (CustomFieldEncoder<Object>) custom);
        }
        catch (StringIndexOutOfBoundsException ex) {
            throw new ParseException(String.format("Insufficient data for %s field %d of length %d, pos %d", this.type, field, this.length, pos), pos);
        }
    }
}
