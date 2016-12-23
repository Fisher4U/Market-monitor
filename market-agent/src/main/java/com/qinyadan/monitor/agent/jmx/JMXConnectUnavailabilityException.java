package com.qinyadan.monitor.agent.jmx;

public class JMXConnectUnavailabilityException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6199661149804215226L;

	/**
	 * JMX 连接不可用状态异常
	 * 
	 * @param e
	 */
	public JMXConnectUnavailabilityException(Exception e) {
		super(e);
	}
}
