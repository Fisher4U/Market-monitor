package com.qinyadan.brick.monitor.agent;

import com.qinyadan.brick.monitor.agent.service.Service;

public interface HarvestService extends Service {
	
	public void startHarvest(IRPMService iRPMService);

	public void addHarvestListener(GatherListener gatherListener);

	public void removeHarvestListener(GatherListener gatherListener);

	public void harvestNow();
}
