package com.example.sheetshowtry.app;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

public class SheetController {

    private HandlerThread handlerThread;
    private Handler handler;
    private Sheet sheet;
    private SheetMoveRecorder recorder;

    public SheetController(Sheet sheet, SheetMoveRecorder recorder) {
        handlerThread = new HandlerThread("Sheet Control Handler Thread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
        this.sheet = sheet;
        this.recorder = recorder;
    }

    public Handler getHandler() {
        return handler;
    }
}