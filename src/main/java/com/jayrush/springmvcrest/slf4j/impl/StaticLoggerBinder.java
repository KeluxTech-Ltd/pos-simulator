// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.slf4j.impl;

import com.jayrush.springmvcrest.slf4j.ILoggerFactory;
import com.jayrush.springmvcrest.slf4j.spi.LoggerFactoryBinder;

public class StaticLoggerBinder implements LoggerFactoryBinder
{
    private static final StaticLoggerBinder SINGLETON;
    public static String REQUESTED_API_VERSION;
    private static final String loggerFactoryClassStr;
    private final ILoggerFactory loggerFactory;
    
    public static final StaticLoggerBinder getSingleton() {
        return StaticLoggerBinder.SINGLETON;
    }
    
    private StaticLoggerBinder() {
        this.loggerFactory = new AndroidLoggerFactory();
    }
    
    public ILoggerFactory getLoggerFactory() {
        return this.loggerFactory;
    }
    
    public String getLoggerFactoryClassStr() {
        return StaticLoggerBinder.loggerFactoryClassStr;
    }
    
    static {
        SINGLETON = new StaticLoggerBinder();
        StaticLoggerBinder.REQUESTED_API_VERSION = "1.6";
        loggerFactoryClassStr = AndroidLoggerFactory.class.getName();
    }
}
