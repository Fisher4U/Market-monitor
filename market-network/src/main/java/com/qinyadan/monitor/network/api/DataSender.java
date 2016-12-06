package com.qinyadan.monitor.network.api;

import java.util.List;

import com.qinyadan.monitor.network.packet.Packet;

public interface DataSender {
	public boolean send(List<Packet> datas);
}
