package com.qinyadan.brick.monitor.agent.stats;

public interface ApdexStats extends StatsBase {

	public void recordApdexFrustrated();

	public void recordApdexResponseTime(long paramLong1, long paramLong2);

	public int getApdexSatisfying();

	public int getApdexTolerating();

	public int getApdexFrustrating();
}
