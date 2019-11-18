// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.iso8583.parse;

import com.jayrush.springmvcrest.iso8583.CustomField;
import com.jayrush.springmvcrest.iso8583.CustomFieldEncoder;
import com.jayrush.springmvcrest.iso8583.IsoType;
import com.jayrush.springmvcrest.iso8583.IsoValue;
import com.jayrush.springmvcrest.iso8583.util.HexCodec;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

public class BinaryParseInfo extends FieldParseInfo
{
    public BinaryParseInfo(final int len) {
        super(IsoType.BINARY, len);
    }
    
    @Override
    public <T> IsoValue<?> parse(final int field, final byte[] buf, final int pos, final CustomField<T> custom) throws ParseException, UnsupportedEncodingException {
        if (pos < 0) {
            throw new ParseException(String.format("Invalid BINARY field %d position %d", field, pos), pos);
        }
        if (pos + this.length * 2 > buf.length) {
            throw new ParseException(String.format("Insufficient data for BINARY field %d of length %d, pos %d", field, this.length, pos), pos);
        }
        final byte[] binval = HexCodec.hexDecode(new String(buf, pos, this.length * 2));
        if (custom == null) {
            return new IsoValue<Object>(this.type, binval, binval.length, null);
        }
        final T dec = custom.decodeField(new String(buf, pos, this.length * 2, this.getCharacterEncoding()));
        return (dec == null) ? new IsoValue<Object>(this.type, binval, binval.length, null) : new IsoValue<Object>(this.type, dec, this.length, (CustomFieldEncoder<Object>) custom);
    }
    
    @Override
    public <T> IsoValue<?> parseBinary(final int field, final byte[] buf, final int pos, final CustomField<T> custom) throws ParseException {
        if (pos < 0) {
            throw new ParseException(String.format("Invalid BINARY field %d position %d", field, pos), pos);
        }
        if (pos + this.length > buf.length) {
            throw new ParseException(String.format("Insufficient data for BINARY field %d of length %d, pos %d", field, this.length, pos), pos);
        }
        final byte[] _v = new byte[this.length];
        System.arraycopy(buf, pos, _v, 0, this.length);
        if (custom == null) {
            return new IsoValue<Object>(this.type, _v, this.length, null);
        }
        final T dec = custom.decodeField(HexCodec.hexEncode(_v, 0, _v.length));
        return (dec == null) ? new IsoValue<Object>(this.type, _v, this.length, null) : new IsoValue<Object>(this.type, dec, this.length, (CustomFieldEncoder<Object>) custom);
    }
}
