package com.qinyadan.brick.monitor.network.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinyadan.brick.monitor.network.command.Command;
import com.qinyadan.brick.monitor.network.command.CommandDispatcher;

import io.netty.buffer.ByteBuf;

public class NativeCommandDecodeHandler implements DecodeHandler {

	private static final Logger logger = LoggerFactory.getLogger(NativeCommandDecodeHandler.class);

	private CommandCodec codec;

	private CommandDispatcher dispatcher;

	@Override
	public void handle(ByteBuf buf) {
		Command cmd = null;
		try {
			cmd = codec.decode(buf);
			dispatcher.dispatch(cmd);
		} catch (Exception e) {
			logger.error("Error when handling command " + cmd + "!", e);
		}
	}
}
