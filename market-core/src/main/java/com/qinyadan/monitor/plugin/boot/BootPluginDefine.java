package com.qinyadan.monitor.plugin.boot;


import com.qinyadan.monitor.plugin.IPlugin;
import com.qinyadan.monitor.plugin.PluginException;

import net.bytebuddy.dynamic.DynamicType;

public abstract class BootPluginDefine implements IPlugin {

    @Override
    public void define(DynamicType.Builder<?> builder) throws PluginException {
        this.boot();
    }

    protected abstract void boot() throws BootException;

}
