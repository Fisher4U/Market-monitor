package com.qinyadan.monitor.protocol;

import com.qinyadan.monitor.protocol.support.CallType;

public interface ServiceType {

	String getName();

	short getCode();

	String getDesc();

	boolean isInternalMethod();

	boolean isRpcClient();

	boolean isRecordStatistics();

	boolean isUnknown();

	// return true when the service type is USER or can not be identified
	boolean isUser();

	boolean isIncludeDestinationId();

	String getTypeName();

	CallType getCallType();
}
