package com.qinyadan.monitor.plugin.interceptor.enhance;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

public class MethodInputAndOutParameterInterceptor {

	@RuntimeType
	public Object interceptor(@AllArguments Object[] allArgument, @Origin Method method, @Origin Class<?> clazz,
			@SuperCall Callable<?> zuper) throws Exception {

		Object ret = null;
		try {
			ret = zuper.call();
		} catch (Throwable e) {

			throw e;
		} finally {

		}

		return ret;
	}
}
