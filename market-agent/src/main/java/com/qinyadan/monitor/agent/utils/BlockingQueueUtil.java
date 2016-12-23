package com.qinyadan.monitor.agent.utils;

import java.io.InterruptedIOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class BlockingQueueUtil {

    /**
     * 获取结果
     * @param blockingQueue
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedIOException
     */
    public static Object getResult(BlockingQueue<Object> blockingQueue,long timeout, TimeUnit unit) throws InterruptedIOException {
        Object result;
        try {
            result = blockingQueue.poll(timeout, unit);
            if (result == null) {
                if (!blockingQueue.offer(""))
                    result = blockingQueue.take();
            }
        } catch (InterruptedException e) {
            throw ExceptionUtil.initCause(new InterruptedIOException(e.getMessage()), e);
        }
        return result;
    }

}
