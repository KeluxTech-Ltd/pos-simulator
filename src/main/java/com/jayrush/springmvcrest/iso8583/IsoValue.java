// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.iso8583;

import com.jayrush.springmvcrest.iso8583.util.Bcd;
import com.jayrush.springmvcrest.iso8583.util.HexCodec;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.TimeZone;

public class IsoValue<T> implements Cloneable
{
    private IsoType type;
    private T value;
    private CustomFieldEncoder<T> encoder;
    private int length;
    private String encoding;
    private TimeZone tz;
    
    public IsoValue(final IsoType t, final T value) {
        this(t, value, null);
    }
    
    public IsoValue(final IsoType t, final T value, final CustomFieldEncoder<T> custom) {
        if (t.needsLength()) {
            throw new IllegalArgumentException("Fixed-value types must use constructor that specifies length");
        }
        this.encoder = custom;
        this.type = t;
        this.value = value;
        if (this.type == IsoType.LLVAR || this.type == IsoType.LLLVAR || this.type == IsoType.LLLLVAR) {
            if (custom == null) {
                this.length = value.toString().length();
            }
            else {
                String enc = custom.encodeField(value);
                if (enc == null) {
                    enc = ((value == null) ? "" : value.toString());
                }
                this.length = enc.length();
            }
            this.validateTypeWithVariableLength();
        }
        else if (this.type == IsoType.LLBIN || this.type == IsoType.LLLBIN || this.type == IsoType.LLLLBIN) {
            if (custom == null) {
                if (value instanceof byte[]) {
                    this.length = ((byte[])(Object)value).length;
                }
                else {
                    this.length = value.toString().length() / 2 + value.toString().length() % 2;
                }
            }
            else if (custom instanceof CustomBinaryField) {
                this.length = ((CustomBinaryField)custom).encodeBinaryField(value).length;
            }
            else {
                String enc = custom.encodeField(value);
                if (enc == null) {
                    enc = ((value == null) ? "" : value.toString());
                }
                this.length = enc.length();
            }
            this.validateTypeWithVariableLength();
        }
        else if (this.type == IsoType.LLBCDBIN || this.type == IsoType.LLLBCDBIN || this.type == IsoType.LLLLBCDBIN) {
            if (value instanceof byte[]) {
                this.length = ((byte[])(Object)value).length * 2;
            }
            else {
                this.length = value.toString().length();
            }
            this.validateTypeWithVariableLength();
        }
        else {
            this.length = this.type.getLength();
        }
    }
    
    public IsoValue(final IsoType t, final T val, final int len) {
        this(t, val, len, null);
    }
    
    public IsoValue(final IsoType t, final T val, final int len, final CustomFieldEncoder<T> custom) {
        this.type = t;
        this.value = val;
        this.length = len;
        this.encoder = custom;
        if (this.length == 0 && t.needsLength()) {
            throw new IllegalArgumentException(String.format("Length must be greater than zero for type %s (value '%s')", t, val));
        }
        if (t == IsoType.LLVAR || t == IsoType.LLLVAR || t == IsoType.LLLLVAR) {
            if (len == 0) {
                this.length = ((custom == null) ? val.toString().length() : custom.encodeField(this.value).length());
            }
            this.validateTypeWithVariableLength();
        }
        else if (t == IsoType.LLBIN || t == IsoType.LLLBIN || t == IsoType.LLLLBIN) {
            if (len == 0) {
                if (custom == null) {
                    this.length = ((byte[])(Object)val).length;
                }
                else if (custom instanceof CustomBinaryField) {
                    this.length = ((CustomBinaryField)custom).encodeBinaryField(this.value).length;
                }
                else {
                    this.length = custom.encodeField(this.value).length();
                }
                this.length = ((custom == null) ? ((byte[])(Object)val).length : custom.encodeField(this.value).length());
            }
            this.validateTypeWithVariableLength();
        }
        else if (t == IsoType.LLBCDBIN || t == IsoType.LLLBCDBIN || t == IsoType.LLLLBCDBIN) {
            if (len == 0) {
                if (this.value instanceof byte[]) {
                    this.length = ((byte[])(Object)this.value).length * 2;
                }
                else {
                    this.length = this.value.toString().length();
                }
            }
            this.validateTypeWithVariableLength();
        }
    }
    
    public IsoType getType() {
        return this.type;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public T getValue() {
        return this.value;
    }
    
    public void setCharacterEncoding(final String value) {
        this.encoding = value;
    }
    
    public String getCharacterEncoding() {
        return this.encoding;
    }
    
    public void setTimeZone(final TimeZone value) {
        this.tz = value;
    }
    
    public TimeZone getTimeZone() {
        return this.tz;
    }
    
    @Override
    public String toString() {
        if (this.value == null) {
            return "ISOValue<null>";
        }
        if (this.type == IsoType.NUMERIC || this.type == IsoType.AMOUNT) {
            if (this.type == IsoType.AMOUNT) {
                if (this.value instanceof BigDecimal) {
                    return this.type.format((BigDecimal)this.value, 12);
                }
                return this.type.format(this.value.toString(), 12);
            }
            else {
                if (this.value instanceof BigInteger) {
                    return this.type.format((this.encoder == null) ? this.value.toString() : this.encoder.encodeField(this.value), this.length);
                }
                if (this.value instanceof Number) {
                    return this.type.format(((Number)this.value).longValue(), this.length);
                }
                return this.type.format((this.encoder == null) ? this.value.toString() : this.encoder.encodeField(this.value), this.length);
            }
        }
        else {
            if (this.type == IsoType.ALPHA) {
                return this.type.format((this.encoder == null) ? this.value.toString() : this.encoder.encodeField(this.value), this.length);
            }
            if (this.type == IsoType.LLVAR || this.type == IsoType.LLLVAR || this.type == IsoType.LLLLVAR) {
                return this.getStringEncoded();
            }
            if (this.value instanceof Date) {
                return this.type.format((Date)this.value, this.tz);
            }
            if (this.type == IsoType.BINARY) {
                if (this.value instanceof byte[]) {
                    final byte[] _v = (byte[]) this.value;
                    return this.type.format((this.encoder == null) ? HexCodec.hexEncode(_v, 0, _v.length) : this.encoder.encodeField(this.value), this.length * 2);
                }
                return this.type.format((this.encoder == null) ? this.value.toString() : this.encoder.encodeField(this.value), this.length * 2);
            }
            else if (this.type == IsoType.LLBIN || this.type == IsoType.LLLBIN || this.type == IsoType.LLLLBIN) {
                if (this.value instanceof byte[]) {
                    final byte[] _v = (byte[]) this.value;
                    return (this.encoder == null) ? HexCodec.hexEncode(_v, 0, _v.length) : this.encoder.encodeField(this.value);
                }
                final String _s = this.getStringEncoded();
                return (_s.length() % 2 == 1) ? String.format("0%s", _s) : _s;
            }
            else {
                if (this.type != IsoType.LLBCDBIN && this.type != IsoType.LLLBCDBIN && this.type != IsoType.LLLLBCDBIN) {
                    return this.getStringEncoded();
                }
                if (this.value instanceof byte[]) {
                    final byte[] _v = (byte[]) this.value;
                    final String val = (this.encoder == null) ? HexCodec.hexEncode(_v, 0, _v.length) : this.encoder.encodeField(this.value);
                    return val.substring(val.length() - this.length);
                }
                return this.getStringEncoded();
            }
        }
    }
    
    private String getStringEncoded() {
        return (this.encoder == null) ? this.value.toString() : this.encoder.encodeField(this.value);
    }
    
    public IsoValue<T> clone() {
        try {
            return (IsoValue)super.clone();
        }
        catch (CloneNotSupportedException ex) {
            return null;
        }
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other == null || !(other instanceof IsoValue)) {
            return false;
        }
        final IsoValue<?> comp = (IsoValue<?>)other;
        return comp.getType() == this.getType() && comp.getValue().equals(this.getValue()) && comp.getLength() == this.getLength();
    }
    
    @Override
    public int hashCode() {
        return (this.value == null) ? 0 : this.toString().hashCode();
    }
    
    public CustomFieldEncoder<T> getEncoder() {
        return this.encoder;
    }
    
    protected void writeLengthHeader(final int l, final OutputStream outs, final IsoType type, final boolean binary, final boolean forceStringEncoding) throws IOException {
        int digits;
        if (type == IsoType.LLLLBIN || type == IsoType.LLLLVAR || type == IsoType.LLLLBCDBIN) {
            digits = 4;
        }
        else if (type == IsoType.LLLBIN || type == IsoType.LLLVAR || type == IsoType.LLLBCDBIN) {
            digits = 3;
        }
        else {
            digits = 2;
        }
        if (binary) {
            if (digits == 4) {
                outs.write(l % 10000 / 1000 << 4 | l % 1000 / 100);
            }
            else if (digits == 3) {
                outs.write(l / 100);
            }
            outs.write(l % 100 / 10 << 4 | l % 10);
        }
        else if (forceStringEncoding) {
            String lhead = Integer.toString(l);
            final int ldiff = digits - lhead.length();
            if (ldiff == 1) {
                lhead = '0' + lhead;
            }
            else if (ldiff == 2) {
                lhead = "00" + lhead;
            }
            else if (ldiff == 3) {
                lhead = "000" + lhead;
            }
            outs.write((this.encoding == null) ? lhead.getBytes() : lhead.getBytes(this.encoding));
        }
        else {
            if (digits == 4) {
                outs.write(l / 1000 + 48);
                outs.write(l % 1000 / 100 + 48);
            }
            else if (digits == 3) {
                outs.write(l / 100 + 48);
            }
            if (l >= 10) {
                outs.write(l % 100 / 10 + 48);
            }
            else {
                outs.write(48);
            }
            outs.write(l % 10 + 48);
        }
    }
    
    public void write(final OutputStream outs, final boolean binary, final boolean forceStringEncoding) throws IOException {
        if (this.type == IsoType.LLLVAR || this.type == IsoType.LLVAR || this.type == IsoType.LLLLVAR) {
            this.writeLengthHeader(this.length, outs, this.type, binary, forceStringEncoding);
        }
        else if (this.type == IsoType.LLBIN || this.type == IsoType.LLLBIN || this.type == IsoType.LLLLBIN) {
            this.writeLengthHeader(binary ? this.length : (this.length * 2), outs, this.type, binary, forceStringEncoding);
        }
        else if (this.type == IsoType.LLBCDBIN || this.type == IsoType.LLLBCDBIN || this.type == IsoType.LLLLBCDBIN) {
            this.writeLengthHeader(this.length, outs, this.type, binary, forceStringEncoding);
        }
        else if (binary) {
            byte[] buf = null;
            if (this.type == IsoType.NUMERIC) {
                buf = new byte[this.length / 2 + this.length % 2];
            }
            else if (this.type == IsoType.AMOUNT) {
                buf = new byte[6];
            }
            else if (this.type == IsoType.DATE10 || this.type == IsoType.DATE4 || this.type == IsoType.DATE_EXP || this.type == IsoType.TIME || this.type == IsoType.DATE12 || this.type == IsoType.DATE14) {
                buf = new byte[this.length / 2];
            }
            if (buf != null) {
                Bcd.encode(this.toString(), buf);
                outs.write(buf);
                return;
            }
        }
        if (binary && (this.type == IsoType.BINARY || IsoType.VARIABLE_LENGTH_BIN_TYPES.contains(this.type))) {
            int missing;
            if (this.value instanceof byte[]) {
                outs.write((byte[])(Object)this.value);
                missing = this.length - ((byte[])(Object)this.value).length;
            }
            else if (this.encoder instanceof CustomBinaryField) {
                final byte[] binval = ((CustomBinaryField)this.encoder).encodeBinaryField(this.value);
                outs.write(binval);
                missing = this.length - binval.length;
            }
            else {
                final byte[] binval = HexCodec.hexDecode(this.value.toString());
                outs.write(binval);
                missing = this.length - binval.length;
            }
            if (this.type == IsoType.BINARY && missing > 0) {
                for (int i = 0; i < missing; ++i) {
                    outs.write(0);
                }
            }
        }
        else {
            outs.write((this.encoding == null) ? this.toString().getBytes() : this.toString().getBytes(this.encoding));
        }
    }
    
    private void validateTypeWithVariableLength() {
        if (this.type == IsoType.LLVAR && this.length > 99) {
            this.throwIllegalArgumentException(this.type, 99);
        }
        else if (this.type == IsoType.LLLVAR && this.length > 999) {
            this.throwIllegalArgumentException(this.type, 999);
        }
        else if (this.type == IsoType.LLLLVAR && this.length > 9999) {
            this.throwIllegalArgumentException(this.type, 9999);
        }
        else if (this.type == IsoType.LLBIN && this.length > 99) {
            this.throwIllegalArgumentException(this.type, 99);
        }
        else if (this.type == IsoType.LLLBIN && this.length > 999) {
            this.throwIllegalArgumentException(this.type, 999);
        }
        else if (this.type == IsoType.LLLLBIN && this.length > 9999) {
            this.throwIllegalArgumentException(this.type, 9999);
        }
        else if (this.type == IsoType.LLBCDBIN && this.length > 50) {
            this.throwIllegalArgumentException(this.type, 50);
        }
        else if (this.type == IsoType.LLLBCDBIN && this.length > 500) {
            this.throwIllegalArgumentException(this.type, 500);
        }
        else if (this.type == IsoType.LLLLBCDBIN && this.length > 5000) {
            this.throwIllegalArgumentException(this.type, 5000);
        }
    }
    
    private void throwIllegalArgumentException(final IsoType t, final int maxLength) {
        throw new IllegalArgumentException(t.name() + " can only hold values up to " + maxLength + " chars");
    }
}
