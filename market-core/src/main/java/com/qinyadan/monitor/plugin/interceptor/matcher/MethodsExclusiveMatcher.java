package com.qinyadan.monitor.plugin.interceptor.matcher;

import static net.bytebuddy.matcher.ElementMatchers.not;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.qinyadan.monitor.plugin.interceptor.MethodMatcher;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

public class MethodsExclusiveMatcher extends ExclusiveObjectDefaultMethodsMatcher {

    private List<MethodMatcher> matchers = new ArrayList<MethodMatcher>();

    public MethodsExclusiveMatcher(String... methodNames) {
        super("exclude method name: " + methodNames.toString());
        for (String methodName : methodNames) {
            matchers.add(new SimpleMethodMatcher(methodName));
        }
    }

    public MethodsExclusiveMatcher(MethodMatcher... matchers) {
        super("exclude methods description :" + matchers.toString());
        this.matchers.addAll(Arrays.asList(matchers));
    }

    @Override
    public ElementMatcher.Junction<MethodDescription> match() {
        ElementMatcher.Junction<MethodDescription> result = null;

        for (MethodMatcher matcher : matchers) {
            if (result == null) {
                result = matcher.buildMatcher();
                continue;
            }

            result = result.or(matcher.buildMatcher());
        }

        return not(result);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("exclude following method(s): ");
        int idx = 1;
        for (MethodMatcher methodMatcher : matchers) {
        	stringBuilder.append(idx++ + "." + methodMatcher.toString() + ". ");
        }

        return stringBuilder.toString();
    }
}
