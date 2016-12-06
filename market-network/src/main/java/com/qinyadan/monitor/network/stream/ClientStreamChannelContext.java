package com.qinyadan.monitor.network.stream;

import java.util.concurrent.atomic.AtomicReference;

import com.qinyadan.monitor.network.packet.stream.StreamCreateFailPacket;
import com.qinyadan.monitor.network.util.AssertUtils;

public class ClientStreamChannelContext extends StreamChannelContext {

    private final ClientStreamChannel clientStreamChannel;
    private final ClientStreamChannelMessageListener clientStreamChannelMessageListener;
    private final AtomicReference<StreamCreateFailPacket> createFailPacketReference;

    public ClientStreamChannelContext(ClientStreamChannel clientStreamChannel, ClientStreamChannelMessageListener clientStreamChannelMessageListener) {
        AssertUtils.assertNotNull(clientStreamChannel);
        AssertUtils.assertNotNull(clientStreamChannelMessageListener);

        this.clientStreamChannel = clientStreamChannel;
        this.clientStreamChannelMessageListener = clientStreamChannelMessageListener;
        this.createFailPacketReference = new AtomicReference<StreamCreateFailPacket>();
    }

    @Override
    public ClientStreamChannel getStreamChannel() {
        return clientStreamChannel;
    }

    public ClientStreamChannelMessageListener getClientStreamChannelMessageListener() {
        return clientStreamChannelMessageListener;
    }

    public StreamCreateFailPacket getCreateFailPacket() {
        return createFailPacketReference.get();
    }

    public boolean setCreateFailPacket(StreamCreateFailPacket createFailPacket) {
        return this.createFailPacketReference.compareAndSet(null, createFailPacket);
    }

}
