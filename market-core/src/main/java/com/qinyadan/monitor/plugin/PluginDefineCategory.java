package com.qinyadan.monitor.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qinyadan.monitor.plugin.boot.BootPluginDefine;


public class PluginDefineCategory {

    private static PluginDefineCategory pluginDefineCategory;

    private final Map<String, AbstractClassEnhancePluginDefine> classEnhancePluginDefines = new HashMap<String, AbstractClassEnhancePluginDefine>();
    private final List<BootPluginDefine>                        bootPluginDefines         = new ArrayList<BootPluginDefine>();

    private PluginDefineCategory(List<IPlugin> plugins) {
        for (IPlugin plugin : plugins) {
            if (plugin instanceof AbstractClassEnhancePluginDefine) {
                classEnhancePluginDefines.put(((AbstractClassEnhancePluginDefine) plugin).enhanceClassName(), (AbstractClassEnhancePluginDefine) plugin);
            }

            if (plugin instanceof BootPluginDefine) {
                bootPluginDefines.add((BootPluginDefine) plugin);
            }
        }
    }

    public static PluginDefineCategory category(List<IPlugin> plugins) {
        if (pluginDefineCategory == null) {
            pluginDefineCategory = new PluginDefineCategory(plugins);
        }
        return pluginDefineCategory;
    }

    public List<BootPluginDefine> getBootPluginsDefines() {
        return pluginDefineCategory.bootPluginDefines;
    }

    public Map<String, AbstractClassEnhancePluginDefine> getClassEnhancePluginDefines() {
        return pluginDefineCategory.classEnhancePluginDefines;
    }

}
