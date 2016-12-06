package com.qinyadan.monitor.network.util;

import java.util.concurrent.TimeUnit;

import io.netty.util.HashedWheelTimer;

public class TimerFactory {

	public static HashedWheelTimer createHashedWheelTimer(String threadName, long tickDuration, TimeUnit unit,
			int ticksPerWheel) {
		final PinpointThreadFactory threadFactory = new PinpointThreadFactory(threadName, true);
		return createHashedWheelTimer(threadFactory, tickDuration, unit, ticksPerWheel);
	}

	public static HashedWheelTimer createHashedWheelTimer(PinpointThreadFactory threadFactory, long tickDuration,
			TimeUnit unit, int ticksPerWheel) {
		return new HashedWheelTimer(threadFactory, tickDuration, unit, ticksPerWheel);
	}

}
