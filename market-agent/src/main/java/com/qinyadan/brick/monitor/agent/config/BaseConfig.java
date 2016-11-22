package com.qinyadan.brick.monitor.agent.config;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;

public class BaseConfig implements Config {

	public static final String COMMA_SEPARATOR = ",";
	public static final String SEMI_COLON_SEPARATOR = ";";
	private final Map<String, Object> props;
	protected final String systemPropertyPrefix;

	public BaseConfig(Map<String, Object> props) {
		this(props, null);
	}

	public BaseConfig(Map<String, Object> props, String systemPropertyPrefix) {
		if ((systemPropertyPrefix != null) && (systemPropertyPrefix.length() == 0)) {
			throw new IllegalArgumentException("prefix must be null or non-empty");
		}
		this.props = (props == null ? Collections.emptyMap() : Collections.unmodifiableMap(props));
		this.systemPropertyPrefix = systemPropertyPrefix;
	}

	public Map<String, Object> getProperties() {
		return this.props;
	}

	protected Map<String, Object> createMap() {
		return new HashMap();
	}

	protected Map<String, Object> nestedProps(String key) {
		Object value = getProperties().get(key);
		if (value == null) {
			return null;
		}
		if ((value instanceof ServerProp)) {
			value = ((ServerProp) value).getValue();
		}
		if (Map.class.isInstance(value)) {
			return (Map) value;
		}
		String msg = MessageFormat.format(
				"Agent configuration expected nested configuration values for \"{0}\", got \"{1}\"",
				new Object[] { key, value });

		return null;
	}

	protected Object getPropertyFromSystemProperties(String name, Object defaultVal) {
		if (this.systemPropertyPrefix == null) {
			return null;
		}
		String key = getSystemPropertyKey(name);
		String result = SystemPropertyFactory.getSystemPropertyProvider().getSystemProperty(key);
		return parseValue(result);
	}

	protected String getSystemPropertyKey(String key) {
		if (key == null) {
			throw new IllegalArgumentException("key");
		}
		return this.systemPropertyPrefix + key;
	}

	protected Object getPropertyFromSystemEnvironment(String name, Object defaultVal) {
		if (this.systemPropertyPrefix == null) {
			return null;
		}
		String key = getSystemPropertyKey(name);
		String result = SystemPropertyFactory.getSystemPropertyProvider().getEnvironmentVariable(key);
		return parseValue(result);
	}

	public static Object parseValue(String val) {
		if (val == null) {
			return val;
		}
		return JSON.parse(val);
	}

	public <T> T getProperty(String key, T defaultVal) {
		Object propVal = getProperties().get(key);
		if ((propVal instanceof ServerProp)) {
			propVal = ((ServerProp) propVal).getValue();
			return (T) castValue(key, propVal, defaultVal);
		}
		Object override = getPropertyFromSystemEnvironment(key, defaultVal);
		if (override != null) {
			return (T) override;
		}
		override = getPropertyFromSystemProperties(key, defaultVal);
		if (override != null) {
			return (T) override;
		}
		return (T) castValue(key, propVal, defaultVal);
	}

	protected <T> T castValue(String key, Object value, T defaultVal) {
		try {
			T val = (T) value;
			if (val == null) {
				return defaultVal;
			}
			return val;
		} catch (ClassCastException e) {
		}
		return defaultVal;
	}

	public <T> T getProperty(String key) {
		return (T) getProperty(key, null);
	}

	protected Set<Integer> getIntegerSet(String key, Set<Integer> defaultVal) {
		Object val = getProperty(key);
		if ((val instanceof String)) {
			return Collections.unmodifiableSet(getIntegerSetFromString((String) val));
		}
		if ((val instanceof Collection)) {
			return Collections.unmodifiableSet(getIntegerSetFromCollection((Collection) val));
		}
		if ((val instanceof Integer)) {
			return Collections
					.unmodifiableSet(getIntegerSetFromCollection(Arrays.asList(new Integer[] { (Integer) val })));
		}
		return defaultVal;
	}

	protected Set<Map<String, Object>> getMapSet(String key) {
		Object val = getProperty(key);
		if ((val instanceof Collection)) {
			return Collections.unmodifiableSet(getMapSetFromCollection((Collection) val));
		}
		return Collections.emptySet();
	}

	protected Set<Map<String, Object>> getMapSetFromCollection(Collection<?> values) {
		Set<Map<String, Object>> result = new HashSet(values.size());
		for (Object value : values) {
			result.add((Map) value);
		}
		return result;
	}

	protected String getFirstString(String key, String separator) {
		Object val = getProperty(key);
		String res;
		if ((val instanceof String)) {
			String[] values = ((String) val).split(separator);
			if (values.length == 0) {
				return null;
			}
			res = values[0].trim();
			if (res.length() == 0) {
				return null;
			}
			return res;
		}
		if ((val instanceof Collection)) {
			Collection<?> values = (Collection) val;
			Iterator _res = values.iterator();
			if (_res.hasNext()) {
				Object value = _res.next();
				res = (String) value;
				res = res.trim();
				if (res.length() != 0) {
					return res;
				}
				return null;
			}
		}
		return null;
	}

	protected Collection<String> getUniqueStrings(String key) {
		return getUniqueStrings(key, ",");
	}

	protected Collection<String> getUniqueStrings(String key, String separator) {
		Object val = getProperty(key);
		if ((val instanceof String)) {
			return Collections.unmodifiableList(getUniqueStringsFromString((String) val, separator));
		}
		if ((val instanceof Collection)) {
			return Collections.unmodifiableList(getUniqueStringsFromCollection((Collection) val));
		}
		return Collections.emptySet();
	}

	public static List<String> getUniqueStringsFromCollection(Collection<?> values, String prefix) {
		List<String> result = new ArrayList(values.size());
		boolean noPrefix = (prefix == null) || (prefix.isEmpty());
		for (Object value : values) {
			String val = null;
			if ((value instanceof Integer)) {
				val = String.valueOf(value);
			} else if ((value instanceof Long)) {
				val = String.valueOf(value);
			} else {
				val = (String) value;
			}
			val = val.trim();
			if ((val.length() != 0) && (!result.contains(val))) {
				if (noPrefix) {
					result.add(val);
				} else {
					result.add(prefix + val);
				}
			}
		}
		return result;
	}

	public static List<String> getUniqueStringsFromCollection(Collection<?> values) {
		return getUniqueStringsFromCollection(values, null);
	}

	public static List<String> getUniqueStringsFromString(String valuesString, String separator, String prefix) {
		String[] valuesArray = valuesString.split(separator);
		List<String> result = new ArrayList(valuesArray.length);
		boolean noPrefix = (prefix == null) || (prefix.isEmpty());
		for (String value : valuesArray) {
			value = value.trim();
			if ((value.length() != 0) && (!result.contains(value))) {
				if (noPrefix) {
					result.add(value);
				} else {
					result.add(prefix + value);
				}
			}
		}
		return result;
	}

	public static List<String> getUniqueStringsFromString(String valuesString, String separator) {
		return getUniqueStringsFromString(valuesString, separator, null);
	}

	protected int getIntProperty(String key, int defaultVal) {
		Number val = (Number) getProperty(key);
		if (val == null) {
			return defaultVal;
		}
		return val.intValue();
	}

	protected double getDoubleProperty(String key, double defaultVal) {
		Number val = (Number) getProperty(key);
		if (val == null) {
			return defaultVal;
		}
		return val.doubleValue();
	}

	private Set<Integer> getIntegerSetFromCollection(Collection<?> values) {
		Set<Integer> result = new HashSet(values.size());
		for (Object value : values) {
			int val = ((Number) value).intValue();
			result.add(Integer.valueOf(val));
		}
		return result;
	}

	private Set<Integer> getIntegerSetFromString(String valuesString) {
		String[] valuesArray = valuesString.split(",");
		Set<Integer> result = new HashSet(valuesArray.length);
		for (String value : valuesArray) {
			value = value.trim();
			if (value.length() != 0) {
				result.add(Integer.valueOf(Integer.parseInt(value)));
			}
		}
		return result;
	}
}
