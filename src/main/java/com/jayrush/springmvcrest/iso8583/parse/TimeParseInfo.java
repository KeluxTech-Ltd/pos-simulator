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

public class TimeParseInfo extends DateTimeParseInfo
{
    public TimeParseInfo() {
        super(IsoType.TIME, 6);
    }
    
    @Override
    public <T> IsoValue<Date> parse(final int field, final byte[] buf, final int pos, final CustomField<T> custom) throws ParseException, UnsupportedEncodingException {
        if (pos < 0) {
            throw new ParseException(String.format("Invalid TIME field %d pos %d", field, pos), pos);
        }
        if (pos + 6 > buf.length) {
            throw new ParseException(String.format("Insufficient data for TIME field %d, pos %d", field, pos), pos);
        }
        final Calendar cal = Calendar.getInstance();
        if (this.forceStringDecoding) {
            cal.set(11, Integer.parseInt(new String(buf, pos, 2, this.getCharacterEncoding()), 10));
            cal.set(12, Integer.parseInt(new String(buf, pos + 2, 2, this.getCharacterEncoding()), 10));
            cal.set(13, Integer.parseInt(new String(buf, pos + 4, 2, this.getCharacterEncoding()), 10));
        }
        else {
            cal.set(11, (buf[pos] - 48) * 10 + buf[pos + 1] - 48);
            cal.set(12, (buf[pos + 2] - 48) * 10 + buf[pos + 3] - 48);
            cal.set(13, (buf[pos + 4] - 48) * 10 + buf[pos + 5] - 48);
        }
        return this.createValue(cal, false);
    }
    
    @Override
    public <T> IsoValue<Date> parseBinary(final int field, final byte[] buf, final int pos, final CustomField<T> custom) throws ParseException {
        if (pos < 0) {
            throw new ParseException(String.format("Invalid bin TIME field %d pos %d", field, pos), pos);
        }
        if (pos + 3 > buf.length) {
            throw new ParseException(String.format("Insufficient data for bin TIME field %d, pos %d", field, pos), pos);
        }
        final int[] tens = new int[3];
        int start = 0;
        for (int i = pos; i < pos + 3; ++i) {
            tens[start++] = Bcd.parseBcdLength(buf[i]);
        }
        final Calendar cal = Calendar.getInstance();
        cal.set(11, tens[0]);
        cal.set(12, tens[1]);
        cal.set(13, tens[2]);
        if (this.tz != null) {
            cal.setTimeZone(this.tz);
        }
        return new IsoValue<Date>(this.type, cal.getTime(), null);
    }
}
