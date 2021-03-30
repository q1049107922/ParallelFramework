package com.parallel.framework.springdemo;

import com.parallel.framework.AutoCloseProcessEngine;
import com.parallel.framework.Processer;
import com.parallel.framework.demo.TestProcessContext;
import com.parallel.framework.demo.proc.GggProc;
import com.parallel.framework.demo.proc.TotalCostTimeProc;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by b_lin on 2021/3/30.
 */
/*@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = StartupApplication.class)*/
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringTestMain {


    @Autowired
    private GggProc gggProc ;
    @Autowired
    private  TotalCostTimeProc totalCostTimeProc ;

    @Test
    public void testBookService() {
        List<Processer> list = new ArrayList<>();
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
