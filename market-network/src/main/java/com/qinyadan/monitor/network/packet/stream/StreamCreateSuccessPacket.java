package com.qinyadan.monitor.network.packet.stream;

import com.qinyadan.monitor.network.packet.PacketType;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author koo.taejin
 */
public class StreamCreateSuccessPacket extends BasicStreamPacket {

	private final static short PACKET_TYPE = PacketType.APPLICATION_STREAM_CREATE_SUCCESS;

	public StreamCreateSuccessPacket(int streamChannelId) {
		super(streamChannelId);
	}

	@Override
	public short getPacketType() {
		return PACKET_TYPE;
	}

	@Override
	public ByteBuf toBuffer() {
		ByteBuf header = Unpooled.buffer(2 + 4);
		header.writeShort(getPacketType());
		header.writeInt(getStreamChannelId());

		return header;
	}

	public static StreamCreateSuccessPacket readBuffer(short packetType, ByteBuf buffer) {
		assert packetType == PACKET_TYPE;

		if (buffer.readableBytes() < 4) {
			buffer.resetReaderIndex();
			return null;
		}

		final int streamChannelId = buffer.readInt();

		final StreamCreateSuccessPacket packet = new StreamCreateSuccessPacket(streamChannelId);
		return packet;
	}

}
