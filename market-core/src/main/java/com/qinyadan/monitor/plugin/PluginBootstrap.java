package com.qinyadan.monitor.plugin;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.qinyadan.monitor.logger.LoggerFactory;
import com.qinyadan.monitor.logger.Logger;

import net.bytebuddy.pool.TypePool;

public class PluginBootstrap {
    private static Logger logger = LoggerFactory.getLogger(PluginBootstrap.class);

    public static TypePool CLASS_TYPE_POOL = null;

    public List<IPlugin> loadPlugins() {
        CLASS_TYPE_POOL = TypePool.Default.ofClassPath();

        PluginResourcesResolver resolver = new PluginResourcesResolver();
        List<URL> resources = resolver.getResources();

        if (resources == null || resources.size() == 0) {
            logger.info("no plugin files (monitor-plugin.properties) found, continue to start application.");
            return new ArrayList<IPlugin>();
        }

        for (URL pluginUrl : resources) {
            try {
                PluginCfg.CFG.load(pluginUrl.openStream());
            } catch (Throwable t) {
                logger.error("plugin [{}] init failure.", new Object[] {pluginUrl}, t);
            }
        }

        List<String> pluginClassList = PluginCfg.CFG.getPluginClassList();

        List<IPlugin> plugins = new ArrayList<IPlugin>();
        for (String pluginClassName : pluginClassList) {
            try {
                logger.debug("prepare to enhance class by plugin {}.", pluginClassName);
                IPlugin plugin = (IPlugin) Class.forName(pluginClassName).newInstance();
                plugins.add(plugin);
            } catch (Throwable t) {
                logger.error("prepare to enhance class by plugin [{}] failure.", new Object[] {pluginClassName}, t);
            }
        }

        return plugins;

    }


}
