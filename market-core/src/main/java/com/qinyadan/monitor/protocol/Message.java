package com.qinyadan.monitor.protocol;

import java.io.Serializable;

import com.qinyadan.monitor.protocol.exception.ConvertFailedException;

public interface Message<T> extends Serializable, Cloneable {

	public byte[] convert2Bytes();

	public int getDataType();

	T convert2Object(byte[] data) throws ConvertFailedException;
}
