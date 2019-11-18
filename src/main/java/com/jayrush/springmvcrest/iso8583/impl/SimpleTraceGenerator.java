// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.iso8583.impl;

import com.jayrush.springmvcrest.iso8583.TraceNumberGenerator;

public class SimpleTraceGenerator implements TraceNumberGenerator
{
    private volatile int value;
    
    public SimpleTraceGenerator(final int initialValue) {
        this.value = 0;
        if (initialValue < 1 || initialValue > 999999) {
            throw new IllegalArgumentException("Initial value must be between 1 and 999999");
        }
        this.value = initialValue - 1;
    }
    
    @Override
    public int getLastTrace() {
        return this.value;
    }
    
    @Override
    public synchronized int nextTrace() {
        ++this.value;
        if (this.value > 999999) {
            this.value = 1;
        }
        return this.value;
    }
}
