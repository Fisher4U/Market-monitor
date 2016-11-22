package com.qinyadan.brick.monitor.agent.stats;

import java.util.List;

import com.qinyadan.brick.monitor.agent.metric.MetricName;

public interface StatsEngine {

	public Stats getStats(String paramString);

	public Stats getStats(MetricName paramMetricName);

	public void recordEmptyStats(String paramString);

	public void recordEmptyStats(MetricName paramMetricName);

	public List<MetricName> getMetricNames();

	public void clear();

	public void mergeStats(StatsEngine paramStatsEngine);

	public int getSize();

	public ResponseTimeStats getResponseTimeStats(String paramString);

	public ResponseTimeStats getResponseTimeStats(MetricName paramMetricName);
}
