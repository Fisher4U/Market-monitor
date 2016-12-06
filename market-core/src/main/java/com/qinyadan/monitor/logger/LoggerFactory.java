package com.qinyadan.monitor.logger;

public class LoggerFactory {

    public static Logger getLogger(Class toBeLoggerClass) {
        return new Logger(toBeLoggerClass);
    }
}
