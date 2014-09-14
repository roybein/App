package com.example.sheetshowtry.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.sheetshowtry.app.craft.FitAxisEnum;

import java.io.File;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class SheetScrollShowActivity extends Activity {

    private final static String LOG_TAG = "Musibox";

    private Bitmap bitmap;
    private File sampleSheetFile = new File("/sdcard/Download/1.gif");
    public Rect imageViewRect;
    private ImageView sheetScrollShowView;
    private ViewTreeObserver vto;
    private Sheet sheet;
    private SheetMoveRecorder recorder;
    private boolean isRecording = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sheet_scroll_show);

        bitmap = BitmapFactory.decodeFile(sampleSheetFile.getPath());
        Log.i(LOG_TAG, "get bitmap: " + bitmap.getWidth() + "," + bitmap.getHeight());

        sheetScrollShowView = (ImageView) findViewById(R.id.sheet_scroll_image_view);

        convertBitmapToFullScreen();
        sheet = new Sheet(bitmap, sheetScrollShowView);
        sheet.show();
        recorder = new SheetMoveRecorder(sheet);

        vto = sheetScrollShowView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //vto.removeOnGlobalLayoutListener(this);
                imageViewRect = new Rect();
                sheetScrollShowView.getDrawingRect(imageViewRect);
                Log.i(LOG_TAG, "get imageViewRect=" + imageViewRect.toShortString());
                sheet.setImageViewRect(imageViewRect);
                sheet.initOffsetRect();
                //sheetScrollShowView.setOnTouchListener(new ImageShowTouchListener());
                sheetScrollShowView.setOnTouchListener(new SheetOnTouchListener(sheet));

                if (isRecording == false) {
                    isRecording = true;
                    recorder.startRecord();
                }
            }
        });
    }

    protected void convertBitmapToFullScreen() {
        Display display = getWindowManager().getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        float scaleRate = (float) screenSize.x/bitmap.getWidth();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleRate, scaleRate);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
    }



    class SheetOnTouchListener implements View.OnTouchListener {

        Sheet sheet;
        PointF sp;

        public SheetOnTouchListener(Sheet sheet) {
            this.sheet = sheet;
            sp = new PointF();
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    Log.d(LOG_TAG, "ACTION_DOWN");
                    sp.set(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d(LOG_TAG, "ACTION_UP");
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.d(LOG_TAG, "ACTION_MOVE");
                    PointF moveVect = new PointF(event.getX() - sp.x, event.getY() - sp.y);
                    Log.d(LOG_TAG, "moveVect: " + moveVect.toString());
                    sp.set(event.getX(), event.getY());
                    sheet.move(moveVect);
            }

            sheet.showWithMatrix();
            return true;
        }
    }

    class ImageShowTouchListener implements View.OnTouchListener {
        private Matrix matrix = new Matrix();
        private PointF startPoint = new PointF();
        private float offsetY = 0f;
        private float offsetBoundMargin = imageViewRect.height() * 0.2f;
        private float offsetBoundTop = 0f - (bitmap.getHeight() - imageViewRect.height()) -
                offsetBoundMargin;
        private float offsetBoundBottom = 0f;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            ImageView imageView = (ImageView) v;
            Log.i(LOG_TAG, "ImageView: " + imageView.getLeft() + "," + imageView.getTop());

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    Log.i(LOG_TAG, "ACTION_DOWN");
                    matrix.set(imageView.getImageMatrix());
                    Log.i(LOG_TAG, "matrix at ACTION_DOWN" + matrix.toString());
                    startPoint.set(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i(LOG_TAG, "ACTION_UP");
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.i(LOG_TAG, "ACTION_MOVE");
                    //matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
                    float distanceY = event.getY() - startPoint.y;
                    Log.i(LOG_TAG, "distanceY=" + distanceY + " " +
                            "recent image offset=" + offsetY );
                    if ( (offsetY + distanceY) < offsetBoundTop ) {
                        distanceY = offsetBoundTop - offsetY;
                    }

                    if ( (offsetY + distanceY) > offsetBoundBottom ) {
                        distanceY = offsetBoundBottom - offsetY;
                    }

                    matrix.postTranslate(0, distanceY);
                    startPoint.set(event.getX(), event.getY());
                    offsetY += distanceY;
                    break;
            }

            Log.i(LOG_TAG, "view set image matrix" + matrix.toString());

            imageView.setImageMatrix(matrix);
            return true;
        }
    }
}

class Sheet {
    private final static String LOG_TAG = "Musibox";

    private ImageView imageView;
    private Rect imageViewRect;
    private Bitmap bmp;
    private RectF offsetRect;
    private Matrix matrix;
    private PointF offsetVect;
    private FitAxisEnum fitAxis = FitAxisEnum.FitAxisNone;

    protected Sheet(Bitmap bmp, ImageView view) {
        this.imageView = view;
        this.bmp = bmp;
        offsetRect = new RectF();
        matrix = new Matrix();
        offsetVect = new PointF();
    }

    public PointF getOffsetVect() {
        return offsetVect;
    }
    protected void setImageViewRect(Rect rect) {
        this.imageViewRect = rect;
    }

    protected void initOffsetRect() {
        Log.d(LOG_TAG, "imageViewRect: " + imageViewRect.width() + "," + imageViewRect.height());
        Log.d(LOG_TAG, "bmp: " + bmp.getWidth() + "," + bmp.getHeight());
        offsetRect.left = imageViewRect.width() - bmp.getWidth();
        offsetRect.right = 0;
        offsetRect.top = imageViewRect.height() - bmp.getHeight();
        offsetRect.bottom = 0;
        Log.i(LOG_TAG, "init offset rectangle: " + offsetRect.toString());
    }

    protected void setFitAxis(FitAxisEnum fitAxis) {
        this.fitAxis = fitAxis;
    }

    protected void show() {
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        imageView.setImageBitmap(bmp);
    }

    protected void move(PointF moveVect) {
        Log.d(LOG_TAG, "move by vector: " + moveVect.toString() +
                " from offsetVect: " + offsetVect.toString());
        PointF newOffsetVect = new PointF();
        if (fitAxis.equals(FitAxisEnum.FitAxisX)) {
            Log.d(LOG_TAG, "fix axis x");
            moveVect.x = 0;
        } else if (fitAxis.equals(FitAxisEnum.FitAxisY)) {
            Log.d(LOG_TAG, "fix axis y");
            moveVect.y = 0;
        }
        newOffsetVect.set(offsetVect.x + moveVect.x, offsetVect.y + moveVect.y);
        if (offsetVect.x + moveVect.x < offsetRect.left) {
            Log.d(LOG_TAG, "left exceeded");
            newOffsetVect.x = offsetRect.left;
        }
        if (offsetVect.x + moveVect.x > offsetRect.right) {
            Log.d(LOG_TAG, "right exceeded");
            newOffsetVect.x = offsetRect.right;
        }
        if (offsetVect.y + moveVect.y < offsetRect.top) {
            Log.d(LOG_TAG, "top exceeded");
            newOffsetVect.y = offsetRect.top;
        }
        if (offsetVect.y + moveVect.y > offsetRect.bottom) {
            Log.d(LOG_TAG, "bottom exceeded");
            newOffsetVect.y = offsetRect.bottom;
        }
        Log.d(LOG_TAG, "newoffsetVect: " + newOffsetVect.toString());

        matrix.postTranslate(newOffsetVect.x - offsetVect.x , newOffsetVect.y - offsetVect.y);
        offsetVect.set(newOffsetVect);
    }

    protected void showWithMatrix() {
        imageView.setImageMatrix(matrix);
        matrix.set(imageView.getImageMatrix());
    }
}

class SheetMoveRecorder {

    private final static String LOG_TAG = "Musibox";

    private final static long MIM_TIME_STEP_MS = 100;
    private final static long MAX_TIME_STEP_MS = 1000;
    private final static long DEF_TIME_STEP_MS = 200;

    private Sheet sheet;
    private LinkedList<PointF> offsetVectList;
    private long timeStepMS;
    private boolean isPause = false;
    private Timer timer = null;
    private TimerTask timerTask = null;

    public SheetMoveRecorder(Sheet sheet) {
        this.sheet = sheet;
        offsetVectList = new LinkedList<PointF>();
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
        offsetVectList.add(offsetVect);
    }


    public void startRecord() {
        if (timer == null) {
            timer = new Timer();
        }

        if (timerTask == null) {
            timerTask = new TimerTask() {
                PointF offsetVect;
                @Override
                public void run() {
                    offsetVect = sheet.getOffsetVect();
                    addOffset(offsetVect);
                    Log.i(LOG_TAG, "record at time " + System.currentTimeMillis() +
                            " with " + offsetVect.toString());
/*
                    do {
                        try {
                            Thread.sleep();
                        } catch (InterruptedException e) {
                            //TODO:
                        }
                    } while (isPause);
                    */
                }
            };
        }

        if (timer != null && timerTask != null) {
            timer.schedule(timerTask, 0, timeStepMS);
        }
    }

    public void stopRecord() {
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