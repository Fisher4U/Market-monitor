package com.qinyadan.brick.monitor.agent.bootstrap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class BootstrapLoader {

	static void load(Instrumentation inst,String jarfile) {
		try {
			getMonitorClass(inst,jarfile);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void getMonitorClass(Instrumentation inst,String jarfile) throws IOException {
		
		@SuppressWarnings("resource")
		JarFile jarFileInAgent = new JarFile(jarfile);
		JarEntry jarEntry = jarFileInAgent.getJarEntry("com/qinyadan/brick/monitor/agent/Agent.class");
		
		byte[] bytes = read(jarFileInAgent.getInputStream(jarEntry),true);
		inst.addTransformer(new MonitorClassFileTransformer(bytes));
	}

	static final class MonitorClassFileTransformer implements ClassFileTransformer {

		private final byte[] bytes;

		MonitorClassFileTransformer(byte[] bytes) {
			this.bytes = bytes;
		}

		public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
				ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
			if (className == null) {
				return null;
			}
			if ("com/qinyadan/brick/monitor/agent/Agent".equals(className)) {
				return this.bytes;
			}
			return null;
		}
	}

	static byte[] read(InputStream input, boolean closeInputStream) throws IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		copy(input, outStream, input.available(), closeInputStream);
		return outStream.toByteArray();
	}

	static int copy(InputStream input, OutputStream output, int bufferSize, boolean closeStreams) throws IOException {
		try {
			byte[] buffer = new byte[bufferSize];
			int count = 0;
			int n = 0;
			while (-1 != (n = input.read(buffer))) {
				output.write(buffer, 0, n);
				count += n;
			}
			return count;
		} finally {
			if (closeStreams) {
				input.close();
				output.close();
			}
		}
	}
}
