package com.qinyadan.brick.monitor.agent;

import java.util.List;
import java.util.Map;

import com.qinyadan.brick.monitor.agent.service.AbstractService;
import com.qinyadan.brick.monitor.agent.stats.StatsEngine;

public class RPMService extends AbstractService implements IRPMService {
	
	//private TransportManager TransportManager;
	private volatile boolean connected = false;
	
	protected RPMService(String name) {
		super(name);
		//ClientConfigManager config = new DefaultClientConfigManager();
		//this.TransportManager = new DefaultTransportManager(config);
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Map<String, Object> launch() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHostString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void harvest(StatsEngine paramStatsEngine) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public List<List<?>> getAgentCommands() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendCommandResults(Map<Long, Object> paramMap) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public String getApplicationName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isMainApp() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getConnectionTimestamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void doStart() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void doStop() throws Exception {
		// TODO Auto-generated method stub

	}
}
