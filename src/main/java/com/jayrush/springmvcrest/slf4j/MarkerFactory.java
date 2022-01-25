// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.slf4j;

import com.jayrush.springmvcrest.slf4j.helpers.BasicMarkerFactory;
import com.jayrush.springmvcrest.slf4j.helpers.Util;
import com.jayrush.springmvcrest.slf4j.impl.StaticMarkerBinder;

public class MarkerFactory
{
    static IMarkerFactory markerFactory;
    
    private MarkerFactory() {
    }
    
    public static Marker getMarker(final String name) {
        return MarkerFactory.markerFactory.getMarker(name);
    }
    
    public static Marker getDetachedMarker(final String name) {
        return MarkerFactory.markerFactory.getDetachedMarker(name);
    }
    
    public static IMarkerFactory getIMarkerFactory() {
        return MarkerFactory.markerFactory;
    }
    
    static {
        try {
            MarkerFactory.markerFactory = StaticMarkerBinder.SINGLETON.getMarkerFactory();
        }
        catch (NoClassDefFoundError e2) {
            MarkerFactory.markerFactory = new BasicMarkerFactory();
        }
        catch (Exception e) {
            Util.report("Unexpected failure while binding MarkerFactory", e);
        }
    }
}
