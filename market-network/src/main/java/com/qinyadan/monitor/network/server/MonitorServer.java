package com.qinyadan.monitor.network.server;

import java.util.Map;

import com.qinyadan.monitor.network.DuplexSocket;
import com.qinyadan.monitor.network.common.SocketStateCode;

public interface MonitorServer extends DuplexSocket {

	void messageReceived(Object message);

	Map<Object, Object> getChannelProperties();
	
	void start();
	
}
