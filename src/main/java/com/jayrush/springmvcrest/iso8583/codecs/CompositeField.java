// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.iso8583.codecs;


import com.jayrush.springmvcrest.iso8583.*;
import com.jayrush.springmvcrest.iso8583.parse.FieldParseInfo;
import com.jayrush.springmvcrest.slf4j.Logger;
import com.jayrush.springmvcrest.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CompositeField implements CustomBinaryField<CompositeField>
{
    private static final Logger log;
    private List<IsoValue> values;
    private List<FieldParseInfo> parsers;
    
    public void setValues(final List<IsoValue> values) {
        this.values = values;
    }
    
    public List<IsoValue> getValues() {
        return this.values;
    }
    
    public CompositeField addValue(final IsoValue<?> v) {
        if (this.values == null) {
            this.values = new ArrayList<IsoValue>(4);
        }
        this.values.add(v);
        return this;
    }
    
    public <T> CompositeField addValue(final T val, final CustomField<T> encoder, final IsoType t, final int length) {
        return this.addValue(t.needsLength() ? new IsoValue<Object>(t, val, length, (CustomFieldEncoder<Object>) encoder) : new IsoValue<Object>(t, val, (CustomFieldEncoder<Object>) encoder));
    }
    
    public <T> IsoValue<T> getField(final int idx) {
        if (idx < 0 || idx >= this.values.size()) {
            return null;
        }
        return this.values.get(idx);
    }
    
    public <T> T getObjectValue(final int idx) {
        final IsoValue<T> v = this.getField(idx);
        return (v == null) ? null : v.getValue();
    }
    
    public void setParsers(final List<FieldParseInfo> fpis) {
        this.parsers = fpis;
    }
    
    public List<FieldParseInfo> getParsers() {
        return this.parsers;
    }
    
    public CompositeField addParser(final FieldParseInfo fpi) {
        if (this.parsers == null) {
            this.parsers = new ArrayList<FieldParseInfo>(4);
        }
        this.parsers.add(fpi);
        return this;
    }
    
    @Override
    public CompositeField decodeBinaryField(final byte[] buf, final int offset, final int length) {
        final List<IsoValue> vals = new ArrayList<IsoValue>(this.parsers.size());
        int pos = offset;
        try {
            for (final FieldParseInfo fpi : this.parsers) {
                final IsoValue<?> v = fpi.parseBinary(0, buf, pos, fpi.getDecoder());
                if (v != null) {
                    if (v.getType() == IsoType.NUMERIC || v.getType() == IsoType.DATE10 || v.getType() == IsoType.DATE4 || v.getType() == IsoType.DATE_EXP || v.getType() == IsoType.AMOUNT || v.getType() == IsoType.TIME || v.getType() == IsoType.DATE12 || v.getType() == IsoType.DATE14) {
                        pos += v.getLength() / 2 + v.getLength() % 2;
                    }
                    else {
                        pos += v.getLength();
                    }
                    if (v.getType() == IsoType.LLVAR || v.getType() == IsoType.LLBIN || v.getType() == IsoType.LLBCDBIN) {
                        ++pos;
                    }
                    else if (v.getType() == IsoType.LLLVAR || v.getType() == IsoType.LLLBIN || v.getType() == IsoType.LLLBCDBIN || v.getType() == IsoType.LLLLVAR || v.getType() == IsoType.LLLLBIN || v.getType() == IsoType.LLLLBCDBIN) {
                        pos += 2;
                    }
                    vals.add(v);
                }
            }
            final CompositeField f = new CompositeField();
            f.setValues(vals);
            return f;
        }
        catch (ParseException ex) {
            CompositeField.log.error("Decoding binary CompositeField", (Throwable)ex);
            return null;
        }
        catch (UnsupportedEncodingException ex2) {
            CompositeField.log.error("Decoding binary CompositeField", (Throwable)ex2);
            return null;
        }
    }
    
    @Override
    public CompositeField decodeField(final String value) {
        final List<IsoValue> vals = new ArrayList<IsoValue>(this.parsers.size());
        final byte[] buf = value.getBytes();
        int pos = 0;
        try {
            for (final FieldParseInfo fpi : this.parsers) {
                final IsoValue<?> v = fpi.parse(0, buf, pos, fpi.getDecoder());
                if (v != null) {
                    pos += v.toString().getBytes(fpi.getCharacterEncoding()).length;
                    if (v.getType() == IsoType.LLVAR || v.getType() == IsoType.LLBIN || v.getType() == IsoType.LLBCDBIN) {
                        pos += 2;
                    }
                    else if (v.getType() == IsoType.LLLVAR || v.getType() == IsoType.LLLBIN || v.getType() == IsoType.LLLBCDBIN) {
                        pos += 3;
                    }
                    else if (v.getType() == IsoType.LLLLBIN || v.getType() == IsoType.LLLLBCDBIN || v.getType() == IsoType.LLLLVAR) {
                        pos += 4;
                    }
                    vals.add(v);
                }
            }
            final CompositeField f = new CompositeField();
            f.setValues(vals);
            return f;
        }
        catch (ParseException | UnsupportedEncodingException ex3) {
            final Exception ex2 = null;
            final Exception ex = ex2;
            CompositeField.log.error("Decoding CompositeField", (Throwable)ex);
            return null;
        }
    }
    
    @Override
    public byte[] encodeBinaryField(final CompositeField value) {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            for (final IsoValue<?> v : value.getValues()) {
                v.write(bout, true, true);
            }
        }
        catch (IOException ex) {
            CompositeField.log.error("Encoding binary CompositeField", (Throwable)ex);
        }
        return bout.toByteArray();
    }
    
    @Override
    public String encodeField(final CompositeField value) {
        try {
            String encoding = null;
            final ByteArrayOutputStream bout = new ByteArrayOutputStream();
            for (final IsoValue<?> v : value.getValues()) {
                v.write(bout, false, true);
                if (encoding == null) {
                    encoding = v.getCharacterEncoding();
                }
            }
            final byte[] buf = bout.toByteArray();
            return new String(buf, (encoding == null) ? "UTF-8" : encoding);
        }
        catch (IOException ex) {
            CompositeField.log.error("Encoding text CompositeField", (Throwable)ex);
            return "";
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CompositeField[");
        if (this.values != null) {
            boolean first = true;
            for (final IsoValue<?> v : this.values) {
                if (first) {
                    first = false;
                }
                else {
                    sb.append(',');
                }
                sb.append(v.getType());
            }
        }
        return sb.append(']').toString();
    }
    
    static {
        log = LoggerFactory.getLogger((Class)CompositeField.class);
    }
}
