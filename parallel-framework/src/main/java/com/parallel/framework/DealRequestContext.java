package com.parallel.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 处理上下文
 * @param <Req>
 * @param <Resp>
 */
public class DealRequestContext<Req ,Resp> {
    protected static Logger logger= LoggerFactory.getLogger(DealRequestContext.class);
    protected Req request;
    protected Resp response;
    protected Future<Resp> respFuture;

    public DealRequestContext(Req rq) {
        request = rq;
    }

    public DealRequestContext(Req rq, Resp resp) {
        this.request = rq;
        this.response = resp;
    }

    public DealRequestContext(Req rq, Future<Resp> respFuture) {
        this.request = rq;
        this.respFuture = respFuture;
    }

    public Req getRequest() {
        return request;
    }

    public Resp getResponse() throws InterruptedException, ExecutionException, TimeoutException {
        return getResponse(2000);
    }

    /**
     *
     * @param timeout MILLISECONDS
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public Resp getResponse(Integer timeout) throws InterruptedException, ExecutionException, TimeoutException {
        return getResponse(timeout, TimeUnit.MILLISECONDS);
    }

    public Resp getResponse(Integer timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (response == null) {
            if (respFuture != null) {

                if (timeout != null && timeout > 0) {
                    response = respFuture.get(timeout, unit);
                } else {
                    response = respFuture.get();
                }
            }
        }
        return response;
    }
}
