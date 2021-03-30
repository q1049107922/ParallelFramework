package com.parallel.framework.springdemo.proc;

import com.parallel.framework.*;
import com.parallel.framework.demo.TestProcessContext;
import com.parallel.framework.demo.TestProcesser;
import com.parallel.framework.demo.ThreadPoolFactory;
import com.parallel.framework.demo.entity.ReqCcc;
import com.parallel.framework.demo.entity.RespCcc;

import java.util.Random;
import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

@Component
public class CccProc extends TestProcesser<ReqCcc,RespCcc> {

    @Dependency
    AaaProc aaaProc;
    @Dependency
    BbbProc bbbProc;

    @Override
    protected ReqCcc getRequest(TestProcessContext context) {
        ReqCcc req = new ReqCcc();
        req.setCcc("Ccc need aaa :" + aaaProc.fillAaaCode() + " and need bbb :" + bbbProc.fillBbbCode());
        addEvent(" fill req " + JsonUtil.beanToJson(req));
        return req;
    }

    @Override
    protected Result<DealRequestContext<ReqCcc, RespCcc>> executeContext(ReqCcc request) throws Exception {
        return SuccessResult.quickResult(request
                , ThreadPoolFactory.executor.submit(new Callable<RespCcc>() {
                    @Override
                    public RespCcc call() throws Exception {
                        // do something
                        Random random = new Random();
                        int ms = random.nextInt(500) + 500;
                        Thread.sleep(ms);
                        System.out.println(request.getCcc());
                        RespCcc resp = new RespCcc();
                        resp.setCccCode(Integer.valueOf(ms));
                        return resp;
                    }
                }));
    }

    public String fillCccCode(){
        RespCcc resp = getResponse();
        if(resp!=null){
            return "Ccc runs for " + resp.getCccCode() + " ms";
        }
        return "";
    }

    public Integer getCccCostTime(){
        RespCcc resp = getResponse();
        if(resp!=null){
            return resp.getCccCode();
        }
        return 0;
    }
}