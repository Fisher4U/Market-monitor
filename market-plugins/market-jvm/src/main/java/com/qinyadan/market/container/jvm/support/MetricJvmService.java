package com.qinyadan.market.container.jvm.support;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.qinyadan.market.container.jvm.AbstractService;
import com.qinyadan.monitor.network.packet.Packet;
import com.qinyadan.monitor.network.packet.SendPacket;

public class MetricJvmService extends AbstractService {

	public final static String NAME = "MeticJvm";

	static final MetricRegistry metrics = new MetricRegistry();

	public MetricJvmService() throws IOException {
		super();
		metrics.register("Heap", new MemoryUsageGaugeSet());
		metrics.register("GC", new GarbageCollectorMetricSet());
		metrics.register("Thread", new ThreadStatesGaugeSet());
		metrics.register("CPU", new CpuUsageGaugeSet());

		JmxReporter reporter = JmxReporter.forRegistry(metrics).build();
		reporter.start();
	}

	@Override
	protected Packet doCollect() {
		Set<String> keys = metrics.getMetrics().keySet();
		Map r = new HashMap<>();
		for (String key : keys) {
			final Gauge gauge = (Gauge) metrics.getMetrics().get(key);
			r.put(key, gauge.getValue());
		}
		SendPacket cp = new SendPacket(getSerializedBytes(r));
		return cp;

	}

	private static byte[] getSerializedBytes(Map list) {
		if (null == list || list.size() < 0)
			return null;
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bo);
			os.writeObject(list);
			return bo.toByteArray();
		} catch (IOException e) {
			return null;
		}
	}

}
