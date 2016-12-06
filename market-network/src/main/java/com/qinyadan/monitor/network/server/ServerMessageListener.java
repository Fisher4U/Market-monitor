package com.qinyadan.monitor.network.server;

import com.qinyadan.monitor.network.MessageListener;
import com.qinyadan.monitor.network.server.handler.PingHandler;

public interface ServerMessageListener extends MessageListener, PingHandler {

}
