// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.slf4j.helpers;

import com.jayrush.springmvcrest.slf4j.spi.MDCAdapter;

import java.util.Map;

public class NOPMDCAdapter implements MDCAdapter
{
    public void clear() {
    }
    
    public String get(final String key) {
        return null;
    }
    
    public void put(final String key, final String val) {
    }
    
    public void remove(final String key) {
    }
    
    public Map getCopyOfContextMap() {
        return null;
    }
    
    public void setContextMap(final Map contextMap) {
    }
}
