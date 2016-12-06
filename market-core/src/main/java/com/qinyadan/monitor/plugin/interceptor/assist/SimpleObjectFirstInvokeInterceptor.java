package com.qinyadan.monitor.plugin.interceptor.assist;

import java.util.concurrent.atomic.AtomicInteger;

import com.qinyadan.monitor.plugin.interceptor.EnhancedClassInstanceContext;
import com.qinyadan.monitor.plugin.interceptor.InterceptorException;
import com.qinyadan.monitor.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;


/**
 * 用于首次拦截方法调用，避免方法内部的方法调用被多次拦截。
 * 
 *
 */
public abstract class SimpleObjectFirstInvokeInterceptor implements InstanceMethodsAroundInterceptor {
	protected String invokeCounterKey = "__$invokeCounterKey";

	protected Object invokeCounterInstLock = new Object();

	public boolean isFirstBeforeMethod(EnhancedClassInstanceContext context) {
		if (!context.isContain(invokeCounterKey)) {
			synchronized (invokeCounterInstLock) {
				if (!context.isContain(invokeCounterKey)) {
					context.set(invokeCounterKey, new AtomicInteger(0));
				}
			}
		}
		AtomicInteger counter = context.get(invokeCounterKey,
				AtomicInteger.class);
		return counter.incrementAndGet() == 1;
	}

	public boolean isLastAfterMethod(EnhancedClassInstanceContext context) {
		if (!context.isContain(invokeCounterKey)) {
			throw new InterceptorException(
					"key=invokeCounterKey not found is context. unexpected failue.");
		}
		AtomicInteger counter = context.get(invokeCounterKey,
				AtomicInteger.class);
		return counter.decrementAndGet() == 0;
	}
}
