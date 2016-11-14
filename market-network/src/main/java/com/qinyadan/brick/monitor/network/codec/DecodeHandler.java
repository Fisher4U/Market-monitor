package com.qinyadan.brick.monitor.network.codec;

import io.netty.buffer.ByteBuf;

public interface DecodeHandler {
	public void handle(ByteBuf buf);
}
