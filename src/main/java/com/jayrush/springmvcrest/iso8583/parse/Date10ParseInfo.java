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

public class Date10ParseInfo extends DateTimeParseInfo
{
    public Date10ParseInfo() {
        super(IsoType.DATE10, 10);
    }
    
    @Override
    public <T> IsoValue<Date> parse(final int field, final byte[] buf, final int pos, final CustomField<T> custom) throws ParseException, UnsupportedEncodingException {
        if (pos < 0) {
            throw new ParseException(String.format("Invalid DATE10 field %d position %d", field, pos), pos);
        }
        if (pos + 10 > buf.length) {
            throw new ParseException(String.format("Insufficient data for DATE10 field %d, pos %d", field, pos), pos);
        }
        final Calendar cal = Calendar.getInstance();
        if (this.forceStringDecoding) {
            cal.set(2, Integer.parseInt(new String(buf, pos, 2, this.getCharacterEncoding()), 10) - 1);
            cal.set(5, Integer.parseInt(new String(buf, pos + 2, 2, this.getCharacterEncoding()), 10));
            cal.set(11, Integer.parseInt(new String(buf, pos + 4, 2, this.getCharacterEncoding()), 10));
            cal.set(12, Integer.parseInt(new String(buf, pos + 6, 2, this.getCharacterEncoding()), 10));
            cal.set(13, Integer.parseInt(new String(buf, pos + 8, 2, this.getCharacterEncoding()), 10));
        }
        else {
            cal.set(2, (buf[pos] - 48) * 10 + buf[pos + 1] - 49);
            cal.set(5, (buf[pos + 2] - 48) * 10 + buf[pos + 3] - 48);
            cal.set(11, (buf[pos + 4] - 48) * 10 + buf[pos + 5] - 48);
            cal.set(12, (buf[pos + 6] - 48) * 10 + buf[pos + 7] - 48);
            cal.set(13, (buf[pos + 8] - 48) * 10 + buf[pos + 9] - 48);
        }
        cal.set(14, 0);
        return this.createValue(cal, true);
    }
    
    @Override
    public <T> IsoValue<Date> parseBinary(final int field, final byte[] buf, final int pos, final CustomField<T> custom) throws ParseException {
        if (pos < 0) {
            throw new ParseException(String.format("Invalid DATE10 field %d position %d", field, pos), pos);
        }
        if (pos + 5 > buf.length) {
            throw new ParseException(String.format("Insufficient data for DATE10 field %d, pos %d", field, pos), pos);
        }
        final int[] tens = new int[5];
        int start = 0;
        for (int i = pos; i < pos + tens.length; ++i) {
            tens[start++] = Bcd.parseBcdLength(buf[i]);
        }
        final Calendar cal = Calendar.getInstance();
        cal.set(2, tens[0] - 1);
        cal.set(5, tens[1]);
        cal.set(11, tens[2]);
        cal.set(12, tens[3]);
        cal.set(13, tens[4]);
        cal.set(14, 0);
        return this.createValue(cal, true);
    }
}
