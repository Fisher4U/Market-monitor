package com.qinyadan.monitor.logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class LoggingUtil {
	
	public static String fetchThrowableStack(Throwable e) {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		e.printStackTrace(new java.io.PrintWriter(buf, true));
		String expMessage = buf.toString();
		try {
			buf.close();
		} catch (IOException e1) {
			System.err.println("Failed to close throwable stack stream.");
			e.printStackTrace();
		}
		return expMessage;
	}
}
