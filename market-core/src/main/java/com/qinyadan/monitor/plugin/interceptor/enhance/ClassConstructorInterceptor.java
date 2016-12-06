package com.qinyadan.monitor.plugin.interceptor.enhance;

import com.qinyadan.monitor.logger.LoggerFactory;
import com.qinyadan.monitor.logger.Logger;
import com.qinyadan.monitor.plugin.interceptor.EnhancedClassInstanceContext;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.FieldProxy;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

public class ClassConstructorInterceptor {
	private static Logger logger = LoggerFactory.getLogger(ClassConstructorInterceptor.class);

	private InstanceMethodsAroundInterceptor interceptor;

	public ClassConstructorInterceptor(InstanceMethodsAroundInterceptor interceptor) {
		this.interceptor = interceptor;
	}

	@RuntimeType
	public void intercept(@This Object obj, @FieldProxy(ClassEnhancePluginDefine.contextAttrName) FieldSetter accessor,
			@AllArguments Object[] allArguments) {
		try {
			EnhancedClassInstanceContext context = new EnhancedClassInstanceContext();
			accessor.setValue(context);
			ConstructorInvokeContext interceptorContext = new ConstructorInvokeContext(obj, allArguments);
			interceptor.onConstruct(context, interceptorContext);
		} catch (Throwable t) {
			logger.error("ClassConstructorInterceptor failue.", t);
		}

	}
}
