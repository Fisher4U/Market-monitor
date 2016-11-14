package com.qinyadan.brick.serverbootstrap.test;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.qinyadan.brick.monitor.network.TcpSocketSender;
import com.qinyadan.brick.monitor.spi.message.ext.support.DefaultMessageTree;
import com.qinyadan.brick.monitor.spi.message.internal.DefaultHeartbeat;

public class TestTcpSender {
	
	public static void main(String[] args) {
		List<InetSocketAddress> address = new ArrayList<InetSocketAddress>();
		InetSocketAddress ii = new InetSocketAddress("127.0.0.1",8889);
		address.add(ii);
		
		TcpSocketSender sender = new TcpSocketSender();
		sender.setServerAddresses(address);
		sender.initialize();
		DefaultMessageTree message = new DefaultMessageTree();
		message.setDomain("localhost");
		message.setMessage(new DefaultHeartbeat("type","name"));
		for(int i =0;i<1000;i++){
			sender.send(message);
		}
	}

}
