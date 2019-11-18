// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.slf4j;

import java.io.Serializable;
import java.util.Iterator;

public interface Marker extends Serializable
{
    public static final String ANY_MARKER = "*";
    public static final String ANY_NON_NULL_MARKER = "+";
    
    String getName();
    
    void add(final Marker p0);
    
    boolean remove(final Marker p0);
    
    @Deprecated
    boolean hasChildren();
    
    boolean hasReferences();
    
    Iterator iterator();
    
    boolean contains(final Marker p0);
    
    boolean contains(final String p0);
    
    boolean equals(final Object p0);
    
    int hashCode();
}
