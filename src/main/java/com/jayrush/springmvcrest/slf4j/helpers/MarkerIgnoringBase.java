// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.slf4j.helpers;

import com.jayrush.springmvcrest.slf4j.Logger;
import com.jayrush.springmvcrest.slf4j.Marker;

public abstract class MarkerIgnoringBase extends com.jayrush.springmvcrest.slf4j.helpers.NamedLoggerBase implements Logger
{
    private static final long serialVersionUID = 9044267456635152283L;
    protected String name;

    public boolean isTraceEnabled(final Marker marker) {
        return this.isTraceEnabled();
    }
    
    public void trace(final Marker marker, final String msg) {
        this.trace(msg);
    }
    
    public void trace(final Marker marker, final String format, final Object arg) {
        this.trace(format, arg);
    }
    
    public void trace(final Marker marker, final String format, final Object arg1, final Object arg2) {
        this.trace(format, arg1, arg2);
    }
    
    public void trace(final Marker marker, final String format, final Object[] argArray) {
        this.trace(format, argArray);
    }
    
    public void trace(final Marker marker, final String msg, final Throwable t) {
        this.trace(msg, t);
    }
    
    public boolean isDebugEnabled(final Marker marker) {
        return this.isDebugEnabled();
    }
    
    public void debug(final Marker marker, final String msg) {
        this.debug(msg);
    }
    
    public void debug(final Marker marker, final String format, final Object arg) {
        this.debug(format, arg);
    }
    
    public void debug(final Marker marker, final String format, final Object arg1, final Object arg2) {
        this.debug(format, arg1, arg2);
    }
    
    public void debug(final Marker marker, final String format, final Object[] argArray) {
        this.debug(format, argArray);
    }
    
    public void debug(final Marker marker, final String msg, final Throwable t) {
        this.debug(msg, t);
    }
    
    public boolean isInfoEnabled(final Marker marker) {
        return this.isInfoEnabled();
    }
    
    public void info(final Marker marker, final String msg) {
        this.info(msg);
    }
    
    public void info(final Marker marker, final String format, final Object arg) {
        this.info(format, arg);
    }
    
    public void info(final Marker marker, final String format, final Object arg1, final Object arg2) {
        this.info(format, arg1, arg2);
    }
    
    public void info(final Marker marker, final String format, final Object[] argArray) {
        this.info(format, argArray);
    }
    
    public void info(final Marker marker, final String msg, final Throwable t) {
        this.info(msg, t);
    }
    
    public boolean isWarnEnabled(final Marker marker) {
        return this.isWarnEnabled();
    }
    
    public void warn(final Marker marker, final String msg) {
        this.warn(msg);
    }
    
    public void warn(final Marker marker, final String format, final Object arg) {
        this.warn(format, arg);
    }
    
    public void warn(final Marker marker, final String format, final Object arg1, final Object arg2) {
        this.warn(format, arg1, arg2);
    }
    
    public void warn(final Marker marker, final String format, final Object[] argArray) {
        this.warn(format, argArray);
    }
    
    public void warn(final Marker marker, final String msg, final Throwable t) {
        this.warn(msg, t);
    }
    
    public boolean isErrorEnabled(final Marker marker) {
        return this.isErrorEnabled();
    }
    
    public void error(final Marker marker, final String msg) {
        this.error(msg);
    }
    
    public void error(final Marker marker, final String format, final Object arg) {
        this.error(format, arg);
    }
    
    public void error(final Marker marker, final String format, final Object arg1, final Object arg2) {
        this.error(format, arg1, arg2);
    }
    
    public void error(final Marker marker, final String format, final Object[] argArray) {
        this.error(format, argArray);
    }
    
    public void error(final Marker marker, final String msg, final Throwable t) {
        this.error(msg, t);
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "(" + this.getName() + ")";
    }
}
