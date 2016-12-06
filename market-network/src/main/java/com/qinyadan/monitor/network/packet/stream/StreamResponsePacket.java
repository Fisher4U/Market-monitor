package com.qinyadan.monitor.network.packet.stream;

import com.qinyadan.monitor.network.packet.PacketType;
import com.qinyadan.monitor.network.packet.PayloadPacket;
import com.qinyadan.monitor.network.util.AssertUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class StreamResponsePacket extends BasicStreamPacket {

	private final static short PACKET_TYPE = PacketType.APPLICATION_STREAM_RESPONSE;

	private final byte[] payload;

	public StreamResponsePacket(int streamChannelId, byte[] payload) {
		super(streamChannelId);

		AssertUtils.assertNotNull(payload);
		this.payload = payload;
	}

	@Override
	public short getPacketType() {
		return PACKET_TYPE;
	}

	@Override
	public byte[] getPayload() {
		return payload;
	}

	@Override
	public ByteBuf toBuffer() {
		ByteBuf header = Unpooled.buffer(2 + 4 + 4);
		header.writeShort(getPacketType());
		header.writeInt(getStreamChannelId());

		return PayloadPacket.appendPayload(header, payload);
	}

	public static StreamResponsePacket readBuffer(short packetType, ByteBuf buffer) {
		assert packetType == PACKET_TYPE;

		if (buffer.readableBytes() < 8) {
			buffer.resetReaderIndex();
			return null;
		}

		final int streamChannelId = buffer.readInt();
		final ByteBuf payload = PayloadPacket.readPayload(buffer);
		if (payload == null) {
			return null;
		}

		final StreamResponsePacket packet = new StreamResponsePacket(streamChannelId, payload.array());
		return packet;
	}

}
