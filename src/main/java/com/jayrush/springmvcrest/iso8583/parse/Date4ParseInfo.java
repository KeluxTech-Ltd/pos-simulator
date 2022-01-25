// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.iso8583.parse;

import com.jayrush.springmvcrest.iso8583.CustomField;
import com.jayrush.springmvcrest.iso8583.IsoType;
import com.jayrush.springmvcrest.iso8583.IsoValue;
import com.jayrush.springmvcrest.iso8583.util.Bcd;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class Date4ParseInfo extends DateTimeParseInfo
{
    public Date4ParseInfo() {
        super(IsoType.DATE4, 4);
    }
    
    @Override
    public <T> IsoValue<Date> parse(final int field, final byte[] buf, final int pos, final CustomField<T> custom) throws ParseException, UnsupportedEncodingException {
        if (pos < 0) {
            throw new ParseException(String.format("Invalid DATE4 field %d position %d", field, pos), pos);
        }
        if (pos + 4 > buf.length) {
            throw new ParseException(String.format("Insufficient data for DATE4 field %d, pos %d", field, pos), pos);
        }
        final Calendar cal = Calendar.getInstance();
        cal.set(10, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        if (this.forceStringDecoding) {
            cal.set(2, Integer.parseInt(new String(buf, pos, 2, this.getCharacterEncoding()), 10) - 1);
            cal.set(5, Integer.parseInt(new String(buf, pos + 2, 2, this.getCharacterEncoding()), 10));
        }
        else {
            cal.set(2, (buf[pos] - 48) * 10 + buf[pos + 1] - 49);
            cal.set(5, (buf[pos + 2] - 48) * 10 + buf[pos + 3] - 48);
        }
        return this.createValue(cal, true);
    }
    
    @Override
    public <T> IsoValue<Date> parseBinary(final int field, final byte[] buf, final int pos, final CustomField<T> custom) throws ParseException {
        final int[] tens = new int[2];
        int start = 0;
        if (buf.length - pos < 2) {
            throw new ParseException(String.format("Insufficient data to parse binary DATE4 field %d pos %d", field, pos), pos);
        }
        for (int i = pos; i < pos + tens.length; ++i) {
            tens[start++] = Bcd.parseBcdLength(buf[i]);
        }
        final Calendar cal = Calendar.getInstance();
        cal.set(10, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(2, tens[0] - 1);
        cal.set(5, tens[1]);
        cal.set(14, 0);
        return this.createValue(cal, true);
    }
}
