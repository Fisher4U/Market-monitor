package com.qinyadan.market.container.jvm.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;

/**
 * 采集cpu某一个时刻使用情况的度量
 * 
 * @author xh-liuzhimin
 *
 */
public class CpuUsageGaugeSet implements MetricSet{

	@Override
	public Map<String, Metric> getMetrics() {
		
		final Map<String, Metric> gauges = new HashMap<String, Metric>();
		
		gauges.put("cpu.usage", new Gauge<Long>() {
            @Override
            public Long getValue() {
                return 20l;
            }
        });
		return  Collections.unmodifiableMap(gauges);
	}

}
