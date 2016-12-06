package com.qinyadan.monitor.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ClientClosePacket extends BasicPacket {

	@Override
	public short getPacketType() {
		return PacketType.CONTROL_CLIENT_CLOSE;
	}

	@Override
	public ByteBuf toBuffer() {
		ByteBuf header = Unpooled.buffer(2 + 4);
		header.writeShort(PacketType.CONTROL_CLIENT_CLOSE);
		return PayloadPacket.appendPayload(header, payload);
	}

	public static ClientClosePacket readBuffer(short packetType, ByteBuf buffer) {
		assert packetType == PacketType.CONTROL_CLIENT_CLOSE;

		if (buffer.readableBytes() < 4) {
			buffer.resetReaderIndex();
			return null;
		}

		final ByteBuf payload = PayloadPacket.readPayload(buffer);
		if (payload == null) {
			return null;
		}
		final ClientClosePacket requestPacket = new ClientClosePacket();
		return requestPacket;

	}

	@Override
	public String toString() {
		return "ClientClosePacket";
	}
}
