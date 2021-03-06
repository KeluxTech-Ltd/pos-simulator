// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.iso8583.parse;

import com.jayrush.springmvcrest.iso8583.*;
import com.jayrush.springmvcrest.iso8583.util.Bcd;
import com.jayrush.springmvcrest.iso8583.util.HexCodec;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

public class LlllbinParseInfo extends FieldParseInfo
{
    public LlllbinParseInfo(final IsoType t, final int len) {
        super(t, len);
    }
    
    public LlllbinParseInfo() {
        super(IsoType.LLLLBIN, 0);
    }
    
    @Override
    public <T> IsoValue<?> parse(final int field, final byte[] buf, final int pos, final CustomField<T> custom) throws ParseException, UnsupportedEncodingException {
        if (pos < 0) {
            throw new ParseException(String.format("Invalid LLLLBIN field %d pos %d", field, pos), pos);
        }
        if (pos + 4 > buf.length) {
            throw new ParseException(String.format("Insufficient LLLLBIN header field %d", field), pos);
        }
        final int l = this.decodeLength(buf, pos, 4);
        if (l < 0) {
            throw new ParseException(String.format("Invalid LLLLBIN length %d field %d pos %d", l, field, pos), pos);
        }
        if (l + pos + 4 > buf.length) {
            throw new ParseException(String.format("Insufficient data for LLLLBIN field %d, pos %d", field, pos), pos);
        }
        final byte[] binval = (l == 0) ? new byte[0] : HexCodec.hexDecode(new String(buf, pos + 4, l));
        if (custom == null) {
            return new IsoValue<Object>(this.type, binval, binval.length, null);
        }
        if (custom instanceof CustomBinaryField) {
            try {
                final T dec = (T) ((CustomBinaryField)custom).decodeBinaryField(buf, pos + 4, l);
                return (dec == null) ? new IsoValue<Object>(this.type, binval, binval.length, null) : new IsoValue<Object>(this.type, dec, 0, (CustomFieldEncoder<Object>) custom);
            }
            catch (IndexOutOfBoundsException ex) {
                throw new ParseException(String.format("Insufficient data for LLLLBIN field %d, pos %d", field, pos), pos);
            }
        }
        try {
            final T dec = custom.decodeField((l == 0) ? "" : new String(buf, pos + 4, l));
            return (dec == null) ? new IsoValue<Object>(this.type, binval, binval.length, null) : new IsoValue<Object>(this.type, dec, l, (CustomFieldEncoder<Object>) custom);
        }
        catch (IndexOutOfBoundsException ex) {
            throw new ParseException(String.format("Insufficient data for LLLLBIN field %d, pos %d", field, pos), pos);
        }
    }
    
    @Override
    public <T> IsoValue<?> parseBinary(final int field, final byte[] buf, final int pos, final CustomField<T> custom) throws ParseException {
        if (pos < 0) {
            throw new ParseException(String.format("Invalid bin LLLLBIN field %d pos %d", field, pos), pos);
        }
        if (pos + 2 > buf.length) {
            throw new ParseException(String.format("Insufficient LLLLBIN header field %d", field), pos);
        }
        final int l = this.getLengthForBinaryParsing(buf, pos);
        if (l < 0) {
            throw new ParseException(String.format("Invalid LLLLBIN length %d field %d pos %d", l, field, pos), pos);
        }
        if (l + pos + 2 > buf.length) {
            throw new ParseException(String.format("Insufficient data for bin LLLLBIN field %d, pos %d requires %d, only %d available", field, pos, l, buf.length - pos + 1), pos);
        }
        final byte[] _v = new byte[l];
        System.arraycopy(buf, pos + 2, _v, 0, l);
        if (custom == null) {
            final int len = Bcd.parseBcdLength2bytes(buf, pos);
            return new IsoValue<Object>(this.type, _v, len);
        }
        if (custom instanceof CustomBinaryField) {
            try {
                final T dec = (T) ((CustomBinaryField)custom).decodeBinaryField(buf, pos + 2, l);
                return (dec == null) ? new IsoValue<Object>(this.type, _v, _v.length, null) : new IsoValue<Object>(this.type, dec, l, (CustomFieldEncoder<Object>) custom);
            }
            catch (IndexOutOfBoundsException ex) {
                throw new ParseException(String.format("Insufficient data for LLLLBIN field %d, pos %d", field, pos), pos);
            }
        }
        final T dec = custom.decodeField(HexCodec.hexEncode(_v, 0, _v.length));
        return (dec == null) ? new IsoValue<Object>(this.type, _v, null) : new IsoValue<Object>(this.type, dec, (CustomFieldEncoder<Object>) custom);
    }
    
    protected int getLengthForBinaryParsing(final byte[] buf, final int pos) {
        return Bcd.parseBcdLength2bytes(buf, pos);
    }
}
