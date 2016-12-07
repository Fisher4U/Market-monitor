package com.qinyadan.monitor.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.UnknownHostException;
import java.util.Map;

import com.qinyadan.monitor.network.DuplexSocket;
import com.qinyadan.monitor.network.packet.PingPacket;
import com.qinyadan.monitor.network.packet.RequestPacket;
import com.qinyadan.monitor.network.packet.SendPacket;
import com.qinyadan.monitor.network.server.MonitorServer;
import com.qinyadan.monitor.network.server.MonitorServerBootstrap;
import com.qinyadan.monitor.network.server.ServerMessageListener;
import com.qinyadan.monitor.server.support.EsStorage;

public class Main {

	public static void main(String[] args) throws InterruptedException, UnknownHostException, IOException {
		Storage storage = new EsStorage("127.0.0.1",9300);
		MonitorServerBootstrap serverAcceptor = new MonitorServerBootstrap(new ServerMessageListener() {
			@Override
			public void handleSend(SendPacket sendPacket, DuplexSocket duplexSocket) {
				storage.doStorage(getSerializedBytes(sendPacket.getPayload()));
			}

			@Override
			public void handleRequest(RequestPacket requestPacket, DuplexSocket duplexSocket) {
			}

			@Override
			public void handlePing(PingPacket pingPacket, MonitorServer monitorServer) {
			}
		});

		serverAcceptor.bind("127.0.0.1", 8889);

		Thread.sleep(1000);
	}
	
	private static Map getSerializedBytes(byte[] bytes)  {
		if (null == bytes )
			return null;
		try {
			ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
		    ObjectInputStream in = new ObjectInputStream(byteIn);
		    Map<Integer, String> data2 = (Map<Integer, String>) in.readObject();
		    return data2;
		} catch (IOException e) {
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
