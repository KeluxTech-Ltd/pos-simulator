// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.iso8583.parse;

import com.jayrush.springmvcrest.iso8583.*;
import com.jayrush.springmvcrest.iso8583.util.Bcd;
import com.jayrush.springmvcrest.iso8583.util.HexCodec;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

public class LlbinParseInfo extends FieldParseInfo
{
    public LlbinParseInfo(final IsoType t, final int len) {
        super(t, len);
    }
    
    public LlbinParseInfo() {
        super(IsoType.LLBIN, 0);
    }
    
    @Override
    public <T> IsoValue<?> parse(final int field, final byte[] buf, final int pos, final CustomField<T> custom) throws ParseException, UnsupportedEncodingException {
        if (pos < 0) {
            throw new ParseException(String.format("Invalid LLBIN field %d position %d", field, pos), pos);
        }
        if (pos + 2 > buf.length) {
            throw new ParseException(String.format("Insufficient LLBIN header field %d", field), pos);
        }
        final int len = this.decodeLength(buf, pos, 2);
        if (len < 0) {
            throw new ParseException(String.format("Invalid LLBIN field %d length %d pos %d", field, len, pos), pos);
        }
        if (len + pos + 2 > buf.length) {
            throw new ParseException(String.format("Insufficient data for LLBIN field %d, pos %d (LEN states '%s')", field, pos, new String(buf, pos, 2)), pos);
        }
        final byte[] binval = (len == 0) ? new byte[0] : HexCodec.hexDecode(new String(buf, pos + 2, len));
        if (custom == null) {
            return new IsoValue<Object>(this.type, binval, binval.length, null);
        }
        if (custom instanceof CustomBinaryField) {
            try {
                final T dec = (T) ((CustomBinaryField)custom).decodeBinaryField(buf, pos + 2, len);
                return (dec == null) ? new IsoValue<Object>(this.type, binval, binval.length, null) : new IsoValue<Object>(this.type, dec, 0, (CustomFieldEncoder<Object>) custom);
            }
            catch (IndexOutOfBoundsException ex) {
                throw new ParseException(String.format("Insufficient data for LLBIN field %d, pos %d (LEN states '%s')", field, pos, new String(buf, pos, 2)), pos);
            }
        }
        try {
            final T dec = custom.decodeField(new String(buf, pos + 2, len));
            return (dec == null) ? new IsoValue<Object>(this.type, binval, binval.length, null) : new IsoValue<Object>(this.type, dec, binval.length, (CustomFieldEncoder<Object>) custom);
        }
        catch (IndexOutOfBoundsException ex) {
            throw new ParseException(String.format("Insufficient data for LLBIN field %d, pos %d (LEN states '%s')", field, pos, new String(buf, pos, 2)), pos);
        }
    }
    
    @Override
    public <T> IsoValue<?> parseBinary(final int field, final byte[] buf, final int pos, final CustomField<T> custom) throws ParseException {
        if (pos < 0) {
            throw new ParseException(String.format("Invalid bin LLBIN field %d position %d", field, pos), pos);
        }
        if (pos + 1 > buf.length) {
            throw new ParseException(String.format("Insufficient bin LLBIN header field %d", field), pos);
        }
        final int l = this.getLengthForBinaryParsing(buf[pos]);
        if (l < 0) {
            throw new ParseException(String.format("Invalid bin LLBIN length %d pos %d", l, pos), pos);
        }
        if (l + pos + 1 > buf.length) {
            throw new ParseException(String.format("Insufficient data for bin LLBIN field %d, pos %d: need %d, only %d available", field, pos, l, buf.length), pos);
        }
        final byte[] _v = new byte[l];
        System.arraycopy(buf, pos + 1, _v, 0, l);
        if (custom == null) {
            final int len = Bcd.parseBcdLength(buf[pos]);
            return new IsoValue<Object>(this.type, _v, len);
        }
        if (custom instanceof CustomBinaryField) {
            try {
                final T dec = (T) ((CustomBinaryField)custom).decodeBinaryField(buf, pos + 1, l);
                return (dec == null) ? new IsoValue<Object>(this.type, _v, _v.length, null) : new IsoValue<Object>(this.type, dec, l, (CustomFieldEncoder<Object>) custom);
            }
            catch (IndexOutOfBoundsException ex) {
                throw new ParseException(String.format("Insufficient data for LLBIN field %d, pos %d length %d", field, pos, l), pos);
            }
        }
        final T dec = custom.decodeField(HexCodec.hexEncode(_v, 0, _v.length));
        return (dec == null) ? new IsoValue<Object>(this.type, _v, null) : new IsoValue<Object>(this.type, dec, (CustomFieldEncoder<Object>) custom);
    }
    
    protected int getLengthForBinaryParsing(final byte b) {
        return Bcd.parseBcdLength(b);
    }
}
