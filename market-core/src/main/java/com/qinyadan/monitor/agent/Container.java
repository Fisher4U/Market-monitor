package com.qinyadan.monitor.agent;

import com.qinyadan.monitor.extension.SPI;

/**
 * Container. (SPI, Singleton, ThreadSafe)
 * 
 * @author william.liangf
 */
@SPI("plugin")
public interface Container {
    
    /**
     * start.
     */
    void start();
    
    /**
     * stop.
     */
    void stop();

}