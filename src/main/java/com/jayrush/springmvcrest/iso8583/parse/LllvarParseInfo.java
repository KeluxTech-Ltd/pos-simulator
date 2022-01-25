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

public class LllvarParseInfo extends FieldParseInfo
{
    public LllvarParseInfo() {
        super(IsoType.LLLVAR, 0);
    }
    
    @Override
    public <T> IsoValue<?> parse(final int field, final byte[] buf, final int pos, final CustomField<T> custom) throws ParseException, UnsupportedEncodingException {
        if (pos < 0) {
            throw new ParseException(String.format("Invalid LLLVAR field %d pos %d", field, pos), pos);
        }
        if (pos + 3 > buf.length) {
            throw new ParseException(String.format("Insufficient data for LLLVAR header field %d pos %d", field, pos), pos);
        }
        final int len = this.decodeLength(buf, pos, 3);
        if (len < 0) {
            throw new ParseException(String.format("Invalid LLLVAR length %d(%s) field %d pos %d", len, new String(buf, pos, 3), field, pos), pos);
        }
        if (len + pos + 3 > buf.length) {
            throw new ParseException(String.format("Insufficient data for LLLVAR field %d, pos %d len %d", field, pos, len), pos);
        }
        String _v;
        try {
            _v = ((len == 0) ? "" : new String(buf, pos + 3, len, this.getCharacterEncoding()));
        }
        catch (IndexOutOfBoundsException ex) {
            throw new ParseException(String.format("Insufficient data for LLLVAR header, field %d pos %d len %d", field, pos, len), pos);
        }
        if (_v.length() != len) {
            _v = new String(buf, pos + 3, buf.length - pos - 3, this.getCharacterEncoding()).substring(0, len);
        }
        if (custom == null) {
            return new IsoValue<Object>(this.type, _v, len, null);
        }
        final T decoded = custom.decodeField(_v);
        return (decoded == null) ? new IsoValue<Object>(this.type, _v, len, null) : new IsoValue<Object>(this.type, decoded, len, (CustomFieldEncoder<Object>) custom);
    }
    
    @Override
    public <T> IsoValue<?> parseBinary(final int field, final byte[] buf, final int pos, final CustomField<T> custom) throws ParseException, UnsupportedEncodingException {
        if (pos < 0) {
            throw new ParseException(String.format("Invalid bin LLLVAR field %d pos %d", field, pos), pos);
        }
        if (pos + 2 > buf.length) {
            throw new ParseException(String.format("Insufficient data for bin LLLVAR header, field %d pos %d", field, pos), pos);
        }
        final int len = (buf[pos] & 0xF) * 100 + Bcd.parseBcdLength(buf[pos + 1]);
        if (len < 0) {
            throw new ParseException(String.format("Invalid bin LLLVAR length %d, field %d pos %d", len, field, pos), pos);
        }
        if (len + pos + 2 > buf.length) {
            throw new ParseException(String.format("Insufficient data for bin LLLVAR field %d, pos %d", field, pos), pos);
        }
        if (custom == null) {
            return new IsoValue<Object>(this.type, new String(buf, pos + 2, len, this.getCharacterEncoding()), null);
        }
        final IsoValue<T> v = new IsoValue<T>(this.type, custom.decodeField(new String(buf, pos + 2, len, this.getCharacterEncoding())), custom);
        if (v.getValue() == null) {
            return new IsoValue<Object>(this.type, new String(buf, pos + 2, len, this.getCharacterEncoding()), null);
        }
        return v;
    }
}
