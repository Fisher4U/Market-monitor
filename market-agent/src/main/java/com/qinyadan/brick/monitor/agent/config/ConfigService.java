package com.qinyadan.brick.monitor.agent.config;

import java.util.Map;

import com.qinyadan.brick.monitor.agent.service.Service;

public interface ConfigService extends Service {

	public  Map<String, Object> getLocalSettings();

	public  Map<String, Object> getSanitizedLocalSettings();

	public  AgentConfig getDefaultAgentConfig();

	public  AgentConfig getLocalAgentConfig();

	public  AgentConfig getAgentConfig(String paramString);

}
