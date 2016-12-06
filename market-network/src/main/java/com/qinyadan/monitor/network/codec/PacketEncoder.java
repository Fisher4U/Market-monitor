package com.qinyadan.monitor.network.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinyadan.monitor.network.packet.Packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
		if (!(msg instanceof Packet)) {
			logger.error("invalid packet:{} channel:{}", msg, ctx.channel());
			return;
		}
		out.writeBytes(msg.toBuffer());
	}
}
