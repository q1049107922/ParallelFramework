package com.parallel.framework.demo;

import com.parallel.framework.AutoCloseProcessEngine;
import com.parallel.framework.Processer;
import com.parallel.framework.demo.TestProcessContext;
import com.parallel.framework.demo.proc.GggProc;
import com.parallel.framework.demo.proc.TotalCostTimeProc;

import java.util.ArrayList;
import java.util.List;

public class TestMain {

    public static void main(String [] a){
        List<Processer> list = new ArrayList<>();
        GggProc gggProc = new GggProc();
        TotalCostTimeProc totalCostTimeProc = new TotalCostTimeProc();
        list.add(gggProc);
        list.add(totalCostTimeProc);
        long start = System.currentTimeMillis();
        try (AutoCloseProcessEngine ser = new AutoCloseProcessEngine(list)) {
            TestProcessContext context = new TestProcessContext();
            context.setId("111");
            context.setName("Tom");
            ser.execute(context);
            String code = gggProc.fillGggCode();
            System.out.println(code);
            System.out.println("without parallel total Cost Time:"+ totalCostTimeProc.getTotalCostTime());
        } catch (Exception e) {
            // logger
        }
        System.out.println("parallel framework total Cost Time:"+ (System.currentTimeMillis()-start));
    }
}
