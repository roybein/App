package com.example.sheetshowtry.app.fake;

import android.os.Handler;
import android.os.Message;

import java.util.Map;

public class Task {

    public class TaskID {
        public static final int LOAD_IMAGE = 0;
    }

    private int taskID;
    private Map<String, Object> taskParams;

    public Task(int taskID, Map<String, Object> taskParams) {
        this.taskID = taskID;
        this.taskParams = taskParams;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public Map<String, Object> getTaskParams() {
        return taskParams;
    }

    public void setTaskParams(Map<String, Object> taskParams) {
        this.taskParams = taskParams;
    }
}