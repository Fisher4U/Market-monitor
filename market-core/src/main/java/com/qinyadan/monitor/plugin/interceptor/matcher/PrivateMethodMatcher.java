package com.qinyadan.monitor.plugin.interceptor.matcher;

import static net.bytebuddy.matcher.ElementMatchers.any;

import com.qinyadan.monitor.plugin.interceptor.MethodMatcher;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

public class PrivateMethodMatcher extends MethodMatcher {
    public PrivateMethodMatcher() {
        super("any private method");
    }

    @Override
    public ElementMatcher.Junction<MethodDescription> buildMatcher() {
        return any().and(ElementMatchers.<MethodDescription>isPrivate());
    }

    @Override
    public String toString() {
        return getMethodMatchDescribe();
    }
}
