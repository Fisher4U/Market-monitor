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

	private TcpSocketSender tcpSocketSender = new TcpSocketSender();

	public DefaultTransportManager(ClientConfigManager configManager) {
		List<Server> servers = configManager.getServers();
		if (!configManager.isCatEnabled()) {
			this.tcpSocketSender = null;
			logger.warn("Market monitor was DISABLED due to not initialized yet!");
		} else {
			List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();
			for (Server server : servers) {
				if (server.isEnabled()) {
					addresses.add(new InetSocketAddress(server.getIp(), server.getPort()));
				}
			}
			logger.info("Remote Market monitor servers: " + addresses);
			if (addresses.isEmpty()) {
				throw new RuntimeException("All servers in configuration are disabled!\r\n" + servers);
			} else {
				this.tcpSocketSender.setServerAddresses(addresses);
				this.tcpSocketSender.initialize();
			}
		}
	}

	@Override
	public MessageSender getSender() {
		return tcpSocketSender;
	}

}
