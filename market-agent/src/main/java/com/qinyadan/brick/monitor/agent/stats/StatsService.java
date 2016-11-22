package com.qinyadan.brick.monitor.agent.stats;

import com.qinyadan.brick.monitor.agent.service.Service;

public interface StatsService extends Service {
	public void doStatsWork(StatsWork paramStatsWork);

	public StatsEngine getStatsEngineForHarvest(String paramString);

	//public MetricAggregator getMetricAggregator();
}
