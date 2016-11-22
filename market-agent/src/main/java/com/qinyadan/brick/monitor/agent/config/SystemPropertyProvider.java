package com.qinyadan.brick.monitor.agent.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


public class SystemPropertyProvider {
	private static final String HEROKU_PREFIX = "NEW_RELIC_";
	private static final String HEROKU_LICENSE_KEY = "NEW_RELIC_LICENSE_KEY";
	private static final String HEROKU_APP_NAME = "NEW_RELIC_APP_NAME";
	private static final String HEROKU_LOG = "NEW_RELIC_LOG";
	private static final String HEROKU_HOST_DISPLAY_NAME = "NEW_RELIC_PROCESS_HOST_DISPLAY_NAME";
	private static final String LICENSE_KEY = "newrelic.config.license_key";
	private static final String APP_NAME = "newrelic.config.app_name";
	private static final String LOG_FILE_NAME = "newrelic.config.log_file_name";
	private static final String HOST_DISPLAY_NAME = "newrelic.config.process_host.display_name";
	private static final String NEW_RELIC_SYSTEM_PROPERTY_ROOT = "newrelic.";
	private final Map<String, String> herokuEnvVars;
	private final Map<String, String> herokuEnvVarsFlattenedMapping;
	private final Map<String, String> newRelicSystemProps;
	private final Map<String, Object> newRelicPropsWithoutPrefix;
	private final SystemProps systemProps;

	public SystemPropertyProvider() {
		this(SystemProps.getSystemProps());
	}

	public SystemPropertyProvider(SystemProps sysProps) {
		this.systemProps = sysProps;
		this.herokuEnvVars = initHerokuEnvVariables();
		this.herokuEnvVarsFlattenedMapping = initHerokuFlattenedEnvVariables();
		this.newRelicSystemProps = initNewRelicSystemProperties();
		this.newRelicPropsWithoutPrefix = createNewRelicSystemPropertiesWithoutPrefix();
	}

	private Map<String, String> initHerokuEnvVariables() {
		Map<String, String> envVars = new HashMap(6);
		envVars.put("newrelic.config.license_key", getenv("NEW_RELIC_LICENSE_KEY"));
		envVars.put("newrelic.config.app_name", getenv("NEW_RELIC_APP_NAME"));
		envVars.put("newrelic.config.log_file_name", getenv("NEW_RELIC_LOG"));
		envVars.put("newrelic.config.process_host.display_name", getenv("NEW_RELIC_PROCESS_HOST_DISPLAY_NAME"));
		return envVars;
	}

	private Map<String, String> initHerokuFlattenedEnvVariables() {
		Map<String, String> envVars = new HashMap(6);
		envVars.put("NEW_RELIC_LICENSE_KEY", "newrelic.config.license_key");
		envVars.put("NEW_RELIC_APP_NAME", "newrelic.config.app_name");
		envVars.put("NEW_RELIC_LOG", "newrelic.config.log_file_name");
		envVars.put("NEW_RELIC_PROCESS_HOST_DISPLAY_NAME", "newrelic.config.process_host.display_name");
		return envVars;
	}

	private Map<String, String> initNewRelicSystemProperties() {
		Map<String, String> nrProps = new HashMap();
		try {
			for (Map.Entry<Object, Object> entry : this.systemProps.getAllSystemPropertes().entrySet()) {
				String key = entry.getKey().toString();
				if (key.startsWith("newrelic.")) {
					String val = entry.getValue().toString();
					nrProps.put(key, val);
				}
			}
		} catch (SecurityException t) {
			// Agent.LOG.log(Level.FINE, "Unable to get system properties");
		}
		return Collections.unmodifiableMap(nrProps);
	}

	private Map<String, Object> createNewRelicSystemPropertiesWithoutPrefix() {
		Map<String, Object> nrProps = new HashMap();

		// addNewRelicSystemProperties(nrProps,
		// this.systemProps.getAllSystemPropertes().entrySet());
		// addNewRelicEnvProperties(nrProps,
		// this.systemProps.getAllEnvProperties().entrySet());

		return Collections.unmodifiableMap(nrProps);
	}

	private void addNewRelicSystemProperties(Map<String, Object> nrProps, Set<Map.Entry> entrySet) {
		for (Map.Entry<?, ?> entry : entrySet) {
			String key = entry.getKey().toString();
			if (key.startsWith("newrelic.config.")) {
				addPropertyWithoutSystemPropRoot(nrProps, key, entry.getValue());
			}
		}
	}

	private void addNewRelicEnvProperties(Map<String, Object> nrProps, Set<Map.Entry> entrySet) {
		for (Map.Entry<?, ?> entry : entrySet) {
			String key = entry.getKey().toString();
			if (key.startsWith("newrelic.config.")) {
				addPropertyWithoutSystemPropRoot(nrProps, key, entry.getValue());
			} else {
				String keyToUse = (String) this.herokuEnvVarsFlattenedMapping.get(key);
				if (keyToUse != null) {
					addPropertyWithoutSystemPropRoot(nrProps, keyToUse, entry.getValue());
				}
			}
		}
	}

	private void addPropertyWithoutSystemPropRoot(Map<String, Object> nrProps, String key, Object value) {
		String val = value.toString();
		key = key.substring("newrelic.config.".length());
		nrProps.put(key, val);
	}

	public String getEnvironmentVariable(String prop) {
		String val = (String) this.herokuEnvVars.get(prop);
		if (val != null) {
			return val;
		}
		return getenv(prop);
	}

	public String getSystemProperty(String prop) {
		return this.systemProps.getSystemProperty(prop);
	}

	private String getenv(String key) {
		return this.systemProps.getenv(key);
	}

	public Map<String, String> getNewRelicSystemProperties() {
		return this.newRelicSystemProps;
	}

	public Map<String, Object> getNewRelicPropertiesWithoutPrefix() {
		return this.newRelicPropsWithoutPrefix;
	}

	protected static abstract class SystemProps {
		static SystemProps getSystemProps() {
			try {
				System.getProperties().get("test");
				System.getenv("test");

				new SystemProps() {
					String getSystemProperty(String prop) {
						return System.getProperty(prop);
					}

					String getenv(String key) {
						return System.getenv(key);
					}

					Properties getAllSystemPropertes() {
						return System.getProperties();
					}

					Map<String, String> getAllEnvProperties() {
						return System.getenv();
					}
				};
			} catch (SecurityException e) {
				// Agent.LOG.error("Unable to access system properties because
				// of a security exception.");
			}
			return new SystemProps() {
				String getSystemProperty(String prop) {
					return null;
				}

				String getenv(String key) {
					return null;
				}

				Properties getAllSystemPropertes() {
					return new Properties();
				}

				Map<String, String> getAllEnvProperties() {
					return Collections.emptyMap();
				}
			};
		}

		abstract String getSystemProperty(String paramString);

		abstract String getenv(String paramString);

		abstract Properties getAllSystemPropertes();

		abstract Map<String, String> getAllEnvProperties();
	}
}
