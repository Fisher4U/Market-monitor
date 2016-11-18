package com.qinyadan.brick.monitor.agent.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.TraceClassVisitor;

import com.google.common.collect.ImmutableSet;

public class Utils {
	
	private static final String PROXY_CLASS_NAME = "java/lang/reflect/Proxy";

	public static boolean isJdkProxy(byte[] classBytes) {
		ClassReader reader = new ClassReader(classBytes);
		return isJdkProxy(reader);
	}

	public static boolean isJdkProxy(ClassReader reader) {
		if ((reader != null) && (looksLikeAProxy(reader))) {
			ProxyClassVisitor cv = new ProxyClassVisitor();
			reader.accept(cv, 1);
			return cv.isProxy();
		}
		return false;
	}

	private static boolean looksLikeAProxy(ClassReader reader) {
		return ("java/lang/reflect/Proxy".equals(reader.getSuperName())) && (Modifier.isFinal(reader.getAccess()));
	}

	public static ClassReader readClass(Class<?> theClass) throws IOException, BenignClassReadException {
		if (theClass.isArray()) {
			throw new BenignClassReadException(theClass.getName() + " is an array");
		}
		if (Proxy.isProxyClass(theClass)) {
			throw new BenignClassReadException(theClass.getName() + " is a Proxy class");
		}
		if (isRMIStubOrProxy(theClass)) {
			throw new BenignClassReadException(theClass.getName() + " is an RMI Stub or Proxy class");
		}
		if (theClass.getName().startsWith("sun.reflect.")) {
			throw new BenignClassReadException(theClass.getName() + " is a reflection class");
		}
		if (isJAXBClass(theClass)) {
			throw new BenignClassReadException(theClass.getName() + " is a JAXB accessor class");
		}
		if ((theClass.getProtectionDomain().getCodeSource() != null)
				&& (theClass.getProtectionDomain().getCodeSource().getLocation() == null)) {
			throw new BenignClassReadException(theClass.getName() + " is a generated class");
		}
		URL resource = getClassResource(theClass.getClassLoader(), Type.getInternalName(theClass));
		return getClassReaderFromResource(theClass.getName(), resource);
	}

	private static final Set<String> JAXB_SUPERCLASSES = ImmutableSet.of(
			"com.sun.xml.internal.bind.v2.runtime.reflect.Accessor", "com.sun.xml.bind.v2.runtime.reflect.Accessor",
			"com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver");

	private static boolean isJAXBClass(Class<?> theClass) {
		if (theClass.getSuperclass() == null) {
			return false;
		}
		return JAXB_SUPERCLASSES.contains(theClass.getSuperclass().getName());
	}

	private static final Set<String> RMI_SUPERCLASSES = ImmutableSet
			.of("org.omg.stub.javax.management.remote.rmi._RMIConnection_Stub", "com.sun.jmx.remote.internal.ProxyRef");

	private static boolean isRMIStubOrProxy(Class<?> theClass) {
		if (theClass.getSuperclass() == null) {
			return false;
		}
		return RMI_SUPERCLASSES.contains(theClass.getSuperclass().getName());
	}

	public static ClassReader readClass(ClassLoader loader, String internalClassName) throws IOException {
		URL resource = getClassResource(loader, internalClassName);
		return getClassReaderFromResource(internalClassName, resource);
	}

	public static ClassReader getClassReaderFromResource(String internalClassName, URL resource) throws IOException {
		if (resource != null) {
			InputStream stream = resource.openStream();
			try {
				return new ClassReader(stream);
			} finally {
				stream.close();
			}
		}
		throw new MissingResourceException("Unable to get the resource stream for class " + internalClassName);
	}

	public static String getClassResourceName(String internalName) {
		return internalName + ".class";
	}

	public static String getClassResourceName(Class<?> clazz) {
		return getClassResourceName(Type.getInternalName(clazz));
	}

	public static URL getClassResource(ClassLoader loader, Type type) {
		return getClassResource(loader, type.getInternalName());
	}

	public static URL getClassResource(ClassLoader loader, String internalClassName) {
		URL url = loader.getResource(getClassResourceName(internalClassName));
		if (url == null) {
			url = BootstrapLoader.get().findResource(internalClassName);
		}
		return url;
	}

	public static void print(byte[] bytes) {
		print(bytes, new PrintWriter(System.out, true));
	}


	public static void print(byte[] bytes, PrintWriter pw) {
		ClassReader cr = new ClassReader(bytes);
		TraceClassVisitor mv = new TraceClassVisitor(pw);
		cr.accept(mv, 8);
		pw.flush();
	}

}
