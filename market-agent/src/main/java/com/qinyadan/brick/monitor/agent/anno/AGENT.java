package com.qinyadan.brick.monitor.agent.anno;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({FIELD})
public @interface AGENT {
	
	boolean isSingle() default true;
	
	String[] value() default {};
	
	String name() default "agent";
}
