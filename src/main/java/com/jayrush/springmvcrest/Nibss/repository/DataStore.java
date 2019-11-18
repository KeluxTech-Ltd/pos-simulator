// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.Nibss.repository;

//public interface DataStore extends com.globasure.nibss.tms.client.lib.repository.DataStore
public interface DataStore
{
    void putString(final String p0, final String p1);
    
    void putInt(final String p0, final int p1);
    
    String getString(final String p0);
    
    int getInt(final String p0);
}
