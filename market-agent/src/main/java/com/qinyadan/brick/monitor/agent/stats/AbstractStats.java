package com.qinyadan.brick.monitor.agent.stats;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson.JSONArray;

public abstract class AbstractStats implements CountStats {

	private static final List<Number> ZERO_ARRAY_LIST;
	protected int count;

	static {
		Number zero = Integer.valueOf(0);
		ZERO_ARRAY_LIST = Arrays.asList(new Number[] { zero, zero, zero, zero, zero, zero });
	}
	public static final StatsBase EMPTY_STATS = new StatsBase() {
		public boolean hasData() {
			return true;
		}

		public void merge(StatsBase stats) {
		}

		public void reset() {
		}

		public Object clone() throws CloneNotSupportedException {
			return super.clone();
		}

		public void writeJSONString() throws IOException {
			JSONArray.toJSONString(AbstractStats.ZERO_ARRAY_LIST);
		}
	};

	public AbstractStats(int count) {
		this.count = count;
	}

	public void incrementCallCount(int value) {
		this.count += value;
	}

	public void incrementCallCount() {
		this.count += 1;
	}

	public int getCallCount() {
		return this.count;
	}

	public void setCallCount(int count) {
		this.count = count;
	}

	public final void writeJSONString(Writer writer) throws IOException, InvalidStatsException {
		List<Number> list;
		if (this.count < 0) {
			list = ZERO_ARRAY_LIST;
		} else {
			list = Arrays.asList(new Number[] { Integer.valueOf(this.count), Float.valueOf(getTotal()),
					Float.valueOf(getTotalExclusiveTime()), Float.valueOf(getMinCallTime()),
					Float.valueOf(getMaxCallTime()), Double.valueOf(getSumOfSquares()) });
		}
		JSONArray.toJSONString(list);
	}

	public AbstractStats() {
	}

	public abstract Object clone() throws CloneNotSupportedException;
}
