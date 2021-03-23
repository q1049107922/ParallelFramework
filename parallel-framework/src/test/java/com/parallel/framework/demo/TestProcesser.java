package com.parallel.framework.demo;

import com.parallel.framework.ProcessContext;
import com.parallel.framework.Processer;

public abstract class TestProcesser <Req,Resp> extends Processer<Req,Resp> {

    protected abstract Req getRequest(TestProcessContext context);

    protected Req getRequest(ProcessContext context) {
        return getRequest((TestProcessContext) context);
    }

    public TestProcessContext getProcessContext() {
        return (TestProcessContext) super.getProcessContext();
    }
}
