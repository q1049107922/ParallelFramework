package com.parallel.framework.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolFactory {

    public static ExecutorService executor = Executors.newFixedThreadPool(20);
}
