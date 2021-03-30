package com.parallel.framework.springdemo.proc;

import com.parallel.framework.*;
import com.parallel.framework.demo.TestProcessContext;
import com.parallel.framework.demo.TestProcesser;
import com.parallel.framework.demo.ThreadPoolFactory;
import com.parallel.framework.demo.entity.ReqGgg;
import com.parallel.framework.demo.entity.RespGgg;

import java.util.Random;
import java.util.concurrent.Callable;
import org.springframework.stereotype.Component;

@Component
public class GggProc extends TestProcesser<ReqGgg,RespGgg> {
    @Dependency
    FffProc fffProc;

    @Dependency
    EeeProc eeeProc;

    @Override
    protected ReqGgg getRequest(TestProcessContext context) {
        ReqGgg req = new ReqGgg();
        req.setGgg("Ggg need fff :" + fffProc.fillFffCode() + " and need eee :" + eeeProc.fillEeeCode());
        addEvent(" fill req " + JsonUtil.beanToJson(req));
        return req;
    }

    @Override
    protected Result<DealRequestContext<ReqGgg, RespGgg>> executeContext(ReqGgg request) throws Exception {
        return SuccessResult.quickResult(request
                , ThreadPoolFactory.executor.submit(new Callable<RespGgg>() {
                    @Override
                    public RespGgg call() throws Exception {
                        // do something
                        Random random = new Random();
                        int ms = random.nextInt(500) + 500;
                        Thread.sleep(ms);
                        System.out.println(request.getGgg());
                        RespGgg resp = new RespGgg();
                        resp.setGggCode(Integer.valueOf(ms));
                        return resp;
                    }
                }));
    }

    public String fillGggCode(){
        RespGgg resp = getResponse();
        if(resp!=null){
            return "Ggg runs for " + resp.getGggCode() + " ms";
        }
        return "";
    }

    public Integer getGggCostTime(){
        RespGgg resp = getResponse();
        if(resp!=null){
            return resp.getGggCode();
        }
        return 0;
    }


}