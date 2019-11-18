// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.iso8583;

import com.jayrush.springmvcrest.iso8583.parse.ConfigParser;
import com.jayrush.springmvcrest.iso8583.parse.DateTimeParseInfo;
import com.jayrush.springmvcrest.iso8583.parse.FieldParseInfo;
import com.jayrush.springmvcrest.slf4j.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;


public class MessageFactory<T extends IsoMessage>
{
    protected final Logger log;
    private Map<Integer, T> typeTemplates;
    protected Map<Integer, Map<Integer, FieldParseInfo>> parseMap;
    protected Map<Integer, List<Integer>> parseOrder;
    private TraceNumberGenerator traceGen;
    private Map<Integer, String> isoHeaders;
    private Map<Integer, byte[]> binIsoHeaders;
    private Map<Integer, CustomField> customFields;
    private boolean setDate;
    private boolean binaryHeader;
    private boolean binaryFields;
    private int etx;
    private boolean ignoreLast;
    private boolean forceb2;
    private boolean binBitmap;
    private boolean forceStringEncoding;
    private String encoding;
    
    public MessageFactory() {
        this.log = LoggerFactory.getLogger((Class)this.getClass());
        this.typeTemplates = new HashMap<Integer, T>();
        this.parseMap = new HashMap<Integer, Map<Integer, FieldParseInfo>>();
        this.parseOrder = new HashMap<Integer, List<Integer>>();
        this.isoHeaders = new HashMap<Integer, String>();
        this.binIsoHeaders = new HashMap<Integer, byte[]>();
        this.customFields = new HashMap<Integer, CustomField>();
        this.etx = -1;
        this.encoding = System.getProperty("file.encoding");
    }
    
    public void setForceStringEncoding(final boolean flag) {
        this.forceStringEncoding = flag;
        for (final Map<Integer, FieldParseInfo> pm : this.parseMap.values()) {
            for (final FieldParseInfo parser : pm.values()) {
                parser.setForceStringDecoding(flag);
            }
        }
    }
    
    public boolean isForceStringEncoding() {
        return this.forceStringEncoding;
    }
    
    public void setUseBinaryBitmap(final boolean flag) {
        this.binBitmap = flag;
    }
    
    public boolean isUseBinaryBitmap() {
        return this.binBitmap;
    }
    
    public void setCharacterEncoding(final String value) {
        if (this.encoding == null) {
            throw new IllegalArgumentException("Cannot set null encoding.");
        }
        this.encoding = value;
        if (!this.parseMap.isEmpty()) {
            for (final Map<Integer, FieldParseInfo> pt : this.parseMap.values()) {
                for (final FieldParseInfo fpi : pt.values()) {
                    fpi.setCharacterEncoding(this.encoding);
                }
            }
        }
        if (!this.typeTemplates.isEmpty()) {
            for (final T tmpl : this.typeTemplates.values()) {
                tmpl.setCharacterEncoding(this.encoding);
                for (int i = 2; i < 129; ++i) {
                    final IsoValue<?> v = tmpl.getField(i);
                    if (v != null) {
                        v.setCharacterEncoding(this.encoding);
                    }
                }
            }
        }
    }
    
    public String getCharacterEncoding() {
        return this.encoding;
    }
    
    public void setForceSecondaryBitmap(final boolean flag) {
        this.forceb2 = flag;
    }
    
    public boolean isForceSecondaryBitmap() {
        return this.forceb2;
    }
    
    public void setIgnoreLastMissingField(final boolean flag) {
        this.ignoreLast = flag;
    }
    
    public boolean getIgnoreLastMissingField() {
        return this.ignoreLast;
    }
    
    public void setCustomFields(final Map<Integer, CustomField> value) {
        this.customFields = value;
    }
    
    public void setCustomField(final int index, final CustomField<?> value) {
        this.customFields.put(index, value);
    }
    
    public <F> CustomField<F> getCustomField(final int index) {
        return this.customFields.get(index);
    }
    
    public <F> CustomField<F> getCustomField(final Integer index) {
        return this.customFields.get(index);
    }
    
    public void setConfigPath(final String path) throws IOException {
        ConfigParser.configureFromClasspathConfig((MessageFactory<IsoMessage>)this, path);
        this.setCharacterEncoding(this.encoding);
        this.setForceStringEncoding(this.forceStringEncoding);
    }
    
    public void setUseBinaryMessages(final boolean flag) {
        this.binaryFields = flag;
        this.binaryHeader = flag;
    }
    
    @Deprecated
    public boolean getUseBinaryMessages() {
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
    
    public int getEtx() {
        return this.etx;
    }
    
    public T newMessage(final int type) {
        T m;
        if (this.binIsoHeaders.get(type) != null) {
            m = this.createIsoMessageWithBinaryHeader(this.binIsoHeaders.get(type));
        }
        else {
            m = this.createIsoMessage(this.isoHeaders.get(type));
        }
        m.setType(type);
        m.setEtx(this.etx);
        m.setBinaryHeader(this.isBinaryHeader());
        m.setBinaryFields(this.isBinaryFields());
        m.setForceSecondaryBitmap(this.forceb2);
        m.setBinaryBitmap(this.binBitmap);
        m.setCharacterEncoding(this.encoding);
        m.setForceStringEncoding(this.forceStringEncoding);
        final IsoMessage templ = this.typeTemplates.get(type);
        if (templ != null) {
            for (int i = 2; i <= 128; ++i) {
                if (templ.hasField(i)) {
                    m.setField(i, templ.getField(i).clone());
                }
            }
        }
        if (this.traceGen != null) {
            m.setValue(11, this.traceGen.nextTrace(), IsoType.NUMERIC, 6);
        }
        if (this.setDate) {
            if (m.hasField(7)) {
                m.updateValue(7, new Date());
            }
            else {
                final IsoValue<Date> now = new IsoValue<Date>(IsoType.DATE10, new Date());
                if (DateTimeParseInfo.getDefaultTimeZone() != null) {
                    now.setTimeZone(DateTimeParseInfo.getDefaultTimeZone());
                }
                m.setField(7, now);
            }
        }
        return m;
    }
    
    public T createResponse(final T request) {
        final T resp = this.createIsoMessage(this.isoHeaders.get(request.getType() + 16));
        resp.setCharacterEncoding(request.getCharacterEncoding());
        resp.setBinaryHeader(request.isBinaryHeader());
        resp.setBinaryFields(request.isBinaryFields());
        resp.setBinaryBitmap(request.isBinaryBitmap());
        resp.setType(request.getType() + 16);
        resp.setEtx(this.etx);
        resp.setForceSecondaryBitmap(this.forceb2);
        final IsoMessage templ = this.typeTemplates.get(resp.getType());
        if (templ == null) {
            for (int i = 2; i < 128; ++i) {
                if (request.hasField(i)) {
                    resp.setField(i, request.getField(i).clone());
                }
            }
        }
        else {
            for (int i = 2; i < 128; ++i) {
                if (request.hasField(i)) {
                    resp.setField(i, request.getField(i).clone());
                }
                else if (templ.hasField(i)) {
                    resp.setField(i, templ.getField(i).clone());
                }
            }
        }
        return resp;
    }
    
    public void setTimezoneForParseGuide(final int messageType, final int field, final TimeZone tz) {
        if (field == 0) {
            DateTimeParseInfo.setDefaultTimeZone(tz);
        }
        final Map<Integer, FieldParseInfo> guide = this.parseMap.get(messageType);
        if (guide != null) {
            final FieldParseInfo fpi = guide.get(field);
            if (fpi instanceof DateTimeParseInfo) {
                ((DateTimeParseInfo)fpi).setTimeZone(tz);
                return;
            }
        }
        this.log.warn("Field {} for message type {} is not for dates, cannot set timezone", (Object)field, (Object)messageType);
    }
    
    public T parseMessage(final byte[] buf, final int isoHeaderLength) throws ParseException, UnsupportedEncodingException {
        return this.parseMessage(buf, isoHeaderLength, false);
    }
    
    public T parseMessage(final byte[] buf, final int isoHeaderLength, final boolean binaryIsoHeader) throws ParseException, UnsupportedEncodingException {
        final int minlength = isoHeaderLength + (this.binaryHeader ? 2 : 4) + ((this.binBitmap || this.binaryHeader) ? 8 : 16);
        if (buf.length < minlength) {
            throw new ParseException("Insufficient buffer length, needs to be at least " + minlength, 0);
        }
        T m;
        if (binaryIsoHeader && isoHeaderLength > 0) {
            final byte[] _bih = new byte[isoHeaderLength];
            System.arraycopy(buf, 0, _bih, 0, isoHeaderLength);
            m = this.createIsoMessageWithBinaryHeader(_bih);
        }
        else {
            m = this.createIsoMessage((isoHeaderLength > 0) ? new String(buf, 0, isoHeaderLength, this.encoding) : null);
        }
        m.setCharacterEncoding(this.encoding);
        int type;
        if (this.binaryHeader) {
            type = ((buf[isoHeaderLength] & 0xFF) << 8 | (buf[isoHeaderLength + 1] & 0xFF));
        }
        else if (this.forceStringEncoding) {
            type = Integer.parseInt(new String(buf, isoHeaderLength, 4, this.encoding), 16);
        }
        else {
            type = (buf[isoHeaderLength] - 48 << 12 | buf[isoHeaderLength + 1] - 48 << 8 | buf[isoHeaderLength + 2] - 48 << 4 | buf[isoHeaderLength + 3] - 48);
        }
        m.setType(type);
        final BitSet bs = new BitSet(64);
        int pos = 0;
        if (this.binaryHeader || this.binBitmap) {
            int i;
            int bitmapStart;
            for (bitmapStart = (i = isoHeaderLength + (this.binaryHeader ? 2 : 4)); i < 8 + bitmapStart; ++i) {
                int bit = 128;
                for (int b = 0; b < 8; ++b) {
                    bs.set(pos++, (buf[i] & bit) != 0x0);
                    bit >>= 1;
                }
            }
            if (bs.get(0)) {
                if (buf.length < minlength + 8) {
                    throw new ParseException("Insufficient length for secondary bitmap", minlength);
                }
                for (i = 8 + bitmapStart; i < 16 + bitmapStart; ++i) {
                    int bit = 128;
                    for (int b = 0; b < 8; ++b) {
                        bs.set(pos++, (buf[i] & bit) != 0x0);
                        bit >>= 1;
                    }
                }
                pos = minlength + 8;
            }
            else {
                pos = minlength;
            }
        }
        else {
            try {
                byte[] bitmapBuffer;
                if (this.forceStringEncoding) {
                    final byte[] _bb = new String(buf, isoHeaderLength + 4, 16, this.encoding).getBytes();
                    bitmapBuffer = new byte[36 + isoHeaderLength];
                    System.arraycopy(_bb, 0, bitmapBuffer, 4 + isoHeaderLength, 16);
                }
                else {
                    bitmapBuffer = buf;
                }
                for (int i = isoHeaderLength + 4; i < isoHeaderLength + 20; ++i) {
                    if (bitmapBuffer[i] >= 48 && bitmapBuffer[i] <= 57) {
                        bs.set(pos++, (bitmapBuffer[i] - 48 & 0x8) > 0);
                        bs.set(pos++, (bitmapBuffer[i] - 48 & 0x4) > 0);
                        bs.set(pos++, (bitmapBuffer[i] - 48 & 0x2) > 0);
                        bs.set(pos++, (bitmapBuffer[i] - 48 & 0x1) > 0);
                    }
                    else if (bitmapBuffer[i] >= 65 && bitmapBuffer[i] <= 70) {
                        bs.set(pos++, (bitmapBuffer[i] - 55 & 0x8) > 0);
                        bs.set(pos++, (bitmapBuffer[i] - 55 & 0x4) > 0);
                        bs.set(pos++, (bitmapBuffer[i] - 55 & 0x2) > 0);
                        bs.set(pos++, (bitmapBuffer[i] - 55 & 0x1) > 0);
                    }
                    else if (bitmapBuffer[i] >= 97 && bitmapBuffer[i] <= 102) {
                        bs.set(pos++, (bitmapBuffer[i] - 87 & 0x8) > 0);
                        bs.set(pos++, (bitmapBuffer[i] - 87 & 0x4) > 0);
                        bs.set(pos++, (bitmapBuffer[i] - 87 & 0x2) > 0);
                        bs.set(pos++, (bitmapBuffer[i] - 87 & 0x1) > 0);
                    }
                }
                if (bs.get(0)) {
                    if (buf.length < minlength + 16) {
                        throw new ParseException("Insufficient length for secondary bitmap", minlength);
                    }
                    if (this.forceStringEncoding) {
                        final byte[] _bb = new String(buf, isoHeaderLength + 20, 16, this.encoding).getBytes();
                        System.arraycopy(_bb, 0, bitmapBuffer, 20 + isoHeaderLength, 16);
                    }
                    for (int i = isoHeaderLength + 20; i < isoHeaderLength + 36; ++i) {
                        if (bitmapBuffer[i] >= 48 && bitmapBuffer[i] <= 57) {
                            bs.set(pos++, (bitmapBuffer[i] - 48 & 0x8) > 0);
                            bs.set(pos++, (bitmapBuffer[i] - 48 & 0x4) > 0);
                            bs.set(pos++, (bitmapBuffer[i] - 48 & 0x2) > 0);
                            bs.set(pos++, (bitmapBuffer[i] - 48 & 0x1) > 0);
                        }
                        else if (bitmapBuffer[i] >= 65 && bitmapBuffer[i] <= 70) {
                            bs.set(pos++, (bitmapBuffer[i] - 55 & 0x8) > 0);
                            bs.set(pos++, (bitmapBuffer[i] - 55 & 0x4) > 0);
                            bs.set(pos++, (bitmapBuffer[i] - 55 & 0x2) > 0);
                            bs.set(pos++, (bitmapBuffer[i] - 55 & 0x1) > 0);
                        }
                        else if (bitmapBuffer[i] >= 97 && bitmapBuffer[i] <= 102) {
                            bs.set(pos++, (bitmapBuffer[i] - 87 & 0x8) > 0);
                            bs.set(pos++, (bitmapBuffer[i] - 87 & 0x4) > 0);
                            bs.set(pos++, (bitmapBuffer[i] - 87 & 0x2) > 0);
                            bs.set(pos++, (bitmapBuffer[i] - 87 & 0x1) > 0);
                        }
                    }
                    pos = 16 + minlength;
                }
                else {
                    pos = minlength;
                }
            }
            catch (NumberFormatException ex) {
                final ParseException _e = new ParseException("Invalid ISO8583 bitmap", pos);
                _e.initCause(ex);
                throw _e;
            }
        }
        final Map<Integer, FieldParseInfo> parseGuide = this.parseMap.get(type);
        final List<Integer> index = this.parseOrder.get(type);
        if (index == null) {
            this.log.error(String.format("ISO8583 MessageFactory has no parsing guide for message type %04x [%s]", type, new String(buf)));
            System.out.println(String.format("ISO8583 MessageFactory has no parsing guide for message type %04x [%s]", type, new String(buf)));
            throw new ParseException(String.format("ISO8583 MessageFactory has no parsing guide for message type %04x [%s]", type, new String(buf)), 0);
        }
        boolean abandon = false;
        for (int j = 1; j < bs.length(); ++j) {
            if (bs.get(j) && !index.contains(j + 1)) {
                this.log.warn("ISO8583 MessageFactory cannot parse field {}: unspecified in parsing guide for type {}", (Object)(j + 1), (Object) Integer.toString(type, 16));
                abandon = true;
            }
        }
        if (abandon) {
            throw new ParseException("ISO8583 MessageFactory cannot parse fields", 0);
        }
        if (this.binaryFields) {
            for (final Integer k : index) {
                final FieldParseInfo fpi = parseGuide.get(k);
                if (bs.get(k - 1)) {
                    if (this.ignoreLast && pos >= buf.length && k == (int)index.get(index.size() - 1)) {
                        this.log.warn("Field {} is not really in the message even though it's in the bitmap", (Object)k);
                        bs.clear(k - 1);
                    }
                    else {
                        CustomField<?> decoder = fpi.getDecoder();
                        if (decoder == null) {
                            decoder = this.getCustomField(k);
                        }
                        final IsoValue<?> val = fpi.parseBinary(k, buf, pos, decoder);
                        m.setField(k, val);
                        if (val == null) {
                            continue;
                        }
                        if (val.getType() == IsoType.NUMERIC || val.getType() == IsoType.DATE10 || val.getType() == IsoType.DATE4 || val.getType() == IsoType.DATE12 || val.getType() == IsoType.DATE14 || val.getType() == IsoType.DATE_EXP || val.getType() == IsoType.AMOUNT || val.getType() == IsoType.TIME) {
                            pos += val.getLength() / 2 + val.getLength() % 2;
                        }
                        else if (val.getType() == IsoType.LLBCDBIN || val.getType() == IsoType.LLLBCDBIN || val.getType() == IsoType.LLLLBCDBIN) {
                            pos += val.getLength() / 2 + ((val.getLength() % 2 != 0) ? 1 : 0);
                        }
                        else {
                            pos += val.getLength();
                        }
                        if (val.getType() == IsoType.LLVAR || val.getType() == IsoType.LLBIN || val.getType() == IsoType.LLBCDBIN) {
                            ++pos;
                        }
                        else {
                            if (val.getType() != IsoType.LLLVAR && val.getType() != IsoType.LLLBIN && val.getType() != IsoType.LLLBCDBIN && val.getType() != IsoType.LLLLVAR && val.getType() != IsoType.LLLLBIN && val.getType() != IsoType.LLLLBCDBIN) {
                                continue;
                            }
                            pos += 2;
                        }
                    }
                }
            }
        }
        else {
            for (final Integer k : index) {
                final FieldParseInfo fpi = parseGuide.get(k);
                if (bs.get(k - 1)) {
                    if (this.ignoreLast && pos >= buf.length && k == (int)index.get(index.size() - 1)) {
                        this.log.warn("Field {} is not really in the message even though it's in the bitmap", (Object)k);
                        bs.clear(k - 1);
                    }
                    else {
                        CustomField<?> decoder = fpi.getDecoder();
                        if (decoder == null) {
                            decoder = this.getCustomField(k);
                        }
                        final IsoValue<?> val = fpi.parse(k, buf, pos, decoder);
                        m.setField(k, val);
                        pos += val.toString().getBytes(fpi.getCharacterEncoding()).length;
                        if (val.getType() == IsoType.LLVAR || val.getType() == IsoType.LLBIN || val.getType() == IsoType.LLBCDBIN) {
                            pos += 2;
                        }
                        else if (val.getType() == IsoType.LLLVAR || val.getType() == IsoType.LLLBIN || val.getType() == IsoType.LLLBCDBIN) {
                            pos += 3;
                        }
                        else {
                            if (val.getType() != IsoType.LLLLVAR && val.getType() != IsoType.LLLLBIN && val.getType() != IsoType.LLLLBCDBIN) {
                                continue;
                            }
                            pos += 4;
                        }
                    }
                }
            }
        }
        m.setBinaryHeader(this.binaryHeader);
        m.setBinaryFields(this.binaryFields);
        m.setBinaryBitmap(this.binBitmap);
        return m;
    }
    
    protected T createIsoMessage(final String header) {
        return (T)new IsoMessage(header);
    }
    
    protected T createIsoMessageWithBinaryHeader(final byte[] binHeader) {
        return (T)new IsoMessage(binHeader);
    }
    
    public void setAssignDate(final boolean flag) {
        this.setDate = flag;
    }
    
    public boolean getAssignDate() {
        return this.setDate;
    }
    
    public void setTraceNumberGenerator(final TraceNumberGenerator value) {
        this.traceGen = value;
    }
    
    public TraceNumberGenerator getTraceNumberGenerator() {
        return this.traceGen;
    }
    
    public void setIsoHeaders(final Map<Integer, String> value) {
        this.isoHeaders.clear();
        this.isoHeaders.putAll(value);
    }
    
    public void setIsoHeader(final int type, final String value) {
        if (value == null) {
            this.isoHeaders.remove(type);
        }
        else {
            this.isoHeaders.put(type, value);
            this.binIsoHeaders.remove(type);
        }
    }
    
    public String getIsoHeader(final int type) {
        return this.isoHeaders.get(type);
    }
    
    public void setBinaryIsoHeader(final int type, final byte[] value) {
        if (value == null) {
            this.binIsoHeaders.remove(type);
        }
        else {
            this.binIsoHeaders.put(type, value);
            this.isoHeaders.remove(type);
        }
    }
    
    public byte[] getBinaryIsoHeader(final int type) {
        return this.binIsoHeaders.get(type);
    }
    
    public void addMessageTemplate(final T templ) {
        if (templ != null) {
            this.typeTemplates.put(templ.getType(), templ);
        }
    }
    
    public void removeMessageTemplate(final int type) {
        this.typeTemplates.remove(type);
    }
    
    public T getMessageTemplate(final int type) {
        return this.typeTemplates.get(type);
    }
    
    public void freeze() {
        this.typeTemplates = Collections.unmodifiableMap((Map<? extends Integer, ? extends T>)this.typeTemplates);
        this.parseMap = Collections.unmodifiableMap((Map<? extends Integer, ? extends Map<Integer, FieldParseInfo>>)this.parseMap);
        this.parseOrder = Collections.unmodifiableMap((Map<? extends Integer, ? extends List<Integer>>)this.parseOrder);
        this.isoHeaders = Collections.unmodifiableMap((Map<? extends Integer, ? extends String>)this.isoHeaders);
        this.binIsoHeaders = Collections.unmodifiableMap((Map<? extends Integer, ? extends byte[]>)this.binIsoHeaders);
        this.customFields = (Map<Integer, CustomField>) Collections.unmodifiableMap((Map<? extends Integer, ? extends CustomField>)this.customFields);
    }
    
    public void setParseMap(final int type, final Map<Integer, FieldParseInfo> map) {
        this.parseMap.put(type, map);
        final ArrayList<Integer> index = new ArrayList<Integer>();
        index.addAll(map.keySet());
        Collections.sort(index);
        this.log.trace(String.format("ISO8583 MessageFactory adding parse map for type %04x with fields %s", type, index));
        this.parseOrder.put(type, index);
    }
}
