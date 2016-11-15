package com.qinyadan.brick.monitor.spi.message.internal;

import com.qinyadan.brick.monitor.spi.message.Message;


public abstract class AbstractMessage implements Message {
	
	private String type;

	private String name;

	private String status = "unset";

	private long timestampInMillis;

	private CharSequence data;

	private boolean completed;

	public AbstractMessage(String type, String name) {
		type = String.valueOf(type);
		name = String.valueOf(name);
		timestampInMillis = MilliSecondTimer.currentTimeMillis();
	}

	@Override
	public void addData(String keyValuePairs) {
		if (data == null) {
			data = keyValuePairs;
		} else if (data instanceof StringBuilder) {
			((StringBuilder) data).append('&').append(keyValuePairs);
		} else {
			StringBuilder sb = new StringBuilder(data.length() + keyValuePairs.length() + 16);

			sb.append(data).append('&');
			sb.append(keyValuePairs);
			data = sb;
		}
	}

	@Override
	public void addData(String key, Object value) {
		if (data instanceof StringBuilder) {
			((StringBuilder) data).append('&').append(key).append('=').append(value);
		} else {
			String str = String.valueOf(value);
			int old = data == null ? 0 : data.length();
			StringBuilder sb = new StringBuilder(old + key.length() + str.length() + 16);

			if (data != null) {
				sb.append(data).append('&');
			}

			sb.append(key).append('=').append(str);
			data = sb;
		}
	}

	@Override
	public CharSequence getData() {
		if (data == null) {
			return "";
		} else {
			return data;
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getStatus() {
		return status;
	}

	@Override
	public long getTimestamp() {
		return timestampInMillis;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public boolean isCompleted() {
		return completed;
	}

	@Override
	public boolean isSuccess() {
		return Message.SUCCESS.equals(status);
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public void setStatus(Throwable e) {
		status = e.getClass().getName();
	}

	public void setTimestamp(long timestamp) {
		timestampInMillis = timestamp;
	}

	public void setType(String type) {
		this.type = type;
	}

}
