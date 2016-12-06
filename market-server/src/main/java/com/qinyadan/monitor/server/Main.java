package com.qinyadan.monitor.server;

import com.qinyadan.monitor.server.support.NettyMonitorServer;

public class Main {

	public static void main(String[] args) {
		MonitorServer server = new NettyMonitorServer();
		server.start();
	}

}
