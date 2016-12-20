package com.qinyadan.monitor.agent.plugin.container;

import java.util.Set;

import com.qinyadan.monitor.agent.Container;
import com.qinyadan.monitor.agent.Plugin;
import com.qinyadan.monitor.extension.ExtensionLoader;

public class PluginContainer implements Container{
	
	public final String NAME= "plugin";
	
	/**
	 * 加载当前扩展点的 所有插件实现
	 */
	@Override
	public void start() {
		Set<String> pluginsnames = ExtensionLoader.getExtensionLoader(Plugin.class).getLoadedExtensions();
		System.out.println(pluginsnames);
	}

	@Override
	public void stop() {
		
	}

}
