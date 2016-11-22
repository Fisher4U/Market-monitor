package com.qinyadan.brick.monitor.agent.bootstrap;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class BootstrapAgent {

	private static final String MONITOR_JAR_FILE = "E:\\newrelic\\agent-jar-with-dependencies.jar";

	public static void main(String[] args) {
	}

	public static void premain(String agentArgs, Instrumentation inst) {
		String javaVersion = System.getProperty("java.version", "");
		System.out.println("java version ==" + javaVersion);
		try {
			startAgent(agentArgs, inst);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void startAgent(String agentArgs, Instrumentation inst) throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		long startTime = System.currentTimeMillis();
		BootstrapLoader.load(inst, MONITOR_JAR_FILE);

		URL[] codeSource = { BootstrapAgent.class.getProtectionDomain().getCodeSource().getLocation() };

		ClassLoader classLoader = new JVMAgentClassLoader(codeSource, null);
		Class<?> agentClass = classLoader.loadClass("com.qinyadan.brick.monitor.agent.Agent");
		Method premain = agentClass.getDeclaredMethod("premain",
				new Class[] { String.class, Instrumentation.class, Long.TYPE });
		premain.invoke(null, new Object[] { agentArgs, inst, Long.valueOf(startTime) });
	}

	private static class JVMAgentClassLoader extends URLClassLoader {
		static {
			try {
				Method method = ClassLoader.class.getDeclaredMethod("registerAsParallelCapable", new Class[0]);
				method.setAccessible(true);

				method.invoke(null, new Object[0]);
			} catch (Throwable localThrowable) {
			}
		}

		public JVMAgentClassLoader(URL[] urls, ClassLoader parent) {
			super(urls, parent);
		}
	}
}
