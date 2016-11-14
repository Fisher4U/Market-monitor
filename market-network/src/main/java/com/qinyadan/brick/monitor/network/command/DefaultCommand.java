package com.qinyadan.brick.monitor.network.command;

import java.util.HashMap;
import java.util.Map;

public class DefaultCommand implements Command {

	private String name;

	private Map<String, String> arguments = new HashMap<String, String>();

	private Map<String, String> headers = new HashMap<String, String>();

	private long timestamp;

	public DefaultCommand(String name, long timestamp) {
		this.name = name;
		this.timestamp = timestamp;
	}

	@Override
	public Map<String, String> getArguments() {
		return arguments;
	}

	@Override
	public Map<String, String> getHeaders() {
		return headers;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return String.format("%s[name=%s, args=%s, timestamp=%s, headers=%s]", getClass().getSimpleName(), name,
				arguments, timestamp, headers);
	}
}
