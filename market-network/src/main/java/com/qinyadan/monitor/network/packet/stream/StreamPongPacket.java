package com.qinyadan.monitor.network.packet.stream;

import com.qinyadan.monitor.network.packet.PacketType;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class StreamPongPacket extends BasicStreamPacket {

	private final static short PACKET_TYPE = PacketType.APPLICATION_STREAM_PONG;

	private final int requestId;

	public StreamPongPacket(int streamChannelId, int requestId) {
		super(streamChannelId);
		this.requestId = requestId;
	}

	@Override
	public short getPacketType() {
		return PACKET_TYPE;
	}

	@Override
	public ByteBuf toBuffer() {
		ByteBuf header = Unpooled.buffer(2 + 4 + 4);
		header.writeShort(getPacketType());
		header.writeInt(getStreamChannelId());
		header.writeInt(requestId);

		return header;
	}

	public static StreamPongPacket readBuffer(short packetType, ByteBuf buffer) {
		assert packetType == PACKET_TYPE;

		if (buffer.readableBytes() < 4) {
			buffer.resetReaderIndex();
			return null;
		}

		final int streamChannelId = buffer.readInt();
		final int requestId = buffer.readInt();

		final StreamPongPacket packet = new StreamPongPacket(streamChannelId, requestId);
		return packet;
	}

	public int getRequestId() {
		return requestId;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName());
		sb.append("{channelId=").append(getStreamChannelId());
		sb.append(", ");
		sb.append("requestId=").append(getRequestId());
		sb.append('}');
		return sb.toString();
	}

}
