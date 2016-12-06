package com.qinyadan.monitor.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class RequestPacket extends BasicPacket {

	private int requestId;

	public RequestPacket() {
	}

	public RequestPacket(byte[] payload) {
		super(payload);
	}

	public RequestPacket(int requestId, byte[] payload) {
		super(payload);
		this.requestId = requestId;
	}

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	@Override
	public short getPacketType() {
		return PacketType.APPLICATION_REQUEST;
	}

	@Override
	public ByteBuf toBuffer() {

		ByteBuf header = Unpooled.buffer(2 + 4 + 4);
		header.writeShort(PacketType.APPLICATION_REQUEST);
		header.writeInt(requestId);

		return PayloadPacket.appendPayload(header, payload);

	}

	public static RequestPacket readBuffer(short packetType, ByteBuf buffer) {
		assert packetType == PacketType.APPLICATION_REQUEST;

		if (buffer.readableBytes() < 8) {
			buffer.resetReaderIndex();
			return null;
		}

		final int messageId = buffer.readInt();
		final ByteBuf payload = PayloadPacket.readPayload(buffer);
		if (payload == null) {
			return null;
		}
		final RequestPacket requestPacket = new RequestPacket(payload.array());
		requestPacket.setRequestId(messageId);
		return requestPacket;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("RequestPacket");
		sb.append("{requestId=").append(requestId);
		sb.append(", ");
		if (payload == null) {
			sb.append("payload=null");
		} else {
			sb.append("payloadLength=").append(payload.length);
		}
		sb.append('}');
		return sb.toString();
	}

}
