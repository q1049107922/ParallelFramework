package com.parallel.framework.springdemo.proc;

import com.parallel.framework.DealRequestContext;
import com.parallel.framework.Dependency;
import com.parallel.framework.Result;
import com.parallel.framework.SuccessResult;
import com.parallel.framework.demo.TestProcessContext;
import com.parallel.framework.demo.TestProcesser;

import org.springframework.stereotype.Component;

@Component
public class TotalCostTimeProc extends TestProcesser<Integer,Integer> {

    @Dependency
    AaaProc aaaProc;
    @Dependency
    BbbProc bbbProc;
    @Dependency
    DddProc dddProc;
    @Dependency
    CccProc cccProc;
    @Dependency
    FffProc fffProc;
    @Dependency
    EeeProc eeeProc;
    @Dependency
    GggProc gggProc;

    @Override
    protected Integer getRequest(TestProcessContext context) {
        return null;
    }

    @Override
    protected Result<DealRequestContext<Integer, Integer>> executeContext(Integer request) throws Exception {
        return SuccessResult.quickResult(0,0);
    }

    public Integer getTotalCostTime() {
        return aaaProc.getAaaCostTime() + bbbProc.getBbbCostTime() + cccProc.getCccCostTime() + dddProc.getDddCostTime()
                + eeeProc.getEeeCostTime() + fffProc.getFffCostTime() + gggProc.getGggCostTime();
    }
}
