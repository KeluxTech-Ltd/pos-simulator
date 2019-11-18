// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.iso8583.parse;

import com.jayrush.springmvcrest.iso8583.IsoType;
import com.jayrush.springmvcrest.iso8583.IsoValue;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public abstract class DateTimeParseInfo extends FieldParseInfo
{
    protected static final long FUTURE_TOLERANCE;
    protected TimeZone tz;
    private static TimeZone defaultTimezone;
    
    public static void setDefaultTimeZone(final TimeZone tz) {
        DateTimeParseInfo.defaultTimezone = tz;
    }
    
    public static TimeZone getDefaultTimeZone() {
        return DateTimeParseInfo.defaultTimezone;
    }
    
    protected DateTimeParseInfo(final IsoType type, final int length) {
        super(type, length);
    }
    
    public void setTimeZone(final TimeZone value) {
        this.tz = value;
    }
    
    public TimeZone getTimeZone() {
        return this.tz;
    }
    
    public static void adjustWithFutureTolerance(final Calendar cal) {
        final long now = System.currentTimeMillis();
        final long then = cal.getTimeInMillis();
        if (then > now && then - now > DateTimeParseInfo.FUTURE_TOLERANCE) {
            cal.add(1, -1);
        }
    }
    
    protected IsoValue<Date> createValue(final Calendar cal, final boolean adjusting) {
        if (this.tz != null) {
            cal.setTimeZone(this.tz);
        }
        else if (getDefaultTimeZone() != null) {
            cal.setTimeZone(getDefaultTimeZone());
        }
        if (adjusting) {
            adjustWithFutureTolerance(cal);
        }
        final IsoValue<Date> v = new IsoValue<Date>(this.type, cal.getTime(), null);
        if (this.tz != null) {
            v.setTimeZone(this.tz);
        }
        else if (getDefaultTimeZone() != null) {
            v.setTimeZone(getDefaultTimeZone());
        }
        return v;
    }
    
    static {
        FUTURE_TOLERANCE = Long.parseLong(System.getProperty("j8583.future.tolerance", "900000"));
    }
}
