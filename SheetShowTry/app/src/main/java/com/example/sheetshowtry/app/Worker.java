package com.example.sheetshowtry.app;

import android.os.Handler;
import android.os.Message;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Worker extends Thread implements IProcess{

    public int workerID;
    private Task task;

    public Worker(int workerID, Task task) {
        this.workerID = workerID;
        this.task = task;
        this.start();
    }

    @Override
    public void run() {
        process();
    }

    @Override
    public void process() {
        Message message = mHandler.obtainMessage();
        message.what = task.getTaskID();

        switch (task.getTaskID()) {
            case Task.TaskID.LOAD_IMAGE:
                //TODO: load image
                List<URL> urls = new ArrayList<URL>();
                try {
                    urls.add(new URL("http://www.ccjt.net/pu/2010/3/23/129137942396196250/1.gif"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ImageLoader imageLoader = new ImageLoader(urls, MainActivity.bitmapList, MainActivity.fileCache);
                imageLoader.loadImage();
                message.obj = "got image";
                break;
        }

        mHandler.sendMessage(message);
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Task.TaskID.LOAD_IMAGE:
                    IActivity activity = ActivityManager.getActivityByName("MainActivity");
                    activity.refresh(msg.obj);
                    break;
            }
        }
    };

}