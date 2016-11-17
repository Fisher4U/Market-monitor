package com.qinyadan.brick.monitor.agent.stats;

import java.io.Serializable;

public interface StatsBase extends Cloneable, Serializable {
	public boolean hasData();

	public void reset();

	public void merge(StatsBase paramStatsBase);

	public Object clone() throws CloneNotSupportedException;
}
