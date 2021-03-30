package com.parallel.framework.springdemo.proc;

import com.parallel.framework.DealRequestContext;
import com.parallel.framework.JsonUtil;
import com.parallel.framework.Result;
import com.parallel.framework.SuccessResult;
import com.parallel.framework.demo.TestProcessContext;
import com.parallel.framework.demo.TestProcesser;
import com.parallel.framework.demo.ThreadPoolFactory;
import com.parallel.framework.demo.entity.ReqBbb;
import com.parallel.framework.demo.entity.RespBbb;

import java.util.Random;
import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

@Component
public class BbbProc extends TestProcesser<ReqBbb,RespBbb> {
    @Override
    protected ReqBbb getRequest(TestProcessContext context) {
        ReqBbb req = new ReqBbb();
        req.setBbb(context.getId());
        addEvent(" fill req " + JsonUtil.beanToJson(req));
        return req;
    }

    @Override
    protected Result<DealRequestContext<ReqBbb, RespBbb>> executeContext(ReqBbb request) throws Exception {
        return SuccessResult.quickResult(request
                , ThreadPoolFactory.executor.submit(new Callable<RespBbb>() {
                    @Override
                    public RespBbb call() throws Exception {
                        // do something
                        Random random = new Random();
                        int ms = random.nextInt(500) + 500;
                        Thread.sleep(ms);
                        RespBbb resp = new RespBbb();
                        resp.setBbbCode(ms);
                        return resp;
                    }
                }));
    }

    public String fillBbbCode(){
        RespBbb resp = getResponse();
        if(resp!=null){
            return "Bbb runs for " + resp.getBbbCode() + " ms";
        }
        return "";
    }

    public Integer getBbbCostTime(){
        RespBbb resp = getResponse();
        if(resp!=null){
            return resp.getBbbCode();
        }
        return 0;
    }
}
