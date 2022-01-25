// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.slf4j.impl;

import com.jayrush.springmvcrest.slf4j.IMarkerFactory;
import com.jayrush.springmvcrest.slf4j.helpers.BasicMarkerFactory;
import com.jayrush.springmvcrest.slf4j.spi.MarkerFactoryBinder;

public class StaticMarkerBinder implements MarkerFactoryBinder
{
    public static final StaticMarkerBinder SINGLETON;
    private final IMarkerFactory markerFactory;
    
    private StaticMarkerBinder() {
        this.markerFactory = new BasicMarkerFactory();
    }
    
    public IMarkerFactory getMarkerFactory() {
        return this.markerFactory;
    }
    
    public String getMarkerFactoryClassStr() {
        return BasicMarkerFactory.class.getName();
    }
    
    static {
        SINGLETON = new StaticMarkerBinder();
    }
}
