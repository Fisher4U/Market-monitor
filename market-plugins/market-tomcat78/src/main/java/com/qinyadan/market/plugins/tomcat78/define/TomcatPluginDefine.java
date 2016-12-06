package com.qinyadan.market.plugins.tomcat78.define;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;

import com.qinyadan.market.plugins.tomcat78.TomcatPluginInterceptor;
import com.qinyadan.monitor.plugin.interceptor.MethodMatcher;
import com.qinyadan.monitor.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import com.qinyadan.monitor.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import com.qinyadan.monitor.plugin.interceptor.matcher.SimpleMethodMatcher;

public class TomcatPluginDefine extends ClassInstanceMethodsEnhancePluginDefine {
	
    @Override
    protected MethodMatcher[] getInstanceMethodsMatchers() {

        return new MethodMatcher[]{new SimpleMethodMatcher("invoke", Request.class, Response.class)};
    }

    @Override
    protected InstanceMethodsAroundInterceptor getInstanceMethodsInterceptor() {
        return new TomcatPluginInterceptor();
    }

    @Override
    protected String enhanceClassName() {
        return "org.apache.catalina.core.StandardEngineValve";
    }
}
