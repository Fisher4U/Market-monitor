package com.qinyadan.brick.monitor.config;

import java.io.File;
import java.util.List;

import com.qinyadan.brick.monitor.domain.Domain;
import com.qinyadan.brick.monitor.domain.Server;


public interface ClientConfigManager {

	public Domain getDomain();

	public int getMaxMessageLength();

	public long getServerAddressRefreshInterval();

	public String getServerConfigUrl();

	public List<Server> getServers();

	public int getTaggedTransactionCacheSize();

	public void initialize(File configFile) throws Exception;

	public boolean isCatEnabled();

	public boolean isDumpLocked();
}