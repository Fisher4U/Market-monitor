package com.qinyadan.brick.monitor.agent.samplers;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

import com.qinyadan.brick.monitor.agent.utils.TimeConversion;

public class CPUHarvester extends AbstractCPUSampler {

	private final OperatingSystemMXBean osMBean;

	public CPUHarvester() {
		this.osMBean = ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean());
	}

	@Override
	protected double getProcessCpuTime() {
		 return TimeConversion.convertNanosToSeconds(osMBean.getProcessCpuTime());
	}

}
