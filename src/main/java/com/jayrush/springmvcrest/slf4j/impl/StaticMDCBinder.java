// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.slf4j.impl;

import com.jayrush.springmvcrest.slf4j.helpers.NOPMDCAdapter;
import com.jayrush.springmvcrest.slf4j.spi.MDCAdapter;

public class StaticMDCBinder
{
    public static final StaticMDCBinder SINGLETON;
    
    private StaticMDCBinder() {
    }
    
    public MDCAdapter getMDCA() {
        return new NOPMDCAdapter();
    }
    
    public String getMDCAdapterClassStr() {
        return NOPMDCAdapter.class.getName();
    }
    
    static {
        SINGLETON = new StaticMDCBinder();
    }
}
