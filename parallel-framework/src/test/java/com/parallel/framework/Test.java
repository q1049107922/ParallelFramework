package com.parallel.framework;

import com.parallel.framework.demo.TestProcessContext;
import com.parallel.framework.demo.proc.GggProc;

import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String [] a){
        List<Processer> list = new ArrayList<>();
        GggProc gggProc = new GggProc();
        list.add(gggProc);
        long start = System.currentTimeMillis();
        try (AutoCloseProcessEngine ser = new AutoCloseProcessEngine(list)) {
            TestProcessContext context = new TestProcessContext();
            context.setId("111");
            context.setName("Tom");
            ser.execute(context);
            String code = gggProc.fillGggCode();
            System.out.println(code);
        } catch (Exception e) {
            // logger
        }
        System.out.println("total :"+ (System.currentTimeMillis()-start));
    }
}
