package com.qinyadan.monitor.network.packet.stream;

import com.qinyadan.monitor.network.packet.Packet;

public interface StreamPacket extends Packet {

    int getStreamChannelId();

}
