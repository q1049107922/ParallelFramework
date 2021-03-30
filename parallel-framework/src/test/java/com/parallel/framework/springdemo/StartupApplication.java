package com.parallel.framework.springdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by b_lin on 2021/3/30.
 */
@SpringBootApplication(scanBasePackages ="com.parallel.framework.springdemo.proc.*" )
// mapper 接口类包扫描
public class StartupApplication {

    public static void main(String[] args) {
        SpringApplication.run(StartupApplication.class, args);
    }
}