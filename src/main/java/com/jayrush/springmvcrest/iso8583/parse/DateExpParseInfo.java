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

public class DateExpParseInfo extends DateTimeParseInfo
{
    public DateExpParseInfo() {
        super(IsoType.DATE_EXP, 4);
    }
    
    @Override
    public <T> IsoValue<Date> parse(final int field, final byte[] buf, final int pos, final CustomField<T> custom) throws ParseException, UnsupportedEncodingException {
        if (pos < 0) {
            throw new ParseException(String.format("Invalid DATE_EXP field %d position %d", field, pos), pos);
        }
        if (pos + 4 > buf.length) {
            throw new ParseException(String.format("Insufficient data for DATE_EXP field %d pos %d", field, pos), pos);
        }
        final Calendar cal = Calendar.getInstance();
        cal.set(10, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(5, 1);
        if (this.forceStringDecoding) {
            cal.set(1, cal.get(1) - cal.get(1) % 100 + Integer.parseInt(new String(buf, pos, 2, this.getCharacterEncoding()), 10));
            cal.set(2, Integer.parseInt(new String(buf, pos + 2, 2, this.getCharacterEncoding()), 10) - 1);
        }
        else {
            cal.set(1, cal.get(1) - cal.get(1) % 100 + (buf[pos] - 48) * 10 + buf[pos + 1] - 48);
            cal.set(2, (buf[pos + 2] - 48) * 10 + buf[pos + 3] - 49);
        }
        return this.createValue(cal, false);
    }
    
    @Override
    public <T> IsoValue<Date> parseBinary(final int field, final byte[] buf, final int pos, final CustomField<T> custom) throws ParseException {
        if (pos < 0) {
            throw new ParseException(String.format("Invalid DATE_EXP field %d position %d", field, pos), pos);
        }
        if (pos + 2 > buf.length) {
            throw new ParseException(String.format("Insufficient data for DATE_EXP field %d pos %d", field, pos), pos);
        }
        final int[] tens = new int[2];
        int start = 0;
        for (int i = pos; i < pos + tens.length; ++i) {
            tens[start++] = Bcd.parseBcdLength(buf[i]);
        }
        final Calendar cal = Calendar.getInstance();
        cal.set(10, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(5, 1);
        cal.set(1, cal.get(1) - cal.get(1) % 100 + tens[0]);
        cal.set(2, tens[1] - 1);
        return this.createValue(cal, false);
    }
}
