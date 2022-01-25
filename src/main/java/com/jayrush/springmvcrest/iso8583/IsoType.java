// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.iso8583;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public enum IsoType
{
    NUMERIC(true, 0), 
    ALPHA(true, 0), 
    LLVAR(false, 0), 
    LLLVAR(false, 0), 
    DATE14(false, 14), 
    DATE10(false, 10), 
    DATE4(false, 4), 
    DATE_EXP(false, 4), 
    TIME(false, 6), 
    AMOUNT(false, 12), 
    BINARY(true, 0), 
    LLBIN(false, 0), 
    LLLBIN(false, 0), 
    LLLLVAR(false, 0),
    LLLLLLVAR(false, 0),
    LLLLBIN(false, 0),
    LLBCDBIN(false, 0), 
    LLLBCDBIN(false, 0), 
    LLLLBCDBIN(false, 0), 
    DATE12(false, 12), 
    DATE6(false, 6);
    
    public static final Set<IsoType> VARIABLE_LENGTH_BIN_TYPES;
    private boolean needsLen;
    private int length;
    
    private IsoType(final boolean flag, final int l) {
        this.needsLen = flag;
        this.length = l;
    }
    
    public boolean needsLength() {
        return this.needsLen;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public String format(final Date value, final TimeZone tz) {
        SimpleDateFormat sdf;
        if (this == IsoType.DATE10) {
            sdf = new SimpleDateFormat("MMddHHmmss");
        }
        else if (this == IsoType.DATE4) {
            sdf = new SimpleDateFormat("MMdd");
        }
        else if (this == IsoType.DATE_EXP) {
            sdf = new SimpleDateFormat("yyMM");
        }
        else if (this == IsoType.TIME) {
            sdf = new SimpleDateFormat("HHmmss");
        }
        else if (this == IsoType.DATE12) {
            sdf = new SimpleDateFormat("yyMMddHHmmss");
        }
        else if (this == IsoType.DATE14) {
            sdf = new SimpleDateFormat("YYYYMMddHHmmss");
        }
        else {
            if (this != IsoType.DATE6) {
                throw new IllegalArgumentException("Cannot format date as " + this);
            }
            sdf = new SimpleDateFormat("yyMMdd");
        }
        if (tz != null) {
            sdf.setTimeZone(tz);
        }
        return sdf.format(value);
    }
    
    public String format(String value, final int length) {
        if (this == IsoType.ALPHA) {
            if (value == null) {
                value = "";
            }
            if (value.length() > length) {
                return value.substring(0, length);
            }
            if (value.length() == length) {
                return value;
            }
            return String.format(String.format("%%-%ds", length), value);
        }
        else {
            if (this == IsoType.LLVAR || this == IsoType.LLLVAR || this == IsoType.LLLLVAR) {
                return value;
            }
            if (this == IsoType.NUMERIC) {
                final char[] c = new char[length];
                final char[] x = value.toCharArray();
                if (x.length > length) {
                    throw new IllegalArgumentException("Numeric value is larger than intended length: " + value + " LEN " + length);
                }
                final int lim = c.length - x.length;
                for (int i = 0; i < lim; ++i) {
                    c[i] = '0';
                }
                System.arraycopy(x, 0, c, lim, x.length);
                return new String(c);
            }
            else {
                if (this == IsoType.AMOUNT) {
                    return IsoType.NUMERIC.format(new BigDecimal(value).movePointRight(2).longValue(), 12);
                }
                if (this == IsoType.BINARY) {
                    if (value == null) {
                        value = "";
                    }
                    if (value.length() > length) {
                        return value.substring(0, length);
                    }
                    final char[] c = new char[length];
                    int end = value.length();
                    if (value.length() % 2 == 1) {
                        c[0] = '0';
                        System.arraycopy(value.toCharArray(), 0, c, 1, value.length());
                        ++end;
                    }
                    else {
                        System.arraycopy(value.toCharArray(), 0, c, 0, value.length());
                    }
                    for (int j = end; j < c.length; ++j) {
                        c[j] = '0';
                    }
                    return new String(c);
                }
                else {
                    if (IsoType.VARIABLE_LENGTH_BIN_TYPES.contains(this)) {
                        return value;
                    }
                    throw new IllegalArgumentException("Cannot format String as " + this);
                }
            }
        }
    }
    
    public String format(final long value, final int length) {
        if (this == IsoType.NUMERIC) {
            final String x = String.format(String.format("%%0%dd", length), value);
            if (x.length() > length) {
                throw new IllegalArgumentException("Numeric value is larger than intended length: " + value + " LEN " + length);
            }
            return x;
        }
        else {
            if (this == IsoType.ALPHA || this == IsoType.LLVAR || this == IsoType.LLLVAR || this == IsoType.LLLLVAR) {
                return this.format(Long.toString(value), length);
            }
            if (this == IsoType.AMOUNT) {
                return String.format("%010d00", value);
            }
            if (this == IsoType.BINARY || IsoType.VARIABLE_LENGTH_BIN_TYPES.contains(this)) {}
            throw new IllegalArgumentException("Cannot format number as " + this);
        }
    }
    
    public String format(final BigDecimal value, final int length) {
        if (this == IsoType.AMOUNT) {
            return String.format("%012d", value.movePointRight(2).longValue());
        }
        if (this == IsoType.NUMERIC) {
            return this.format(value.longValue(), length);
        }
        if (this == IsoType.ALPHA || this == IsoType.LLVAR || this == IsoType.LLLVAR || this == IsoType.LLLLVAR) {
            return this.format(value.toString(), length);
        }
        if (this == IsoType.BINARY || IsoType.VARIABLE_LENGTH_BIN_TYPES.contains(this)) {}
        throw new IllegalArgumentException("Cannot format BigDecimal as " + this);
    }
    
    public <T> IsoValue<T> value(final T val, final int len) {
        return new IsoValue<T>(this, val, len);
    }
    
    public <T> IsoValue<T> value(final T val) {
        return new IsoValue<T>(this, val);
    }
    
    public <T> IsoValue<T> call(final T val, final int len) {
        return new IsoValue<T>(this, val, len);
    }
    
    public <T> IsoValue<T> call(final T val) {
        return new IsoValue<T>(this, val);
    }
    
    public <T> IsoValue<T> apply(final T val, final int len) {
        return new IsoValue<T>(this, val, len);
    }
    
    public <T> IsoValue<T> apply(final T val) {
        return new IsoValue<T>(this, val);
    }
    
    static {
        VARIABLE_LENGTH_BIN_TYPES = Collections.unmodifiableSet((Set<? extends IsoType>) EnumSet.of(IsoType.LLBIN, new IsoType[] { IsoType.LLLBIN, IsoType.LLLLBIN, IsoType.LLBCDBIN, IsoType.LLLBCDBIN, IsoType.LLLLBCDBIN }));
    }
}
