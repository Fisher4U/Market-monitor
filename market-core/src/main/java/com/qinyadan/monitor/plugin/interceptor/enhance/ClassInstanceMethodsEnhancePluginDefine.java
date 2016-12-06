package com.qinyadan.monitor.plugin.interceptor.enhance;

import com.qinyadan.monitor.plugin.interceptor.MethodMatcher;

/**
 * 仅增强拦截实例方法
 * 
 *
 */
public abstract class ClassInstanceMethodsEnhancePluginDefine extends ClassEnhancePluginDefine {

	@Override
	protected MethodMatcher[] getStaticMethodsMatchers() {
		return null;
	}

	@Override
	protected StaticMethodsAroundInterceptor getStaticMethodsInterceptor() {
		return null;
	}

}
