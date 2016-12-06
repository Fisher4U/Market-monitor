package com.qinyadan.monitor.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ServerClosePacket extends BasicPacket {

	public static final ServerClosePacket DEFAULT_SERVER_CLOSE_PACKET = new ServerClosePacket();
	private static final byte[] DEFAULT_SERVER_CLOSE_PACKET_BUFFER;

	static {
		ByteBuf buffer = Unpooled.buffer(6);
		buffer.writeShort(PacketType.CONTROL_SERVER_CLOSE);
		buffer.writeInt(-1);
		DEFAULT_SERVER_CLOSE_PACKET_BUFFER = buffer.array();
	}

	@Override
	public short getPacketType() {
		return PacketType.CONTROL_SERVER_CLOSE;
	}

	@Override
	public ByteBuf toBuffer() {
		if (DEFAULT_SERVER_CLOSE_PACKET == this) {
			return Unpooled.wrappedBuffer(DEFAULT_SERVER_CLOSE_PACKET_BUFFER);
		}

		ByteBuf header = Unpooled.buffer(6);
		header.writeShort(PacketType.CONTROL_SERVER_CLOSE);
		return PayloadPacket.appendPayload(header, payload);
	}

	public static ServerClosePacket readBuffer(short packetType, ByteBuf buffer) {
		assert packetType == PacketType.CONTROL_SERVER_CLOSE;

		if (buffer.readableBytes() < 4) {
			buffer.resetReaderIndex();
			return null;
		}

		final ByteBuf payload = PayloadPacket.readPayload(buffer);
		if (payload == null) {
			return null;
		}
		final ServerClosePacket requestPacket = new ServerClosePacket();

		return requestPacket;

	}

	@Override
	public String toString() {
		return "ServerClosePacket";
	}
}
