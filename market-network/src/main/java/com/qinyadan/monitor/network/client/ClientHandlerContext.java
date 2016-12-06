package com.qinyadan.monitor.network.client;


import com.qinyadan.monitor.network.packet.stream.StreamPacket;
import com.qinyadan.monitor.network.stream.ClientStreamChannel;
import com.qinyadan.monitor.network.stream.ClientStreamChannelContext;
import com.qinyadan.monitor.network.stream.ClientStreamChannelMessageListener;
import com.qinyadan.monitor.network.stream.StreamChannelContext;
import com.qinyadan.monitor.network.stream.StreamChannelManager;
import com.qinyadan.monitor.network.stream.StreamChannelStateChangeEventHandler;

import io.netty.channel.Channel;

public class ClientHandlerContext {
	
    private final Channel channel;
    
    private final StreamChannelManager streamChannelManager;

    public ClientHandlerContext(Channel channel, StreamChannelManager streamChannelManager) {
        if (channel == null) {
            throw new NullPointerException("channel must not be null");
        }
        if (streamChannelManager == null) {
            throw new NullPointerException("streamChannelManager must not be null");
        }
        this.channel = channel;
        this.streamChannelManager = streamChannelManager;
    }

    public Channel getChannel() {
        return channel;
    }

    public ClientStreamChannelContext openStream(byte[] payload, ClientStreamChannelMessageListener messageListener) {
        return openStream(payload, messageListener, null);
    }

    public ClientStreamChannelContext openStream(byte[] payload, ClientStreamChannelMessageListener messageListener, StreamChannelStateChangeEventHandler<ClientStreamChannel> stateChangeListener) {
        return streamChannelManager.openStream(payload, messageListener, stateChangeListener);
    }

    public void handleStreamEvent(StreamPacket message) {
        streamChannelManager.messageReceived(message);
    }

    public void closeAllStreamChannel() {
        streamChannelManager.close();
    }

    public StreamChannelContext getStreamChannel(int streamChannelId) {
        return streamChannelManager.findStreamChannel(streamChannelId);
    }
    
}
