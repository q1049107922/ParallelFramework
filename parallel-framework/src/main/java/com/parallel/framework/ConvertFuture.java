package com.parallel.framework;


import com.ctrip.payment.provider.common.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Future 类型转换器
 * @param <Source>
 * @param <Target>
 */
public abstract class ConvertFuture<Source,Target> implements Future<Target> {
    private static final Logger logger = LoggerFactory.getLogger(ConvertFuture.class);
    private Future<Source> future;
    private Target target;
    public ConvertFuture(Future<Source> future) {
        this.future = future;
    }

    public abstract Target convert(Source source);

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    @Override
    public boolean isDone() {
        return future.isDone();
    }

    @Override
    public Target get() throws InterruptedException, ExecutionException {
        if (target == null) {
            Source source = future.get();
            logger.info(source.getClass() + JsonUtil.beanToJson(source));
            target = convert(source);
        }
        return target;
    }

    @Override
    public Target get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (target == null) {
            Source source = future.get(timeout, unit);
            logger.info(source.getClass() + JsonUtil.beanToJson(source));
            return convert(source);
        }
        return target;
    }
}
