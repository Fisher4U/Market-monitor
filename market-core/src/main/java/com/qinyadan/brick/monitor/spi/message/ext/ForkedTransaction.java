package com.qinyadan.brick.monitor.spi.message.ext;

import com.qinyadan.brick.monitor.spi.message.Transaction;

public interface ForkedTransaction extends Transaction {
	
	public void fork();

	public String getForkedMessageId();
}
