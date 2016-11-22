package com.qinyadan.brick.monitor.agent.samplers;

import com.qinyadan.brick.monitor.agent.stats.StatsEngine;

public interface MetricSampler {
	public void sample(StatsEngine paramStatsEngine);
}
