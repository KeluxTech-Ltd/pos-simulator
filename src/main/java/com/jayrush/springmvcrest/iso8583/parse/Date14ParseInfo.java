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

public class Date14ParseInfo extends DateTimeParseInfo
{
    public Date14ParseInfo() {
        super(IsoType.DATE14, 14);
    }
    
    @Override
    public <T> IsoValue<Date> parse(final int field, final byte[] buf, final int pos, final CustomField<T> custom) throws ParseException, UnsupportedEncodingException {
        if (pos < 0) {
            throw new ParseException(String.format("Invalid DATE14 field %d position %d", field, pos), pos);
        }
        if (pos + 14 > buf.length) {
            throw new ParseException(String.format("Insufficient data for DATE14 field %d, pos %d", field, pos), pos);
        }
        final Calendar cal = Calendar.getInstance();
        if (this.forceStringDecoding) {
            cal.set(1, Integer.parseInt(new String(buf, pos, 4, this.getCharacterEncoding()), 10));
            cal.set(2, Integer.parseInt(new String(buf, pos, 2, this.getCharacterEncoding()), 10) - 1);
            cal.set(5, Integer.parseInt(new String(buf, pos + 2, 2, this.getCharacterEncoding()), 10));
            cal.set(11, Integer.parseInt(new String(buf, pos + 4, 2, this.getCharacterEncoding()), 10));
            cal.set(12, Integer.parseInt(new String(buf, pos + 6, 2, this.getCharacterEncoding()), 10));
            cal.set(13, Integer.parseInt(new String(buf, pos + 8, 2, this.getCharacterEncoding()), 10));
        }
        else {
            cal.set(1, (buf[pos] - 48) * 1000 + (buf[pos + 1] - 48) * 100 + (buf[pos + 2] - 48) * 10 + buf[pos + 3] - 48);
            cal.set(2, (buf[pos + 4] - 48) * 10 + buf[pos + 5] - 49);
            cal.set(5, (buf[pos + 6] - 48) * 10 + buf[pos + 7] - 48);
            cal.set(11, (buf[pos + 8] - 48) * 10 + buf[pos + 9] - 48);
            cal.set(12, (buf[pos + 10] - 48) * 10 + buf[pos + 11] - 48);
            cal.set(13, (buf[pos + 12] - 48) * 10 + buf[pos + 13] - 48);
        }
        cal.set(14, 0);
        return this.createValue(cal, true);
    }
    
    @Override
    public <T> IsoValue<Date> parseBinary(final int field, final byte[] buf, final int pos, final CustomField<T> custom) throws ParseException {
        if (pos < 0) {
            throw new ParseException(String.format("Invalid DATE14 field %d position %d", field, pos), pos);
        }
        if (pos + 7 > buf.length) {
            throw new ParseException(String.format("Insufficient data for DATE14 field %d, pos %d", field, pos), pos);
        }
        final int[] tens = new int[7];
        int start = 0;
        for (int i = pos; i < pos + tens.length; ++i) {
            tens[start++] = Bcd.parseBcdLength(buf[i]);
        }
        final Calendar cal = Calendar.getInstance();
        cal.set(1, tens[0] * 100 + tens[1]);
        cal.set(2, tens[2] - 1);
        cal.set(5, tens[3]);
        cal.set(11, tens[4]);
        cal.set(12, tens[5]);
        cal.set(13, tens[6]);
        cal.set(14, 0);
        return this.createValue(cal, true);
    }
}
