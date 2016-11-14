package com.qinyadan.brick.monitor.network.codec;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

public class DefaultDecodeHandlerManager implements DecodeHandlerManager {
	
	private Map<String, DecodeHandler> cached = new HashMap<String, DecodeHandler>();
	
	public DefaultDecodeHandlerManager(){
		Map<String, DecodeHandler> map = null ; //lookupMap(DecodeHandler.class);
		cached.putAll(map);
	}

	@Override
	public DecodeHandler getHandler(ByteBuf buf) {
		byte[] data = new byte[3];
		buf.getBytes(0, data);
		String hint = new String(data);
		return cached.get(hint);
	}

}
