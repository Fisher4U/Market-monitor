package com.qinyadan.monitor.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PayloadPacket {

	private static final ByteBuf EMPTY_BUFFER = Unpooled.buffer(0);

	public static ByteBuf readPayload(ByteBuf buffer) {
		if (buffer.readableBytes() < 4) {
			buffer.resetReaderIndex();
			return null;
		}

		final int payloadLength = buffer.readInt();
		if (payloadLength <= 0) {
			return EMPTY_BUFFER;
		}

		if (buffer.readableBytes() < payloadLength) {
			buffer.resetReaderIndex();
			return null;
		}
		return buffer.readBytes(payloadLength);
	}

	public static ByteBuf appendPayload(final ByteBuf header, final byte[] payload) {
		if (payload == null) {
			// this is also payload header
			header.writeInt(-1);
			return header;
		} else {
			header.writeInt(payload.length);
			ByteBuf payloadWrap = Unpooled.wrappedBuffer(payload);
            return Unpooled.wrappedBuffer(header, payloadWrap);
		}
	}

}
