package com.qinyadan.brick.monitor.agent.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class AgentConfigImpl extends BaseConfig implements AgentConfig {

	public static final String APDEX_T = "apdex_t";
	public static final String API_HOST = "api_host";
	public static final String API_PORT = "api_port";
	public static final String APP_NAME = "app_name";
	public static final String AUDIT_MODE = "audit_mode";
	public static final String BROWSER_MONITORING = "browser_monitoring";
	public static final String ATTRIBUTES = "attributes";
	public static final String CAPTURE_PARAMS = "capture_params";
	public static final String CAPTURE_MESSAGING_PARAMS = "capture_messaging_params";
	public static final String CLASS_TRANSFORMER = "class_transformer";
	public static final String CPU_SAMPLING_ENABLED = "cpu_sampling_enabled";
	public static final String CROSS_APPLICATION_TRACER = "cross_application_tracer";
	public static final String DEBUG = "newrelic.debug";
	public static final String AGENT_ENABLED = "agent_enabled";
	public static final String ENABLED = "enabled";
	public static final String ENABLE_AUTO_APP_NAMING = "enable_auto_app_naming";
	public static final String ENABLE_AUTO_TRANSACTION_NAMING = "enable_auto_transaction_naming";
	public static final String ENABLE_BOOTSTRAP_CLASS_INSTRUMENTATION = "enable_bootstrap_class_instrumentation";
	public static final String ENABLE_CLASS_RETRANSFORMATION = "enable_class_retransformation";
	public static final String ENABLE_CUSTOM_TRACING = "enable_custom_tracing";
	public static final String ENABLE_SESSION_COUNT_TRACKING = "enable_session_count_tracking";
	public static final String ERROR_COLLECTOR = "error_collector";
	public static final String HIGH_SECURITY = "high_security";
	public static final String JMX = "jmx";
	public static final String JAR_COLLECTOR = "jar_collector";
	public static final String ANALYTICS_EVENTS = "analytics_events";
	public static final String TRANSACTION_EVENTS = "transaction_events";
	public static final String CUSTOM_INSIGHT_EVENTS = "custom_insights_events";
	public static final String USE_PRIVATE_SSL = "use_private_ssl";
	public static final String REINSTRUMENT = "reinstrument";
	public static final String XRAY_SESSIONS_ENABLED = "xray_session_enabled";
	public static final String PLATFORM_INFORMATION_ENABLED = "platform_information_enabled";
	public static final String IBM = "ibm";
	public static final String TRANSACTION_NAMING_SCHEME = "transaction_naming_scheme";
	public static final String EXT_CONFIG_DIR = "extensions.dir";
	public static final String HOST = "host";
	public static final String IGNORE_JARS = "ignore_jars";
	public static final String IS_SSL = "ssl";
	public static final String LABELS = "labels";
	public static final String LANGUAGE = "language";
	public static final String LICENSE_KEY = "license_key";
	public static final String LOG_FILE_COUNT = "log_file_count";
	public static final String LOG_FILE_NAME = "log_file_name";
	public static final String LOG_FILE_PATH = "log_file_path";
	public static final String LOG_LEVEL = "log_level";
	public static final String LOG_LIMIT = "log_limit_in_kbytes";
	public static final String LOG_DAILY = "log_daily";
	public static final int MAX_USER_PARAMETERS = 64;
	public static final int MAX_USER_PARAMETER_SIZE = 255;
	public static final String KEY_TRANSACTIONS = "web_transactions_apdex";
	public static final String PORT = "port";
	public static final String PROXY_HOST = "proxy_host";
	public static final String PROXY_PORT = "proxy_port";
	public static final String PROXY_USER = "proxy_user";
	public static final String PROXY_PASS = "proxy_password";
	public static final String REPORT_SQL_PARSER_ERRORS = "report_sql_parser_errors";
	public static final String SEND_DATA_ON_EXIT = "send_data_on_exit";
	public static final String SEND_DATA_ON_EXIT_THRESHOLD = "send_data_on_exit_threshold";
	public static final String SEND_ENVIRONMENT_INFO = "send_environment_info";
	public static final String SEND_JVM_PROPERY = "send_jvm_props";
	public static final String SIMPLE_COMPRESSION_PROPERTY = "simple_compression";
	public static final String PUT_FOR_DATA_SEND_PROPERTY = "put_for_data_send";
	public static final String SLOW_SQL = "slow_sql";
	public static final String STARTUP_LOG_LEVEL = "startup_log_level";
	public static final String STDOUT = "STDOUT";
	public static final String SYNC_STARTUP = "sync_startup";
	public static final String STARTUP_TIMING = "startup_timing";
	public static final String STRIP_EXCEPTION_MESSAGES = "strip_exception_messages";
	public static final String THREAD_PROFILER = "thread_profiler";
	public static final String TRANSACTION_SIZE_LIMIT = "transaction_size_limit";
	public static final String TRANSACTION_TRACER = "transaction_tracer";
	public static final String THREAD_CPU_TIME_ENABLED = "thread_cpu_time_enabled";
	public static final String THREAD_PROFILER_ENABLED = "enabled";
	public static final String TRACE_DATA_CALLS = "trace_data_calls";
	public static final String TRIM_STATS = "trim_stats";
	public static final String UTILIZATION = "utilization";
	public static final String DATASTORE = "datastore_tracer";
	public static final String WAIT_FOR_RPM_CONNECT = "wait_for_rpm_connect";
	public static final String COMPRESSED_CONTENT_ENCODING_PROPERTY = "compressed_content_encoding";
	public static final String DEFAULT_COMPRESSED_CONTENT_ENCODING = "deflate";
	public static final double DEFAULT_APDEX_T = 1.0D;
	public static final String DEFAULT_API_HOST = "rpm.newrelic.com";
	public static final boolean DEFAULT_AUDIT_MODE = false;
	public static final boolean DEFAULT_CPU_SAMPLING_ENABLED = true;
	public static final boolean DEFAULT_ENABLED = true;
	public static final boolean DEFAULT_ENABLE_AUTO_APP_NAMING = false;
	public static final boolean DEFAULT_ENABLE_AUTO_TRANSACTION_NAMING = true;
	public static final boolean DEFAULT_ENABLE_CUSTOM_TRACING = true;
	public static final boolean DEFAULT_ENABLE_SESSION_COUNT_TRACKING = false;
	public static final boolean DEFAULT_HIGH_SECURITY = false;
	public static final boolean DEFAULT_PLATFORM_INFORMATION_ENABLED = true;
	public static final boolean DEFAULT_SIMPLE_COMPRESSION_ENABLED = false;
	public static final boolean DEFAULT_PUT_FOR_DATA_SEND_ENABLED = false;
	public static final String DEFAULT_HOST = "collector.newrelic.com";
	public static final boolean DEFAULT_IS_SSL = true;
	public static final String DEFAULT_LANGUAGE = "java";
	public static final int DEFAULT_LOG_FILE_COUNT = 1;
	public static final String DEFAULT_LOG_FILE_NAME = "newrelic_agent.log";
	public static final String DEFAULT_LOG_LEVEL = "info";
	public static final int DEFAULT_LOG_LIMIT = 0;
	public static final boolean DEFAULT_LOG_DAILY = false;
	public static final int DEFAULT_PORT = 80;
	public static final String DEFAULT_PROXY_HOST = null;
	public static final int DEFAULT_PROXY_PORT = 8080;
	public static final boolean DEFAULT_REPORT_SQL_PARSER_ERRORS = false;
	public static final boolean DEFAULT_SEND_DATA_ON_EXIT = false;
	public static final int DEFAULT_SEND_DATA_ON_EXIT_THRESHOLD = 60;
	public static final boolean DEFAULT_SEND_ENVIRONMENT_INFO = true;
	public static final int DEFAULT_SSL_PORT = 443;
	public static final boolean DEFAULT_SYNC_STARTUP = false;
	public static final boolean DEFAULT_STARTUP_TIMING = true;
	public static final boolean DEFAULT_TRACE_DATA_CALLS = false;
	public static final int DEFAULT_TRANSACTION_SIZE_LIMIT = 2000;
	public static final boolean DEFAULT_TRIM_STATS = true;
	public static final boolean DEFAULT_WAIT_FOR_RPM_CONNECT = true;
	public static final String SYSTEM_PROPERTY_ROOT = "newrelic.config.";
	public static final boolean DEFAULT_USE_PRIVATE_SSL = false;
	public static final boolean DEFAULT_XRAY_SESSIONS_ENABLED = true;
	public static final String IBM_WORKAROUND = "ibm_iv25688_workaround";
	public static final String GENERIC_JDBC_SUPPORT = "generic";
	public static final String MYSQL_JDBC_SUPPORT = "mysql";
	private final boolean highSecurity;
	private final boolean enabled;
	private final boolean debug;
	private final String licenseKey;
	private final String host;
	private final int port;
	private final Integer proxyPort;
	private final boolean isSSL;
	private final List<String> ignoreJars;
	private final String appName;
	private final List<String> appNames;
	private final boolean cpuSamplingEnabled;
	private final boolean autoAppNamingEnabled;
	private final boolean autoTransactionNamingEnabled;
	private final String logLevel;
	private final boolean logDaily;
	private final String proxyHost;
	private final String proxyUser;
	private final String proxyPass;
	private final boolean sessionCountTrackingEnabled;
	private final int transactionSizeLimit;
	private final boolean reportSqlParserErrors;
	private final boolean auditMode;
	private final boolean waitForRPMConnect;
	private final boolean startupTimingEnabled;
	private final boolean isApdexTSet;
	private final boolean sendJvmProps;
	private final boolean usePrivateSSL;
	private final boolean xRaySessionsEnabled;
	private final boolean trimStats;
	private final boolean platformInformationEnabled;
	private final Map<String, Object> flattenedProperties;
	private final HashSet<String> jdbcSupport;
	private final boolean genericJdbcSupportEnabled;
	private final int maxStackTraceLines;
	private final Config instrumentationConfig;
	private final boolean simpleCompression;
	private final String compressedContentEncoding;
	private final boolean putForDataSend;

	private AgentConfigImpl(Map<String, Object> props) {
		super(props, "newrelic.config.");

		this.highSecurity = ((Boolean) getProperty("high_security", Boolean.valueOf(false))).booleanValue();
		this.simpleCompression = ((Boolean) getProperty("simple_compression", Boolean.valueOf(false))).booleanValue();
		this.compressedContentEncoding = ((String) getProperty("compressed_content_encoding", "deflate"));
		this.putForDataSend = ((Boolean) getProperty("put_for_data_send", Boolean.valueOf(false))).booleanValue();
		this.isSSL = initSsl(this.highSecurity, props);
		this.isApdexTSet = (getProperty("apdex_t") != null);
		this.debug = Boolean.getBoolean("newrelic.debug");
		this.enabled = ((((Boolean) getProperty("enabled", Boolean.valueOf(true))).booleanValue())
				&& (((Boolean) getProperty("agent_enabled", Boolean.valueOf(true))).booleanValue()));
		this.licenseKey = ((String) getProperty("license_key"));
		this.host = ((String) getProperty("host", "collector.newrelic.com"));
		this.ignoreJars = new ArrayList(getUniqueStrings("ignore_jars", ","));
		this.logLevel = initLogLevel();
		this.logDaily = ((Boolean) getProperty("log_daily", Boolean.valueOf(false))).booleanValue();
		this.port = getIntProperty("port", this.isSSL ? 443 : 80);
		this.proxyHost = ((String) getProperty("proxy_host", DEFAULT_PROXY_HOST));
		this.proxyPort = Integer.valueOf(getIntProperty("proxy_port", 8080));
		this.proxyUser = ((String) getProperty("proxy_user"));
		this.proxyPass = ((String) getProperty("proxy_password"));
		this.appNames = new ArrayList(getUniqueStrings("app_name", ";"));
		this.appName = getFirstString("app_name", ";");
		this.cpuSamplingEnabled = ((Boolean) getProperty("cpu_sampling_enabled", Boolean.valueOf(true))).booleanValue();
		this.autoAppNamingEnabled = ((Boolean) getProperty("enable_auto_app_naming", Boolean.valueOf(false)))
				.booleanValue();
		this.autoTransactionNamingEnabled = ((Boolean) getProperty("enable_auto_transaction_naming",
				Boolean.valueOf(true))).booleanValue();
		this.transactionSizeLimit = (getIntProperty("transaction_size_limit", 2000) * 1024);
		this.sessionCountTrackingEnabled = ((Boolean) getProperty("enable_session_count_tracking",
				Boolean.valueOf(false))).booleanValue();
		this.reportSqlParserErrors = ((Boolean) getProperty("report_sql_parser_errors", Boolean.valueOf(false)))
				.booleanValue();

		this.auditMode = ((((Boolean) getProperty("trace_data_calls", Boolean.valueOf(false))).booleanValue())
				|| (((Boolean) getProperty("audit_mode", Boolean.valueOf(false))).booleanValue()));
		this.waitForRPMConnect = ((Boolean) getProperty("wait_for_rpm_connect", Boolean.valueOf(true))).booleanValue();
		this.startupTimingEnabled = ((Boolean) getProperty("startup_timing", Boolean.valueOf(true))).booleanValue();
		this.sendJvmProps = ((Boolean) getProperty("send_jvm_props", Boolean.valueOf(true))).booleanValue();
		this.usePrivateSSL = ((Boolean) getProperty("use_private_ssl", Boolean.valueOf(false))).booleanValue();
		this.xRaySessionsEnabled = ((Boolean) getProperty("xray_session_enabled", Boolean.valueOf(true)))
				.booleanValue();
		this.trimStats = ((Boolean) getProperty("trim_stats", Boolean.valueOf(true))).booleanValue();
		this.platformInformationEnabled = ((Boolean) getProperty("platform_information_enabled", Boolean.valueOf(true)))
				.booleanValue();

		this.instrumentationConfig = new BaseConfig(nestedProps("instrumentation"), "newrelic.config.instrumentation");

		this.maxStackTraceLines = ((Integer) getProperty("max_stack_trace_lines", Integer.valueOf(30))).intValue();

		String[] jdbcSupport = ((String) getProperty("jdbc_support", "generic")).split(",");
		this.jdbcSupport = new HashSet(Arrays.asList(jdbcSupport));
		this.genericJdbcSupportEnabled = this.jdbcSupport.contains("generic");

		Map<String, Object> flattenedProps = new HashMap();
		flatten("", props, flattenedProps);
		Map<String, Object> propsWithSystemProps = new HashMap();
		propsWithSystemProps
				.putAll(SystemPropertyFactory.getSystemPropertyProvider().getNewRelicPropertiesWithoutPrefix());
		flatten("", propsWithSystemProps, flattenedProps);
		checkHighSecurityPropsInFlattened(flattenedProps);
		this.flattenedProperties = Collections.unmodifiableMap(flattenedProps);
	}

	private void checkHighSecurityPropsInFlattened(Map<String, Object> flattenedProps) {
		if ((this.highSecurity) && (!flattenedProps.isEmpty())) {
			flattenedProps.put("ssl", Boolean.valueOf(this.isSSL));
		}
	}

	private boolean initSsl(boolean isHighSec, Map<String, Object> props) {
		boolean ssl;
		if (isHighSec) {
			ssl = true;
			props.put("ssl", Boolean.TRUE);
		} else {
			ssl = ((Boolean) getProperty("ssl", Boolean.valueOf(true))).booleanValue();
		}
		return ssl;
	}

	private void flatten(String prefix, Map<String, Object> source, Map<String, Object> dest) {
		for (Map.Entry<String, Object> e : source.entrySet()) {
			if ((e.getValue() instanceof Map)) {
				flatten(prefix + (String) e.getKey() + '.', (Map) e.getValue(), dest);
			} else {
				dest.put(prefix + (String) e.getKey(), e.getValue());
			}
		}
	}

	public <T> T getValue(String path) {
		return (T) getValue(path, null);
	}

	public <T> T getValue(String path, T defaultValue) {
		Object value = this.flattenedProperties.get(path);
		if (value == null) {
			return defaultValue;
		}
		if ((value instanceof ServerProp)) {
			value = ((ServerProp) value).getValue();
			return (T) castValue(path, value, defaultValue);
		}
		if (((value instanceof String)) && ((defaultValue instanceof Boolean))) {
			value = Boolean.valueOf((String) value);
			return (T) value;
		}
		if (((value instanceof String)) && ((defaultValue instanceof Integer))) {
			value = Integer.valueOf((String) value);
			return (T) value;
		}
		try {
			return (T) value;
		} catch (ClassCastException ccx) {
			// Agent.LOG.log(Level.FINE, "Using default value \"{0}\" for
			// \"{1}\"", new Object[] { defaultValue, path });
		}
		return defaultValue;
	}

	private String initLogLevel() {
		Object val = getProperty("log_level", "info");
		if ((val instanceof Boolean)) {
			return "off";
		}
		return ((String) getProperty("log_level", "info")).toLowerCase();
	}

	public boolean isApdexTSet() {
		return this.isApdexTSet;
	}

	public boolean isAgentEnabled() {
		return this.enabled;
	}

	public String getLicenseKey() {
		return this.licenseKey;
	}

	public String getHost() {
		return this.host;
	}

	public int getPort() {
		return this.port;
	}

	public String getProxyHost() {
		return this.proxyHost;
	}

	public Integer getProxyPort() {
		return this.proxyPort;
	}

	public String getProxyUser() {
		return this.proxyUser;
	}

	public String getProxyPassword() {
		return this.proxyPass;
	}

	public String getApiHost() {
		return (String) getProperty("api_host", "rpm.newrelic.com");
	}

	public int getApiPort() {
		return ((Integer) getProperty("api_port", Integer.valueOf(this.isSSL ? 443 : 80))).intValue();
	}

	public boolean isSSL() {
		return this.isSSL;
	}

	public String getApplicationName() {
		return this.appName;
	}

	public List<String> getApplicationNames() {
		return this.appNames;
	}

	public boolean isCpuSamplingEnabled() {
		return this.cpuSamplingEnabled;
	}

	public boolean isAutoAppNamingEnabled() {
		return this.autoAppNamingEnabled;
	}

	public boolean isAutoTransactionNamingEnabled() {
		return this.autoTransactionNamingEnabled;
	}

	public boolean isDebugEnabled() {
		return this.debug;
	}

	public boolean isSessionCountTrackingEnabled() {
		return this.sessionCountTrackingEnabled;
	}

	public String getLanguage() {
		return (String) getProperty("language", "java");
	}

	public boolean isSendDataOnExit() {
		return ((Boolean) getProperty("send_data_on_exit", Boolean.valueOf(false))).booleanValue();
	}

	public long getSendDataOnExitThresholdInMillis() {
		int valueInSecs = getIntProperty("send_data_on_exit_threshold", 60);
		return TimeUnit.MILLISECONDS.convert(valueInSecs, TimeUnit.SECONDS);
	}

	public boolean isAuditMode() {
		return this.auditMode;
	}

	public boolean isReportSqlParserErrors() {
		return this.reportSqlParserErrors;
	}

	public int getTransactionSizeLimit() {
		return this.transactionSizeLimit;
	}

	public boolean waitForRPMConnect() {
		return this.waitForRPMConnect;
	}

	public boolean isSyncStartup() {
		return ((Boolean) getProperty("sync_startup", Boolean.valueOf(false))).booleanValue();
	}

	public boolean isSendEnvironmentInfo() {
		return ((Boolean) getProperty("send_environment_info", Boolean.valueOf(true))).booleanValue();
	}

	public boolean isLoggingToStdOut() {
		String logFileName = getLogFileName();
		return "STDOUT".equalsIgnoreCase(logFileName);
	}

	public int getLogFileCount() {
		return getIntProperty("log_file_count", 1);
	}

	public String getLogFileName() {
		return (String) getProperty("log_file_name", "newrelic_agent.log");
	}

	public String getLogFilePath() {
		return (String) getProperty("log_file_path");
	}

	public String getLogLevel() {
		return this.logLevel;
	}

	public int getLogLimit() {
		return getIntProperty("log_limit_in_kbytes", 0);
	}

	public List<String> getIgnoreJars() {
		return this.ignoreJars;
	}

	public boolean isSendJvmProps() {
		return this.sendJvmProps;
	}

	public boolean isUsePrivateSSL() {
		return this.usePrivateSSL;
	}

	public boolean isLogDaily() {
		return this.logDaily;
	}

	public boolean isXraySessionEnabled() {
		return this.xRaySessionsEnabled;
	}

	public boolean isTrimStats() {
		return this.trimStats;
	}

	public static AgentConfig createAgentConfig(Map<String, Object> settings) {
		if (settings == null) {
			settings = Collections.emptyMap();
		}
		return new AgentConfigImpl(settings);
	}

	public boolean isPlatformInformationEnabled() {
		return this.platformInformationEnabled;
	}

	public Set<String> getJDBCSupport() {
		return this.jdbcSupport;
	}

	public boolean isGenericJDBCSupportEnabled() {
		return this.genericJdbcSupportEnabled;
	}

	public int getMaxStackTraceLines() {
		return this.maxStackTraceLines;
	}

	public Config getInstrumentationConfig() {
		return this.instrumentationConfig;
	}

	public int getMaxUserParameters() {
		return 64;
	}

	public int getMaxUserParameterSize() {
		return 255;
	}

	public boolean isHighSecurity() {
		return this.highSecurity;
	}

	public boolean isSimpleCompression() {
		return this.simpleCompression;
	}

	public String getCompressedContentEncoding() {
		return this.compressedContentEncoding;
	}

	public boolean isPutForDataSend() {
		return this.putForDataSend;
	}

	public boolean getIbmWorkaroundEnabled() {
		return true;
	}

	@Override
	public long getApdexTInMillis() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getApdexTInMillis(String paramString) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isApdexTSet(String paramString) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStartupTimingEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

}
