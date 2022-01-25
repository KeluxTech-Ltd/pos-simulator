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

public class Date6ParseInfo extends DateTimeParseInfo
{
    public Date6ParseInfo() {
        super(IsoType.DATE6, 6);
    }
    
    @Override
    public <T> IsoValue<Date> parse(final int field, final byte[] buf, final int pos, final CustomField<T> custom) throws ParseException, UnsupportedEncodingException {
        if (pos < 0) {
            throw new ParseException(String.format("Invalid DATE6 field %d position %d", field, pos), pos);
        }
        if (pos + 6 > buf.length) {
            throw new ParseException(String.format("Insufficient data for DATE6 field %d, pos %d", field, pos), pos);
        }
        final Calendar cal = Calendar.getInstance();
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        int year;
        if (this.forceStringDecoding) {
            year = Integer.parseInt(new String(buf, pos, 2, this.getCharacterEncoding()), 10);
            cal.set(2, Integer.parseInt(new String(buf, pos + 2, 2, this.getCharacterEncoding()), 10) - 1);
            cal.set(5, Integer.parseInt(new String(buf, pos + 4, 2, this.getCharacterEncoding()), 10));
        }
        else {
            year = (buf[pos] - 48) * 10 + buf[pos + 1] - 48;
            cal.set(2, (buf[pos + 2] - 48) * 10 + buf[pos + 3] - 49);
            cal.set(5, (buf[pos + 4] - 48) * 10 + buf[pos + 5] - 48);
        }
        if (year > 50) {
            cal.set(1, 1900 + year);
        }
        else {
            cal.set(1, 2000 + year);
        }
        return this.createValue(cal, false);
    }
    
    @Override
    public <T> IsoValue<Date> parseBinary(final int field, final byte[] buf, final int pos, final CustomField<T> custom) throws ParseException, UnsupportedEncodingException {
        if (pos < 0) {
            throw new ParseException(String.format("Invalid DATE6 field %d position %d", field, pos), pos);
        }
        if (pos + 3 > buf.length) {
            throw new ParseException(String.format("Insufficient data for DATE6 field %d, pos %d", field, pos), pos);
        }
        final int[] tens = new int[3];
        int start = 0;
        for (int i = pos; i < pos + tens.length; ++i) {
            tens[start++] = Bcd.parseBcdLength(buf[i]);
        }
        final Calendar cal = Calendar.getInstance();
        if (tens[0] > 50) {
            cal.set(1, 1900 + tens[0]);
        }
        else {
            cal.set(1, 2000 + tens[0]);
        }
        cal.set(2, tens[1] - 1);
        cal.set(5, tens[2]);
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        if (this.tz != null) {
            cal.setTimeZone(this.tz);
        }
        return this.createValue(cal, true);
    }
}
