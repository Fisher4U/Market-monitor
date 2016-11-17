package com.qinyadan.brick.monitor.agent;

import com.qinyadan.brick.monitor.agent.service.Service;

public interface HarvestService extends Service {
	
	public abstract void startHarvest(IRPMService paramIRPMService);

	public abstract void addHarvestListener(MonitorHook paramHarvestListener);

	public abstract void removeHarvestListener(MonitorHook paramHarvestListener);

	public abstract void harvestNow();
}
