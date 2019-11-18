// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.iso8583;

public interface TraceNumberGenerator
{
    int nextTrace();
    
    int getLastTrace();
}
