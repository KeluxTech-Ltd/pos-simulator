// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.slf4j.spi;

import com.jayrush.springmvcrest.slf4j.ILoggerFactory;

public interface LoggerFactoryBinder
{
    ILoggerFactory getLoggerFactory();
    
    String getLoggerFactoryClassStr();
}
