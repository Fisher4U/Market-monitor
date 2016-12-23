package com.qinyadan.monitor.agent.protocol;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportMetrics {

	private static final Logger log = LoggerFactory.getLogger(ReportMetrics.class);

	/**
	 * 推送数据到falcon
	 * 
	 * @param reportObjectList
	 */
	public static void push(Collection<ReportObject> reportObjectList) {
		if (reportObjectList != null) {

		} else {
			log.info("push对象为null");
		}
	}

	/**
	 * 推送数据到falcon
	 * 
	 * @param reportObject
	 */
	public static void push(ReportObject reportObject) {
		if (!isValidTag(reportObject)) {
			log.error("报告对象的tag为空,此metrics将不允上报:{}", reportObject.toString());
			return;
		}

		try {
		} catch (Exception e) {
			log.error("metrics push异常,检查Falcon组件是否运行正常", e);
			return;
		}
	}

	/**
	 * 判断tag是否有效
	 * 
	 * @param reportObject
	 * @return
	 */
	private static boolean isValidTag(ReportObject reportObject) {
		return reportObject != null && !StringUtils.isEmpty(reportObject.getTags());
	}

}
