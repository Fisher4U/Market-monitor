package com.qinyadan.brick.monitor.agent.utils;

import java.lang.reflect.Method;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class ProxyClassVisitor implements ClassVisitor {

	protected final int api;

	protected ClassVisitor cv;

	private static final String PROXY_METHOD_DESC = Type.getDescriptor(Method.class);

	private boolean hasProxyMethod = false;

	public ProxyClassVisitor() {
		this(327680);
	}

	public ProxyClassVisitor(int api) {
		this(api, null);
	}

	public boolean isProxy() {
		return this.hasProxyMethod;
	}

	public FieldVisitor visitField0(int access, String name, String desc, String signature, Object value) {
		if ((!this.hasProxyMethod) && (desc.equals(PROXY_METHOD_DESC))) {
			this.hasProxyMethod = true;
		}
		return this.visitField(access, name, desc, signature, value);
	}

	public ProxyClassVisitor(int api, ClassVisitor cv) {
		if ((api != 262144) && (api != 327680)) {
			throw new IllegalArgumentException();
		}
		this.api = api;
		this.cv = cv;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		if (this.cv != null) {
			this.cv.visit(version, access, name, signature, superName, interfaces);
		}

	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		if (this.cv != null) {
			return this.cv.visitAnnotation(desc, visible);
		}
		return null;
	}

	@Override
	public void visitAttribute(Attribute attr) {
		if (this.cv != null) {
			this.cv.visitAttribute(attr);
		}
	}

	@Override
	public void visitEnd() {
		if (this.cv != null) {
			this.cv.visitEnd();
		}

	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		if (this.cv != null) {
			return this.cv.visitField(access, name, desc, signature, value);
		}
		return null;
	}

	@Override
	public void visitInnerClass(String name, String outerName, String innerName, int access) {
		if (this.cv != null) {
			this.cv.visitInnerClass(name, outerName, innerName, access);
		}

	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if (this.cv != null) {
			return this.cv.visitMethod(access, name, desc, signature, exceptions);
		}
		return null;
	}

	@Override
	public void visitOuterClass(String owner, String name, String desc) {
		if (this.cv != null) {
			this.cv.visitOuterClass(owner, name, desc);
		}

	}

	@Override
	public void visitSource(String source, String debug) {
		if (this.cv != null) {
			this.cv.visitSource(source, debug);
		}

	}

}
