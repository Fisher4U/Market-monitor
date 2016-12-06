package com.qinyadan.monitor.network.test;

import com.qinyadan.monitor.network.client.MonitorClient;
import com.qinyadan.monitor.network.client.MonitorClientFactory;

public class ClientTest {

	public static void main(String[] args) {
		
		MonitorClientFactory clientFactory = new MonitorClientFactory();
		MonitorClient client = clientFactory.connect("127.0.0.1", 8889);
		String msg = "Hello world!";
		for(int i= 0;i<1000;i++){
			client.send((msg+i).getBytes());
		}

	}

}
