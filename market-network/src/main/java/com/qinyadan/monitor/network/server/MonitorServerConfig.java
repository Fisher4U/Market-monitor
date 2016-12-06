package com.qinyadan.monitor.network.server;

import com.qinyadan.monitor.network.cluster.ClusterOption;
import com.qinyadan.monitor.network.stream.ServerStreamChannelMessageListener;

import io.netty.util.Timer;

public interface MonitorServerConfig {

    long getDefaultRequestTimeout();
    
    Timer getHealthCheckTimer();
    
    Timer getRequestManagerTimer();

    ServerMessageListener getMessageListener();
    
    ServerStreamChannelMessageListener getStreamMessageListener();

    ClusterOption getClusterOption();

}
