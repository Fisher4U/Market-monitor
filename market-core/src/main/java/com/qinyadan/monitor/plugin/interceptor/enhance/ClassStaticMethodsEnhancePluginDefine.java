package com.qinyadan.monitor.plugin.interceptor.enhance;

import com.qinyadan.monitor.plugin.interceptor.MethodMatcher;

/**
 * 仅增强拦截类级别静态方法
 * 
 */
public abstract class ClassStaticMethodsEnhancePluginDefine extends
		ClassEnhancePluginDefine {

	@Override
	protected MethodMatcher[] getInstanceMethodsMatchers() {
		return null;
	}

	@Override
	protected InstanceMethodsAroundInterceptor getInstanceMethodsInterceptor() {
		return null;
	}
}
