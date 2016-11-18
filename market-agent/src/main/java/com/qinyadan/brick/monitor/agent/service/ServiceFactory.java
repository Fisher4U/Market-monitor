package com.qinyadan.brick.monitor.agent.service;

import com.qinyadan.brick.monitor.agent.HarvestService;
import com.qinyadan.brick.monitor.agent.config.ConfigService;

public class ServiceFactory {

	private static volatile ServiceManager SERVICE_MANAGER;

	public static void setServiceManager(ServiceManager serviceManager) {
		if (serviceManager != null) {
			SERVICE_MANAGER = serviceManager;
		}
	}

	public static ServiceManager getServiceManager() {
		return SERVICE_MANAGER;
	}

	public static ConfigService getConfigService() {
		return SERVICE_MANAGER.getConfigService();
	}

	public static HarvestService getHarvestService() {
		return SERVICE_MANAGER.getHarvestService();
	}
}
