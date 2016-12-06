package com.qinyadan.monitor.plugin.interceptor.enhance;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import com.qinyadan.monitor.logger.LoggerFactory;
import com.qinyadan.monitor.logger.Logger;
import com.qinyadan.monitor.plugin.interceptor.EnhancedClassInstanceContext;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.FieldValue;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

/**
 * 类方法拦截、控制器
 *
 * @author wusheng
 */
public class ClassInstanceMethodsInterceptor {
    private static Logger logger = LoggerFactory
            .getLogger(ClassInstanceMethodsInterceptor.class);

    private InstanceMethodsAroundInterceptor interceptor;

    public ClassInstanceMethodsInterceptor(InstanceMethodsAroundInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @RuntimeType
    public Object intercept(
            @This Object obj,
            @AllArguments Object[] allArguments,
            @Origin Method method,
            @SuperCall Callable<?> zuper,
            @FieldValue(ClassEnhancePluginDefine.contextAttrName) EnhancedClassInstanceContext instanceContext)
            throws Exception {
        InstanceMethodInvokeContext interceptorContext = new InstanceMethodInvokeContext(obj,
                method.getName(), allArguments);
        MethodInterceptResult result = new MethodInterceptResult();
        try {
            interceptor.beforeMethod(instanceContext, interceptorContext, result);
        } catch (Throwable t) {
            logger.error("class[{}] before method[{}] intercept failue:{}",
                    new Object[]{obj.getClass(), method.getName(), t.getMessage()}, t);
        }
        if (!result.isContinue()) {
            return result._ret();
        }

        Object ret = null;
        try {
            ret = zuper.call();
        } catch (Throwable t) {
            try {
                interceptor.handleMethodException(t, instanceContext, interceptorContext, ret);
            } catch (Throwable t2) {
                logger.error("class[{}] handle method[{}] exception failue:{}",
                        new Object[]{obj.getClass(), method.getName(), t2.getMessage()}, t2);
            }
            throw t;
        } finally {
            try {
                ret = interceptor.afterMethod(instanceContext, interceptorContext, ret);
            } catch (Throwable t) {
                logger.error("class[{}] after method[{}] intercept failue:{}",
                        new Object[]{obj.getClass(), method.getName(), t.getMessage()}, t);
            }
        }
        return ret;
    }
}
