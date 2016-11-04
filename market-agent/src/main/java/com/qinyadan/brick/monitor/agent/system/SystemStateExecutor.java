package com.qinyadan.brick.monitor.agent.system;

import org.hyperic.sigar.Sigar;

public class SystemStateExecutor {

	public static final String ID = "SystemStateExecutor";

	private Sigar m_sigar = new Sigar();

	private String m_md5String;

	private String m_hostName;

	private String m_ipAddr;

	
}
