package com.qinyadan.brick.monitor.agent.system;

import java.util.HashMap;
import java.util.Map;

import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.Sigar;

public class SystemPerformanceExecutor {

	public static final String ID = "PerformanceExecutor";

	private Sigar m_sigar = new Sigar();

	private Map<String, NetInterfaceStat> m_preIfStatMap = new HashMap<String, NetInterfaceStat>();

	private Map<String, FileSystemUsage> m_fileSystemUsageMap = new HashMap<String, FileSystemUsage>();

	
}
