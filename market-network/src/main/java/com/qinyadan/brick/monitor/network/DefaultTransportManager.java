package com.qinyadan.brick.monitor.network;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinyadan.brick.monitor.config.ClientConfigManager;
import com.qinyadan.brick.monitor.domain.Server;

public class DefaultTransportManager implements TransportManager {

	private static final Logger logger = LoggerFactory.getLogger(DefaultTransportManager.class);

	private ClientConfigManager configManager;

	private TcpSocketSender tcpSocketSender;

	@Override
	public MessageSender getSender() {
		return tcpSocketSender;
	}

	public DefaultTransportManager() {
		List<Server> servers = configManager.getServers();
		if (!configManager.isCatEnabled()) {
			tcpSocketSender = null;
			logger.warn("CAT was DISABLED due to not initialized yet!");
		} else {
			List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();

			for (Server server : servers) {
				if (server.isEnabled()) {
					addresses.add(new InetSocketAddress(server.getIp(), server.getPort()));
				}
			}

			logger.info("Remote CAT servers: " + addresses);

			if (addresses.isEmpty()) {
				throw new RuntimeException("All servers in configuration are disabled!\r\n" + servers);
			} else {
				tcpSocketSender.setServerAddresses(addresses);
				tcpSocketSender.initialize();
			}
		}
	}

}
