package com.qinyadan.monitor.network.util;

public final class StringUtils {

	private StringUtils() {
	}

	public static boolean isEmpty(String string) {
		return string == null || string.isEmpty();
	}

	public static boolean isEquals(String string1, String string2) {
		if (string1 == null) {
			return string2 == null;
		}

		return string1.equals(string2);
	}

}
