package com.qinyadan.monitor.agent.plugin.container;

import com.qinyadan.monitor.agent.Container;
import com.qinyadan.monitor.agent.PluginsManager;

public class PluginContainer implements Container{
	
	public final String NAME= "plugin";
	
	/**
	 * 加载当前扩展点的 所有插件实现
	 */
	@Override
	public void start() {
		PluginsManager.loadPlugins();
	}

	@Override
	public void stop() {
		
	}

}
