package com.qinyadan.market.container.jvm;

import com.qinyadan.monitor.agent.Container;
import com.qinyadan.monitor.extension.ExtensionLoader;

public class JvmContailer implements Container{

	@Override
	public void start() {
		GCService gcService = ExtensionLoader.getExtensionLoader(GCService.class).getExtension("cm1");
		gcService.collect();
	}

	@Override
	public void stop() {
		
	}

}
