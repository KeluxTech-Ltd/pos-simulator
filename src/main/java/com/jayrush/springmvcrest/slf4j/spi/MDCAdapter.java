// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.slf4j.spi;

import java.util.Map;

public interface MDCAdapter
{
    void put(final String p0, final String p1);
    
    String get(final String p0);
    
    void remove(final String p0);
    
    void clear();
    
    Map getCopyOfContextMap();
    
    void setContextMap(final Map p0);
}
