package com.qinyadan.monitor.extension.factory;

import com.qinyadan.monitor.extension.ExtensionFactory;
import com.qinyadan.monitor.extension.ExtensionLoader;
import com.qinyadan.monitor.extension.SPI;

/**
 * SpiExtensionFactory
 * 
 * @author william.liangf
 */
public class SpiExtensionFactory implements ExtensionFactory {

    public <T> T getExtension(Class<T> type, String name) {
        if (type.isInterface() && type.isAnnotationPresent(SPI.class)) {
            ExtensionLoader<T> loader = ExtensionLoader.getExtensionLoader(type);
            if (loader.getSupportedExtensions().size() > 0) {
                return loader.getAdaptiveExtension();
            }
        }
        return null;
    }

}
