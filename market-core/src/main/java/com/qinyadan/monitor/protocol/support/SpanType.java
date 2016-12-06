package com.qinyadan.monitor.protocol.support;

import com.qinyadan.monitor.protocol.exception.SpanTypeCannotConvertException;

public enum SpanType {

    LOCAL(1),
    RPC_CLIENT(2),
    RPC_SERVER(4);

    private int value;

    SpanType(int value) {
        this.value = value;
    }

    public static SpanType convert(int spanTypeValue) {
        switch (spanTypeValue) {
            case 1:
                return LOCAL;
            case 2:
                return RPC_CLIENT;
            case 3:
                return RPC_SERVER;
            default:
                throw new SpanTypeCannotConvertException(spanTypeValue + "");
        }
    }


    public int getValue() {
        return value;
    }
}
