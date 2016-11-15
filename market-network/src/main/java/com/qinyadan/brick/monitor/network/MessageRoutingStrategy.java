package com.qinyadan.brick.monitor.network;

import com.qinyadan.brick.monitor.spi.message.ext.MessageTree;

public interface MessageRoutingStrategy {
    public int getIndex(MessageTree tree, int size);
}
