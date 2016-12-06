package com.qinyadan.monitor.network;

public interface FutureListener<T> {
	
	void onComplete(Future<T> future);
}
