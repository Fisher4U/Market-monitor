package com.qinyadan.brick.monitor.agent.Threads;

import java.lang.management.ThreadInfo;

public class BasicThreadInfo {
	private final long id;
	private final String name;

	public BasicThreadInfo(Thread thread) {
		this(thread.getId(), thread.getName());
	}

	public BasicThreadInfo(ThreadInfo thread) {
		this(thread.getThreadId(), thread.getThreadName());
	}

	public BasicThreadInfo(long id, String name) {
		this.id = id;
		this.name = name;
	}

	public long getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}
}
