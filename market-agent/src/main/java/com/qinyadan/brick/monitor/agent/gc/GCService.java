package com.qinyadan.brick.monitor.agent.gc;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

import com.qinyadan.brick.monitor.agent.GatherListener;
import com.qinyadan.brick.monitor.agent.service.AbstractService;
import com.qinyadan.brick.monitor.agent.service.ServiceFactory;
import com.qinyadan.brick.monitor.agent.stats.StatsEngine;

public class GCService extends AbstractService implements GatherListener {

	private final Map<String, GarbageCollector> garbageCollectors = new HashMap();

	public GCService() {
		super(GCService.class.getSimpleName());
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void before(String paramString, StatsEngine statsEngine) {
		for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
			GarbageCollector garbageCollector = (GarbageCollector) this.garbageCollectors.get(gcBean.getName());
			if (garbageCollector == null) {
				garbageCollector = new GarbageCollector(gcBean);
				this.garbageCollectors.put(gcBean.getName(), garbageCollector);
			} else {
				garbageCollector.recordGC(gcBean, statsEngine);
			}
		}
	}

	@Override
	public void after(String paramString) {

	}

	@Override
	protected void doStart() throws Exception {
		ServiceFactory.getHarvestService().addHarvestListener(this);
	}

	@Override
	protected void doStop() throws Exception {

	}

	private class GarbageCollector {

		private long collectionCount;
		private long collectionTime;

		public GarbageCollector(GarbageCollectorMXBean gcBean) {
			this.collectionCount = gcBean.getCollectionCount();
			this.collectionTime = gcBean.getCollectionTime();
		}

		private void recordGC(GarbageCollectorMXBean gcBean, StatsEngine statsEngine) {
			long lastCollectionCount = this.collectionCount;
			long lastCollectionTime = this.collectionTime;

			this.collectionCount = gcBean.getCollectionCount();
			this.collectionTime = gcBean.getCollectionTime();

			long numberOfCollections = this.collectionCount - lastCollectionCount;
			long time = this.collectionTime - lastCollectionTime;
			if (numberOfCollections > 0L) {
				String rootMetricName = "GC/" + gcBean.getName();
				System.out.println(rootMetricName);
				// ResponseTimeStats stats =
				// statsEngine.getResponseTimeStats(rootMetricName);
				//stats.recordResponseTime(time, TimeUnit.MILLISECONDS);
				//stats.setCallCount((int) numberOfCollections);
			}
		}
	}

}
