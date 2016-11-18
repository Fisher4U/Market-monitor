package com.qinyadan.brick.monitor.agent.stats;

import java.util.concurrent.TimeUnit;

public class ResponseTimeStatsImpl extends AbstractStats implements ResponseTimeStats {

	private long total;
	private long totalExclusive;
	private long minValue;
	private long maxValue;
	private double sumOfSquares;

	@Override
	public float getTotal() {
		return (float) this.total / 1.0E9F;
	}

	@Override
	public float getTotalExclusiveTime() {
		return (float) this.totalExclusive / 1.0E9F;
	}

	@Override
	public float getMinCallTime() {

		return (float) this.minValue / 1.0E9F;
	}

	@Override
	public float getMaxCallTime() {
		return (float) this.maxValue / 1.0E9F;
	}

	@Override
	public double getSumOfSquares() {
		return this.sumOfSquares / 1.0E18D;
	}

	@Override
	public boolean hasData() {
		return (this.count > 0) || (this.total > 0L) || (this.totalExclusive > 0L);
	}

	@Override
	public void reset() {
		this.count = 0;
		this.total = (this.totalExclusive = this.minValue = this.maxValue = 0L);
		this.sumOfSquares = 0.0D;

	}

	@Override
	public void merge(StatsBase statsObj) {
		if ((statsObj instanceof ResponseTimeStatsImpl)) {
			ResponseTimeStatsImpl stats = (ResponseTimeStatsImpl) statsObj;
			if (stats.count > 0) {
				if (this.count > 0) {
					this.minValue = Math.min(this.minValue, stats.minValue);
				} else {
					this.minValue = stats.minValue;
				}
			}
			this.count += stats.count;
			this.total += stats.total;
			this.totalExclusive += stats.totalExclusive;

			this.maxValue = Math.max(this.maxValue, stats.maxValue);
			this.sumOfSquares += stats.sumOfSquares;
		}

	}

	@Override
	public void recordResponseTime(long responseTime, TimeUnit timeUnit) {
		long responseTimeInNanos = TimeUnit.NANOSECONDS.convert(responseTime, timeUnit);
		recordResponseTimeInNanos(responseTimeInNanos, responseTimeInNanos);

	}

	@Override
	public void recordResponseTime(long responseTime, long exclusiveTime, TimeUnit timeUnit) {
		long responseTimeInNanos = TimeUnit.NANOSECONDS.convert(responseTime, timeUnit);
		long exclusiveTimeInNanos = TimeUnit.NANOSECONDS.convert(exclusiveTime, timeUnit);
		recordResponseTimeInNanos(responseTimeInNanos, exclusiveTimeInNanos);

	}

	@Override
	public void recordResponseTime(int count, long totalTime, long minTime, long maxTime, TimeUnit unit) {
		long totalTimeInNanos = TimeUnit.NANOSECONDS.convert(totalTime, unit);
		this.count = count;
		this.total = totalTimeInNanos;
		this.totalExclusive = totalTimeInNanos;
		this.minValue = TimeUnit.NANOSECONDS.convert(minTime, unit);
		this.maxValue = TimeUnit.NANOSECONDS.convert(maxTime, unit);
		double totalTimeInNanosAsDouble = totalTimeInNanos;
		totalTimeInNanosAsDouble *= totalTimeInNanosAsDouble;
		this.sumOfSquares += totalTimeInNanosAsDouble;

	}

	@Override
	public void recordResponseTimeInNanos(long responseTime, long exclusiveTime) {
		double responseTimeAsDouble = responseTime;
		responseTimeAsDouble *= responseTimeAsDouble;
		this.sumOfSquares += responseTimeAsDouble;
		if (this.count > 0) {
			this.minValue = Math.min(responseTime, this.minValue);
		} else {
			this.minValue = responseTime;
		}
		this.count += 1;
		this.total += responseTime;
		this.maxValue = Math.max(responseTime, this.maxValue);
		this.totalExclusive += exclusiveTime;

	}

	@Override
	public void recordResponseTimeInNanos(long responseTime) {
		recordResponseTimeInNanos(responseTime, responseTime);

	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		ResponseTimeStatsImpl newStats = new ResponseTimeStatsImpl();
		newStats.count = this.count;
		newStats.total = this.total;
		newStats.totalExclusive = this.totalExclusive;
		newStats.minValue = this.minValue;
		newStats.maxValue = this.maxValue;
		newStats.sumOfSquares = this.sumOfSquares;
		return newStats;
	}

}
