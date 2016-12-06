package com.qinyadan.monitor.protocol.support.internal;

public class MessageId {

	private String application;

	private String ipAddressInHex;

	private int hour;

	private int index;

	public MessageId(String application, String ipAddressInHex, int hour, int index) {
		this.application = application;
		this.ipAddressInHex = ipAddressInHex;
		this.hour = hour;
		this.index = index;

		validate(application);
	}

	public static MessageId parse(String messageId) {
		int index = -1;
		int hour = -1;
		String ipAddressInHex = null;
		String domain = null;
		int len = messageId == null ? 0 : messageId.length();
		int part = 4;
		int end = len;

		try {
			for (int i = end - 1; i >= 0; i--) {
				char ch = messageId.charAt(i);

				if (ch == '-') {
					switch (part) {
					case 4:
						index = Integer.parseInt(messageId.substring(i + 1, end));
						end = i;
						part--;
						break;
					case 3:
						hour = Integer.parseInt(messageId.substring(i + 1, end));
						end = i;
						part--;
						break;
					case 2:
						ipAddressInHex = messageId.substring(i + 1, end);
						domain = messageId.substring(0, i);
						part--;
						break;
					default:
						break;
					}
				}
			}
		} catch (NumberFormatException e) {
			throw new RuntimeException("Invalid message ID format: " + messageId, e);
		}

		if (domain == null || ipAddressInHex == null || hour < 0 || index < 0) {
			throw new RuntimeException("Invalid message ID format: " + messageId);
		} else {
			return new MessageId(domain, ipAddressInHex, hour, index);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MessageId) {
			MessageId o = (MessageId) obj;

			if (!this.application.equals(o.application)) {
				return false;
			}

			if (!this.ipAddressInHex.equals(o.ipAddressInHex)) {
				return false;
			}

			if (this.hour != o.hour) {
				return false;
			}

			if (this.index != o.index) {
				return false;
			}

			return true;
		}

		return false;
	}

	public String getApplication() {
		return application;
	}

	public int getHour() {
		return hour;
	}

	public int getIndex() {
		return index;
	}

	public String getIpAddress() {
		StringBuilder sb = new StringBuilder();
		String local = this.ipAddressInHex;
		int length = local.length();

		for (int i = 0; i < length; i += 2) {
			char ch1 = local.charAt(i);
			char ch2 = local.charAt(i + 1);
			int value = 0;

			if (ch1 >= '0' && ch1 <= '9') {
				value += (ch1 - '0') << 4;
			} else {
				value += ((ch1 - 'a') + 10) << 4;
			}

			if (ch2 >= '0' && ch2 <= '9') {
				value += ch2 - '0';
			} else {
				value += (ch2 - 'a') + 10;
			}

			if (sb.length() > 0) {
				sb.append('.');
			}

			sb.append(value);
		}

		return sb.toString();
	}

	public String getIpAddressInHex() {
		return this.ipAddressInHex;
	}

	public int getIpAddressValue() {
		String local = this.ipAddressInHex;
		int length = local.length();
		int ip = 0;

		for (int i = 0; i < length; i += 2) {
			char ch1 = local.charAt(i);
			char ch2 = local.charAt(i + 1);
			int value = 0;

			if (ch1 >= '0' && ch1 <= '9') {
				value += (ch1 - '0') << 4;
			} else {
				value += ((ch1 - 'a') + 10) << 4;
			}

			if (ch2 >= '0' && ch2 <= '9') {
				value += ch2 - '0';
			} else {
				value += (ch2 - 'a') + 10;
			}

			ip = (ip << 8) + value;
		}

		return ip;
	}

	public long getTimestamp() {
		return this.hour * 3600 * 1000L;
	}

	@Override
	public int hashCode() {
		int result = 1;

		result = 31 * result + ((this.application == null) ? 0 : this.application.hashCode());
		result = 31 * result + ((this.ipAddressInHex == null) ? 0 : this.ipAddressInHex.hashCode());
		result = 31 * result + this.hour;
		result = 31 * result + this.index;

		return result;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(this.application.length() + 24);

		sb.append(this.application);
		sb.append('-');
		sb.append(this.ipAddressInHex);
		sb.append('-');
		sb.append(this.hour);
		sb.append('-');
		sb.append(this.index);

		return sb.toString();
	}

	void validate(String application) {
		int len = application.length();

		for (int i = 0; i < len; i++) {
			char ch = application.charAt(i);

			if (Character.isJavaIdentifierPart(ch) || ch == '.') {
				continue;
			} else {
				throw new RuntimeException("Invalid application of message ID: " + this);
			}
		}
	}
}
