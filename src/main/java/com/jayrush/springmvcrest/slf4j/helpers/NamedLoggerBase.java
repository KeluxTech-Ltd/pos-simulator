// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.slf4j.helpers;

import com.jayrush.springmvcrest.slf4j.Logger;
import com.jayrush.springmvcrest.slf4j.LoggerFactory;

import java.io.ObjectStreamException;
import java.io.Serializable;

public abstract class NamedLoggerBase implements Logger, Serializable
{
    private static final long serialVersionUID = 7535258609338176893L;
    protected String name;
    
    public String getName() {
        return this.name;
    }
    
    protected Object readResolve() throws ObjectStreamException {
        return LoggerFactory.getLogger(this.getName());
    }
}
