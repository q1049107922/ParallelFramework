package com.parallel.framework.demo.proc;

import com.parallel.framework.*;
import com.parallel.framework.demo.TestProcessContext;
import com.parallel.framework.demo.TestProcesser;
import com.parallel.framework.demo.ThreadPoolFactory;
import com.parallel.framework.demo.entity.*;

import java.util.Random;
import java.util.concurrent.Callable;

public class EeeProc extends TestProcesser<ReqEee,RespEee> {

    @Dependency
    DddProc dddProc;
    @Dependency
    CccProc cccProc;

    @Override
    protected ReqEee getRequest(TestProcessContext context) {
        ReqEee req = new ReqEee();
        req.setEee("Eee need ddd :" + dddProc.fillDddCode() +"and need ccc :"+cccProc.fillCccCode());
        addEvent(" fill req " + JsonUtil.beanToJson(req));
        return req;
    }

    @Override
    protected Result<DealRequestContext<ReqEee, RespEee>> executeContext(ReqEee request) throws Exception {
        return SuccessResult.quickResult(request
                , ThreadPoolFactory.executor.submit(new Callable<RespEee>() {
                    @Override
                    public RespEee call() throws Exception {
                        // do something
                        Random random = new Random();
                        int ms = random.nextInt(500) + 500;
                        Thread.sleep(ms);
                        System.out.println(request.getEee());
                        RespEee resp = new RespEee();
                        resp.setEeeCode(Integer.valueOf(ms));
                        return resp;
                    }
                }));
    }

    public String fillEeeCode(){
        RespEee resp = getResponse();
        if(resp!=null){
            return "Eee runs for " + resp.getEeeCode() + " ms";
        }
        return "";
    }


    public Integer getEeeCostTime(){
        RespEee resp = getResponse();
        if(resp!=null){
            return resp.getEeeCode();
        }
        return 0;
    }
}