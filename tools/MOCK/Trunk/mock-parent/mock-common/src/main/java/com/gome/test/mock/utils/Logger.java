package com.gome.test.mock.utils;

import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

/**
 * delegate slf4j
 *
 * But why need do this?
 *
 */
public class Logger implements org.slf4j.Logger {
    private final org.slf4j.Logger delegate;

    public Logger(Class<?> clazz) {
        LoadUtils.initLog4jProperties();
        this.delegate = LoggerFactory.getLogger(clazz);
    }

    @Override
    public void debug(String arg0) {
        this.delegate.debug(arg0);
    }

    @Override
    public void debug(String arg0, Object arg1) {
        this.delegate.debug(arg0, arg1);
    }

    @Override
    public void debug(String arg0, Object... arg1) {
        this.delegate.debug(arg0, arg1);
    }

    @Override
    public void debug(String arg0, Throwable arg1) {
        this.delegate.debug(arg0, arg1);
    }

    @Override
    public void debug(Marker arg0, String arg1) {
        this.delegate.debug(arg0, arg1);
    }

    @Override
    public void debug(String arg0, Object arg1, Object arg2) {
        this.delegate.debug(arg0, arg1, arg2);
    }

    @Override
    public void debug(Marker arg0, String arg1, Object arg2) {
        this.delegate.debug(arg0, arg1, arg2);
    }

    @Override
    public void debug(Marker arg0, String arg1, Object... arg2) {
        this.delegate.debug(arg0, arg1, arg2);
    }

    @Override
    public void debug(Marker arg0, String arg1, Throwable arg2) {
        this.delegate.debug(arg0, arg1, arg2);
    }

    @Override
    public void debug(Marker arg0, String arg1, Object arg2, Object arg3) {
        this.delegate.debug(arg0, arg1, arg2, arg3);
    }

    @Override
    public void error(String arg0) {
        this.delegate.error(arg0);
    }

    @Override
    public void error(String arg0, Object arg1) {
        this.delegate.error(arg0, arg1);
    }

    @Override
    public void error(String arg0, Object... arg1) {
        this.delegate.error(arg0, arg1);
    }

    @Override
    public void error(String arg0, Throwable arg1) {
        this.delegate.error(arg0, arg1);
    }

    @Override
    public void error(Marker arg0, String arg1) {
        this.delegate.error(arg0, arg1);
    }

    @Override
    public void error(String arg0, Object arg1, Object arg2) {
        this.delegate.error(arg0, arg1, arg2);
    }

    @Override
    public void error(Marker arg0, String arg1, Object arg2) {
        this.delegate.error(arg0, arg1, arg2);
    }

    @Override
    public void error(Marker arg0, String arg1, Object... arg2) {
        this.delegate.error(arg0, arg1, arg2);
    }

    @Override
    public void error(Marker arg0, String arg1, Throwable arg2) {
        this.delegate.error(arg0, arg1, arg2);
    }

    @Override
    public void error(Marker arg0, String arg1, Object arg2, Object arg3) {
        this.delegate.error(arg0, arg1, arg2, arg3);
    }

    @Override
    public String getName() {
        return this.delegate.getName();
    }

    @Override
    public void info(String arg0) {
        this.delegate.info(arg0);
    }

    @Override
    public void info(String arg0, Object arg1) {
        this.delegate.info(arg0, arg1);
    }

    @Override
    public void info(String arg0, Object... arg1) {
        this.delegate.info(arg0, arg1);
    }

    @Override
    public void info(String arg0, Throwable arg1) {
        this.delegate.info(arg0, arg1);
    }

    @Override
    public void info(Marker arg0, String arg1) {
        this.delegate.info(arg0, arg1);
    }

    @Override
    public void info(String arg0, Object arg1, Object arg2) {
        this.delegate.info(arg0, arg1, arg2);
    }

    @Override
    public void info(Marker arg0, String arg1, Object arg2) {
        this.delegate.info(arg0, arg1, arg2);
    }

    @Override
    public void info(Marker arg0, String arg1, Object... arg2) {
        this.delegate.info(arg0, arg1, arg2);
    }

    @Override
    public void info(Marker arg0, String arg1, Throwable arg2) {
        this.delegate.info(arg0, arg1, arg2);
    }

    @Override
    public void info(Marker arg0, String arg1, Object arg2, Object arg3) {
        this.delegate.info(arg0, arg1, arg2, arg3);
    }

    @Override
    public boolean isDebugEnabled() {
        return this.delegate.isDebugEnabled();
    }

    @Override
    public boolean isDebugEnabled(Marker arg0) {
        return this.delegate.isDebugEnabled(arg0);
    }

    @Override
    public boolean isErrorEnabled() {
        return this.delegate.isErrorEnabled();
    }

    @Override
    public boolean isErrorEnabled(Marker arg0) {
        return this.delegate.isErrorEnabled(arg0);
    }

    @Override
    public boolean isInfoEnabled() {
        return this.delegate.isInfoEnabled();
    }

    @Override
    public boolean isInfoEnabled(Marker arg0) {
        return this.delegate.isInfoEnabled(arg0);
    }

    @Override
    public boolean isTraceEnabled() {
        return this.delegate.isTraceEnabled();
    }

    @Override
    public boolean isTraceEnabled(Marker arg0) {
        return this.delegate.isTraceEnabled(arg0);
    }

    @Override
    public boolean isWarnEnabled() {
        return this.delegate.isWarnEnabled();
    }

    @Override
    public boolean isWarnEnabled(Marker arg0) {
        return this.delegate.isWarnEnabled(arg0);
    }

    @Override
    public void trace(String arg0) {
        this.delegate.trace(arg0);
    }

    @Override
    public void trace(String arg0, Object arg1) {
        this.delegate.trace(arg0, arg1);
    }

    @Override
    public void trace(String arg0, Object... arg1) {
        this.delegate.trace(arg0, arg1);
    }

    @Override
    public void trace(String arg0, Throwable arg1) {
        this.delegate.trace(arg0, arg1);
    }

    @Override
    public void trace(Marker arg0, String arg1) {
        this.delegate.trace(arg0, arg1);
    }

    @Override
    public void trace(String arg0, Object arg1, Object arg2) {
        this.delegate.trace(arg0, arg1, arg2);
    }

    @Override
    public void trace(Marker arg0, String arg1, Object arg2) {
        this.delegate.trace(arg0, arg1, arg2);
    }

    @Override
    public void trace(Marker arg0, String arg1, Object... arg2) {
        this.delegate.trace(arg0, arg1, arg2);
    }

    @Override
    public void trace(Marker arg0, String arg1, Throwable arg2) {
        this.delegate.trace(arg0, arg1, arg2);
    }

    @Override
    public void trace(Marker arg0, String arg1, Object arg2, Object arg3) {
        this.delegate.trace(arg0, arg1, arg2, arg3);
    }

    @Override
    public void warn(String arg0) {
        this.delegate.warn(arg0);
    }

    @Override
    public void warn(String arg0, Object arg1) {
        this.delegate.warn(arg0, arg1);
    }

    @Override
    public void warn(String arg0, Object... arg1) {
        this.delegate.warn(arg0, arg1);
    }

    @Override
    public void warn(String arg0, Throwable arg1) {
        this.delegate.warn(arg0, arg1);
    }

    @Override
    public void warn(Marker arg0, String arg1) {
        this.delegate.warn(arg0, arg1);
    }

    @Override
    public void warn(String arg0, Object arg1, Object arg2) {
        this.delegate.warn(arg0, arg1, arg2);
    }

    @Override
    public void warn(Marker arg0, String arg1, Object arg2) {
        this.delegate.warn(arg0, arg1, arg2);
    }

    @Override
    public void warn(Marker arg0, String arg1, Object... arg2) {
        this.delegate.warn(arg0, arg1, arg2);
    }

    @Override
    public void warn(Marker arg0, String arg1, Throwable arg2) {
        this.delegate.warn(arg0, arg1, arg2);
    }

    @Override
    public void warn(Marker arg0, String arg1, Object arg2, Object arg3) {
        this.delegate.warn(arg0, arg1, arg2, arg3);
    }

}
