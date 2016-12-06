package com.qinyadan.monitor.network.util;

import java.util.Map;

import com.qinyadan.monitor.network.control.ControlMessageDecoder;
import com.qinyadan.monitor.network.control.ControlMessageEncoder;
import com.qinyadan.monitor.network.control.ProtocolException;

public final class ControlMessageEncodingUtils {

    private static final ControlMessageEncoder encoder = new ControlMessageEncoder();
    private static final ControlMessageDecoder decoder = new ControlMessageDecoder();

    private ControlMessageEncodingUtils() {
    }

    public static byte[] encode(Map<String, Object> value) throws ProtocolException {
        return encoder.encode(value);
    }

    public static Object decode(byte[] in) throws ProtocolException {
        return decoder.decode(in);
    }

}
