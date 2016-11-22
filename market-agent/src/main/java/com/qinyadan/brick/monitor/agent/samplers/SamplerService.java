package com.qinyadan.brick.monitor.agent.samplers;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

import com.qinyadan.brick.monitor.agent.service.Service;

public interface SamplerService extends Service {
	public Closeable addSampler(Runnable paramRunnable, long paramLong, TimeUnit paramTimeUnit);

	public Closeable addSampler(Runnable paramRunnable, long paramLong1, long paramLong2,
			TimeUnit paramTimeUnit);
}
