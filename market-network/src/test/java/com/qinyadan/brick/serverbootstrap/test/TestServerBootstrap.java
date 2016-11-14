package com.qinyadan.brick.serverbootstrap.test;

import com.qinyadan.brick.monitor.config.ServerTransportConfiguration;
import com.qinyadan.brick.monitor.network.TcpSocketReceiver;

public class TestServerBootstrap {
	
	public static void main(String[] args) {
		
		ServerTransportConfiguration config = new TestServerBootstrap().new ServerConfig();
		TcpSocketReceiver server = new TcpSocketReceiver(config);
		try {
			server.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public class ServerConfig implements ServerTransportConfiguration{

		@Override
		public int getBossThreads() {
			return 1;
		}

		@Override
		public int getWorkerThreads() {
			return 4;
		}

		@Override
		public int getTcpPort() {
			return 8889;
		}
		
	}
}
