package com.qinyadan.monitor.network;

import com.qinyadan.monitor.network.packet.RequestPacket;
import com.qinyadan.monitor.network.packet.SendPacket;

public interface MessageListener {

	void handleSend(SendPacket sendPacket, DuplexSocket duplexSocket);

	void handleRequest(RequestPacket requestPacket, DuplexSocket duplexSocket);

}
