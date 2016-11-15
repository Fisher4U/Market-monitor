package com.qinyadan.brick.monitor.network.codec;

import io.netty.buffer.ByteBuf;

public interface DecodeHandlerManager {
	public DecodeHandler getHandler(ByteBuf buf);
	
}
