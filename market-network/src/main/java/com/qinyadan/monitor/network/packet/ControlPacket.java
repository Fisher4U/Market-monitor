package com.qinyadan.monitor.network.packet;

public abstract class ControlPacket extends BasicPacket {

    private int requestId;

    public ControlPacket(byte[] payload) {
        super(payload);
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

}
