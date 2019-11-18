// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.iso8583.parse;

import com.jayrush.springmvcrest.iso8583.*;
import com.jayrush.springmvcrest.iso8583.codecs.CompositeField;
import com.jayrush.springmvcrest.iso8583.util.HexCodec;
import com.jayrush.springmvcrest.slf4j.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;


public class ConfigParser
{
    private static final Logger log;
    
    public static MessageFactory<IsoMessage> createDefault(final ClassLoader loader) throws IOException {
        if (loader.getResource("j8583.xml") == null) {
            ConfigParser.log.warn("ISO8583 ConfigParser cannot find j8583.xml, returning empty message factory");
            return new MessageFactory<IsoMessage>();
        }
        return createFromClasspathConfig(loader, "j8583.xml");
    }
    
    public static MessageFactory<IsoMessage> createDefault() throws IOException {
        return createDefault(MessageFactory.class.getClassLoader());
    }
    
    public static MessageFactory<IsoMessage> createFromClasspathConfig(final String path) throws IOException {
        return createFromClasspathConfig(MessageFactory.class.getClassLoader(), path);
    }
    
    public static MessageFactory<IsoMessage> createFromClasspathConfig(final ClassLoader loader, final String path) throws IOException {
        final MessageFactory<IsoMessage> mfact = new MessageFactory<IsoMessage>();
        final InputStream ins = loader.getResourceAsStream(path);
        try {
            if (ins != null) {
                ConfigParser.log.debug("ISO8583 Parsing config from classpath file {}", (Object)path);
                parse(mfact, new InputSource(ins));
            }
            else {
                ConfigParser.log.error("ISO8583 File not found in classpath: {}", (Object)path);
            }
            if (ins != null) {
                ins.close();
            }
        }
        catch (Throwable t) {
            if (ins != null) {
                try {
                    ins.close();
                }
                catch (Throwable exception) {
                    t.addSuppressed(exception);
                }
            }
            throw t;
        }
        return mfact;
    }
    
    public static MessageFactory<IsoMessage> createFromUrl(final URL url) throws IOException {
        final MessageFactory<IsoMessage> mfact = new MessageFactory<IsoMessage>();
        final InputStream stream = url.openStream();
        try {
            parse(mfact, new InputSource(stream));
            if (stream != null) {
                stream.close();
            }
        }
        catch (Throwable t) {
            if (stream != null) {
                try {
                    stream.close();
                }
                catch (Throwable exception) {
                    t.addSuppressed(exception);
                }
            }
            throw t;
        }
        return mfact;
    }
    
    public static MessageFactory<IsoMessage> createFromReader(final Reader reader) throws IOException {
        final MessageFactory<IsoMessage> mfact = new MessageFactory<IsoMessage>();
        parse(mfact, new InputSource(reader));
        return mfact;
    }
    
    protected static <T extends IsoMessage> void parseHeaders(final NodeList nodes, final MessageFactory<T> mfact) throws IOException {
        ArrayList<Element> refs = null;
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Element elem = (Element)nodes.item(i);
            final int type = parseType(elem.getAttribute("type"));
            if (type == -1) {
                throw new IOException("Invalid type for ISO8583 header: " + elem.getAttribute("type"));
            }
            if (elem.getChildNodes() == null || elem.getChildNodes().getLength() == 0) {
                if (elem.getAttribute("ref") == null || elem.getAttribute("ref").isEmpty()) {
                    throw new IOException("Invalid ISO8583 header element");
                }
                if (refs == null) {
                    refs = new ArrayList<Element>(nodes.getLength() - i);
                }
                refs.add(elem);
            }
            else {
                final String header = elem.getChildNodes().item(0).getNodeValue();
                final boolean binHeader = "true".equals(elem.getAttribute("binary"));
                if (ConfigParser.log.isTraceEnabled()) {
                    ConfigParser.log.trace("Adding {}ISO8583 header for type {}: {}", new Object[] { binHeader ? "binary " : "", elem.getAttribute("type"), header });
                }
                if (binHeader) {
                    mfact.setBinaryIsoHeader(type, HexCodec.hexDecode(header));
                }
                else {
                    mfact.setIsoHeader(type, header);
                }
            }
        }
        if (refs != null) {
            for (final Element elem : refs) {
                final int type = parseType(elem.getAttribute("type"));
                if (type == -1) {
                    throw new IOException("Invalid type for ISO8583 header: " + elem.getAttribute("type"));
                }
                if (elem.getAttribute("ref") == null || elem.getAttribute("ref").isEmpty()) {
                    continue;
                }
                final int t2 = parseType(elem.getAttribute("ref"));
                if (t2 == -1) {
                    throw new IOException("Invalid type reference " + elem.getAttribute("ref") + " for ISO8583 header " + type);
                }
                final String h = mfact.getIsoHeader(t2);
                if (h == null) {
                    throw new IllegalArgumentException("Header def " + type + " refers to nonexistent header " + t2);
                }
                if (ConfigParser.log.isTraceEnabled()) {
                    ConfigParser.log.trace("Adding ISO8583 header for type {}: {} (copied from {})", new Object[] { elem.getAttribute("type"), h, elem.getAttribute("ref") });
                }
                mfact.setIsoHeader(type, h);
            }
        }
    }
    
    protected static <T extends IsoMessage> void parseTemplates(final NodeList nodes, final MessageFactory<T> mfact) throws IOException {
        ArrayList<Element> subs = null;
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Element elem = (Element)nodes.item(i);
            final int type = parseType(elem.getAttribute("type"));
            if (type == -1) {
                throw new IOException("Invalid ISO8583 type for template: " + elem.getAttribute("type"));
            }
            if (elem.getAttribute("extends") != null && !elem.getAttribute("extends").isEmpty()) {
                if (subs == null) {
                    subs = new ArrayList<Element>(nodes.getLength() - i);
                }
                subs.add(elem);
            }
            else {
                final T m = (T)new IsoMessage();
                m.setType(type);
                m.setCharacterEncoding(mfact.getCharacterEncoding());
                final NodeList fields = elem.getElementsByTagName("field");
                for (int j = 0; j < fields.getLength(); ++j) {
                    final Element f = (Element)fields.item(j);
                    if (f.getParentNode() == elem) {
                        final int num = Integer.parseInt(f.getAttribute("num"));
                        final IsoValue<?> v = getTemplateField(f, mfact, true);
                        if (v != null) {
                            v.setCharacterEncoding(mfact.getCharacterEncoding());
                        }
                        m.setField(num, v);
                    }
                }
                mfact.addMessageTemplate(m);
            }
        }
        if (subs != null) {
            for (final Element elem : subs) {
                final int type = parseType(elem.getAttribute("type"));
                final int ref = parseType(elem.getAttribute("extends"));
                if (ref == -1) {
                    throw new IllegalArgumentException("Message template " + elem.getAttribute("type") + " extends invalid template " + elem.getAttribute("extends"));
                }
                final IsoMessage tref = mfact.getMessageTemplate(ref);
                if (tref == null) {
                    throw new IllegalArgumentException("Message template " + elem.getAttribute("type") + " extends nonexistent template " + elem.getAttribute("extends"));
                }
                final T k = (T)new IsoMessage();
                k.setType(type);
                k.setCharacterEncoding(mfact.getCharacterEncoding());
                for (int l = 2; l < 128; ++l) {
                    if (tref.hasField(l)) {
                        k.setField(l, tref.getField(l).clone());
                    }
                }
                final NodeList fields2 = elem.getElementsByTagName("field");
                for (int j2 = 0; j2 < fields2.getLength(); ++j2) {
                    final Element f2 = (Element)fields2.item(j2);
                    final int num2 = Integer.parseInt(f2.getAttribute("num"));
                    if (f2.getParentNode() == elem) {
                        final IsoValue<?> v2 = getTemplateField(f2, mfact, true);
                        if (v2 != null) {
                            v2.setCharacterEncoding(mfact.getCharacterEncoding());
                        }
                        k.setField(num2, v2);
                    }
                }
                mfact.addMessageTemplate(k);
            }
        }
    }
    
    protected static <M extends IsoMessage> IsoValue<?> getTemplateField(final Element f, final MessageFactory<M> mfact, final boolean toplevel) {
        final int num = Integer.parseInt(f.getAttribute("num"));
        final String typedef = f.getAttribute("type");
        if ("exclude".equals(typedef)) {
            return null;
        }
        int length = 0;
        if (f.getAttribute("length").length() > 0) {
            length = Integer.parseInt(f.getAttribute("length"));
        }
        final IsoType itype = IsoType.valueOf(typedef);
        final NodeList subs = f.getElementsByTagName("field");
        if (subs != null && subs.getLength() > 0) {
            final CompositeField cf = new CompositeField();
            for (int j = 0; j < subs.getLength(); ++j) {
                final Element sub = (Element)subs.item(j);
                if (sub.getParentNode() == f) {
                    final IsoValue<?> sv = getTemplateField(sub, (MessageFactory<IsoMessage>)mfact, false);
                    if (sv != null) {
                        sv.setCharacterEncoding(mfact.getCharacterEncoding());
                        cf.addValue(sv);
                    }
                }
            }
            final IsoValue<?> rv = itype.needsLength() ? new IsoValue<CompositeField>(itype, cf, length, cf) : new IsoValue<CompositeField>(itype, cf, cf);
            if (f.hasAttribute("tz")) {
                final TimeZone tz = TimeZone.getTimeZone(f.getAttribute("tz"));
                rv.setTimeZone(tz);
            }
            return rv;
        }
        String v;
        if (f.getChildNodes().getLength() == 0) {
            v = "";
        }
        else {
            v = f.getChildNodes().item(0).getNodeValue();
        }
        final CustomField<Object> cf2 = toplevel ? mfact.getCustomField(num) : null;
        IsoValue<?> rv2;
        if (cf2 == null) {
            rv2 = (itype.needsLength() ? new IsoValue<Object>(itype, v, length) : new IsoValue<Object>(itype, v));
        }
        else {
            rv2 = (itype.needsLength() ? new IsoValue<Object>(itype, cf2.decodeField(v), length, cf2) : new IsoValue<Object>(itype, cf2.decodeField(v), cf2));
        }
        if (f.hasAttribute("tz")) {
            final TimeZone tz2 = TimeZone.getTimeZone(f.getAttribute("tz"));
            rv2.setTimeZone(tz2);
        }
        return rv2;
    }
    
    protected static <T extends IsoMessage> FieldParseInfo getParser(final Element f, final MessageFactory<T> mfact) {
        final IsoType itype = IsoType.valueOf(f.getAttribute("type"));
        int length = 0;
        if (f.getAttribute("length").length() > 0) {
            length = Integer.parseInt(f.getAttribute("length"));
        }
        final FieldParseInfo fpi = FieldParseInfo.getInstance(itype, length, mfact.getCharacterEncoding());
        final NodeList subs = f.getElementsByTagName("field");
        if (subs != null && subs.getLength() > 0) {
            final CompositeField combo = new CompositeField();
            for (int i = 0; i < subs.getLength(); ++i) {
                final Element sf = (Element)subs.item(i);
                if (sf.getParentNode() == f) {
                    combo.addParser(getParser(sf, (MessageFactory<IsoMessage>)mfact));
                }
            }
            fpi.setDecoder(combo);
        }
        if (f.hasAttribute("tz") && fpi instanceof DateTimeParseInfo) {
            final TimeZone tz = TimeZone.getTimeZone(f.getAttribute("tz"));
            ((DateTimeParseInfo)fpi).setTimeZone(tz);
        }
        return fpi;
    }
    
    protected static <T extends IsoMessage> void parseGuides(final NodeList nodes, final MessageFactory<T> mfact) throws IOException {
        ArrayList<Element> subs = null;
        final HashMap<Integer, HashMap<Integer, FieldParseInfo>> guides = new HashMap<Integer, HashMap<Integer, FieldParseInfo>>();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Element elem = (Element)nodes.item(i);
            final int type = parseType(elem.getAttribute("type"));
            if (type == -1) {
                throw new IOException("Invalid ISO8583 type for parse guide: " + elem.getAttribute("type"));
            }
            if (elem.getAttribute("extends") != null && !elem.getAttribute("extends").isEmpty()) {
                if (subs == null) {
                    subs = new ArrayList<Element>(nodes.getLength() - i);
                }
                subs.add(elem);
            }
            else {
                final HashMap<Integer, FieldParseInfo> parseMap = new HashMap<Integer, FieldParseInfo>();
                final NodeList fields = elem.getElementsByTagName("field");
                for (int j = 0; j < fields.getLength(); ++j) {
                    final Element f = (Element)fields.item(j);
                    if (f.getParentNode() == elem) {
                        final int num = Integer.parseInt(f.getAttribute("num"));
                        parseMap.put(num, getParser(f, mfact));
                    }
                }
                mfact.setParseMap(type, parseMap);
                guides.put(type, parseMap);
            }
        }
        if (subs != null) {
            for (final Element elem : subs) {
                final int type = parseType(elem.getAttribute("type"));
                final int ref = parseType(elem.getAttribute("extends"));
                if (ref == -1) {
                    throw new IllegalArgumentException("Message template " + elem.getAttribute("type") + " extends invalid template " + elem.getAttribute("extends"));
                }
                final HashMap<Integer, FieldParseInfo> parent = guides.get(ref);
                if (parent == null) {
                    throw new IllegalArgumentException("Parsing guide " + elem.getAttribute("type") + " extends nonexistent guide " + elem.getAttribute("extends"));
                }
                final HashMap<Integer, FieldParseInfo> child = new HashMap<Integer, FieldParseInfo>();
                child.putAll(parent);
                final List<Element> fields2 = getDirectChildrenByTagName(elem, "field");
                for (final Element f2 : fields2) {
                    final int num2 = Integer.parseInt(f2.getAttribute("num"));
                    final String typedef = f2.getAttribute("type");
                    if ("exclude".equals(typedef)) {
                        child.remove(num2);
                    }
                    else {
                        child.put(num2, getParser(f2, mfact));
                    }
                }
                mfact.setParseMap(type, child);
                guides.put(type, child);
            }
        }
    }
    
    private static List<Element> getDirectChildrenByTagName(final Element elem, final String tagName) {
        final List<Element> childElementsByTagName = new ArrayList<Element>();
        final NodeList childNodes = elem.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            if (childNodes.item(i).getNodeType() == 1) {
                final Element childElem = (Element)childNodes.item(i);
                if (childElem.getTagName().equals(tagName)) {
                    childElementsByTagName.add(childElem);
                }
            }
        }
        return childElementsByTagName;
    }
    
    protected static <T extends IsoMessage> void parse(final MessageFactory<T> mfact, final InputSource source) throws IOException {
        final DocumentBuilderFactory docfact = DocumentBuilderFactory.newInstance();
        DocumentBuilder docb = null;
        Document doc = null;
        try {
            docb = docfact.newDocumentBuilder();
            docb.setEntityResolver(new EntityResolver() {
                @Override
                public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
                    if (systemId.contains("j8583.dtd")) {
                        final URL dtd = this.getClass().getResource("j8583.dtd");
                        if (dtd != null) {
                            return new InputSource(dtd.toString());
                        }
                        ConfigParser.log.warn("Cannot find j8583.dtd in classpath. j8583 config files will not be validated.");
                    }
                    return null;
                }
            });
            doc = docb.parse(source);
        }
        catch (ParserConfigurationException | SAXException ex3) {
            final Exception ex2 = null;
            final Exception ex = ex2;
            ConfigParser.log.error("ISO8583 Cannot parse XML configuration", (Throwable)ex);
            return;
        }
        final Element root = doc.getDocumentElement();
        parseHeaders(root.getElementsByTagName("header"), mfact);
        parseTemplates(root.getElementsByTagName("template"), mfact);
        parseGuides(root.getElementsByTagName("parse"), mfact);
    }
    
    public static <T extends IsoMessage> void configureFromDefault(final MessageFactory<T> mfact) throws IOException {
        if (mfact.getClass().getClassLoader().getResource("j8583.xml") == null) {
            ConfigParser.log.warn("ISO8583 config file j8583.xml not found!");
        }
        else {
            configureFromClasspathConfig(mfact, "j8583.xml");
        }
    }
    
    public static <T extends IsoMessage> void configureFromUrl(final MessageFactory<T> mfact, final URL url) throws IOException {
        final InputStream stream = url.openStream();
        try {
            parse(mfact, new InputSource(stream));
            if (stream != null) {
                stream.close();
            }
        }
        catch (Throwable t) {
            if (stream != null) {
                try {
                    stream.close();
                }
                catch (Throwable exception) {
                    t.addSuppressed(exception);
                }
            }
            throw t;
        }
    }
    
    public static <T extends IsoMessage> void configureFromClasspathConfig(final MessageFactory<T> mfact, final String path) throws IOException {
        final InputStream ins = mfact.getClass().getClassLoader().getResourceAsStream(path);
        try {
            if (ins != null) {
                ConfigParser.log.debug("ISO8583 Parsing config from classpath file {}", (Object)path);
                parse(mfact, new InputSource(ins));
            }
            else {
                ConfigParser.log.warn("ISO8583 File not found in classpath: {}", (Object)path);
            }
            if (ins != null) {
                ins.close();
            }
        }
        catch (Throwable t) {
            if (ins != null) {
                try {
                    ins.close();
                }
                catch (Throwable exception) {
                    t.addSuppressed(exception);
                }
            }
            throw t;
        }
    }
    
    public static <T extends IsoMessage> void configureFromReader(final MessageFactory<T> mfact, final Reader reader) throws IOException {
        parse(mfact, new InputSource(reader));
    }
    
    private static int parseType(String type) throws IOException {
        if (type.length() % 2 == 1) {
            type = "0" + type;
        }
        if (type.length() != 4) {
            return -1;
        }
        return type.charAt(0) - '0' << 12 | type.charAt(1) - '0' << 8 | type.charAt(2) - '0' << 4 | type.charAt(3) - '0';
    }
    
    static {
        log = LoggerFactory.getLogger((Class)ConfigParser.class);
    }
}
