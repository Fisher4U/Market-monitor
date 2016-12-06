package com.qinyadan.monitor.plugin.interceptor.enhance;

/**
 * 方法执行拦截上下文
 * 
 */
public class MethodInvokeContext {
	/**
	 * 方法名称
	 */
	private String methodName;
	/**
	 * 方法参数
	 */
	private Object[] allArguments;
	
	MethodInvokeContext(String methodName, Object[] allArguments) {
		this.methodName = methodName;
		this.allArguments = allArguments;
	}
	
	public Object[] allArguments(){
		return this.allArguments;
	}
	
	public String methodName(){
		return methodName;
	}
}
