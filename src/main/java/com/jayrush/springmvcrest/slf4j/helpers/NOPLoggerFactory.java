// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.slf4j.helpers;

import com.jayrush.springmvcrest.slf4j.ILoggerFactory;
import com.jayrush.springmvcrest.slf4j.Logger;

public class NOPLoggerFactory implements ILoggerFactory
{
    public Logger getLogger(final String name) {
        return NOPLogger.NOP_LOGGER;
    }
}
