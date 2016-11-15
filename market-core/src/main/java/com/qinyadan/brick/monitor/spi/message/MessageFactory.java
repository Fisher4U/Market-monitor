package com.qinyadan.brick.monitor.spi.message;

import com.qinyadan.brick.monitor.spi.message.ext.ForkedTransaction;
import com.qinyadan.brick.monitor.spi.message.ext.TaggedTransaction;

public interface MessageFactory {
	/**
	 * Create a new message id.
	 * 
	 * @return new message id
	 */
	public String createMessageId();

	/**
	 * Check if the CAT client is enabled for current domain.
	 * 
	 * @return true if CAT client is enabled, false means CAT client is disabled.
	 */
	public boolean isEnabled();

	/**
	 * Log an error.
	 * 
	 * @param cause
	 *           root cause exception
	 */
	public void logError(Throwable cause);

	/**
	 * Log an error.
	 * 
	 * @param cause
	 *           root cause exception
	 */
	public void logError(String message, Throwable cause);

	/**
	 * Log an event in one shot with SUCCESS status.
	 * 
	 * @param type
	 *           event type
	 * @param name
	 *           event name
	 */
	public void logEvent(String type, String name);

	/**
	 * Log an trace in one shot with SUCCESS status.
	 * 
	 * @param type
	 *           trace type
	 * @param name
	 *           trace name
	 */
	public void logTrace(String type, String name);

	/**
	 * Log an event in one shot.
	 * 
	 * @param type
	 *           event type
	 * @param name
	 *           event name
	 * @param status
	 *           "0" means success, otherwise means error code
	 * @param nameValuePairs
	 *           name value pairs in the format of "a=1&b=2&..."
	 */
	public void logEvent(String type, String name, String status, String nameValuePairs);

	/**
	 * Log an trace in one shot.
	 * 
	 * @param type
	 *           trace type
	 * @param name
	 *           trace name
	 * @param status
	 *           "0" means success, otherwise means error code
	 * @param nameValuePairs
	 *           name value pairs in the format of "a=1&b=2&..."
	 */
	public void logTrace(String type, String name, String status, String nameValuePairs);

	/**
	 * Log a heartbeat in one shot.
	 * 
	 * @param type
	 *           heartbeat type
	 * @param name
	 *           heartbeat name
	 * @param status
	 *           "0" means success, otherwise means error code
	 * @param nameValuePairs
	 *           name value pairs in the format of "a=1&b=2&..."
	 */
	public void logHeartbeat(String type, String name, String status, String nameValuePairs);

	/**
	 * Log a metric in one shot.
	 * 
	 * @param name
	 *           metric name
	 * @param status
	 *           "0" means success, otherwise means error code
	 * @param nameValuePairs
	 *           name value pairs in the format of "a=1&b=2&..."
	 */
	public void logMetric(String name, String status, String nameValuePairs);

	/**
	 * Create a new event with given type and name.
	 * 
	 * @param type
	 *           event type
	 * @param name
	 *           event name
	 */
	public Event newEvent(String type, String name);

	/**
	 * Create a new trace with given type and name.
	 * 
	 * @param type
	 *           trace type
	 * @param name
	 *           trace name
	 */
	public Trace newTrace(String type, String name);

	/**
	 * Create a new heartbeat with given type and name.
	 * 
	 * @param type
	 *           heartbeat type
	 * @param name
	 *           heartbeat name
	 */
	public Heartbeat newHeartbeat(String type, String name);

	/**
	 * Create a new metric with given type and name.
	 * 
	 * @param type
	 *           metric type
	 * @param name
	 *           metric name
	 */
	public Metric newMetric(String type, String name);

	/**
	 * Create a new transaction with given type and name.
	 * 
	 * @param type
	 *           transaction type
	 * @param name
	 *           transaction name
	 */
	public Transaction newTransaction(String type, String name);

	/**
	 * Create a forked transaction for child thread.
	 * 
	 * @param type
	 *           transaction type
	 * @param name
	 *           transaction name
	 * @return forked transaction
	 */
	public ForkedTransaction newForkedTransaction(String type, String name);

	/**
	 * Create a tagged transaction for another process or thread.
	 * 
	 * @param type
	 *           transaction type
	 * @param name
	 *           transaction name
	 * @param tag
	 *           tag applied to the transaction
	 * @return tagged transaction
	 */
	public TaggedTransaction newTaggedTransaction(String type, String name, String tag);
	
}
