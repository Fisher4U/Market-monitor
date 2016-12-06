package com.qinyadan.monitor.network.client;

import java.net.SocketAddress;

import com.qinyadan.monitor.network.Future;
import com.qinyadan.monitor.network.ResponseMessage;
import com.qinyadan.monitor.network.cluster.ClusterOption;
import com.qinyadan.monitor.network.common.SocketStateCode;
import com.qinyadan.monitor.network.stream.ClientStreamChannel;
import com.qinyadan.monitor.network.stream.ClientStreamChannelContext;
import com.qinyadan.monitor.network.stream.ClientStreamChannelMessageListener;
import com.qinyadan.monitor.network.stream.StreamChannelContext;
import com.qinyadan.monitor.network.stream.StreamChannelStateChangeEventHandler;

public interface DuplexClient {

    void setConnectSocketAddress(SocketAddress address);

    void initReconnect();

    ConnectFuture getConnectFuture();
    
    void setPinpointClient(MonitorClient monitorClient);

    void sendSync(byte[] bytes);

    @SuppressWarnings("rawtypes")
	Future sendAsync(byte[] bytes);

    void close();

    void send(byte[] bytes);

    Future<ResponseMessage> request(byte[] bytes);

    void response(int requestId, byte[] payload);

    ClientStreamChannelContext openStream(byte[] payload, ClientStreamChannelMessageListener messageListener);
   
    ClientStreamChannelContext openStream(byte[] payload, ClientStreamChannelMessageListener messageListener, StreamChannelStateChangeEventHandler<ClientStreamChannel> stateChangeListener);

    StreamChannelContext findStreamChannel(int streamChannelId);
    
    void sendPing();

    boolean isConnected();

    boolean isSupportServerMode();
    
    SocketStateCode getCurrentStateCode();

    SocketAddress getRemoteAddress();

    ClusterOption getLocalClusterOption();
    
    ClusterOption getRemoteClusterOption();

}
