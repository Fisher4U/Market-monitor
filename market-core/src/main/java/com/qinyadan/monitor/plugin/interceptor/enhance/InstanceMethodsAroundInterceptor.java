package com.qinyadan.monitor.plugin.interceptor.enhance;

import com.qinyadan.monitor.plugin.interceptor.EnhancedClassInstanceContext;

public interface InstanceMethodsAroundInterceptor {
	public void onConstruct(EnhancedClassInstanceContext context, ConstructorInvokeContext interceptorContext);
	
	public void beforeMethod(EnhancedClassInstanceContext context, InstanceMethodInvokeContext interceptorContext, MethodInterceptResult result);
	
	public Object afterMethod(EnhancedClassInstanceContext context, InstanceMethodInvokeContext interceptorContext, Object ret);
	
	public void handleMethodException(Throwable t, EnhancedClassInstanceContext context, InstanceMethodInvokeContext interceptorContext, Object ret);
}
