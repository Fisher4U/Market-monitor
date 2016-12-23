package com.qinyadan.monitor.agent.jmx.vo;

import java.util.List;

public class JMXMetricsValueInfo {

	/**
	 * 此jmx 连接的对象信息
	 */
	private List<JMXObjectNameInfo> jmxObjectNameInfoList;

	/**
	 * jmx 连接信息
	 */
	private JMXConnectionInfo jmxConnectionInfo;

	@Override
	public String toString() {
		return "JMXMetricsValueInfo{" + "jmxObjectNameInfoList=" + jmxObjectNameInfoList + ", jmxConnectionInfo="
				+ jmxConnectionInfo + '}';
	}

	public JMXConnectionInfo getJmxConnectionInfo() {
		return jmxConnectionInfo;
	}

	public void setJmxConnectionInfo(JMXConnectionInfo jmxConnectionInfo) {
		this.jmxConnectionInfo = jmxConnectionInfo;
	}

	public List<JMXObjectNameInfo> getJmxObjectNameInfoList() {
		return jmxObjectNameInfoList;
	}

	public void setJmxObjectNameInfoList(List<JMXObjectNameInfo> jmxObjectNameInfoList) {
		this.jmxObjectNameInfoList = jmxObjectNameInfoList;
	}
}
