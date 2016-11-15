package com.qinyadan.brick.monitor.spi.message.internal;

import com.qinyadan.brick.monitor.spi.message.Heartbeat;

public class DefaultHeartbeat extends AbstractMessage implements Heartbeat {
	
	private MessageManager manager;

	public DefaultHeartbeat(String type, String name) {
		super(type, name);
	}

	public DefaultHeartbeat(String type, String name, MessageManager manager) {
		super(type, name);
		this.manager = manager;
   }

	@Override
	public void complete() {
		setCompleted(true);
		if (manager != null) {
			manager.add(this);
		}
	}
}
