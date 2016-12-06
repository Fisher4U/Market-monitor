package com.qinyadan.monitor.plugin.interceptor.matcher;

import static net.bytebuddy.matcher.ElementMatchers.nameMatches;

import com.qinyadan.monitor.plugin.interceptor.MethodMatcher;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

public class MethodRegexMatcher extends MethodMatcher {

    public MethodRegexMatcher(String methodMatchDescribe) {
        super(methodMatchDescribe);
    }

    public MethodRegexMatcher(String methodMatchDescribe, int argNum) {
        super(methodMatchDescribe, argNum);
    }

    public MethodRegexMatcher(String methodMatchDescribe, Class<?>... argTypeArray) {
        super(methodMatchDescribe, argTypeArray);
    }

    public MethodRegexMatcher(Modifier modifier, String methodMatchDescribe) {
        super(modifier, methodMatchDescribe);
    }

    public MethodRegexMatcher(Modifier modifier, String methodMatchDescribe, int argNum) {
        super(modifier, methodMatchDescribe, argNum);
    }

    public MethodRegexMatcher(Modifier modifier, String methodMatchDescribe, Class<?>... argTypeArray) {
        super(modifier, methodMatchDescribe, argTypeArray);
    }


    @Override
    public ElementMatcher.Junction<MethodDescription> buildMatcher() {
        ElementMatcher.Junction<MethodDescription> matcher = nameMatches(getMethodMatchDescribe());
        return mergeArgumentsIfNecessary(matcher);
    }

}
