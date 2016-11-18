package com.qinyadan.brick.monitor.agent.service;

import java.util.Map;

import com.qinyadan.brick.monitor.agent.HarvestService;
import com.qinyadan.brick.monitor.agent.config.ConfigService;

public interface ServiceManager extends Service {

	public Map<String, Map<String, Object>> getServicesConfiguration();

	public void addService(Service paramService);

	public Service getService(String paramString);

	public ConfigService getConfigService();
	
	public  HarvestService getHarvestService();
}
