package com.qinyadan.brick.monitor.agent.stats;

import java.util.List;

import com.qinyadan.brick.monitor.agent.metric.MetricName;


public interface StatsEngine {

	public abstract Stats getStats(String paramString);

	public abstract Stats getStats(MetricName paramMetricName);

	public abstract void recordEmptyStats(String paramString);

	public abstract void recordEmptyStats(MetricName paramMetricName);

	public abstract List<MetricName> getMetricNames();

	public abstract void clear();

	public abstract void mergeStats(StatsEngine paramStatsEngine);

	public abstract int getSize();
}
