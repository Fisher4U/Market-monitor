package com.qinyadan.brick.monitor.network.codec;

import com.qinyadan.brick.monitor.spi.message.ext.MessageTree;

import io.netty.buffer.ByteBuf;

public interface MessageCodec extends DecodeHandler{
	
   public void decode(ByteBuf buf, MessageTree tree);
   public void encode(MessageTree tree, ByteBuf buf);

}
