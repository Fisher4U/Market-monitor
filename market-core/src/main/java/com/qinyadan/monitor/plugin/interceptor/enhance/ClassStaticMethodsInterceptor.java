package com.qinyadan.monitor.plugin.interceptor.enhance;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import com.qinyadan.monitor.logger.LoggerFactory;
import com.qinyadan.monitor.logger.Logger;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

/**
 * 类静态方法拦截、控制器
 *
 * @author wusheng
 */
public class ClassStaticMethodsInterceptor {
    private static Logger logger = LoggerFactory.getLogger(ClassStaticMethodsInterceptor.class);

    private StaticMethodsAroundInterceptor interceptor;

    public ClassStaticMethodsInterceptor(StaticMethodsAroundInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @RuntimeType
    public Object intercept(@Origin Class<?> clazz, @AllArguments Object[] allArguments, @Origin Method method, @SuperCall Callable<?> zuper) throws Exception {
        MethodInvokeContext interceptorContext = new MethodInvokeContext(method.getName(), allArguments);
        MethodInterceptResult result = new MethodInterceptResult();
        try {
            interceptor.beforeMethod(interceptorContext, result);
        } catch (Throwable t) {
            logger.error("class[{}] before static method[{}] intercept failue:{}", new Object[] {clazz, method.getName(), t.getMessage()}, t);
        }
        if (!result.isContinue()) {
            return result._ret();
        }

        Object ret = null;
        try {
            ret = zuper.call();
        } catch (Throwable t) {
            try {
                interceptor.handleMethodException(t, interceptorContext, ret);
            } catch (Throwable t2) {
                logger.error("class[{}] handle static method[{}] exception failue:{}", new Object[] {clazz, method.getName(), t2.getMessage()}, t2);
            }
            throw t;
        } finally {
            try {
                ret = interceptor.afterMethod(interceptorContext, ret);
            } catch (Throwable t) {
                logger.error("class[{}] after static method[{}] intercept failue:{}", new Object[] {clazz, method.getName(), t.getMessage()}, t);
            }
        }
        return ret;
    }
}
