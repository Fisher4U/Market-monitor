package com.qinyadan.monitor.agent.utils;

import java.util.ArrayList;
import java.util.List;

public class ExceptionUtil {

    public static <T extends Throwable> T initCause(T wrapper, Throwable wrapped) {
        wrapper.initCause(wrapped);
        return wrapper;
    }

    /**
     * 获取异常的所有上层异常
     * @param e
     * @return
     */
    public static List<Throwable> getExceptionCauses(Throwable e){
        List<Throwable> throwables = new ArrayList<>();
        Throwable throwable = getExceptionCause(e);
        throwables.add(throwable);
        while (throwable != null){
            throwable = getExceptionCause(throwable);
            throwables.add(throwable);
        }
        return throwables;
    }

    private static Throwable getExceptionCause(Throwable e){
        return e.getCause();
    }
}
