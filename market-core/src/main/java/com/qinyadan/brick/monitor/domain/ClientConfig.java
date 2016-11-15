package com.qinyadan.brick.monitor.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClientConfig {
	private String mode;

	private Boolean enabled = true;

	private Boolean dumpLocked;

	private List<Server> servers = new ArrayList<Server>();

	private Map<String, Domain> domains = new LinkedHashMap<String, Domain>();

	private Bind bind;

	private Map<String, Property> properties = new LinkedHashMap<String, Property>();

	private String baseLogDir = "target/catlog";

	private Map<String, String> dynamicAttributes = new LinkedHashMap<String, String>();
	
	
	public ClientConfig addServer(Server server) {
	      servers.add(server);
	      return this;
   }
	
	public String getMode() {
		return mode;
	}

	public ClientConfig setMode(String mode) {
		this.mode = mode;
		return this;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getDumpLocked() {
		return dumpLocked;
	}

	public void setDumpLocked(Boolean dumpLocked) {
		this.dumpLocked = dumpLocked;
	}

	public List<Server> getServers() {
		return servers;
	}

	public void setServers(List<Server> servers) {
		this.servers = servers;
	}

	public Map<String, Domain> getDomains() {
		return domains;
	}

	public void setDomains(Map<String, Domain> domains) {
		this.domains = domains;
	}

	public Bind getBind() {
		return bind;
	}

	public void setBind(Bind bind) {
		this.bind = bind;
	}

	public Map<String, Property> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Property> properties) {
		this.properties = properties;
	}

	public String getBaseLogDir() {
		return baseLogDir;
	}

	public void setBaseLogDir(String baseLogDir) {
		this.baseLogDir = baseLogDir;
	}

	public Map<String, String> getDynamicAttributes() {
		return dynamicAttributes;
	}

	public void setDynamicAttributes(Map<String, String> dynamicAttributes) {
		this.dynamicAttributes = dynamicAttributes;
	}

}
