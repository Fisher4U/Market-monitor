package com.qinyadan.monitor.protocol;

import com.qinyadan.monitor.protocol.support.internal.MessageId;

/**
 * 
 * 调用链
 * 
 * @author liuzhimin
 *
 */
public interface CallTree extends Cloneable {
	
	public CallTree copy();
	
	public String getApplication();
	
	public MessageId getFormatMessageId();

	public String getHostName();

	public String getIpAddress();

	public Message getMessage();

	public String getMessageId();

	public String getParentMessageId();

	public String getRootMessageId();

	public String getSessionToken();

	public String getThreadGroupName();

	public String getThreadId();

	public String getThreadName();

	public boolean isSample();

	public void setApplication(String application);

	public void setFormatMessageId(MessageId messageId);

	public void setHostName(String hostName);

	public void setIpAddress(String ipAddress);

	public void setMessage(Message message);

	public void setMessageId(String messageId);

	public void setParentMessageId(String parentMessageId);

	public void setRootMessageId(String rootMessageId);

	public void setSample(boolean sample);

	public void setSessionToken(String sessionToken);

	public void setThreadGroupName(String name);
	
	public void setThreadId(String threadId);
	
	public void setThreadName(String id);
}
