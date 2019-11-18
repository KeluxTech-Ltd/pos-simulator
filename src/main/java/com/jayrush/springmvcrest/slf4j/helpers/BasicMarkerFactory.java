// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.slf4j.helpers;

import com.jayrush.springmvcrest.slf4j.IMarkerFactory;
import com.jayrush.springmvcrest.slf4j.Marker;

import java.util.HashMap;
import java.util.Map;

public class BasicMarkerFactory implements IMarkerFactory
{
    Map markerMap;
    
    public BasicMarkerFactory() {
        this.markerMap = new HashMap();
    }
    
    public synchronized Marker getMarker(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Marker name cannot be null");
        }
        Marker marker = (Marker) this.markerMap.get(name);
        if (marker == null) {
            marker = (Marker) new BasicMarker(name);
            this.markerMap.put(name, marker);
        }
        return marker;
    }
    
    public synchronized boolean exists(final String name) {
        return name != null && this.markerMap.containsKey(name);
    }
    
    public boolean detachMarker(final String name) {
        return name != null && this.markerMap.remove(name) != null;
    }
    
    public Marker getDetachedMarker(final String name) {
        return (Marker) new BasicMarker(name);
    }
}
