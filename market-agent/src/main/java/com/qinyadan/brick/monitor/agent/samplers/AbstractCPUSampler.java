package com.qinyadan.brick.monitor.agent.samplers;

import java.lang.management.ManagementFactory;

import com.qinyadan.brick.monitor.agent.stats.StatsEngine;
import com.qinyadan.brick.monitor.agent.utils.TimeConversion;

public abstract class AbstractCPUSampler {

	private double lastCPUTimeSeconds;
	private long lastTimestampNanos;
	private final int processorCount;

	protected AbstractCPUSampler() {
		this.processorCount = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
	}

	protected abstract double getProcessCpuTime();

	protected void recordCPU(StatsEngine statsEngine) {
		double currentProcessTime = getProcessCpuTime();
		double dCPU = currentProcessTime - this.lastCPUTimeSeconds;
		this.lastCPUTimeSeconds = currentProcessTime;

		long now = System.nanoTime();
		long elapsedNanos = now - this.lastTimestampNanos;
		this.lastTimestampNanos = now;

		double elapsedTime = TimeConversion.convertNanosToSeconds(elapsedNanos);
		double utilization = dCPU / (elapsedTime * this.processorCount);

		if ((this.lastCPUTimeSeconds > 0.0D) && (dCPU >= 0.0D)) {
			if ((Double.isNaN(dCPU)) || (Double.isInfinite(dCPU))) {

			} else {
				statsEngine.getStats("CPU/User Time").recordDataPoint((float) dCPU);
			}
			if ((Double.isNaN(utilization)) || (Double.isInfinite(utilization))) {
			} else {
				statsEngine.getStats("CPU/User/Utilization").recordDataPoint((float) utilization);
			}
		}
	}
}
