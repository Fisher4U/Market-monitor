package com.qinyadan.brick.monitor.agent.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

public abstract class BootstrapLoader implements ClassFinder {
	public static final ClassLoader PLACEHOLER = new ClassLoader(null) {
		public String toString() {
			return "BoostrapPlaceholder";
		}
	};
	private static final BootstrapLoader loader = create();

	public static BootstrapLoader get() {
		return loader;
	}

	private static BootstrapLoader create() {
		try {
			return new BootstrapLoaderImpl();
		} catch (Exception e) {
			try {
				return new IBMBootstrapLoader();
			} catch (Exception localException1) {
			}
		}
		return new BootstrapLoader() {
			public URL findResource(String name) {
				return null;
			}

			public boolean isBootstrapClass(String internalName) {
				return internalName.startsWith("java/");
			}
		};
	}

	public boolean isBootstrapClass(String internalOrClassName) {
		URL bootstrapResource = findResource(this.getClassResourceName(internalOrClassName));
		return bootstrapResource != null;
	}

	public abstract URL findResource(String paramString);

	private static class BootstrapLoaderImpl extends BootstrapLoader {
		private final Method getBootstrapResourceMethod;

		private BootstrapLoaderImpl() throws NoSuchMethodException, SecurityException, IllegalAccessException,
				IllegalArgumentException, InvocationTargetException {
			this.getBootstrapResourceMethod = ClassLoader.class.getDeclaredMethod("getBootstrapResource",
					new Class[] { String.class });
			this.getBootstrapResourceMethod.setAccessible(true);
			this.getBootstrapResourceMethod.invoke(null, new Object[] { "dummy" });
		}

		public URL findResource(String internalOrClassName) {
			try {
				return (URL) this.getBootstrapResourceMethod.invoke(null,
						new Object[] { this.getClassResourceName(internalOrClassName) });
			} catch (Exception e) {
			}
			return null;
		}
	}

	private static class IBMBootstrapLoader extends BootstrapLoader {
		private static final Set<String> BOOTSTRAP_CLASSLOADER_FIELDS = ImmutableSet.of("bootstrapClassLoader",
				"systemClassLoader");
		private final ClassLoader bootstrapLoader;

		public IBMBootstrapLoader() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
			Field field = getBootstrapField();
			field.setAccessible(true);
			ClassLoader cl = (ClassLoader) field.get(null);
			this.bootstrapLoader = cl;
		}

		private Field getBootstrapField() throws NoSuchFieldException {
			for (String fieldName : BOOTSTRAP_CLASSLOADER_FIELDS) {
				try {
					return ClassLoader.class.getDeclaredField(fieldName);
				} catch (NoSuchFieldException localNoSuchFieldException) {
				} catch (SecurityException localSecurityException) {
				}
			}
			throw new NoSuchFieldException(MessageFormat.format("No bootstrap fields found: {0}",
					new Object[] { BOOTSTRAP_CLASSLOADER_FIELDS }));
		}

		public URL findResource(String name) {
			return this.bootstrapLoader.getResource(this.getClassResourceName(name));
		}
	}

	public String getClassResourceName(String binaryName) {
		if (binaryName.endsWith(".class")) {
			return binaryName;
		}
		return binaryName.replace('.', '/') + ".class";
	}
}
