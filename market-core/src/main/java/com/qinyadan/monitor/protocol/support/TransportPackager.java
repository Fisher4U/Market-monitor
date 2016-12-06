package com.qinyadan.monitor.protocol.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.qinyadan.monitor.protocol.Message;
import com.qinyadan.monitor.protocol.exception.ConvertFailedException;
import com.qinyadan.monitor.utils.IntegerAssist;

public class TransportPackager {

	public static byte[] pack(List<Message<?>> beSendingData) {
		// 对协议格式进行修改
		// | check sum(4 byte) | data
		byte[] dataText = packSerializableObjects(beSendingData);
		byte[] dataPackage = packCheckSum(dataText);
		return dataPackage;
	}

	public static List<Message> unpackSerializableObjects(byte[] dataPackage) {
		List<Message> serializeData = new ArrayList<Message>();
		int currentLength = 0;
		while (true) {
			// 读取长度
			int dataLength = IntegerAssist.bytesToInt(dataPackage, currentLength);
			// 反序列化
			byte[] data = new byte[dataLength];
			System.arraycopy(dataPackage, currentLength + 4, data, 0, dataLength);

			try {
				Message serializable = SerializedFactory.unSerialize(data);
				serializeData.add(serializable);
			} catch (ConvertFailedException e) {
				e.printStackTrace();
			}

			currentLength += 4 + dataLength;
			if (currentLength >= dataPackage.length) {
				break;
			}
		}

		return serializeData;
	}

	/**
	 * 生成校验和参数
	 *
	 * @param data
	 * @return
	 */
	private static byte[] generateChecksum(byte[] data, int offset) {
		int result = data[offset];
		for (int i = offset + 1; i < data.length; i++) {
			result ^= data[i];
		}

		return IntegerAssist.intToBytes(result);
	}

	private static byte[] packCheckSum(byte[] dataText) {
		byte[] dataPackage = new byte[dataText.length + 4];
		byte[] checkSum = generateChecksum(dataText, 0);
		System.arraycopy(checkSum, 0, dataPackage, 0, 4);
		System.arraycopy(dataText, 0, dataPackage, 4, dataText.length);
		return dataPackage;
	}

	private static byte[] packSerializableObjects(List<Message<?>> beSendingData) {
		byte[] dataText = null;
		int currentIndex = 0;
		for (Message sendingData : beSendingData) {
			byte[] dataElementText = packSerializableObject(sendingData);
			dataText = appendingDataBytes(dataText, currentIndex, dataElementText);
			currentIndex += dataElementText.length;
		}

		return dataText;
	}

	private static byte[] appendingDataBytes(byte[] dataText, int currentIndex, byte[] dataElementText) {
		if (dataText == null) {
			dataText = new byte[dataElementText.length];
		} else {
			dataText = Arrays.copyOf(dataText, dataText.length + dataElementText.length);
		}
		System.arraycopy(dataElementText, 0, dataText, currentIndex, dataElementText.length);
		return dataText;
	}

	public static byte[] packSerializableObject(Message serializable) {
		byte[] serializableBytes = serializable.convert2Bytes();
		byte[] dataText = Arrays.copyOf(serializableBytes, serializableBytes.length + 4);
		byte[] length = IntegerAssist.intToBytes(serializableBytes.length);
		System.arraycopy(length, 0, dataText, 0, 4);
		return dataText;
	}
}
