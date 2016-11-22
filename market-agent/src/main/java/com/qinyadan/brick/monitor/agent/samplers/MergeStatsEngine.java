package com.qinyadan.brick.monitor.agent.samplers;

import com.qinyadan.brick.monitor.agent.stats.StatsEngine;
import com.qinyadan.brick.monitor.agent.stats.StatsWork;

public class MergeStatsEngine implements StatsWork {

	private final String appName;
	private final StatsEngine statsEngine;

	public MergeStatsEngine(String appName, StatsEngine statsEngine) {
		this.appName = appName;
		this.statsEngine = statsEngine;
	}

	public void doWork(StatsEngine statsEngine) {
		statsEngine.mergeStats(this.statsEngine);
	}

	public String getAppName() {
		return this.appName;
	}

}
