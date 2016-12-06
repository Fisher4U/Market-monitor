package com.qinyadan.monitor.extension.compiler;

import com.qinyadan.monitor.extension.SPI;

/**
 * Compiler. (SPI, Singleton, ThreadSafe)
 * 
 * @author william.liangf
 */
@SPI("javassist")
public interface Compiler {

	/**
	 * Compile java source code.
	 * 
	 * @param code Java source code
	 * @param classLoader TODO
	 * @return Compiled class
	 */
	Class<?> compile(String code, ClassLoader classLoader);

}
