package com.qinyadan.brick.monitor.network.codec;


import com.qinyadan.brick.monitor.network.command.Command;

import io.netty.buffer.ByteBuf;

public interface CommandCodec {
	public Command decode(ByteBuf buf);
	public void encode(Command cmd, ByteBuf buf);
}
