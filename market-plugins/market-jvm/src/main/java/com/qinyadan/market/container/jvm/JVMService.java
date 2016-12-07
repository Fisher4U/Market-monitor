package com.qinyadan.market.container.jvm;

import com.qinyadan.market.container.jvm.support.MetricJvmService;
import com.qinyadan.monitor.extension.Activate;
import com.qinyadan.monitor.extension.SPI;

@SPI(MetricJvmService.NAME)
public interface JVMService {
	
	@Activate
	void collect();
}
