package com.qinyadan.brick.monitor.agent;

import java.util.List;
import java.util.Map;

import com.qinyadan.brick.monitor.agent.service.Service;
import com.qinyadan.brick.monitor.agent.stats.StatsEngine;

public interface IRPMService extends Service {

	public abstract Map<String, Object> launch() throws Exception;

	public abstract String getHostString();

	public abstract void harvest(StatsEngine paramStatsEngine) throws Exception;

	public abstract List<List<?>> getAgentCommands() throws Exception;

	public abstract void sendCommandResults(Map<Long, Object> paramMap) throws Exception;

	public abstract String getApplicationName();

	public abstract boolean isMainApp();

	public abstract long getConnectionTimestamp();

}
