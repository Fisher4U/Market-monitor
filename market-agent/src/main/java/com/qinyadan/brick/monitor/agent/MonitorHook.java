package com.qinyadan.brick.monitor.agent;

import com.qinyadan.brick.monitor.agent.stats.StatsEngine;

public interface MonitorHook {
	public void before(String paramString, StatsEngine statsEngine);

	public void after(String paramString);
}
