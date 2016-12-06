package com.qinyadan.monitor.network.packet;

import com.qinyadan.monitor.network.common.SocketStateCode;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;

public class PingPacket extends BasicPacket {

	public static final PingPacket PING_PACKET = new PingPacket();

	// optional
	private final int pingId;
	private final byte stateVersion;
	private final byte stateCode;

	static {
		ByteBuf buffer = Unpooled.buffer(2);
		buffer.writeShort(PacketType.CONTROL_PING);
		PING_BYTE = buffer.array();
	}

	private static final byte[] PING_BYTE;

	public PingPacket() {
		this(-1);
	}

	public PingPacket(int pingId) {
		this(pingId, (byte) -1, (byte) -1);
	}

	public PingPacket(int pingId, byte stateVersion, byte stateCode) {
		this.pingId = pingId;

		this.stateVersion = stateVersion;
		this.stateCode = stateCode;
	}

	@Override
	public short getPacketType() {
		return PacketType.CONTROL_PING;
	}

	@Override
	public ByteBuf toBuffer() {
		if (pingId == -1) {
			return Unpooled.wrappedBuffer(PING_BYTE);
		} else {
			// 2 + 4 + 1 + 1
			ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(8);
			buffer.writeShort(PacketType.CONTROL_PING);
			buffer.writeInt(pingId);
			buffer.writeByte(stateVersion);
			buffer.writeByte(stateCode);
			return buffer;
		}
	}

	public static PingPacket readBuffer(short packetType, ByteBuf buffer) {
		assert packetType == PacketType.CONTROL_PING;

		if (buffer.readableBytes() == 6) {
			int pingId = buffer.readInt();
			byte stateVersion = buffer.readByte();
			byte stateCode = buffer.readByte();

			return new PingPacket(pingId, stateVersion, stateCode);
		} else {
			return PING_PACKET;
		}
	}

	public int getPingId() {
		return pingId;
	}

	public byte getStateVersion() {
		return stateVersion;
	}

	public byte getStateCode() {
		return stateCode;
	}

	@Override
	public String toString() {
		if (pingId == -1) {
			return "PingPacket";
		}

		StringBuilder sb = new StringBuilder(32);
		sb.append("PingPacket");

		if (pingId != -1) {
			sb.append("{pingId:");
			sb.append(pingId);
			sb.append("(");
			sb.append(SocketStateCode.getStateCode(stateCode));
			sb.append(")");
			sb.append("}");
		}

		return sb.toString();
	}

}
