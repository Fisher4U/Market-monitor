package com.qinyadan.brick.monitor.agent.service;

import com.qinyadan.brick.monitor.agent.HarvestService;
import com.qinyadan.brick.monitor.agent.IAgent;
import com.qinyadan.brick.monitor.agent.ThreadService;
import com.qinyadan.brick.monitor.agent.config.ConfigService;
import com.qinyadan.brick.monitor.agent.stats.StatsService;

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

	public static ThreadService getThreadService() {
		return SERVICE_MANAGER.getThreadService();
	}

	public static IAgent getAgent() {
		return SERVICE_MANAGER.getAgent();
	}
	
	public static StatsService getStatsService(){
		return SERVICE_MANAGER.getStatsService();
	}
}
