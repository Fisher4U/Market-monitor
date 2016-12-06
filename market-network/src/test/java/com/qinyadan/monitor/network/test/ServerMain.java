package com.qinyadan.monitor.network.test;

import java.io.IOException;
import java.net.UnknownHostException;

import com.qinyadan.monitor.network.server.MonitorServerBootstrap;

public class ServerMain {

	public static void main(String[] args) throws InterruptedException, UnknownHostException, IOException {
		
		MonitorServerBootstrap serverAcceptor = new MonitorServerBootstrap();
        serverAcceptor.bind("127.0.0.1", 8889);
        

        Thread.sleep(1000);
	}

}
