// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.slf4j;

import com.jayrush.springmvcrest.slf4j.helpers.NOPLoggerFactory;
import com.jayrush.springmvcrest.slf4j.helpers.SubstituteLoggerFactory;
import com.jayrush.springmvcrest.slf4j.helpers.Util;
import com.jayrush.springmvcrest.slf4j.impl.StaticLoggerBinder;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public final class LoggerFactory
{
    static final String CODES_PREFIX = "http://www.slf4j.org/codes.html";
    static final String NO_STATICLOGGERBINDER_URL = "http://www.slf4j.org/codes.html#StaticLoggerBinder";
    static final String MULTIPLE_BINDINGS_URL = "http://www.slf4j.org/codes.html#multiple_bindings";
    static final String NULL_LF_URL = "http://www.slf4j.org/codes.html#null_LF";
    static final String VERSION_MISMATCH = "http://www.slf4j.org/codes.html#version_mismatch";
    static final String SUBSTITUTE_LOGGER_URL = "http://www.slf4j.org/codes.html#substituteLogger";
    static final String UNSUCCESSFUL_INIT_URL = "http://www.slf4j.org/codes.html#unsuccessfulInit";
    static final String UNSUCCESSFUL_INIT_MSG = "com.jayrush.springmvcrest.LoggerFactory could not be successfully initialized. See also http://www.slf4j.org/codes.html#unsuccessfulInit";
    static final int UNINITIALIZED = 0;
    static final int ONGOING_INITILIZATION = 1;
    static final int FAILED_INITILIZATION = 2;
    static final int SUCCESSFUL_INITILIZATION = 3;
    static final int NOP_FALLBACK_INITILIZATION = 4;
    static int INITIALIZATION_STATE;
    static SubstituteLoggerFactory TEMP_FACTORY;
    static NOPLoggerFactory NOP_FALLBACK_FACTORY;
    private static final String[] API_COMPATIBILITY_LIST;
    private static String STATIC_LOGGER_BINDER_PATH;
    
    private LoggerFactory() {
    }
    
    static void reset() {
        LoggerFactory.INITIALIZATION_STATE = 0;
        LoggerFactory.TEMP_FACTORY = new SubstituteLoggerFactory();
    }
    
    private static final void performInitialization() {
        singleImplementationSanityCheck();
        bind();
        if (LoggerFactory.INITIALIZATION_STATE == 3) {
            versionSanityCheck();
        }
    }
    
    private static final void bind() {
        try {
            StaticLoggerBinder.getSingleton();
            LoggerFactory.INITIALIZATION_STATE = 3;
            emitSubstituteLoggerWarning();
        }
        catch (NoClassDefFoundError ncde) {
            final String msg = ncde.getMessage();
            if (msg == null || msg.indexOf("org/slf4j/impl/StaticLoggerBinder") == -1) {
                failedBinding(ncde);
                throw ncde;
            }
            LoggerFactory.INITIALIZATION_STATE = 4;
            Util.report("Failed to load class \"com.jayrush.springmvcrest.impl.StaticLoggerBinder\".");
            Util.report("Defaulting to no-operation (NOP) logger implementation");
            Util.report("See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.");
        }
        catch (NoSuchMethodError nsme) {
            final String msg = nsme.getMessage();
            if (msg != null && msg.indexOf("com.jayrush.springmvcrest.impl.StaticLoggerBinder.getSingleton()") != -1) {
                LoggerFactory.INITIALIZATION_STATE = 2;
                Util.report("slf4j-api 1.6.x (or later) is incompatible with this binding.");
                Util.report("Your binding is version 1.5.5 or earlier.");
                Util.report("Upgrade your binding to version 1.6.x. or 2.0.x");
            }
            throw nsme;
        }
        catch (Exception e) {
            failedBinding(e);
            throw new IllegalStateException("Unexpected initialization failure", e);
        }
    }
    
    static void failedBinding(final Throwable t) {
        LoggerFactory.INITIALIZATION_STATE = 2;
        Util.report("Failed to instantiate SLF4J LoggerFactory", t);
    }
    
    private static final void emitSubstituteLoggerWarning() {
        final List loggerNameList = LoggerFactory.TEMP_FACTORY.getLoggerNameList();
        if (loggerNameList.size() == 0) {
            return;
        }
        Util.report("The following loggers will not work becasue they were created");
        Util.report("during the default configuration phase of the underlying logging system.");
        Util.report("See also http://www.slf4j.org/codes.html#substituteLogger");
        for (int i = 0; i < loggerNameList.size(); ++i) {
            final String loggerName = (String) loggerNameList.get(i);
            Util.report(loggerName);
        }
    }
    
    private static final void versionSanityCheck() {
        try {
            final String requested = StaticLoggerBinder.REQUESTED_API_VERSION;
            boolean match = false;
            for (int i = 0; i < LoggerFactory.API_COMPATIBILITY_LIST.length; ++i) {
                if (requested.startsWith(LoggerFactory.API_COMPATIBILITY_LIST[i])) {
                    match = true;
                }
            }
            if (!match) {
                Util.report("The requested version " + requested + " by your slf4j binding is not compatible with " + Arrays.asList(LoggerFactory.API_COMPATIBILITY_LIST).toString());
                Util.report("See http://www.slf4j.org/codes.html#version_mismatch for further details.");
            }
        }
        catch (NoSuchFieldError nsfe) {}
        catch (Throwable e) {
            Util.report("Unexpected problem occured during version sanity check", e);
        }
    }
    
    private static void singleImplementationSanityCheck() {
        try {
            final ClassLoader loggerFactoryClassLoader = LoggerFactory.class.getClassLoader();
            Enumeration paths;
            if (loggerFactoryClassLoader == null) {
                paths = ClassLoader.getSystemResources(LoggerFactory.STATIC_LOGGER_BINDER_PATH);
            }
            else {
                paths = loggerFactoryClassLoader.getResources(LoggerFactory.STATIC_LOGGER_BINDER_PATH);
            }
            final List implementationList = new ArrayList();
            while (paths.hasMoreElements()) {
                final URL path = (URL) paths.nextElement();
                implementationList.add(path);
            }
            if (implementationList.size() > 1) {
                Util.report("Class path contains multiple SLF4J bindings.");
                for (int i = 0; i < implementationList.size(); ++i) {
                    Util.report("Found binding in [" + implementationList.get(i) + "]");
                }
                Util.report("See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.");
            }
        }
        catch (IOException ioe) {
            Util.report("Error getting resources from path", ioe);
        }
    }
    
    public static Logger getLogger(final String name) {
        final ILoggerFactory iLoggerFactory = getILoggerFactory();
        return iLoggerFactory.getLogger(name);
    }
    
    public static Logger getLogger(final Class clazz) {
        return getLogger(clazz.getName());
    }
    
    public static ILoggerFactory getILoggerFactory() {
        if (LoggerFactory.INITIALIZATION_STATE == 0) {
            LoggerFactory.INITIALIZATION_STATE = 1;
            performInitialization();
        }
        switch (LoggerFactory.INITIALIZATION_STATE) {
            case 3: {
                return StaticLoggerBinder.getSingleton().getLoggerFactory();
            }
            case 4: {
                return (ILoggerFactory) LoggerFactory.NOP_FALLBACK_FACTORY;
            }
            case 2: {
                throw new IllegalStateException("com.jayrush.springmvcrest.LoggerFactory could not be successfully initialized. See also http://www.slf4j.org/codes.html#unsuccessfulInit");
            }
            case 1: {
                return (ILoggerFactory) LoggerFactory.TEMP_FACTORY;
            }
            default: {
                throw new IllegalStateException("Unreachable code");
            }
        }
    }
    
    static {
        LoggerFactory.INITIALIZATION_STATE = 0;
        LoggerFactory.TEMP_FACTORY = new SubstituteLoggerFactory();
        LoggerFactory.NOP_FALLBACK_FACTORY = new NOPLoggerFactory();
        API_COMPATIBILITY_LIST = new String[] { "1.6" };
        LoggerFactory.STATIC_LOGGER_BINDER_PATH = "org/slf4j/impl/StaticLoggerBinder.class";
    }
}
