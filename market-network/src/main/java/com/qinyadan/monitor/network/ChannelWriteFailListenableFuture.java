package com.qinyadan.monitor.network;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class ChannelWriteFailListenableFuture<T> extends DefaultFuture<T> implements ChannelFutureListener {

	public ChannelWriteFailListenableFuture() {
		super(3000);
	}

	public ChannelWriteFailListenableFuture(long timeoutMillis) {
		super(timeoutMillis);
	}

	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		if (!future.isSuccess()) {
			// io write fail
			this.setFailure(future.cause());
		}
	}
}
