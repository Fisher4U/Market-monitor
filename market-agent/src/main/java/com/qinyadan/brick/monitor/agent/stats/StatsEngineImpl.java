package com.qinyadan.brick.monitor.agent.stats;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinyadan.brick.monitor.agent.MetricData;
import com.qinyadan.brick.monitor.agent.metric.MetricIdRegistry;
import com.qinyadan.brick.monitor.agent.metric.MetricName;

public class StatsEngineImpl implements StatsEngine {


	private static final float HASH_SET_LOAD_FACTOR = 0.75F;
	public static final int DEFAULT_CAPACITY = 140;
	public static final int DEFAULT_SCOPED_CAPACITY = 32;
	public static final int DOUBLE = 2;
	private final SimpleStatsEngine unscopedStats;
	private final Map<String, SimpleStatsEngine> scopedStats;

	public StatsEngineImpl() {
		this(DEFAULT_CAPACITY);
	}

	public StatsEngineImpl(int capacity) {
		this.unscopedStats = new SimpleStatsEngine(capacity);
		this.scopedStats = new HashMap<>(capacity);
	}

	@Override
	public Stats getStats(String name) {
		return getStats(MetricName.create(name));
	}

	@Override
	public Stats getStats(MetricName metricName) {
		if (metricName == null) {
			throw new RuntimeException("Cannot get a stat for a null metric");
		}
		return getStatsEngine(metricName).getStats(metricName.getName());
	}

	@Override
	public void recordEmptyStats(String name) {
		recordEmptyStats(MetricName.create(name));
	}

	@Override
	public void recordEmptyStats(MetricName metricName) {
		if (metricName == null) {
			throw new RuntimeException("Cannot create stats for a null metric");
		}
		getStatsEngine(metricName).recordEmptyStats(metricName.getName());

	}

	@Override
	public List<MetricName> getMetricNames() {
		List<MetricName> result = new ArrayList<>(getSize());
		for (String name : this.unscopedStats.getStatsMap().keySet()) {
			result.add(MetricName.create(name));
		}
		Iterator it;
		for (it = this.scopedStats.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			for (String name : ((SimpleStatsEngine) entry.getValue()).getStatsMap().keySet()) {
				result.add(MetricName.create(name, (String) entry.getKey()));
			}
		}
		Map.Entry<String, SimpleStatsEngine> entry;
		return result;
	}

	@Override
	public void clear() {
		this.unscopedStats.clear();
		this.scopedStats.clear();

	}

	@Override
	public void mergeStats(StatsEngine statsEngine) {
		if ((statsEngine instanceof StatsEngineImpl)) {
			mergeStats((StatsEngineImpl) statsEngine);
		}

	}

	@Override
	public int getSize() {
		int size = this.unscopedStats.getStatsMap().size();
		for (SimpleStatsEngine engine : this.scopedStats.values()) {
			size += engine.getStatsMap().size();
		}
		return size;
	}

	private SimpleStatsEngine getStatsEngine(MetricName metricName) {
		if (metricName.isScoped()) {
			SimpleStatsEngine statsEngine = (SimpleStatsEngine) this.scopedStats.get(metricName.getScope());
			if (statsEngine == null) {
				statsEngine = new SimpleStatsEngine(32);
				this.scopedStats.put(metricName.getScope(), statsEngine);
			}
			return statsEngine;
		}
		return this.unscopedStats;
	}

	public ResponseTimeStats getResponseTimeStats(String name) {
		return getResponseTimeStats(MetricName.create(name));
	}

	public ResponseTimeStats getResponseTimeStats(MetricName metricName) {
		if (metricName == null) {
			throw new RuntimeException("Cannot get a stat for a null metric");
		}
		return getStatsEngine(metricName).getResponseTimeStats(metricName.getName());
	}

	public ApdexStats getApdexStats(MetricName metricName) {
		if (metricName == null) {
			throw new RuntimeException("Cannot get a stat for a null metric");
		}
		return getStatsEngine(metricName).getApdexStats(metricName.getName());
	}

	private void mergeStats(StatsEngineImpl other) {
		this.unscopedStats.mergeStats(other.unscopedStats);
		for (Map.Entry<String, SimpleStatsEngine> entry : other.scopedStats.entrySet()) {
			SimpleStatsEngine scopedStatsEngine = (SimpleStatsEngine) this.scopedStats.get(entry.getKey());
			if (scopedStatsEngine == null) {
				scopedStatsEngine = new SimpleStatsEngine(((SimpleStatsEngine) entry.getValue()).getSize());
				this.scopedStats.put(entry.getKey(), scopedStatsEngine);
			}
			scopedStatsEngine.mergeStats((SimpleStatsEngine) entry.getValue());
		}
	}

	public void mergeStatsResolvingScope(TransactionStats txStats, String resolvedScope) {
		this.unscopedStats.mergeStats(txStats.getUnscopedStats());
		if (resolvedScope == null) {
			return;
		}
		SimpleStatsEngine scopedStatsEngine = (SimpleStatsEngine) this.scopedStats.get(resolvedScope);
		if (scopedStatsEngine == null) {
			scopedStatsEngine = new SimpleStatsEngine(txStats.getScopedStats().getSize());
			this.scopedStats.put(resolvedScope, scopedStatsEngine);
		}
		scopedStatsEngine.mergeStats(txStats.getScopedStats());
	}

	public List<MetricData> getMetricData(Normalizer metricNormalizer, MetricIdRegistry metricIdRegistry) {
		List<MetricData> result = new ArrayList<>(
				this.unscopedStats.getStatsMap().size() + this.scopedStats.size() * 32 * 2);
		for (Map.Entry<String, SimpleStatsEngine> entry : this.scopedStats.entrySet()) {
			result.addAll(((SimpleStatsEngine) entry.getValue()).getMetricData(metricNormalizer, metricIdRegistry,
					(String) entry.getKey()));
		}
		result.addAll(createUnscopedCopies(metricNormalizer, metricIdRegistry, result));

		result.addAll(this.unscopedStats.getMetricData(metricNormalizer, metricIdRegistry, ""));

		return aggregate(metricIdRegistry, result);
	}

	public static List<MetricData> createUnscopedCopies(Normalizer metricNormalizer, MetricIdRegistry metricIdRegistry,
			List<MetricData> scopedMetrics) {
		int size = (int) (scopedMetrics.size() / 0.75D) + 2;
		Map<String, MetricData> allUnscopedMetrics = new HashMap<>(size);
		List<MetricData> results = new ArrayList<>(scopedMetrics.size());
		for (MetricData scoped : scopedMetrics) {
			String theMetricName = scoped.getMetricName().getName();
			MetricData unscopedMetric = getUnscopedCloneOfData(metricNormalizer, metricIdRegistry, theMetricName,
					scoped.getStats());
			if (unscopedMetric != null) {
				MetricData mapUnscoped = (MetricData) allUnscopedMetrics.get(theMetricName);
				if (mapUnscoped == null) {
					allUnscopedMetrics.put(theMetricName, unscopedMetric);
					results.add(unscopedMetric);
				} else {
					mapUnscoped.getStats().merge(unscopedMetric.getStats());
				}
			}
		}
		return results;
	}

	private static MetricData getUnscopedCloneOfData(Normalizer metricNormalizer, MetricIdRegistry metricIdRegistry,
			String metricName, StatsBase stats) {
		if (stats != null) {
			MetricName metricNameUnscoped = MetricName.create(metricName);
			try {
				return SimpleStatsEngine.createMetricData(metricNameUnscoped, (StatsBase) stats.clone(),
						metricNormalizer, metricIdRegistry);
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}
		return null;
	}

	static List<MetricData> aggregate(MetricIdRegistry metricIdRegistry, List<MetricData> result) {
		if (metricIdRegistry.getSize() == 0) {
			return result;
		}
		int hashMapSize = (int) (result.size() / HASH_SET_LOAD_FACTOR) + 1;
		HashMap<Object, MetricData> data = new HashMap<>(hashMapSize);
		for (MetricData md : result) {
			MetricData existing = (MetricData) data.get(md.getKey());
			if (existing == null) {
				data.put(md.getKey(), md);
			} else {
				existing.getStats().merge(md.getStats());
			}
		}
		if (data.size() == result.size()) {
			return result;
		}
		return new ArrayList<>(data.values());
	}

}
