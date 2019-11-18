// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.slf4j.impl;

import com.jayrush.springmvcrest.slf4j.helpers.MarkerIgnoringBase;

//import android.util.Log;

//import static org.graalvm.compiler.debug.DebugOptions.Log;

public class AndroidLogger extends MarkerIgnoringBase
{
    public AndroidLogger(String actualName) {
        super();
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void trace(String p0) {

    }

    @Override
    public void trace(String p0, Object p1) {

    }

    @Override
    public void trace(String p0, Object p1, Object p2) {

    }

    @Override
    public void trace(String p0, Object[] p1) {

    }

    @Override
    public void trace(String p0, Throwable p1) {

    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void debug(String p0) {

    }

    @Override
    public void debug(String p0, Object p1) {

    }

    @Override
    public void debug(String p0, Object p1, Object p2) {

    }

    @Override
    public void debug(String p0, Object[] p1) {

    }

    @Override
    public void debug(String p0, Throwable p1) {

    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public void info(String p0) {

    }

    @Override
    public void info(String p0, Object p1) {

    }

    @Override
    public void info(String p0, Object p1, Object p2) {

    }

    @Override
    public void info(String p0, Object[] p1) {

    }

    @Override
    public void info(String p0, Throwable p1) {

    }

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    @Override
    public void warn(String p0) {

    }

    @Override
    public void warn(String p0, Object p1) {

    }

    @Override
    public void warn(String p0, Object[] p1) {

    }

    @Override
    public void warn(String p0, Object p1, Object p2) {

    }

    @Override
    public void warn(String p0, Throwable p1) {

    }

    @Override
    public boolean isErrorEnabled() {
        return false;
    }

    @Override
    public void error(String p0) {

    }

    @Override
    public void error(String p0, Object p1) {

    }

    @Override
    public void error(String p0, Object p1, Object p2) {

    }

    @Override
    public void error(String p0, Object[] p1) {

    }

    @Override
    public void error(String p0, Throwable p1) {

    }
    /*private static final long serialVersionUID = -1227274521521287937L;
    
    AndroidLogger(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return null;
    }

    public boolean isTraceEnabled() {
        return Log.isLoggable(this.name, 2);
    }
    
    public void trace(final String msg) {
        Log.v(this.name, msg);
    }
    
    public void trace(final String format, final Object param1) {
        Log.v(this.name, this.format(format, param1, null));
    }
    
    public void trace(final String format, final Object param1, final Object param2) {
        Log.v(this.name, this.format(format, param1, param2));
    }
    
    public void trace(final String format, final Object[] argArray) {
        Log.v(this.name, this.format(format, argArray));
    }
    
    public void trace(final String msg, final Throwable t) {
        Log.v(this.name, msg, t);
    }
    
    public boolean isDebugEnabled() {
        return Log.isLoggable(this.name, 3);
    }
    
    public void debug(final String msg) {
        Log.d(this.name, msg);
    }
    
    public void debug(final String format, final Object arg1) {
        Log.d(this.name, this.format(format, arg1, null));
    }
    
    public void debug(final String format, final Object param1, final Object param2) {
        Log.d(this.name, this.format(format, param1, param2));
    }
    
    public void debug(final String format, final Object[] argArray) {
        Log.d(this.name, this.format(format, argArray));
    }
    
    public void debug(final String msg, final Throwable t) {
        Log.d(this.name, msg, t);
    }
    
    public boolean isInfoEnabled() {
        return Log.isLoggable(this.name, 4);
    }
    
    public void info(final String msg) {
        Log.i(this.name, msg);
    }
    
    public void info(final String format, final Object arg) {
        Log.i(this.name, this.format(format, arg, null));
    }
    
    public void info(final String format, final Object arg1, final Object arg2) {
        Log.i(this.name, this.format(format, arg1, arg2));
    }
    
    public void info(final String format, final Object[] argArray) {
        Log.i(this.name, this.format(format, argArray));
    }
    
    public void info(final String msg, final Throwable t) {
        Log.i(this.name, msg, t);
    }
    
    public boolean isWarnEnabled() {
        return Log.isLoggable(this.name, 5);
    }
    
    public void warn(final String msg) {
        Log.w(this.name, msg);
    }
    
    public void warn(final String format, final Object arg) {
        Log.w(this.name, this.format(format, arg, null));
    }
    
    public void warn(final String format, final Object arg1, final Object arg2) {
        Log.w(this.name, this.format(format, arg1, arg2));
    }
    
    public void warn(final String format, final Object[] argArray) {
        Log.w(this.name, this.format(format, argArray));
    }
    
    public void warn(final String msg, final Throwable t) {
        Log.w(this.name, msg, t);
    }
    
    public boolean isErrorEnabled() {
        return Log.isLoggable(this.name, 6);
    }
    
    public void error(final String msg) {
        Log.e(this.name, msg);
    }
    
    public void error(final String format, final Object arg) {
        Log.e(this.name, this.format(format, arg, null));
    }
    
    public void error(final String format, final Object arg1, final Object arg2) {
        Log.e(this.name, this.format(format, arg1, arg2));
    }
    
    public void error(final String format, final Object[] argArray) {
        Log.e(this.name, this.format(format, argArray));
    }
    
    public void error(final String msg, final Throwable t) {
        Log.e(this.name, msg, t);
    }
    
    private String format(final String format, final Object arg1, final Object arg2) {
        return MessageFormatter.format(format, arg1, arg2).getMessage();
    }
    
    private String format(final String format, final Object[] args) {
        return MessageFormatter.arrayFormat(format, args).getMessage();
    }*/
}
