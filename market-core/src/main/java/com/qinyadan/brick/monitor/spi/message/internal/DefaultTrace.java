package com.qinyadan.brick.monitor.spi.message.internal;

import com.qinyadan.brick.monitor.spi.message.Trace;

public class DefaultTrace extends AbstractMessage implements Trace {
	
	private MessageManager m_manager;

	public DefaultTrace(String type, String name) {
		super(type, name);
	}

	public DefaultTrace(String type, String name, MessageManager manager) {
		super(type, name);

		m_manager = manager;
	}

	@Override
	public void complete() {
		setCompleted(true);

		if (m_manager != null) {
			m_manager.add(this);
		}
	}
}
