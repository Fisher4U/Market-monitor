package com.qinyadan.brick.monitor.agent;

import java.lang.instrument.Instrumentation;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import com.qinyadan.brick.monitor.agent.config.AgentConfig;
import com.qinyadan.brick.monitor.agent.config.ConfigService;
import com.qinyadan.brick.monitor.agent.service.AbstractService;
import com.qinyadan.brick.monitor.agent.service.ServiceFactory;
import com.qinyadan.brick.monitor.agent.service.ServiceManager;
import com.qinyadan.brick.monitor.agent.service.ServiceManagerImpl;

public class Agent extends AbstractService implements IAgent {

	//public static final Logger LOG = LoggerFactory.getLogger(Agent.class);

	private static final String VERSION = initVersion();
	private static long agentPremainTime;
	private volatile boolean enabled = true;
	private final Instrumentation instrumentation;
	private volatile InstrumentationProxy instrumentationProxy;

	private Agent(Instrumentation instrumentation) {
		super(IAgent.class.getSimpleName());
		this.instrumentation = instrumentation;
	}

	protected void doStart() {
		ConfigService configService = ServiceFactory.getConfigService();
		AgentConfig config = configService.getDefaultAgentConfig();

		logHostIp();
		System.out.println(MessageFormat.format("Monitor Agent v{0} is initializing...", new Object[] { getVersion() }));

		this.enabled = config.isAgentEnabled();
		if (!this.enabled) {
			System.out.println("Monitor agent is disabled.");
		}
		this.instrumentationProxy = InstrumentationProxy.getInstrumentationProxy(this.instrumentation);

		final long startTime = System.currentTimeMillis();
		Runnable runnable = new Runnable() {
			public void run() {
				Agent.this.jvmShutdown(startTime);
			}
		};
		Thread shutdownThread = new Thread(runnable, "Monitor JVM Shutdown");
		Runtime.getRuntime().addShutdownHook(shutdownThread);
	}

	private void logHostIp() {
		try {
			InetAddress address = InetAddress.getLocalHost();
			System.out.println("Agent Host: " + address.getHostName() + " IP: " + address.getHostAddress());
		} catch (UnknownHostException e) {
			System.out.println("Monitor could not identify host/ip.");
		}
	}

	protected void doStop() {
	}

	public void shutdownAsync() {
		Runnable runnable = new Runnable() {
			public void run() {
				Agent.this.shutdown();
			}
		};
		Thread shutdownThread = new Thread(runnable, "Monitor Shutdown");
		shutdownThread.start();
	}

	private void jvmShutdown(long startTime) {
		AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
		if ((config.isSendDataOnExit())
				&& (System.currentTimeMillis() - startTime >= config.getSendDataOnExitThresholdInMillis())) {
			ServiceFactory.getHarvestService().harvestNow();
		}
		System.out.println("JVM is shutting down");
		shutdown();
	}

	public synchronized void shutdown() {
		try {
			ServiceFactory.getServiceManager().stop();
			System.out.println("Monitor Agent has shutdown");
		} catch (Throwable t) {
			System.out.println("Error shutting down New Relic Agent"+new Object[0]);
		}
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public InstrumentationProxy getInstrumentation() {
		return this.instrumentationProxy;
	}

	public static String getVersion() {
		return VERSION;
	}

	private static String initVersion() {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle(Agent.class.getName());
			return bundle.getString("version");
		} catch (Throwable localThrowable) {
		}
		return "0.0";
	}

	private static volatile boolean canFastPath = true;

	public static boolean canFastPath() {
		return canFastPath;
	}

	public static void disableFastPath() {
		if (canFastPath) {
			canFastPath = false;
		}
	}

	public static void premain(String agentArgs, Instrumentation inst, long startTime) {
		if (ServiceFactory.getServiceManager() != null) {
			System.out.println(
					"Monitor Agent is already running! Check if more than one -javaagent switch is used on the command line.");
			return;
		}
		String enabled = System.getProperty("newrelic.config.agent_enabled");
		if ((enabled != null) && (!Boolean.parseBoolean(enabled.toString()))) {
			System.out.println("Monitor Agent is disabled by a system property.");
			return;
		}
		String jvmName = System.getProperty("java.vm.name");
		if (jvmName.contains("Oracle JRockit")) {
			String msg = MessageFormat.format("New Relic Agent {0} does not support the Oracle JRockit JVM (\"{1}\").",
					new Object[] { getVersion(), jvmName });
			System.out.println(msg);
		}
		try {
			try {
				IAgent agent = new Agent(inst);
				System.out.println(agent);
				ServiceManager serviceManager = new ServiceManagerImpl(agent);
				System.out.println(serviceManager);
				ServiceFactory.setServiceManager(serviceManager);
				/*if (!serviceManager.getConfigService().getDefaultAgentConfig().isAgentEnabled()) {
					System.out.println("agent_enabled is false in the config. Not starting New Relic Agent.");
					return;
				}*/
				serviceManager.start();
			} catch (Throwable t) {
				System.out.println(
						"Unable to start the Monitor Agent. Your application will continue to run but it will not be monitored."+
						t);
				t.printStackTrace();
				return;
			}

			System.out.println(MessageFormat.format("Monitor Agent v{0} has started", new Object[] { getVersion() }));

		} catch (Throwable t) {
			String msg = "Unable to start Monitor Agent. Please remove -javaagent from your startup arguments and contact New Relic support.";
			try {
				System.out.println(msg+new Object[0]);
			} catch (Throwable localThrowable1) {
			}
			System.err.println(msg);
			t.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		String javaVersion = System.getProperty("java.version");
		if (javaVersion.startsWith("1.5")) {
			String msg = MessageFormat.format(
					"Java version is: {0}.  This version of the New Relic Agent does not support Java 1.5.  Please use a 2.21.x New Relic agent or a later version of Java.",
					new Object[] { javaVersion });

			System.err.println("----------");
			System.err.println(msg);
			System.err.println("----------");
			return;
		}
		if (javaVersion.startsWith("9")) {
			boolean java9enabled = System.getProperty("newrelic.enable.java.9") != null;
			if (!java9enabled) {
				String msg = MessageFormat.format(
						"Java version is: {0}.  This version of the New Relic Agent does not support Java 9. Please use an earlier version of Java.",
						new Object[] { javaVersion });

				System.err.println("----------");
				System.err.println(msg);
				System.err.println("----------");
				return;
			}
		}
	}

	public static long getAgentPremainTimeInMillis() {
		return agentPremainTime;
	}

}
