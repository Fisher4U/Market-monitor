package com.qinyadan.monitor.protocol.support;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import com.qinyadan.monitor.protocol.Message;
import com.qinyadan.monitor.protocol.exception.ConvertFailedException;
import com.qinyadan.monitor.utils.IntegerAssist;

public class SerializedFactory {

	public static Map<Integer, Message> serializableMap = new HashMap<Integer, Message>();

	static {
		ServiceLoader<Message> loaders = ServiceLoader.load(Message.class);

		for (Message serializable : loaders) {
			serializableMap.put(serializable.getDataType(), serializable);
		}
	}

	public static Message unSerialize(byte[] bytes) throws ConvertFailedException {
		try {
			Message serializable = serializableMap.get(IntegerAssist.bytesToInt(bytes, 0));
			if (serializable != null) {
				Message object = (Message) serializable.convert2Object(bytes);
				return object;
			}
		} catch (Exception e) {
			throw new ConvertFailedException();
		}
		return null;
	}

	public static byte[] serialize(Message dataSerializable) {
		return dataSerializable.convert2Bytes();
	}

	public static boolean isCanSerialized(int dataType) {
		return serializableMap.get(dataType) != null ? true : false;
	}
}
