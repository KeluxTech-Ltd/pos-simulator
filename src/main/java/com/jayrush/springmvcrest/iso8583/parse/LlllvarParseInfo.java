// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.iso8583.parse;

import com.jayrush.springmvcrest.iso8583.CustomField;
import com.jayrush.springmvcrest.iso8583.CustomFieldEncoder;
import com.jayrush.springmvcrest.iso8583.IsoType;
import com.jayrush.springmvcrest.iso8583.IsoValue;
import com.jayrush.springmvcrest.iso8583.util.Bcd;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

public class LlllvarParseInfo extends FieldParseInfo
{
    public LlllvarParseInfo() {
        super(IsoType.LLLLVAR, 0);
    }
    
    @Override
    public <T> IsoValue<?> parse(final int field, final byte[] buf, final int pos, final CustomField<T> custom) throws ParseException, UnsupportedEncodingException {
        if (pos < 0) {
            throw new ParseException(String.format("Invalid LLLLVAR field %d %d", field, pos), pos);
        }
        if (pos + 4 > buf.length) {
            throw new ParseException(String.format("Insufficient data for LLLLVAR header, pos %d", pos), pos);
        }
        final int len = this.decodeLength(buf, pos, 4);
        if (len < 0) {
            throw new ParseException(String.format("Invalid LLLLVAR length %d, field %d pos %d", len, field, pos), pos);
        }
        if (len + pos + 4 > buf.length) {
            throw new ParseException(String.format("Insufficient data for LLLLVAR field %d, pos %d", field, pos), pos);
        }
        String _v;
        try {
            _v = ((len == 0) ? "" : new String(buf, pos + 4, len, this.getCharacterEncoding()));
        }
        catch (IndexOutOfBoundsException ex) {
            throw new ParseException(String.format("Insufficient data for LLLLVAR header, field %d pos %d", field, pos), pos);
        }
        if (_v.length() != len) {
            _v = new String(buf, pos + 4, buf.length - pos - 4, this.getCharacterEncoding()).substring(0, len);
        }
        if (custom == null) {
            return new IsoValue<Object>(this.type, _v, len, null);
        }
        final T dec = custom.decodeField(_v);
        return (dec == null) ? new IsoValue<Object>(this.type, _v, len, null) : new IsoValue<Object>(this.type, dec, len, (CustomFieldEncoder<Object>) custom);
    }
    
    @Override
    public <T> IsoValue<?> parseBinary(final int field, final byte[] buf, final int pos, final CustomField<T> custom) throws ParseException, UnsupportedEncodingException {
        if (pos < 0) {
            throw new ParseException(String.format("Invalid bin LLLLVAR field %d pos %d", field, pos), pos);
        }
        if (pos + 2 > buf.length) {
            throw new ParseException(String.format("Insufficient data for bin LLLLVAR header, field %d pos %d", field, pos), pos);
        }
        final int len = Bcd.parseBcdLength2bytes(buf, pos);
        if (len < 0) {
            throw new ParseException(String.format("Invalid bin LLLLVAR length %d, field %d pos %d", len, field, pos), pos);
        }
        if (len + pos + 2 > buf.length) {
            throw new ParseException(String.format("Insufficient data for bin LLLLVAR field %d, pos %d", field, pos), pos);
        }
        if (custom == null) {
            return new IsoValue<Object>(this.type, new String(buf, pos + 2, len, this.getCharacterEncoding()), null);
        }
        final T dec = custom.decodeField(new String(buf, pos + 2, len, this.getCharacterEncoding()));
        return (dec == null) ? new IsoValue<Object>(this.type, new String(buf, pos + 2, len, this.getCharacterEncoding()), null) : new IsoValue<Object>(this.type, dec, (CustomFieldEncoder<Object>) custom);
    }
}
