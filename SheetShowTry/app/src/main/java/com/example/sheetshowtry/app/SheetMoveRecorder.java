package com.example.sheetshowtry.app;

import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bianrh on 9/20/14.
 */
class SheetMoveRecorder {

    private final static String LOG_TAG = "Musibox";

    private final static long MIM_TIME_STEP_MS = 100;
    private final static long MAX_TIME_STEP_MS = 1000;
    private final static long DEF_TIME_STEP_MS = 200;

    private Sheet sheet;
    private ArrayList<PointF> offsetVectList;
    //private ArrayList<PointF> offsetVectList;
    private long timeStepMS;
    private boolean isPause = false;
    private Timer timer = null;
    private TimerTask timerTask = null;

    public SheetMoveRecorder(Sheet sheet) {
        this.sheet = sheet;
        this.offsetVectList = sheet.offsetVectList;
        //this.offsetVectList = new ArrayList<PointF>();
        timeStepMS = DEF_TIME_STEP_MS;
    }

    public void setTimeStepMS(long stepMS) {
        if (stepMS < MIM_TIME_STEP_MS) {
            //TODO: too small step
        } else if (stepMS > MAX_TIME_STEP_MS) {
            //TODO: too big step
        } else {
            timeStepMS = stepMS;
            Log.i(LOG_TAG, "set time step: " + stepMS + " ms");
        }
    }

    protected void addOffset(PointF offsetVect) {
        PointF vect = new PointF();
        vect.set(offsetVect);
        offsetVectList.add(vect);
        Log.i(LOG_TAG, "record at time " + System.currentTimeMillis() +
                " with " + offsetVect.toString());
    }


    public void startRecord() {
        Log.i(LOG_TAG, "Start Record");
        if (timer == null) {
            timer = new Timer();
        }

        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    addOffset(sheet.getOffsetVect());

//                    do {
//                        try {
//                            Thread.sleep();
//                        } catch (InterruptedException e) {
//                            //TODO:
//                        }
//                    } while (isPause);
                }
            };


            if (timer != null && timerTask != null) {
                timer.schedule(timerTask, 0, timeStepMS);
            }
        }
    }

    public void stopRecord() {
        Log.i(LOG_TAG, "Stop Record");
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }
}
