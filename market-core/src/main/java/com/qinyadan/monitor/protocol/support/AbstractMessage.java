package com.qinyadan.monitor.protocol.support;

import com.qinyadan.monitor.protocol.Message;
import com.qinyadan.monitor.utils.MilliSecondTimer;

public abstract class AbstractMessage implements Message{
	
	private String type;
	
	private String name;
	
	private String status;
	
	private long timestampInMillis;
	
	private CharSequence data;
	
	private boolean completed;
	
	public AbstractMessage(String type, String name) {
		this.type = String.valueOf(type);
		this.name = String.valueOf(name);
		
		timestampInMillis = MilliSecondTimer.currentTimeMillis();
	}
	
	

}
