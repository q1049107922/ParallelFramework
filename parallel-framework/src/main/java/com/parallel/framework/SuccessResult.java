package com.parallel.framework;



import java.util.concurrent.Future;

public class SuccessResult<T> extends Result<T> {

    /**
     * 成功的结果类，方便简写
     * @param obj
     */
    public SuccessResult(T obj) {
        super(ResultCodeEnum.success,"", obj);
    }

    /**
     * 快捷创建结果
     * @param req
     * @param resp
     * @param <Req>
     * @param <Resp>
     * @return
     */
    public static <Req, Resp> SuccessResult<DealRequestContext<Req, Resp>> quickResult(Req req, Resp resp){
        return new SuccessResult<>(new DealRequestContext<>(req,resp));
    }

    /**
     * 快捷创建结果
     * @param req
     * @param resp
     * @param <Req>
     * @param <Resp>
     * @return
     */
    public static <Req, Resp> SuccessResult<DealRequestContext<Req, Resp>> quickResult(Req req, Future<Resp> resp){
        return new SuccessResult<>(new DealRequestContext<>(req,resp));
    }
}
