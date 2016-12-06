package com.qinyadan.monitor.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PongPacket extends BasicPacket {

    public static final PongPacket PONG_PACKET = new PongPacket();

    static {
    	ByteBuf buffer = Unpooled.buffer(2);
        buffer.writeShort(PacketType.CONTROL_PONG);
        PONG_BYTE = buffer.array();
    }

    private static final byte[] PONG_BYTE;

    public PongPacket() {
    }

    @Override
    public short getPacketType() {
        return PacketType.CONTROL_PONG;
    }

    @Override
    public ByteBuf toBuffer() {
        return Unpooled.wrappedBuffer(PONG_BYTE);
    }

    public static PongPacket readBuffer(short packetType, ByteBuf buffer) {
        assert packetType == PacketType.CONTROL_PONG;
        return PONG_PACKET;
    }

    @Override
    public String toString() {
        return "PongPacket";
    }

}
