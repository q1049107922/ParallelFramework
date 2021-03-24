package com.parallel.framework.demo.proc;

import com.parallel.framework.*;
import com.parallel.framework.demo.TestProcessContext;
import com.parallel.framework.demo.TestProcesser;
import com.parallel.framework.demo.ThreadPoolFactory;
import com.parallel.framework.demo.entity.*;

import java.util.Random;
import java.util.concurrent.Callable;

public class FffProc extends TestProcesser<ReqFff,RespFff> {

    @Dependency
    CccProc cccProc;

    @Override
    protected ReqFff getRequest(TestProcessContext context) {
        ReqFff req = new ReqFff();
        req.setFff("Fff need ccc :"+cccProc.fillCccCode());
        addEvent(" fill req " + JsonUtil.beanToJson(req));
        return req;
    }

    @Override
    protected Result<DealRequestContext<ReqFff, RespFff>> executeContext(ReqFff request) throws Exception {
        return SuccessResult.quickResult(request
                , ThreadPoolFactory.executor.submit(new Callable<RespFff>() {
                    @Override
                    public RespFff call() throws Exception {
                        // do something
                        Random random = new Random();
                        int ms = random.nextInt(500) + 500;
                        Thread.sleep(ms);
                        System.out.println(request.getFff());
                        RespFff resp = new RespFff();
                        resp.setFffCode(Integer.valueOf(ms));
                        return resp;
                    }
                }));
    }

    public String fillFffCode(){
        RespFff resp = getResponse();
        if(resp!=null){
            return "Fff runs for " + resp.getFffCode() + " ms";
        }
        return "";
    }

    public Integer getFffCostTime(){
        RespFff resp = getResponse();
        if(resp!=null){
            return resp.getFffCode();
        }
        return 0;
    }
}