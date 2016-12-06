package com.qinyadan.monitor.network.util;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;

public class LoggerFactorySetup {

	public static final Slf4JLoggerFactory LOGGER_FACTORY = new Slf4JLoggerFactory();

	public static void setupSlf4jLoggerFactory() {
		InternalLoggerFactory.setDefaultFactory(LOGGER_FACTORY);
	}
}
