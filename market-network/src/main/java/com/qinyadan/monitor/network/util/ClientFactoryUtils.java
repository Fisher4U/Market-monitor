package com.qinyadan.monitor.network.util;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinyadan.monitor.network.MonitorSocketException;
import com.qinyadan.monitor.network.client.MonitorClient;
import com.qinyadan.monitor.network.client.MonitorClientFactory;

public final class ClientFactoryUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientFactoryUtils.class);

	public static MonitorClient createPinpointClient(String host, int port, MonitorClientFactory clientFactory) {
		InetSocketAddress connectAddress = new InetSocketAddress(host, port);
		return createPinpointClient(connectAddress, clientFactory);
	}

	public static MonitorClient createPinpointClient(InetSocketAddress connectAddress,
			MonitorClientFactory clientFactory) {
		MonitorClient monitorClient = null;
		for (int i = 0; i < 3; i++) {
			try {
				monitorClient = clientFactory.connect(connectAddress);
				LOGGER.info("tcp connect success. remote:{}", connectAddress);
				return monitorClient;
			} catch (MonitorSocketException e) {
				LOGGER.warn("tcp connect fail. remote:{} try reconnect, retryCount:{}", connectAddress, i);
			}
		}
		LOGGER.warn("change background tcp connect mode remote:{} ", connectAddress);
		return monitorClient;
	}

}
