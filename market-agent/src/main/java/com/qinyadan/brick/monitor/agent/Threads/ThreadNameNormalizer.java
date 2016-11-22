package com.qinyadan.brick.monitor.agent.Threads;

import java.util.regex.Pattern;

import com.qinyadan.brick.monitor.agent.config.AgentConfig;

public class ThreadNameNormalizer {
	private static final String DEFAULT_PATTERN = "((?<=[\\W_]|^)([0-9a-fA-F]){4,}(?=[\\W_]|$))|\\d+";
	private final Pattern replacementPattern;
	private final ThreadNames threadNames;

	public ThreadNameNormalizer(AgentConfig config, ThreadNames threadNames) {
		this(config.getProperty("thread_sampler.name_pattern",DEFAULT_PATTERN), threadNames);
	}

	public ThreadNameNormalizer(ThreadNames threadNames) {
		this("((?<=[\\W_]|^)([0-9a-fA-F]){4,}(?=[\\W_]|$))|\\d+", threadNames);
	}

	private ThreadNameNormalizer(String pattern, ThreadNames threadNames) {
		this.replacementPattern = Pattern.compile(pattern);
		this.threadNames = threadNames;
	}

	public String getNormalizedThreadName(BasicThreadInfo threadInfo) {
		return getNormalizedThreadName(this.threadNames.getThreadName(threadInfo));
	}

	protected String getNormalizedThreadName(String name) {
		String renamed = this.replacementPattern.matcher(name).replaceAll("#");

		return renamed.replace('/', '-');
	}
}
