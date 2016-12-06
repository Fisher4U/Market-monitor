package com.qinyadan.monitor.plugin;

import static com.qinyadan.monitor.plugin.PluginBootstrap.CLASS_TYPE_POOL;

import com.qinyadan.monitor.logger.LoggerFactory;
import com.qinyadan.monitor.logger.Logger;
import com.qinyadan.monitor.utils.StringUtil;

import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.pool.TypePool.Resolution;

public abstract class AbstractClassEnhancePluginDefine implements IPlugin {
    private static Logger logger = LoggerFactory.getLogger(AbstractClassEnhancePluginDefine.class);

    @Override
    public void define(DynamicType.Builder<?> builder) throws PluginException {
        builder = define0(builder);
        builder.name(enhanceClassName()).make().load(ClassLoader.getSystemClassLoader(), ClassLoadingStrategy.Default.INJECTION).getLoaded();
    }

    public DynamicType.Builder<?> define0(DynamicType.Builder<?> builder) throws PluginException {
        String interceptorDefineClassName = this.getClass().getName();

        String enhanceOriginClassName = enhanceClassName();
        if (StringUtil.isEmpty(enhanceOriginClassName)) {
            logger.warn("classname of being intercepted is not defined by {}.", interceptorDefineClassName);
            return builder;
        }

        logger.debug("prepare to enhance class {} by {}.", enhanceOriginClassName, interceptorDefineClassName);

        /**
         * find witness classes for enhance class
         */
        String[] witnessClasses = witnessClasses();
        if (witnessClasses != null) {
            for (String witnessClass : witnessClasses) {
                Resolution witnessClassResolution = CLASS_TYPE_POOL.describe(witnessClass);
                if (!witnessClassResolution.isResolved()) {
                    logger.warn("enhance class {} by plugin {} is not working. Because witness class {} is not existed.", enhanceOriginClassName, interceptorDefineClassName,
                            witnessClass);
                    return builder;
                }
            }
        }

        /**
         * find origin class source code for interceptor
         */
        DynamicType.Builder<?> newClassBuilder = this.enhance(enhanceOriginClassName, builder);


        logger.debug("enhance class {} by {} completely.", enhanceOriginClassName, interceptorDefineClassName);

        return newClassBuilder;
    }

    protected abstract DynamicType.Builder<?> enhance(String enhanceOriginClassName, DynamicType.Builder<?> newClassBuilder) throws PluginException;

    /**
     * 返回要被增强的类，应当返回类全名
     *
     * @return
     */
    protected abstract String enhanceClassName();

    /**
     * 返回一个类名的列表
     * 如果列表中的类在JVM中存在,则enhance可以会尝试生效
     *
     * @return
     */
    protected String[] witnessClasses() {
        return new String[] {};
    }
}
