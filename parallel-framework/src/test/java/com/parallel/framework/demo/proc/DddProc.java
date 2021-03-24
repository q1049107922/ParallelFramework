package com.parallel.framework.demo.proc;

import com.parallel.framework.*;
import com.parallel.framework.demo.TestProcessContext;
import com.parallel.framework.demo.TestProcesser;
import com.parallel.framework.demo.ThreadPoolFactory;
import com.parallel.framework.demo.entity.*;

import java.util.Random;
import java.util.concurrent.Callable;

public class DddProc extends TestProcesser<ReqDdd,RespDdd> {
    @Override
    protected ReqDdd getRequest(TestProcessContext context) {
        ReqDdd req = new ReqDdd();
        req.setDdd(context.getId());
        addEvent(" fill req " + JsonUtil.beanToJson(req));
        return req;
    }

    @Override
    protected Result<DealRequestContext<ReqDdd, RespDdd>> executeContext(ReqDdd request) throws Exception {
        return SuccessResult.quickResult(request
                , ThreadPoolFactory.executor.submit(new Callable<RespDdd>() {
                    @Override
                    public RespDdd call() throws Exception {
                        // do something
                        Random random = new Random();
                        int ms = random.nextInt(500) + 500;
                        Thread.sleep(ms);
                        RespDdd resp = new RespDdd();
                        resp.setDddCode(Integer.valueOf(ms));
                        return resp;
                    }
                }));
    }

    public String fillDddCode(){
        RespDdd resp = getResponse();
        if(resp!=null){
            return "Ddd runs for " + resp.getDddCode() + " ms";
        }
        return "";
    }

    public Integer getDddCostTime(){
        RespDdd resp = getResponse();
        if(resp!=null){
            return resp.getDddCode();
        }
        return 0;
    }
}