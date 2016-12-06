package com.qinyadan.monitor.network.api.support;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import com.qinyadan.monitor.network.api.DataSender;
import com.qinyadan.monitor.network.client.MonitorClient;
import com.qinyadan.monitor.network.client.MonitorClientFactory;
import com.qinyadan.monitor.network.packet.Packet;
import com.qinyadan.monitor.protocol.Message;

public class NettyDataSender implements DataSender {

	private SenderStatus status = SenderStatus.FAILED;
	
	private final MonitorClientFactory clientFactory = new MonitorClientFactory();
	
	private final MonitorClient client;
	
	public NettyDataSender(String ip, int port) throws IOException {
		this(new InetSocketAddress(ip, port));
	}

	public NettyDataSender(InetSocketAddress address) throws IOException {
		this.client = clientFactory.connect(address);
	}

	@Override
	public boolean send(List<Packet> datas) {
		try {
			for(Packet data : datas){
				client.send(data.getPayload());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}


	public void close() {
		client.close();
	}

	public enum SenderStatus {
		READY, FAILED
	}

	public SenderStatus getStatus() {
		return status;
	}

	public void setStatus(SenderStatus status) {
		this.status = status;
	}
}
