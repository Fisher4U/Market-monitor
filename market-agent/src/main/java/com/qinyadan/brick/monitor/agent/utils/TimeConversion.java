package com.qinyadan.brick.monitor.agent.utils;

import java.util.concurrent.TimeUnit;

public class TimeConversion {
	
	public static final long NANOSECONDS_PER_SECOND = 1000000000L;
	public static final float NANOSECONDS_PER_SECOND_FLOAT = 1.0E9F;
	public static final long MICROSECONDS_PER_SECOND = 1000000L;
	public static final long MILLISECONDS_PER_SECOND = 1000L;

	public static double convertMillisToSeconds(double millis) {
		return millis / 1000.0D;
	}

	public static double convertNanosToSeconds(double nanos) {
		return nanos / 1.0E9D;
	}

	public static long convertSecondsToMillis(double seconds) {
		return (long)(seconds * 1000.0D);
	}

	public static long convertSecondsToNanos(double seconds) {
		return (long)(seconds * 1.0E9D);
	}

	public static long convertToMilliWithLowerBound(long sourceValue, TimeUnit sourceTimeUnit, long lowerBoundMilli) {
		sourceValue = TimeUnit.MILLISECONDS.convert(sourceValue, sourceTimeUnit);
		return sourceValue < lowerBoundMilli ? lowerBoundMilli : sourceValue;
	}
}
