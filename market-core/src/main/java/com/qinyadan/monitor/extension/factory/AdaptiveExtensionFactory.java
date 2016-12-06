package com.qinyadan.monitor.extension.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.qinyadan.monitor.extension.Adaptive;
import com.qinyadan.monitor.extension.ExtensionFactory;
import com.qinyadan.monitor.extension.ExtensionLoader;

/**
 * AdaptiveExtensionFactory
 * 
 * @author william.liangf
 */
@Adaptive
public class AdaptiveExtensionFactory implements ExtensionFactory {
    
    private final List<ExtensionFactory> factories;
    
    public AdaptiveExtensionFactory() {
        ExtensionLoader<ExtensionFactory> loader = ExtensionLoader.getExtensionLoader(ExtensionFactory.class);
        List<ExtensionFactory> list = new ArrayList<>();
        for (String name : loader.getSupportedExtensions()) {
            list.add(loader.getExtension(name));
        }
        factories = Collections.unmodifiableList(list);
    }

    public <T> T getExtension(Class<T> type, String name) {
        for (ExtensionFactory factory : factories) {
            T extension = factory.getExtension(type, name);
            if (extension != null) {
                return extension;
            }
        }
        return null;
    }

}
