package com.qinyadan.brick.monitor.agent.config;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import com.qinyadan.brick.monitor.agent.service.AbstractService;

public class ConfigServiceImpl extends AbstractService implements ConfigService {

	private final File configFile;
	
	private final Map<String, Object> localSettings;
	
	private volatile AgentConfig defaultAgentConfig;
	
	private volatile AgentConfig localAgentConfig;
	
	private final String defaultAppName;

	protected ConfigServiceImpl(AgentConfig config, File configFile, Map<String, Object> localSettings) {
		super(ConfigService.class.getSimpleName());
		this.configFile = configFile;
		this.localSettings = Collections.unmodifiableMap(localSettings);
	    this.localAgentConfig = config;
		this.defaultAgentConfig = this.localAgentConfig;
	    this.defaultAppName = this.defaultAgentConfig.getApplicationName();
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Map<String, Object> getLocalSettings() {
		return null;
	}

	@Override
	public Map<String, Object> getSanitizedLocalSettings() {
		return null;
	}

	@Override
	public AgentConfig getDefaultAgentConfig() {
		return  this.defaultAgentConfig;
	}

	@Override
	public AgentConfig getLocalAgentConfig() {
		return null;
	}

	@Override
	public AgentConfig getAgentConfig(String paramString) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void doStart() throws Exception {

	}

	@Override
	protected void doStop() throws Exception {

	}

}
