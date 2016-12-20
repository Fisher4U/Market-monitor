package com.qinyadan.monitor.agent;

import com.qinyadan.monitor.extension.ExtensionLoader;

public class PluginsManager {

	public static void loadPlugins() {
		// 加载paasplugin，采集agent所在机器的有关信息
		Plugin paasPlugin = ExtensionLoader.getExtensionLoader(Plugin.class).getExtension("paas");
		// 加载tomcat监控插件
		Plugin tomcatPlugin = ExtensionLoader.getExtensionLoader(Plugin.class).getExtension("tomcat");
	}

}
