// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.iso8583;

import com.jayrush.springmvcrest.iso8583.util.HexCodec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.Map;

public class IsoMessage
{
    static final byte[] HEX;
    private int type;
    private boolean binaryHeader;
    private boolean binaryFields;
    private IsoValue[] fields;
    private String isoHeader;
    private byte[] binIsoHeader;
    private int etx;
    private boolean forceb2;
    private boolean binBitmap;
    private boolean forceStringEncoding;
    private String encoding;
    
    public IsoMessage() {
        this.fields = new IsoValue[129];
        this.etx = -1;
        this.encoding = System.getProperty("file.encoding");
    }
    
    protected IsoMessage(final String header) {
        this.fields = new IsoValue[129];
        this.etx = -1;
        this.encoding = System.getProperty("file.encoding");
        this.isoHeader = header;
    }
    
    protected IsoMessage(final byte[] binaryHeader) {
        this.fields = new IsoValue[129];
        this.etx = -1;
        this.encoding = System.getProperty("file.encoding");
        this.binIsoHeader = binaryHeader;
    }
    
    public void setBinaryBitmap(final boolean flag) {
        this.binBitmap = flag;
    }
    
    public boolean isBinaryBitmap() {
        return this.binBitmap;
    }
    
    public void setForceSecondaryBitmap(final boolean flag) {
        this.forceb2 = flag;
    }
    
    public boolean getForceSecondaryBitmap() {
        return this.forceb2;
    }
    
    public void setCharacterEncoding(final String value) {
        if (value == null) {
            throw new IllegalArgumentException("Cannot set null encoding.");
        }
        this.encoding = value;
    }
    
    public String getCharacterEncoding() {
        return this.encoding;
    }
    
    public void setForceStringEncoding(final boolean flag) {
        this.forceStringEncoding = flag;
    }
    
    public void setIsoHeader(final String value) {
        this.isoHeader = value;
        this.binIsoHeader = null;
    }
    
    public String getIsoHeader() {
        return this.isoHeader;
    }
    
    public void setBinaryIsoHeader(final byte[] binaryHeader) {
        this.isoHeader = null;
        this.binIsoHeader = binaryHeader;
    }
    
    public byte[] getBinaryIsoHeader() {
        return this.binIsoHeader;
    }
    
    public void setType(final int value) {
        this.type = value;
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setBinary(final boolean flag) {
        this.binaryFields = flag;
        this.binaryHeader = flag;
    }
    
    @Deprecated
    public boolean isBinary() {
        return this.binaryHeader && this.binaryFields;
    }
    
    public void setBinaryHeader(final boolean flag) {
        this.binaryHeader = flag;
    }
    
    public boolean isBinaryHeader() {
        return this.binaryHeader;
    }
    
    public void setBinaryFields(final boolean flag) {
        this.binaryFields = flag;
    }
    
    public boolean isBinaryFields() {
        return this.binaryFields;
    }
    
    public void setEtx(final int value) {
        this.etx = value;
    }
    
    public <T> T getObjectValue(final int field) {
        final IsoValue<T> v = (IsoValue<T>)this.fields[field];
        return (v == null) ? null : v.getValue();
    }
    
    public <T> IsoValue<T> getField(final int field) {
        return (IsoValue<T>)this.fields[field];
    }
    
    public IsoMessage setField(final int index, final IsoValue<?> field) {
        if (index < 2 || index > 128) {
            throw new IndexOutOfBoundsException("Field index must be between 2 and 128");
        }
        if (field != null) {
            field.setCharacterEncoding(this.encoding);
        }
        this.fields[index] = field;
        return this;
    }
    
    public IsoMessage setFields(final Map<Integer, IsoValue<?>> values) {
        for (final Map.Entry<Integer, IsoValue<?>> e : values.entrySet()) {
            this.setField(e.getKey(), e.getValue());
        }
        return this;
    }
    
    public IsoMessage setValue(final int index, final Object value, final IsoType t, final int length) {
        return this.setValue(index, value, null, t, length);
    }
    
    public <T> IsoMessage setValue(final int index, final T value, final CustomFieldEncoder<T> encoder, final IsoType t, final int length) {
        if (index < 2 || index > 128) {
            throw new IndexOutOfBoundsException("Field index must be between 2 and 128");
        }
        if (value == null) {
            this.fields[index] = null;
        }
        else {
            IsoValue<T> v = null;
            if (t.needsLength()) {
                v = new IsoValue<T>(t, value, length, encoder);
            }
            else {
                v = new IsoValue<T>(t, value, encoder);
            }
            v.setCharacterEncoding(this.encoding);
            this.fields[index] = v;
        }
        return this;
    }
    
    public <T> IsoMessage updateValue(final int index, final T value) {
        final IsoValue<T> current = this.getField(index);
        if (current == null) {
            throw new IllegalArgumentException("Value-only field setter can only be used on existing fields");
        }
        this.setValue(index, value, current.getEncoder(), current.getType(), current.getLength());
        this.getField(index).setCharacterEncoding(current.getCharacterEncoding());
        this.getField(index).setTimeZone(current.getTimeZone());
        return this;
    }
    
    public boolean hasField(final int idx) {
        return this.fields[idx] != null;
    }
    
    public void write(final OutputStream outs, final int lengthBytes) throws IOException {
        if (lengthBytes > 4) {
            throw new IllegalArgumentException("The length header can have at most 4 bytes");
        }
        final byte[] data = this.writeData();
        if (lengthBytes > 0) {
            int l = data.length;
            if (this.etx > -1) {
                ++l;
            }
            final byte[] buf = new byte[lengthBytes];
            int pos = 0;
            if (lengthBytes == 4) {
                buf[0] = (byte)((l & 0xFF000000) >> 24);
                ++pos;
            }
            if (lengthBytes > 2) {
                buf[pos] = (byte)((l & 0xFF0000) >> 16);
                ++pos;
            }
            if (lengthBytes > 1) {
                buf[pos] = (byte)((l & 0xFF00) >> 8);
                ++pos;
            }
            buf[pos] = (byte)(l & 0xFF);
            outs.write(buf);
        }
        outs.write(data);
        if (this.etx > -1) {
            outs.write(this.etx);
        }
        outs.flush();
    }
    
    public ByteBuffer writeToBuffer(final int lengthBytes) {
        if (lengthBytes > 4) {
            throw new IllegalArgumentException("The length header can have at most 4 bytes");
        }
        final byte[] data = this.writeData();
        final ByteBuffer buf = ByteBuffer.allocate(lengthBytes + data.length + ((this.etx > -1) ? 1 : 0));
        if (lengthBytes > 0) {
            int l = data.length;
            if (this.etx > -1) {
                ++l;
            }
            if (lengthBytes == 4) {
                buf.put((byte)((l & 0xFF000000) >> 24));
            }
            if (lengthBytes > 2) {
                buf.put((byte)((l & 0xFF0000) >> 16));
            }
            if (lengthBytes > 1) {
                buf.put((byte)((l & 0xFF00) >> 8));
            }
            buf.put((byte)(l & 0xFF));
        }
        buf.put(data);
        if (this.etx > -1) {
            buf.put((byte)this.etx);
        }
        buf.flip();
        return buf;
    }
    
    protected BitSet createBitmapBitSet() {
        BitSet bs = new BitSet(this.forceb2 ? 128 : 64);
        for (int i = 2; i < 129; ++i) {
            if (this.fields[i] != null) {
                bs.set(i - 1);
            }
        }
        if (this.forceb2) {
            bs.set(0);
        }
        else if (bs.length() > 64) {
            final BitSet b2 = new BitSet(128);
            b2.or(bs);
            bs = b2;
            bs.set(0);
        }
        return bs;
    }
    
    public byte[] writeData() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        if (this.isoHeader != null) {
            try {
                bout.write(this.isoHeader.getBytes(this.encoding));
            }
            catch (IOException ex) {}
        }
        else if (this.binIsoHeader != null) {
            try {
                bout.write(this.binIsoHeader);
            }
            catch (IOException ex2) {}
        }
        if (this.binaryHeader) {
            bout.write((this.type & 0xFF00) >> 8);
            bout.write(this.type & 0xFF);
        }
        else {
            try {
                bout.write(String.format("%04x", this.type).getBytes(this.encoding));
            }
            catch (IOException ex3) {}
        }
        final BitSet bs = this.createBitmapBitSet();
        if (this.binaryHeader || this.binBitmap) {
            int pos = 128;
            int b = 0;
            for (int i = 0; i < bs.size(); ++i) {
                if (bs.get(i)) {
                    b |= pos;
                }
                pos >>= 1;
                if (pos == 0) {
                    bout.write(b);
                    pos = 128;
                    b = 0;
                }
            }
        }
        else {
            ByteArrayOutputStream bout2 = null;
            if (this.forceStringEncoding) {
                bout2 = bout;
                bout = new ByteArrayOutputStream();
            }
            int pos2 = 0;
            for (int lim = bs.size() / 4, j = 0; j < lim; ++j) {
                int nibble = 0;
                if (bs.get(pos2++)) {
                    nibble |= 0x8;
                }
                if (bs.get(pos2++)) {
                    nibble |= 0x4;
                }
                if (bs.get(pos2++)) {
                    nibble |= 0x2;
                }
                if (bs.get(pos2++)) {
                    nibble |= 0x1;
                }
                bout.write(IsoMessage.HEX[nibble]);
            }
            if (this.forceStringEncoding) {
                final String _hb = new String(bout.toByteArray());
                bout = bout2;
                try {
                    bout.write(_hb.getBytes(this.encoding));
                }
                catch (IOException ex4) {}
            }
        }
        for (int k = 2; k < 129; ++k) {
            final IsoValue<?> v = (IsoValue<?>)this.fields[k];
            if (v != null) {
                try {
                    v.write(bout, this.binaryFields, this.forceStringEncoding);
                }
                catch (IOException ex5) {}
            }
        }
        return bout.toByteArray();
    }
    
    public String debugString() {
        final StringBuilder sb = new StringBuilder();
        if (this.isoHeader != null) {
            sb.append(this.isoHeader);
        }
        else if (this.binIsoHeader != null) {
            sb.append("[0x").append(HexCodec.hexEncode(this.binIsoHeader, 0, this.binIsoHeader.length)).append("]");
        }
        sb.append(String.format("%04x", this.type));
        final BitSet bs = this.createBitmapBitSet();
        int pos = 0;
        for (int lim = bs.size() / 4, i = 0; i < lim; ++i) {
            int nibble = 0;
            if (bs.get(pos++)) {
                nibble |= 0x8;
            }
            if (bs.get(pos++)) {
                nibble |= 0x4;
            }
            if (bs.get(pos++)) {
                nibble |= 0x2;
            }
            if (bs.get(pos++)) {
                nibble |= 0x1;
            }
            sb.append(new String(IsoMessage.HEX, nibble, 1));
        }
        for (int i = 2; i < 129; ++i) {
            final IsoValue<?> v = (IsoValue<?>)this.fields[i];
            if (v != null) {
                final String desc = v.toString();
                if (v.getType() == IsoType.LLBIN || v.getType() == IsoType.LLBCDBIN || v.getType() == IsoType.LLVAR) {
                    sb.append(String.format("%02d", desc.length()));
                }
                else if (v.getType() == IsoType.LLLBIN || v.getType() == IsoType.LLLBCDBIN || v.getType() == IsoType.LLLVAR) {
                    sb.append(String.format("%03d", desc.length()));
                }
                else if (v.getType() == IsoType.LLLLBIN || v.getType() == IsoType.LLLLBCDBIN || v.getType() == IsoType.LLLLVAR) {
                    sb.append(String.format("%04d", desc.length()));
                }
                sb.append(desc);
            }
        }
        return sb.toString();
    }
    
    public <T> void putAt(final int i, final IsoValue<T> v) {
        this.setField(i, v);
    }
    
    public <T> IsoValue<T> getAt(final int i) {
        return (IsoValue<T>)this.getField(i);
    }
    
    public <T> void update(final int i, final IsoValue<T> v) {
        this.setField(i, v);
    }
    
    public <T> IsoValue<T> apply(final int i) {
        return (IsoValue<T>)this.getField(i);
    }
    
    public void copyFieldsFrom(final IsoMessage src, final int... idx) {
        for (final int i : idx) {
            final IsoValue<Object> v = src.getField(i);
            if (v != null) {
                this.setValue(i, v.getValue(), v.getEncoder(), v.getType(), v.getLength());
            }
        }
    }
    
    public void removeFields(final int... idx) {
        for (final int i : idx) {
            this.setField(i, null);
        }
    }
    
    public boolean hasEveryField(final int... idx) {
        for (final int i : idx) {
            if (!this.hasField(i)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean hasAnyField(final int... idx) {
        for (final int i : idx) {
            if (this.hasField(i)) {
                return true;
            }
        }
        return false;
    }
    
    static {
        HEX = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
    }
}
