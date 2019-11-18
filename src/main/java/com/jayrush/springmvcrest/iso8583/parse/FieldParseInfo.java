// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.iso8583.parse;

import com.jayrush.springmvcrest.iso8583.CustomField;
import com.jayrush.springmvcrest.iso8583.IsoType;
import com.jayrush.springmvcrest.iso8583.IsoValue;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

public abstract class FieldParseInfo
{
    protected IsoType type;
    protected final int length;
    private String encoding;
    protected boolean forceStringDecoding;
    private CustomField<?> decoder;
    
    public FieldParseInfo(final IsoType t, final int len) {
        this.encoding = System.getProperty("file.encoding");
        if (t == null) {
            throw new IllegalArgumentException("IsoType cannot be null");
        }
        this.type = t;
        this.length = len;
    }
    
    public void setForceStringDecoding(final boolean flag) {
        this.forceStringDecoding = flag;
    }
    
    public void setCharacterEncoding(final String value) {
        this.encoding = value;
    }
    
    public String getCharacterEncoding() {
        return this.encoding;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public IsoType getType() {
        return this.type;
    }
    
    public void setDecoder(final CustomField<?> value) {
        this.decoder = value;
    }
    
    public CustomField<?> getDecoder() {
        return this.decoder;
    }
    
    public abstract <T> IsoValue<?> parse(final int p0, final byte[] p1, final int p2, final CustomField<T> p3) throws ParseException, UnsupportedEncodingException;
    
    public abstract <T> IsoValue<?> parseBinary(final int p0, final byte[] p1, final int p2, final CustomField<T> p3) throws ParseException, UnsupportedEncodingException;
    
    public static FieldParseInfo getInstance(final IsoType t, final int len, final String encoding) {
        FieldParseInfo fpi = null;
        if (t == IsoType.ALPHA) {
            fpi = new AlphaParseInfo(len);
        }
        else if (t == IsoType.AMOUNT) {
            fpi = new AmountParseInfo();
        }
        else if (t == IsoType.BINARY) {
            fpi = new BinaryParseInfo(len);
        }
        else if (t == IsoType.DATE10) {
            fpi = new Date10ParseInfo();
        }
        else if (t == IsoType.DATE12) {
            fpi = new Date12ParseInfo();
        }
        else if (t == IsoType.DATE14) {
            fpi = new Date14ParseInfo();
        }
        else if (t == IsoType.DATE4) {
            fpi = new Date4ParseInfo();
        }
        else if (t == IsoType.DATE_EXP) {
            fpi = new DateExpParseInfo();
        }
        else if (t == IsoType.LLBIN) {
            fpi = new LlbinParseInfo();
        }
        else if (t == IsoType.LLLBIN) {
            fpi = new LllbinParseInfo();
        }
        else if (t == IsoType.LLLVAR) {
            fpi = new LllvarParseInfo();
        }
        else if (t == IsoType.LLVAR) {
            fpi = new LlvarParseInfo();
        }
        else if (t == IsoType.NUMERIC) {
            fpi = new NumericParseInfo(len);
        }
        else if (t == IsoType.TIME) {
            fpi = new TimeParseInfo();
        }
        else if (t == IsoType.LLLLVAR) {
            fpi = new LlllvarParseInfo();
        }
        else if (t == IsoType.LLLLBIN) {
            fpi = new LlllbinParseInfo();
        }
        else if (t == IsoType.LLBCDBIN) {
            fpi = new BcdLengthLlbinParseInfo();
        }
        else if (t == IsoType.LLLBCDBIN) {
            fpi = new BcdLengthLllbinParseInfo();
        }
        else if (t == IsoType.LLLLBCDBIN) {
            fpi = new BcdLengthLlllbinParseInfo();
        }
        if (fpi == null) {
            throw new IllegalArgumentException(String.format("Cannot parse type %s", t));
        }
        fpi.setCharacterEncoding(encoding);
        return fpi;
    }
    
    protected int decodeLength(final byte[] buf, final int pos, final int digits) throws UnsupportedEncodingException {
        if (this.forceStringDecoding) {
            return Integer.parseInt(new String(buf, pos, digits, this.encoding), 10);
        }
        switch (digits) {
            case 2: {
                return (buf[pos] - 48) * 10 + (buf[pos + 1] - 48);
            }
            case 3: {
                return (buf[pos] - 48) * 100 + (buf[pos + 1] - 48) * 10 + (buf[pos + 2] - 48);
            }
            case 4: {
                return (buf[pos] - 48) * 1000 + (buf[pos + 1] - 48) * 100 + (buf[pos + 2] - 48) * 10 + (buf[pos + 3] - 48);
            }
            default: {
                return -1;
            }
        }
    }
}
