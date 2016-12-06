package com.qinyadan.monitor.network;

public class MonitorSocketException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3983437136879405214L;

	public MonitorSocketException() {
	}

	public MonitorSocketException(String message) {
		super(message);
	}

	public MonitorSocketException(String message, Throwable cause) {
		super(message, cause);
	}

	public MonitorSocketException(Throwable cause) {
		super(cause);
	}
}
