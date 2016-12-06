package com.qinyadan.monitor.network.control;

public class ProtocolException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7649107231002850666L;

	public ProtocolException() {
	}

	public ProtocolException(String message) {
		super(message);
	}

	public ProtocolException(Throwable cause) {
		super(cause);
	}

	public ProtocolException(String message, Throwable cause) {
		super(message, cause);
	}

}
