package com.qinyadan.monitor.plugin.interceptor.matcher;

import static net.bytebuddy.matcher.ElementMatchers.any;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

public class AnyMethodsMatcher extends ExclusiveObjectDefaultMethodsMatcher {

    public AnyMethodsMatcher() {
        super("any method");
    }

    @Override
    public ElementMatcher.Junction<MethodDescription> match() {
        return any();
    }

    @Override
    public String toString() {
        return getMethodMatchDescribe();
    }
}
