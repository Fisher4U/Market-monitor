package com.qinyadan.market.container.jvm;

import com.qinyadan.market.container.jvm.support.GCCM1Service;
import com.qinyadan.monitor.extension.Activate;
import com.qinyadan.monitor.extension.SPI;

@SPI(GCCM1Service.NAME)
public interface GCService {
	
	@Activate
	void collect();
}
