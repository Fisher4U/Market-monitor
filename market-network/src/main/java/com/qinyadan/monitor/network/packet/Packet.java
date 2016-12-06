package com.qinyadan.monitor.network.packet;

import io.netty.buffer.ByteBuf;

public interface Packet {

	short getPacketType();

	byte[] getPayload();

	ByteBuf toBuffer();
}
