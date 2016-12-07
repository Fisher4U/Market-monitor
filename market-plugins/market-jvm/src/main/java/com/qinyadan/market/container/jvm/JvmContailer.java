package com.qinyadan.market.container.jvm;

import com.qinyadan.monitor.agent.Container;
import com.qinyadan.monitor.extension.ExtensionLoader;

public class JvmContailer implements Container{

	@Override
	public void start() {
		JVMService service = ExtensionLoader.getExtensionLoader(JVMService.class).getExtension("MeticJvm");
		service.collect();
	}

	@Override
	public void stop() {
		
	}

}
