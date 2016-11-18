package com.qinyadan.brick.monitor.agent.utils;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.jar.JarFile;

public class InstrumentationWrapper implements Instrumentation {

	protected final Instrumentation delegate;

	public InstrumentationWrapper(Instrumentation delegate) {
		this.delegate = delegate;
	}

	@Override
	public void addTransformer(ClassFileTransformer transformer, boolean canRetransform) {
		this.delegate.addTransformer(transformer, canRetransform);
	}

	@Override
	public void addTransformer(ClassFileTransformer transformer) {

		this.delegate.addTransformer(transformer);

	}

	@Override
	public boolean removeTransformer(ClassFileTransformer transformer) {
		return this.delegate.removeTransformer(transformer);
	}

	@Override
	public boolean isRetransformClassesSupported() {
		return this.delegate.isRetransformClassesSupported();
	}

	@Override
	public void retransformClasses(Class<?>... classes) throws UnmodifiableClassException {
		StringBuilder sb = new StringBuilder("Classes about to be retransformed: ");
		for (Class<?> current : classes) {
			sb.append(current.getName()).append(" ");
		}
		this.delegate.retransformClasses(classes);

	}

	@Override
	public boolean isRedefineClassesSupported() {
		return this.delegate.isRedefineClassesSupported();
	}

	@Override
	public void redefineClasses(ClassDefinition... definitions)
			throws ClassNotFoundException, UnmodifiableClassException {
		StringBuilder sb = new StringBuilder("Classes about to be redefined: ");
		for (ClassDefinition current : definitions) {
			sb.append(current.getDefinitionClass().getName()).append(" ");
		}
		this.delegate.redefineClasses(definitions);

	}

	@Override
	public boolean isModifiableClass(Class<?> theClass) {
		return this.delegate.isModifiableClass(theClass);
	}

	@Override
	public Class[] getAllLoadedClasses() {
		return this.delegate.getAllLoadedClasses();
	}

	@Override
	public Class[] getInitiatedClasses(ClassLoader loader) {
		return this.delegate.getInitiatedClasses(loader);
	}

	@Override
	public long getObjectSize(Object objectToSize) {
		return this.delegate.getObjectSize(objectToSize);
	}

	@Override
	public void appendToBootstrapClassLoaderSearch(JarFile jarfile) {
		this.delegate.appendToBootstrapClassLoaderSearch(jarfile);
	}

	@Override
	public void appendToSystemClassLoaderSearch(JarFile jarfile) {
		this.delegate.appendToSystemClassLoaderSearch(jarfile);
	}

	@Override
	public boolean isNativeMethodPrefixSupported() {
		return this.delegate.isNativeMethodPrefixSupported();
	}

	@Override
	public void setNativeMethodPrefix(ClassFileTransformer transformer, String prefix) {
		this.delegate.setNativeMethodPrefix(transformer, prefix);
	}

}
