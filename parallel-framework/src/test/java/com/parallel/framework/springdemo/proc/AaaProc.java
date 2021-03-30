package com.parallel.framework.springdemo.proc;

import com.parallel.framework.DealRequestContext;
import com.parallel.framework.JsonUtil;
import com.parallel.framework.Result;
import com.parallel.framework.SuccessResult;
import com.parallel.framework.demo.TestProcessContext;
import com.parallel.framework.demo.TestProcesser;
import com.parallel.framework.demo.ThreadPoolFactory;
import com.parallel.framework.demo.entity.ReqAaa;
import com.parallel.framework.demo.entity.RespAaa;

import java.util.Random;
import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;

@Component
public class AaaProc extends TestProcesser<ReqAaa,RespAaa> {
    @Override
    protected ReqAaa getRequest(TestProcessContext context) {
        ReqAaa req = new ReqAaa();
        req.setAaa(context.getId());
        addEvent(" fill req " + JsonUtil.beanToJson(req));
        return req;
    }

    @Override
    protected Result<DealRequestContext<ReqAaa, RespAaa>> executeContext(ReqAaa request) throws Exception {
        return SuccessResult.quickResult(request
                , ThreadPoolFactory.executor.submit(new Callable<RespAaa>() {
                    @Override
                    public RespAaa call() throws Exception {
                        // do something
                        Random random = new Random();
                        int ms = random.nextInt(500) + 500;
                        Thread.sleep(ms);
                        RespAaa resp = new RespAaa();
                        resp.setAaaCode(ms);
                        return resp;
                    }
                }));
    }

    public String fillAaaCode(){
        RespAaa resp = getResponse();
        if(resp!=null){
            return "Aaa runs for " + resp.getAaaCode() + " ms";
        }
        return "";
    }

    public Integer getAaaCostTime(){
        RespAaa resp = getResponse();
        if(resp!=null){
            return resp.getAaaCode();
        }
        return 0;
    }
}
