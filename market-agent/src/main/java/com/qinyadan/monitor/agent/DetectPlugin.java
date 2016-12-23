package com.qinyadan.monitor.agent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.qinyadan.monitor.agent.protocol.detect.DetectResult;

/**
 * 探测监控插件
 */
public interface DetectPlugin extends Plugin {

	/**
	 * 监控的具体服务的agentSignName tag值
	 * 
	 * @param address
	 *            被监控的探测地址
	 * @return 根据地址提炼的标识,如域名等
	 */
	String agentSignName(String address);

	/**
	 * 一次地址的探测结果
	 * 
	 * @param address
	 *            被探测的地址,地址来源于方法
	 *            {@link com.yiji.falcon.agent.plugins.DetectPlugin#detectAddressCollection()}
	 * @return 返回被探测的地址的探测结果,将用于上报监控状态
	 */
	DetectResult detectResult(String address);

	/**
	 * 被探测的地址集合
	 * 
	 * @return 只要该集合不为空,就会触发监控 pluginActivateType属性将不起作用
	 */
	Collection<String> detectAddressCollection();

	/**
	 * 转换配置地址为地址集合工具方法
	 * 
	 * @param address
	 *            配置的地址
	 * @param split
	 *            分隔的字符串 null代表不分隔
	 * @return
	 */
	default Collection<String> helpTransformAddressCollection(String address, String split) {
		Set<String> addresses = new HashSet<>();
		if (!StringUtils.isEmpty(address)) {
			if (split != null) {
				Collections.addAll(addresses, address.split(split));
			} else {
				if (!StringUtils.isEmpty(address)) {
					addresses.add(address);
				}
			}
		}
		return addresses;
	}

	/**
	 * 自动探测地址的实现 若配置文件已配置地址,将不会调用此方法
	 * 若配置文件未配置探测地址的情况下,将会调用此方法,若该方法返回非null且有元素的集合,则启动运行插件,使用该方法返回的探测地址进行监控
	 * 
	 * @return
	 */
	default Collection<String> autoDetectAddress() {
		return null;
	}
}
