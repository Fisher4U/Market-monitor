package com.qinyadan.market.plugins.tomcat78;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.qinyadan.monitor.plugin.interceptor.EnhancedClassInstanceContext;
import com.qinyadan.monitor.plugin.interceptor.enhance.ConstructorInvokeContext;
import com.qinyadan.monitor.plugin.interceptor.enhance.InstanceMethodInvokeContext;
import com.qinyadan.monitor.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import com.qinyadan.monitor.plugin.interceptor.enhance.MethodInterceptResult;



public class TomcatPluginInterceptor implements InstanceMethodsAroundInterceptor {
	
    private String tracingName = DEFAULT_TRACE_NAME;
    private static final String DEFAULT_TRACE_NAME = "MONITOR-TRACING-NAME";

    @Override
    public void onConstruct(EnhancedClassInstanceContext context, ConstructorInvokeContext interceptorContext) {
        //DO Nothing
    }

    @Override
    public void beforeMethod(EnhancedClassInstanceContext context, InstanceMethodInvokeContext interceptorContext, MethodInterceptResult result) {
        Object[] args = interceptorContext.allArguments();
        HttpServletRequest requests = (HttpServletRequest) args[0];
        String tracingHeaderValue = requests.getHeader(tracingName);
        
    }




    @Override
    public Object afterMethod(EnhancedClassInstanceContext context, InstanceMethodInvokeContext interceptorContext, Object ret) {
        Object[] args = interceptorContext.allArguments();
        HttpServletResponse httpServletResponse = (HttpServletResponse) args[1];
        return ret;
    }

    @Override
    public void handleMethodException(Throwable t, EnhancedClassInstanceContext context, InstanceMethodInvokeContext interceptorContext, Object ret) {
        // DO Nothing
    }
}
