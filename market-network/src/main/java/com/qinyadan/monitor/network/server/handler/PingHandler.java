package com.qinyadan.monitor.network.server.handler;

import com.qinyadan.monitor.network.packet.PingPacket;
import com.qinyadan.monitor.network.server.MonitorServer;

public interface PingHandler {

    void handlePing(PingPacket pingPacket, MonitorServer monitorServer); 
}
