// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.slf4j.impl;

import com.jayrush.springmvcrest.slf4j.ILoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

//import android.util.Log;

public class AndroidLoggerFactory implements ILoggerFactory
{
    private final Map<String, AndroidLogger> loggerMap;
    static final int TAG_MAX_LENGTH = 23;
    
    public AndroidLoggerFactory() {
        this.loggerMap = new HashMap<String, AndroidLogger>();
    }
    
    public AndroidLogger getLogger(final String name) {
        final String actualName = this.forceValidName(name);
        AndroidLogger slogger = null;
        synchronized (this) {
            slogger = this.loggerMap.get(actualName);
            if (slogger == null) {
                if (!actualName.equals(name)) {
                    //Log.i(AndroidLoggerFactory.class.getSimpleName(), "Logger name '" + name + "' exceeds maximum length of " + 23 + " characters, using '" + actualName + "' instead.");
                }
                slogger = new AndroidLogger(actualName);
                this.loggerMap.put(actualName, slogger);
            }
        }
        return slogger;
    }
    
    private final String forceValidName(String name) {
        if (name != null && name.length() > 23) {
            final StringTokenizer st = new StringTokenizer(name, ".");
            if (st.hasMoreTokens()) {
                final StringBuilder sb = new StringBuilder();
                do {
                    final String token = st.nextToken();
                    if (token.length() == 1) {
                        sb.append(token);
                        sb.append('.');
                    }
                    else if (st.hasMoreTokens()) {
                        sb.append(token.charAt(0));
                        sb.append("*.");
                    }
                    else {
                        sb.append(token);
                    }
                } while (st.hasMoreTokens());
                name = sb.toString();
            }
            if (name.length() > 23) {
                name = name.substring(0, 22) + '*';
            }
        }
        return name;
    }
}
