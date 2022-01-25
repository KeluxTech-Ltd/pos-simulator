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

public class LlvarParseInfo extends FieldParseInfo
{
    public LlvarParseInfo() {
        super(IsoType.LLVAR, 0);
    }
    
    @Override
    public <T> IsoValue<?> parse(final int field, final byte[] buf, final int pos, final CustomField<T> custom) throws ParseException, UnsupportedEncodingException {
        if (pos < 0) {
            throw new ParseException(String.format("Invalid LLVAR field %d %d", field, pos), pos);
        }
        if (pos + 2 > buf.length) {
            throw new ParseException(String.format("Insufficient data for LLVAR header, pos %d", pos), pos);
        }
        final int len = this.decodeLength(buf, pos, 2);
        if (len < 0) {
            throw new ParseException(String.format("Invalid LLVAR length %d, field %d pos %d", len, field, pos), pos);
        }
        if (len + pos + 2 > buf.length) {
            throw new ParseException(String.format("Insufficient data for LLVAR field %d, pos %d len %d", field, pos, len), pos);
        }
        String _v;
        try {
            _v = ((len == 0) ? "" : new String(buf, pos + 2, len, this.getCharacterEncoding()));
        }
        catch (IndexOutOfBoundsException ex) {
            throw new ParseException(String.format("Insufficient data for LLVAR header, field %d pos %d len %d", field, pos, len), pos);
        }
        if (_v.length() != len) {
            _v = new String(buf, pos + 2, buf.length - pos - 2, this.getCharacterEncoding()).substring(0, len);
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
            throw new ParseException(String.format("Invalid bin LLVAR field %d pos %d", field, pos), pos);
        }
        if (pos + 1 > buf.length) {
            throw new ParseException(String.format("Insufficient data for bin LLVAR header, field %d pos %d", field, pos), pos);
        }
        final int len = Bcd.parseBcdLength(buf[pos]);
        if (len < 0) {
            throw new ParseException(String.format("Invalid bin LLVAR length %d, field %d pos %d", len, field, pos), pos);
        }
        if (len + pos + 1 > buf.length) {
            throw new ParseException(String.format("Insufficient data for bin LLVAR field %d, pos %d", field, pos), pos);
        }
        if (custom == null) {
            return new IsoValue<Object>(this.type, new String(buf, pos + 1, len, this.getCharacterEncoding()), null);
        }
        final T dec = custom.decodeField(new String(buf, pos + 1, len, this.getCharacterEncoding()));
        return (dec == null) ? new IsoValue<Object>(this.type, new String(buf, pos + 1, len, this.getCharacterEncoding()), null) : new IsoValue<Object>(this.type, dec, (CustomFieldEncoder<Object>) custom);
    }
}
