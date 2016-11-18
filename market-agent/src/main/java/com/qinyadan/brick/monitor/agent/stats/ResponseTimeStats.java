package com.qinyadan.brick.monitor.agent.stats;

import java.util.concurrent.TimeUnit;

public interface ResponseTimeStats extends CountStats {

	public void recordResponseTime(long paramLong, TimeUnit paramTimeUnit);

	public void recordResponseTime(long paramLong1, long paramLong2, TimeUnit paramTimeUnit);

	public void recordResponseTime(int paramInt, long paramLong1, long paramLong2, long paramLong3,
			TimeUnit paramTimeUnit);

	public void recordResponseTimeInNanos(long paramLong1, long paramLong2);

	public void recordResponseTimeInNanos(long paramLong);
}
