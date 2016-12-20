package com.qinyadan.monitor.agent;

import com.qinyadan.monitor.extension.Adaptive;
import com.qinyadan.monitor.extension.SPI;

@SPI
public interface Plugin {
	
	@Adaptive
	void collect();
}
