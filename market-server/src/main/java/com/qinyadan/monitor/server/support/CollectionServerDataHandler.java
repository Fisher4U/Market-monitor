package com.qinyadan.monitor.server.support;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class CollectionServerDataHandler extends SimpleChannelInboundHandler<byte[]> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
        Thread.currentThread().setName("ServerReceiver");
        // 当接受到这条消息的是空，则忽略
        System.out.println(">>>>>");
        if (msg != null && msg.length >= 0) {
        	System.out.println(msg);
        }
    }

}
