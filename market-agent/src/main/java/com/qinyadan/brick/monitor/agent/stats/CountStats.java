package com.qinyadan.brick.monitor.agent.stats;

public interface CountStats extends StatsBase {
	public void incrementCallCount();

	public void incrementCallCount(int paramInt);

	public int getCallCount();

	public void setCallCount(int paramInt);

	public float getTotal();

	public float getTotalExclusiveTime();

	public float getMinCallTime();

	public float getMaxCallTime();

	public double getSumOfSquares();
}
