package com.qinyadan.brick.monitor.agent;

import java.util.List;
import java.util.Map;

import com.qinyadan.brick.monitor.agent.service.Service;
import com.qinyadan.brick.monitor.agent.stats.StatsEngine;

public interface IRPMService extends Service {

	public Map<String, Object> launch() throws Exception;

	public String getHostString();

	public void harvest(StatsEngine paramStatsEngine) throws Exception;

	public List<List<?>> getAgentCommands() throws Exception;

	public void sendCommandResults(Map<Long, Object> paramMap) throws Exception;

	public String getApplicationName();

	public boolean isMainApp();

	public long getConnectionTimestamp();

}
