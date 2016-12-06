package com.qinyadan.market.container.jvm.support;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

import com.qinyadan.market.container.jvm.AbstractService;
import com.qinyadan.monitor.network.packet.Packet;
import com.qinyadan.monitor.network.packet.SendPacket;

public class GCCM1Service extends AbstractService {

	public final static String NAME = "cm1";

	private final Map<String, GarbageCollector> garbageCollectors = new HashMap<>();

	public GCCM1Service() throws IOException {
		super();
	}

	@Override
	protected Packet doCollect() {
		System.gc();
		for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
			GarbageCollector garbageCollector = this.garbageCollectors.get(gcBean.getName());

			if (garbageCollector == null) {
				garbageCollector = new GarbageCollector(gcBean);
				this.garbageCollectors.put(gcBean.getName(), garbageCollector);
			} else {
				Map c = garbageCollector.recordGC(gcBean);
				return new SendPacket(c.toString().getBytes());
			}
		}
		return null;
		
	}

	private class GarbageCollector {

		private long collectionCount;
		private long collectionTime;

		public GarbageCollector(GarbageCollectorMXBean gcBean) {
			this.collectionCount = gcBean.getCollectionCount();
			this.collectionTime = gcBean.getCollectionTime();
		}

		private Map recordGC(GarbageCollectorMXBean gcBean) {
			long lastCollectionCount = this.collectionCount;
			long lastCollectionTime = this.collectionTime;

			this.collectionCount = gcBean.getCollectionCount();
			this.collectionTime = gcBean.getCollectionTime();

			long numberOfCollections = this.collectionCount - lastCollectionCount;
			long time = this.collectionTime - lastCollectionTime;

			if (numberOfCollections > 0) {
				Map c = new HashMap<>();
				c.put("numberOfCollections", numberOfCollections);
				c.put("time", time);
				
				return c;
			}
			return null;
		}

		public long getCollectionCount() {
			return collectionCount;
		}

		public void setCollectionCount(long collectionCount) {
			this.collectionCount = collectionCount;
		}

		public long getCollectionTime() {
			return collectionTime;
		}

		public void setCollectionTime(long collectionTime) {
			this.collectionTime = collectionTime;
		}
		
	}
}
