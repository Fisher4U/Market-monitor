package com.qinyadan.brick.monitor.agent.bootstrap;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;

public class BootstrapAgent {

	private static final String MONITOR_JAR_FILE = "E:\\newrelic\\agent.jar";

	public static void main(String[] args) {
	}

	public static void premain(String agentArgs, Instrumentation inst) {
		String javaVersion = System.getProperty("java.version", "");
		String msg = MessageFormat.format(
				"Java version is: {0}.  This version of the Monitor Agent  support Java 8. Please use an earlier version of Java.",
				new Object[] { javaVersion });

		System.out.println(msg);

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
		System.out.println(classLoader);
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
