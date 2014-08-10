package com.example.sheetshowtry.app;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Factory {

    public ThreadPoolExecutor threadPoolExecutor = null;
    public static List<Task> taskQueue = null;
    private static int worksNum = 5;

    public Factory() {
        threadPoolExecutor = new ThreadPoolExecutor(worksNum, worksNum, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(3));
        taskQueue = Collections.synchronizedList(new LinkedList<Task>());
    }

}