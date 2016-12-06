package com.qinyadan.monitor.network.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinyadan.monitor.network.client.WriteFailFutureListener;
import com.qinyadan.monitor.network.packet.ClientClosePacket;
import com.qinyadan.monitor.network.packet.ControlHandshakePacket;
import com.qinyadan.monitor.network.packet.ControlHandshakeResponsePacket;
import com.qinyadan.monitor.network.packet.PacketType;
import com.qinyadan.monitor.network.packet.PingPacket;
import com.qinyadan.monitor.network.packet.PongPacket;
import com.qinyadan.monitor.network.packet.RequestPacket;
import com.qinyadan.monitor.network.packet.ResponsePacket;
import com.qinyadan.monitor.network.packet.SendPacket;
import com.qinyadan.monitor.network.packet.ServerClosePacket;
import com.qinyadan.monitor.network.packet.stream.StreamClosePacket;
import com.qinyadan.monitor.network.packet.stream.StreamCreateFailPacket;
import com.qinyadan.monitor.network.packet.stream.StreamCreatePacket;
import com.qinyadan.monitor.network.packet.stream.StreamCreateSuccessPacket;
import com.qinyadan.monitor.network.packet.stream.StreamPingPacket;
import com.qinyadan.monitor.network.packet.stream.StreamPongPacket;
import com.qinyadan.monitor.network.packet.stream.StreamResponsePacket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class PacketDecoder extends LengthFieldBasedFrameDecoder {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final WriteFailFutureListener pongWriteFutureListener = new WriteFailFutureListener(logger,"pong write fail.", "pong write success.");

	private static final int FRAME_MAX_LENGTH = 16777216;

	public PacketDecoder() {
		super(FRAME_MAX_LENGTH, 0, 4, 0, 4);
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
		if (buffer.readableBytes() < 2) {
			return null;
		}
		buffer.markReaderIndex(); // 当读到剩余2字节时标记
		final short packetType = buffer.readShort(); // 剩下的2字节按short读取
		switch (packetType) {
			case PacketType.APPLICATION_SEND:
				return readSend(packetType, buffer);
			case PacketType.APPLICATION_REQUEST:
				return readRequest(packetType, buffer);
			case PacketType.APPLICATION_RESPONSE:
				return readResponse(packetType, buffer);
			case PacketType.APPLICATION_STREAM_CREATE:
				return readStreamCreate(packetType, buffer);
			case PacketType.APPLICATION_STREAM_CLOSE:
				return readStreamClose(packetType, buffer);
			case PacketType.APPLICATION_STREAM_CREATE_SUCCESS:
				return readStreamCreateSuccess(packetType, buffer);
			case PacketType.APPLICATION_STREAM_CREATE_FAIL:
				return readStreamCreateFail(packetType, buffer);
			case PacketType.APPLICATION_STREAM_RESPONSE:
				return readStreamData(packetType, buffer);
			case PacketType.APPLICATION_STREAM_PING:
				return readStreamPing(packetType, buffer);
			case PacketType.APPLICATION_STREAM_PONG:
				return readStreamPong(packetType, buffer);
			case PacketType.CONTROL_CLIENT_CLOSE:
				return readControlClientClose(packetType, buffer);
			case PacketType.CONTROL_SERVER_CLOSE:
				return readControlServerClose(packetType, buffer);
			case PacketType.CONTROL_PING:
				PingPacket pingPacket = (PingPacket) readPing(packetType, buffer);
				if (pingPacket == PingPacket.PING_PACKET) {
					sendPong(ctx.channel());
					return null;
				}
			case PacketType.CONTROL_PONG:
				logger.debug("receive pong. {}", ctx.channel());
				return readPong(packetType, buffer);
			case PacketType.CONTROL_HANDSHAKE:
				return readEnableWorker(packetType, buffer);
			case PacketType.CONTROL_HANDSHAKE_RESPONSE:
				return readEnableWorkerConfirm(packetType, buffer);
		}
		logger.error("invalid packetType received. packetType:{}, channel:{}", packetType, ctx.channel());
		return null;
	}

	private void sendPong(Channel channel) {
		// a "pong" responds to a "ping" automatically.
		logger.debug("received ping. sending pong. {}", channel);
		ChannelFuture write = channel.write(PongPacket.PONG_PACKET);
		write.addListener(pongWriteFutureListener);
	}

	private Object readControlClientClose(short packetType, ByteBuf buffer) {
		return ClientClosePacket.readBuffer(packetType, buffer);
	}

	private Object readControlServerClose(short packetType, ByteBuf buffer) {
		return ServerClosePacket.readBuffer(packetType, buffer);
	}

	private Object readPong(short packetType, ByteBuf buffer) {
		return PongPacket.readBuffer(packetType, buffer);
	}

	private Object readPing(short packetType, ByteBuf buffer) {
		return PingPacket.readBuffer(packetType, buffer);
	}

	private Object readSend(short packetType, ByteBuf buffer) {
		return SendPacket.readBuffer(packetType, buffer);
	}

	private Object readRequest(short packetType, ByteBuf buffer) {
		return RequestPacket.readBuffer(packetType, buffer);
	}

	private Object readResponse(short packetType, ByteBuf buffer) {
		return ResponsePacket.readBuffer(packetType, buffer);
	}

	private Object readStreamCreate(short packetType, ByteBuf buffer) {
		return StreamCreatePacket.readBuffer(packetType, buffer);
	}

	private Object readStreamCreateSuccess(short packetType, ByteBuf buffer) {
		return StreamCreateSuccessPacket.readBuffer(packetType, buffer);
	}

	private Object readStreamCreateFail(short packetType, ByteBuf buffer) {
		return StreamCreateFailPacket.readBuffer(packetType, buffer);
	}

	private Object readStreamData(short packetType, ByteBuf buffer) {
		return StreamResponsePacket.readBuffer(packetType, buffer);
	}

	private Object readStreamPong(short packetType, ByteBuf buffer) {
		return StreamPongPacket.readBuffer(packetType, buffer);
	}

	private Object readStreamPing(short packetType, ByteBuf buffer) {
		return StreamPingPacket.readBuffer(packetType, buffer);
	}

	private Object readStreamClose(short packetType, ByteBuf buffer) {
		return StreamClosePacket.readBuffer(packetType, buffer);
	}

	private Object readEnableWorker(short packetType, ByteBuf buffer) {
		return ControlHandshakePacket.readBuffer(packetType, buffer);
	}

	private Object readEnableWorkerConfirm(short packetType, ByteBuf buffer) {
		return ControlHandshakeResponsePacket.readBuffer(packetType, buffer);
	}

}
